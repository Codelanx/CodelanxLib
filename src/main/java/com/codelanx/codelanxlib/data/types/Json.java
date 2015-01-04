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
package com.codelanx.codelanxlib.data.types;

import com.codelanx.codelanxlib.data.FileDataType;
import com.codelanx.codelanxlib.util.DebugUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Represents a JSON file that has been parsed and loaded into memory.
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class Json implements FileDataType {

    protected final File location;
    protected final JSONObject root;

    /**
     * Reads and loads a JSON file into memory
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param location The location of the file to parse
     * @throws ParseException If the file is not in standard JSON format
     */
    public Json(File location) throws ParseException {
        this.location = location;
        JSONParser parser = new JSONParser();
        JSONObject root = null;
        try {
            root = (JSONObject) parser.parse(new FileReader(this.location));
        } catch (IOException ex) {
            DebugUtil.error("Error loading JSON file!", ex);
        }
        this.root = root;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @param value {@inheritDoc}
     */
    @Override
    public void set(String path, Object value) {
        String[] ladder = this.getLadder(path);
        this.traverse(true, ladder).put(ladder[ladder.length - 1], value);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isSet(String path) {
        String[] ladder = this.getLadder(path);
        return this.getContainer(ladder).containsKey(ladder[ladder.length - 1]);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Object get(String path) {
        String[] ladder = this.getLadder(path);
        return this.getContainer(ladder).get(ladder[ladder.length - 1]);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path {@inheritDoc}
     * @param def {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Object get(String path, Object def) {
        String[] ladder = this.getLadder(path);
        JSONObject container = this.traverse(false, ladder);
        return container.get(ladder[ladder.length - 1]);
    }

    /**
     * Gets the {@link JSONObject} above the requested object specified by the
     * supplied {@code ladder} parameter.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param ladder A string array, already split in order of levels to
     *               traverse
     * @return The {@link JSONObject} above the requested object
     */
    protected JSONObject getContainer(String... ladder) {
        return this.traverse(false, ladder);
    }

    /**
     * Gets the {@link JSONObject} above the requested object specified by the
     * supplied {@code path} parameter.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path The period-delimited path to the object desired
     * @return The {@link JSONObject} above the requested object
     */
    protected JSONObject getContainer(String path) {
        return this.getContainer(this.getLadder(path));
    }

    /**
     * Converts a period-delimited string into a String array
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param path The path to split
     * @return The split path
     */
    protected String[] getLadder(String path) {
        return path.split("\\.");
    }

    /**
     * Traverses a {@link JSONObject} tree from the internal root node. Will
     * return a {@link JSONObject} container of the relevant element at the end
     * of the search, or just an empty {@link JSONObject} if nothing exists.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param makePath Whether to fill empty space with {@link JSONObject}s
     * @param ladder A String array depicting the location to search in
     * @return A {@link JSONObject} containing the last node in the ladder
     */
    protected JSONObject traverse(boolean makePath, String... ladder) {
        JSONObject container = this.root;
        for (int i = 0; i < ladder.length - 1; i++) {
            if (!container.containsKey(ladder[i]) && makePath) {
                container.put(ladder[i], new JSONObject());
            }
            JSONObject temp = (JSONObject) container.get(ladder[i]);
            if (temp == null) {
                //purposefully set as null
                break;
            } else {
                container = temp;
            }
        }
        return container;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void save() throws IOException {
        this.save(this.location);
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param target {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void save(File target) throws IOException {
        new FileWriter(target).write(this.root.toJSONString());
    }

}
