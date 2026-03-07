package com.university.vrclassroombackend.module.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    @NotNull(message = "校区ID不能为空")
    private Integer campusId;
    
    @NotNull(message = "楼栋ID不能为空")
    private Integer buildingId;
    
    @NotNull(message = "教室ID不能为空")
    private Integer roomId;
    
    @NotEmpty(message = "请选择座位")
    @Valid
    private List<SeatLockDTO> seatList;
}
