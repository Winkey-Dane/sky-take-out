package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 动态条件查询购物车
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 修改购物车中某个菜品的数量
     * @param shoppingCart
     */
    void updateNumberById(ShoppingCart cartItem);

    /**
     * 新增购物车记录
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, name, image, dish_flavor, number, amount, create_time) " +
            "values (#{userId}, #{dishId}, #{setmealId}, #{name}, #{image}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insertShoppingCart(ShoppingCart shoppingCart);
}
