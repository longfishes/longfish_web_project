package com.longfish.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/scope")
public class ScopeServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        req.setAttribute("KEY1",14);

        System.out.println(req.getAttribute("KEY1"));
    }
}
