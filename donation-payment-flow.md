# 捐赠与支付流程文档

## 1. 流程概述

捐赠与支付流程是VR教室项目中的核心功能之一，允许用户通过不同的支付方式为VR教室的座位进行捐赠。整个流程包括捐赠订单创建、支付订单创建、支付处理、支付回调处理以及订单状态同步等环节。

### 1.1 流程步骤

1. **用户发起捐赠**：用户选择座位和捐赠等级，填写捐赠信息
2. **创建捐赠订单**：后端创建捐赠订单，生成订单号
3. **创建支付订单**：后端根据捐赠订单信息创建支付订单
4. **用户完成支付**：用户通过选择的支付方式完成支付
5. **支付回调处理**：支付平台通知后端支付结果
6. **更新订单状态**：后端更新支付订单和捐赠订单的状态
7. **通知用户**：前端显示支付结果，引导用户查看捐赠记录

## 2. 核心实体类

### 2.1 捐赠订单 (DonationOrder)

| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Integer | 捐赠订单ID |
| donorId | Integer | 捐赠者ID |
| seatId | Integer | 座位ID |
| tierId | Integer | 捐赠等级ID |
| amount | BigDecimal | 捐赠金额 |
| message | String | 捐赠留言 |
| status | Integer | 订单状态（0：待支付，1：已完成，2：已取消，3：支付失败） |
| orderNo | String | 订单号 |
| createdAt | LocalDateTime | 创建时间 |
| paidAt | LocalDateTime | 支付时间 |
| completedAt | LocalDateTime | 完成时间 |
| cancelledAt | LocalDateTime | 取消时间 |
| failedAt | LocalDateTime | 失败时间 |
| version | Integer | 乐观锁版本号 |

### 2.2 支付订单 (PaymentOrder)

| 字段名 | 类型 | 描述 |
|-------|------|------|
| id | Integer | 支付订单ID |
| userId | Integer | 用户ID |
| amount | BigDecimal | 支付金额 |
| orderNo | String | 订单号 |
| status | Integer | 订单状态（0：待支付，1：已支付，2：已完成，3：已取消，4：支付失败） |
| paymentMethod | String | 支付方式 |
| transactionId | String | 交易ID |
| productType | String | 产品类型（DONATION：捐赠） |
| productId | String | 产品ID（捐赠订单ID） |
| remark | String | 备注 |
| createdAt | LocalDateTime | 创建时间 |
| paidAt | LocalDateTime | 支付时间 |
| completedAt | LocalDateTime | 完成时间 |
| cancelledAt | LocalDateTime | 取消时间 |
| failedAt | LocalDateTime | 支付失败时间 |
| version | Integer | 乐观锁版本号 |

## 3. 服务层实现

### 3.1 捐赠服务 (DonationService)

#### 核心方法

- **createDonation**：创建捐赠订单，设置订单状态为待支付，生成订单号
- **getDonationByOrderNo**：根据订单号获取捐赠订单
- **getDonationsByDonorId**：获取用户的捐赠订单列表
- **completeDonation**：完成捐赠订单，更新状态为已完成
- **cancelDonation**：取消捐赠订单，更新状态为已取消
- **failDonation**：标记捐赠订单为支付失败

### 3.2 支付服务 (PaymentService)

#### 核心方法

- **createPayment**：创建支付订单，设置订单状态为待支付，生成订单号，存入Redis缓存
- **getPaymentByOrderNo**：根据订单号获取支付订单（优先从Redis缓存获取）
- **getPaymentsByUserId**：获取用户的支付订单列表
- **handlePaymentCallback**：处理支付回调，更新支付订单状态，同步更新捐赠订单状态
- **cancelPayment**：取消支付订单，更新状态为已取消，同步更新捐赠订单状态

## 4. 控制器实现

### 4.1 捐赠控制器 (DonationController)

#### 核心接口

- **POST /api/donation/create**：创建捐赠订单和支付订单
- **GET /api/donation/orders**：获取用户的捐赠订单列表
- **GET /api/donation/orders/{orderNo}**：获取捐赠订单详情
- **POST /api/donation/orders/{id}/cancel**：取消捐赠订单

### 4.2 支付控制器 (PaymentController)

#### 核心接口

- **POST /api/payment/create**：创建支付订单
- **GET /api/payment/orders**：获取用户的支付订单列表
- **GET /api/payment/orders/{orderNo}**：获取支付订单详情
- **POST /api/payment/orders/{id}/cancel**：取消支付订单
- **POST /api/payment/callback**：处理支付回调

## 5. 交互设计

### 5.1 前端与后端交互

1. **创建捐赠订单**：
   - 前端发送POST请求到/api/donation/create，包含座位ID、捐赠等级ID、留言、支付方式
   - 后端验证用户登录状态，创建捐赠订单，生成订单号
   - 后端根据捐赠订单信息创建支付订单，返回支付订单信息
   - 前端根据返回的支付订单信息，跳转到支付页面或调用支付SDK

2. **用户完成支付**：
   - 用户在支付页面或通过支付SDK完成支付
   - 支付平台通知后端支付结果（回调）
   - 后端处理支付回调，更新支付订单和捐赠订单状态
   - 前端轮询或通过WebSocket获取支付结果

3. **查看捐赠记录**：
   - 前端发送GET请求到/api/donation/orders，获取用户的捐赠订单列表
   - 后端返回捐赠订单列表
   - 前端展示捐赠记录

### 5.2 服务之间的交互

1. **捐赠服务与支付服务**：
   - 捐赠服务创建捐赠订单后，调用支付服务创建支付订单
   - 支付服务处理支付回调时，根据支付结果调用捐赠服务更新捐赠订单状态
   - 支付服务取消支付订单时，调用捐赠服务取消捐赠订单

2. **支付服务与Redis**：
   - 支付服务创建支付订单后，将订单信息存入Redis缓存，设置过期时间为1小时
   - 支付服务获取支付订单时，优先从Redis缓存获取
   - 支付服务更新支付订单状态后，更新Redis缓存中的订单信息

## 6. 关键技术点

### 6.1 并发处理

#### 6.1.1 乐观锁（Optimistic Locking）

**原理**：乐观锁假设数据不会发生冲突，只在更新时检查数据是否被其他线程修改过。在实体类中添加一个版本号字段，每次更新时版本号递增，更新时比较版本号是否与数据库中的一致。

**实现**：在DonationOrder和PaymentOrder实体类中使用@Version注解实现乐观锁：
```java
@Version
private Integer version; // 乐观锁版本号
```

**工作流程**：
1. 读取数据时，同时读取版本号
2. 更新数据时，将版本号加1
3. 更新时使用WHERE条件包含版本号：`UPDATE table SET ..., version = version + 1 WHERE id = ? AND version = ?`
4. 如果版本号不匹配，更新失败，抛出OptimisticLockingFailureException

**适用场景**：
- 读操作远多于写操作的场景
- 并发冲突较少的场景
- 对性能要求较高的场景

#### 6.1.2 悲观锁（Pessimistic Locking）

**原理**：悲观锁假设数据会发生冲突，在查询时就锁定资源，防止其他线程修改。

**实现**：在PaymentRepository中使用@Lock注解和LockModeType.PESSIMISTIC_WRITE实现悲观锁：
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@QueryHints({
    @QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000") // 5秒超时
})
@Query("SELECT p FROM PaymentOrder p WHERE p.userId = ?1 AND p.productType = ?2 AND p.productId = ?3 AND p.status = ?4")
PaymentOrder findByUserIdAndProductTypeAndProductIdAndStatusWithLock(Integer userId, String productType, String productId, Integer status);
```

**工作流程**：
1. 查询数据时，使用SELECT ... FOR UPDATE语句锁定行
2. 其他线程尝试修改或锁定同一行时会被阻塞
3. 事务提交或回滚后，锁释放

**适用场景**：
- 写操作频繁的场景
- 并发冲突较多的场景
- 数据一致性要求较高的场景

#### 6.1.3 两种锁的对比

| 特性 | 乐观锁 | 悲观锁 |
|------|--------|--------|
| 锁定时机 | 更新时 | 查询时 |
| 性能 | 高（无锁竞争） | 低（有锁竞争） |
| 并发控制 | 最终一致性 | 强一致性 |
| 实现复杂度 | 低 | 高（需要事务支持） |
| 死锁风险 | 无 | 有 |

#### 6.1.4 其他并发处理技术

##### 6.1.4.1 异步处理

**原理**：将耗时操作异步执行，减少主线程阻塞，提高系统并发能力。

**实现**：使用@Async注解实现支付回调异步执行：
```java
@Async
public void handlePaymentCallback(String orderNo, String transactionId, Integer status, String sign) {
    // 处理支付回调逻辑
}
```

**适用场景**：
- 耗时操作，如支付回调处理、发送邮件等
- 不需要即时返回结果的操作

##### 6.1.4.2 缓存机制

**原理**：使用缓存存储热点数据，减少数据库访问，提高系统响应速度和并发能力。

**实现**：使用Redis缓存支付订单信息：
```java
String cacheKey = "payment:order:" + savedOrder.getOrderNo();
redisTemplate.opsForValue().set(cacheKey, savedOrder, 3600, java.util.concurrent.TimeUnit.SECONDS);
```

**适用场景**：
- 热点数据查询
- 读多写少的场景
- 需要快速响应的场景

#### 6.1.5 专有名词解释

- **并发（Concurrency）**：多个线程同时访问和操作同一资源的情况。
- **竞态条件（Race Condition）**：多个线程同时操作共享资源，导致结果依赖于线程执行顺序的情况。
- **死锁（Deadlock）**：两个或多个线程相互等待对方释放资源，导致所有线程都无法继续执行的情况。
- **活锁（Livelock）**：线程不断尝试解决冲突，但始终无法成功的情况。
- **锁升级（Lock Escalation）**：从行级锁升级为表级锁的过程。
- **锁超时（Lock Timeout）**：获取锁时等待的最大时间，超过时间则放弃获取锁。
- **幂等性（Idempotency）**：多次操作产生相同结果的特性，在支付回调中尤为重要。

### 6.2 异步处理

- **支付回调异步处理**：使用@Async注解实现支付回调异步执行，提高系统并发能力

### 6.3 缓存机制

- **Redis缓存**：使用Redis缓存支付订单信息，提高查询性能，设置过期时间为1小时

### 6.4 安全考虑

- **签名验证**：支付回调时验证签名，防止伪造回调
- **幂等性处理**：支付回调处理时，检查订单状态，避免重复处理
- **参数验证**：验证请求参数，防止恶意请求

### 6.5 异常处理

- **Redis连接失败**：Redis连接失败时，不影响主要功能，只记录日志
- **支付平台回调失败**：支付平台回调失败时，通过定时任务处理超时订单

## 7. 订单状态流转

### 7.1 捐赠订单状态流转

```
待支付（0） → 已完成（1）
    ↑          ↑
    |          |
    └→ 已取消（2）
    |
    └→ 支付失败（3）
```

### 7.2 支付订单状态流转

```
待支付（0） → 已支付（1） → 已完成（2）
    ↑          ↑            ↑
    |          |            |
    └→ 已取消（3）           |
    |                       |
    └→ 支付失败（4）         |
```

## 8. 典型场景

### 8.1 创建捐赠订单

1. 用户登录系统
2. 进入VR教室座位选择页面
3. 选择一个座位，点击"捐赠"
4. 选择捐赠等级，填写留言
5. 选择支付方式
6. 点击"确认捐赠"
7. 前端发送请求到后端
8. 后端创建捐赠订单和支付订单
9. 前端跳转到支付页面
10. 用户完成支付
11. 支付平台通知后端
12. 后端更新订单状态
13. 前端显示支付结果

### 8.2 查看捐赠记录

1. 用户登录系统
2. 进入个人中心
3. 点击"我的捐赠"
4. 前端发送请求到后端
5. 后端返回捐赠订单列表
6. 前端展示捐赠记录

## 9. 代码优化建议

1. **等级金额配置**：建议将捐赠等级对应的金额配置到数据库或配置文件中，而不是硬编码在代码中
2. **异常处理**：建议使用统一的异常处理机制，提高代码可维护性
3. **日志记录**：建议使用SLF4J进行日志记录，而不是System.err.println
4. **配置管理**：建议使用@ConfigurationProperties注解管理配置，而不是硬编码在代码中
5. **接口文档**：建议使用Swagger生成接口文档，方便前端开发人员了解接口
6. **测试覆盖**：建议增加单元测试和集成测试，提高代码质量
7. **代码风格**：建议统一代码风格，使用工具如Checkstyle或Spotless

## 10. 总结

捐赠与支付流程是VR教室项目中的重要功能，通过合理的架构设计和技术选型，实现了完整的捐赠与支付功能。系统采用了乐观锁和悲观锁解决并发问题，使用Redis缓存提高查询性能，通过异步处理提高系统并发能力，确保了系统的稳定性和可靠性。

同时，系统还考虑了安全性和异常处理，确保了支付过程的安全和系统的容错能力。通过完善的订单状态流转和服务之间的交互，实现了捐赠与支付流程的无缝对接，为用户提供了良好的捐赠体验。