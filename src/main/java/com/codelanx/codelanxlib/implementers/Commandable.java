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
package com.codelanx.codelanxlib.implementers;

import com.codelanx.codelanxlib.command.CommandHandler;
import org.bukkit.plugin.Plugin;

/**
 * Interface for a {@link Plugin} that makes use of a {@link CommandHandler}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public interface Commandable extends Formatted {

    /**
     * Gets the {@link CommandHandler} for the {@link Plugin}
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return The {@link CommandHandler} instance
     */
    public abstract CommandHandler getCommandHandler();

    /**
     * Returns the main string to use as a command for the primary
     * {@link CommandHandler}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The main command in the form of a string
     */
    public abstract String getMainCommand();

}
