
package com.securestatements.servlet;

import com.securestatements.dao.StatementDAO;
import com.securestatements.security.JwtUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.nio.file.*;
import java.io.IOException;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String token = req.getParameter("token");

        if (token == null || token.isEmpty()) {
            res.sendError(400, "Missing token");
            return;
        }

        String statementId = JwtUtil.validate(token);

        if (statementId == null) {
            res.sendError(403, "Invalid or expired token");
            return;
        }

        try {

            String filePath = StatementDAO.getFile(Integer.parseInt(statementId));

            if (filePath == null) {
                res.sendError(404, "File not found");
                return;
            }

            Path path = Paths.get(filePath);

            res.setContentType("application/pdf");
            res.setHeader("Content-Disposition",
                    "attachment; filename=\"" + path.getFileName() + "\"");

            Files.copy(path, res.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(500, "Download failed");
        }
    }
}
