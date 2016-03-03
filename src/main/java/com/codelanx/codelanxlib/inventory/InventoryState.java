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

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * An immutable class that represents the state of an {@link Inventory}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class InventoryState {

    private final ItemStack[] contents;

    /**
     * Constructor. Copies the contents of the passed inventory
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param i The {@link Inventory} to copy
     */
    public InventoryState(Inventory i) {
        ItemStack[] arr = i.getContents();
        this.contents = Arrays.copyOf(arr, arr.length);
    }

    /**
     * Returns a copy of the underlying array from the inventory contents.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @deprecated Returns an array copy, should not be used heavily
     * @return A copy of the inventory contents
     */
    public ItemStack[] getContents() {
        return Arrays.copyOf(this.contents, this.contents.length);
    }

    /**
     * Returns the {@link ItemStack} in the given inventory slot
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param index The index or "slot"  to retrieve from
     * @return The {@link ItemStack} in the relevant index
     * @throws ArrayIndexOutOfBoundsException {@code 0 <= index < size()}
     */
    public ItemStack getItem(int index) {
        return this.contents[index];
    }

    /**
     * Returns the length of the underlying {@link ItemStack} array
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The length of the underlying contents array 
     */
    public int size() {
        return this.contents.length;
    }

    /**
     * Sets the contents of this state into an {@link InventoryHolder}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param holder The {@link InventoryHolder} to set the contents of
     */
    public void setContents(InventoryHolder holder) {
        holder.getInventory().setContents(this.getContents());
    }

}
