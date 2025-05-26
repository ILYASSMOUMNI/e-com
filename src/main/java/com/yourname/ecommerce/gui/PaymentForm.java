package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.Payment;
import com.yourname.ecommerce.models.Order;
import com.yourname.ecommerce.services.PaymentService;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

public class PaymentForm extends JPanel {
    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JTextField expiryDateField;
    private JPasswordField cvvField;
    private JComboBox<String> paymentMethodCombo;
    private JButton processButton;
    private JButton cancelButton;
    private JLabel amountLabel;
    private JLabel orderIdLabel;
    private Order currentOrder;
    private PaymentService paymentService;
    private Runnable onPaymentSuccess;
    private Runnable onPaymentCancel;

    public PaymentForm(Order order, PaymentService paymentService) {
        this.currentOrder = order;
        this.paymentService = paymentService;
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        initializeComponents();
        setupLayout();
    }
    
    public void setOnPaymentSuccess(Runnable onPaymentSuccess) {
        this.onPaymentSuccess = onPaymentSuccess;
    }

    public void setOnPaymentCancel(Runnable onPaymentCancel) {
        this.onPaymentCancel = onPaymentCancel;
    }

    private void initializeComponents() {
        // Initialize fields
        cardNumberField = new JTextField(20);
        cardHolderField = new JTextField(20);
        expiryDateField = new JTextField(20);
        cvvField = new JPasswordField(20);
        
        // Initialize payment method combo box
        String[] paymentMethods = {"Credit Card", "Debit Card"};
        paymentMethodCombo = new JComboBox<>(paymentMethods);
        
        // Initialize buttons
        processButton = new JButton("Process Payment");
        cancelButton = new JButton("Cancel");
        
        // Initialize labels
        amountLabel = new JLabel(String.format("Total Amount: $%.2f", currentOrder.getTotalAmount()));
        orderIdLabel = new JLabel("Order #" + currentOrder.getId());

        // Style components
        styleComponents();

        // Add action listeners
        processButton.addActionListener(e -> processPayment());
        cancelButton.addActionListener(e -> cancelPayment());
        
        // Add input validation listeners
        addValidationListeners();
    }

    private void styleComponents() {
        // Style title
        JLabel titleLabel = new JLabel("Payment Information", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.PRIMARY_COLOR);
        
        // Style amount and order labels
        amountLabel.setFont(AppTheme.HEADER_FONT);
        amountLabel.setForeground(AppTheme.PRIMARY_COLOR);
        orderIdLabel.setFont(AppTheme.NORMAL_FONT);
        orderIdLabel.setForeground(AppTheme.TEXT_COLOR);
        
        // Style text fields
        AppTheme.applyTheme(cardNumberField);
        AppTheme.applyTheme(cardHolderField);
        AppTheme.applyTheme(expiryDateField);
        AppTheme.applyTheme(cvvField);
        AppTheme.applyTheme(paymentMethodCombo);
        
        // Style buttons
        AppTheme.applyTheme(processButton);
        AppTheme.applyTheme(cancelButton);
    }

    private void addValidationListeners() {
        // Card number validation (16 digits)
        cardNumberField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateCardNumber(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateCardNumber(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateCardNumber(); }
            
            private void validateCardNumber() {
                String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
                if (!cardNumber.matches("\\d{16}")) {
                    cardNumberField.setBorder(BorderFactory.createLineBorder(AppTheme.ERROR_COLOR));
                } else {
                    cardNumberField.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR));
                }
            }
        });

        // Expiry date validation (MM/YY format)
        expiryDateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateExpiryDate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateExpiryDate(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateExpiryDate(); }
            
            private void validateExpiryDate() {
                String expiryDate = expiryDateField.getText();
                if (!expiryDate.matches("(0[1-9]|1[0-2])/([0-9]{2})")) {
                    expiryDateField.setBorder(BorderFactory.createLineBorder(AppTheme.ERROR_COLOR));
                } else {
                    expiryDateField.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR));
                }
            }
        });

        // CVV validation (3-4 digits)
        cvvField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateCVV(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateCVV(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateCVV(); }
            
            private void validateCVV() {
                String cvv = new String(cvvField.getPassword());
                if (!cvv.matches("\\d{3,4}")) {
                    cvvField.setBorder(BorderFactory.createLineBorder(AppTheme.ERROR_COLOR));
                } else {
                    cvvField.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR));
                }
            }
        });
    }

    private void setupLayout() {
        // Create main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel with card-like appearance
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(AppTheme.PANEL_BORDER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Order Information
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(orderIdLabel, gbc);
        gbc.gridy++;
        formPanel.add(amountLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Payment Method
        gbc.gridx = 0;
        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        paymentMethodLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(paymentMethodLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(paymentMethodCombo, gbc);

        // Card Number
        gbc.gridx = 0; gbc.gridy++;
        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(cardNumberLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(cardNumberField, gbc);

        // Card Holder Name
        gbc.gridx = 0; gbc.gridy++;
        JLabel cardHolderLabel = new JLabel("Card Holder Name:");
        cardHolderLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(cardHolderLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(cardHolderField, gbc);

        // Expiry Date
        gbc.gridx = 0; gbc.gridy++;
        JLabel expiryDateLabel = new JLabel("Expiry Date (MM/YY):");
        expiryDateLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(expiryDateLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(expiryDateField, gbc);

        // CVV
        gbc.gridx = 0; gbc.gridy++;
        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(cvvLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(cvvField, gbc);

        // Button panel with spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(processButton);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    private void processPayment() {
        if (!validateFields()) {
            return;
        }

        // Create payment object
        Payment payment = new Payment(
            currentOrder.getId(),
            cardNumberField.getText(),
            cardHolderField.getText(),
            expiryDateField.getText(),
            new String(cvvField.getPassword()),
            currentOrder.getTotalAmount(),
            "PENDING",
            (String) paymentMethodCombo.getSelectedItem()
        );

        // Show processing dialog
        JDialog processingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Processing Payment", true);
        JLabel processingLabel = new JLabel("Processing your payment...", SwingConstants.CENTER);
        processingLabel.setFont(AppTheme.NORMAL_FONT);
        processingDialog.add(processingLabel);
        processingDialog.setSize(300, 100);
        processingDialog.setLocationRelativeTo(this);

        // Process payment in background
        new Thread(() -> {
            try {
                // First save payment to database
                boolean saved = paymentService.createPayment(payment);
                if (!saved) {
                    SwingUtilities.invokeLater(() -> {
                        processingDialog.dispose();
                        JOptionPane.showMessageDialog(this,
                            "Failed to save payment information. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                    return;
                }

                // Then process the payment
                boolean processed = paymentService.processPayment(
                    currentOrder,
                    payment.getCardNumber(),
                    payment.getExpiryDate(),
                    payment.getCvv()
                );

                SwingUtilities.invokeLater(() -> {
                    processingDialog.dispose();
                    if (processed) {
                        // Update payment status to SUCCESS
                        paymentService.updatePaymentStatus(payment.getId(), "SUCCESS");
                        JOptionPane.showMessageDialog(this,
                            "Payment processed successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        if (onPaymentSuccess != null) {
                            onPaymentSuccess.run();
                        }
                    } else {
                        // Update payment status to FAILED
                        paymentService.updatePaymentStatus(payment.getId(), "FAILED");
                        JOptionPane.showMessageDialog(this,
                            "Payment processing failed. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    processingDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "An error occurred while processing payment: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

        processingDialog.setVisible(true);
    }

    private boolean validateFields() {
        if (cardNumberField.getText().isEmpty() || cardHolderField.getText().isEmpty() ||
            expiryDateField.getText().isEmpty() || cvvField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate card number (16 digits)
        if (!cardNumberField.getText().replaceAll("\\s", "").matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this,
                "Invalid card number. Please enter 16 digits.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate expiry date (MM/YY format)
        if (!expiryDateField.getText().matches("(0[1-9]|1[0-2])/([0-9]{2})")) {
            JOptionPane.showMessageDialog(this,
                "Invalid expiry date. Please use MM/YY format.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate CVV (3-4 digits)
        if (!new String(cvvField.getPassword()).matches("\\d{3,4}")) {
            JOptionPane.showMessageDialog(this,
                "Invalid CVV. Please enter 3 or 4 digits.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void cancelPayment() {
         if (onPaymentCancel != null) {
            onPaymentCancel.run();
        }
    }
} 