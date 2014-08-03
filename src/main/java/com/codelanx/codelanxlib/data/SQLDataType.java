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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class description for {@link SQLDataType}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public interface SQLDataType extends DataType, AutoCloseable {

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
    public boolean checkTable(String tablename) throws SQLException;

    /**
     * Executes a query, but does not update any information
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param query The string query to execute
     * @return A ResultSet from the query
     * @throws SQLException The connection cannot be established
     */
    public ResultSet query(String query) throws SQLException;

    /**
     * Executes a query that can change values
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param query The string query to execute
     * @return 0 for no returned results, or the number of returned rows
     * @throws SQLException The connection cannot be established
     */
    public int update(String query) throws SQLException;

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
    public PreparedStatement prepare(String stmt) throws SQLException;
    
    public void setAutoCommit(boolean set) throws SQLException;

    public void commit() throws SQLException;
    
    public void rollback() throws SQLException;

    /**
     * Returns the {@link Connection} object for ease of use in exposing more
     * internal API
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The {@link Connection} object in use by this {@link SQLDataType}
     */
    public Connection getConnection();
}
