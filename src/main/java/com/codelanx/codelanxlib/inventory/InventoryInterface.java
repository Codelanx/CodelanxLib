/*
 * Copyright (C) 2014 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2014 or as published
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
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
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public final class InventoryInterface {

    static final int SEED_LENGTH = 4;
    private InventoryPanel root;
    private final String seed;
    private final Map<String, InventoryPanel> panels = new HashMap<>();
    private final Map<MenuIcon, InventoryPanel> links = new HashMap<>();

    public InventoryInterface(Plugin plugin) {
        //generate seed
        this.seed = this.generateSeed(InventoryInterface.SEED_LENGTH);
        //register listener
        Bukkit.getServer().getPluginManager().registerEvents(new InterfaceListener(plugin, this), plugin);
    }

    public InventoryPanel getOpeningPanel() {
        return this.root;
    }

    public void openInterface(Player p) {
        if (this.getOpeningPanel() == null) {
            throw new NullPointerException("No root panel set in interface!");
        }
        p.openInventory(this.getOpeningPanel().getMenu());
    }

    public InventoryPanel createPanel(String name, int rows) {
        InventoryPanel ip = new InventoryPanel(this, name, rows);
        this.panels.put(ip.getSeed(), ip);
        return ip;
    }

    public InventoryPanel getLinkedPanel(MenuIcon icon) {
        return this.links.get(icon);
    }

    public Collection<? extends InventoryPanel> getPanels() {
        return Collections.unmodifiableCollection(this.panels.values());
    }

    public void setRootPanel(InventoryPanel panel) {
        if (!this.panels.containsValue(panel)) {
            throw new IllegalArgumentException("Must use a registered panel as root!");
        }
        this.root = panel;
    }

    public boolean isLinked(MenuIcon icon) {
        return this.links.containsKey(icon);
    }

    public InventoryPanel getPanelBySeed(String title) {
        int ii = InventoryInterface.SEED_LENGTH * 2;
        int ip = ii + (InventoryPanel.SEED_LENGTH * 2);
        return this.panels.get(title.substring(title.length() - ip, title.length() - ii));
    }

    protected boolean isRoot(InventoryPanel panel) {
        return this.root.getSeed().equals(panel.getSeed());
    }

    protected void linkPanel(MenuIcon icon, InventoryPanel panel) {
        this.links.put(icon, panel);
    }

    protected String getSeed() {
        return this.seed;
    }

    protected final String generateSeed(int length) {
        StringBuilder sb = new StringBuilder();
        ChatColor[] vals = ChatColor.values();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append('&').append(vals[rand.nextInt(vals.length)]);
        }
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    public static InventoryInterface deserialize(Plugin p, File f) {
        if (f == null) {
            throw new IllegalArgumentException("File cannot be null!");
        }
        InventoryInterface ii = new InventoryInterface(p);
        if (f.exists()) {
            FileConfiguration yml = YamlConfiguration.loadConfiguration(f);
            for (Object o : yml.getList("panels")) {
                InventoryPanel ip = InventoryPanel.valueOf(ii, o);
                ii.panels.put(ip.getSeed(), ip);
            }
        }
        return ii;
    }

    public static void serialize(InventoryInterface ii, File save) throws IOException {
        if (save == null) {
            throw new IllegalArgumentException("File cannot be null!");
        }
        FileConfiguration f = YamlConfiguration.loadConfiguration(save);
        List<InventoryPanel> panes = new ArrayList<>(ii.panels.values());
        f.set("panels", panes.stream().map(InventoryPanel::toMap).collect(Collectors.toList()));
        f.save(save);
    }

}
