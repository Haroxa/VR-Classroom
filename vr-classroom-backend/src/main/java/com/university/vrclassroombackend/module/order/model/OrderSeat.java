package com.university.vrclassroombackend.module.order.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@TableName("order_seat")
@Data
public class OrderSeat {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("orderId")
    private Long orderId;

    @TableField("seatId")
    private Integer seatId;

    @TableField("lookPrice")
    private java.math.BigDecimal lookPrice;
}
