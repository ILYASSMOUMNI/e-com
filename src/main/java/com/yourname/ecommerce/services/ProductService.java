package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.Product;
import com.yourname.ecommerce.models.Category;
import com.yourname.ecommerce.dao.ProductDAO;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;
    
    public ProductService() {
        this.productDAO = new ProductDAO();
    }
    
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
    
    public Product getProductById(int id) {
        return productDAO.findById(id);
    }
    
    public Product createProduct(Product product) {
        return productDAO.create(product);
    }
    
    public Product updateProduct(Product product) {
        return productDAO.update(product);
    }
    
    public boolean deleteProduct(int id) {
        return productDAO.delete(id);
    }
    
    public List<Product> getProductsByCategory(Category category) {
        return productDAO.findByCategory(category);
    }
    
    public List<Category> getAllCategories() {
        return new CategoryService().getAllCategories();
    }
} 