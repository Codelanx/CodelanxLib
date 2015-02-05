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

import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Class description for {@link PlayerInventoryState}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class PlayerInventoryState extends InventoryState {

    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;

    public PlayerInventoryState(PlayerInventory i) {
        super(i);
        this.helmet = i.getHelmet();
        this.chestplate = i.getChestplate();
        this.leggings = i.getLeggings();
        this.boots = i.getBoots();
    }

    public ItemStack getHelmet() {
        return this.helmet;
    }

    public ItemStack getChestplate() {
        return this.chestplate;
    }

    public ItemStack getLeggings() {
        return this.leggings;
    }

    public ItemStack getBoots() {
        return this.boots;
    }

    @Override
    public void setContents(InventoryHolder holder) {
        Validate.isTrue(holder instanceof HumanEntity, "InventoryHolder is not a HumanEntity!");
        super.setContents(holder);
        HumanEntity e = (HumanEntity) holder;
        e.getInventory().setHelmet(this.helmet);
        e.getInventory().setChestplate(this.chestplate);
        e.getInventory().setLeggings(this.leggings);
        e.getInventory().setBoots(this.boots);
    }

}
