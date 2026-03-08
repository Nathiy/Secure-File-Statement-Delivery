package com.securestatements.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static final String URL =
            "jdbc:mysql://localhost:3306/secure_statements";

    private static final String USER = "root";
    private static final String PASSWORD = "root@password";

    public static Connection getConnection() {

        try {
            LOGGER.info("Attempting to load MySQL JDBC driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("✓ MySQL JDBC driver loaded successfully");

            LOGGER.info("Attempting database connection to: " + URL);
            LOGGER.info("  User: " + USER);

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            LOGGER.info("✓ Database connection established successfully");
            return conn;

        } catch(ClassNotFoundException e){
            LOGGER.log(Level.SEVERE, "MySQL JDBC driver not found. Ensure mysql-connector-java is in classpath", e);
        } catch(Exception e){
            LOGGER.log(Level.SEVERE, "Failed to establish database connection. Check: " +
                    "1) MySQL server is running on localhost:3306 " +
                    "2) Database 'secure_statements' exists " +
                    "3) User 'root' with password 'root@password' has access " +
                    "4) Connection URL is correct: " + URL, e);
        }

        return null;
    }
}