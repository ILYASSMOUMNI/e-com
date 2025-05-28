package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;

public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    private UserService userService;
    
    public UserManagementPanel(UserService userService) {
        this.userService = userService;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(AppTheme.BACKGROUND_COLOR);
        initializeComponents();
        setupLayout();
        loadUsers();
    }
    
    private void initializeComponents() {
        // Initialize table model
        String[] columns = {"ID", "Username", "Email", "First Name", "Last Name", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(28);
        userTable.setFont(AppTheme.NORMAL_FONT);
        userTable.setBackground(Color.WHITE);
        userTable.setShowGrid(false);
        userTable.setForeground(AppTheme.TEXT_COLOR);
        
        // Style table header
        JTableHeader header = userTable.getTableHeader();
        header.setFont(AppTheme.HEADER_FONT);
        header.setBackground(AppTheme.PRIMARY_COLOR);
        header.setForeground(AppTheme.TEXT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Alternating row colors
        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(AppTheme.TEXT_COLOR);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                } else {
                    c.setBackground(new Color(220, 235, 252));
                }
                return c;
            }
        });
        
        // Initialize buttons
        addButton = new JButton("Add User");
        editButton = new JButton("Edit User");
        deleteButton = new JButton("Delete User");
        refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        refreshButton.addActionListener(e -> loadUsers());
        
        // Style buttons
        AppTheme.applyTheme(addButton);
        AppTheme.applyTheme(editButton);
        AppTheme.applyTheme(deleteButton);
        AppTheme.applyTheme(refreshButton);
    }
    
    private void setupLayout() {
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Style button panel
        buttonPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Add components to panel
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add User", true);
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<User.UserRole> roleCombo = new JComboBox<>(User.UserRole.values());
        
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setFirstName(firstNameField.getText());
            user.setLastName(lastNameField.getText());
            user.setPhone(phoneField.getText());
            user.setPasswordHash(new String(passwordField.getPassword())); // In a real app, hash the password
            user.setRole((User.UserRole) roleCombo.getSelectedItem());
            user.setActive(true);
            
            userService.createUser(user);
            loadUsers();
            dialog.dispose();
        });
        
        panel.add(saveButton);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        User user = userService.getUserById(userId);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        
        JTextField usernameField = new JTextField(user.getUsername());
        JTextField emailField = new JTextField(user.getEmail());
        JTextField firstNameField = new JTextField(user.getFirstName());
        JTextField lastNameField = new JTextField(user.getLastName());
        JTextField phoneField = new JTextField(user.getPhone());
        JComboBox<User.UserRole> roleCombo = new JComboBox<>(User.UserRole.values());
        roleCombo.setSelectedItem(user.getRole());
        JCheckBox activeCheckBox = new JCheckBox("Active", user.isActive());
        
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Status:"));
        panel.add(activeCheckBox);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setFirstName(firstNameField.getText());
            user.setLastName(lastNameField.getText());
            user.setPhone(phoneField.getText());
            user.setRole((User.UserRole) roleCombo.getSelectedItem());
            user.setActive(activeCheckBox.isSelected());
            
            userService.updateUser(user);
            loadUsers();
            dialog.dispose();
        });
        
        panel.add(saveButton);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this user?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            userService.deleteUser(userId);
            loadUsers();
        }
    }
    
    public void refreshData() {
        loadUsers();
    }
} 