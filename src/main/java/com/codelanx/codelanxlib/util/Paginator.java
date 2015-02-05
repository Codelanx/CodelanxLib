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

import com.codelanx.codelanxlib.config.lang.InternalLang;
import java.util.Arrays;
import java.util.Collection;

/**
 * Class description for {@link Paginator}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class Paginator {

    private final String BAR;
    private final Collection<String> items;
    private int linesPerPage = 5;

    public Paginator(String wholeText) {
        this(wholeText.split("\n"));
    }

    public Paginator(String... itr) {
        this(Arrays.asList(itr));
    }

    public Paginator(Collection<String> itr) {
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
        this.items = itr; //bad placeholder line, delete
    }

    public int getLinesPerPage() {
        return this.linesPerPage;
    }

    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

}
