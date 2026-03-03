package com.university.vrclassroombackend.module.order.service;

import com.university.vrclassroombackend.module.order.dto.CreateOrderDTO;
import com.university.vrclassroombackend.module.order.dto.UpdateOrderDTO;
import com.university.vrclassroombackend.module.order.vo.OrderListVO;
import com.university.vrclassroombackend.module.order.vo.OrderVO;
import com.university.vrclassroombackend.module.order.vo.RoomSeatVO;

import java.util.List;

public interface OrderService {
    RoomSeatVO getRoomSeats(String roomId);
    OrderVO createOrder(Integer userId, CreateOrderDTO createOrderDTO);
    void updateOrder(String orderId, UpdateOrderDTO updateOrderDTO);
    void mockPayNotify(String orderId);
    List<OrderListVO> getOrderList(Integer userId, Integer page, Integer size);
}
