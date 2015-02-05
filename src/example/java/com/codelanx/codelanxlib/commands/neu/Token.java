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
package com.codelanx.codelanxlib.command.neu;

import com.codelanx.codelanxlib.command.CommandStatus;
import com.codelanx.codelanxlib.config.lang.Lang;
import com.codelanx.codelanxlib.implementers.Commandable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Class description for {@link Token}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 * 
 * @param <T> The type of the {@link Plugin} for this {@link Token}
 */
public abstract class Token<T extends Plugin & Commandable> {

    protected final T plugin;
    protected final Map<String, Token<T>> subtokens = new HashMap<>();

    public Token(T plugin) {
        this.plugin = plugin;
    }

    /**
     * Recursively goes through available {@link Token} objects until it finds
     * the appropriate one to execute
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param sender The command executor
     * @param permission A recursed string representing the permission to be
     *                   checked
     * @param args The command arguments, starting after the command name
     * @return The {@link CommandStatus} representing the result of the command
     */
    public CommandStatus determine(CommandSender sender, String permission, String... args) {
        if (!(this instanceof RootToken)) {
            permission += "." + this.getName().toLowerCase();
            if (!sender.hasPermission(permission)) {
                return CommandStatus.NO_PERMISSION;
            }
        } else {
            permission = this.plugin.getName() + ".cmd";
        }
        if (args.length > 0) {
            Token<T> sub = this.subtokens.get(args[0]);
            if (sub != null) {
                return sub.determine(sender, permission, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return this.execute(sender, args);
    }

    /**
     * Adds a {@link Token} to the current as a sub-token that can be called in
     * commands
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param token The {@link Token} to add
     */
    public void subToken(Token<T> token) {
        this.subtokens.put(token.getName(), token);
    }

    /**
     * Executes a relevant command grabbed from the
     * {@link com.codelanx.codelanxlib.command.CommandHandler}.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param sender The command executor
     * @param args The command arguments, starting after the command name
     * @return The {@link CommandStatus} representing the result of the command
     */
    public CommandStatus execute(CommandSender sender, String... args) {
        return CommandStatus.UNSUPPORTED;
    }

    public Token<T> walk(String find) {
        return this.subtokens.get(find);
    }

    public Set<String> getSubTokens() {
        return Collections.unmodifiableSet(this.subtokens.keySet());
    }

    /**
     * Returns a {@link Map} of {@link Token} objects, and space-delimited
     * strings representing the path to the relevant {@link Token} objects.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return A {@link Map} representing all sub-tokens
     */
    public Map<String, ? extends Token<T>> deepWalk() {
        return this.att(this.getName());
    }

    private Map<String, ? extends Token<T>> att(String prefix) {
        Map<String, Token<T>> back = new HashMap<>();
        if (this.getSubTokens().size() > 0) {
            this.getSubTokens().forEach(t -> back.putAll(this.walk(t).att(prefix + " " + t)));
        } else {
            back.put(prefix, this);
        }
        return back;
    }

    /**
     * Returns the name of the command, used for storing a
     * {@link java.util.HashMap} of the commands as well as the subcommand
     * argument
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return The command's name
     */
    public abstract String getName();

    /**
     * Returns the command usage
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return Usage for this {@link Token}
     */
    public String getUsage() {
        return "/" + this.plugin.getCommandHandler().getMainCommand() + " " + this.getName();
    }

    /**
     * Information about this specific command. Should be kept concise
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return A small string about the command
     */
    public abstract Lang info();
}
