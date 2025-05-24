package com.yourname.ecommerce.models;

import java.util.Date;

public class Review {
    private int id;
    private User user;
    private Product product;
    private int rating;
    private String comment;
    private Date createdAt;
    private boolean verifiedPurchase;
    private boolean helpful;
    private int helpfulCount;

    public Review(int id, User user, Product product, int rating, String comment) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = new Date();
        this.verifiedPurchase = false;
        this.helpful = false;
        this.helpfulCount = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getRating() { return rating; }
    public void setRating(int rating) { 
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        } else {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }

    public boolean isHelpful() { return helpful; }
    public void setHelpful(boolean helpful) { this.helpful = helpful; }

    public int getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }

    public void markAsHelpful() {
        this.helpful = true;
        this.helpfulCount++;
    }

    public void markAsUnhelpful() {
        this.helpful = false;
        this.helpfulCount--;
    }

    @Override
    public String toString() {
        return String.format("Rating: %d/5 - %s", rating, comment);
    }
} 