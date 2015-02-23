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

import com.codelanx.codelanxlib.config.Lang;
import com.codelanx.codelanxlib.logging.Debugger;
import com.codelanx.codelanxlib.util.Reflections;
import com.codelanx.codelanxlib.util.exception.Exceptions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

/**
 * Class description for {@link CommandNode}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 0.1.0
 * 
 * @param <E> The type of the {@link Plugin} associated with this {@link CommandNode}
 */
public abstract class CommandNode<E extends Plugin> implements CommandExecutor, TabCompleter, Comparable<CommandNode<?>> {

    protected final E plugin;
    private final Lang format;
    private CommandNode<? extends Plugin> parent;
    private final Map<String, CommandNode<? extends Plugin>> subcommands = new HashMap<>();
    private boolean executable = true;

    public CommandNode(E plugin) {
        this(plugin, null);
    }

    public CommandNode(E plugin, CommandNode<?> parent) {
        this.plugin = plugin;
        this.format = Lang.getFormat(this.plugin);
        if (parent != null) {
            this.setParent(parent);
        }
        this.addChild(new HelpCommand<>(this.plugin));
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Exceptions.illegalPluginAccess(Reflections.accessedFromBukkit(), "Only bukkit may call this method");
        CommandNode<? extends Plugin> child = this.getClosestChild(StringUtils.join(args, " "));
        child.execute(sender, args);
        return true;
    }

    /**
     * Represents the code at the end of a {@link CommandNode} chain
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param sender The command executor
     * @param args The command arguments, starting after the subcommand name
     *
     * @return The {@link CommandStatus} representing the result of the command
     */
    public abstract CommandStatus execute(CommandSender sender, String... args);

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Exceptions.illegalPluginAccess(Reflections.accessedFromBukkit(), "Only bukkit may call this method");
        CommandNode<? extends Plugin> child = this.getClosestChild(StringUtils.join(args, " "));
        List<String> back = child.tabComplete(sender, args);
        if (!child.subcommands.isEmpty()) {
            back.addAll(Reflections.matchClosestKeys(child.subcommands, args[0]));
        }
        return back;
    }

    /**
     * Returns a {@link List} of possible strings that could be supplied for
     * the next argument
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param sender The command "tabber"
     * @param args The command arguments, starting after the subcommand name and
     *             contains potentially unfinished arguments
     * @return A {@link List} of strings that can be supplied for the next arg
     */
    public abstract List<String> tabComplete(CommandSender sender, String... args);

    /**
     * Returns the name of the command, used for storing a
     * {@link HashMap} of the commands as well as the subcommand
     * argument
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return The command's name
     */
    public abstract String getName();

    /**
     * Returns the command usage
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @return Usage for this {@link CommandNode}
     */
    public final String getUsage() {
        if (this.parent != null) {
            return this.parent.getUsage().replaceAll("\\[.*\\]", "").trim() + " " + this.usage();
        }
        return this.usage();
    }

    protected String usage() {
         return this.getName();
    }

    /**
     * Information about this specific command. Should be kept concise
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @return A small string about the command
     */
    public abstract Lang info();

    public final CommandNode<? extends Plugin> getParent() {
        return this.parent;
    }

    private void setParent(CommandNode<? extends Plugin> parent) {
        if (this.getParent() != null) {
            this.getParent().subcommands.remove(this.getName());
        }
        this.parent = parent;
        if (this.getParent() != null) {
            this.getParent().subcommands.put(this.getName(), this);
        }
    }

    public final void addChild(CommandNode<? extends Plugin>... children) {
        for (CommandNode<? extends Plugin> ccmd : children) {
            ccmd.setParent(this);
        }
    }

    /**
     * Returns a subcommand, or {@code null} if none exists.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param name The name of the subcommand
     * @return A relevant {@link CommandNode}, or null if it does not exist
     */
    public final CommandNode<? extends Plugin> getChild(String name) {
        return this.subcommands.get(name);
    }

    public CommandNode<? extends Plugin> getChild(String... args) {
        CommandNode<? extends Plugin> next = this;
        for (String s : args) {
            next = next.getChild(s);
            if (next == null) {
                return null;
            }
        }
        return next;
    }

    public CommandNode<? extends Plugin> getClosestChild(String... args) {
        CommandNode<? extends Plugin> next = this;
        for (String s : args) {
            if (next.getChild(s) == null) {
                return next;
            }
            next = next.getChild(s);
        }
        return next;
    }

    protected final void registerAsBukkitCommand() {
        PluginCommand cmd = this.plugin.getServer().getPluginCommand(this.getName());
        Validate.notNull(cmd, "Attempted to register a non-existant command");
        cmd.setExecutor(this);
    }

    /**
     * Returns all subcommands as a {@link Collection}.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return A {@link Collection} of all registered {@link CommandNode}
     */
    public final Collection<CommandNode<? extends Plugin>> getChildren() {
        return Collections.unmodifiableCollection(this.subcommands.values());
    }

    /**
     * Compares {@link CommandNode} objects by command name via
     * {@link CCommand#getName()}, or their parent objects if possible
     * <br><br> {inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param o The {@link CommandNode} to compare to
     * @return {@inheritDoc}
     */
    @Override
    public final int compareTo(CommandNode<?> o) {
        //Do not check for null, comparable contract calls for NPE
        int dirComp = this.getName().compareTo(o.getName());
        if (this.getParent() != null && o.getParent() != null) {
            if (this.getParent() == o.getParent()) {
                return dirComp;
            } else {
                return this.getParent().compareTo(o.getParent());
            }
        } else if (this.getParent() != null && o.getParent() == null) {
            return -1;
        } else if (this.getParent() == null && o.getParent() != null) {
            return 1; 
        } else {
            return dirComp;
        }
    }

    public Collection<CommandNode<? extends Plugin>> traverse() {
        Collection<CommandNode<? extends Plugin>> back = new ArrayList<>();
        back.add(this);
        back = this.subcommands.values().stream().map(CommandNode::traverse)
                .reduce(back, (u, r) -> { u.addAll(r); return u; });
        back.removeIf(c -> c.getClass() == HelpCommand.class);
        back.removeIf(c -> !c.isExecutable());
        return back;
    }

    public final Collection<CommandNode<? extends Plugin>> traverseCommands() {
        Collection<CommandNode<? extends Plugin>> back = this.traverse();
        //implement
        return back;
    }

    public final Collection<CommandNode<? extends Plugin>> traverseAliases() {
        Collection<CommandNode<? extends Plugin>> back = this.traverse();
        //implement
        return back;
    }

    protected final void attachReloadCommand() {
        this.addChild(new ReloadCommand<>(this.plugin));
    }

    public final void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public final boolean isExecutable() {
        return this.executable;
    }

    public final <T extends Plugin> void alias(String command, CommandNode<T> toAlias) {
        if (!command.contains(" ")) {
            this.subcommands.put(command, toAlias);
        }
        String cmd = command.split(" ")[0];
        CommandNode<? extends Plugin> next = this.getChild(cmd);
        if (next == null) {
            CommandNode<T> put = CommandNode.getLinkingNode(cmd, toAlias.plugin, (node) -> node.setParent(this));
            this.subcommands.put(cmd, put);
            next = put;
        }
        next.alias(command.substring(cmd.length() + 1), toAlias);
    }

    protected final void aliasAsBukkitCommand(String cmd) {
        String token = cmd.split(" ")[0];
        PluginCommand bcmd = this.getBukkitCommand(token, this.plugin);
        if (bcmd != null) {
            this.registerBukkitCommand(bcmd);
        }
    }

    private PluginCommand getBukkitCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            return c.newInstance(name, plugin);
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException ex) {
            Debugger.error(ex, "Error aliasing bukkit command");
        }
        return null;
    }

    private void registerBukkitCommand(PluginCommand cmd) {
        try {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            SimpleCommandMap scm;
            synchronized(scm = (SimpleCommandMap) f.get(Bukkit.getServer().getPluginManager())) {
                scm.register(this.plugin.getName().toLowerCase() + ".", cmd);
            }
        } catch (IllegalArgumentException
                | IllegalAccessException
                | NoSuchFieldException
                | SecurityException ex) {
            Debugger.error(ex, "Error registering Bukkit command alias");
        }
    }

    public static <T extends Plugin> CommandNode<T> getLinkingNode(String command, T rel, Consumer<CommandNode<T>> onConstruct) {
        return new CommandNode<T>(rel) {

            {
                this.setExecutable(false);
                if (onConstruct != null) {
                    onConstruct.accept(this);
                }
            }

            @Override
            public CommandStatus execute(CommandSender sender, String... args) {
                return CommandStatus.NOT_EXECUTABLE;
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String... args) {
                return new ArrayList<>();
            }

            @Override
            public String getName() {
                return command;
            }

            @Override
            public Lang info() {
                return Lang.createLang("This is a linking node for '" + command + "'");
            }

        };
    }

}
