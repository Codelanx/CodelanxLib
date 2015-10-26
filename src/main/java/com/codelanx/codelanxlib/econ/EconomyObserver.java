package com.codelanx.codelanxlib.econ;

import net.milkbowl.vault.economy.Economy;

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
