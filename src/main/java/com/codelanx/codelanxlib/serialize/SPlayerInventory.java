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
package com.codelanx.codelanxlib.serialize;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Class description for {@link SPlayerInventory}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
@SerializableAs("PlayerInventory")
public class SPlayerInventory implements ConfigurationSerializable {

    protected final ItemStack helmet;
    protected final ItemStack chest;
    protected final ItemStack legs;
    protected final ItemStack boots;
    protected final SInventory inv;

    public SPlayerInventory(PlayerInventory inv) {
        if (inv == null) {
            this.helmet = this.chest = this.legs = this.boots = null;
            this.inv = null;
        } else {
            this.helmet = inv.getHelmet();
            this.chest = inv.getChestplate();
            this.legs = inv.getLeggings();
            this.boots = inv.getBoots();
            this.inv = new SInventory(inv.getContents());
        }
    }

    public SPlayerInventory(Map<String, Object> map) {
        if (map.isEmpty()) {
            this.helmet = this.chest = this.legs = this.boots = null;
            this.inv = null;
        } else {
            this.helmet = (ItemStack) map.get("helmet");
            this.chest = (ItemStack) map.get("chest");
            this.legs = (ItemStack) map.get("legs");
            this.boots = (ItemStack) map.get("boots");
            this.inv = (SInventory) map.get("contents");
        }
    }

    public ItemStack getHelmet() {
        return this.helmet;
    }

    public ItemStack getChestPlate() {
        return this.chest;
    }

    public ItemStack getLeggings() {
        return this.legs;
    }

    public ItemStack getBoots() {
        return this.boots;
    }

    public SInventory getInventory() {
        return this.inv;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new HashMap<>();
        back.put("helmet", this.helmet);
        back.put("chest", this.chest);
        back.put("legs", this.legs);
        back.put("boots", this.boots);
        back.put("contents", this.inv);
        return back;
    }

    public static SPlayerInventory valueOf(Map<String, Object> map) {
        return new SPlayerInventory(map);
    }

    public static SPlayerInventory deserialize(Map<String, Object> map) {
        return new SPlayerInventory(map);
    }

}
