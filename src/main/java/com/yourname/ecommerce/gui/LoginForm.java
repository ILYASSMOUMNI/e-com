package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.services.AuthService;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthService authService;
    
    public LoginForm() {
        authService = new AuthService();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("E-Commerce Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton, gbc);
        
        // Register Button
        gbc.gridy = 3;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegister());
        mainPanel.add(registerButton, gbc);
        
        add(mainPanel);
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (authService.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            openProductCatalog();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegister() {
        // TODO: Implement registration form
        JOptionPane.showMessageDialog(this, "Registration functionality coming soon!");
    }
    
    private void openProductCatalog() {
        ProductCatalog catalog = new ProductCatalog();
        catalog.setVisible(true);
        this.dispose();
    }
} 