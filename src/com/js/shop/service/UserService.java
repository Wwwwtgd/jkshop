package com.js.shop.service;

import com.js.shop.dao.UserDao;
import com.js.shop.domain.User;

public class UserService
{
    UserDao dao = new UserDao();

    /**
     * 用户注册
     * @param user
     * @return
     */
    public boolean register(User user)
    {
        int row = dao.register(user);
        return row > 0 ? true : false;
    }

    /**
     * 用户激活
     * @param code
     * @return
     */
    public boolean active(String code)
    {
        int result = dao.active(code);
        return result > 0 ? true : false;
    }

    /**
     * 校验用户是否存在
     * @param username
     * @return
     */
    public boolean checkUsername(String username)
    {
        Long rows = dao.checkUsername(username);
        return rows > 0 ? true : false;
    }

    /**
     * 根据用户名和密码查询用户信息
     * @param username
     * @param pwd
     * @return
     */
    public User findUserByUserNamePwd(String username, String pwd)
    {
        return dao.findUserByUserNamePwd(username, pwd);
    }
}
