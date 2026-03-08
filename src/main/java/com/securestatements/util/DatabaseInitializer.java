package com.securestatements.util;

import com.securestatements.App;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseInitializer implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "root@password";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Load the schema.sql from resources
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("schema.sql");
            if (inputStream == null) {
                throw new RuntimeException("schema.sql not found in resources");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }
            reader.close();

            String sqlScript = sqlBuilder.toString();

            // Connect to MySQL without specifying database
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();

            // Split the script by semicolon and execute each statement
            String[] statements = sqlScript.split(";");
            for (String stmt : statements) {
                stmt = stmt.trim();
                if (!stmt.isEmpty()) {
                    statement.execute(stmt);
                }
            }

            statement.close();
            connection.close();

            LOGGER.info("Database initialized successfully.");

            // Initialize all application services
            LOGGER.info("Initializing application services...");
            App.initialize();
            LOGGER.info("Application services initialized successfully.");

        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to initialize application", e);
            LOGGER.log(java.util.logging.Level.SEVERE, "Running database diagnostics to identify issues...", e);
            DatabaseDiagnostics.runDiagnostics();
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Application shutting down...");
        App.shutdown();
    }
}
