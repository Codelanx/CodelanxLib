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

import com.codelanx.codelanxlib.implementers.Commandable;
import com.codelanx.codelanxlib.config.lang.Lang;
import com.codelanx.codelanxlib.util.Exceptions;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

/**
 * Manages commands abstractly for the {@link Plugin}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 *
 * @param <E> The specific {@link Plugin} to use
 */
public class CommandHandler<E extends Plugin> implements CommandExecutor {

    /** The format for output */
    protected final Lang name;
    /** Private {@link Plugin} instance */
    protected final E plugin;
    /** Private {@link HashMap} of subcommands */
    protected final Map<String, SubCommand<E>> commands = new HashMap<>();
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
    public CommandHandler(E plugin, String command) {
        this.plugin = plugin;
        this.command = command;
        this.name = Lang.getFormat(plugin);
        //this.root = new RootToken<>(plugin, this.command);
        final CommandHandler<E> chand = this;
        PluginCommand cmd = this.plugin.getServer().getPluginCommand(command);
        Validate.notNull(cmd, "Attempted to register a non-existant command!");
        cmd.setExecutor(chand);
        this.registerSubCommands(new HelpCommand<>(this.plugin, this),
                new ReloadCommand<>(this.plugin, this));
    }

    /**
     * Executes the proper {@link SubCommand}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param sender The command executor
     * @param cmd The command instance
     * @param commandLabel The command name
     * @param args The command arguments
     *
     * @return Success of command, false if no command is found
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 1 || (args.length == 1 && "help".equalsIgnoreCase(args[0]))) {
            args = new String[]{"help", "1"};
        }
        SubCommand<E> scommand = this.getCommand(args[0]);
        if (scommand == null) {
            scommand = this.getCommand("help");
            Exceptions.illegalState(scommand != null, "CommandHandler does not have a help command!");
        }
        scommand.execute(sender, Arrays.copyOfRange(args, 1, args.length)).handle(sender, this.name, scommand);
        return false;
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
    public final SubCommand<E> getCommand(String name) {
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
    public final Collection<SubCommand<E>> getCommands() {
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
     * @throws CommandInUseException If the command's name is already in use
     * @return The registered subcommand
     */
    public final <T extends SubCommand<E>> T registerSubCommand(T command) throws CommandInUseException {
        if (this.commands.containsKey(command.getName())) {
            throw new CommandInUseException("Command already in use: " + command.getName());
        } else {
            this.commands.put(command.getName(), command);
            return command;
        }
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
     * @throws CommandInUseException If the command's name is already in use
     */
    public final <T extends SubCommand<E>> void registerSubCommands(T... commands) throws CommandInUseException {
        CommandInUseException ex = null;
        for (T scommand : commands) {
            try {
                this.registerSubCommand(scommand);
            } catch (CommandInUseException e) {
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
    public void unregisterSubCommand(String name) {
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
