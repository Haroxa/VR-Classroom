# VR教室项目部署运行文档

## 目录
1. [环境准备](#环境准备)
2. [首次部署](#首次部署)
3. [单实例部署](#单实例部署)
4. [日常运行](#日常运行)
5. [更新重启](#更新重启)
6. [常见问题](#常见问题)
7. [开发环境](#开发环境)

---

## 环境准备

### 1. 系统要求
- **操作系统**: Linux / Windows / macOS
- **JDK**: OpenJDK 17+
- **Maven**: 3.8+
- **Docker**: 20.10+
- **Docker Compose**: 2.0+

### 2. 安装依赖

#### Ubuntu/Debian
```bash
# 安装 JDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# 安装 Maven
sudo apt install maven

# 安装 Docker
sudo apt install docker.io docker-compose

# 验证安装
java -version
mvn -version
docker --version
docker-compose --version
```

#### CentOS/RHEL
```bash
# 安装 JDK 17
sudo yum install java-17-openjdk-devel

# 安装 Maven
sudo yum install maven

# 安装 Docker
sudo yum install docker docker-compose

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker
```

#### Windows
1. 安装 [JDK 17](https://adoptium.net/)
2. 安装 [Maven](https://maven.apache.org/download.cgi)
3. 安装 [Docker Desktop](https://www.docker.com/products/docker-desktop)
4. 配置环境变量

---

## 首次部署

### 1. 克隆项目
```bash
# 进入项目目录
cd /home/zdd/桌面/HRX/VR-Classroom

# 克隆项目（如未克隆）
git clone <项目仓库地址>
```

### 2. 配置环境变量
```bash
# 复制环境变量模板
cp vr-classroom-backend/.env.example vr-classroom-backend/.env

# 编辑环境变量
vim vr-classroom-backend/.env
```

**关键配置项**:
```env
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://192.168.247.150:3306/cj_vr?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=cj
SPRING_DATASOURCE_PASSWORD=WEOrMSs@rtDeMw@ItoN#_YkLhUyXj167Oq5nLPBvTSQKv06v

# JWT密钥（生产环境必须修改）
JWT_SECRET=your_jwt_secret_key_at_least_32_characters_long

# OSS配置（生产环境必须配置）
OSS_ACCESS_KEY_ID=your_oss_access_key_id
OSS_ACCESS_KEY_SECRET=your_oss_access_key_secret

# 微信登录配置（生产环境必须配置）
WECHAT_APP_ID=your-wechat-app-id
WECHAT_APP_SECRET=your-wechat-app-secret
```

### 3. 构建项目
```bash
# 进入后端项目目录
cd vr-classroom-backend

# 清理并打包（跳过测试）
mvn clean package -DskipTests

# 或者包含测试（耗时较长）
mvn clean package
```

### 4. 启动服务（多实例模式）
```bash
# 返回项目根目录
cd ..

# 启动所有服务（5个后端实例）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 5. 验证部署
```bash
# 检查后端服务
curl http://localhost:8082/api/health

# 检查 Redis
docker exec vr-classroom-redis redis-cli ping
```

---

## 单实例部署

### 适用场景
- 开发测试环境
- 资源受限的服务器
- 快速验证功能

### 1. 删除现有容器
```bash
# 停止并删除所有 VR 相关容器
docker ps -a | grep vr-classroom | awk '{print $1}' | xargs docker rm -f

# 或者使用 docker-compose
docker-compose down
```

### 2. 构建后端项目
```bash
# 进入后端目录
cd /home/zdd/桌面/HRX/VR-Classroom/vr-classroom-backend

# 添加 mvnw 执行权限
chmod +x ./mvnw

# 构建项目
./mvnw clean package -DskipTests
```

### 3. 启动单实例服务
```bash
# 返回项目根目录
cd /home/zdd/桌面/HRX/VR-Classroom

# 启动单个后端实例 + Nginx + Redis
docker-compose up -d vr-classroom-backend-1 nginx redis

# 查看服务状态
docker-compose ps
```

### 4. 验证单实例运行
```bash
# 检查服务状态
docker ps --filter "name=vr-classroom" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 测试 API 接口
curl http://localhost:8082/api/posts?page=1&pageSize=10

# 检查后端日志
docker logs vr-classroom-backend-1 --tail 50
```

### 5. 单实例 vs 多实例对比

| 特性 | 单实例 | 多实例 |
|------|--------|--------|
| 资源占用 | 低 | 高 |
| 并发能力 | 一般 | 高 |
| 高可用性 | 低 | 高 |
| 适用场景 | 开发测试 | 生产环境 |
| 启动命令 | `docker-compose up -d vr-classroom-backend-1 nginx redis` | `docker-compose up -d` |

---

## 日常运行

### 查看服务状态
```bash
# 查看所有容器状态
docker-compose ps

# 查看后端服务日志
docker-compose logs -f vr-classroom-backend-1

# 查看 Redis 日志
docker-compose logs -f redis

# 查看最后100行日志
docker-compose logs --tail=100 vr-classroom-backend-1
```

### 停止服务
```bash
# 停止所有服务（保留数据）
docker-compose stop

# 停止并删除容器（保留数据卷）
docker-compose down

# 停止并删除容器和数据卷（⚠️ 数据会丢失）
docker-compose down -v
```

### 启动服务
```bash
# 启动已停止的服务
docker-compose start

# 重新创建并启动
docker-compose up -d

# 强制重新创建容器
docker-compose up -d --force-recreate
```

### 单实例服务管理
```bash
# 仅启动单实例后端
docker-compose up -d vr-classroom-backend-1

# 重启单实例后端
docker-compose restart vr-classroom-backend-1

# 停止单实例后端
docker-compose stop vr-classroom-backend-1

# 查看单实例日志
docker-compose logs -f vr-classroom-backend-1
```

---

## 更新重启

### 完整更新流程（多实例）
```bash
# 1. 进入项目目录
cd /home/zdd/桌面/HRX/VR-Classroom

# 2. 拉取最新代码
git pull origin main

# 3. 进入后端目录并重新构建
cd vr-classroom-backend
mvn clean package -DskipTests

# 4. 返回项目根目录
cd ..

# 5. 停止现有服务
docker-compose down

# 6. 启动新服务（多实例）
docker-compose up -d

# 7. 查看服务状态
docker-compose ps
```

### 单实例更新流程
```bash
# 1. 进入项目目录
cd /home/zdd/桌面/HRX/VR-Classroom

# 2. 拉取最新代码
git pull origin main

# 3. 进入后端目录并重新构建
cd vr-classroom-backend
chmod +x ./mvnw
./mvnw clean package -DskipTests

# 4. 返回项目根目录
cd ..

# 5. 删除现有容器
docker ps -a | grep vr-classroom | awk '{print $1}' | xargs docker rm -f

# 6. 启动单实例服务
docker-compose up -d vr-classroom-backend-1 nginx redis

# 7. 查看服务状态
docker-compose ps
```

### 快速热更新（仅修改配置）
```bash
# 1. 停止服务
docker-compose stop

# 2. 修改配置
vim docker-compose.yml

# 3. 启动服务
docker-compose start
```

### 仅重启后端服务
```bash
# 重新构建并重启单实例后端
docker-compose up -d --build vr-classroom-backend-1

# 或者
docker-compose restart vr-classroom-backend-1
```

### 仅重启 Redis
```bash
docker-compose restart redis
```

---

## 常见问题

### 1. 端口冲突
**问题**: 8082 或 6380 端口被占用

**解决**:
```bash
# 查看端口占用
netstat -tlnp | grep 8082

# 修改 docker-compose.yml 中的端口映射
# 例如：将 "8083:8080" 改为其他端口
vim docker-compose.yml
```

### 2. 数据库连接失败
**问题**: 无法连接到 MySQL 数据库

**解决**:
```bash
# 检查数据库连接
docker exec vr-classroom-backend-1 ping -c 3 192.168.247.150

# 检查数据库配置
docker-compose logs vr-classroom-backend-1 | grep -i "database\|datasource"

# 验证数据库凭据
mysql -h 192.168.247.150 -u cj -p
```

### 3. 内存不足
**问题**: Java 应用内存溢出

**解决**:
```bash
# 修改 docker-compose.yml 中的内存限制
vim docker-compose.yml

# 调整 JVM 参数（在 environment 中添加）
JAVA_OPTS: "-Xms512m -Xmx1024m"
```

### 4. 日志查看
```bash
# 实时查看日志
docker-compose logs -f vr-classroom-backend-1

# 查看最近100行
docker-compose logs --tail=100 vr-classroom-backend-1

# 查看特定时间段的日志
docker-compose logs --since="2026-03-04T10:00:00" vr-classroom-backend-1

# 导出日志到文件
docker-compose logs vr-classroom-backend-1 > backend.log 2>&1
```

### 5. 进入容器调试
```bash
# 进入后端容器
docker exec -it vr-classroom-backend-1 sh

# 进入 Redis 容器
docker exec -it vr-classroom-redis sh

# 在 Redis 中执行命令
docker exec vr-classroom-redis redis-cli
```

### 6. mvnw 权限问题
**问题**: `./mvnw: 权限不够`

**解决**:
```bash
# 添加执行权限
chmod +x ./mvnw

# 或者使用 mvn 命令
mvn clean package -DskipTests
```

---

## 开发环境

### 本地开发运行
```bash
# 进入后端项目目录
cd vr-classroom-backend

# 使用开发配置运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或者
java -jar target/vr-classroom-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserControllerTest

# 跳过测试打包
mvn clean package -DskipTests
```

### 代码检查
```bash
# 代码格式化检查
mvn spotless:check

# 代码格式化
mvn spotless:apply
```

---

## 生产环境部署

### 1. 安全加固
```bash
# 修改默认密码
vim vr-classroom-backend/.env

# 使用强密码
# 配置防火墙
sudo ufw allow 8082/tcp
sudo ufw enable
```

### 2. 监控配置
```bash
# 查看资源使用
docker stats

# 设置日志轮转
vim /etc/docker/daemon.json
```

### 3. 备份策略
```bash
# 备份数据库
mysqldump -h 192.168.247.150 -u cj -p cj_vr > backup_$(date +%Y%m%d).sql

# 备份 Redis 数据
docker exec vr-classroom-redis redis-cli BGSAVE
```

---

## 快捷命令汇总

```bash
# 一键更新部署（多实例）
alias vr-update='cd /home/zdd/桌面/HRX/VR-Classroom && git pull && cd vr-classroom-backend && mvn clean package -DskipTests && cd .. && docker-compose down && docker-compose up -d'

# 一键更新部署（单实例）
alias vr-update-single='cd /home/zdd/桌面/HRX/VR-Classroom && git pull && cd vr-classroom-backend && chmod +x ./mvnw && ./mvnw clean package -DskipTests && cd .. && docker ps -a | grep vr-classroom | awk '"'"'{print $1}'"'"' | xargs docker rm -f && docker-compose up -d vr-classroom-backend-1 nginx redis'

# 查看状态
alias vr-status='cd /home/zdd/桌面/HRX/VR-Classroom && docker-compose ps'

# 查看日志（单实例）
alias vr-logs='cd /home/zdd/桌面/HRX/VR-Classroom && docker-compose logs -f vr-classroom-backend-1'

# 重启服务（单实例）
alias vr-restart='cd /home/zdd/桌面/HRX/VR-Classroom && docker-compose restart vr-classroom-backend-1 nginx redis'

# 删除所有 VR 容器
alias vr-rm='docker ps -a | grep vr-classroom | awk '"'"'{print $1}'"'"' | xargs docker rm -f'

# 启动单实例
alias vr-start-single='cd /home/zdd/桌面/HRX/VR-Classroom && docker-compose up -d vr-classroom-backend-1 nginx redis'

# 启动多实例
alias vr-start-multi='cd /home/zdd/桌面/HRX/VR-Classroom && docker-compose up -d'
```

---

## 服务访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 后端 API | http://localhost:8082/api | 主后端服务 |
| Swagger UI | http://localhost:8082/swagger-ui/index.html | API 文档 |
| 前端管理 | http://localhost:8083 | 管理后台 |
| Redis | localhost:6380 | 缓存服务 |

---

## 注意事项

1. **单实例模式**：适用于开发和测试环境，生产环境建议使用多实例模式
2. **数据持久化**：Redis 数据通过 Docker 卷持久化，重启不会丢失
3. **日志管理**：定期清理日志文件，避免磁盘空间不足
4. **资源监控**：关注服务器资源使用情况，及时调整配置
