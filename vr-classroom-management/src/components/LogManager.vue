<template>
  <div class="log-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>接口日志管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="exportLogs" :loading="exporting">
              导出日志
            </el-button>
            <el-button type="danger" @click="clearLogs" :loading="clearing">
              清空日志
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="log-filters">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="日志类型">
            <el-select v-model="filterForm.type" placeholder="全部" style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="API" value="api" />
              <el-option label="请求" value="request" />
              <el-option label="响应" value="response" />
              <el-option label="错误" value="error" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="filterForm.keyword" placeholder="路径或消息" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="filterLogs">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="log-list">
        <el-table :data="filteredLogs" style="width: 100%" border>
          <el-table-column label="时间" width="200">
            <template #default="scope">
              {{ scope.row.timestamp }}
            </template>
          </el-table-column>
          <el-table-column label="类型" width="100">
            <template #default="scope">
              <el-tag :type="getLogTypeTag(scope.row.type)">
                {{ getLogTypeText(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="消息" min-width="400">
            <template #default="scope">
              <div class="log-message">{{ scope.row.message }}</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewLogDetail(scope.row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="log-pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredLogs.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
          size="small"
        />
      </div>
    </el-card>
    
    <!-- 日志详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="日志详情"
      width="800px"
      append-to-body
    >
      <div v-if="currentLog" class="log-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="时间">{{ currentLog.timestamp }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ getLogTypeText(currentLog.type) }}</el-descriptions-item>
          <el-descriptions-item label="消息">{{ currentLog.message }}</el-descriptions-item>
          <el-descriptions-item label="详细数据">
            <div class="log-data-container">
              <div class="log-data">
                <pre>{{ formatJsonWithLineBreaks(currentLog.data) }}</pre>
              </div>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLogs, exportLogs as exportLogsService, clearLogs as clearLogsService } from '../services/logger'

const logs = ref([])
const filteredLogs = ref([])
const page = ref(1)
const pageSize = ref(20)
const exporting = ref(false)
const clearing = ref(false)
const detailDialogVisible = ref(false)
const currentLog = ref(null)

const filterForm = ref({
  type: '',
  keyword: ''
})

// 过滤日志
const filterLogs = () => {
  let result = [...logs.value]
  
  // 按类型过滤
  if (filterForm.value.type) {
    result = result.filter(log => log.type === filterForm.value.type)
  }
  
  // 按关键词过滤
  if (filterForm.value.keyword) {
    const keyword = filterForm.value.keyword.toLowerCase()
    result = result.filter(log => 
      log.message.toLowerCase().includes(keyword) ||
      JSON.stringify(log.data).toLowerCase().includes(keyword)
    )
  }
  
  filteredLogs.value = result
  page.value = 1
}

// 重置过滤器
const resetFilter = () => {
  filterForm.value.type = ''
  filterForm.value.keyword = ''
  filteredLogs.value = [...logs.value]
  page.value = 1
}

// 处理页码大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
}

// 处理页码变化
const handleCurrentChange = (current) => {
  page.value = current
}

// 导出日志
const exportLogs = async () => {
  try {
    exporting.value = true
    const success = exportLogsService()
    if (success) {
      ElMessage.success('日志导出成功')
    } else {
      ElMessage.error('日志导出失败')
    }
  } catch (error) {
    ElMessage.error('日志导出失败')
  } finally {
    exporting.value = false
  }
}

// 清空日志
const clearLogs = async () => {
  try {
    clearing.value = true
    clearLogsService()
    logs.value = []
    filteredLogs.value = []
    ElMessage.success('日志清空成功')
  } catch (error) {
    ElMessage.error('日志清空失败')
  } finally {
    clearing.value = false
  }
}

// 查看日志详情
const viewLogDetail = (log) => {
  currentLog.value = log
  detailDialogVisible.value = true
}

// 获取日志类型标签
const getLogTypeTag = (type) => {
  switch (type) {
    case 'request': return 'info'
    case 'response': return 'success'
    case 'error': return 'danger'
    case 'api': return 'info'
    default: return 'info'
  }
}

// 获取日志类型文本
const getLogTypeText = (type) => {
  switch (type) {
    case 'request': return '请求'
    case 'response': return '响应'
    case 'error': return '错误'
    case 'api': return 'API'
    default: return type
  }
}

// 格式化JSON，为长字符串添加换行
const formatJsonWithLineBreaks = (data) => {
  const jsonString = JSON.stringify(data, null, 2)
  // 为长字符串（如token）添加换行符，每50个字符换一行
  return jsonString.replace(/"([^"]{50,})"/g, (match, p1) => {
    const parts = []
    for (let i = 0; i < p1.length; i += 50) {
      parts.push(p1.substring(i, i + 50))
    }
    return `"${parts.join('\n  ')}"`
  })
}

// 加载日志
const loadLogs = () => {
  logs.value = getLogs()
  // 按时间倒序排序，确保最新的日志在最上面
  logs.value.sort((a, b) => b.timestampMs - a.timestampMs)
  filteredLogs.value = [...logs.value]
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.log-manager {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.log-filters {
  margin-bottom: 20px;
}

.log-list {
  margin-bottom: 20px;
  max-height: 500px;
  overflow: auto;
}

.log-message {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.log-data-container {
  width: 100%;
  overflow-x: auto;
}

.log-data {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  max-height: 300px;
  overflow: auto;
  min-width: 100%;
}

.log-data pre {
  margin: 0;
  font-family: monospace;
  font-size: 14px;
  white-space: pre;
  min-width: 100%;
}

.log-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
