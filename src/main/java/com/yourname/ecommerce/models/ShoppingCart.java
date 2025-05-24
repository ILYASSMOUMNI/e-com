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

    public ShoppingCart(int id, User user) {
        this.id = id;
        this.user = user;
        this.items = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
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

    // Cart Operations
    public void addItem(Product product, int quantity) {
        Optional<CartItem> existingItem = items.stream()
            .filter(item -> item.getProduct().getId() == product.getId())
            .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().incrementQuantity(quantity);
        } else {
            items.add(new CartItem(items.size() + 1, product, quantity));
        }
        this.updatedAt = new Date();
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getId() == productId);
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
        this.updatedAt = new Date();
    }

    public void clear() {
        items.clear();
        this.updatedAt = new Date();
    }

    // Calculations
    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public double getTax() {
        return items.stream().mapToDouble(CartItem::getTaxAmount).sum();
    }

    public double getTotal() {
        return getSubtotal() + getTax();
    }

    public int getItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
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

    @Override
    public String toString() {
        return String.format("Cart #%d - %d items - $%.2f", id, getItemCount(), getTotal());
    }
} 