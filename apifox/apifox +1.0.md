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
        "categoryId": "string",
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
|»»» categoryId|string|true|none||分类id|
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
  "categoryId": "string",
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
|categoryId|string|true|none||分类id|
|status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|categoryName|string|true|none||分类名字|
|author|[UserPublicVO](#schemauserpublicvo)|true|none||none|

<h2 id="tocS_RelatedPostVO">RelatedPostVO</h2>

<a id="schemarelatedpostvo"></a>
<a id="schema_RelatedPostVO"></a>
<a id="tocSrelatedpostvo"></a>
<a id="tocsrelatedpostvo"></a>

```json
{
  "id": "string",
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
|id|string|true|none||帖子id|
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
|id|string|true|none||评论id|
|date|string|true|none||发布时间|
|content|string|true|none||评论内容|
|status|integer¦null|true|none||状态 0：待审核，1：已发布，2：已驳回|
|rejectReason|string¦null|true|none||驳回理由|
|commenter|[UserPublicVO](#schemauserpublicvo)|true|none||none|
|relatedPost|[RelatedPostVO](#schemarelatedpostvo)|true|none||none|

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

