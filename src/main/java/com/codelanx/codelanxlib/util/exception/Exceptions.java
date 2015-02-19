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

import com.codelanx.codelanxlib.logging.Debugger;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * A utility class for throwing conditional exceptions, similar to Apache's
 * {@link org.apache.commons.lang.Validate Validate} class.
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Exceptions {

    private Exceptions() {
    }

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
            throw Exceptions.newException(IllegalStateException.class, message);
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
     * {@link IllegalPluginAccessException}
     * @throws IllegalPluginAccessException if {@code state} is {@code false}
     */
    public static void illegalPluginAccess(boolean state, String message) {
        if (!state) {
            throw Exceptions.newException(IllegalPluginAccessException.class, message);
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
     *                {@link UnsupportedOperationException}
     * @throws UnsupportedOperationException if {@code state} is {@code false}
     */
    public static void unsupportedOperation(boolean state, String message) {
        if (!state) {
            throw Exceptions.newException(UnsupportedOperationException.class, message);
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

    /**
     * Provides a null check, and throws a custom {@link RuntimeException} as
     * specified by the passed class parameter
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The exception type
     * @param obj The object to check for {@code null}
     * @param message The message to add to the exception if {@code obj} is null
     * @param ex The {@link RuntimeException} class to instantiate
     */
    public static <T extends RuntimeException> void notNull(Object obj, String message, Class<T> ex) {
        Exceptions.isTrue(obj != null, message, ex);
        if (obj == null) {
            throw Exceptions.newException(ex, message);
        }
    }

    /**
     * Provides a null check, and throws a custom {@link RuntimeException} as
     * specified by the passed class parameter
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The exception type
     * @param obj The object to check for {@code null}
     * @param ex The {@link RuntimeException} class to instantiate
     */
    public static <T extends RuntimeException> void notNull(Object obj, Class<T> ex) {
        Exceptions.notNull(obj, null, ex);
    }

    /**
     * Validates a statement is {@code true}, and throws a custom
     * {@link RuntimeException} as specified by the passed class parameter
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The exception type
     * @param validate The {@code boolean} to evaluate
     * @param message The message to add to the exception if {@code obj} is null
     * @param ex The {@link RuntimeException} class to instantiate
     */
    public static <T extends RuntimeException> void isTrue(boolean validate, String message, Class<T> ex) {
        if (!validate) {
            throw Exceptions.newException(ex, message);
        }
    }

    /**
     * Validates a statement is {@code true}, and throws a custom
     * {@link RuntimeException} as specified by the passed class parameter
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The exception type
     * @param validate The {@code boolean} to evaluate
     * @param ex The {@link RuntimeException} class to instantiate
     */
    public static <T extends RuntimeException> void isTrue(boolean validate, Class<T> ex) {
        Exceptions.isTrue(validate, null, ex);
    }

    /**
     * Dynamically constructs a new instance of a {@link RuntimeException}
     * either via a {@link RuntimeException#RuntimeException(String)}
     * constructor or a no-argument constructor
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The {@link RuntimeException} type
     * @param ex The exception class to instantiate
     * @param message The message to add, {@code null} if there is no message
     * @return The newly constructed {@link RuntimeException}
     */
    private static <T extends RuntimeException> T newException(Class<T> ex, String message) {
        if (message != null) {
            try {
                Constructor<T> c = ex.getConstructor(String.class);
                c.setAccessible(true);
                return c.newInstance(message);
            } catch (NoSuchMethodException e) {
                Debugger.print(Level.WARNING, String.format("Class '%s' does not have a String "
                        + "message constructor! Using default constructor...", ex.getName()));
            } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Debugger.error(e, "Error creating new exception instance");
            }
        }
        //Now try here if the previous failed or message wasn't null
        try {
            return ex.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Debugger.error(e, "Error creating new exception instance");
        }
        throw new IllegalArgumentException(String.format("Class '%s' does not have the "
                + "appropriate constructors to be instantiated", ex.getName()));
    }

    /**
     * Creates a readable stack trace from a passed {@link Throwable}. This
     * method will reproduce the same output that
     * {@link Throwable#printStackTrace()} would output
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param t The {@link Throwable} to make readable
     * @return A string representing the entire stack trace
     */
    public static String readableStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement elem : trace) {
            sb.append("\tat ").append(elem).append('\n');
        }
        if (t.getCause() != null) {
            Exceptions.readableStackTraceAsCause(sb, t.getCause(), trace);
        }
        return sb.toString();
    }

    /**
     * Recursive method for appending {@link Throwable] causes that are appended
     * to a {@link Throwable}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param sb The {@link StringBuilder} being appended to
     * @param t The {@link Throwable} root
     * @param causedTrace An array of already visited stack nodes
     */
    private static void readableStackTraceAsCause(StringBuilder sb, Throwable t, StackTraceElement[] causedTrace) {
        // Compute number of frames in common between previous and caused
        StackTraceElement[] trace = t.getStackTrace();
        int m = trace.length - 1;
        int n = causedTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
            m--; n--;
        }
        int common = trace.length - 1 - m;

        sb.append("Caused by: ").append(t).append('\n');
        for (int i = 0; i <= m; i++) {
            sb.append("\tat ").append(trace[i]).append('\n');
        }
        if (common != 0) {
            sb.append("\t... ").append(common).append(" more\n");
        }
        if (t.getCause() != null) {
            Exceptions.readableStackTraceAsCause(sb, t.getCause(), trace);
        }
    }

}
