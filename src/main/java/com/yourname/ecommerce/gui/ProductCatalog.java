package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.Product;
import com.yourname.ecommerce.models.Category;
import com.yourname.ecommerce.services.ProductService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductCatalog extends JPanel {
    private JPanel productsPanel;
    private JButton viewCartButton;
    private List<Runnable> viewCartListeners;
    private List<Consumer<Product>> productClickListeners;
    private ProductService productService;
    private JComboBox<Category> categoryFilter;

    public ProductCatalog() {
        setLayout(new BorderLayout());
        viewCartListeners = new ArrayList<>();
        productClickListeners = new ArrayList<>();
        productService = new ProductService();
        initializeComponents();
        setupLayout();
        loadProducts();
    }

    private void initializeComponents() {
        productsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        productsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(e -> notifyViewCart());

        // Initialize category filter
        List<Category> categories = productService.getAllCategories();
        categoryFilter = new JComboBox<>(categories.toArray(new Category[0]));
        categoryFilter.insertItemAt(null, 0);
        categoryFilter.setSelectedIndex(0);
        categoryFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("All Categories");
                } else {
                    setText(((Category) value).getName());
                }
                return this;
            }
        });
        categoryFilter.addActionListener(e -> loadProducts());
    }

    private void setupLayout() {
        // Add scroll pane for products
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Top panel with title and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Product Catalog", SwingConstants.CENTER), BorderLayout.NORTH);
        topPanel.add(categoryFilter, BorderLayout.SOUTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewCartButton);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        productsPanel.removeAll();
        
        List<Product> products;
        Category selectedCategory = (Category) categoryFilter.getSelectedItem();
        if (selectedCategory == null) {
            products = productService.getAllProducts();
        } else {
            products = productService.getProductsByCategory(selectedCategory);
        }

        for (Product product : products) {
            addProductCard(product);
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void addProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Product image
        JLabel imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        card.add(imageLabel);

        // Product name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        // Product price
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(priceLabel);

        // View details button
        JButton viewButton = new JButton("View Details");
        viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewButton.addActionListener(e -> notifyProductClick(product));
        card.add(Box.createVerticalStrut(10));
        card.add(viewButton);

        productsPanel.add(card);
    }

    public void addProductClickListener(Consumer<Product> listener) {
        productClickListeners.add(listener);
    }

    private void notifyProductClick(Product product) {
        for (Consumer<Product> listener : productClickListeners) {
            listener.accept(product);
        }
    }

    public void addViewCartListener(Runnable listener) {
        viewCartListeners.add(listener);
    }

    private void notifyViewCart() {
        for (Runnable listener : viewCartListeners) {
            listener.run();
        }
    }
} 