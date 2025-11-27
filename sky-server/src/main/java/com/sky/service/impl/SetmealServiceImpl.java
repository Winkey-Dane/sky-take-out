package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 保存套餐的基本信息
        setmealMapper.insert(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 设置套餐id
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        // 保存套餐和菜品的关联关系
        setmealDishMapper.batchInsert(setmealDishes);
    }

    /**
     * 分页查询套餐
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuerySetmeal(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuerySetmeal(categoryPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    @Override
    public void changeSetmealStatus(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.updateById(setmeal);
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO selectSetmealById(Long id) {
        Setmeal setmeal = setmealMapper.selectSetmealById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 涉及多表操作，使用事务保证数据一致性
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 更新套餐的基本信息
        setmealMapper.updateById(setmeal);
        // 删除套餐和菜品的关联关系
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId()); // 删除原本的关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 向set_meal表插入数据
        setmealMapper.insertIntoSetMealDishes(setmealDishes, setmeal.getId()); // 需要套餐表的主键回显
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteSetmealByIds(List<Long> ids) {
        // 在售的套餐不能删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectSetmealById(id);
            if (Objects.equals(setmeal.getStatus(), StatusConstant.DISABLE)) {
                log.error("删除套餐失败，套餐正在售卖中，套餐id：{}", id);
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        // 删除套餐表中的数据
        setmealMapper.deleteSetmealByIds(ids);
        // 删除套餐和菜品的关联关系
        setmealDishMapper.deleteBySetmealIds(ids);
    }
}
