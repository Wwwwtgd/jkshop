package com.js.shop.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order
{
    private String oid; //订单标识
    private Date ordertime;//下单时间
    private double total;//总金额
    private int state;//订单状态  付款状态1--已付款   0--未付款
    private String address;//收货地址
    private String name;//收货人姓名
    private String telephone;//联系方式
    private User user;//谁下的单

    //该订单下的订单详情集合,为了方便后续传递参数
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    public String getOid()
    {
        return oid;
    }

    public void setOid(String oid)
    {
        this.oid = oid;
    }

    public Date getOrdertime()
    {
        return ordertime;
    }

    public void setOrdertime(Date ordertime)
    {
        this.ordertime = ordertime;
    }

    public double getTotal()
    {
        return total;
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public List<OrderItem> getOrderItems()
    {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems)
    {
        this.orderItems = orderItems;
    }

    @Override
    public String toString()
    {
        return "Order [oid=" + oid + ", ordertime=" + ordertime + ", total=" + total + ", state=" + state + ", address=" + address + ", name=" + name + ", telephone=" + telephone + ", user=" + user + "]";
    }

}
