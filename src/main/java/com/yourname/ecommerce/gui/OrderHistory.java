package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.User;
import com.yourname.ecommerce.models.Order;
import com.yourname.ecommerce.models.CartItem;
import com.yourname.ecommerce.services.OrderService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class OrderHistory extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton viewDetailsButton;
    private JButton backButton;
    private User currentUser;
    private OrderService orderService;

    public OrderHistory() {
        setLayout(new BorderLayout());
        orderService = new OrderService();
        initializeComponents();
        setupLayout();
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadOrderHistory();
    }

    private void initializeComponents() {
        // Initialize table model
        String[] columns = {"Order ID", "Date", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Initialize buttons
        viewDetailsButton = new JButton("View Details");
        backButton = new JButton("Back");

        // Add action listeners
        viewDetailsButton.addActionListener(e -> viewOrderDetails());
        backButton.addActionListener(e -> goBack());
    }

    private void setupLayout() {
        // Table panel with scroll pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(viewDetailsButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadOrderHistory() {
        tableModel.setRowCount(0); // Clear existing rows
        
        if (currentUser != null) {
            List<Order> orders = orderService.getOrdersByUser(currentUser);
            for (Order order : orders) {
                tableModel.addRow(new Object[]{
                    order.getId(),
                    order.getOrderDate(),
                    String.format("$%.2f", order.getTotal()),
                    order.getStatus()
                });
            }
        }
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view details", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        Order order = orderService.getOrderById(orderId);
        
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Could not load order details", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create and show order details dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Order Details", true);
        dialog.setLayout(new BorderLayout());
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add order details
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(new JLabel("Order ID:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(order.getId())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getOrderDate().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getStatus().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(new JLabel("Total:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.format("$%.2f", order.getTotal())), gbc);
        
        // Add items table
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        String[] columns = {"Product", "Quantity", "Price", "Subtotal"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0);
        JTable itemsTable = new JTable(itemsModel);
        
        for (CartItem item : order.getItems()) {
            Object[] row = {
                item.getProduct().getName(),
                item.getQuantity(),
                String.format("$%.2f", item.getProduct().getPrice()),
                String.format("$%.2f", item.getProduct().getPrice() * item.getQuantity())
            };
            itemsModel.addRow(row);
        }
        
        detailsPanel.add(new JScrollPane(itemsTable), gbc);
        
        // Add shipping and billing address information
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel addressPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        // Shipping Address
        JPanel shippingPanel = new JPanel(new BorderLayout());
        shippingPanel.setBorder(BorderFactory.createTitledBorder("Shipping Address"));
        JTextArea shippingAddress = new JTextArea(order.getShippingAddress().toString());
        shippingAddress.setEditable(false);
        shippingPanel.add(shippingAddress, BorderLayout.CENTER);
        
        // Billing Address
        JPanel billingPanel = new JPanel(new BorderLayout());
        billingPanel.setBorder(BorderFactory.createTitledBorder("Billing Address"));
        JTextArea billingAddress = new JTextArea(order.getBillingAddress().toString());
        billingAddress.setEditable(false);
        billingPanel.add(billingAddress, BorderLayout.CENTER);
        
        addressPanel.add(shippingPanel);
        addressPanel.add(billingPanel);
        
        detailsPanel.add(addressPanel, gbc);
        
        dialog.add(detailsPanel, BorderLayout.CENTER);
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void goBack() {
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