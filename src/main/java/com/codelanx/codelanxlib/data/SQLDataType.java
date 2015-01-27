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
package com.codelanx.codelanxlib.data;

import com.codelanx.codelanxlib.util.Debugger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class description for {@link SQLDataType}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public interface SQLDataType extends DataType, AutoCloseable {

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
    public boolean checkTable(String tablename) throws SQLException;

    /**
     * Executes a query, but does not update any information
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param query The string query to execute
     * @return A ResultSet from the query
     * @throws SQLException The connection cannot be established
     */
    default public ResultSet query(String query) throws SQLException {
        return this.getConnection().createStatement().executeQuery(query);
    }

    /**
     * Executes a query that can change values
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param query The string query to execute
     * @return 0 for no returned results, or the number of returned rows
     * @throws SQLException The connection cannot be established
     */
    default public int update(String query) throws SQLException {
        return this.getConnection().createStatement().executeUpdate(query);
    }

    /**
     * Returns a {@link PreparedStatement} in which you can easily protect
     * against SQL injection attacks.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param stmt The string to prepare
     * @return A {@link PreparedStatement} from the passed string
     * @throws SQLException The connection cannot be established
     */
    default public PreparedStatement prepare(String stmt) throws SQLException {
        return this.getConnection().prepareStatement(stmt);
    }

    /**
     * Returns whether or not this connection automatically commits changes
     * to the database.
     *
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return the current state of this connection's auto-commit mode 
     * @throws SQLException database access error or closed connection
     */
    default public boolean isAutoCommit() throws SQLException {
        return this.getConnection().getAutoCommit();
    }

    /**
     * Sets whether or not to automatically commit changes to the database. If
     * disabled, transactions will be grouped and not executed except from a
     * manual call to {@link SQLDataType#commit()} or
     * {@link SQLDataType#rollback()}.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param set {@code true} to enable, {@code false} to disable
     * @throws SQLException The connection cannot be established,
     *                      or an access error occurred
     */
    default public void setAutoCommit(boolean set) throws SQLException {
        if (this.getConnection() != null) {
            this.getConnection().setAutoCommit(set);
        }
    }

    /**
     * Pushes any queued transactions to the database
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @throws SQLException The connection cannot be established, an access
     *                      error occurred, or auto-commit is enabled
     */
    default public void commit() throws SQLException {
        if (this.getConnection() != null) {
            this.getConnection().commit();
        }
    }

    /**
     * Cancel any queued transactions
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @throws SQLException The connection cannot be established, an access
     *                      error occurred, or auto-commit is enabled
     */
    default public void rollback() throws SQLException {
        if (this.getConnection() != null) {
            this.getConnection().rollback();
        }
    }

    /**
     * Returns the {@link Connection} object for ease of use in exposing more
     * internal API
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The {@link Connection} object in use by this {@link SQLDataType}
     */
    public Connection getConnection();

    @Override
    default public void close() {
        try {
            this.getConnection().close();
        } catch (SQLException ex) {
            Debugger.error(ex, "Error closing SQL connection!");
        }
    }

}
