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
    public void init(ServletConfig config) {
        this.config = config;
        LOGGER.info("UploadStatementServlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.getWriter().println("Only POST method is supported");
            return;
        }

        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            // Get form parameters
            String customerIdStr = request.getParameter("customerId");
            String customerName = request.getParameter("customerName");

            LOGGER.info("Received upload request - Customer ID: " + customerIdStr + ", Name: " + customerName);

            if (customerIdStr == null || customerIdStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Customer ID is required");
                return;
            }

            if (customerName == null || customerName.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Customer Name is required");
                return;
            }

            int customerId = Integer.parseInt(customerIdStr);

            // Handle file upload
            String fileName = null;
            byte[] fileData = null;

            try {
                // Try to get file part (if using FormData)
                Part filePart = request.getPart("file");
                if (filePart != null && filePart.getSize() > 0) {
                    fileName = filePart.getSubmittedFileName();
                    fileData = readInputStreamToBytes(filePart.getInputStream());
                    LOGGER.info("Received file: " + fileName + ", Size: " + fileData.length + " bytes");
                }
            } catch (Exception e) {
                // Fall back to alternative file handling
                LOGGER.info("Could not read file part, trying alternative method: " + e.getMessage());
                fileName = request.getParameter("fileName");
                String fileDataStr = request.getParameter("fileData");
                fileData = (fileDataStr != null) ? fileDataStr.getBytes() : new byte[0];
            }

            if (fileName == null || fileName.isEmpty() || fileData == null || fileData.length == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("File is required");
                return;
            }

            // Save file
            String path = FileService.saveFile(fileName, fileData);
            LOGGER.info("File saved to: " + path);

            // Save to database
            conn = DBConnection.getConnection();
            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Database connection failed");
                return;
            }

            String sql = "INSERT INTO statements(customer_id, file_name, file_path) VALUES(?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setString(2, fileName);
            ps.setString(3, path);
            ps.executeUpdate();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().println("{\"success\": true, \"message\": \"Upload successful\"}");
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

    private byte[] readInputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    @Override
    public String getServletInfo() {
        return "UploadStatementServlet - Handles secure file uploads";
    }

    @Override
    public void destroy() {
        LOGGER.info("UploadStatementServlet destroyed");
    }
}
