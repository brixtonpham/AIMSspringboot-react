-- SQLite Database Schema for Project_ITSS
-- Created from Entity analysis

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    password INTEGER NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT,
    role TEXT DEFAULT 'customer',
    registration_date TEXT,
    salary REAL DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS products (
  product_id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  category TEXT NOT NULL,               -- corresponds to `category` field
  price INTEGER NOT NULL,
  product_value INTEGER NOT NULL,       -- required for price validation
  weight REAL,
  rush_order_supported BOOLEAN DEFAULT FALSE,
  dimensions TEXT,                      -- physical dimensions
  condition TEXT,                       -- condition (New, Used, etc.)
  image_url TEXT CHECK (length(image_url) <= 500),
  barcode TEXT UNIQUE CHECK (length(barcode) <= 50),
  import_date TEXT,
  introduction TEXT,                    -- TEXT for long description
  quantity INTEGER DEFAULT 0,
  type TEXT CHECK (type IN ('book', 'cd', 'dvd')),
  created_at TEXT DEFAULT (datetime('now')),
  updated_at TEXT DEFAULT (datetime('now'))
);

-- Books table (extends products)
CREATE TABLE IF NOT EXISTS books (
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    genre TEXT,
    page_count INTEGER,
    publication_date TEXT,
    authors TEXT,
    publishers TEXT,
    cover_type TEXT,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- CDs table (extends products)
CREATE TABLE IF NOT EXISTS cds (
    cd_id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER NOT NULL,
    track_list TEXT,
    genre TEXT,
    record_label TEXT,
    artists TEXT,
    release_date TEXT,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- DVDs table (extends products)
CREATE TABLE IF NOT EXISTS dvds (
  dvd_id INTEGER PRIMARY KEY AUTOINCREMENT,
  product_id INTEGER NOT NULL,  -- FK to base Product
  release_date TEXT,
  dvd_type TEXT CHECK (length(dvd_type) <= 50),
  genre TEXT CHECK (length(genre) <= 100),
  studio TEXT CHECK (length(studio) <= 200),
  directors TEXT CHECK (length(directors) <= 500),
  duration_minutes INTEGER,
  rating TEXT CHECK (length(rating) <= 10),
  FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);


-- Delivery Information table
CREATE TABLE IF NOT EXISTS delivery_information (
    delivery_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT NOT NULL,
    address TEXT NOT NULL,
    ward TEXT NOT NULL,
    province TEXT NOT NULL,
    delivery_message TEXT,
    delivery_fee INTEGER DEFAULT 0
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
    total_before_vat INTEGER NOT NULL,
    total_after_vat INTEGER NOT NULL,
    status TEXT DEFAULT 'pending',
    delivery_id INTEGER,
    vat INTEGER DEFAULT 10,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_id) REFERENCES delivery_information(delivery_id)
);

-- Order lines table
CREATE TABLE IF NOT EXISTS orderlines (
    orderline_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    status TEXT DEFAULT 'pending',
    rush_order_using BOOLEAN DEFAULT FALSE,
    quantity INTEGER NOT NULL,
    total_fee INTEGER NOT NULL,
    delivery_time TEXT,
    instructions TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Invoices table
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    transaction_id INTEGER,
    description TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

-- Logger table for audit trail
CREATE TABLE IF NOT EXISTS audit_log (
  action_id INTEGER PRIMARY KEY AUTOINCREMENT,

  action_name TEXT NOT NULL CHECK (length(action_name) <= 255),
  recorded_at TEXT DEFAULT CURRENT_TIMESTAMP,
  note TEXT,

  user_id INTEGER,
  entity_type TEXT CHECK (length(entity_type) <= 50),
  entity_id INTEGER,

  action_type TEXT CHECK (action_type IN (
    'CREATE', 'UPDATE', 'DELETE', 'VIEW', 'LOGIN', 'LOGOUT', 'PAYMENT', 'ORDER', 'CANCEL'
  ))
);


-- VNPay Transactions table (already has JPA annotations)
CREATE TABLE IF NOT EXISTS vnpay_transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id TEXT NOT NULL,
    transaction_no TEXT,
    amount INTEGER,
    bank_code TEXT,
    response_code TEXT,
    transaction_status TEXT,
    pay_date TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_products_type ON products(type);
CREATE INDEX IF NOT EXISTS idx_products_barcode ON products(barcode);
CREATE INDEX IF NOT EXISTS idx_books_product_id ON books(product_id);
CREATE INDEX IF NOT EXISTS idx_cds_product_id ON cds(product_id);
CREATE INDEX IF NOT EXISTS idx_dvds_product_id ON dvds(product_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orderlines_order_id ON orderlines(order_id);
CREATE INDEX IF NOT EXISTS idx_orderlines_product_id ON orderlines(product_id);
CREATE INDEX IF NOT EXISTS idx_vnpay_order_id ON vnpay_transactions(order_id);

-- Insert sample data for testing
-- INSERT OR IGNORE INTO users (user_id, password, name, email, phone, role, registration_date, salary) VALUES
-- (1, 123456, 'Admin User', 'admin@itss.com', '0123456789', 'admin', '2024-01-01', 1000.0),
-- (2, 654321, 'Customer User', 'customer@itss.com', '0987654321', 'customer', '2024-01-02', 0.0);

-- Insert sample products
-- INSERT OR IGNORE INTO products (product_id, title, price, weight, rush_order_supported, image_url, barcode, import_date, introduction, quantity, type) VALUES
-- (1, 'The Great Gatsby', 150000, 0.3, true, 'https://example.com/gatsby.jpg', '9780743273565', '2024-01-01', 'A classic American novel', 50, 'book'),
-- (2, 'Abbey Road - The Beatles', 250000, 0.1, false, 'https://example.com/abbey.jpg', '094639154426', '2024-01-01', 'Classic Beatles album', 30, 'cd'), 
-- (3, 'The Matrix', 200000, 0.2, true, 'https://example.com/matrix.jpg', '085391163928', '2024-01-01', 'Sci-fi action movie', 25, 'dvd');

-- Insert sample book details
-- INSERT OR IGNORE INTO books (book_id, product_id, genre, page_count, publication_date, authors, publishers, cover_type) VALUES
-- (1, 1, 'Fiction', 180, '1925-04-10', 'F. Scott Fitzgerald', 'Scribner', 'Hardcover');

-- Insert sample CD details  
-- INSERT OR IGNORE INTO cds (cd_id, product_id, track_list, genre, record_label, artists, release_date) VALUES
-- (1, 2, 'Come Together, Something, Maxwell''s Silver Hammer, Oh! Darling, Octopus''s Garden, I Want You (She''s So Heavy), Here Comes the Sun, Because, You Never Give Me Your Money, Sun King, Mean Mr. Mustard, Polythene Pam, She Came in Through the Bathroom Window, Golden Slumbers, Carry That Weight, The End, Her Majesty', 'Rock', 'Apple Records', 'The Beatles', '1969-09-26');

-- Insert sample DVD details
-- INSERT OR IGNORE INTO dvds (dvd_id, product_id, title, release_date, dvd_type, genre, studio, directors) VALUES
-- (1, 3, 'The Matrix', '1999-03-31', 'Blu-ray', 'Sci-Fi/Action', 'Warner Bros', 'The Wachowskis');