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
import com.codelanx.codelanxlib.util.RNG;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an {@link ItemStack} in an {@link InventoryPanel}'s inventory that
 * will execute code when clicked.
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class MenuIcon {

    /** A mapping of metadata options */
    protected Map<String, Object> options;
    /** A seed representing this {@link MenuIcon object} */
    protected final long seed;
    /** The {@link ItemStack} that this instance uses */
    protected final ItemStack item;
    /** The {@link Execution} function to run when clicked */
    protected Execution onExec;
    /** Permissions required to see this icon */
    protected final List<String> perms = new ArrayList<>();

    /**
     * Initializes fields
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param item The {@link ItemStack} to use for display in the inventory
     * @param onExec The {@link Execution} to use when clicked
     * @param options A mapping of metadata options
     */
    MenuIcon(ItemStack item, Execution onExec, Map<String, Object> options) {
        if (item == null || options == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!");
        }
        this.item = item;
        this.seed = RNG.THREAD_LOCAL().nextLong();
        this.onExec = onExec;
        this.options = options;
    }

    /**
     * Returns the {@link ItemStack} that is displayed when a user opens the
     * {@link InventoryInterface}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The {@link ItemStack} to display
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Sets the {@link Executable} function that is called when this icon is
     * clicked. Can be set to {@code null} to not execute anything
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param onExec The {@link Executable} to be used
     */
    public void setExecutable(Execution onExec) {
        this.onExec = onExec;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.seed ^ (this.seed >>> 32));
        hash = 53 * hash + Objects.hashCode(this.item);
        return hash;
    }

    /**
     * Returns a {@link Map} of all the metadata options
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return A {@link Map} of metadata values
     */
    public Map<String, Object> getOptions() {
        return Collections.unmodifiableMap(this.options);
    }

    /**
     * Adds a metadata option
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param key The {@link String} key to retrieve the option
     * @param value The value of the option
     * @return The previous value associated with the key, or {@code null} if
     * there was no previous mapping using the key
     */
    public Object addOption(String key, Object value) {
        return this.options.put(key, value);
    }

    /**
     * Returns a metadata option
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param key The key that maps to the value
     * @return The stored metadata value, or {@code null} if none is set
     */
    public Object getOption(String key) {
        return this.options.get(key);
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MenuIcon)) {
            return false;
        }
        final MenuIcon other = (MenuIcon) obj;
        if (this.seed != other.seed) {
            return false;
        }
        return Objects.equals(this.item, other.item);
    }

    /**
     * Runs the {@link Executable} associated with this icon if one is set
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param p The {@link Player} that clicked the icon
     * @param ii The {@link InventoryInterface} associated with this icon
     */
    void execute(Player p, InventoryInterface ii) {
        if (this.onExec != null) {
            this.onExec.onExec(p, ii, this);
        }
    }

    /**
     * Manually deserializes a YAML mapping and returns the appropriate
     * {@link MenuIcon}. This method is permitted to fail, if values are missing
     * or malformed from the yaml file, it will throw an exception (Most like a
     * {@link NullPointerException}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param ii The {@link InventoryInterface} to serialize for
     * @param o The YAML mapping to deserialize
     * @return A new {@link MenuIcon} instance
     */
    static MenuIcon valueOf(InventoryInterface ii, Object o) {
        Map<String, Object> map = Config.getConfigSectionValue(o);
        if (map == null || map.isEmpty()) {
            return null;
        }
        ItemStack item = (ItemStack) map.get("item");
        Map<String, Object> opts = Config.getConfigSectionValue(map.get("options"));
        List<String> perm = (List<String>) map.get("permissions");
        String link = String.valueOf(map.get("link"));
        if (item != null && opts != null) {
            MenuIcon back = new MenuIcon(item, null, opts);
            if (perm != null && !perm.isEmpty()) {
                back.perms.addAll(perm);
            }
            if (link != null) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        InventoryPanel ip = ii.find(p -> link.equalsIgnoreCase(p.getSerializedName()));
                        if (ip != null) {
                            ip.linkIcon(back);
                        }
                    }
                }, 3000);
            }
            return back;
        } else {
            return null;
        }
    }

    /**
     * Converts the current instance into a {@link Map} for serialization
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param ii The relevant {@link InventoryInterface} for this serialization
     * @return A mapping of this object's values
     */
    Map<String, Object> toMap(InventoryInterface ii) {
        Map<String, Object> back = new HashMap<>();
        back.put("item", this.item);
        back.put("options", this.options);
        if (!this.perms.isEmpty()) {
            back.put("permissions", this.perms);
        }
        if (ii.isLinked(this)) {
            back.put("link", ii.getLinkedPanel(this).getSerializedName());
        }
        return back;
    }

    /**
     * Adds a permission node that a player will need in order to see this icon.
     * If no permissions are set, everyone is capable of seeing the icon. If
     * multiple permissions are set, only one of the permission nodes is needed
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param permission A string representing a permission node
     */
    public void addPermission(String permission) {
        this.perms.add(permission);
    }

    /**
     * Returns {@code true} if any permissions are required to see this icon
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return {@code true} if a permission is needed to see the icon
     */
    public boolean requiresPerms() {
        return !this.perms.isEmpty();
    }

    /**
     * Returns whether or not a {@link Player} has permission to view this icon
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param p The {@link Player} to check
     * @return {@code true} if the player has permission to view the icon
     */
    public boolean hasPermission(Player p) {
        return this.requiresPerms() ? this.perms.stream().anyMatch(p::hasPermission) : true;
    }

}
