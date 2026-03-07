# VR教室后端系统

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Java](https://img.shields.io/badge/Java-17-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)]()
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.7-blue.svg)]()

> 基于Spring Boot 3和MyBatis-Plus的VR教室后端系统，提供用户管理、座位预约、订单管理等核心功能

---

## 📖 目录

- [功能特性](#-功能特性)
- [技术栈](#-技术栈)
- [快速开始](#-快速开始)
- [使用方法](#-使用方法)
- [API文档](#-api文档)
- [项目结构](#-项目结构)
- [数据库设计](#-数据库设计)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)
- [常见问题](#-常见问题)
- [联系我们](#-联系我们)

---

## ✨ 功能特性

### 核心功能
- ✅ 用户管理：支持微信登录和手机号登录
- ✅ 座位预约：支持VR教室座位的查询和预约
- ✅ 订单管理：支持订单创建、查询和状态更新
- ✅ 论坛系统：支持帖子发布和评论

### 扩展功能
- 🚀 捐赠系统：支持用户捐赠（已标记为废弃）
- 🚀 支付系统：支持订单支付（已标记为废弃）
- 🚀 OSS服务：支持文件上传（已标记为废弃）

---

## 🛠 技术栈

### 核心依赖

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.5.9 | 应用框架 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Redis | 7.0 | 缓存 |
| Lombok | 1.18.30 | 代码简化 |

### 开发工具

| 工具 | 用途 |
|------|------|
| Maven | 项目构建 |
| Docker | 容器化部署 |
| Docker Compose | 多容器管理 |

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+
- Docker（可选，用于容器化部署）

### 安装步骤

```bash
# 克隆项目
git clone <repository-url>
cd VR教室

# 进入后端目录
cd vr-classroom-backend

# 构建项目
./mvnw clean package -DskipTests

# 运行项目
java -jar target/vr-classroom-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 访问地址

- 应用地址：http://localhost:8080
- API文档：http://localhost:8080/swagger-ui.html（开发环境）

---

## 📦 使用方法

### Docker部署

```bash
# 1. 构建项目
cd vr-classroom-backend
./mvnw clean package -DskipTests

# 2. 启动容器
cd ..
docker-compose up -d

# 3. 查看状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f vr-classroom-backend
```

### 本地部署

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE vr_classroom CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入数据库结构
mysql -u root -p vr_classroom < vr-classroom-backend/src/main/resources/db/schema.sql

# 3. 导入测试数据
python insert-mock-data.py

# 4. 配置环境变量
# 编辑 application-prod.yml 文件，配置数据库和Redis连接信息

# 5. 构建并运行
./mvnw clean package -DskipTests
java -jar target/vr-classroom-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| SPRING_DATASOURCE_URL | 数据库连接URL | jdbc:mysql://mysql:3306/vr_classroom |
| SPRING_DATASOURCE_USERNAME | 数据库用户名 | root |
| SPRING_DATASOURCE_PASSWORD | 数据库密码 | password |
| SPRING_REDIS_HOST | Redis主机 | redis |
| SPRING_REDIS_PORT | Redis端口 | 6379 |
| JWT_SECRET | JWT密钥 | - |
| OSS_ENDPOINT | OSS端点 | - |
| WECHAT_APP_ID | 微信AppID | - |

---

## 📚 API文档

### 用户接口

| 路径 | 方法 | 描述 |
|------|------|------|
| `/api/users/login` | POST | 微信登录（需要loginCode和phoneCode） |
| `/api/users/login/phone` | POST | 手机号登录（测试用） |
| `/api/users/info` | GET | 获取当前用户信息 |
| `/api/users/update` | PUT | 更新用户信息 |

### 论坛接口

| 路径 | 方法 | 描述 |
|------|------|------|
| `/api/posts` | GET | 获取帖子列表 |
| `/api/posts` | POST | 创建帖子 |
| `/api/posts/{id}` | GET | 获取帖子详情 |
| `/api/posts/{id}/comments` | GET | 获取帖子评论 |

### 订单接口

| 路径 | 方法 | 描述 |
|------|------|------|
| `/api/rooms/{roomId}/seats` | GET | 获取教室座位信息 |
| `/api/orders` | POST | 创建订单（v4.1，需要campusId、buildingId、roomId和seatList） |
| `/api/orders` | GET | 获取订单列表（v4.1，支持分页） |
| `/api/orders/{orderId}` | GET | 获取订单详情（v4.1） |
| `/api/orders/{orderId}` | PATCH | 更新订单状态（用于取消订单） |
| `/api/mock/pay/notify` | POST | 模拟支付回调 |

### 捐赠/支付接口（已废弃）

| 路径 | 方法 | 描述 |
|------|------|------|
| `/api/donations` | POST | 创建捐赠订单 |
| `/api/payments/create` | POST | 创建支付订单 |
| `/api/payments/callback` | POST | 支付回调 |
| `/api/donations/my` | GET | 获取我的捐赠记录 |

> 注意：捐赠/支付相关接口已标记为@Deprecated，将在未来版本中移除

---

## 🧪 测试框架

### 测试脚本说明

项目提供完善的API测试框架，支持自动化测试和详细报告生成：

| 测试脚本 | 说明 | 功能覆盖 |
|---------|------|---------|
| `mock-data/tests/test-api.py` | 基础API测试 | 帖子、评论、用户、OSS等接口 |
| `mock-data/tests/test_order_api_v4_1.py` | 订单模块测试 | 订单创建、取消、支付、并发场景 |
| `mock-data/tests/test-admin-api.py` | 后台管理测试 | 管理员接口测试 |

### 运行测试

```bash
# 进入测试目录
cd mock-data/tests

# 运行基础API测试
python test-api.py

# 运行订单模块测试
python test_order_api_v4_1.py

# 运行所有测试（使用pytest）
pytest -v
```

### 测试报告

测试执行后会自动生成详细报告：

- **报告位置**：`mock-data/tests/logs/`
- **报告格式**：
  - Markdown格式（`.md`）：人类可读的测试报告
  - JSON格式（`.json`）：结构化数据，便于程序处理
- **报告内容**：
  - 测试摘要（总计、通过、失败、通过率）
  - 测试详情（接口、状态码、请求数据、响应数据）
  - 错误信息（如有）

### 测试框架特性

- ✅ 详细的日志输出，使用logging模块记录测试过程
- ✅ 自动收集测试详情，包括接口、状态码、请求/响应数据
- ✅ 自动生成JSON和Markdown格式的测试报告
- ✅ 支持pytest框架，可使用参数化测试
- ✅ 测试报告保存到logs目录，便于管理和查看

---

## 📁 项目结构

```
VR教室/
├── vr-classroom-backend/        # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/university/vrclassroombackend/  # 主包
│   │   │   │       ├── config/      # 配置类
│   │   │   │       ├── controller/  # 控制器
│   │   │   │       ├── service/     # 服务层
│   │   │   │       ├── mapper/      # MyBatis Mapper
│   │   │   │       ├── entity/      # 实体类
│   │   │   │       ├── dto/         # 数据传输对象
│   │   │   │       └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml       # 主配置文件
│   │   │       ├── application-dev.yml   # 开发环境配置
│   │   │       ├── application-prod.yml  # 生产环境配置
│   │   │       └── db/
│   │   │           ├── schema.sql        # 数据库结构
│   │   │           └── mock-data.sql     # 测试数据
│   │   └── test/                 # 测试代码
│   ├── Dockerfile                # Docker构建文件
│   └── pom.xml                   # Maven配置
├── mock-data/                    # 测试脚本
│   ├── create-test-users.py              # 创建测试用户
│   ├── test-api.py                       # 测试用户和论坛API
│   ├── test-admin-api.py                 # 测试管理员API
│   ├── test-donation-payment.py          # 测试捐赠和支付
│   ├── test-order-api.py                 # 测试订单API
│   └── VR教室API测试.ipynb               # 测试笔记本
├── insert-mock-data.py           # 导入测试数据脚本
├── docker-compose.yml            # Docker Compose配置
└── README.md                     # 项目说明
```

---

## 🗄 数据库设计

### 主要表结构

| 表名 | 描述 | 状态 |
|------|------|------|
| user | 用户表 | 核心表 |
| college | 学院表 | 核心表 |
| category | 分类表 | 核心表 |
| post | 帖子表 | 核心表 |
| comment | 评论表 | 核心表 |
| campus | 校区表 | 扩展表 |
| building | 建筑表 | 扩展表 |
| classroom | 教室表 | 核心表 |
| seat | 座位表 | 核心表 |
| `order` | 订单表 | 核心表 |
| order_seat | 订单座位关联表 | 核心表 |
| donation_order | 捐赠订单表 | 扩展表 |
| payment_order | 支付订单表 | 扩展表 |
| certificate | 证书表 | 扩展表 |

### 表关系

```
user ────┐
         ├── post ──── comment
         └── order ─── order_seat

classroom ─── seat

campus ─── building ─── classroom
```

> 注意：campus、building、donation_order、payment_order、certificate 为扩展表，当前版本暂未完全实现相关功能

---

## 👥 贡献指南

### 开发规范

- 使用Java开发语言
- 遵循Lombok代码简化
- 使用MyBatis-Plus进行数据库操作
- 遵循RESTful API设计规范

### 分支管理

- `main`：主分支，用于生产环境
- `develop`：开发分支
- `feature/*`：功能分支
- `hotfix/*`：修复分支

### 提交规范

```
feat: 添加新功能
fix: 修复bug
docs: 文档更新
style: 代码风格调整
refactor: 代码重构
test: 测试相关
chore: 构建/依赖调整
```

---

## 🧪 测试

### 单元测试

```bash
# 运行所有测试
./mvnw test

# 运行指定测试类
./mvnw test -Dtest=UserServiceTest
```

### API测试

```bash
# 测试用户和论坛API
python mock-data/test-api.py

# 测试管理员API
python mock-data/test-admin-api.py

# 测试捐赠和支付
python mock-data/test-donation-payment.py

# 测试订单API
python mock-data/test-order-api.py
```

### Jupyter Notebook测试

```bash
# 启动Jupyter Notebook
jupyter notebook mock-data/VR教室API测试.ipynb
```

### 测试账号

| 手机号 | 姓名 | 学院 |
|--------|------|------|
| 13800138001 | 张三 | 计算机科学与技术学院 |
| 13800138002 | 李四 | 电子信息工程学院 |
| 13800138003 | 王五 | 软件工程学院 |

---

## 📄 贡献流程

1. Fork 项目
2. 创建功能分支（`git checkout -b feature/AmazingFeature`）
3. 提交更改（`git commit -m 'feat: Add some AmazingFeature'`）
4. 推送到分支（`git push origin feature/AmazingFeature`）
5. 打开 Pull Request

---

## 📝 许可证

本项目采用MIT许可证 - 详情请查看 [LICENSE](LICENSE) 文件

---

## ❓ 常见问题

- **Q: 为什么捐赠/支付接口被标记为废弃？**
  A: 这些接口将在未来版本中被订单接口取代，目前仅保留用于兼容性。

- **Q: 如何配置微信登录？**
  A: 需要在 application.yml 中配置 WECHAT_APP_ID 和相关密钥。

- **Q: 如何处理订单超时？**
  A: 系统会自动处理超时订单，将其状态更新为取消。

- **Q: 价格单位是什么？**
  A: 所有价格相关字段统一使用"分"作为单位（1元 = 100分），包括订单金额、座位价格等。

---

## 📞 联系我们

如有问题或建议，请通过以下方式联系我们：

- 创建 Issue
- 提交 Pull Request

---

感谢您使用VR教室后端系统！