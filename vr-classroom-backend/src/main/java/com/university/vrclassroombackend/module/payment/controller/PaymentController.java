package com.university.vrclassroombackend.module.payment.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.payment.dto.CreatePaymentRequest;
import com.university.vrclassroombackend.module.payment.service.PaymentService;
import com.university.vrclassroombackend.module.payment.vo.PaymentOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付控制器
 * 用于处理支付相关的 HTTP 请求
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单
     * @param request 创建支付订单请求
     * @param httpRequest HTTP 请求对象，用于获取用户信息
     * @return 支付订单信息
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestBody CreatePaymentRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        // 从请求中获取用户ID，这里假设用户ID存储在请求属性中
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok().body(ApiResponse.error(401, "用户未登录"));
        }
        
        // 创建支付订单
        var paymentOrder = paymentService.createPayment(
                userId,
                request.getAmount().doubleValue(),
                request.getProductType(),
                request.getProductId(),
                request.getPaymentMethod(),
                request.getRemark()
        );
        
        // 转换为VO并返回
        PaymentOrderVO vo = paymentService.convertToPaymentOrderVO(paymentOrder);
        return ResponseEntity.ok().body(ApiResponse.success(vo));
    }

    /**
     * 获取支付订单列表
     * @param page 页码
     * @param httpRequest HTTP 请求对象，用于获取用户信息
     * @return 支付订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getPaymentOrders(
            @RequestParam(defaultValue = "0") Integer page,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        // 从请求中获取用户ID，这里假设用户ID存储在请求属性中
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok().body(ApiResponse.error(401, "用户未登录"));
        }
        
        // 获取支付订单列表
        var payments = paymentService.getPaymentsByUserId(userId);
        
        // 转换为VO列表并返回
        List<PaymentOrderVO> vos = payments.stream()
                .map(paymentService::convertToPaymentOrderVO)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(ApiResponse.success(vos));
    }

    /**
     * 获取支付订单详情
     * @param orderNo 订单号
     * @return 支付订单详情
     */
    @GetMapping("/orders/{orderNo}")
    public ResponseEntity<?> getPaymentOrder(@PathVariable String orderNo) {
        // 根据订单号获取支付订单
        var paymentOrder = paymentService.getPaymentByOrderNo(orderNo);
        if (paymentOrder == null) {
            return ResponseEntity.ok().body(ApiResponse.error(404, "订单不存在"));
        }
        
        // 转换为VO并返回
        PaymentOrderVO vo = paymentService.convertToPaymentOrderVO(paymentOrder);
        return ResponseEntity.ok().body(ApiResponse.success(vo));
    }

    /**
     * 取消支付订单
     * @param id 支付订单ID
     * @return 操作结果
     */
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable Integer id) {
        // 取消支付订单
        boolean success = paymentService.cancelPayment(id);
        if (!success) {
            return ResponseEntity.ok().body(ApiResponse.error(400, "取消订单失败"));
        }
        return ResponseEntity.ok().body(ApiResponse.success());
    }

    /**
     * 支付回调
     * @param orderNo 订单号
     * @param transactionId 交易ID
     * @param status 支付状态
     * @param sign 签名
     * @return 操作结果
     */
    @PostMapping("/callback")
    public ResponseEntity<?> paymentCallback(
            @RequestParam String orderNo,
            @RequestParam String transactionId,
            @RequestParam Integer status,
            @RequestParam String sign) {
        // 处理支付回调（异步处理，不等待结果）
        paymentService.handlePaymentCallback(orderNo, transactionId, status, sign);
        // 直接返回成功，因为回调是异步处理的
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}
