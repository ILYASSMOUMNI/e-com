package com.yourname.ecommerce.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LoginForm extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private List<Runnable> loginSuccessListeners;
    private List<Runnable> registerClickListeners;

    public LoginForm() {
        setLayout(new BorderLayout());
        loginSuccessListeners = new ArrayList<>();
        registerClickListeners = new ArrayList<>();
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
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to form panel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Add panels to main panel
        add(new JLabel("Login", SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: Implement actual login logic here
        // For now, we'll just simulate a successful login
        notifyLoginSuccess();
    }

    public void addLoginSuccessListener(Runnable listener) {
        loginSuccessListeners.add(listener);
    }

    public void addRegisterClickListener(Runnable listener) {
        registerClickListeners.add(listener);
    }

    private void notifyLoginSuccess() {
        for (Runnable listener : loginSuccessListeners) {
            listener.run();
        }
    }

    private void notifyRegisterClick() {
        for (Runnable listener : registerClickListeners) {
            listener.run();
        }
    }
} 