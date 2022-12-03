package com.js.shop.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.js.shop.service.UserService;

/**
 * 用户名是否存在校验
 * @ClassName CheckUsernameServlet
 * @author 86159
 * @date 2022年9月15日 上午9:01:19
 */
@WebServlet("/checkUsername")
public class CheckUsernameServlet extends HttpServlet
{
    private UserService userService = new UserService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        System.out.println("CheckUsernameServlet.doPost()");
        String username = req.getParameter("username");
        boolean isExist = false;
        isExist = userService.checkUsername(username);
        String json = "{\"isExist\":"+isExist+"}";
        resp.getWriter().write(json);
    }

}
