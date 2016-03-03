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
package com.codelanx.codelanxlib.internal;

import com.codelanx.codelanxlib.permission.Permissions;
import com.codelanx.codelanxlib.util.Protections;

/**
 * Internal {@link Permissions} enum for CodelanxLib
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public enum InternalPerms implements Permissions {

    /**
     * Allows bypassing protection provided by the
     * {@link Protections Protections} class
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    PROTECTION_OVERRIDE("protect.override");

    private final String permission;

    private InternalPerms(String permission) {
        this.permission = permission;
    }

    @Override
    public String getBase() {
        return "commons";
    }

    @Override
    public String getNode() {
        return this.permission;
    }

}
