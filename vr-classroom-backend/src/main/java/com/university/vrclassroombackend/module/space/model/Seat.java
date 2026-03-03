package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("seat")
@Data
public class Seat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("room_id")
    private Integer roomId;
    
    @TableField("`row`")
    private Integer row;
    
    @TableField("`col`")
    private Integer col;
    
    @TableField
    private Integer price = 10000;

    /**
     * 座位状态
     * 0 - 过道/不可用
     * 1 - 可选
     * 2 - 锁定
     * 3 - 已购买
     */
    @TableField
    private Integer status = 1;
    
    @Version
    @TableField
    private Integer version = 0;
    
    @TableField("donor_id")
    private Integer donorId;
    
    @TableField("claimed_at")
    private LocalDateTime claimedAt;
    
    @TableField("reserved_at")
    private LocalDateTime reservedAt;
    
    @TableField("reserve_expire_at")
    private LocalDateTime reserveExpireAt;
}
