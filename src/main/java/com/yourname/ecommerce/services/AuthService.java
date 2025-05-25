package com.yourname.ecommerce.services;

import com.yourname.ecommerce.dao.DBConnection;
import com.yourname.ecommerce.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final DBConnection dbConnection;
    
    public AuthService() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    public User login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND is_active = true";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real application, use password hashing!
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setPhone(rs.getString("phone"));
                user.setRole(User.UserRole.valueOf(rs.getString("role")));
                user.setActive(rs.getBoolean("is_active"));
                
                // Update last login
                updateLastLogin(user.getId());
                
                return user;
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            return null;
        }
    }
    
    public User register(String username, String password, String email, String firstName, String lastName, String phone) {
        // First check if username or email already exists
        if (isUserExists(username, email)) {
            return null;
        }
        
        String query = "INSERT INTO users (username, password_hash, email, first_name, last_name, phone, role, is_active) " +
                      "VALUES (?, ?, ?, ?, ?, ?, 'CUSTOMER', true)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real application, use password hashing!
            stmt.setString(3, email);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            stmt.setString(6, phone);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        User user = new User();
                        user.setId(generatedKeys.getInt(1));
                        user.setUsername(username);
                        user.setEmail(email);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setPhone(phone);
                        user.setRole(User.UserRole.CUSTOMER);
                        user.setActive(true);
                        return user;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during registration", e);
            return null;
        }
    }
    
    private boolean isUserExists(String username, String email) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists", e);
            return true; // Return true to prevent registration in case of error
        }
    }
    
    private void updateLastLogin(int userId) {
        String query = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating last login", e);
        }
    }
} 