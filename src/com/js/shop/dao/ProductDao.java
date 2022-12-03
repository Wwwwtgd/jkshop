package com.js.shop.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.js.common.util.DataSourceUtil;
import com.js.shop.domain.Category;
import com.js.shop.domain.Order;
import com.js.shop.domain.OrderItem;
import com.js.shop.domain.Product;

public class ProductDao
{
    QueryRunner runner = new QueryRunner(DataSourceUtil.getDataSource());

    QueryRunner qu = new QueryRunner();
    Connection conn = null;

    /**
     * 查询热门商品列表信息
     * @return
     */
    public List<Product> findHotProducts()
    {
        List<Product> hotList = null;
        String sql = "select pid,pname,market_price,shop_price,pimage,pdate,is_hot,pdesc,pflag,cid  from product a where a.is_hot = ? limit ?,? ";
        try
        {
            hotList = runner.query(sql, new BeanListHandler<Product>(Product.class), 1, 0, 9);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hotList;
    }

    /**
     * 查询最新商品信息
     * @return
     */
    public List<Product> findNewProducts()
    {
        List<Product> newList = null;
        String sql = "select pid,pname,market_price,shop_price,pimage,pdate,is_hot,pdesc,pflag,cid  from product a order by a.pdate desc limit ?,? ";
        try
        {
            newList = runner.query(sql, new BeanListHandler<Product>(Product.class), 0, 9);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newList;
    }

    /**
     * 根据商品类别分页查询商品列表信息
     * @param cid
     * @param start
     * @param pagesize
     * @return
     */
    public List<Product> findProductListByCid(String cid, int start, int pagesize)
    {
        String sql = "select pid,pname,market_price,shop_price,pimage,pdate,is_hot,pdesc,pflag,cid  from product a where a.cid = ?  limit ?,? ";
        List<Product> cList = null;
        try
        {
            cList = runner.query(sql, new BeanListHandler<Product>(Product.class), cid, start, pagesize);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cList;
    }

    /**
     * 查询某商品分类下所有商品个数
     * @param cid
     * @return
     */
    public int findProductTotalByCid(String cid)
    {
        String sql = "select count(*) from product a where a.cid = ?";
        Long rows = 0l;
        try
        {
            rows = runner.query(sql, new ScalarHandler<Long>(), cid);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rows.intValue();
    }

    /**
     * 查询商品分类信息
     * @return
     */
    public List<Category> findCategorys()
    {
        List<Category> cateList = null;
        String sql = "select a.cid,a.cname from category a";
        try
        {
            cateList = runner.query(sql, new BeanListHandler<Category>(Category.class));
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cateList;
    }

    /**
     * 根据pid获取商品详细信息
     * @param pid
     * @return
     */
    public Product findProductsByPid(String pid)
    {
        String sql = "select * from product a where a.pid = ?";
        Product product = null;
        try
        {
            product = runner.query(sql, new BeanHandler<>(Product.class), pid);
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return product;
    }

    /**
     * 存储订单信息
     * @param order
     * @throws SQLException 
     */
    public void saveOrder(Order order) throws SQLException
    {
        conn = DataSourceUtil.getConnection();
        String sql = "insert into orders(oid,ordertime,total,state,uid) values(?,now(),?,?,?) ";
        qu.update(conn, sql, order.getOid(), order.getTotal(), order.getState(), order.getUser().getUid());
    }

    /**
     * 存储订单项信息
     * @param orderItem
     */
    public void saveOrderItem(OrderItem orderItem) throws SQLException
    {
        conn = DataSourceUtil.getConnection();
        String sql = "insert into orderitem(itemid,count,subtotal,pid,oid) values(?,?,?,?,?) ";
        qu.update(conn, sql, orderItem.getItemid(), orderItem.getCount(), orderItem.getSubtotal(), orderItem.getProduct().getPid(), orderItem.getOrder().getOid());
    }

    /**
     * 确认订单
     * @param order
     */
    public void confirmOrder(Order order)
    {
        String sql = "update orders set address=?,name=?,telephone=? where oid = ?";
        try
        {
            runner.update(sql, order.getAddress(), order.getName(), order.getTelephone(), order.getOid());
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 查询用户下所有订单信息
     * @param uid
     * @return
     * @throws SQLException 
     */
    public List<Order> findOrderList(String uid, int start, int pageSize) throws SQLException
    {
        String sql = "select * from orders a where a.uid = ? limit ?,?";
        List<Order> orderList = runner.query(sql, new BeanListHandler<Order>(Order.class), uid, start, pageSize);
        return orderList;
    }

    /**
     * 查询该用户总的订单数
     * @param uid
     * @return
     */
    public int findOrderTotal(String uid) throws Exception
    {
        String sql = "select count(a.oid) from orders a where a.uid = ?";
        Long rows = runner.query(sql, new ScalarHandler<Long>(), uid);
        return rows.intValue();
    }

    /**
     * 获取订单项列表信息
     * @param oid
     * @return
     * @throws SQLException 
     */
    public List<Map<String, Object>> findOrderItemList(String oid) throws SQLException
    {
        StringBuffer sb = new StringBuffer();
        sb.append("select i.count,i.subtotal,p.pimage,p.pname,p.shop_price");
        sb.append(" from orderitem i,product p");
        sb.append(" where i.pid = p.pid and i.oid = ?");
        List<Map<String, Object>> items = runner.query(sb.toString(), new MapListHandler(), oid);
        return items;
    }

}
