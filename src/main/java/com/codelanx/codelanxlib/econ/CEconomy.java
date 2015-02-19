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

import com.codelanx.codelanxlib.config.Config;
import com.codelanx.codelanxlib.config.Lang;
import com.codelanx.codelanxlib.internal.InternalLang;
import com.codelanx.codelanxlib.util.exception.Exceptions;
import java.util.Observable;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Represents an observable façade class for Vault's {@link Economy} object
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public class CEconomy extends Observable {

    /** The {@link Lang} format to use for output */
    protected final Lang format;
    /** The underlying {@link Economy} object, usually a proxy */
    private Economy econ;

    /**
     * Sets the format string for this object to use for output
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param plugin The {@link Plugin} that instantiated this class
     */
    public CEconomy(Plugin plugin) {
        this.format = Lang.getFormat(plugin);
    }

    /**
     * Returns whether or not a specific {@link OfflinePlayer} can be charged an
     * amount
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param p The {@link OfflinePlayer} to check if they can be charged
     * @param value A {@link Config} double value representing how much to take
     * @return A {@link ChargeStatus} representing the total cost and the status
     *         of whether or not the {@link OfflinePlayer} can be charged. If
     *         {@link CEconomy#isEnabled()} returns {@code false}, this will be
     *         a {@link ChargeStatus} of {@code true} with a returned cost of 0
     */
    public ChargeStatus canCharge(OfflinePlayer p, Config value) {
        return this.canCharge(p, value.as(double.class));
    }

    /**
     * Returns whether or not a specific {@link OfflinePlayer} can be charged an
     * amount
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param p The {@link OfflinePlayer} to check if they can be charged
     * @param cost The amount to charge
     * @return A {@link ChargeStatus} representing the total cost and the status
     *         of whether or not the {@link OfflinePlayer} can be charged. If
     *         {@link CEconomy#isEnabled()} returns {@code false}, this will be
     *         a {@link ChargeStatus} of {@code true} with a returned cost of 0
     */
    public ChargeStatus canCharge(OfflinePlayer p, double cost) {
        if (!this.isEnabled()) {
            return new ChargeStatus(true, 0);
        }
        cost *= this.bonus();
        if (cost < 0) {
            if (p.isOnline()) {
                Lang.sendMessage(p.getPlayer(), this.format, InternalLang.ECONOMY_FAILED);
            }
            return new ChargeStatus(false, -1);
        }
        return new ChargeStatus(this.getEconomy().has(p, cost), cost);
    }

    /**
     * Takes money away from the {@link OfflinePlayer}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param p The {@link OfflinePlayer} to take money from
     * @param value A {@link Config} double value representing how much to take
     * @return {@code true} if the money was successfully taken, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean charge(OfflinePlayer p, Config value) {
        return this.charge(p, value.as(double.class));
    }

    /**
     * Takes money away from the {@link OfflinePlayer}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param p The {@link OfflinePlayer} to take money from
     * @param cost The amount of money to take
     * @return {@code true} if the money was successfully taken, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean charge(OfflinePlayer p, double cost) {
        if (!this.isEnabled()) {
            return true;
        }
        if (cost < 0) {
            if (p.isOnline()) {
                Lang.sendMessage(p.getPlayer(), this.format, InternalLang.ECONOMY_FAILED);
            }
            return false;
        }
        cost *= this.tax();
        EconomyResponse r = this.getEconomy().withdrawPlayer(p, cost);
        boolean bad = r.type == EconomyResponse.ResponseType.FAILURE;
        if (bad) {
            if (p.isOnline()) {
                Lang.sendMessage(p.getPlayer(), this.format, InternalLang.ECONOMY_INSUFF, cost);
            }
        }
        this.setChanged();
        this.notifyObservers(new EconomyChangePacket(p, this.getBalance(p)));
        return !bad;
    }

    /**
     * Gives money to the {@link OfflinePlayer}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param p The {@link OfflinePlayer} to give money to
     * @param value A {@link Config} double value representing how much to give
     * @return {@code true} if the money was deposited successfully, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean pay(OfflinePlayer p, Config value) {
        return this.pay(p, value.as(double.class));
    }

    /**
     * Gives money to the {@link OfflinePlayer}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param p The {@link OfflinePlayer} to give money to
     * @param amount The amount of money to give
     * @return {@code true} if the money was deposited successfully, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean pay(OfflinePlayer p, double amount) {
        if (!this.isEnabled()) {
            return true;
        }
        amount *= this.bonus();
        EconomyResponse r = this.getEconomy().depositPlayer(p, amount);
        this.notifyObservers(new EconomyChangePacket(p, this.getBalance(p)));
        return r.type != EconomyResponse.ResponseType.FAILURE;
    }

    /**
     * Modified to send a Bukkit event and force notification of the change
     * <br><br> {@inheritDoc}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param arg An {@link EconomyChangePacket}
     */
    @Override
    public void notifyObservers(Object arg) {
        this.setChanged();
        if (!(arg instanceof EconomyChangePacket)) {
            return;
        }
        super.notifyObservers(arg);
    }

    /**
     * Allows the protected {@link Observable#setChanged()} method to be called
     * by {@link VaultProxy}
     * <br><br>{@inheritDoc}
     *
     * @since 0.0.1
     * @version 0.0.1
     */
    @Override
    protected void setChanged() {
        super.setChanged();
    }

    /**
     * Returns the relevant balance for the {@link OfflinePlayer}
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param p The {@link OfflinePlayer} for which the balanced is checked
     * @return The OfflinePlayer's balance, or -1 if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public double getBalance(OfflinePlayer p) {
        if (!this.isEnabled()) {
            return -1;
        }
        return this.getEconomy().getBalance(p);
    }

    /**
     * Returns whether or not the {@link Economy} object could be retrieved from
     * Vault
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @return {@code false} if the {@link Economy} object is {@code null}
     */
    public final boolean isEnabled() {
        return this.getEconomy() != null;
    }

    /**
     * Returns the underlying {@link Economy} object that is backed by a
     * {@link VaultProxy} InvocationHandler
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return The underlying {@link Economy} object
     */
    protected final Economy getEconomy() {
        if (this.econ == null) {
            RegisteredServiceProvider<Economy> rsp
                    = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Exceptions.notNull(rsp, "No registered economy handler!", IllegalStateException.class);
            this.econ = rsp.getProvider();
        }
        return this.econ;
    }

    /**
     * The internal retrieval for the tax rate of money. Adds a verification
     * step to ensure values are not negative or zero
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The return value of {@link CEconomy#taxRate()}, or 1 if the value
     *         was negative or zero
     */
    private double tax() {
        double back = this.taxRate();
        if (back < 0) {
            back = 1D;
        }
        return back;
    }

    /**
     * Represents a rate that is multiplied to money taken through the
     * {@link CEconomy#charge(OfflinePlayer, double)} method. This method is
     * purposely overridable to allow for people to specify their own rates.
     * Values that are negative or zero will be automatically converted to 1.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The rate at which given money is multiplied
     */
    public double taxRate() {
        return 1D;
    }

    /**
     * The internal retrieval for the bonus rate of money. Adds a verification
     * step to ensure values are not negative or zero
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The return value of {@link CEconomy#bonusRate()}, or 1 if the
     *         value was negative or zero
     */
    private double bonus() {
        double back = this.bonusRate();
        if (back <= 0) {
            back = 1D;
        }
        return back;
    }

    /**
     * Represents a rate that is multiplied to money given through the
     * {@link CEconomy#pay(OfflinePlayer, double)} method. This method is
     * purposely overridable to allow for people to specify their own rates.
     * Values that are negative or zero will be automatically converted to 1.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The rate at which given money is multiplied
     */
    public double bonusRate() {
        return 1D;
    }

}
