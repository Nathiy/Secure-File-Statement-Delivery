package com.securestatements.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.securestatements.service.FileService;
import com.securestatements.util.DBConnection;

public class UploadStatementServlet implements Servlet {

    private static final Logger LOGGER = Logger.getLogger(UploadStatementServlet.class.getName());
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        LOGGER.info("UploadStatementServlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.getWriter().println("Only POST method is supported");
            return;
        }

        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            int customerId = Integer.parseInt(request.getParameter("customerId"));

            // Note: Since we no longer extend HttpServlet, multipart handling needs manual implementation
            // For demonstration, using regular parameters (not suitable for real file uploads)
            String fileName = request.getParameter("fileName");
            String fileData = request.getParameter("fileData"); // Would need base64 decoding

            if (fileName == null || fileName.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("File name is empty");
                return;
            }

            // Simplified file handling - in production, implement proper multipart parsing
            byte[] data = fileData != null ? fileData.getBytes() : new byte[0];

            String path = FileService.saveFile(fileName, data);

            conn = DBConnection.getConnection();
            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Database connection failed");
                return;
            }

            String sql = "INSERT INTO statements(customer_id,file_path) VALUES(?,?)";
            ps = conn.prepareStatement(sql);

            ps.setInt(1, customerId);
            ps.setString(2, path);

            ps.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Upload successful");
            LOGGER.info("File uploaded successfully for customer: " + customerId);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid customer ID format", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Invalid customer ID");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during file upload", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Upload failed: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database resources", e);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "UploadStatementServlet - Handles secure file uploads";
    }

    @Override
    public void destroy() {
        LOGGER.info("UploadStatementServlet destroyed");
    }

    private ServletContext getServletContext() {
        return config.getServletContext();
    }
}
