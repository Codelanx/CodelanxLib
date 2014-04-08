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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

/**
 * Handles listeners for the {@link Plugin}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 * 
 * @param <T> The specific plugin to use
 */
public class ListenerManager<T extends Plugin> {
    
    private final T plugin;
    private final Map<String, SubListener> listeners = new HashMap<>();
    
    /**
     * {@link ListenerManager} constructor
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param plugin The main {@link Plugin} instance
     */
    public ListenerManager(T plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Gets a listener by its string name. Returns null if the listener is
     * disabled or not registered.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @deprecated Use {@link #getListener(com.codelanx.codelanxlib.listener.SubListener)} 
     * @param name Name of the listener
     * @return The listener class, null if disabled or not registered
     */
    public SubListener getListener(String name) {
        return this.listeners.get(name);
    }

    /**
     * Gets a listener by its string name. Returns null if the listener is
     * disabled or not registered.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @deprecated Use {@link #getListener(com.codelanx.codelanxlib.listener.SubListener)} 
     * @param <T> The {@link SubListener} class to get
     * @param listener An instance of the class type to retrieve
     * @return The listener class, null if disabled or not registered
     */
    public <T extends SubListener> SubListener getListener(Class<T> listener) {
        try {
            Method m = listener.getDeclaredMethod("getName");
            return this.listeners.get((String) m.invoke(null));
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Error reflecting listener field for name!", ex);
        }
        return null;
    }
    
    /**
     * Returns whether or not a listener is registered under the relevant listener key
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param name The key to look for
     * @return {@code true} if registered, {@code false} otherwise
     */
    public boolean isRegistered(String name) {
        return this.listeners.containsKey(name);
    }

    /**
     * Registers a listener through bukkit and {@link ListenerManager}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param name The name to place the listener as, cannot be the same as a current listener
     * @param listener The listener to register
     * @throws ListenerReregisterException Attempted to register a Listener under a similar key
     */
    public void registerListener(String name, SubListener listener) throws ListenerReregisterException {
        if (!this.listeners.containsKey(name)) {
            this.listeners.put(name, listener);
            this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
        } else {
            throw new ListenerReregisterException("Listener Map already contains key: " + name);
        }
    }
    
    /**
     * Unregisters all the listeners attached to {@link Nations}
     * 
     * @since 1.0.0
     * @version 1.0.0
     */
    public void cleanup() {
        HandlerList.unregisterAll(this.plugin);
    }
}