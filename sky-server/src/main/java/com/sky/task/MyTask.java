package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {


    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理支付超时的订单
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void orderOutTime() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());

        //判断状态为待支付且下单时间超过15min的订单
        List<Orders> ordersList = orderMapper.getPaymentTimeoutOrders(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));

        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                //更新订单状态为4
                Orders ordersDB = Orders.builder()
                        .id(orders.getId())
                        .status(Orders.CANCELLED)
                        .cancelReason("支付超时，取消订单")
                        .cancelTime(LocalDateTime.now())
                        .build();
                orderMapper.update(ordersDB);
            }
        }
    }


    /**
     * 处理一直在派送中订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点触发一次
    public void processDeliveryOrders() {
        log.info("处理派送中的订单：{}", LocalDateTime.now());

        //查询状态为3的订单
        List<Orders> ordersList = orderMapper.getPaymentTimeoutOrders(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));

        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                //更新订单状态为4
                Orders ordersDB = Orders.builder()
                        .id(orders.getId())
                        .status(Orders.COMPLETED)
                        .deliveryTime(LocalDateTime.now())
                        .build();
                        orderMapper.update(ordersDB);
            }
        }
    }
}
