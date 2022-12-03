package com.js.shop.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

import com.js.common.util.CommonUtil;
import com.js.common.util.MailUtil;
import com.js.shop.domain.User;
import com.js.shop.service.UserService;

@WebServlet("/user")
public class UserServlet extends BaseServlet
{
    private UserService service = new UserService();

    /**
     * 用户注册
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException 
     */
    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
    {

        //解决post请求中文乱码问题
        req.setCharacterEncoding("utf-8");
        HttpSession session = req.getSession();
        String checkcode_session = (String) session.getAttribute("checkcode_session");
        String checkCode = req.getParameter("checkCode");
        checkCode = new String(checkCode.getBytes("ISO8859-1"),"utf-8");
        if (!checkcode_session.equals(checkCode))
        {
            req.setAttribute("loginError", "验证错误！");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        //获取参数信息
        Map<String, String[]> parameterMap = req.getParameterMap();
        User user = new User();
        try
        {
            //将String转成Date类型
            ConvertUtils.register((Converter) new DateLocaleConverter(), Date.class);
            BeanUtils.populate(user, parameterMap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String uid = CommonUtil.generateUUID();
        user.setUid(uid);
        String code = CommonUtil.generateUUID();
        user.setCode(code);
        user.setState(0);//默认是0 未激活状态  1激活状态
        boolean result = service.register(user);
        if (result)
        {
            //注册成功发送激活邮件
            String email = user.getEmail();
            String emailMsg = user.getUsername() + "用户恭喜您注册成功，请点击下面的连接进行激活账户" + "<a href='http://localhost:8080/jkshop/user?method=active&activeCode=" + code + "'>"
                    + "http://localhost:8080/jkshop/user?method=active&activeCode=" + code + "</a>";
            String topic = "用户激活";
            try
            {
                MailUtil.sendMail(email, emailMsg);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            resp.sendRedirect("/jkshop/login.jsp");
        }
        else
        {
            req.setAttribute("msg", "注册失败，请重新注册");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }

        System.out.println("user-> " + user);

    }

    /**
     * 用户登录
     * @param req
     * @param resp
     * @throws IOException 
     */
    public void login(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        String userName = req.getParameter("username");
        String pwd = req.getParameter("pwd");
        String autoLogin = req.getParameter("autoLogin");
        String checknumber = req.getParameter("checknumber");
        checknumber = new String(checknumber.getBytes("ISO-8859-1"), "utf-8");
        System.err.println("checknumber-> " + checknumber);
        HttpSession session = req.getSession();
        String checkcode_session = (String) session.getAttribute("checkcode_session");
        if (checknumber == null || "".equals(checknumber) || !checknumber.equals(checkcode_session))
        {
            req.setAttribute("loginError", "验证码错误！");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
        else
        {
            User user = service.findUserByUserNamePwd(userName, pwd);

            if (user != null)
            {
                session.setAttribute("user", user);
                if (autoLogin != null && "1".equals(autoLogin))
                {
                    Cookie cookie_username = new Cookie("cookie_username", userName);
                    Cookie cookie_pwd = new Cookie("cookie_pwd", pwd);
                    cookie_username.setMaxAge(60 * 60 * 24 * 7);//用户名保留在浏览器cookie中7天
                    cookie_pwd.setMaxAge(60 * 60 * 24 * 7);
                    resp.addCookie(cookie_username);
                    resp.addCookie(cookie_pwd);
                }
                resp.sendRedirect(req.getContextPath() + "/product?method=index");
            }
            else
            {
                req.setAttribute("loginError", "用户名或密码错误");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                //resp.sendRedirect(req.getContextPath() + "/login.jsp");
            }
        }
    }

    /**
     * 退出
     * @param req
     * @param resp
     * @throws Exception
     */
    public void logout(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        HttpSession session = req.getSession();
        session.removeAttribute("user");
        resp.sendRedirect(req.getContextPath() + "/product?method=index");
    }

    /**
     * 校验用户名是否已存在
     * @param req
     * @param resp
     * @throws IOException
     */
    public void checkUsername(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        System.out.println("CheckUsernameServlet.doPost()");
        String username = req.getParameter("username");
        boolean isExist = false;
        isExist = service.checkUsername(username);
        String json = "{\"isExist\":" + isExist + "}";
        resp.getWriter().write(json);

    }

    /**
     * 用户激活
     * @param req
     * @param resp
     * @throws IOException 
     */
    public void active(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {

        String code = req.getParameter("activeCode");
        boolean result = service.active(code);
        if (result)
        {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

}
