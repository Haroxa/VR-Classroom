# 模拟数据文档

## 1. 项目概述

本文档提供了用于测试项目接口的模拟数据，包括用户、帖子、评论、分类等数据，以及对应的测试脚本。

## 2. 基础数据

### 2.1 学院数据

```json
[
  {
    "id": 1,
    "name": "计算机学院"
  },
  {
    "id": 2,
    "name": "电子信息学院"
  },
  {
    "id": 3,
    "name": "机械工程学院"
  },
  {
    "id": 4,
    "name": "经济管理学院"
  },
  {
    "id": 5,
    "name": "人文学院"
  }
]
```

### 2.2 分类数据

```json
[
  {
    "id": 1,
    "name": "未分类"
  },
  {
    "id": 2,
    "name": "技术分享"
  },
  {
    "id": 3,
    "name": "求职经验"
  },
  {
    "id": 4,
    "name": "校园生活"
  },
  {
    "id": 5,
    "name": "校友聚会"
  },
  {
    "id": 6,
    "name": "其他"
  }
]
```

### 2.3 用户数据

```json
[
  {
    "id": 1,
    "phone": "13800138001",
    "openId": "wx_openid_001",
    "name": "张三",
    "avatar": "assets/avatar_001.png",
    "collegeId": "1",
    "verifyStatus": 2
  },
  {
    "id": 2,
    "phone": "13800138002",
    "openId": "wx_openid_002",
    "name": "李四",
    "avatar": "assets/avatar_002.png",
    "collegeId": "2",
    "verifyStatus": 2
  },
  {
    "id": 3,
    "phone": "13800138003",
    "openId": "wx_openid_003",
    "name": "王五",
    "avatar": "assets/avatar_003.png",
    "collegeId": "3",
    "verifyStatus": 1
  },
  {
    "id": 4,
    "phone": "13800138004",
    "openId": "wx_openid_004",
    "name": "赵六",
    "avatar": "assets/avatar_004.png",
    "collegeId": "4",
    "verifyStatus": 0
  },
  {
    "id": 5,
    "phone": "13800138005",
    "openId": "wx_openid_005",
    "name": "钱七",
    "avatar": "assets/avatar_005.png",
    "collegeId": "5",
    "verifyStatus": 2
  }
]
```

### 2.4 帖子数据

```json
[
  {
    "id": 1,
    "date": "2026-02-01 10:00:00",
    "title": "Spring Boot 3.0 新特性详解",
    "summary": "Spring Boot 3.0 带来了很多新特性，包括对 Java 17 的支持、GraalVM 原生镜像的改进等...",
    "content": "Spring Boot 3.0 带来了很多新特性，包括对 Java 17 的支持、GraalVM 原生镜像的改进、Jakarta EE 的迁移等。本文将详细介绍这些新特性，并通过示例代码展示如何使用它们。",
    "images": ["images/spring-boot-3-0.png"],
    "authorId": 1,
    "categoryId": 1,
    "likeCount": 15,
    "shareCount": 5,
    "commentCount": 8,
    "status": 1,
    "rejectReason": null
  },
  {
    "id": 2,
    "date": "2026-02-01 11:30:00",
    "title": "阿里巴巴实习面试经验分享",
    "summary": "本文分享了我在阿里巴巴实习面试的全过程，包括简历准备、技术面试、HR面试等环节...",
    "content": "本文分享了我在阿里巴巴实习面试的全过程，包括简历准备、技术面试、HR面试等环节的经验和技巧。希望对正在找实习或工作的同学有所帮助。",
    "images": ["images/alibaba-interview.png"],
    "authorId": 2,
    "categoryId": 2,
    "likeCount": 23,
    "shareCount": 12,
    "commentCount": 15,
    "status": 1,
    "rejectReason": null
  },
  {
    "id": 3,
    "date": "2026-02-01 14:20:00",
    "title": "校园图书馆新馆开放了",
    "summary": "学校图书馆新馆终于开放了，环境非常好，有很多新的设施和资源...",
    "content": "学校图书馆新馆终于开放了，环境非常好，有很多新的设施和资源。本文分享了新馆的一些照片和使用体验。",
    "images": ["images/library-new-1.png", "images/library-new-2.png"],
    "authorId": 3,
    "categoryId": 3,
    "likeCount": 8,
    "shareCount": 3,
    "commentCount": 5,
    "status": 0,
    "rejectReason": null
  },
  {
    "id": 4,
    "date": "2026-02-01 16:45:00",
    "title": "10周年校友聚会通知",
    "summary": "母校将举办10周年校友聚会，欢迎各位校友参加...",
    "content": "母校将举办10周年校友聚会，欢迎各位校友参加。本文包含聚会时间、地点、流程等详细信息。",
    "images": ["images/alumni-meeting.png"],
    "authorId": 4,
    "categoryId": 4,
    "likeCount": 12,
    "shareCount": 8,
    "commentCount": 10,
    "status": 2,
    "rejectReason": "内容需要进一步审核"
  },
  {
    "id": 5,
    "date": "2026-02-01 19:10:00",
    "title": "如何高效学习计算机专业课程",
    "summary": "作为一名计算机专业的学生，如何高效学习专业课程是一个重要的问题...",
    "content": "作为一名计算机专业的学生，如何高效学习专业课程是一个重要的问题。本文分享了一些学习方法和技巧，希望对大家有所帮助。",
    "images": ["images/study-tips.png"],
    "authorId": 5,
    "categoryId": 1,
    "likeCount": 20,
    "shareCount": 6,
    "commentCount": 12,
    "status": 1,
    "rejectReason": null
  }
]
```

### 2.5 评论数据

```json
[
  {
    "id": 1,
    "date": "2026-02-01 10:30:00",
    "content": "非常详细的分享，学到了很多！",
    "commenterId": 2,
    "postId": 1,
    "likeCount": 3,
    "status": 1,
    "rejectReason": null
  },
  {
    "id": 2,
    "date": "2026-02-01 10:45:00",
    "content": "请问 Spring Boot 3.0 对性能有什么提升吗？",
    "commenterId": 3,
    "postId": 1,
    "likeCount": 2,
    "status": 1,
    "rejectReason": null
  },
  {
    "id": 3,
    "date": "2026-02-01 11:15:00",
    "content": "感谢分享，对我准备面试很有帮助！",
    "commenterId": 1,
    "postId": 2,
    "likeCount": 4,
    "status": 1,
    "rejectReason": null
  },
  {
    "id": 4,
    "date": "2026-02-01 11:45:00",
    "content": "请问面试中主要考察哪些技术点？",
    "commenterId": 4,
    "postId": 2,
    "likeCount": 1,
    "status": 1,
    "rejectReason": null
  },
  {
    "id": 5,
    "date": "2026-02-01 14:40:00",
    "content": "新图书馆看起来很棒，周末去看看！",
    "commenterId": 5,
    "postId": 3,
    "likeCount": 2,
    "status": 0,
    "rejectReason": null
  }
]
```

## 3. 接口测试数据

### 3.1 GET /api/posts - 获取公开帖子列表

#### 测试用例 1: 正常获取帖子列表

```json
{
  "page": 0,
  "categoryId": null,
  "keyword": null
}
```

#### 测试用例 2: 按分类筛选

```json
{
  "page": 0,
  "categoryId": 1,
  "keyword": null
}
```

#### 测试用例 3: 按关键词搜索

```json
{
  "page": 0,
  "categoryId": null,
  "keyword": "Spring"
}
```

#### 测试用例 4: 组合筛选

```json
{
  "page": 0,
  "categoryId": 1,
  "keyword": "Spring"
}
```

#### 测试用例 5: 分页测试

```json
{
  "page": 1,
  "categoryId": null,
  "keyword": null
}
```

### 3.2 POST /api/posts - 发布新帖子

#### 测试用例 1: 正常发布帖子

```json
{
  "title": "测试帖子标题",
  "content": "这是测试帖子的内容，包含详细的测试信息。",
  "images": ["images/test1.png", "images/test2.png"],
  "likeCount": 0
}
```

#### 测试用例 2: 无图片帖子

```json
{
  "title": "无图片测试帖子",
  "content": "这是一个没有图片的测试帖子。",
  "images": [],
  "likeCount": 0
}
```

#### 测试用例 3: 长内容帖子

```json
{
  "title": "长内容测试帖子",
  "content": "这是一个长内容的测试帖子，用于测试摘要生成功能。" + "内容重复" * 20,
  "images": [],
  "likeCount": 0
}
```

### 3.3 GET /api/posts/{id} - 获取帖子详情

#### 测试用例 1: 正常获取帖子详情

```
GET /api/posts/1
```

#### 测试用例 2: 获取不存在的帖子

```
GET /api/posts/999
```

### 3.4 PUT /api/posts/{id} - 修改帖子

#### 测试用例 1: 正常修改帖子

```json
{
  "title": "修改后的帖子标题",
  "content": "修改后的帖子内容",
  "images": ["images/updated1.png"],
  "categoryId": 2
}
```

#### 测试用例 2: 修改帖子分类

```json
{
  "title": "Spring Boot 3.0 新特性详解",
  "content": "Spring Boot 3.0 带来了很多新特性...",
  "images": ["images/spring-boot-3-0.png"],
  "categoryId": 2
}
```

### 3.5 DELETE /api/posts/{id} - 删除帖子

#### 测试用例 1: 正常删除帖子

```
DELETE /api/posts/1
```

#### 测试用例 2: 删除不存在的帖子

```
DELETE /api/posts/999
```

### 3.6 GET /api/comments - 获取帖子评论列表

#### 测试用例 1: 正常获取评论列表

```json
{
  "postId": "1",
  "page": 0
}
```

#### 测试用例 2: 获取不存在帖子的评论

```json
{
  "postId": "999",
  "page": 0
}
```

#### 测试用例 3: 分页测试

```json
{
  "postId": "1",
  "page": 1
}
```

### 3.7 POST /api/comments - 发布新评论

#### 测试用例 1: 正常发布评论

```json
{
  "content": "这是一条测试评论",
  "postId": 1
}
```

#### 测试用例 2: 发布长评论

```json
{
  "content": "这是一条很长的测试评论，用于测试评论功能的完整性。" + "评论内容重复" * 10,
  "postId": 1
}
```

### 3.8 PUT /api/comments/{id} - 修改评论

#### 测试用例 1: 正常修改评论

```json
{
  "content": "修改后的评论内容"
}
```

### 3.9 DELETE /api/comments/{id} - 删除评论

#### 测试用例 1: 正常删除评论

```
DELETE /api/comments/1
```

### 3.10 GET /api/users/posts - 获取我的帖子列表

#### 测试用例 1: 正常获取

```json
{
  "page": 0
}
```

#### 测试用例 2: 分页测试

```json
{
  "page": 1
}
```

### 3.11 GET /api/users/comments - 获取我的评论列表

#### 测试用例 1: 正常获取

```json
{
  "page": 0
}
```

#### 测试用例 2: 分页测试

```json
{
  "page": 1
}
```

### 3.12 GET /api/oss/sign - 获取阿里云OSS POST签名v4

#### 测试用例 1: 正常获取签名

```
GET /api/oss/sign
```

