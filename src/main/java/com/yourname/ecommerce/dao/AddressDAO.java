package com.yourname.ecommerce.dao;

import com.yourname.ecommerce.models.Address;
import com.yourname.ecommerce.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {
    
    public AddressDAO() {
        // No need to store DBConnection instance since it uses static methods
    }
    
    public Address create(Address address) {
        String query = "INSERT INTO addresses (user_id, street, city, state, postal_code, country, is_default) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, address.getUserId());
            stmt.setString(2, address.getStreet());
            stmt.setString(3, address.getCity());
            stmt.setString(4, address.getState());
            stmt.setString(5, address.getZipCode());
            stmt.setString(6, address.getCountry());
            stmt.setBoolean(7, address.isDefault());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        address.setId(generatedKeys.getInt(1));
                        return address;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating address: " + e.getMessage());
        }
        
        return null;
    }
    
    public Address findById(int id) {
        String query = "SELECT * FROM addresses WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAddress(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setId(rs.getInt("id"));
        address.setUserId(rs.getInt("user_id"));
        address.setStreet(rs.getString("street"));
        address.setCity(rs.getString("city"));
        address.setState(rs.getString("state"));
        address.setZipCode(rs.getString("postal_code"));
        address.setCountry(rs.getString("country"));
        address.setDefault(rs.getBoolean("is_default"));
        return address;
    }
} 