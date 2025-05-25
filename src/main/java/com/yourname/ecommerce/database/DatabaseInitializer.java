package com.yourname.ecommerce.database;

import com.yourname.ecommerce.dao.DBConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    private final DBConnection dbConnection;

    public DatabaseInitializer() {
        this.dbConnection = DBConnection.getInstance();
    }

    public void initializeDatabase() {
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create tables
            createTables(stmt);
            
            // Insert sample data
            insertSampleData(stmt);
            
            LOGGER.info("Database initialized successfully!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void createTables(Statement stmt) throws SQLException {
        // Users table
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "password_hash VARCHAR(255) NOT NULL," +
                    "first_name VARCHAR(50)," +
                    "last_name VARCHAR(50)," +
                    "phone VARCHAR(20)," +
                    "role ENUM('CUSTOMER', 'STAFF', 'ADMIN') DEFAULT 'CUSTOMER'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "last_login TIMESTAMP," +
                    "is_active BOOLEAN DEFAULT TRUE)");

        // Categories table
        stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "description TEXT," +
                    "parent_id INT," +
                    "FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL)");

        // Products table
        stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(200) NOT NULL," +
                    "description TEXT," +
                    "price DECIMAL(10,2) NOT NULL," +
                    "stock_quantity INT NOT NULL DEFAULT 0," +
                    "image_url VARCHAR(255)," +
                    "category_id INT," +
                    "rating DECIMAL(3,2) DEFAULT 0.0," +
                    "review_count INT DEFAULT 0," +
                    "status ENUM('IN_STOCK', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED') DEFAULT 'IN_STOCK'," +
                    "reorder_level INT DEFAULT 10," +
                    "tax_rate DECIMAL(4,2) DEFAULT 20.00," +
                    "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL)");

        // Addresses table
        stmt.execute("CREATE TABLE IF NOT EXISTS addresses (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "street VARCHAR(255) NOT NULL," +
                    "city VARCHAR(100) NOT NULL," +
                    "state VARCHAR(100) NOT NULL," +
                    "postal_code VARCHAR(20) NOT NULL," +
                    "country VARCHAR(100) NOT NULL," +
                    "is_default BOOLEAN DEFAULT FALSE," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

        // Shopping carts table
        stmt.execute("CREATE TABLE IF NOT EXISTS shopping_carts (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "is_active BOOLEAN DEFAULT TRUE," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

        // Cart items table
        stmt.execute("CREATE TABLE IF NOT EXISTS cart_items (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "cart_id INT NOT NULL," +
                    "product_id INT NOT NULL," +
                    "quantity INT NOT NULL DEFAULT 1," +
                    "price_at_time DECIMAL(10,2) NOT NULL," +
                    "FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)");

        // Orders table
        stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "subtotal DECIMAL(10,2) NOT NULL," +
                    "tax DECIMAL(10,2) NOT NULL," +
                    "total DECIMAL(10,2) NOT NULL," +
                    "status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "shipping_address_id INT," +
                    "billing_address_id INT," +
                    "payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER') NOT NULL," +
                    "tracking_number VARCHAR(100)," +
                    "notes TEXT," +
                    "transaction_id VARCHAR(100)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (shipping_address_id) REFERENCES addresses(id) ON DELETE SET NULL," +
                    "FOREIGN KEY (billing_address_id) REFERENCES addresses(id) ON DELETE SET NULL)");

        // Order items table
        stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "order_id INT NOT NULL," +
                    "product_id INT NOT NULL," +
                    "quantity INT NOT NULL," +
                    "price_at_time DECIMAL(10,2) NOT NULL," +
                    "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)");

        // Reviews table
        stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "product_id INT NOT NULL," +
                    "rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5)," +
                    "comment TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "verified_purchase BOOLEAN DEFAULT FALSE," +
                    "helpful BOOLEAN DEFAULT FALSE," +
                    "helpful_count INT DEFAULT 0," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)");
    }

    private void insertSampleData(Statement stmt) throws SQLException {
        // Insert sample users
        stmt.execute("INSERT IGNORE INTO users (username, email, password_hash, first_name, last_name, role) VALUES " +
                    "('admin', 'admin@example.com', 'admin123', 'Admin', 'User', 'ADMIN')," +
                    "('john', 'john@example.com', 'john123', 'John', 'Doe', 'CUSTOMER')," +
                    "('jane', 'jane@example.com', 'jane123', 'Jane', 'Smith', 'CUSTOMER')");

        // Insert sample categories
        stmt.execute("INSERT IGNORE INTO categories (name, description) VALUES " +
                    "('Electronics', 'Electronic devices and accessories')," +
                    "('Clothing', 'Fashion and apparel')," +
                    "('Books', 'Books and publications')");

        // Insert subcategories
        stmt.execute("INSERT IGNORE INTO categories (name, description, parent_id) VALUES " +
                    "('Smartphones', 'Mobile phones and accessories', 1)," +
                    "('Laptops', 'Notebooks and accessories', 1)," +
                    "('Men''s Clothing', 'Men''s fashion', 2)," +
                    "('Women''s Clothing', 'Women''s fashion', 2)," +
                    "('Fiction', 'Fiction books', 3)," +
                    "('Non-Fiction', 'Non-fiction books', 3)");

        // Insert sample products
        stmt.execute("INSERT IGNORE INTO products (name, description, price, stock_quantity, category_id) VALUES " +
                    "('iPhone 13', 'Latest Apple smartphone', 999.99, 50, 4)," +
                    "('MacBook Pro', 'Professional laptop', 1299.99, 30, 5)," +
                    "('Men''s T-Shirt', 'Cotton t-shirt', 29.99, 100, 6)," +
                    "('Women''s Dress', 'Summer dress', 49.99, 75, 7)," +
                    "('The Great Gatsby', 'Classic novel', 14.99, 200, 8)," +
                    "('Python Programming', 'Programming guide', 39.99, 150, 9)");

        // Insert sample addresses
        stmt.execute("INSERT IGNORE INTO addresses (user_id, street, city, state, postal_code, country) VALUES " +
                    "(2, '123 Main St', 'New York', 'NY', '10001', 'USA')," +
                    "(3, '456 Oak Ave', 'Los Angeles', 'CA', '90001', 'USA')");
    }
} 