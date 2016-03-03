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
package com.codelanx.codelanxlib.internal;

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.command.HelpCommand;
import com.codelanx.codelanxlib.command.ReloadCommand;
import com.codelanx.codelanxlib.config.Lang;
import com.codelanx.codelanxlib.config.PluginClass;
import com.codelanx.codelanxlib.util.Paginator;
import com.codelanx.commons.config.DataHolder;
import com.codelanx.commons.config.LangFile;
import com.codelanx.commons.config.RelativePath;
import com.codelanx.commons.data.types.Yaml;

/**
 * Internal {@link LangFile} enum for CodelanxLib
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
@PluginClass(CodelanxLib.class)
@RelativePath("lang.yml")
public enum InternalLang implements Lang {

    /**
     * Used to relay the usage of a command
     */
    COMMAND_STATUS_USAGE("command.status.usage", "Usage: /%s"),
    /**
     * Displayed when a non-executable command is run
     */
    COMMAND_STATUS_NOTEXEC("command.status.not-executable", "Did you mean?:%s"),
    /**
     * Relayed if a CommandSender does not have permissions to use the command
     */
    COMMAND_STATUS_NOPERM("command.status.noperm", "&cYou do not have permission for this!"),
    /**
     * Relayed when a command execution fail (exceptions or the like)
     */
    COMMAND_STATUS_FAILED("command.status.failed", "Command execution failed :("),
    /**
     * Relayed upon an unknown/unsupported command
     */
    COMMAND_STATUS_UNSUPPORTED("command.status.unsupported", "Unknown commmand."),
    /**
     * Relayed when an inappropriate CommandSender is used for a command
     */
    COMMAND_STATUS_RESTRICTED("command.status.restricted", "This command should only be used by %s!"),
    /**
     * Relayed when a proxied CommandSender attempts executing a command
     */
    COMMAND_STATUS_NOPROXIES("command.status.no-proxies", "This command cannot be executed by a proxied sender!"),
    /**
     * Title for the {@link HelpCommand}'s internal {@link Paginator}
     */
    COMMAND_HELP_TITLEFORMAT("command.help.format.title", "/%s help"),
    /**
     * Format for listing a command in {@link HelpCommand}
     */
    COMMAND_HELP_ITEMFORMAT("command.help.format.item", "&9%s &f- &7%s"),
    /**
     * Help information for {@link HelpCommand}
     */
    COMMAND_HELP_INFO("command.help.info", "Displays help information about this plugin"),
    /**
     * What to title command aliases as
     */
    COMMAND_HELP_ALIASES("command.help.format.aliases-title", "Aliases:"),
    /**
     * Relayed if reloading is not supported
     */
    COMMAND_RELOAD_UNSUPPORTED("command.reload.unsupported", "This plugin does not support reloading!"),
    /**
     * Output of a finished {@link ReloadCommand}
     */
    COMMAND_RELOAD_DONE("command.reload.done", "&9%s&f v&9%s&f reloaded!"),
    /**
     * Info for {@link ReloadCommand}
     */
    COMMAND_RELOAD_INFO("command.reload.info", "Reloads the plugin"),
    /**
     * Relayed if funds for an economy transaction are insufficient
     */
    ECONOMY_INSUFF("economy.insufficient", "You do not have enough money for this! (Required: %.2f)"),
    /**
     * Relayed if a player is refunded money
     */
    ECONOMY_REFUND("economy.refund", "Refunded amount &7.2f&9"),
    /**
     * Relayed if an economy transaction failed
     */
    ECONOMY_FAILED("economy.trans-failed", "&cError:&7 Failed to charge your account!"),
    /**
     * Character to use for the {@link Paginator} bars
     */
    PAGINATOR_BARCHAR("utils.paginator.barchar", "-"),
    /**
     * Bar color for the {@link Paginator} class
     */
    PAGINATOR_BARCOLOR("utils.paginator.barcolor", "&f"),
    /**
     * Color of the title for the {@link Paginator} class
     */
    PAGINATOR_TITLECOLOR("utils.paginator.titlecolor", "&c"),
    /**
     * Accept a bar color first, title color second, then the title itself third
     */
    PAGINATOR_TITLECONTAINER("utils.paginator.title-container", "%1$s[ %2$s%3$s %1$s]"),
    /**
     * The format specified for displaying page count
     */
    PAGINATOR_PAGEFORMAT("utils.paginator.page-format", "Page (%d/%d)"),
    /**
     * Format for CodelanxLib
     */
    FORMAT("format", "&f[&9CL-Lib&f] %s");

    private static final DataHolder<Yaml> DATA = new DataHolder<>(Yaml.class);
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
    public DataHolder<Yaml> getData() {
        return InternalLang.DATA;
    }

}
