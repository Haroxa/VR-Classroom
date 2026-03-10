# JMeter服务器性能测试方案

## 1. 测试目标

### 1.1 主要目标
- 评估未优化服务器的性能上限
- 识别系统瓶颈和性能问题
- 为后续高并发优化提供数据支撑
- 建立性能基准，用于优化前后对比

### 1.2 具体目标
- 测试系统在不同并发用户下的QPS表现
- 测试系统在高负载下的响应时间变化
- 测试系统的稳定性和错误率
- 测试系统资源使用情况

## 2. 测试环境

### 2.1 服务器环境
- **服务器地址**：10.86.136.242:8082
- **部署方式**：Docker容器（5个后端实例）
- **系统配置**：
  - 容器内存：4GB/实例
  - CPU限制：1.5核/实例
  - Nginx负载均衡

### 2.2 测试工具
- **JMeter版本**：5.6.3
- **测试机器**：Linux服务器
- **网络环境**：局域网

## 3. 测试场景设计

### 3.1 基础测试场景

| 场景 | 并发用户数 | 启动时间 | 持续时间 | 循环次数 | 总请求数 |
|------|------------|----------|----------|----------|----------|
| **场景1-基础负载** | 100 | 60秒 | 300秒 | 30 | ~3000 |
| **场景2-中等负载** | 500 | 60秒 | 300秒 | 30 | ~15000 |
| **场景3-高负载** | 1000 | 60秒 | 300秒 | 30 | ~30000 |
| **场景4-极限负载** | 2000 | 120秒 | 300秒 | 30 | ~60000 |
| **场景5-持续负载** | 500 | 60秒 | 1800秒 | 180 | ~90000 |

### 3.2 测试接口

| 接口 | 方法 | 路径 | 权重 | 说明 |
|------|------|------|------|------|
| 获取帖子列表 | GET | `/api/posts?page=1` | 70% | 主要业务接口 |
| 获取帖子详情 | GET | `/api/posts/1` | 15% | 详情查询接口 |
| 获取审核帖子列表 | GET | `/api/admin/posts?page=1` | 15% | 后台管理接口 |

### 3.3 测试数据

| 数据类型 | 数据量 | 说明 |
|----------|--------|------|
| 帖子数据 | 1000+ | 包含不同分类和状态的帖子 |
| 评论数据 | 5000+ | 每个帖子平均5条评论 |
| 用户数据 | 100+ | 包含普通用户和管理员 |

## 4. 测试配置

### 4.1 JMeter配置

#### 4.1.1 线程组配置
```xml
<ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="场景X-XXX负载测试">
  <intProp name="ThreadGroup.num_threads">[并发用户数]</intProp>
  <intProp name="ThreadGroup.ramp_time">[启动时间]</intProp>
  <boolProp name="ThreadGroup.scheduler">true</boolProp>
  <stringProp name="ThreadGroup.duration">[持续时间]</stringProp>
  <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="循环控制器">
    <stringProp name="LoopController.loops">[循环次数]</stringProp>
    <boolProp name="LoopController.continue_forever">false</boolProp>
  </elementProp>
</ThreadGroup>
```

#### 4.1.2 HTTP请求配置
```xml
<HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="[接口名称]">
  <intProp name="HTTPSampler.connect_timeout">10000</intProp>
  <intProp name="HTTPSampler.response_timeout">30000</intProp>
  <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
  <stringProp name="HTTPSampler.port">${PORT}</stringProp>
  <stringProp name="HTTPSampler.protocol">http</stringProp>
  <stringProp name="HTTPSampler.path">[接口路径]</stringProp>
  <stringProp name="HTTPSampler.method">GET</stringProp>
  <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
</HTTPSamplerProxy>
```

#### 4.1.3 监听器配置
- **聚合报告**：收集响应时间、QPS等核心指标
- **图形结果**：展示响应时间趋势
- **查看结果树**：详细的请求和响应信息
- **Synthesis Report**：综合性能指标
- **后端监听器**：将结果保存到InfluxDB（可选）

### 4.2 环境变量配置

| 变量 | 值 | 说明 |
|------|-----|------|
| BASE_URL | 10.86.136.242 | 服务器IP地址 |
| PORT | 8082 | 服务器端口 |
| JMETER_HOME | /opt/jmeter | JMeter安装目录 |
| JVM_ARGS | -Xms2g -Xmx4g | JVM内存配置 |

## 5. 测试执行步骤

### 5.1 准备工作
1. **安装JMeter**：
   ```bash
   wget https://downloads.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz
   tar -xzf apache-jmeter-5.6.3.tgz
   sudo mv apache-jmeter-5.6.3 /opt/jmeter
   sudo ln -s /opt/jmeter/bin/jmeter /usr/local/bin/jmeter
   ```

2. **准备测试数据**：
   - 确保数据库中有足够的测试数据
   - 验证所有接口正常工作

3. **配置监控**：
   - 启动系统资源监控
   - 配置应用日志监控

### 5.2 执行测试

#### 5.2.1 命令行执行
```powershell
# 场景1-基础负载测试（100并发）
# 作用：使用100个并发用户，60秒内逐渐启动，持续运行300秒，测试系统在基础负载下的性能
& "P:\Tools\apache-jmeter-5.6.3\bin\jmeter" -n -t jmeter-server-test.jmx -JthreadCount=100 -JrampUp=60 -Jduration=300 -l test-results/scenario1.jtl

# 场景2-中等负载测试（500并发）
# 作用：使用500个并发用户，60秒内逐渐启动，持续运行300秒，测试系统在中等负载下的性能
& "P:\Tools\apache-jmeter-5.6.3\bin\jmeter" -n -t jmeter-server-test.jmx -JthreadCount=500 -JrampUp=60 -Jduration=300 -l test-results/scenario2.jtl

# 场景3-高负载测试（1000并发）
# 作用：使用1000个并发用户，60秒内逐渐启动，持续运行300秒，测试系统在高负载下的性能
& "P:\Tools\apache-jmeter-5.6.3\bin\jmeter" -n -t jmeter-server-test.jmx -JthreadCount=1000 -JrampUp=60 -Jduration=300 -l test-results/scenario3.jtl

# 场景4-极限负载测试（2000并发）
# 作用：使用2000个并发用户，120秒内逐渐启动，持续运行300秒，测试系统在极限负载下的性能
& "P:\Tools\apache-jmeter-5.6.3\bin\jmeter" -n -t jmeter-server-test.jmx -JthreadCount=2000 -JrampUp=120 -Jduration=300 -l test-results/scenario4.jtl

# 场景5-持续负载测试（500并发，30分钟）
# 作用：使用500个并发用户，60秒内逐渐启动，持续运行1800秒（30分钟），测试系统在持续负载下的稳定性
& "P:\Tools\apache-jmeter-5.6.3\bin\jmeter" -n -t jmeter-server-test.jmx -JthreadCount=500 -JrampUp=60 -Jduration=1800 -l test-results/scenario5.jtl
```

#### 5.2.2 GUI模式执行
```bash
# 启动JMeter GUI
jmeter -t jmeter-server-test.jmx

# 在GUI中修改线程组参数并执行测试
```

### 5.3 监控与记录

#### 5.3.1 系统监控
```bash
# 监控CPU和内存
watch -n 1 'top -b -n 1 | head -20'

# 监控磁盘IO
iostat -x 1

# 监控网络IO
tcpdump -i eth0 -n port 8082

# 监控数据库连接
mysql -u root -p -e "SHOW PROCESSLIST;"
```

#### 5.3.2 应用监控
```bash
# 查看应用日志
tail -f /path/to/application.log

# 查看Nginx日志
tail -f /var/log/nginx/access.log

# 健康检查
curl -s http://10.86.136.242:8082/actuator/health
```

## 6. 性能指标与基准

### 6.1 核心性能指标

| 指标 | 单位 | 目标值 | 警告值 | 临界值 |
|------|------|--------|--------|--------|
| **QPS** | 次/秒 | > 8000 | < 5000 | < 3000 |
| **平均响应时间** | 毫秒 | < 200 | < 500 | < 1000 |
| **P95响应时间** | 毫秒 | < 500 | < 800 | < 1500 |
| **P99响应时间** | 毫秒 | < 1000 | < 1500 | < 2000 |
| **错误率** | % | < 1 | < 5 | < 10 |
| **CPU使用率** | % | < 70 | < 85 | < 95 |
| **内存使用率** | % | < 80 | < 90 | < 95 |
| **数据库连接数** | 个 | < 100 | < 150 | < 200 |

### 6.2 性能基准

| 并发用户 | 预期QPS | 预期响应时间 | 预期错误率 |
|----------|---------|--------------|------------|
| 100 | > 1000 | < 50ms | < 0.1% |
| 500 | > 3000 | < 100ms | < 0.5% |
| 1000 | > 5000 | < 150ms | < 1% |
| 2000 | > 7000 | < 200ms | < 2% |

## 7. 结果分析方法

### 7.1 数据分析步骤

1. **汇总指标分析**：
   - 计算各场景的QPS、响应时间、错误率
   - 对比实际值与目标值
   - 识别性能瓶颈

2. **趋势分析**：
   - 分析响应时间随并发用户增加的变化趋势
   - 分析错误率随并发用户增加的变化趋势
   - 分析系统资源使用随负载增加的变化趋势

3. **瓶颈识别**：
   - CPU瓶颈：CPU使用率持续超过90%
   - 内存瓶颈：内存使用率持续超过90%
   - 数据库瓶颈：查询时间变长，连接数达到上限
   - 网络瓶颈：网络IO达到带宽上限
   - 应用瓶颈：线程池满，请求队列过长

### 7.2 报告生成

#### 7.2.1 JMeter HTML聚合报告
```bash
# 检查测试结果文件
ls -la results/

# 为load-100.jtl生成聚合报告
jmeter -g results/load-100.jtl -o results/report-100

# 为load-500.jtl生成聚合报告
jmeter -g results/load-500.jtl -o results/report-500

# 为load-1000.jtl生成聚合报告
jmeter -g results/load-1000.jtl -o results/report-1000
```

#### 7.2.2 查看聚合报告
1. **打开报告**：在浏览器中打开 `results/report-100/index.html`
2. **查看关键指标**：
   - 响应时间统计（平均值、中位数、95%/99%百分位）
   - 吞吐量（QPS）
   - 错误率
   - 延迟时间
   - 接口性能对比

#### 7.2.3 聚合报告分析要点
| 报告部分 | 分析内容 | 关注点 |
|---------|----------|--------|
| **Summary** | 总体性能指标 | QPS、平均响应时间、错误率 |
| **Graphs** | 性能趋势图表 | 响应时间变化、吞吐量波动 |
| **Tables** | 详细接口性能 | 每个接口的响应时间和错误率 |
| **Errors** | 错误统计 | 错误类型和频率 |

#### 7.2.4 自定义报告
- **性能概览**：各场景的核心指标对比
- **趋势图表**：QPS、响应时间、错误率随时间的变化
- **资源使用**：CPU、内存、网络IO的使用情况
- **瓶颈分析**：识别的性能瓶颈及建议
- **优化建议**：针对瓶颈的具体优化措施

## 8. 注意事项与风险控制

### 8.1 注意事项

1. **测试环境隔离**：
   - 使用独立的测试环境，避免影响生产系统
   - 确保测试环境与生产环境配置相似

2. **测试数据准备**：
   - 测试数据应接近真实生产数据
   - 确保数据量足够大，能够反映真实情况

3. **测试顺序**：
   - 从低并发开始，逐步增加
   - 每次测试后给系统充分的恢复时间

4. **监控覆盖**：
   - 同时监控系统资源和应用指标
   - 记录详细的测试日志

### 8.2 风险控制

1. **系统保护**：
   - 设置最大并发上限，避免系统崩溃
   - 配置自动停止条件（如错误率超过10%）

2. **数据安全**：
   - 使用测试数据，避免使用真实用户数据
   - 测试完成后清理测试数据

3. **网络安全**：
   - 确保测试网络环境安全
   - 避免测试流量影响其他系统

4. **应急措施**：
   - 准备应急停止脚本
   - 制定系统恢复方案

## 9. 测试计划时间安排

| 阶段 | 时间 | 任务 |
|------|------|------|
| **准备阶段** | 1天 | 环境搭建、数据准备、监控配置 |
| **测试执行** | 2天 | 执行5个测试场景 |
| **结果分析** | 1天 | 数据整理、瓶颈分析、报告生成 |
| **优化建议** | 1天 | 制定优化方案、编写分析报告 |

## 10. 预期输出

### 10.1 测试报告
- **执行摘要**：测试执行情况和主要发现
- **性能指标**：各场景的详细性能数据
- **瓶颈分析**：识别的性能瓶颈
- **优化建议**：具体的优化措施
- **对比分析**：与目标值的对比

### 10.2 数据文件
- **原始测试结果**：JTL格式的原始数据
- **HTML报告**：JMeter生成的详细报告
- **监控数据**：系统资源使用记录
- **日志文件**：测试过程中的应用日志

## 11. 后续工作

1. **优化实施**：根据测试结果实施性能优化
2. **验证测试**：优化后再次进行压力测试
3. **持续监控**：建立生产环境的性能监控体系
4. **定期测试**：定期进行性能测试，确保系统稳定

## 12. 附件

### 12.1 JMeter测试计划
- `jmeter-server-test.jmx`：完整的测试计划文件

### 12.2 监控脚本
- `monitor-system.sh`：系统资源监控脚本
- `monitor-app.sh`：应用监控脚本

### 12.3 报告模板
- `performance-report-template.xlsx`：性能报告模板

---

**测试负责人**：开发团队  
**测试日期**：2026-03-09  
**文档版本**：v1.1