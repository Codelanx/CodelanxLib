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
package com.codelanx.codelanxlib.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player balance changes via Vault
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public class EconomyChangeEvent extends Event {

    /** {@link HandlerList} for this event */
    protected static final HandlerList handlers = new HandlerList();
    /** The new balance */
    private final double money;
    /** The {@link OfflinePlayer} whose money changed */
    private final OfflinePlayer p;

    /**
     * Constructor. Assigns the {@link OfflinePlayer} and money to fields
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link OfflinePlayer} whose balance changed
     * @param money The new balance
     */
    public EconomyChangeEvent(OfflinePlayer p, double money) {
        this.p = p;
        this.money = money;
    }

    /**
     * The {@link OfflinePlayer} whose balance changed
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @return The relevant {@link OfflinePlayer} object 
     */
    public OfflinePlayer getPlayer() {
        return this.p;
    }

    /**
     * Returns the new balance for the {@link OfflinePlayer}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The new balance
     */
    public double getNewBalance() {
        return this.money;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return {@inheritDoc} 
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the {@link HandlerList} that Bukkit uses to register plugins
     * for events
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The internal {@link HandlerList} for this event
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
