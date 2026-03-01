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
    private Integer status = 0;
    
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




