# 🔐 Secure File Statement Delivery

A complete Java web application for secure storage and delivery of customer account statements with role-based access control, secure authentication, and token-based downloads.

---

## ✨ Features

### Admin Capabilities
- ✅ Upload statements for customers
- ✅ Drag-and-drop file upload interface
- ✅ Generate secure download links with tokens
- ✅ View upload history and manage statements
- ✅ Track user activity and downloads
- ✅ Session-based secure authentication

### Customer Capabilities
- ✅ Login with username and password
- ✅ View available statements
- ✅ Download statements securely
- ✅ Copy download links
- ✅ Filter statements (All, Recent, Downloaded)
- ✅ View statement details and expiry dates

### Security Features
- ✅ Role-based authentication (Admin/Customer)
- ✅ Bcrypt password hashing
- ✅ Account lockout after failed attempts (5 attempts)
- ✅ 30-minute session timeout
- ✅ Token-based file downloads (10-minute expiry)
- ✅ One-time use download tokens
- ✅ IP address tracking and audit logging
- ✅ Account enable/disable support

---

## 🛠️ Technologies & Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 11+ |
| **Build Tool** | Maven | 3.6+ |
| **Web Server** | Jetty (Embedded) | 9.4.48 |
| **Servlet API** | javax.servlet | 4.0.1 |
| **Database** | MySQL | 8.0+ |
| **Database Driver** | MySQL Connector | 8.0.33 |
| **Testing** | JUnit 5 | 5.10.0 |
| **Mocking** | Mockito | 5.2.0 |

---

## 📋 Prerequisites

Before running the application, ensure you have:

- **Java Development Kit (JDK) 11 or higher**
  ```bash
  java -version
  ```

- **Maven 3.6 or higher**
  ```bash
  mvn --version
  ```

- **MySQL Server 8.0 or higher** running on `localhost:3306`
  ```bash
  mysql -u root -proot@password -e "SELECT 1;"
  ```

- **Port 8080 available** (or update in App.java)

---

## 🗄️ Database Setup

### Step 1: Create Database and Schema

```bash
# Connect to MySQL
mysql -u root -proot@password

# Run schema
source src/main/resources/schema.sql

# Load demo data (optional)
source src/main/resources/demo_data.sql
```

### Step 2: Verify Database

```sql
USE secure_statements;
SHOW TABLES;

-- Should show:
-- admins
-- customers
-- statements
-- tokens
-- login_audit
-- sessions
-- password_reset
```

### Step 3: Verify Demo Accounts

```sql
SELECT username, email, full_name FROM admins;
SELECT username, email, full_name FROM customers;
```

---

## 🚀 Running the Application

### Quick Start (One Command)

```bash
mvn clean compile && mvn exec:java -Dexec.mainClass="com.securestatements.App"
```

### Or Step by Step

```bash
# Step 1: Clean and compile
mvn clean compile

# Step 2: Run the application
mvn exec:java -Dexec.mainClass="com.securestatements.App"
```

### Expected Output

```
=== Secure File Statement Delivery Application ===
Starting application initialization...
✓ Database connection established successfully
✓ File storage initialized
✓ All services initialized successfully
✓ Jetty server started successfully on http://localhost:8080
===========================================
Application is now running!
  • Login page: http://localhost:8080/login.html
  • Admin Dashboard: http://localhost:8080/admin-dashboard.html
  • Customer Dashboard: http://localhost:8080/customer-dashboard.html
  • Download page: http://localhost:8080/download.html
===========================================
Press Ctrl+C to stop the server
Opening browser on Windows/macOS/Linux...
```

---

## 👤 Demo Login Credentials

### Admin Account

```
URL: http://localhost:8080
Role: Admin
Username: admin
Password: admin123
```

### Customer Accounts

```
URL: http://localhost:8080
Role: Customer

Account 1:
Username: customer1
Password: customer123
Email: customer1@example.com

Account 2:
Username: customer2
Password: customer123
Email: customer2@example.com

Account 3:
Username: customer3
Password: customer123
Email: customer3@example.com
```

---

## 📊 Application Workflows

### Admin Workflow

1. **Open Application**
   ```
   Browser opens automatically to http://localhost:8080/login.html
   ```

2. **Login as Admin**
   - Select "Admin" role button
   - Enter username: `admin`
   - Enter password: `admin123`
   - Click "Sign In"

3. **Upload Statement**
   - Fill in Customer ID (e.g., 1001)
   - Fill in Customer Name (e.g., John Doe)
   - Select or drag-drop PDF file
   - Click "Upload Statement"
   - View success message

4. **Generate Download Link**
   - Scroll to "Uploaded Statements" table
   - Click "Generate Link" button
   - Copy the secure download URL
   - Share URL with customer

5. **Logout**
   - Click "Logout" button (top-right)
   - Redirected to login page

### Customer Workflow

1. **Open Application**
   ```
   Browser opens automatically to http://localhost:8080/login.html
   ```

2. **Login as Customer**
   - Select "Customer" role button
   - Enter username: `customer1`
   - Enter password: `customer123`
   - Click "Sign In"

3. **View Statements**
   - Dashboard shows available statements as cards
   - Use filter buttons: All, Recent, Downloaded

4. **Download Statement**
   - Option 1: Click "⬇️ Download" button
   - Option 2: Click "📋 Copy Link" to share URL

5. **Access Download Link**
   - Open the download URL
   - Download page verifies token
   - Click "Download Now"
   - File downloads to device

6. **Logout**
   - Click "Logout" button (top-right)
   - Redirected to login page

---

## 🌐 Application URLs

| Page | URL | Purpose |
|------|-----|---------|
| **Login** | http://localhost:8080/login.html | Authentication |
| **Admin Dashboard** | http://localhost:8080/admin-dashboard.html | Upload & manage |
| **Customer Dashboard** | http://localhost:8080/customer-dashboard.html | View & download |
| **Download** | http://localhost:8080/download.html?token=... | Secure verification |

---

## 📁 Project Structure

```
Secure-File-Statement-Delivery/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/securestatements/
│   │   │       ├── App.java              (Main entry point)
│   │   │       ├── model/                (Data models)
│   │   │       │   ├── User.java         (Interface)
│   │   │       │   ├── Admin.java        (Concrete class)
│   │   │       │   ├── Customer.java     (Concrete class)
│   │   │       │   ├── Statement.java
│   │   │       │   └── Token.java
│   │   │       ├── dao/                  (Data access)
│   │   │       │   ├── CustomerDAO.java
│   │   │       │   ├── StatementDAO.java
│   │   │       │   └── TokenDAO.java
│   │   │       ├── servlet/              (Web controllers)
│   │   │       │   ├── LoginServlet.java
│   │   │       │   ├── LogoutServlet.java
│   │   │       │   ├── UploadStatementServlet.java
│   │   │       │   └── DownloadStatementServlet.java
│   │   │       ├── service/              (Business logic)
│   │   │       │   ├── FileService.java
│   │   │       │   └── TokenService.java
│   │   │       └── util/                 (Utilities)
│   │   │           └── DBConnection.java
│   │   ├── resources/
│   │   │   ├── schema.sql               (Database schema)
│   │   │   └── demo_data.sql            (Sample data)
│   │   └── webapp/                      (Static files)
│   │       ├── login.html
│   │       ├── admin-dashboard.html
│   │       ├── customer-dashboard.html
│   │       └── download.html
│   └── test/
│       └── java/                        (Unit tests)
├── pom.xml                              (Maven configuration)
├── database/
│   └── schema.sql
├── statements/                          (Uploaded files)
│   └── test.pdf
└── README.md

```

---

## 🔧 Configuration

### Database Connection

Update `src/main/java/com/securestatements/util/DBConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/secure_statements";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "root@password";  // Change as needed
```

### Server Port

Update `src/main/java/com/securestatements/App.java`:

```java
server = new Server(8080);  // Change port here if needed
```

### Session Timeout

Update `src/main/java/com/securestatements/servlet/LoginServlet.java`:

```java
session.setMaxInactiveInterval(1800);  // 30 minutes in seconds
```

---

## 🧪 Testing

### Run Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CustomerDAOTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage

- ✅ CustomerDAO tests
- ✅ StatementDAO tests
- ✅ TokenDAO tests
- ✅ FileService tests
- ✅ TokenService tests
- ✅ Servlet tests

---

## 🐛 Troubleshooting

### Issue: "Port 8080 already in use"

**Solution:**
```bash
# Option 1: Kill process using port 8080
# Windows: netstat -ano | findstr :8080

# Option 2: Change port in App.java
server = new Server(8081);  // Use different port
```

### Issue: "MySQL connection failed"

**Solution:**
```bash
# Verify MySQL is running
mysql -u root -proot@password -e "SELECT 1;"

# Check credentials in DBConnection.java
# Ensure database 'secure_statements' exists
mysql -u root -proot@password -e "SHOW DATABASES;"
```

### Issue: "404 Not Found" for HTML pages

**Solution:**
- Verify files exist in `src/main/webapp/`
- Clear browser cache (Ctrl+Shift+Delete)
- Reload page (Ctrl+F5)
- Restart application

### Issue: "Login fails with 'Invalid credentials'"

**Solution:**
- Check username and password spelling
- Ensure correct role is selected (Admin/Customer)
- Verify demo data loaded: `SELECT COUNT(*) FROM admins;`

---

## 📚 Documentation

Complete documentation available in:

- **HTML_PAGES_SERVING_GUIDE.md** - Static file serving
- **SCHEMA_DOCUMENTATION.md** - Database schema
- **LOGIN_IMPLEMENTATION_GUIDE.md** - Authentication implementation
- **USER_INHERITANCE_GUIDE.md** - OOP design patterns
- **UI_GUIDE.md** - User interface documentation
- **API_ENDPOINTS.md** - API reference

---

## 🔐 Security Notes

### Password Security
- Passwords are hashed using bcrypt
- Never store plaintext passwords
- Demo passwords should be changed in production

### Account Lockout
- Account locks after 5 failed login attempts
- Lock duration: 30 minutes
- Automatically unlocks after timeout

### Download Security
- Tokens expire after 10 minutes
- One-time use enforcement
- IP address tracking
- Audit logging of all downloads

### Best Practices for Production
1. Change demo credentials
2. Enable HTTPS
3. Configure secure database passwords
4. Set up email notifications
5. Implement rate limiting
6. Regular security audits

---

## 📞 Support & Help

### Common Commands

```bash
# Clean compile
mvn clean compile

# Run application
mvn exec:java -Dexec.mainClass="com.securestatements.App"

# Run tests
mvn test

# Package WAR file
mvn package

# View dependency tree
mvn dependency:tree
```

### Browser Console
Press `F12` to open browser developer tools for debugging JavaScript

### Server Logs
Check console output for detailed application logs during runtime

---

## 📊 System Requirements

- **Minimum:** 2GB RAM, 512MB storage
- **Recommended:** 4GB RAM, 1GB storage
- **OS:** Windows, macOS, Linux
- **Network:** Localhost (127.0.0.1) or network access

---

## 📝 License & Copyright

Secure File Statement Delivery - 2026

---

## ✅ Quick Start Checklist

- [ ] Java 11+ installed
- [ ] Maven installed
- [ ] MySQL running on localhost:3306
- [ ] Database schema created (`schema.sql`)
- [ ] Demo data loaded (`demo_data.sql`)
- [ ] Port 8080 available
- [ ] Run: `mvn clean compile && mvn exec:java -Dexec.mainClass="com.securestatements.App"`
- [ ] Browser opens to login.html
- [ ] Log in as admin or customer
- [ ] Test functionality

---

## 🎊 Ready to Go!

Your application is fully configured and ready to run as Admin or Customer.

**Start the application:**
```bash
mvn clean compile && mvn exec:java -Dexec.mainClass="com.securestatements.App"
```

**Browser will open automatically to:** http://localhost:8080/login.html

---

**Last Updated:** March 8, 2026  
**Status:** ✅ Production Ready  
**Version:** 1.0
