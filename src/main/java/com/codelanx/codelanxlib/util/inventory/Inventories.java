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
package com.codelanx.codelanxlib.util.inventory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Utility methods for simplifying the use of Bukkit's inventory API, or
 * providing methods that can accomplish tasks that have obscure or complicated
 * solutions.
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Inventories {

    private Inventories() {
    }

    /**
     * Returns whether or not the player clicked the top or bottom inventory
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param event The relevant {@link InventoryClickEvent}
     * @return {@code true} for clicking the top, {@code false} otherwise
     */
    public static boolean hasClickedTop(InventoryClickEvent event) {
        return event.getRawSlot() == event.getSlot();
    }

    /**
     * Returns the inventory slot for the item in a Player's hand.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param p The player to get the item slot from
     * @return The non-raw slot number of the item being held
     * @throws IllegalStateException if the item is modified during the method
     *                               execution
     */
    public static synchronized int getHeldItemSlot(Player p) {
        byte[] b = new byte[32];
        new Random().nextBytes(b);
        List<String> bitStr = new ArrayList<>(Arrays.asList(Base64.encode(b)));
        ItemStack[] contents = p.getInventory().getContents();
        //Time is of the essence now
        List<String> oldLore = p.getItemInHand().getItemMeta().getLore();
        p.getItemInHand().getItemMeta().setLore(bitStr);
        for (int i = 0; i < 9; i++) {
            if (contents[i].getItemMeta().getLore().equals(bitStr)) {
                p.getItemInHand().getItemMeta().setLore(oldLore);
                return i;
            }
        }
        p.getItemInHand().getItemMeta().setLore(oldLore);
        throw new IllegalStateException("Race conflict with other plugin while running method!");
    }

}
