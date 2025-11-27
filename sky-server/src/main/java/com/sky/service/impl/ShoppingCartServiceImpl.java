package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addMealToCart(ShoppingCartDTO shoppingCartDTO) {
        // 是否存在购物车中
        log.info("添加套餐到购物车: {}", shoppingCartDTO);
        ShoppingCart shoppingCart = new ShoppingCart();
        // 设置属性，属性拷贝
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long UserId = BaseContext.getCurrentId();
        shoppingCart.setUserId(UserId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 存在则数量加一
        if (list != null && list.size() > 0) {
            ShoppingCart cartItem = list.get(0); // 0下标代表
            Integer number = cartItem.getNumber();
            cartItem.setNumber(number + 1);
            shoppingCartMapper.updateNumberById(cartItem);
        }else{
            // 不存在则添加到购物车，数量为一
            // 判断是套餐还是菜品
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            if (dishId != null) {
                // 菜品
                Dish dish = dishMapper.selectById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                // 套餐
                Setmeal setmeal = setmealMapper.selectSetmealById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insertShoppingCart(shoppingCart);
        }
    }


    /**
     * 查看购物车列表
     *
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }
}
