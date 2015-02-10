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
package com.codelanx.codelanxlib.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Class description for {@link SubListener}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 *
 * @param <T> The specific {@link Plugin} to use
 */
public abstract class SubListener<T extends Plugin> implements Listener {

    /**
     * The stored {@link Plugin} reference relevant to this {@link SubListener}
     *
     * @since 0.0.1
     * @version 0.0.1
     */
    protected final T plugin;

    /**
     * Stores the {@link Plugin} reference
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param plugin The {@link Plugin} relevant to this {@link SubListener}
     */
    public SubListener(T plugin) {
        this.plugin = plugin;
    }

    /**
     * Called when the plugin is being disabled. This is a convenience method to
     * allow simple cleanup of any resources
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    public void onDisable() {}

    /**
     * Returns the {@link Plugin} used for this {@link SubListener}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The {@link Plugin} that registered this {@link SubListener}
     */
    public T getPlugin() {
        return this.plugin;
    }

    /**
     * Registers the listener to the {@link ListenerManager}. If the listener
     * class is already registered, this method will do nothing.
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public final void register() {
        if (!ListenerManager.isRegistered(this.getClass())) {
            ListenerManager.registerListener(this);
        }
    }

}
