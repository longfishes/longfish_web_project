package com.longfish.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/demo001")
public class Demo01Servlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().setAttribute("KEY1",14);
        System.out.println(14);

        resp.sendRedirect("demo02.html");
    }
}
