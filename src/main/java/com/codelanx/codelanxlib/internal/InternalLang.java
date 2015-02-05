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
package com.codelanx.codelanxlib.internal;

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.annotation.PluginClass;
import com.codelanx.codelanxlib.annotation.RelativePath;
import com.codelanx.codelanxlib.config.lang.Lang;
import com.codelanx.codelanxlib.data.types.Yaml;

/**
 * Class description for {@link InternalLang}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
@PluginClass(CodelanxLib.class)
@RelativePath("lang.yml")
public enum InternalLang implements Lang {

    COMMAND_STATUS_USAGE("command.status.usage", "Usage: %s"),
    COMMAND_STATUS_NOPERM("command.status.noperm", "&cYou do not have permission for this!"),
    COMMAND_STATUS_FAILED("command.status.failed", "Command execution failed :("),
    COMMAND_STATUS_UNSUPPORTED("command.status.unsupported", "Unknown commmand."),
    COMMAND_STATUS_RESTRICTED("command.status.restricted", "This command should only be used by %s!"),
    COMMAND_HELP_TITLEFORMAT("command.help.format.title", "/%s help"),
    COMMAND_HELP_ITEMFORMAT("command.help.format.item", "&9%s &f- &7%s"),
    COMMAND_HELP_INFO("command.help.info", "Displays help information about this plugin"),
    COMMAND_RELOAD_UNSUPPORTED("command.reload.unsupported", "This plugin does not support reloading!"),
    COMMAND_RELOAD_DONE("command.reload.done", "&9%s&f v&9%s&f reloaded!"),
    COMMAND_RELOAD_INFO("command.reload.info", "Reloads the plugin"),
    ECONOMY_INSUFF("economy.insufficient", "You do not have enough money for this! (Required: %.2f)"),
    ECONOMY_REFUND("economy.refund", "Refunded amount &7.2f&9"),
    ECONOMY_FAILED("economy.trans-failed", "&cError:&7 Failed to charge your account!"),
    PAGINATOR_BARCHAR("utils.paginator.barchar", "-"),
    PAGINATOR_BARCOLOR("utils.paginator.barcolor", "&f"),
    PAGINATOR_TITLECOLOR("utils.paginator.titlecolor", "&c"),
    /**
     * Accept a bar color first, title color second, then the title itself third
     */
    PAGINATOR_TITLECONTAINER("utils.paginator.title-container", "%1$s[ %2$s%3$s %1$s]"),
    PAGINATOR_PAGEFORMAT("utils.paginator.page-format", "Page (%d/%d)"),
    FORMAT("format", "&f[&9CL-Lib&f] %s");

    private static Yaml yaml;
    private final String def;
    private final String path;

    /**
     * {@link InternalLang} private constructor
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
    public Yaml getConfig() {
        if (InternalLang.yaml == null) {
            InternalLang.yaml = this.init(Yaml.class);
        }
        return InternalLang.yaml;
    }

}
