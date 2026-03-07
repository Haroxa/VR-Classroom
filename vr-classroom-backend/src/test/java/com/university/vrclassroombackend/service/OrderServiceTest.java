package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.exception.BusinessException;
import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.SeatLockDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.mapper.OrderMapper;
import com.university.vrclassroombackend.module.order.mapper.OrderSeatMapper;
import com.university.vrclassroombackend.module.order.model.Order;
import com.university.vrclassroombackend.module.order.model.OrderSeat;
import com.university.vrclassroombackend.module.order.service.impl.OrderServiceImpl;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;
import com.university.vrclassroombackend.module.space.mapper.ClassRoomMapper;
import com.university.vrclassroombackend.module.space.mapper.SeatMapper;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.Seat;
import com.university.vrclassroombackend.util.RedisDistributedLock;
import com.university.vrclassroombackend.util.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderSeatMapper orderSeatMapper;

    @Mock
    private SeatMapper seatMapper;

    @Mock
    private ClassRoomMapper classRoomMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisDistributedLock redisDistributedLock;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private Seat testSeat;
    private ClassRoom testClassRoom;
    private CreateOrderDTO createOrderDTO;
    private UpdateOrderDTO updateOrderDTO;

    @BeforeEach
    void setUp() {
        testClassRoom = new ClassRoom();
        testClassRoom.setId(1);
        testClassRoom.setBuildingId(1);
        testClassRoom.setRoomNumber("101");
        testClassRoom.setTotalRows(5);
        testClassRoom.setTotalCols(10);

        // 座位状态：0-过道，1-可选，2-已锁定，3-已售出
        testSeat = new Seat();
        testSeat.setId(1);
        testSeat.setRoomId(1);
        testSeat.setRow(1);
        testSeat.setCol(1);
        testSeat.setStatus(1); // 可选状态
        testSeat.setVersion(1);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1);
        testOrder.setRoomId(1);
        testOrder.setStatus("PENDING");
        testOrder.setAmount(100);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setCampusId(1);
        createOrderDTO.setBuildingId(1);
        createOrderDTO.setRoomId(1);

        SeatLockDTO seatLockDTO = new SeatLockDTO();
        seatLockDTO.setId("1");
        seatLockDTO.setVersion(1);

        List<SeatLockDTO> seatList = new ArrayList<>();
        seatList.add(seatLockDTO);
        createOrderDTO.setSeatList(seatList);

        updateOrderDTO = new UpdateOrderDTO();
        updateOrderDTO.setStatus("CANCELLED");

        // 注入seatPrice配置值
        ReflectionTestUtils.setField(orderService, "seatPrice", 100000);
    }

    @Test
    void testGetRoomSeats() {
        when(classRoomMapper.selectById(1)).thenReturn(testClassRoom);
        when(seatMapper.selectList(any())).thenReturn(List.of(testSeat));

        RoomSeatVO result = orderService.getRoomSeats("1");

        assertNotNull(result);
        verify(classRoomMapper, times(1)).selectById(1);
        verify(seatMapper, times(1)).selectList(any());
    }

    @Test
    void testCreateOrderSuccess() {
        when(classRoomMapper.selectById(1)).thenReturn(testClassRoom);
        when(seatMapper.selectById(1)).thenReturn(testSeat);
        when(redisDistributedLock.tryLock(anyString(), anyLong(), any())).thenReturn(true);
        // 使用lenient避免UnnecessaryStubbingException，因为snowflakeIdGenerator通过构造函数注入
        lenient().when(snowflakeIdGenerator.nextId()).thenReturn(1L);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderSeatMapper.insert(any(OrderSeat.class))).thenReturn(1);
        // updateSeatStatusWithOptimisticLock使用update方法
        when(seatMapper.update(any(Seat.class), any())).thenReturn(1);

        OrderVO result = orderService.createOrder(1, createOrderDTO);

        assertNotNull(result);
        verify(orderMapper, times(1)).insert(any(Order.class));
        verify(orderSeatMapper, times(1)).insert(any(OrderSeat.class));
    }

    @Test
    void testCreateOrderSeatOccupied() {
        // 座位状态2表示已锁定，不是可选状态
        testSeat.setStatus(2);
        when(classRoomMapper.selectById(1)).thenReturn(testClassRoom);
        when(seatMapper.selectById(1)).thenReturn(testSeat);
        // 使用lenient避免UnnecessaryStubbingException
        lenient().when(redisDistributedLock.tryLock(anyString(), anyLong(), any())).thenReturn(true);

        assertThrows(BusinessException.class, () -> orderService.createOrder(1, createOrderDTO));
    }

    @Test
    void testUpdateOrderSuccess() {
        // 设置座位状态为已锁定(LOCKED=2)，这样releaseOrderSeats才会更新座位
        testSeat.setStatus(2);
        OrderSeat orderSeat = new OrderSeat();
        orderSeat.setSeatId(1);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderSeatMapper.selectList(any())).thenReturn(List.of(orderSeat));
        when(seatMapper.selectById(1)).thenReturn(testSeat);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        // updateSeatStatusWithOptimisticLock使用update方法，不是updateById
        when(seatMapper.update(any(Seat.class), any())).thenReturn(1);

        orderService.updateOrder("1", updateOrderDTO);

        verify(orderMapper, times(1)).updateById(any(Order.class));
        verify(seatMapper, times(1)).update(any(Seat.class), any());
    }

    @Test
    void testUpdateOrderNotFound() {
        when(orderMapper.selectById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> orderService.updateOrder("1", updateOrderDTO));
    }

    @Test
    void testMockPayNotifySuccess() {
        // 设置座位状态为已锁定(LOCKED=2)，这样confirmOrderSeats才会更新座位
        testSeat.setStatus(2);
        OrderSeat orderSeat = new OrderSeat();
        orderSeat.setSeatId(1);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderSeatMapper.selectList(any())).thenReturn(List.of(orderSeat));
        when(seatMapper.selectById(1)).thenReturn(testSeat);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        // updateSeatStatusWithOptimisticLock使用update方法，不是updateById
        when(seatMapper.update(any(Seat.class), any())).thenReturn(1);

        orderService.mockPayNotify("1");

        verify(orderMapper, times(1)).updateById(any(Order.class));
        verify(seatMapper, times(1)).update(any(Seat.class), any());
    }

    @Test
    void testMockPayNotifyOrderNotFound() {
        when(orderMapper.selectById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> orderService.mockPayNotify("1"));
    }
}
