package com.university.vrclassroombackend.module.donation.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.donation.dto.CreateDonationRequest;
import com.university.vrclassroombackend.module.donation.service.DonationService;
import com.university.vrclassroombackend.module.payment.dto.CreatePaymentRequest;
import com.university.vrclassroombackend.module.payment.service.PaymentService;
import com.university.vrclassroombackend.module.payment.vo.PaymentOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 捐赠控制器
 * 用于处理捐赠相关的 HTTP 请求
 * 
 * @deprecated 捐赠功能已整合到订单模块，请使用订单模块相关接口
 */
@Deprecated
@RestController
@RequestMapping("/api/donation")
public class DonationController {
    
    @Autowired
    private DonationService donationService;
    
    @Autowired
    private PaymentService paymentService;

    /**
     * 创建捐赠订单
     * @param request 创建捐赠订单请求
     * @param httpRequest HTTP 请求对象，用于获取用户信息
     * @return 捐赠订单信息
     */
    @PostMapping("/create")
    public ResponseEntity<?> createDonation(
            @RequestBody CreateDonationRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        // 从请求中获取用户ID，这里假设用户ID存储在请求属性中
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok().body(ApiResponse.error(401, "用户未登录"));
        }
        
        // 创建捐赠订单
        var donationOrder = donationService.createDonation(
                userId,
                request.getSeatId(),
                request.getTierId(),
                request.getMessage()
        );
        
        // 创建支付订单
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setAmount(donationOrder.getAmount());
        paymentRequest.setProductType("DONATION");
        paymentRequest.setProductId(donationOrder.getId().toString());
        // 使用前端传入的支付方式，如果没有传入则默认使用微信支付
        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : "WECHAT";
        paymentRequest.setPaymentMethod(paymentMethod);
        paymentRequest.setRemark(donationOrder.getMessage());
        
        var paymentOrder = paymentService.createPayment(
                userId,
                donationOrder.getAmount().doubleValue(),
                "DONATION",
                donationOrder.getId().toString(),
                paymentMethod,
                donationOrder.getMessage()
        );
        
        // 转换为VO并返回
        PaymentOrderVO vo = paymentService.convertToPaymentOrderVO(paymentOrder);
        return ResponseEntity.ok().body(ApiResponse.success(vo));
    }

    /**
     * 获取捐赠订单列表
     * @param page 页码
     * @param httpRequest HTTP 请求对象，用于获取用户信息
     * @return 捐赠订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getDonationOrders(
            @RequestParam(defaultValue = "0") Integer page,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        // 从请求中获取用户ID，这里假设用户ID存储在请求属性中
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok().body(ApiResponse.error(401, "用户未登录"));
        }
        
        // 获取捐赠订单列表
        var donations = donationService.getDonationsByDonorId(userId);
        
        // 转换为VO列表并返回
        return ResponseEntity.ok().body(ApiResponse.success(donations));
    }

    /**
     * 获取捐赠订单详情
     * @param orderNo 订单号
     * @return 捐赠订单详情
     */
    @GetMapping("/orders/{orderNo}")
    public ResponseEntity<?> getDonationOrder(@PathVariable String orderNo) {
        // 根据订单号获取捐赠订单
        var donationOrder = donationService.getDonationByOrderNo(orderNo);
        if (donationOrder == null) {
            return ResponseEntity.ok().body(ApiResponse.error(404, "订单不存在"));
        }
        
        return ResponseEntity.ok().body(ApiResponse.success(donationOrder));
    }

    /**
     * 取消捐赠订单
     * @param id 捐赠订单ID
     * @return 操作结果
     */
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelDonation(@PathVariable Integer id) {
        // 取消捐赠订单
        boolean success = donationService.cancelDonation(id);
        if (!success) {
            return ResponseEntity.ok().body(ApiResponse.error(400, "取消订单失败"));
        }
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}




