package com.yourname.ecommerce.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserProfile extends JPanel {
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton saveButton;
    private JButton cancelButton;

    public UserProfile() {
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        loadUserData();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        fullNameField = new JTextField(20);
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        saveButton = new JButton("Save Changes");
        cancelButton = new JButton("Cancel");

        // Add action listeners
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> cancelChanges());
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Profile Information
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Profile Information", SwingConstants.LEFT), gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Password Change Section
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Change Password", SwingConstants.LEFT), gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        formPanel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(currentPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(newPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Confirm New Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add panels to main panel
        add(new JLabel("User Profile", SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        // TODO: Load user data from service
        // This is a placeholder for demonstration
        usernameField.setText("currentUser");
        fullNameField.setText("John Doe");
        emailField.setText("john.doe@example.com");
    }

    private void saveChanges() {
        // Validate form fields
        if (!validateFields()) {
            return;
        }

        // TODO: Implement save changes logic
        JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateFields() {
        if (usernameField.getText().isEmpty() || fullNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if password change is being attempted
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all password fields", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (newPassword.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private void cancelChanges() {
        // TODO: Implement navigation back to previous screen
        Container parent = getParent();
        if (parent instanceof JFrame) {
            JFrame frame = (JFrame) parent;
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new MainFrame());
            frame.revalidate();
            frame.repaint();
        }
    }
} 