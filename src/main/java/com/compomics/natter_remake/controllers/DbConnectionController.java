package com.compomics.natter_remake.controllers;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Davy
 */
public class DbConnectionController {

    private static Connection connection;

    /**
     * get connection to db, this overwrites any previously established connections
     * @param username db user
     * @param password db password
     * @param url url to the db for example: foo.bar.com
     * @param port the port to connect to the database
     * @param database the name of the db
     * @return a jdbc connection object
     * @throws SQLException 
     */
    public static Connection createConnection(String username, String password, String url,int port, String database) throws SQLException {
        DbConnectionController dbConnectionController = new DbConnectionController(username, password, url,port, database);
        return DbConnectionController.getConnection();
    }
    
    /**
     * get connection to db for port 3306, this overwrites any previously established connections
     * @param username db user
     * @param password db password
     * @param url url to the db for example: foo.bar.com
     * @param database the name of the db
     * @return a jdbc connection object
     * @throws SQLException 
     */
    public static Connection createConnection(String username, String password, String url, String database) throws SQLException {
        DbConnectionController instance = new DbConnectionController(username, password, url, 3306, database);
        return DbConnectionController.getConnection();
    }

    private DbConnectionController(String username, String password, String url,int port, String database) throws SQLException {
        MysqlDataSource dbSource = new MysqlDataSource();
        dbSource.setServerName(url);
        dbSource.setDatabaseName(database);
        dbSource.setUser(username);
        dbSource.setPort(port);
        dbSource.setPassword(password);
        DbConnectionController.connection = dbSource.getConnection();
    }
    /**
     * getter for connection
     * @return a jdbc connection object
     * @throws SQLException if the connection does not exist yet
     */
    public static Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        } else {
            throw new SQLException("connection not yet established");
        }
    }
}
