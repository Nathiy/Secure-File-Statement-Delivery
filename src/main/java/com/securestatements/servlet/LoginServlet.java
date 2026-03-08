package com.securestatements.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginServlet implements Servlet {

    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) {
        this.config = config;
        LOGGER.info("LoginServlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            handleLogin(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String role = request.getParameter("role");

            if (username == null || password == null || role == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Missing required parameters");
                return;
            }

            // For demo purposes, validate credentials
            if (authenticateUser(username, password, role)) {
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("username", username);
                session.setAttribute("role", role);
                session.setMaxInactiveInterval(1800); // 30 minutes

                response.setStatus(HttpServletResponse.SC_OK);
                LOGGER.info("User " + username + " logged in as " + role);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("Invalid credentials");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Login error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Login failed: " + e.getMessage());
        }
    }

    private boolean authenticateUser(String username, String password, String role) {
        // Demo authentication - in production, validate against database
        if ("admin".equals(role)) {
            return "admin".equals(username) && "admin123".equals(password);
        } else if ("customer".equals(role)) {
            // Accept customer1, customer2, customer3 (and any customer* with correct password)
            return (username.startsWith("customer")) && "customer123".equals(password);
        }
        return false;
    }

    @Override
    public String getServletInfo() {
        return "LoginServlet - Handles user authentication";
    }

    @Override
    public void destroy() {
        LOGGER.info("LoginServlet destroyed");
    }
}

