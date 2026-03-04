package com.university.vrclassroombackend.module.order.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("`order`")
@Data
public class Order {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("userId")
    private Integer userId;

    @TableField("campusId")
    private Integer campusId;

    @TableField("buildingId")
    private Integer buildingId;

    @TableField("roomId")
    private Integer roomId;

    @TableField
    private Integer amount;

    @TableField
    private String status;

    @TableField("expiresAt")
    private LocalDateTime expiresAt;

    @TableField("createdAt")
    private LocalDateTime createdAt;

    @TableField("updatedAt")
    private LocalDateTime updatedAt;
}
