package com.js.common.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.js.shop.domain.Product;
import com.js.shop.service.ProductService;

@WebServlet("/productInfo")
public class ProductInfoServlet extends HttpServlet
{
    ProductService service = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String pid = req.getParameter("pid");
        String currentPage = req.getParameter("currentPage");
        Product product = service.findProductsByPid(pid);
        
        //将商品信息放到cookie
        String pids = pid;
        Cookie[] cookies = req.getCookies();
        if(cookies != null)
        {
        	for(Cookie cookie : cookies)
        	{
        		if("pids".contentEquals(cookie.getName()))
        		{
        			String value = cookie.getValue();
        			pids = value;
        			break;
        		}
        	}
        	//1)将最新访问的放在最前面，
        	//2)如果有重复商品，则先将字符串中重复的ID删除，再将该重复的放在最前面
        	String[] pidArr = pids.split("#");
        	List<String> asList = Arrays.asList(pidArr);
        	LinkedList<String> linkedList = new LinkedList<String>(asList);
        	linkedList.remove(pid);
        	linkedList.addFirst(pid);
        	StringBuffer sb = new StringBuffer();
        	for(int i=0;i<linkedList.size() && i<6 ;i++) {
        		if(i>0) {
        			sb.append("#");
        		}
        		sb.append(linkedList.get(i));
        	}   	
        	pids = sb.toString();
        }
        //创建Cookie，将新拼好的pids携带回客户端
        Cookie cookie = new Cookie("pids",pids);
        resp.addCookie(cookie);
        req.setAttribute("product", product);
        req.setAttribute("currentPage", currentPage);
        req.getRequestDispatcher("/product_info.jsp").forward(req, resp);
    }
}
