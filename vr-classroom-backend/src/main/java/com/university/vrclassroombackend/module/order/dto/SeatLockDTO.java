package com.university.vrclassroombackend.module.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "座位锁定参数")
public class SeatLockDTO {
    @NotBlank(message = "座位ID不能为空")
    @Schema(description = "座位ID", example = "1")
    private String id;
    
    @Schema(description = "座位价格", example = "5")
    private Integer price;
    
    @NotNull(message = "座位版本号不能为空")
    @Schema(description = "座位版本号", example = "1")
    private Integer version;
}
