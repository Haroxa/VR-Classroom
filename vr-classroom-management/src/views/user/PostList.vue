<template>
  <div class="user-post-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>浏览帖子</span>
          <el-button type="primary" @click="showCreatePostDialog">发布帖子</el-button>
        </div>
      </template>
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
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
            <el-button type="primary" @click="getPosts" :loading="loading">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      <div v-else class="post-list">
        <el-row :gutter="20">
          <el-col
            v-for="post in postsData"
            :key="post.id"
            :xs="24"
            :sm="12"
            :md="8"
          >
            <el-card :class="['post-card', { 'liked': post.liked }]" @click="viewPost(post.id)" shadow="hover">
              <!-- 头部：标题 -->
              <div class="post-header">
                <h3 class="post-title">{{ post.title }}</h3>
                <div class="post-meta">
                  <el-avatar :size="24" :src="post.author?.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar&image_size=square'" />
                  <span class="author">{{ post.author?.name || '未知作者' }}</span>
                  <span class="time">{{ post.date }}</span>
                </div>
              </div>
              
              <!-- 内容：描述 -->
              <div class="post-body">
                <p class="post-desc">{{ post.summary }}</p>
              </div>
              
              <!-- 图片区域（可选） -->
              <div v-if="post.images && post.images.length > 0" class="post-images">
                <el-image
                  v-for="(image, index) in post.images.slice(0, 3)"
                  :key="index"
                  :src="image"
                  :preview-src-list="post.images"
                  fit="cover"
                  class="post-img"
                >
                  <template #error>
                    <div class="image-error">FAILED</div>
                  </template>
                </el-image>
              </div>
              
              <!-- 底部：标签 + 互动数据（固定底部） -->
              <div class="post-footer">
                <el-tag size="small" :type="getCategoryType(post.categoryName)">{{ post.categoryName }}</el-tag>
                <div class="post-actions">
                  <span class="action-item" :class="{ 'liked': post.liked }">
                    <el-icon><Star /></el-icon> {{ post.likeCount || 0 }}
                  </span>
                  <span class="action-item">
                    <el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }}
                  </span>
                  <span class="action-item">
                    <el-icon><Share /></el-icon> {{ post.shareCount || 0 }}
                  </span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <div v-if="postsData.length === 0" class="empty-container">
          <el-empty description="暂无帖子" />
        </div>
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

    <!-- 发布帖子对话框 -->
    <el-dialog
      v-model="createPostDialogVisible"
      title="发布帖子"
      width="800px"
    >
      <el-form :model="postForm" label-width="80px">
        <el-form-item label="分类" required>
          <el-select v-model="postForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="postForm.content"
            type="textarea"
            placeholder="请输入帖子内容"
            rows="6"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="post-upload"
            action=""
            :http-request="handleImageUpload"
            :on-preview="handleImagePreview"
            :on-remove="handleImageRemove"
            :file-list="postForm.images"
            list-type="picture-card"
            :limit="9"
            :auto-upload="true"
          >
            <template #default>
              <el-icon><Plus /></el-icon>
              <div class="el-upload__text">点击上传</div>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                最多上传9张图片
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createPostDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createPost" :loading="createPostLoading">发布</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="图片预览"
      width="800px"
    >
      <el-image
        :src="previewImage"
        fit="contain"
        style="width: 100%; height: 600px"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, ChatDotRound, Share, Plus } from '@element-plus/icons-vue'
import api from '../../services/api'

const router = useRouter()

const postsData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const loading = ref(false)

const filterForm = reactive({
  categoryId: '',
  keyword: ''
})

const categories = ref([])

// 发布帖子相关状态
const createPostDialogVisible = ref(false)
const createPostLoading = ref(false)
const previewDialogVisible = ref(false)
const previewImage = ref('')

const postForm = reactive({
  title: '',
  content: '',
  images: [],
  categoryId: ''
})

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

// 根据分类名称返回标签类型，统一为蓝色
const getCategoryType = (categoryName) => {
  return 'primary'
}

const getPosts = async () => {
  try {
    loading.value = true
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      categoryId: filterForm.categoryId || undefined,
      keyword: filterForm.keyword
    }
    // 假设API接口为getPosts
    const response = await api.getPosts(params)
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
        
        // 修正：将 isLiked 字段映射到 liked
        if (post.isLiked !== undefined) {
          post.liked = post.isLiked
        }
        
        return post
      })
      postsData.value = records
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
    postsData.value = []
    total.value = 0
  } finally {
    loading.value = false
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
  filterForm.categoryId = ''
  filterForm.keyword = ''
  getPosts()
}

const viewPost = (id) => {
  // 保存当前分页状态
  savePaginationState()
  // 跳转到帖子详情页
  router.push(`/user/post/${id}`)
}

// 保存分页状态
const savePaginationState = () => {
  localStorage.setItem('postListPagination', JSON.stringify({
    page: page.value,
    pageSize: pageSize.value,
    filterForm: { ...filterForm }
  }))
}

// 恢复分页状态
const restorePaginationState = () => {
  const savedState = localStorage.getItem('postListPagination')
  if (savedState) {
    try {
      const state = JSON.parse(savedState)
      page.value = state.page || 1
      pageSize.value = state.pageSize || 20
      if (state.filterForm) {
        filterForm.categoryId = state.filterForm.categoryId || ''
        filterForm.keyword = state.filterForm.keyword || ''
      }
    } catch (error) {
      console.error('恢复分页状态失败:', error)
    }
  }
}

onMounted(async () => {
  await getCategories()
  // 恢复分页状态
  restorePaginationState()
  getPosts()
})

// 显示发布帖子对话框
const showCreatePostDialog = () => {
  // 重置表单
  postForm.title = ''
  postForm.content = ''
  postForm.images = []
  postForm.categoryId = ''
  createPostDialogVisible.value = true
}

// 处理图片上传
const handleImageUpload = async (file) => {
  try {
    const response = await api.uploadImage(file.file)
    if (response.code === 0) {
      // 构建完整的图片URL用于预览
      const imageUrl = response.data.url.startsWith('/') ? `http://localhost:8082${response.data.url}` : response.data.url
      postForm.images.push({
        name: file.file.name,
        url: imageUrl,
        response: response.data
      })
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    ElMessage.error('图片上传失败')
  }
}

// 处理图片预览
const handleImagePreview = (file) => {
  previewImage.value = file.url
  previewDialogVisible.value = true
}

// 处理图片删除
const handleImageRemove = (file, fileList) => {
  postForm.images = fileList
}

// 发布帖子
const createPost = async () => {
  // 验证表单
  if (!postForm.categoryId) {
    ElMessage.error('请选择分类')
    return
  }
  if (!postForm.title.trim()) {
    ElMessage.error('请输入帖子标题')
    return
  }
  if (!postForm.content.trim()) {
    ElMessage.error('请输入帖子内容')
    return
  }
  
  try {
    createPostLoading.value = true
    
    // 准备发布帖子的数据
    const postData = {
      title: postForm.title,
      content: postForm.content,
      images: postForm.images.map(img => img.response.url),
      likeCount: 0,
      categoryId: postForm.categoryId
    }
    
    const response = await api.createPost(postData)
    if (response.code === 0) {
      ElMessage.success('发布成功')
      createPostDialogVisible.value = false
      // 刷新帖子列表
      getPosts()
    } else {
      ElMessage.error('发布失败')
    }
  } catch (error) {
    ElMessage.error('发布失败')
  } finally {
    createPostLoading.value = false
  }
}
</script>

<style scoped>
.user-post-list {
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

.post-list {
  margin-bottom: 20px;
}

/* 关键样式：Flex 布局实现底部固定 */
.post-card {
  height: 100%; /* 必须设置高度 */
  min-height: 300px; /* 固定最小高度，确保所有卡片高度一致 */
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: all 0.3s ease;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* 头部 */
.post-header {
  margin-bottom: 12px;
}

.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 13px;
}

.author {
  color: #606266;
}

.time {
  color: #909399;
  margin-left: auto;
}

/* 内容区域 - 关键：flex: 1 填充剩余空间 */
.post-body {
  flex: 1; /* 占据所有可用空间，将底部推下去 */
  margin-bottom: 12px;
}

.post-desc {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 图片区域 */
.post-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.post-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

/* 底部区域 - 关键：始终固定在底部 */
.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  margin-top: auto; /* 保险：确保贴底 */
}

.post-actions {
  display: flex;
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.3s;
}

.action-item:hover {
  color: #409eff;
}

.action-item.liked {
  color: #409eff;
  font-weight: bold;
}

.action-item.liked .el-icon {
  color: #409eff;
}

/* 确保 el-card 内部也是 flex 布局 */
:deep(.el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 16px;
  box-sizing: border-box;
}

.loading-container {
  margin: 20px 0;
}

.empty-container {
  margin: 40px 0;
  text-align: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding-right: 10px;
}

.post-upload {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>