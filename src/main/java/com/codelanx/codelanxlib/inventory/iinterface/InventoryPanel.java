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
package com.codelanx.codelanxlib.inventory.iinterface;

import com.codelanx.codelanxlib.config.Config;
import com.codelanx.codelanxlib.util.Lambdas;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A single inventory that represents a menu
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class InventoryPanel {

    static final int SEED_LENGTH = 4;
    private int index = 0;
    private final String seed;
    private final String name;
    private String serializer;
    private final InventoryInterface ii;
    private final int rows;
    private final List<MenuIcon> icons = new ArrayList<>();
    private final Map<Integer, MenuIcon> locations = new HashMap<>();

    InventoryPanel(InventoryInterface ii, String name, int rows) {
        Validate.notNull(ii, "InventoryInterface cannot be null");
        if (name == null) {
            name = "Choose an option!";
        }
        this.ii = ii;
        this.name = name;
        this.seed = this.ii.generateSeed(InventoryPanel.SEED_LENGTH);
        this.rows = rows;
    }

    /**
     * Returns a new {@link MenuIcon} stored in this panel
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param item The {@link ItemStack} to use for displaying the icon
     * @param onExec The {@link Execution} to use when the icon is clicked
     * @param options A mapping of metadata options for the icon
     * @return The new {@link MenuIcon}
     */
    public MenuIcon newIcon(ItemStack item, Execution onExec, Map<String, Object> options) {
        MenuIcon icon = new MenuIcon(item, onExec, options);
        this.locations.put(this.index++, icon);
        return icon;
    }

    /**
     * Returns a new {@link MenuIcon} stored in this panel
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param item The {@link ItemStack} to use for displaying the icon
     * @param onExec The {@link Execution} to use when the icon is clicked
     * @return The new {@link MenuIcon}
     */
    public MenuIcon newIcon(ItemStack item, Execution onExec) {
        return this.newIcon(item, onExec, new HashMap<>());
    }

    /**
     * Links this panel to the passed {@link MenuIcon} such that clicking it
     * will open this panel
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @see InventoryInterface#linkPanel(MenuIcon, InventoryPanel)
     * @param icon The {@link MenuIcon} to link
     */
    public void linkIcon(MenuIcon icon) {
        this.ii.linkPanel(icon, this);
    }

    /**
     * Sets the {@link Execution} for all {@link MenuIcon} objects held in this
     * panel
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param onExec The {@link Execution} to set
     */
    public void setAllExecutions(Execution onExec) {
        this.locations.values().forEach(i -> i.setExecutable(onExec));
    }

    /**
     * Returns the seed relevant to this specific panel
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The seed for this panel
     */
    public String getSeed() {
        return this.seed;
    }

    /**
     * Called when a player clicks a {@link MenuIcon} inside of an
     * {@link InventoryPanel}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} who clicked
     * @param slot The slot that was clicked
     */
    public void click(Player p, int slot) {
        MenuIcon icon = this.locations.get(slot);
        if (icon != null && icon.hasPermission(p)) {
            if (this.ii.isLinked(icon)) {
                InventoryPanel next = this.ii.getLinkedPanel(icon);
                if (next != null) {
                    next.open(p);
                }
            } else {
                icon.execute(p, this.ii);
                p.closeInventory();
            }
        }
    }

    static InventoryPanel valueOf(InventoryInterface ii, Object o) {
        Map<String, Object> map = Config.getConfigSectionValue(o);
        if (map == null || map.isEmpty()) {
            return null;
        }
        List<Object> objs = (List<Object>) map.get("icons");
        Boolean root = Boolean.valueOf(String.valueOf(map.get("root")));
        String name = String.valueOf(map.get("name"));
        if (objs != null && root != null) {
            List<MenuIcon> icons = objs.stream()
                    .map(obj -> MenuIcon.valueOf(ii, obj))
                    .filter(Lambdas::notNull)
                    .collect(Collectors.toList());
            int rows;
            if (map.get("rows") == null) {
                rows = (icons.size() / 9) + 1;
            } else {
                rows = Integer.valueOf(String.valueOf(map.get("rows")));
            }
            InventoryPanel ip = ii.newPanel(name, rows);
            ip.icons.addAll(icons);
            ip.icons.forEach(i -> ip.locations.put(ip.index++, i));
            if (root) {
                ii.setRootPanel(ip);
            }
            return ip;
        } else {
            return null;
        }
    }

    /**
     * Opens this {@link InventoryPanel} for the passed {@link Player}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} to open this panel for
     */
    public void open(Player p) {
        String name = this.name;
        int maxLength = 32 - (InventoryInterface.SEED_LENGTH * 2) - (InventoryPanel.SEED_LENGTH * 2);
        if (name.length() > maxLength) {
            name = name.substring(0, maxLength);
        }
        Inventory back = Bukkit.getServer().createInventory(null, this.rows * 9, name + this.seed + this.ii.getSeed());
        this.locations.entrySet().stream().filter(ent -> ent.getValue().hasPermission(p)).forEach(ent -> {
            back.setItem(ent.getKey(), ent.getValue().getItem());
        });
        p.openInventory(back);
    }

    /**
     * Sets the serialized name for this panel
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param name The name to set
     * @return The current instance (chained)
     */
    public InventoryPanel setSerializedName(String name) {
        this.serializer = name;
        return this;
    }

    /**
     * Returns the serialized name for this panel
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The serialized name
     */
    public String getSerializedName() {
        return this.serializer;
    }

    Map<String, Object> toMap() {
        Map<String, Object> back = new HashMap<>();
        back.put("icons", this.icons.stream().map(i -> i.toMap(this.ii)).collect(Collectors.toList()));
        back.put("root", this.ii.isRoot(this));
        back.put("name", this.name);
        back.put("rows", this.rows);
        return back;
    }

    /**
     * Returns the name/title used for the {@link Inventory} in this panel
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The name for this panel 
     */
    public String getName() {
        return this.name;
    }

}
