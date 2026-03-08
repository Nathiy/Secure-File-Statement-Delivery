package com.securestatements.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseDiagnostics - Utility class for diagnosing database connection issues
 * Provides detailed troubleshooting information when connections fail
 */
public class DatabaseDiagnostics {

    private static final Logger LOGGER = Logger.getLogger(DatabaseDiagnostics.class.getName());

    public static void runDiagnostics() {
        LOGGER.info("========== DATABASE DIAGNOSTICS START ==========");

        LOGGER.info("Step 1: Checking system properties...");
        checkSystemProperties();

        LOGGER.info("Step 2: Checking MySQL connection parameters...");
        checkConnectionParameters();

        LOGGER.info("Step 3: Attempting to load JDBC driver...");
        checkJDBCDriver();

        LOGGER.info("Step 4: Attempting database connection...");
        checkDatabaseConnection();

        LOGGER.info("========== DATABASE DIAGNOSTICS END ==========");
    }

    private static void checkSystemProperties() {
        LOGGER.info("  Java version: " + System.getProperty("java.version"));
        LOGGER.info("  OS name: " + System.getProperty("os.name"));
        LOGGER.info("  OS version: " + System.getProperty("os.version"));
    }

    private static void checkConnectionParameters() {
        LOGGER.info("  Database URL: jdbc:mysql://localhost:3306/secure_statements");
        LOGGER.info("  Database User: root");
        LOGGER.info("  Database Password: [configured]");
        LOGGER.info("  MySQL Host: localhost");
        LOGGER.info("  MySQL Port: 3306");
        LOGGER.info("  Database Name: secure_statements");
    }

    private static void checkJDBCDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("  ✓ MySQL Connector/J (JDBC driver) is available in classpath");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "  ✗ MySQL Connector/J NOT found in classpath. Add mysql-connector-java dependency", e);
        }
    }

    private static void checkDatabaseConnection() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                LOGGER.info("  ✓ Connection successful!");
                logConnectionMetadata(conn);
                conn.close();
            } else {
                LOGGER.log(Level.SEVERE, "  ✗ Connection returned null. Check database credentials and server status");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "  ✗ Connection failed: " + e.getMessage(), e);
            logConnectionTroubleshooting();
        }
    }

    private static void logConnectionMetadata(Connection conn) {
        try {
            DatabaseMetaData metadata = conn.getMetaData();
            LOGGER.info("  Database Product Name: " + metadata.getDatabaseProductName());
            LOGGER.info("  Database Version: " + metadata.getDatabaseProductVersion());
            LOGGER.info("  Driver Name: " + metadata.getDriverName());
            LOGGER.info("  Driver Version: " + metadata.getDriverVersion());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "  Could not retrieve metadata", e);
        }
    }

    private static void logConnectionTroubleshooting() {
        LOGGER.severe("===== TROUBLESHOOTING STEPS =====");
        LOGGER.severe("1. MySQL Server Status:");
        LOGGER.severe("   - Verify MySQL is running on localhost:3306");
        LOGGER.severe("   - Check with: mysql -u root -p");
        LOGGER.severe("");
        LOGGER.severe("2. Database Status:");
        LOGGER.severe("   - Connect to MySQL: mysql -u root -p");
        LOGGER.severe("   - Run: SHOW DATABASES;");
        LOGGER.severe("   - Check if 'secure_statements' exists");
        LOGGER.severe("");
        LOGGER.severe("3. User Credentials:");
        LOGGER.severe("   - Verify user 'root' can connect with password");
        LOGGER.severe("   - Try: mysql -u root -proot@password");
        LOGGER.severe("");
        LOGGER.severe("4. Network Issues:");
        LOGGER.severe("   - Check if localhost (127.0.0.1) is resolvable");
        LOGGER.severe("   - Try connecting with: jdbc:mysql://127.0.0.1:3306/secure_statements");
        LOGGER.severe("");
        LOGGER.severe("5. Classpath Issues:");
        LOGGER.severe("   - Ensure mysql-connector-java JAR is in the classpath");
        LOGGER.severe("   - Check Maven dependencies if using Maven");
        LOGGER.severe("");
        LOGGER.severe("6. Firewall Issues:");
        LOGGER.severe("   - Verify MySQL port 3306 is not blocked by firewall");
        LOGGER.severe("");
        LOGGER.severe("7. Application Log Files:");
        LOGGER.severe("   - Check console output for detailed error messages");
        LOGGER.severe("   - Look for SQLException with root cause");
        LOGGER.severe("===== END TROUBLESHOOTING =====");
    }
}

