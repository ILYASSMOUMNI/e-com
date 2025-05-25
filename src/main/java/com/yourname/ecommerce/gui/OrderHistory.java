package com.yourname.ecommerce.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class OrderHistory extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton viewDetailsButton;
    private JButton backButton;

    public OrderHistory() {
        setLayout(new BorderLayout());
        initializeComponents();
        setupLayout();
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
        // TODO: Load order history from service
        // This is sample data for demonstration
        tableModel.addRow(new Object[]{"ORD001", "2024-03-15", "$150.00", "Delivered"});
        tableModel.addRow(new Object[]{"ORD002", "2024-03-10", "$75.50", "Processing"});
        tableModel.addRow(new Object[]{"ORD003", "2024-03-05", "$200.00", "Shipped"});
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to view details", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        // TODO: Implement order details view
        JOptionPane.showMessageDialog(this, "Viewing details for order: " + orderId, "Order Details", JOptionPane.INFORMATION_MESSAGE);
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