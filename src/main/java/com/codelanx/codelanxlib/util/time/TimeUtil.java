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

import java.util.concurrent.TimeUnit;

/**
 * Provides utility method for determining differences in time as well as
 * providing ways to utilize things such as timers
 * 
 * TODO: Refactor class in a way that works with time points in every time
 *       setting, and consider moving to use Java 8's Time API
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class TimeUtil {

    private TimeUtil() {
    }

    /**
     * Returns a formatted string of how long ago the passed time was
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param start How long ago something was in nanoseconds
     * @return A formatted string representing the passed point in time
     */
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
     * measurement
     * @return The formatted string
     */
    public static String formatTime(long durationNS, TimeUnit min) {
        if (min == null) {
            throw new IllegalArgumentException("Minimum TimeUnit cannot be null!");
        }
        TimePoint point = TimePoint.getTimePoint(durationNS);
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
     * measurement
     * @return The formatted string
     */
    public static String formatTime(TimePoint point, TimeUnit min) {
        TimePoint next = point.getNextNonZero();
        if (next != null && next.getUnit().compareTo(min) > 0) {
            return String.format("%d %s, %d %s", point.getTime(), point.properName(),
                    next.getTime(), next.properName());
        } else {
            return String.format("%d %s", point.getTime(), point.properName());
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

}
