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

import com.codelanx.codelanxlib.config.ConfigurationLoader;
import com.codelanx.codelanxlib.util.DebugUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Class description for {@link MenuIcon}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class MenuIcon {

    protected static final Random RAND = new Random();
    protected Map<String, Object> options;
    protected final long seed;
    protected final ItemStack item;
    protected Execution onExec;
    protected final List<String> perms = new ArrayList<>();

    MenuIcon(ItemStack item, Execution onExec, Map<String, Object> options) {
        if (item == null || options == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!");
        }
        this.item = item;
        this.seed = MenuIcon.RAND.nextLong();
        this.onExec = onExec;
        this.options = options;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setExecutable(Execution onExec) {
        this.onExec = onExec;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.seed ^ (this.seed >>> 32));
        hash = 53 * hash + Objects.hashCode(this.item);
        return hash;
    }

    public Map<String, Object> getOptions() {
        return Collections.unmodifiableMap(this.options);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MenuIcon)) {
            return false;
        }
        final MenuIcon other = (MenuIcon) obj;
        if (this.seed != other.seed) {
            return false;
        }
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        return true;
    }

    void execute(Player p, InventoryInterface ii) {
        if (this.onExec != null) {
            this.onExec.onExec(p, ii, this);
        }
    }

    static MenuIcon valueOf(InventoryInterface ii, Object o) {
        Map<String, Object> map = ConfigurationLoader.getConfigSectionValue(o);
        if (map == null || map.isEmpty()) {
            return null;
        }
        ItemStack item = (ItemStack) map.get("item");
        Map<String, Object> opts = ConfigurationLoader.getConfigSectionValue(map.get("options"));
        List<String> perm = (List<String>) map.get("permissions");
        String link = String.valueOf(map.get("link"));
        if (item != null && opts != null) {
            MenuIcon back = new MenuIcon(item, null, opts);
            if (perm != null && !perm.isEmpty()) {
                back.perms.addAll(perm);
            }
            if (link != null) {
                new Timer().schedule(new TimerTask() {          
                    @Override
                    public void run() {
                        InventoryPanel ip = ii.find(p -> link.equalsIgnoreCase(p.getSerializedName()));
                        if (ip != null) {
                            ip.linkIcon(back);
                        }
                    }
                }, 3000);
            }
            return back;
        } else {
            return null;
        }
    }

    Map<String, Object> toMap(InventoryInterface ii) {
        Map<String, Object> back = new HashMap<>();
        back.put("item", this.item);
        back.put("options", this.options);
        if (!this.perms.isEmpty()) {
            back.put("permissions", this.perms);
        }
        if (ii.isLinked(this)) {
            back.put("link", ii.getLinkedPanel(this).getSerializedName());
        }
        return back;
    }

    public void addPermission(String permission) {
        this.perms.add(permission);
    }

    public boolean requiresPerms() {
        return !this.perms.isEmpty();
    }

    public boolean hasPermission(Player p) {
        return this.requiresPerms() ? this.perms.stream().anyMatch(p::hasPermission) : true;
    }

}
