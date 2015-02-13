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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Allows for the full creation of an {@link ItemStack} within a static context.
 * All color codes ('&') are automatically converted
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class ItemStackBuilder {

    protected static final String lorePrefix = ChatColor.RESET + "" + ChatColor.BLUE + "" + ChatColor.BOLD;
    protected String name;
    protected List<String> lore = new ArrayList<>();
    protected Map<Enchantment, Integer> enchantments = new HashMap<>();
    protected Material type;
    protected int amount;

    /**
     * Sets the display name for the {@link ItemStack}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param name The display name
     * @return This instance (chained)
     */
    public ItemStackBuilder setName(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    /**
     * Adds lore to the {@link ItemStack}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param lore The lore to add
     * @return This instance (chained)
     */
    public ItemStackBuilder addLore(String lore) {
        this.lore.add(lorePrefix + ChatColor.translateAlternateColorCodes('&', lore));
        return this;
    }

    /**
     * Adds an enchantment to the {@link ItemStack}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param ench The {@link Enchantment} to add
     * @param level The level of the enchantment
     * @return This instance (chained)
     */
    public ItemStackBuilder addEnchantment(Enchantment ench, int level) {
        this.enchantments.put(ench, level);
        return this;
    }

    /**
     * Sets the {@link Material} type for the {@link ItemStack}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param type The {@link Material} type to set
     * @return This instance (chained)
     */
    public ItemStackBuilder setType(Material type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the amount of items in the {@link ItemStack}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param amount The amount to set
     * @return This instance (chained)
     */
    public ItemStackBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Builds the {@link ItemStack} object and returns it
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The new {@link ItemStack}
     * @throws IllegalArgumentException If type is {@code null},
     *                                  {@code amount < 0}, or
     *                                  {@code amount > }
     *                                  {@link Material#getMaxStackSize}
     */
    public ItemStack build() {
        if (this.type == null || this.amount < 0 || this.amount > this.type.getMaxStackSize()) {
            throw new IllegalArgumentException("Illegal fields in " + this.getClass().getSimpleName() + "!");
        }
        ItemStack back = new ItemStack(this.type);
        back.setAmount(this.amount);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(this.type);
        meta.setDisplayName(this.name);
        meta.setLore(this.lore);
        back.setItemMeta(meta);
        back.addEnchantments(this.enchantments);
        return back;
    }
    
}
