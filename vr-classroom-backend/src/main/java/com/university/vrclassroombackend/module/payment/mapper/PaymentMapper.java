package com.university.vrclassroombackend.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.payment.model.PaymentOrder;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentMapper extends BaseMapper<PaymentOrder> {
    // 可以在这里添加自定义的查询方法
    PaymentOrder selectByOrderNo(String orderNo);
    List<PaymentOrder> selectByUserId(Integer userId);
    PaymentOrder selectByUserIdAndProductTypeAndProductIdAndStatusWithLock(Integer userId, String productType, String productId, Integer status);
    List<PaymentOrder> selectByStatusAndCreatedAtBefore(Integer status, LocalDateTime createdAt);
}
