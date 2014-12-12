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

import com.codelanx.codelanxlib.annotation.PluginClass;
import com.codelanx.codelanxlib.annotation.RelativePath;
import com.codelanx.codelanxlib.config.PluginFile;
import com.codelanx.codelanxlib.implementers.Formatted;
import com.codelanx.codelanxlib.util.AnnotationUtil;
import com.codelanx.codelanxlib.util.DebugUtil;
import java.io.File;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Class description for {@link Lang}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 * 
 * @param <E> Represents the enum type that implements this interface
 */
public interface Lang<E extends Enum<E> & Lang<E>> extends PluginFile<E> {

    /**
     * Returns the format specifier for this {@link Lang} file. This should be
     * a constant in the enum and represent a value that is used for all
     * messages universally.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The format specifier for this {@link Lang}
     */
    public Lang getFormat();

    /**
     * The default value of this YAML string
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The default value
     */
    @Override
    public String getDefault();

    /**
     * Returns the relevant {@link FileConfiguration} for this lang file. In
     * later Java releases when privatized interface methods will be implemented
     * this will no longer be visible. You should not be using this outside of
     * the Lang files themselves.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @deprecated
     * 
     * @return The internal {@link FileConfiguration} of this {@link Lang}
     */
    @Override
    public FileConfiguration getConfig();

    /**
     * Formats a {@link Lang} enum constant with the supplied arguments
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param args The arguments to supply
     * @return The formatted string
     */
    default public String format(Object... args) {
        if (this instanceof MutableLang) {
            return Lang.__(String.format(this.getDefault(), args));
        }
        return Lang.__(String.format(this.getConfig().getString(this.getPath(), this.getDefault()), args));
    }

    /**
     * Will format a string with "PLURAL" or "PLURALA" tokens in them.
     * <br><br><ul>
     * <li> <em>PLURALA</em>: Token that will evaluate gramatically. An int
     * value of 1 will return "is &lt;amount&gt; 'word'", otherwise it will be
     * "are &lt;amount&gt; 'word'".
     * </li><li> <em>PLURAL</em>: Token that will evaluate the word. An int
     * value of 1 will return the first word, value of 2 the second word.
     * </ul>
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param amount The amount representative of the data token
     * @param args The arguments to replace any other tokens with.
     * @return The formatting string value for plurals
     */
    default public String pluralFormat(int amount, Object... args) {
        String repl;
        if (this instanceof MutableLang) {
            repl = this.getDefault();
        } else {
            repl = this.getConfig().getString(this.getPath());
        }
        repl = repl.replaceAll("\\{PLURALA (.*)\\|(.*)\\}", amount == 1 ? "is " + amount + " $1" : "are " + amount + " $2");
        repl = repl.replaceAll("\\{PLURAL (.*)\\|(.*)\\}", amount == 1 ? "$1" : "$2");
        return Lang.__(String.format(repl, args));
    }

    /**
     * Returns a raw Lang object that can be used for dynamic creation of Lang
     * variables
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param format The string to wrap in a {@link Lang} object
     * @return A {@link Lang} object that will 
     */
    public static Lang createLangFormat(String format) {
        return new MutableLang(format);
    }

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
     * @return A {@link Lang} object representing the default format
     */
    public static Lang defaultFormat(String option) {
        return Lang.createLangFormat("&f[&9" + option == null ? "CL-Lib" : option + "&f] %s");
    }

    /**
     * Returns an acceptable message format for the plugin at hand
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param plugin A {@link Plugin} that needs a format
     * @return A relevant {@link Lang} format
     */
    public static Lang getFormat(Plugin plugin) {
        return plugin instanceof Formatted
                ? ((Formatted) plugin).getFormat()
                : Lang.defaultFormat(plugin.getName());
    }

    /**
     * Converts pre-made strings to have chat colors in them
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param color String with un-converted color codes
     * @return string with correct chat colors included
     */
    public static String __(String color) {
        return ChatColor.translateAlternateColorCodes('&', color);
    }

    /**
     * Loads the lang values from the configuration file. Safe to use for
     * reloading.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The relevant {@link FileConfiguration} for all the lang info
     */
    default public FileConfiguration init() {
        if (!(AnnotationUtil.hasAnnotation(this.getClass(), PluginClass.class)
                && AnnotationUtil.hasAnnotation(this.getClass(), RelativePath.class))) {
            throw new IllegalStateException("Lang enum is missing either PluginClass or RelativePath annotations!");
        }
        String path = null;
        try {
            File folder = AnnotationUtil.getPlugin(this.getClass()).getDataFolder();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File ref = new File(folder, this.getClass().getAnnotation(RelativePath.class).value());
            path = ref.getPath();
            if (!ref.exists()) {
                ref.createNewFile();
            }
            FileConfiguration yaml = YamlConfiguration.loadConfiguration(ref);
            for (Lang l : this.getClass().getEnumConstants()) {
                if (!yaml.isSet(l.getPath())) {
                    yaml.set(l.getPath(), l.getDefault());
                }
            }
            yaml.save(ref);
            return yaml;
        } catch (IOException ex) {
            DebugUtil.error(String.format("Error creating lang file '%s'!", path), ex);
            return null;
        }
    }

    /**
     * Sends a formatted string and prepends the {@link Lang#getFormat} to
     * it.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link Lang} message
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
     *
     * @param target The target to send to
     * @param format The format provided
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link InternalLang} message
     */
    public static void sendMessage(CommandSender target, Lang format, Lang message, Object... args) {
        if (target == null || format == null || message == null) {
            return;
        }
        String s = format.format(message.format(args));
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
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link Lang} message
     */
    public static void sendRawMessage(CommandSender target, Lang message, Object... args) {
        if (message == null || target == null) {
            return;
        }
        String s = Lang.__(message.format(args));
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * Sends a formatted string to the player without using the Lang interface
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @deprecated
     *
     * @param target The target to send to
     * @param format The format to use
     * @param args The message arguments
     */
    public static void sendRawMessage(CommandSender target, String format, Object... args) {
        if (target == null || format == null) {
            return;
        }
        target.sendMessage(Lang.__(String.format(format, args)));
    }

}
