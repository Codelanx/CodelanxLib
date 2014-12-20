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
package com.codelanx.codelanxlib.data.types;

import com.codelanx.codelanxlib.data.FileDataType;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represents a YAML file that is parsed and loaded into memory.
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class Yaml implements FileDataType {

    protected final File location;
    protected final FileConfiguration yaml;

    /**
     * Reads and loads a YAML file into memory.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param location The location of the file to read
     */
    public Yaml(File location) {
        this.location = location;
        this.yaml = YamlConfiguration.loadConfiguration(this.location);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isSet(String path) {
        return this.yaml.isSet(path);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @param value {@inheritDoc}
     */
    @Override
    public void set(String path, Object value) {
        this.yaml.set(path, value);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Object get(String path) {
        return this.yaml.get(path);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @param def {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Object get(String path, Object def) {
        return this.yaml.get(path, def);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void save() throws IOException {
        this.yaml.save(this.location);
    }

    /**
     * Returns the underlying {@link FileConfiguration} used in reading the
     * YAML file.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The underlying {@link FileConfiguration} in use
     */
    public FileConfiguration getFileConfiguration() {
        return this.yaml;
    }

}
