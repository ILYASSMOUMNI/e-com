package com.yourname.ecommerce.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {
    private int id;
    private User user;
    private List<CartItem> items;
    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;
    private double totalAmount;
    private double taxAmount;
    private static final double TAX_RATE = 0.20; // 20% tax rate

    public ShoppingCart(int id, User user) {
        this.id = id;
        this.user = user;
        this.items = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
        this.totalAmount = 0.0;
        this.taxAmount = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { 
        this.items = new ArrayList<>(items);
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    // Cart Operations
    public void addItem(Product product, int quantity) {
        CartItem existingItem = findItem(product);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(items.size() + 1, product, quantity);
            items.add(newItem);
        }
        calculateTotals();
        this.updatedAt = new Date();
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getId() == productId);
        calculateTotals();
        this.updatedAt = new Date();
    }

    public void updateQuantity(int productId, int quantity) {
        items.stream()
            .filter(item -> item.getProduct().getId() == productId)
            .findFirst()
            .ifPresent(item -> {
                if (quantity <= 0) {
                    removeItem(productId);
                } else {
                    item.setQuantity(quantity);
                }
            });
        calculateTotals();
        this.updatedAt = new Date();
    }

    public void clear() {
        items.clear();
        calculateTotals();
        this.updatedAt = new Date();
    }

    // Calculations
    public double getSubtotal() {
        return totalAmount - taxAmount;
    }

    public double getTax() {
        return taxAmount;
    }

    public double getTotal() {
        return totalAmount;
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsProduct(int productId) {
        return items.stream().anyMatch(item -> item.getProduct().getId() == productId);
    }

    public CartItem getItem(int productId) {
        return items.stream()
            .filter(item -> item.getProduct().getId() == productId)
            .findFirst()
            .orElse(null);
    }

    private CartItem findItem(Product product) {
        return items.stream()
                .filter(item -> item.getProduct().getId() == product.getId())
                .findFirst()
                .orElse(null);
    }

    private void calculateTotals() {
        totalAmount = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        taxAmount = totalAmount * TAX_RATE;
    }

    @Override
    public String toString() {
        return String.format("Cart #%d - %d items - $%.2f", id, getItemCount(), getTotal());
    }
} 