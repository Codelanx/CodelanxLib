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
package com.codelanx.codelanxlib.util.ref;

/**
 * Represents a wrapper class for a pair of objects
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.2.0
 * 
 * @param <E> The first object type
 * @param <T> The second object type
 */
public class Tuple<E, T> {
    
    private final E one;
    private final T two;

    /**
     * Constructor. Stores the two values
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param one The first parameter
     * @param two The second parameter
     */
    public Tuple(E one, T two) {
        this.one = one;
        this.two = two;
    }

    /**
     * Retrieves the first parameter
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The first parameter
     */
    public E getFirst() {
        return this.one;
    }

    /**
     * Retrieves the second parameter
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The second parameter
     */
    public T getSecond() {
        return this.two;
    }

}
