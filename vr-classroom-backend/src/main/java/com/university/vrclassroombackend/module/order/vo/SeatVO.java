package com.university.vrclassroombackend.module.order.vo;

import lombok.Data;

@Data
public class SeatVO {
    private String id;
    private Integer row;
    private Integer col;
    private Integer status;
    private Integer version;
}
