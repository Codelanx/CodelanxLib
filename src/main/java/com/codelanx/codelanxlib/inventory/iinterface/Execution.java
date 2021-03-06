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
package com.codelanx.codelanxlib.inventory.iinterface;

import org.bukkit.entity.Player;

/**
 * Represents the actions to be taken upon clicking a {@link MenuIcon}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
@FunctionalInterface
public interface Execution {

    /**
     * Called when a {@link MenuIcon} is clicked
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param p The {@link Player} who clicked the icon
     * @param ii The {@link InventoryInterface} this icon belongs to
     * @param icon The {@link MenuIcon} that was clicked
     */
    public void onExec(Player p, InventoryInterface ii, MenuIcon icon);

}
