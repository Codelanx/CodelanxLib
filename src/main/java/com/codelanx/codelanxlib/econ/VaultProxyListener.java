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
package com.codelanx.codelanxlib.econ;

import com.codelanx.codelanxlib.listener.ListenerManager;
import com.codelanx.codelanxlib.listener.SubListener;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.exception.Exceptions;
import com.codelanx.codelanxlib.CodelanxLib;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Listens for when Vault registers its {@link Economy} service provider and
 * replaces it with a {@link VaultProxy}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class VaultProxyListener extends SubListener<CodelanxLib> {

    /**
     * Useless, just follows contract for {@link SubListener}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param plugin The {@link CodelanxLib} plugin
     */
    public VaultProxyListener(CodelanxLib plugin) {
        super(plugin);
        Exceptions.illegalInvocation(Reflections.accessedFrom(CodelanxLib.class));
    }

    /**
     * Listens for registration of Vault's {@link Economy} class and replaces it
     * with a {@link VaultProxy}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param event The relevant {@link ServiceRegisterEvent}
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEconomy(ServiceRegisterEvent event) {
        RegisteredServiceProvider<?> rsp = event.getProvider();
        if (Economy.class.isAssignableFrom(rsp.getProvider().getClass())) {
            VaultProxy.proxyVault();
            ListenerManager.unregisterListener(VaultProxyListener.class);
        }
    }

}
