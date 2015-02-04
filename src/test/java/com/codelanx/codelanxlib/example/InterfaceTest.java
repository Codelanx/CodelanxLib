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
package com.codelanx.codelanxlib.example;

import com.codelanx.codelanxlib.inventory.InventoryInterface;
import com.codelanx.codelanxlib.inventory.InventoryPanel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class description for {@link InterfaceTest}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class InterfaceTest {

    private final InventoryInterface internal;

    public InterfaceTest() {
        this.internal = this.sampleInventory();
    }

    /**
     * Opens the interface for the player
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param p 
     */
    public void open(Player p) {
        this.internal.openInterface(p);
    }

    /**
     * Creates an example of an {@link InventoryInterface}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The example {@link InventoryInterface}
     */
    private InventoryInterface sampleInventory() {
        InventoryInterface myInterface = new InventoryInterface();
        //Add panels, or inventory screens
        //Makes an inventory screen with 1 row of 9 slots, with the title "Buy a diamond!"
        InventoryPanel sellDiamond = myInterface.newPanel("Buy a diamond!", 1);
        //creates an icon in this panel's inventory and defines behavior
        sellDiamond.newIcon(new ItemStack(Material.DIAMOND), (player, ii, icon) -> {
            /*
                Types:
                player -> org.bukkit.entity.Player
                ii -> com.codelanx.codelanxlib.inventory.InventoryInterface
                icon -> com.codelanx.codelanxlib.inventory.MenuIcon
            */
            //take money using the "player" variable
            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
            player.closeInventory();
        });
        return myInterface;
    }

}
