package com.crio.starter.connection;

import java.sql.Connection;
import lombok.extern.log4j.Log4j2;
import java.sql.DriverManager;
import java.sql.SQLException;

// This class can be used to initialize the database connection
@Log4j2
public class DatabaseConnection {
    public static Connection initializeDatabase()
        throws SQLException, ClassNotFoundException
    {
        // Initialize all the information regarding
        // Database Connection
        log.info("Connection Started");
        String dbDriver = "com.mysql.jdbc.Driver";
        String dbURL = "jdbc:mysql://localhost:3306/";
        // Database name to access
        String dbName = "mysql";
        String extra = "?autoReconnect=true&useSSL=false";
        String dbUsername = "admin";
        String dbPassword = "Mahima10";

        Class.forName(dbDriver);
        Connection con = DriverManager.getConnection(dbURL + dbName + extra,
                                                     dbUsername,
                                                     dbPassword);
        return con;
    }
}
