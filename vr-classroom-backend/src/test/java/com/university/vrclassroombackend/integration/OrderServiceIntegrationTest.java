package com.university.vrclassroombackend.integration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.university.vrclassroombackend.exception.BusinessException;
import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.SeatLockDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.mapper.OrderMapper;
import com.university.vrclassroombackend.module.order.mapper.OrderSeatMapper;
import com.university.vrclassroombackend.module.order.model.Order;
import com.university.vrclassroombackend.module.order.model.OrderSeat;
import com.university.vrclassroombackend.module.order.service.OrderService;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;
import com.university.vrclassroombackend.module.space.mapper.ClassRoomMapper;
import com.university.vrclassroombackend.module.space.mapper.SeatMapper;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderService 集成测试
 * <p>
 * 测试订单服务的核心功能，包括：
 * 1. 获取教室座位信息
 * 2. 创建订单
 * 3. 更新订单状态
 * 4. 模拟支付回调
 * </p>
 *
 * @author VR Classroom Team
 * @since 1.0.0
 */
class OrderServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ClassRoomMapper classRoomMapper;

    private ClassRoom testClassRoom;
    private Seat testSeat;
    private Integer testUserId = 99999;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();

        // 清理测试数据
        orderSeatMapper.delete(new LambdaQueryWrapper<>());
        orderMapper.delete(new LambdaQueryWrapper<>());
        seatMapper.delete(new LambdaQueryWrapper<>());
        classRoomMapper.delete(new LambdaQueryWrapper<>());

        // 创建测试教室
        testClassRoom = new ClassRoom();
        testClassRoom.setBuildingId(1);
        testClassRoom.setRoomNumber("TEST-101");
        testClassRoom.setTotalRows(5);
        testClassRoom.setTotalCols(10);
        classRoomMapper.insert(testClassRoom);

        // 创建测试座位（状态1表示可选）
        testSeat = new Seat();
        testSeat.setRoomId(testClassRoom.getId());
        testSeat.setRow(1);
        testSeat.setCol(1);
        testSeat.setStatus(1);
        testSeat.setVersion(1);
        testSeat.setPrice(100000);
        seatMapper.insert(testSeat);
    }

    @Test
    void testGetRoomSeats() {
        RoomSeatVO result = orderService.getRoomSeats(String.valueOf(testClassRoom.getId()));

        assertNotNull(result);
        assertEquals(testClassRoom.getTotalRows(), result.getTotalRows());
        assertEquals(testClassRoom.getTotalCols(), result.getTotalCols());
        assertEquals(1, result.getSeats().size());
    }

    @Test
    void testCreateOrderSuccess() {
        // 从数据库获取最新的座位信息（包括正确的版本号）
        Seat freshSeat = seatMapper.selectById(testSeat.getId());
        CreateOrderDTO createOrderDTO = buildCreateOrderDTO(freshSeat);

        OrderVO result = orderService.createOrder(testUserId, createOrderDTO);

        assertNotNull(result);
        assertNotNull(result.getOrderId());

        // 验证订单已创建
        Long orderId = Long.parseLong(result.getOrderId());
        Order order = orderMapper.selectById(orderId);
        assertNotNull(order);
        assertEquals(testUserId, order.getUserId());
        assertEquals("PENDING", order.getStatus());

        // 验证订单座位关联已创建
        LambdaQueryWrapper<OrderSeat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderSeat::getOrderId, orderId);
        List<OrderSeat> orderSeats = orderSeatMapper.selectList(queryWrapper);
        assertEquals(1, orderSeats.size());
        assertEquals(testSeat.getId(), orderSeats.get(0).getSeatId());

        // 验证座位状态已更新为锁定(2)
        Seat updatedSeat = seatMapper.selectById(testSeat.getId());
        assertEquals(2, updatedSeat.getStatus());
    }

    @Test
    void testCreateOrderSeatOccupied() {
        // 将座位状态设置为已锁定
        testSeat.setStatus(2);
        seatMapper.updateById(testSeat);

        // 从数据库获取最新的座位信息
        Seat freshSeat = seatMapper.selectById(testSeat.getId());
        CreateOrderDTO createOrderDTO = buildCreateOrderDTO(freshSeat);

        assertThrows(BusinessException.class, () -> orderService.createOrder(testUserId, createOrderDTO));
    }

    @Test
    void testUpdateOrderSuccess() {
        // 先创建订单
        Seat freshSeat = seatMapper.selectById(testSeat.getId());
        CreateOrderDTO createOrderDTO = buildCreateOrderDTO(freshSeat);
        OrderVO orderVO = orderService.createOrder(testUserId, createOrderDTO);
        Long orderId = Long.parseLong(orderVO.getOrderId());

        // 更新订单状态为取消
        UpdateOrderDTO updateOrderDTO = new UpdateOrderDTO();
        updateOrderDTO.setStatus("CANCELLED");

        orderService.updateOrder(String.valueOf(orderId), updateOrderDTO);

        // 验证订单状态已更新
        Order updatedOrder = orderMapper.selectById(orderId);
        assertEquals("CANCELLED", updatedOrder.getStatus());

        // 验证座位状态已恢复为可选(1)
        Seat updatedSeat = seatMapper.selectById(testSeat.getId());
        assertEquals(1, updatedSeat.getStatus());
    }

    @Test
    void testUpdateOrderNotFound() {
        UpdateOrderDTO updateOrderDTO = new UpdateOrderDTO();
        updateOrderDTO.setStatus("CANCELLED");

        assertThrows(BusinessException.class, () -> orderService.updateOrder("999999", updateOrderDTO));
    }

    @Test
    void testMockPayNotifySuccess() {
        // 先创建订单
        Seat freshSeat = seatMapper.selectById(testSeat.getId());
        CreateOrderDTO createOrderDTO = buildCreateOrderDTO(freshSeat);
        OrderVO orderVO = orderService.createOrder(testUserId, createOrderDTO);
        Long orderId = Long.parseLong(orderVO.getOrderId());

        // 模拟支付回调
        orderService.mockPayNotify(String.valueOf(orderId));

        // 验证订单状态已更新为已支付
        Order updatedOrder = orderMapper.selectById(orderId);
        assertEquals("PAID", updatedOrder.getStatus());

        // 验证座位状态已更新为已售出(3)
        Seat updatedSeat = seatMapper.selectById(testSeat.getId());
        assertEquals(3, updatedSeat.getStatus());
    }

    @Test
    void testMockPayNotifyOrderNotFound() {
        assertThrows(BusinessException.class, () -> orderService.mockPayNotify("999999"));
    }

    @Test
    void testMockPayNotifyInvalidStatus() {
        // 先创建订单
        Seat freshSeat = seatMapper.selectById(testSeat.getId());
        CreateOrderDTO createOrderDTO = buildCreateOrderDTO(freshSeat);
        OrderVO orderVO = orderService.createOrder(testUserId, createOrderDTO);
        Long orderId = Long.parseLong(orderVO.getOrderId());

        // 将订单状态更新为已支付
        Order order = orderMapper.selectById(orderId);
        order.setStatus("PAID");
        orderMapper.updateById(order);

        // 再次支付应该失败
        assertThrows(BusinessException.class, () -> orderService.mockPayNotify(String.valueOf(orderId)));
    }

    /**
     * 构建创建订单DTO
     */
    private CreateOrderDTO buildCreateOrderDTO(Seat seat) {
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCampusId(1);
        createOrderDTO.setBuildingId(1);
        createOrderDTO.setRoomId(testClassRoom.getId());

        SeatLockDTO seatLockDTO = new SeatLockDTO();
        seatLockDTO.setId(String.valueOf(seat.getId()));
        seatLockDTO.setVersion(seat.getVersion());

        List<SeatLockDTO> seatList = new ArrayList<>();
        seatList.add(seatLockDTO);
        createOrderDTO.setSeatList(seatList);

        return createOrderDTO;
    }
}
