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
import java.util.logging.Level;
import java.util.stream.Collectors;
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
 * Represents a singular point in a command argument chain (or even the command
 * itself)
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 *
 * @param <E> The type of the {@link Plugin} associated with this
 *            {@link CommandNode}
 */
public abstract class CommandNode<E extends Plugin> implements CommandExecutor, TabCompleter, Comparable<CommandNode<?>> {

    /** Represents a blank {@link List} for returning no input from {@link #tabComplete(CommandSender, String...)} */
    public static final List<String> BLANK_TAB_COMPLETE = Collections.unmodifiableList(new ArrayList<>());
    /** The {@link Plugin} relevant to this {@link CommandNode} */
    protected final E plugin;
    /** The format to output with */
    private final Lang format;
    /** The {@link CommandNode} that directly proceeds this node */
    private CommandNode<? extends Plugin> parent;
    /** A mapping of {@link CommandNode CommandNodes} as sub-commands */
    private final Map<String, CommandNode<? extends Plugin>> subcommands = new HashMap<>();
    /** Indicates whether or not this node is meant to be directly executed */
    private boolean executable = true;

    /**
     * Initializes a new {@link CommandNode} with no parent object
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @see CommandNode#CommandNode(Plugin, CommandNode)
     * @param plugin The {@link Plugin} relevant to this node
     */
    public CommandNode(E plugin) {
        this(plugin, null);
    }

    /**
     * Initializes a new {@link CommandNode} with the passed parent object. This
     * also attaches a {@link HelpCommand} as a sub-command in order to automate
     * help output
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param plugin The {@link Plugin} relevant to this node
     * @param parent The parent {@link CommandNode}, or {@code null} for none
     */
    public CommandNode(E plugin, CommandNode<?> parent) {
        this.plugin = plugin;
        this.format = Lang.getFormat(this.plugin);
        if (parent != null) {
            this.setParent(parent);
        }
        this.addChild(new HelpCommand<>(this.plugin));
    }

    /**
     * Called from Bukkit to indicate an executed command
     * <br><br> {@inheritDoc}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param sender {@inheritDoc}
     * @param command {@inheritDoc}
     * @param label {@inheritDoc}
     * @param args {@inheritDoc}
     * @return {@code true} if the returned {@link CommandStatus} was not a
     *         {@link CommandStatus#FAILED}
     */
    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Exceptions.illegalPluginAccess(Reflections.accessedFromBukkit(), "Only bukkit may call this method");
        CommandNode<? extends Plugin> child = this.getClosestChild(StringUtils.join(args, " "));
        CommandStatus stat;
        try {
            stat = child.execute(sender, args);
        } catch (Throwable ex) {
            stat = CommandStatus.FAILED;
            child.plugin.getLogger().log(Level.SEVERE, String.format("Unhandled exception executing command '%s %s'", label, StringUtils.join(args, " ")), ex);
        }
        stat.handle(sender, child.format, child);
        return stat != CommandStatus.FAILED;
    }

    /**
     * Represents the code at the end of a {@link CommandNode} chain
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param sender The command executor
     * @param args The command arguments, starting after the subcommand name
     *
     * @return The {@link CommandStatus} representing the result of the command
     */
    public abstract CommandStatus execute(CommandSender sender, String... args);

    /**
     * Called from Bukkit to indicate a call for tab completing
     * <br><br> {@inheritDoc}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param sender {@inheritDoc}
     * @param command {@inheritDoc}
     * @param alias {@inheritDoc}
     * @param args {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Exceptions.illegalPluginAccess(Reflections.accessedFromBukkit(), "Only bukkit may call this method");
        CommandNode<? extends Plugin> child = this.getClosestChild(StringUtils.join(args, " "));
        List<String> back = new ArrayList<>();
        back.addAll(child.tabComplete(sender, args));
        if (!child.subcommands.isEmpty()) {
            if (args.length < 1) {
                back.addAll(child.subcommands.keySet());
            } else {
                back.addAll(Reflections.matchClosestKeys(child.subcommands, args[0]));
            }
        }
        return back;
    }

    /**
     * Returns a {@link List} of possible strings that could be supplied for the
     * next argument
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param sender The command "tabber"
     * @param args The command arguments, starting after the subcommand name and
     *             contains potentially unfinished arguments
     * @return A {@link List} of strings that can be supplied for the next arg
     */
    protected abstract List<String> tabComplete(CommandSender sender, String... args);

    /**
     * Returns the name of the command, used for storing a {@link HashMap} of
     * the commands as well as the subcommand argument
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The command's name
     */
    public abstract String getName();

    /**
     * Returns the command usage
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return Usage for this {@link CommandNode}
     */
    public final String getUsage() {
        if (this.parent != null) {
            return this.parent.getUsage().replaceAll("\\[.*\\]", "")
                    .replaceAll("\\<.*\\>", "").trim() + " " + this.usage();
        }
        return this.usage();
    }

    /**
     * Describes the usage in the context of this specific {@link CommandNode}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The usage, defaults to {@link CommandNode#getName()}
     */
    protected String usage() {
        return this.getName() + " ";
    }

    /**
     * Information about this specific command. Should be kept concise
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return A small string about the command
     */
    public abstract Lang info();

    /**
     * Returns the direct parent {@link CommandNode} for this node
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The parent of this node
     */
    public final CommandNode<? extends Plugin> getParent() {
        return this.parent;
    }

    /**
     * Sets the parent for this node, and modifies the necessary subcommand
     * mappings
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param parent The {@link CommandNode} parent to set
     */
    private void setParent(CommandNode<? extends Plugin> parent) {
        if (this.getParent() != null) {
            this.getParent().subcommands.remove(this.getName());
        }
        this.parent = parent;
        if (this.getParent() != null) {
            this.getParent().subcommands.put(this.getName(), this);
        }
    }

    /**
     * Adds new child subcommands to this {@link CommandNode}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param children Any {@link CommandNode CommandNodes} to add
     */
    public final void addChild(CommandNode<? extends Plugin>... children) {
        for (CommandNode<? extends Plugin> ccmd : children) {
            ccmd.setParent(this);
        }
    }

    /**
     * Returns a subcommand, or {@code null} if none exists.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param name The name of the subcommand
     * @return A relevant {@link CommandNode}, or null if it does not exist
     */
    public final CommandNode<? extends Plugin> getChild(String name) {
        return this.subcommands.get(name);
    }

    /**
     * Iteratively retrieves the child {@link CommandNode} down the command
     * tree. If at any point in the depth search a node does not exist, this
     * will return {@code null}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param args The different {@link CommandNode} names to search through
     * @return The child node, or {@code null} if not found
     */
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

    /**
     * Returns the closest possible approximation of where the supplied argument
     * ladder will stop. As soon as the search reaches a nonexistent child child
     * node, the parent node is returned, up to and including the original node
     * this method is called upon
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param args The argument ladder to climb through
     * @return The closest {@link CommandNode} approximation found
     */
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

    /**
     * Returns all subcommands as a {@link Collection}
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
     * {@link CommandNode#getName()}, or their parent objects if possible
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
        return this.getUsage().compareTo(o.getUsage());
    }

    /**
     * Traverses the {@link CommandNode} tree and returns all child objects
     * found that satisfy the following:
     * <br><br>
     * <ul><li> The child's parent is the class that maps to it (aliases will be
     * mapped to, but will not have the mapping parent class as an actual
     * parent).
     * <li> The node is not a {@link HelpCommand}
     * <li> The value of the node's {@link CommandNode#isExecutable()} is
     * {@code true}
     * </ul>
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return All found {@link CommandNode} children for this node
     */
    public Collection<CommandNode<? extends Plugin>> traverse() {
        Collection<CommandNode<? extends Plugin>> back = new ArrayList<>();
        back.add(this);
        back = this.subcommands.values().stream()
                .filter(c -> this == c.getParent())
                .filter(c -> c.getClass() == HelpCommand.class)
                .map(c -> c.traverse())
                .reduce(back, (u, r) -> {
                    u.addAll(r);
                    return u;
                });
        back.removeIf(c -> !c.isExecutable());
        return back;
    }

    /**
     * Returns all {@link CommandNode} objects held by this node that are
     * aliases of other {@link CommandNode CommandNodes}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return Any aliased {@link CommandNode} objects
     */
    public final Map<String, CommandNode<? extends Plugin>> getAliases() {
        return this.subcommands.entrySet().stream().filter(c -> {
            return this != c.getValue().getParent();
        }).collect(Collectors.toMap(
                (c) -> this.getUsage() + " " + c.getKey(),
                (c) -> c.getValue()
        ));
    }

    /**
     * Finds the closest possible matches when a player executes a
     * non-executable node
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return Up to 3 of the nearest possible commands, can exceed 3 if the
     *         current node has more than 3 direct children that are executable
     */
    final List<CommandNode<? extends Plugin>> closestCommands() {
        if (this.subcommands.isEmpty()) {
            return new ArrayList<>();
        } else if (this.subcommands.values().stream().noneMatch(CommandNode::isExecutable)) {
            List<CommandNode<? extends Plugin>> back = new ArrayList<>();
            this.subcommands.values().forEach(c -> back.addAll(c.closestCommands()));
            if (back.size() > 3) {
                return back.subList(0, 2);
            }
            return back;
        } else {
            return new ArrayList<>(this.subcommands.values().stream()
                    .filter(CommandNode::isExecutable).collect(Collectors.toList()));
        }
    }

    /**
     * Attaches a {@link ReloadCommand} to this command object, to allow
     * reloading of the {@link Plugin} relevant to this node
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    protected final void attachReloadCommand() {
        this.addChild(new ReloadCommand<>(this.plugin));
    }

    /**
     * Sets whether or not this {@link CommandNode} can be executed. If this is
     * set to {@code false}, then the returned value of
     * {@link CommandNode#execute(CommandSender, String...)} should be
     * {@link CommandStatus#NOT_EXECUTABLE}. Normally a call to this method can
     * easily be supplemented by using
     * {@link CommandNode#getLinkingNode(String, Plugin, Consumer)} instead.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @see CommandNode#getLinkingNode(String, Plugin, Consumer)
     * @param executable
     */
    protected final void setExecutable(boolean executable) {
        this.executable = executable;
    }

    /**
     * Returns whether or not this node can be directly executed
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return {@code true} if this node can be directly executed
     */
    public final boolean isExecutable() {
        return this.executable;
    }

    /**
     * Aliases a passed {@link CommandNode}. If no string arguments are
     * supplied, the passed child node will be aliased under this parent node
     * using the child's returned value from {@link CommandNode#getName()}
     * <br><br>
     * If arguments are supplied, then this method will iterate down through
     * child nodes until an appropriate node is found, and will then use the
     * last argument as the alias for the passed child node. If any nodes do not
     * exist when iterating through child nodes, this method will create them
     * via {@link CommandNode#getLinkingNode(String, Plugin, Consumer)}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The type of the passed {@link CommandNode} alias's plugin
     * @param toAlias The {@link CommandNode} to alias
     * @param args The arguments chaining from this node to the alias
     */
    public final <T extends Plugin> void alias(CommandNode<T> toAlias, String... args) {
        if (args.length < 0) {
            this.subcommands.put(toAlias.getName(), toAlias);
        }
        CommandNode<? extends Plugin> child = this;
        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1) {
                child.subcommands.put(args[i], toAlias);
            }
            if (child.getChild(args[i]) == null) {
                final CommandNode<? extends Plugin> parent = child;
                CommandNode<T> put = CommandNode.getLinkingNode(args[i], toAlias.plugin, (node) -> node.setParent(parent));
                child.subcommands.put(args[i], put);
                child = put;
            } else {
                child = child.getChild(args[i]);
            }
        }
    }

    /**
     * Allows this {@link CommandNode} to be executed from Bukkit directly
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param cmd The command to register as under Bukkit
     */
    protected final void aliasAsBukkitCommand(String cmd) {
        String token = cmd.split(" ")[0];
        PluginCommand bcmd = this.getBukkitCommand(token, this.plugin);
        if (bcmd != null) {
            this.registerBukkitCommand(bcmd);
        }
    }

    /**
     * Registers this {@link CommandNode} as a bukkit-executable command, and
     * places this {@link CommandNode} as the command name
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    protected final void registerAsBukkitCommand() {
        PluginCommand cmd = this.plugin.getServer().getPluginCommand(this.getName());
        Validate.notNull(cmd, "Attempted to register a non-existant command");
        cmd.setExecutor(this);
    }

    /**
     * Creates a {@link PluginCommand} object with reflection, as the
     * constructor has protected access
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param name The name of the command
     * @param plugin The {@link Plugin} relevant to the command
     * @return A new {@link PluginCommand} object, or {@code null} if the
     *         creation of the object failed
     */
    private PluginCommand getBukkitCommand(String name, Plugin plugin) {
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            return c.newInstance(name, plugin);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Debugger.error(ex, "Error aliasing bukkit command");
        }
        return null;
    }

    /**
     * Registers a {@link PluginCommand} to Bukkit by dynamically adding it to
     * the {@link SimpleCommandMap} held by the {@link SimplePluginManager}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param cmd The {@link PluginCommand} to register
     */
    private void registerBukkitCommand(PluginCommand cmd) {
        try {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            SimpleCommandMap scm;
            synchronized (scm = (SimpleCommandMap) f.get(Bukkit.getServer().getPluginManager())) {
                scm.register(this.plugin.getName().toLowerCase() + ".", cmd);
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            Debugger.error(ex, "Error registering Bukkit command alias");
        }
    }

    /**
     * Returns an anonymous {@link CommandNode} instance which is defined as
     * non-executable {@link CommandNode} used for chaining together other
     * {@link CommandNode} objects.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <T> The type of the {@link Plugin} relevant to the new node
     * @param command The value for {@link CommandNode#getName()}
     * @param plugin The relevant {@link Plugin} to this new node
     * @param onConstruct Code to execute upon construction of the object, can
     *                    be {@code null}
     * @return The newly created {@link CommandNode} object
     */
    public static <T extends Plugin> CommandNode<T> getLinkingNode(String command, T plugin, Consumer<CommandNode<T>> onConstruct) {
        Validate.notNull(command, "Command cannot be null");
        Validate.notNull(plugin, "Plugin cannot be null");
        return new CommandNode<T>(plugin) {
            
            private final Lang lang;

            {
                this.setExecutable(false);
                this.lang = Lang.createLang("This is a linking node for '" + command + "'");
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
                return CommandNode.BLANK_TAB_COMPLETE;
            }

            @Override
            public String getName() {
                return command;
            }

            @Override
            public Lang info() {
                return this.lang;
            }

        };
    }

}
