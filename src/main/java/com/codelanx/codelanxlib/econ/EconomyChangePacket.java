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
package com.codelanx.codelanxlib.econ;

import org.bukkit.OfflinePlayer;

/**
 * Packet used to describe a change in balance from a transaction with Vault
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public class EconomyChangePacket {

    /** The relevant {@link OfflinePlayer} to this packet */
    private final OfflinePlayer p;
    /** The new balance */
    private final double amount;

    /**
     * Constructor. Assigns parameters to fields
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link OfflinePlayer} whose balance changed
     * @param amount The new balance
     */
    public EconomyChangePacket(OfflinePlayer p, double amount) {
        this.p = p;
        this.amount = amount;
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
     * Returns the new balance
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The new balance
     */
    public double getAmount() {
        return this.amount;
    }

}
