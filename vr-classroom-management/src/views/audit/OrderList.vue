<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单审核</span>
        </div>
      </template>
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部" style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="0" />
              <el-option label="已通过" value="1" />
              <el-option label="已驳回" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="filterForm.keyword" placeholder="订单编号或用户名称" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getOrders">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="table-container">
        <el-table :data="ordersData" style="width: 100%" border>
          <el-table-column label="ID" width="100">
            <template #default="scope">
              {{ (page - 1) * pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="订单编号" width="200" />
          <el-table-column label="用户" width="120">
            <template #default="scope">
              {{ scope.row.user?.name || '未知用户' }}
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="100" />
          <el-table-column prop="date" label="创建时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="auditOrder(scope.row)" :disabled="scope.row.status !== 0">审核</el-button>
              <el-button size="small" @click="viewOrder(scope.row.id)">查看</el-button>
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

    <!-- 审核弹窗 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="审核订单"
      width="500px"
    >
      <el-form :model="auditForm" :rules="auditRules" ref="auditFormRef">
        <el-form-item label="审核状态" prop="status">
          <el-select v-model="auditForm.status" placeholder="请选择审核状态">
            <el-option label="通过" value="1" />
            <el-option label="驳回" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="auditForm.status === 2" label="驳回理由" prop="rejectReason">
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
          <el-button type="primary" @click="submitAudit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="orderDialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentOrder.user.name }}</el-descriptions-item>
          <el-descriptions-item label="金额">{{ currentOrder.amount }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.date }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单内容">
            <div v-for="(item, index) in currentOrder.items" :key="index" class="order-item">
              {{ item.name }} - {{ item.quantity }} x {{ item.price }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../services/api'

const ordersData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

const filterForm = reactive({
  status: '',
  keyword: ''
})

const auditDialogVisible = ref(false)
const auditForm = reactive({
  status: 1,
  rejectReason: ''
})
const auditRules = {
  status: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
  rejectReason: [{ required: true, message: '请输入驳回理由', trigger: 'blur' }]
}
const auditFormRef = ref(null)
const currentOrderId = ref('')

const orderDialogVisible = ref(false)
const currentOrder = ref(null)

const getOrders = async () => {
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      status: filterForm.status,
      keyword: filterForm.keyword
    }
    // 假设API接口为getAdminOrders
    const response = await api.getAdminOrders(params)
    if (response.code === 0) {
      ordersData.value = response.data.records || []
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
    // 确保数据初始化，避免组件更新错误
    ordersData.value = []
    total.value = 0
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getOrders()
}

const handleCurrentChange = (current) => {
  page.value = current
  getOrders()
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.keyword = ''
  getOrders()
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    default: return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '已驳回'
    default: return ''
  }
}

const auditOrder = (order) => {
  currentOrderId.value = order.id
  auditForm.status = 1
  auditForm.rejectReason = ''
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
        // 假设API接口为auditOrder
        const response = await api.auditOrder(currentOrderId.value, data)
        if (response.code === 0) {
          ElMessage.success('审核成功')
          auditDialogVisible.value = false
          getOrders()
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error('审核失败，请重试')
      }
    }
  })
}

const viewOrder = async (id) => {
  try {
    // 假设API接口为getOrderDetail
    const response = await api.getOrderDetail(id)
    if (response.code === 0) {
      currentOrder.value = response.data
      orderDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

onMounted(() => {
  getOrders()
})
</script>

<style scoped>
.order-list {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding-right: 10px;
}

.order-detail {
  padding: 20px 0;
}

.order-item {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>