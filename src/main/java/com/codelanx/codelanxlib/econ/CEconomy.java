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
import java.util.Observable;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
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

    protected final Lang format;
    protected Economy econ;

    /**
     * Constructor. Will check if the Vault {@link Economy} class is proxied and
     * if not, then it will manually call for it to be proxied.
     * @param plugin 
     */
    public CEconomy(Plugin plugin) {
        this.format = Lang.getFormat(plugin);
        if (plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            VaultProxy.proxyVault();
            RegisteredServiceProvider<Economy> economyProvider =
                    plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider == null) {
                plugin.getLogger().log(Level.WARNING, "No economy provider found, will not charge players!");
            } else {
                this.econ = economyProvider.getProvider();
            }
            if (!this.isEnabled()) {
                plugin.getLogger().log(Level.WARNING, "No economy found, will not charge players!");
            }
            final CEconomy ce = this;
            VaultProxy.register(ce);
        } else {
            plugin.getLogger().log(Level.WARNING, "No vault found, will not charge players!");
        }
    }

    /**
     * Returns whether or not a specific {@link Player} can be charged an amount
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} to charge
     * @param value A {@link Config} double value representing how much to
     *              take
     * @return A {@link ChargeStatus} representing the total cost and the status
     *         of whether or not the {@link Player} can be charged. If
     *         {@link CEconomy#isEnabled()} returns {@code false}, this will be
     *         a {@link ChargeStatus} of {@code true} with a returned cost of 0
     */
    public ChargeStatus canCharge(Player p, Config value) {
        return this.canCharge(p, value.as(double.class));
    }

    /**
     * Returns whether or not a specific {@link Player} can be charged an amount
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} to charge
     * @param cost The amount to charge
     * @return A {@link ChargeStatus} representing the total cost and the status
     *         of whether or not the {@link Player} can be charged. If
     *         {@link CEconomy#isEnabled()} returns {@code false}, this will be
     *         a {@link ChargeStatus} of {@code true} with a returned cost of 0
     */
    public ChargeStatus canCharge(Player p, double cost) {
        if (!this.isEnabled()) {
            return new ChargeStatus(true, 0);
        }
        if (cost < 0) {
            Lang.sendMessage(p, this.format, InternalLang.ECONOMY_FAILED);
            return new ChargeStatus(false, -1);
        }
        return new ChargeStatus(this.econ.has(p.getName(), cost), cost);
    }

    /**
     * Takes money away from the {@link Player}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} to take from
     * @param value A {@link Config} double value representing how much to
     *              take
     * @return {@code true} if the money was successfully taken, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean charge(Player p, Config value) {
        return this.charge(p, value.as(double.class));
    }

    /**
     * Takes money away from the {@link Player}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} to take money from
     * @param cost The amount of money to take
     * @return {@code true} if the money was successfully taken, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean charge(Player p, double cost) {
        if (!this.isEnabled()) {
            return true;
        }
        if (cost < 0) {
            Lang.sendMessage(p, this.format, InternalLang.ECONOMY_FAILED);
            return false;
        }
        cost *= this.tax();
        EconomyResponse r = this.econ.withdrawPlayer(p.getName(), cost);
        boolean bad = r.type == EconomyResponse.ResponseType.FAILURE;
        if (bad) {
           Lang.sendMessage(p, this.format, InternalLang.ECONOMY_INSUFF, cost);
        }
        this.setChanged();
        this.notifyObservers(new EconomyChangePacket(p, this.getBalance(p)));
        return !bad;
    }

    /**
     * Gives money to the {@link Player}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param p The {@link Player} to give money to
     * @param value A {@link Config} double value representing how much to
     *              give
     * @return {@code true} if the money was deposited successfully, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean pay(Player p, Config value) {
        return this.pay(p, value.as(double.class));
    }

    /**
     * Gives money to the {@link Player}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} to give money to
     * @param amount The amount of money to give
     * @return {@code true} if the money was deposited successfully, or if
     *         {@link CEconomy#isEnabled()} returns {@code false}
     */
    public boolean pay(Player p, double amount) {
        if (!this.isEnabled()) {
            return true;
        }
        amount *= this.bonus();
        EconomyResponse r = this.econ.depositPlayer(p.getName(), amount);
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
        super.notifyObservers((EconomyChangePacket) arg);
    }

    @Override
    protected void setChanged() {
        super.setChanged();
    }

    /**
     * Returns the relevant balance for the {@link Player}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} for which the balanced is checked
     * @return The player's balance, or -1 if {@link CEconomy#isEnabled()}
     *         returns {@code false}
     */
    public double getBalance(Player p) {
        if (!this.isEnabled()) {
            return -1;
        }
        return this.econ.getBalance(p.getName());
    }

    /**
     * Returns whether or not the {@link Economy} object could be retrieved
     * from Vault
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return {@code false} if the {@link Economy} object is {@code null}
     */
    public boolean isEnabled() {
        return this.econ != null;
    }

    /**
     * Returns the underlying {@link Economy} object that is backed by a
     * {@link VaultProxy} {@link InvocationHandler}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The underlying {@link Economy} object
     */
    protected Economy getEconomy() {
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
     * {@link CEconomy#charge(Player, double)} method. This method is purposely
     * overridable to allow for people to specify their own rates. Values that
     * are negative or zero will be automatically converted to 1.
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
        double back = this.taxRate();
        if (back <= 0) {
            back = 1D;
        }
        return back;
    }

    /**
     * Represents a rate that is multiplied to money given through the
     * {@link CEconomy#pay(Player, double)} method. This method is purposely
     * overridable to allow for people to specify their own rates. Values that
     * are negative or zero will be automatically converted to 1.
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