package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;

public interface AdminOrderService {
    void cancelOrder(OrdersCancelDTO ordersCancelDTO);
    OrderStatisticsVO getStatistics();
    void completeOrder(Long id);
    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);
    void confirmOrder(Long id);
    OrderVO getOrderDetails(Long id);
    void deliveryOrder(Long id);
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);
    Orders selectAll();
}
