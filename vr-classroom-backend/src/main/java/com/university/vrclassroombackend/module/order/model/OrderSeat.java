package com.university.vrclassroombackend.module.order.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("order_seat")
@Data
public class OrderSeat {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("seat_id")
    private Integer seatId;
}
