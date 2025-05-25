package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.Order;
import java.util.Random;
import com.yourname.ecommerce.models.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private static final Random random = new Random();
    private Connection connection;

    public PaymentService(Connection connection) {
        this.connection = connection;
    }

    public boolean processPayment(Order order, String cardNumber, String expiryDate, String cvv) {
        // Simulate payment processing
        if (!isValidCard(cardNumber, expiryDate, cvv)) {
            return false;
        }

        // Simulate network delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        // Simulate random success/failure (90% success rate)
        return random.nextDouble() < 0.9;
    }

    private boolean isValidCard(String cardNumber, String expiryDate, String cvv) {
        // Basic validation
        if (cardNumber == null || cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            return false;
        }

        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        if (cvv == null || cvv.length() != 3 || !cvv.matches("\\d+")) {
            return false;
        }

        // Validate expiry date
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        
        if (month < 1 || month > 12) {
            return false;
        }

        // Add more validation as needed
        return true;
    }

    public String generateTransactionId() {
        // Generate a random transaction ID
        return String.format("TXN%08d", random.nextInt(100000000));
    }

    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO payments (order_id, card_number, card_holder_name, expiry_date, " +
                    "cvv, amount, status, payment_date, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        }
        return false;
    }

    public Payment getPaymentById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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