package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api("后台套餐管理接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;


    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(value = "setMealCache", key = "#setmealDTO.categoryId")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询接口
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询接口")
    public Result<PageResult> findSetmealPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = setmealService.pageQuerySetmeal(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改套餐状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐状态")
    @CacheEvict(value = "setMealCache", allEntries = true)
    public Result changeSetmealStatus(@PathVariable Integer status, @RequestParam Long id) {
        setmealService.changeSetmealStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐信息，进行套餐信息回显")
    public Result<SetmealVO> selectSetmealById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.selectSetmealById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐信息")
    @CacheEvict(value = "setMealCache", allEntries = true)
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        // Implementation for updating a setmeal would go here
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐接口")
    @CacheEvict(value = "setMealCache", allEntries = true)
    public Result deleteSetmealByIds(@RequestParam List<Long> ids) {
        // Implementation for deleting setmeals would go here
        setmealService.deleteSetmealByIds(ids);
        return Result.success();
    }
}
