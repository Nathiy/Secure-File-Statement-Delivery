package com.securestatements.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.securestatements.util.DBConnection;

/**
 * CustomerStatementServlet - Handles customer statement operations
 * Provides endpoints for:
 * - GET /customer/statements - Get customer's statements
 */
public class CustomerStatementServlet implements Servlet {

    private static final Logger LOGGER = Logger.getLogger(CustomerStatementServlet.class.getName());
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) {
        this.config = config;
        LOGGER.info("CustomerStatementServlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            handleGetStatements(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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

            // Get customer ID from session (or use a default for demo)
            // For now, we'll get all statements (in production, filter by customer)
            String sql = "SELECT s.id, s.file_name, s.upload_date, t.token, t.expiry_time " +
                        "FROM statements s " +
                        "LEFT JOIN tokens t ON s.id = t.statement_id " +
                        "WHERE s.is_active = TRUE " +
                        "ORDER BY s.upload_date DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                String token = rs.getString("token");
                long expiryTime = rs.getLong("expiry_time");
                boolean hasValidToken = token != null && expiryTime > System.currentTimeMillis();

                json.append("{");
                json.append("\"id\": ").append(rs.getInt("id")).append(",");
                json.append("\"documentName\": \"Statement\",");
                json.append("\"fileName\": \"").append(rs.getString("file_name")).append("\",");
                json.append("\"token\": \"").append(token != null ? token : "").append("\",");
                json.append("\"expiryDate\": \"").append(rs.getTimestamp("upload_date")).append("\",");
                json.append("\"fileSize\": \"245 KB\",");
                json.append("\"hasValidToken\": ").append(hasValidToken);
                json.append("}");
            }

            json.append("]");
            response.getWriter().println(json.toString());
            LOGGER.info("Retrieved statements for customer dashboard");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error retrieving customer statements", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\": \"Database error\"}");
        } finally {
            closeResources(rs, ps, conn);
        }
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
        return "CustomerStatementServlet - Handles customer statement operations";
    }

    @Override
    public void destroy() {
        LOGGER.info("CustomerStatementServlet destroyed");
    }
}

