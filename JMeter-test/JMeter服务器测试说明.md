# JMeter服务器压力测试说明

## 测试计划文件

`jmeter-server-test.jmx` - 针对Linux服务器部署的压力测试计划

## 测试场景

| 场景 | 并发数 | 循环次数 | 总请求数 | 启动时间 | 说明 |
|-------|---------|-----------|-----------|----------|------|
| 场景1-正常负载 | 100 | 10 | 1000 | 10秒 | 包含3个API接口 |
| 场景2-中等负载 | 500 | 10 | 5000 | 30秒 | 获取帖子列表 |
| 场景3-高负载 | 1000 | 10 | 10000 | 60秒 | 获取帖子列表 |

## 测试的API接口

1. **获取帖子列表** - GET `/api/posts?page=1`
2. **获取帖子详情** - GET `/api/posts/1`
3. **获取审核帖子列表** - GET `/api/admin/posts?page=1`

## 使用步骤

### 1. 修改服务器地址

打开 `jmeter-server-test.jmx` 文件，修改以下变量：

```xml
<elementProp name="BASE_URL" elementType="Argument">
  <stringProp name="Argument.name">BASE_URL</stringProp>
  <stringProp name="Argument.value">10.86.136.242</stringProp>
  <stringProp name="Argument.metadata">=</stringProp>
</elementProp>

<elementProp name="PORT" elementType="Argument">
  <stringProp name="Argument.name">PORT</stringProp>
  <stringProp name="Argument.value">8082</stringProp>
  <stringProp name="Argument.metadata">=</stringProp>
</elementProp>
```

当前配置：
- **服务器IP**: `10.86.136.242`
- **端口**: `8082`

### 2. 在Linux服务器上安装JMeter

```bash
# 下载JMeter
wget https://downloads.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz

# 解压
tar -xzf apache-jmeter-5.6.3.tgz

# 移动到合适的位置
sudo mv apache-jmeter-5.6.3 /opt/jmeter

# 创建软链接
sudo ln -s /opt/jmeter/bin/jmeter /usr/local/bin/jmeter
```

### 3. 运行测试

#### 方式1：GUI模式（用于调试）

```bash
jmeter -t jmeter-server-test.jmx
```

#### 方式2：命令行模式（推荐用于压力测试）

```bash
# 运行测试并生成报告
jmeter -n -t jmeter-server-test.jmx \
  -l result.jtl \
  -e -o report

# 参数说明：
# -n: 非GUI模式
# -t: 测试计划文件
# -l: 结果文件（JTL格式）
# -e: 生成HTML报告
# -o: 报告输出目录
```

### 4. 查看测试结果

测试完成后，打开 `report/index.html` 查看详细的测试报告。

## 性能指标说明

### 关键指标

| 指标 | 说明 | 目标值 |
|-------|------|--------|
| **QPS** | 每秒查询数 | > 8000 |
| **平均响应时间** | 所有请求的平均响应时间 | < 200ms |
| **P95响应时间** | 95%的请求响应时间 | < 500ms |
| **P99响应时间** | 99%的请求响应时间 | < 1000ms |
| **错误率** | 失败请求占比 | < 1% |

### JMeter报告解读

1. **汇总报告**：
   - Samples: 总请求数
   - Average: 平均响应时间
   - 90% Line, 95% Line, 99% Line: 百分位响应时间
   - Error %: 错误率

2. **聚合报告**：
   - Throughput: QPS（每秒请求数）
   - KB/sec: 每秒传输数据量
   - Min/Max/Avg: 最小/最大/平均响应时间

## 测试建议

### 1. 渐进式测试

建议按以下顺序进行测试：

1. 先运行场景1（100并发），观察系统表现
2. 如果正常，再运行场景2（500并发）
3. 最后运行场景3（1000并发）

### 2. 监控系统资源

在测试过程中，监控系统资源使用情况：

```bash
# CPU使用率
top

# 内存使用情况
free -h

# 网络连接数
netstat -an | grep ESTABLISHED | wc -l

# 磁盘IO
iostat -x 1
```

### 3. 查看应用日志

```bash
# 查看Nginx访问日志
tail -f /var/log/nginx/access.log

# 查看应用日志
tail -f /path/to/application.log
```

## 常见问题

### 1. 连接超时

如果出现连接超时，检查：

- 服务器防火墙是否开放端口
- 服务器IP地址是否正确
- 端口号是否正确

### 2. 内存不足

如果JMeter内存不足，增加JVM堆内存：

```bash
export JVM_ARGS="-Xms2g -Xmx4g"
jmeter -n -t jmeter-server-test.jmx -l result.jtl
```

### 3. 文件描述符限制

如果出现"Too many open files"错误：

```bash
# 临时修改
ulimit -n 65536

# 永久修改（编辑 /etc/security/limits.conf）
* soft nofile 65536
* hard nofile 65536
```

## 测试报告示例

测试完成后，报告包含以下内容：

1. **Dashboard**：总体性能概览
2. **Charts**：响应时间图表
3. **Statistics**：详细统计数据
4. **Errors**：错误详情

## 注意事项

1. **测试时间**：建议在业务低峰期进行测试
2. **数据备份**：测试前备份重要数据
3. **环境隔离**：使用独立的测试环境
4. **结果对比**：多次测试取平均值

## 性能优化建议

根据测试结果，可以考虑以下优化：

1. **增加后端实例**：如果CPU/内存未满，增加实例数量
2. **优化数据库查询**：检查慢查询日志
3. **启用缓存**：使用Redis缓存热点数据
4. **调整JVM参数**：优化垃圾回收策略
5. **使用CDN**：加速静态资源访问
