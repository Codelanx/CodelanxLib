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

import com.codelanx.codelanxlib.config.ConfigMarker;
import com.codelanx.codelanxlib.config.ConfigurationLoader;
import com.codelanx.codelanxlib.lang.InternalLang;
import java.util.Observable;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class CEconomy extends Observable {

    /**
     * Represents the status of a charge
     * 
     * @since 1.0.0
     * @author 1Rogue
     * @version 1.0.0
     */
    public static class ChargeStatus {
        
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

    public class EconomyChangePacket {
        
        private final Player p;
        private final double amount;
        
        public EconomyChangePacket(Player p, double amount) {
            this.p = p;
            this.amount = amount;
        }
        
        public Player getPlayer() {
            return this.p;
        }
        
        public double getAmount() {
            return this.amount;
        }

    }
    
    protected final net.milkbowl.vault.economy.Economy econ;
    
    public CEconomy(Plugin plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider =
                    plugin.getServer().getServicesManager().getRegistration(
                            net.milkbowl.vault.economy.Economy.class
                    );
            this.econ = economyProvider.getProvider();
            if (this.econ == null) {
                plugin.getLogger().log(Level.WARNING, "No economy found, will not charge players!");
            }
        } else {
            this.econ = null;
            plugin.getLogger().log(Level.WARNING, "No vault found, will not charge players!");
        }
        
    }

    public ChargeStatus canCharge(Player p, ConfigMarker value, ConfigurationLoader config) {
        return this.canCharge(p, config.getDouble(value));
    }
    
    public ChargeStatus canCharge(Player p, double cost) {
        if (this.econ == null) {
            return new ChargeStatus(true, 0);
        }
        if (cost < 0) {
            InternalLang.sendMessage(p, InternalLang.ECONOMY_FAILED);
            return new ChargeStatus(false, -1);
        }
        return new ChargeStatus(this.econ.has(p.getName(), cost), cost);
    }

    public boolean charge(Player p, ConfigMarker value, ConfigurationLoader config) {
        return this.charge(p, config.getDouble(value));
    }

    public boolean charge(Player p, double cost) {
        if (this.econ == null) {
            return true;
        }
        if (cost < 0) {
           InternalLang.sendMessage(p, InternalLang.ECONOMY_FAILED);
            return false;
        }
        net.milkbowl.vault.economy.EconomyResponse r = this.econ.withdrawPlayer(p.getName(), cost);
        boolean bad = r.type == net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE;
        if (bad) {
           InternalLang.sendMessage(p, InternalLang.ECONOMY_INSUFF, cost);
        }
        this.setChanged();
        this.notifyObservers(new EconomyChangePacket(p, this.getBalance(p)));
        return !bad;
    }

    public boolean pay(Player p, double amount) {
        if (this.econ == null) {
            return true;
        }
        net.milkbowl.vault.economy.EconomyResponse r = this.econ.depositPlayer(p.getName(), amount);
        this.setChanged();
        this.notifyObservers(new EconomyChangePacket(p, this.getBalance(p)));
        return r.type != net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE;
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

}