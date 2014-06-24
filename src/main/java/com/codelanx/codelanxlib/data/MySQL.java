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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Instantiable MySQL connector
 * 
 * TODO: Create a way to allow multiple connections to different databases
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class MySQL implements AutoCloseable {

    private static byte connections = 0;
    private static String HOST = "";
    private static String USER = "";
    private static String PASS = "";
    private static String DATABASE = "";
    private static String PORT = "";
    private Connection con = null;

    /**
     * Sets the static variables to use in future MySQL connections
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param host The hostname to use
     * @param user The username to use
     * @param pass The password to use
     * @param database The database to use
     * @param port The port number to use
     */
    public MySQL(String host, String user, String pass, String database, String port) {
        MySQL.HOST = host;
        MySQL.USER = user;
        MySQL.PASS = pass;
        MySQL.DATABASE = database;
        MySQL.PORT = port;
    }

    /**
     * Opens a connection to the MySQL database. Make sure to call MySQL.close()
     * after you are finished working with the database for your segment of your
     * code.
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return The Connection object
     * @throws SQLException
     */
    public Connection open() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", MySQL.USER);
        connectionProps.put("password", MySQL.PASS);

        this.con = DriverManager.getConnection("jdbc:mysql://" + MySQL.HOST + ":" + MySQL.PORT + "/" + MySQL.DATABASE, connectionProps);
        DebugUtil.print("Open MySQL connections: %d", ++connections);
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
     * @throws SQLException
     */
    public boolean checkTable(String tablename) throws SQLException {
        byte i;
        PreparedStatement stmt = this.prepare("SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = ?) AND (TABLE_NAME = ?)");
        stmt.setString(1, MySQL.DATABASE);
        stmt.setString(2, tablename);
        try (ResultSet count = stmt.executeQuery()) {
            i = 0;
            if (count.next()) {
                i = count.getByte(1);
            }
        }
        return i == 1;
    }

    /**
     * Executes a query, but does not update any information
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param query The string query to execute
     * @return A ResultSet from the query
     * @throws SQLException
     */
    public ResultSet query(String query) throws SQLException {
        Statement stmt = this.con.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     * Executes a query that can change values
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param query The string query to execute
     * @return 0 for no returned results, or the number of returned rows
     * @throws SQLException
     */
    public int update(String query) throws SQLException {
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
     * @return A {@link PreparedStatment} from the passed string
     * @throws SQLException 
     */
    public PreparedStatement prepare(String stmt) throws SQLException {
        return this.con.prepareStatement(stmt);
    }

    /**
     * Closes the MySQL connection. Must be open first.
     *
     * @since 1.0.0
     * @version 1.0.0
     */
    public void close() {
        try {
            this.con.close();
            DebugUtil.print("Open SQLite connections: %d", --connections);
        } catch (SQLException ex) {
            DebugUtil.error("Error closing SQLite connection!", ex);
        }
    }

    /**
     * Checks to make sure the connection is active to the MySQL server
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @return true if connected, false otherwise
     * @throws SQLException
     */
    public boolean checkConnection() throws SQLException {
        boolean give;
        try (ResultSet count = query("SELECT count(*) FROM information_schema.SCHEMATA")) {
            give = count.first();
        }
        return give;
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