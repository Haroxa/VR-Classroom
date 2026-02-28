package com.university.vrclassroombackend.module.donation.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("donation_order")
@Data
public class DonationOrder {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("donor_id")
    private Integer donorId;
    
    @TableField("seat_id")
    private Integer seatId;
    
    @TableField("tier_id")
    private Integer tierId;
    
    @TableField
    private BigDecimal amount;
    
    private String message;
    
    @TableField
    private Integer status = 0;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
    
    @TableField("paid_at")
    private LocalDateTime paidAt;
    
    @TableField("completed_at")
    private LocalDateTime completedAt;
    
    @TableField("cancelled_at")
    private LocalDateTime cancelledAt;
    
    @TableField("failed_at")
    private LocalDateTime failedAt;
    
    @Version
    private Integer version; // 乐观锁版本号
}




