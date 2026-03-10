package com.university.vrclassroombackend.module.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "创建订单参数")
public class CreateOrderDTO {
    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID", example = "1")
    private Integer campusId;
    
    @NotNull(message = "楼栋ID不能为空")
    @Schema(description = "楼栋ID", example = "1")
    private Integer buildingId;
    
    @NotNull(message = "教室ID不能为空")
    @Schema(description = "教室ID", example = "1")
    private Integer roomId;
    
    @NotEmpty(message = "请选择座位")
    @Valid
    @Schema(description = "座位列表", example = "[{\"seatId\": 1, \"startTime\": \"2024-01-01 09:00:00\", \"endTime\": \"2024-01-01 10:00:00\"}]")
    private List<SeatLockDTO> seatList;
}
