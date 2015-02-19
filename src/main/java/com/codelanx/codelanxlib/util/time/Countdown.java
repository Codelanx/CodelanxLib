/*
 * Copyright (C) 2015 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2015 or as published
 * by a later date. You may not provide the source files or provide a means
 * of running the software outside of those licensed to use it.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the Creative Commons BY-NC-ND license
 * long with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.util.time;

import com.codelanx.codelanxlib.util.Scheduler;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Allows for having a countdown timer which can be applied through messages
 * or one of Bukkit's {@link Scoreboard} objects. Additionally, can run
 * code provided through the {@link Countdown#start(long, Runnable)} method
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class Countdown {

    private final Set<TimePoint> queue = new TreeSet<>();
    private final Set<Scoreboard> boards = new LinkedHashSet<>();
    private final Map<Scoreboard, String> formats = new HashMap<>();
    private String defFormat = "%d:%d:%s";
    private String announcement = "There are %s remaining!";
    private long start = -1;
    private volatile ScheduledFuture<?> task;

    /**
     * Starts a countdown timer that will run for as long as the specified
     * {@code duration} argument.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param duration The amount of time in seconds to run the countdown
     * @param exec Task to run when countdown completes
     */
    public void start(long duration, Runnable exec) {
        this.start = System.nanoTime() + TimeUnit.SECONDS.toNanos(duration);
        if (this.task != null) {
            this.task.cancel(true);
        }
        final TreeSet<TimePoint> test = new TreeSet<>(this.queue);
        this.task = Scheduler.getService().scheduleWithFixedDelay(() -> {
            if (this.start - System.nanoTime() < 0) {
                if (exec != null) {
                    exec.run();
                }
                if (this.task != null) {
                    this.task.cancel(false);
                }
            }
            TimePoint now = TimePoint.getTimePoint(this.start - System.nanoTime());
            TimePoint ref = TimePoint.findClosestAndWipe(test, now);
            if (ref != null) {
                //announce
                Bukkit.broadcastMessage(String.format(this.announcement, ref.format(TimeUnit.SECONDS)));
            }
            this.formatTimes(this.boards).entrySet().forEach(ent
                    -> ent.getKey().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ent.getValue()));
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Formats multiple scoreboards at once and caches the default result for
     * any scoreboards without a specified format
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param boards The boards to create formats for
     * @return A {@link Map} of the passed Scoreboards mapped to outputs
     */
    private Map<Scoreboard, String> formatTimes(Collection<Scoreboard> boards) {
        TimePoint now = TimePoint.getTimePoint(this.start - System.nanoTime());
        String temp = this.formatTime(null);
        if (temp == null) { //extreme case catch
            temp = this.formatReadable(now, this.defFormat);
        }
        final String def = temp;
        return boards.stream().collect(Collectors.toMap(
                Function.identity(),
                b -> {
                    if (b != null && this.formats.containsKey(b)) {
                        return this.formats.get(b);
                    } else {
                        return def;
                    }
                }
        ));
    }

    /**
     * Formats the {@link Countdown#defFormat} field and returns it for use in
     * the countdown timer display.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param board A board to search for a specific timer. Will use the default
     * formatting if null.
     * @return The formatting timer
     */
    private String formatTime(Scoreboard board) {
        long left = this.start - System.nanoTime();
        if (left < 0) {
            return null;
        }
        String format = this.defFormat;
        if (board != null && this.formats.containsKey(board)) {
            format = this.formats.get(board);
        }
        return this.formatReadable(TimePoint.getTimePoint(left), format);
    }

    /**
     * Takes a time-based format and converts it to a readable output based upon
     * the passed {@link TimePoint}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param t The {@link TimePoint} to use as reference
     * @param format The format to use
     * @return
     */
    private String formatReadable(TimePoint t, String format) {
        if (format.matches("(?!.*%[^sd])(?!.*%d.*%d)(?!.*%s.*%s)(?!.*%s.*%d)(?=.*%s)(?=.*%d).*")) {
            return String.format(format, t.getAmount(TimeUnit.MINUTES), t.getAmount(TimeUnit.SECONDS));
        } else {
            return String.format(format, t.getAmount(TimeUnit.HOURS), t.getAmount(TimeUnit.MINUTES), t.getAmount(TimeUnit.SECONDS));
        }
    }

    /**
     * Assigns a scoreboard to have the objective in the
     * {@link DisplaySlot#SIDEBAR}'s title changed in accordance to the format
     * given to this specific board.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @see Countdown#setTimeFormat(java.lang.String) for info about the syntax
     * for setting a format string
     * @param s The scoreboard to modify
     * @param format Optional board-specific format. Can be null
     * @return The current class instance (chaining)
     */
    public Countdown assignScoreboard(Scoreboard s, String format) {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (!format.matches("(?!.*%[^sd])(?!.*%d.*%d)(?!.*%s.*%s.*%s)(?!.*%s.*%d)(?=.*%s)(?=.*%d).*")) {
            throw new IllegalArgumentException("Countdown format must follow contract! (See javadoc)");
        }
        this.formats.put(s, format);
        this.assignScoreboard(s);
        return this;
    }

    /**
     * Assigns a scoreboard to have the objective in the
     * {@link DisplaySlot#SIDEBAR}'s title changed in accordance to the default
     * format
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param s The scoreboard to modify
     * @return The current class instance (chaining)
     */
    public Countdown assignScoreboard(Scoreboard s) {
        if (s.getObjective(DisplaySlot.SIDEBAR) == null) {
            s.registerNewObjective("timer", "dummy").setDisplayName(this.defFormat);
        }
        this.boards.add(s);
        return this;
    }

    /**
     * Sets the format for the title of the sidebar scoreboard. This format
     * requires the format tokens ({@code %s} and {@code %d}), but if an
     * additional {@code %s} is supplied it will use the first {@code %d} for
     * displaying hours and the following for minutes and seconds. Additionally,
     * {@code %d} should be the first token in the format string. So in
     * practice:
     * <br><br> {@code .* %d .* %s (optional) .* %s .*}
     * <br><br>
     * The actual regex in use to verify this formatting string is:
     * <br>      {@code (?!.*%[^sd])(?!.*%d.*%d)(?!.*%s.*%s.*%s)
         * (?!.*%s.*%d)(?=.*%s)(?=.*%d).*}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param format The format for the clock
     * @return The current {@link Countdown} object
     */
    public Countdown setTimeFormat(String format) {
        if (!format.matches("(?!.*%[^sd])(?!.*%d.*%d)(?!.*%s.*%s.*%s)(?!.*%s.*%d)(?=.*%s)(?=.*%d).*")) {
            throw new IllegalArgumentException("Countdown format must follow contract! (See javadoc)");
        }
        this.defFormat = format;
        return this;
    }

    /**
     * Sets the format for announcement messages in the chat from this
     * {@link Countdown}.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param format The format to set
     * @return The current instance (chained)
     */
    public Countdown setAnnouncementFormat(String format) {
        if (!format.matches("(?!.*%[^sd])")) {
            throw new IllegalArgumentException("Countdown format must follow contract! (See javadoc)");
        }
        this.announcement = format;
        return this;
    }

    /**
     * Notes to make a chat-based announcement at the specified time in the
     * countdown
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param time The amount of time to announce at
     * @param unit The relevant unit of time
     * @return The current instance (chained)
     */
    public Countdown announceAt(long time, TimeUnit unit) {
        return this.announceAt(TimePoint.getTimePoint(unit.toNanos(time))); //Convert for consistency and large input
    }

    /**
     * Notes to make a chat-based announcement at the specified time in the
     * countdown
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param point The relevant {@link TimePoint} to announce at
     * @return The current instance (chained)
     */
    public Countdown announceAt(TimePoint point) {
        this.queue.add(point);
        return this;
    }

    /**
     * Sets an announcement at every single integer point between the specified
     * {@code max} and {@code min} values, inclusively. E.g. a call of
     * {@code announceAtRange.(5, 10, TimeUnit.MINUTES)} would announce at
     * every minute between 5 and 10, as well as those times themselves
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param min The minimum time to announce at
     * @param max The maximum time to announce at
     * @param unit The unit of time relevant to this range
     * @return The current instance (chained)
     */
    public Countdown announceAtRange(long min, long max, TimeUnit unit) {
        for (; min <= max; min++) {
            this.announceAt(min, unit);
        }
        return this;
    }

    /**
     * Returns the default format used for all boards
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The default format
     */
    public String getDefaultFormat() {
        return this.defFormat;
    }

}
