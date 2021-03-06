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
package com.codelanx.codelanxlib.util;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents a {@link CommandSender} which outputs to a {@link Logger}. This is
 * specifically made with the intention of unit testing without running a Bukkit
 * environment
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class RuntimeCommandSender implements CommandSender {

    private static final Logger LOG = Logger.getLogger(RuntimeCommandSender.class.getName());

    @Override
    public void sendMessage(String message) {
        LOG.info(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String s : messages) {
            LOG.info(s);
        }
    }

    @Override
    public Server getServer() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is not attached to any server instance");
    }

    @Override
    public String getName() {
        return "RUNTIME";
    }

    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot have permission attachments");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot have permission attachments");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot have permission attachments");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot have permission attachments");
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {}

    @Override
    public void recalculatePermissions() {}

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot have permission attachments");
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {}

}
