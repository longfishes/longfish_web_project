package com.longfish.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;

public class SessionTestServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        System.out.println("sessionId = " + session.getId());//15C40F899014195D95D60177FF2FA673 ...
        System.out.println(session.getMaxInactiveInterval());
        session.setMaxInactiveInterval(3600 * 24);

        session.setAttribute("key1", "1145");
        session.setAttribute("key2", new int[]{1,2,3});
        session.setAttribute("key3", req);

        System.out.println(session.getAttribute("key1"));
        System.out.println(Arrays.toString((int[]) session.getAttribute("key2")));
        System.out.println(session.getAttribute("key3"));

        resp.sendRedirect("testRedirect.html");
    }
}
