
package com.securestatements.servlet;

import com.securestatements.security.JwtUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/generate-link")
public class GenerateLinkServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String id = req.getParameter("statementId");
        String token = JwtUtil.generateToken(id);
        String url = "http://localhost:8080/download.html?token=" + token;


        res.getWriter().write("{\"url\":\"" + url + "\"}");
    }
}
