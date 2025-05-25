package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.User;
import com.yourname.ecommerce.services.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RegisterForm extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton backToLoginButton;
    private List<Runnable> backToLoginListeners;
    private List<Consumer<User>> registrationSuccessListeners;
    private AuthService authService;

    public RegisterForm() {
        setLayout(new BorderLayout());
        backToLoginListeners = new ArrayList<>();
        registrationSuccessListeners = new ArrayList<>();
        authService = new AuthService();
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        emailField = new JTextField(20);
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        phoneField = new JTextField(20);
        registerButton = new JButton("Register");
        backToLoginButton = new JButton("Back to Login");

        // Add action listeners
        registerButton.addActionListener(e -> handleRegistration());
        backToLoginButton.addActionListener(e -> notifyBackToLogin());
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to form panel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);

        // Add panels to main panel
        add(new JLabel("Register New Account", SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate all fields
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || 
            firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 8 characters long", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, 
                "Invalid email format", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Attempt to register the user
        User newUser = authService.register(username, password, email, firstName, lastName, phone);
        
        if (newUser != null) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Notify registration success listeners
            for (Consumer<User> listener : registrationSuccessListeners) {
                listener.accept(newUser);
            }
            
            notifyBackToLogin();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed. Username or email may already exist.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addBackToLoginListener(Runnable listener) {
        backToLoginListeners.add(listener);
    }

    public void addRegistrationSuccessListener(Consumer<User> listener) {
        registrationSuccessListeners.add(listener);
    }

    private void notifyBackToLogin() {
        for (Runnable listener : backToLoginListeners) {
            listener.run();
        }
    }
} 