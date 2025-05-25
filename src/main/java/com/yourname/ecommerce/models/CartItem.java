package com.yourname.ecommerce.models;

public class CartItem {
    private int id;
    private Product product;
    private int quantity;
    private double subtotal;
    private double taxAmount;
    private static final double TAX_RATE = 0.20; // 20% tax rate

    public CartItem() {
        this.quantity = 1;
    }

    public CartItem(int id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        calculateTotals();
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        calculateTotals();
    }

    private void calculateTotals() {
        this.subtotal = product.getPrice() * quantity;
        this.taxAmount = subtotal * TAX_RATE;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { 
        this.product = product;
        calculateTotals();
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        calculateTotals();
    }

    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }

    @Override
    public String toString() {
        return String.format("%s x %d = $%.2f", product.getName(), quantity, subtotal);
    }
} 