package com.university.vrclassroombackend.module.payment.constant;

/**
 * 支付状态枚举类
 * 用于表示支付订单的状态
 */
public enum PaymentStatus {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消"),
    FAILED(4, "支付失败");
    
    private final Integer code;
    private final String message;
    
    PaymentStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * 根据状态码获取支付状态枚举
     * @param code 状态码
     * @return 支付状态枚举
     */
    public static PaymentStatus getByCode(Integer code) {
        for (PaymentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
