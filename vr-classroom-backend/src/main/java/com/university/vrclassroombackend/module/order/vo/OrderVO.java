package com.university.vrclassroombackend.module.order.vo;

import lombok.Data;

@Data
public class OrderVO {
    private String id;
    private Integer amount;
    private String status;
    private String expiresAt;
}
