package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 保存菜品及其口味信息
     *
     * @param dishDTO
     */
    @Override
    // 涉及到两个表的操作所以使用事务，确保数据一致性，出现异常时回滚，要么全部成功，要么全部失败
    @Transactional(rollbackFor = Exception.class)
    public void saveDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        // 保存菜品基本信息到菜品表dish
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.InsertDish(dish);
        Long dishId = dish.getId(); // 获取菜品id
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null || flavors.size() > 0) {
            // 口味会有多个，插入n条数据 保存菜品口味信息到菜品口味表dish_flavor
            // 可以批量插入
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertDishFlavorBatch(flavors);
        }
    }

    @Override
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = new PageResult();
        PageHelper.startPage(
                categoryPageQueryDTO.getPage(),
                categoryPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(categoryPageQueryDTO);
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return Result.success(pageResult);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateById(dish);

    }

    /**
     * 删除菜品及其口味信息
     *
     * @param ids
     */
    @Override
    public void deleteDishByIds(List<Long> ids) {
        // 在售状态的菜品不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 是否被套餐关联了
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中的数据
//        for(Long id : ids){
//            dishMapper.deleteById(id);
//            // 删除关联的菜品口味表中的数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        // 优化：批量删除
        dishMapper.dleteByIds(ids);
        // 删除关联的菜品口味表中的数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品及其口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish != null) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
            dishVO.setFlavors(flavors);
            return dishVO;
        }
        return null;
    }

    /**
     * 更新菜品及其口味信息
     *
     * @param dishDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        // 更新菜品表中的基本信息
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
        Long dishId = dishDTO.getId();
        // 更新菜品口味表中的信息
        // 先删除原有口味数据
        dishFlavorMapper.deleteByDishId(dishId);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            // 再插入新的口味数据，口味可能有多个，插入n条数据
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertDishFlavorBatch(flavors);
        }
    }


    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish); // 根据条件查询菜品列表,dish中有分类id和状态两个条件,id为null不会作为条件，status为1表示查询起售中的菜品

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    @Override
    public List<Dish> listDishByCategoryId(Long categoryId) {

        List<Dish> list = dishMapper.listDishByCategoryId(categoryId);
        return list;
    }
}