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
 * areas, but rather indescriminate points. Large-area protection will be added
 * later
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

    public static void protect(Location loc) {
        if (!Protections.listenerRegistered) {
            Bukkit.getServer().getPluginManager().registerEvents(new ProtectionListener(), JavaPlugin.getPlugin(CodelanxLib.class));
            Protections.listenerRegistered = true;
        }
        Protections.protect.add(loc);
    }

    public static void unprotect(Location loc) {
        Protections.protect.remove(loc);
    }

    public static boolean isProtected(Location loc) {
        return Protections.protect.contains(loc);
    }

    /**
     * Provides total protection for blocks cached in {@link Protections}
     * 
     * @since 0.1.0
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

        @EventHandler
        public void onPistonExtend(BlockPistonExtendEvent event) {
            if (!this.handlePiston(event.getBlock(), event.getDirection(), 12)) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPistonRetract(BlockPistonRetractEvent event) {
            if (event.isSticky()) {
                if (!this.handlePiston(event.getBlock(), event.getDirection(), 1)) {
                    event.setCancelled(true);
                }
            }
        }

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

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    event.getPlayer(), event.isCancelled()));
        }

        @EventHandler
        public void onBurn(BlockBurnEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    null, event.isCancelled()));
        }

        @EventHandler
        public void onEntityBlock(EntityChangeBlockEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    null, event.isCancelled()));
        }

        @EventHandler
        public void onPlace(BlockPlaceEvent event) {
            event.setCancelled(this.doCancel(event.getBlock().getLocation(),
                    event.getPlayer(), event.isCancelled()));
        }

        @EventHandler
        public void onExplode(EntityExplodeEvent event) {
            event.blockList().removeIf(b -> Protections.isProtected(b.getLocation()));
        }

    }

}
