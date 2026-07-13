package com.sky.controller.user;


import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderSubmitService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.beans.beancontext.BeanContext;

@RestController
@RequestMapping("/user/order")
@Api(tags = "用户订单提交")
@Slf4j
public class OrderSubmitController {

    @Autowired
    private OrderSubmitService orderSubmitService;

    @PostMapping("/submit")
    @ApiOperation("用户订单提交接口")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户订单提交的数据为:{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderSubmitService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);

    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("用户再来一单提交接口")
    public Result repetition(@PathVariable Long id) {
        log.info("用户再来一单提交的订单id为:{}", id);
        orderSubmitService.repetition(id);
        return Result.success();
    }

    @GetMapping("/historyOrders")
    @ApiOperation("用户订单历史查询接口")
    public Result<PageResult> historyOrders(Integer pageNum, Integer pageSize, Integer status) {
        log.info("用户id为:{}", BaseContext.getCurrentId());
        // 设置默认值
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;
        PageResult pageResult = orderSubmitService.pageQuery4User(pageNum, pageSize, status);
        return Result.success(pageResult);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("用户订单取消接口")
    public Result cancel(@PathVariable Long id) {
        log.info("用户取消订单的订单id为:{}", id);
        orderSubmitService.cancel(id);
        return Result.success();
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderSubmitService.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<String> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        orderSubmitService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return  Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("用户订单催单提醒接口")
    public Result reminder(@PathVariable Long id) {
        log.info("用户订单催单提醒的订单id为:{}", id);
        orderSubmitService.reminder(id);
        return Result.success();
    }

}