/*
 * Copyright (C) 2014 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2014 or as published
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

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class description for {@link DebugUtil}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class DebugUtil {
    
    private static boolean ENABLED = true;

    public static class TimeUtil {

        public static String getTime(long start) {
            return TimeUtil.formatTime(System.nanoTime() - start);
        }

        public static String formatTime(long durationNS) {
            TimePoint point = TimeUtil.getTimePoint(durationNS);
            if (point.getNext() != null) {
                return String.format("%d %s, %d %s", point.getTime(), TimeUtil.getProperUnitName(point.getUnit(), point.getTime()),
                        point.getNext().getTime(), TimeUtil.getProperUnitName(point.getNext().getUnit(), point.getNext().getTime()));
            } else {
                return String.format("%d %s", point.getTime(), TimeUtil.getProperUnitName(point.getUnit(), point.getTime()));
            }
        }

        private static class TimePoint {

            private final long time;
            private final TimeUnit unit;
            private TimePoint next;

            public TimePoint(long time, TimeUnit unit, TimePoint next) {
                this.time = time;
                this.unit = unit;
                this.next = next;
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

            public TimePoint setNext(TimePoint next) {
                this.next = next;
                return this;
            }
        }

        /**
         * Absolutely disgusting method of retrieving the largest non-zero times
         * difference in a nanosecond period. Forgive me for my sins
         *
         * @version 1.0.0
         * @since 1.0.0
         *
         * @param diff The difference in nanoseconds between two points
         * @return A new {@link TimePoint} representing the largest represented
         * time
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

        private static TimePoint allocateNodes(TimePoint root, TimePoint allocate) {
            if (root == null) {
                return allocate;
            } else if (root.getNext() != null) {
                return root.setNext(TimeUtil.allocateNodes(root.getNext(), allocate));
            } else {
                return root.setNext(allocate);
            }
        }

        private static String getProperUnitName(TimeUnit unit, long amount) {
            String proper = unit.toString().substring(0, 1) + unit.toString().substring(1, unit.toString().length()).toLowerCase();
            return amount != 1 ? proper : proper.substring(0, proper.length() - 1);
        }
    }

    public static void print(Level level, String format, Object... args) {
        if (!DebugUtil.ENABLED) { return; }
        Logger.getLogger(DebugUtil.class.getName()).log(level,
                "[debug] {0}", String.format(format, args));
    }

    public static void print(String format, Object... args) {
        DebugUtil.print(Level.INFO, format, args);
    }

    public static void error(String message, Throwable error) {
        Logger.getLogger(DebugUtil.class.getName()).log(Level.SEVERE,
                message, error);
    }

    public static void toggleOutput(boolean output) {
        DebugUtil.ENABLED = output;
    }

}
