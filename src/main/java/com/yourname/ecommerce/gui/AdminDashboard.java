package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    // Colors
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color SECONDARY_COLOR = new Color(245, 245, 245);
    private final Color HIGHLIGHT_COLOR = new Color(0, 153, 255);
    
    public AdminDashboard() {
        setLayout(new BorderLayout());
        productService = new ProductService();
        categoryService = new CategoryService();
        userService = new UserService();
        orderService = new OrderService();
        
        initializeComponents();
        setupLayout();
        applyStyles();
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(AppTheme.BACKGROUND_COLOR);
    }
    
    private void initializeComponents() {
        // Create a custom tabbed pane with better styling
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT) {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new CustomTabbedPaneUI());
            }
        };
        
        productManagementPanel = new ProductManagementPanel(productService);
        categoryManagementPanel = new CategoryManagementPanel(categoryService);
        userManagementPanel = new UserManagementPanel(userService);
        orderManagementPanel = new OrderManagementPanel();
        
        // Add status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }
    
    private void setupLayout() {
        tabbedPane.addTab("Products", null, productManagementPanel);
        tabbedPane.addTab("Categories", null, categoryManagementPanel);
        tabbedPane.addTab("Users", null, userManagementPanel);
        tabbedPane.addTab("Orders", null, orderManagementPanel);
        
        // Add header panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void applyStyles() {
        setBackground(AppTheme.BACKGROUND_COLOR);
        
        // Tabbed pane styling
        tabbedPane.setFont(AppTheme.BUTTON_FONT);
        tabbedPane.setBackground(AppTheme.BACKGROUND_COLOR);
        tabbedPane.setForeground(AppTheme.TEXT_COLOR);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Set border for content area
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        setBackground(AppTheme.BACKGROUND_COLOR);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppTheme.PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(AppTheme.PRIMARY_COLOR);
        statusBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        statusLabel.setFont(AppTheme.NORMAL_FONT);
        
        JLabel userLabel = new JLabel("Admin User");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userLabel.setFont(AppTheme.NORMAL_FONT);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HIGHLIGHT_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        AppTheme.applyTheme(button);
    }
    
    private ImageIcon createScaledIcon(String path, int width, int height) {
        // Fallback icon if the image is not found
        ImageIcon icon;
        try {
            icon = new ImageIcon(getClass().getResource(path));
        } catch (Exception e) {
            // Create a simple placeholder icon
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, width, height);
            g2d.dispose();
            icon = new ImageIcon(image);
        }
        
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
    
    public void refreshData() {
        productManagementPanel.refreshData();
        categoryManagementPanel.refreshData();
        userManagementPanel.refreshData();
        // Only call if OrderManagementPanel has refreshData() method
        // orderManagementPanel.refreshData();
    }
    
    // Custom TabbedPane UI for better appearance
    private class CustomTabbedPaneUI extends javax.swing.plaf.metal.MetalTabbedPaneUI {
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, 
                                        int tabIndex, int x, int y, int w, int h, 
                                        boolean isSelected) {
            if (isSelected) {
                g.setColor(HIGHLIGHT_COLOR);
            } else {
                g.setColor(SECONDARY_COLOR);
            }
            g.fillRoundRect(x, y, w, h, 10, 10);
        }
        
        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, 
                                     int tabIndex, int x, int y, int w, int h, 
                                     boolean isSelected) {
            g.setColor(isSelected ? HIGHLIGHT_COLOR : Color.LIGHT_GRAY);
            g.drawRect(x, y, w, h);
        }
        
        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            int width = tabPane.getWidth();
            int height = tabPane.getHeight();
            Insets insets = tabPane.getInsets();
            
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(insets.left, insets.top + calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight),
                     width - insets.left - insets.right,
                     height - insets.top - calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight) - insets.bottom);
        }
    }
}