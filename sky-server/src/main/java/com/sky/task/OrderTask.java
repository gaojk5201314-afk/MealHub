package com.sky.task;

/*定时任务类*/

import com.sky.entity.Orders;
import com.sky.mapper.AdminOrderMapping;
import com.sky.service.AdminOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.sky.entity.Orders.CANCELLED;
import static com.sky.entity.Orders.DELIVERY_IN_PROGRESS;

/*component注解是把这个类实例化，并放到spring容器中管理*/
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private AdminOrderMapping adminOrderMapping;

    /*定时任务方法*/
    @Scheduled(cron = "0 0/1 * * * ?")
    public void processTimeoutOrder() {
        log.info("处理超时订单");
        // 处理超时订单的逻辑
        List<Orders> orders = adminOrderMapping.processTimeoutOrder(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));
        if(orders != null && !orders.isEmpty()){
            for(Orders order : orders){
                order.setStatus(CANCELLED);
                order.setCancelReason("订单超时未付款");
                order.setCancelTime(LocalDateTime.now());
                adminOrderMapping.update(order);
            }
        }

    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中订单");
        List<Orders> orders = adminOrderMapping.processDeliveryOrder(DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));
        if(orders != null && !orders.isEmpty()){
            for(Orders order : orders){
                order.setStatus(Orders.COMPLETED);
                adminOrderMapping.update(order);
            }
        }
    }

}
