package com.yourname.ecommerce.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;
    private static boolean isInitialized = false;

    public static synchronized Connection getConnection() {
        if (!isInitialized) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                isInitialized = true;
                System.out.println("Database connection established successfully");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to establish database connection: " + e.getMessage());
            }
        }
        
        // Check if connection is still valid
        try {
            if (connection != null && !connection.isValid(1)) {
                System.out.println("Connection is invalid, attempting to reconnect...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to validate/reconnect database connection: " + e.getMessage());
        }
        
        return connection;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                isInitialized = false;
                System.out.println("Database connection closed successfully");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
    }
} 