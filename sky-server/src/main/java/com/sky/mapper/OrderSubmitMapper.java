package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.vo.OrderSubmitVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderSubmitMapper {

    void submit(List<OrderDetail> orderDetails);
    void insert(Orders order);
    void insertBatch(List<ShoppingCart> List);
    List<OrderDetail> getByOrderId(@Param("orderId") Long orderId);
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    Orders getById(@Param("id") Long id);
    void updateById(Orders order);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


}
