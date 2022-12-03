package com.js.shop.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.js.common.util.CommonUtil;
import com.js.shop.domain.Cart;
import com.js.shop.domain.CartItem;
import com.js.shop.domain.Category;
import com.js.shop.domain.Order;
import com.js.shop.domain.OrderItem;
import com.js.shop.domain.PageBean;
import com.js.shop.domain.Product;
import com.js.shop.domain.User;
import com.js.shop.service.ProductService;

@WebServlet("/product")
public class ProductServlet extends BaseServlet
{
    ProductService service = new ProductService();
    private int pagesize = 12;
    private int orderPageSize = 3;

    /**
     * 查询商品分类列表信息
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void categoryList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/heml;charset=utf8");
        List<Category> categorys = service.findCategorys();
        String json = JSON.toJSONString(categorys);
        resp.getWriter().write(json);
    }

    /**
     * 查询首页热门商品和最新商品
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void index(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        //查询热门商品
        List<Product> hotProducts = service.findHotProducts();
        req.setAttribute("hotProducts", hotProducts);
        //查询最新商品
        List<Product> newProducts = service.findNewProducts();
        req.setAttribute("newProducts", newProducts);
        //查询商品分类信息
        /*List<Category> categorys = service.findCategorys();
        req.getSession().setAttribute("categorys", categorys);*/
        // System.out.println(newProducts);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);

    }

    /**
     * 查看商品详情
     * @param req
     * @param resp
     * @throws IOException 
     * @throws ServletException 
     */
    public void productInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

        String pid = req.getParameter("pid");
        String currentPage = req.getParameter("currentPage");
        Product product = service.findProductsByPid(pid);

        //将商品放到cookie
        String pids = pid;
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if ("pids".equals(cookie.getName()))
                {
                    String value = cookie.getValue();
                    pids = value;
                    break;
                }
            }
            //1)将最新访问的放在最前面.
            //2)如果有重复商品,则先将字符串中重复的ID删除,再将该重复的放在最前面
            String[] pidArr = pids.split("#");
            List<String> asList = Arrays.asList(pidArr);
            LinkedList<String> linkedList = new LinkedList<String>(asList);
            linkedList.remove(pid);//删除重复访问的商品

            linkedList.addFirst(pid);//最近访问的放在最前面

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < linkedList.size() && i < 6; i++)
            {
                if (i > 0)
                {
                    sb.append("#");
                }
                sb.append(linkedList.get(i));

            }

            pids = sb.toString();
        }
        //创建Cookie,将新拼接好的pids携带回客户端
        //        System.err.println(pids);
        Cookie cookie = new Cookie("pids", pids);
        resp.addCookie(cookie);
        req.setAttribute("product", product);
        req.setAttribute("currentPage", currentPage);
        req.getRequestDispatcher("/product_info.jsp").forward(req, resp);
    }

    /**
     * 根据商品分类信息查询商品列表
     * @param req
     * @param resp
     * @throws IOException 
     * @throws ServletException 
     */
    public void productListByCid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
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

        //根据cookie里存放的pids查询浏览过的商品信息
        List<Product> hisProducts = new ArrayList<Product>();
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                if ("pids".equals(cookie.getName()))
                {
                    String value = cookie.getValue();
                    //                    System.err.println("value-> " + value);
                    String[] pidarr = value.split("#");
                    for (int i = 0; i < pidarr.length; i++)
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

    /**
     * 添加购物车
     * @param req
     * @param resp
     * @throws Exception
     */
    public void addCart(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        //1、获取商品信息
        String pid = req.getParameter("pid");
        Product product = service.findProductsByPid(pid);
        String buyNumStr = req.getParameter("buyNum");
        int buyNum = 0;
        if (buyNumStr != null && !"".equals(buyNumStr))
        {
            buyNum = Integer.parseInt(buyNumStr);
        }
        CartItem cartItem = new CartItem(product, buyNum);
        //2、获取购物车，判断该商品是否已在购物车内
        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null)
        {
            Map<String, CartItem> cartItems = cart.getCartItems();
            if (cartItems.containsKey(pid))
            {
                cartItem = cartItems.get(pid);
                buyNum += cartItem.getBuyNum();
                cartItem = new CartItem(product, buyNum);
                cartItems.put(pid, cartItem);
            }
            else
            {
                cartItems.put(pid, cartItem);
            }

            cart = new Cart(cartItems);
        }
        else
        {
            Map<String, CartItem> cartItems = new HashMap<>();
            cartItems.put(pid, cartItem);
            cart = new Cart(cartItems);
        }
        session.setAttribute("cart", cart);

        //req.getRequestDispatcher("/cart.jsp").forward(req, resp);
        resp.sendRedirect(req.getContextPath() + "/cart.jsp");
    }

    /**
     * 根据商品pid删除购物车中某个购物项
     * @param req
     * @param resp
     * @throws IOException 
     */
    public void delFromCart(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String pid = req.getParameter("pid");
        //        System.out.println("pid-> " + pid);

        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null)
        {
            Map<String, CartItem> cartItems = cart.getCartItems();
            cartItems.remove(pid);
            cart = new Cart(cartItems);
            session.setAttribute("cart", cart);
        }
        resp.sendRedirect(req.getContextPath() + "/cart.jsp");
    }

    /**
     * 清空购物车
     * @param req
     * @param resp
     * @throws IOException
     */
    public void clearCart(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        HttpSession session = req.getSession();
        session.removeAttribute("cart");
        resp.sendRedirect(req.getContextPath() + "/cart.jsp");
    }

    /**
     * 提交订单
     * @param req
     * @param resp
     * @throws IOException 
     */
    public void submitOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        //1、判断用户是否登录
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null)//未登录用户跳转到登录页面
        {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }

        //2、将购物车数据结构转换成订单的数据结构，并将订单信息存入数据库
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) //程序逻辑上的容错
        {
            //转订单
            Order order = new Order();
            order.setTotal(cart.getTotal());//订单总计
            order.setUser(user);//订单所属用户
            order.setOid(CommonUtil.generateUUID());//生成订单标识
            order.setState(0);//设置订单状态为我付款
            order.setOrdertime(new Date());//设置订单创建时间

            Map<String, CartItem> cartItems = cart.getCartItems();
            Set<String> keySet = cartItems.keySet();
            List<OrderItem> orderItems = new ArrayList<OrderItem>();
            order.setOrderItems(orderItems);//将订单项列表添加到订单对象
            for (String key : keySet)
            {
                CartItem cartItem = cartItems.get(key);
                OrderItem orderItem = new OrderItem();
                orderItem.setCount(cartItem.getBuyNum()); //购买个数
                orderItem.setSubtotal(cartItem.getSubTotal());//小计
                orderItem.setProduct(cartItem.getProduct());//订单项对应商品信息
                orderItem.setOrder(order);//所属订单
                orderItem.setItemid(CommonUtil.generateUUID());//生成订单项标识
                orderItems.add(orderItem);
            }

            //存订单
            service.submitOrder(order);
            session.setAttribute("order", order);
        }
        resp.sendRedirect(req.getContextPath() + "/order_info.jsp");

    }

    /**
     * 确认订单
     * @param req
     * @param resp
     * @throws Exception
     */
    public void confirmOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        Order order = (Order) req.getSession().getAttribute("order");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        order.setName(new String(name.getBytes("ISO-8859-1"), "utf-8"));
        order.setAddress(new String(address.getBytes("ISO-8859-1"), "utf-8"));
        order.setTelephone(req.getParameter("telephone"));
        service.confirmOrder(order);
        req.setAttribute("success", true);
        req.getRequestDispatcher("/order_info.jsp").forward(req, resp);
    }

    /**
     * 我的订单
     * @param req
     * @param resp
     * @throws IOException 
     * @throws ServletException 
     */
    public void myOrders(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        String currentPageStr = req.getParameter("currentPage");
        int currentPage = 1;
        if (currentPageStr != null && !"".equals(currentPageStr))
        {
            currentPage = Integer.parseInt(currentPageStr);
        }
        //1、判断用户是否登录
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null)
        {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
        else
        {
            String userId = user.getUid();

            PageBean pageBean = service.findOrderList(userId, orderPageSize, currentPage);
            req.setAttribute("pageBean", pageBean);
            req.getRequestDispatcher("/order_list.jsp").forward(req, resp);
        }
    }
}
