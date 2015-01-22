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
package com.codelanx.codelanxlib.inventory;

import com.codelanx.codelanxlib.config.Config;
import com.codelanx.codelanxlib.util.Exceptions;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A collection of {@link InventoryPanel} objects with an opening reference
 * 
 * TODO: Find way to persist menu linkings
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 */
public final class InventoryInterface {

    static final int SEED_LENGTH = 3;
    private InventoryPanel root;
    private final String seed;
    private final Map<String, InventoryPanel> panels = new HashMap<>();
    private final Map<MenuIcon, InventoryPanel> links = new HashMap<>();

    /**
     * Generates a new seed and registers itself to an {@link InterfaceListener}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param plugin The relevant {@link Plugin} controlling this interface
     */
    public InventoryInterface(Plugin plugin) {
        //generate seed
        this.seed = this.generateSeed(InventoryInterface.SEED_LENGTH);
        //register listener
        Bukkit.getServer().getPluginManager().registerEvents(new InterfaceListener(this), plugin);
    }

    /**
     * Returns the root {@link InventoryPanel}.
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The root {@link InventoryPanel}
     */
    public InventoryPanel getRootPanel() {
        return this.root;
    }

    /**
     * Opens the {@link InventoryInterface} for a player.
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Player} to open the interface for
     * @throws IllegalStateException if no root panel is set
     */
    public void openInterface(Player p) {
        Exceptions.illegalState(this.getRootPanel() != null, "Root panel cannot be null!");
        this.getRootPanel().open(p);
    }

    /**
     * Creates and returns an {@link InventoryPanel} for use as a menu
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param name The name (title) for the {@link InventoryPanel}
     * @param rows The number of rows for the {@link InventoryPanel}
     * @return The new {@link InventoryPanel} object
     */
    public InventoryPanel createPanel(String name, int rows) {
        InventoryPanel ip = new InventoryPanel(this, name, rows);
        this.panels.put(ip.getSeed(), ip);
        return ip;
    }

    /**
     * Gets a linked {@link InventoryPanel} from a {@link MenuIcon} object. If
     * the panel is not linked via {@link InventoryPanel#linkIcon(MenuIcon)},
     * this method will return {@code null}.
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param icon The {@link MenuIcon} to find a link from
     * @return The linked {@link InventoryPanel}, or {@code null} if there is no
     *         linked panel.
     */
    public InventoryPanel getLinkedPanel(MenuIcon icon) {
        return this.links.get(icon);
    }

    /**
     * Returns all {@link InventoryPanel} objects controlled by this interface
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return Any {@link InventoryPanel} objects in use
     */
    public Collection<? extends InventoryPanel> getPanels() {
        return Collections.unmodifiableCollection(this.panels.values());
    }

    /**
     * Searches for a specific {@link InventoryPanel} used in this interface
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param filter A {@link Predicate}{@code <? super InventoryPanel>} to
     *               search through active panels with.
     * @return The first matching {@link InventoryPanel}, or {@code null} if
     *         nothing matches
     */
    public InventoryPanel find(Predicate<? super InventoryPanel> filter) {
        Optional<InventoryPanel> pan = this.panels.values().stream().filter(filter).findFirst();
        return pan.isPresent() ? pan.get() : null;
    }

    /**
     * Sets the root panel of this {@link InventoryInterface}
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param panel The {@link InventoryPanel} to set
     * @throws IllegalArgumentException if {@code panel} is null
     * @throws IllegalArgumentException if this interface did not make the panel
     */
    public void setRootPanel(InventoryPanel panel) {
        Validate.notNull(panel, "");
        Validate.isTrue(this.panels.containsValue(panel), "Must use a registered panel as root!");
        this.root = panel;
    }

    /**
     * Determines if a {@link MenuIcon} is linked to an {@link InventoryPanel}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param icon The {@link MenuIcon} to check
     * @return {@code true} if the icon is linked to a panel
     */
    public boolean isLinked(MenuIcon icon) {
        return this.links.containsKey(icon);
    }

    /**
     * Returns an {@link InventoryPanel} based on its generated seed
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param title The overall title of the panel (All seeds and the name)
     * @return The relevant {@link InventoryPanel}, or {@code null} if not found
     */
    public InventoryPanel getPanelBySeed(String title) {
        int ii = InventoryInterface.SEED_LENGTH * 2;
        int ip = ii + (InventoryPanel.SEED_LENGTH * 2);
        return this.panels.get(title.substring(title.length() - ip, title.length() - ii));
    }

    /**
     * Determines if a passed {@link InventoryPanel} is the root panel
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param panel The {@link InventoryPanel} to compare
     * @return {@code true} if the passed panel object is the root panel
     */
    protected boolean isRoot(InventoryPanel panel) {
        Validate.notNull(panel, "InventoryPanel cannot be null!");
        if (this.root == null) {
            return false;
        }
        return this.root.getSeed().equals(panel.getSeed());
    }

    /**
     * Links a {@link MenuIcon} to open an {@link InventoryPanel}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param icon
     * @param panel 
     */
    protected void linkPanel(MenuIcon icon, InventoryPanel panel) {
        this.links.put(icon, panel);
    }

    /**
     * Returns the randomized {@link ChatColor} seed made for this interface
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The seed in use by this {@link InventoryInterface}
     */
    protected String getSeed() {
        return this.seed;
    }

    /**
     * Generates a new random {@link ChatColor} seed for a panel or interface.
     * Note the format of the seed for the overall panel is:
     * <br><br>
     * {@code <Custom panel title><Panel seed><Interface seed>}
     * <br><br>
     * As an example: {@code My new Panel&1&2&3&4&5&6}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param length The number of {@link ChatColor} objects to use
     * @return A String containing all relevant objects
     */
    protected final String generateSeed(int length) {
        StringBuilder sb = new StringBuilder();
        ChatColor[] vals = ChatColor.values();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(vals[rand.nextInt(vals.length)]);
        }
        return sb.toString();
    }

    /**
     * Reads an {@link InventoryInterface} from a YAML file
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param p The {@link Plugin} associated with this {@link File}
     * @param f The {@link File} to load from
     * @return The deserialized {@link InventoryInterface}
     * @throws IllegalArgumentException If a null parameter is provided
     */
    public static InventoryInterface deserialize(Plugin p, File f) {
        Validate.notNull(f, "File cannot be null!");
        Validate.notNull(p, "Plugin cannot be null!");
        Validate.isTrue(f.exists(), "File must exist!");
        InventoryInterface ii = new InventoryInterface(p);
        if (f.exists()) {
            FileConfiguration yml = YamlConfiguration.loadConfiguration(f);
            Map<String, Object> panes = Config.getConfigSectionValue(yml.get("panels"));
            if (panes == null) {
                p.getLogger().log(Level.WARNING, String.format("No root panel for Inventory Interface '%s'!", f.getName()));
                return ii;
            }
            panes.entrySet().stream()
                    .map((ent) -> InventoryPanel.valueOf(ii, ent.getValue()).setSerializedName(ent.getKey()))
                    .filter(ip -> ip != null)
                    .forEach(ip -> ii.panels.put(ip.getSeed(), ip));
            if (ii.getRootPanel() == null) {
                p.getLogger().log(Level.WARNING, String.format("No root panel for Inventory Interface '%s'!", f.getName()));
            }
        }
        return ii;
    }

    /**
     * Saves an {@link InventoryInterface} to a specified {@link File} in YAML
     * 
     * @since 0.0.1
     * @version 0.1.0
     * 
     * @param ii The {@link InventoryInterface} to save
     * @param save The {@link File} to save to
     * @throws IOException If the method failed to save to the file
     */
    public static void serialize(InventoryInterface ii, File save) throws IOException {
        Validate.notNull(save, "File cannot be null!");
        FileConfiguration f = YamlConfiguration.loadConfiguration(save);
        f.set("panels", ii.panels.values().stream().collect(Collectors.toMap(
                InventoryPanel::getSerializedName,
                Function.identity()
        )));
        f.save(save);
    }

}
