package com.compomics.natter_remake.controllers;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Davy
 */
public class DbConnectionController {

    private static MysqlDataSource connectionSource = new MysqlDataSource();

    private static boolean connectionMade = false;
    
    private static Connection connection;

    /**
     * get connectionSource to db, this overwrites any previously established connections
     * @param username db user
     * @param password db password
     * @param url url to the db for example: foo.bar.com
     * @param port the port to connect to the database
     * @param database the name of the db
     * @return a jdbc connectionSource object
     * @throws SQLException 
     */
    public static Connection createConnection(String username, String password, String url, int port, String database) throws SQLException {
        initConnection(username, password, url, port, database);
        connectionMade = true;
        return connection;
    }

    /**
     * get connectionSource to db for port 3306, this overwrites any previously
     * established connections
     *
     * @param username db user
     * @param password db password
     * @param url url to the db for example: foo.bar.com
     * @param database the name of the db
     * @return a jdbc connection object
     * @throws SQLException
     */
    
    public static Connection createConnection(String username, String password, String url, String database) throws SQLException {
        initConnection(username, password, url, 3306, database);
        connection = connectionSource.getConnection();
        connectionMade = true;
        return connection;
    }

    /**
     * sets up the connection
     * @param username db user
     * @param password db password
     * @param url url to the db for example: foo.bar.com
     * @param database the name of the db
     * @param port the port the mysql db runs on
     * @throws SQLException 
     */
    private static void initConnection(String username, String password, String url, int port, String database) throws SQLException {
        connectionSource.setServerName(url);
        connectionSource.setDatabaseName(database);
        connectionSource.setUser(username);
        connectionSource.setPort(port);
        connectionSource.setPassword(password);
    }

    /**
     * getter for connectionSource
     *
     * @return a jdbc connectionSource object
     * @throws SQLException if the connectionSource does not exist yet
     */
    public static Connection getConnection() throws SQLException {
        if (connectionSource != null && connection != null) {
            return connection;
        } else {
            throw new SQLException("connection not yet established");
        }
    }
/**
 * boolean for checking
 * @return true if connection is made otherwise false
 */
    public boolean isConnectionMade() {
        return connectionMade;
    }
}
