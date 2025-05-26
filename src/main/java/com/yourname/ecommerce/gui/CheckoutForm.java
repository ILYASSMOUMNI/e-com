package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.Order;
import com.yourname.ecommerce.models.Order.OrderStatus;
import com.yourname.ecommerce.services.PaymentService;
import com.yourname.ecommerce.services.OrderService;
import com.yourname.ecommerce.utils.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CheckoutForm extends JPanel {
    private PaymentForm paymentForm;
    private JButton backToCartButton;
    private List<Runnable> backToCartListeners;
    private OrderService orderService;
    private Order currentOrder;
    private JPanel contentPanel;

    public CheckoutForm() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        backToCartListeners = new ArrayList<>();
        orderService = new OrderService();
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // Initialize back button
        backToCartButton = new JButton("Back to Cart");
        AppTheme.applyTheme(backToCartButton);

        // Add action listener for back button
        backToCartButton.addActionListener(e -> notifyBackToCart());
    }

    private void setupLayout() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Checkout", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        if (currentOrder != null) {
            contentPanel.add(paymentForm, BorderLayout.CENTER);
        } else {
            JLabel waitingLabel = new JLabel("Please wait while loading order details...", SwingConstants.CENTER);
            waitingLabel.setFont(AppTheme.NORMAL_FONT);
            contentPanel.add(waitingLabel, BorderLayout.CENTER);
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        buttonPanel.add(backToCartButton);

        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setOrder(Order order) {
        // First save the order to the database
        Order savedOrder = orderService.createOrder(order);
        if (savedOrder == null) {
            JOptionPane.showMessageDialog(this,
                "Failed to create order. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        this.currentOrder = savedOrder;
        
        // Initialize payment form with the saved order
        PaymentService paymentService = new PaymentService();
        paymentForm = new PaymentForm(currentOrder, paymentService);
        paymentForm.setOnPaymentSuccess(this::handlePaymentSuccess);
        paymentForm.setOnPaymentCancel(this::notifyBackToCart);
        
        // Update the content panel
        contentPanel.removeAll();
        contentPanel.add(paymentForm, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void handlePaymentSuccess() {
        // Update order status to CONFIRMED
        currentOrder.setStatus(OrderStatus.CONFIRMED);
        orderService.updateOrder(currentOrder);
        
        JOptionPane.showMessageDialog(this,
            "Order placed successfully!",
            "Order Confirmed",
            JOptionPane.INFORMATION_MESSAGE);
        // For now, navigate back to catalog
        Container parent = getParent();
        if (parent instanceof JFrame) {
            JFrame frame = (JFrame) parent;
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new MainFrame());
            frame.revalidate();
            frame.repaint();
        }
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