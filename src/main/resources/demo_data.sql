-- Secure File Statement Delivery - Demo Data Initialization
-- This script inserts demo admin and customer accounts for testing

USE secure_statements;

SELECT 'Inserting demo admin accounts...' AS status;

-- Demo admin account (password: admin123 - should be hashed in production)
-- For bcrypt hashed "admin123": $2a$10$slYQmyNdGzin7olVMEXNv.xJk7jDBJ1Yj0K3Fo4dQCqYfCVqqzqNu
INSERT IGNORE INTO admins (username, password_hash, email, full_name, is_active)
VALUES (
    'admin',
    '$2a$10$slYQmyNdGzin7olVMEXNv.xJk7jDBJ1Yj0K3Fo4dQCqYfCVqqzqNu',
    'admin@securefile.com',
    'System Administrator',
    TRUE
);

SELECT 'Demo admin account inserted' AS status;

SELECT 'Inserting demo customer accounts...' AS status;

-- Demo customer account 1 (password: customer123 - should be hashed in production)
-- For bcrypt hashed "customer123": $2a$10$xh3X5pRxUUKVE5r2bHjAq.EbNhkNcHJdGVT6HFjT2Hq4Y9J7q9Gwe
INSERT IGNORE INTO customers (username, password_hash, email, full_name, is_active)
VALUES (
    'customer1',
    '$2a$10$xh3X5pRxUUKVE5r2bHjAq.EbNhkNcHJdGVT6HFjT2Hq4Y9J7q9Gwe',
    'customer1@example.com',
    'John Doe',
    TRUE
);

-- Demo customer account 2
INSERT IGNORE INTO customers (username, password_hash, email, full_name, is_active)
VALUES (
    'customer2',
    '$2a$10$xh3X5pRxUUKVE5r2bHjAq.EbNhkNcHJdGVT6HFjT2Hq4Y9J7q9Gwe',
    'customer2@example.com',
    'Jane Smith',
    TRUE
);

-- Demo customer account 3
INSERT IGNORE INTO customers (username, password_hash, email, full_name, is_active)
VALUES (
    'customer3',
    '$2a$10$xh3X5pRxUUKVE5r2bHjAq.EbNhkNcHJdGVT6HFjT2Hq4Y9J7q9Gwe',
    'customer3@example.com',
    'Alice Johnson',
    TRUE
);

SELECT 'Demo customer accounts inserted' AS status;

SELECT 'Inserting demo statements...' AS status;

-- Demo statement 1
INSERT IGNORE INTO statements (customer_id, file_name, file_path, uploaded_by, is_active)
VALUES (
    1,
    'Q1_2026_Statement.pdf',
    'statements/doc_1.pdf',
    1,
    TRUE
);

-- Demo statement 2
INSERT IGNORE INTO statements (customer_id, file_name, file_path, uploaded_by, is_active)
VALUES (
    2,
    'Q1_2026_Statement.pdf',
    'statements/doc_2.pdf',
    1,
    TRUE
);

-- Demo statement 3
INSERT IGNORE INTO statements (customer_id, file_name, file_path, uploaded_by, is_active)
VALUES (
    3,
    'Annual_Report_2025.pdf',
    'statements/doc_3.pdf',
    1,
    TRUE
);

SELECT 'Demo statements inserted' AS status;

SELECT 'Demo data initialization completed!' AS status;

-- Verify data was inserted
SELECT 'Admin accounts:' AS type;
SELECT id, username, email, full_name FROM admins WHERE is_active = TRUE;

SELECT 'Customer accounts:' AS type;
SELECT id, username, email, full_name FROM customers WHERE is_active = TRUE;

SELECT 'Statements:' AS type;
SELECT s.id, s.file_name, c.full_name AS customer, s.upload_date
FROM statements s
JOIN customers c ON s.customer_id = c.id
WHERE s.is_active = TRUE;

