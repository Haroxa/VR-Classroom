---
title: 默认模块
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# 默认模块

Base URLs:

# Authentication

# 捐赠

## GET 获取教室座位图

GET /api/rooms/{roomId}/seats

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|roomId|path|string| 是 |none|

> 返回示例

> 200 Response

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

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» msg|string|false|none||none|
|» data|[RoomSeatVO](#schemaroomseatvo)|false|none||none|
|»» totalRows|integer|true|none||总行数|
|»» totalCols|integer|true|none||总列数|
|»» seats|[[SeatVO](#schemaseatvo)]|true|none||none|
|»»» id|string|true|none||座位id|
|»»» row|integer|true|none||行号|
|»»» col|integer|true|none||列号|
|»»» status|integer|true|none||0=过道, 1=空闲(可买), 2=已锁定(待支付), 3=已售出|
|»»» version|integer|true|none||乐观锁，放并发|

## POST 创建订单

POST /api/orders

> Body 请求参数

```json
{
  "seatList": [
    {
      "id": "string",
      "version": 0
    }
  ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |none|
|body|body|[CreateOrderDTO](#schemacreateorderdto)| 是 |none|

> 返回示例

> 200 Response

```json
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

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» msg|string|false|none||none|
|» data|[OrderVO](#schemaordervo)|false|none||none|
|»» id|string|true|none||订单号，推荐用雪花算法或时间戳+随机数？|
|»» amount|integer|true|none||订单总金额(单位：分)|
|»» status|string|true|none||PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)|
|»» expiresAt|string|true|none||订单超时时间（创建时间+10分钟）|

## PATCH 取消订单

PATCH /api/orders/{orderId}

> Body 请求参数

```json
{
  "status": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|orderId|path|string| 是 |none|
|Authorization|header|string| 否 |none|
|body|body|[UpdateOrderDTO](#schemaupdateorderdto)| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» msg|string|false|none||none|
|» data|object|false|none||none|

## POST 模拟支付成功回调

POST /api/mock/pay/notify

> Body 请求参数

```json
{
  "orderId": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 是 |none|
|» orderId|body|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {}
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» msg|string|false|none||none|
|» data|object|false|none||none|

# 数据模型

<h2 id="tocS_Room">Room</h2>

<a id="schemaroom"></a>
<a id="schema_Room"></a>
<a id="tocSroom"></a>
<a id="tocsroom"></a>

```json
{
  "id": 0,
  "buildingId": 0,
  "roomNumber": "string",
  "totalRows": 0,
  "totalCols": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||教室id|
|buildingId|integer|true|none||关联的building|
|roomNumber|string|true|none||门牌号|
|totalRows|integer|true|none||总行数|
|totalCols|integer|true|none||总列数|

<h2 id="tocS_Seat">Seat</h2>

<a id="schemaseat"></a>
<a id="schema_Seat"></a>
<a id="tocSseat"></a>
<a id="tocsseat"></a>

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

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||座位id|
|roomId|integer|true|none||关联的教室id|
|row|integer|true|none||行号|
|col|integer|true|none||列号|
|status|integer|true|none||0=过道, 1=空闲(可买), 2=已锁定(待支付), 3=已售出|
|version|integer|true|none||乐观锁，放并发|

<h2 id="tocS_Order">Order</h2>

<a id="schemaorder"></a>
<a id="schema_Order"></a>
<a id="tocSorder"></a>
<a id="tocsorder"></a>

```json
{
  "id": 0,
  "userId": 0,
  "amount": 0,
  "status": "string",
  "expiresAt": "string",
  "createdAt": "string",
  "updatedAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||订单号，推荐用雪花算法或时间戳+随机数？|
|userId|integer|true|none||下单用户id|
|amount|integer|true|none||订单总金额(单位：分)|
|status|string|true|none||PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)|
|expiresAt|string|true|none||订单超时时间（创建时间+10分钟）|
|createdAt|string|true|none||订单创建时间|
|updatedAt|string|true|none||订单更新时间，存时间戳，上同|

<h2 id="tocS_SeatVO">SeatVO</h2>

<a id="schemaseatvo"></a>
<a id="schema_SeatVO"></a>
<a id="tocSseatvo"></a>
<a id="tocsseatvo"></a>

```json
{
  "id": "string",
  "row": 0,
  "col": 0,
  "status": 0,
  "version": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||座位id|
|row|integer|true|none||行号|
|col|integer|true|none||列号|
|status|integer|true|none||0=过道, 1=空闲(可买), 2=已锁定(待支付), 3=已售出|
|version|integer|true|none||乐观锁，放并发|

<h2 id="tocS_RoomSeatVO">RoomSeatVO</h2>

<a id="schemaroomseatvo"></a>
<a id="schema_RoomSeatVO"></a>
<a id="tocSroomseatvo"></a>
<a id="tocsroomseatvo"></a>

```json
{
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

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|totalRows|integer|true|none||总行数|
|totalCols|integer|true|none||总列数|
|seats|[[SeatVO](#schemaseatvo)]|true|none||none|

<h2 id="tocS_OrderVO">OrderVO</h2>

<a id="schemaordervo"></a>
<a id="schema_OrderVO"></a>
<a id="tocSordervo"></a>
<a id="tocsordervo"></a>

```json
{
  "id": "string",
  "amount": 0,
  "status": "string",
  "expiresAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||订单号，推荐用雪花算法或时间戳+随机数？|
|amount|integer|true|none||订单总金额(单位：分)|
|status|string|true|none||PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)|
|expiresAt|string|true|none||订单超时时间（创建时间+10分钟）|

<h2 id="tocS_CreateOrderDTO">CreateOrderDTO</h2>

<a id="schemacreateorderdto"></a>
<a id="schema_CreateOrderDTO"></a>
<a id="tocScreateorderdto"></a>
<a id="tocscreateorderdto"></a>

```json
{
  "seatList": [
    {
      "id": "string",
      "version": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|seatList|[[SeatLockDTO](#schemaseatlockdto)]|true|none||none|

<h2 id="tocS_UpdateOrderDTO">UpdateOrderDTO</h2>

<a id="schemaupdateorderdto"></a>
<a id="schema_UpdateOrderDTO"></a>
<a id="tocSupdateorderdto"></a>
<a id="tocsupdateorderdto"></a>

```json
{
  "status": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|status|string|true|none||PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)|

<h2 id="tocS_SeatLockDTO">SeatLockDTO</h2>

<a id="schemaseatlockdto"></a>
<a id="schema_SeatLockDTO"></a>
<a id="tocSseatlockdto"></a>
<a id="tocsseatlockdto"></a>

```json
{
  "id": "string",
  "version": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||座位id|
|version|integer|true|none||乐观锁，放并发|

