-- Updated database schema for refactored entities
-- This will be executed by Spring Boot after DDL auto-generation

-- Insert sample users
INSERT OR IGNORE INTO users (user_id, password, name, email, phone, role, registration_date, salary, is_active) VALUES
(1, 'hashedpassword123', 'Admin User', 'admin@itss.com', '0123456789', 'ADMIN', datetime('now'), 1000.0, 1),
(2, 'hashedpassword456', 'Customer User', 'customer@itss.com', '0987654321', 'CUSTOMER', datetime('now'), 0.0, 1),
(3, 'hashedpassword789', 'Manager User', 'manager@itss.com', '0555123456', 'MANAGER', datetime('now'), 800.0, 1);

-- Insert sample products with inheritance structure
-- Base products first, then subclass-specific data

-- Sample Books
INSERT OR IGNORE INTO products (product_id, title, price, weight, rush_order_supported, image_url, barcode, import_date, introduction, quantity, type, created_at, updated_at) VALUES
(1, 'The Great Gatsby', 1599, 0.3, 0, 'https://covers.openlibrary.org/b/id/8225261-L.jpg', '978-0-7432-7356-5', '2024-01-15', 'A classic American novel about the Jazz Age and the American Dream.', 25, 'book', datetime('now'), datetime('now')),
(2, 'To Kill a Mockingbird', 1299, 0.35, 0, 'https://covers.openlibrary.org/b/id/8226816-L.jpg', '978-0-06-112008-4', '2024-01-10', 'A gripping tale of racial injustice and childhood innocence in the American South.', 30, 'book', datetime('now'), datetime('now')),
(3, '1984', 1399, 0.28, 1, 'https://covers.openlibrary.org/b/id/8226112-L.jpg', '978-0-452-28423-4', '2024-01-12', 'A dystopian social science fiction novel about totalitarianism.', 40, 'book', datetime('now'), datetime('now'));

-- Sample CDs
INSERT OR IGNORE INTO products (product_id, title, price, weight, rush_order_supported, image_url, barcode, import_date, introduction, quantity, type, created_at, updated_at) VALUES
(4, 'Abbey Road', 1999, 0.1, 0, 'https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg', '094638241621', '2024-01-20', 'The Beatles final studio album, featuring iconic tracks like Come Together and Here Comes the Sun.', 15, 'cd', datetime('now'), datetime('now')),
(5, 'Dark Side of the Moon', 2199, 0.1, 1, 'https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png', '094637420621', '2024-01-18', 'Pink Floyd classic progressive rock masterpiece exploring themes of conflict, greed, and mental illness.', 20, 'cd', datetime('now'), datetime('now')),
(6, 'Thriller', 1899, 0.1, 0, 'https://upload.wikimedia.org/wikipedia/en/5/55/Michael_Jackson_-_Thriller.png', '074643851428', '2024-01-22', 'Michael Jackson iconic pop album featuring Billie Jean, Beat It, and the title track Thriller.', 18, 'cd', datetime('now'), datetime('now'));

-- Sample DVDs
INSERT OR IGNORE INTO products (product_id, title, price, weight, rush_order_supported, image_url, barcode, import_date, introduction, quantity, type, created_at, updated_at) VALUES
(7, 'The Shawshank Redemption', 2499, 0.15, 0, 'https://m.media-amazon.com/images/I/519NBNHX5BL._AC_.jpg', '085391163926', '2024-01-25', 'Hope can set you free. A story of friendship and redemption in a brutal prison.', 12, 'dvd', datetime('now'), datetime('now')),
(8, 'The Godfather', 2799, 0.15, 1, 'https://m.media-amazon.com/images/I/41+dkHBquGL._AC_.jpg', '097360719147', '2024-01-23', 'The aging patriarch of an organized crime dynasty transfers control to his reluctant son.', 10, 'dvd', datetime('now'), datetime('now')),
(9, 'Pulp Fiction', 2399, 0.15, 0, 'https://m.media-amazon.com/images/I/71c05lTE03L._AC_SL1024_.jpg', '031398187737', '2024-01-24', 'Quentin Tarantino masterpiece weaving together multiple storylines in Los Angeles.', 14, 'dvd', datetime('now'), datetime('now'));

-- Book-specific data
INSERT OR IGNORE INTO books (product_id, genre, page_count, publication_date, authors, publishers, cover_type) VALUES
(1, 'Classic Literature', 180, '1925-04-10', 'F. Scott Fitzgerald', 'Charles Scribner''s Sons', 'Paperback'),
(2, 'Classic Literature', 281, '1960-07-11', 'Harper Lee', 'J.B. Lippincott & Co.', 'Paperback'),
(3, 'Science Fiction', 328, '1949-06-08', 'George Orwell', 'Secker & Warburg', 'Paperback');

-- CD-specific data
INSERT OR IGNORE INTO cds (product_id, track_list, genre, record_label, artists, release_date) VALUES
(4, 'Come Together, Something, Maxwell''s Silver Hammer, Oh! Darling, Octopus''s Garden, I Want You (She''s So Heavy), Here Comes the Sun, Because, You Never Give Me Your Money, Sun King, Mean Mr. Mustard, Polythene Pam, She Came in Through the Bathroom Window, Golden Slumbers, Carry That Weight, The End, Her Majesty', 'Rock', 'Apple Records', 'The Beatles', '1969-09-26'),
(5, 'Speak to Me, Breathe, On the Run, Time, The Great Gig in the Sky, Money, Us and Them, Any Colour You Like, Brain Damage, Eclipse', 'Progressive Rock', 'Harvest Records', 'Pink Floyd', '1973-03-01'),
(6, 'Wanna Be Startin'' Somethin'', Baby Be Mine, The Girl Is Mine, Thriller, Beat It, Billie Jean, Human Nature, P.Y.T. (Pretty Young Thing), The Lady in My Life', 'Pop', 'Epic Records', 'Michael Jackson', '1982-11-30');

-- DVD-specific data
INSERT OR IGNORE INTO dvds (product_id, release_date, dvd_type, genre, studio, directors, duration_minutes, rating) VALUES
(7, '1994-09-23', 'DVD', 'Drama', 'Columbia Pictures', 'Frank Darabont', 142, 'R'),
(8, '1972-03-24', 'DVD', 'Crime Drama', 'Paramount Pictures', 'Francis Ford Coppola', 175, 'R'),
(9, '1994-10-14', 'DVD', 'Crime', 'Miramax Films', 'Quentin Tarantino', 154, 'R');

-- Insert sample delivery information
INSERT OR IGNORE INTO delivery_information (delivery_id, name, phone, email, address, province, delivery_message, delivery_fee) VALUES
(1, 'John Doe', '0123456789', 'john@example.com', '123 Main Street, District 1', 'Ho Chi Minh City', 'Please call before delivery', 25000),
(2, 'Jane Smith', '0987654321', 'jane@example.com', '456 Oak Avenue, District 3', 'Ho Chi Minh City', 'Leave at front door', 25000);

-- Insert audit logs
INSERT OR IGNORE INTO logger (action_id, action_name, recorded_at, note, action_type) VALUES
(1, 'System Initialization', datetime('now'), 'Database initialized with sample data', 'CREATE'),
(2, 'User Registration', datetime('now'), 'Sample users created', 'CREATE'),
(3, 'Product Setup', datetime('now'), 'Sample products initialized', 'CREATE');