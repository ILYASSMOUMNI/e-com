package com.yourname.ecommerce.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import com.yourname.ecommerce.utils.DBConnection;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private LoginForm loginForm;
    private RegisterForm registerForm;
    private ProductCatalog productCatalog;
    private ShoppingCart shoppingCart;
    private UserProfile userProfile;
    private OrderHistory orderHistory;
    private ProductDetail productDetail;
    private CheckoutForm checkoutForm;
    private AdminDashboard adminDashboard;
    
    private ProductService productService;
    private PaymentService paymentService;
    private User currentUser;
    private Order currentOrder;

    public MainFrame() {
        setTitle("E-Commerce Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        productService = new ProductService();
        paymentService = new PaymentService(DBConnection.getConnection());
        
        initializeComponents();
        setupLayout();
        setupNavigation();
        applyTheme();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        loginForm = new LoginForm();
        registerForm = new RegisterForm();
        productCatalog = new ProductCatalog();
        shoppingCart = new ShoppingCart();
        userProfile = new UserProfile();
        orderHistory = new OrderHistory();
        productDetail = new ProductDetail();
        checkoutForm = new CheckoutForm();
        adminDashboard = new AdminDashboard();

        menuBar = new JMenuBar();
        menuBar.setVisible(false);
        setJMenuBar(menuBar);
    }

    private void setupLayout() {
        mainPanel.add(loginForm, "LOGIN");
        mainPanel.add(registerForm, "REGISTER");
        mainPanel.add(productCatalog, "CATALOG");
        mainPanel.add(shoppingCart, "CART");
        mainPanel.add(userProfile, "PROFILE");
        mainPanel.add(orderHistory, "ORDERS");
        mainPanel.add(productDetail, "PRODUCT_DETAIL");
        mainPanel.add(checkoutForm, "CHECKOUT");
        mainPanel.add(adminDashboard, "ADMIN_DASHBOARD");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void applyTheme() {
        // Apply theme to main panel
        AppTheme.applyTheme(mainPanel);
        
        // Apply theme to menu bar
        AppTheme.styleMenuBar(menuBar);
        
        // Set frame background
        getContentPane().setBackground(AppTheme.BACKGROUND_COLOR);
        
        // Apply theme to all panels
        AppTheme.applyTheme(loginForm);
        AppTheme.applyTheme(registerForm);
        AppTheme.applyTheme(productCatalog);
        AppTheme.applyTheme(shoppingCart);
        AppTheme.applyTheme(userProfile);
        AppTheme.applyTheme(orderHistory);
        AppTheme.applyTheme(productDetail);
        AppTheme.applyTheme(checkoutForm);
        AppTheme.applyTheme(adminDashboard);
    }

    private void setupNavigation() {
        // Login form navigation
        loginForm.addLoginSuccessListener(user -> {
            currentUser = user;
            menuBar.setVisible(true);
            setupMenuBar();
            if (user.isAdmin()) {
                cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
                adminDashboard.refreshData();
            } else {
                cardLayout.show(mainPanel, "CATALOG");
            }
        });
        
        loginForm.addRegisterClickListener(() -> {
            cardLayout.show(mainPanel, "REGISTER");
        });

        // Register form navigation
        registerForm.addBackToLoginListener(() -> {
            cardLayout.show(mainPanel, "LOGIN");
        });

        // Product catalog navigation
        productCatalog.addProductClickListener(product -> {
            if (!currentUser.isAdmin()) {
                productDetail.setProductDetails(product);
                productDetail.addBackListener(() -> cardLayout.show(mainPanel, "CATALOG"));
                productDetail.addAddToCartListener((p, qty) -> {
                    shoppingCart.addItem(p, qty);
                    cardLayout.show(mainPanel, "CART");
                });
                cardLayout.show(mainPanel, "PRODUCT_DETAIL");
            }
        });

        // Add view cart listener to product catalog
        productCatalog.addViewCartListener(() -> {
            if (!currentUser.isAdmin()) {
                cardLayout.show(mainPanel, "CART");
            }
        });

        // Shopping cart navigation
        shoppingCart.addCheckoutListener(() -> {
            currentOrder = new Order();
            currentOrder.setUser(currentUser);
            currentOrder.setItems(new ArrayList<>(shoppingCart.getCartItems()));
            currentOrder.setTotalAmount(shoppingCart.getTotalAmount());
            
            // Set default addresses
            Address defaultAddress = new Address();
            defaultAddress.setStreet("123 Main St");
            defaultAddress.setCity("Default City");
            defaultAddress.setState("Default State");
            defaultAddress.setZipCode("12345");
            defaultAddress.setCountry("Default Country");
            
            currentOrder.setShippingAddress(defaultAddress);
            currentOrder.setBillingAddress(defaultAddress);
            currentOrder.setPaymentMethod(Order.PaymentMethod.CREDIT_CARD);
            
            checkoutForm.setOrder(currentOrder);
            cardLayout.show(mainPanel, "CHECKOUT");
        });
        
        shoppingCart.addContinueShoppingListener(() -> {
            cardLayout.show(mainPanel, "CATALOG");
        });

        // Checkout form navigation
        checkoutForm.addBackToCartListener(() -> {
            cardLayout.show(mainPanel, "CART");
        });
    }
    
    private void setupMenuBar() {
        menuBar.removeAll();
        
        // Create menus
        JMenu fileMenu = new JMenu("File");
        JMenu catalogMenu = new JMenu("Catalog");
        JMenu accountMenu = new JMenu("Account");
        
        // Add menu items
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenuItem catalogItem = new JMenuItem("Browse Products");
        catalogItem.addActionListener(e -> cardLayout.show(mainPanel, "CATALOG"));
        catalogMenu.add(catalogItem);
        
        // Only add shopping cart menu item for non-admin users
        if (!currentUser.isAdmin()) {
            JMenuItem cartItem = new JMenuItem("Shopping Cart");
            cartItem.addActionListener(e -> cardLayout.show(mainPanel, "CART"));
            catalogMenu.add(cartItem);
        }
        
        JMenuItem profileItem = new JMenuItem("My Profile");
        profileItem.addActionListener(e -> {
            userProfile.setUser(currentUser);
            cardLayout.show(mainPanel, "PROFILE");
        });
        accountMenu.add(profileItem);
        
        // Only add order history menu item for non-admin users
        if (!currentUser.isAdmin()) {
            JMenuItem ordersItem = new JMenuItem("Order History");
            ordersItem.addActionListener(e -> {
                orderHistory.setUser(currentUser);
                cardLayout.show(mainPanel, "ORDERS");
            });
            accountMenu.add(ordersItem);
        }
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            currentUser = null;
            menuBar.setVisible(false);
            cardLayout.show(mainPanel, "LOGIN");
        });
        accountMenu.add(logoutItem);
        
        // Add admin menu if user is admin
        if (currentUser.isAdmin()) {
            JMenu adminMenu = new JMenu("Admin");
            JMenuItem dashboardItem = new JMenuItem("Dashboard");
            dashboardItem.addActionListener(e -> {
                adminDashboard.refreshData();
                cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
            });
            adminMenu.add(dashboardItem);
            menuBar.add(adminMenu);
        }
        
        // Add all menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(catalogMenu);
        menuBar.add(accountMenu);
        
        // Apply theme to menu bar
        AppTheme.styleMenuBar(menuBar);
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default font for all components
            UIManager.put("Button.font", AppTheme.BUTTON_FONT);
            UIManager.put("Label.font", AppTheme.NORMAL_FONT);
            UIManager.put("TextField.font", AppTheme.NORMAL_FONT);
            UIManager.put("PasswordField.font", AppTheme.NORMAL_FONT);
            UIManager.put("Menu.font", AppTheme.BUTTON_FONT);
            UIManager.put("MenuItem.font", AppTheme.NORMAL_FONT);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 