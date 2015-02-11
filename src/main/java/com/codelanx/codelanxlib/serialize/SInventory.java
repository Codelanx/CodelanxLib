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
package com.codelanx.codelanxlib.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an Inventory which is ready to be serialized
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
@SerializableAs("Inventory")
public class SInventory implements ConfigurationSerializable {

    /**
     * Represents a collection of the relevant inventory items
     * 
     * @since 0.0.1
     * @version 0.0.1
     */
    protected final List<ItemStack> items = new ArrayList<>();

    /**
     * Copies the passed {@link ItemStack} objects
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param contents The {@link ItemStack}s to copy
     */
    public SInventory(ItemStack... contents) {
        this.items.addAll(Arrays.asList(contents).stream()
                .map(i -> i.clone()).collect(Collectors.toList()));
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
    @SuppressWarnings("unchecked")
    public SInventory(Map<String, Object> config) {
        this.items.addAll((Collection<? extends ItemStack>) config.get("items"));
    }

    /**
     * Returns a copy of this instance's stored {@link ItemStack ItemStacks}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return A copy of the relevant {@link ItemStack ItemStacks}
     */
    public List<ItemStack> getContents() {
        return Collections.unmodifiableList(this.items.stream().map(i -> i.clone())
                .collect(Collectors.toList()));
    }

    /**
     * Returns a copy of this instance's stored {@link ItemStack ItemStacks}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return A copy of the relevant {@link ItemStack ItemStacks}
     */
    public ItemStack[] getContentsAsArray() {
        return this.getContents().toArray(new ItemStack[this.items.size()]);
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
        back.put("items", this.items);
        return back;
    }

    /**
     * Creates a new {@link SInventory} object and returns it. Should only be
     * used by Bukkit
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     * @return A new {@link SInventory} object
     */
    public static SInventory valueOf(Map<String, Object> config) {
        return new SInventory(config);
    }

    /**
     * Creates a new {@link SInventory} object and returns it. Should only be
     * used by Bukkit
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     * @return A new {@link SInventory} object
     */
    public static SInventory deserialize(Map<String, Object> config) {
        return new SInventory(config);
    }

}
