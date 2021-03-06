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
package com.codelanx.codelanxlib.permission;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

/**
 * Represents a single permission value for use with checking whether or not a
 * {@link org.bukkit.permissions.Permissible Permissible} has a specific
 * permission or not. Meant for use on enum types
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public interface Permissions {

    /**
     * Returns the base permission node for the plugin. e.g. "commons".
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
    public String getNode();

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
        return this.getBase().toLowerCase() + median + this.getNode().toLowerCase();
    }

    /**
     * Determines whether or not the {@link Permissible} has this permission
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param p The {@link Permissible} to check permissions for
     * @return {@code true} if the {@link Permissible} has the permissions
     */
    default public boolean has(Permissible p) {
        Validate.notNull(p, "Player cannot be null");
        //Register permission to conform to Bukkit API
        String fullPerm = this.build();
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (pm.getPermission(fullPerm) == null) {
            pm.addPermission(new Permission(fullPerm));
        }
        //end register
        return p.hasPermission(fullPerm);
    }

}
