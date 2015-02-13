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

import com.codelanx.codelanxlib.CodelanxLib;
import com.codelanx.codelanxlib.internal.CLPerms;
import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Adds protection to specific locations. This should not be used for large
 * areas, but rather indiscriminate points. Large-area protection will be added
 * later. Note that this will not protect from plugins dynamically modifying
 * the block themselves, such as WorldEdit
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class Protections {

    private static final Set<Location> protect = new LinkedHashSet<>();
    private static boolean listenerRegistered = false;

    private Protections() {

    }

    /**
     * Protects a single {@link Location} from being altered
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param loc The {@link Location} to protect
     */
    public static void protect(Location loc) {
        if (!Protections.listenerRegistered) {
            Bukkit.getServer().getPluginManager().registerEvents(new ProtectionListener(), JavaPlugin.getPlugin(CodelanxLib.class));
            Protections.listenerRegistered = true;
        }
        Protections.protect.add(loc);
    }

    /**
     * Removes protection from a single {@link Location}. Does nothing if the
     * location was not protected in the first place.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param loc The {@link Location} to unprotect
     */
    public static void unprotect(Location loc) {
        Protections.protect.remove(loc);
    }

    /**
     * Returns {@code true} if the passed {@link Location} is protected
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param loc The {@link Location} to check
     * @return {@code true} if protected
     */
    public static boolean isProtected(Location loc) {
        return Protections.protect.contains(loc);
    }

    /**
     * Provides total protection for blocks cached in {@link Protections}
     * 
     * @since 0.1.0
     * @author 1Rogue
     * @version 0.1.0
     */
    public static class ProtectionListener implements Listener {

        private boolean doCancel(Location loc, Player p, boolean cancelled) {
            if (Protections.isProtected(loc)) {
                if (p == null) {
                    return true;
                }
                return !CLPerms.PROTECTION_OVERRIDE.has(p) ? true : cancelled;
            }
            return cancelled;
        }

        /**
         * Prevents pistons from pushing protected blocks
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param event The relevant {@link BlockPistonExtendEvent}
         */
        @EventHandler
        public void onPistonExtend(BlockPistonExtendEvent event) {
            if (!this.handlePiston(event.getBlock(), event.getDirection(), 12)) {
                event.setCancelled(true);
            }
        }

        /**
         * Prevents sticky pistons from pulling protected blocks
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param event The relevant {@link BlockPistonRetractEvent}
         */
        @EventHandler
        public void onPistonRetract(BlockPistonRetractEvent event) {
            if (event.isSticky()) {
                if (!this.handlePiston(event.getBlock(), event.getDirection(), 1)) {
                    event.setCancelled(true);
                }
            }
        }

        /**
         * Determines if a protected block will be affected by a piston
         * movement in a specific direction
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param b The starting piston block
         * @param dir The {@link BlockFace} direction to search in
         * @param amount The amount of blocks to check
         * @return {@code true} if no protected blocks will be affected
         */
        private boolean handlePiston(Block b, BlockFace dir, int amount) {
            for (int i = amount; i > 0; i--) {
                b = b.getRelative(dir);
                if (b == null || b.getType() == Material.AIR) {
                    return true;
                }
                if (Protections.isProtected(b.getLocation())) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Prevents protected blocks from being broken
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param event The relevant {@link BlockBreakEvent}
         */
        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    event.getPlayer(), event.isCancelled()));
        }

        /**
         * Prevents blocks from being burned
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param event The relevant {@link BlockBurnEvent}
         */
        @EventHandler
        public void onBurn(BlockBurnEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    null, event.isCancelled()));
        }

        /**
         * Prevents blocks from being turned into entities (e.g. falling sand)
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param event The relevant {@link EntityChangeBlockEvent}
         */
        @EventHandler
        public void onEntityBlock(EntityChangeBlockEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    null, event.isCancelled()));
        }

        /**
         * Prevents people from placing a block in the protected location
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param event The relevant {@link BlockPlaceEvent}
         */
        @EventHandler
        public void onPlace(BlockPlaceEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    event.getPlayer(), event.isCancelled()));
        }

        /**
         * Prevents the block from exploding
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param event The relevant {@link EntityExplodeEvent}
         */
        @EventHandler
        public void onExplode(EntityExplodeEvent event) {
            event.blockList().removeIf(b -> Protections.isProtected(b.getLocation()));
        }

    }

}
