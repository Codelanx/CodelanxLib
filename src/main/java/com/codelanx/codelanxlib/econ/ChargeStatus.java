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
package com.codelanx.codelanxlib.econ;

/**
 * Represents the result of a call to
 * {@link CEconomy#canCharge(org.bukkit.OfflinePlayer, double) CEconomy#canCharge}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class ChargeStatus {

    /** Whether or not the transaction is capable of being done */
    private final boolean status;
    /** The amount being charged */
    private final double amount;

    /**
     * Constructor. Assigns parameters to fields
     * 
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param status {@code true} if the charge can be made
     * @param amount The actual amount being charged (accounts for taxes, etc)
     */
    protected ChargeStatus(boolean status, double amount) {
        this.status = status;
        this.amount = amount;
    }

    /**
     * Returns whether or not the player is capable of paying the charge
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return {@code true} if the player can pay the charge
     */
    public boolean getStatus() {
        return this.status;
    }

    /**
     * The actual amount being charged, including any modifications such as
     * taxes. Outputs to the player about balance should use this return value
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The amount being charged
     */
    public double getAmount() {
        return this.amount;
    }

}
