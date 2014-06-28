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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Loads and manages {@link FileConfiguration} objects with a supplied
 * {@link Enum} that implements {@link ConfigMarker}
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
     * @param <T> An {@link Enum} that implements {@link ConfigMarker}
     * @param plugin A {@link Plugin} instance using this class
     * @param clazz The {@link ConfigMarker} {@link Enum} to load values from
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
     * @param <T> An {@link Enum} that implements {@link ConfigMarker}
     * @param filePath The {@link File} to the YAML file to work with
     * @param file The {@link FileConfiguration} to work with
     * @param clazz The {@link ConfigMarker} {@link Enum} to load values from
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
     * @param <T> An {@link Enum} that implements {@link ConfigMarker}
     * @param filePath The {@link File} to the YAML file to work with
     * @param clazz The {@link ConfigMarker} {@link Enum} to load values from
     */
    public <T extends Enum<T> & ConfigMarker> ConfigurationLoader(File filePath, Class<T> clazz) {
        this(filePath, YamlConfiguration.loadConfiguration(filePath), clazz);
    }

    /**
     * Saves the current configuration from memory
     *
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @throws IOException Failed to save to the file
     */
    public synchronized void saveConfig() throws IOException {
        this.yaml.save(this.filePath);
    }

    /**
     * Gets the configuration file for this {@link Plugin}
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
     * Gets a {@code String} value from the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} representing the
     *             {@link FileConfiguration} value
     * @return The relevant {@code String} value, or {@code null} if none found
     */
    public synchronized String getString(ConfigMarker path) {
        return this.yaml.getString(path.getPath());
    }

    /**
     * Gets a {@code String} value from the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} representing the
     *             {@link FileConfiguration} value
     * @return The relevant {@code String}, or the default value if not set
     */
    public synchronized String getSafeString(ConfigMarker path) {
        return this.yaml.getString(path.getPath(), path.getDefault().toString());
    }

    /**
     * Gets a {@code int} value from the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} representing the
     *             {@link FileConfiguration} value
     * @return The relevant {@code int} value, or -1 if none found
     */
    public synchronized int getInt(ConfigMarker path) {
        return this.yaml.getInt(path.getPath(), -1);
    }

    /**
     * Gets a {@code double} value from the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} representing the
     *             {@link FileConfiguration} value
     * @return The relevant {@code double} value, or -1 if none found
     */
    public synchronized double getDouble(ConfigMarker path) {
        return this.yaml.getDouble(path.getPath(), -1);
    }

    /**
     * Gets a {@code long} value from the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} representing the
     *             {@link FileConfiguration} value
     * @return The relevant {@code long} value, or -1 if none found
     */
    public synchronized long getLong(ConfigMarker path) {
        return this.yaml.getLong(path.getPath(), -1);
    }

    /**
     * Gets a {@code boolean} value from the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} representing the
     *             {@link FileConfiguration} value
     * @return The relevant {@code boolean}, or the default value if not set
     */
    public synchronized boolean getBoolean(ConfigMarker path) {
        return this.yaml.getBoolean(path.getPath(), (boolean) path.getDefault());
    }

    /**
     * Gets a {@code Map} representative of a section of the YAML file. This
     * will always be a {@link Map} regardless of the Bukkit implementation in
     * use.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} representing the path to the section
     * @return The {@link Map} representing this section
     */
    public synchronized Map<String, Object> getSection(ConfigMarker path) {
        return ConfigurationLoader.getConfigSectionValue(this.get(path));
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

    /**
     * Gets the current object in memory relevant to the passed
     * {@link ConfigMarker}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} leading to the value
     * @return The Object found at the relevant location
     */
    public synchronized Object get(ConfigMarker path) {
        return this.yaml.get(path.getPath());
    }

    /**
     * Sets a value in the {@link FileConfiguration}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param path The {@link ConfigMarker} location to set to
     * @param set The value to set
     */
    public synchronized void set(ConfigMarker path, Object set) {
        this.yaml.set(path.getPath(), set);
    }

}
