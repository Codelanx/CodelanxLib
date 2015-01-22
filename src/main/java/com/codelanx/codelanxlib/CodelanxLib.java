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
package com.codelanx.codelanxlib;

import com.codelanx.codelanxlib.serialize.*;
import com.codelanx.codelanxlib.util.Scheduler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

/**
 * Main class. Only used for reporting plugin metrics
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public class CodelanxLib extends JavaPlugin {
    
    public CodelanxLib() {
        SerializationFactory.registerClasses(false,
                SerializationFactory.getNativeSerializables());
    }

    @Override
    public void onLoad() {
        SerializationFactory.registerToBukkit();
    }

    /**
     * Reports metrics to http://mcstats.org/
     * <br><br>
     * {@inheritDoc}
     * 
     * @since 0.0.1
     * @version 0.0.1
     */
    @Override
    public void onEnable() {
        try {
            Metrics m = new Metrics(this);
            m.start();
        } catch (IOException ex) {
            Logger.getLogger(CodelanxLib.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onDisable() {
        Scheduler.cancelAllTasks();
        Scheduler.getService().shutdown();
    }

    public static CodelanxLib get() {
        return JavaPlugin.getPlugin(CodelanxLib.class);
    }

}
