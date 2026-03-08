SELECT 'Initializing secure_statements database...' AS status;
CREATE DATABASE IF NOT EXISTS secure_statements;
SELECT 'Database secure_statements created or already exists' AS status;

USE secure_statements;
SELECT 'Using database secure_statements' AS status;

SELECT 'Creating customers table...' AS status;
CREATE TABLE IF NOT EXISTS customers(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);
SELECT 'Table customers created or already exists' AS status;

SELECT 'Creating statements table...' AS status;
CREATE TABLE IF NOT EXISTS statements(
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    file_path VARCHAR(255),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(customer_id) REFERENCES customers(id)
);
SELECT 'Table statements created or already exists' AS status;

SELECT 'Creating tokens table...' AS status;
CREATE TABLE IF NOT EXISTS tokens(
    id INT AUTO_INCREMENT PRIMARY KEY,
    statement_id INT,
    token VARCHAR(255),
    expiry_time BIGINT,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY(statement_id) REFERENCES statements(id)
);
SELECT 'Table tokens created or already exists' AS status;
