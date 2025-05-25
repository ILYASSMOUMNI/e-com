package com.yourname.ecommerce.models;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private String description;
    private Category parent;
    private List<Category> subCategories;
    private List<Product> products;

    public Category() {
    }

    public Category(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subCategories = new ArrayList<>();
        this.products = new ArrayList<>();
    }

    public Category(int id, String name, String description, Category parent) {
        this(id, name, description);
        this.parent = parent;
        if (parent != null) {
            parent.addSubCategory(this);
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public List<Category> getSubCategories() { return subCategories; }
    public void setSubCategories(List<Category> subCategories) { this.subCategories = subCategories; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public void addSubCategory(Category category) {
        if (!subCategories.contains(category)) {
            subCategories.add(category);
        }
    }

    public void addProduct(Product product) {
        if (!products.contains(product)) {
            products.add(product);
        }
    }

    public boolean isRootCategory() {
        return parent == null;
    }

    public boolean isLeafCategory() {
        return subCategories.isEmpty();
    }

    public String getFullPath() {
        if (isRootCategory()) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }

    @Override
    public String toString() {
        return name;
    }
} 