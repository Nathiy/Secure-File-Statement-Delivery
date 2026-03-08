package com.securestatements;

import com.securestatements.dao.CustomerDAO;
import com.securestatements.dao.StatementDAO;
import com.securestatements.dao.TokenDAO;
import com.securestatements.util.DBConnection;
import com.securestatements.servlet.UploadStatementServlet;
import com.securestatements.servlet.DownloadStatementServlet;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * App.java - Main application entry point and service initializer
 *
 * This class is responsible for initializing and managing all services required
 * for secure file statement delivery including:
 * - Database connectivity
 * - File storage services
 * - Token generation and validation services
 * - Customer and statement data access
 *
 * The application provides secure upload and download functionality for statements
 * with token-based access control and expiry validation.
 */
public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private static final String STORAGE_DIRECTORY = "statements";
    private static final String LOG_PREFIX = "=== Secure File Statement Delivery Application ===";

    // Service instances
    private static CustomerDAO customerDAO;
    private static StatementDAO statementDAO;
    private static TokenDAO tokenDAO;

    private App() {
    }

    /**
     * Initializes all services required by the application
     *
     * @throws Exception if initialization fails at any step
     */
    public static void initialize() throws Exception {
        try {
            LOGGER.info(LOG_PREFIX);
            LOGGER.info("Starting application initialization...");

            initializeDatabaseConnection();
            initializeDAOs();
            initializeFileStorage();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize application", e);
            throw new Exception("Application initialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Initializes database connection and verifies connectivity
     *
     * @throws Exception if database connection fails
     */
    private static void initializeDatabaseConnection() throws Exception {
        LOGGER.info("Initializing database connection...");

        try {
            if (DBConnection.getConnection() != null) {
                LOGGER.info("✓ Database connection established successfully");
            } else {
                throw new Exception("Failed to establish database connection");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database connection initialization failed", e);
            throw new Exception("Database initialization error: " + e.getMessage(), e);
        }
    }

    private static void initializeDAOs() {
        LOGGER.info("Initializing Data Access Objects...");

        customerDAO = new CustomerDAO();
        statementDAO = new StatementDAO();
        tokenDAO = new TokenDAO();
    }

    /**
     * Initializes file storage directory and verifies it's accessible
     *
     * @throws Exception if file storage cannot be initialized
     */
    private static void initializeFileStorage() throws Exception {
        LOGGER.info("Initializing file storage...");

        try {
            File storageDir = new File(STORAGE_DIRECTORY);

            if (!storageDir.exists()) {
                if (storageDir.mkdirs()) {
                    LOGGER.info("✓ Created storage directory: " + STORAGE_DIRECTORY);
                } else {
                    throw new Exception("Failed to create storage directory");
                }
            } else {
                LOGGER.info("✓ Storage directory already exists: " + STORAGE_DIRECTORY);
            }

            if (!storageDir.canWrite()) {
                throw new Exception("Storage directory is not writable");
            }

            LOGGER.info("✓ File storage initialized and verified");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "File storage initialization failed", e);
            throw new Exception("File storage initialization error: " + e.getMessage(), e);
        }
    }


    /**
     * Starts the application by initializing all services and starting the embedded web server
     *
     * This method initializes the application services and starts an embedded Jetty server
     * on localhost:8080 to serve the web application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Server server = null;
        try {
            // Initialize all application services
            initialize();
            LOGGER.info("Application services initialized successfully.");

            // Start embedded Jetty server
            LOGGER.info("Starting embedded Jetty server on localhost:8080...");

            server = new Server(8080);

            // Create servlet context handler
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            // Add servlets
            ServletHolder uploadHolder = new ServletHolder();
            uploadHolder.setClassName(UploadStatementServlet.class.getName());
            uploadHolder.setName("upload");
            context.addServlet(uploadHolder, "/upload");

            ServletHolder downloadHolder = new ServletHolder();
            downloadHolder.setClassName(DownloadStatementServlet.class.getName());
            downloadHolder.setName("download");
            context.addServlet(downloadHolder, "/download");

            // Serve static files from webapp directory
            context.setResourceBase("src/main/webapp");
            context.setWelcomeFiles(new String[]{"index.jsp"});

            server.setHandler(context);

            // Start the server
            server.start();
            LOGGER.info("✓ Jetty server started successfully on http://localhost:8080");
            LOGGER.info("===========================================");
            LOGGER.info("Application is now running!");
            LOGGER.info("  • Upload endpoint: http://localhost:8080/upload");
            LOGGER.info("  • Download endpoint: http://localhost:8080/download");
            LOGGER.info("  • Web interface: http://localhost:8080/index.jsp");
            LOGGER.info("===========================================");
            LOGGER.info("Press Ctrl+C to stop the server");

            // Wait for the server to be stopped
            server.join();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start application", e);
            System.exit(1);
        } finally {
            if (server != null) {
                try {
                    LOGGER.info("Stopping server...");
                    server.stop();
                    shutdown();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error stopping server", e);
                }
            }
        }
    }

    // ===== Getter methods for accessing initialized services =====

    /**
     * Gets the initialized CustomerDAO instance
     *
     * @return CustomerDAO instance
     */
    public static CustomerDAO getCustomerDAO() {
        return customerDAO;
    }

    /**
     * Gets the initialized StatementDAO instance
     *
     * @return StatementDAO instance
     */
    public static StatementDAO getStatementDAO() {
        return statementDAO;
    }

    /**
     * Gets the initialized TokenDAO instance
     *
     * @return TokenDAO instance
     */
    public static TokenDAO getTokenDAO() {
        return tokenDAO;
    }


    /**
     * Performs graceful shutdown of the application
     * Closes any open resources and logs shutdown information
     */
    public static void shutdown() {
        LOGGER.info("Shutting down application...");
        LOGGER.info("✓ All services shut down successfully");
    }
}

