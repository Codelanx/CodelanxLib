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

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a player balance changes via Vault
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class EconomyChangeEvent extends PlayerEvent {

    /** {@link HandlerList} for this event */
    protected static final HandlerList handlers = new HandlerList();
    /** The new balance */
    private final double money;

    /**
     * Constructor. Assigns the {@link Player} and money to fields
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param p The {@link Player} whose balance changed
     * @param money The new balance
     */
    public EconomyChangeEvent(Player p, double money) {
        super(p);
        this.money = money;
    }

    /**
     * Returns the new balance for the {@link Player}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The new balance
     */
    public double getNewBalance() {
        return this.money;
    }

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
