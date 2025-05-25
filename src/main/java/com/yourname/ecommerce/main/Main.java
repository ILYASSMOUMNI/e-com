package com.yourname.ecommerce.main;

import com.yourname.ecommerce.gui.MainFrame;
import com.yourname.ecommerce.database.DatabaseInitializer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            // Initialize database
            LOGGER.info("Initializing database...");
            DatabaseInitializer dbInitializer = new DatabaseInitializer();
            dbInitializer.initializeDatabase();
            LOGGER.info("Database initialization completed.");

            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Start the application on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting application", e);
            e.printStackTrace();
        }
    }
} 