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
package com.codelanx.codelanxlib.util;

import com.codelanx.codelanxlib.internal.InternalLang;
import com.codelanx.codelanxlib.config.Lang;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class description for {@link Paginator}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class Paginator {

    private final String BAR;
    private final List<String> pages = new ArrayList<>();

    public Paginator(String title, int itemsPerPage, String wholeText) {
        this(title, itemsPerPage, wholeText.split("\n"));
    }

    public Paginator(String title, int itemsPerPage, String... itr) {
        this(title, itemsPerPage, Arrays.asList(itr));
    }

    public Paginator(String title, int itemsPerPage, List<String> content) {
        String s = InternalLang.PAGINATOR_BARCHAR.format();
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
        //divide into pages
        int pageCount = content.size() / itemsPerPage + ((content.size() % itemsPerPage) == 0 ? 0 : 1);
        for (int i = 0; i < pageCount; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.formatTitle(title,
                InternalLang.PAGINATOR_BARCOLOR.format(),
                InternalLang.PAGINATOR_TITLECOLOR.format()));
            sb.append('\n');
            sb.append(InternalLang.PAGINATOR_PAGEFORMAT.format(i + 1, pageCount));
            sb.append('\n');
            int stop = (i + 1) * itemsPerPage;
            for (int w = i * itemsPerPage; w < stop; w++) {
                sb.append(content.get(w)).append('\n');
            }
            sb.append(this.formatFooter(InternalLang.PAGINATOR_BARCOLOR.format()));
            sb.append('\n');
            this.pages.add(sb.toString());
        }
    }

    /**
     * Returns the appropriately formatted page for this {@link Paginator}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param page The page to retrieve
     * @return The page in the form of a string 
     */
    public String getPage(int page) {
        page--;
        if (page < 0 || page > this.pages.size()) {
            throw new IndexOutOfBoundsException("Page " + ++page + " does not exist!");
        }
        return this.pages.get(page);
    }

    /**
     * Formats the title-bar for displaying help information
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param title The title to use
     * @param barcolor The color of the bar (ref: {@link ChatColor})
     * @param titlecolor The color of the title (ref: {@link ChatColor})
     * @return A formatted header
     */
    private String formatTitle(String title, String barcolor, String titlecolor) {
        String line = barcolor + this.BAR;
        int pivot = line.length() / 2;
        String center = InternalLang.PAGINATOR_TITLECONTAINER.format(barcolor, titlecolor, title);
        return Lang.__(line.substring(0, pivot - center.length() / 2)
                + center
                + line.substring(0, pivot - center.length() / 2));
    }

    /**
     * Formats the footer-bar of the help information.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param barcolor The color of the footer-bar
     * @return A formatted footer
     */
    private String formatFooter(String barcolor) {
        String back = barcolor + this.BAR;
        return Lang.__(back.substring(0, back.length() - 11));
    }

}
