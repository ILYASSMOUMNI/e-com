package com.yourname.ecommerce.dao;

import com.yourname.ecommerce.models.Product;
import com.yourname.ecommerce.models.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {
    private List<Product> products;
    private final CategoryDAO categoryDAO;
    
    public ProductDAO() {
        this.products = new ArrayList<>();
        this.categoryDAO = new CategoryDAO();
        initializeSampleProducts();
    }
    
    private void initializeSampleProducts() {
        // Get categories
        Category electronics = categoryDAO.findById(1);
        Category clothing = categoryDAO.findById(2);
        Category books = categoryDAO.findById(3);
        Category homeKitchen = categoryDAO.findById(4);
        
        // Electronics
        products.add(new Product(1, "iPhone 13", "Latest Apple smartphone with A15 Bionic chip", 999.99, 50, 
            "media/iPhone 13.png", electronics));
        products.add(new Product(2, "Samsung Galaxy S21", "Powerful Android smartphone", 799.99, 45, 
            "media/Samsung Galaxy S21.png", electronics));
        products.add(new Product(3, "MacBook Pro", "Professional laptop for developers", 1299.99, 30, 
            "media/MacBook Pro.png", electronics));
        
        // Clothing
        products.add(new Product(4, "Men's T-Shirt", "Cotton casual t-shirt", 29.99, 100, 
            "media/Men's T-Shirt.png", clothing));
        products.add(new Product(5, "Women's Dress", "Elegant summer dress", 59.99, 75, 
            "media/Women's Dress.png", clothing));
        
        // Books
        products.add(new Product(6, "Java Programming", "Complete guide to Java development", 49.99, 60, 
            "media/Java Programming.png", books));
        products.add(new Product(7, "Data Structures", "Advanced data structures and algorithms", 39.99, 40, 
            "media/Data Structures.png", books));
        
        // Home & Kitchen
        products.add(new Product(8, "Coffee Maker", "Automatic coffee maker", 89.99, 25, 
            "media/Coffee Maker.png", homeKitchen));
        products.add(new Product(9, "Blender", "High-speed blender", 69.99, 35, 
            "media/Blender.png", homeKitchen));
    }
    
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }
    
    public Product findById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public Product create(Product product) {
        products.add(product);
        return product;
    }
    
    public Product update(Product product) {
        Optional<Product> existingProduct = products.stream()
                .filter(p -> p.getId() == product.getId())
                .findFirst();
                
        if (existingProduct.isPresent()) {
            int index = products.indexOf(existingProduct.get());
            products.set(index, product);
            return product;
        }
        return null;
    }
    
    public boolean delete(int id) {
        return products.removeIf(p -> p.getId() == id);
    }
    
    public List<Product> findByCategory(Category category) {
        return products.stream()
                .filter(p -> p.getCategory().getId() == category.getId())
                .toList();
    }
} 