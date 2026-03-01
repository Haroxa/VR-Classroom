package com.university.vrclassroombackend.module.order.vo;

import lombok.Data;
import java.util.List;

@Data
public class RoomSeatVO {
    private Integer totalRows;
    private Integer totalCols;
    private List<SeatVO> seats;
}
