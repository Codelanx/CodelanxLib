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
package com.codelanx.codelanxlib.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
 * Class description for {@link TimeUtil}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class TimeUtil {

    private TimeUtil() {
    }

    public static String getTime(long start) {
        return TimeUtil.formatTime(System.nanoTime() - start);
    }

    /**
     * Formats time in a readable output.
     * <br><br>
     * Calling this method is equivalent to
     * {@code TimeUtil.formatTime(durationNS, TimeUnit.NANOSECONDS)}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @see TimeUtil#formatTime(long, TimeUnit)
     * @param durationNS The amount of time in nanoseconds
     * @return The formatted string
     */
    public static String formatTime(long durationNS) {
        return TimeUtil.formatTime(durationNS, TimeUnit.NANOSECONDS);
    }

    /**
     * Formats time in a readable output. Creates a TimePoint out of the pass
     * long in order to do so.
     * <br><br>
     * Calling this method is equivalent to
     * {@code TimeUtil.formatTime(TimePoint, TimeUnit.NANOSECONDS)}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @see TimeUtil#formatTime(TimePoint, TimeUnit)
     * @param durationNS The amount of time in nanoseconds
     * @param min The minimum (inclusive) unit of time to output as a secondary
     *            measurement
     * @return The formatted string
     */
    public static String formatTime(long durationNS, TimeUnit min) {
        if (min == null) {
            throw new IllegalArgumentException("Minimum TimeUnit cannot be null!");
        }
        TimePoint point = TimeUtil.getTimePoint(durationNS);
        return TimeUtil.formatTime(point, min);
    }

    /**
     * Formats time in a readable output.
     * <br><br>
     * Calling this method is equivalent to
     * {@code TimeUtil.formatTime(point, TimeUnit.NANOSECONDS)}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @see TimeUtil#formatTime(TimePoint, TimeUnit)
     * @param point The {@link TimePoint} to use
     * @return The formatted string
     */
    public static String formatTime(TimePoint point) {
        return TimeUtil.formatTime(point, TimeUnit.NANOSECONDS);
    }

    /**
     * Formats time in a readable output. Depending on the amount of time, the
     * format will attempt to output the largest possible value first (e.g. 
     * days), followed by the next non-zero measurement of time (e.g., minutes).
     * If there is no non-zero secondary measurement, it is excluded.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param point The {@link TimePoint} to use
     * @param min The minimum (inclusive) unit of time to output as a secondary
     *            measurement
     * @return The formatted string
     */
    public static String formatTime(TimePoint point, TimeUnit min) {
        TimePoint next = point.getNextNonZero();
        if (next != null && next.getUnit().compareTo(min) >= 0) {
            return String.format("%d %s, %d %s", point.getTime(), point.properName(),
                    next.getTime(), next.properName());
        } else {
            return String.format("%d %s", point.getTime(), point.properName());
        }
    }

    /**
     * Absolutely disgusting method of retrieving the largest non-zero times
     * difference in a nanosecond period. Forgive me for my sins
     *
     * @version 0.0.1
     * @since 0.0.1
     *
     * @param diff The difference in nanoseconds between two points
     * @return A new {@link TimePoint} representing the largest represented time
     */
    private static TimePoint getTimePoint(long diff) {
        if (diff < 0) {
            return null;
        }
        long temp;
        TimeUnit u = TimeUnit.NANOSECONDS;
        TimePoint root = null;
        if ((temp = u.toDays(diff)) > 0) {
            root = new TimePoint(temp, TimeUnit.DAYS, null);
            diff -= root.getUnit().toNanos(root.getTime());
        }
        if ((temp = u.toHours(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.HOURS, null);
            root = TimeUtil.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMinutes(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MINUTES, null);
            root = TimeUtil.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toSeconds(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.SECONDS, null);
            root = TimeUtil.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMillis(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MILLISECONDS, null);
            root = TimeUtil.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMicros(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MICROSECONDS, null);
            root = TimeUtil.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if (diff >= 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.NANOSECONDS, null);
            root = TimeUtil.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        return root;
    }

    /**
     * Helper method to {@link TimeUtil#getTimePoint(long)}. Allocates nodes
     * further down the {@link TimePoint} queue.
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param root The root {@link TimePoint} node
     * @param allocate The node to allocate
     * @return The root node
     */
    private static TimePoint allocateNodes(TimePoint root, TimePoint allocate) {
        if (root == null) {
            return allocate;
        } else if (root.getNext() != null) {
            return root.setNext(TimeUtil.allocateNodes(root.getNext(), allocate));
        } else {
            return root.setNext(allocate);
        }
    }

    /**
     * Returns the appropriate spelling for a {@link TimeUnit} value based on
     * the value of the "amount" parameter. Values other than 1 will return a
     * plural measurement whereas 1 will return a singular measurement.
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param unit The unit to beautify
     * @param amount The amount relevant to the measurement output
     * @return A formatted word
     */
    public static String getProperUnitName(TimeUnit unit, long amount) {
        String proper = unit.toString().substring(0, 1) + unit.toString().substring(1, unit.toString().length()).toLowerCase();
        return amount != 1 ? proper : proper.substring(0, proper.length() - 1);
    }

    public static class TimePoint implements Comparable<TimePoint> {

        private final long time;
        private final TimeUnit unit;
        private TimePoint next;

        public TimePoint(long time, TimeUnit unit, TimePoint next) {
            this.time = time;
            this.unit = unit;
            this.next = next;
        }

        public long getAmount(TimeUnit unit) {
            TimePoint point = this.getPoint(unit);
            return point == null ? 0 : point.getTime();
        }

        public TimePoint getPoint(TimeUnit unit) {
            int diff = this.unit.compareTo(unit);
            if (diff < 0) {
                return null;
            }
            TimePoint back = this.next;
            for (;diff > 1; diff--) {
                back = back.next;
            }
            return back;
        }

        public long getTime() {
            return this.time;
        }

        public TimeUnit getUnit() {
            return this.unit;
        }

        public TimePoint getNext() {
            return this.next;
        }
        
        public TimePoint getNextNonZero() {
            if (this.next == null || this.next.getTime() <= 0) {
                return null;
            }
            TimePoint back = this;
            while (back.next != null && back.next.getTime() <= 0) {
                back = back.next;
            }
            return back.next;
        }

        public TimePoint setNext(TimePoint next) {
            this.next = next;
            return this;
        }

        public String properName() {
            return TimeUtil.getProperUnitName(this.unit, this.time);
        }

        public String format(TimeUnit min) {
            return TimeUtil.formatTime(this, min);
        }

        public String format() {
            return this.format(TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(TimePoint o) {
            //Do not check for null, Comparable contract calls for NPE
            int curr = this.unit.compareTo(o.unit);
            if (curr != 0) {
                return curr > 0 ? 1 : -1;
            }
            curr = Long.compare(this.time, o.time);
            if (curr == 0 && this.next != null && o.next != null) {
                curr = this.next.compareTo(o.next);
            }
            return curr > 0 ? 1 : -1;
        }

        /**
         * This is a helper method to find the closest {@link TimePoint} in a
         * collection that is still of a greater value than the passed
         * {@link TimePoint}. This method also truncates the passed collection
         * and will modify its contents! However, if no values are found this
         * method will return null.
         * 
         * TODO: Optimize to use binary search
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param points The {@link TimePoint} objects to look through. Should
         *               be a sorted collection
         * @param now The point of reference (ha!)
         * @return The closest matching point
         */
        public static TimePoint findClosestAndWipe(TreeSet<? extends TimePoint> points, TimePoint now) {
            Iterator<? extends TimePoint> itr = points.iterator();
            while (itr.hasNext()) {
                TimePoint next = itr.next();
                if (next.compareTo(now) > 0) {
                    itr.remove();
                    return next;
                }
            }
            return null;
        }
    }

    public static class Countdown {
        
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
                TimePoint now = TimeUtil.getTimePoint(this.start - System.nanoTime());
                TimePoint ref = TimePoint.findClosestAndWipe(test, now);
                if (ref != null) {
                    //announce
                    Bukkit.broadcastMessage(String.format(this.announcement, ref.format(TimeUnit.SECONDS)));
                }
                this.formatTimes(this.boards).entrySet().forEach(ent -> 
                        ent.getKey().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ent.getValue()));
            }, 0, 500, TimeUnit.MILLISECONDS);
        }

        /**
         * Formats multiple scoreboards at once and caches the default result
         * for any scoreboards without a specified format
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param boards The boards to create formats for
         * @return A {@link Map} of the passed Scoreboards mapped to outputs
         */
        private Map<Scoreboard, String> formatTimes(Collection<Scoreboard> boards) {
            TimePoint now = TimeUtil.getTimePoint(this.start - System.nanoTime());
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
         * Formats the {@link Countdown#defFormat} field and returns it for use
         * in the countdown timer display.
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param board A board to search for a specific timer. Will use the
         *              default formatting if null.
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
            return this.formatReadable(TimeUtil.getTimePoint(left), format);
        }

        /**
         * Takes a time-based format and converts it to a readable output based
         * upon the passed {@link TimePoint}
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
         * {@link DisplaySlot#SIDEBAR}'s title changed in accordance to the
         * format given to this specific board.
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @see Countdown#setTimeFormat(java.lang.String) for info about the
         *      syntax for setting a format string
         * @param s The scoreboard to modify
         * @param format Optional board-specific format. Can be null
         * @return The current class instance (chaining)
         */
        public Countdown assignScoreboard(Scoreboard s, String format) {
            if (format == null) {
                throw new IllegalArgumentException("Format cannot be null!");
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
         * {@link DisplaySlot#SIDEBAR}'s title changed in accordance to the
         * default format
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
         * requires the format tokens ({@code %s} and {@code %d}), but
         * if an additional {@code %s} is supplied it will use the first
         * {@code %d} for displaying hours and the following for minutes and
         * seconds. Additionally, {@code %d} should be
         * the first token in the format string. So in practice:
         * <br><br>
         * {@code .* %d .* %s (optional) .* %s .*}
         * <br><br>
         * The actual regex in use to verify this formatting string is:
         * <br>
         * {@code (?!.*%[^sd])(?!.*%d.*%d)(?!.*%s.*%s.*%s)
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

        public Countdown setAnnouncementFormat(String format) {
            if (!format.matches("(?!.*%[^sd])")) {
                throw new IllegalArgumentException("Countdown format must follow contract! (See javadoc)");
            }
            this.announcement = format;
            return this;
        }

        public Countdown announceAt(long time, TimeUnit unit) {
            return this.announceAt(TimeUtil.getTimePoint(unit.toNanos(time))); //Convert for consistency and large input
        }

        public Countdown announceAt(TimePoint point) {
            this.queue.add(point);
            return this;
        }

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

}
