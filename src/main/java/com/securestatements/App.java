package com.securestatements;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;

import com.securestatements.servlet.UploadServlet;
import com.securestatements.servlet.DownloadServlet;
import com.securestatements.servlet.GenerateLinkServlet;

import javax.servlet.MultipartConfigElement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App
{
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {

        Server server = null;

        try {
            server = new Server(8080);

            ServletContextHandler context =
                    new ServletContextHandler(ServletContextHandler.SESSIONS);

            context.setContextPath("/");
            context.setResourceBase(
                    App.class.getClassLoader()
                            .getResource("webapp")
                            .toExternalForm()
            );

            // ✅ THIS LINE FIXES YOUR ISSUE
            context.setWelcomeFiles(new String[]{"admin.html"});

            // Serve static content
            context.addServlet(DefaultServlet.class, "/");

            // Upload servlet
            ServletHolder uploadHolder = new ServletHolder(UploadServlet.class);
            uploadHolder.getRegistration()
                    .setMultipartConfig(new MultipartConfigElement("secure-storage"));

            context.addServlet(uploadHolder, "/admin/upload");

            // Other servlets
            context.addServlet(GenerateLinkServlet.class, "/admin/generate-link");
            context.addServlet(DownloadServlet.class, "/download");

            server.setHandler(context);
            server.start();

            System.out.println("App running at:");
            System.out.println("http://localhost:8080/");

            server.join();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start application", e);
            System.exit(1);
        }
    }
}
