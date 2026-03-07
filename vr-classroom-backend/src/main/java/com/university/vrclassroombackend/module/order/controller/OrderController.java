package com.university.vrclassroombackend.module.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.service.OrderService;
import com.university.vrclassroombackend.module.order.vo.OrderListVO;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/rooms/{roomId}/seats")
    public ResponseEntity<?> getRoomSeats(@PathVariable String roomId) {
        RoomSeatVO roomSeatVO = orderService.getRoomSeats(roomId);
        return ResponseEntity.ok().body(ApiResponse.success(roomSeatVO));
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(HttpServletRequest request,
                                         @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("创建订单失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }

        OrderVO orderVO = orderService.createOrder(userId, createOrderDTO);
        logger.info("订单创建成功: orderId={}, userId={}", orderVO.getOrderId(), userId);
        return ResponseEntity.ok().body(ApiResponse.success(orderVO));
    }

    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrder(HttpServletRequest request,
                                         @PathVariable String orderId,
                                         @Valid @RequestBody UpdateOrderDTO updateOrderDTO) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("取消订单失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }

        orderService.updateOrder(orderId, updateOrderDTO);
        logger.info("订单取消成功: orderId={}, userId={}", orderId, userId);
        return ResponseEntity.ok().body(ApiResponse.success(new HashMap<>()));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrder(HttpServletRequest request,
                                       @PathVariable String orderId) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("查询订单详情失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }

        OrderListVO orderListVO = orderService.getOrder(userId, orderId);
        logger.info("查询订单详情成功: orderId={}, userId={}", orderId, userId);
        return ResponseEntity.ok().body(ApiResponse.success(orderListVO));
    }

    @PostMapping("/mock/pay/notify")
    public ResponseEntity<?> mockPayNotify(@RequestBody Map<String, String> request) {
        String orderId = request.get("orderId");
        if (orderId == null || orderId.isEmpty()) {
            logger.warn("模拟支付回调失败: 缺少 orderId 参数");
            return ResponseEntity.status(AppConstants.HttpStatusCode.BAD_REQUEST)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, "缺少 orderId 参数"));
        }

        orderService.mockPayNotify(orderId);
        logger.info("模拟支付回调成功: orderId={}", orderId);
        return ResponseEntity.ok().body(ApiResponse.success(new HashMap<>()));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrderList(HttpServletRequest request,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("查询订单列表失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }

        IPage<OrderListVO> orderPage = orderService.getOrderList(userId, page, size);
        Map<String, Object> data = new HashMap<>();
        data.put("current", orderPage.getCurrent());
        data.put("total", orderPage.getPages());
        data.put("records", orderPage.getRecords());
        logger.info("查询订单列表成功: userId={}, page={}, size={}, total={}", userId, page, size, orderPage.getTotal());
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }
}
