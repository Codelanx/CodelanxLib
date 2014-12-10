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
package com.codelanx.codelanxlib.implementers;

import com.codelanx.codelanxlib.command.CommandHandler;
import org.bukkit.plugin.Plugin;

/**
 * Interface for a plugin that makes use of a {@link CommandHandler}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public interface Commandable<E extends Plugin & Commandable<E>> extends Formatted {

    /**
     * Gets the {@link CommandHandler} for the plugin
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The {@link CommandHandler} instance
     */
    public abstract CommandHandler<E> getCommandHandler();

}
