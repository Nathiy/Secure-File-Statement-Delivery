package com.securestatements.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.UUID;

import com.securestatements.util.DBConnection;

/**
 * AdminStatementServlet - Handles admin statement operations
 * Provides endpoints for:
 * - GET /admin/statements - Get all statements
 * - POST /admin/generate-link/{id} - Generate download link
 */
public class AdminStatementServlet implements Servlet {

    private static final Logger LOGGER = Logger.getLogger(AdminStatementServlet.class.getName());
    private ServletConfig config;
    private static final long TOKEN_EXPIRY_TIME = 10 * 60 * 1000; // 10 minutes

    @Override
    public void init(ServletConfig config) {
        this.config = config;
        LOGGER.info("AdminStatementServlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String pathInfo = request.getPathInfo();
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method) && "/statements".equals(pathInfo)) {
            handleGetStatements(request, response);
        } else if ("POST".equalsIgnoreCase(method) && pathInfo != null && pathInfo.startsWith("/generate-link/")) {
            handleGenerateLink(request, response, pathInfo);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleGetStatements(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("{\"error\": \"Database connection failed\"}");
                return;
            }

            String sql = "SELECT s.id, s.customer_id, c.full_name as customer_name, s.file_name, s.upload_date " +
                        "FROM statements s " +
                        "LEFT JOIN customers c ON s.customer_id = c.id " +
                        "WHERE s.is_active = TRUE " +
                        "ORDER BY s.upload_date DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"id\": ").append(rs.getInt("id")).append(",");
                json.append("\"customerId\": ").append(rs.getInt("customer_id")).append(",");
                json.append("\"customerName\": \"").append(rs.getString("customer_name")).append("\",");
                json.append("\"fileName\": \"").append(rs.getString("file_name")).append("\",");
                json.append("\"uploadDate\": \"").append(rs.getTimestamp("upload_date")).append("\"");
                json.append("}");
            }

            json.append("]");
            response.getWriter().println(json.toString());
            LOGGER.info("Retrieved statements for admin dashboard");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error retrieving statements", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"Database error\"}");
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    private void handleGenerateLink(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        response.setContentType("application/json");

        try {
            // Extract statement ID from path: /admin/generate-link/{id}
            String statementIdStr = pathInfo.substring("/generate-link/".length());
            int statementId = Integer.parseInt(statementIdStr);

            String token = generateToken();
            long expiryTime = System.currentTimeMillis() + TOKEN_EXPIRY_TIME;

            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("{\"error\": \"Database connection failed\"}");
                return;
            }

            // Insert token into database
            String sql = "INSERT INTO tokens (statement_id, token, expiry_time, used) VALUES (?, ?, ?, FALSE)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, statementId);
            ps.setString(2, token);
            ps.setLong(3, expiryTime);
            ps.executeUpdate();

            ps.close();
            conn.close();

            // Return token and download URL
            String downloadUrl = request.getScheme() + "://" + request.getServerName() +
                                ":" + request.getServerPort() + "/download.html?token=" + token;

            response.getWriter().println("{\"token\": \"" + token + "\", \"downloadUrl\": \"" + downloadUrl + "\", \"expiryTime\": " + expiryTime + "}");
            LOGGER.info("Generated download token for statement " + statementId);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid statement ID format", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Invalid statement ID\"}");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error generating token", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"Database error\"}");
        }
    }

    private String generateToken() {
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }

    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { LOGGER.warning("Error closing ResultSet: " + e.getMessage()); }
        }
        if (ps != null) {
            try { ps.close(); } catch (SQLException e) { LOGGER.warning("Error closing PreparedStatement: " + e.getMessage()); }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { LOGGER.warning("Error closing Connection: " + e.getMessage()); }
        }
    }

    @Override
    public String getServletInfo() {
        return "AdminStatementServlet - Handles admin statement operations";
    }

    @Override
    public void destroy() {
        LOGGER.info("AdminStatementServlet destroyed");
    }
}

