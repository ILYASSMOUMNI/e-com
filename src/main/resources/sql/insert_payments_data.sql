-- Insert sample payment records
INSERT INTO payments (order_id, card_number, card_holder_name, expiry_date, cvv, amount, status, payment_date, payment_method) VALUES
(1, '4532123456781234', 'John Doe', '12/25', '123', 299.99, 'COMPLETED', '2024-03-15 14:30:00', 'CREDIT_CARD'),
(2, '4532123456785678', 'Jane Smith', '09/24', '456', 149.50, 'COMPLETED', '2024-03-15 15:45:00', 'CREDIT_CARD'),
(3, '4532123456789012', 'Mike Johnson', '03/26', '789', 499.99, 'PENDING', '2024-03-15 16:20:00', 'DEBIT_CARD'),
(4, '4532123456783456', 'Sarah Wilson', '06/25', '234', 199.75, 'COMPLETED', '2024-03-15 17:10:00', 'CREDIT_CARD'),
(5, '4532123456787890', 'David Brown', '12/24', '567', 79.99, 'FAILED', '2024-03-15 18:05:00', 'CREDIT_CARD');

-- Note: Make sure the order_id values exist in your orders table before running this script
-- The card numbers shown are examples and should be replaced with actual masked card numbers in production 