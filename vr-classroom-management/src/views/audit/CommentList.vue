<template>
  <div class="comment-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论审核</span>
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
          <el-form-item label="关键词">
            <el-input v-model="filterForm.keyword" placeholder="评论内容" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getComments" :loading="loading">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      <div v-else class="table-container">
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
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <template v-if="scope.row.status === 2">
                <el-tag type="danger" :title="scope.row.rejectReason || '无驳回理由'">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
              <template v-else>
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="作者" width="120">
            <template #default="scope">
              {{ scope.row.commenter?.name || '未知作者' }}
            </template>
          </el-table-column>
          <el-table-column label="所属帖子" width="200">
            <template #default="scope">
              <div class="post-title">{{ scope.row.relatedPost?.title || '未知帖子' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="success" size="small" @click="approveComment(scope.row)" :disabled="scope.row.status !== 0">通过</el-button>
              <el-button type="danger" size="small" @click="rejectComment(scope.row)" :disabled="scope.row.status !== 0">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="commentsData.length === 0" class="empty-container">
          <el-empty description="暂无评论" />
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

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="auditDialogVisible"
      :title="auditForm.status === 1 ? '通过评论' : '驳回评论'"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../services/api'

const commentsData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const loading = ref(false)

const filterForm = reactive({
  status: '',
  keyword: ''
})

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
const currentCommentId = ref('')

// 常见的驳回理由
const commonReasons = [
  { label: '内容与主题无关', value: '内容与主题无关' },
  { label: '包含违规信息', value: '包含违规信息' },
  { label: '内容重复', value: '内容重复' },
  { label: '质量过低', value: '质量过低' },
  { label: '其他原因', value: '其他原因' }
]

const getComments = async () => {
  try {
    loading.value = true
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      status: filterForm.status,
      keyword: filterForm.keyword
    }
    const response = await api.getAdminComments(params)
    if (response.code === 0) {
      // 查看后端返回的数据结构
      console.log('评论数据:', response.data.records)
      // 处理评论数据，确保作者和帖子信息正确显示
      const records = (response.data.records || []).map(comment => {
        // 确保评论者信息存在
        if (!comment.commenter && comment.userId) {
          // 如果没有评论者信息，但有userId，可以尝试根据userId获取评论者信息
          // 这里暂时设置一个默认值
          comment.commenter = {
            name: `用户${comment.userId}`
          }
        }
        // 确保帖子信息存在
        if (!comment.relatedPost && comment.postId) {
          // 如果没有帖子信息，但有postId，可以尝试根据postId获取帖子信息
          // 这里暂时设置一个默认值
          comment.relatedPost = {
            title: `帖子${comment.postId}`
          }
        }
        return comment
      })
      commentsData.value = records
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取评论列表失败')
    // 确保数据初始化，避免组件更新错误
    commentsData.value = []
    total.value = 0
  } finally {
    loading.value = false
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

const resetFilter = () => {
  filterForm.status = ''
  filterForm.keyword = ''
  getComments()
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

// 通过评论
const approveComment = (comment) => {
  currentCommentId.value = comment.id
  auditForm.status = 1
  auditForm.rejectReason = '审核通过'
  auditForm.selectedReason = ''
  auditDialogVisible.value = true
}

// 驳回评论
const rejectComment = (comment) => {
  currentCommentId.value = comment.id
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
        const response = await api.auditComment(currentCommentId.value, data)
        if (response.code === 0) {
          ElMessage.success('审核成功')
          auditDialogVisible.value = false
          getComments()
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error('审核失败，请重试')
      }
    }
  })
}

onMounted(() => {
  getComments()
})
</script>

<style scoped>
.comment-list {
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

.loading-container {
  margin: 20px 0;
}

.empty-container {
  margin: 40px 0;
  text-align: center;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>