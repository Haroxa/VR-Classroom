package com.university.vrclassroombackend.module.donation.service.impl;

import com.university.vrclassroombackend.module.donation.constant.DonationStatus;
import com.university.vrclassroombackend.module.donation.model.DonationOrder;
import com.university.vrclassroombackend.module.donation.mapper.DonationMapper;
import com.university.vrclassroombackend.module.donation.service.DonationService;
import com.university.vrclassroombackend.module.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 捐赠服务实现类
 * 用于处理捐赠相关的业务逻辑
 * @ConditionalOnProperty 仅在开发环境启用
 */
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "spring.profiles.active",
    havingValue = "dev",
    matchIfMissing = false
)
@Service
public class DonationServiceImpl implements DonationService {
    
    private final DonationMapper donationMapper;
    private PaymentService paymentService;
    
    @Autowired
    public DonationServiceImpl(DonationMapper donationMapper) {
        this.donationMapper = donationMapper;
    }
    
    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public DonationOrder createDonation(Integer donorId, Integer seatId, Integer tierId, String message) {
        // 创建捐赠订单对象
        DonationOrder donationOrder = new DonationOrder();
        donationOrder.setDonorId(donorId);
        donationOrder.setSeatId(seatId);
        donationOrder.setTierId(tierId);
        
        // 根据 tierId 获取捐赠金额
        // 这里使用简单的映射模拟从数据库获取金额的过程
        // 实际项目中应该从数据库中获取等级对应的金额
        BigDecimal amount = getAmountByTierId(tierId);
        donationOrder.setAmount(amount);
            
            donationOrder.setMessage(message);
            donationOrder.setStatus(DonationStatus.PENDING.getCode()); // 待支付
            donationOrder.setOrderNo(generateOrderNo());
            donationOrder.setCreatedAt(LocalDateTime.now());
        
        // 保存捐赠订单
        donationMapper.insert(donationOrder);
        return donationOrder;
    }
    
    /**
     * 根据等级ID获取捐赠金额
     * @param tierId 等级ID
     * @return 捐赠金额
     */
    private BigDecimal getAmountByTierId(Integer tierId) {
        // 简单的等级金额映射
        // 实际项目中应该从数据库中获取
        switch (tierId) {
            case 1:
                return BigDecimal.valueOf(50.0);
            case 2:
                return BigDecimal.valueOf(100.0);
            case 3:
                return BigDecimal.valueOf(200.0);
            case 4:
                return BigDecimal.valueOf(500.0);
            case 5:
                return BigDecimal.valueOf(1000.0);
            default:
                return BigDecimal.valueOf(100.0); // 默认金额
        }
    }

    @Override
    public DonationOrder getDonationByOrderNo(String orderNo) {
        return donationMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<DonationOrder> getDonationsByDonorId(Integer donorId) {
        return donationMapper.selectByDonorId(donorId);
    }

    @Override
    public boolean completeDonation(Integer donationId) {
        // 根据ID获取捐赠订单
        DonationOrder donationOrder = donationMapper.selectById(donationId);
        if (donationOrder == null) {
            return false;
        }
        
        // 只有待支付的订单可以完成
        if (donationOrder.getStatus() != DonationStatus.PENDING.getCode()) {
            return false;
        }
        
        // 更新捐赠订单状态
        donationOrder.setStatus(DonationStatus.COMPLETED.getCode()); // 已完成
        donationOrder.setCompletedAt(LocalDateTime.now());
        
        // 保存更新后的捐赠订单
        donationMapper.updateById(donationOrder);
        return true;
    }

    @Override
    public boolean cancelDonation(Integer donationId) {
        // 根据ID获取捐赠订单
        DonationOrder donationOrder = donationMapper.selectById(donationId);
        if (donationOrder == null) {
            return false;
        }
        
        // 只有待支付的订单可以取消
        if (donationOrder.getStatus() != DonationStatus.PENDING.getCode()) {
            return false;
        }
        
        // 更新捐赠订单状态
        donationOrder.setStatus(DonationStatus.CANCELLED.getCode()); // 已取消
        donationOrder.setCancelledAt(LocalDateTime.now());
        
        // 保存更新后的捐赠订单
        donationMapper.updateById(donationOrder);
        return true;
    }
    
    @Override
    public boolean failDonation(Integer donationId) {
        // 根据ID获取捐赠订单
        DonationOrder donationOrder = donationMapper.selectById(donationId);
        if (donationOrder == null) {
            return false;
        }
        
        // 只有待支付的订单可以标记为支付失败
        if (donationOrder.getStatus() != DonationStatus.PENDING.getCode()) {
            return false;
        }
        
        // 更新捐赠订单状态
        donationOrder.setStatus(DonationStatus.FAILED.getCode()); // 支付失败
        donationOrder.setFailedAt(LocalDateTime.now());
        
        // 保存更新后的捐赠订单
        donationMapper.updateById(donationOrder);
        return true;
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        // 生成唯一的订单号，格式：DON + 时间戳 + 随机数
        return "DON" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}




