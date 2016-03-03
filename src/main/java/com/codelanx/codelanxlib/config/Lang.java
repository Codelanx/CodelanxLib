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

import com.codelanx.codelanxlib.implementers.Formatted;
import com.codelanx.commons.config.DataHolder;
import com.codelanx.commons.config.InfoFile;
import com.codelanx.commons.config.LangFile;
import com.codelanx.commons.config.RelativePath;
import com.codelanx.commons.data.FileDataType;
import com.codelanx.commons.util.Reflections;
import com.codelanx.codelanxlib.internal.InternalLang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Created by Rogue on 11/6/2015.
 */
public interface Lang extends LangFile {

    LangFile DEFAULT_DYNAMIC = LangFile.createLang("");

    /**
     * Returns an acceptable message format for the plugin at hand
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param plugin A {@link Plugin} that needs a format
     * @return A relevant {@link LangFile} format
     */
    public static Lang getFormat(Plugin plugin) {
        return plugin instanceof Formatted
                ? ((Formatted) plugin).getFormat()
                : Lang.defaultFormat(plugin.getName());
    }

    /**
     * Returns the format specifier for this {@link LangFile} file. This should be
     * a constant in the enum and represent a value that is used for all
     * messages universally.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The format specifier for this {@link LangFile}
     */
    public Lang getFormat();

    /**
     * Returns a Lang object representing the default formatting for all
     * CodelanxLib plugins. The {@code option} parameter can be supplemented
     * with {@code null} which will default to "CL-Lib". The returned format is:
     * <br> <br>
     * {@code "&amp;f[&amp;9" + option == null ? "CL-Lib" : option + "&amp;f] %amp;s"}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param option The optional "title" string for the format
     * @return A {@link LangFile} object representing the default format
     */
    public static Lang defaultFormat(String option) {
        return Lang.createLang("&f[&9" + (option == null ? "CL-Lib" : option) + "&f] %s");
    }

    /**
     * Automatically translates strings that contain color codes using the
     * '{@code &}' symbol
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param color String with un-converted color codes
     * @return string with correct chat colors included
     */
    public static String color(String color) {
        return ChatColor.translateAlternateColorCodes('&', color);
    }

    /**
     * Sends a formatted string and prepends the {@link Lang#getFormat} to
     * it.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link LangFile} message
     */
    public static void sendMessage(CommandSender target, Lang message, Object... args) {
        Lang.sendMessage(target, message.getFormat(), message, args);
    }

    /**
     * Sends a message but does not include {@link Lang#getFormat}, instead it
     * uses a supplied format
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The target to send to
     * @param format The format provided
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link LangFile} message
     */
    public static void sendMessage(CommandSender target, Lang format, LangFile message, Object... args) {
        if (target == null || format == null || message == null) {
            return;
        }
        String s = format.formatAndColor(message.format(args));
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * Sends a raw message without additional formatting aside from translating
     * color codes
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link LangFile} message
     */
    public static void sendRawMessage(CommandSender target, Lang message, Object... args) {
        if (message == null || target == null) {
            return;
        }
        String s = message.formatAndColor(args);
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * Issues a "tellRaw" to a {@link Player} target with the supplied Lang
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The {@link Player} to send the JSON message to
     * @param message A {@link LangFile} representing a JSON payload
     * @param args The arguments for the passed {@link LangFile}
     */
    public static void tellRaw(Player target, LangFile message, Object... args) {
        if (message == null || target == null) {
            return;
        }
        String s = message.format(args);
        if (!s.isEmpty()) {
            //Use command until proper api is in place
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellRaw " + target.getName() + " " + s);
        }
    }

    /**
     * Issues a "title" command to a {@link Player} target with the passed Lang
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The {@link Player} to send the JSON title to
     * @param message A {@link LangFile} representing a JSON payload for the title
     * @param args The arguments for the passed {@link LangFile}
     */
    public static void sendTitle(Player target, LangFile message, Object... args) {
        if (message == null || target == null) {
            return;
        }
        String s = message.format(args);
        if (!s.isEmpty()) {
            //Use command until proper api is in place
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + target.getName() + " " + s); //inb4 "bukkit injection"
        }
    }

    /**
     * Formats a {@link LangFile} enum constant with the supplied arguments, and
     * colors it
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param args The arguments to supply
     * @return The formatted string
     */
    default public String formatAndColor(Object... args) {
        return Lang.color(this.format(args));
    }

    /**
     * Returns a raw Lang object that can be used for dynamic creation of Lang
     * variables
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param format The string to wrap in a {@link LangFile} object
     * @return A {@link LangFile} object that will
     */
    public static Lang createLang(String format) {
        return new Lang() {

            @Override
            public Lang getFormat() {
                return InternalLang.FORMAT.getFormat();
            }

            @Override
            public String getDefault() {
                return format;
            }

            @Override
            public String getPath() {
                return DEFAULT_DYNAMIC.getPath();
            }

            @Override
            public FileDataType getConfig() {
                return DEFAULT_DYNAMIC.getConfig();
            }

            @Override
            public DataHolder<? extends FileDataType> getData() {
                return DEFAULT_DYNAMIC.getData();
            }

        };
    }

    //Convenience methods

    public static void sendMessage(OfflinePlayer target, Lang format, LangFile message, Object... args) {
        if (target.isOnline()) {
            Lang.sendMessage((CommandSender) target.getPlayer(), format, message, args);
        }
    }
    public static void sendMessage(OfflinePlayer target, Lang message, Object... args) {
        if (target.isOnline()) {
            Lang.sendMessage((CommandSender) target.getPlayer(), message.getFormat(), message, args);
        }
    }

    public static void sendRawMessage(OfflinePlayer target, Lang message, Object... args) {
        if (target.isOnline()) {
            Lang.sendRawMessage((CommandSender) target.getPlayer(), message, args);
        }
    }

    public static void sendMessage(Player target, Lang format, LangFile message, Object... args) {
        Lang.sendMessage((CommandSender) target, format, message, args);
    }

    public static void sendMessage(Player target, Lang message, Object... args) {
        Lang.sendMessage((CommandSender) target, message.getFormat(), message, args);
    }

    public static void sendRawMessage(Player target, Lang message, Object... args) {
        Lang.sendRawMessage((CommandSender) target, message, args);
    }

    @Override
    default public File getFileLocation() {
        Class<? extends InfoFile> clazz = this.getClass();
        if (!(Reflections.hasAnnotation(clazz, PluginClass.class)
                && Reflections.hasAnnotation(clazz, RelativePath.class))) {
            throw new IllegalStateException("'" + clazz.getName() + "' is missing either PluginClass or RelativePath annotations");
        }
        File folder = Configs.getPlugin(clazz).getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, clazz.getAnnotation(RelativePath.class).value());
    }
}
