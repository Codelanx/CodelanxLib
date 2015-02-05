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

import com.codelanx.codelanxlib.implementers.Commandable;
import com.codelanx.codelanxlib.internal.InternalLang;
import com.codelanx.codelanxlib.util.Paginator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Displays help information.
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.1.0
 *
 * @param <E> Represents a {@link Plugin} that implements the
 * {@link Commandable} interface
 */
public final class HelpCommand<E extends Plugin> extends SubCommand<E> {

    /** Internal {@link Paginator} cache, used to pre-render and output pages */
    private Paginator pages;
    /** The time in milliseconds that a cache should be remade */
    private long nextCache;
    /** The number of commands to show per page */
    private int factor = 5;

    /**
     * {@link HelpCommand} constructor. Initializes the {@link HelpCommand#BAR}
     * field.
     *
     * @since 0.0.1
     * @version 0.1.0
     *
     * @param plugin {@inheritDoc}
     * @param handler {@inheritDoc}
     */
    public HelpCommand(E plugin, CommandHandler<E> handler) {
        super(plugin, handler);
    }

    /**
     * Displays help information about the various commands registered to and
     * available from the {@link CommandHandler} instance that instantiated this
     * class.
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
        this.checkCache();
        sender.sendMessage(this.pages.getPage(select));
        return CommandStatus.SUCCESS;
    }

    /**
     * Checks if the {@link Paginator} cache needs to be reset
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    private void checkCache() {
        if (this.pages == null || System.currentTimeMillis() >= this.nextCache) {
            this.setNextCache();
        }
    }

    /**
     * Resets the {@link Paginator} cache
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    private void setNextCache() {
        this.nextCache = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        this.pages = this.newPaginator();
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
        List<SubCommand<E>> cmds = new ArrayList<>(this.handler.getCommands());
        Collections.sort(cmds);
        String title = InternalLang.COMMAND_HELP_TITLEFORMAT.format(
                this.handler.getMainCommand());
        Paginator back = new Paginator(title, this.factor,
                cmds.stream().map(this::toHelpInfo).collect(Collectors.toList()));
        return back;
    }

    /**
     * Converts a {@link SubCommand} into a readable format for help output
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param cmd The {@link SubCommand} to convert
     * @return The human-readable output of the command information
     */
    private String toHelpInfo(SubCommand<?> cmd) {
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
        this.setNextCache();
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
    public String getUsage() {
        return super.getUsage() + " [page-number]";
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

}
