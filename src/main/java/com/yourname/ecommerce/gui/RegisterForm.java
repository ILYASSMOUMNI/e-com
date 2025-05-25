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
        applyTheme();
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
        
        // Add input validation listeners
        addInputValidationListeners();
    }

    private void addInputValidationListeners() {
        // Email validation
        emailField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
        });

        // Password validation
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
        });

        // Phone validation
        phoneField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
        });
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            emailField.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_COLOR));
        } else {
            emailField.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR));
        }
    }

    private void validatePassword() {
        String password = new String(passwordField.getPassword());
        if (!password.isEmpty() && password.length() < 8) {
            passwordField.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_COLOR));
        } else {
            passwordField.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR));
        }
    }

    private void validatePhone() {
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("^\\+?[0-9]{10,15}$")) {
            phoneField.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_COLOR));
        } else {
            phoneField.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR));
        }
    }

    private void setupLayout() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(AppTheme.PANEL_BORDER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add title
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.TITLE_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        // Add components to form panel
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel("First Name:");
        formPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Last Name:");
        formPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel phoneLabel = new JLabel("Phone:");
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Password:");
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Add form panel to main panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(formPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    private void applyTheme() {
        // Apply theme to all components
        AppTheme.applyTheme(this);
        AppTheme.applyTheme(usernameField);
        AppTheme.applyTheme(passwordField);
        AppTheme.applyTheme(confirmPasswordField);
        AppTheme.applyTheme(emailField);
        AppTheme.applyTheme(firstNameField);
        AppTheme.applyTheme(lastNameField);
        AppTheme.applyTheme(phoneField);
        AppTheme.applyTheme(registerButton);
        AppTheme.applyTheme(backToLoginButton);
        
        // Custom styling for register button
        registerButton.setBackground(AppTheme.SUCCESS_COLOR);
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(AppTheme.SUCCESS_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(AppTheme.SUCCESS_COLOR);
            }
        });
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

        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
            JOptionPane.showMessageDialog(this, 
                "Invalid phone number format", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Attempt to register the user
        User newUser = authService.register(username, password, email, firstName, lastName, phone);
        
        if (newUser != null) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! Please login with your new account.", 
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