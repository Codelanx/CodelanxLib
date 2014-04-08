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

/**
 * Holds string instances of config values
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public enum ConfigValue implements ConfigMarker<ConfigValue> {

    /** An example node representing an integer config value */
    //DEBUG_LEVEL("debug-level", 0)
    ;
    
    private final String key;
    private final Object def;

    /**
     * private constructor for {@link ConfigValues}
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param key The string path to the value
     */
    private ConfigValue(String key, Object def) {
        this.key = key;
        this.def = def;
    }

    /**
     * Returns the default value and key path in the format:
     * <br>    default-value@key-path-value
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The default and key-path value of the {@link ConfigValue} variable
     */
    @Override
    public String toString() {
        return this.def + "@" + this.key;
    }
    
    /**
     * Returns the string path to the value
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The string path to the value
     */
    @Override
    public String getPath() {
        return this.key;
    }
    
    /**
     * Returns the default value of the key
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The key's default value
     */
    @Override
    public Object getDefault() {
        return this.def;
    }

}