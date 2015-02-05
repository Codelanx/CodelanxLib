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
import com.codelanx.codelanxlib.config.lang.Lang;
import com.codelanx.codelanxlib.config.lang.InternalLang;
import com.codelanx.codelanxlib.util.Paginator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Displays help information
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 *
 * @param <E> Represents a {@link Plugin} that implements the
 * {@link Commandable} interface
 */
public final class HelpCommand<E extends Plugin & Commandable<E>> extends SubCommand<E> {

    private Paginator pages;
    private long nextCache;
    private int factor = 5;

    /**
     * {@link HelpCommand} constructor. Initializes the
     * {@link HelpCommand#BAR} field.
     *
     * @since 0.0.1
     * @version 0.0.1
     *
     * @param plugin {@inheritDoc}
     */
    public HelpCommand(E plugin) {
        super(plugin);
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

    private void checkCache() {
        if (this.pages == null || System.currentTimeMillis() >= this.nextCache) {
            this.setNextCache();
        }
    }

    private void setNextCache() {
        this.nextCache = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        this.pages = this.newPaginator();
    }

    private Paginator newPaginator() {
        List<SubCommand<E>> cmds = new ArrayList<>(this.plugin.getCommandHandler().getCommands());
        Collections.sort(cmds);
        String title = InternalLang.COMMAND_HELP_TITLEFORMAT.format(
                this.plugin.getCommandHandler().getMainCommand());
        Paginator back = new Paginator(title, this.factor,
                cmds.stream().map(this::toHelpInfo).collect(Collectors.toList()));
        return back;
    }

    private String toHelpInfo(SubCommand<E> cmd) {
        return InternalLang.COMMAND_HELP_ITEMFORMAT.format(cmd.getUsage(), cmd.info());
    }

    public void setItemsPerPage(int factor) {
        this.factor = factor;
    }

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