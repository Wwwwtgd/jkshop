package com.js.shop.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.js.shop.domain.PageBean;
import com.js.shop.domain.Product;
import com.js.shop.service.ProductService;

@WebServlet("/productListByCid")
public class ProductListByCidServlet extends HttpServlet
{
    ProductService service = new ProductService();
    private int pagesize = 12;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String cid = req.getParameter("cid");
        String currentPage = req.getParameter("currentPage");
        int page = 1;
        if (currentPage != null && !"".equals(currentPage))
        {
            page = Integer.valueOf(currentPage);
        }

        PageBean<Product> pageBean = service.findProductListByCid(cid, page, pagesize);
        req.setAttribute("pageBean", pageBean);
        req.setAttribute("cid", cid);
        
        //根据cookie里存放的pids查询览过的商品信息
        List<Product> hisProducts = new ArrayList<Product>();
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
        {
        	for (Cookie cookie : cookies)
        	{
        		if ("pids".equals(cookie.getName()))
        		{
        			String value = cookie.getValue();
        			String[] pidarr = value.split("#");
        			for(int i = 0;i < pidarr.length;i++)
        			{
        				Product product = service.findProductsByPid(pidarr[i]);
        				hisProducts.add(product);
        			}
        			break;
        		}
        	}
        }
        
        req.setAttribute("hisProducts", hisProducts);
        
        req.getRequestDispatcher("/product_list.jsp").forward(req, resp);
    }
}
