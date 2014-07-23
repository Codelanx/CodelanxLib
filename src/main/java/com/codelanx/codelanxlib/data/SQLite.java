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
package com.codelanx.codelanxlib.data;

import com.codelanx.codelanxlib.util.DebugUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.plugin.Plugin;

/**
 * Instantiable SQLite connector
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class SQLite implements AutoCloseable {

    protected static byte connections = 0;
    protected Connection con = null;

    /**
     * Opens a connection to the SQLite database. Make sure to call
     * SQLite.close() after you are finished working with the database for your
     * segment of your code.
     *
     * @since 1.0.0
     * @version 1.0.0
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
     * @since 1.0.0
     * @version 1.0.0
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
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param folder A {@link File} pointing to a data folder for the database
     * @param name The name of the database file
     * @return The Connection object
     * @throws SQLException If the connection fails to open
     */
    public Connection open(File folder, String name) throws SQLException {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Folder must be a non-null, existing directory!");
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            DebugUtil.error("Error loading SQLite drivers!", ex);
        }
        this.con = DriverManager.getConnection("jdbc:sqlite:" + folder.getAbsolutePath() + File.separatorChar + name + ".db");
        DebugUtil.print("Open SQLite connections: %d", ++connections);
        return this.con;
    }

    /**
     * Checks if a table exists within the set database
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param tablename Name of the table to check for
     * @return true if exists, false otherwise
     * @throws SQLException The query on the database fails
     */
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
     * Executes a query, but does not update any information nor lock the
     * database
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param query The string query to execute
     * @return A ResultSet from the query
     * @throws SQLException The connection cannot be established
     */
    public ResultSet query(String query) throws SQLException {
        Statement stmt = this.con.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     * Executes a query that can change values, and will lock the database for
     * the duration of the query
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param query The string query to execute
     * @return 0 for no returned results, or the number of returned rows
     * @throws SQLException The connection cannot be established
     */
    public synchronized int update(String query) throws SQLException {
        Statement stmt = this.con.createStatement();
        return stmt.executeUpdate(query);
    }

    /**
     * Returns a {@link PreparedStatement} in which you can easily protect
     * against SQL injection attacks.
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param stmt The string to prepare
     * @return A {@link PreparedStatement} from the passed string
     * @throws SQLException The connection cannot be established
     */
    public PreparedStatement prepare(String stmt) throws SQLException {
        return this.con.prepareStatement(stmt);
    }

    /**
     * Closes the SQLite connection. Must be open first.
     *
     * @since 1.0.0
     * @version 1.0.0
     */
    @Override
    public void close() {
        try {
            this.con.close();
            DebugUtil.print("Open SQLite connections: %d", --connections);
        } catch (SQLException ex) {
            DebugUtil.error("Error closing SQLite connection!", ex);
        }
    }

    public void setAutoCommit(boolean set) throws SQLException {
        if (this.con != null) {
            this.con.setAutoCommit(set);
        }
    }

    public void commit() throws SQLException {
        if (this.con != null) {
            this.con.commit();
        }
    }

    public void rollback() throws SQLException {
        if (this.con != null) {
            this.con.commit();
        }
    }

    public Connection getConnection() {
        return this.con;
    }

}