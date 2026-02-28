package com.university.vrclassroombackend.module.payment.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体类
 * 用于记录用户的支付订单信息
 */
@TableName("payment_order")
@Data
public class PaymentOrder {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField
    private BigDecimal amount;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField
    private Integer status = 0; // 0: 待支付, 1: 已支付, 2: 已完成, 3: 已取消, 4: 支付失败
    
    @TableField("payment_method")
    private String paymentMethod; // 支付方式
    
    @TableField("transaction_id")
    private String transactionId; // 交易ID
    
    @TableField("product_type")
    private String productType; // 产品类型
    
    @TableField("product_id")
    private String productId; // 产品ID
    
    private String remark; // 备注
    
    @TableField(value = "created_at", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
    
    @TableField("paid_at")
    private LocalDateTime paidAt; // 支付时间
    
    @TableField("completed_at")
    private LocalDateTime completedAt; // 完成时间
    
    @TableField("cancelled_at")
    private LocalDateTime cancelledAt; // 取消时间
    
    @TableField("failed_at")
    private LocalDateTime failedAt; // 支付失败时间
    
    @Version
    private Integer version; // 乐观锁版本号
}
