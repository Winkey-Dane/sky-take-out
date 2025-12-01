package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
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

    /**
     * 根据用户id删除购物车记录
     * @param currentId
     */
    @Delete("delete from shopping_cart where user_id = #{currentId}")
    void deleteByUserId(Long currentId);

    /**
     * 根据id删除购物车记录,这个id是购物车记录的id
     * @param id
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量插入购物车记录
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
