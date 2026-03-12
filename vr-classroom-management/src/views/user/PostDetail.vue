<template>
  <div class="user-post-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>帖子详情</span>
          <div class="header-buttons">
            <el-button v-if="isPostOwner" type="warning" @click="editPost">编辑帖子</el-button>
            <el-button type="primary" @click="goBack">返回列表</el-button>
          </div>
        </div>
      </template>
      <!-- 添加加载状态 -->
      <el-skeleton v-if="!post" :rows="5" animated />
      <div v-else class="post-detail">
        <h2 class="post-title">{{ post.title }}</h2>
        <div class="post-meta">
          <span>发布时间：{{ post.date }}</span>
          <span>分类：{{ post.categoryName }}</span>
          <span>作者：{{ post.author?.name }}</span>
          <span>状态：
            <template v-if="post.status === null || post.status === undefined">
              <el-tag type="info">无权查看</el-tag>
            </template>
            <template v-else-if="post.status === 2">
              <el-tag type="danger" :title="post.rejectReason || '无驳回理由'">
                {{ getPostStatusText(post.status) }}
              </el-tag>
            </template>
            <template v-else>
              <el-tag :type="getPostStatusType(post.status)">{{ getPostStatusText(post.status) }}</el-tag>
            </template>
          </span>
        </div>
        <div class="post-content">{{ post.content }}</div>
        <div v-if="post.images && post.images.length > 0" class="post-images">
          <el-image
            v-for="(image, index) in post.images"
            :key="index"
            :src="image"
            :preview-src-list="post.images"
            style="width: 200px; height: 200px; margin-right: 20px; margin-bottom: 20px"
          />
        </div>
        <div class="post-actions">
          <el-button @click="likePost" :type="post.liked ? 'primary' : 'default'">
            <el-icon><Star /></el-icon> {{ post.likeCount }}
          </el-button>
          <el-button @click="sharePost">
            <el-icon><Share /></el-icon> {{ post.shareCount }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comment-section">
      <template #header>
        <div class="card-header">
          <span>评论区</span>
        </div>
      </template>
      <div class="comment-form">
        <el-form :model="commentForm" :rules="commentRules" ref="commentFormRef">
          <el-form-item prop="content">
            <el-input
              v-model="commentForm.content"
              type="textarea"
              placeholder="请输入评论内容"
              :rows="3"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitComment">发表评论</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="comment-list">
        <div v-if="!comments.length" class="empty-comments">
          <el-empty description="暂无评论，快来发表第一条评论吧" />
        </div>
        <el-card
          v-else
          v-for="comment in comments"
          :key="comment.id"
          class="comment-card"
        >
          <div class="comment-header">
            <div class="comment-author-info">
              <el-avatar :src="comment.commenter?.avatar" size="small" style="margin-right: 10px" />
              <div>
                <span class="comment-author">{{ comment.commenter?.name }}</span>
                <span class="comment-college">{{ comment.commenter?.collegeName }}</span>
                <el-tag v-if="comment.commenter?.isVerified" size="small" type="success" effect="plain">已认证</el-tag>
              </div>
            </div>
            <div class="comment-header-right">
              <template v-if="comment.status === 2">
                <el-tag size="small" type="danger" :title="comment.rejectReason || '无驳回理由'" style="margin-right: 10px">
                  已驳回
                </el-tag>
              </template>
              <template v-else-if="comment.status === 0">
                <el-tag size="small" type="warning" style="margin-right: 10px">
                  待审核
                </el-tag>
              </template>
              <template v-else-if="comment.status === 1">
                <el-tag size="small" type="success" style="margin-right: 10px">
                  已发布
                </el-tag>
              </template>
              <template v-else-if="comment.status === -1">
                <el-tag size="small" type="info" style="margin-right: 10px">
                  已删除
                </el-tag>
              </template>
              <span class="comment-date">{{ comment.date }}</span>
            </div>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-actions">
            <el-button v-if="isCommentOwner(comment)" size="small" @click="editComment(comment)">
              编辑
            </el-button>
            <el-button size="small" @click="replyComment(comment.id)">
              <el-icon><ChatDotRound /></el-icon> 回复
            </el-button>
            <el-button size="small" @click="likeComment(comment.id)" :type="comment.isLiked ? 'primary' : 'default'">
              <el-icon><Star /></el-icon> {{ comment.likeCount }}
            </el-button>
          </div>
        </el-card>
      </div>
      <div class="comment-pagination" v-if="totalComments > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 15, 20, 25, 30, 50, 75, 100, 200]"
          layout="sizes, prev, pager, next, jumper"
          :page-count="totalComments"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 回复弹窗 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复评论"
      width="500px"
    >
      <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef">
        <el-form-item prop="content">
          <el-input
            v-model="replyForm.content"
            type="textarea"
            placeholder="请输入回复内容"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑帖子对话框 -->
    <el-dialog
      v-model="editPostDialogVisible"
      title="编辑帖子"
      width="800px"
    >
      <el-form :model="editPostForm" label-width="80px">
        <el-form-item label="分类" required>
          <el-select v-model="editPostForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="editPostForm.title" placeholder="请输入帖子标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="editPostForm.content"
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
            :file-list="editPostForm.images"
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
          <el-button @click="editPostDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updatePost" :loading="updatePostLoading">保存</el-button>
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

    <!-- 编辑评论对话框 -->
    <el-dialog
      v-model="editCommentDialogVisible"
      title="编辑评论"
      width="600px"
    >
      <el-form :model="editCommentForm" label-width="80px">
        <el-form-item label="评论内容" required>
          <el-input
            v-model="editCommentForm.content"
            type="textarea"
            placeholder="请输入评论内容"
            rows="4"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editCommentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateComment" :loading="updateCommentLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Share, ChatDotRound, Plus } from '@element-plus/icons-vue'
import api from '../../services/api'

const router = useRouter()
const route = useRoute()

const post = ref(null)
const comments = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const totalComments = ref(0)

// 检查用户是否是帖子作者
const isPostOwner = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo || !post.value) return false
  const user = JSON.parse(userInfo)
  return user.id === post.value.author?.id
})

// 检查用户是否是评论作者
const isCommentOwner = (comment) => {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo || !comment) return false
  const user = JSON.parse(userInfo)
  return user.id === comment.commenter?.id
}

// 获取帖子状态类型
const getPostStatusType = (status) => {
  const statusTypes = {
    '-1': 'info',
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return statusTypes[status] || 'info'
}

// 获取帖子状态文本
const getPostStatusText = (status) => {
  const statusTexts = {
    '-1': '已删除',
    0: '待审核',
    1: '已发布',
    2: '已驳回'
  }
  return statusTexts[status] || '未知状态'
}

// 编辑帖子相关状态
const editPostDialogVisible = ref(false)
const updatePostLoading = ref(false)
const previewDialogVisible = ref(false)
const previewImage = ref('')

const editPostForm = reactive({
  title: '',
  content: '',
  images: [],
  categoryId: ''
})

// 编辑评论相关状态
const editCommentDialogVisible = ref(false)
const updateCommentLoading = ref(false)

const editCommentForm = reactive({
  content: ''
})

// 分类列表
const categories = ref([])

const commentForm = reactive({
  content: ''
})

const commentRules = {
  content: [{ required: true, message: '请输入评论内容', trigger: 'blur' }]
}

const commentFormRef = ref(null)

const replyDialogVisible = ref(false)
const replyForm = reactive({
  content: ''
})

const replyRules = {
  content: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const replyFormRef = ref(null)
const currentCommentId = ref('')

// 获取分类列表
const getCategories = async () => {
  try {
    // 检查缓存
    const cachedData = localStorage.getItem('categories')
    const cachedExpiry = localStorage.getItem('categories_expiry')
    const now = Date.now()
    
    // 强制刷新缓存，避免使用旧数据
    // if (cachedData && cachedExpiry && now < parseInt(cachedExpiry)) {
    //   categories.value = JSON.parse(cachedData)
    //   return
    // }
    
    // 缓存无效，请求API
    const response = await api.getCategories()
    if (response.code === 0) {
      // 确保分类数据结构正确
      categories.value = response.data.map(category => ({
        id: category.id.toString(), // 确保id是字符串
        name: category.name || category.title || `分类${category.id}` // 确保name存在
      }))
      // 存储到缓存，设置24小时过期
      localStorage.setItem('categories', JSON.stringify(categories.value))
      localStorage.setItem('categories_expiry', (now + 24 * 60 * 60 * 1000).toString())
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

const getPostDetail = async () => {
  try {
    const id = route.params.id
    const response = await api.getPostDetail(id)
    if (response.code === 0) {
      // 处理图片路径，转换为完整路径
      const postData = response.data
      if (postData.images && postData.images.length > 0) {
        postData.images = postData.images.map(url => {
          if (url.startsWith('/')) {
            return `http://localhost:8082${url}`
          }
          return url
        })
      }
      // 修正：将 isLiked 字段映射到 liked
      if (postData.isLiked !== undefined) {
        postData.liked = postData.isLiked
      }
      post.value = postData
      // 获取评论列表
      getComments()
    }
  } catch (error) {
    ElMessage.error('获取帖子详情失败')
  }
}

const getComments = async () => {
  try {
    const id = route.params.id
    const response = await api.getPostComments(id, {
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (response.code === 0) {
      // 修正：确保评论的 isLiked 字段存在
      const commentsData = response.data.records.map(comment => {
        if (comment.isLiked !== undefined) {
          return comment
        }
        return { ...comment, isLiked: false }
      })
      comments.value = commentsData
      totalComments.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取评论列表失败')
  }
}

const goBack = () => {
  router.push('/user/posts')
}

const likePost = async () => {
  try {
    if (!post.value) {
      ElMessage.error('帖子信息未加载，请刷新页面重试')
      return
    }
    const id = route.params.id
    let response
    if (post.value.liked) {
      // 如果已经点赞，则取消点赞
      response = await api.unlikePost(id)
      if (response.code === 0) {
        // 根据返回的数据更新状态
        post.value.liked = response.data.isLiked
        post.value.likeCount = response.data.likeCount
        ElMessage.success('取消点赞成功')
      } else {
        ElMessage.error(response.msg || '操作失败，请重试')
      }
    } else {
      // 如果未点赞，则点赞
      response = await api.likePost(id)
      if (response.code === 0) {
        // 根据返回的数据更新状态
        post.value.liked = response.data.isLiked
        post.value.likeCount = response.data.likeCount
        ElMessage.success('点赞成功')
      } else if (response.code === 400 && response.msg === '已经点过赞了') {
        // 处理重复点赞的情况
        ElMessage.info('已经点过赞了')
        // 确保本地状态正确
        post.value.liked = true
      } else {
        ElMessage.error(response.msg || '操作失败，请重试')
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

const sharePost = () => {
  // 实现分享功能
  ElMessage.info('分享功能开发中')
}

const submitComment = async () => {
  if (!commentFormRef.value) return
  await commentFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const id = route.params.id
        const data = {
          content: commentForm.content
        }
        // 假设API接口为addComment
        const response = await api.addComment(id, data)
        if (response.code === 0) {
          ElMessage.success('评论成功')
          commentForm.content = ''
          getComments()
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error('评论失败，请重试')
      }
    }
  })
}

const replyComment = (commentId) => {
  ElMessage.info('回复功能待开发')
}

const submitReply = async () => {
  ElMessage.info('回复功能待开发')
  replyDialogVisible.value = false
}

const likeComment = async (commentId) => {
  try {
    const comment = comments.value.find(c => c.id === commentId)
    if (!comment) return
    
    let response
    if (comment.isLiked) {
      // 如果已经点赞，则取消点赞
      response = await api.unlikeComment(commentId)
      if (response.code === 0) {
        // 根据返回的数据更新状态
        comment.isLiked = response.data.isLiked
        comment.likeCount = response.data.likeCount
        ElMessage.success('取消点赞成功')
      } else {
        ElMessage.error(response.msg || '操作失败，请重试')
      }
    } else {
      // 如果未点赞，则点赞
      response = await api.likeComment(commentId)
      if (response.code === 0) {
        // 根据返回的数据更新状态
        comment.isLiked = response.data.isLiked
        comment.likeCount = response.data.likeCount
        ElMessage.success('点赞成功')
      } else if (response.code === 400 && response.msg === '已经点过赞了') {
        // 处理重复点赞的情况
        ElMessage.info('已经点过赞了')
        // 确保本地状态正确
        comment.isLiked = true
      } else {
        ElMessage.error(response.msg || '操作失败，请重试')
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getComments()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getComments()
}

onMounted(async () => {
  await getCategories()
  getPostDetail()
})

// 编辑帖子
const editPost = () => {
  if (!post.value) return
  
  // 填充表单数据
  editPostForm.title = post.value.title
  editPostForm.content = post.value.content
  
  // 处理分类ID
  if (post.value.categoryId) {
    // 如果有categoryId，确保是字符串类型
    editPostForm.categoryId = post.value.categoryId.toString()
  } else if (post.value.categoryName && categories.value.length > 0) {
    // 如果没有categoryId但有categoryName，根据名称查找对应的ID
    const category = categories.value.find(cat => cat.name === post.value.categoryName)
    if (category) {
      editPostForm.categoryId = category.id
    }
  } else if (categories.value.length > 0) {
    // 如果都没有，默认选择第一个分类
    editPostForm.categoryId = categories.value[0].id
  }
  
  // 处理图片数据
  if (post.value.images && post.value.images.length > 0) {
    editPostForm.images = post.value.images.map((url, index) => ({
      name: `image_${index}`,
      url: url,
      response: { url: url }
    }))
  } else {
    editPostForm.images = []
  }
  
  editPostDialogVisible.value = true
}

// 处理图片上传
const handleImageUpload = async (file) => {
  try {
    const response = await api.uploadImage(file.file)
    if (response.code === 0) {
      // 构建完整的图片URL用于预览
      const imageUrl = response.data.url.startsWith('/') ? `http://localhost:8082${response.data.url}` : response.data.url
      editPostForm.images.push({
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
  editPostForm.images = fileList
}

// 更新帖子
const updatePost = async () => {
  if (!post.value) return
  
  // 验证表单
  if (!editPostForm.categoryId) {
    ElMessage.error('请选择分类')
    return
  }
  if (!editPostForm.title.trim()) {
    ElMessage.error('请输入帖子标题')
    return
  }
  if (!editPostForm.content.trim()) {
    ElMessage.error('请输入帖子内容')
    return
  }
  
  try {
    updatePostLoading.value = true
    
    // 准备更新帖子的数据
    const postData = {
      title: editPostForm.title,
      content: editPostForm.content,
      images: editPostForm.images.map(img => img.response.url),
      categoryId: editPostForm.categoryId
    }
    
    const response = await api.updatePost(post.value.id, postData)
    if (response.code === 0) {
      ElMessage.success('更新成功')
      editPostDialogVisible.value = false
      // 刷新帖子详情
      getPostDetail()
    } else {
      ElMessage.error('更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    updatePostLoading.value = false
  }
}

// 编辑评论
const editComment = (comment) => {
  currentCommentId.value = comment.id
  // 填充表单数据
  editCommentForm.content = comment.content
  editCommentDialogVisible.value = true
}

// 更新评论
const updateComment = async () => {
  // 验证表单
  if (!editCommentForm.content.trim()) {
    ElMessage.error('请输入评论内容')
    return
  }
  
  try {
    updateCommentLoading.value = true
    
    // 准备更新评论的数据
    const commentData = {
      content: editCommentForm.content
    }
    
    const response = await api.updateComment(currentCommentId.value, commentData)
    if (response.code === 0) {
      ElMessage.success('更新成功')
      editCommentDialogVisible.value = false
      // 刷新评论列表
      getComments()
    } else {
      ElMessage.error('更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    updateCommentLoading.value = false
  }
}
</script>

<style scoped>
.user-post-detail {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-detail {
  padding: 20px 0;
}

.post-title {
  font-size: 24px;
  font-weight: bold;
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
  margin-bottom: 30px;
  line-height: 1.8;
  font-size: 16px;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-images {
  margin-bottom: 30px;
  display: flex;
  flex-wrap: wrap;
}

.post-actions {
  margin-top: 30px;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.comment-section {
  margin-top: 30px;
}

.comment-form {
  margin-bottom: 30px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-card {
  transition: all 0.3s ease;
}

.comment-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-header-right {
  display: flex;
  align-items: center;
}

.comment-author {
  font-weight: bold;
  color: #303133;
}

.comment-date {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  margin-bottom: 10px;
  line-height: 1.6;
}

.comment-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.comment-author-info {
  display: flex;
  align-items: center;
}

.comment-college {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
  margin-right: 10px;
}

.comment-pagination {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}

.empty-comments {
  padding: 40px 0;
  text-align: center;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.post-upload {
  margin-bottom: 20px;
}
</style>