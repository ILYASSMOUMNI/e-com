package com.yourname.ecommerce.dao;

import com.yourname.ecommerce.models.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {
    private List<Category> categories;
    
    public CategoryDAO() {
        this.categories = new ArrayList<>();
        initializeDefaultCategories();
    }
    
    private void initializeDefaultCategories() {
        categories.add(new Category(1, "Electronics", "Electronic devices and accessories"));
        categories.add(new Category(2, "Clothing", "Fashion and apparel"));
        categories.add(new Category(3, "Books", "Books and publications"));
        categories.add(new Category(4, "Home & Kitchen", "Home appliances and kitchenware"));
    }
    
    public List<Category> findAll() {
        return new ArrayList<>(categories);
    }
    
    public Category findById(int id) {
        return categories.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public Category create(Category category) {
        categories.add(category);
        return category;
    }
    
    public Category update(Category category) {
        Optional<Category> existingCategory = categories.stream()
                .filter(c -> c.getId() == category.getId())
                .findFirst();
                
        if (existingCategory.isPresent()) {
            int index = categories.indexOf(existingCategory.get());
            categories.set(index, category);
            return category;
        }
        return null;
    }
    
    public boolean delete(int id) {
        return categories.removeIf(c -> c.getId() == id);
    }
} 