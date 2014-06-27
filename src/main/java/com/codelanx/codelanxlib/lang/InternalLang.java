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

import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Class description for {@link InternalLang}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public enum InternalLang {

    COMMAND_HANDLER_UNKNOWN("command.handler.unknown", "Unknown command"),
    COMMAND_HANDLER_USAGE("command.handler.info", "Usage: "),
    COMMAND_HELP_BARCHAR("command.help.barchar", "-"),
    COMMAND_HELP_BARCOLOR("command.help.barcolor", "&f"),
    COMMAND_HELP_TITLECOLOR("command.help.titlecolor", "&c"),
    /** Accept a bar color first, title color second, then the title itself third */
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
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param path The path to the value
     * @param def The default value
     */
    private InternalLang(String path, String def) {
        this.path = path;
        this.def = def;
    }

    /**
     * Formats a {@link InternalLang} enum constant with the supplied arguments
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param args The arguments to supply
     * @return The formatted string
     */
    public String format(Object... args) {
        return InternalLang.__(String.format(yaml.getString(this.path, this.def), args));
    }

    /**
     * Will format a string with "PLURAL" or "PLURALA" tokens in them.
     * <br /><br /><ul>
     * <li> <em>PLURALA</em>: Token that will evaluate gramatically. An int
     * value of 1 will return "is &lt;amount&gt; 'word'", otherwise it will be
     * "are &lt;amount&gt; 'word'".
     * </li><li> <em>PLURAL</em>: Token that will evaluate the word. An int
     * value of 1 will return the first word, value of 2 the second word.
     * </ul>
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param amount The amount representative of the data token
     * @param args The arguments to replace any other tokens with.
     * @return
     */
    public String pluralFormat(int amount, Object... args) {
        String repl = yaml.getString(this.path);
        repl = repl.replaceAll("\\{PLURALA (.*)\\|(.*)\\}", amount == 1 ? "is " + amount + " $1" : "are " + amount + " $2");
        repl = repl.replaceAll("\\{PLURAL (.*)\\|(.*)\\}", amount == 1 ? "$1" : "$2");
        return InternalLang.__(String.format(repl, args));
    }

    /**
     * Converts pre-made strings to have chat colors in them
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param color String with unconverted color codes
     * @return string with correct chat colors included
     */
    public static String __(String color) {
        return ChatColor.translateAlternateColorCodes('&', color);
    }

    /**
     * Loads the lang values from the configuration file. Safe to use for
     * reloading.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param plugin The {@link Plugin} to load lang information from
     * @throws IOException If the file cannot be read
     */
    public static void init(Plugin plugin) throws IOException {
        InternalLang.init(plugin.getDataFolder());
    }

    /**
     * Loads the lang values from the configuration file. Safe to use for
     * reloading.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param folder A {@link File} location to store the lang file in
     * @throws IOException If the file cannot be read
     */
    public static void init(File folder) throws IOException {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File ref = new File(folder, "lang.yml");
        if (!ref.exists()) {
            ref.createNewFile();
        }
        yaml = YamlConfiguration.loadConfiguration(ref);
        for (InternalLang l : InternalLang.values()) {
            if (!yaml.isSet(l.getPath())) {
                yaml.set(l.getPath(), l.getDefault());
            }
        }
        yaml.save(ref);
    }

    /**
     * Sends a formatted string and prepends the {@link Lang.FORMAT} to it.
     *
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @deprecated
     *
     * @param target The target to send to
     * @param message The message to format and send
     */
    public static void sendMessage(CommandSender target, String message) {
        target.sendMessage(InternalLang.FORMAT.format(message));
    }

    /**
     * Sends a raw message without additional formatting aside from translating
     * color codes
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @deprecated
     * 
     * @param target The target to send to
     * @param message The message to colorize and send
     */
    public static void sendRawMessage(CommandSender target, String message) {
        target.sendMessage(__(message));
    }

    /**
     * Sends a formatted string and prepends the {@link InternalLang.FORMAT} to it.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * 
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link InternalLang} message
     */
    public static void sendMessage(CommandSender target, InternalLang message, Object... args) {
        if (message == null) {
            return;
        }
        String s = InternalLang.FORMAT.format(message.format(args));
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * Sends a raw message without additional formatting aside from translating
     * color codes
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * 
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link InternalLang} message
     */
    public static void sendRawMessage(CommandSender target, InternalLang message, Object... args) {
        if (message == null) {
            return;
        }
        String s = __(message.format(args));
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * The YAML path to store this value in
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The path to the YAML value
     */
    private String getPath() {
        return this.path;
    }

    /**
     * The default value of this YAML string
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The default value
     */
    private String getDefault() {
        return this.def;
    }
    
}
