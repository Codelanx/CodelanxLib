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
import com.codelanx.codelanxlib.events.EconomyChangeEvent;
import com.codelanx.codelanxlib.config.Lang;
import com.codelanx.codelanxlib.internal.InternalLang;
import java.util.Observable;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public final class CEconomy extends Observable {

    protected final Lang format;
    private Economy econ;

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
            if (this.econ == null) {
                plugin.getLogger().log(Level.WARNING, "No economy found, will not charge players!");
            }
            final CEconomy ce = this;
            VaultProxy.register(ce);
        } else {
            plugin.getLogger().log(Level.WARNING, "No vault found, will not charge players!");
        }
    }

    public ChargeStatus canCharge(Player p, Config value) {
        return this.canCharge(p, value.as(double.class));
    }
    
    public ChargeStatus canCharge(Player p, double cost) {
        if (this.econ == null) {
            return new ChargeStatus(true, 0);
        }
        if (cost < 0) {
            Lang.sendMessage(p, this.format, InternalLang.ECONOMY_FAILED);
            return new ChargeStatus(false, -1);
        }
        return new ChargeStatus(this.econ.has(p.getName(), cost), cost);
    }

    public boolean charge(Player p, Config value) {
        return this.charge(p, value.as(Double.class));
    }

    public boolean charge(Player p, double cost) {
        if (this.econ == null) {
            return true;
        }
        if (cost < 0) {
            Lang.sendMessage(p, this.format, InternalLang.ECONOMY_FAILED);
            return false;
        }
        EconomyResponse r = this.econ.withdrawPlayer(p.getName(), cost);
        boolean bad = r.type == EconomyResponse.ResponseType.FAILURE;
        if (bad) {
           Lang.sendMessage(p, this.format, InternalLang.ECONOMY_INSUFF, cost);
        }
        this.setChanged();
        this.notifyObservers(new EconomyChangePacket(p, this.getBalance(p)));
        return !bad;
    }

    public boolean pay(Player p, double amount) {
        if (this.econ == null) {
            return true;
        }
        EconomyResponse r = this.econ.depositPlayer(p.getName(), amount);
        this.notifyObservers(new EconomyChangePacket(p, this.getBalance(p)));
        return r.type != EconomyResponse.ResponseType.FAILURE;
    }

    /**
     * Modified to send a Bukkit event and force notification of the change
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param arg An {@link EconomyChangePacket}
     */
    @Override
    public void notifyObservers(Object arg) {
        this.setChanged();
        if (!(arg instanceof EconomyChangePacket)) {
            return;
        }
        EconomyChangePacket packet = (EconomyChangePacket) arg;
        Bukkit.getServer().getPluginManager().callEvent(new EconomyChangeEvent(packet.getPlayer(), packet.getAmount()));
        super.notifyObservers(arg);
    }

    @Override
    protected void setChanged() {
        super.setChanged();
    }

    public double getBalance(Player p) {
        if (this.econ == null) {
            return -1;
        }
        return this.econ.getBalance(p.getName());
    }

    public boolean isEnabled() {
        return this.econ != null;
    }

    protected Economy getEconomy() {
        return this.econ;
    }

}