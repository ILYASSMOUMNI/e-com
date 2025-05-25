package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.CartItem;
import com.yourname.ecommerce.models.Order;
import com.yourname.ecommerce.models.Product;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart extends JPanel {
    private JPanel cartItemsPanel;
    private JLabel totalLabel;
    private JButton checkoutButton;
    private JButton continueShoppingButton;
    private List<CartItem> cartItems;
    private List<Runnable> checkoutListeners;
    private List<Runnable> continueShoppingListeners;

    public ShoppingCart() {
        cartItems = new ArrayList<>();
        checkoutListeners = new ArrayList<>();
        continueShoppingListeners = new ArrayList<>();
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        checkoutButton = new JButton("Proceed to Checkout");
        continueShoppingButton = new JButton("Continue Shopping");
        
        checkoutButton.addActionListener(e -> handleCheckout());
        continueShoppingButton.addActionListener(e -> notifyContinueShopping());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(continueShoppingButton);
        buttonPanel.add(checkoutButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void addItem(Product product, int quantity) {
        CartItem existingItem = findItem(product);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(product, quantity);
            cartItems.add(newItem);
        }
        updateCartDisplay();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getTotalAmount() {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    private CartItem findItem(Product product) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId() == product.getId())
                .findFirst()
                .orElse(null);
    }

    private void updateCartDisplay() {
        cartItemsPanel.removeAll();
        
        for (CartItem item : cartItems) {
            JPanel itemPanel = createItemPanel(item);
            cartItemsPanel.add(itemPanel);
            cartItemsPanel.add(Box.createVerticalStrut(10));
        }
        
        totalLabel.setText(String.format("Total: $%.2f", getTotalAmount()));
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel createItemPanel(CartItem item) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel nameLabel = new JLabel(item.getProduct().getName());
        JLabel priceLabel = new JLabel(String.format("$%.2f x %d = $%.2f",
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getPrice() * item.getQuantity()));
        
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> {
            cartItems.remove(item);
            updateCartDisplay();
        });
        
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(priceLabel, BorderLayout.CENTER);
        panel.add(removeButton, BorderLayout.EAST);
        
        return panel;
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your cart is empty!",
                    "Empty Cart",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        notifyCheckout();
    }

    public void addCheckoutListener(Runnable listener) {
        checkoutListeners.add(listener);
    }

    public void addContinueShoppingListener(Runnable listener) {
        continueShoppingListeners.add(listener);
    }

    private void notifyCheckout() {
        for (Runnable listener : checkoutListeners) {
            listener.run();
        }
    }

    private void notifyContinueShopping() {
        for (Runnable listener : continueShoppingListeners) {
            listener.run();
        }
    }
} 