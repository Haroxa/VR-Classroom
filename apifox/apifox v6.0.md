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

# 校友论坛

## GET 公开帖子列表

GET /api/posts

> Body 请求参数

```yaml
page: 0
categoryId: 0
keyword: ""

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |用于判断 isLiked，没传默认未登录|
|body|body|object| 是 |none|
|» page|body|integer| 是 |页码，每页固定20条数据|
|» categoryId|body|integer| 否 |分类id，没传则查全部|
|» keyword|body|string| 否 |搜索关键词|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "title": "string",
        "summary": "string",
        "images": [
          "string"
        ],
        "likeCount": 0,
        "shareCount": 0,
        "commentCount": 0,
        "categoryName": "string",
        "isLiked": true,
        "author": {
          "id": "string",
          "name": "未认证",
          "avatar": "assets/default_avatar.png",
          "collegeId": "string",
          "collegeName": "string",
          "isVerified": true
        }
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[PostVO](#schemapostvo)]|true|none||none|
|»»» id|string|true|none||帖子id|
|»»» date|string|true|none||发布时间|
|»»» title|string|true|none||帖子标题|
|»»» summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|»»» images|[string]|true|none||帖子图片URL Key|
|»»» likeCount|integer|true|none||点赞数|
|»»» shareCount|integer|true|none||分享数|
|»»» commentCount|integer|true|none||评论数|
|»»» categoryName|string|true|none||分类名字|
|»»» isLiked|boolean|true|none||是否被用户点赞|
|»»» author|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|»»»» id|string|true|none||用户id|
|»»»» name|string|true|none||姓名|
|»»»» avatar|string|true|none||头像URL Key|
|»»»» collegeId|string|true|none||学院id|
|»»»» collegeName|string|true|none||学院名称|
|»»»» isVerified|boolean|true|none||是否认证|

## POST 发布新帖子

POST /api/posts

> Body 请求参数

```json
{
  "title": "string",
  "content": "string",
  "images": [
    "string"
  ],
  "likeCount": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |用于获取当前登录用户id作为authorId|
|body|body|[PostCreateDTO](#schemapostcreatedto)| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
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
|» data|string|false|none||帖子id|

## GET 帖子详情

GET /api/posts/{id}

若非作者本人则 status 和 rejectReason 返回 null

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|string| 是 |none|
|Authorization|header|string| 否 |用于判断 isLiked 以及是否为作者本人|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": "string",
    "date": "string",
    "title": "string",
    "content": "string",
    "images": [
      "string"
    ],
    "likeCount": 0,
    "shareCount": 0,
    "commentCount": 0,
    "status": 0,
    "rejectReason": "string",
    "categoryName": "string",
    "author": {
      "id": "string",
      "name": "未认证",
      "avatar": "assets/default_avatar.png",
      "collegeId": "string",
      "collegeName": "string",
      "isVerified": true
    },
    "isLiked": "string"
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
|» data|[PostDetailVO](#schemapostdetailvo)|false|none||none|
|»» id|string|true|none||帖子id|
|»» date|string|true|none||发布时间|
|»» title|string|true|none||帖子标题|
|»» content|string|true|none||帖子完整内容|
|»» images|[string]|true|none||帖子图片URL Key|
|»» likeCount|integer|true|none||点赞数|
|»» shareCount|integer|true|none||分享数|
|»» commentCount|integer|true|none||评论数|
|»» status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|»» rejectReason|string¦null|true|none||驳回理由|
|»» categoryName|string|true|none||分类名字|
|»» author|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|»»» id|string|true|none||用户id|
|»»» name|string|true|none||姓名|
|»»» avatar|string|true|none||头像URL Key|
|»»» collegeId|string|true|none||学院id|
|»»» collegeName|string|true|none||学院名称|
|»»» isVerified|boolean|true|none||是否认证|
|»» isLiked|string|true|none||none|

## PUT 修改帖子

PUT /api/posts/{id}

> Body 请求参数

```json
{
  "title": "string",
  "content": "string",
  "images": [
    "string"
  ],
  "categoryId": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|string| 是 |none|
|Authorization|header|string| 是 |用于鉴权，确保是作者本人在修改|
|body|body|[PostUpdateDTO](#schemapostupdatedto)| 是 |none|

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

## DELETE 删除帖子

DELETE /api/posts/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|string| 是 |none|
|Authorization|header|string| 是 |用于校验是否为作者本人|

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

## GET 帖子评论列表

GET /api/comments

> Body 请求参数

```yaml
postId: ""
page: 0

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 否 |用于判断 isLiked|
|body|body|object| 是 |none|
|» postId|body|string| 是 |关联的帖子id|
|» page|body|integer| 是 |页码，每页固定20条数据|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "content": "string",
        "commenterId": 0,
        "likeCount": 0,
        "isLiked": true,
        "commenter": {
          "id": "string",
          "name": "未认证",
          "avatar": "assets/default_avatar.png",
          "collegeId": "string",
          "collegeName": "string",
          "isVerified": true
        }
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[CommentVO](#schemacommentvo)]|true|none||none|
|»»» id|string|true|none||评论id|
|»»» date|string|true|none||发布时间|
|»»» content|string|true|none||评论内容|
|»»» commenterId|integer|true|none||评论人id|
|»»» likeCount|integer|true|none||点赞数|
|»»» isLiked|boolean|true|none||是否被用户点赞|
|»»» commenter|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|»»»» id|string|true|none||用户id|
|»»»» name|string|true|none||姓名|
|»»»» avatar|string|true|none||头像URL Key|
|»»»» collegeId|string|true|none||学院id|
|»»»» collegeName|string|true|none||学院名称|
|»»»» isVerified|boolean|true|none||是否认证|

## POST 发布新评论

POST /api/comments

> Body 请求参数

```json
{
  "content": "string",
  "postId": 0
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |用于获取当前评论人id|
|body|body|[CommentCreateDTO](#schemacommentcreatedto)| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": "string"
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
|» data|string|false|none||评论id|

## PUT 修改评论

PUT /api/comments/{id}

> Body 请求参数

```json
{
  "content": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|string| 是 |none|
|Authorization|header|string| 是 |用于鉴权，确保是评论发布者本人|
|body|body|[CommentUpdateDTO](#schemacommentupdatedto)| 是 |none|

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

## DELETE 删除评论

DELETE /api/comments/{id}

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|string| 是 |none|
|Authorization|header|string| 是 |用于校验是否为作者本人|

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

## POST 上传帖子图片

POST /api/images/post

后端自动将文件重命名为 userId+随机串.jpg
存入/opt/1panel/apps/openresty/openresty/www/sites/vr-static/index/posts/{yyyy}/{MM}/{dd}/{postId}/{filename}

> Body 请求参数

```yaml
file: ""

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Content-Type|header|string| 否 |none|
|Authorization|header|string| 否 |none|
|body|body|object| 是 |none|
|» file|body|string(binary)| 否 |图片文件|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "url": "string"
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
|» data|object|false|none||none|
|»» url|string|true|none||图片路径|

## POST 点赞帖子

POST /api/posts/{postId}/likes

幂等：重复点赞也返回成功和当前状态，不报错。

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|postId|path|string| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "likeCount": 0,
    "isLiked": true
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
|» data|[PostLikeActionVO](#schemapostlikeactionvo)|false|none||none|
|»» id|integer|true|none||帖子id|
|»» likeCount|integer|true|none||点赞数|
|»» isLiked|boolean|true|none||none|

## DELETE 取消点赞帖子

DELETE /api/posts/{postId}/likes

幂等：未点赞时取消也返回成功。

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|postId|path|string| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "likeCount": 0,
    "isLiked": true
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
|» data|[PostLikeActionVO](#schemapostlikeactionvo)|false|none||none|
|»» id|integer|true|none||帖子id|
|»» likeCount|integer|true|none||点赞数|
|»» isLiked|boolean|true|none||none|

## POST 点赞评论

POST /api/comments/{commentId}/likes

幂等：重复点赞也返回成功和当前状态，不报错。

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|commentId|path|string| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "likeCount": 0,
    "isLiked": true
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
|» data|[CommentLikeActionVO](#schemacommentlikeactionvo)|false|none||none|
|»» id|integer|true|none||评论id|
|»» likeCount|integer|true|none||点赞数|
|»» isLiked|boolean|true|none||none|

## DELETE 取消点赞评论

DELETE /api/comments/{commentId}/likes

幂等：未点赞时取消也返回成功。

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|commentId|path|string| 是 |none|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "id": 0,
    "likeCount": 0,
    "isLiked": true
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
|» data|[CommentLikeActionVO](#schemacommentlikeactionvo)|false|none||none|
|»» id|integer|true|none||评论id|
|»» likeCount|integer|true|none||点赞数|
|»» isLiked|boolean|true|none||none|

# 用户

## GET 我的帖子列表

GET /api/users/posts

> Body 请求参数

```yaml
page: 0

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |用于识别当前用户|
|body|body|object| 是 |none|
|» page|body|integer| 是 |页码，每页固定20条数据|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "title": "string",
        "summary": "string",
        "images": [
          "string"
        ],
        "likeCount": 0,
        "shareCount": 0,
        "commentCount": 0,
        "status": 0,
        "rejectReason": "string",
        "categoryName": "string",
        "isLiked": true
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[UserPostVO](#schemauserpostvo)]|true|none||none|
|»»» id|string|true|none||帖子id|
|»»» date|string|true|none||发布时间|
|»»» title|string|true|none||帖子标题|
|»»» summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|»»» images|[string]|true|none||帖子图片URL Key|
|»»» likeCount|integer|true|none||点赞数|
|»»» shareCount|integer|true|none||分享数|
|»»» commentCount|integer|true|none||评论数|
|»»» status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|»»» rejectReason|string¦null|true|none||驳回理由|
|»»» categoryName|string|true|none||分类名字|
|»»» isLiked|boolean|true|none||是否被用户点赞|

## GET 我的评论列表

GET /api/users/comments

> Body 请求参数

```yaml
page: 0

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |用于识别当前用户|
|body|body|object| 是 |none|
|» page|body|integer| 是 |页码，每页固定20条数据|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "content": "string",
        "likeCount": 0,
        "status": 0,
        "rejectReason": "string",
        "isLiked": true,
        "relatedPost": {
          "id": "string",
          "title": "string",
          "status": 0
        }
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[UserCommentVO](#schemausercommentvo)]|true|none||none|
|»»» id|string|true|none||评论id|
|»»» date|string|true|none||发布时间|
|»»» content|string|true|none||评论内容|
|»»» likeCount|integer|true|none||点赞数|
|»»» status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|»»» rejectReason|string¦null|true|none||驳回理由|
|»»» isLiked|boolean|true|none||是否被用户点赞|
|»»» relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|
|»»»» id|string|true|none||帖子id|
|»»»» title|string|true|none||帖子标题|
|»»»» status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|

## GET 我点赞的帖子列表

GET /api/users/liked-posts

> Body 请求参数

```yaml
page: 0

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |用于识别当前用户|
|body|body|object| 是 |none|
|» page|body|integer| 是 |页码，每页固定20条数据|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "title": "string",
        "summary": "string",
        "images": [
          "string"
        ],
        "likeCount": 0,
        "shareCount": 0,
        "commentCount": 0,
        "categoryName": "string",
        "isLiked": true,
        "author": {
          "id": "string",
          "name": "未认证",
          "avatar": "assets/default_avatar.png",
          "collegeId": "string",
          "collegeName": "string",
          "isVerified": true
        }
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[PostVO](#schemapostvo)]|true|none||none|
|»»» id|string|true|none||帖子id|
|»»» date|string|true|none||发布时间|
|»»» title|string|true|none||帖子标题|
|»»» summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|»»» images|[string]|true|none||帖子图片URL Key|
|»»» likeCount|integer|true|none||点赞数|
|»»» shareCount|integer|true|none||分享数|
|»»» commentCount|integer|true|none||评论数|
|»»» categoryName|string|true|none||分类名字|
|»»» isLiked|boolean|true|none||是否被用户点赞|
|»»» author|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|»»»» id|string|true|none||用户id|
|»»»» name|string|true|none||姓名|
|»»»» avatar|string|true|none||头像URL Key|
|»»»» collegeId|string|true|none||学院id|
|»»»» collegeName|string|true|none||学院名称|
|»»»» isVerified|boolean|true|none||是否认证|

## GET 我点赞的评论列表

GET /api/users/liked-comments

> Body 请求参数

```yaml
page: 0

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |用于识别当前用户|
|body|body|object| 是 |none|
|» page|body|integer| 是 |页码，每页固定20条数据|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "content": "string",
        "likeCount": 0,
        "status": 0,
        "rejectReason": "string",
        "isLiked": true,
        "relatedPost": {
          "id": "string",
          "title": "string",
          "status": 0
        }
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[UserCommentVO](#schemausercommentvo)]|true|none||none|
|»»» id|string|true|none||评论id|
|»»» date|string|true|none||发布时间|
|»»» content|string|true|none||评论内容|
|»»» likeCount|integer|true|none||点赞数|
|»»» status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|»»» rejectReason|string¦null|true|none||驳回理由|
|»»» isLiked|boolean|true|none||是否被用户点赞|
|»»» relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|
|»»»» id|string|true|none||帖子id|
|»»»» title|string|true|none||帖子标题|
|»»»» status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|

## POST 登录

POST /api/users/login

获取接口调用凭证文档https://developers.weixin.qq.com/miniprogram/dev/server/API/mp-access-token/api_getaccesstoken.html
获取手机号文档https://developers.weixin.qq.com/miniprogram/dev/server/API/user-info/phone-number/api_getphonenumber.html
获取openId文档https://developers.weixin.qq.com/miniprogram/dev/server/API/user-login/api_code2session.html

AppID：wxea208099d2372b84
AppSecret：d10f3a6b58b0c1562a82bfc467c06418

若用户存在，则needBindPhone返回false，user返回用户信息
若用户不存在，则创建用户信息，needBindPhone返回true，user返回null

> Body 请求参数

```json
{
  "loginCode": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 是 |none|
|» loginCode|body|string| 是 |换取openId|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "token": "string",
    "needBindPhone": true,
    "user": {
      "id": "string",
      "phone": "string",
      "name": "未认证",
      "avatar": "assets/default_avatar.png",
      "collegeId": "string",
      "verifyStatus": 0,
      "collegeName": "string"
    }
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
|» data|object|false|none||none|
|»» token|string|true|none||none|
|»» needBindPhone|boolean|true|none||none|
|»» user|[UserProfileVO](#schemauserprofilevo)¦null|true|none||none|
|»»» id|string|true|none||用户id|
|»»» phone|string|true|none||手机号，唯一|
|»»» name|string|true|none||姓名|
|»»» avatar|string|true|none||头像URL Key|
|»»» collegeId|string|true|none||学院id|
|»»» verifyStatus|integer|true|none||认证状态，0：未认证，1：审核中，2：已认证|
|»»» collegeName|string|true|none||学院名称|

## POST 退出登录

POST /api/users/logout

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |none|

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

## POST 绑定手机号

POST /api/users/bind-phone

获取手机号文档https://developers.weixin.qq.com/miniprogram/dev/server/API/user-info/phone-number/api_getphonenumber.html

绑定后返回新user信息

> Body 请求参数

```json
{
  "phoneCode": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |token|
|body|body|object| 是 |none|
|» phoneCode|body|string| 是 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "token": "string",
    "user": {
      "id": "string",
      "phone": "string",
      "name": "未认证",
      "avatar": "assets/default_avatar.png",
      "collegeId": "string",
      "verifyStatus": 0,
      "collegeName": "string"
    }
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
|» data|object|false|none||none|
|»» token|string|true|none||none|
|»» user|[UserProfileVO](#schemauserprofilevo)|true|none||none|
|»»» id|string|true|none||用户id|
|»»» phone|string|true|none||手机号，唯一|
|»»» name|string|true|none||姓名|
|»»» avatar|string|true|none||头像URL Key|
|»»» collegeId|string|true|none||学院id|
|»»» verifyStatus|integer|true|none||认证状态，0：未认证，1：审核中，2：已认证|
|»»» collegeName|string|true|none||学院名称|

## GET 获取用户信息

GET /api/users/profile

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |none|

> 返回示例

> 200 Response

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
    "verifyStatus": 0,
    "collegeName": "string"
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
|» data|[UserProfileVO](#schemauserprofilevo)|false|none||none|
|»» id|string|true|none||用户id|
|»» phone|string|true|none||手机号，唯一|
|»» name|string|true|none||姓名|
|»» avatar|string|true|none||头像URL Key|
|»» collegeId|string|true|none||学院id|
|»» verifyStatus|integer|true|none||认证状态，0：未认证，1：审核中，2：已认证|
|»» collegeName|string|true|none||学院名称|

# 后台

## GET 帖子列表

GET /api/admin/posts

> Body 请求参数

```yaml
page: 0
status: 0
categoryId: 0
keyword: ""

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 是 |none|
|» page|body|integer| 是 |页码，每页固定20条数据|
|» status|body|integer| 否 |状态，没传则查全部|
|» categoryId|body|integer| 否 |分类id，没传则查全部|
|» keyword|body|string| 否 |搜索关键词|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "title": "string",
        "summary": "string",
        "status": 0,
        "rejectReason": "string",
        "categoryName": "string",
        "author": {
          "id": "string",
          "name": "未认证",
          "avatar": "assets/default_avatar.png",
          "collegeId": "string",
          "collegeName": "string",
          "isVerified": true
        }
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[PostAuditVO](#schemapostauditvo)]|true|none||none|
|»»» id|string|true|none||帖子id|
|»»» date|string|true|none||发布时间|
|»»» title|string|true|none||帖子标题|
|»»» summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|»»» status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|»»» rejectReason|string¦null|true|none||驳回理由|
|»»» categoryName|string|true|none||分类名字|
|»»» author|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|»»»» id|string|true|none||用户id|
|»»»» name|string|true|none||姓名|
|»»»» avatar|string|true|none||头像URL Key|
|»»»» collegeId|string|true|none||学院id|
|»»»» collegeName|string|true|none||学院名称|
|»»»» isVerified|boolean|true|none||是否认证|

## GET 评论列表

GET /api/admin/comments

> Body 请求参数

```yaml
page: 0
status: 0

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|body|body|object| 是 |none|
|» page|body|integer| 是 |页码，每页固定20条数据|
|» status|body|integer| 否 |状态，没传则查全部|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
      {
        "id": "string",
        "date": "string",
        "content": "string",
        "status": 0,
        "rejectReason": "string",
        "commenter": {
          "id": "string",
          "name": "未认证",
          "avatar": "assets/default_avatar.png",
          "collegeId": "string",
          "collegeName": "string",
          "isVerified": true
        },
        "relatedPost": {
          "id": "string",
          "title": "string",
          "status": 0
        }
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
|» data|object|false|none||none|
|»» current|integer|true|none||当前页|
|»» total|integer|true|none||总页数|
|»» records|[[CommentAuditVO](#schemacommentauditvo)]|true|none||none|
|»»» id|string|true|none||评论id|
|»»» date|string|true|none||发布时间|
|»»» content|string|true|none||评论内容|
|»»» status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|»»» rejectReason|string¦null|true|none||驳回理由|
|»»» commenter|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|»»»» id|string|true|none||用户id|
|»»»» name|string|true|none||姓名|
|»»»» avatar|string|true|none||头像URL Key|
|»»»» collegeId|string|true|none||学院id|
|»»»» collegeName|string|true|none||学院名称|
|»»»» isVerified|boolean|true|none||是否认证|
|»»» relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|
|»»»» id|string|true|none||帖子id|
|»»»» title|string|true|none||帖子标题|
|»»»» status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|

## PATCH 审核帖子

PATCH /api/admin/posts/{id}

> Body 请求参数

```json
{
  "status": 0,
  "rejectReason": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|string| 是 |none|
|body|body|[PostAuditDTO](#schemapostauditdto)| 是 |none|

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

## PATCH 审核评论

PATCH /api/admin/comments/{id}

> Body 请求参数

```json
{
  "status": 0,
  "rejectReason": "string"
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|path|string| 是 |none|
|body|body|[CommentAuditDTO](#schemacommentauditdto)| 是 |none|

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

# 通用

## GET 获取阿里云OSS POST签名v4

GET /api/oss/sign

签名计算方法请看文档
https://help.aliyun.com/zh/oss/developer-reference/signature-version-4-recommend?spm=a2c4g.11186623.help-menu-31815.d_1_4_5_0.3360349adLsV8m

参数
AccessKeyId: LTAI5tSDmSeP9fisj2FsDcjg
AccessKeySecret: HNyqYFC7a6ZJHYRIztxhnzIOkl0qNX
region: cn-wuhan-lr

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Authorization|header|string| 是 |token|

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
        "price": 0,
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
|»»» price|integer|true|none||100000，单位分|
|»»» status|integer|true|none||0=过道, 1=空闲(可买), 2=已锁定(待支付), 3=已售出|
|»»» version|integer|true|none||乐观锁，放并发|

## GET 查询订单

GET /api/orders

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|page|query|integer| 否 |页码，每页固定20条数据|
|Authorization|header|string| 否 |none|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "msg": "string",
  "data": {
    "current": 0,
    "total": 0,
    "records": [
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
          null
        ]
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
|» data|object|false|none||none|
|»» current|integer|true|none||none|
|»» total|integer|true|none||none|
|»» records|[[OrderListVO](#schemaorderlistvo)]|true|none||none|
|»»» id|integer|true|none||订单号，推荐用雪花算法或时间戳+随机数？|
|»»» campusId|integer|true|none||校区Id|
|»»» buildingId|integer|true|none||教学楼Id|
|»»» roomId|integer|true|none||教室Id|
|»»» amount|integer|true|none||订单总金额(单位：分)|
|»»» status|string|true|none||PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)|
|»»» expiresAt|string|true|none||订单超时时间（创建时间+10分钟）|
|»»» createdAt|string|true|none||订单创建时间|
|»»» updatedAt|string|true|none||订单更新时间，存时间戳，上同|
|»»» seatList|[allOf]|true|none||none|

*allOf*

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|»»»» *anonymous*|object|false|none||none|
|»»»»» id|integer|true|none||座位id|
|»»»»» row|integer|true|none||行号|
|»»»»» col|integer|true|none||列号|

*and*

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|»»»» *anonymous*|object|false|none||none|
|»»»»» lookPrice|string|true|none||下单时的价格快照(单位：分)|

## POST 创建订单

POST /api/orders

> Body 请求参数

```json
{
  "campusId": 0,
  "buildingId": 0,
  "roomId": 0,
  "seatList": [
    {
      "id": "string",
      "price": 0,
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
    "orderId": "string"
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
|» data|object|false|none||none|
|»» orderId|string|true|none||订单id|

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

<h2 id="tocS_User">User</h2>

<a id="schemauser"></a>
<a id="schema_User"></a>
<a id="tocSuser"></a>
<a id="tocsuser"></a>

```json
{
  "id": 0,
  "phone": "string",
  "openId": "string",
  "name": "未认证",
  "avatar": "assets/default_avatar.png",
  "collegeId": "string",
  "verifyStatus": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||用户id|
|phone|string|true|none||手机号，唯一|
|openId|string|true|none||微信openId，唯一|
|name|string|true|none||姓名|
|avatar|string|true|none||头像URL Key|
|collegeId|string|true|none||学院id|
|verifyStatus|integer|true|none||认证状态，0：未认证，1：审核中，2：已认证|

<h2 id="tocS_College">College</h2>

<a id="schemacollege"></a>
<a id="schema_College"></a>
<a id="tocScollege"></a>
<a id="tocscollege"></a>

```json
{
  "id": 0,
  "name": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||学院id|
|name|string|true|none||学院名字|

<h2 id="tocS_Post">Post</h2>

<a id="schemapost"></a>
<a id="schema_Post"></a>
<a id="tocSpost"></a>
<a id="tocspost"></a>

```json
{
  "id": 0,
  "date": "string",
  "title": "string",
  "summary": "string",
  "content": "string",
  "images": [
    "string"
  ],
  "authorId": 0,
  "categoryId": 0,
  "likeCount": 0,
  "shareCount": 0,
  "commentCount": 0,
  "status": 0,
  "rejectReason": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||帖子id|
|date|string|true|none||发布时间|
|title|string|true|none||帖子标题|
|summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|content|string|true|none||帖子完整内容|
|images|[string]|true|none||帖子图片URL Key|
|authorId|integer|true|none||发帖人id|
|categoryId|integer|true|none||分类id|
|likeCount|integer|true|none||点赞数|
|shareCount|integer|true|none||分享数|
|commentCount|integer|true|none||评论数|
|status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|

<h2 id="tocS_Comment">Comment</h2>

<a id="schemacomment"></a>
<a id="schema_Comment"></a>
<a id="tocScomment"></a>
<a id="tocscomment"></a>

```json
{
  "id": 0,
  "date": "string",
  "content": "string",
  "commenterId": 0,
  "postId": 0,
  "likeCount": 0,
  "status": 0,
  "rejectReason": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||评论id|
|date|string|true|none||发布时间|
|content|string|true|none||评论内容|
|commenterId|integer|true|none||评论人id|
|postId|integer|true|none||帖子id|
|likeCount|integer|true|none||点赞数|
|status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|

<h2 id="tocS_Category">Category</h2>

<a id="schemacategory"></a>
<a id="schema_Category"></a>
<a id="tocScategory"></a>
<a id="tocscategory"></a>

```json
{
  "id": 0,
  "name": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||分类id|
|name|string|true|none||分类名字|

<h2 id="tocS_Building">Building</h2>

<a id="schemabuilding"></a>
<a id="schema_Building"></a>
<a id="tocSbuilding"></a>
<a id="tocsbuilding"></a>

```json
{
  "id": 0,
  "name": "string",
  "campusId": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||楼栋id|
|name|string|true|none||楼栋名称|
|campusId|integer|true|none||关联的校区id|

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
  "price": 0,
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
|price|integer|true|none||100000，单位分|
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
  "campusId": 0,
  "buildingId": 0,
  "roomId": 0,
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
|campusId|integer|true|none||校区Id|
|buildingId|integer|true|none||教学楼Id|
|roomId|integer|true|none||教室Id|
|amount|integer|true|none||订单总金额(单位：分)|
|status|string|true|none||PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)|
|expiresAt|string|true|none||订单超时时间（创建时间+10分钟）|
|createdAt|string|true|none||订单创建时间|
|updatedAt|string|true|none||订单更新时间，存时间戳，上同|

<h2 id="tocS_Order_Seat">Order_Seat</h2>

<a id="schemaorder_seat"></a>
<a id="schema_Order_Seat"></a>
<a id="tocSorder_seat"></a>
<a id="tocsorder_seat"></a>

```json
{
  "id": 0,
  "orderId": 0,
  "seatId": "string",
  "lookPrice": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||none|
|orderId|integer|true|none||关联order表的id|
|seatId|string|true|none||关联seat表的id|
|lookPrice|string|true|none||下单时的价格快照(单位：分)|

<h2 id="tocS_Campus">Campus</h2>

<a id="schemacampus"></a>
<a id="schema_Campus"></a>
<a id="tocScampus"></a>
<a id="tocscampus"></a>

```json
{
  "id": 0,
  "name": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||校区id|
|name|string|true|none||校区名字|

<h2 id="tocS_Post_Like">Post_Like</h2>

<a id="schemapost_like"></a>
<a id="schema_Post_Like"></a>
<a id="tocSpost_like"></a>
<a id="tocspost_like"></a>

```json
{
  "id": 0,
  "userId": 0,
  "postId": 0,
  "createdAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||ID 编号|
|userId|integer|true|none||none|
|postId|integer|true|none||none|
|createdAt|string|true|none||none|

<h2 id="tocS_Comment_Like">Comment_Like</h2>

<a id="schemacomment_like"></a>
<a id="schema_Comment_Like"></a>
<a id="tocScomment_like"></a>
<a id="tocscomment_like"></a>

```json
{
  "id": 0,
  "userId": 0,
  "commentId": 0,
  "createdAt": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||ID 编号|
|userId|integer|true|none||none|
|commentId|integer|true|none||none|
|createdAt|string|true|none||none|

<h2 id="tocS_UserPublicVO">UserPublicVO</h2>

<a id="schemauserpublicvo"></a>
<a id="schema_UserPublicVO"></a>
<a id="tocSuserpublicvo"></a>
<a id="tocsuserpublicvo"></a>

```json
{
  "id": "string",
  "name": "未认证",
  "avatar": "assets/default_avatar.png",
  "collegeId": "string",
  "collegeName": "string",
  "isVerified": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||用户id|
|name|string|true|none||姓名|
|avatar|string|true|none||头像URL Key|
|collegeId|string|true|none||学院id|
|collegeName|string|true|none||学院名称|
|isVerified|boolean|true|none||是否认证|

<h2 id="tocS_UserProfileVO">UserProfileVO</h2>

<a id="schemauserprofilevo"></a>
<a id="schema_UserProfileVO"></a>
<a id="tocSuserprofilevo"></a>
<a id="tocsuserprofilevo"></a>

```json
{
  "id": "string",
  "phone": "string",
  "name": "未认证",
  "avatar": "assets/default_avatar.png",
  "collegeId": "string",
  "verifyStatus": 0,
  "collegeName": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||用户id|
|phone|string|true|none||手机号，唯一|
|name|string|true|none||姓名|
|avatar|string|true|none||头像URL Key|
|collegeId|string|true|none||学院id|
|verifyStatus|integer|true|none||认证状态，0：未认证，1：审核中，2：已认证|
|collegeName|string|true|none||学院名称|

<h2 id="tocS_PostVO">PostVO</h2>

<a id="schemapostvo"></a>
<a id="schema_PostVO"></a>
<a id="tocSpostvo"></a>
<a id="tocspostvo"></a>

```json
{
  "id": "string",
  "date": "string",
  "title": "string",
  "summary": "string",
  "images": [
    "string"
  ],
  "likeCount": 0,
  "shareCount": 0,
  "commentCount": 0,
  "categoryName": "string",
  "isLiked": true,
  "author": {
    "id": "string",
    "name": "未认证",
    "avatar": "assets/default_avatar.png",
    "collegeId": "string",
    "collegeName": "string",
    "isVerified": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||帖子id|
|date|string|true|none||发布时间|
|title|string|true|none||帖子标题|
|summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|images|[string]|true|none||帖子图片URL Key|
|likeCount|integer|true|none||点赞数|
|shareCount|integer|true|none||分享数|
|commentCount|integer|true|none||评论数|
|categoryName|string|true|none||分类名字|
|isLiked|boolean|true|none||是否被用户点赞|
|author|[UserPublicVO](#schemauserpublicvo)|true|none||none|

<h2 id="tocS_PostDetailVO">PostDetailVO</h2>

<a id="schemapostdetailvo"></a>
<a id="schema_PostDetailVO"></a>
<a id="tocSpostdetailvo"></a>
<a id="tocspostdetailvo"></a>

```json
{
  "id": "string",
  "date": "string",
  "title": "string",
  "content": "string",
  "images": [
    "string"
  ],
  "likeCount": 0,
  "shareCount": 0,
  "commentCount": 0,
  "status": 0,
  "rejectReason": "string",
  "categoryName": "string",
  "author": {
    "id": "string",
    "name": "未认证",
    "avatar": "assets/default_avatar.png",
    "collegeId": "string",
    "collegeName": "string",
    "isVerified": true
  },
  "isLiked": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||帖子id|
|date|string|true|none||发布时间|
|title|string|true|none||帖子标题|
|content|string|true|none||帖子完整内容|
|images|[string]|true|none||帖子图片URL Key|
|likeCount|integer|true|none||点赞数|
|shareCount|integer|true|none||分享数|
|commentCount|integer|true|none||评论数|
|status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|categoryName|string|true|none||分类名字|
|author|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|isLiked|string|true|none||none|

<h2 id="tocS_PostAuditVO">PostAuditVO</h2>

<a id="schemapostauditvo"></a>
<a id="schema_PostAuditVO"></a>
<a id="tocSpostauditvo"></a>
<a id="tocspostauditvo"></a>

```json
{
  "id": "string",
  "date": "string",
  "title": "string",
  "summary": "string",
  "status": 0,
  "rejectReason": "string",
  "categoryName": "string",
  "author": {
    "id": "string",
    "name": "未认证",
    "avatar": "assets/default_avatar.png",
    "collegeId": "string",
    "collegeName": "string",
    "isVerified": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||帖子id|
|date|string|true|none||发布时间|
|title|string|true|none||帖子标题|
|summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|categoryName|string|true|none||分类名字|
|author|[UserPublicVO](#schemauserpublicvo)|true|none||none|

<h2 id="tocS_UserPostVO">UserPostVO</h2>

<a id="schemauserpostvo"></a>
<a id="schema_UserPostVO"></a>
<a id="tocSuserpostvo"></a>
<a id="tocsuserpostvo"></a>

```json
{
  "id": "string",
  "date": "string",
  "title": "string",
  "summary": "string",
  "images": [
    "string"
  ],
  "likeCount": 0,
  "shareCount": 0,
  "commentCount": 0,
  "status": 0,
  "rejectReason": "string",
  "categoryName": "string",
  "isLiked": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||帖子id|
|date|string|true|none||发布时间|
|title|string|true|none||帖子标题|
|summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|images|[string]|true|none||帖子图片URL Key|
|likeCount|integer|true|none||点赞数|
|shareCount|integer|true|none||分享数|
|commentCount|integer|true|none||评论数|
|status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|categoryName|string|true|none||分类名字|
|isLiked|boolean|true|none||是否被用户点赞|

<h2 id="tocS_RelatedPostVO">RelatedPostVO</h2>

<a id="schemarelatedpostvo"></a>
<a id="schema_RelatedPostVO"></a>
<a id="tocSrelatedpostvo"></a>
<a id="tocsrelatedpostvo"></a>

```json
{
  "id": "string",
  "title": "string",
  "status": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||帖子id|
|title|string|true|none||帖子标题|
|status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|

<h2 id="tocS_PostLikeActionVO">PostLikeActionVO</h2>

<a id="schemapostlikeactionvo"></a>
<a id="schema_PostLikeActionVO"></a>
<a id="tocSpostlikeactionvo"></a>
<a id="tocspostlikeactionvo"></a>

```json
{
  "id": 0,
  "likeCount": 0,
  "isLiked": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||帖子id|
|likeCount|integer|true|none||点赞数|
|isLiked|boolean|true|none||none|

<h2 id="tocS_CommentVO">CommentVO</h2>

<a id="schemacommentvo"></a>
<a id="schema_CommentVO"></a>
<a id="tocScommentvo"></a>
<a id="tocscommentvo"></a>

```json
{
  "id": "string",
  "date": "string",
  "content": "string",
  "commenterId": 0,
  "likeCount": 0,
  "isLiked": true,
  "commenter": {
    "id": "string",
    "name": "未认证",
    "avatar": "assets/default_avatar.png",
    "collegeId": "string",
    "collegeName": "string",
    "isVerified": true
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||评论id|
|date|string|true|none||发布时间|
|content|string|true|none||评论内容|
|commenterId|integer|true|none||评论人id|
|likeCount|integer|true|none||点赞数|
|isLiked|boolean|true|none||是否被用户点赞|
|commenter|[UserPublicVO](#schemauserpublicvo)|true|none||none|

<h2 id="tocS_CommentAuditVO">CommentAuditVO</h2>

<a id="schemacommentauditvo"></a>
<a id="schema_CommentAuditVO"></a>
<a id="tocScommentauditvo"></a>
<a id="tocscommentauditvo"></a>

```json
{
  "id": "string",
  "date": "string",
  "content": "string",
  "status": 0,
  "rejectReason": "string",
  "commenter": {
    "id": "string",
    "name": "未认证",
    "avatar": "assets/default_avatar.png",
    "collegeId": "string",
    "collegeName": "string",
    "isVerified": true
  },
  "relatedPost": {
    "id": "string",
    "title": "string",
    "status": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||评论id|
|date|string|true|none||发布时间|
|content|string|true|none||评论内容|
|status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|commenter|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|

<h2 id="tocS_UserCommentVO">UserCommentVO</h2>

<a id="schemausercommentvo"></a>
<a id="schema_UserCommentVO"></a>
<a id="tocSusercommentvo"></a>
<a id="tocsusercommentvo"></a>

```json
{
  "id": "string",
  "date": "string",
  "content": "string",
  "likeCount": 0,
  "status": 0,
  "rejectReason": "string",
  "isLiked": true,
  "relatedPost": {
    "id": "string",
    "title": "string",
    "status": 0
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||评论id|
|date|string|true|none||发布时间|
|content|string|true|none||评论内容|
|likeCount|integer|true|none||点赞数|
|status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|isLiked|boolean|true|none||是否被用户点赞|
|relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|

<h2 id="tocS_CommentLikeActionVO">CommentLikeActionVO</h2>

<a id="schemacommentlikeactionvo"></a>
<a id="schema_CommentLikeActionVO"></a>
<a id="tocScommentlikeactionvo"></a>
<a id="tocscommentlikeactionvo"></a>

```json
{
  "id": 0,
  "likeCount": 0,
  "isLiked": true
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||评论id|
|likeCount|integer|true|none||点赞数|
|isLiked|boolean|true|none||none|

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
  "price": 0,
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
|price|integer|true|none||100000，单位分|
|status|integer|true|none||0=过道, 1=空闲(可买), 2=已锁定(待支付), 3=已售出|
|version|integer|true|none||乐观锁，放并发|

<h2 id="tocS_OrderSeatVO">OrderSeatVO</h2>

<a id="schemaorderseatvo"></a>
<a id="schema_OrderSeatVO"></a>
<a id="tocSorderseatvo"></a>
<a id="tocsorderseatvo"></a>

```json
{
  "id": 0,
  "row": 0,
  "col": 0,
  "lookPrice": "string"
}

```

### 属性

allOf

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|*anonymous*|object|false|none||none|
|» id|integer|true|none||座位id|
|» row|integer|true|none||行号|
|» col|integer|true|none||列号|

and

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|*anonymous*|object|false|none||none|
|» lookPrice|string|true|none||下单时的价格快照(单位：分)|

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
      "price": 0,
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

<h2 id="tocS_OrderListVO">OrderListVO</h2>

<a id="schemaorderlistvo"></a>
<a id="schema_OrderListVO"></a>
<a id="tocSorderlistvo"></a>
<a id="tocsorderlistvo"></a>

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

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||订单号，推荐用雪花算法或时间戳+随机数？|
|campusId|integer|true|none||校区Id|
|buildingId|integer|true|none||教学楼Id|
|roomId|integer|true|none||教室Id|
|amount|integer|true|none||订单总金额(单位：分)|
|status|string|true|none||PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)|
|expiresAt|string|true|none||订单超时时间（创建时间+10分钟）|
|createdAt|string|true|none||订单创建时间|
|updatedAt|string|true|none||订单更新时间，存时间戳，上同|
|seatList|[[OrderSeatVO](#schemaorderseatvo)]|true|none||none|

<h2 id="tocS_PostUpdateDTO">PostUpdateDTO</h2>

<a id="schemapostupdatedto"></a>
<a id="schema_PostUpdateDTO"></a>
<a id="tocSpostupdatedto"></a>
<a id="tocspostupdatedto"></a>

```json
{
  "title": "string",
  "content": "string",
  "images": [
    "string"
  ],
  "categoryId": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|title|string|true|none||帖子标题|
|content|string|true|none||帖子完整内容|
|images|[string]|true|none||帖子图片URL Key|
|categoryId|integer|true|none||分类id|

<h2 id="tocS_PostAuditDTO">PostAuditDTO</h2>

<a id="schemapostauditdto"></a>
<a id="schema_PostAuditDTO"></a>
<a id="tocSpostauditdto"></a>
<a id="tocspostauditdto"></a>

```json
{
  "status": 0,
  "rejectReason": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|

<h2 id="tocS_PostCreateDTO">PostCreateDTO</h2>

<a id="schemapostcreatedto"></a>
<a id="schema_PostCreateDTO"></a>
<a id="tocSpostcreatedto"></a>
<a id="tocspostcreatedto"></a>

```json
{
  "title": "string",
  "content": "string",
  "images": [
    "string"
  ],
  "likeCount": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|title|string|true|none||帖子标题|
|content|string|true|none||帖子完整内容|
|images|[string]|true|none||帖子图片URL Key|
|likeCount|integer|true|none||点赞数|

<h2 id="tocS_CommentUpdateDTO">CommentUpdateDTO</h2>

<a id="schemacommentupdatedto"></a>
<a id="schema_CommentUpdateDTO"></a>
<a id="tocScommentupdatedto"></a>
<a id="tocscommentupdatedto"></a>

```json
{
  "content": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|content|string|true|none||评论内容|

<h2 id="tocS_CommentAuditDTO">CommentAuditDTO</h2>

<a id="schemacommentauditdto"></a>
<a id="schema_CommentAuditDTO"></a>
<a id="tocScommentauditdto"></a>
<a id="tocscommentauditdto"></a>

```json
{
  "status": 0,
  "rejectReason": "string"
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|

<h2 id="tocS_CommentCreateDTO">CommentCreateDTO</h2>

<a id="schemacommentcreatedto"></a>
<a id="schema_CommentCreateDTO"></a>
<a id="tocScommentcreatedto"></a>
<a id="tocscommentcreatedto"></a>

```json
{
  "content": "string",
  "postId": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|content|string|true|none||评论内容|
|postId|integer|true|none||帖子id|

<h2 id="tocS_CreateOrderDTO">CreateOrderDTO</h2>

<a id="schemacreateorderdto"></a>
<a id="schema_CreateOrderDTO"></a>
<a id="tocScreateorderdto"></a>
<a id="tocscreateorderdto"></a>

```json
{
  "campusId": 0,
  "buildingId": 0,
  "roomId": 0,
  "seatList": [
    {
      "id": "string",
      "price": 0,
      "version": 0
    }
  ]
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|campusId|integer|true|none||校区Id|
|buildingId|integer|true|none||教学楼Id|
|roomId|integer|true|none||教室Id|
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
  "price": 0,
  "version": 0
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|string|true|none||座位id|
|price|integer|true|none||100000，单位分|
|version|integer|true|none||乐观锁，放并发|

