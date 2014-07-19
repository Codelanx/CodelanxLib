/*
 * Copyright (C) 2014 CodeLanx , All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2014 or as published
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
import com.codelanx.codelanxlib.lang.InternalLang;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Displays help information
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 *
 * @param <E> Represents a {@link Plugin} that implements the
 * {@link Commandable} interface
 */
public final class HelpCommand<E extends Plugin & Commandable<E>> extends SubCommand<E> {

    /** The bar to use for encapsulating help info */
    private final String BAR;

    /**
     * {@link HelpCommand} constructor. Initializes the
     * {@link HelpCommand#BAR} field.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param plugin {@inheritDoc}
     */
    public HelpCommand(E plugin) {
        super(plugin);
        String s = InternalLang.COMMAND_HELP_BARCHAR.format();
        if (s.isEmpty()) {
            this.BAR = "------------------------------"
                    + "------------------------------";
        } else {
            char[] barr = new char[60];
            char c = s.toCharArray()[0];
            for (int i = barr.length - 1; i >= 0; i--) {
                barr[i] = c;
            }
            this.BAR = new String(barr);
        }
    }

    /**
     * Displays help information about the various commands registered to and
     * available from the {@link CommandHandler} instance that instantiated this
     * class.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param sender {@inheritDoc}
     * @param args {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }
        int select;
        try {
            select = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            return false;
        }

        int factor = 5;
        List<HelpItem> help = this.getOrderedCommands(sender,
                this.plugin.getCommandHandler().getCommands());
        int pages = 0;
        pages = help.stream().map((h)
                -> ((h.getOutputs().size() - 1) / factor) + 1)
                .reduce(pages, Integer::sum);
        if (select > pages) {
            select = pages;
        }
        if (select < 1) {
            select = 1;
        }

        sender.sendMessage(InternalLang.__(this.showHelp(
                this.getView(help, select, factor), pages)));

        return true;
    }

    /**
     * Gets s list of the enabled {@link SubCommand} objects in natural sorting
     * order.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param sender The executor of the {@link SubCommand}
     * @param cmds A {@link Collection} representing the registered
     * {@link SubCommand} objects
     * @return A list of {@link HelpItem} objects for commands
     */
    private List<HelpItem> getOrderedCommands(CommandSender sender,
            Collection<SubCommand<E>> cmds) {
        List<HelpItem> back = new ArrayList<>();
        List<SubCommand<E>> vals = new ArrayList<>(cmds);
        Collections.sort(vals, (SubCommand<E> o1, SubCommand<E> o2) -> o1.getName().compareTo(o2.getName()));
        List<String> temp = new ArrayList<>();
        vals.stream().filter((cmd)
                -> (this.plugin.getCommandHandler().hasPermission(sender, cmd))).forEach((cmd) -> {
                    temp.add(InternalLang.COMMAND_HELP_ITEMFORMAT.format(cmd.getUsage(), cmd.info()));
                });
        back.add(new HelpItem(temp, this.plugin.getCommandHandler().getMainCommand(), 0));
        return back;
    }

    /**
     * Returns a relevant {@link HelpItem} for printing out based on the
     * requested page and amount of commands to display per "help page".
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param help All the {@link HelpItem} objects
     * @param pages The number of pages
     * @param factor The number of commands to display per page
     * @return A single {@link HelpItem} to print out
     */
    private HelpItem getView(List<HelpItem> help, int pages, int factor) {
        int current = 1;
        for (HelpItem h : help) {
            List<String> out = h.getOutputs();
            while (!out.isEmpty()) {
                if (current < pages) {
                    for (int w = 0; !out.isEmpty() && w < factor; w++) {
                        out.remove(0);
                    }
                    current++;
                } else {
                    List<String> last = new ArrayList<>();
                    for (int w = 0; w < out.size() && w < factor; w++) {
                        last.add(out.get(w));
                    }
                    return new HelpItem(last, h.getTitle(), current);
                }
            }
        }
        return null;
    }

    /**
     * Formats and returns the requested help information
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param item The {@link HelpItem} to parse
     * @param pages The total number of pages
     * @return The formatted help output
     */
    private String showHelp(HelpItem item, int pages) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.formatTitle(InternalLang.COMMAND_HELP_TITLEFORMAT.format(item.getTitle()),
                InternalLang.COMMAND_HELP_BARCOLOR.format(),
                InternalLang.COMMAND_HELP_TITLECOLOR.format()));
        sb.append('\n');
        sb.append(InternalLang.COMMAND_HELP_PAGEFORMAT.format(item.getPage(), pages));
        sb.append('\n');
        item.getOutputs().stream().forEach((s) -> {
            sb.append(s).append('\n');
        });
        sb.append(this.formatFooter("&f"));
        sb.append('\n');
        return sb.toString();
    }

    /**
     * Formats the title-bar for displaying help information
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param title The title to use
     * @param barcolor The color of the bar (ref: {@link ChatColor})
     * @param titlecolor The color of the title (ref: {@link ChatColor})
     * @return A formatted header
     */
    private String formatTitle(String title, String barcolor, String titlecolor) {
        String line = barcolor + this.BAR;
        int pivot = line.length() / 2;
        String center = InternalLang.COMMAND_HELP_TITLECONTAINER.format(barcolor, titlecolor, title);
        return InternalLang.__(line.substring(0, pivot - center.length() / 2)
                + center
                + line.substring(0, pivot - center.length() / 2));
    }

    /**
     * Formats the footer-bar of the help information.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param barcolor The color of the footer-bar
     * @return A formatted footer
     */
    private String formatFooter(String barcolor) {
        String back = barcolor + this.BAR;
        return InternalLang.__(back.substring(0, back.length() - 11));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     * @version 1.0.0
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
     * @since 1.0.0
     * @version 1.0.0
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
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return {@inheritDoc}
     */
    @Override
    public String info() {
        return InternalLang.COMMAND_HELP_INFO.format();
    }

}

/**
 * Helper class for representing data used in the help menus. Simply stores
 * data to be used later
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
class HelpItem {

    private final List<String> outputs;
    private final String title;
    private final int page;

    /**
     * {@link HelpItem} constructor
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param outputs The outputs relevant to this {@link HelpItem}
     * @param title The title to use, if relevant
     * @param page The page associated with this {@link HelpItem}, if relevant
     */
    public HelpItem(List<String> outputs, String title, int page) {
        this.outputs = outputs;
        this.title = title;
        this.page = page;
    }

    /**
     * Returns the {@link List} of {@link HelpItem} objects passed upon
     * construction
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return A {@link List} of {@link HelpItem} objects
     */
    public List<String> getOutputs() {
        return this.outputs;
    }

    /**
     * Gets the page associated with this {@link HelpItem}. Will be 0 or -1 if
     * the page is irrelevant.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The relevant page 
     */
    public int getPage() {
        return this.page;
    }

    /**
     * Gets the title to use for the title-bar for this {@link HelpItem}. May
     * be null if no titlebar is associated with this item.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The title to use for the title-bar relevant to this HelpItem 
     */
    public String getTitle() {
        return this.title;
    }

}
