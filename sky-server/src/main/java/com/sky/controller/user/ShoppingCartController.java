package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api("C端-购物车相关接口")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加商品到购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public Result addMealToCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加商品到购物车");
        shoppingCartService.addMealToCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车列表")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车列表");
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result clear() {
        log.info("清空购物车");
        shoppingCartService.clear();
        return Result.success();
    }

    /**
     * 减少购物车商品数量
     * @param shoppingCartDTO
     */
    @PostMapping("/sub")
    @ApiOperation("减少购物车商品数量")
    public Result reduceMealFromCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("减少购物车商品数量");
        shoppingCartService.reduceMealFromCart(shoppingCartDTO);
        return Result.success();
    }
}
