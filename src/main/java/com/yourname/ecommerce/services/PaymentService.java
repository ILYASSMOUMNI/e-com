package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.Order;
import com.yourname.ecommerce.models.Payment;
import com.yourname.ecommerce.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PaymentService {
    private static final Random random = new Random();

    public PaymentService() {
        // No need to store connection since we'll use DBConnection.getConnection()
    }

    public boolean processPayment(Order order, String cardNumber, String expiryDate, String cvv) {
        // Validate card details
        if (!isValidCard(cardNumber, expiryDate, cvv)) {
            return false;
        }

        // Simulate network delay
        try {
            Thread.sleep(1000); // Reduced delay for better UX
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        // For demo purposes, we'll accept all valid cards
        // In a real application, this would integrate with a payment gateway
        return true;
    }

    private boolean isValidCard(String cardNumber, String expiryDate, String cvv) {
        // Basic validation
        if (cardNumber == null || cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            return false;
        }

        if (expiryDate == null || !expiryDate.matches("(0[1-9]|1[0-2])/([0-9]{2})")) {
            return false;
        }

        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            return false;
        }

        // Validate expiry date
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        
        if (month < 1 || month > 12) {
            return false;
        }

        return true;
    }

    public String generateTransactionId() {
        // Generate a random transaction ID
        return String.format("TXN%08d", random.nextInt(100000000));
    }

    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO payments (order_id, card_number, card_holder_name, expiry_date, " +
                    "cvv, amount, status, payment_date, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setString(2, payment.getCardNumber());
            stmt.setString(3, payment.getCardHolderName());
            stmt.setString(4, payment.getExpiryDate());
            stmt.setString(5, payment.getCvv());
            stmt.setDouble(6, payment.getAmount());
            stmt.setString(7, payment.getStatus());
            stmt.setTimestamp(8, new Timestamp(payment.getPaymentDate().getTime()));
            stmt.setString(9, payment.getPaymentMethod());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating payment: " + e.getMessage());
        }
        return false;
    }

    public Payment getPaymentById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Payment> getPaymentsByOrderId(int orderId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public boolean updatePaymentStatus(int paymentId, String status) {
        String sql = "UPDATE payments SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, paymentId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating payment status: " + e.getMessage());
        }
        return false;
    }

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setCardNumber(rs.getString("card_number"));
        payment.setCardHolderName(rs.getString("card_holder_name"));
        payment.setExpiryDate(rs.getString("expiry_date"));
        payment.setCvv(rs.getString("cvv"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setStatus(rs.getString("status"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        return payment;
    }
} 