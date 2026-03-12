# Docker部署说明

本文件提供了VR教室管理系统的Docker部署配置和使用指南。

## 项目结构

- `Dockerfile` - 前端应用的Docker构建文件
- `nginx.conf` - Nginx配置文件，用于前端静态文件服务和API代理
- `docker-compose.yml` - Docker Compose配置文件，定义了完整的应用栈

## 部署步骤

### 1. 准备工作

- 确保已安装Docker和Docker Compose
- 确保后端项目已构建为Docker镜像（`vr-classroom-backend:latest`）

### 2. 构建和运行

在项目根目录执行以下命令：

```bash
# 构建并运行整个应用栈
docker-compose up -d

# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 3. 访问应用

- 前端应用：http://localhost
- 后端API：http://localhost:8082/api

## 配置说明

### 前端配置

- 基于Vue 3 + Vite构建
- 使用Nginx作为静态文件服务器
- API请求通过Nginx代理到后端服务

### 后端配置

- 环境变量配置在`docker-compose.yml`中
- 数据库连接：MySQL 8.0
- 缓存：Redis 7.0

### 数据库配置

- 自动创建`vr_classroom`数据库
- 用户名：root
- 密码：123456

## 常见问题

### 1. 后端服务无法启动

检查：
- MySQL服务是否正常运行
- 数据库连接参数是否正确
- 后端镜像是否存在

### 2. 前端无法访问后端API

检查：
- 后端服务是否正常运行
- Nginx配置中的代理设置是否正确
- 网络连接是否畅通

### 3. 构建失败

检查：
- Node.js版本是否兼容
- 依赖安装是否成功
- 构建命令是否正确

## 扩展配置

### 生产环境配置

1. 修改`docker-compose.yml`中的环境变量
2. 配置HTTPS（添加SSL证书）
3. 调整容器资源限制

### 多环境部署

可以通过创建不同的`docker-compose`文件来实现多环境部署：
- `docker-compose.dev.yml` - 开发环境
- `docker-compose.prod.yml` - 生产环境

使用方式：
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```