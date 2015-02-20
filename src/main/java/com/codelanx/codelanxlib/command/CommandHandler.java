/*
 * Copyright (C) 2015 CodeLanx , All Rights Reserved
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
 * along with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.command;

import com.codelanx.codelanxlib.config.Lang;
import com.codelanx.codelanxlib.util.exception.Exceptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

/**
 * Manages commands abstractly for the {@link Plugin}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public class CommandHandler implements CommandExecutor, TabCompleter { //More verbose, but clearer

    /** The format for output */
    protected final Lang name;
    /** Private {@link Plugin} instance */
    protected final Plugin plugin;
    /** Private {@link HashMap} of subcommands */
    protected final Map<String, SubCommand<? extends Plugin>> commands = new HashMap<>();
    /** The primary command to access this {@link CommandHandler} in-game */
    protected String command;

    /**
     * {@link CommandHandler} constructor. Sets fields and registers the main
     * command through Bukkit to be executed by this handler.
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param plugin The main {@link Plugin} instance
     * @param command The command to write subcommands under
     */
    public CommandHandler(Plugin plugin, String command) {
        this.plugin = plugin;
        this.command = command;
        this.name = Lang.getFormat(plugin);
        final CommandHandler chand = this;
        PluginCommand cmd = this.plugin.getServer().getPluginCommand(command);
        Validate.notNull(cmd, "Attempted to register a non-existant command");
        cmd.setExecutor(chand);
        this.register(new HelpCommand<>(this.plugin, this),
                new ReloadCommand<>(this.plugin, this));
    }

    /**
     * Executes the proper {@link SubCommand}
     * <br><br> {@inheritDoc}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param sender {@inheritDoc}
     * @param cmd The command instance which was executed
     * @param commandLabel {@inheritDoc}
     * @param args {@inheritDoc}
     * @return {@code false}
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 1 || (args.length == 1 && "help".equalsIgnoreCase(args[0]))) {
            args = new String[]{"help", "1"};
        }
        SubCommand<? extends Plugin> scommand = this.getCommand(args[0]);
        if (scommand == null) {
            scommand = this.getCommand("help");
            Exceptions.notNull(scommand, "CommandHandler does not have a help command", IllegalStateException.class);
        }
        try {
            scommand.execute(sender, Arrays.copyOfRange(args, 1, args.length)).handle(sender, this.name, scommand);
        } catch (Exception ex) {
            CommandStatus.FAILED.handle(sender, this.name, scommand);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param sender {@inheritDoc}
     * @param command {@inheritDoc}
     * @param alias {@inheritDoc}
     * @param args {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1) {
            return new ArrayList<>(this.commands.keySet());
        }
        SubCommand<? extends Plugin> scommand = this.getCommand(args[0]);
        if (scommand == null) {
            return new ArrayList<>();
        }
        return scommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }


    /**
     * Returns a subcommand, or {@code null} if none exists.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param name The name of the subcommand
     * @return A relevant {@link SubCommand}, or null if it does not exist
     */
    public final SubCommand<? extends Plugin> getCommand(String name) {
        return this.commands.get(name);
    }

    /**
     * Returns all subcommands as a {@link Collection}.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return A {@link Collection} of all registered {@link SubCommand}
     */
    public final Collection<SubCommand<? extends Plugin>> getCommands() {
        return this.commands.values();
    }

    /**
     * Registers a {@link SubCommand} under the main supplied command name
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param <T> The subcommand type
     * @param command The {@link SubCommand} to register
     * @throws IllegalArgumentException If the command's name is already in use
     * @return The registered subcommand
     */
    public final <T extends SubCommand<? extends Plugin>> T register(T command) {
        Validate.isTrue(!this.isRegistered(command.getName()), "Command already in use: " + command.getName());
        this.commands.put(command.getName(), command);
        return command;
    }

    /**
     * Registers multiple {@link SubCommand} instance under the provided command
     * name. If an exception is encountered, it will continue registering
     * commands and re-throw the exception upon method completion.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param <T> The subcommand type
     * @param commands The {@link SubCommand} instances to register
     * @throws IllegalArgumentException If the command's name is already in use
     */
    public final <T extends SubCommand<? extends Plugin>> void register(T... commands) {
        IllegalArgumentException ex = null;
        for (T scommand : commands) {
            try {
                this.register(scommand);
            } catch (IllegalArgumentException e) {
                if (ex == null) {
                    ex = e;
                }
            }
        }
        if (ex != null) {
            throw ex;
        }
    }

    /**
     * Removes a {@link SubCommand} in use by this handler. If no
     * {@link SubCommand} is registered under the name, then nothing happens
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param name The command to check for
     */
    public void unregister(String name) {
        if (this.isRegistered(name)) {
            this.commands.remove(name);
        }
    }

    /**
     * Checks to see if a {@link SubCommand} is registered under the passed
     * command name
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param name The command to check for
     * @return {@code true} if registered to a {@link SubCommand}
     */
    public boolean isRegistered(String name) {
        return this.commands.containsKey(name);
    }

    /**
     * Returns the main command associated with this {@link CommandHandler}
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return The main command
     */
    public String getMainCommand() {
        return this.command;
    }

}
