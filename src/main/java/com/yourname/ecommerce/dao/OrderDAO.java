package com.yourname.ecommerce.dao;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAO {
    private final UserDAO userDAO;
    private final AddressDAO addressDAO;
    
    public OrderDAO() {
        this.userDAO = new UserDAO();
        this.addressDAO = new AddressDAO();
    }
    
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.*, oi.id as item_id, oi.quantity, oi.price, oi.tax, " +
                      "p.id as product_id, p.name as product_name, p.description as product_description, p.price as product_price " +
                      "FROM orders o " +
                      "LEFT JOIN users u ON o.user_id = u.id " +
                      "LEFT JOIN order_items oi ON o.id = oi.order_id " +
                      "LEFT JOIN products p ON oi.product_id = p.id " +
                      "ORDER BY o.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            try (ResultSet rs = stmt.executeQuery(query)) {
                Order currentOrder = null;
                while (rs.next()) {
                    int orderId = rs.getInt("o.id");
                    
                    if (currentOrder == null || currentOrder.getId() != orderId) {
                        if (currentOrder != null) {
                            orders.add(currentOrder);
                        }
                        currentOrder = mapResultSetToOrderAndUser(rs);
                    }
                    
                    // Add item if it exists
                    int itemId = rs.getInt("item_id");
                    if (!rs.wasNull()) {
                        Product product = new Product();
                        product.setId(rs.getInt("product_id"));
                        product.setName(rs.getString("product_name"));
                        product.setDescription(rs.getString("product_description"));
                        product.setPrice(rs.getDouble("product_price"));
                        
                        CartItem item = new CartItem();
                        item.setProduct(product);
                        item.setQuantity(rs.getInt("quantity"));
                        
                        currentOrder.addItem(item);
                    }
                }
                
                // Add the last order
                if (currentOrder != null) {
                    orders.add(currentOrder);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    public List<Order> findByUser(User user) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.* FROM orders o " +
                      "LEFT JOIN users u ON o.user_id = u.id " +
                      "WHERE o.user_id = ? " +
                      "ORDER BY o.created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, user.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrderAndUser(rs);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    public Order findById(int id) {
        String query = "SELECT o.*, u.* FROM orders o " +
                      "LEFT JOIN users u ON o.user_id = u.id " +
                      "WHERE o.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrderAndUser(rs);
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Order create(Order order) {
        // First save the addresses
        order.getShippingAddress().setUserId(order.getUser().getId());
        Address savedShippingAddress = addressDAO.create(order.getShippingAddress());
        if (savedShippingAddress == null) {
            System.err.println("Failed to save shipping address");
            return null;
        }
        
        order.getBillingAddress().setUserId(order.getUser().getId());
        Address savedBillingAddress = addressDAO.create(order.getBillingAddress());
        if (savedBillingAddress == null) {
            System.err.println("Failed to save billing address");
            return null;
        }
        
        // Update order with saved addresses
        order.setShippingAddress(savedShippingAddress);
        order.setBillingAddress(savedBillingAddress);
        
        String query = "INSERT INTO orders (user_id, subtotal, tax, total, status, shipping_address_id, " +
                      "billing_address_id, payment_method, tracking_number, notes) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, order.getUser().getId());
            stmt.setDouble(2, order.getSubtotal());
            stmt.setDouble(3, order.getTax());
            stmt.setDouble(4, order.getTotal());
            stmt.setString(5, order.getStatus().toString());
            stmt.setInt(6, order.getShippingAddress().getId());
            stmt.setInt(7, order.getBillingAddress().getId());
            stmt.setString(8, order.getPaymentMethod().toString());
            stmt.setString(9, order.getTrackingNumber());
            stmt.setString(10, order.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getInt(1));
                        System.out.println("OrderDAO.create: Order created with ID=" + order.getId()); // Debug log
                        
                        // Insert order items
                        String insertOrderItemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price, tax) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement itemStmt = conn.prepareStatement(insertOrderItemQuery)) {
                            for (CartItem item : order.getItems()) {
                                itemStmt.setInt(1, order.getId());
                                itemStmt.setInt(2, item.getProduct().getId());
                                itemStmt.setInt(3, item.getQuantity());
                                itemStmt.setDouble(4, item.getProduct().getPrice());
                                itemStmt.setDouble(5, item.getProduct().getPrice() * 0.20); // 20% tax rate
                                itemStmt.executeUpdate();
                                System.out.println("OrderDAO.create: Inserted order item for product ID=" + item.getProduct().getId() + ", quantity=" + item.getQuantity()); // Debug log
                            }
                        }
                        
                        return order;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating order: " + e.getMessage());
        }
        
        return null;
    }
    
    public Order update(Order order) {
        String query = "UPDATE orders SET status = ?, tracking_number = ?, notes = ?, " +
                      "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, order.getStatus().toString());
            stmt.setString(2, order.getTrackingNumber());
            stmt.setString(3, order.getNotes());
            stmt.setInt(4, order.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean delete(int id) {
        String query = "DELETE FROM orders WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Order mapResultSetToOrderAndUser(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("o.id"));
        order.setSubtotal(rs.getDouble("o.subtotal"));
        order.setTax(rs.getDouble("o.tax"));
        order.setTotal(rs.getDouble("o.total"));
        order.setStatus(Order.OrderStatus.valueOf(rs.getString("o.status")));
        order.setCreatedAt(rs.getTimestamp("o.created_at"));
        order.setUpdatedAt(rs.getTimestamp("o.updated_at"));
        order.setPaymentMethod(Order.PaymentMethod.valueOf(rs.getString("o.payment_method")));
        order.setTrackingNumber(rs.getString("o.tracking_number"));
        order.setNotes(rs.getString("o.notes"));
        
        // Check if user data exists
        int userId = rs.getInt("u.id");
        if (!rs.wasNull()) {
            User user = new User();
            user.setId(userId);
            user.setUsername(rs.getString("u.username"));
            user.setEmail(rs.getString("u.email"));
            user.setFirstName(rs.getString("u.first_name"));
            user.setLastName(rs.getString("u.last_name"));
            user.setPhone(rs.getString("u.phone"));
            user.setRole(User.UserRole.valueOf(rs.getString("u.role")));
            user.setActive(rs.getBoolean("u.is_active"));
            order.setUser(user);
            System.out.println("OrderDAO.mapResultSetToOrderAndUser: Mapped user ID=" + userId + " for order ID=" + order.getId());
        } else {
            System.out.println("OrderDAO.mapResultSetToOrderAndUser: No user found for order ID=" + order.getId());
        }
        
        return order;
    }
    
    private void loadOrderItems(Order order) {
        String query = "SELECT oi.*, p.* FROM order_items oi " +
                      "JOIN products p ON oi.product_id = p.id " +
                      "WHERE oi.order_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, order.getId());
            ResultSet rs = stmt.executeQuery();
            
            int itemCount = 0;
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                
                CartItem item = new CartItem();
                item.setProduct(product);
                item.setQuantity(rs.getInt("quantity"));
                
                order.addItem(item);
                itemCount++;
            }
            System.out.println("OrderDAO.loadOrderItems: Loaded " + itemCount + " items for order ID=" + order.getId()); // Debug log
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 