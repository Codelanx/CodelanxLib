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

import org.bukkit.potion.PotionEffectType;

/**
 * Represents utility methods for either dealing with or fixing issues with
 * minecraft's internal server implementation
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class NMS {

    private NMS() {
        
    }

    /**
     * This method returns a duration that is constant-time based upon the given
     * {@link PotionEffectType}. Due to NMS internals with "MobEffectType",
     * some effects are abstractly divided by 2, to which this method undoes
     * that division for the relevant effects. Note this is a hardcoded method
     * which is up-to-date for Minecraft 1.8.*
     * 
     * @param type The {@link PotionEffectType} to check
     * @param duration The duration for the effect
     * @return The same duration provided, multiplied by 2 for "flagged" effects
     */
    public int fixPotionEffectDuration(PotionEffectType type, int duration) {
        if (type == PotionEffectType.SLOW
                || type == PotionEffectType.SLOW_DIGGING
                || type == PotionEffectType.HARM
                || type == PotionEffectType.CONFUSION
                || type == PotionEffectType.BLINDNESS
                || type == PotionEffectType.HUNGER
                || type == PotionEffectType.WEAKNESS
                || type == PotionEffectType.POISON
                || type == PotionEffectType.WITHER) {
            return duration << 1;
        }
        return duration;
    }

}
