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
package com.codelanx.codelanxlib.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for mapping command arguments to simplify basic uses of
 * {@link CommandNode#tabComplete(CommandSender, String...)}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class TabInfo {

    /** Represents a mapping of default arguments */
    private final Map<Integer, List<String>> defaults = new HashMap<>();

    /**
     * Maps an argument count to a series of default argument values. Note that
     * your first arguments (the ones which immediately proceed the
     * {@link CommandNode} itself) should be mapped at {@code argCount 0}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param argCount The number of arguments for these defaults
     * @param defaults The defaults to set
     */
    public void map(int argCount, String... defaults) {
        this.map(argCount, Arrays.asList(defaults));
    }

    /**
     * Maps an argument count to a specific list of default argument values.
     * Note that your first arguments (the ones which immediately proceed the
     * {@link CommandNode} itself) should be mapped at {@code argCount 0}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param argCount The number of arguments for these defaults
     * @param defaults The defaults to set
     */
    public void map(int argCount, List<String> defaults) {
        this.defaults.put(argCount, new ArrayList<>(defaults));
    }

    /**
     * Applies this {@link TabInfo} to a series of command arguments. If the
     * final argument is incomplete, then any default arguments which do not
     * start with the specified incomplete argument will be removed from the
     * possible results
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param args The arguments being tested
     * @return The possible arguments that could be used
     */
    public List<String> apply(String... args) {
        if (args.length < 1) {
            return this.defaults.get(0);
        }
        List<String> back = this.defaults.get(args.length - 1);
        if (back == null) {
            return CommandNode.BLANK_TAB_COMPLETE;
        }
        back.removeIf(s -> !s.startsWith(args[args.length - 1]));
        return back;
    }

}
