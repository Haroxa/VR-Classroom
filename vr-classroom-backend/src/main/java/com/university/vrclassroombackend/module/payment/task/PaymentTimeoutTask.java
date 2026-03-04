package com.university.vrclassroombackend.module.payment.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.university.vrclassroombackend.module.donation.service.DonationService;
import com.university.vrclassroombackend.module.payment.constant.PaymentStatus;
import com.university.vrclassroombackend.module.payment.mapper.PaymentMapper;
import com.university.vrclassroombackend.module.payment.model.PaymentOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付超时处理任务
 * 用于处理超时的支付订单，将其标记为支付失败
 * @ConditionalOnProperty 仅在开发环境启用
 */
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "dev",
    matchIfMissing = false
)
@Component
public class PaymentTimeoutTask {
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private DonationService donationService;
    
    /**
     * 定时处理超时订单
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000) // 60秒 = 1分钟
    public void handleTimeoutOrders() {
        // 定义超时时间：30分钟
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        
        // 查询超时的待支付订单
        LambdaQueryWrapper<PaymentOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrder::getStatus, PaymentStatus.PENDING.getCode())
                   .lt(PaymentOrder::getCreatedAt, timeoutTime);
        List<PaymentOrder> timeoutOrders = paymentMapper.selectList(queryWrapper);
        
        // 处理每个超时订单
        for (PaymentOrder order : timeoutOrders) {
            // 更新订单状态为支付失败
            order.setStatus(PaymentStatus.FAILED.getCode());
            order.setFailedAt(LocalDateTime.now());
            paymentMapper.updateById(order);
            
            // 如果是捐赠订单，更新对应的捐赠订单状态
            if ("DONATION".equals(order.getProductType())) {
                try {
                    Integer donationId = Integer.parseInt(order.getProductId());
                    donationService.failDonation(donationId);
                } catch (NumberFormatException e) {
                    // 处理产品ID格式错误的情况
                    e.printStackTrace();
                }
            }
        }
    }
}
