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
package com.codelanx.codelanxlib.util.exception;

/**
 * A utility class for throwing conditional exceptions, similar to Apache's
 * {@link org.apache.commons.lang.Validate Validate} class.
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Exceptions {

    private Exceptions() {}

    /**
     * Throws an {@link IllegalStateException} if the value of the {@code state}
     * parameter is {@code false}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param state The conditional to verify
     * @param message The message to include in an {@link IllegalStateException}
     * @throws IllegalStateException if {@code state} is {@code false}
     */
    public static void illegalState(boolean state, String message) {
        if (!state) {
            if (message != null) {
                throw new IllegalStateException(message);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Throws an {@link IllegalStateException} if the value of the {@code state}
     * parameter is {@code false}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param state The conditional to verify
     * @throws IllegalStateException if {@code state} is {@code false}
     */
    public static void illegalState(boolean state) {
        Exceptions.illegalState(state, null);
    }

    /**
     * Throws an {@link IllegalPluginAccessException} if the value of the
     * {@code state} parameter is {@code false}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param state The conditional to verify
     * @param message The message to include in an
     *                {@link IllegalPluginAccessException}
     * @throws IllegalPluginAccessException if {@code state} is {@code false}
     */
    public static void illegalPluginAccess(boolean state, String message) {
        if (!state) {
            if (message != null) {
                throw new IllegalPluginAccessException(message);
            } else {
                throw new IllegalPluginAccessException();
            }
        }
    }

    /**
     * Throws an {@link IllegalPluginAccessException} if the value of the
     * {@code state} parameter is {@code false}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param state The conditional to verify
     * @throws IllegalPluginAccessException if {@code state} is {@code false}
     */
    public static void illegalPluginAccess(boolean state) {
        Exceptions.illegalPluginAccess(state, null);
    }

    /**
     * Throws an {@link UnsupportedOperationException} if the value of the
     * {@code state} parameter is {@code false}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param state The conditional to verify
     * @param message The message to include in an
     *                {@link UnsupportedOperation}
     * @throws UnsupportedOperationException if {@code state} is {@code false}
     */
    public static void unsupportedOperation(boolean state, String message) {
        if (!state) {
            if (message != null) {
                throw new UnsupportedOperationException(message);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Throws an {@link UnsupportedOperationException} if the value of the
     * {@code state} parameter is {@code false}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param state The conditional to verify
     * @throws UnsupportedOperationException if {@code state} is {@code false}
     */
    public static void unsupportedOperation(boolean state) {
        Exceptions.unsupportedOperation(state, null);
    }

}
