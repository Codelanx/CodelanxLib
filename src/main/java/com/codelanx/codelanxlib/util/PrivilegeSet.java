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

import com.codelanx.codelanxlib.util.exception.Exceptions;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Allows using enum constants for permissions, up to 64 constants. In context
 * of this documentation, "privilege" refers to any enum constant used in this
 * set
 *
 * @since 0.1.0
 * @author 1Rogue
 * @author Fireblast709 Helped with some bitshifting
 * @version 0.1.0
 * 
 * @param <E> The type of the enum this {@link PrivilegeSet} applies to
 */
public class PrivilegeSet<E extends Enum> { //Purposefully raw-typed

    private long level;

    /**
     * Constructs an empty {@link PrivilegeSet}
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public PrivilegeSet() {
        this(0);
    }

    /**
     * Constructs a new {@link PrivilegeSet} with the passed privilege level
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param level 
     */
    public PrivilegeSet(int level) {
        this.level = level;
    }

    /**
     * Returns {@code true} if this set contains the privilege
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param t The privilege to check for
     * @return {@code true} if this set contains the privilege
     */
    public boolean has(E t) {
        if (this.level == 0) {
            return false;
        }
        long val = this.level & this.powerfy(t);
        return val > 0 || val == this.level;
    }

    /**
     * Adds a privilege to this set. Does nothing if this set already contains
     * the privilege
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param t The privilege to add
     */
    public void add(E t) {
        if (!this.has(t)) {
            this.level |= this.powerfy(t);
        }
    }

    /**
     * Removes a privilege from this set. Does nothing if this set does not
     * contain the privilege
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param t The privilege to remove
     */
    public void remove(E t) {
        if (this.has(t)) {
            this.level &= ~this.powerfy(t);
        }
    }

    /**
     * Clears the current set of all privileges
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public void clear() {
        this.level = 0;
    }

    /**
     * Returns the underlying level representing this set, for use in storage
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The underlying level that represents this set
     */
    public long getLevel() {
        return this.level;
    }

    /**
     * Converts an enum constant into its appropriate magnitude
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param t The privilege to convert
     * @return The relevant power level
     */
    private long powerfy(E t) {
        return 1 << t.ordinal();
    }

    /**
     * Converts this {@link PrivilegeSet} into a {@link EnumSet} containing all
     * the elements that this set "has".
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param clazz The class of the enum used in this set
     * @return An {@link EnumSet} of the privileges
     */
    public Set<E> toSet(Class<E> clazz) {
        E[] cn = clazz.getEnumConstants();
        Exceptions.illegalState(cn.length <= 64, "Cannot support enums with over 64 constants!");
        Set<E> temp = new HashSet<>();
        for (E e : cn) {
            if (this.has(e)) {
                temp.add(e);
            } 
        }
        return temp.isEmpty() ? temp : EnumSet.copyOf(temp);
    }

}
