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
        "categoryId": "string",
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
|»»» categoryId|string|true|none||分类id|
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
    "summary": "string",
    "images": [
      "string"
    ],
    "categoryId": "string",
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
|» data|[PostDetailVO](#schemapostdetailvo)|false|none||none|
|»» id|string|true|none||帖子id|
|»» date|string|true|none||发布时间|
|»» title|string|true|none||帖子标题|
|»» summary|string|true|none||帖子缩略内容，用户上传content自动截取前 50 个字存入该字段|
|»» images|[string]|true|none||帖子图片URL Key|
|»» categoryId|string|true|none||分类id|
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
        "id": 0,
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
|»»» id|integer|true|none||评论id|
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
        "categoryId": "string",
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
|»»» categoryId|string|true|none||分类id|
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
        "id": 0,
        "date": "string",
        "content": "string",
        "likeCount": 0,
        "status": 0,
        "rejectReason": "string",
        "isLiked": true,
        "relatedPost": {
          "id": 0,
          "date": "string",
          "title": "string",
          "status": 0,
          "author": {}
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
|»»» id|integer|true|none||评论id|
|»»» date|string|true|none||发布时间|
|»»» content|string|true|none||评论内容|
|»»» likeCount|integer|true|none||点赞数|
|»»» status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|»»» rejectReason|string¦null|true|none||驳回理由|
|»»» isLiked|boolean|true|none||是否被用户点赞|
|»»» relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|
|»»»» id|integer|true|none||帖子id|
|»»»» date|string|true|none||发布时间|
|»»»» title|string|true|none||帖子标题|
|»»»» status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|»»»» author|object|true|none||none|
|»»»»» id|string|true|none||用户id|
|»»»»» name|string|true|none||姓名|
|»»»»» avatar|string|true|none||头像URL Key|
|»»»»» collegeId|string|true|none||学院id|
|»»»»» collegeName|string|true|none||学院名称|
|»»»»» isVerified|boolean|true|none||是否认证|

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
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

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
  "categoryId": "string",
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
|categoryId|string|true|none||分类id|
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
  "summary": "string",
  "images": [
    "string"
  ],
  "categoryId": "string",
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
|categoryId|string|true|none||分类id|
|likeCount|integer|true|none||点赞数|
|shareCount|integer|true|none||分享数|
|commentCount|integer|true|none||评论数|
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
  "categoryId": "string",
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
|categoryId|string|true|none||分类id|
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
  "id": 0,
  "date": "string",
  "title": "string",
  "status": 0,
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
|id|integer|true|none||帖子id|
|date|string|true|none||发布时间|
|title|string|true|none||帖子标题|
|status|integer|true|none||状态 -1：已删除， 0：待审核，1：已发布，2：已驳回|
|author|object|true|none||none|
|» id|string|true|none||用户id|
|» name|string|true|none||姓名|
|» avatar|string|true|none||头像URL Key|
|» collegeId|string|true|none||学院id|
|» collegeName|string|true|none||学院名称|
|» isVerified|boolean|true|none||是否认证|

<h2 id="tocS_CommentVO">CommentVO</h2>

<a id="schemacommentvo"></a>
<a id="schema_CommentVO"></a>
<a id="tocScommentvo"></a>
<a id="tocscommentvo"></a>

```json
{
  "id": 0,
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
|id|integer|true|none||评论id|
|date|string|true|none||发布时间|
|content|string|true|none||评论内容|
|commenterId|integer|true|none||评论人id|
|likeCount|integer|true|none||点赞数|
|isLiked|boolean|true|none||是否被用户点赞|
|commenter|[UserPublicVO](#schemauserpublicvo)|true|none||none|

<h2 id="tocS_UserCommentVO">UserCommentVO</h2>

<a id="schemausercommentvo"></a>
<a id="schema_UserCommentVO"></a>
<a id="tocSusercommentvo"></a>
<a id="tocsusercommentvo"></a>

```json
{
  "id": 0,
  "date": "string",
  "content": "string",
  "likeCount": 0,
  "status": 0,
  "rejectReason": "string",
  "isLiked": true,
  "relatedPost": {
    "id": 0,
    "date": "string",
    "title": "string",
    "status": 0,
    "author": {
      "id": "string",
      "name": "未认证",
      "avatar": "assets/default_avatar.png",
      "collegeId": "string",
      "collegeName": "string",
      "isVerified": true
    }
  }
}

```

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|id|integer|true|none||评论id|
|date|string|true|none||发布时间|
|content|string|true|none||评论内容|
|likeCount|integer|true|none||点赞数|
|status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|isLiked|boolean|true|none||是否被用户点赞|
|relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|

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

