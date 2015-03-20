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

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.annotation.PluginClass;
import com.codelanx.codelanxlib.annotation.RelativePath;
import com.codelanx.codelanxlib.data.types.Yaml;
import com.codelanx.codelanxlib.internal.InternalLang;

/**
 * Class description for {@link DefaultLang}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
@PluginClass(CodelanxLib.class)
@RelativePath("defaults.yml")
public enum DefaultLang implements Lang {

    PLAYER_NOT_ONLINE("player.not-online", "&9%s&f is not online!"),
    PLAYER_NEVER_PLAYED("player.never-played", "&9%s&f has not played on this server before!"),
    NOT_A_NUMBER("math.not-a-number", "&9%s&f is not a number!"),
    NOT_POSITIVE("math.not-positive", "&9%d&f is not a positive number"),
    ;

    private static final DataHolder<Yaml> DATA = new DataHolder<>(Yaml.class);
    private final String path;
    private final String def;

    private DefaultLang(String path, String def) {
        this.path = path;
        this.def = def;
    }

    @Override
    public Lang getFormat() {
        return InternalLang.FORMAT.getFormat();
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getDefault() {
        return this.def;
    }

    @Override
    public DataHolder<Yaml> getData() {
        return DefaultLang.DATA;
    }

}
