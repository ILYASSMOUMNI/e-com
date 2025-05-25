package com.yourname.ecommerce.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;

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
    
    private ProductService productService;
    private PaymentService paymentService;
    private User currentUser;
    private Order currentOrder;

    public MainFrame() {
        setTitle("E-Commerce Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        productService = new ProductService();
        paymentService = new PaymentService();
        
        initializeComponents();
        setupLayout();
        setupNavigation();
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

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void setupNavigation() {
        // Login form navigation
        loginForm.addLoginSuccessListener(() -> {
            currentUser = new User();
            currentUser.setUsername("guest");
            currentUser.setRole(User.UserRole.CUSTOMER);
            menuBar.setVisible(true);
            cardLayout.show(mainPanel, "CATALOG");
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
            productDetail.setProductDetails(product);
            productDetail.addBackListener(() -> cardLayout.show(mainPanel, "CATALOG"));
            productDetail.addAddToCartListener((p, qty) -> {
                shoppingCart.addItem(p, qty);
                cardLayout.show(mainPanel, "CART");
            });
            cardLayout.show(mainPanel, "PRODUCT_DETAIL");
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

        // Setup menu bar
        JMenu navigationMenu = new JMenu("Navigation");
        JMenuItem catalogItem = new JMenuItem("Product Catalog");
        JMenuItem cartItem = new JMenuItem("Shopping Cart");
        JMenuItem profileItem = new JMenuItem("User Profile");
        JMenuItem ordersItem = new JMenuItem("Order History");
        JMenuItem logoutItem = new JMenuItem("Logout");

        navigationMenu.add(catalogItem);
        navigationMenu.add(cartItem);
        navigationMenu.add(profileItem);
        navigationMenu.add(ordersItem);
        navigationMenu.addSeparator();
        navigationMenu.add(logoutItem);

        menuBar.add(navigationMenu);

        // Menu item actions
        catalogItem.addActionListener(e -> cardLayout.show(mainPanel, "CATALOG"));
        cartItem.addActionListener(e -> cardLayout.show(mainPanel, "CART"));
        profileItem.addActionListener(e -> cardLayout.show(mainPanel, "PROFILE"));
        ordersItem.addActionListener(e -> cardLayout.show(mainPanel, "ORDERS"));
        logoutItem.addActionListener(e -> {
            currentUser = null;
            menuBar.setVisible(false);
            cardLayout.show(mainPanel, "LOGIN");
        });
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 