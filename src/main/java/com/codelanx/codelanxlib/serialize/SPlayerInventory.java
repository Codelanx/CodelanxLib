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
package com.codelanx.codelanxlib.serialize;

import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a {@link PlayerInventory} that is capable of being serialized
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
@SerializableAs("PlayerInventory")
public class SPlayerInventory implements ConfigurationSerializable {

    /** The {@link ItemStack} stored on the player's head */
    protected final ItemStack helmet;
    /** The {@link ItemStack} stored on the player's torso */
    protected final ItemStack chest;
    /** The {@link ItemStack} stored on the player's legs */
    protected final ItemStack legs;
    /** The {@link ItemStack} stored on the player's feet */
    protected final ItemStack boots;
    /** The general contents of the player's inventory */
    protected final SInventory inv;

    /**
     * Copies the contents of the passed {@link PlayerInventory}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param inv The {@link PlayerInventory} to copy
     */
    public SPlayerInventory(PlayerInventory inv) {
        Validate.notNull(inv, "PlayerInventory cannot be null");
        this.helmet = inv.getHelmet();
        this.chest = inv.getChestplate();
        this.legs = inv.getLeggings();
        this.boots = inv.getBoots();
        this.inv = new SInventory(inv.getContents());
    }

    /**
     * {@link ConfigurationSerializable} constructor. Should not be used by
     * anything other than Bukkit.
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     */
    public SPlayerInventory(Map<String, Object> config) {
        if (config.isEmpty()) {
            this.helmet = this.chest = this.legs = this.boots = null;
            this.inv = null;
        } else {
            this.helmet = (ItemStack) config.get("helmet");
            this.chest = (ItemStack) config.get("chest");
            this.legs = (ItemStack) config.get("legs");
            this.boots = (ItemStack) config.get("boots");
            this.inv = (SInventory) config.get("contents");
        }
    }

   /**
     * Returns the item in the helmet slot of the {@link PlayerInventory}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The helmet in the {@link PlayerInventory}
     */
    public ItemStack getHelmet() {
        return this.helmet;
    }

    /**
     * Returns the item in the torso slot of the {@link PlayerInventory}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The torso in the {@link PlayerInventory}
     */
    public ItemStack getChestplate() {
        return this.chest;
    }

    /**
     * Returns the item in the pants slot of the {@link PlayerInventory}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The pants in the {@link PlayerInventory}
     */
    public ItemStack getLeggings() {
        return this.legs;
    }

    /**
     * Returns the item in the boots slot of the {@link PlayerInventory}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The boots in the {@link PlayerInventory}
     */
    public ItemStack getBoots() {
        return this.boots;
    }

    /**
     * Returns the underlying {@link SInventory} object that stores normal
     * inventory items
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The underlying {@link SInventory} object
     */
    public SInventory getInventory() {
        return this.inv;
    }

    /**
     * Sets the current contents of this class into a passed
     * {@link PlayerInventory}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param inv The {@link PlayerInventory} to store into
     */
    public void set(PlayerInventory inv) {
        inv.setContents(this.getInventory().getContentsAsArray());
        inv.setHelmet(this.getHelmet());
        inv.setChestplate(this.getChestplate());
        inv.setLeggings(this.getLeggings());
        inv.setBoots(this.getBoots());
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return {@inheritDoc}
     */
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

    /**
     * Creates a new {@link SPlayerInventory} object and returns it. Should only
     * be used by Bukkit
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     * @return A new {@link SPlayerInventory} object
     */
    public static SPlayerInventory valueOf(Map<String, Object> config) {
        return new SPlayerInventory(config);
    }

    /**
     * Creates a new {@link SPlayerInventory} object and returns it. Should only
     * be used by Bukkit
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     * @return A new {@link SPlayerInventory} object
     */
    public static SPlayerInventory deserialize(Map<String, Object> config) {
        return new SPlayerInventory(config);
    }

}
