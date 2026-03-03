package com.university.vrclassroombackend.module.order.vo;

import lombok.Data;

@Data
public class OrderSeatVO {
    private Integer id;
    private Integer row;
    private Integer col;
    private String lookPrice;
}
