package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class CategoryManagementPanel extends JPanel {
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    private CategoryService categoryService;
    
    public CategoryManagementPanel(CategoryService categoryService) {
        this.categoryService = categoryService;
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        loadCategories();
    }
    
    private void initializeComponents() {
        // Initialize table model
        String[] columns = {"ID", "Name", "Description", "Parent Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Initialize buttons
        addButton = new JButton("Add Category");
        editButton = new JButton("Edit Category");
        deleteButton = new JButton("Delete Category");
        refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddCategoryDialog());
        editButton.addActionListener(e -> showEditCategoryDialog());
        deleteButton.addActionListener(e -> deleteSelectedCategory());
        refreshButton.addActionListener(e -> loadCategories());
    }
    
    private void setupLayout() {
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCategories() {
        tableModel.setRowCount(0);
        List<Category> categories = categoryService.getAllCategories();
        for (Category category : categories) {
            Object[] row = {
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getName() : "None"
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Category", true);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<Category> parentCategoryCombo = new JComboBox<>();
        parentCategoryCombo.addItem(null); // Add null option for no parent
        for (Category category : categoryService.getAllCategories()) {
            parentCategoryCombo.addItem(category);
        }
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Parent Category:"));
        panel.add(parentCategoryCombo);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            Category category = new Category();
            category.setName(nameField.getText());
            category.setDescription(descriptionField.getText());
            category.setParent((Category) parentCategoryCombo.getSelectedItem());
            
            categoryService.createCategory(category);
            loadCategories();
            dialog.dispose();
        });
        
        panel.add(saveButton);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showEditCategoryDialog() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
        Category category = categoryService.getCategoryById(categoryId);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Category", true);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField nameField = new JTextField(category.getName());
        JTextField descriptionField = new JTextField(category.getDescription());
        JComboBox<Category> parentCategoryCombo = new JComboBox<>();
        parentCategoryCombo.addItem(null); // Add null option for no parent
        for (Category cat : categoryService.getAllCategories()) {
            if (cat.getId() != category.getId()) { // Don't allow self as parent
                parentCategoryCombo.addItem(cat);
            }
        }
        parentCategoryCombo.setSelectedItem(category.getParent());
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Parent Category:"));
        panel.add(parentCategoryCombo);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            category.setName(nameField.getText());
            category.setDescription(descriptionField.getText());
            category.setParent((Category) parentCategoryCombo.getSelectedItem());
            
            categoryService.updateCategory(category);
            loadCategories();
            dialog.dispose();
        });
        
        panel.add(saveButton);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this category? This will also delete all subcategories and associated products.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
            categoryService.deleteCategory(categoryId);
            loadCategories();
        }
    }
    
    public void refreshData() {
        loadCategories();
    }
} 