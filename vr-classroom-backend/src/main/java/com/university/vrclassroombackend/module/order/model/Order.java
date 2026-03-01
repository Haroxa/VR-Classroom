package com.university.vrclassroombackend.module.order.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("`order`")
@Data
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Integer userId;

    @TableField
    private Integer amount;

    @TableField
    private String status;

    @TableField("expires_at")
    private LocalDateTime expiresAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
