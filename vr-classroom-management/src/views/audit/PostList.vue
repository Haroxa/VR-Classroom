<template>
  <div class="post-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>帖子审核</span>
        </div>
      </template>
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部" style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="0" />
              <el-option label="已发布" value="1" />
              <el-option label="已驳回" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="filterForm.categoryId" placeholder="全部" style="width: 150px;">
              <el-option label="全部" value="" />
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="filterForm.keyword" placeholder="标题或内容" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getPosts">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="table-container">
        <el-table :data="postsData" style="width: 100%" border>
          <el-table-column label="ID" width="100">
            <template #default="scope">
              {{ (page - 1) * pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="300">
            <template #default="scope">
              <div class="post-title">
                <a href="javascript:void(0)" @click="viewPost(scope.row.id)">{{ scope.row.title }}</a>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="date" label="发布时间" width="180" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="作者" width="120">
            <template #default="scope">
              {{ scope.row.author?.name || '未知作者' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="success" size="small" @click="approvePost(scope.row)" :disabled="scope.row.status !== 0">通过</el-button>
              <el-button type="danger" size="small" @click="rejectPost(scope.row)" :disabled="scope.row.status !== 0">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 15, 20, 25, 30, 50, 75, 100, 200]"
          layout="sizes, prev, pager, next, jumper"
          :page-count="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
          size="small"
        />
      </div>
    </el-card>

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="auditDialogVisible"
      :title="auditForm.status === 1 ? '通过帖子' : '驳回帖子'"
      width="500px"
    >
      <el-form :model="auditForm" :rules="auditRules" ref="auditFormRef">
        <el-form-item v-if="auditForm.status === 1" label="通过理由">
          <el-input
            v-model="auditForm.rejectReason"
            type="textarea"
            placeholder="审核通过"
            rows="2"
            readonly
          />
        </el-form-item>
        <el-form-item v-if="auditForm.status === 2" label="驳回理由" prop="rejectReason">
          <el-select v-model="auditForm.selectedReason" placeholder="请选择常见驳回理由" @change="selectReason">
            <el-option
              v-for="reason in commonReasons"
              :key="reason.value"
              :label="reason.label"
              :value="reason.value"
            />
          </el-select>
          <el-input
            v-model="auditForm.rejectReason"
            type="textarea"
            placeholder="请输入驳回理由"
            rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button :type="auditForm.status === 1 ? 'success' : 'danger'" @click="submitAudit">{{ auditForm.status === 1 ? '通过' : '驳回' }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 帖子详情弹窗 -->
    <el-dialog
      v-model="postDialogVisible"
      title="帖子详情"
      width="800px"
    >
      <div v-if="currentPost" class="post-detail">
        <h2>{{ currentPost.title }}</h2>
        <div class="post-meta">
          <span>发布时间：{{ currentPost.date }}</span>
          <span>分类：{{ currentPost.categoryName }}</span>
          <span>作者：{{ currentPost.author.name }}</span>
          <span>状态：
            <el-tag :type="getStatusType(currentPost.status)">
              {{ getStatusText(currentPost.status) }}
            </el-tag>
          </span>
        </div>
        <div class="post-content" v-html="currentPost.summary || currentPost.content"></div>
        <div v-if="currentPost.images && currentPost.images.length > 0" class="post-images">
          <el-image
            v-for="(image, index) in currentPost.images"
            :key="index"
            :src="image"
            :preview-src-list="currentPost.images"
            style="width: 100px; height: 100px; margin-right: 10px"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../services/api'

const postsData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

const filterForm = reactive({
  status: '',
  categoryId: '',
  keyword: ''
})

const categories = ref([])

// 获取分类列表
const getCategories = async () => {
  try {
    // 检查缓存
    const cachedData = localStorage.getItem('categories')
    const cachedExpiry = localStorage.getItem('categories_expiry')
    const now = Date.now()
    
    // 如果缓存存在且未过期（24小时）
    if (cachedData && cachedExpiry && now < parseInt(cachedExpiry)) {
      categories.value = JSON.parse(cachedData)
      return
    }
    
    // 缓存无效，请求API
    const response = await api.getCategories()
    if (response.code === 0) {
      categories.value = response.data
      // 存储到缓存，设置24小时过期
      localStorage.setItem('categories', JSON.stringify(response.data))
      localStorage.setItem('categories_expiry', (now + 24 * 60 * 60 * 1000).toString())
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

const auditDialogVisible = ref(false)
const auditForm = reactive({
  status: 1,
  rejectReason: '',
  selectedReason: ''
})
const auditRules = {
  status: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
  rejectReason: [{ required: true, message: '请输入驳回理由', trigger: 'blur' }]
}
const auditFormRef = ref(null)
const currentPostId = ref('')

// 常见的驳回理由
const commonReasons = [
  { label: '内容与主题无关', value: '内容与主题无关' },
  { label: '包含违规信息', value: '包含违规信息' },
  { label: '内容重复', value: '内容重复' },
  { label: '质量过低', value: '质量过低' },
  { label: '其他原因', value: '其他原因' }
]

const postDialogVisible = ref(false)
const currentPost = ref(null)

const getPosts = async () => {
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      status: filterForm.status,
      categoryId: filterForm.categoryId || undefined,
      keyword: filterForm.keyword
    }
    const response = await api.getAdminPosts(params)
    if (response.code === 0) {
      // 处理图片路径，转换为完整路径
      const records = (response.data.records || []).map(post => {
        if (post.images && post.images.length > 0) {
          post.images = post.images.map(url => {
            if (url.startsWith('/')) {
              return `http://localhost:8082${url}`
            }
            return url
          })
        }
        
        // 处理分类名称映射
        if (post.categoryId) {
          const category = categories.value.find(cat => cat.id == post.categoryId)
          if (category) {
            post.categoryName = category.name
          } else {
            post.categoryName = '未知分类'
          }
        }
        
        return post
      })
      postsData.value = records
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
    // 确保数据初始化，避免组件更新错误
    postsData.value = []
    total.value = 0
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getPosts()
}

const handleCurrentChange = (current) => {
  page.value = current
  getPosts()
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.categoryId = ''
  filterForm.keyword = ''
  getPosts()
}

const getStatusType = (status) => {
  switch (status) {
    case -1: return 'info'
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    default: return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case -1: return '已删除'
    case 0: return '待审核'
    case 1: return '已发布'
    case 2: return '已驳回'
    default: return ''
  }
}

// 选择驳回理由
const selectReason = (value) => {
  auditForm.rejectReason = value
}

// 通过帖子
const approvePost = (post) => {
  currentPostId.value = post.id
  auditForm.status = 1
  auditForm.rejectReason = '审核通过'
  auditForm.selectedReason = ''
  auditDialogVisible.value = true
}

// 驳回帖子
const rejectPost = (post) => {
  currentPostId.value = post.id
  auditForm.status = 2
  auditForm.rejectReason = ''
  auditForm.selectedReason = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  if (!auditFormRef.value) return
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = {
          status: auditForm.status,
          rejectReason: auditForm.rejectReason
        }
        const response = await api.auditPost(currentPostId.value, data)
        if (response.code === 0) {
          ElMessage.success('审核成功')
          auditDialogVisible.value = false
          getPosts()
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error('审核失败，请重试')
      }
    }
  })
}

const viewPost = (id) => {
  // 从帖子列表中查找对应ID的帖子
  const post = postsData.value.find(p => p.id === id)
  if (post) {
    console.log('从列表中获取帖子详情:', post)
    // 处理图片路径，转换为完整路径
    const postCopy = { ...post }
    if (postCopy.images && postCopy.images.length > 0) {
      postCopy.images = postCopy.images.map(url => {
        if (url.startsWith('/')) {
          return `http://localhost:8082${url}`
        }
        return url
      })
    }
    // 确保状态字段存在且为数字类型
    if (postCopy.status === undefined) {
      postCopy.status = 0 // 默认设置为待审核状态
    } else if (typeof postCopy.status === 'string') {
      postCopy.status = parseInt(postCopy.status) // 转换为数字类型
    }
    currentPost.value = postCopy
    postDialogVisible.value = true
  } else {
    ElMessage.error('未找到帖子信息')
  }
}

onMounted(async () => {
  await getCategories()
  getPosts()
})
</script>

<style scoped>
.post-list {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 20px;
}

.table-container {
  overflow-x: auto;
  overflow-y: auto;
  margin-bottom: 20px;
  min-height: 400px;
  max-height: 500px;
  height: 400px;
}

.table-container::-webkit-scrollbar {
  height: 8px;
  width: 8px;
}

.table-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.post-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.post-title a {
  color: #1890ff;
  text-decoration: none;
}

.post-title a:hover {
  text-decoration: underline;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding-right: 10px;
}

.post-detail h2 {
  margin-bottom: 20px;
  color: #303133;
}

.post-meta {
  margin-bottom: 20px;
  color: #909399;
  font-size: 14px;
}

.post-meta span {
  margin-right: 20px;
}

.post-content {
  margin-bottom: 20px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-images {
  margin-top: 20px;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>