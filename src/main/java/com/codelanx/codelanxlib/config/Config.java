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
package com.codelanx.codelanxlib.config;

import com.codelanx.commons.config.ConfigFile;
import com.codelanx.commons.config.InfoFile;
import com.codelanx.commons.config.RelativePath;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.exception.Exceptions;

import java.io.File;

/**
 * Created by Rogue on 11/17/2015.
 */
public interface Config extends ConfigFile {

    @Override
    default public File getFileLocation() {
        Class<? extends InfoFile> clazz = this.getClass();
        Exceptions.illegalState(Reflections.hasAnnotation(clazz, PluginClass.class)
                && Reflections.hasAnnotation(clazz, RelativePath.class),
                "'" + clazz.getName() + "' is missing either PluginClass or RelativePath annotations");
        File folder = Configs.getPlugin(clazz).getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, clazz.getAnnotation(RelativePath.class).value());
    }

}
