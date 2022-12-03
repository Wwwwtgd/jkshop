package com.js.shop.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.js.shop.domain.Category;
import com.js.shop.service.ProductService;

@WebServlet("/categoryList")
public class CategoryServlet extends HttpServlet
{
    ProductService service = new ProductService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/heml;charset=utf8");
        List<Category> categorys = service.findCategorys();
        String json = JSON.toJSONString(categorys);
        resp.getWriter().write(json);
    }
}
