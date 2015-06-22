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
package com.codelanx.codelanxlib.util.number;

/**
 * Holds a single reference to a stored object
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 * 
 * @param <E> The type of the boxed item
 */
public class Single<E> {

    private E val;

    /**
     * Constructor. Assigns the value to an internal field
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param val The value to set
     */
    public Single(E val) {
        this.val = val;
    }

    /**
     * Retrieves the stored value
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The stored object
     */
    public E getValue() {
        return this.val;
    }

    /**
     * Sets the stored value
     * 
     * @since 0.0.1
     * @version 0.0.1
     *  
     * @param val The value to set
     * @return This {@link Single} instance (chained)
     */
    public Single<E> setValue(E val) {
        this.val = val;
        return this;
    }

}