package com.securestatements;

import com.securestatements.util.DatabaseDiagnostics;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * ConnectionTest - Standalone utility to test database connection
 * Run this class directly to diagnose database connection issues
 * Usage: java -cp target/classes:target/dependency/* com.securestatements.ConnectionTest
 */
public class ConnectionTest {

    public static void main(String[] args) {
        // ...existing code...
        try {
            // Configure logging to console with detailed output
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.ALL);
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);

            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║   DATABASE CONNECTION TEST - DIAGNOSTICS                   ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");

            DatabaseDiagnostics.runDiagnostics();

            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║   TEST COMPLETED - CHECK LOGS FOR DETAILS                 ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        } catch (Exception e) {
            System.out.println("\n");
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║   TEST FAILED - SEE DIAGNOSTICS ABOVE                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("\n");
            Logger.getLogger(ConnectionTest.class.getName()).log(Level.SEVERE, "Connection test failed", e);
        }
    }
}

