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
package com.codelanx.codelanxlib.util.auth;

import java.util.UUID;

/**
 * Represents information about a player
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class UserInfo {

    private final String name;
    private final UUID uuid;

    /**
     * Stores the passed fields
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param name The player name
     * @param uuid The player {@link UUID}
     */
    public UserInfo(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Returns the name of this player
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The player name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the {@link UUID} of this player
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The player {@link UUID}
     */
    public UUID getUUID() {
        return this.uuid;
    }

}
