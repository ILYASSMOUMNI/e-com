package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JPanel {
    private JTabbedPane tabbedPane;
    private ProductManagementPanel productManagementPanel;
    private CategoryManagementPanel categoryManagementPanel;
    private UserManagementPanel userManagementPanel;
    private OrderManagementPanel orderManagementPanel;
    
    private ProductService productService;
    private CategoryService categoryService;
    private UserService userService;
    private OrderService orderService;
    
    public AdminDashboard() {
        setLayout(new BorderLayout());
        productService = new ProductService();
        categoryService = new CategoryService();
        userService = new UserService();
        orderService = new OrderService();
        
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        productManagementPanel = new ProductManagementPanel(productService);
        categoryManagementPanel = new CategoryManagementPanel(categoryService);
        userManagementPanel = new UserManagementPanel(userService);
        orderManagementPanel = new OrderManagementPanel();
    }
    
    private void setupLayout() {
        tabbedPane.addTab("Products", new ImageIcon("icons/products.png"), productManagementPanel);
        tabbedPane.addTab("Categories", new ImageIcon("icons/categories.png"), categoryManagementPanel);
        tabbedPane.addTab("Users", new ImageIcon("icons/users.png"), userManagementPanel);
        tabbedPane.addTab("Orders", new ImageIcon("icons/orders.png"), orderManagementPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        productManagementPanel.refreshData();
        categoryManagementPanel.refreshData();
        userManagementPanel.refreshData();
    }
} 