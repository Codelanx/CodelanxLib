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
package com.codelanx.codelanxlib.listener;

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.util.Exceptions;
import com.codelanx.codelanxlib.util.Exceptions.IllegalPluginAccessException;
import com.codelanx.codelanxlib.util.Reflections;
import com.codelanx.codelanxlib.util.Scheduler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

/**
 * Centrally holds references to different Listener classes to allow for
 * retrieval of {@link SubListener} references
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class ListenerManager {

    /**
     * Private constructor to prevent instantiation
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    private ListenerManager() {
    }

    /**
     * Maps {@link SubListener} class types to a {@link ListenerPluginPair} for
     * easy reference of both the {@link SubListener} instance as well as the
     * {@link Plugin} responsible for registering it.
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends SubListener>, ListenerPluginPair<?>> listeners = new HashMap<>();

    /**
     * Gets a listener by its string name. Returns null if the listener is
     * disabled or not registered.
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param <T> The {@link SubListener} class to get
     * @param listener An instance of the class type to retrieve
     * @return The listener class, null if disabled or not registered
     * @throws IllegalArgumentException If the listener isn't registered
     */
    public static <T extends SubListener<?>> T getListener(Class<T> listener) {
        return (T) (ListenerManager.getPair(listener).getListener());
    }

    /**
     * Returns the {@link Plugin} relevant to the passed {@link SubListener}
     * class
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param listener The {@link SubListener} class that was registered
     * @return
     * @throws IllegalArgumentException If the listener isn't registered
     */
    public static Plugin getRegisteringPlugin(Class<? extends SubListener<?>> listener) {
        return ListenerManager.getPair(listener).getPlugin();
    }

    /**
     * Returns the {@link ListenerPluginPair} associated with the passed
     * {@link SubListener} class
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param listener The {@link SubListener} class to check for
     * @return The relevant {@link ListenerPluginPair}
     * @throws IllegalArgumentException If the listener isn't registered
     */
    private static ListenerPluginPair<?> getPair(Class<? extends SubListener<?>> listener) {
        Validate.isTrue(ListenerManager.isRegistered(listener), "Class is not registered with listener manager!");
        return ListenerManager.listeners.get(listener);
    }

    /**
     * Returns whether or not a listener is registered under the relevant
     * listener key
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param <T> The {@link SubListener} class to get
     * @param listener The listener class to look for
     * @return {@code true} if registered, {@code false} otherwise
     */
    public static <T extends SubListener<?>> boolean isRegistered(Class<T> listener) {
        return ListenerManager.listeners.containsKey(listener);
    }

    /**
     * Registers a listener through bukkit and {@link ListenerManager}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param <T> The {@link SubListener} class to register
     * @param plugin The {@link Plugin} registering the {@link SubListener}
     * @param listener The listener to register
     * @throws IllegalArgumentException Attempted to register a Listener twice
     * @return The listener that was registered
     */
    public static <T extends SubListener<?>> T registerListener(Plugin plugin, T listener) {
        Validate.isTrue(!ListenerManager.listeners.containsKey(listener.getClass()),
                "Listener Map already contains key: " + listener.getClass().getName());
        ListenerManager.listeners.put(listener.getClass(), new ListenerPluginPair<>(listener, plugin));
        //SO HACKY - ensures objection creation before bukkit events will fire
        Scheduler.runAsyncTask(() -> {
            plugin.getServer().getScheduler().callSyncMethod(plugin, () -> {
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                return null;
            });
        }, 1);
        return listener;
    }

    /**
     * Calls {@link ListenerManager#registerListener(SubListener)} for every
     * passed listener. Any exception thrown will be re-thrown after all
     * listeners are registered
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param <T> The {@link SubListener} class to register
     * @param plugin The {@link Plugin} registering the {@link SubListener}s
     * @param listeners The listeners to register
     * @throws IllegalArgumentException Attempted to register a Listener twice
     */
    public static <T extends SubListener<?>> void registerListeners(Plugin plugin, T... listeners) {
        IllegalArgumentException ex = null;
        for (T listener : listeners) {
            try {
                ListenerManager.registerListener(plugin, listener);
            } catch (IllegalArgumentException e) {
                if (ex != null) {
                    ex = e;
                }
            }
        }
        if (ex != null) {
            throw ex;
        }
    }

    /**
     * Unregisters a specific {@link SubListener} from both CodelanxLib and
     * Bukkit
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param listener The {@link SubListener} class to unregister
     */
    public static void unregisterListener(Class<? extends SubListener<?>> listener) {
        ListenerPluginPair<?> lpp = ListenerManager.listeners.remove(listener);
        if (lpp != null) {
            HandlerList.unregisterAll(lpp.getListener());
        }
    }

    /**
     * Unregisters all the listeners attached to this {@link ListenerManager}.
     * Can only be called from {@link CodelanxLib}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @throws IllegalPluginAccessException Only {@link CodelanxLib} can use
     */
    public static void release() {
        Exceptions.illegalPluginAccess(Reflections.accessedFrom(CodelanxLib.class),
                "ListenerManager#release may only be called by CodelanxLib!");
        ListenerManager.listeners.values().forEach((l) -> {
            l.getListener().onDisable();
            HandlerList.unregisterAll(l.getListener());
        });
        ListenerManager.listeners.clear();
    }

    /**
     * Private helper class for holding a reference to both the actual
     * {@link SubListener} instance, as well as the {@link Plugin} that
     * registered it.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <E> The type bounding for the {@link Plugin}
     */
    private static class ListenerPluginPair<E extends Plugin> {

        private final E plugin;
        private final SubListener<?> listener;

        /**
         * Class constructor, stores both the {@link Plugin} and
         * {@link SubListener} instances
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param listener The {@link SubListener} reference to store
         * @param plugin The {@link Plugin} reference to store
         */
        public ListenerPluginPair(SubListener<?> listener, E plugin) {
            this.plugin = plugin;
            this.listener = listener;
        }

        /**
         * Returns the stored {@link Plugin} instance
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The stored {@link Plugin} reference
         */
        public E getPlugin() {
            return plugin;
        }

        /**
         * Returns the stored {@link SubListener}{@literal <?>} reference
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The stored {@link SubListener}{@literal <?>} reference
         */
        public SubListener<?> getListener() {
            return listener;
        }

    }
}
