/*
 * Copyright (C) 2016 Codelanx, All Rights Reserved
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
package com.codelanx.codelanxlib.serialize;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Façade for registering {@link ConfigurationSerializable} classes to Bukkit
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public class SerializationFactory {

    /**
     * Registers a single {@link ConfigurationSerializable} class to Bukkit
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param clazz The class to register
     */
    public static void registerClass(Class<? extends ConfigurationSerializable> clazz) {
        ConfigurationSerialization.registerClass(clazz);
    }

    /**
     * Registers multiple {@link ConfigurationSerializable} class to Bukkit
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param clazz The classes to register
     */
    public static void registerClasses(Class<? extends ConfigurationSerializable>... clazz) {
        for (Class<? extends ConfigurationSerializable> c : clazz) {
            SerializationFactory.registerClass(c);
        }
    }

    /**
     * Registers multiple {@link ConfigurationSerializable} class to Bukkit
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param clazz The classes to register
     */
    public static void registerClasses(Iterable<? extends Class<? extends ConfigurationSerializable>> clazz) {
        clazz.forEach(SerializationFactory::registerClass);
    }

    /**
     * Returns the native {@link ConfigurationSerializable} classes that are
     * provided by CodelanxLib
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The native serializable types
     */
    @SuppressWarnings("rawtypes")
    public static Class[] getNativeSerializables() {
        return new Class[] {
            SInventory.class,
            SPlayerInventory.class,
            SLocation.class
        };
    }
}
