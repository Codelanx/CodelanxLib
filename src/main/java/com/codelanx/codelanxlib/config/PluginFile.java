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

import com.codelanx.codelanxlib.annotation.PluginClass;
import com.codelanx.codelanxlib.annotation.RelativePath;
import com.codelanx.codelanxlib.data.FileDataType;
import com.codelanx.codelanxlib.util.Reflections;
import com.codelanx.codelanxlib.logging.Debugger;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a file containing mappings that is owned by a plugin, and can
 * be automatically initialized
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public interface PluginFile {

    /**
     * Returns the save location for passed {@link PluginFile} argument
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param clazz An implementing class with the {@link PluginClass} and
     *              {@link RelativePath} annotations
     * @return A {@link File} pointing to the location containing saved values
     *           for this configuration type
     */
    public static File getFileLocation(Class<? extends PluginFile> clazz) {
        if (!(Reflections.hasAnnotation(clazz, PluginClass.class)
                && Reflections.hasAnnotation(clazz, RelativePath.class))) {
            throw new IllegalStateException("'" + clazz.getName() + "' is missing either PluginClass or RelativePath annotations");
        }
        File folder = Reflections.getPlugin(clazz).getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, clazz.getAnnotation(RelativePath.class).value());
    }

    /**
     * The YAML path to store this value in
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The path to the file value
     */
    public String getPath();

    /**
     * Returns the default value of the key
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The key's default value
     */
    public Object getDefault();

    /**
     * Returns the relevant {@link FileDataType} for this config.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The internal {@link FileDataType} of this {@link Config}
     */
    public FileDataType getConfig();

    /**
     * Loads the lang values from the configuration file. Safe to use for
     * reloading.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The type of {@link FileDataType} to return
     * @param clazz The {@link Class} of the returned {@link FileDataType}
     * @return The relevant {@link FileDataType} for all the config info
     */
    default public <T extends FileDataType> T init(Class<T> clazz) {
        Class<? extends PluginFile> me = this.getClass();
        //Get fields
        Iterable<? extends PluginFile> itr;
        if (me.isEnum()) {
            itr = Arrays.asList(me.getEnumConstants());
        } else if (Iterable.class.isAssignableFrom(me)) {
            itr = ((Iterable<? extends PluginFile>) this);
        } else {
            throw new IllegalStateException("'" + me.getName() + "' is neither an enum nor an Iterable");
        }
        //Initialize file
        String path = null;
        try {
            File ref = PluginFile.getFileLocation(this.getClass());
            path = ref.getPath();
            if (!ref.exists()) {
                ref.createNewFile();
            }
            FileDataType use = FileDataType.newInstance(clazz, ref);
            for (PluginFile l : itr) {
                if (!use.isSet(l.getPath())) {
                    use.set(l.getPath(), l.getDefault());
                }
            }
            use.save();
            return (T) use;
        } catch (IOException ex) {
            Debugger.error(ex, "Error creating plugin file '%s'", path);
            return null;
        }
    }

    /**
     * Saves the current configuration from memory
     *
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @throws IOException Failed to save to the file
     */
    default public void save() throws IOException {
        this.save(PluginFile.getFileLocation(this.getClass()));
    }

    /**
     * Saves the current configuration from memory to a specific {@link File}
     *
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param file The file to save to
     * @throws IOException Failed to save to the file
     */
    default public void save(File file) throws IOException {
        this.getConfig().save();
    }

}
