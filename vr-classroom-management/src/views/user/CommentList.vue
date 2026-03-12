<template>
  <div class="user-comment-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的评论</span>
        </div>
      </template>

      <div class="table-container">
        <el-table :data="commentsData" style="width: 100%" border>
            <el-table-column label="ID" width="100">
              <template #default="scope">
                {{ (page - 1) * pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="content" label="评论内容" min-width="400">
              <template #default="scope">
                <div class="comment-content">{{ scope.row.content }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="date" label="发布时间" width="180" />
            <el-table-column prop="likeCount" label="点赞数" width="100" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <template v-if="scope.row.status === 2">
                  <el-tag type="danger" :title="scope.row.rejectReason || '无驳回理由'">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
                <template v-else>
                  <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                </template>
              </template>
            </el-table-column>
            <el-table-column prop="relatedPost.title" label="所属帖子" width="200">
              <template #default="scope">
                <div class="post-title">{{ scope.row.relatedPost?.title || '未知帖子' }}</div>
              </template>
            </el-table-column>
            <el-table-column label="帖子状态" width="120">
              <template #default="scope">
                <el-tag :type="getPostStatusType(scope.row.relatedPost?.status)">{{ getPostStatusText(scope.row.relatedPost?.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <div class="operation-buttons">
                  <el-button size="small" @click="viewPost(scope.row.relatedPost?.id)" class="operation-button" :disabled="!scope.row.relatedPost?.id">查看帖子</el-button>
                  <el-button type="danger" size="small" @click="deleteComment(scope.row.id, scope.row.status)" class="operation-button" :disabled="scope.row.status === -1">删除评论</el-button>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../../services/api'

const router = useRouter()

const commentsData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})



// 获取评论状态类型
const getStatusType = (status) => {
  const statusTypes = {
    '-1': 'info',
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return statusTypes[status] || 'info'
}

// 获取评论状态文本
const getStatusText = (status) => {
  const statusTexts = {
    '-1': '已删除',
    0: '待审核',
    1: '已发布',
    2: '已驳回'
  }
  return statusTexts[status] || '未知状态'
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

const getComments = async () => {
  if (!isLoggedIn.value) {
    return
  }
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    // 假设API接口为getUserComments
    const response = await api.getUserComments(params)
    if (response.code === 0) {
      commentsData.value = response.data.records
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取评论列表失败')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getComments()
}

const handleCurrentChange = (current) => {
  page.value = current
  getComments()
}

const viewPost = (postId) => {
  if (postId) {
    router.push(`/user/post/${postId}`)
  } else {
    ElMessage.warning('帖子不存在')
  }
}

const deleteComment = async (id, status) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  if (status === -1) {
    ElMessage.warning('该评论已删除')
    return
  }
  try {
    // 假设API接口为deleteComment
    const response = await api.deleteComment(id)
    if (response.code === 0) {
      ElMessage.success('删除成功')
      getComments()
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    ElMessage.error('删除失败，请重试')
  }
}

onMounted(() => {
  getComments()
})
</script>

<style scoped>
.user-comment-list {
  height: 100%;
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

.comment-content {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.post-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding-right: 10px;
}
</style>