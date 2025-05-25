package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.User;
import com.yourname.ecommerce.services.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoginForm extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private List<Consumer<User>> loginSuccessListeners;
    private List<Runnable> registerClickListeners;
    private AuthService authService;

    public LoginForm() {
        setLayout(new BorderLayout());
        loginSuccessListeners = new ArrayList<>();
        registerClickListeners = new ArrayList<>();
        authService = new AuthService();
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register New Account");

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> notifyRegisterClick());
    }

    private void setupLayout() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel);

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authService.login(username, password);
        if (user != null) {
            notifyLoginSuccess(user);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addLoginSuccessListener(Consumer<User> listener) {
        loginSuccessListeners.add(listener);
    }

    public void addRegisterClickListener(Runnable listener) {
        registerClickListeners.add(listener);
    }

    private void notifyLoginSuccess(User user) {
        for (Consumer<User> listener : loginSuccessListeners) {
            listener.accept(user);
        }
    }

    private void notifyRegisterClick() {
        for (Runnable listener : registerClickListeners) {
            listener.run();
        }
    }
} 