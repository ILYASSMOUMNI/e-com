package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private static final Color HIGHLIGHT_COLOR = new Color(0, 153, 255);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    
    private ProductService productService;
    
    public ProductManagementPanel(ProductService productService) {
        this.productService = productService;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(SECONDARY_COLOR);
        
        initializeComponents();
        setupLayout();
        loadProducts();
    }
    
    private void initializeComponents() {
        // Table setup with better styling
        String[] columns = {"ID", "Name", "Description", "Price", "Stock", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0 || columnIndex == 4) return Integer.class;
    if (columnIndex == 3) return Double.class;
    return String.class;
}
        };
        
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(30);
        productTable.setIntercellSpacing(new Dimension(10, 5));
        productTable.setShowGrid(false);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Custom header renderer
        JTableHeader header = productTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(AppTheme.TEXT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Custom cell renderer for prices and stock
        productTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        productTable.setDefaultRenderer(Integer.class, new StockRenderer());
        
        // Search and filter components
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search products...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        filterCombo = new JComboBox<>(new String[]{"All Categories", "Electronics", "Clothing", "Books"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Initialize buttons with icons and styling
        addButton = createStyledButton("Add", "/icons/add.png", PRIMARY_COLOR);
        editButton = createStyledButton("Edit", "/icons/edit.png", PRIMARY_COLOR);
        deleteButton = createStyledButton("Delete", "/icons/delete.png", ERROR_COLOR);
        refreshButton = createStyledButton("Refresh", "/icons/refresh.png", PRIMARY_COLOR);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddProductDialog());
        editButton.addActionListener(e -> showEditProductDialog());
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        refreshButton.addActionListener(e -> loadProducts());
        searchField.addActionListener(e -> filterProducts());
        filterCombo.addActionListener(e -> filterProducts());
        
        // Set default enabled state
        setButtonEnabled(addButton, true);
        setButtonEnabled(refreshButton, true);
        setButtonEnabled(editButton, true);
        setButtonEnabled(deleteButton, true);
    }
    
private JButton createStyledButton(String text, String iconPath, Color bgColor) {
    JButton button = new JButton(text);
    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);  // Ensures white text
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    button.setOpaque(true); // Ensures background is painted
    button.setContentAreaFilled(true);

    try {
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    } catch (Exception e) {
        System.err.println("Could not load icon: " + iconPath);
    }

    button.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent evt) {
            button.setBackground(bgColor.darker());
        }

        public void mouseExited(MouseEvent evt) {
            button.setBackground(bgColor);
        }
    });

    return button;
}

    
    private void setupLayout() {
        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(SECONDARY_COLOR);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by:"));
        searchPanel.add(filterCombo);
        
        // Create button panel with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(SECONDARY_COLOR);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
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
    
    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase();
        String filterCategory = (String) filterCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        List<Product> products = productService.getAllProducts();
        
        for (Product product : products) {
            if ((filterCategory.equals("All Categories") || 
                 product.getCategory().getName().equals(filterCategory)) &&
                (searchText.isEmpty() ||
                 product.getName().toLowerCase().contains(searchText) ||
                 product.getDescription().toLowerCase().contains(searchText))) {
                
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
    }
    
    private void showAddProductDialog() {
        JDialog dialog = createDialog("Add Product");
        JPanel panel = createFormPanel();
        
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JComboBox<Category> categoryCombo = new JComboBox<>(productService.getAllCategories().toArray(new Category[0]));
        
        addFormField(panel, "Name:", nameField);
        addFormField(panel, "Description:", descriptionField);
        addFormField(panel, "Price:", priceField);
        addFormField(panel, "Stock:", stockField);
        addFormField(panel, "Category:", categoryCombo);
        
        JButton saveButton = createStyledButton("Save", "/icons/save.png", PRIMARY_COLOR);
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
                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
        
        JDialog dialog = createDialog("Edit Product");
        JPanel panel = createFormPanel();
        
        JTextField nameField = new JTextField(product.getName());
        JTextField descriptionField = new JTextField(product.getDescription());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        JTextField stockField = new JTextField(String.valueOf(product.getStockQuantity()));
        JComboBox<Category> categoryCombo = new JComboBox<>(productService.getAllCategories().toArray(new Category[0]));
        categoryCombo.setSelectedItem(product.getCategory());
        
        addFormField(panel, "Name:", nameField);
        addFormField(panel, "Description:", descriptionField);
        addFormField(panel, "Price:", priceField);
        addFormField(panel, "Stock:", stockField);
        addFormField(panel, "Category:", categoryCombo);
        
        JButton saveButton = createStyledButton("Save Changes", "/icons/save.png", PRIMARY_COLOR);
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
                JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
    
    private JDialog createDialog(String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(SECONDARY_COLOR);
        return dialog;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);
        return panel;
    }
    
    private void addFormField(JPanel panel, String label, JComponent field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(jLabel);
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
        panel.add(field);
    }
    
    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "<html><b>Are you sure you want to delete this product?</b><br>" +
            "This action cannot be undone.</html>", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            productService.deleteProduct(productId);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void refreshData() {
        loadProducts();
    }
    
    // Custom renderer for currency values
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        public CurrencyRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Double) {
                setText(String.format("$%.2f", value));
            }
            return this;
        }
    }
    
    // Custom renderer for stock values
    private static class StockRenderer extends DefaultTableCellRenderer {
        public StockRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // No custom color or font for stock numbers
            return this;
        }
    }
    
    private void setButtonEnabled(JButton button, boolean enabled) {
        button.setEnabled(enabled);
        if (enabled) {
            AppTheme.styleButton(button);
        } else {
            AppTheme.styleButtonDisabled(button);
        }
    }
}