/*
 * Copyright (C) 2014 Codelanx, All Rights Reserved
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
 * long with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.econ;

/**
 * Represents the status of a charge
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class ChargeStatus {

    private final boolean status;
    private final double amount;

    protected ChargeStatus(boolean status, double amount) {
        this.status = status;
        this.amount = amount;
    }

    public boolean getStatus() {
        return this.status;
    }

    public double getAmount() {
        return this.amount;
    }

}
