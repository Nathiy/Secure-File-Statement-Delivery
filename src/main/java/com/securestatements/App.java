package com.securestatements;

import com.securestatements.dao.CustomerDAO;
import com.securestatements.dao.StatementDAO;
import com.securestatements.dao.TokenDAO;
import com.securestatements.util.DBConnection;
import com.securestatements.servlet.UploadStatementServlet;
import com.securestatements.servlet.DownloadStatementServlet;
import com.securestatements.servlet.LoginServlet;
import com.securestatements.servlet.LogoutServlet;
import com.securestatements.servlet.AdminStatementServlet;
import com.securestatements.servlet.CustomerStatementServlet;
import com.securestatements.servlet.VerifyTokenServlet;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;

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
            context.setResourceBase("src/main/webapp");

            // Add custom application servlets (POST endpoints)
            ServletHolder uploadHolder = new ServletHolder();
            uploadHolder.setClassName(UploadStatementServlet.class.getName());
            uploadHolder.setName("upload");
            context.addServlet(uploadHolder, "/upload");
            context.addServlet(uploadHolder, "/admin/upload");  // Admin route

            ServletHolder downloadHolder = new ServletHolder();
            downloadHolder.setClassName(DownloadStatementServlet.class.getName());
            downloadHolder.setName("download");
            context.addServlet(downloadHolder, "/download");

            ServletHolder loginHolder = new ServletHolder();
            loginHolder.setClassName(LoginServlet.class.getName());
            loginHolder.setName("login");
            context.addServlet(loginHolder, "/login");

            ServletHolder logoutHolder = new ServletHolder();
            logoutHolder.setClassName(LogoutServlet.class.getName());
            logoutHolder.setName("logout");
            context.addServlet(logoutHolder, "/logout");

            // Add admin statement servlet
            ServletHolder adminStmtHolder = new ServletHolder();
            adminStmtHolder.setClassName(AdminStatementServlet.class.getName());
            adminStmtHolder.setName("adminStatements");
            context.addServlet(adminStmtHolder, "/admin/statements");
            context.addServlet(adminStmtHolder, "/admin/generate-link/*");

            // Add customer statement servlet
            ServletHolder customerStmtHolder = new ServletHolder();
            customerStmtHolder.setClassName(CustomerStatementServlet.class.getName());
            customerStmtHolder.setName("customerStatements");
            context.addServlet(customerStmtHolder, "/customer/statements");

            // Add token verification servlet
            ServletHolder verifyTokenHolder = new ServletHolder();
            verifyTokenHolder.setClassName(VerifyTokenServlet.class.getName());
            verifyTokenHolder.setName("verifyToken");
            context.addServlet(verifyTokenHolder, "/verify-token/*");

            // Add default servlet for static files (HTML, CSS, JS, images)
            ServletHolder defaultHolder = new ServletHolder("default", DefaultServlet.class);
            defaultHolder.setInitParameter("resourceBase", "src/main/webapp");
            defaultHolder.setInitParameter("dirAllowed", "false");
            context.addServlet(defaultHolder, "/");

            // Set welcome files (files served when accessing a directory)
            context.setWelcomeFiles(new String[]{"login.html", "index.html"});

            server.setHandler(context);

            // Start the server
            server.start();
            LOGGER.info("✓ Jetty server started successfully on http://localhost:8080");
            LOGGER.info("===========================================");
            LOGGER.info("Application is now running!");
            LOGGER.info("  • Login page: http://localhost:8080/login.html");
            LOGGER.info("  • Admin Dashboard: http://localhost:8080/admin-dashboard.html");
            LOGGER.info("  • Customer Dashboard: http://localhost:8080/customer-dashboard.html");
            LOGGER.info("  • Download page: http://localhost:8080/download.html");
            LOGGER.info("===========================================");
            LOGGER.info("Press Ctrl+C to stop the server");

            // Open default browser with login page
            openBrowserToLoginPage();

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

    /**
     * Opens the default web browser to the login page
     * This method attempts to open the application in the system's default browser
     */
    private static void openBrowserToLoginPage() {
        try {
            String url = "http://localhost:8080/login.html";
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                // Windows
                Runtime.getRuntime().exec("cmd /c start " + url);
                LOGGER.info("Opening browser on Windows...");
            } else if (osName.contains("mac")) {
                // macOS
                Runtime.getRuntime().exec(new String[]{"open", url});
                LOGGER.info("Opening browser on macOS...");
            } else if (osName.contains("linux")) {
                // Linux
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                LOGGER.info("Opening browser on Linux...");
            } else {
                // Fallback
                LOGGER.info("Unable to auto-open browser. Please visit: " + url);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not open browser automatically. Please visit: http://localhost:8080/login.html", e);
        }
    }
}
