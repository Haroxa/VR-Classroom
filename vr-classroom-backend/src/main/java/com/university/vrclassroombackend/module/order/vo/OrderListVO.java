package com.university.vrclassroombackend.module.order.vo;

import lombok.Data;
import java.util.List;

@Data
public class OrderListVO {
    private Long id;
    private Integer campusId;
    private Integer buildingId;
    private Integer roomId;
    private Integer amount;
    private String status;
    private String expiresAt;
    private String createdAt;
    private String updatedAt;
    private List<OrderSeatVO> seatList;
}
