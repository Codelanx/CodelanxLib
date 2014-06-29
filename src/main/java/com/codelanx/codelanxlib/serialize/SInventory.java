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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

/**
 * Class description for {@link SInventory}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
@SerializableAs("Inventory")
public class SInventory implements ConfigurationSerializable {

    protected final List<ItemStack> items = new ArrayList<>();
    
    public SInventory(ItemStack... contents) {
        this.items.addAll(Arrays.asList(contents));
        this.items.removeIf((i) -> {return i == null;});
    }

    public SInventory(Map<String, Object> map) {
        this.items.addAll((List<ItemStack>) map.get("items"));
    }

    public List<ItemStack> getContents() {
        return this.items;
    }

    public ItemStack[] getContentsAsArray() {
        return this.items.toArray(new ItemStack[this.items.size()]);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new HashMap<>();
        back.put("items", this.items);
        return back;
    }

    public static SInventory valueOf(Map<String, Object> map) {
        return new SInventory(map);
    }

    public static SInventory deserialize(Map<String, Object> map) {
        return new SInventory(map);
    }

}
