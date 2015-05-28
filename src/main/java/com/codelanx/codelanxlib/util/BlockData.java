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

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Allows for representing material and it's data counterpart as a single string
 *
 * @since 0.2.0
 * @author 1Rogue
 * @version 0.2.0
 */
public class BlockData {

    private final Material mat;
    private final byte data;

    /**
     * Creates a {@link BlockData} instance
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param mat The {@link Material} value
     * @param data A {@code byte} representing an internal data value, set to
     *             negative to match all data values
     */
    public BlockData(Material mat, Number data) {
        this.mat = mat;
        this.data = data.byteValue();
    }

    public Material getMaterial() {
        return this.mat;
    }

    public byte getData() {
        return this.data;
    }

    /**
     * Converts this {@link BlockData} to an {@link ItemStack}, which can then
     * be assigned to a block
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @return This {@link BlockData} in {@link ItemStack} form
     */
    public ItemStack toItemStack() {
        ItemStack back = new ItemStack(this.mat);
        if (this.data > 0) {
            back.getData().setData(this.data);
        }
        return back;
    }

    @Override
    public String toString() {
        return this.mat.toString() + ":" + (this.data < 0 ? "*" : this.data);
    }

    /**
     * Converts a {@link BlockData} string back into an object instance
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param in
     * @return 
     */
    public static BlockData fromString(String in) {
        String[] raw = in.split(":");
        Validate.isTrue(raw.length == 2, "Malformed string passed to BlockData#fromString");
        Material mat = Material.matchMaterial(raw[0]);
        byte data;
        if (raw[1].equals("*")) {
            data = -1;
        } else {
            data = Byte.valueOf(raw[1]);
        }
        Validate.notNull(mat);
        return new BlockData(mat, data);
    }

}
