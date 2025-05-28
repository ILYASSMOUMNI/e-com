package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.Product;
import com.yourname.ecommerce.models.Category;
import com.yourname.ecommerce.services.ProductService;
import javax.swing.*;
import javax.swing.border.*;
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
    private JTextField searchField;
    private JLabel totalProductsLabel;
    private JPanel filterPanel;

    public ProductCatalog() {
        setLayout(new BorderLayout());
        viewCartListeners = new ArrayList<>();
        productClickListeners = new ArrayList<>();
        productService = new ProductService();
        initializeComponents();
        setupLayout();
        applyTheme();
        loadProducts();
    }

    private void initializeComponents() {
        productsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        productsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
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

        // Initialize search field
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search products...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadProducts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadProducts(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadProducts(); }
        });

        // Initialize total products label
        totalProductsLabel = new JLabel("0 products found");
    }

    private void setupLayout() {
        // Create filter panel
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(totalProductsLabel);

        // Add scroll pane for products
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        // Top panel with title and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Product Catalog", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewCartButton);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void applyTheme() {
        // Apply theme to all components
        AppTheme.applyTheme(this);
        AppTheme.applyTheme(productsPanel);
        AppTheme.applyTheme(viewCartButton);
        AppTheme.applyTheme(searchField);
        AppTheme.applyTheme(categoryFilter);
        AppTheme.applyTheme(filterPanel);
        AppTheme.applyTheme(totalProductsLabel);
    }

    private void loadProducts() {
        productsPanel.removeAll();
        
        String searchQuery = searchField.getText().trim().toLowerCase();
        Category selectedCategory = (Category) categoryFilter.getSelectedItem();
        
        List<Product> products;
        if (selectedCategory == null) {
            products = productService.getAllProducts();
        } else {
            products = productService.getProductsByCategory(selectedCategory);
        }

        // Filter products by search query
        if (!searchQuery.isEmpty()) {
            products = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchQuery) ||
                           p.getDescription().toLowerCase().contains(searchQuery))
                .toList();
        }

        for (Product product : products) {
            addProductCard(product);
        }

        totalProductsLabel.setText(products.size() + " products found");
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void addProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        // Product image
        JLabel imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        imageLabel.setMaximumSize(new Dimension(200, 200));
        imageLabel.setBorder(BorderFactory.createLineBorder(AppTheme.BACKGROUND_COLOR));
        
        // Load image from media folder
        new Thread(() -> {
            try {
                String imagePath = "media/" + product.getName() + ".png";
                // Try PNG first, then WebP
                if (!new java.io.File(imagePath).exists()) {
                    imagePath = "media/" + product.getName() + ".webp";
                }
                java.awt.Image image = javax.imageio.ImageIO.read(new java.io.File(imagePath));
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setIcon(new ImageIcon(scaledImage));
                        imageLabel.setText("");
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setIcon(null);
                        imageLabel.setText("No image");
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(null);
                    imageLabel.setText("No image");
                });
            }
        }).start();
        
        card.add(imageLabel);

        // Product name
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(AppTheme.HEADER_FONT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);

        // Product price
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(AppTheme.NORMAL_FONT);
        priceLabel.setForeground(AppTheme.ACCENT_COLOR);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(5));
        card.add(priceLabel);

        // Product description (truncated)
        String description = product.getDescription();
        if (description.length() > 100) {
            description = description.substring(0, 97) + "...";
        }
        JLabel descLabel = new JLabel("<html><body style='text-align: center;'>" + description + "</body></html>");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);

        // View details button
        JButton viewButton = new JButton("View Details");
        viewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewButton.addActionListener(e -> notifyProductClick(product));
        card.add(Box.createVerticalStrut(15));
        card.add(viewButton);

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.SECONDARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

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