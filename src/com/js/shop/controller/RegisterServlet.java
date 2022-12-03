package com.js.shop.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

import com.js.common.util.CommonUtil;
import com.js.common.util.MailUtil;
import com.js.shop.domain.User;
import com.js.shop.service.UserService;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet
{
    private UserService service = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        //解决post请求中文乱码问题
        req.setCharacterEncoding("utf-8");
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
            String emailMsg = user.getUsername()+"用户恭喜您注册成功，请点击下面的连接进行激活账户" + "<a href='http://localhost:8080/jkshop/active?activeCode=" + code + "'>" + "http://localhost:8080/jkshop/active?activeCode=" + code + "</a>";
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

}
