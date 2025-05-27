package com.yourname.ecommerce.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    private static DBConnection instance;
    private Connection connection;
    
    private DBConnection() {
        try {
            LOGGER.info("Initializing database connection...");
            LOGGER.info("Database URL: " + DB_URL);
            LOGGER.info("Username: " + USER);
            
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("JDBC driver loaded successfully");
            
            // Set connection properties
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("autoReconnect", "true");
            props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "UTF-8");
            
            // Attempt to establish connection
            LOGGER.info("Attempting to connect to database...");
            connection = DriverManager.getConnection(DB_URL, props);
            
            if (connection != null && !connection.isClosed()) {
                LOGGER.info("Database connection established successfully");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database", e);
            LOGGER.log(Level.SEVERE, "SQL State: " + e.getSQLState());
            LOGGER.log(Level.SEVERE, "Error Code: " + e.getErrorCode());
            LOGGER.log(Level.SEVERE, "Message: " + e.getMessage());
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
    
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                LOGGER.info("Connection is null or closed. Creating new connection...");
                Properties props = new Properties();
                props.setProperty("user", USER);
                props.setProperty("password", PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("autoReconnect", "true");
                props.setProperty("useUnicode", "true");
                props.setProperty("characterEncoding", "UTF-8");
                
                connection = DriverManager.getConnection(DB_URL, props);
                LOGGER.info("New database connection established");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting database connection", e);
            LOGGER.log(Level.SEVERE, "SQL State: " + e.getSQLState());
            LOGGER.log(Level.SEVERE, "Error Code: " + e.getErrorCode());
            LOGGER.log(Level.SEVERE, "Message: " + e.getMessage());
            throw new RuntimeException("Failed to get database connection", e);
        }
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing database connection", e);
        }
    }
} 