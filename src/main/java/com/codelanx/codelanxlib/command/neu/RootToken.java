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
package com.codelanx.codelanxlib.command.neu;

import com.codelanx.codelanxlib.command.CommandStatus;
import com.codelanx.codelanxlib.config.lang.Lang;
import com.codelanx.codelanxlib.implementers.Commandable;
import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Class description for {@link RootToken}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 * 
 * @param <T> Represents a {@link Plugin} that implements the
 *            {@link Commandable} interface
 */
public class RootToken<T extends Plugin & Commandable<T>> extends Token<T> {

    protected final String command;

    public RootToken(T plugin, String command) {
        super(plugin);
        this.command = command;
    }

    @Override
    public CommandStatus determine(CommandSender sender, String permission, String... args) {
        permission = this.plugin.getName().toLowerCase() + ".cmd";
        if (args.length > 0) {
            Token<T> sub = this.subtokens.get(args[0]);
            if (sub != null) {
                return sub.determine(sender, permission, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return this.execute(sender, args);
    }

    @Override
    public CommandStatus execute(CommandSender sender, String... args) {
        return this.subtokens.get("help").determine(sender, null, args);
    }

    @Override
    public String getName() {
        return this.command;
    }

    @Override
    public Lang info() {
        throw new UnsupportedOperationException("Method circumvented - Root Token cannot be called!");
    }

}
