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
package com.codelanx.codelanxlib.listener;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

/**
 * Handles listeners for the {@link Plugin}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 * 
 * @param <E> The specific {@link Plugin} to bound all {@link SubListener}
 *            classes to
 */
public class ListenerManager<E extends Plugin> {
    
    protected final E plugin;
    protected final Map<String, SubListener> listeners = new HashMap<>();
    
    /**
     * {@link ListenerManager} constructor
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param plugin The main {@link Plugin} instance
     */
    public ListenerManager(E plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets a listener by its string name. Returns null if the listener is
     * disabled or not registered.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param <T> The {@link SubListener} class to get
     * @param listener An instance of the class type to retrieve
     * @return The listener class, null if disabled or not registered
     */
    public <T extends SubListener<E>> T getListener(Class<T> listener) {
        return (T) this.listeners.get(listener.getName());
    }
    
    /**
     * Returns whether or not a listener is registered under the relevant listener key
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param <T> The {@link SubListener} class to get
     * @param listener The listener class to look for
     * @return {@code true} if registered, {@code false} otherwise
     */
    public <T extends SubListener<E>> boolean isRegistered(Class<T> listener) {
        return this.listeners.containsKey(listener.getName());
    }

    /**
     * Registers a listener through bukkit and {@link ListenerManager}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param <T> The {@link SubListener} class to register
     * @param listener The listener to register
     * @throws ListenerReregisterException Attempted to register a Listener under a similar key
     * @return The listener that was registered
     */
    public <T extends SubListener<E>> T registerListener(T listener) throws ListenerReregisterException {
        String name = listener.getName();
        if (!this.listeners.containsKey(name)) {
            this.listeners.put(name, listener);
            this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
            return listener;
        } else {
            throw new ListenerReregisterException("Listener Map already contains key: " + name);
        }
    }

    /**
     * Calls {@link ListenerManager#registerListener(SubListener)} for every
     * passed listener. Any exception thrown will be re-thrown after all
     * listeners are registered
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param <T> The {@link SubListener} class to register
     * @param listeners The listeners to register
     * @throws ListenerReregisterException Attempted to register a Listener under a similar key
     */
    public <T extends SubListener<E>> void registerListeners(T... listeners) throws ListenerReregisterException {
        ListenerReregisterException ex = null;
        for (T listener : listeners) {
            try {
                this.registerListener(listener);
            } catch (ListenerReregisterException e) {
                if (ex != null) { ex = e; }
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
    
    /**
     * Unregisters all the listeners attached to this {@link ListenerManager}
     * 
     * @since 1.0.0
     * @version 1.0.0
     */
    public void cleanup() {
        this.listeners.values().forEach((l) -> { l.onDisable(); HandlerList.unregisterAll(l); });
    }
}