package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.Category;
import com.yourname.ecommerce.dao.CategoryDAO;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;
    
    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }
    
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }
    
    public Category getCategoryById(int id) {
        return categoryDAO.findById(id);
    }
    
    public Category createCategory(Category category) {
        return categoryDAO.create(category);
    }
    
    public Category updateCategory(Category category) {
        return categoryDAO.update(category);
    }
    
    public boolean deleteCategory(int id) {
        return categoryDAO.delete(id);
    }
} 