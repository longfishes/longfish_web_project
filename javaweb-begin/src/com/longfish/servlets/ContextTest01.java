package com.longfish.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/context2")
public class ContextTest01 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println(getServletContext().getAttribute("KEY1"));
    }
}
