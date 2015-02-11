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

import com.codelanx.codelanxlib.annotation.PluginClass;
import com.google.common.primitives.Primitives;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class description for {@link Reflections}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Reflections {

    private Reflections() {
    }

    /**
     * Returns the relevant {@link JavaPlugin} that is specified by a
     * class-level {@link PluginClass} annotation if it is loaded, otherwise
     * {@code null}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param clazz The {@link Class} that holds the {@link Annotation}
     * @return The relevant {@link JavaPlugin}, or {@code null} if not found
     */
    public static JavaPlugin getPlugin(Class<?> clazz) {
        PluginClass pc = clazz.getAnnotation(PluginClass.class);
        if (pc == null) {
            return null;
        }
        return JavaPlugin.getPlugin(pc.value());
    }

    /**
     * Returns {@code true} if the specified target has the passed
     * {@link Annotation}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param target The relevant element to check for an {@link Annotation}
     * @param check The {@link Annotation} class type to check for
     * @return {@code true} if the {@link Annotation} is present
     */
    public static boolean hasAnnotation(AnnotatedElement target, Class<? extends Annotation> check) {
        return target.getAnnotation(check) != null;
    }

    /**
     * Returns whether or not the current context was called from a class
     * (instance or otherwise) that is passed to this method. This method can
     * match a regex pattern for multiple classes. Note anonymous classes have
     * an empty name.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param regex The regex to check against the calling class
     * @return {@code true} if accessed from a class that matches the regex
     */
    public static boolean accessedFrom(String regex) {
        return Reflections.getCaller(1).getClass().getName().matches(regex);
    }

    /**
     * Returns whether or not the current context was called from a class
     * (instance or otherwise) that is passed to this method. Note anonymous
     * classes have an empty name.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param clazz The class to check
     * @return {@code true} if accessed from this class
     */
    public static boolean accessedFrom(Class<?> clazz) {
        return Reflections.getCaller(1).getClass() == clazz;
    }

    /**
     * Façade method for determining if Bukkit is the invoker of the method
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return {@code true} if Bukkit is the direct invoker of the method
     */
    public static boolean accessedFromBukkit() {
        return Reflections.accessedFrom("org\\.bukkit\\..*");
    }

    /**
     * Returns the {@link JavaPlugin} that immediately called the method in the
     * current context. Useful for finding out which plugins accessed static API
     * methods
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param offset The number of additional methods to look back
     * @return The relevant {@link JavaPlugin}
     * @throws UnsupportedOperationException If not called from a
     * {@link JavaPlugin} class (either through an alternative ClassLoader,
     * executing code directly, or some voodoo magic)
     */
    public static JavaPlugin getCallingPlugin(int offset) {
        JavaPlugin back = JavaPlugin.getProvidingPlugin(Reflections.getCaller(1 + offset).getClass());
        if (back == null) {
            throw new UnsupportedOperationException("Must be called from a class loaded from a plugin");
        }
        return back;
    }

    /**
     * Returns the {@link JavaPlugin} that immediately called the method in the
     * current context. Useful for finding out which plugins accessed static API
     * methods. This method is equivalent to calling
     * {@code Reflections.getCallingPlugin(0)}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @see Reflections#getCallingPlugin(int)
     * @return The relevant {@link JavaPlugin}
     * @throws UnsupportedOperationException If not called from a
     * {@link JavaPlugin} class (either through an alternative ClassLoader,
     * executing code directly, or some voodoo magic)
     */
    public static JavaPlugin getCallingPlugin() {
        return Reflections.getCallingPlugin(1);
    }

    /**
     * Returns a {@link StackTraceElement} of the direct caller of the current
     * method's context.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param offset The number of additional methods to look back
     * @return A {@link StackTraceElement} representing where the current
     *         context was called from
     */
    public static StackTraceElement getCaller(int offset) {
        Validate.isTrue(offset >= 0, "Offset must be a positive number");
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        if (elems.length < 3 + offset) {
            //We shouldn't be able to get this high on the stack at offset 0
            throw new IndexOutOfBoundsException("Offset too large for current stack");
        }
        return elems[2 + offset];
    }

    /**
     * Returns a {@link StackTraceElement} of the direct caller of the current
     * method's context. This method is equivalent to calling
     * {@code Reflections.getCaller(0)}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return A {@link StackTraceElement} representing where the current
     *         context was called from
     */
    public static StackTraceElement getCaller() {
        return Reflections.getCaller(1);
    }

    /**
     * Returns a "default value" of -1 or {@code false} for a default type's
     * class or autoboxing class. Will return {@code null} if not relevant to
     * primitives
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The type of the primitive
     * @param c The primitive class
     * @return The default value, or {@code null} if not a primitive
     */
    public static <T> T defaultPrimitiveValue(Class<T> c) {
        if (c.isPrimitive() || Primitives.isWrapperType(c)) {
            c = Primitives.unwrap(c);
            T back;
            if (c == boolean.class) {
                back = c.cast(false);
            } else if (c == char.class) { //god help me
                back = c.cast((char) -1);
            } else if (c == float.class) {
                back = c.cast(-1F);
            } else if (c == long.class) {
                back = c.cast(-1L);
            } else if (c == double.class) {
                back = c.cast(-1D);
            } else if (c == int.class) {
                back = c.cast(-1); //ha
            } else if (c == short.class) {
                back = c.cast((short) -1);
            } else if (c == byte.class) {
                back = c.cast((byte) -1);
            }
        }
        return null;
    }

}
