package com.js.shop.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.js.common.util.DataSourceUtil;
import com.js.shop.domain.User;

public class UserDao
{
    QueryRunner runner = new QueryRunner(DataSourceUtil.getDataSource());

    /**
     * 用户注册
     * @param user
     * @return
     */
    public int register(User user)
    {
        String sql = "insert into user(uid,username,password,name,email,telephone,birthday,sex,state,code) values(?,?,?,?,?,?,?,?,?,?)";
        int row = 0;
        try
        {
            row = runner.update(sql, user.getUid(), user.getUsername(), user.getPassword(), user.getName(), user.getEmail(), user.getTelephone(), user.getBirthday(), user.getSex(), user.getState(), user.getCode());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return row;
    }

    /**
     * 用户激活
     * @param code
     * @return
     */
    public int active(String code)
    {
        String sql = "update user set state = 1 where code=?";
        int result = 0;
        try
        {
            result = runner.update(sql, code);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 校验用户名是否存在
     * @param userName
     * @return
     */
    public Long checkUsername(String username)
    {
        String sql = "select count(*) from user a where a.username = ?";
        Long rows = 0l;
        try
        {
            rows = (Long) runner.query(sql, new ScalarHandler(), username);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rows;
    }

    /**
     * 根据用户名和密码查询用户信息
     * @param username
     * @param pwd
     * @return
     */
    public User findUserByUserNamePwd(String username, String pwd)
    {
        String sql = "select * from user a where a.username = ? and a.password = ? and a.state = 1";
        User user = null;
        try
        {
            user = runner.query(sql, new BeanHandler<User>(User.class), username, pwd);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return user;
    }

}
