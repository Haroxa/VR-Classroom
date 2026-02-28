package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("seat")
@Data
public class Seat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("classroom_id")
    private Integer classroomId;
    
    @TableField("seat_row")
    private String seatRow;
    
    @TableField("seat_column")
    private String seatColumn;
    
    @TableField
    private Integer status = 0;
    
    @TableField("donor_id")
    private Integer donorId;
    
    @TableField("claimed_at")
    private LocalDateTime claimedAt;
    
    @TableField("reserved_at")
    private LocalDateTime reservedAt;
    
    @TableField("reserve_expire_at")
    private LocalDateTime reserveExpireAt;
}




