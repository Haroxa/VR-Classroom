<template>
  <div class="user-order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
        </div>
      </template>
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="全部" style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="待支付" value="PENDING" />
              <el-option label="已支付" value="PAID" />
              <el-option label="已取消" value="CANCELLED" />
              <el-option label="已退款" value="REFUNDED" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="filterForm.keyword" placeholder="订单编号" />
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
          <el-table-column label="订单编号" width="200">
            <template #default="scope">
              {{ scope.row.id.toString() }}
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="100">
            <template #default="scope">
              ¥{{ (scope.row.amount / 100).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="scope">
              <div class="operation-buttons">
                <el-button size="small" @click="viewOrder(scope.row.id)" class="operation-button">查看</el-button>
                <el-button type="danger" size="small" @click="cancelOrder(scope.row.id)" :disabled="scope.row.status !== 'PENDING'" class="operation-button">取消</el-button>
                <el-button type="primary" size="small" @click="mockPay(scope.row.id)" :disabled="scope.row.status !== 'PENDING'" class="operation-button">模拟支付</el-button>
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
          small
        />
      </div>
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="orderDialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单编号">{{ currentOrder.id.toString() }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ (currentOrder.amount / 100).toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="过期时间">{{ currentOrder.expiresAt }}</el-descriptions-item>
          <el-descriptions-item label="座位信息">
            <div v-for="(seat, index) in currentOrder.seatList" :key="index" class="order-item">
              座位：第{{ seat.row }}行第{{ seat.col }}列 - 价格：¥{{ (parseInt(seat.lookPrice) / 100).toFixed(2) }}
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../services/api'

const ordersData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

// 检查用户是否已登录
const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

const filterForm = reactive({
  status: '',
  keyword: ''
})

const orderDialogVisible = ref(false)
const currentOrder = ref(null)

const getOrders = async () => {
  if (!isLoggedIn.value) {
    return
  }
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      status: filterForm.status,
      keyword: filterForm.keyword
    }
    // 假设API接口为getUserOrders
    const response = await api.getUserOrders(params)
    if (response.code === 0) {
      // 将订单ID转换为字符串，解决数字精度问题
      ordersData.value = response.data.records.map(order => {
        // 深拷贝订单对象
        const processedOrder = JSON.parse(JSON.stringify(order));
        // 转换订单ID为字符串
        processedOrder.id = processedOrder.id.toString();
        return processedOrder;
      });
      // 后端返回的total是总页数，直接使用
      total.value = response.data.total;
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
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
    case 'PENDING': return 'warning'
    case 'PAID': return 'success'
    case 'CANCELLED': return 'danger'
    case 'REFUNDED': return 'info'
    default: return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'PENDING': return '待支付'
    case 'PAID': return '已支付'
    case 'CANCELLED': return '已取消'
    case 'REFUNDED': return '已退款'
    default: return ''
  }
}

const viewOrder = async (id) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
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

const cancelOrder = async (id) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    // 假设API接口为cancelOrder
    const response = await api.cancelOrder(id)
    if (response.code === 0) {
      ElMessage.success('取消成功')
      getOrders()
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    ElMessage.error('取消失败，请重试')
  }
}

const mockPay = async (id) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    // 调用模拟支付API
    const response = await api.mockPay(id)
    if (response.code === 0) {
      ElMessage.success('支付成功')
      getOrders()
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    ElMessage.error('支付失败，请重试')
  }
}

onMounted(() => {
  getOrders()
})
</script>

<style scoped>
.user-order-list {
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