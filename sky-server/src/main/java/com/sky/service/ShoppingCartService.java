package com.sky.service;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加套餐到购物车
     * @param shoppingCartDTO
     */
    void addMealToCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车列表
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 清空购物车
     */
    void clear();

    /**
     * 减少购物车中套餐数量
     * @param shoppingCartDTO
     */
    void reduceMealFromCart(ShoppingCartDTO shoppingCartDTO);
}
