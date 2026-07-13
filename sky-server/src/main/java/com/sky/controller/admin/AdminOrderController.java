package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.AdminOrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理员订单接口")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @PutMapping("/cancel")
    @ApiOperation("取消订单管理端(RequestBody)")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单，DTO完整内容：{}", ordersCancelDTO);
        log.info("取消订单，id={}，cancelReason={}", ordersCancelDTO.getId(), ordersCancelDTO.getCancelReason());
        adminOrderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单管理端(PathVariable)")
    public Result cancelOrderById(@PathVariable Long id, @RequestBody(required = false) OrdersCancelDTO ordersCancelDTO) {
        String cancelReason = (ordersCancelDTO != null) ? ordersCancelDTO.getCancelReason() : null;
        log.info("取消订单(PathVariable)，订单id：{}，取消原因：{}", id, cancelReason);
        OrdersCancelDTO dto = new OrdersCancelDTO();
        dto.setId(id);
        dto.setCancelReason(cancelReason);
        adminOrderService.cancelOrder(dto);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getStatistics() {
        log.info("获取订单统计信息");
        return Result.success(adminOrderService.getStatistics());
    }

    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable Long id) {
        log.info("完成订单，订单id：{}", id);
        adminOrderService.completeOrder(id);
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒绝订单，订单id：{}，拒绝原因：{}", ordersRejectionDTO.getId(), ordersRejectionDTO.getRejectionReason());
        adminOrderService.rejectionOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody Map<String, Object> body) {
        log.info("confirm 原始请求体：{}", body);
        Long id = body != null && body.get("id") != null ? Long.valueOf(body.get("id").toString()) : null;
        log.info("确认订单，订单id：{}", id);
        if (id == null) {
            return Result.error("订单id不能为空");
        }
        adminOrderService.confirmOrder(id);
        return Result.success();
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetails(@PathVariable Long id) {
        log.info("获取订单详情，订单id：{}", id);
        OrderVO orderVO =  adminOrderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    @PutMapping("/delivery/{id}")
    public Result deliveryOrder(@PathVariable Long id) {
        log.info("派送订单，订单id：{}", id);
        adminOrderService.deliveryOrder(id);
        return Result.success();
    }

    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("根据条件查询订单，订单：{}", ordersPageQueryDTO);
        PageResult pageResult = adminOrderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /*查询回显*/
    @GetMapping()
    public Result<Orders> getOrderDetails() {
        log.info("查询回显订单（最新订单）");
        Orders order = adminOrderService.selectAll();
        return Result.success(order);
    }

}
