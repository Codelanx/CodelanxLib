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
package com.codelanx.codelanxlib.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Class description for {@link InventoryUtil}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class InventoryUtil {

    /*public static Inventory getSpacedInventory() {
        return null;
    }*/
    
    public static boolean hasClickedTop(InventoryClickEvent event) {
        return event.getRawSlot() == event.getSlot();
    }

    /**
     * Returns the best approximation for the slot relevant to the item held
     * in the player's hand
     * <br><br>
     * Note! This method will fail if there are {@link ItemStack} objects that
     * exactly match the one in the hotbar.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param p The player to get the item slot from
     * @return The non-raw slot number of the item being held
     */
    public static int getHeldItemSlot(Player p) {
        Map<Integer, ItemStack> quickMatch = new HashMap<>();
        ItemStack item;
        for (int i = 0; i < 9; i++) {
            item = p.getInventory().getContents()[i];
            if (item.getType() == p.getItemInHand().getType()) {
                quickMatch.put(i, item);
            }
        }
        if (quickMatch.size() == 1) {
            return quickMatch.entrySet().iterator().next().getKey();
        } else {
            Optional<Integer> opt = quickMatch.entrySet().stream()
                    .filter(i -> i.getValue().equals(p.getItemInHand()))
                    .map(i -> i.getKey()).findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        throw new IllegalStateException("Player hotbar item not found!");
    }

}
