package com.yourname.ecommerce.services;

import com.yourname.ecommerce.models.User;
import com.yourname.ecommerce.dao.UserDAO;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    public User getUserById(int id) {
        return userDAO.findById(id).orElse(null);
    }
    
    public User createUser(User user) {
        return userDAO.create(user);
    }
    
    public User updateUser(User user) {
        return userDAO.update(user);
    }
    
    public boolean deleteUser(int id) {
        return userDAO.delete(id);
    }
    
    public User findByUsername(String username) {
        return userDAO.findByUsername(username).orElse(null);
    }
    
    public User findByEmail(String email) {
        return userDAO.findByEmail(email).orElse(null);
    }
    
    public User authenticateUser(String username, String password) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent() && user.get().getPasswordHash().equals(password)) {
            return user.get();
        }
        return null;
    }
    
    public User registerUser(User user) {
        if (userDAO.findByUsername(user.getUsername()).isPresent()) {
            return null;
        }
        return userDAO.create(user);
    }
} 