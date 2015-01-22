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
package com.codelanx.codelanxlib.inventory;

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.util.Inventories;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Listens for inventory clicks and conveys information to any appropriate and
 * registered {@link InventoryInterface}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class InterfaceListener implements Listener {

    private final Map<String, InventoryInterface> interfaces = new HashMap<>();

    /**
     * Constructs the listener instance and registers it to Bukkit
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public InterfaceListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, CodelanxLib.get());
    }

    /**
     * Listens to inventory clicks, and conveys said information to an
     * appropriate {@link InventoryInterface}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param event The fired {@link InventoryClickEvent}
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getType() != EntityType.PLAYER) {
            return;
        }
        if (Inventories.hasClickedTop(event)) {
            String title = event.getInventory().getTitle();
            title = title.substring(title.length() - (InventoryInterface.SEED_LENGTH * 2));
            InventoryInterface ii = this.interfaces.get(title);
            if (ii != null) {
                Inventory i = event.getInventory();
                InventoryPanel ip = ii.getPanelBySeed(i.getTitle());
                if (ip != null) {
                    ip.click((Player) event.getWhoClicked(), event.getSlot());
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Registers an {@link InventoryInterface} to this listener
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param ii The {@link InventoryInterface} to register
     */
    public void register(InventoryInterface ii) {
        this.interfaces.put(ii.getSeed(), ii);
    }

}
