package com.yourname.ecommerce.gui;

import com.yourname.ecommerce.models.*;
import com.yourname.ecommerce.services.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.border.EmptyBorder;

public class OrderManagementPanel extends JPanel {
    private final OrderService orderService;
    private final JTable orderTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> statusFilter;
    private final JTextField searchField;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public OrderManagementPanel() {
        this.orderService = new OrderService();
        setLayout(new BorderLayout());
        
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(AppTheme.BACKGROUND_COLOR);
        
        // Create table model
        String[] columns = {"Order ID", "Customer", "Date", "Total", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only allow editing of the Actions column
            }
        };
        
        // Create table
        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(28);
        orderTable.setFont(AppTheme.NORMAL_FONT);
        orderTable.setBackground(Color.WHITE);
        orderTable.setShowGrid(false);
        orderTable.setForeground(AppTheme.TEXT_COLOR);
        
        // Style table header
        JTableHeader header = orderTable.getTableHeader();
        header.setFont(AppTheme.HEADER_FONT);
        header.setBackground(AppTheme.PRIMARY_COLOR);
        header.setForeground(AppTheme.TEXT_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Alternating row colors
        orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        
        // After orderTable is created and before adding it to the scroll pane, add:
        orderTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Status filter
        String[] statuses = {"All", "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "CONFIRMED", "REFUNDED"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.addActionListener(e -> loadOrders());
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        
        // Search field
        searchField = new JTextField(20);
        searchField.addActionListener(e -> loadOrders());
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadOrders());
        filterPanel.add(refreshButton);
        
        add(filterPanel, BorderLayout.NORTH);
        
        // Style filter panel
        filterPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        filterPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Style statusFilter combo box
        statusFilter.setFont(AppTheme.NORMAL_FONT);
        
        // Load initial data
        loadOrders();
    }
    
    private void loadOrders() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Get all orders
        List<Order> orders = orderService.getAllOrders();
        System.out.println("Total orders retrieved: " + orders.size()); // Debug log
        
        // Apply filters
        String statusFilter = (String) this.statusFilter.getSelectedItem();
        String searchText = searchField.getText().toLowerCase();
        
        System.out.println("Current filters - Status: " + statusFilter + ", Search: " + searchText); // Debug log
        
        int filteredCount = 0;
        for (Order order : orders) {
            // Skip if status doesn't match
            if (!statusFilter.equals("All") && !order.getStatus().toString().equals(statusFilter)) {
                filteredCount++;
                System.out.println("Filtered out order " + order.getId() + " due to status mismatch"); // Debug log
                continue;
            }
            
            // Skip if search text doesn't match
            if (!searchText.isEmpty()) {
                boolean matches = false;
                if (order.getUser() != null) {
                    matches = order.getUser().getUsername().toLowerCase().contains(searchText) ||
                             order.getUser().getEmail().toLowerCase().contains(searchText);
                }
                if (!matches) {
                    filteredCount++;
                    System.out.println("Filtered out order " + order.getId() + " due to search text mismatch"); // Debug log
                    continue;
                }
            }
            
            // Add order to table
            Object[] row = {
                order.getId(),
                order.getUser() != null ? order.getUser().getUsername() : "Unknown",
                dateFormat.format(order.getCreatedAt()),
                String.format("$%.2f", order.getTotal()),
                order.getStatus().toString(),
                "View Details"
            };
            tableModel.addRow(row);
        }
        
        System.out.println("Orders filtered out: " + filteredCount); // Debug log
        System.out.println("Orders displayed: " + tableModel.getRowCount()); // Debug log
    }
    
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            currentRow = row;
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the order ID from the first column
                int orderId = (int) tableModel.getValueAt(currentRow, 0);
                Order order = orderService.getOrderById(orderId);
                if (order != null) {
                    showOrderDetails(order);
                }
            }
            isPushed = false;
            return label;
        }
    }
    
    private void showOrderDetails(Order order) {
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
        detailsPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getUser() != null ? order.getUser().getUsername() : "Unknown"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(dateFormat.format(order.getCreatedAt())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getStatus().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        detailsPanel.add(new JLabel("Total:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.format("$%.2f", order.getTotal())), gbc);
        
        // Add items table
        gbc.gridx = 0; gbc.gridy = 5;
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
        
        // Add status update panel
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 5, 5, 5);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Update Status:"));
        
        JComboBox<Order.OrderStatus> statusCombo = new JComboBox<>(Order.OrderStatus.values());
        statusCombo.setSelectedItem(order.getStatus());
        statusPanel.add(statusCombo);
        
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            Order.OrderStatus newStatus = (Order.OrderStatus) statusCombo.getSelectedItem();
            order.setStatus(newStatus);
            orderService.updateOrder(order);
            loadOrders();
            dialog.dispose();
        });
        statusPanel.add(updateButton);
        
        detailsPanel.add(statusPanel, gbc);
        
        dialog.add(detailsPanel, BorderLayout.CENTER);
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 