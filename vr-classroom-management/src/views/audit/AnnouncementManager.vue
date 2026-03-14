<template>
  <div class="announcement-manager-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="page-title">公告管理</span>
          </div>
          <div class="buttons-container">
            <!-- 暂时注释掉添加和清空按钮 -->
            <!-- <el-button type="primary" @click="openCreateDialog">
              <el-icon><Plus /></el-icon>
              <span>添加</span>
            </el-button>
            <el-button type="danger" @click="clearAllAnnouncements" :disabled="announcements.length === 0">
              <el-icon><Delete /></el-icon>
              <span>清空</span>
            </el-button> -->
          </div>
        </div>
      </template>
      
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="标签">
            <el-select v-model="filterForm.tags" placeholder="选择标签" multiple style="width: 120px">
              <el-option label="新增" value="新增" />
              <el-option label="修复" value="修复" />
              <el-option label="实现" value="实现" />
              <el-option label="更新" value="更新" />
              <el-option label="通知" value="通知" />
            </el-select>
          </el-form-item>
          <el-form-item label="发布人">
            <el-select v-model="filterForm.author" placeholder="选择发布人" style="width: 120px">
              <el-option label="后端人员" value="后端人员" />
              <el-option label="前端人员" value="前端人员" />
              <el-option label="项目负责人" value="项目负责人" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="filterForm.keyword" placeholder="搜索标题或内容" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="filterAnnouncements">查询</el-button>
            <el-button @click="clearFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="announcement-list">
        <el-table :data="paginatedAnnouncements" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column label="标签" width="200">
          <template #default="scope">
            <el-tag 
              v-for="tag in scope.row.tags" 
              :key="tag" 
              :type="getTagType(tag)"
              style="margin-right: 5px; margin-bottom: 5px;"
            >{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="发布人" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button size="small" @click="viewAnnouncement(scope.row)">
              <el-icon><View /></el-icon>
              <span>查看</span>
            </el-button>
            <el-button size="small" :type="scope.row.isActive ? 'info' : 'primary'" @click="setActiveAnnouncement(scope.row)" style="width: 80px;">
              <el-icon><Check /></el-icon>
              <span>{{ scope.row.isActive ? '取消展示' : '展示' }}</span>
            </el-button>
            <!-- 暂时注释掉删除按钮 -->
            <!-- <el-button size="small" type="danger" @click="deleteAnnouncement(scope.row.id)">
              <el-icon><Delete /></el-icon>
              <span>删除</span>
            </el-button> -->
          </template>
        </el-table-column>
        </el-table>
      </div>
      
      <div class="announcement-pagination" v-if="filteredAnnouncements.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredAnnouncements.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
          size="small"
        />
      </div>
      <div class="empty-state" v-else>
        <el-empty description="暂无符合条件的公告" />
      </div>
    </el-card>
    
    <!-- 暂时注释掉创建公告对话框 -->
    <!-- <el-dialog
      v-model="dialogVisible"
      title="创建公告"
      width="500px"
    >
      <el-form :model="newAnnouncement" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="newAnnouncement.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="newAnnouncement.tags" placeholder="请选择公告标签" multiple style="width: 100%">
            <el-option label="新增" value="新增" />
            <el-option label="修复" value="修复" />
            <el-option label="实现" value="实现" />
            <el-option label="更新" value="更新" />
            <el-option label="通知" value="通知" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="newAnnouncement.content"
            type="textarea"
            :rows="4"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="发布人">
          <el-select v-model="newAnnouncement.author" placeholder="请选择发布人" style="width: 100%">
            <el-option label="后端人员" value="后端人员" />
            <el-option label="前端人员" value="前端人员" />
            <el-option label="项目负责人" value="项目负责人" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createAnnouncement">确定</el-button>
        </span>
      </template>
    </el-dialog> -->
    
    <!-- 查看公告对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="500px"
      :close-on-click-modal="false"
      center
    >
      <div v-if="currentAnnouncement" class="announcement-dialog-content">
        <h3 class="announcement-title">{{ currentAnnouncement.title }}</h3>
        <div class="announcement-meta">
          <div class="meta-item">
            <span class="meta-label">标签：</span>
            <div class="tags-container">
              <el-tag 
                v-for="tag in currentAnnouncement.tags" 
                :key="tag" 
                :type="getTagType(tag)"
                style="margin-right: 5px; margin-bottom: 5px;"
              >{{ tag }}</el-tag>
            </div>
          </div>
          <div class="meta-item">
            <span class="meta-label">发布人：</span>
            <span class="meta-value">{{ currentAnnouncement.author }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">创建时间：</span>
            <span class="meta-value">{{ currentAnnouncement.createdAt }}</span>
          </div>
        </div>
        <div class="announcement-content">
          <div class="content-label">内容：</div>
          <div class="content-text">{{ currentAnnouncement.content }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, View, Check, Refresh } from '@element-plus/icons-vue'
import { saveAnnouncements, loadAnnouncements, clearAnnouncements } from '../../services/fileStorage'

const announcements = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentAnnouncement = ref(null)
const newAnnouncement = ref({
  title: '',
  content: '',
  author: '',
  tags: []
})

// 筛选表单
const filterForm = ref({
  tags: [],
  author: '',
  keyword: ''
})

// 分页相关状态
const page = ref(1)
const pageSize = ref(20)

// 筛选后的公告列表
const filteredAnnouncements = ref([])

// 分页后的公告列表
const paginatedAnnouncements = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredAnnouncements.value.slice(start, end)
})

// 执行筛选
const filterAnnouncements = () => {
  let result = [...announcements.value]
  
  // 按标签筛选
  if (filterForm.value.tags.length > 0) {
    result = result.filter(announcement => 
      filterForm.value.tags.some(tag => announcement.tags.includes(tag))
    )
  }
  
  // 按发布人筛选
  if (filterForm.value.author) {
    result = result.filter(announcement => 
      announcement.author === filterForm.value.author
    )
  }
  
  // 按关键词搜索
  if (filterForm.value.keyword) {
    const keyword = filterForm.value.keyword.toLowerCase()
    result = result.filter(announcement => 
      announcement.title.toLowerCase().includes(keyword) ||
      announcement.content.toLowerCase().includes(keyword)
    )
  }
  
  filteredAnnouncements.value = result
  page.value = 1
}

// 清空筛选条件
const clearFilter = () => {
  filterForm.value = {
    tags: [],
    author: '',
    keyword: ''
  }
  filteredAnnouncements.value = [...announcements.value]
  page.value = 1
}

// 处理页码大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
}

// 处理页码变化
const handleCurrentChange = (current) => {
  page.value = current
}

// 加载公告数据
const loadAnnouncementsData = async () => {
  try {
    const data = await loadAnnouncements()
    if (data && data.length > 0) {
      // 按照创建时间降序排列
      announcements.value = data.sort((a, b) => {
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
      // 重新编号，确保ID按照展示顺序
      reorderIds()
    } else {
      announcements.value = []
    }
    // 初始化筛选后的公告列表
    filteredAnnouncements.value = [...announcements.value]
  } catch (error) {
    console.error('加载公告失败:', error)
    announcements.value = []
    filteredAnnouncements.value = []
  }
}

// 保存公告数据
const saveAnnouncementsData = () => {
  try {
    saveAnnouncements(announcements.value)
    console.log('公告保存成功')
  } catch (error) {
    console.error('保存公告失败:', error)
  }
}

// 重新编号，确保ID按照展示顺序
const reorderIds = () => {
  announcements.value.forEach((announcement, index) => {
    announcement.id = index + 1
  })
  saveAnnouncementsData()
}

// 暂时注释掉创建公告函数
/*
const createAnnouncement = () => {
  if (!newAnnouncement.value.title || !newAnnouncement.value.content || !newAnnouncement.value.author || newAnnouncement.value.tags.length === 0) {
    ElMessage.warning('请填写完整的公告信息')
    return
  }
  
  const announcement = {
    id: announcements.value.length + 1,
    title: newAnnouncement.value.title,
    content: newAnnouncement.value.content,
    author: newAnnouncement.value.author,
    tags: newAnnouncement.value.tags,
    createdAt: new Date().toLocaleString(),
    isActive: false
  }
  
  announcements.value.unshift(announcement)
  // 重新编号
  reorderIds()
  // 更新筛选后的公告列表
  filterAnnouncements()
  
  // 重置表单
  newAnnouncement.value = {
    title: '',
    content: '',
    author: '',
    tags: []
  }
  
  dialogVisible.value = false
  ElMessage.success('公告创建成功')
}
*/

// 查看公告
const viewAnnouncement = (announcement) => {
  currentAnnouncement.value = announcement
  viewDialogVisible.value = true
}

// 设置为展示公告
const setActiveAnnouncement = (announcement) => {
  // 保存当前状态
  const wasActive = announcement.isActive
  
  // 先将所有公告设置为非活跃
  announcements.value.forEach(a => {
    a.isActive = false
  })
  
  // 如果当前公告已经是活跃状态，则保持非活跃（取消展示）
  // 否则设置为活跃
  if (!wasActive) {
    announcement.isActive = true
  }
  
  saveAnnouncements()
  // 更新筛选后的公告列表
  filterAnnouncements()
  ElMessage.success(announcement.isActive ? '已设置为展示公告' : '已取消展示公告')
}

// 暂时注释掉删除公告函数
/*
const deleteAnnouncement = (id) => {
  const index = announcements.value.findIndex(a => a.id === id)
  if (index !== -1) {
    announcements.value.splice(index, 1)
    // 重新编号
    reorderIds()
    // 更新筛选后的公告列表
    filterAnnouncements()
    ElMessage.success('公告删除成功')
  }
}
*/

// 暂时注释掉清空所有公告函数
/*
const clearAllAnnouncements = () => {
  announcements.value = []
  clearAnnouncements()
  // 更新筛选后的公告列表
  filterAnnouncements()
  ElMessage.success('所有公告已清空')
}
*/

// 获取标签类型
const getTagType = (tag) => {
  const tagTypes = {
    '新增': 'success',
    '修复': 'warning',
    '实现': 'info',
    '更新': 'primary',
    '通知': 'danger'
  }
  return tagTypes[tag] || 'default'
}

// 暂时注释掉打开创建公告对话框函数
/*
const openCreateDialog = () => {
  dialogVisible.value = true
}
*/

// 组件挂载时加载数据
onMounted(() => {
  loadAnnouncementsData()
})
</script>

<style scoped>
.announcement-manager-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 18px;
  font-weight: normal;
  color: #333;
  margin: 0;
}

.buttons-container {
  display: flex;
  gap: 10px;
}

.filter-container {
  margin-bottom: 20px;
}

.demo-form-inline {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.announcement-list {
  margin-bottom: 20px;
  max-height: 500px;
  overflow: auto;
}

.announcement-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination {
  margin-top: 20px;
  text-align: right;
  color: #666;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.announcement-dialog-content {
  padding: 10px 0;
}

.announcement-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
  text-align: center;
}

.announcement-meta {
  margin: 0 0 20px 0;
}

.meta-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  font-size: 14px;
}

.meta-label {
  font-weight: bold;
  color: #666;
  min-width: 80px;
}

.meta-value {
  color: #333;
  flex: 1;
}

.tags-container {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.announcement-content {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.content-label {
  font-weight: bold;
  color: #666;
  margin-bottom: 10px;
}

.content-text {
  line-height: 1.6;
  white-space: pre-wrap;
  color: #333;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  min-height: 100px;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: center;
}
</style>