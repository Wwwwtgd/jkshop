package com.js.shop.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cart
{
    private Map<String, CartItem> cartItems = new HashMap<>();
    private double total;//合计

    /**
     * 构造方法 计算购物车合计
     * @param cartItems
     */
    public Cart(Map<String, CartItem> cartItems)
    {
        this.cartItems = cartItems;
        Set<String> keySet = cartItems.keySet();
        for (String key : keySet)
        {
            this.total += cartItems.get(key).getSubTotal();
        }
    }

    public Map<String, CartItem> getCartItems()
    {
        return cartItems;
    }

    public void setCartItems(Map<String, CartItem> cartItems)
    {
        this.cartItems = cartItems;
    }

    public double getTotal()
    {
        return total;
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    @Override
    public String toString()
    {
        return "Cart [cartItems=" + cartItems + ", total=" + total + "]";
    }

}
