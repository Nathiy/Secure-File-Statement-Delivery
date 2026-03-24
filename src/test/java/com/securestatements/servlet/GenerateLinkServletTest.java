package com.securestatements.servlet;

import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class GenerateLinkServletTest {

    @Test
    void shouldGenerateLink() throws Exception {

        GenerateLinkServlet servlet = new GenerateLinkServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("statementId")).thenReturn("1");

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(request, response);

        String output = writer.toString();

        assert output.contains("http://localhost:8080/download.html?token=");
    }
}