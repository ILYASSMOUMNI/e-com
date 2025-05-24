package com.yourname.ecommerce.utils;

import com.yourname.ecommerce.dao.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectionTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database connection...");
            
            // Get connection
            DBConnection dbConnection = DBConnection.getInstance();
            Connection conn = dbConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful!");
                
                // Test query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
                
                if (rs.next()) {
                    System.out.println("Connected to database: " + rs.getString(1));
                }
                
                // Test users table
                rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next()) {
                    System.out.println("Number of users in database: " + rs.getInt(1));
                }
                
                // Close resources
                rs.close();
                stmt.close();
                conn.close();
                System.out.println("Database connection test completed successfully!");
            }
        } catch (Exception e) {
            System.err.println("Database connection test failed!");
            e.printStackTrace();
        }
    }
} 