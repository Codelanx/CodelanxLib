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
package com.codelanx.codelanxlib.config;

import com.codelanx.codelanxlib.data.FileDataType;
import com.codelanx.codelanxlib.util.Reflections;
import com.google.common.primitives.Primitives;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Class description for {@link Config}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
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
     * @return A casted value, or {@code null} if unable to cast. If the passed
     *         class parameter is of a primitive type or autoboxed primitive,
     *         then a casted value of -1 is returned, or {@code false} for
     *         booleans
     */
    default public <T> T as(Class<T> c) {
        Validate.notNull(c, "Cannot cast to null!");
        Validate.isTrue(Primitives.unwrap(c) != void.class, "Cannot cast to a void type!");
        Object o = this.get();
        if (o == null) {
            return Reflections.defaultPrimitiveValue(c);
        }
        if (c.isInstance(o)) {
            return c.cast(o);
        }
        if (c.isInstance(this.getDefault())) {
            return c.cast(this.getDefault());
        }
        throw new ClassCastException("Unable to cast config value!");
    }

    /**
     * Gets the current object in memory
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The Object found at the relevant location
     */
    default public Object get() {
        return this.getConfig().get(this.getPath(), this.getDefault());
    }

    /**
     * Sets a value in the {@link FileDataType}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param val The value to set
     * @return The previous {@link Config} value
     */
    default public Config<?> set(Object val) {
        this.getConfig().set(this.getPath(), val);
        return this;
    }

    /**
     * Returns a {@link Map} representative of the passed Object that represents
     * a section of a YAML file. This method neglects the implementation of the
     * section (whether it be {@link MemorySection} or just a {@link Map}), and
     * returns the appropriate value.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param o The object to interpret
     * @return A {@link Map} representing the section
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getConfigSectionValue(Object o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> map;
        if (o instanceof ConfigurationSection) {
            map = ((ConfigurationSection) o).getValues(false);
        } else if (o instanceof Map) {
            map = (Map<String, Object>) o;
        } else {
            return null;
        }
        return map;
    }

    /**
     * Retrieves an {@link AnonymousConfig} value which can utilize a
     * {@link Config} parameter to retrieve data from any source
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <T> Represents a {@link FileDataType} passed to the method
     * @param file The {@link FileDataType} to use
     * @param config The {@link Config} value to search with
     * @return An {@link AnonymousConfig} wrapping the configuration and keys
     */
    public static <T extends FileDataType> Config<?> retrieve(T file, Config<?> config) {
        return new AnonymousConfig<>(file, config.getPath(), config.getDefault());
    }

    /**
     * Facade method for {@link Config#retrieve(FileDataType, Config)}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param t A {@link FileDataType} to retrieve this config value from
     * @see Config#retrieve(FileDataType, Config)
     * @return A config value that can be used to retrieve values from
     */
    default public Config<?> fromOther(FileDataType t) {
        return Config.retrieve(t, this);
    }

}