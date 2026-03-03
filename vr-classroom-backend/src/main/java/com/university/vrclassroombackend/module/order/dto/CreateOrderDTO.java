package com.university.vrclassroombackend.module.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    private Integer campusId;
    private Integer buildingId;
    private Integer roomId;
    private List<SeatLockDTO> seatList;
}
