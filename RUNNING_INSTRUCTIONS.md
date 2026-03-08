# Running the Secure File Statement Delivery Application

## Prerequisites

1. **Java 11+** installed
2. **MySQL Server** running on localhost:3306
3. **Maven** installed

## Quick Start

### 1. Ensure MySQL is Running
```bash
mysql -u root -proot@password
# Should connect successfully
```

### 2. Build the Application
```bash
mvn clean compile
```

### 3. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.securestatements.App"
```

### 4. Access the Application
- **Web Interface:** http://localhost:8080/index.jsp
- **Upload Endpoint:** http://localhost:8080/upload
- **Download Endpoint:** http://localhost:8080/download

## What Happens on Startup

1. **Database Initialization**
   - Creates `secure_statements` database if it doesn't exist
   - Creates tables: `customers`, `statements`, `tokens`
   - Logs each step with debug messages

2. **Service Initialization**
   - Initializes DAOs (CustomerDAO, StatementDAO, TokenDAO)
   - Creates `statements/` directory for file storage
   - Verifies all services are ready

3. **Server Startup**
   - Starts embedded Jetty server on port 8080
   - Serves the web application
   - Logs server status and endpoints

## Stopping the Application

Press `Ctrl+C` in the terminal where the application is running.

## Troubleshooting

### Database Connection Issues
```bash
# Test connection manually
mvn exec:java -Dexec.mainClass="com.securestatements.ConnectionTest"
```

### Server Won't Start
- Check if port 8080 is available
- Verify MySQL is running
- Check console logs for error messages

### File Upload Issues
- Ensure `statements/` directory exists and is writable
- Check file size limits in servlet configuration

## Application Features

- ✅ **Secure File Upload** - Multipart file uploads with validation
- ✅ **Token-Based Downloads** - Secure access with expiry tokens
- ✅ **Database Storage** - MySQL backend for metadata
- ✅ **File Storage** - Local filesystem storage
- ✅ **Web Interface** - Simple HTML interface
- ✅ **REST Endpoints** - Programmatic access via HTTP

## Configuration

### Database Settings
Edit `src/main/java/com/securestatements/util/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/secure_statements";
private static final String USER = "root";
private static final String PASSWORD = "root@password";
```

### Server Port
Edit `src/main/java/com/securestatements/App.java`:
```java
server = new Server(8080);  // Change port here
```

### File Storage
Edit `src/main/java/com/securestatements/App.java`:
```java
private static final String STORAGE_DIRECTORY = "statements";
```

## Development

### Running Tests
```bash
mvn test
```

### Building WAR for Deployment
```bash
mvn clean package
# WAR file: target/secure-statements.war
```

### IDE Integration
- Import as Maven project
- Run `App.main()` to start embedded server
- Debug servlets and DAOs directly

## Architecture

```
App.java (Main)
├── DatabaseInitializer (on startup)
│   ├── Creates database/tables
│   └── Initializes services
├── Embedded Jetty Server (port 8080)
│   ├── UploadStatementServlet (/upload)
│   ├── DownloadStatementServlet (/download)
│   └── Web pages (/index.jsp, etc.)
└── Service Layer
    ├── FileService (file operations)
    ├── TokenService (token generation)
    ├── CustomerDAO (database access)
    ├── StatementDAO (database access)
    └── TokenDAO (database access)
```

## Security Features

- **Token-based access control** for downloads
- **Token expiry validation** (10 minutes default)
- **File type validation** (PDF files)
- **Secure file storage** with unique paths
- **Database-backed metadata** tracking

## API Endpoints

### Upload Statement
```
POST /upload
Content-Type: multipart/form-data

Parameters:
- customerId: integer (customer identifier)
- file: multipart file (PDF statement)

Response: "Upload successful" or error message
```

### Download Statement
```
GET /download?token=<secure_token>

Response: PDF file or "Link expired" message
```

## Database Schema

```sql
-- Customers table
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

-- Statements table
CREATE TABLE statements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    file_path VARCHAR(255),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(customer_id) REFERENCES customers(id)
);

-- Tokens table
CREATE TABLE tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    statement_id INT,
    token VARCHAR(255),
    expiry_time BIGINT,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY(statement_id) REFERENCES statements(id)
);
```

## Logs and Monitoring

The application provides detailed logging:
- **Startup sequence** - Step-by-step initialization
- **Database operations** - Connection status and queries
- **File operations** - Upload/download activities
- **Server events** - Jetty server lifecycle
- **Error conditions** - Detailed error messages

All logs use Java's `java.util.logging` framework.
