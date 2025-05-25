package com.yourname.ecommerce.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import com.yourname.ecommerce.models.Product;

public class ProductDetail extends JPanel {
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel descriptionLabel;
    private JSpinner quantitySpinner;
    private JButton addToCartButton;
    private JButton backButton;
    private List<Runnable> backListeners;
    private List<BiConsumer<Product, Integer>> addToCartListeners;
    private String imageUrl;
    private Product currentProduct;

    public ProductDetail() {
        setLayout(new BorderLayout());
        backListeners = new ArrayList<>();
        addToCartListeners = new ArrayList<>();
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 400));
        
        nameLabel = new JLabel();
        priceLabel = new JLabel();
        descriptionLabel = new JLabel();
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 99, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        
        addToCartButton = new JButton("Add to Cart");
        backButton = new JButton("Back to Catalog");

        addToCartButton.addActionListener(e -> handleAddToCart());
        backButton.addActionListener(e -> notifyBack());
    }

    private void setupLayout() {
        // Image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Product info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(descriptionLabel);
        detailsPanel.add(infoPanel, BorderLayout.CENTER);

        // Quantity and buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Quantity:"));
        controlPanel.add(quantitySpinner);
        controlPanel.add(addToCartButton);
        controlPanel.add(backButton);
        detailsPanel.add(controlPanel, BorderLayout.SOUTH);

        // Add panels to main panel
        add(imagePanel, BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.SOUTH);
    }

    public void setProductDetails(Product product) {
        this.currentProduct = product;
        this.imageUrl = product.getImageUrl();
        nameLabel.setText(product.getName());
        priceLabel.setText(String.format("$%.2f", product.getPrice()));
        descriptionLabel.setText("<html><body style='width: 300px'>" + product.getDescription() + "</body></html>");
        loadImage();
    }

    private void loadImage() {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                BufferedImage image = ImageIO.read(url);
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setIcon(new ImageIcon(scaledImage));
                        imageLabel.setText("");
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setIcon(null);
                        imageLabel.setText("Image not available");
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(null);
                    imageLabel.setText("Error loading image");
                });
            }
        }).start();
    }

    private void handleAddToCart() {
        if (currentProduct != null) {
            int quantity = (Integer) quantitySpinner.getValue();
            for (BiConsumer<Product, Integer> listener : addToCartListeners) {
                listener.accept(currentProduct, quantity);
            }
        }
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