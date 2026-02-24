package com.university.vrclassroombackend.module.payment.repository;

import com.university.vrclassroombackend.module.payment.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付订单仓库接口
 * 用于操作支付订单数据
 */
public interface PaymentRepository extends JpaRepository<PaymentOrder, Integer> {
    /**
     * 根据订单号查询支付订单
     * @param orderNo 订单号
     * @return 支付订单对象
     */
    PaymentOrder findByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询支付订单列表
     * @param userId 用户ID
     * @return 支付订单列表
     */
    List<PaymentOrder> findByUserId(Integer userId);
    
    /**
     * 根据交易ID查询支付订单
     * @param transactionId 交易ID
     * @return 支付订单对象
     */
    PaymentOrder findByTransactionId(String transactionId);
    
    /**
     * 根据用户ID、产品类型和产品ID获取待支付的支付订单
     * @param userId 用户ID
     * @param productType 产品类型
     * @param productId 产品ID
     * @param status 订单状态
     * @return 支付订单对象
     */
    PaymentOrder findByUserIdAndProductTypeAndProductIdAndStatus(Integer userId, String productType, String productId, Integer status);
    
    /**
     * 根据状态和创建时间查询支付订单
     * @param status 订单状态
     * @param createdAtBefore 创建时间
     * @return 支付订单列表
     */
    List<PaymentOrder> findByStatusAndCreatedAtBefore(Integer status, LocalDateTime createdAtBefore);
    
    /**
     * 根据用户ID、产品类型、产品ID和状态查询支付订单（带悲观锁）
     * @param userId 用户ID
     * @param productType 产品类型
     * @param productId 产品ID
     * @param status 订单状态
     * @return 支付订单对象
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000") // 5秒超时
    })
    @Query("SELECT p FROM PaymentOrder p WHERE p.userId = ?1 AND p.productType = ?2 AND p.productId = ?3 AND p.status = ?4")
    PaymentOrder findByUserIdAndProductTypeAndProductIdAndStatusWithLock(Integer userId, String productType, String productId, Integer status);
}
