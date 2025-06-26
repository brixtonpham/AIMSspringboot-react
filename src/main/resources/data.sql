-- Sample Users
INSERT OR IGNORE INTO user (user_id, password, name, email, phone, role, registration_date, salary, is_active) VALUES
(1, 'hashedpassword123', 'Admin User', 'admin@itss.com', '0123456789', 'ADMIN', datetime('now'), 1000.0, 1),
(2, 'hashedpassword456', 'Customer User', 'customer@itss.com', '0987654321', 'CUSTOMER', datetime('now'), 0.0, 1),
(3, 'hashedpassword789', 'Manager User', 'manager@itss.com', '0555123456', 'MANAGER', datetime('now'), 800.0, 1);

-- Base Products
-- Base Products
INSERT OR IGNORE INTO product (
  type, title, price, product_value, weight,
  rush_order_supported, dimensions, condition, image_url,
  barcode, import_date, introduction, quantity,
  created_at, updated_at
) VALUES
-- Books
('book', 'The Great Gatsby', 1599, 1000, 0.3, 0, '20x13x2 cm', 'New', 'https://covers.openlibrary.org/b/id/8225261-L.jpg', '978-0-7432-7356-5', '2024-01-15', 'A classic American novel about the Jazz Age and the American Dream.', 25, datetime('now'), datetime('now')),
('book', 'To Kill a Mockingbird', 1299, 1000, 0.35, 0, '21x14x2.5 cm', 'New', 'https://covers.openlibrary.org/b/id/8226816-L.jpg', '978-0-06-112008-4', '2024-01-10', 'A gripping tale of racial injustice and childhood innocence in the American South.', 30, datetime('now'), datetime('now')),
('book', '1984', 1399, 1000, 0.28, 1, '20x13x2 cm', 'New', 'https://covers.openlibrary.org/b/id/8226112-L.jpg', '978-0-452-28423-4', '2024-01-12', 'A dystopian social science fiction novel about totalitarianism.', 40, datetime('now'), datetime('now')),

-- CDs
('cd', 'Abbey Road', 1999, 1500, 0.1, 0, '14x12x1 cm', 'New', 'https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg', '094638241621', '2024-01-20', 'The Beatles final studio album featuring Come Together.', 15, datetime('now'), datetime('now')),
('cd', 'Dark Side of the Moon', 2199, 1500, 0.1, 1, '14x12x1 cm', 'New', 'https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png', '094637420621', '2024-01-18', 'Pink Floyd masterpiece exploring conflict and madness.', 20, datetime('now'), datetime('now')),
('cd', 'Thriller', 1899, 1500, 0.1, 0, '14x12x1 cm', 'New', 'https://upload.wikimedia.org/wikipedia/en/5/55/Michael_Jackson_-_Thriller.png', '074643851428', '2024-01-22', 'Michael Jackson''s Thriller with Billie Jean and Beat It.', 18, datetime('now'), datetime('now')),

-- DVDs
('dvd', 'The Shawshank Redemption', 2499, 1800, 0.15, 0, '19x13x1.5 cm', 'New', 'https://m.media-amazon.com/images/I/519NBNHX5BL._AC_.jpg', '085391163926', '2024-01-25', 'A story of friendship and redemption in prison.', 12, datetime('now'), datetime('now')),
('dvd', 'The Godfather', 2799, 1800, 0.15, 1, '19x13x1.5 cm', 'New', 'https://m.media-amazon.com/images/I/41+dkHBquGL._AC_.jpg', '097360719147', '2024-01-23', 'The aging patriarch hands control to his son.', 10, datetime('now'), datetime('now')),
('dvd', 'Pulp Fiction', 2399, 1800, 0.15, 0, '19x13x1.5 cm', 'New', 'https://m.media-amazon.com/images/I/71c05lTE03L._AC_SL1024_.jpg', '031398187737', '2024-01-24', 'Tarantino''s cult classic about L.A. criminals.', 14, datetime('now'), datetime('now'));

-- Book Subclass Data
INSERT OR IGNORE INTO book (product_id, genre, page_count, publication_date, authors, publishers, cover_type)
SELECT product_id, 'Classic Literature', 180, '1925-04-10', 'F. Scott Fitzgerald', 'Charles Scribner''s Sons', 'Paperback'
FROM product WHERE barcode = '978-0-7432-7356-5'
UNION
SELECT product_id, 'Classic Literature', 281, '1960-07-11', 'Harper Lee', 'J.B. Lippincott & Co.', 'Paperback'
FROM product WHERE barcode = '978-0-06-112008-4'
UNION
SELECT product_id, 'Science Fiction', 328, '1949-06-08', 'George Orwell', 'Secker & Warburg', 'Paperback'
FROM product WHERE barcode = '978-0-452-28423-4';

-- CD Subclass Data
INSERT OR IGNORE INTO cd (product_id, track_list, genre, record_label, artists, release_date)
SELECT product_id,
'Come Together, Something, Maxwell''s Silver Hammer, Oh! Darling, Octopus''s Garden, I Want You (She''s So Heavy), Here Comes the Sun, Because, You Never Give Me Your Money, Sun King, Mean Mr. Mustard, Polythene Pam, She Came in Through the Bathroom Window, Golden Slumbers, Carry That Weight, The End, Her Majesty',
'Rock', 'Apple Records', 'The Beatles', '1969-09-26'
FROM product WHERE barcode = '094638241621'
UNION
SELECT product_id,
'Speak to Me, Breathe, On the Run, Time, The Great Gig in the Sky, Money, Us and Them, Any Colour You Like, Brain Damage, Eclipse',
'Progressive Rock', 'Harvest Records', 'Pink Floyd', '1973-03-01'
FROM product WHERE barcode = '094637420621'
UNION
SELECT product_id,
'Wanna Be Startin'' Somethin'', Baby Be Mine, The Girl Is Mine, Thriller, Beat It, Billie Jean, Human Nature, P.Y.T. (Pretty Young Thing), The Lady in My Life',
'Pop', 'Epic Records', 'Michael Jackson', '1982-11-30'
FROM product WHERE barcode = '074643851428';

-- DVD Subclass Data
INSERT OR IGNORE INTO dvd (product_id, release_date, dvd_type, genre, studio, directors, duration_minutes, rating)
SELECT product_id, '1994-09-23', 'DVD', 'Drama', 'Columbia Pictures', 'Frank Darabont', 142, 'R'
FROM product WHERE barcode = '085391163926'
UNION
SELECT product_id, '1972-03-24', 'DVD', 'Crime Drama', 'Paramount Pictures', 'Francis Ford Coppola', 175, 'R'
FROM product WHERE barcode = '097360719147'
UNION
SELECT product_id, '1994-10-14', 'DVD', 'Crime', 'Miramax Films', 'Quentin Tarantino', 154, 'R'
FROM product WHERE barcode = '031398187737';

-- Delivery Info
INSERT OR IGNORE INTO delivery_information (delivery_id, name, phone, email, address, ward, province, delivery_message, delivery_fee) VALUES
(1, 'John Doe', '0123456789', 'john@example.com', '123 Main Street', 'Ward 1', 'Ho Chi Minh City', 'Please call before delivery', 25000),
(2, 'Jane Smith', '0987654321', 'jane@example.com', '456 Oak Avenue', 'Ward 3', 'Ho Chi Minh City', 'Leave at front door', 25000);

-- Audit Logs
INSERT OR IGNORE INTO audit_log (audit_log_id, action_name, recorded_at, note, action_type) VALUES
(1, 'System Initialization', datetime('now'), 'Database initialized with sample data', 'CREATE'),
(2, 'User Registration', datetime('now'), 'Sample users created', 'CREATE'),
(3, 'Product Setup', datetime('now'), 'Sample products initialized', 'CREATE');