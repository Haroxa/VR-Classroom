package com.university.vrclassroombackend.module.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.annotation.RateLimiter;
import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.service.OrderService;
import com.university.vrclassroombackend.module.order.vo.OrderListVO;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping("/rooms/{roomId}/seats")
    @Operation(summary = "获取教室座位信息", description = "根据教室ID获取教室座位信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public ResponseEntity<?> getRoomSeats(@Parameter(description = "教室ID", required = true, example = "1") @PathVariable String roomId) {
        RoomSeatVO roomSeatVO = orderService.getRoomSeats(roomId);
        return ResponseEntity.ok().body(ApiResponse.success(roomSeatVO));
    }

    @PostMapping("/orders")
    @RateLimiter(limit = 10, timeout = 1)
    @Operation(summary = "创建订单", description = "创建新订单")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    public ResponseEntity<?> createOrder(HttpServletRequest request,
                                         @Parameter(description = "订单创建参数", required = true) @Valid @RequestBody CreateOrderDTO createOrderDTO) {
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
    @Operation(summary = "更新订单", description = "更新订单状态，如取消订单")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    public ResponseEntity<?> updateOrder(HttpServletRequest request,
                                         @Parameter(description = "订单ID", required = true, example = "1") @PathVariable String orderId,
                                         @Parameter(description = "订单更新参数", required = true) @Valid @RequestBody UpdateOrderDTO updateOrderDTO) {
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
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详情")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    public ResponseEntity<?> getOrder(HttpServletRequest request,
                                       @Parameter(description = "订单ID", required = true, example = "1") @PathVariable String orderId) {
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
    @Operation(summary = "模拟支付回调", description = "模拟支付成功后的回调通知")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求错误")
    public ResponseEntity<?> mockPayNotify(@Parameter(description = "回调参数", required = true, example = "{\"orderId\": \"1\"}") @RequestBody Map<String, String> request) {
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
    @Operation(summary = "获取订单列表", description = "获取当前用户的订单列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    public ResponseEntity<?> getOrderList(HttpServletRequest request,
                                            @Parameter(description = "页码，默认1", example = "1") @RequestParam(defaultValue = "1") Integer page,
                                            @Parameter(description = "每页大小，默认20", example = "20") @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("查询订单列表失败: 未认证");
            return ResponseEntity.status(AppConstants.HttpStatusCode.UNAUTHORIZED)
                    .body(ApiResponse.error(AppConstants.HttpStatusCode.UNAUTHORIZED, AppConstants.ErrorMessage.UNAUTHORIZED_USER));
        }

        IPage<OrderListVO> orderPage = orderService.getOrderList(userId, page, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("current", orderPage.getCurrent());
        data.put("total", orderPage.getPages());
        data.put("records", orderPage.getRecords());
        logger.info("查询订单列表成功: userId={}, page={}, pageSize={}, total={}", userId, page, pageSize, orderPage.getPages());
        return ResponseEntity.ok().body(ApiResponse.success(data));
    }
}
