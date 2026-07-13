package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AdminOrderMapping;
import com.sky.result.PageResult;
import com.sky.service.AdminOrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.sky.entity.Orders.*;

@Service
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private AdminOrderMapping adminOrderMapping;

    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        log.info("cancelOrder 入参：id={}, cancelReason={}", ordersCancelDTO.getId(), ordersCancelDTO.getCancelReason());

        // 校验订单id
        if (ordersCancelDTO.getId() == null) {
            log.error("cancelOrder 失败：订单id为null");
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 根据id查询订单
        log.info("cancelOrder 开始查询订单：id={}", ordersCancelDTO.getId());
        Orders ordersDB = adminOrderMapping.getById(ordersCancelDTO.getId());
        log.info("cancelOrder 查询结果：{}", ordersDB);
        if (ordersDB == null) {
            log.error("cancelOrder 失败：订单不存在，id={}", ordersCancelDTO.getId());
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 校验订单状态，只有待付款、待接单状态才能取消
        log.info("cancelOrder 订单当前状态：{}", ordersDB.getStatus());
        if (!ordersDB.getStatus().equals(PENDING_PAYMENT) && !ordersDB.getStatus().equals(TO_BE_CONFIRMED)) {
            log.error("cancelOrder 失败：订单状态错误，当前状态={}", ordersDB.getStatus());
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 取消订单
        Orders order = new Orders();
        order.setId(ordersCancelDTO.getId());
        order.setStatus(CANCELLED);
        order.setCancelReason(ordersCancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());
        order.setPayStatus(REFUND);
        /*更新订单*/
        log.info("cancelOrder 执行更新：id={}, status={}", order.getId(), order.getStatus());
        adminOrderMapping.update(order);
        log.info("cancelOrder 更新完成");

    }

    @Override
    public OrderStatisticsVO getStatistics() {
        List<Orders> orders = adminOrderMapping.getStatistics();
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        // 初始化统计字段为0
        orderStatisticsVO.setToBeConfirmed(0);
        orderStatisticsVO.setConfirmed(0);
        orderStatisticsVO.setDeliveryInProgress(0);
        
        for (Orders order : orders) {
            if (Objects.equals(order.getStatus(), TO_BE_CONFIRMED)) {
                orderStatisticsVO.setToBeConfirmed(orderStatisticsVO.getToBeConfirmed() + 1);
            }
            else if (Objects.equals(order.getStatus(), CONFIRMED)) {
                orderStatisticsVO.setConfirmed(orderStatisticsVO.getConfirmed() + 1);
            }
            else if (Objects.equals(order.getStatus(), DELIVERY_IN_PROGRESS)) {
                orderStatisticsVO.setDeliveryInProgress(orderStatisticsVO.getDeliveryInProgress() + 1);
            }
        }

        return orderStatisticsVO;

    }

    @Override
    public void completeOrder(Long id) {
        // 根据id查询订单
        Orders ordersDB = adminOrderMapping.getById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 完成订单
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(COMPLETED);
        order.setDeliveryTime(LocalDateTime.now());
        adminOrderMapping.updateById(order);
    }

    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        // 拒绝订单
        Orders order = new Orders();
        order.setStatus(CANCELLED);
        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        order.setCancelTime(LocalDateTime.now());
        order.setId(ordersRejectionDTO.getId());
        order.setPayStatus(REFUND);
        /*更新订单*/
        adminOrderMapping.RejectOrder(order);
    }

    @Override
    public void confirmOrder(Long id) {
        // 根据id查询订单
        Orders ordersDB = adminOrderMapping.getById(id);
        
        // 校验订单是否存在，并且状态为待接单(2)
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!ordersDB.getStatus().equals(TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        
        // 确认订单
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(CONFIRMED);
        adminOrderMapping.ConfirmOrder(order);
    }

    @Override
    public OrderVO getOrderDetails(Long id) {
        Orders order = adminOrderMapping.getById(id);
        List<OrderDetail> orderDetail = adminOrderMapping.getOrderDetail(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetail);
        return orderVO;
    }

    @Override
    public void deliveryOrder(Long id) {
        // 派送订单
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(DELIVERY_IN_PROGRESS);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        adminOrderMapping.DeliveryOrder(order);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        List<Orders> orders = adminOrderMapping.select(ordersPageQueryDTO);
        Page<Orders> page = (Page<Orders>) orders;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Orders selectAll() {
        Orders order = adminOrderMapping.selectAll();
        return order;
    }
}