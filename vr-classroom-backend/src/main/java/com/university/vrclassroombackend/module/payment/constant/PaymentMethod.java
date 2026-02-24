package com.university.vrclassroombackend.module.payment.constant;

/**
 * 支付方式枚举类
 * 用于表示支持的支付方式
 */
public enum PaymentMethod {
    ALIPAY("ALIPAY", "支付宝"),
    WECHAT_PAY("WECHAT_PAY", "微信支付"),
    UNION_PAY("UNION_PAY", "银联支付");
    
    private final String code;
    private final String name;
    
    PaymentMethod(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * 根据支付方式代码获取支付方式枚举
     * @param code 支付方式代码
     * @return 支付方式枚举
     */
    public static PaymentMethod getByCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        return null;
    }
}
