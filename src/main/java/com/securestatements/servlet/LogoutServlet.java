package com.securestatements.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogoutServlet implements Servlet {

    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) {
        this.config = config;
        LOGGER.info("LogoutServlet initialized");
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
            handleLogout(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String username = (String) session.getAttribute("username");
                session.invalidate();
                LOGGER.info("User " + username + " logged out");
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Logged out successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Logout error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Logout failed");
        }
    }

    @Override
    public String getServletInfo() {
        return "LogoutServlet - Handles user logout";
    }

    @Override
    public void destroy() {
        LOGGER.info("LogoutServlet destroyed");
    }
}

