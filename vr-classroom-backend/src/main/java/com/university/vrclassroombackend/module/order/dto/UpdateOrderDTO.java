package com.university.vrclassroombackend.module.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateOrderDTO {
    @NotBlank(message = "订单状态不能为空")
    @Pattern(regexp = "CANCELLED|PAID", message = "订单状态只能是 CANCELLED 或 PAID")
    private String status;
}
