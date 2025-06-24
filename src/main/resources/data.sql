-- Updated database schema for refactored entities
-- This will be executed by Spring Boot after DDL auto-generation

-- Insert sample users
INSERT OR IGNORE INTO users (user_id, password, name, email, phone, role, registration_date, salary, is_active) VALUES
(1, 'hashedpassword123', 'Admin User', 'admin@itss.com', '0123456789', 'ADMIN', datetime('now'), 1000.0, 1),
(2, 'hashedpassword456', 'Customer User', 'customer@itss.com', '0987654321', 'CUSTOMER', datetime('now'), 0.0, 1),
(3, 'hashedpassword789', 'Manager User', 'manager@itss.com', '0555123456', 'MANAGER', datetime('now'), 800.0, 1);

-- Insert sample products (will be created through JPA inheritance)
-- Note: JPA will handle the inheritance table structure automatically

-- Insert sample delivery information
INSERT OR IGNORE INTO delivery_information (delivery_id, name, phone, email, address, province, delivery_message, delivery_fee) VALUES
(1, 'John Doe', '0123456789', 'john@example.com', '123 Main Street, District 1', 'Ho Chi Minh City', 'Please call before delivery', 25000),
(2, 'Jane Smith', '0987654321', 'jane@example.com', '456 Oak Avenue, District 3', 'Ho Chi Minh City', 'Leave at front door', 25000);

-- Insert audit logs
INSERT OR IGNORE INTO logger (action_id, action_name, recorded_at, note, action_type) VALUES
(1, 'System Initialization', datetime('now'), 'Database initialized with sample data', 'CREATE'),
(2, 'User Registration', datetime('now'), 'Sample users created', 'CREATE'),
(3, 'Product Setup', datetime('now'), 'Sample products initialized', 'CREATE');