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
package com.codelanx.codelanxlib.util;

import com.codelanx.codelanxlib.util.auth.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

/**
 * Represents utility functions to simplify or clarify common operations
 * with Bukkit's {@link Player} object
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Players {

    private Players() {
    }

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
        int frange = range * range;
        origin.getWorld().getPlayers().forEach((p) -> {
                    double d = p.getLocation().distanceSquared(origin);
                    if (d <= frange) {
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
        Map<Player, Double> back = Players.getPlayersInRange(range, origin.getLocation());
        back.remove(origin);
        return back;
    }

    /**
     * Returns the closest {@link Player} adjacent to another {@link Player}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param p The {@link Player} at the origin to search around
     * @return The closest {@link Player}, or {@code null} if no one else is in
     *         the world
     */
    public static Player getClosestPlayer(Player p) {
        Location loc = p.getLocation();
        return p.getWorld().getPlayers().stream()
                .filter((o) -> !p.equals(o))
                .min((p1, p2) -> {
                    return Double.compare(p1.getLocation().distanceSquared(loc), p2.getLocation().distanceSquared(loc));
                })
                .orElse(null);
    }

    /**
     * Returns the closest {@link Player} to a specific {@link Location}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param loc The {@link Location} representing the origin to search from
     * @return The closest {@link Player}, or {@code null} if no one is in the
     *         world
     */
    public static Player getClosestPlayer(Location loc) {
        return loc.getWorld().getPlayers().stream().min((o1, o2) -> {
            return Double.compare(o1.getLocation().distanceSquared(loc), o2.getLocation().distanceSquared(loc));
        }).orElse(null);
    }

    /**
     * Determines whether or not a location is harmful if a player was to be
     * located there in the current instant of time (such as a teleport)
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param in The {@link Location} to check
     * @return {@code true} if the location is safe
     */
    public static boolean isSafeLocation(final Location in) {
        Location l = in.clone();
        boolean hole = false;
        BiPredicate<Integer, Material> fallDmg = (i, m) -> i > 3 && m.isBlock();
        int count = 0;
        while (l.getBlockY() > 0) {
            l.add(0, -1, 0);
            count++;
            Material type = l.getBlock().getType();
            if (fallDmg.test(count, type)) {
                return false;
            }
            if (Blocks.isHarmful(type)) {
                return false;
            }
            if (type != Material.AIR && (type.isBlock() || type == Material.WATER || type == Material.STATIONARY_WATER)) {
                break;
            }
        }
        l = in.clone();
        for (int i = 0; i < 2; i++) {
            Material type = l.getBlock().getType();
            if (Blocks.isHarmful(type) || type.isBlock() || Blocks.isLiquid(type)) {
                return false;
            }
            l.add(0, 1, 0);
        }
        while (l.getBlockY() < 255) {
            Material type = l.getBlock().getType();
            if (Blocks.isDangerousFromAbove(type)) {
                return false;
            } else if (type.isBlock()) {
                break;
            }
            l.add(0, 1, 0);
        }
        return true;
    }

    /**
     * Gets the most correct UUID for the {@link Player} in the least expensive
     * way possible. Note however, if there is no UUID information about the
     * player on the server (e.g., they never played before), it will send a
     * blocking web request to Mojang's servers
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param name The name of the {@link Player}
     * @return The {@link UUID} for that player
     */
    public static UUID getUUID(String name) {
        if (Bukkit.getServer().getOnlineMode()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            if (op.hasPlayedBefore() || op.isOnline()) {
                return op.getUniqueId();
            }
        }
        try {
            return UUIDFetcher.getUUIDOf(name);
        } catch (IOException | ParseException | InterruptedException ex) {
            throw new IllegalArgumentException("Cannot determine UUID of player '" + name + "'", ex);
        }
    }

    /**
     * Returns whether or not a player by the specified {@code name} parameter
     * has played on this server before, or is currently online, thus resulting
     * in having a correct {@link UUID}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param name The player name to look for
     * @return {@code true} if the UUID will be correct 
     */
    public static boolean hasCorrectOfflineUUID(String name) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        return op.hasPlayedBefore() || op.isOnline();
    }

}
