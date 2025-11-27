package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api("菜品管理相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品接口
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品接口")
    // @RequestBody 将请求体中的JSON数据转换为DishDTO对象
    public Result saveDishWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveDishWithFlavor(dishDTO);
        // 清理缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        clearRedis(key);

        return Result.success();
    }

    /**
     * 菜品分页查询接口
     *
     * @param page     当前页码
     * @param pageSize 每页显示条数
     * @param name     菜品名称（可选）模糊查询
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询接口")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) { // 菜品名称可选
        log.info("菜品分页查询：{}", categoryPageQueryDTO);
        return dishService.pageQuery(categoryPageQueryDTO);
    }

    /**
     * 启用禁用菜品接口
     *
     * @param status 菜品状态 0-禁用 1-启用
     * @param id     菜品id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用菜品接口")
    public Result updateStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("修改菜品状态：{},{}", status, id);
        dishService.updateStatus(status, id);
        // 全部清理缓存数据
        clearRedis("dish_*");
        return Result.success();
    }


    /**
     * 批量删除菜品接口
     *
     * @param ids 菜品ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品接口")
    public Result deleteDishByIds(@RequestParam List<Long> ids) {
        // 传过来是字符串，Spring会自动转换为List<Long>
        log.info("批量删除菜品：{}", ids);
        dishService.deleteDishByIds(ids);
        // 清理缓存数据
        clearRedis("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     *
     * @param id 菜品id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品信息")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品信息：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息：{}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);

        // 清理缓存数据
        clearRedis("dish_*");

        return Result.success();
    }

    private void clearRedis(String keyPattern) {
        redisTemplate.delete(redisTemplate.keys(keyPattern));
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> listDishByCategoryId(Long categoryId) {
        List<Dish> list = dishService.listDishByCategoryId(categoryId);
        return Result.success(list);
    }
}