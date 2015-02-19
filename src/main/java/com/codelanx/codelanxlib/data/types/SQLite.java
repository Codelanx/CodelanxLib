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
package com.codelanx.codelanxlib.data.types;

import com.codelanx.codelanxlib.data.SQLDataType;
import com.codelanx.codelanxlib.logging.Debugger;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.plugin.Plugin;

/**
 * Instantiable SQLite connector
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class SQLite implements SQLDataType {

    /** The number of connections in use by this data type */
    protected static byte connections = 0;
    /** The {@link Connection} object */
    protected Connection con = null;

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * SQLite.close() after you are finished working with the database for your
     * segment of your code.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param plugin The {@link Plugin} with the data folder to use
     * @return The Connection object
     * @throws SQLException If the connection fails to open
     */
    public Connection open(Plugin plugin) throws SQLException {
        return this.open(plugin, "database");
    }

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * SQLite.close() after you are finished working with the database for your
     * segment of your code.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param plugin The {@link Plugin} with the data folder to use
     * @param name The name of the database file
     * @return The Connection object
     * @throws SQLException If the connection fails to open
     */
    public Connection open(Plugin plugin, String name) throws SQLException {
        return this.open(plugin.getDataFolder(), name);
    }

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * SQLite.close() after you are finished working with the database for your
     * segment of your code.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param folder A {@link File} pointing to a data folder for the database
     * @param name The name of the database file
     * @return The Connection object
     * @throws SQLException If the connection fails to open
     */
    public Connection open(File folder, String name) throws SQLException {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Folder must be a non-null, existing directory");
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Debugger.error(ex, "Error loading SQLite drivers");
        }
        this.con = DriverManager.getConnection("jdbc:sqlite:" + folder.getAbsolutePath() + File.separatorChar + name + ".db");
        Debugger.print("Open SQLite connections: %d", ++connections);
        return this.con;
    }

    /**
     * Checks if a table exists within the set database
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param tablename Name of the table to check for
     * @return true if exists, false otherwise
     * @throws SQLException The query on the database fails
     */
    @Override
    public boolean checkTable(String tablename) throws SQLException {
        byte i;
        PreparedStatement stmt = this.prepare("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?");
        stmt.setString(1, tablename);
        try (ResultSet count = stmt.executeQuery()) {
            i = 0;
            if (count.next()) {
                i = count.getByte(1);
            }
        }
        return i == 1;
    }

    /**
     * Closes the SQLite connection. Must be open first.
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    @Override
    public void close() {
        SQLDataType.super.close();
        Debugger.print("Open SQLite connections: %d", --connections);
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return {@inheritDoc}
     */
    @Override
    public Connection getConnection() {
        return this.con;
    }

}
