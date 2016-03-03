/*
 * Copyright (C) 2016 Codelanx, All Rights Reserved
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
package com.codelanx.codelanxlib.econ;

import java.util.Observable;
import java.util.Observer;

/**
 * Provides a type to {@link Observer} for ease-of-use
 *
 * @since 0.2.0
 * @author 1Rogue
 * @version 0.2.0
 */
@FunctionalInterface
public interface EconomyObserver extends Observer {

    /**
     * Called whenever the economy state changes
     *
     * @since 0.2.0
     * @version 0.2.0
     *
     * @param o The {@link CEconomy} object being observed
     * @param arg The {@link EconomyChangePacket} of the economy change
     */
    void update(CEconomy o, EconomyChangePacket arg);

    /**
     * {@inheritDoc}
     *
     * @since 0.2.0
     * @version 0.2.0
     *
     * @param o {@inheritDoc}
     * @param arg {@inheritDoc}
     */
    default void update(Observable o, Object arg) {
        if (!(o instanceof CEconomy) || !(arg instanceof EconomyChangePacket)) {
            return;
        }
        update((CEconomy) o, (EconomyChangePacket) arg);
    }
}
