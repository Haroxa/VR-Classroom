package com.university.vrclassroombackend.module.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "更新订单参数")
public class UpdateOrderDTO {
    @NotBlank(message = "订单状态不能为空")
    @Pattern(regexp = "CANCELLED|PAID", message = "订单状态只能是 CANCELLED 或 PAID")
    @Schema(description = "订单状态", example = "CANCELLED")
    private String status;
}
