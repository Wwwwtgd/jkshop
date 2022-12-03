package com.js.shop.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.js.common.util.DataSourceUtil;
import com.js.shop.dao.ProductDao;
import com.js.shop.domain.Category;
import com.js.shop.domain.Order;
import com.js.shop.domain.OrderItem;
import com.js.shop.domain.PageBean;
import com.js.shop.domain.Product;

public class ProductService
{

    ProductDao dao = new ProductDao();

    /**
     * 查询热门商品
     * @return
     */
    public List<Product> findHotProducts()
    {
        List<Product> hotProducts = dao.findHotProducts();
        return hotProducts;
    }

    /**
     * 查询最新商品
     * @return
     */
    public List<Product> findNewProducts()
    {
        List<Product> newProducts = dao.findNewProducts();
        return newProducts;

    }

    /**
     * 查询商品分类信息
     * @return
     */
    public List<Category> findCategorys()
    {
        return dao.findCategorys();
    }

    /**
     * 根据cid读取商品分类列表信息
     * @param cid
     * @return
     */
    public PageBean<Product> findProductListByCid(String cid, int currentPage, int pageSize)
    {
        int start = (currentPage - 1) * pageSize; //第一页 0*pageSize ;第二页 12
        int totalCount = dao.findProductTotalByCid(cid);
        PageBean<Product> pageBean = new PageBean<Product>(currentPage, pageSize, totalCount);
        List<Product> dataList = dao.findProductListByCid(cid, start, pageSize);
        pageBean.setList(dataList);
        return pageBean;
    }

    /**
     * 根据pid获取商品详细信息
     * @param pid
     * @return
     */
    public Product findProductsByPid(String pid)
    {
        return dao.findProductsByPid(pid);
    }

    /**
     * 提交订单
     * 事务处理(原子操作：所有操作同时成功，失败要都失败。)
     * @param order
     * @throws SQLException 
     */
    public void submitOrder(Order order) throws SQLException
    {

        DataSourceUtil.startTransaction();//开启事务
        try
        {
            //存储订单信息
            dao.saveOrder(order);
            //存储订单项信息
            List<OrderItem> orderItems = order.getOrderItems();
            if (orderItems != null)
            {
                for (OrderItem orderItem : orderItems)
                {
                    dao.saveOrderItem(orderItem);
                }
            }
        }
        catch (Exception e)
        {
            DataSourceUtil.rollback();//任何操作失败，所有操作回滚
        }
        finally
        {
            DataSourceUtil.commitAndRelease();//所有操作成功，提交事务并且释放资源（连接）
        }
    }

    /**
     * 确认订单
     * @param order
     */
    public void confirmOrder(Order order)
    {
        dao.confirmOrder(order);
    }

    /**
     * 查询我的订单列表信息
     * @param uid
     * @return
     * @throws SQLException 
     */
    public PageBean<Order> findOrderList(String uid, int pageSize, int currentPage) throws Exception
    {
        //1、查询用户下所有订单
        int total = dao.findOrderTotal(uid);
        int start = (currentPage - 1) * pageSize;
        PageBean pageBean = new PageBean<Order>(currentPage, pageSize, total);
        List<Order> orderList = dao.findOrderList(uid,start,pageSize);
        pageBean.setList(orderList);
        //2、循环order列表获取每一个order的订单项信息
        for (Order order : orderList)
        {
            List<Map<String, Object>> items = dao.findOrderItemList(order.getOid());
            List<OrderItem> orderItems = new ArrayList<OrderItem>();
            order.setOrderItems(orderItems);
            for (Map<String, Object> map : items)
            {
                OrderItem orderItem = new OrderItem();
                Product product = new Product();
                BeanUtils.populate(orderItem, map);
                BeanUtils.populate(product, map);
                orderItem.setProduct(product);
                orderItems.add(orderItem);
            }
        }
        return pageBean;
    }
}
