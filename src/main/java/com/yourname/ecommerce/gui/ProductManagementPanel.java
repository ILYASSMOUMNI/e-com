package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    private ProductService productService;
    
    public ProductManagementPanel(ProductService productService) {
        this.productService = productService;
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
        loadProducts();
    }
    
    private void initializeComponents() {
        // Initialize table model
        String[] columns = {"ID", "Name", "Description", "Price", "Stock", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Initialize buttons
        addButton = new JButton("Add Product");
        editButton = new JButton("Edit Product");
        deleteButton = new JButton("Delete Product");
        refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        refreshButton.addActionListener(e -> loadProducts());
    }
    
    private void setupLayout() {
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Add components to panel
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            Object[] row = {
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory().getName()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddProductDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Product", true);
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JComboBox<Category> categoryCombo = new JComboBox<>(productService.getAllCategories().toArray(new Category[0]));
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Product product = new Product();
                product.setName(nameField.getText());
                product.setDescription(descriptionField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setStockQuantity(Integer.parseInt(stockField.getText()));
                product.setCategory((Category) categoryCombo.getSelectedItem());
                
                productService.createProduct(product);
                loadProducts();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for price and stock", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(saveButton);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showEditProductDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = productService.getProductById(productId);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Product", true);
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        JTextField nameField = new JTextField(product.getName());
        JTextField descriptionField = new JTextField(product.getDescription());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        JTextField stockField = new JTextField(String.valueOf(product.getStockQuantity()));
        JComboBox<Category> categoryCombo = new JComboBox<>(productService.getAllCategories().toArray(new Category[0]));
        categoryCombo.setSelectedItem(product.getCategory());
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                product.setName(nameField.getText());
                product.setDescription(descriptionField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setStockQuantity(Integer.parseInt(stockField.getText()));
                product.setCategory((Category) categoryCombo.getSelectedItem());
                
                productService.updateProduct(product);
                loadProducts();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for price and stock", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(saveButton);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this product?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            productService.deleteProduct(productId);
            loadProducts();
        }
    }
    
    public void refreshData() {
        loadProducts();
    }
} 