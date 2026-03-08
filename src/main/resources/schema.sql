SELECT 'Initializing secure_statements database...' AS status;
CREATE DATABASE IF NOT EXISTS secure_statements;
SELECT 'Database secure_statements created or already exists' AS status;

USE secure_statements;
SELECT 'Using database secure_statements' AS status;

SELECT 'Creating admins table...' AS status;
CREATE TABLE IF NOT EXISTS admins(
    id INT AUTO_INCREMENT PRIMARY KEY,rom User
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    last_login_ip VARCHAR(45),
    login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP NULL
);
SELECT 'Table admins created or already exists' AS status;

SELECT 'Creating customers table...' AS status;
CREATE TABLE IF NOT EXISTS customers(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    last_login_ip VARCHAR(45),
    login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP NULL
);
SELECT 'Table customers created or already exists' AS status;

SELECT 'Creating statements table...' AS status;
CREATE TABLE IF NOT EXISTS statements(
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    file_path VARCHAR(255),
    file_name VARCHAR(255),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uploaded_by INT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY(customer_id) REFERENCES customers(id),
    FOREIGN KEY(uploaded_by) REFERENCES admins(id)
);
SELECT 'Table statements created or already exists' AS status;

SELECT 'Creating tokens table...' AS status;
CREATE TABLE IF NOT EXISTS tokens(
    id INT AUTO_INCREMENT PRIMARY KEY,
    statement_id INT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_time BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN DEFAULT FALSE,
    used_at TIMESTAMP NULL,
    used_by_customer_id INT,
    used_by_ip VARCHAR(45),
    FOREIGN KEY(statement_id) REFERENCES statements(id),
    FOREIGN KEY(used_by_customer_id) REFERENCES customers(id)
);
SELECT 'Table tokens created or already exists' AS status;

SELECT 'Creating login_audit table...' AS status;
CREATE TABLE IF NOT EXISTS login_audit(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_type VARCHAR(20) NOT NULL,
    user_id INT NOT NULL,
    username VARCHAR(50),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    reason VARCHAR(255)
);
SELECT 'Table login_audit created or already exists' AS status;

SELECT 'Creating sessions table...' AS status;
CREATE TABLE IF NOT EXISTS sessions(
    session_id VARCHAR(255) PRIMARY KEY,
    user_type VARCHAR(20) NOT NULL,
    user_id INT NOT NULL,
    username VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE
);
SELECT 'Table sessions created or already exists' AS status;

SELECT 'Creating password_reset table...' AS status;
CREATE TABLE IF NOT EXISTS password_reset(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_type VARCHAR(20) NOT NULL,
    user_id INT NOT NULL,
    reset_token VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    used_at TIMESTAMP NULL
);
SELECT 'Table password_reset created or already exists' AS status;

SELECT 'Database initialization completed successfully!' AS status;

