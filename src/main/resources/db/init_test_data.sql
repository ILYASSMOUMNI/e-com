-- Create test admin user
-- Password is 'admin123' hashed with bcrypt
INSERT INTO users (username, email, password_hash, first_name, last_name, phone, role, created_at, is_active)
VALUES (
    'admin',
    'admin@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Admin',
    'User',
    '+1234567890',
    'ADMIN',
    NOW(),
    true
);

-- Create test staff user
-- Password is 'staff123' hashed with bcrypt
INSERT INTO users (username, email, password_hash, first_name, last_name, phone, role, created_at, is_active)
VALUES (
    'staff',
    'staff@example.com',
    '$2a$10$8K1p/a0dR1xqM8K3hQz1eOQZQZQZQZQZQZQZQZQZQZQZQZQZQZQZQ',
    'Staff',
    'User',
    '+1234567891',
    'STAFF',
    NOW(),
    true
);

-- Create test customer user
-- Password is 'customer123' hashed with bcrypt
INSERT INTO users (username, email, password_hash, first_name, last_name, phone, role, created_at, is_active)
VALUES (
    'customer',
    'customer@example.com',
    '$2a$10$8K1p/a0dR1xqM8K3hQz1eOQZQZQZQZQZQZQZQZQZQZQZQZQZQZQZQ',
    'Customer',
    'User',
    '+1234567892',
    'CUSTOMER',
    NOW(),
    true
); 