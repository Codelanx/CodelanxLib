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
 * Represents a {@link ConfigurationSerializable} {@link Location} with a lazily
 * initialized world
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
@SerializableAs("Location")
public class SLocation implements ConfigurationSerializable {

    private final Vector loc;
    private final float yaw;
    private final float pitch;
    private final UUID uuid;
    private World world;

    /**
     * Creates a new {@link SLocation} object from the passed {@link Location}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param loc The {@link Location} to serialize
     */
    public SLocation(Location loc) {
        this.loc = loc.toVector();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.uuid = loc.getWorld().getUID();
    }

    /**
     * Allows constructing an {@link SLocation} from serialized parts
     * 
     * @since 0.1.0
     * @version 0.1.0 
     * 
     * @param loc The relevant {@link Vector}
     * @param worldUUID The {@link UUID} of the world for this {@link SLocation}
     * @param pitch The pitch
     * @param yaw The yaw
     */
    public SLocation(Vector loc, UUID worldUUID, float pitch, float yaw) {
        this.loc = loc;
        this.uuid = worldUUID;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * {@link ConfigurationSerializable} constructor. Should not be used by
     * anything other than Bukkit.
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     */
    public SLocation(Map<String, Object> config) {
        this.loc = (Vector) config.get("location");
        this.uuid = UUID.fromString((String) config.get("world"));
        this.pitch = config.containsKey("pitch") ? ((Number) config.get("pitch")).floatValue() : 0F;
        this.yaw = config.containsKey("yaw") ? ((Number) config.get("yaw")).floatValue() : 0F;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return {@inheritDoc}
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new HashMap<>();
        back.put("location", this.loc);
        back.put("world", this.uuid.toString());
        back.put("pitch", this.pitch);
        back.put("yaw", this.yaw);
        return back;
    }

    /**
     * Creates a new {@link SLocation} object and returns it. Should only be
     * used by Bukkit
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     * @return A new {@link SLocation} object
     */
    public static SLocation valueOf(Map<String, Object> config) {
        return new SLocation(config);
    }

    /**
     * Creates a new {@link SLocation} object and returns it. Should only be
     * used by Bukkit
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @param config A serialized {@link Map} of this object
     * @return A new {@link SLocation} object
     */
    public static SLocation deserialize(Map<String, Object> config) {
        return new SLocation(config);
    }

    /**
     * Retrieves the {@link World} object relevant to this instance. This is
     * lazily-initialized, so until this method is called the class will not
     * attempt to retrieve the {@link World} value
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The relevant {@link World} object, or {@code null} if not found
     */
    public World getWorld() {
        if (this.world == null) {
            this.world = Bukkit.getWorld(this.uuid);
        }
        return this.world;
    }

    /**
     * Returns the relevant {@link Vector} object to this instance
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return The relevant {@link Vector} object
     */
    public Vector getVector() {
        return this.loc;
    }

    /**
     * Converts this instance into a {@link Location} object. This method will
     * fail if called before Bukkit has loaded any worlds
     * 
     * @since 0.0.1
     * @version 0.0.1
     * 
     * @return This instance in the context of a {@link Location} object
     */
    public Location toLocation() {
        return this.getVector().toLocation(this.getWorld(), this.yaw, this.pitch);
    }

}