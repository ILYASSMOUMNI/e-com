package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.Order;
import java.util.Random;

public class PaymentService {
    private static final Random random = new Random();

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
} 