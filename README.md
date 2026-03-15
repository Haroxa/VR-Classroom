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
- [高并发优化](#-高并发优化)
- [监控告警](#-监控告警)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)
- [常见问题](#-常见问题)
- [联系我们](#-联系我们)

---

## ✨ 功能特性

### 核心功能
- ✅ 用户管理：支持微信登录和手机号登录，优化登录流程，支持退出登录和绑定手机号
- ✅ 座位预约：支持VR教室座位的查询和预约
- ✅ 订单管理：支持订单创建、查询和状态更新
- ✅ 论坛系统：支持帖子发布和评论，优化了帖子布局，修复了images字段返回问题，修复点赞列表状态过滤
- ✅ 图片上传：支持帖子图片上传，返回访问URL，优化了存储路径（posts/{yyyy}/{MM}/{dd}/）
- ✅ 后台管理：支持用户管理、帖子审核、公告管理等功能，完善了日志分类系统

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

- 应用地址：http://localhost:8082（负载均衡入口）
- API文档：http://localhost:8082/swagger-ui.html
- 后台管理：http://localhost:8083（Management模块）

---

## 📦 使用方法

### Docker部署

```bash
# 1. 构建项目
cd vr-classroom-backend
./mvnw clean package -DskipTests

# 2. 启动容器（5个后端实例 + Nginx负载均衡 + Redis）
cd ..
docker-compose up -d

# 3. 查看状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f vr-classroom-backend-1

# 5. 查看负载均衡状态
docker exec vr-classroom-nginx cat /var/log/nginx/vr-classroom-access.log
```

> **部署架构**：5个Spring Boot实例 + Nginx负载均衡 + Redis，支持万人并发访问

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
| FILE_UPLOAD_PATH | 文件上传路径 | ./uploads |
| FILE_UPLOAD_SERVER_PATH | 服务器文件上传路径 | - |
| FILE_ACCESS_URL | 文件访问URL | /assets |

---

## 📚 API文档

详细的API接口文档已整理到 `apifox` 目录下，包含多个版本的接口定义：

| 文档文件 | 版本 | 说明 |
|---------|------|------|
| `apifox v6.0.md` | v6.0 | 最新版本，优化登录流程，新增退出登录和绑定手机号接口 |
| `apifox v5.0.md` | v5.0 | 包含所有新增接口 |
| `apifox v4.1.md` | v4.1 | 订单接口优化版本 |
| `apifox v4.0.md` | v4.0 | 基础功能版本 |
| `apifox v3.0.md` | v3.0 | 早期版本 |

### 主要接口类别

#### 用户接口
- 微信登录、手机号登录
- 退出登录、绑定手机号
- 用户信息获取与更新

#### 论坛接口
- 帖子发布、查询、详情
- 评论发布、查询
- 点赞功能

#### 图片接口
- 帖子图片上传

#### 订单接口
- 教室座位查询
- 订单创建、查询、状态更新
- 模拟支付回调

#### 后台管理接口
- 帖子审核
- 评论审核

> 详细接口定义请参考 `apifox` 目录下的对应版本文档

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

### 压力测试

项目提供JMeter压力测试方案，支持高并发场景性能测试：

| 测试文件 | 说明 | 测试场景 |
|---------|------|---------|
| `JMeter-test/jmeter-server-test.jmx` | JMeter测试计划 | 100/500/1000并发用户测试 |
| `JMeter-test/JMeter服务器测试说明.md` | 测试使用说明 | 详细的使用指南和性能指标 |
| `JMeter-test/JMeter测试方案.md` | 测试方案文档 | 完整的测试计划和场景设计 |

#### 测试场景

- **场景1-正常负载**：100并发用户，10秒启动，循环10次
- **场景2-中等负载**：500并发用户，30秒启动，循环10次
- **场景3-高负载**：1000并发用户，60秒启动，循环10次

#### 测试接口

- `GET /api/posts?page=1` - 获取帖子列表
- `GET /api/posts/1` - 获取帖子详情
- `GET /api/admin/posts?page=1` - 获取审核帖子列表

#### 运行压力测试

```bash
# 在Linux服务器上安装JMeter
wget https://downloads.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz
tar -xzf apache-jmeter-5.6.3.tgz
sudo mv apache-jmeter-5.6.3 /opt/jmeter
sudo ln -s /opt/jmeter/bin/jmeter /usr/local/bin/jmeter

# 运行压力测试（非GUI模式）
cd JMeter-test
jmeter -n -t jmeter-server-test.jmx -l test-results/result.jtl -e -o test-results/report

# 查看测试报告
# 打开 JMeter-test/test-results/report/index.html 查看详细报告
```

#### 性能目标

| 指标 | 目标值 |
|------|--------|
| QPS | > 8000 |
| 平均响应时间 | < 200ms |
| P95响应时间 | < 500ms |
| 错误率 | < 1% |
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
├── vr-classroom-management/     # 后台管理前端项目
│   ├── Dockerfile               # Docker构建文件
│   ├── nginx.conf               # Nginx配置文件
│   ├── package.json             # 前端依赖配置
│   ├── src/                     # 前端源码
│   └── dist/                    # 构建输出目录
│   │   │   │       ├── advice/         # 响应处理
│   │   │   │       ├── annotation/     # 自定义注解
│   │   │   │       ├── aspect/         # 切面
│   │   │   │       ├── common/         # 公共类
│   │   │   │       ├── config/         # 配置类
│   │   │   │       ├── constant/       # 常量
│   │   │   │       ├── exception/      # 异常处理
│   │   │   │       ├── interceptor/    # 拦截器
│   │   │   │       ├── module/         # 业务模块
│   │   │   │       │   ├── admin/      # 后台管理模块
│   │   │   │       │   ├── common/     # 通用模块
│   │   │   │       │   ├── forum/      # 论坛模块
│   │   │   │       │   ├── order/      # 订单模块
│   │   │   │       │   ├── space/      # 空间管理模块
│   │   │   │       │   ├── test/       # 测试模块
│   │   │   │       │   └── user/       # 用户模块
│   │   │   │       ├── util/           # 工具类
│   │   │   │       └── VrClassroomBackendApplication.java  # 应用入口
│   │   │   └── resources/
│   │   │       ├── db/                 # 数据库文件
│   │   │       ├── scripts/            # 脚本文件
│   │   │       ├── application.yml     # 主配置文件
│   │   │       ├── application-dev.yml # 开发环境配置
│   │   │       ├── application-prod.yml # 生产环境配置
│   │   │       ├── application-test.yml # 测试环境配置
│   │   │       └── logback-spring.xml  # 日志配置
│   │   └── test/                       # 测试代码
│   ├── .env.example                    # 环境变量示例
│   ├── .gitattributes                  # Git属性
│   ├── Dockerfile                      # Docker构建文件
│   ├── mvnw                            # Maven包装器（Linux）
│   ├── mvnw.cmd                        # Maven包装器（Windows）
│   └── pom.xml                         # Maven配置
├── mock-data/                    # 测试数据与脚本
│   ├── data/                     # 测试数据
│   ├── scripts/                  # 辅助脚本
│   └── tests/                    # 测试脚本
│       ├── __pycache__/          # 缓存文件
│       ├── test-admin-api.py     # 管理员API测试
│       ├── test-api.py           # 基础API测试
│       ├── test-comprehensive.py # 综合测试
│       ├── test-donation-payment.py # 捐赠支付测试
│       ├── test-users.py         # 用户测试
│       └── test_order_api_v4_1.py # 订单API测试
├── JMeter-test/                  # JMeter测试相关文件
│   ├── test-results/             # 测试结果
│   │   ├── report-100/           # 100并发测试报告
│   │   ├── load-100.jtl          # 100并发测试结果
│   │   ├── scenario1.jtl         # 场景1测试结果
│   │   ├── scenario2.jtl         # 场景2测试结果
│   │   └── scenario3.jtl         # 场景3测试结果
│   ├── JMeter服务器测试说明.md    # 测试说明
│   ├── JMeter测试方案.md         # 测试方案
│   ├── jmeter-load-test.jmx      # 负载测试计划
│   ├── jmeter-server-test.jmx    # 服务器测试计划
│   └── jmeter-test-direct.jmx    # 直接测试计划
├── apifox/                       # API文档
│   ├── apifox +1.0.md            # API文档版本1.0
│   ├── apifox +2.0.md            # API文档版本2.0
│   ├── apifox +3.0.md            # API文档版本3.0
│   ├── apifox +4.0.md            # API文档版本4.0
│   ├── apifox v1.0.md            # API文档版本1.0
│   ├── apifox v2.0.md            # API文档版本2.0
│   ├── apifox v3.0.md            # API文档版本3.0
│   ├── apifox v4.0.md            # API文档版本4.0
│   ├── apifox v4.1.md            # API文档版本4.1
│   └── apifox v5.0.md            # API文档版本5.0
├── doc/                          # 项目文档
│   ├── VR Classroom v1.0.pdf     # 项目文档PDF
│   ├── run.md                    # 运行说明
│   ├── 接口详细文档.md            # 接口详细文档
│   ├── 项目技术详解文档.md        # 项目技术详解
│   └── 高并发优化待办.md          # 高并发优化计划
├── .gitignore                    # Git忽略文件
├── docker-compose.yml            # Docker Compose配置（5实例）
├── nginx.conf                    # Nginx负载均衡配置
├── pytest.ini                    # pytest配置文件
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

## ⚡ 高并发优化

### 优化目标

本项目针对校友回校活动期间万人访问的高并发场景，实施了全面的优化措施，性能提升约5-10倍。

### 已实施优化

#### 1. 应用层优化

| 优化项 | 优化前 | 优化后 | 性能提升 |
|--------|--------|--------|----------|
| Tomcat线程池 | max-threads: 200 | max-threads: 2000 | +900% |
| Tomcat最小线程 | min-spare-threads: 20 | min-spare-threads: 200 | +900% |
| Tomcat等待队列 | accept-count: 100 | accept-count: 1000 | +900% |
| Redis连接池 | max-active: 8 | max-active: 50 | +525% |

#### 2. 数据库层优化

| 优化项 | 优化前 | 优化后 | 性能提升 |
|--------|--------|--------|----------|
| 数据库连接池 | 未配置 | HikariCP: 100连接 | - |
| 最小空闲连接 | 未配置 | minimum-idle: 20 | - |
| 连接超时 | 默认 | connection-timeout: 30s | - |

#### 3. 容器资源优化

| 优化项 | 优化前 | 优化后 |
|--------|--------|--------|
| 后端实例数 | 1个 | 5个 |
| 容器内存限制 | 无限制 | 4G |
| 容器CPU限制 | 无限制 | 1.5核/实例 |

#### 4. 负载均衡优化

| 优化项 | 配置 |
|--------|------|
| 负载均衡算法 | least_conn（最少连接） |
| 后端实例 | 5个实例 |
| 健康检查 | max_fails=3, fail_timeout=30s |
| Nginx连接数 | worker_connections: 2048 |

#### 5. 接口限流优化

| 接口 | 限流阈值 | 说明 |
|------|----------|------|
| POST /api/orders | 10 req/s | 创建订单接口 |
| POST /api/posts | 5 req/s | 创建帖子接口 |
| POST /api/comments | 10 req/s | 创建评论接口 |

#### 6. 异步处理优化

| 优化项 | 配置 |
|--------|------|
| 核心线程数 | 10 |
| 最大线程数 | 50 |
| 队列容量 | 200 |
| 线程名前缀 | async-task- |

### 性能预估

| 指标 | 优化前 | 优化后 |
|------|--------|--------|
| QPS | 200-400 | 8000-10000 |
| 并发用户 | 200-500 | 8000-10000 |
| 平均响应时间 | 500ms-2s | 50ms-200ms |
| 错误率 | >5% | <0.1% |

### 优化完成状态

- [x] 增加容器内存限制（4G/实例）
- [x] 配置负载均衡（5实例部署）
- [x] 配置异步线程池
- [x] Tomcat线程池优化（2000线程）
- [x] 数据库连接池优化（100连接）
- [ ] 数据库读写分离（当前规模无需）
- [ ] 引入消息队列（当前规模无需）

详细优化计划请参考 [高并发优化待办.md](./高并发优化待办.md)

---

## 📊 监控告警

### 监控方案概述

项目已集成 **Spring Boot Actuator**，提供基础的健康检查和监控能力。

### 一、容器日志监控

```powershell
# 查看后端实例日志
docker logs vr-classroom-backend-1 --tail 100

# 实时跟踪日志
docker logs -f vr-classroom-backend-1

# 查看Nginx访问日志（含负载均衡信息）
docker exec vr-classroom-nginx cat /var/log/nginx/vr-classroom-access.log

# 查看所有容器状态
docker ps -a
```

### 二、健康检查端点

```powershell
# 应用健康检查
curl http://localhost:8082/actuator/health
# 返回：{"status":"UP"}

# 应用信息
curl http://localhost:8082/actuator/info
```

### 三、Docker资源监控

```powershell
# 实时查看容器资源使用
docker stats

# 输出示例：
# CONTAINER           CPU %   MEM USAGE / LIMIT   NET I/O
# backend-1           0.5%    512MiB / 4GiB       1.2MB
# backend-2           0.3%    480MiB / 4GiB       0.8MB
# nginx               0.1%    10MiB / 256MiB      2MB
# redis               0.2%    8MiB / 128MiB       0.5MB
```

### 四、监控脚本

创建简单的健康检查脚本 `monitor.ps1`：

```powershell
# 监控脚本
$services = @(
    "http://localhost:8082/actuator/health"
)

foreach ($url in $services) {
    try {
        $response = Invoke-WebRequest -Uri $url -TimeoutSec 5
        Write-Host "✅ $url - OK" -ForegroundColor Green
    } catch {
        Write-Host "❌ $url - FAILED" -ForegroundColor Red
    }
}

# 检查容器状态
docker ps --format "table {{.Names}}\t{{.Status}}"
```

### 五、监控指标说明

| 监控项 | 检查方式 | 正常值 |
|--------|----------|--------|
| 应用健康 | `/actuator/health` | status: UP |
| 容器状态 | `docker ps` | Status: Up |
| 内存使用 | `docker stats` | < 80% 限制值 |
| CPU使用 | `docker stats` | < 70% |
| 数据库连接 | 应用日志 | 无连接超时错误 |

### 六、告警阈值建议

| 指标 | 警告阈值 | 严重阈值 |
|------|----------|----------|
| 内存使用 | > 70% | > 90% |
| CPU使用 | > 60% | > 80% |
| 响应时间 | > 500ms | > 2s |
| 错误率 | > 1% | > 5% |

### 七、生产级监控（可选）

如需更完善的监控，可引入以下组件：

| 组件 | 用途 |
|------|------|
| Prometheus | 指标采集 |
| Grafana | 可视化面板 |
| AlertManager | 告警通知 |

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
python mock-data/tests/test-api.py

# 测试管理员API
python mock-data/tests/test-admin-api.py

# 测试订单API
python mock-data/tests/test_order_api_v4_1.py
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