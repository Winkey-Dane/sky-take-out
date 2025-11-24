package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")// RestController的作用相当于@Controller + @ResponseBody，
// 表示该类是一个控制器，并且返回的结果直接作为HTTP响应体返回给客户端
@RequestMapping("/user/shop") // @RequestMapping用于映射HTTP请求到处理方法上，
@Slf4j
@Api("店铺相关接口")
public class ShopController {
    private RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";
    /**
     * 获取店铺营业状态
     * @return 店铺状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("店铺营业状态：{}", status==1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
