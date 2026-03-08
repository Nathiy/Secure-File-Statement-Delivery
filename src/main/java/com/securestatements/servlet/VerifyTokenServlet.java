package com.securestatements.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.securestatements.util.DBConnection;

/**
 * VerifyTokenServlet - Handles token verification for downloads
 * Provides endpoints for:
 * - GET /verify-token/{token} - Verify download token
 */
public class VerifyTokenServlet implements Servlet {

    private static final Logger LOGGER = Logger.getLogger(VerifyTokenServlet.class.getName());
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) {
        this.config = config;
        LOGGER.info("VerifyTokenServlet initialized");
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
            handleVerifyToken(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void handleVerifyToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String token = pathInfo != null ? pathInfo.substring(1) : null; // Remove leading /

        response.setContentType("application/json");

        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\": \"Token is required\"}");
            return;
        }

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

            String sql = "SELECT t.id, t.statement_id, t.expiry_time, t.used, s.file_name " +
                        "FROM tokens t " +
                        "JOIN statements s ON t.statement_id = s.id " +
                        "WHERE t.token = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            rs = ps.executeQuery();

            if (rs.next()) {
                long expiryTime = rs.getLong("expiry_time");
                boolean used = rs.getBoolean("used");
                int statementId = rs.getInt("statement_id");
                String fileName = rs.getString("file_name");

                // Check if token is expired
                if (System.currentTimeMillis() > expiryTime) {
                    response.setStatus(HttpServletResponse.SC_GONE); // 410 Gone
                    response.getWriter().println("{\"error\": \"Token has expired\"}");
                    LOGGER.info("Token verification failed: Token expired - " + token);
                    return;
                }

                // Check if token was already used
                if (used) {
                    response.setStatus(HttpServletResponse.SC_GONE); // 410 Gone
                    response.getWriter().println("{\"error\": \"Token has already been used\"}");
                    LOGGER.info("Token verification failed: Token already used - " + token);
                    return;
                }

                // Token is valid
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("{\"valid\": true, \"statementId\": " + statementId + ", \"fileName\": \"" + fileName + "\", \"expiryTime\": " + expiryTime + "}");
                LOGGER.info("Token verified successfully - " + token);

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("{\"error\": \"Invalid token\"}");
                LOGGER.info("Token verification failed: Token not found - " + token);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during token verification", e);
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
        return "VerifyTokenServlet - Handles token verification";
    }

    @Override
    public void destroy() {
        LOGGER.info("VerifyTokenServlet destroyed");
    }
}

