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

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Class description for {@link PlayerUtil}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class PlayerUtil {

    /**
     * Gets any players within range of a specific location
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param range The range in which to look for players
     * @param origin The {@link Location} representing the center of the circle
     * @return Any players within the radius range of the origin, mapped to
     *         the distance away they are
     */
    public static Map<Player, Double> getPlayersInRange(int range, Location origin) {
        Map<Player, Double> back = new HashMap<>();
        origin.getWorld().getPlayers().forEach((p) -> {
                    double d = p.getLocation().distanceSquared(origin);
                    if (d <= range) {
                        back.put(p, d);
                    }
                });
        return back;
    }

    /**
     * Gets any players within range of a specific player, exclusive of the
     * player themselves.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param range The range in which to look for players
     * @param origin The {@link Player} representing the center of the circle
     * @return Any players within the radius range of the origin, mapped to
     *         the distance away they are
     */
    public static Map<Player, Double> getPlayersInRange(int range, Player origin) {
        Map<Player, Double> back = new HashMap<>();
        origin.getWorld().getPlayers().stream()
                .filter((p) -> !p.equals(origin))
                .forEach((p) -> {
                    double d = p.getLocation().distanceSquared(origin.getLocation());
                    if (d <= range) {
                        back.put(p, d);
                    }
                });
        return back;
    }

    public static Player getClosestPlayer(Player p) {
        Location loc = p.getLocation();
        return p.getWorld().getPlayers().stream()
                .filter((o) -> !p.equals(o))
                .min((p1, p2) -> {
                    return Double.compare(p1.getLocation().distanceSquared(loc), p2.getLocation().distanceSquared(loc));
                })
                .orElse(null);
    }

    public static Player getClosestPlayer(Location loc) {
        return loc.getWorld().getPlayers().stream().min((o1, o2) -> {
            return Double.compare(o1.getLocation().distanceSquared(loc), o2.getLocation().distanceSquared(loc));
        }).orElse(null);
    }

}
