<template>
  <div class="announcement-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <div class="header-actions">
            <el-button type="primary" size="small" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>
              添加公告
            </el-button>
            <el-button type="danger" size="small" @click="clearAnnouncements" :disabled="announcements.length === 0">
              <el-icon><Delete /></el-icon>
              清空公告
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="announcement-list">
        <el-table :data="announcements" style="width: 100%" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column label="标签" width="120">
            <template #default="scope">
              <el-tag :type="getTagType(scope.row.tag)">{{ scope.row.tag }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="author" label="发布人" width="120" />
          <el-table-column prop="createdAt" label="发布时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <div class="operation-buttons">
                <el-button size="small" @click="viewAnnouncement(scope.row)">
                  查看
                </el-button>
                <el-button type="primary" size="small" @click="setActiveAnnouncement(scope.row)" :disabled="scope.row.id === activeAnnouncement?.id">
                  {{ scope.row.id === activeAnnouncement?.id ? '已展示' : '设为展示' }}
                </el-button>
                <el-button type="danger" size="small" @click="deleteAnnouncement(scope.row.id)">
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="announcements.length === 0" class="empty-container">
          <el-empty description="暂无公告" />
        </div>
      </div>
    </el-card>
    
    <!-- 创建公告对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="添加公告"
      width="600px"
    >
      <el-form :model="newAnnouncement" :rules="formRules" ref="formRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="newAnnouncement.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="标签" prop="tag">
          <el-select v-model="newAnnouncement.tag" placeholder="请选择公告标签">
            <el-option v-for="tag in tags" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="newAnnouncement.content"
            type="textarea"
            placeholder="请输入公告内容"
            rows="5"
          />
        </el-form-item>
        <el-form-item label="发布人" prop="author">
          <el-input v-model="newAnnouncement.author" placeholder="请输入发布人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createAnnouncement">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看公告对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="600px"
    >
      <div v-if="currentAnnouncement" class="announcement-detail">
        <h3 class="announcement-title">{{ currentAnnouncement.title }}</h3>
        <div class="announcement-meta">
          <el-tag :type="getTagType(currentAnnouncement.tag)">{{ currentAnnouncement.tag }}</el-tag>
          <span class="announcement-author">发布人：{{ currentAnnouncement.author }}</span>
          <span class="announcement-time">{{ currentAnnouncement.createdAt }}</span>
        </div>
        <div class="announcement-content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const announcements = ref([])
const activeAnnouncement = ref(null)
const createDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentAnnouncement = ref(null)
const formRef = ref(null)

const tags = ['新增', '修复', '实现', '更新', '通知']

const newAnnouncement = ref({
  title: '',
  content: '',
  author: '',
  tag: ''
})

const formRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  author: [{ required: true, message: '请输入发布人', trigger: 'blur' }],
  tag: [{ required: true, message: '请选择公告标签', trigger: 'change' }]
}

// 获取标签类型
const getTagType = (tag) => {
  const tagTypes = {
    '新增': 'success',
    '修复': 'warning',
    '实现': 'info',
    '更新': 'primary',
    '通知': 'danger'
  }
  return tagTypes[tag] || 'info'
}

// 加载公告
const loadAnnouncements = () => {
  const stored = localStorage.getItem('announcements')
  if (stored) {
    announcements.value = JSON.parse(stored)
  }
  
  const storedActive = localStorage.getItem('activeAnnouncement')
  if (storedActive) {
    activeAnnouncement.value = JSON.parse(storedActive)
  }
}

// 保存公告
const saveAnnouncements = () => {
  localStorage.setItem('announcements', JSON.stringify(announcements.value))
}

// 保存活跃公告
const saveActiveAnnouncement = () => {
  localStorage.setItem('activeAnnouncement', JSON.stringify(activeAnnouncement.value))
}

// 创建公告
const createAnnouncement = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const announcement = {
        id: Date.now().toString(),
        ...newAnnouncement.value,
        createdAt: new Date().toLocaleString('zh-CN'),
        updatedAt: new Date().toLocaleString('zh-CN')
      }
      announcements.value.unshift(announcement)
      saveAnnouncements()
      createDialogVisible.value = false
      
      // 重置表单
      newAnnouncement.value = {
        title: '',
        content: '',
        author: '',
        tag: ''
      }
      
      ElMessage.success('公告添加成功')
    }
  })
}

// 查看公告
const viewAnnouncement = (announcement) => {
  currentAnnouncement.value = announcement
  viewDialogVisible.value = true
}

// 设置活跃公告
const setActiveAnnouncement = (announcement) => {
  activeAnnouncement.value = announcement
  saveActiveAnnouncement()
  ElMessage.success('公告已设置为展示')
}

// 删除公告
const deleteAnnouncement = (id) => {
  const index = announcements.value.findIndex(a => a.id === id)
  if (index !== -1) {
    announcements.value.splice(index, 1)
    saveAnnouncements()
    
    // 如果删除的是活跃公告，清除活跃公告
    if (activeAnnouncement.value && activeAnnouncement.value.id === id) {
      activeAnnouncement.value = null
      saveActiveAnnouncement()
    }
    
    ElMessage.success('公告删除成功')
  }
}

// 清空公告
const clearAnnouncements = () => {
  announcements.value = []
  activeAnnouncement.value = null
  saveAnnouncements()
  saveActiveAnnouncement()
  ElMessage.success('公告清空成功')
}

// 显示创建对话框
const showCreateDialog = () => {
  newAnnouncement.value = {
    title: '',
    content: '',
    author: '',
    tag: ''
  }
  createDialogVisible.value = true
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcement-manager {
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

.announcement-list {
  margin-top: 20px;
}

.empty-container {
  margin: 40px 0;
  text-align: center;
}

.operation-buttons {
  display: flex;
  gap: 8px;
}

.announcement-detail {
  padding: 10px 0;
}

.announcement-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.announcement-content {
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-word;
}

.announcement-author {
  flex-shrink: 0;
}

.announcement-time {
  margin-left: auto;
  flex-shrink: 0;
}
</style>