package com.yourname.ecommerce.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {
    // Create
    T create(T entity);
    
    // Read
    Optional<T> findById(int id);
    List<T> findAll();
    
    // Update
    T update(T entity);
    
    // Delete
    boolean delete(int id);
    
    // Additional common operations
    boolean exists(int id);
    int count();
} 