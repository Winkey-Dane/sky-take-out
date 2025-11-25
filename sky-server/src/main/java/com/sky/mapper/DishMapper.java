package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void InsertDish(Dish dish);

    Page<DishVO> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void updateById(Dish dish);

    @Select("select * from dish where id = #{id}")
    Dish selectById(Long id);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /*
    * 根据ids批量删除菜品
     */
    void dleteByIds(List<Long> ids);

    List<Dish> list(Dish dish);
}
