<template>
  <div class="user-my-posts">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的帖子</span>
        </div>
      </template>
      <div class="table-container">
        <el-table :data="postsData" style="width: 100%" border>
          <el-table-column label="ID" width="100">
            <template #default="scope">
              {{ (page - 1) * pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="title" label="帖子标题" min-width="300">
            <template #default="scope">
              <div class="post-title">{{ scope.row.title }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="date" label="发布时间" width="180" />
          <el-table-column prop="likeCount" label="点赞数" width="100" />
          <el-table-column prop="commentCount" label="评论数" width="100" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <div class="operation-buttons">
                <el-button size="small" @click="viewPost(scope.row.id)" class="operation-button">查看</el-button>
                <el-button type="danger" size="small" @click="deletePost(scope.row.id, scope.row.status)" class="operation-button" :disabled="scope.row.status === -1">删除</el-button>
              </div>
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
          :total="total * pageSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
          size="small"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../../services/api'

const router = useRouter()

const postsData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

// 分类列表
const categories = ref([])

// 获取帖子状态类型
const getStatusType = (status) => {
  const statusTypes = {
    '-1': 'info',
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return statusTypes[status] || 'info'
}

// 获取帖子状态文本
const getStatusText = (status) => {
  const statusTexts = {
    '-1': '已删除',
    0: '待审核',
    1: '已发布',
    2: '已驳回'
  }
  return statusTexts[status] || '未知状态'
}

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

const getMyPosts = async () => {
  if (!isLoggedIn.value) {
    return
  }
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    // 假设API接口为getMyPosts
    const response = await api.getMyPosts(params)
    if (response.code === 0) {
      // 处理分类名称映射
      const records = (response.data.records || []).map(post => {
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
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getMyPosts()
}

const handleCurrentChange = (current) => {
  page.value = current
  getMyPosts()
}

const viewPost = (postId) => {
  router.push(`/user/post/${postId}`)
}

const deletePost = async (id, status) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  if (status === -1) {
    ElMessage.warning('该帖子已删除')
    return
  }
  try {
    // 调用用户模块的删除帖子API
    const response = await api.deletePost(id)
    if (response.code === 0) {
      ElMessage.success('删除成功')
      getMyPosts()
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    ElMessage.error('删除失败，请重试')
  }
}

onMounted(async () => {
  await getCategories()
  getMyPosts()
})
</script>

<style scoped>
.user-my-posts {
  min-height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding-right: 10px;
}

.operation-buttons {
  display: flex;
  gap: 5px;
  width: 100%;
  box-sizing: border-box;
  padding: 0 5px;
}

.operation-button {
  flex: 1;
  min-width: 60px;
  text-align: center;
  padding: 0 8px;
  font-size: 12px;
}
</style>