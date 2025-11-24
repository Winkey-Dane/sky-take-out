package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    void insertDishFlavorBatch(List<DishFlavor> flavors, Long dishId);

    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(Long id);

    // 根据菜品dishIds批量删除对应的口味数据
    void deleteByDishIds(List<Long> dishIds);
}
