package com.js.shop.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BaseServlet")
public class BaseServlet extends HttpServlet
{
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp)
    {
        String methodStr = req.getParameter("method");
        try
        {
            Method method = this.getClass().getMethod(methodStr, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
