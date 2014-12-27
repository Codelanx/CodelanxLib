/*
 * Copyright (C) 2014 Codelanx, All Rights Reserved
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
 * long with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib;

import com.codelanx.codelanxlib.command.CommandHandler;
import com.codelanx.codelanxlib.implementers.Commandable;
import com.codelanx.codelanxlib.implementers.Listening;
import com.codelanx.codelanxlib.listener.ListenerManager;
import com.codelanx.codelanxlib.serialize.*;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class description for {@link CodelanxPlugin}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 * 
 * @param <E> The implementing plugin instance
 */
public abstract class CodelanxPlugin<E extends CodelanxPlugin<E>> extends JavaPlugin implements Commandable<E>, Listening<E> {
    
    static {
        SerializationFactory.registerClasses(false,
                SerializationFactory.getNativeSerializables());
    }

    private Class cnfg; //Purposefully raw-typed
    private String cmd;
    protected CommandHandler<E> commands;
    protected ListenerManager<E> listener;
    
    public CodelanxPlugin(String command) {
        this.cmd = command;
    }

    @Override
    public void onLoad() {
        SerializationFactory.registerToBukkit();
    }

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Loading configuration...");
        this.cnfg = null;
        
        this.getLogger().log(Level.INFO, "Enabling listeners...");
        this.listener = new ListenerManager<>((E) this);
        
        this.getLogger().log(Level.INFO, "Enabling command handler...");
        this.commands = new CommandHandler<>((E) this, this.cmd);
        this.cmd = null;
    }

    @Override
    public void onDisable() {
        this.listener.cleanup();
    }

    @Override
    public CommandHandler<E> getCommandHandler() {
        return this.commands;
    }

    @Override
    public ListenerManager<E> getListenerManager() {
        return this.listener;
    }

}
