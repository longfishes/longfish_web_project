package com.longfish.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String fname = req.getParameter("fname");
        Integer price = null;
        Integer fcount = null;
        try {
            price = Integer.parseInt(req.getParameter("price"));
            fcount = Integer.parseInt(req.getParameter("fcount"));
        } catch (NumberFormatException e) {
            System.out.println("数字错误");
        }


        String remark = req.getParameter("remark");

        System.out.println("fname = " + fname);
        System.out.println("price = " + price);
        System.out.println("fcount = " + fcount);
        System.out.println("remark = " + remark);

        if (price != null && fcount != null)
            System.out.println("test = " + (price + fcount));
        else System.out.println("Wrong!");
    }

}
