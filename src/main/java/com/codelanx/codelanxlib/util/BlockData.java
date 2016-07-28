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
package com.codelanx.codelanxlib.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Objects;

/**
 * Allows for representing material and it's data counterpart as a single string
 *
 * @since 0.2.0
 * @author 1Rogue
 * @version 0.2.0
 */
public class BlockData implements Comparable<BlockData> {

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

    public BlockData(Block b) {
        this(b.getType(), b.getData());
    }

    public BlockData(ItemStack stack) {
        this(stack.getType(), stack.getData().getData());
    }

    public BlockData(MaterialData data) {
        this(data.getItemType(), data.getData());
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

    public MaterialData toMaterialData() {
        return new MaterialData(this.mat, this.data > 0 ? this.data : 0);
    }

    public void applyToBlock(Block b) {
        b.setType(this.mat);
        if (this.data > 0) {
            b.setData(this.data);
        }
    }

    public boolean comparable(ItemStack stack) {
        return this.comparable(stack.getData());
    }

    public boolean comparable(Block b) {
        return this.mat == b.getType()
                && (this.data < 0 || b.getData() == this.data);
    }

    public boolean comparable(MaterialData data) {
        return this.mat == data.getItemType()
                && (this.data < 0 || data.getData() == this.data);
    }

    @Override
    public String toString() {
        return this.mat.toString() + (this.data != 0 ? ":" + (this.data < 0 ? "*" : this.data) : "");
    }

    /**
     * Converts a {@link BlockData} string back into an object instance
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param in The string to parse
     * @return The relevant {@link BlockData}
     */
    public static BlockData fromString(String in) {
        String[] raw = in.split(":");
        byte data = 0;
        Material mat;
        switch (raw.length) {
            case 2:
                if (raw[1].equals("*")) {
                    data = -1;
                } else {
                    data = Byte.valueOf(raw[1]);
                }
            case 1:
                mat = Material.matchMaterial(raw[0].toUpperCase());
                Validate.notNull(mat);
                break;
            default:
                throw new IllegalArgumentException("Malformed string passed to BlockData#fromString: '" + in + "'");
        }
        return new BlockData(mat, data);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.mat);
        hash = 41 * hash + this.data;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockData other = (BlockData) obj;
        if (this.mat != other.mat) {
            return false;
        }
        return this.data == other.data || this.data < 0 || other.data < 0; //TODO: remove comparison exception?
    }

    @Override
    public int compareTo(BlockData o) {
        if (this.mat.ordinal() != o.mat.ordinal()) {
            return this.mat.ordinal() - o.mat.ordinal();
        } else {
            if (this.data < 0 || o.data < 0) {
                return 0;
            }
            return this.data - o.data;
        }
    }
    
}
