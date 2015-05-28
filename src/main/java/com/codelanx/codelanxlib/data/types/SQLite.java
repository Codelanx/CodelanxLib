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
    private boolean errors = true;

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * {@link SQLite#close()} after you are finished working with the database
     * for your segment of your code.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param plugin The {@link Plugin} with the data folder to use
     * @return The established {@link Connection}
     * @throws SQLException If the connection fails to open
     */
    public Connection open(Plugin plugin) throws SQLException {
        return this.open(plugin, "database");
    }

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * {@link SQLite#close()} after you are finished working with the database
     * for your segment of your code.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param plugin The {@link Plugin} with the data folder to use
     * @param name The name of the database file
     * @return The established {@link Connection}
     * @throws SQLException If the connection fails to open
     */
    public Connection open(Plugin plugin, String name) throws SQLException {
        return this.open(plugin.getDataFolder(), name);
    }

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * {@link SQLite#close()} after you are finished working with the database
     * for your segment of your code.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param folder A {@link File} pointing to a data folder for the database
     * @param name The name of the database file
     * @return The established {@link Connection}
     * @throws SQLException If the connection fails to open
     */
    public Connection open(File folder, String name) throws SQLException {
        this.open(new File(folder, name + ".db"));
        return this.con;
    }

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * {@link SQLite#close()} after you are finished working with the database
     * for your segment of your code.
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param database A {@link File} that represents the database location
     * @return The established {@link Connection}
     * @throws SQLException If the connection fails to open
     */
    public Connection open(File database) throws SQLException {
        if (database == null || database.isDirectory() || !database.exists()) {
            throw new IllegalArgumentException("Folder must be a non-null, existing file!");
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Debugger.error(ex, "Error loading SQLite drivers");
        }
        this.con = DriverManager.getConnection("jdbc:sqlite:" + database.getAbsolutePath());
        Debugger.print("Open SQLite connections: %d", ++connections);
        return this.con;
    }

    /**
     * Checks if a table exists within the set database
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param tableName Name of the table to check for
     * @return true if exists, false otherwise
     */
    @Override
    public boolean checkTable(String tableName) {
        return 1 == this.query(rs -> { return rs.next() ? rs.getByte(1) : 0; },
                "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?", tableName).getResponse();
    }

    /**
     * {@inheritDoc}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param tableName {@inheritDoc}
     * @param columnName {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkColumn(String tableName, String columnName) {
        return 1 == this.query(rs -> { return rs.next() ? rs.getByte(1) : 0; },
                "SELECT COUNT(*) FROM sqlite_master WHERE type='index' AND name=?", columnName).getResponse();
    }

    @Override
    public void setErrorOutput(boolean errors) {
        this.errors = errors;
    }

    @Override
    public boolean isSendingErrorOutput() {
        return this.errors;
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
