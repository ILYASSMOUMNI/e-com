package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.Order;
import com.yourname.ecommerce.services.PaymentService;
import com.yourname.ecommerce.services.OrderService;
import com.yourname.ecommerce.utils.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CheckoutForm extends JPanel {
    private JTextField cardNumberField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JButton processPaymentButton;
    private JButton backToCartButton;
    private List<Runnable> backToCartListeners;
    private PaymentService paymentService;
    private OrderService orderService;
    private Order currentOrder;

    public CheckoutForm() {
        setLayout(new BorderLayout());
        backToCartListeners = new ArrayList<>();
        paymentService = new PaymentService(DBConnection.getConnection());
        orderService = new OrderService();
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        cardNumberField = new JTextField(20);
        expiryDateField = new JTextField(5);
        cvvField = new JTextField(3);
        processPaymentButton = new JButton("Process Payment");
        backToCartButton = new JButton("Back to Cart");

        // Add action listeners
        processPaymentButton.addActionListener(e -> handlePayment());
        backToCartButton.addActionListener(e -> notifyBackToCart());
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to form panel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Card Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cardNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Expiry Date (MM/YY):"), gbc);
        gbc.gridx = 1;
        formPanel.add(expiryDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("CVV:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cvvField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(processPaymentButton);
        buttonPanel.add(backToCartButton);

        // Add panels to main panel
        add(new JLabel("Payment Information", SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setOrder(Order order) {
        this.currentOrder = order;
    }

    private void handlePayment() {
        String cardNumber = cardNumberField.getText();
        String expiryDate = expiryDateField.getText();
        String cvv = cvvField.getText();

        if (currentOrder == null) {
            JOptionPane.showMessageDialog(this, 
                "No order information available", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show processing dialog
        JDialog processingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Processing Payment", true);
        JLabel processingLabel = new JLabel("Processing payment, please wait...");
        processingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        processingDialog.add(processingLabel);
        processingDialog.setSize(300, 100);
        processingDialog.setLocationRelativeTo(this);

        // Process payment in background
        new Thread(() -> {
            boolean success = paymentService.processPayment(currentOrder, cardNumber, expiryDate, cvv);
            
            SwingUtilities.invokeLater(() -> {
                processingDialog.dispose();
                
                if (success) {
                    String transactionId = paymentService.generateTransactionId();
                    currentOrder.setTransactionId(transactionId);
                    Order savedOrder = orderService.createOrder(currentOrder);
                    if (savedOrder != null) {
                        JOptionPane.showMessageDialog(this,
                            "Payment successful!\nTransaction ID: " + transactionId,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        notifyBackToCart();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Payment successful, but order could not be saved. Please contact support.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Payment failed. Please check your card details and try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();

        processingDialog.setVisible(true);
    }

    public void addBackToCartListener(Runnable listener) {
        backToCartListeners.add(listener);
    }

    private void notifyBackToCart() {
        for (Runnable listener : backToCartListeners) {
            listener.run();
        }
    }
} 