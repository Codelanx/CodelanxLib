/*
 * Copyright (C) 2014 CodeLanx , All Rights Reserved
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
 * along with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.command;

import com.codelanx.codelanxlib.implementers.Commandable;
import com.codelanx.codelanxlib.lang.InternalLang;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

/**
 * Manages commands abstractly for the {@link Plugin}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 * 
 * @param <E> The specific {@link Plugin} to use
 */
public class CommandHandler<E extends Plugin & Commandable<E>> implements CommandExecutor {

    /** Private {@link Plugin} instance */
    protected final E plugin;
    /** Private {@link HashMap} of subcommands */
    protected final Map<String, SubCommand<E>> commands = new HashMap<>();
    /** The primary command to access this {@link CommandHandler} in-game */
    protected String command;

    /**
     * {@link CommandHandler} constructor
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param plugin The main {@link Plugin} instance
     * @param command The command to write subcommands under
     */
    public CommandHandler(E plugin, String command) {
        this.plugin = plugin;
        
        this.command = command;
        
        final CommandHandler<E> chand = this;
        PluginCommand cmd = this.plugin.getServer().getPluginCommand(command);
        if (cmd == null) {
            throw new NullPointerException("Attempted to register a non-existant command!");
        } else {
            cmd.setExecutor(chand);
        }
    }

    /**
     * Executes the proper {@link SubCommand}
     *
     * @since 1.0.0
     * @version 1.0.0
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
        if (scommand != null) {
            String[] newArgs = new String[args.length - 1];
            for (int i = 0; i < newArgs.length; i++) {
                newArgs[i] = args[i + 1];
            }
            if (scommand.execute(sender, newArgs)) {
                return true;
            } else {
                InternalLang.sendMessage(sender, this.plugin.getName(), InternalLang.COMMAND_HANDLER_USAGE, scommand.getUsage());
                InternalLang.sendMessage(sender, scommand.info());
            }
        } else {
            InternalLang.sendMessage(sender, this.plugin.getName(), InternalLang.COMMAND_HANDLER_UNKNOWN);
        }
        return false;
    }
    
    /**
     * Returns a subcommand, or {@code null} if none exists.
     * 
     * @since 1.0.0
     * @version 1.0.0
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
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return A {@link Collection} of all registered {@link SubCommand}
     */
    public final Collection<SubCommand<E>> getCommands() {
        return this.commands.values();
    }

    /**
     * Returns a permissions check for
     * {@code <plugin-name>.cmd.<subcommand-name>}, can be nested further
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param sender The {@link CommandSender} executing the command
     * @param cmd The {@link SubCommand} being executed
     * @return {@code true} if they have permission, false otherwise
     */
    public boolean hasPermission(CommandSender sender, SubCommand cmd) {
        return sender.hasPermission(this.plugin.getName() + ".cmd." + cmd.getName());
    }

    /**
     * Registers a {@link SubCommand} under the main supplied command name
     * 
     * @since 1.0.0
     * @version 1.0.0
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
     * Registers multiple {@link SubCommand} instance under the provided
     * command name. If an exception is encountered, it will continue
     * registering commands and re-throw the exception upon method completion.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param <T> The subcommand type
     * @param commands The {@link SubCommand} instances to register
     * @throws CommandInUseException If the command's name is already in use
     */
    public final <T extends SubCommand<E>> void registerSubCommands(T... commands) throws CommandInUseException {
        CommandInUseException ex = null;
        for (T command : commands) {
            try {
                this.registerSubCommand(command);
            } catch (CommandInUseException e) {
                if (ex == null) { ex = e; }
            }
        }
        if (ex != null) {
            throw ex;
        }
    }

    /**
     * Returns the main command associated with this {@link CommandHandler}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The main command
     */
    public String getMainCommand() {
        return this.command;
    }

}