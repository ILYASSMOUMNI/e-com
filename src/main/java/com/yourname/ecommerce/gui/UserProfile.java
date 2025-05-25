package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.User;
import javax.swing.*;
import javax.swing.border.*;
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
    private User currentUser;
    private JLabel titleLabel;
    private JLabel profileInfoLabel;
    private JLabel passwordLabel;

    public UserProfile() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BACKGROUND_COLOR);
        initializeComponents();
        setupLayout();
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadUserData();
    }

    private void initializeComponents() {
        // Initialize fields
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        fullNameField = new JTextField(20);
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        // Initialize buttons
        saveButton = new JButton("Save Changes");
        cancelButton = new JButton("Cancel");
        
        // Initialize labels
        titleLabel = new JLabel("User Profile", SwingConstants.CENTER);
        profileInfoLabel = new JLabel("Profile Information");
        passwordLabel = new JLabel("Change Password");

        // Style components
        styleComponents();

        // Add action listeners
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> cancelChanges());
        
        // Add input validation listeners
        addValidationListeners();
    }

    private void styleComponents() {
        // Style title
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.PRIMARY_COLOR);
        
        // Style section headers
        profileInfoLabel.setFont(AppTheme.HEADER_FONT);
        profileInfoLabel.setForeground(AppTheme.PRIMARY_COLOR);
        passwordLabel.setFont(AppTheme.HEADER_FONT);
        passwordLabel.setForeground(AppTheme.PRIMARY_COLOR);
        
        // Style text fields
        AppTheme.applyTheme(usernameField);
        AppTheme.applyTheme(emailField);
        AppTheme.applyTheme(fullNameField);
        AppTheme.applyTheme(currentPasswordField);
        AppTheme.applyTheme(newPasswordField);
        AppTheme.applyTheme(confirmPasswordField);
        
        // Style buttons
        AppTheme.applyTheme(saveButton);
        AppTheme.applyTheme(cancelButton);
    }

    private void addValidationListeners() {
        // Email validation
        emailField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            
            private void validateEmail() {
                String email = emailField.getText();
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    emailField.setBorder(BorderFactory.createLineBorder(AppTheme.ERROR_COLOR));
                } else {
                    emailField.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR));
                }
            }
        });
    }

    private void setupLayout() {
        // Create main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel with card-like appearance
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(AppTheme.PANEL_BORDER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Profile Information Section
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(profileInfoLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Username
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy++;
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(fullNameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Password Change Section
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(passwordLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Current Password
        gbc.gridx = 0;
        JLabel currentPassLabel = new JLabel("Current Password:");
        currentPassLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(currentPassLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0; gbc.gridy++;
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(newPassLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy++;
        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        confirmPassLabel.setFont(AppTheme.NORMAL_FONT);
        formPanel.add(confirmPassLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Button panel with spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add panels to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadUserData() {
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
        }
    }

    private void saveChanges() {
        // Validate form fields
        if (!validateFields()) {
            return;
        }

        // TODO: Implement save changes logic
        JOptionPane.showMessageDialog(this, 
            "Profile updated successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateFields() {
        if (usernameField.getText().isEmpty() || fullNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, 
                "Invalid email format", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if password change is being attempted
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all password fields", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "New passwords do not match", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (newPassword.length() < 8) {
                JOptionPane.showMessageDialog(this, 
                    "Password must be at least 8 characters long", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
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