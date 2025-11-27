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

}
