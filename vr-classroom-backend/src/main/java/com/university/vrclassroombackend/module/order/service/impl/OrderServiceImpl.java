package com.university.vrclassroombackend.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.exception.BusinessException;
import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.SeatLockDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.mapper.OrderMapper;
import com.university.vrclassroombackend.module.order.mapper.OrderSeatMapper;
import com.university.vrclassroombackend.module.order.model.Order;
import com.university.vrclassroombackend.module.order.model.OrderSeat;
import com.university.vrclassroombackend.module.order.service.OrderService;
import com.university.vrclassroombackend.module.order.vo.OrderListVO;
import com.university.vrclassroombackend.module.order.vo.OrderSeatVO;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;
import com.university.vrclassroombackend.module.order.vo.SeatVO;
import com.university.vrclassroombackend.module.space.mapper.ClassRoomMapper;
import com.university.vrclassroombackend.module.space.mapper.SeatMapper;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.Seat;
import com.university.vrclassroombackend.util.RedisDistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int ORDER_EXPIRE_MINUTES = 10;
    private static final String ORDER_CACHE_PREFIX = "order:";
    private static final String SEAT_LOCK_PREFIX = "seat:lock:";
    private static final long LOCK_EXPIRE_TIME = 30;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderSeatMapper orderSeatMapper;

    @Autowired
    private ClassRoomMapper classRoomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisDistributedLock distributedLock;

    @Override
    public RoomSeatVO getRoomSeats(String roomId) {
        Integer roomIdInt = Integer.parseInt(roomId);
        
        String cacheKey = "room:seats:" + roomId;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof RoomSeatVO) {
                return (RoomSeatVO) cached;
            }
        } catch (Exception e) {
            logger.warn("Redis缓存读取失败: {}", e.getMessage());
        }

        ClassRoom classRoom = classRoomMapper.selectById(roomIdInt);
        if (classRoom == null) {
            throw new BusinessException(404, "教室不存在");
        }

        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getRoomId, roomIdInt);
        List<Seat> seatList = seatMapper.selectList(queryWrapper);

        RoomSeatVO roomSeatVO = new RoomSeatVO();
        roomSeatVO.setTotalRows(classRoom.getTotalRows());
        roomSeatVO.setTotalCols(classRoom.getTotalCols());

        List<SeatVO> seatVOList = new ArrayList<>();
        for (Seat seat : seatList) {
            SeatVO seatVO = new SeatVO();
            seatVO.setId(seat.getId().toString());
            seatVO.setRow(seat.getRow());
            seatVO.setCol(seat.getCol());
            seatVO.setPrice(seat.getPrice());
            seatVO.setStatus(seat.getStatus());
            seatVO.setVersion(seat.getVersion());
            seatVOList.add(seatVO);
        }
        roomSeatVO.setSeats(seatVOList);

        try {
            redisTemplate.opsForValue().set(cacheKey, roomSeatVO, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.warn("Redis缓存写入失败: {}", e.getMessage());
        }

        return roomSeatVO;
    }

    @Override
    @Transactional
    public OrderVO createOrder(Integer userId, CreateOrderDTO createOrderDTO) {
        List<SeatLockDTO> seatList = createOrderDTO.getSeatList();
        if (seatList == null || seatList.isEmpty()) {
            throw new BusinessException(400, "请选择座位");
        }

        List<String> lockKeys = new ArrayList<>();
        List<Seat> seatsToLock = new ArrayList<>();
        int totalAmount = 0;

        try {
            for (SeatLockDTO seatLockDTO : seatList) {
                String lockKey = SEAT_LOCK_PREFIX + seatLockDTO.getId();
                if (!distributedLock.tryLock(lockKey, LOCK_EXPIRE_TIME, TimeUnit.SECONDS)) {
                    throw new BusinessException(409, "座位正在被其他人选择，请稍后重试");
                }
                lockKeys.add(lockKey);
            }

            for (SeatLockDTO seatLockDTO : seatList) {
                Integer seatId = Integer.parseInt(seatLockDTO.getId());
                Seat seat = seatMapper.selectById(seatId);

                if (seat == null) {
                    throw new BusinessException(404, "座位不存在: " + seatLockDTO.getId());
                }
                // 座位状态: 0-过道/不可用, 1-可选, 2-锁定, 3-已购买
                if (seat.getStatus() == 0) {
                    throw new BusinessException(400, "该座位为过道，不可选择");
                }
                if (seat.getStatus() != 1) {
                    throw new BusinessException(409, "座位已被占用");
                }
                if (!seat.getVersion().equals(seatLockDTO.getVersion())) {
                    throw new BusinessException(409, "座位状态已更新，请刷新页面重新选择");
                }

                seatsToLock.add(seat);
                totalAmount += 10000;
            }

            for (Seat seat : seatsToLock) {
                int updated = updateSeatStatusWithOptimisticLock(seat, 2);
                if (updated == 0) {
                    throw new BusinessException(409, "座位锁定失败，请刷新页面重新选择");
                }
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setCampusId(createOrderDTO.getCampusId());
            order.setBuildingId(createOrderDTO.getBuildingId());
            order.setRoomId(createOrderDTO.getRoomId());
            order.setAmount(totalAmount);
            order.setStatus("PENDING");
            order.setExpiresAt(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES));
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.insert(order);

            for (SeatLockDTO seatLockDTO : seatList) {
                OrderSeat orderSeat = new OrderSeat();
                orderSeat.setOrderId(order.getId());
                orderSeat.setSeatId(Integer.parseInt(seatLockDTO.getId()));
                orderSeat.setLookPrice(new java.math.BigDecimal("100.00"));
                orderSeatMapper.insert(orderSeat);
            }

            OrderVO orderVO = convertToOrderVO(order);

            try {
                String cacheKey = ORDER_CACHE_PREFIX + order.getId();
                redisTemplate.opsForValue().set(cacheKey, order, ORDER_EXPIRE_MINUTES, TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.warn("Redis缓存写入失败: {}", e.getMessage());
            }

            invalidateRoomSeatsCache(seatsToLock.get(0).getRoomId());

            logger.info("订单创建成功: orderId={}, userId={}, amount={}", order.getId(), userId, totalAmount);

            return orderVO;

        } finally {
            for (String lockKey : lockKeys) {
                distributedLock.unlock(lockKey);
            }
        }
    }

    @Override
    @Transactional
    public void updateOrder(String orderId, UpdateOrderDTO updateOrderDTO) {
        Long orderIdLong = Long.parseLong(orderId);
        
        String lockKey = "order:update:" + orderId;
        if (!distributedLock.tryLock(lockKey, LOCK_EXPIRE_TIME, TimeUnit.SECONDS)) {
            throw new BusinessException(409, "订单正在处理中，请稍后重试");
        }

        try {
            Order order = orderMapper.selectById(orderIdLong);
            if (order == null) {
                throw new BusinessException(404, "订单不存在");
            }

            if (!"CANCELLED".equals(updateOrderDTO.getStatus())) {
                throw new BusinessException(400, "只能取消订单");
            }

            if (!"PENDING".equals(order.getStatus())) {
                throw new BusinessException(400, "订单状态不允许取消");
            }

            order.setStatus("CANCELLED");
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);

            LambdaQueryWrapper<OrderSeat> orderSeatQueryWrapper = new LambdaQueryWrapper<>();
            orderSeatQueryWrapper.eq(OrderSeat::getOrderId, orderIdLong);
            List<OrderSeat> orderSeatList = orderSeatMapper.selectList(orderSeatQueryWrapper);

            for (OrderSeat orderSeat : orderSeatList) {
                Seat seat = seatMapper.selectById(orderSeat.getSeatId());
                if (seat != null && seat.getStatus() == 2) {
                    int updated = updateSeatStatusWithOptimisticLock(seat, 1);
                    if (updated > 0) {
                        invalidateRoomSeatsCache(seat.getRoomId());
                    }
                }
            }

            try {
                String cacheKey = ORDER_CACHE_PREFIX + orderId;
                redisTemplate.delete(cacheKey);
            } catch (Exception e) {
                logger.warn("Redis缓存删除失败: {}", e.getMessage());
            }

            logger.info("订单取消成功: orderId={}", orderId);

        } finally {
            distributedLock.unlock(lockKey);
        }
    }

    @Override
    @Async
    @Transactional
    public void mockPayNotify(String orderId) {
        Long orderIdLong = Long.parseLong(orderId);
        
        String lockKey = "order:pay:" + orderId;
        if (!distributedLock.tryLock(lockKey, LOCK_EXPIRE_TIME, TimeUnit.SECONDS)) {
            logger.warn("订单支付处理中，请勿重复提交: orderId={}", orderId);
            return;
        }

        try {
            Order order = orderMapper.selectById(orderIdLong);
            if (order == null) {
                logger.error("订单不存在: orderId={}", orderId);
                return;
            }

            if (!"PENDING".equals(order.getStatus())) {
                logger.warn("订单状态不允许支付: orderId={}, status={}", orderId, order.getStatus());
                return;
            }

            if (order.getExpiresAt().isBefore(LocalDateTime.now())) {
                logger.warn("订单已过期: orderId={}, expiresAt={}", orderId, order.getExpiresAt());
                handleOrderTimeout(orderIdLong);
                return;
            }

            order.setStatus("PAID");
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);

            LambdaQueryWrapper<OrderSeat> orderSeatQueryWrapper = new LambdaQueryWrapper<>();
            orderSeatQueryWrapper.eq(OrderSeat::getOrderId, orderIdLong);
            List<OrderSeat> orderSeatList = orderSeatMapper.selectList(orderSeatQueryWrapper);

            for (OrderSeat orderSeat : orderSeatList) {
                Seat seat = seatMapper.selectById(orderSeat.getSeatId());
                if (seat != null && seat.getStatus() == 2) {
                    int updated = updateSeatStatusWithOptimisticLock(seat, 3);
                    if (updated > 0) {
                        invalidateRoomSeatsCache(seat.getRoomId());
                    }
                }
            }

            try {
                String cacheKey = ORDER_CACHE_PREFIX + orderId;
                redisTemplate.delete(cacheKey);
            } catch (Exception e) {
                logger.warn("Redis缓存删除失败: {}", e.getMessage());
            }

            logger.info("订单支付成功: orderId={}", orderId);

        } finally {
            distributedLock.unlock(lockKey);
        }
    }

    private int updateSeatStatusWithOptimisticLock(Seat seat, int newStatus) {
        seat.setStatus(newStatus);
        // 不要手动增加version，MyBatis-Plus乐观锁插件会自动处理
        return seatMapper.updateById(seat);
    }

    private void invalidateRoomSeatsCache(Integer roomId) {
        try {
            String cacheKey = "room:seats:" + roomId;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            logger.warn("Redis缓存删除失败: {}", e.getMessage());
        }
    }

    private void handleOrderTimeout(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && "PENDING".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);

            LambdaQueryWrapper<OrderSeat> orderSeatQueryWrapper = new LambdaQueryWrapper<>();
            orderSeatQueryWrapper.eq(OrderSeat::getOrderId, orderId);
            List<OrderSeat> orderSeatList = orderSeatMapper.selectList(orderSeatQueryWrapper);

            for (OrderSeat orderSeat : orderSeatList) {
                Seat seat = seatMapper.selectById(orderSeat.getSeatId());
                if (seat != null && seat.getStatus() == 2) {
                    updateSeatStatusWithOptimisticLock(seat, 1);
                    invalidateRoomSeatsCache(seat.getRoomId());
                }
            }

            logger.info("订单超时自动取消: orderId={}", orderId);
        }
    }

    private OrderVO convertToOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(order.getId().toString());
        return orderVO;
    }

    @Override
    public List<OrderListVO> getOrderList(Integer userId, Integer page, Integer size) {
        // 设置默认分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100; // 限制最大分页大小
        }

        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                   .orderByDesc(Order::getCreatedAt);
        
        Page<Order> orderPage = orderMapper.selectPage(pageParam, queryWrapper);
        List<Order> orders = orderPage.getRecords();
        
        List<OrderListVO> result = new ArrayList<>();
        for (Order order : orders) {
            OrderListVO vo = convertToOrderListVO(order);
            result.add(vo);
        }
        
        return result;
    }

    private OrderListVO convertToOrderListVO(Order order) {
        OrderListVO vo = new OrderListVO();
        vo.setId(order.getId());
        vo.setCampusId(order.getCampusId());
        vo.setBuildingId(order.getBuildingId());
        vo.setRoomId(order.getRoomId());
        vo.setAmount(order.getAmount());
        vo.setStatus(order.getStatus());
        vo.setExpiresAt(order.getExpiresAt() != null ? order.getExpiresAt().format(DATE_TIME_FORMATTER) : null);
        vo.setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_TIME_FORMATTER) : null);
        vo.setUpdatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt().format(DATE_TIME_FORMATTER) : null);
        
        LambdaQueryWrapper<OrderSeat> orderSeatQuery = new LambdaQueryWrapper<>();
        orderSeatQuery.eq(OrderSeat::getOrderId, order.getId());
        List<OrderSeat> orderSeats = orderSeatMapper.selectList(orderSeatQuery);
        
        List<OrderSeatVO> seatList = new ArrayList<>();
        for (OrderSeat orderSeat : orderSeats) {
            Seat seat = seatMapper.selectById(orderSeat.getSeatId());
            if (seat != null) {
                OrderSeatVO seatVO = new OrderSeatVO();
                seatVO.setId(seat.getId());
                seatVO.setRow(seat.getRow());
                seatVO.setCol(seat.getCol());
                if (orderSeat.getLookPrice() != null) {
                    seatVO.setLookPrice(orderSeat.getLookPrice().toString());
                } else {
                    seatVO.setLookPrice("100.00");
                }
                seatList.add(seatVO);
            }
        }
        vo.setSeatList(seatList);
        
        return vo;
    }
}
