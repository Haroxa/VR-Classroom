<template>
  <div class="user-liked-posts">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我点赞的帖子</span>
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
          <el-table-column prop="author.name" label="作者" width="150">
            <template #default="scope">
              <div class="author-name">{{ scope.row.author?.name || '未知作者' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="viewPost(scope.row.id)">查看</el-button>
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

const postsData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

const getLikedPosts = async () => {
  if (!isLoggedIn.value) {
    return
  }
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    // 假设API接口为getLikedPosts
    const response = await api.getLikedPosts(params)
    if (response.code === 0) {
      postsData.value = response.data.records
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取点赞帖子列表失败')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getLikedPosts()
}

const handleCurrentChange = (current) => {
  page.value = current
  getLikedPosts()
}

const viewPost = (postId) => {
  router.push(`/user/post/${postId}`)
}

onMounted(() => {
  getLikedPosts()
})
</script>

<style scoped>
.user-liked-posts {
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

.author-name {
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