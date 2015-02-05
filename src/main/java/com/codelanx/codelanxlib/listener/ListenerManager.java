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
     * Maps {@link SubListener} class types to a {@link SubListener} instance
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends SubListener>, SubListener<?>> listeners = new HashMap<>();

    /**
     * Gets a listener by its string name. Returns null if the listener is
     * disabled or not registered.
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param <S> The {@link SubListener} type
     * @param listener An instance of the class type to retrieve
     * @return The listener class, null if disabled or not registered
     * @throws IllegalArgumentException If the listener isn't registered
     */
    public static <S extends SubListener<? extends Plugin>> S getListener(Class<S> listener) {
        Validate.isTrue(ListenerManager.isRegistered(listener), "Class is not registered with listener manager!");
        return (S) ListenerManager.listeners.get(listener);
    }

    /**
     * Returns the {@link Plugin} relevant to the passed {@link SubListener}
     * class
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <P> The {@link Plugin} type
     * @param <S> The {@link SubListener} type
     * @param listener The {@link SubListener} class that was registered
     * @return The {@link Plugin} that registered the {@link SubListener}
     * @throws IllegalArgumentException If the listener isn't registered
     */
    public static <P extends Plugin, S extends SubListener<P>> P getRegisteringPlugin(Class<S> listener) {
        return ListenerManager.getListener(listener).getPlugin();
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
     * @param <S> The {@link SubListener} class to register
     * @param listener The listener to register
     * @throws IllegalArgumentException Attempted to register a Listener twice
     * @return The listener that was registered
     */
    public static <S extends SubListener<?>> S registerListener(S listener) {
        Validate.isTrue(!ListenerManager.listeners.containsKey(listener.getClass()),
                "Listener Map already contains key: " + listener.getClass().getName());
        ListenerManager.listeners.put(listener.getClass(), listener);
        //SO HACKY - ensures objection creation before bukkit events will fire
        Scheduler.runAsyncTask(() -> {
            listener.getPlugin().getServer().getScheduler().callSyncMethod(listener.getPlugin(), () -> {
                listener.getPlugin().getServer().getPluginManager().registerEvents(listener, listener.getPlugin());
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
     * @param listeners The listeners to register
     * @throws IllegalArgumentException Attempted to register a Listener twice
     */
    public static <T extends SubListener<?>> void registerListeners(T... listeners) {
        IllegalArgumentException ex = null;
        for (T listener : listeners) {
            try {
                ListenerManager.registerListener(listener);
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
        if (ListenerManager.isRegistered(listener)) {
            HandlerList.unregisterAll(ListenerManager.listeners.remove(listener));
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
            l.onDisable();
            HandlerList.unregisterAll(l);
        });
        ListenerManager.listeners.clear();
    }

}
