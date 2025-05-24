package com.yourname.ecommerce.services;

import com.yourname.ecommerce.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private Connection connection;
    
    public AuthService() {
        this.connection = DBConnection.getConnection();
    }
    
    public boolean login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real application, use password hashing!
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean register(String username, String password, String email) {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real application, use password hashing!
            stmt.setString(3, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 