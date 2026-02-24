package com.university.vrclassroombackend.module.payment.dto;

import java.math.BigDecimal;

/**
 * 创建支付订单请求DTO
 * 用于接收创建支付订单的请求参数
 */
public class CreatePaymentRequest {
    private BigDecimal amount; // 支付金额
    private String productType; // 产品类型
    private String productId; // 产品ID
    private String paymentMethod; // 支付方式
    private String remark; // 备注

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
