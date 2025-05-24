package com.yourname.ecommerce.models;

public class CartItem {
    private int id;
    private Product product;
    private int quantity;
    private double price;
    private double tax;

    public CartItem(int id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice();
        this.tax = product.getPrice() * product.getTaxRate();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        if (quantity > 0 && quantity <= product.getStockQuantity()) {
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("Invalid quantity");
        }
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getSubtotal() {
        return price * quantity;
    }

    public double getTaxAmount() {
        return tax * quantity;
    }

    public double getTotal() {
        return getSubtotal() + getTaxAmount();
    }

    public void incrementQuantity() {
        setQuantity(quantity + 1);
    }

    public void decrementQuantity() {
        if (quantity > 1) {
            setQuantity(quantity - 1);
        }
    }

    public void incrementQuantity(int amount) {
        setQuantity(this.quantity + amount);
    }

    @Override
    public String toString() {
        return String.format("%s x%d - $%.2f", product.getName(), quantity, getTotal());
    }
} 