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
package com.codelanx.codelanxlib.serialize;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

/**
 * Class description for {@link SLocation}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
@SerializableAs("Location")
public class SLocation implements ConfigurationSerializable {

    private final Vector loc;
    private final UUID uuid;
    private World world;

    public SLocation(Location loc) {
        this.loc = loc.toVector();
        this.uuid = loc.getWorld().getUID();
    }

    public SLocation(Map<String, Object> config) {
        this.loc = (Vector) config.get("location");
        this.uuid = UUID.fromString((String) config.get("world"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new HashMap<>();
        back.put("location", this.loc);
        back.put("world", this.uuid.toString());
        return back;
    }

    public SLocation valueOf(Map<String, Object> config) {
        return new SLocation(config);
    }

    public SLocation deserialize(Map<String, Object> config) {
        return new SLocation(config);
    }

    public World getWorld() {
        if (this.world == null) {
            this.world = Bukkit.getWorld(this.uuid);
        }
        return this.world;
    }

    public Vector getVector() {
        return this.loc;
    }

    public Location toLocation() {
        return this.getVector().toLocation(this.getWorld());
    }

}