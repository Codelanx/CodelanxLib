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

import com.codelanx.commons.util.Lambdas;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class for mapping command arguments to simplify basic uses of
 * {@link CommandNode#tabComplete(CommandSender, String...)}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class TabInfo {

    /** Represents a blank {@link List} for returning no input from {@link CommandNode#tabComplete(CommandSender, String...)} */
    public static final List<String> BLANK_TAB_COMPLETE = Collections.unmodifiableList(new ArrayList<>());
    /** Represents a mapping of default arguments */
    private final Map<Integer, SupplierContainer> defaults = new HashMap<>();

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
        Validate.notNull(defaults);
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
        Validate.notNull(defaults);
        this.getContainer(argCount).list = defaults;
    }

    /**
     * Maps an argument count to a {@link Supplier} for a
     * {@link List List&lt;String&gt;}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param argCount The number of arguments for these defaults
     * @param defaults A {@link Supplier} that returns a {@link List} of strings
     */
    public void map(int argCount, Supplier<? extends List<String>> defaults) {
        Validate.notNull(defaults);
        this.getContainer(argCount).supplier = defaults;
    }

    /**
     * Maps an argument count to a {@link Function} that accepts the relevant
     * argument being parsed, or {@code null} if there is no argument to parse,
     * and returns a {@link List List&lt;String&gt;} of the results
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param argCount The number of arguments for these defaults
     * @param defaults A {@link Function} which accepts a possibly null argument
     *                 that is being parsed, and returns a {@link List} of
     *                 possible strings for arguments
     */
    public void map(int argCount, Function<String, ? extends List<String>> defaults) {
        Validate.notNull(defaults);
        this.getContainer(argCount).function = defaults;
    }

    /**
     * Maps an argument count to a {@link BiFunction} that accepts the
     * {@link CommandSender} who tabbed, the relevant argument being parsed, or
     * {@code null} if there is no argument to parse, and returns a
     * {@link List List&lt;String&gt;} of the results.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param argCount The number of arguments for these defaults
     * @param defaults A {@link BiFunction} that accepts the sender who tabbed,
     *                 the relevant argument being tabbed (or null), and returns
     *                 a {@link List} of possible string arguments to fill with
     */
    public void map(int argCount, BiFunction<CommandSender, String, ? extends List<String>> defaults) {
        Validate.notNull(defaults);
        this.getContainer(argCount).biFunction = defaults;
    }

    //Retrieves the internal SupplierContainer
    private SupplierContainer getContainer(int argCount) {
        SupplierContainer back = this.defaults.get(argCount);
        if (back == null) {
            back = new SupplierContainer();
            this.defaults.put(argCount, back);
        }
        return back;
    }

    /**
     * Returns {@link TabInfo#apply(CommandSender, String...) this.apply(null, args)}
     * <br><br>
     * Applies this {@link TabInfo} to a series of command arguments. If the
     * final argument is incomplete, then any default arguments which do not
     * start with the specified incomplete argument will be removed from the
     * possible results
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @see TabInfo#apply(CommandSender, String...)
     * @param args The arguments being tested
     * @return The possible arguments that could be used
     */
    public List<String> apply(String... args) {
        return this.apply(null, args);
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
     * @param sender The {@link CommandSender} who is tabbing
     * @param args The arguments being tested
     * @return The possible arguments that could be used
     */
    public List<String> apply(CommandSender sender, String... args) {
        String arg = null;
        int get = args.length - 1;
        if (args.length > 0) {
            arg = args[get];
        } else {
            get = 0;
        }
        SupplierContainer back = this.defaults.get(get);
        if (back == null) {
            return TabInfo.BLANK_TAB_COMPLETE;
        } else {
            List<String> fil = new ArrayList<>(back.apply(sender, arg)); //Potentially unknown return (unmodifiable)
            fil.removeIf(Lambdas::isNull);
            if (arg != null) {
                final String tempArg = arg;
                fil.removeIf(s -> !s.startsWith(tempArg));
            }
            return fil;
        }
    }

    /**
     * Returns a {@link Supplier} which will return a string {@link List} of
     * online players
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return A {@link Supplier} for a {@link List} of online player's names
     */
    public static Supplier<List<String>> onlinePlayers() {
        return () -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
    }

    /**
     * Returns a {@link Supplier} which will return a string {@link List} of
     * online players, filtered by the given predicate. This {@link Supplier}
     * will contain only the elements which return {@code true} from the
     * {@link Predicate}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param filter The {@link Predicate} to filter by
     * @return A {@link Supplier} for a {@link List} of online player's names
     */
    public static Supplier<List<String>> onlinePlayers(Predicate<? super Player> filter) {
        return () -> Bukkit.getOnlinePlayers().stream().filter(filter).map(p -> p.getName()).collect(Collectors.toList());
    }

    //Accepts anything (relevant) that will return a List<String> value
    private class SupplierContainer {
        
        private List<String> list;
        private Supplier<? extends List<String>> supplier;
        private Function<String, ? extends List<String>> function;
        private BiFunction<CommandSender, String, ? extends List<String>> biFunction;

        //While a bit messy, applies logic for each type
        public List<String> apply(CommandSender sender, String arg) {
            List<String> back = new ArrayList<>();
            if (this.list != null) {
                back.addAll(this.list); //unmodifiable
            }
            if (this.supplier != null) {
                back.addAll(this.supplier.get());
            }
            if (this.function != null) {
                back.addAll(this.function.apply(arg));
            }
            if (this.biFunction != null) {
                back.addAll(this.biFunction.apply(sender, arg));
            }
            return back;
        }

    }
}
