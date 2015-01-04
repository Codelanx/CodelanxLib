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
package com.codelanx.codelanxlib.data;

import com.codelanx.codelanxlib.util.DebugUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Opens and loads a file into memory using the appropriate data type. This
 * data type should have a single-argument constructor which takes a
 * {@link File} argument.
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public interface FileDataType extends DataType {

    /**
     * Sets the value at the location specified by the passed path
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path The path to set, delimited by '{@code .}'
     * @param value The value to set
     */
    public void set(String path, Object value);

    /**
     * Returns whether or not there is an object located at the specified path
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path The path to set, delimited by '{@code .}'
     * @return {@code true} if a value is found, {@code false} otherwise
     */
    public boolean isSet(String path);

    /**
     * Gets the object at the specified path
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path The path to set, delimited by '{@code .}'
     * @return The object found in memory at this location, or {@code null} if
     *         nothing is found
     */
    public Object get(String path);

    /**
     * Gets the object at the specified path, or returns the passed "default"
     * value if nothing is found
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path The path to set, delimited by '{@code .}'
     * @param def The default value to return upon not finding a value
     * @return The relevant object, or the default if no value is found
     */
    public Object get(String path, Object def);

    /**
     * Saves any information in memory to the file it was loaded from.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @throws IOException Any read/write locks or permission errors on the file
     */
    public void save() throws IOException;

    /**
     * Saves any information in memory to the file specified.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param target The file to save to
     * @throws IOException Any read/write locks or permission errors on the file
     */
    public void save(File target) throws IOException;

    /**
     * Returns a new instance of a {@link FileDataType} based on the passed
     * class instance.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <T> Represents the type that implements {@link FileDataType}
     * @param clazz The class object to be used for a new instance
     * @param location The location of the file to parse and use
     * @return The new instance of the requested {@link FileDataType}
     */
    public static <T extends FileDataType> T newInstance(Class<T> clazz, File location) {
        try {
            Constructor r = clazz.getConstructor(File.class);
            r.setAccessible(true);
            return (T) r.newInstance(location);
        } catch (NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            DebugUtil.error("Error parsing data file!", ex);
        }
        return null;
    }

}
