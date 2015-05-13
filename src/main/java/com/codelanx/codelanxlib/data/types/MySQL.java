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

import com.codelanx.codelanxlib.config.Config;
import com.codelanx.codelanxlib.data.SQLDataType;
import com.codelanx.codelanxlib.logging.Debugger;
import com.codelanx.codelanxlib.util.cache.Cache;
import com.codelanx.codelanxlib.util.Databases;
import java.sql.Connection;
import java.sql.DriverManager;
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
    private boolean errors = true;
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
            throw new IllegalArgumentException(this.getClass().getName() + " does not take null arguments in the constructor");
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
     * @param tableName {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean checkTable(String tableName) {
        return 1 == this.query(rs -> { return rs.next() ? rs.getByte(1) : 0; },
                "SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = ?) AND (TABLE_NAME = ?)", this.prefs.getDatabase(), tableName).getResponse();
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
                "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                this.prefs.getDatabase(), tableName, columnName).getResponse();
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
        try {
            ResultSet rs = this.prepare("SELECT count(*) FROM information_schema.SCHEMATA").executeQuery();
            return rs.first();
        } catch (SQLException ex) {
            return false;
        }
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
     * Returns a new cached MySQL connection, which will automatically renew
     * itself at the specified interval via {@code keepAliveMS}, in
     * milliseconds. Keep in mind, due to the way {@link Cache} works,
     * until {@link Cache#get()} is called a connection may remain open past
     * the specified keep-alive
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param prefs The {@link ConnectionPrefs} relevant to the connection
     * @param keepAliveMS The time in milliseconds to keep a connection open
     * @return A new self-renewing {@link Cache} object with an instance of this
     */
    public static Cache<MySQL> newCache(ConnectionPrefs prefs, long keepAliveMS) {
        return new Cache<MySQL>(keepAliveMS) {
            @Override
            protected void update() {
                if (this.getCurrentValue() != null && this.getCurrentValue().checkConnection()) {
                    this.getCurrentValue().close();
                }
                MySQL set = new MySQL(prefs);
                try {
                    set.setAutoCommit(true);
                    set.open();
                } catch (SQLException ex) {
                    Debugger.error(ex, "Error opening SQL connection!: %s", Databases.simpleErrorOutput(ex));
                }
                this.setCurrentValue(set);
            }
        };
    }

    @Override
    public void toggleErrorOutput(boolean errors) {
        this.errors = errors;
    }

    @Override
    public boolean isSendingErrorOutput() {
        return this.errors;
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

        /**
         * Class constructor. Assigns the passed variables to fields
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param host The host name for this MySQL connection
         * @param user The user to utilize when connecting
         * @param pass The password for the user
         * @param database The database to use in the cluster
         * @param port The port of the database
         */
        public ConnectionPrefs(String host, String user, String pass, String database, String port) {
            this.host = host;
            this.user = user;
            this.pass = pass.toCharArray();
            this.database = database;
            this.port = port;
        }

        /**
         * Class constructor. Takes {@link Config Configs} that are string-types
         * (The {@code port} parameter can be an int or string) and retrieves
         * the appropriate values for them
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param host The host name for this MySQL connection
         * @param user The user to utilize when connecting
         * @param pass The password for the user
         * @param database The database to use in the cluster
         * @param port The port of the database
         */
        public ConnectionPrefs(Config host, Config user, Config pass, Config database, Config port) {
            this.host = host.as(String.class);
            this.user = user.as(String.class);
            this.pass = pass.as(String.class).toCharArray();
            this.database = database.as(String.class);
            this.port = port.as(String.class);
        }

        /**
         * Returns the host in use by these {@link ConnectionPrefs}
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The host name
         */
        public String getHost() {
            return this.host;
        }

        /**
         * Returns the username to use when connecting
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The username to use 
         */
        public String getUser() {
            return this.user;
        }

        /**
         * Returns the password to use when connecting with the user. Note the
         * password is stored internally as a char array, so the string is
         * freshly constructed and interned upon every call to this method
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The password to use
         */
        public String getPass() {
            return new String(this.pass).intern();
        }

        /**
         * Returns the database to use in the MySQL cluster
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The database to use 
         */
        public String getDatabase() {
            return this.database;
        }

        /**
         * Returns the port to use for the MySQL connection. This is stored as
         * a string for the most part because it is not needed as an int type,
         * since it is concatenated into a connection string regardless
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return The port to use 
         */
        public String getPort() {
            return this.port;
        }

    }

}