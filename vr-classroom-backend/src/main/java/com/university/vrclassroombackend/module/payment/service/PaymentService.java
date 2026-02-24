package com.university.vrclassroombackend.module.payment.service;

import com.university.vrclassroombackend.module.payment.model.PaymentOrder;
import com.university.vrclassroombackend.module.payment.vo.PaymentOrderVO;

import java.util.List;

/**
 * 支付服务接口
 * 用于处理支付相关的业务逻辑
 */
public interface PaymentService {
    /**
     * 创建支付订单
     * @param userId 用户ID
     * @param amount 支付金额
     * @param productType 产品类型
     * @param productId 产品ID
     * @param paymentMethod 支付方式
     * @param remark 备注
     * @return 支付订单对象
     */
    PaymentOrder createPayment(Integer userId, Double amount, String productType, String productId, String paymentMethod, String remark);
    
    /**
     * 根据订单号获取支付订单
     * @param orderNo 订单号
     * @return 支付订单对象
     */
    PaymentOrder getPaymentByOrderNo(String orderNo);
    
    /**
     * 根据用户ID获取支付订单列表
     * @param userId 用户ID
     * @return 支付订单列表
     */
    List<PaymentOrder> getPaymentsByUserId(Integer userId);
    
    /**
     * 处理支付回调
     * @param orderNo 订单号
     * @param transactionId 交易ID
     * @param status 支付状态
     * @param sign 签名
     */
    void handlePaymentCallback(String orderNo, String transactionId, Integer status, String sign);
    
    /**
     * 取消支付订单
     * @param paymentId 支付订单ID
     * @return 是否取消成功
     */
    boolean cancelPayment(Integer paymentId);
    
    /**
     * 将支付订单转换为VO
     * @param paymentOrder 支付订单对象
     * @return 支付订单VO
     */
    PaymentOrderVO convertToPaymentOrderVO(PaymentOrder paymentOrder);
}
