/*
 * Copyright (C) 2015 CodeLanx , All Rights Reserved
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
 * along with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.command;

import com.codelanx.codelanxlib.internal.InternalLang;
import com.codelanx.codelanxlib.util.cache.Cache;
import com.codelanx.codelanxlib.util.Paginator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Provides a listing of help information derived from all {@link CommandNode}
 * objects that are returned from a call to a parent's
 * {@link CommandNode#traverse()} method
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 *
 * @param <E> The {@link Plugin} that caused this class to be instantiated
 */
public final class HelpCommand<E extends Plugin> extends CommandNode<E> {

    /** Internal {@link Paginator} cache, used to pre-render and output pages */
    private final Cache<Paginator> pages = new Cache<Paginator>(TimeUnit.MINUTES.toMillis(5)) {

        @Override
        protected void update() {
            this.setCurrentValue(HelpCommand.this.newPaginator());
        }
        
    };
    /** The number of commands to show per page */
    private int factor = 5;

    /**
     * {@link HelpCommand} constructor.
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param plugin {@inheritDoc}
     */
    protected HelpCommand(E plugin) {
        super(plugin);
    }

    /**
     * Displays help information from all linked child {@link CommandNode} nodes
     * for the parent {@link CommandNode} relevant to this {@link HelpCommand}
     * instance
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param sender {@inheritDoc}
     * @param args {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public CommandStatus execute(CommandSender sender, String... args) {
        if (args.length != 1) {
            return CommandStatus.BAD_ARGS;
        }
        int select;
        try {
            select = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            return CommandStatus.BAD_ARGS;
        }
        sender.sendMessage(this.getPages().getPage(select));
        return CommandStatus.SUCCESS;
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param sender {@inheritDoc}
     * @param args {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String... args) {
        if (args.length < 1) {
            return new ArrayList<>();
        }
        int size = this.getPages().size();
        int curr;
        try {
            curr = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            return new ArrayList<>();
        }
        if (curr > size) {
            return new ArrayList<>();
        }
        List<String> back = new ArrayList<>();
        for (int i = 1; i < size; i++) { //awful, spawns too many strings
            String s = i + "";
            if (s.startsWith(args[0])) {
                back.add(s);
            }
        }
        return back;
    }

    /**
     * Returns a new {@link Paginator} instance according to the current state
     * of the {@link CommandHandler} command map and the set page factor for
     * this {@link HelpCommand}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return A new {@link Paginator} instance containing help information
     */
    private Paginator newPaginator() {
        List<CommandNode<? extends Plugin>> cmds = new ArrayList<>(this.getParent().traverse());
        Map<String, CommandNode<? extends Plugin>> aliases = new HashMap<>();
        cmds.forEach(c -> aliases.putAll(c.getAliases()));
        Collections.sort(cmds);
        String title = InternalLang.COMMAND_HELP_TITLEFORMAT.format(this.getParent().getUsage());
        List<String> out = cmds.stream().map(this::toHelpInfo).collect(Collectors.toList());
        if (!aliases.isEmpty()) {
            int blanks = this.getItemsPerPage() - (cmds.size() % this.getItemsPerPage());
            for (; blanks > 0; blanks--) {
                out.add("");
            }
            out.add(InternalLang.COMMAND_HELP_ALIASES.format());
            List<String> aliasInfo = this.aliasInfo(aliases);
            Collections.sort(aliasInfo);
            out.addAll(aliasInfo);
        }
        return new Paginator(title, this.factor, out);
    }


    /**
     * Formats a mapping of aliases to {@link CommandNode} objects
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param aliases The aliases to format
     * @return A {@link List} of the formatted aliases
     */
    private List<String> aliasInfo(Map<String, CommandNode<? extends Plugin>> aliases) {
        List<String> back = new ArrayList<>();
        aliases.entrySet().forEach(ent -> back.add(InternalLang.COMMAND_HELP_ITEMFORMAT.format(
                ent.getKey(), "Aliased from " + ent.getValue().getUsage())));
        return back;
    }

    /**
     * Converts a {@link CommandNode} into a readable format for help output
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param cmd The {@link CommandNode} to convert
     * @return The human-readable output of the command information
     */
    private String toHelpInfo(CommandNode<?> cmd) {
        return InternalLang.COMMAND_HELP_ITEMFORMAT.format(cmd.getUsage(), cmd.info());
    }

    /**
     * Sets the number of commands to show information for per page. This will
     * forcefully refresh the {@link Paginator} cache
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param factor The number of commands per page
     */
    public void setItemsPerPage(int factor) {
        this.factor = factor;
        this.pages.forceRefresh();
    }

    /**
     * Returns the number of commands displayed per page
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The number of commands shown per page
     */
    public int getItemsPerPage() {
        return this.factor;
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
    public String usage() {
        return super.usage() + " [page-number]";
    }

    /**
     * Subcommand name: "help"
     * <br><br> {@inheritDoc}
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getName() {
        return "help";
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
    public InternalLang info() {
        return InternalLang.COMMAND_HELP_INFO;
    }

    private Paginator getPages() {
        return this.pages.get();
    }

}
