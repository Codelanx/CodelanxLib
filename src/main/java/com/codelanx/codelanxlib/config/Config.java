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
package com.codelanx.codelanxlib.config;

import java.io.IOException;
import java.util.Map;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Class description for {@link Config}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 * 
 * @param <E> Represents the enum type that implements this interface
 */
public interface Config<E extends Enum<E> & Config<E>> extends PluginFile<E> {

    /**
     * Attempts to return the {@link Config} value as a casted type. If the
     * value cannot be casted it will attempt to return the default value. If
     * the default value is inappropriate for the class, the method will
     * throw a {@link ClassCastException}.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <T> The type of the casting class
     * @param c The class type to cast to
     * @return A casted value, or {@code null} if unable to cast
     */
    default public <T> T as(Class<T> c) {
        Object o = this.get();
        if (o == null) { //Add safety check for primitive classes?
            return null;
        }
        if (c.isInstance(o)) {
            return c.cast(o);
        }
        if (c.isInstance(this.getDefault())) {
            return c.cast(this.getDefault());
        }
        throw new ClassCastException();
    }

    /**
     * This method adds null-safety to the return value to avoid a
     * {@link NullPointerException}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @see Config#as(java.lang.Class)
     * @param <T> The type of the casting class
     * @param c The class type to cast to
     * @return A casted value, -1 for null numbers, and {@code false} for
     *         booleans.
     */
    default public <T> T asPrimitive(Class<T> c) {
        T back = this.as(c);
        if (back == null) {
            if (c == Boolean.class) {
                back = c.cast(false);
            } /*else if (Number.class.isAssignableFrom(c) || c == Character.class) {
                back = c.cast(-1); // Will throw cast exceptions for Long etc.
            }*/
            //god help me
            else if (c == Character.class) {
                back = c.cast((char) -1);
            } else if (c == Float.class) {
                back = c.cast(-1F);
            } else if (c == Long.class) {
                back = c.cast(-1L);
            } else if (c == Double.class) {
                back = c.cast(-1D);
            } else if (c == Integer.class) {
                back = c.cast(-1); //ha
            } else if (c == Short.class) {
                back = c.cast((short) -1);
            } else if (c == Byte.class) {
                back = c.cast((byte) -1);
            }
        }
        return back;
    }

    /**
     * Gets the current object in memory
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The Object found at the relevant location
     */
    default public Object get() {
        return this.getConfig().get(this.getPath(), this.getDefault());
    }

    /**
     * Sets a value in the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param val The value to set
     */
    default public void set(Object val) {
        this.getConfig().set(this.getPath(), val);
    }

    /**
     * Returns a {@link Map} representative of the passed Object that represents
     * a section of a YAML file. This method neglects the implementation of the
     * section (whether it be {@link MemorySection} or just a {@link Map}), and
     * returns the appropriate value.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param o The object to interpret
     * @return A {@link Map} representing the section
     */
    public static Map<String, Object> getConfigSectionValue(Object o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> map;
        if (o instanceof MemorySection) {
            map = ((MemorySection) o).getValues(false);
        } else if (o instanceof Map) {
            map = (Map<String, Object>) o;
        } else {
            return null;
        }
        return map;
    }

}