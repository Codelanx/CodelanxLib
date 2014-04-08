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
package com.codelanx.codelanxlib.command;

import org.bukkit.command.CommandSender;

/**
 * Skeleton class representing the structure of a command for use in {@link PhanaticWalls}.
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public abstract class SubCommand {

    /** The main {@link PhanaticWalls} instance */
    protected final CommandHandler handler;

    /**
     * {@link SubCommand} constructor
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param handler The {@link CommandHandler} associated with this command
     */
    public SubCommand(CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Executes a relevant command grabbed from the CommandHandler.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param sender The command executor
     * @param args The command arguments, starting after the command name
     *
     * @return true on success, false if failed
     */
    public abstract boolean execute(CommandSender sender, String[] args);

    /**
     * Returns the name of the command, used for storing a hashmap of the
     * commands
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The command's name
     */
    public abstract String getName();

    /**
     * Returns the command usage
     * <br /><br />
     * TODO: Make this better/dynamic
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return Usage for this {@link SubCommand}
     */
    public String getUsage() {
        return "/" + this.handler.getMainCommand() + " " + this.getName();
    }

    /**
     * Information about this specific command. Should be kept concise
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return A small string about the command
     */
    public abstract String info();

}