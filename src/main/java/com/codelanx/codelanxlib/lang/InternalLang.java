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
package com.codelanx.codelanxlib.lang;

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.annotation.PluginClass;
import com.codelanx.codelanxlib.annotation.RelativePath;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Class description for {@link InternalLang}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
@PluginClass(CodelanxLib.class)
@RelativePath("lang.yml")
public enum InternalLang implements Lang<InternalLang> {

    COMMAND_HANDLER_UNKNOWN("command.handler.unknown", "Unknown command"),
    COMMAND_HANDLER_USAGE("command.handler.usage", "Usage: %s"),
    COMMAND_HELP_BARCHAR("command.help.barchar", "-"),
    COMMAND_HELP_BARCOLOR("command.help.barcolor", "&f"),
    COMMAND_HELP_TITLECOLOR("command.help.titlecolor", "&c"),
    /**
     * Accept a bar color first, title color second, then the title itself third
     */
    COMMAND_HELP_TITLECONTAINER("command.help.title-container", "%1$s[ %2$s%3$s %1$s]"),
    COMMAND_HELP_TITLEFORMAT("command.help.format.title", "/%s help"),
    COMMAND_HELP_PAGEFORMAT("command.help.format.page", "Page (%d/%d)"),
    COMMAND_HELP_ITEMFORMAT("command.help.format.item", "&9%s &f- &7%s"),
    COMMAND_HELP_INFO("command.help.info", "Displays help information about this plugin"),
    ECONOMY_INSUFF("economy.insufficient", "You do not have enough money for this! (Required: %.2f)"),
    ECONOMY_REFUND("economy.refund", "Refunded amount &7.2f&9"),
    ECONOMY_FAILED("economy.trans-failed", "&cError:&7 Failed to charge your account!"),
    FORMAT("format", "&f[&9CL-Lib&f] %s");

    private static FileConfiguration yaml;
    private final String def;
    private final String path;

    /**
     * {@link Lang} private constructor
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param path The path to the value
     * @param def The default value
     */
    private InternalLang(String path, String def) {
        this.path = path;
        this.def = def;
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
    public Lang getFormat() {
        return InternalLang.FORMAT;
    }

    @Override
    public FileConfiguration getLangConfig() {
        if (InternalLang.yaml == null) {
            InternalLang.yaml = Lang.init(InternalLang.class);
        }
        return InternalLang.yaml;
    }

}
