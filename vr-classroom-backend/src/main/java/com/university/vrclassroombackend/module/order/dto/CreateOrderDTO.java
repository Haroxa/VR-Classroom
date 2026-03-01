package com.university.vrclassroombackend.module.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    private List<SeatLockDTO> seatList;
}
