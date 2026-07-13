package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminOrderMapping {
    int update(Orders order);
    List<Orders> getStatistics();
    void updateById(Orders order);
    void RejectOrder(Orders order);
    void ConfirmOrder(Orders order);
    Orders getById(@Param("id") Long id);
    List<OrderDetail> getOrderDetail(@Param("id") Long id);
    void DeliveryOrder(Orders order);
    List<Orders> select(OrdersPageQueryDTO orders);
    Orders selectAll();
    @Select("select * from orders where status = #{status} and order_time < #{timeoutTime}")
    List<Orders> processTimeoutOrder(Integer status, LocalDateTime timeoutTime);
    @Select("select * from orders where status = #{status} and order_time < #{timeoutTime}")
    List<Orders> processDeliveryOrder(Integer status, LocalDateTime timeoutTime);
}
