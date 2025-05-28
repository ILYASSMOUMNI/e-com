package com.yourname.ecommerce.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import com.yourname.ecommerce.models.Product;
import java.io.File;

public class ProductDetail extends JPanel {
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel descriptionLabel;
    private JLabel stockLabel;
    private JLabel categoryLabel;
    private JSpinner quantitySpinner;
    private JButton addToCartButton;
    private JButton backButton;
    private JButton wishlistButton;
    private List<Runnable> backListeners;
    private List<BiConsumer<Product, Integer>> addToCartListeners;
    private String imageUrl;
    private Product currentProduct;
    private boolean isInWishlist;

    public ProductDetail() {
        setLayout(new BorderLayout());
        backListeners = new ArrayList<>();
        addToCartListeners = new ArrayList<>();
        initializeComponents();
        setupLayout();
        applyTheme();
    }

    private void initializeComponents() {
        imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 400));
        imageLabel.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1));
        
        nameLabel = new JLabel();
        nameLabel.setFont(AppTheme.TITLE_FONT);
        
        priceLabel = new JLabel();
        priceLabel.setFont(AppTheme.HEADER_FONT);
        priceLabel.setForeground(AppTheme.ACCENT_COLOR);
        
        descriptionLabel = new JLabel();
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        descriptionLabel.setFont(AppTheme.NORMAL_FONT);
        
        stockLabel = new JLabel();
        stockLabel.setFont(AppTheme.NORMAL_FONT);
        
        categoryLabel = new JLabel();
        categoryLabel.setFont(AppTheme.NORMAL_FONT);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 99, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        JComponent editor = quantitySpinner.getEditor();
        JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
        textField.setColumns(3);
        
        addToCartButton = new JButton("Add to Cart");
        backButton = new JButton("Back to Catalog");
        wishlistButton = new JButton("♡ Add to Wishlist");
        isInWishlist = false;

        addToCartButton.addActionListener(e -> handleAddToCart());
        backButton.addActionListener(e -> notifyBack());
        wishlistButton.addActionListener(e -> toggleWishlist());
    }

    private void setupLayout() {
        // Main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Image panel with border
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Product info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        infoPanel.add(nameLabel, gbc);
        infoPanel.add(priceLabel, gbc);
        infoPanel.add(categoryLabel, gbc);
        infoPanel.add(stockLabel, gbc);
        
        // Description panel with scroll
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR),
            "Description"
        ));
        descPanel.add(descriptionLabel, BorderLayout.CENTER);
        JScrollPane descScroll = new JScrollPane(descPanel);
        descScroll.setPreferredSize(new Dimension(400, 150));
        infoPanel.add(descScroll, gbc);

        detailsPanel.add(infoPanel, BorderLayout.CENTER);

        // Control panel with quantity and buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quantityPanel.add(new JLabel("Quantity:"));
        quantityPanel.add(quantitySpinner);
        controlPanel.add(quantityPanel);
        
        controlPanel.add(addToCartButton);
        controlPanel.add(wishlistButton);
        controlPanel.add(backButton);
        
        detailsPanel.add(controlPanel, BorderLayout.SOUTH);

        // Add panels to main panel
        mainPanel.add(imagePanel, BorderLayout.CENTER);
        mainPanel.add(detailsPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void applyTheme() {
        // Apply theme to all components
        AppTheme.applyTheme(this);
        AppTheme.applyTheme(imageLabel);
        AppTheme.applyTheme(nameLabel);
        AppTheme.applyTheme(priceLabel);
        AppTheme.applyTheme(descriptionLabel);
        AppTheme.applyTheme(stockLabel);
        AppTheme.applyTheme(categoryLabel);
        AppTheme.applyTheme(quantitySpinner);
        AppTheme.applyTheme(addToCartButton);
        AppTheme.applyTheme(backButton);
        AppTheme.applyTheme(wishlistButton);
        
        // Custom styling for buttons
        addToCartButton.setBackground(AppTheme.SUCCESS_COLOR);
        addToCartButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addToCartButton.setBackground(AppTheme.SUCCESS_COLOR.darker());
            }
            public void mouseExited(MouseEvent e) {
                addToCartButton.setBackground(AppTheme.SUCCESS_COLOR);
            }
        });
        
        wishlistButton.setBackground(AppTheme.ACCENT_COLOR);
        wishlistButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                wishlistButton.setBackground(AppTheme.ACCENT_COLOR.darker());
            }
            public void mouseExited(MouseEvent e) {
                wishlistButton.setBackground(AppTheme.ACCENT_COLOR);
            }
        });
    }

    public void setProductDetails(Product product) {
        this.currentProduct = product;
        this.imageUrl = product.getImageUrl();
        nameLabel.setText(product.getName());
        priceLabel.setText(String.format("$%.2f", product.getPrice()));
        descriptionLabel.setText("<html><body style='width: 300px'>" + product.getDescription() + "</body></html>");
        categoryLabel.setText("Category: " + product.getCategory().getName());
        stockLabel.setText("In Stock: " + product.getStockQuantity());
        stockLabel.setForeground(product.getStockQuantity() > 0 ? AppTheme.SUCCESS_COLOR : AppTheme.ERROR_COLOR);
        loadImage();
    }

    private void loadImage() {
        new Thread(() -> {
            try {
                // Get the absolute path to the media folder
                File mediaFolder = new File("media");
                if (!mediaFolder.exists()) {
                    System.out.println("Media folder not found at: " + mediaFolder.getAbsolutePath());
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setIcon(null);
                        imageLabel.setText("Media folder not found");
                        imageLabel.setFont(AppTheme.HEADER_FONT);
                    });
                    return;
                }

                // List all files in the media folder for debugging
                System.out.println("Files in media folder:");
                File[] files = mediaFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        System.out.println("- " + file.getName());
                    }
                }

                // Try to find the image file
                String productName = currentProduct.getName();
                File imageFile = null;
                
                // First try exact match
                String pngPath = mediaFolder.getAbsolutePath() + File.separator + productName + ".png";
                String webpPath = mediaFolder.getAbsolutePath() + File.separator + productName + ".webp";
                
                System.out.println("Looking for image at:");
                System.out.println("- PNG: " + pngPath);
                System.out.println("- WebP: " + webpPath);

                if (new File(pngPath).exists()) {
                    imageFile = new File(pngPath);
                } else if (new File(webpPath).exists()) {
                    imageFile = new File(webpPath);
                } else {
                    // Try case-insensitive search
                    if (files != null) {
                        for (File file : files) {
                            String fileName = file.getName().toLowerCase();
                            if (fileName.equals(productName.toLowerCase() + ".png") || 
                                fileName.equals(productName.toLowerCase() + ".webp")) {
                                imageFile = file;
                                break;
                            }
                        }
                    }
                }

                if (imageFile == null || !imageFile.exists()) {
                    System.out.println("Image file not found for product: " + productName);
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setIcon(null);
                        imageLabel.setText("Image not found: " + productName);
                        imageLabel.setFont(AppTheme.HEADER_FONT);
                    });
                    return;
                }

                System.out.println("Loading image from: " + imageFile.getAbsolutePath());
                
                // Try loading the image using ImageIO
                try {
                    BufferedImage image = ImageIO.read(imageFile);
                    if (image != null) {
                        System.out.println("Successfully loaded image: " + imageFile.getName());
                        Image scaledImage = image.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                        SwingUtilities.invokeLater(() -> {
                            imageLabel.setIcon(new ImageIcon(scaledImage));
                            imageLabel.setText("");
                        });
                    } else {
                        throw new Exception("ImageIO.read returned null");
                    }
                } catch (Exception e) {
                    System.out.println("Failed to read image using ImageIO: " + e.getMessage());
                    // Try alternative loading method
                    try {
                        ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                        if (icon.getIconWidth() > 0) {
                            System.out.println("Successfully loaded image using ImageIcon: " + imageFile.getName());
                            Image scaledImage = icon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                            SwingUtilities.invokeLater(() -> {
                                imageLabel.setIcon(new ImageIcon(scaledImage));
                                imageLabel.setText("");
                            });
                        } else {
                            throw new Exception("ImageIcon width is 0");
                        }
                    } catch (Exception e2) {
                        System.out.println("Failed to read image using ImageIcon: " + e2.getMessage());
                        SwingUtilities.invokeLater(() -> {
                            imageLabel.setIcon(null);
                            imageLabel.setText("Error loading image");
                            imageLabel.setFont(AppTheme.HEADER_FONT);
                        });
                    }
                }
            } catch (Exception e) {
                System.out.println("Error loading image for " + currentProduct.getName() + ": " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(null);
                    imageLabel.setText("Error loading image");
                    imageLabel.setFont(AppTheme.HEADER_FONT);
                });
            }
        }).start();
    }

    private void handleAddToCart() {
        if (currentProduct != null) {
            int quantity = (Integer) quantitySpinner.getValue();
            if (quantity > currentProduct.getStockQuantity()) {
                JOptionPane.showMessageDialog(this,
                    "Sorry, only " + currentProduct.getStockQuantity() + " items available in stock.",
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (BiConsumer<Product, Integer> listener : addToCartListeners) {
                listener.accept(currentProduct, quantity);
            }
            JOptionPane.showMessageDialog(this,
                quantity + " " + currentProduct.getName() + "(s) added to cart!",
                "Added to Cart",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void toggleWishlist() {
        isInWishlist = !isInWishlist;
        wishlistButton.setText(isInWishlist ? "♥ Remove from Wishlist" : "♡ Add to Wishlist");
        // TODO: Implement wishlist functionality
    }

    public void addBackListener(Runnable listener) {
        backListeners.add(listener);
    }

    public void addAddToCartListener(BiConsumer<Product, Integer> listener) {
        addToCartListeners.add(listener);
    }

    private void notifyBack() {
        for (Runnable listener : backListeners) {
            listener.run();
        }
    }
} 