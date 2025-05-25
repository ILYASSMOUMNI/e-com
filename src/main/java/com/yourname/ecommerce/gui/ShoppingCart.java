package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.CartItem;
import com.yourname.ecommerce.models.Order;
import com.yourname.ecommerce.models.Product;
import javax.swing.*;
import javax.swing.border.*;
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
        applyTheme();
    }

    private void initializeComponents() {
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(AppTheme.HEADER_FONT);
        
        checkoutButton = new JButton("Proceed to Checkout");
        continueShoppingButton = new JButton("Continue Shopping");
        
        checkoutButton.addActionListener(e -> handleCheckout());
        continueShoppingButton.addActionListener(e -> notifyContinueShopping());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Shopping Cart", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Cart items panel with scroll
        cartItemsPanel.setPreferredSize(new Dimension(0, 400)); // Set fixed height
        cartItemsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        // Bottom panel with total and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setOpaque(false);
        totalPanel.add(new JLabel("Total Amount:"));
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(continueShoppingButton);
        buttonPanel.add(checkoutButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void applyTheme() {
        // Apply theme to all components
        AppTheme.applyTheme(this);
        AppTheme.applyTheme(cartItemsPanel);
        AppTheme.applyTheme(totalLabel);
        AppTheme.applyTheme(checkoutButton);
        AppTheme.applyTheme(continueShoppingButton);
        
        // Custom styling for checkout button
        checkoutButton.setBackground(AppTheme.SUCCESS_COLOR);
        checkoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                checkoutButton.setBackground(AppTheme.SUCCESS_COLOR.darker());
            }
            public void mouseExited(MouseEvent e) {
                checkoutButton.setBackground(AppTheme.SUCCESS_COLOR);
            }
        });
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
        
        if (cartItems.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            emptyLabel.setFont(AppTheme.HEADER_FONT);
            emptyLabel.setForeground(AppTheme.TEXT_COLOR);
            cartItemsPanel.add(emptyLabel);
        } else {
            for (CartItem item : cartItems) {
                JPanel itemPanel = createItemPanel(item);
                cartItemsPanel.add(itemPanel);
                cartItemsPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        totalLabel.setText(String.format("$%.2f", getTotalAmount()));
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel createItemPanel(CartItem item) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Set fixed height for each item

        // Left panel with product info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(item.getProduct().getName());
        nameLabel.setFont(AppTheme.HEADER_FONT);
        
        JLabel priceLabel = new JLabel(String.format("$%.2f each", item.getProduct().getPrice()));
        priceLabel.setForeground(AppTheme.ACCENT_COLOR);
        
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        
        // Center panel with quantity controls
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        quantityPanel.setOpaque(false);
        
        JButton decreaseButton = new JButton("-");
        JLabel quantityLabel = new JLabel(String.valueOf(item.getQuantity()));
        JButton increaseButton = new JButton("+");
        
        decreaseButton.addActionListener(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateCartDisplay();
            }
        });
        
        increaseButton.addActionListener(e -> {
            item.setQuantity(item.getQuantity() + 1);
            updateCartDisplay();
        });
        
        quantityPanel.add(decreaseButton);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(increaseButton);
        
        // Right panel with subtotal and remove button
        JPanel rightPanel = new JPanel(new BorderLayout(10, 0));
        rightPanel.setOpaque(false);
        
        JLabel subtotalLabel = new JLabel(String.format("$%.2f", 
            item.getProduct().getPrice() * item.getQuantity()));
        subtotalLabel.setFont(AppTheme.HEADER_FONT);
        
        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(AppTheme.ACCENT_COLOR);
        removeButton.addActionListener(e -> {
            cartItems.remove(item);
            updateCartDisplay();
        });
        
        rightPanel.add(subtotalLabel, BorderLayout.CENTER);
        rightPanel.add(removeButton, BorderLayout.SOUTH);
        
        // Add all panels to main panel
        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(quantityPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        
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