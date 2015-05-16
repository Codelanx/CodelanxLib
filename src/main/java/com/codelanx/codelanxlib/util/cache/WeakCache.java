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

import java.lang.ref.WeakReference;
import java.util.function.Supplier;

/**
 * Represents a single variable which will be dynamically updated upon an
 * internal {@link WeakReference} being {@code null} upon accessing
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 * 
 * @param <E> The type of the cached variable
 */
public class WeakCache<E> {

    private volatile WeakReference<E> value;
    private Supplier<? extends E> update;

    /**
     * Stores the {@link Supplier} for the cached value
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param update Called when a cached value is null
     */
    public WeakCache(Supplier<? extends E> update) {
        this(update, false);
    }

    /**
     * Stores the {@link Supplier} for the cached value
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param update Called when a cached value is null
     * @param forceRefresh {@code true} to refresh and retrieve the cached
     *                     instance upon construction, instead of on the first
     *                     call to {@link Cache#get()}
     */
    public WeakCache(Supplier<? extends E> update, boolean forceRefresh) {
        this.update = update;
        if (forceRefresh) {
            this.forceRefresh();
        }
    }


    /**
     * Calls a check to see if the cache needs to be updated, and returns the
     * stored variable
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The stored value of this {@link WeakCache}
     */
    public final E get() {
        this.checkCache();
        return this.value.get();
    }

    /**
     * Checks if it is time to refresh the current variable
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    private synchronized void checkCache() {
        if (this.value == null || this.value.get() == null) {
            this.setNextCache();
        }
    }

    /**
     * Forcibly updates the variable and sets the next point in time for an
     * update
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    private void setNextCache() {
        this.setCurrentValue(this.update.get());
    }

    /**
     * Returns the currently in-use variable without checking the cache
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The current value of this {@link WeakCache}
     */
    protected E getCurrentValue() {
        return this.value == null ? null : this.value.get();
    }

    /**
     * Sets the value of this {@link WeakCache}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param value The new value to set
     */
    protected void setCurrentValue(E value) {
        this.value = new WeakReference<>(value);
    }

    /**
     * Forcibly refreshes the current value of this {@link WeakCache}
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public final void forceRefresh() {
        this.setNextCache();
    }

}
