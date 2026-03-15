package com.university.vrclassroombackend.module.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "模拟支付回调请求参数")
public class MockPayNotifyDTO {
    @Schema(description = "订单ID", required = true, example = "1")
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
