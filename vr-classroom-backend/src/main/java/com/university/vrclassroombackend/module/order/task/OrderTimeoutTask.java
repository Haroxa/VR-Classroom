package com.university.vrclassroombackend.module.order.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.university.vrclassroombackend.module.order.mapper.OrderMapper;
import com.university.vrclassroombackend.module.order.mapper.OrderSeatMapper;
import com.university.vrclassroombackend.module.order.model.Order;
import com.university.vrclassroombackend.module.order.model.OrderSeat;
import com.university.vrclassroombackend.module.space.mapper.SeatMapper;
import com.university.vrclassroombackend.module.space.model.Seat;
import com.university.vrclassroombackend.util.RedisDistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderTimeoutTask {

    private static final Logger logger = LoggerFactory.getLogger(OrderTimeoutTask.class);
    private static final String ORDER_TIMEOUT_LOCK = "order:timeout:lock";
    private static final long LOCK_EXPIRE_TIME = 60;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisDistributedLock distributedLock;

    @Scheduled(fixedRate = 60000)
    public void handleTimeoutOrders() {
        if (!distributedLock.tryLock(ORDER_TIMEOUT_LOCK, LOCK_EXPIRE_TIME, java.util.concurrent.TimeUnit.SECONDS)) {
            logger.debug("订单超时处理任务正在执行，跳过本次执行");
            return;
        }

        try {
            LocalDateTime now = LocalDateTime.now();

            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getStatus, "PENDING")
                       .lt(Order::getExpiresAt, now);
            List<Order> timeoutOrders = orderMapper.selectList(queryWrapper);

            for (Order order : timeoutOrders) {
                handleSingleTimeoutOrder(order);
            }

            if (!timeoutOrders.isEmpty()) {
                logger.info("处理超时订单完成，共处理 {} 个订单", timeoutOrders.size());
            }

        } finally {
            distributedLock.unlock(ORDER_TIMEOUT_LOCK);
        }
    }

    private void handleSingleTimeoutOrder(Order order) {
        String orderLockKey = "order:timeout:" + order.getId();
        if (!distributedLock.tryLock(orderLockKey, 30, java.util.concurrent.TimeUnit.SECONDS)) {
            logger.warn("订单正在处理中，跳过: orderId={}", order.getId());
            return;
        }

        try {
            logger.debug("订单超时开始自动取消: order={}", order);

            Order freshOrder = orderMapper.selectById(order.getId());
            if (freshOrder == null || !"PENDING".equals(freshOrder.getStatus())) {
                return;
            }

            freshOrder.setStatus("CANCELLED");
            freshOrder.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(freshOrder);

            LambdaQueryWrapper<OrderSeat> orderSeatQueryWrapper = new LambdaQueryWrapper<>();
            orderSeatQueryWrapper.eq(OrderSeat::getOrderId, freshOrder.getId());
            List<OrderSeat> orderSeatList = orderSeatMapper.selectList(orderSeatQueryWrapper);

            for (OrderSeat orderSeat : orderSeatList) {
                Seat seat = seatMapper.selectById(orderSeat.getSeatId());
                logger.debug("订单超时处理座位: orderSeat={}, seat={}", orderSeat, seat);
                if (seat != null && seat.getStatus() == 2) {
                    seat.setStatus(1);
                    // 移除手动版本号修改 ，让 MyBatis-Plus 自动处理
                    // seat.setVersion(seat.getVersion() + 1);
                    seatMapper.updateById(seat);

                    invalidateRoomSeatsCache(seat.getRoomId());
                }
                logger.debug("订单超时处理座位完成: seat={}", seat);
                // Seat checkedSeat = seatMapper.selectById(seat.getId());
                // logger.debug("订单超时检查座位状态: checkedSeat={}", checkedSeat);
            }

            try {
                String cacheKey = "order:" + freshOrder.getId();
                logger.debug("订单超时删除缓存: cacheKey={}", cacheKey);
                redisTemplate.delete(cacheKey);
            } catch (Exception e) {
                logger.warn("Redis缓存删除失败: {}", e.getMessage());
            }

            logger.info("订单超时自动取消: orderId={}", freshOrder.getId());

        } finally {
            distributedLock.unlock(orderLockKey);
        }
    }

    private void invalidateRoomSeatsCache(Integer roomId) {
        try {
            String cacheKey = "room:seats:" + roomId;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            logger.warn("Redis缓存删除失败: {}", e.getMessage());
        }
    }
}
