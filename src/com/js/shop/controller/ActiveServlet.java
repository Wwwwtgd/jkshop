package com.js.shop.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.js.shop.service.UserService;

@WebServlet("/active")
public class ActiveServlet extends HttpServlet
{
    private UserService service = new UserService();

    /**
     * 用户激活
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String code = req.getParameter("activeCode");
        boolean result = service.active(code);
        if (result)
        {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }

    }
}
