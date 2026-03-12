<template>
  <div class="user-liked-comments">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我点赞的评论</span>
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
          <el-table-column prop="relatedPost.title" label="所属帖子" width="200">
            <template #default="scope">
              <div class="post-title">{{ scope.row.relatedPost?.title || '未知帖子' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="viewPost(scope.row.relatedPost?.id)" :disabled="!scope.row.relatedPost">查看帖子</el-button>
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
          small
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

const commentsData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

const getLikedComments = async () => {
  if (!isLoggedIn.value) {
    return
  }
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    // 假设API接口为getLikedComments
    const response = await api.getLikedComments(params)
    if (response.code === 0) {
      commentsData.value = response.data.records
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取点赞评论列表失败')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getLikedComments()
}

const handleCurrentChange = (current) => {
  page.value = current
  getLikedComments()
}

const viewPost = (postId) => {
  if (postId) {
    router.push(`/user/post/${postId}`)
  }
}

onMounted(() => {
  getLikedComments()
})
</script>

<style scoped>
.user-liked-comments {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding-right: 10px;
}
</style>