package com.yourname.ecommerce.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class AppTheme {
    // Colors
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Light Blue
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);      // Red
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    public static final Color TEXT_COLOR = new Color(44, 62, 80);         // Dark Gray
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);    // Green
    public static final Color WARNING_COLOR = new Color(241, 196, 15);    // Yellow
    public static final Color ERROR_COLOR = new Color(231, 76, 60);       // Red for errors
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Borders
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    );
    
    public static void applyTheme(JComponent component) {
        if (component instanceof JButton) {
            styleButton((JButton) component);
        } else if (component instanceof JTextField || component instanceof JPasswordField) {
            styleTextField((JTextField) component);
        } else if (component instanceof JLabel) {
            styleLabel((JLabel) component);
        } else if (component instanceof JPanel) {
            stylePanel((JPanel) component);
        }
    }
    
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }
    
    public static void styleTextField(JTextField textField) {
        textField.setFont(NORMAL_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    public static void styleLabel(JLabel label) {
        label.setFont(NORMAL_FONT);
        label.setForeground(TEXT_COLOR);
    }
    
    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
    }
    
    public static void styleMenuBar(JMenuBar menuBar) {
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setForeground(Color.BLACK);
        
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            menu.setFont(BUTTON_FONT);
            menu.setForeground(Color.BLACK);
            
            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem item = menu.getItem(j);
                if (item != null) {
                    item.setFont(NORMAL_FONT);
                    item.setForeground(TEXT_COLOR);
                    item.setBackground(Color.WHITE);
                    
                    // Add hover effect for menu items
                    item.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            item.setBackground(BACKGROUND_COLOR);
                        }
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                            item.setBackground(Color.WHITE);
                        }
                    });
                }
            }
        }
    }
    
    public static void styleButtonDisabled(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.GRAY);
        button.setBackground(new Color(200, 200, 200));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setEnabled(false);
    }
} 