package com.university.vrclassroombackend.module.donation.service;

import com.university.vrclassroombackend.module.donation.model.DonationOrder;

import java.util.List;

/**
 * 捐赠服务接口
 * 用于处理捐赠相关的业务逻辑
 */
public interface DonationService {
    /**
     * 创建捐赠订单
     * @param donorId 捐赠者ID
     * @param seatId 座位ID
     * @param tierId 捐赠等级ID
     * @param message 捐赠留言
     * @return 捐赠订单对象
     */
    DonationOrder createDonation(Integer donorId, Integer seatId, Integer tierId, String message);
    
    /**
     * 根据订单号获取捐赠订单
     * @param orderNo 订单号
     * @return 捐赠订单对象
     */
    DonationOrder getDonationByOrderNo(String orderNo);
    
    /**
     * 根据捐赠者ID获取捐赠订单列表
     * @param donorId 捐赠者ID
     * @return 捐赠订单列表
     */
    List<DonationOrder> getDonationsByDonorId(Integer donorId);
    
    /**
     * 完成捐赠订单
     * @param donationId 捐赠订单ID
     * @return 是否完成成功
     */
    boolean completeDonation(Integer donationId);
    
    /**
     * 取消捐赠订单
     * @param donationId 捐赠订单ID
     * @return 是否取消成功
     */
    boolean cancelDonation(Integer donationId);
    
    /**
     * 标记捐赠订单为支付失败
     * @param donationId 捐赠订单ID
     * @return 是否标记成功
     */
    boolean failDonation(Integer donationId);
}




