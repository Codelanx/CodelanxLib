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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Loads and manages {@link FileConfiguration} objects with supplied
 * {@link ConfigMarker} enums.
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public final class ConfigurationLoader {

    protected final File filePath;
    protected final FileConfiguration yaml;

    /**
     * Constructor for {@link ConfigurationLoader}
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param <T> An enum that implements {@link ConfigMarker}
     * @param plugin The {@link Nations} instance
     * @param clazz The {@link ConfigMarker} enum to load config values from
     */
    public <T extends Enum<T> & ConfigMarker> ConfigurationLoader(Plugin plugin, Class<T> clazz) {
        this.filePath = new File(plugin.getDataFolder(), "config.yml");
        plugin.saveDefaultConfig();
        this.yaml = plugin.getConfig();
        ConfigMarker.setDefaults(yaml, clazz);
        try {
            this.saveConfig();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error saving default config values!", ex);
        }
    }

    /**
     * Constructor for {@link ConfigurationLoader}
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param <T> An enum that implements {@link ConfigMarker}
     * @param filePath The {@link File} to the config file to work with
     * @param file The {@link FileConfiguration to work with
     * @param clazz The {@link ConfigMarker} enum to load config values from
     */
    public <T extends Enum<T> & ConfigMarker<T>> ConfigurationLoader(File filePath, FileConfiguration file, Class<T> clazz) {
        this.filePath = filePath;
        this.yaml = file;
        ConfigMarker.setDefaults(yaml, clazz);
        try {
            this.saveConfig();
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationLoader.class.getName()).log(Level.SEVERE, "Error saving default config values!", ex);
        }
    }
    
    /**
     * Constructor for {@link ConfigurationLoader}
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param <T> An enum that implements {@link ConfigMarker}
     * @param filePath The {@link File} to the config file to work with
     * @param clazz The {@link ConfigMarker} enum to load config values from
     */
    public <T extends Enum<T> & ConfigMarker> ConfigurationLoader(File filePath, Class<T> clazz) {
        this(filePath, YamlConfiguration.loadConfiguration(filePath), clazz);
    }

    /**
     * Saves the current configuration from memory
     *
     * @since 1.0.0
     * @version 1.0.0
     */
    public synchronized void saveConfig() throws IOException {
        this.yaml.save(this.filePath);
    }

    /**
     * Gets the configuration file for {@link Nations}
     *
     * @since 1.3.0
     * @version 1.3.0
     *
     * @return YamlConfiguration file, null if verifyConfig() has not been run
     */
    public FileConfiguration getConfig() {
        return this.yaml;
    }

    /**
     * Gets a string value from the config
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param path Path to string value
     * @return String value
     */
    public synchronized String getString(ConfigMarker path) {
        return this.yaml.getString(path.getPath());
    }
    
    /**
     * Gets a string value from the config
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param path Path to string value
     * @return String value
     */
    public synchronized String getSafeString(ConfigMarker path) {
        return this.yaml.getString(path.getPath(), path.getDefault().toString());
    }

    /**
     * Gets an int value from the config
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param path Path to int value
     * @return int value
     */
    public synchronized int getInt(ConfigMarker path) {
        return this.yaml.getInt(path.getPath());
    }

    public synchronized double getDouble(ConfigMarker path) {
        return this.yaml.getDouble(path.getPath());
    }

    public synchronized long getLong(ConfigMarker path) {
        return this.yaml.getLong(path.getPath());
    }

    /**
     * Gets a boolean value from the config
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param path Path to boolean value
     * @return boolean value
     */
    public synchronized boolean getBoolean(ConfigMarker path) {
        return this.yaml.getBoolean(path.getPath());
    }

    public synchronized Map<String, Object> getSection(ConfigMarker path) {
        Object o = this.yaml.get(path.getPath());
        return ConfigurationLoader.getConfigSectionValue(o);
    }

    public static Map<String, Object> getConfigSectionValue(Object o) {
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

    public synchronized Object get(ConfigMarker path) {
        return this.yaml.get(path.getPath());
    }

    public synchronized void set(ConfigMarker path, Object set) {
        this.yaml.set(path.getPath(), set);
    }

}