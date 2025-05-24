package com.yourname.ecommerce.models;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String imageUrl;
    private Category category;
    private List<String> imageUrls;
    private double rating;
    private int reviewCount;
    private ProductStatus status;
    private int reorderLevel;
    private double taxRate;
    private List<Review> reviews;

    public enum ProductStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK,
        DISCONTINUED
    }

    public Product(int id, String name, String description, double price, int stockQuantity, 
                  String imageUrl, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.category = category;
        this.imageUrls = new ArrayList<>();
        this.imageUrls.add(imageUrl);
        this.rating = 0.0;
        this.reviewCount = 0;
        this.status = ProductStatus.IN_STOCK;
        this.reorderLevel = 10;
        this.taxRate = 0.20; // 20% tax rate
        this.reviews = new ArrayList<>();
        updateStatus();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { 
        this.stockQuantity = stockQuantity;
        updateStatus();
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public double getPriceWithTax() {
        return price * (1 + taxRate);
    }

    public void addReview(Review review) {
        reviews.add(review);
        updateRating();
    }

    private void updateRating() {
        if (reviews.isEmpty()) {
            rating = 0.0;
            reviewCount = 0;
        } else {
            double sum = reviews.stream().mapToDouble(Review::getRating).sum();
            rating = sum / reviews.size();
            reviewCount = reviews.size();
        }
    }

    private void updateStatus() {
        if (stockQuantity <= 0) {
            status = ProductStatus.OUT_OF_STOCK;
        } else if (stockQuantity <= reorderLevel) {
            status = ProductStatus.LOW_STOCK;
        } else {
            status = ProductStatus.IN_STOCK;
        }
    }

    public boolean needsReorder() {
        return stockQuantity <= reorderLevel;
    }

    @Override
    public String toString() {
        return name + " - $" + price;
    }
} 