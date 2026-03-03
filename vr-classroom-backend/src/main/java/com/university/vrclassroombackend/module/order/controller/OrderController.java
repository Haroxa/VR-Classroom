package com.university.vrclassroombackend.module.order.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.service.OrderService;
import com.university.vrclassroombackend.module.order.vo.OrderListVO;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;
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
        try {
            RoomSeatVO roomSeatVO = orderService.getRoomSeats(roomId);
            return ResponseEntity.ok().body(ApiResponse.success(roomSeatVO));
        } catch (Exception e) {
            logger.error("获取教室座位图失败: roomId={}", roomId, e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(jakarta.servlet.http.HttpServletRequest request,
                                         @RequestBody CreateOrderDTO createOrderDTO) {
        try {
            Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("创建订单失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                        .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }

            OrderVO orderVO = orderService.createOrder(userId, createOrderDTO);
            logger.info("订单创建成功: orderId={}, userId={}", orderVO.getOrderId(), userId);
            return ResponseEntity.ok().body(ApiResponse.success(orderVO));
        } catch (Exception e) {
            logger.error("创建订单失败", e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrder(jakarta.servlet.http.HttpServletRequest request,
                                         @PathVariable String orderId,
                                         @RequestBody UpdateOrderDTO updateOrderDTO) {
        try {
            Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("取消订单失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                        .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }

            orderService.updateOrder(orderId, updateOrderDTO);
            logger.info("订单取消成功: orderId={}, userId={}", orderId, userId);
            return ResponseEntity.ok().body(ApiResponse.success(new HashMap<>()));
        } catch (Exception e) {
            logger.error("取消订单失败: orderId={}", orderId, e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    @PostMapping("/mock/pay/notify")
    public ResponseEntity<?> mockPayNotify(@RequestBody Map<String, String> request) {
        try {
            String orderId = request.get("orderId");
            if (orderId == null || orderId.isEmpty()) {
                logger.warn("模拟支付回调失败: 缺少 orderId 参数");
                return ResponseEntity.status(AppConstants.HttpStatusCode.BAD_REQUEST)
                        .body(ApiResponse.error(AppConstants.HttpStatusCode.BAD_REQUEST, "缺少 orderId 参数"));
            }

            orderService.mockPayNotify(orderId);
            logger.info("模拟支付回调成功: orderId={}", orderId);
            return ResponseEntity.ok().body(ApiResponse.success(new HashMap<>()));
        } catch (Exception e) {
            logger.error("模拟支付回调失败", e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrderList(jakarta.servlet.http.HttpServletRequest request,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size) {
        try {
            Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
            if (userId == null) {
                logger.warn("查询订单列表失败: 未认证");
                return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                        .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
            }

            List<OrderListVO> orderList = orderService.getOrderList(userId, page, size);
            logger.info("查询订单列表成功: userId={}, page={}, size={}, count={}", userId, page, size, orderList.size());
            return ResponseEntity.ok().body(ApiResponse.success(orderList));
        } catch (Exception e) {
            logger.error("查询订单列表失败", e);
            return ResponseEntity.status(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }
}
