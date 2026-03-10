package com.university.vrclassroombackend.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.exception.BusinessException;
import java.util.Collections;
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
import com.university.vrclassroombackend.util.SnowflakeIdGenerator;
import com.university.vrclassroombackend.aspect.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务实现类
 * <p>
 * 提供座位查询、订单创建、订单支付、订单超时处理等功能
 * 使用Redis分布式锁和乐观锁保证并发安全
 * </p>
 *
 * @author University VR Classroom Team
 * @since 1.0.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 订单过期时间（分钟）
     */
    private static final int ORDER_EXPIRE_MINUTES = 10;

    /**
     * 座位锁前缀
     */
    private static final String SEAT_LOCK_PREFIX = "seat:lock:";

    /**
     * 锁过期时间（秒）
     */
    private static final long LOCK_EXPIRE_TIME = 30;

    /**
     * 座位价格（单位：分）
     */
    @Value("${order.seat.price:100000}")
    private Integer seatPrice;

    /**
     * 订单数据访问对象
     */
    private final OrderMapper orderMapper;

    /**
     * 订单座位数据访问对象
     */
    private final OrderSeatMapper orderSeatMapper;

    /**
     * 教室数据访问对象
     */
    private final ClassRoomMapper classRoomMapper;

    /**
     * 座位数据访问对象
     */
    private final SeatMapper seatMapper;

    /**
     * Redis模板
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 分布式锁
     */
    private final RedisDistributedLock distributedLock;

    /**
     * 雪花ID生成器
     */
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 构造方法，注入依赖
     *
     * @param orderMapper          订单数据访问对象
     * @param orderSeatMapper      订单座位数据访问对象
     * @param classRoomMapper      教室数据访问对象
     * @param seatMapper           座位数据访问对象
     * @param redisTemplate        Redis模板
     * @param distributedLock      分布式锁
     * @param snowflakeIdGenerator 雪花ID生成器
     */
    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderSeatMapper orderSeatMapper,
                           ClassRoomMapper classRoomMapper, SeatMapper seatMapper,
                           RedisTemplate<String, Object> redisTemplate,
                           RedisDistributedLock distributedLock,
                           SnowflakeIdGenerator snowflakeIdGenerator) {
        this.orderMapper = orderMapper;
        this.orderSeatMapper = orderSeatMapper;
        this.classRoomMapper = classRoomMapper;
        this.seatMapper = seatMapper;
        this.redisTemplate = redisTemplate;
        this.distributedLock = distributedLock;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    /**
     * 获取教室座位信息
     * <p>
     * 使用Redis缓存优化查询性能
     * </p>
     *
     * @param roomId 教室ID
     * @return 教室座位信息
     * @throws BusinessException 教室不存在时抛出
     */
    @Override
    public RoomSeatVO getRoomSeats(String roomId) {
        Integer roomIdInt = Integer.parseInt(roomId);
        String cacheKey = "room:seats:" + roomId;

        // 尝试从缓存获取
        RoomSeatVO cachedResult = getFromCache(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        // 查询教室信息
        ClassRoom classRoom = classRoomMapper.selectById(roomIdInt);
        if (classRoom == null) {
            throw new BusinessException(404, "教室不存在");
        }

        // 查询座位列表
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getRoomId, roomIdInt);
        List<Seat> seatList = seatMapper.selectList(queryWrapper);

        // 构建返回对象
        RoomSeatVO roomSeatVO = buildRoomSeatVO(classRoom, seatList);

        // 写入缓存
        putToCache(cacheKey, roomSeatVO);

        return roomSeatVO;
    }

    /**
     * 从缓存获取数据
     *
     * @param cacheKey 缓存键
     * @return 缓存数据，不存在返回null
     */
    private RoomSeatVO getFromCache(String cacheKey) {
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof RoomSeatVO) {
                return (RoomSeatVO) cached;
            }
        } catch (Exception e) {
            logger.warn("Redis缓存读取失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将数据写入缓存
     *
     * @param cacheKey 缓存键
     * @param value    缓存值
     */
    private void putToCache(String cacheKey, RoomSeatVO value) {
        try {
            redisTemplate.opsForValue().set(cacheKey, value, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.warn("Redis缓存写入失败: {}", e.getMessage());
        }
    }

    /**
     * 构建教室座位VO
     *
     * @param classRoom 教室信息
     * @param seatList  座位列表
     * @return 教室座位VO
     */
    private RoomSeatVO buildRoomSeatVO(ClassRoom classRoom, List<Seat> seatList) {
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

        return roomSeatVO;
    }

    /**
     * 创建订单
     * <p>
     * 1. 使用分布式锁锁定座位
     * 2. 校验座位状态
     * 3. 使用乐观锁更新座位状态
     * 4. 创建订单记录
     * </p>
     * <p>
     * TODO: 高并发优化 - 添加限流策略
     * 优化建议：
     * 1. 在Controller层添加 @RateLimiter(limit = 10) 注解，限制每秒10个请求
     * 2. 使用令牌桶算法实现更精细的限流控制
     * 3. 考虑按用户ID限流，防止单用户频繁下单
     * 预计提升：保护系统免受流量冲击
     * </p>
     *
     * @param userId         用户ID
     * @param createOrderDTO 创建订单参数
     * @return 订单信息
     * @throws BusinessException 座位被占用、座位不存在或锁定失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(operation = "创建订单", module = "订单模块", logParams = true, logResult = true)
    public OrderVO createOrder(Integer userId, CreateOrderDTO createOrderDTO) {
        List<SeatLockDTO> seatList = createOrderDTO.getSeatList();
        if (seatList == null || seatList.isEmpty()) {
            throw new BusinessException(400, "请选择座位");
        }

        List<String> lockKeys = new ArrayList<>();
        List<Seat> seatsToLock = new ArrayList<>();
        int totalAmount = 0;

        try {
            // 第一步：获取所有座位的分布式锁
            acquireSeatLocks(seatList, lockKeys);

            // 第二步：校验座位状态并计算总价
            for (SeatLockDTO seatLockDTO : seatList) {
                Seat seat = validateAndGetSeat(seatLockDTO);
                seatsToLock.add(seat);
                totalAmount += seat.getPrice(); // 使用座位的实际价格
            }

            // 第三步：使用乐观锁更新座位状态
            lockSeatsWithOptimisticLock(seatsToLock);

            // 第四步：创建订单
            Order order = createOrderRecord(userId, createOrderDTO, totalAmount);

            // 第五步：创建订单座位关联记录（使用实际座位价格）
            createOrderSeatRecords(order.getId(), seatsToLock);

            logger.info("订单创建成功: orderId={}, userId={}, amount={}分", order.getId(), userId, totalAmount);

            OrderVO orderVO = new OrderVO();
            orderVO.setOrderId(order.getId().toString());
            return orderVO;
        } finally {
            // 释放分布式锁
            releaseLocks(lockKeys);
        }
    }

    /**
     * 获取座位分布式锁
     *
     * @param seatList 座位列表
     * @param lockKeys 锁键列表（输出参数）
     * @throws BusinessException 获取锁失败时抛出
     */
    private void acquireSeatLocks(List<SeatLockDTO> seatList, List<String> lockKeys) {
        for (SeatLockDTO seatLockDTO : seatList) {
            String lockKey = SEAT_LOCK_PREFIX + seatLockDTO.getId();
            if (!distributedLock.tryLock(lockKey, LOCK_EXPIRE_TIME, TimeUnit.SECONDS)) {
                throw new BusinessException(409, "座位正在被其他人选择，请稍后重试");
            }
            lockKeys.add(lockKey);
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKeys 锁键列表
     */
    private void releaseLocks(List<String> lockKeys) {
        for (String lockKey : lockKeys) {
            try {
                distributedLock.unlock(lockKey);
            } catch (Exception e) {
                logger.warn("释放锁失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 校验并获取座位
     *
     * @param seatLockDTO 座位锁定信息
     * @return 座位对象
     * @throws BusinessException 座位不存在、状态不正确或版本不匹配时抛出
     */
    private Seat validateAndGetSeat(SeatLockDTO seatLockDTO) {
        Integer seatId = Integer.parseInt(seatLockDTO.getId());
        Seat seat = seatMapper.selectById(seatId);

        if (seat == null) {
            throw new BusinessException(404, "座位不存在: " + seatLockDTO.getId());
        }
        if (seat.getStatus() == 0) {
            throw new BusinessException(400, "该座位为过道，不可选择");
        }
        if (seat.getStatus() != 1) {
            throw new BusinessException(409, "座位已被占用");
        }
        if (!seat.getVersion().equals(seatLockDTO.getVersion())) {
            throw new BusinessException(409, "座位状态已更新，请刷新页面重新选择");
        }

        return seat;
    }

    /**
     * 使用乐观锁锁定座位
     *
     * @param seatsToLock 待锁定座位列表
     * @throws BusinessException 锁定失败时抛出
     */
    private void lockSeatsWithOptimisticLock(List<Seat> seatsToLock) {
        for (Seat seat : seatsToLock) {
            int updated = updateSeatStatusWithOptimisticLock(seat, 2);
            if (updated == 0) {
                throw new BusinessException(409, "座位锁定失败，请刷新页面重新选择");
            }
        }
    }

    /**
     * 使用乐观锁更新座位状态
     * <p>
     * 注意：Seat类使用了@Version注解，MyBatis-Plus会自动处理version的乐观锁逻辑
     * 使用updateById时，必须传入包含正确version的对象
     * </p>
     *
     * @param seat   座位对象（包含正确的version）
     * @param status 目标状态
     * @return 更新影响的行数
     */
    private int updateSeatStatusWithOptimisticLock(Seat seat, int status) {
        // 直接使用查询到的seat对象，修改status
        // @Version注解会自动处理version的递增和乐观锁检查
        seat.setStatus(status);

        return seatMapper.updateById(seat);
    }

    /**
     * 创建订单记录
     *
     * @param userId         用户ID
     * @param createOrderDTO 创建订单参数
     * @param totalAmount    订单总金额
     * @return 订单对象
     */
    private Order createOrderRecord(Integer userId, CreateOrderDTO createOrderDTO, int totalAmount) {
        Order order = new Order();
        order.setId(snowflakeIdGenerator.nextId());
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
        return order;
    }

    /**
     * 创建订单座位关联记录
     *
     * @param orderId  订单ID
     * @param seatsToLock 座位对象列表（包含实际价格）
     */
    private void createOrderSeatRecords(Long orderId, List<Seat> seatsToLock) {
        for (Seat seat : seatsToLock) {
            OrderSeat orderSeat = new OrderSeat();
            orderSeat.setOrderId(orderId);
            orderSeat.setSeatId(seat.getId());
            // 使用座位的实际价格（单位：分），保持单位统一
            orderSeat.setLookPrice(seat.getPrice());
            orderSeatMapper.insert(orderSeat);
        }
    }

    /**
     * 更新订单状态
     * <p>
     * 支持取消订单，取消时释放座位
     * </p>
     *
     * @param orderId       订单ID
     * @param updateOrderDTO 更新参数
     * @throws BusinessException 订单不存在、状态不正确或已过期时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(operation = "更新订单", module = "订单模块", logParams = true)
    public void updateOrder(String orderId, UpdateOrderDTO updateOrderDTO) {
        Order order = orderMapper.selectById(Long.parseLong(orderId));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        // 校验订单状态
        validateOrderStatusForUpdate(order);

        // 更新订单状态
        order.setStatus(updateOrderDTO.getStatus());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 如果是取消操作，释放座位
        if ("CANCELLED".equals(updateOrderDTO.getStatus())) {
            releaseOrderSeats(order.getId());
        }

        logger.info("订单状态更新成功: orderId={}, status={}", orderId, updateOrderDTO.getStatus());
    }

    /**
     * 校验订单状态是否允许更新
     *
     * @param order 订单对象
     * @throws BusinessException 订单状态不正确或已过期时抛出
     */
    private void validateOrderStatusForUpdate(Order order) {
        if ("PAID".equals(order.getStatus())) {
            throw new BusinessException(400, "订单已支付，无法操作");
        }
        if ("CANCELLED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单已取消，无法操作");
        }
        if (order.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "订单已过期");
        }
    }

    /**
     * 释放订单占用的座位
     *
     * @param orderId 订单ID
     */
    private void releaseOrderSeats(Long orderId) {
        LambdaQueryWrapper<OrderSeat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderSeat::getOrderId, orderId);
        List<OrderSeat> orderSeats = orderSeatMapper.selectList(queryWrapper);

        for (OrderSeat orderSeat : orderSeats) {
            Seat seat = seatMapper.selectById(orderSeat.getSeatId());
            if (seat != null && seat.getStatus() == 2) {
                updateSeatStatusWithOptimisticLock(seat, 1);
            }
        }
    }

    /**
     * 模拟支付回调
     * <p>
     * 更新订单状态为已支付，并将座位状态更新为已售出
     * </p>
     *
     * @param orderId 订单ID
     * @throws BusinessException 订单不存在或状态不正确时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(operation = "支付回调", module = "订单模块", logParams = true, logResult = true)
    public void mockPayNotify(String orderId) {
        Order order = orderMapper.selectById(Long.parseLong(orderId));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确");
        }

        // 更新订单状态
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        // 将座位状态更新为已售出
        confirmOrderSeats(order.getId());

        logger.info("订单支付成功: orderId={}", orderId);
    }

    /**
     * 确认订单座位（将座位状态更新为已售出）
     *
     * @param orderId 订单ID
     */
    private void confirmOrderSeats(Long orderId) {
        LambdaQueryWrapper<OrderSeat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderSeat::getOrderId, orderId);
        List<OrderSeat> orderSeats = orderSeatMapper.selectList(queryWrapper);

        for (OrderSeat orderSeat : orderSeats) {
            Seat seat = seatMapper.selectById(orderSeat.getSeatId());
            if (seat != null && seat.getStatus() == 2) {
                updateSeatStatusWithOptimisticLock(seat, 3);
            }
        }
    }

    /**
     * 获取订单列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页订单列表
     */
    @Override
    public IPage<OrderListVO> getOrderList(Integer userId, Integer page, Integer pageSize) {
        Page<Order> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        queryWrapper.orderByDesc(Order::getCreatedAt);

        IPage<Order> orderPage = orderMapper.selectPage(pageParam, queryWrapper);
        IPage<OrderListVO> resultPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());

        List<OrderListVO> orderList = convertToOrderListVOs(orderPage.getRecords());
        resultPage.setRecords(orderList);

        return resultPage;
    }

    @Override
    public OrderListVO getOrder(Integer userId, String orderId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getId, Long.parseLong(orderId));
        queryWrapper.eq(Order::getUserId, userId);

        Order order = orderMapper.selectOne(queryWrapper);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        List<OrderListVO> orderList = convertToOrderListVOs(Collections.singletonList(order));
        return orderList.get(0);
    }

    /**
     * 将订单列表转换为VO列表
     *
     * @param orders 订单列表
     * @return 订单VO列表
     */
    private List<OrderListVO> convertToOrderListVOs(List<Order> orders) {
        List<OrderListVO> orderList = new ArrayList<>();

        for (Order order : orders) {
            OrderListVO orderListVO = new OrderListVO();
            orderListVO.setId(order.getId());
            orderListVO.setCampusId(order.getCampusId());
            orderListVO.setBuildingId(order.getBuildingId());
            orderListVO.setRoomId(order.getRoomId());
            orderListVO.setAmount(order.getAmount());
            orderListVO.setStatus(order.getStatus());
            orderListVO.setExpiresAt(order.getExpiresAt().format(DATE_TIME_FORMATTER));
            orderListVO.setCreatedAt(order.getCreatedAt().format(DATE_TIME_FORMATTER));
            orderListVO.setUpdatedAt(order.getUpdatedAt().format(DATE_TIME_FORMATTER));

            // 查询订单座位信息
            List<OrderSeatVO> seatList = getOrderSeatVOs(order.getId());
            orderListVO.setSeatList(seatList);

            orderList.add(orderListVO);
        }

        return orderList;
    }

    /**
     * 获取订单座位VO列表
     *
     * @param orderId 订单ID
     * @return 订单座位VO列表
     */
    private List<OrderSeatVO> getOrderSeatVOs(Long orderId) {
        LambdaQueryWrapper<OrderSeat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderSeat::getOrderId, orderId);
        List<OrderSeat> orderSeats = orderSeatMapper.selectList(queryWrapper);

        List<OrderSeatVO> seatList = new ArrayList<>();
        for (OrderSeat orderSeat : orderSeats) {
            OrderSeatVO orderSeatVO = new OrderSeatVO();
            orderSeatVO.setId(orderSeat.getSeatId());
            orderSeatVO.setLookPrice(orderSeat.getLookPrice().toString());
            
            // 查询座位信息获取row和col
            Seat seat = seatMapper.selectById(orderSeat.getSeatId());
            if (seat != null) {
                orderSeatVO.setRow(seat.getRow());
                orderSeatVO.setCol(seat.getCol());
            }
            
            seatList.add(orderSeatVO);
        }

        return seatList;
    }

    /**
     * 检查订单超时
     * <p>
     * 异步任务，自动取消超时的待支付订单并释放座位
     * </p>
     * <p>
     * TODO: 高并发优化 - 配置专用异步线程池
     * 优化建议：
     * 高并发优化：使用专用线程池 orderTaskExecutor
     * 预计提升：+30% 性能，避免阻塞主线程
     * </p>
     */
    @Async("orderTaskExecutor")
    public void checkOrderTimeout() {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getStatus, "PENDING");
        queryWrapper.lt(Order::getExpiresAt, LocalDateTime.now());
        List<Order> expiredOrders = orderMapper.selectList(queryWrapper);

        for (Order order : expiredOrders) {
            try {
                cancelExpiredOrder(order);
                logger.info("订单超时自动取消: orderId={}", order.getId());
            } catch (Exception e) {
                logger.error("处理超时订单失败: orderId={}", order.getId(), e);
            }
        }
    }

    /**
     * 取消过期订单
     *
     * @param order 订单对象
     */
    private void cancelExpiredOrder(Order order) {
        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        releaseOrderSeats(order.getId());
    }
}
