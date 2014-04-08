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

import com.codelanx.codelanxlib.lang.Lang;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * Displays help information
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class HelpCommand extends SubCommand {

    private final String BAR;

    public HelpCommand(CommandHandler handler) {
        super(handler);
        String s = Lang.COMMAND_HELP_BARCHAR.format();
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
        int page = 1;
        int num = 0;
        String title;
        List<String> last = new ArrayList<>();

        List<HelpItem> help = this.getOrderedCommands(sender,
                this.handler.getCommands());
        int pages = 0;
        for (HelpItem h : help) {
            pages += ((h.getOutputs().size() - 1) / factor) + 1;
        }
        if (select > pages) {
            select = pages;
        }
        if (select < 1) {
            select = 1;
        }

        sender.sendMessage(Lang.__(this.showHelp(
                this.getView(help, select, factor), pages)));

        return true;
    }

    private List<HelpItem> getOrderedCommands(CommandSender sender,
                                              Collection<SubCommand> cmds) {
        List<HelpItem> back = new ArrayList<>();
        List<SubCommand> vals = new ArrayList<>(cmds);
        Collections.sort(vals, new Comparator<SubCommand>() {

            @Override
            public int compare(SubCommand o1, SubCommand o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        List<String> temp = new ArrayList<>();
        for (SubCommand cmd : vals) {
            if (this.handler.hasPermission(sender, cmd)) {
                temp.add(Lang.COMMAND_HELP_ITEMFORMAT.format(cmd.getUsage(), cmd.info()));
            }
        }
        back.add(new HelpItem(temp, this.handler.getMainCommand(), 0));
        return back;
    }

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

    private String showHelp(HelpItem item, int pages) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.formatTitle(Lang.COMMAND_HELP_TITLEFORMAT.format(item.getTitle()),
                Lang.COMMAND_HELP_BARCOLOR.format(),
                Lang.COMMAND_HELP_TITLECOLOR.format()));
        sb.append('\n');
        sb.append(Lang.COMMAND_HELP_PAGEFORMAT.format(item.getPage(), pages));
        sb.append('\n');
        for (String s : item.getOutputs()) {
            sb.append(s).append('\n');
        }
        sb.append(this.formatFooter("&f"));
        sb.append('\n');
        return sb.toString();
    }

    private String formatTitle(String title, String barcolor, String titlecolor) {
        String line = barcolor + this.BAR;
        int pivot = line.length() / 2;
        String center = Lang.COMMAND_HELP_TITLECONTAINER.format(barcolor, titlecolor, title);
        return Lang.__(line.substring(0, pivot - center.length() / 2)
                       + center
                       + line.substring(0, pivot - center.length() / 2));
    }

    private String formatFooter(String barcolor) {
        String back = barcolor + this.BAR;
        return Lang.__(back.substring(0, back.length() - 11));
    }

    @Override
    public String getUsage() {
        return super.getUsage() + " [page-number]";
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String info() {
        return Lang.COMMAND_HELP_INFO.format();
    }

}

class HelpItem {

    private final List<String> outputs;
    private final String title;
    private final int page;

    public HelpItem(List<String> outputs, String title, int page) {
        this.outputs = outputs;
        this.title = title;
        this.page = page;
    }

    public List<String> getOutputs() {
        return this.outputs;
    }

    public int getPage() {
        return this.page;
    }

    public String getTitle() {
        return this.title;
    }

}