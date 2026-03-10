---
title: VR教室API文档
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"
---

# 概述

Base URL: `http://localhost:8082/api`

认证方式：JWT Token (Bearer Token)

项目采用RESTful API风格，所有接口返回统一的数据结构。

# 通用说明

## 通用响应结构

所有接口返回统一格式：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，0表示成功 |
| msg | String | 消息 |
| data | Object | 响应数据 |

## 分页说明

所有分页接口使用以下参数：
- `page`: 页码，默认1
- `pageSize`: 每页大小，默认20

响应分页数据结构：
```json
{
  "current": 1,
  "total": 5,
  "records": []
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| current | Integer | 当前页码 |
| total | Integer | 总页数 |
| records | Array | 数据列表 |

## 认证说明

除公开接口外，其他接口需要在请求头中携带JWT Token：

```
Authorization: Bearer {token}
```

# 公开接口

## GET 公开帖子列表

GET /posts

获取公开可见的帖子列表，支持分页、分类筛选和关键词搜索。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |
| categoryId | query | Integer | 否 | 分类ID，不传则查全部 |
| keyword | query | String | 否 | 搜索关键词 |
| Authorization | header | String | 否 | 用于判断isLiked，不传默认未登录 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 5,
    "records": [
      {
        "id": "288590991759335400",
        "date": "2026-03-07 16:37:34",
        "title": "VR技术在教育领域的应用",
        "summary": "VR技术正在改变传统教育方式...",
        "images": ["assets/posts/2026/03/07/xxx.jpg"],
        "likeCount": 10,
        "shareCount": 5,
        "commentCount": 8,
        "categoryName": "技术分享",
        "isLiked": false,
        "author": {
          "id": "1",
          "name": "张三",
          "avatar": "assets/default_avatar.png",
          "collegeId": "1",
          "collegeName": "计算机学院",
          "isVerified": true
        }
      }
    ]
  }
}
```

### 返回字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 帖子id |
| date | String | 发布时间 |
| title | String | 帖子标题 |
| summary | String | 帖子摘要，内容前50字 |
| images | Array | 帖子图片URL Key列表 |
| likeCount | Integer | 点赞数 |
| shareCount | Integer | 分享数 |
| commentCount | Integer | 评论数 |
| categoryName | String | 分类名称 |
| isLiked | Boolean | 当前用户是否点赞 |
| author | Object | 作者信息 |

## GET 帖子详情

GET /posts/{id}

获取帖子详情，若非作者本人则status和rejectReason返回null。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| id | path | String | 是 | 帖子ID |
| Authorization | header | String | 否 | 用于判断isLiked及是否为作者本人 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": "288590991759335400",
    "date": "2026-03-07 16:37:34",
    "title": "VR技术在教育领域的应用",
    "content": "详细内容...",
    "images": ["assets/posts/2026/03/07/xxx.jpg"],
    "likeCount": 10,
    "shareCount": 5,
    "commentCount": 8,
    "status": 1,
    "rejectReason": null,
    "categoryName": "技术分享",
    "author": {
      "id": "1",
      "name": "张三",
      "avatar": "assets/default_avatar.png",
      "collegeId": "1",
      "collegeName": "计算机学院",
      "isVerified": true
    },
    "isLiked": false
  }
}
```

### 返回字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| status | Integer/null | 状态：0待审核，1已发布，2已驳回 |
| rejectReason | String/null | 驳回理由 |
| isLiked | Boolean | 当前用户是否点赞 |

## GET 帖子评论列表

GET /comments

获取指定帖子的评论列表。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| postId | query | Integer | 是 | 帖子ID |
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |
| Authorization | header | String | 否 | 用于判断isLiked |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 3,
    "records": [
      {
        "id": "123456789",
        "date": "2026-03-07 17:00:00",
        "content": "很有价值的分享！",
        "commenterId": 1,
        "likeCount": 5,
        "isLiked": false,
        "commenter": {
          "id": "1",
          "name": "李四",
          "avatar": "assets/default_avatar.png",
          "collegeId": "1",
          "collegeName": "计算机学院",
          "isVerified": false
        }
      }
    ]
  }
}
```

## GET 获取教室座位信息

GET /rooms/{roomId}/seats

获取指定教室的座位分布信息。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| roomId | path | String | 是 | 教室ID |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "totalRows": 10,
    "totalCols": 15,
    "seats": [
      {
        "id": "1",
        "row": 1,
        "col": 1,
        "price": 100000,
        "status": 1,
        "version": 0
      }
    ]
  }
}
```

### 座位状态说明

| 状态值 | 说明 |
|--------|------|
| 0 | 过道 |
| 1 | 空闲(可买) |
| 2 | 已锁定(待支付) |
| 3 | 已售出 |

# 用户模块

## POST 手机号登录

POST /users/login/phone

使用手机号登录，无需验证码。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| phone | body | String | 是 | 手机号，格式：13800138000 |

### 请求示例

```json
{
  "phone": "13800138000"
}
```

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "user": {
      "id": "1",
      "phone": "13800138000",
      "name": "用户8000",
      "avatar": "assets/default_avatar.png",
      "collegeId": "1",
      "verifyStatus": 0,
      "collegeName": "计算机学院"
    }
  }
}
```

### 认证状态说明

| 状态值 | 说明 |
|--------|------|
| 0 | 未认证 |
| 1 | 审核中 |
| 2 | 已认证 |

## GET 我的帖子列表

GET /users/posts

获取当前用户发布的帖子列表。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 2,
    "records": [
      {
        "id": "288590991759335400",
        "date": "2026-03-07 16:37:34",
        "title": "我的VR体验",
        "summary": "今天体验了VR设备...",
        "images": [],
        "likeCount": 5,
        "shareCount": 2,
        "commentCount": 3,
        "status": 1,
        "rejectReason": null,
        "categoryName": "经验分享",
        "isLiked": false
      }
    ]
  }
}
```

### 帖子状态说明

| 状态值 | 说明 |
|--------|------|
| -1 | 已删除 |
| 0 | 待审核 |
| 1 | 已发布 |
| 2 | 已驳回 |

## GET 我的评论列表

GET /users/comments

获取当前用户发布的评论列表。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 3,
    "records": [
      {
        "id": "123456789",
        "date": "2026-03-07 17:00:00",
        "content": "很有价值的分享！",
        "likeCount": 5,
        "status": 1,
        "rejectReason": null,
        "isLiked": false,
        "relatedPost": {
          "id": "288590991759335400",
          "title": "VR技术在教育领域的应用",
          "status": 1
        }
      }
    ]
  }
}
```

## GET 我点赞的帖子列表

GET /users/liked-posts

获取当前用户点赞的帖子列表。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 2,
    "records": [
      {
        "id": "288590991759335400",
        "date": "2026-03-07 16:37:34",
        "title": "VR技术在教育领域的应用",
        "summary": "VR技术正在改变...",
        "images": [],
        "likeCount": 10,
        "shareCount": 5,
        "commentCount": 8,
        "categoryName": "技术分享",
        "isLiked": true,
        "author": {
          "id": "1",
          "name": "张三",
          "avatar": "assets/default_avatar.png",
          "collegeId": "1",
          "collegeName": "计算机学院",
          "isVerified": true
        }
      }
    ]
  }
}
```

## GET 我点赞的评论列表

GET /users/liked-comments

获取当前用户点赞的评论列表。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 1,
    "records": [
      {
        "id": "123456789",
        "date": "2026-03-07 17:00:00",
        "content": "很有价值的分享！",
        "likeCount": 5,
        "status": 1,
        "rejectReason": null,
        "isLiked": true,
        "relatedPost": {
          "id": "288590991759335400",
          "title": "VR技术在教育领域的应用",
          "status": 1
        }
      }
    ]
  }
}
```

## POST 发布新帖子

POST /posts

发布新帖子，需要登录认证。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| title | body | String | 是 | 帖子标题 |
| content | body | String | 是 | 帖子内容 |
| images | body | Array | 否 | 图片URL Key列表 |
| categoryId | body | Integer | 否 | 分类ID |

### 请求示例

```json
{
  "title": "VR技术在教育领域的应用",
  "content": "VR技术正在改变传统教育方式...",
  "images": ["assets/posts/2026/03/07/xxx.jpg"],
  "categoryId": 1
}
```

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": "288590991759335400"
}
```

## PUT 修改帖子

PUT /posts/{id}

修改帖子，需要是作者本人。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| id | path | String | 是 | 帖子ID |
| Authorization | header | String | 是 | Bearer token |
| title | body | String | 是 | 帖子标题 |
| content | body | String | 是 | 帖子内容 |
| images | body | Array | 否 | 图片URL Key列表 |
| categoryId | body | Integer | 否 | 分类ID |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## DELETE 删除帖子

DELETE /posts/{id}

删除帖子，需要是作者本人。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| id | path | String | 是 | 帖子ID |
| Authorization | header | String | 是 | Bearer token |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## POST 发布新评论

POST /comments

发布新评论，需要登录认证。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| content | body | String | 是 | 评论内容 |
| postId | body | Integer | 是 | 帖子ID |

### 请求示例

```json
{
  "content": "很有价值的分享！",
  "postId": 288590991759335400
}
```

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": "123456789"
}
```

## PUT 修改评论

PUT /comments/{id}

修改评论，需要是评论发布者本人。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| id | path | String | 是 | 评论ID |
| Authorization | header | String | 是 | Bearer token |
| content | body | String | 是 | 评论内容 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## DELETE 删除评论

DELETE /comments/{id}

删除评论，需要是评论发布者本人。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| id | path | String | 是 | 评论ID |
| Authorization | header | String | 是 | Bearer token |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## POST 点赞帖子

POST /posts/{postId}/likes

点赞帖子，幂等操作。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| postId | path | String | 是 | 帖子ID |
| Authorization | header | String | 否 | Bearer token，不传则无法记录点赞用户 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 288590991759335400,
    "likeCount": 11,
    "isLiked": true
  }
}
```

## DELETE 取消点赞帖子

DELETE /posts/{postId}/likes

取消点赞帖子，幂等操作。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| postId | path | String | 是 | 帖子ID |
| Authorization | header | String | 否 | Bearer token |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 288590991759335400,
    "likeCount": 10,
    "isLiked": false
  }
}
```

## POST 点赞评论

POST /comments/{commentId}/likes

点赞评论，幂等操作。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| commentId | path | String | 是 | 评论ID |
| Authorization | header | String | 否 | Bearer token |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 123456789,
    "likeCount": 6,
    "isLiked": true
  }
}
```

## DELETE 取消点赞评论

DELETE /comments/{commentId}/likes

取消点赞评论，幂等操作。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| commentId | path | String | 是 | 评论ID |
| Authorization | header | String | 否 | Bearer token |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 123456789,
    "likeCount": 5,
    "isLiked": false
  }
}
```

## POST 上传帖子图片

POST /api/images/post

上传帖子图片，返回图片URL。需要登录认证。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| file | body | file | 是 | 图片文件，支持 jpg、jpeg、png、gif、webp，最大5MB |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "url": "assets/posts/2026/03/07/1a1b2c3d.jpg"
  }
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 文件为空、文件过大（>5MB）、不支持的文件类型 |
| 401 | 未登录或token无效 |
| 500 | 服务器内部错误，文件保存失败 |

### 文件存储说明

- **本地开发环境**：文件存储在项目根目录的 `uploads` 文件夹下
- **服务器环境**：文件存储在配置的服务器路径下（通过 `FILE_UPLOAD_SERVER_PATH` 环境变量配置）
- **文件命名格式**：`{userId}{8位随机字符串}.{扩展名}`
- **文件路径格式**：`posts/{yyyy}/{MM}/{dd}/{filename}`
- **访问URL**：`http://localhost:8082/assets/posts/{yyyy}/{MM}/{dd}/{filename}`

# 数据查询模块

## GET 获取所有校区

GET /campuses

获取所有校区列表。

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "主校区"
    },
    {
      "id": 2,
      "name": "东校区"
    }
  ]
}
```

## GET 获取校区下的楼栋

GET /campuses/{campusId}/buildings

获取指定校区下的楼栋列表。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| campusId | path | Integer | 是 | 校区ID |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "campusId": 1,
      "name": "第一教学楼"
    }
  ]
}
```

## GET 获取所有楼栋

GET /buildings

获取所有楼栋列表。

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "campusId": 1,
      "name": "第一教学楼"
    }
  ]
}
```

## GET 获取楼栋下的教室

GET /buildings/{buildingId}/rooms

获取指定楼栋下的教室列表。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| buildingId | path | Integer | 是 | 楼栋ID |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "buildingId": 1,
      "roomNumber": "101",
      "totalRows": 10,
      "totalCols": 15
    }
  ]
}
```

## GET 获取所有教室

GET /rooms

获取所有教室列表。

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "buildingId": 1,
      "roomNumber": "101",
      "totalRows": 10,
      "totalCols": 15
    }
  ]
}
```

## GET 获取教室详情

GET /rooms/{roomId}

获取指定教室的详细信息。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| roomId | path | Integer | 是 | 教室ID |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "buildingId": 1,
    "roomNumber": "101",
    "totalRows": 10,
    "totalCols": 15
  }
}
```

## GET 获取所有分类

GET /categories

获取所有帖子分类列表。

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "技术分享"
    },
    {
      "id": 2,
      "name": "经验分享"
    }
  ]
}
```

## GET 获取所有学院

GET /colleges

获取所有学院列表。

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "计算机学院"
    },
    {
      "id": 2,
      "name": "信息工程学院"
    }
  ]
}
```

# 订单模块

## GET 查询订单列表

GET /orders

获取当前用户的订单列表，需要登录认证。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 1,
    "records": [
      {
        "id": 288590991759335400,
        "campusId": 1,
        "buildingId": 1,
        "roomId": 1,
        "amount": 100000,
        "status": "PAID",
        "expiresAt": "2026-03-04 10:30:00",
        "createdAt": "2026-03-04 10:20:00",
        "updatedAt": "2026-03-04 10:25:00",
        "seatList": [
          {
            "id": 1,
            "row": 1,
            "col": 1,
            "lookPrice": "100000"
          }
        ]
      }
    ]
  }
}
```

### 订单状态说明

| 状态值 | 说明 |
|--------|------|
| PENDING | 待支付 |
| PAID | 已支付 |
| CANCELLED | 已取消 |
| REFUNDED | 已退款 |

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 订单号 |
| campusId | Integer | 校区ID |
| buildingId | Integer | 教学楼ID |
| roomId | Integer | 教室ID |
| amount | Integer | 订单总金额(单位：分) |
| seatList | Array | 座位列表 |
| seatList[].row | Integer | 行号 |
| seatList[].col | Integer | 列号 |
| seatList[].lookPrice | String | 下单时的价格快照(单位：分) |

## GET 查询订单详情

GET /orders/{orderId}

获取订单详情，需要登录认证且必须是订单所有者。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| orderId | path | String | 是 | 订单ID |
| Authorization | header | String | 是 | Bearer token |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 288590991759335400,
    "campusId": 1,
    "buildingId": 1,
    "roomId": 1,
    "amount": 100000,
    "status": "PAID",
    "expiresAt": "2026-03-04 10:30:00",
    "createdAt": "2026-03-04 10:20:00",
    "updatedAt": "2026-03-04 10:25:00",
    "seatList": [
      {
        "id": 1,
        "row": 1,
        "col": 1,
        "lookPrice": "100000"
      }
    ]
  }
}
```

## POST 创建订单

POST /orders

创建订单，需要登录认证。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |
| campusId | body | Integer | 是 | 校区ID |
| buildingId | body | Integer | 是 | 教学楼ID |
| roomId | body | Integer | 是 | 教室ID |
| seatList | body | Array | 是 | 座位列表 |

### 请求示例

```json
{
  "campusId": 1,
  "buildingId": 1,
  "roomId": 1,
  "seatList": [
    {
      "id": "1",
      "price": 100000,
      "version": 0
    }
  ]
}
```

### seatList字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 座位ID |
| price | Integer | 价格(单位：分) |
| version | Integer | 座位版本号(乐观锁) |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "orderId": "288590991759335400"
  }
}
```

## PATCH 取消订单

PATCH /orders/{orderId}

取消订单，需要是订单所有者。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| orderId | path | String | 是 | 订单ID |
| Authorization | header | String | 是 | Bearer token |
| status | body | String | 是 | 订单状态，传入CANCELLED |

### 请求示例

```json
{
  "status": "CANCELLED"
}
```

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## POST 模拟支付成功回调

POST /mock/pay/notify

模拟支付成功回调接口，用于测试。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| orderId | body | String | 是 | 订单ID |

### 请求示例

```json
{
  "orderId": "288590991759335400"
}
```

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

# 后台管理模块

## GET 帖子列表

GET /admin/posts

获取帖子列表（后台审核用），需要管理员权限。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |
| status | query | Integer | 否 | 状态，不传则查全部 |
| categoryId | query | Integer | 否 | 分类ID，不传则查全部 |
| keyword | query | String | 否 | 搜索关键词 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 10,
    "records": [
      {
        "id": "288590991759335400",
        "date": "2026-03-07 16:37:34",
        "title": "VR技术在教育领域的应用",
        "summary": "VR技术正在改变...",
        "status": 0,
        "rejectReason": null,
        "categoryName": "技术分享",
        "author": {
          "id": "1",
          "name": "张三",
          "avatar": "assets/default_avatar.png",
          "collegeId": "1",
          "collegeName": "计算机学院",
          "isVerified": true
        }
      }
    ]
  }
}
```

### 帖子状态说明

| 状态值 | 说明 |
|--------|------|
| 0 | 待审核 |
| 1 | 已发布 |
| 2 | 已驳回 |

## GET 评论列表

GET /admin/comments

获取评论列表（后台审核用），需要管理员权限。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| page | query | Integer | 否 | 页码，默认1 |
| pageSize | query | Integer | 否 | 每页大小，默认20 |
| status | query | Integer | 否 | 状态，不传则查全部 |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "current": 1,
    "total": 5,
    "records": [
      {
        "id": "123456789",
        "date": "2026-03-07 17:00:00",
        "content": "很有价值的分享！",
        "status": 0,
        "rejectReason": null,
        "commenter": {
          "id": "1",
          "name": "李四",
          "avatar": "assets/default_avatar.png",
          "collegeId": "1",
          "collegeName": "计算机学院",
          "isVerified": false
        },
        "relatedPost": {
          "id": "288590991759335400",
          "title": "VR技术在教育领域的应用",
          "status": 1
        }
      }
    ]
  }
}
```

### 评论状态说明

| 状态值 | 说明 |
|--------|------|
| 0 | 待审核 |
| 1 | 已发布 |
| 2 | 已驳回 |

## PATCH 审核帖子

PATCH /admin/posts/{id}

审核帖子，需要管理员权限。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| id | path | String | 是 | 帖子ID |
| status | body | Integer | 是 | 审核状态：1通过，2驳回 |
| rejectReason | body | String | 否 | 驳回理由 |

### 请求示例

```json
{
  "status": 1,
  "rejectReason": ""
}
```

或

```json
{
  "status": 2,
  "rejectReason": "内容不符合社区规范"
}
```

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## PATCH 审核评论

PATCH /admin/comments/{id}

审核评论，需要管理员权限。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| id | path | String | 是 | 评论ID |
| status | body | Integer | 是 | 审核状态：1通过，2驳回 |
| rejectReason | body | String | 否 | 驳回理由 |

### 请求示例

```json
{
  "status": 1,
  "rejectReason": ""
}
```

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

# 通用模块

## GET 获取阿里云OSS POST签名v4

GET /oss/sign

获取阿里云OSS上传签名。

### 请求参数

| 名称 | 位置 | 类型 | 必选 | 说明 |
|------|------|------|------|------|
| Authorization | header | String | 是 | Bearer token |

### 返回示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "signature": "...",
    "key": "...",
    "policy": "...",
    "accessKeyId": "...",
    "bucket": "...",
    "region": "cn-wuhan-lr"
  }
}
```

# 数据模型

## UserPublicVO

```json
{
  "id": "1",
  "name": "张三",
  "avatar": "assets/default_avatar.png",
  "collegeId": "1",
  "collegeName": "计算机学院",
  "isVerified": true
}
```

## UserProfileVO

```json
{
  "id": "1",
  "phone": "13800138000",
  "name": "张三",
  "avatar": "assets/default_avatar.png",
  "collegeId": "1",
  "verifyStatus": 2,
  "collegeName": "计算机学院"
}
```

## PostVO

```json
{
  "id": "288590991759335400",
  "date": "2026-03-07 16:37:34",
  "title": "VR技术在教育领域的应用",
  "summary": "VR技术正在改变...",
  "images": [],
  "likeCount": 10,
  "shareCount": 5,
  "commentCount": 8,
  "categoryName": "技术分享",
  "isLiked": false,
  "author": {}
}
```

## PostDetailVO

```json
{
  "id": "288590991759335400",
  "date": "2026-03-07 16:37:34",
  "title": "VR技术在教育领域的应用",
  "content": "详细内容...",
  "images": [],
  "likeCount": 10,
  "shareCount": 5,
  "commentCount": 8,
  "status": 1,
  "rejectReason": null,
  "categoryName": "技术分享",
  "author": {},
  "isLiked": false
}
```

## UserPostVO

```json
{
  "id": "288590991759335400",
  "date": "2026-03-07 16:37:34",
  "title": "VR技术在教育领域的应用",
  "summary": "VR技术正在改变...",
  "images": [],
  "likeCount": 10,
  "shareCount": 5,
  "commentCount": 8,
  "status": 1,
  "rejectReason": null,
  "categoryName": "技术分享",
  "isLiked": false
}
```

## CommentVO

```json
{
  "id": "123456789",
  "date": "2026-03-07 17:00:00",
  "content": "很有价值的分享！",
  "commenterId": 1,
  "likeCount": 5,
  "isLiked": false,
  "commenter": {}
}
```

## UserCommentVO

```json
{
  "id": "123456789",
  "date": "2026-03-07 17:00:00",
  "content": "很有价值的分享！",
  "likeCount": 5,
  "status": 1,
  "rejectReason": null,
  "isLiked": false,
  "relatedPost": {
    "id": "288590991759335400",
    "title": "VR技术在教育领域的应用",
    "status": 1
  }
}
```

## PostAuditVO

```json
{
  "id": "288590991759335400",
  "date": "2026-03-07 16:37:34",
  "title": "VR技术在教育领域的应用",
  "summary": "VR技术正在改变...",
  "status": 0,
  "rejectReason": null,
  "categoryName": "技术分享",
  "author": {}
}
```

## CommentAuditVO

```json
{
  "id": "123456789",
  "date": "2026-03-07 17:00:00",
  "content": "很有价值的分享！",
  "status": 0,
  "rejectReason": null,
  "commenter": {},
  "relatedPost": {
    "id": "288590991759335400",
    "title": "VR技术在教育领域的应用",
    "status": 1
  }
}
```

## RelatedPostVO

```json
{
  "id": "288590991759335400",
  "title": "VR技术在教育领域的应用",
  "status": 1
}
```

## RoomSeatVO

```json
{
  "totalRows": 10,
  "totalCols": 15,
  "seats": [
    {
      "id": "1",
      "row": 1,
      "col": 1,
      "price": 100000,
      "status": 1,
      "version": 0
    }
  ]
}
```

## SeatVO

```json
{
  "id": "1",
  "row": 1,
  "col": 1,
  "price": 100000,
  "status": 1,
  "version": 0
}
```

## OrderListVO

```json
{
  "id": 288590991759335400,
  "campusId": 1,
  "buildingId": 1,
  "roomId": 1,
  "amount": 100000,
  "status": "PAID",
  "expiresAt": "2026-03-04 10:30:00",
  "createdAt": "2026-03-04 10:20:00",
  "updatedAt": "2026-03-04 10:25:00",
  "seatList": [
    {
      "id": 1,
      "row": 1,
      "col": 1,
      "lookPrice": "100000"
    }
  ]
}
```

## OrderSeatVO

```json
{
  "id": 1,
  "row": 1,
  "col": 1,
  "lookPrice": "100000"
}
```

## PostLikeActionVO

```json
{
  "id": 288590991759335400,
  "likeCount": 11,
  "isLiked": true
}
```

## CommentLikeActionVO

```json
{
  "id": 123456789,
  "likeCount": 6,
  "isLiked": true
}
```

## PostCreateDTO

```json
{
  "title": "VR技术在教育领域的应用",
  "content": "VR技术正在改变传统教育方式...",
  "images": ["assets/posts/2026/03/07/xxx.jpg"],
  "categoryId": 1
}
```

## PostUpdateDTO

```json
{
  "title": "VR技术在教育领域的应用",
  "content": "VR技术正在改变传统教育方式...",
  "images": ["assets/posts/2026/03/07/xxx.jpg"],
  "categoryId": 1
}
```

## CommentCreateDTO

```json
{
  "content": "很有价值的分享！",
  "postId": 288590991759335400
}
```

## CommentUpdateDTO

```json
{
  "content": "修改后的评论内容"
}
```

## PostAuditDTO

```json
{
  "status": 1,
  "rejectReason": ""
}
```

## CommentAuditDTO

```json
{
  "status": 1,
  "rejectReason": ""
}
```

## CreateOrderDTO

```json
{
  "campusId": 1,
  "buildingId": 1,
  "roomId": 1,
  "seatList": [
    {
      "id": "1",
      "price": 100000,
      "version": 0
    }
  ]
}
```

## UpdateOrderDTO

```json
{
  "status": "CANCELLED"
}
```

# 错误码说明

| 错误码 | 说明 |
|--------|------|
| 0 | 成功 |
| 401 | 未认证 |
| 403 | 没有权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

# 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v5.0 | 2026-03 | 初始版本 |
| max | 2026-03 | 完善版本，统一分页参数为pageSize |
