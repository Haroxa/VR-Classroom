package com.university.vrclassroombackend.module.payment.service.impl;

import com.university.vrclassroombackend.module.donation.service.DonationService;
import com.university.vrclassroombackend.module.payment.constant.PaymentStatus;
import com.university.vrclassroombackend.module.payment.model.PaymentOrder;
import com.university.vrclassroombackend.module.payment.repository.PaymentRepository;
import com.university.vrclassroombackend.module.payment.service.PaymentService;
import com.university.vrclassroombackend.module.payment.vo.PaymentOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 支付服务实现类
 * 用于处理支付相关的业务逻辑
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private DonationService donationService;
    
    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, RedisTemplate<String, Object> redisTemplate) {
        this.paymentRepository = paymentRepository;
        this.redisTemplate = redisTemplate;
    }
    
    @Autowired
    public void setDonationService(DonationService donationService) {
        this.donationService = donationService;
    }

    @Override
    @Transactional
    public PaymentOrder createPayment(Integer userId, Double amount, String productType, String productId, String paymentMethod, String remark) {
        // 检查是否已经存在相同的待支付订单，避免重复支付（使用悲观锁）
        PaymentOrder existingOrder = paymentRepository.findByUserIdAndProductTypeAndProductIdAndStatusWithLock(userId, productType, productId, PaymentStatus.PENDING.getCode());
        if (existingOrder != null) {
            // 如果存在相同的待支付订单，直接返回该订单
            return existingOrder;
        }
        
        // 创建支付订单对象
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUserId(userId);
        paymentOrder.setAmount(BigDecimal.valueOf(amount));
        paymentOrder.setOrderNo(generateOrderNo());
        paymentOrder.setStatus(PaymentStatus.PENDING.getCode()); // 待支付
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setProductType(productType);
        paymentOrder.setProductId(productId);
        paymentOrder.setRemark(remark);
        paymentOrder.setCreatedAt(LocalDateTime.now());
        
        // 保存支付订单
        PaymentOrder savedOrder = paymentRepository.save(paymentOrder);
        
        // 将支付订单存入 Redis 缓存，设置过期时间为 1 小时
        try {
            String cacheKey = "payment:order:" + savedOrder.getOrderNo();
            redisTemplate.opsForValue().set(cacheKey, savedOrder, 3600, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis 连接失败，不影响主要功能，只记录日志
            System.err.println("Redis 缓存失败: " + e.getMessage());
        }
        
        return savedOrder;
    }

    @Override
    public PaymentOrder getPaymentByOrderNo(String orderNo) {
        // 先从 Redis 缓存中获取
        try {
            String cacheKey = "payment:order:" + orderNo;
            PaymentOrder paymentOrder = (PaymentOrder) redisTemplate.opsForValue().get(cacheKey);
            if (paymentOrder != null) {
                return paymentOrder;
            }
        } catch (Exception e) {
            // Redis 连接失败，不影响主要功能，只记录日志
            System.err.println("Redis 缓存读取失败: " + e.getMessage());
        }
        
        // 从数据库中获取
        PaymentOrder paymentOrder = paymentRepository.findByOrderNo(orderNo);
        if (paymentOrder != null) {
            // 将结果存入 Redis 缓存，设置过期时间为 1 小时
            try {
                String cacheKey = "payment:order:" + orderNo;
                redisTemplate.opsForValue().set(cacheKey, paymentOrder, 3600, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception e) {
                // Redis 连接失败，不影响主要功能，只记录日志
                System.err.println("Redis 缓存失败: " + e.getMessage());
            }
        }
        
        return paymentOrder;
    }

    @Override
    public List<PaymentOrder> getPaymentsByUserId(Integer userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    @Async
    public void handlePaymentCallback(String orderNo, String transactionId, Integer status, String sign) {
        // 验证签名
        if (!verifySign(orderNo, transactionId, status, sign)) {
            return;
        }
        
        // 根据订单号获取支付订单
        PaymentOrder paymentOrder = paymentRepository.findByOrderNo(orderNo);
        if (paymentOrder == null) {
            return;
        }
        
        // 幂等性处理：如果订单已经处理过，直接返回
        if (paymentOrder.getStatus() != PaymentStatus.PENDING.getCode()) {
            return;
        }
        
        // 更新支付订单状态
        paymentOrder.setStatus(status);
        paymentOrder.setTransactionId(transactionId);
        
        // 根据状态更新时间
        PaymentStatus paymentStatus = PaymentStatus.getByCode(status);
        if (paymentStatus != null) {
            switch (paymentStatus) {
                case PAID:
                    paymentOrder.setPaidAt(LocalDateTime.now());
                    break;
                case COMPLETED:
                    paymentOrder.setCompletedAt(LocalDateTime.now());
                    break;
                case CANCELLED:
                    paymentOrder.setCancelledAt(LocalDateTime.now());
                    break;
                case FAILED:
                    paymentOrder.setFailedAt(LocalDateTime.now());
                    break;
                default:
                    break;
            }
        }
        
        // 保存更新后的支付订单
        paymentRepository.save(paymentOrder);
        
        // 更新 Redis 缓存中的订单信息
        try {
            String cacheKey = "payment:order:" + paymentOrder.getOrderNo();
            redisTemplate.opsForValue().set(cacheKey, paymentOrder, 3600, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            // Redis 连接失败，不影响主要功能，只记录日志
            System.err.println("Redis 缓存失败: " + e.getMessage());
        }
        
        // 如果是捐赠订单，更新对应的捐赠订单状态
        if ("DONATION".equals(paymentOrder.getProductType())) {
            try {
                Integer donationId = Integer.parseInt(paymentOrder.getProductId());
                // 直接根据状态码判断，不依赖 paymentStatus
                if (status == 1 || status == 2) { // 已支付或已完成
                    donationService.completeDonation(donationId);
                } else if (status == 3) { // 已取消
                    donationService.cancelDonation(donationId);
                } else if (status == 4) { // 支付失败
                    donationService.failDonation(donationId);
                }
            } catch (NumberFormatException e) {
                // 处理产品ID格式错误的情况
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 验证支付平台签名
     * @param orderNo 订单号
     * @param transactionId 交易ID
     * @param status 支付状态
     * @param sign 签名
     * @return 是否验证通过
     */
    public boolean verifySign(String orderNo, String transactionId, Integer status, String sign) {
        try {
            // 测试环境下，暂时跳过签名验证，总是返回true
            if ("test_sign".equals(sign)) {
                return true;
            }
            
            // 实际项目中，应该根据支付平台的签名算法验证签名
            // 示例：拼接参数，使用密钥生成签名，与传入的签名比较
            // 1. 拼接参数
            StringBuilder sb = new StringBuilder();
            sb.append("orderNo=").append(orderNo)
              .append("&transactionId=").append(transactionId)
              .append("&status=").append(status)
              .append("&key=").append(getPaymentPlatformKey()); // 从配置中获取密钥
            
            // 2. 使用密钥生成签名
            // 这里使用简单的MD5签名，实际项目中应该使用支付平台要求的签名算法
            String generatedSign = generateSign(sb.toString());
            
            // 3. 比较签名
            return generatedSign.equals(sign);
        } catch (Exception e) {
            // 签名验证失败
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 生成签名
     * @param data 待签名数据
     * @return 签名
     */
    private String generateSign(String data) throws Exception {
        // 实际项目中，应该使用支付平台要求的签名算法
        // 这里使用简单的MD5签名作为示例
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * 获取支付平台密钥
     * @return 支付平台密钥
     */
    private String getPaymentPlatformKey() {
        // 实际项目中，应该从配置文件中获取密钥
        // 这里为了演示，直接返回一个示例密钥
        // TODO: 从配置文件中获取密钥
        return "payment-platform-secret-key"; // 示例密钥
    }

    @Override
    public boolean cancelPayment(Integer paymentId) {
        // 根据ID获取支付订单
        PaymentOrder paymentOrder = paymentRepository.findById(paymentId).orElse(null);
        if (paymentOrder == null) {
            return false;
        }
        
        // 只有待支付的订单可以取消
        if (paymentOrder.getStatus() != PaymentStatus.PENDING.getCode()) {
            return false;
        }
        
        // 更新支付订单状态
        paymentOrder.setStatus(PaymentStatus.CANCELLED.getCode()); // 已取消
        paymentOrder.setCancelledAt(LocalDateTime.now());
        
        // 保存更新后的支付订单
        paymentRepository.save(paymentOrder);
        
        // 如果是捐赠订单，同步更新对应的捐赠订单状态
        if ("DONATION".equals(paymentOrder.getProductType())) {
            try {
                Integer donationId = Integer.parseInt(paymentOrder.getProductId());
                donationService.cancelDonation(donationId);
            } catch (NumberFormatException e) {
                // 处理产品ID格式错误的情况
                e.printStackTrace();
            }
        }
        
        return true;
    }

    @Override
    public PaymentOrderVO convertToPaymentOrderVO(PaymentOrder paymentOrder) {
        PaymentOrderVO vo = new PaymentOrderVO();
        vo.setId(paymentOrder.getId());
        vo.setUserId(paymentOrder.getUserId());
        vo.setAmount(paymentOrder.getAmount());
        vo.setOrderNo(paymentOrder.getOrderNo());
        vo.setStatus(paymentOrder.getStatus());
        vo.setPaymentMethod(paymentOrder.getPaymentMethod());
        vo.setTransactionId(paymentOrder.getTransactionId());
        vo.setProductType(paymentOrder.getProductType());
        vo.setProductId(paymentOrder.getProductId());
        vo.setRemark(paymentOrder.getRemark());
        vo.setCreatedAt(paymentOrder.getCreatedAt());
        vo.setPaidAt(paymentOrder.getPaidAt());
        vo.setCompletedAt(paymentOrder.getCompletedAt());
        vo.setCancelledAt(paymentOrder.getCancelledAt());
        return vo;
    }

    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        // 生成唯一的订单号，格式：PAY + 时间戳 + 随机数
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
