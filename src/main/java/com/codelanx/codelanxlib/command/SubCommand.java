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
package com.codelanx.codelanxlib.command;

import com.codelanx.codelanxlib.config.Lang;
import com.codelanx.codelanxlib.implementers.Commandable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

/**
 * Skeleton class representing the structure of a sub-command for
 * {@link CommandHandler}
 * <br><br>
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 * 
 * @param <E> Represents a {@link Plugin} that implements the
 *            {@link Commandable} interface
 */
public abstract class SubCommand<E extends Plugin> implements Comparable<SubCommand<E>> {

    /** The main {@link Plugin} instance */
    protected final E plugin;
    /** The {@link CommandHandler} responsible for this command */
    protected final CommandHandler<E> handler;

    /**
     * {@link SubCommand} constructor
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param plugin The {@link Plugin} associated with this command
     * @param handler The {@link CommandHandler} responsible for this command
     */
    public SubCommand(E plugin, CommandHandler<E> handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    /**
     * Executes a relevant command grabbed from the {@link CommandHandler}.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param sender The command executor
     * @param args The command arguments, starting after the command name
     *
     * @return The {@link CommandStatus} representing the result of the command
     */
    public abstract CommandStatus execute(CommandSender sender, String... args);

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
     * @version 0.1.0
     *
     * @return Usage for this {@link SubCommand}
     */
    public String getUsage() {
        return "/" + this.handler.getMainCommand() + " " + this.getName();
    }

    /**
     * Information about this specific command. Should be kept concise
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @return A small string about the command
     */
    public abstract Lang info();

    /**
     * Returns a permissions check for
     * {@code <plugin-name>.cmd.<subcommand-name>}, can be nested into further
     * permissions by passing tokens to append to the end of the permission
     * string
     * 
     * TODO: Figure out catch for multiple CommandHandlers
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param sender The {@link CommandSender} executing this command
     * @param tokens Any additional tokens to append to the end of the string
     * @return {@code true} if they have permission, {@code false} otherwise
     */
    public boolean hasPermission(CommandSender sender, String... tokens) {
        List<String> lis = new LinkedList<>();
        lis.add(this.plugin.getName().toLowerCase());
        lis.add("cmd");
        lis.add(this.getName());
        lis.addAll(Arrays.asList(tokens));
        String perm = String.join(".", lis);
        //register perm to Bukkit
        this.plugin.getServer().getPluginManager().addPermission(new Permission(perm));
        return sender.hasPermission(perm);
    }

    /**
     * Compares {@link SubCommand} objects by command name via
     * {@link SubCommand#getName()}
     * <br><br> {inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param o The {@link SubCommand} to compare to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(SubCommand<E> o) {
        //Do not check for null, comparable contract calls for NPE
        return this.getName().compareTo(o.getName());
    }

}