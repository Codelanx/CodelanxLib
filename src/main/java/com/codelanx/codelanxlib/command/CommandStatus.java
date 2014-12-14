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
package com.codelanx.codelanxlib.command;

import com.codelanx.codelanxlib.command.neu.Token;
import com.codelanx.codelanxlib.config.lang.InternalLang;
import com.codelanx.codelanxlib.config.lang.Lang;
import org.bukkit.command.CommandSender;

/**
 * Class description for {@link CommandStatus}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public enum CommandStatus {
    
    SUCCESS,
    FAILED,
    BAD_ARGS,
    /** Use if your {@link Token} is not meant to be an endpoint */
    UNSUPPORTED,
    NO_PERMISSION;

    public void handle(CommandSender sender, Lang format, SubCommand<?> tok) {
        switch (this) {
            case FAILED:
                Lang.sendMessage(sender, format, InternalLang.COMMAND_STATUS_FAILED);
                break;
            case BAD_ARGS:
                Lang.sendMessage(sender, format, InternalLang.COMMAND_STATUS_USAGE, tok.getUsage());
                Lang.sendMessage(sender, format, tok.info());
                break;
            case NO_PERMISSION:
                Lang.sendMessage(sender, format, InternalLang.COMMAND_STATUS_NOPERM);
                break;
            case UNSUPPORTED:
                Lang.sendMessage(sender, format, InternalLang.COMMAND_STATUS_USAGE);
                break;
        }
    }
    
}
