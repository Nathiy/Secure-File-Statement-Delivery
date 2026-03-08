package com.securestatements.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseSetup - Standalone utility for setting up the database schema
 * Can be run independently to initialize the database without starting the full application
 */
public class DatabaseSetup {

    private static final Logger LOGGER = Logger.getLogger(DatabaseSetup.class.getName());

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "root@password";

    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   DATABASE SETUP - Initialize Schema                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        try {
            setupDatabase();
            System.out.println("\n✓ Database setup completed successfully!\n");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database setup failed", e);
            System.exit(1);
        }
    }

    public static void setupDatabase() throws Exception {
        LOGGER.info("Step 1: Loading MySQL JDBC driver...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("✓ Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new Exception("MySQL JDBC driver not found. Add mysql-connector-java to dependencies.", e);
        }

        LOGGER.info("Step 2: Connecting to MySQL server (without database)...");
        Connection connection;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("✓ Connected to MySQL server");
        } catch (Exception e) {
            throw new Exception("Failed to connect to MySQL. Check:\n" +
                    "  1) MySQL is running on localhost:3306\n" +
                    "  2) User 'root' with password 'root@password' has access\n" +
                    "  3) Network connectivity to database server", e);
        }

        LOGGER.info("Step 3: Loading SQL schema from resources...");
        InputStream inputStream = DatabaseSetup.class.getClassLoader().getResourceAsStream("schema.sql");
        if (inputStream == null) {
            throw new Exception("schema.sql not found in resources");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sqlBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sqlBuilder.append(line).append("\n");
        }
        reader.close();
        LOGGER.info("✓ Schema loaded from resources");

        LOGGER.info("Step 4: Executing SQL schema...");
        Statement statement = connection.createStatement();
        String sqlScript = sqlBuilder.toString();
        String[] statements = sqlScript.split(";");

        int count = 0;
        for (String stmt : statements) {
            stmt = stmt.trim();
            if (!stmt.isEmpty()) {
                try {
                    statement.execute(stmt);
                    count++;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Could not execute statement: " + stmt.substring(0, Math.min(50, stmt.length())), e);
                }
            }
        }

        statement.close();
        connection.close();

        LOGGER.info("✓ Executed " + count + " SQL statements");
        LOGGER.info("Step 5: Database schema initialized successfully!");
    }
}

