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
 * Class description for {@link ItemStackBuilder}
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

    public ItemStackBuilder setName(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    public ItemStackBuilder addLore(String lore) {
        this.lore.add(lorePrefix + ChatColor.translateAlternateColorCodes('&', lore));
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment ench, int level) {
        this.enchantments.put(ench, level);
        return this;
    }

    public ItemStackBuilder setType(Material type) {
        this.type = type;
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

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
