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
import com.codelanx.codelanxlib.util.Debugger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Instantiable MySQL connector
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public class MySQL implements SQLDataType {

    private static byte connections = 0;
    private final ConnectionPrefs prefs;
    private Connection con = null;

    /**
     * Creates a new {@link MySQL} object for use in working with a database
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param host The hostname to use
     * @param user The username to use
     * @param pass The password to use
     * @param database The database to use
     * @param port The port number to use
     */
    public MySQL(String host, String user, String pass, String database, String port) {
        if (host == null || user == null || pass == null || database == null || port == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " does not take null arguments in the constructor!");
        }
        this.prefs = new ConnectionPrefs(host, user, pass, database, port);
    }

    /**
     * Creates a new {@link MySQL} object for use in working with a database
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param pref A {@link ConnectionPrefs} for this {@link MySQL} object
     */
    public MySQL(ConnectionPrefs pref) {
        this.prefs = pref;
    }

    /**
     * Opens a connection to the SQL database. Make sure to call
     * {@link SQLDataType#close()} or wrap in try-with-resources after you are
     * finished working with the database for your segment of your code.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The Connection object
     * @throws SQLException If the connection fails to open
     */
    public Connection open() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.prefs.getUser());
        connectionProps.put("password", this.prefs.getPass());

        this.con = DriverManager.getConnection("jdbc:mysql://" + this.prefs.getHost() + ":"
                + this.prefs.getPort() + "/" + this.prefs.getDatabase(), connectionProps);
        Debugger.print("Open MySQL connections: %d", ++connections);
        return this.con;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param tablename {@inheritDoc}
     * @return {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    @Override
    public boolean checkTable(String tablename) throws SQLException {
        byte i;
        PreparedStatement stmt = this.prepare("SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = ?) AND (TABLE_NAME = ?)");
        stmt.setString(1, this.prefs.getDatabase());
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
     * {@inheritDoc}
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    @Override
    public void close() {
        SQLDataType.super.close();
        Debugger.print("Open MySQL connections: %d", --connections);
    }

    /**
     * Checks to make sure the connection is active to the MySQL server
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return true if connected, false otherwise
     */
    public boolean checkConnection() {
        boolean give;
        try (ResultSet count = query("SELECT count(*) FROM information_schema.SCHEMATA")) {
            give = count.first();
        } catch (SQLException ex) {
            give = false;
        }
        return give;
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

    /**
     * Subclass for managing connection preferences. Merely wraps data
     * in a single class with getters.
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public static class ConnectionPrefs {
        
        private final String host;
        private final String user;
        //Security reasons
        private final char[] pass;
        private final String database;
        private final String port;

        public ConnectionPrefs(String host, String user, String pass, String database, String port) {
            if (host == null || user == null || pass == null || database == null || port == null) {
                throw new IllegalArgumentException(this.getClass().getName() + " does not take null arguments in the constructor!");
            }
            this.host = host;
            this.user = user;
            this.pass = pass.toCharArray();
            this.database = database;
            this.port = port;
        }

        public String getHost() {
            return this.host;
        }

        public String getUser() {
            return this.user;
        }

        public String getPass() {
            return new String(this.pass).intern();
        }

        public String getDatabase() {
            return this.database;
        }

        public String getPort() {
            return this.port;
        }

    }
}