package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺管理")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("{status}")
    @ApiOperation("管理员端更新店铺状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("更新店铺状态为：{}", status == 1 ? "店铺营业中" : "店铺休息中");
        redisTemplate.opsForValue().set("ShopStatus", status);
        return Result.success();
    }
    @GetMapping("/status")
    @ApiOperation("管理员端获取店铺状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("ShopStatus");
        log.info("店铺状态为：{}", status == 1 ? "店铺营业中" : "店铺休息中");
        return Result.success(status);
    }
}
