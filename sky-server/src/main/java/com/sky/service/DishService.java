package com.sky.service;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;

import java.util.List;


public interface DishService {
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDTO
     */
    public void saveDishWithFlavor(DishDTO dishDTO);

    Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateStatus(Integer status, Long id);

    void deleteDishByIds(List<Long> ids);
}
