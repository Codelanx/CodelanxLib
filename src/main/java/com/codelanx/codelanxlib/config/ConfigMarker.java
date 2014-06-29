/*
 * Copyright (C) 2014 CodeLanx , All Rights Reserved
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
 * along with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Defines an interface which can be implemented into an enum to allow it to be
 * utilized by a {@link ConfigurationLoader}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 *
 * @param <E> The enum implementing this interface
 */
public interface ConfigMarker<E extends Enum<E> & ConfigMarker<E>> {

    /**
     * Returns the string path to the value
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The string path to the value
     */
    public String getPath();

    /**
     * Returns the default value of the key
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The key's default value
     */
    public Object getDefault();

    /**
     * Allows you to dynamically set the values of a passed enum class to a
     * {@link FileConfiguration}.
     * <br><br>
     * How to call this method:<br>
     * {@code ConfigMarker.setDefaults(#FileConfiguration, YourEnum.class);}
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param <T> An enum implementing the {@link ConfigMarker} interface
     * @param file A {@link FileConfiguration} to write to
     * @param clazz A reference to the enum {@link Class}
     */
    public static <T extends Enum<T> & ConfigMarker> void setDefaults(FileConfiguration file, Class<T> clazz) {
        for (ConfigMarker conf : clazz.getEnumConstants()) {
            if (!file.isSet(conf.getPath())) {
                file.set(conf.getPath(), conf.getDefault());
            }
        }
    }

}