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
package com.codelanx.codelanxlib.inventory;

import com.codelanx.commons.util.RNG;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
     * Returns the slot(s) that an {@link Item} would be placed into.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param item The {@link Item} being placed into an {@link Inventory}
     * @param inv The {@link Inventory} being placed into
     * @return The slot(s) that would be affected by this placement
     */
    public static Integer[] findPlacement(Item item, Inventory inv) {
        ItemStack stack = item.getItemStack();
        int amount = stack.getAmount();
        List<Integer> back = new ArrayList<>();
        Map<Integer, ? extends ItemStack> items = inv.all(stack.getType());
        for (Map.Entry<Integer, ? extends ItemStack> ent : items.entrySet()) {
            if (amount <= 0) {
                break;
            }
            back.add(ent.getKey());
            ItemStack i = ent.getValue();
            amount -= i.getMaxStackSize() - i.getAmount();
        }
        if (amount > 0) {
            ItemStack[] inventory = inv.getContents();
            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] == null) {
                    amount -= stack.getMaxStackSize();
                    back.add(i);
                }
            }
        }
        return back.toArray(new Integer[back.size()]);
    }

}
