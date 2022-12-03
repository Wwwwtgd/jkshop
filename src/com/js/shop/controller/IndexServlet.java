package com.js.shop.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.js.shop.domain.Category;
import com.js.shop.domain.Product;
import com.js.shop.service.ProductService;

@WebServlet("/index")
public class IndexServlet extends HttpServlet
{
    ProductService service = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        //查询热门商品
        List<Product> hotProducts = service.findHotProducts();
        req.setAttribute("hotProducts", hotProducts);
        //查询最新商品
        List<Product> newProducts = service.findNewProducts();
        req.setAttribute("newProducts", newProducts);
        //查询商品分类信息
        List<Category> categorys = service.findCategorys();
        req.setAttribute("categorys", categorys);
        System.out.println(newProducts);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
