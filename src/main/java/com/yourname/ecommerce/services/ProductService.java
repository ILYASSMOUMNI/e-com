package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.Product;
import com.yourname.ecommerce.models.Category;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> products;
    private List<Category> categories;

    public ProductService() {
        initializeCategories();
        initializeProducts();
    }

    private void initializeCategories() {
        categories = new ArrayList<>();
        categories.add(new Category(1, "Electronics", "Electronic devices and accessories"));
        categories.add(new Category(2, "Clothing", "Fashion and apparel"));
        categories.add(new Category(3, "Books", "Books and publications"));
        categories.add(new Category(4, "Home & Kitchen", "Home appliances and kitchenware"));
    }

    private void initializeProducts() {
        products = new ArrayList<>();
        
        // Electronics
        products.add(new Product(1, "iPhone 13", "Latest Apple smartphone with A15 Bionic chip", 999.99, 50, 
            "https://example.com/iphone13.jpg", categories.get(0)));
        products.add(new Product(2, "Samsung Galaxy S21", "Powerful Android smartphone", 799.99, 45, 
            "https://example.com/galaxys21.jpg", categories.get(0)));
        products.add(new Product(3, "MacBook Pro", "Professional laptop for developers", 1299.99, 30, 
            "https://example.com/macbook.jpg", categories.get(0)));
        
        // Clothing
        products.add(new Product(4, "Men's T-Shirt", "Cotton casual t-shirt", 29.99, 100, 
            "https://example.com/tshirt.jpg", categories.get(1)));
        products.add(new Product(5, "Women's Dress", "Elegant summer dress", 59.99, 75, 
            "https://example.com/dress.jpg", categories.get(1)));
        
        // Books
        products.add(new Product(6, "Java Programming", "Complete guide to Java development", 49.99, 60, 
            "https://example.com/javabook.jpg", categories.get(2)));
        products.add(new Product(7, "Data Structures", "Advanced data structures and algorithms", 39.99, 40, 
            "https://example.com/dsbook.jpg", categories.get(2)));
        
        // Home & Kitchen
        products.add(new Product(8, "Coffee Maker", "Automatic coffee maker", 89.99, 25, 
            "https://example.com/coffee.jpg", categories.get(3)));
        products.add(new Product(9, "Blender", "High-speed blender", 69.99, 35, 
            "https://example.com/blender.jpg", categories.get(3)));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public Product getProductById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return products.stream()
                .filter(p -> p.getCategory().getId() == categoryId)
                .toList();
    }
} 