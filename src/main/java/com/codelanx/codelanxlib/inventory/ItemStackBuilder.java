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

import com.codelanx.codelanxlib.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Allows for the full creation of an {@link ItemStack} within a static context.
 * All color codes using '{@code &}' can be automatically converted
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class ItemStackBuilder {

    /** Prefix for lore items because purple is ugly */
    public static final String DEFAULT_PREFIX = ChatColor.RESET + "" + ChatColor.BLUE + "" + ChatColor.BOLD;
    /** A prefix that can be set for all lore */
    protected String lorePrefix = ItemStackBuilder.DEFAULT_PREFIX;
    /** The name for the {@link ItemStack} */
    protected String name;
    /** The lore for the {@link ItemStack} */
    protected List<String> lore = new ArrayList<>();
    /** A mapping of {@link Enchantment Enchantments} to their levels */
    protected Map<Enchantment, Integer> enchantments = new HashMap<>();
    /** The {@link Material type} for the {@link ItemStack} */
    protected Material type;
    /** The amount of items in the {@link ItemStack} */
    protected int amount;
    /** The durability of the {@link ItemStack} */
    protected short durability;
    /** Specifies whether or not to automatically translate color codes */
    protected boolean autoTranslate = true;

    /**
     * Sets the display name for the {@link ItemStack}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param name The display name
     * @return This instance (chained)
     */
    public ItemStackBuilder name(String name) {
        this.name = Lang.color(name);
        return this;
    }

    /**
     * Adds lore to the {@link ItemStack}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param lore The lore to add
     * @return This instance (chained)
     */
    public ItemStackBuilder addLore(String lore) {
        this.lore.add(Lang.color(lore));
        return this;
    }

    /**
     * Sets the prefix to use for all lore items
     * 
     * @since 0.1.0
     * @version 0.l.0
     * 
     * @param prefix The prefix to set
     * @return This instance (chained)
     */
    public ItemStackBuilder lorePrefix(String prefix) {
        this.lorePrefix = prefix;
        return this;
    }

    /**
     * Sets the durability to apply to the {@link ItemStack}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param durability The durability to use (can also be a data value)
     * @return This instance (chained)
     */
    public ItemStackBuilder durability(short durability) {
        this.durability = durability;
        return this;
    }

    /**
     * Sets whether or not to automatically translate color codes
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param autoTranslate {@code true} to automatically translate '{@code &}'
     * @return This instance (chained)
     */
    public ItemStackBuilder autoTranslateColors(boolean autoTranslate) {
        this.autoTranslate = autoTranslate;
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
    public ItemStackBuilder type(Material type) {
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
    public ItemStackBuilder amount(int amount) {
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
     *                                  {@code amount <= 0}, or
     *                                  {@code amount > }
     *                                  {@link Material#getMaxStackSize}
     */
    public ItemStack build() {
        if (this.type == null || this.amount <= 0 || this.amount > this.type.getMaxStackSize()) {
            throw new IllegalArgumentException("Illegal fields in " + this.getClass().getSimpleName());
        }
        String name;
        List<String> lore = new ArrayList<>(this.lore).stream().map(l -> this.lorePrefix + l).collect(Collectors.toList());
        if (this.autoTranslate) {
            name = Lang.color(this.name);
            lore = lore.stream().map(l -> Lang.color(l)).collect(Collectors.toList());
        } else {
            name = this.name;
        }
        ItemStack back = new ItemStack(this.type);
        back.setAmount(this.amount);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(this.type);
        meta.setDisplayName(name);
        meta.setLore(lore);
        back.setItemMeta(meta);
        back.addEnchantments(this.enchantments);
        back.setDurability(this.durability);
        return back;
    }
    
}
