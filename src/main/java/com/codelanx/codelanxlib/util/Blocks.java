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
package com.codelanx.codelanxlib.util;

import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 * Utility class for working with any {@link org.bukkit.block.Block Block}
 * objects and their derivatives
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Blocks {

    private Blocks() {
    }

    /**
     * Forcibly sends an update of sign text to any players within a close
     * enough range of the sign to render its text
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param s The {@link Sign} to update
     */
    public static void updatePlayersInRange(Sign s) {
        //Seems to be that entities within ~60 block radius are not sent/updated
        Players.getPlayersInRange(65, s.getLocation()).keySet().forEach(p -> {
            p.sendSignChange(s.getLocation(), s.getLines());
        });
    }

    /**
     * Returns whether or not a {@link Material} will harm a
     * {@link org.bukkit.entity.Player Player} who comes into contact with it
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param mat The {@link Material} to compare
     * @return {@code true} if the material will harm the player
     */
    public static boolean isHarmful(Material mat) {
        switch(mat) {
            case LAVA:
            case CACTUS:
            case FIRE:
            case STATIONARY_LAVA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determines if a material will be liquid when placed
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param mat The {@link Material} to check
     * @return {@code true} if lava or water (non-contained of any kind)
     */
    public static boolean isLiquid(Material mat) {
        switch(mat) {
            case LAVA:
            case STATIONARY_LAVA:
            case WATER:
            case STATIONARY_WATER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determines if a block would be dangerous if above a player's head with
     * nothing but air inbetween, assuming no movement on part of the player
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param mat The {@link Material} to check
     * @return {@code true} if a player could be harmed
     */
    public static boolean isDangerousFromAbove(Material mat) {
        switch(mat) {
            case LAVA:
            case STATIONARY_LAVA:
            case SAND:
            case GRAVEL:
            case ANVIL:
                return true;
            default:
                return false;
        }
    }

}
