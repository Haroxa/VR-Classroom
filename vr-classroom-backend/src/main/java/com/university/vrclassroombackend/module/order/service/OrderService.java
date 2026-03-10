package com.university.vrclassroombackend.module.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.vo.OrderListVO;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;

public interface OrderService {
    RoomSeatVO getRoomSeats(String roomId);
    OrderVO createOrder(Integer userId, CreateOrderDTO createOrderDTO);
    void updateOrder(String orderId, UpdateOrderDTO updateOrderDTO);
    void mockPayNotify(String orderId);
    IPage<OrderListVO> getOrderList(Integer userId, Integer page, Integer pageSize);
    OrderListVO getOrder(Integer userId, String orderId);
}
