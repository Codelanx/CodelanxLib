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
package com.codelanx.codelanxlib.permission;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

/**
 * Represents an enum that 
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 * 
 * @param <E> The permission enum type
 */
public interface Permissions<E extends Enum<E> & Permissions<E>> {

    /**
     * Returns the base permission node for the plugin. e.g. "codelanxlib".
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The base permission node to prefix all permissions
     */
    public String getBase();

    /**
     * Returns the raw node for the specified enum constant without prefixing
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The permission suffix supplied by the enum
     */
    public String getPermission();

    /**
     * Builds the full permission string
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The full permission string
     */
    default public String build() {
        String median = ".";
        if (this.getBase().endsWith(median)) {
            median = "";
        }
        return this.getBase().toLowerCase() + median + this.getPermission().toLowerCase();
    }

    /**
     * Determines whether or not the player has this permission
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param p The {@link Player} to check permissions for
     * @return {@code true} if the player has the permissions
     */
    default public boolean has(Player p) {
        Validate.notNull(p, "Player cannot be null!");
        //Register permission to conform to bukkit API
        String fullPerm = this.build();
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (pm.getPermission(fullPerm) == null) {
            pm.addPermission(new Permission(fullPerm));
        }
        //end register
        return p.hasPermission(fullPerm);
    }

}
