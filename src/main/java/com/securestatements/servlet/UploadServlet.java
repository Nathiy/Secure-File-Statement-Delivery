
package com.securestatements.servlet;

import com.securestatements.dao.StatementDAO;
import com.securestatements.service.FileStorageService;

import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        try {

            Part file = req.getPart("file");

            int customerId = Integer.parseInt(req.getParameter("customerId"));
            String name = req.getParameter("customerName");

            String path = FileStorageService.saveFile(file);

            int id = StatementDAO.save(customerId, name, path);

            res.getWriter().write("Uploaded statement id=" + id);

        } catch(Exception e){
            e.printStackTrace();
            res.sendError(500);
        }
    }
}
