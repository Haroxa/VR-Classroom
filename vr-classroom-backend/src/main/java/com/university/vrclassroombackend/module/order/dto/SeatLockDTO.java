package com.university.vrclassroombackend.module.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeatLockDTO {
    @NotBlank(message = "座位ID不能为空")
    private String id;
    
    private Integer price;
    
    @NotNull(message = "座位版本号不能为空")
    private Integer version;
}
