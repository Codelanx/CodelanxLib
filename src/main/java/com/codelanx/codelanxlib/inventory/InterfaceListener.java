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
package com.codelanx.codelanxlib.inventory;

import com.codelanx.codelanxlib.util.InventoryUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Class description for {@link InterfaceListener}
 * 
 * TODO:
 * 
 * Move listener to be a singleton listener that allows InventoryInterfaces to
 * register their seeds to them
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public final class InterfaceListener implements Listener {

    private final InventoryInterface ii;

    public InterfaceListener(InventoryInterface ii) {
        this.ii = ii;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getType() != EntityType.PLAYER) {
            return;
        }
        if (InventoryUtil.hasClickedTop(event)) {
            if (event.getInventory().getTitle().endsWith(this.ii.getSeed())) {
                Inventory i = event.getInventory();
                InventoryPanel ip = this.ii.getPanelBySeed(i.getTitle());
                if (ip != null) {
                    ip.click((Player) event.getWhoClicked(), event.getSlot());
                    event.setCancelled(true);
                }
            }
        }
    }

}
