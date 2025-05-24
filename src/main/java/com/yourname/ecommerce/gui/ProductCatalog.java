package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.Product;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCatalog extends JFrame {
    private List<Product> products;
    private JPanel productPanel;
    
    public ProductCatalog() {
        products = new ArrayList<>();
        // Add some sample products
        products.add(new Product(1, "Laptop", "High-performance laptop", 999.99, 10, ""));
        products.add(new Product(2, "Smartphone", "Latest model smartphone", 699.99, 15, ""));
        products.add(new Product(3, "Headphones", "Wireless noise-cancelling headphones", 199.99, 20, ""));
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Product Catalog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Product Catalog", new ImageIcon(), SwingConstants.LEFT));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create product panel with GridLayout
        productPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add products to the panel
        for (Product product : products) {
            JPanel productCard = createProductCard(product);
            productPanel.add(productCard);
        }
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(productPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        // Product name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);
        
        // Product price
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(priceLabel);
        
        // Add to cart button
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartButton.addActionListener(e -> handleAddToCart(product));
        card.add(addToCartButton);
        
        return card;
    }
    
    private void handleAddToCart(Product product) {
        // TODO: Implement add to cart functionality
        JOptionPane.showMessageDialog(this, 
            "Added " + product.getName() + " to cart!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
} 