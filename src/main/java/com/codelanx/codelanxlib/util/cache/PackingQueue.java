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
package com.codelanx.codelanxlib.util.cache;

import com.codelanx.codelanxlib.util.Scheduler;
import java.util.LinkedList;
import java.util.function.Consumer;
import org.apache.commons.lang.Validate;

/**
 * Represents a queue which flushes all of its elements upon reaching a maximum
 * size
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 * 
 * @see LinkedList
 * @param <E> The type of objects in this queue
 */
public class PackingQueue<E> extends LinkedList<E> {

    private final int maxSize;
    private final Consumer<E> onConsume;
    private final boolean threaded;

    /**
     * Initializes this {@link PackingQueue} for use
     * 
     * @see #PackingQueue(int, Consumer, boolean)
     * @param maxSize The maximum size before flushing elements
     * @param onConsume How to handle each element before removing it
     */
    public PackingQueue(int maxSize, Consumer<E> onConsume) {
        this(maxSize, onConsume, false);
    }

    /**
     * Initializes this {@link PackingQueue} for use
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param maxSize The maximum size before flushing elements
     * @param onConsume How to handle each element before removing it
     * @param threaded {@code true} to execute the element flushing on another
     *                 thread
     */
    public PackingQueue(int maxSize, Consumer<E> onConsume, boolean threaded) {
        Validate.isTrue(maxSize > 0, "maxSize must be greater than 0");
        Validate.notNull(onConsume);
        this.maxSize = maxSize;
        this.onConsume = onConsume;
        this.threaded = threaded;
    }

    @Override
    public synchronized boolean add(E e) {
        this.handle();
        return super.add(e);
    }

    @Override
    public synchronized void addFirst(E e) {
        this.handle();
        super.addFirst(e);
    }

    @Override
    public synchronized void addLast(E e) {
        this.handle();
        super.addLast(e);
    }

    private void handle() {
        if (this.size() >= this.maxSize) {
            this.setup();
        }
    }

    private void setup() {
        if (this.threaded) {
            Scheduler.runAsyncTask(() -> this.flush(), 0);
        } else {
            this.flush();
        }
    }

    private void flush() {
        this.forEach(this.onConsume::accept);
        this.clear();
    }
}
