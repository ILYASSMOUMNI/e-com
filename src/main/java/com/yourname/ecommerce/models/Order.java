package com.yourname.ecommerce.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private User user;
    private List<CartItem> items;
    private double subtotal;
    private double tax;
    private double total;
    private OrderStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Address shippingAddress;
    private Address billingAddress;
    private PaymentMethod paymentMethod;
    private String trackingNumber;
    private String notes;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        BANK_TRANSFER
    }

    public Order(int id, User user, List<CartItem> items, Address shippingAddress, 
                Address billingAddress, PaymentMethod paymentMethod) {
        this.id = id;
        this.user = user;
        this.items = new ArrayList<>(items);
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
        this.status = OrderStatus.PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        calculateTotals();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { 
        this.items = new ArrayList<>(items);
        calculateTotals();
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { 
        this.status = status;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }

    public Address getBillingAddress() { return billingAddress; }
    public void setBillingAddress(Address billingAddress) { this.billingAddress = billingAddress; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    private void calculateTotals() {
        subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        tax = items.stream().mapToDouble(CartItem::getTaxAmount).sum();
        total = subtotal + tax;
    }

    public void addItem(CartItem item) {
        items.add(item);
        calculateTotals();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        calculateTotals();
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (canBeCancelled()) {
            setStatus(OrderStatus.CANCELLED);
        } else {
            throw new IllegalStateException("Order cannot be cancelled in its current state");
        }
    }

    public void process() {
        if (status == OrderStatus.CONFIRMED) {
            setStatus(OrderStatus.PROCESSING);
        } else {
            throw new IllegalStateException("Order must be confirmed before processing");
        }
    }

    public void ship(String trackingNumber) {
        if (status == OrderStatus.PROCESSING) {
            this.trackingNumber = trackingNumber;
            setStatus(OrderStatus.SHIPPED);
        } else {
            throw new IllegalStateException("Order must be processing before shipping");
        }
    }

    public void deliver() {
        if (status == OrderStatus.SHIPPED) {
            setStatus(OrderStatus.DELIVERED);
        } else {
            throw new IllegalStateException("Order must be shipped before delivery");
        }
    }

    public void refund() {
        if (status == OrderStatus.DELIVERED) {
            setStatus(OrderStatus.REFUNDED);
        } else {
            throw new IllegalStateException("Order must be delivered before refund");
        }
    }

    @Override
    public String toString() {
        return String.format("Order #%d - %s - $%.2f", id, status, total);
    }
} 