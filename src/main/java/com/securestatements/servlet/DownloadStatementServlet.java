package com.securestatements.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.logging.Logger;

import com.securestatements.util.DBConnection;

public class DownloadStatementServlet implements Servlet {

    private static final Logger LOGGER = Logger.getLogger(DownloadStatementServlet.class.getName());
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        LOGGER.info("DownloadStatementServlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.getWriter().println("Only GET method is supported");
            return;
        }

        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String token = request.getParameter("token");

            if (token == null || token.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Token parameter is required");
                return;
            }

            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Database connection failed");
                return;
            }

            String sql = "SELECT s.file_path,t.expiry_time,t.used " +
                        "FROM tokens t JOIN statements s " +
                        "ON t.statement_id=s.id WHERE token=?";

            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, token);
                rs = ps.executeQuery();

                if (rs.next()) {
                    long expiry = rs.getLong("expiry_time");
                    boolean used = rs.getBoolean("used");

                    if (System.currentTimeMillis() > expiry || used) {
                        response.setStatus(HttpServletResponse.SC_GONE);
                        response.getWriter().println("Link expired");
                        return;
                    }

                    String path = rs.getString("file_path");
                    File file = new File(path);

                    if (!file.exists()) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().println("File not found");
                        return;
                    }

                    // Set content type based on file extension
                    String contentType = getServletContext().getMimeType(file.getName());
                    if (contentType == null) {
                        contentType = "application/octet-stream";
                    }
                    response.setContentType(contentType);

                    // Set content disposition for download
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                    response.setContentLengthLong(file.length());

                    FileInputStream in = new FileInputStream(file);
                    OutputStream out = response.getOutputStream();

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    in.close();
                    out.flush();

                    LOGGER.info("File downloaded successfully: " + file.getName());

                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().println("Invalid token or file not found");
                }

            } finally {
                if (rs != null) try { rs.close(); } catch (SQLException e) { LOGGER.warning("Error closing ResultSet: " + e.getMessage()); }
                if (ps != null) try { ps.close(); } catch (SQLException e) { LOGGER.warning("Error closing PreparedStatement: " + e.getMessage()); }
                if (conn != null) try { conn.close(); } catch (SQLException e) { LOGGER.warning("Error closing Connection: " + e.getMessage()); }
            }

        } catch (SQLException e) {
            LOGGER.severe("Database error during file download: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Database error occurred");
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during file download: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("An unexpected error occurred");
        }
    }

    @Override
    public String getServletInfo() {
        return "DownloadStatementServlet - Handles secure file downloads using token authentication";
    }

    @Override
    public void destroy() {
        LOGGER.info("DownloadStatementServlet destroyed");
    }

    private ServletContext getServletContext() {
        return config.getServletContext();
    }
}
