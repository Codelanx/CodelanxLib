/*
 * Copyright (C) 2016 Codelanx, All Rights Reserved
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

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.exception.Exceptions;
import com.codelanx.commons.util.exception.IllegalInvocationException;
import com.codelanx.codelanxlib.util.ReflectBukkit;
import org.apache.commons.lang.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

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
        Validate.isTrue(ListenerManager.isRegistered(listener), "Class is not registered with listener manager");
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
     * Returns {@code true} if the passed {@link Listener} has another Listener
     * of the same class type already registered for bukkit. This should not be
     * used with any listeners that are from an anonymous class, as this will
     * return {@code true} for any other anonymous classes as well
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param p The {@link Plugin} that registers this {@link Listener}
     * @param l The {@link Listener} to check
     * @return {@code true} if registered to bukkit
     */
    public static boolean isRegisteredToBukkit(Plugin p, Listener l) {
        if (l.getClass().isAnonymousClass()) {
            StackTraceElement t = Reflections.getCaller();
            Logging.simple().here().print(Level.WARNING, "Passed an anonymous class from %s:%d", t.getClass().getName(), t.getLineNumber());
        }
        return HandlerList.getRegisteredListeners(p).stream().anyMatch(r -> r.getListener().getClass() == l.getClass());
    }

    /**
     * Registers a listener through Bukkit and {@link ListenerManager}
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
        Validate.isTrue(!ListenerManager.isRegistered(listener.getClass()),
                "Listener Map already contains key: " + listener.getClass().getName());
        ListenerManager.listeners.put(listener.getClass(), listener);
        if (!ListenerManager.isRegisteredToBukkit(listener.getPlugin(), listener)) {
            listener.getPlugin().getServer().getPluginManager().registerEvents(listener, listener.getPlugin());
        }
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
     * @throws IllegalInvocationException Only {@link CodelanxLib} can use
     */
    public static void release() {
        Exceptions.illegalInvocation(Reflections.accessedFrom(CodelanxLib.class),
                "ListenerManager#release may only be called by CodelanxLib");
        ListenerManager.listeners.values().forEach((l) -> {
            l.onDisable();
            HandlerList.unregisterAll(l);
        });
        ListenerManager.listeners.clear();
    }

    /**
     * Allows registering an anonymous listener for any bukkit listeners using
     * Java 8's function API, useful for listening to events in one-liner solutions
     *
     * @since 0.2.0
     * @version 0.2.0
     *
     * @param clazz The {@link Event} fired within Bukkit to listen to
     * @param event A {@link Consumer} of the event, called when the event is fired
     * @param <T> The type of the {@link Event}
     */
    public static <T extends Event> void listen(Class<T> clazz, Consumer<T> event) {
        ListenerManager.registerListener(new SubListener<JavaPlugin>(ReflectBukkit.getCallingPlugin()) {

            @EventHandler
            public void handle(T bukkitEvent) {
                event.accept(bukkitEvent);
            }
        });
    }

}
