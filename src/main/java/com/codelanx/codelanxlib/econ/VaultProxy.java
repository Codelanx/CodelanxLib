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

import com.codelanx.codelanxlib.util.Debugger;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class description for {@link VaultProxy}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public final class VaultProxy implements InvocationHandler {

    private final static Set<CEconomy> econs = new LinkedHashSet<>();
    private final static Set<String> blackListed = new LinkedHashSet<String>() {{
        addAll(Arrays.asList(
                "getBalance",
                "bankBalance",
                "bankHas",
                "currencyNameSingular",
                "currencyNamePlural",
                "format",
                "fractionalDigits",
                "getBanks",
                "getName",
                "has",
                "hasAccount",
                "hasBankSupport",
                "isBankMember",
                "isBankOwner",
                "isEnabled"
        ));
    }};
    private final Economy econ;

    private VaultProxy(Economy econ) {
        this.econ = econ;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object back = m.invoke(this.econ, args);
        if (args != null && args.length > 0 && !VaultProxy.blackListed.contains(m.getName())) {
            OfflinePlayer o;
            if (args[0] instanceof String) {
                String s = (String) args[0];
                o = Bukkit.getOfflinePlayer(s);
            } else if (args[0] instanceof OfflinePlayer) {
                o = (OfflinePlayer) args[0];
            } else {
                return back;
            }
            if (o.isOnline()) {
                Player p = (Player) o;
                VaultProxy.econs.forEach(e -> {
                    e.setChanged();
                    e.notifyObservers(new EconomyChangePacket(p, e.getBalance(p)));
                });
            }
        }
        return back;
    }

    public static void proxyVault() {
        try {
            Server server = Bukkit.getServer();
            RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                Debugger.print(Level.SEVERE, "No economy found, will not proxy Vault!");
                return;
            }
            Economy e = rsp.getProvider();
            if (e == null || Proxy.isProxyClass(e.getClass())) {
                Debugger.print(Level.SEVERE, "Error proxying vault economy! No responsive updating");
                return;
            }
            ClassLoader l;
            l = Economy.class.getClassLoader();
            Vault v = JavaPlugin.getPlugin(Vault.class);
            if (l == null) {
                Method m = v.getClass().getMethod("getClassLoader");
                m.setAccessible(true);
                l = (ClassLoader) m.invoke(v);
                if (l == null) {
                    Debugger.print(Level.SEVERE, "Unable to retrieve economy classloader!");
                    return;
                }
            }
            server.getServicesManager().unregister(Economy.class);
            server.getServicesManager().register(Economy.class,
                    (Economy) Proxy.newProxyInstance(l, new Class[]{Economy.class}, new VaultProxy(e)),
                    v,
                    ServicePriority.Normal);
        } catch (NoSuchMethodException
                | SecurityException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException ex) {
            Debugger.error(ex, "Error proxying vault economy class!");
        }
    }

    public static <T extends CEconomy> boolean register(T econ) {
        return VaultProxy.econs.add(econ);
    }

}
