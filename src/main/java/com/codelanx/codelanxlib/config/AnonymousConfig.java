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

import com.codelanx.codelanxlib.data.FileDataType;

/**
 * Class description for {@link AnonymousConfig}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class AnonymousConfig implements Config {

    private final String path;
    private final Object def;
    private final FileDataType file;

    public AnonymousConfig(FileDataType file, String path, Object def) {
        this.file = file;
        this.path = path;
        this.def = def;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Object getDefault() {
        return this.def;
    }

    @Override
    public FileDataType getConfig() {
        return this.file;
    }

}
