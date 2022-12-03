package com.js.shop.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.js.shop.domain.User;
import com.js.shop.service.UserService;

@WebFilter(filterName = "autoLoginFilter", urlPatterns = { "/default.jsp" })
public class AutoLoginFilter implements Filter
{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            chain.doFilter(request, response);
        }
        else
        {
            String username = "";
            String password = "";
            Cookie[] cookies = req.getCookies();
            if (cookies != null)
            {
                for (Cookie cookie : cookies)
                {
                    if ("cookie_username".equals(cookie.getName()))
                    {
                        username = cookie.getValue();
                    }
                    if ("cookie_pwd".equals(cookie.getName()))
                    {
                        password = cookie.getValue();
                    }
                }

                if (username != null && password != null && !"".equals(password) && !"".equals(username))
                {
                    UserService service = new UserService();
                    user = service.findUserByUserNamePwd(username, password);
                    if (user != null)
                    {
                        session.setAttribute("user", user);
                    }

                }
            }
            chain.doFilter(request, response);
        }
    }

}
