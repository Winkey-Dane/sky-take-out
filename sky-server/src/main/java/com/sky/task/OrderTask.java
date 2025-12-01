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
 * @description: 订单相关定时任务
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?") // 一分钟执行一次
    public void processTimedOutOrders() {
        log.info("处理超时未支付订单{}", LocalDateTime.now());
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(15); // 当前时间减去15分钟
        List<Orders> list = orderMapper.getByStatusAndTimeLT(Orders.PENDING_PAYMENT,orderTime);
        if (list.size() > 0 && list != null) {
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("超时未支付，系统自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
                log.info("订单号{}已超时未支付，系统自动取消订单", orders.getNumber());
            }

        }
    }

    /**
     * 处理配送中的订单
     * 每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行一次
    public void processDeliveryOrder(){
        log.info("处理配送中的订单{}", LocalDateTime.now());
        // 减去1小时
        LocalDateTime orderTime = LocalDateTime.now().minusHours(1);
        List<Orders> list = orderMapper.getByStatusAndTimeLT(Orders.DELIVERY_IN_PROGRESS,orderTime);
        if (list.size() > 0 && list != null) {
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }

        }
    }
}
