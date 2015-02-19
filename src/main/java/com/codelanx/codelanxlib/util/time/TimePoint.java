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

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Represents a relative, specific amount of time
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public class TimePoint implements Comparable<TimePoint> {

    private final long time;
    private final TimeUnit unit;
    private TimePoint next;

    /**
     * Constructs a new {@link TimePoint}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param time The magnitude or measure of time
     * @param unit The {@link TimeUnit} representing the unit of time
     * @param next The next {@link TimePoint} in this chain
     */
    public TimePoint(long time, TimeUnit unit, TimePoint next) {
        this.time = time;
        this.unit = unit;
        this.next = next;
    }

    /**
     * Returns the relevant measure for the specified unit of time
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param unit The {@link TimeUnit} of time to look for
     * @return The amount of the specific unit of time in this {@link TimePoint}
     */
    public long getAmount(TimeUnit unit) {
        TimePoint point = this.getPoint(unit);
        return point == null ? 0 : point.getTime();
    }

    /**
     * Returns the specific {@link TimePoint} for the passed {@link TimeUnit}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param unit The {@link TimeUnit} of time to look for
     * @return The specific {@link TimePoint} representing the amount of the
     *         specific unit
     */
    public TimePoint getPoint(TimeUnit unit) {
        int diff = this.unit.compareTo(unit);
        if (diff < 0) {
            return null;
        }
        TimePoint back = this.next;
        for (; diff > 1; diff--) {
            back = back.next;
        }
        return back;
    }

    /**
     * Returns the magnitude (or measure) of the current unit for this point
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The amount of the current {@link TimeUnit}
     */
    public long getTime() {
        return this.time;
    }

    /**
     * Returns the relevant {@link TimeUnit} for the measure in this point
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The relevant {@link TimeUnit} to this current measure
     */
    public TimeUnit getUnit() {
        return this.unit;
    }

    /**
     * Returns the next {@link TimePoint} for this measure of time. For
     * instance, if the current {@link TimePoint} is a measure of minutes,
     * then the proceeding point will be a measure of seconds
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The next {@link TimePoint} in this chain
     */
    public TimePoint getNext() {
        return this.next;
    }

    /**
     * Returns the next non-zero {@link TimePoint} that can be found, or
     * {@code null} if there are no proceeding points of measure that are
     * non-null and non-zero
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The next {@link TimePoint} that is not zero, or {@code null}
     */
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

    /**
     * Sets the next {@link TimePoint} for this measure
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param next
     * @return The current instance (chained)
     */
    private TimePoint setNext(TimePoint next) {
        this.next = next;
        return this;
    }

    /**
     * Returns the proper name for the current unit relevant to this point
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The proper name for the {@link TimeUnit} in use 
     */
    public String properName() {
        return TimeUtil.getProperUnitName(this.unit, this.time);
    }

    /**
     * Formats the current {@link TimePoint} to be human-readable, specifying
     * either one or two units of measure depending on what is available
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param min The minimum {@link TimeUnit} to output
     * @return The formatted string
     */
    public String format(TimeUnit min) {
        return TimeUtil.formatTime(this, min);
    }

    /**
     * Returns a formatted string with a minimum unit of
     * {@link TimeUnit#NANOSECONDS}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @see TimePoint#format(TimeUnit)
     * @return 
     */
    public String format() {
        return this.format(TimeUnit.NANOSECONDS);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     */
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
     * {@link TimePoint}. This method also truncates the passed collection and
     * will modify its contents! However, if no values are found this method
     * will return null.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param points The {@link TimePoint} objects to look through. Should be a
     * sorted collection
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
    public static TimePoint getTimePoint(long diff) {
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
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMinutes(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MINUTES, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toSeconds(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.SECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMillis(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MILLISECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMicros(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MICROSECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if (diff >= 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.NANOSECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        return root;
    }

    /**
     * Helper method to {@link TimePoint#getTimePoint(long)}. Allocates nodes
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
            return root.setNext(TimePoint.allocateNodes(root.getNext(), allocate));
        } else {
            return root.setNext(allocate);
        }
    }

}
