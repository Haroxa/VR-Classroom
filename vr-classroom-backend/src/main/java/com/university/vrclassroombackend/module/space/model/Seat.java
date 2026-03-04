package com.university.vrclassroombackend.module.space.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("seat")
@Data
public class Seat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("roomId")
    private Integer roomId;
    
    @TableField("`row`")
    private Integer row;
    
    @TableField("`col`")
    private Integer col;
    
    /**
     * 座位状态
     * 0=过道, 1=空闲(可买), 2=已锁定(待支付), 3=已售出
     */
    @TableField
    private Integer status = 1;
    
    @Version
    @TableField
    private Integer version = 0;
    
    /**
     * 价格，单位分
     */
    @TableField
    private Integer price = 100000;
}
