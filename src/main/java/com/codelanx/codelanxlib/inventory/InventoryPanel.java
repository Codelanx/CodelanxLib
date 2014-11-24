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
package com.codelanx.codelanxlib.inventory;

import com.codelanx.codelanxlib.config.ConfigurationLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A single inventory that represents a menu
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public final class InventoryPanel {

    static final int SEED_LENGTH = 6;
    private final Inventory inv;
    private final String seed;
    private final String name;
    private final InventoryInterface ii;
    private final Map<Integer, MenuIcon> locations = new HashMap<>();

    InventoryPanel(InventoryInterface ii, String name, int rows) {
        if (ii == null) {
            throw new IllegalArgumentException("InventoryInterface cannot be null!");
        }
        if (name == null) {
            name = "Choose an option!";
        }
        this.ii = ii;
        this.name = name;
        this.seed = this.ii.generateSeed(InventoryPanel.SEED_LENGTH);
        this.inv = Bukkit.getServer().createInventory(null, rows * 9, this.name + this.seed + this.ii.getSeed());
    }

    public MenuIcon newIcon(ItemStack item, Execution onExec, Map<String, Object> options) {
        MenuIcon icon = new MenuIcon(item, onExec, options);
        this.placeIcon(icon);
        return icon;
    }

    protected final void placeIcon(MenuIcon icon) {
        int loc = this.inv.firstEmpty();
        if (loc < 0) {
            //full inv
        }
        this.inv.setItem(loc, icon.getItem());
        this.locations.put(loc, icon);
    }

    public void linkIcon(MenuIcon icon) {
        this.ii.linkPanel(icon, this);
    }

    public void setAllExecutions(Execution onExec) {
        this.locations.values().forEach(i -> i.setExecutable(onExec));
    }

    public Inventory getMenu() {
        return this.inv;
    }

    public String getSeed() {
        return this.seed;
    }

    public void click(Player p, int slot) {
        MenuIcon icon = this.locations.get(slot);
        if (icon != null) {
            if (this.ii.isLinked(icon)) {
                // move link
            } else {
                icon.execute(p, this.ii);
            }
        }
    }

    static InventoryPanel valueOf(InventoryInterface ii, Object o) {
        Map<String, Object> map = ConfigurationLoader.getConfigSectionValue(o);
        if (map == null || map.isEmpty()) {
            return null;
        }
        List<Object> objs = (List<Object>) map.get("icons");
        Boolean root = Boolean.valueOf(String.valueOf(map.get("root")));
        String name = String.valueOf(map.get("name"));
        if (objs != null && root != null) {
            List<MenuIcon> icons = objs.stream().map(MenuIcon::valueOf).collect(Collectors.toList());
            int rows;
            if (map.get("rows") == null) {
                rows = rows = (icons.size() / 9) + 1;
            } else {
                rows = Integer.valueOf(String.valueOf(map.get("rows")));
            }
            InventoryPanel ip = new InventoryPanel(ii, name, rows);
            icons.forEach(ip::placeIcon);
            if (root) {
                ii.setRootPanel(ip);
            }
            return ip;
        } else {
            return null;
        }
    }

    Map<String, Object> toMap() {
        Map<String, Object> back = new HashMap<>();
        List<MenuIcon> icons = new ArrayList<>(this.locations.values());
        back.put("icons", icons.stream().map(MenuIcon::toMap).collect(Collectors.toList()));
        back.put("root", this.ii.isRoot(this));
        back.put("name", this.name);
        back.put("rows", this.inv.getSize() / 9);
        return back;
    }

}
