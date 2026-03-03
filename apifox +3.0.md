# Apifox v4.1 vs v3.0 完整对比文档

> 本文档详细对比 apifox v4.1 和 v3.0 的所有差异，按优先级给出修改建议，待审核后更新项目代码。

---

## 📋 目录

- [接口数量对比](#接口数量对比)
- [新增接口](#新增接口)
- [修改接口](#修改接口)
- [数据模型变更](#数据模型变更)
- [优先级修改建议](#优先级修改建议)
- [数据库变更脚本](#数据库变更脚本)
- [风险评估](#风险评估)

---

## 接口数量对比

| 版本 | 接口总数 | 论坛模块 | 用户模块 | 订单模块 | 后台模块 | 其他 |
|------|----------|----------|----------|----------|----------|------|
| v3.0 | 20 | 9 | 0 | 4 | 5 | 2 |
| v4.1 | 22 | 9 | 1 | 5 | 5 | 2 |
| 差异 | +2 | 0 | +1 | +1 | 0 | 0 |

---

## 新增接口

### 1. POST /api/users/login （用户登录）【高优先级】

**新增原因**：v3.0 中没有用户登录接口，v4.1 新增微信小程序登录功能

**接口详情**：
- **路径**: `POST /api/users/login`
- **功能**: 微信小程序登录，通过 loginCode 换取 openId，通过 phoneCode 换取手机号
- **请求参数**:
  ```json
  {
    "loginCode": "string",  // 换取openId
    "phoneCode": "string"   // 换取手机号
  }
  ```
- **响应数据**:
  ```json
  {
    "code": 0,
    "msg": "string",
    "data": {
      "id": "string",
      "phone": "string",
      "name": "未认证",
      "avatar": "assets/default_avatar.png",
      "collegeId": "string",
      "verifyStatus": 0,  // 0：未认证，1：审核中，2：已认证
      "collegeName": "string"
    }
  }
  ```

**微信配置信息**：
- AppID：wxea208099d2372b84
- AppSecret：d10f3a6b58b0c1562a82bfc467c06418

---

### 2. GET /api/orders （查询订单）【高优先级】

**新增原因**：v3.0 中没有查询订单接口，v4.1 新增用户查询自己订单的功能

**接口详情**：
- **路径**: `GET /api/orders`
- **功能**: 查询当前登录用户的订单信息（支持分页）
- **请求参数**:
  | 名称 | 位置 | 类型 | 必选 | 说明 |
  |------|------|------|------|------|
  | page | query | integer | 否 | 页码，每页固定20条数据 |
  | Authorization | header | string | 否 | JWT Token |
- **响应数据**:
  ```json
  {
    "code": 0,
    "msg": "string",
    "data": {
      "id": 0,
      "campusId": 0,
      "buildingId": 0,
      "roomId": 0,
      "amount": 0,
      "status": "string",
      "expiresAt": "string",
      "createdAt": "string",
      "updatedAt": "string",
      "seatList": [
        {
          "id": 0,
          "row": 0,
          "col": 0,
          "lookPrice": "string"
        }
      ]
    }
  }
  ```

---

## 修改接口

### 1. POST /api/orders （创建订单）【高优先级】

**v3.0 版本**：
```json
// 请求
{
  "seatList": [
    {
      "id": "string",
      "version": 0
    }
  ]
}

// 响应
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": "string",
    "amount": 0,
    "status": "string",
    "expiresAt": "string"
  }
}
```

**v4.1 版本**：
```json
// 请求
{
  "campusId": 0,      // 新增
  "buildingId": 0,    // 新增
  "roomId": 0,        // 新增
  "seatList": [
    {
      "id": "string",
      "price": 0,      // 新增
      "version": 0
    }
  ]
}

// 响应
{
  "code": 0,
  "msg": "string",
  "data": {
    "orderId": "string"   // 简化响应
  }
}
```

**变更点**：
1. 请求新增 `campusId`、`buildingId`、`roomId` 字段
2. seatList 中的对象新增 `price` 字段
3. 响应简化，只返回 `orderId`

---

### 2. GET /api/rooms/{roomId}/seats （获取教室座位图）【中优先级】

**v3.0 版本**：
```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "totalRows": 0,
    "totalCols": 0,
    "seats": [
      {
        "id": "string",
        "row": 0,
        "col": 0,
        "status": 0,
        "version": 0
      }
    ]
  }
}
```

**v4.1 版本**：
```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "totalRows": 0,
    "totalCols": 0,
    "seats": [
      {
        "id": "string",
        "row": 0,
        "col": 0,
        "price": 0,      // 新增字段，单位：分
        "status": 0,
        "version": 0
      }
    ]
  }
}
```

**变更点**：
1. SeatVO 新增 `price` 字段（单位：分）

---

### 3. PATCH /api/orders/{orderId} （取消订单）【低优先级】

**v3.0 版本**：
- 订单超时时间：30分钟

**v4.1 版本**：
- 订单超时时间：10分钟

**变更点**：
1. 订单超时时间从 30 分钟改为 10 分钟

---

## 数据模型变更

### 1. Order 实体变更【高优先级】

**v3.0 版本**：
```json
{
  "id": 0,
  "userId": 0,
  "amount": 0,
  "status": "string",
  "expiresAt": "string"
}
```

**v4.1 版本**：
```json
{
  "id": 0,
  "userId": 0,
  "campusId": 0,       // 新增
  "buildingId": 0,     // 新增
  "roomId": 0,         // 新增
  "amount": 0,
  "status": "string",
  "expiresAt": "string",
  "createdAt": "string",   // 新增
  "updatedAt": "string"    // 新增
}
```

**变更点**：
1. 新增 `campusId` 字段（校区ID）
2. 新增 `buildingId` 字段（教学楼ID）
3. 新增 `roomId` 字段（教室ID）
4. 新增 `createdAt` 字段（创建时间）
5. 新增 `updatedAt` 字段（更新时间）

---

### 2. Seat 实体变更【高优先级】

**v3.0 版本**：
```json
{
  "id": 0,
  "roomId": 0,
  "row": 0,
  "col": 0,
  "status": 0,
  "version": 0
}
```

**v4.1 版本**：
```json
{
  "id": 0,
  "roomId": 0,
  "row": 0,
  "col": 0,
  "price": 0,       // 新增
  "status": 0,
  "version": 0
}
```

**变更点**：
1. 新增 `price` 字段（座位价格，单位：分）

---

### 3. SeatVO 变更【中优先级】

**v3.0 版本**：
```json
{
  "id": "string",
  "row": 0,
  "col": 0,
  "status": 0,
  "version": 0
}
```

**v4.1 版本**：
```json
{
  "id": "string",
  "row": 0,
  "col": 0,
  "price": 0,       // 新增
  "status": 0,
  "version": 0
}
```

**变更点**：
1. 新增 `price` 字段

---

### 4. SeatLockDTO 变更【高优先级】

**v3.0 版本**：
```json
{
  "id": "string",
  "version": 0
}
```

**v4.1 版本**：
```json
{
  "id": "string",
  "price": 0,       // 新增
  "version": 0
}
```

**变更点**：
1. 新增 `price` 字段（用于前端传递座位价格，后端需验证）

---

### 5. CreateOrderDTO 变更【高优先级】

**v3.0 版本**：
```json
{
  "seatList": [...]
}
```

**v4.1 版本**：
```json
{
  "campusId": 0,       // 新增
  "buildingId": 0,     // 新增
  "roomId": 0,         // 新增
  "seatList": [...]
}
```

**变更点**：
1. 新增 `campusId` 字段
2. 新增 `buildingId` 字段
3. 新增 `roomId` 字段

---

### 6. 新增数据模型【中优先级】

#### Campus（校区）
```json
{
  "id": 0,
  "name": "string"
}
```

#### OrderSeatVO（订单座位VO）
```json
{
  "id": 0,
  "row": 0,
  "col": 0,
  "lookPrice": "string"
}
```

#### OrderListVO（订单列表VO，替代原 OrderVO）
```json
{
  "id": 0,
  "campusId": 0,
  "buildingId": 0,
  "roomId": 0,
  "amount": 0,
  "status": "string",
  "expiresAt": "string",
  "createdAt": "string",
  "updatedAt": "string",
  "seatList": [
    {
      "id": 0,
      "row": 0,
      "col": 0,
      "lookPrice": "string"
    }
  ]
}
```

---

### 7. 删除数据模型

| 模型 | 说明 |
|------|------|
| OrderVO | 被 OrderListVO 替代 |

---

### 8. 用户认证状态扩展【中优先级】

**v3.0 版本**：
- `verifyStatus`: 0（未认证），1（已认证）

**v4.1 版本**：
- `verifyStatus`: 0（未认证），1（审核中），2（已认证）

**变更点**：
1. 新增"审核中"状态

---

## 优先级修改建议

### 🔴 高优先级（必须实现，影响核心功能）

| 序号 | 变更项 | 涉及文件 | 工作量 | 风险 | 说明 |
|------|--------|----------|--------|------|------|
| 1 | 新增用户登录接口 | UserController, UserService, WechatUtil | 中 | 中 | 核心功能，影响用户使用 |
| 2 | Order表新增字段 | Order.java, schema.sql, mock-data.sql | 中 | 高 | 数据库结构变更 |
| 3 | Seat表新增price字段 | Seat.java, schema.sql, mock-data.sql | 中 | 高 | 数据库结构变更 |
| 4 | CreateOrderDTO新增字段 | CreateOrderDTO.java, OrderServiceImpl | 中 | 高 | 接口参数变更 |
| 5 | SeatLockDTO新增price字段 | SeatLockDTO.java, OrderServiceImpl | 中 | 高 | 价格验证逻辑 |
| 6 | 创建订单响应简化 | OrderController, OrderServiceImpl | 低 | 低 | 接口响应变更 |
| 7 | 新增查询订单接口 | OrderController, OrderService | 中 | 低 | 新增功能 |
| 8 | 价格验证逻辑 | OrderServiceImpl | 中 | 高 | 防止价格篡改 |

### 🟡 中优先级（建议实现，影响用户体验）

| 序号 | 变更项 | 涉及文件 | 工作量 | 风险 | 说明 |
|------|--------|----------|--------|------|------|
| 9 | SeatVO新增price字段 | SeatVO.java, OrderServiceImpl | 低 | 中 | 前端显示价格 |
| 10 | 新增OrderListVO | OrderListVO.java, OrderSeatVO.java | 低 | 低 | 替代原OrderVO |
| 11 | 扩展用户认证状态 | User.java, UserServiceImpl | 低 | 中 | 新增审核中状态 |
| 12 | 新增Campus实体 | Campus.java, CampusMapper | 低 | 低 | 校区数据模型 |

### 🟢 低优先级（可选实现，优化项）

| 序号 | 变更项 | 涉及文件 | 工作量 | 风险 | 说明 |
|------|--------|----------|--------|------|------|
| 13 | 订单超时时间调整 | OrderServiceImpl, OrderTimeoutTask | 低 | 低 | 30分钟改为10分钟 |
| 14 | 更新接口文档 | README.md, 接口详细文档.md | 低 | 无 | 文档同步 |
| 15 | 更新测试脚本 | VR教室API测试.ipynb | 低 | 无 | 测试同步 |

---

## 数据库变更脚本

### 1. Order 表新增字段

```sql
-- 备份原数据
CREATE TABLE `order_backup` AS SELECT * FROM `order`;

-- 新增字段
ALTER TABLE `order` 
  ADD COLUMN campus_id INTEGER COMMENT '校区ID' AFTER user_id,
  ADD COLUMN building_id INTEGER COMMENT '教学楼ID' AFTER campus_id,
  ADD COLUMN room_id INTEGER COMMENT '教室ID' AFTER building_id,
  ADD COLUMN created_at BIGINT COMMENT '创建时间戳' AFTER expires_at,
  ADD COLUMN updated_at BIGINT COMMENT '更新时间戳' AFTER created_at;

-- 添加索引
ALTER TABLE `order` 
  ADD INDEX idx_campus_id (campus_id),
  ADD INDEX idx_building_id (building_id),
  ADD INDEX idx_room_id (room_id);

-- 数据迁移（如果有历史数据，需要从seat表反推）
-- UPDATE `order` o 
-- JOIN order_seat os ON o.id = os.order_id 
-- JOIN seat s ON os.seat_id = s.id 
-- SET o.room_id = s.room_id;
```

### 2. Seat 表新增 price 字段

```sql
-- 备份原数据
CREATE TABLE `seat_backup` AS SELECT * FROM `seat`;

-- 新增 price 字段
ALTER TABLE `seat` 
  ADD COLUMN price INTEGER NOT NULL DEFAULT 10000 COMMENT '座位价格（分）' AFTER col;

-- 更新现有数据（示例：设置不同价格）
UPDATE `seat` SET price = 10000 WHERE `row` <= 5;        -- 前5排 100元
UPDATE `seat` SET price = 8000 WHERE `row` > 5 AND `row` <= 10;   -- 6-10排 80元
UPDATE `seat` SET price = 5000 WHERE `row` > 10;         -- 后排 50元
```

### 3. User 表认证状态字段说明更新

```sql
-- 更新字段注释
ALTER TABLE `user` 
  MODIFY COLUMN verify_status TINYINT DEFAULT 0 COMMENT '认证状态：0-未认证，1-审核中，2-已认证';
```

---

## 风险评估

| 风险项 | 风险等级 | 影响范围 | 缓解措施 |
|--------|----------|----------|----------|
| Order表结构变更导致历史数据丢失 | 高 | 订单模块 | 备份数据，逐步迁移 |
| Seat表新增price字段导致数据不一致 | 高 | 订单模块 | 设置默认价格，逐步调整 |
| 价格验证逻辑漏洞 | 高 | 订单模块 | 严格验证前端价格与数据库价格 |
| 用户登录接口安全问题 | 中 | 用户模块 | 验证微信API调用，防止伪造 |
| 认证状态扩展导致兼容性问题 | 中 | 用户模块 | 更新所有使用verifyStatus的地方 |
| 订单超时时间缩短影响用户体验 | 低 | 订单模块 | 前端提示用户尽快支付 |

---

## 实现建议

### 1. 价格验证逻辑（关键安全点）

```java
public String createOrder(CreateOrderDTO dto, Integer userId) {
    // 1. 查询座位实际价格
    List<Integer> seatIds = dto.getSeatList().stream()
        .map(SeatLockDTO::getId)
        .collect(Collectors.toList());
    
    List<Seat> seats = seatMapper.selectBatchIds(seatIds);
    Map<Integer, Integer> seatPriceMap = seats.stream()
        .collect(Collectors.toMap(Seat::getId, Seat::getPrice));
    
    // 2. 验证前端传递的价格是否正确
    int totalAmount = 0;
    for (SeatLockDTO lockDTO : dto.getSeatList()) {
        Integer actualPrice = seatPriceMap.get(Integer.valueOf(lockDTO.getId()));
        if (actualPrice == null) {
            throw new BusinessException(400, "座位不存在: " + lockDTO.getId());
        }
        if (!actualPrice.equals(lockDTO.getPrice())) {
            throw new BusinessException(400, "座位价格异常，请刷新页面重试");
        }
        totalAmount += actualPrice;
    }
    
    // 3. 创建订单...
}
```

### 2. 用户登录接口实现

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    String loginCode = request.get("loginCode");
    String phoneCode = request.get("phoneCode");
    
    // 1. 获取 openId
    Map<String, String> session = wechatUtil.getOpenIdAndSessionKey(loginCode);
    String openId = session.get("openId");
    
    // 2. 获取手机号
    String phone = wechatUtil.getPhoneNumber(phoneCode);
    
    // 3. 查询或创建用户
    User user = userService.getUserByOpenId(openId);
    if (user == null) {
        user = new User();
        user.setOpenId(openId);
        user.setPhone(phone);
        user.setName("微信用户");
        user.setAvatar("assets/default_avatar.png");
        user.setVerifyStatus(0);
        user = userService.saveUser(user);
    } else {
        user.setPhone(phone);
        userService.updateUser(user);
    }
    
    // 4. 生成 Token
    String token = jwtUtil.generateToken(user.getId());
    
    // 5. 返回数据
    Map<String, Object> data = new HashMap<>();
    data.put("token", token);
    data.put("user", userService.getUserProfile(user.getId()));
    
    return ResponseEntity.ok(ApiResponse.success(data));
}
```

---

## 变更汇总表

| 类别 | 变更项 | v3.0 | v4.1 | 优先级 |
|------|--------|------|------|--------|
| **新增接口** | 用户登录 | 无 | POST /api/users/login | 高 |
| **新增接口** | 查询订单 | 无 | GET /api/orders | 高 |
| **Order实体** | campusId | 无 | 新增 | 高 |
| **Order实体** | buildingId | 无 | 新增 | 高 |
| **Order实体** | roomId | 无 | 新增 | 高 |
| **Order实体** | createdAt | 无 | 新增 | 高 |
| **Order实体** | updatedAt | 无 | 新增 | 高 |
| **Seat实体** | price | 无 | 新增 | 高 |
| **SeatVO** | price | 无 | 新增 | 中 |
| **SeatLockDTO** | price | 无 | 新增 | 高 |
| **CreateOrderDTO** | campusId | 无 | 新增 | 高 |
| **CreateOrderDTO** | buildingId | 无 | 新增 | 高 |
| **CreateOrderDTO** | roomId | 无 | 新增 | 高 |
| **创建订单响应** | 响应结构 | OrderVO | {orderId} | 高 |
| **用户认证状态** | verifyStatus | 0/1 | 0/1/2 | 中 |
| **订单超时** | 超时时间 | 30分钟 | 10分钟 | 低 |
| **新增模型** | Campus | 无 | 新增 | 中 |
| **新增模型** | OrderSeatVO | 无 | 新增 | 中 |
| **新增模型** | OrderListVO | 无 | 新增 | 中 |
| **删除模型** | OrderVO | 存在 | 删除 | 中 |

---

**文档版本**: 2.0  
**创建日期**: 2026-03-03  
**对比版本**: v4.1 vs v3.0  
**审核状态**: 待审核

---

**注意**：本文档列出的所有变更需经审核确认后再进行代码更新。建议按优先级顺序实施，高优先级变更必须完成后再进行中低优先级变更。
