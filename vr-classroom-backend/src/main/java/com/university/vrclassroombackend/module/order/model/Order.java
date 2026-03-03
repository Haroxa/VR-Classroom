package com.university.vrclassroombackend.module.order.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("`order`")
@Data
public class Order {
    /**
     * 订单ID
     * 使用雪花算法生成，确保全局唯一
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Integer userId;

    @TableField("campus_id")
    private Integer campusId;

    @TableField("building_id")
    private Integer buildingId;

    @TableField("room_id")
    private Integer roomId;

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
