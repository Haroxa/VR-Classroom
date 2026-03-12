<template>
  <div class="user-seat-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>浏览座位</span>
        </div>
      </template>
      
      <!-- 校区、楼栋、教室选择 -->
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm" class="demo-form-inline">
          <el-form-item label="校区">
            <el-select v-model="filterForm.campusId" placeholder="请选择校区" style="width: 150px;" @change="handleCampusChange">
              <el-option
                v-for="campus in campuses"
                :key="campus.id"
                :label="campus.name"
                :value="campus.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="楼栋">
            <el-select v-model="filterForm.buildingId" placeholder="请选择楼栋" style="width: 150px;" @change="handleBuildingChange" :disabled="!filterForm.campusId">
              <el-option
                v-for="building in buildings"
                :key="building.id"
                :label="building.name"
                :value="building.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="教室">
            <el-select v-model="filterForm.roomId" placeholder="请选择教室" style="width: 150px;" @change="handleRoomChange" :disabled="!filterForm.buildingId">
              <el-option
                v-for="room in rooms"
                :key="room.id"
                :label="room.roomNumber"
                :value="room.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 教室信息 -->
      <div v-if="roomInfo" class="room-info">
        <el-card shadow="hover" class="room-info-card">
          <template #header>
            <div class="room-info-header">
              <span>教室信息</span>
            </div>
          </template>
          <div class="room-info-content">
            <p>教室编号：{{ roomInfo.roomNumber }}</p>
            <p>座位数量：{{ roomInfo.totalRows }}行 × {{ roomInfo.totalCols }}列</p>
          </div>
        </el-card>
      </div>
      
      <!-- 座位图 -->
      <div v-if="seatsData" class="seat-map">
        <el-card shadow="hover">
          <template #header>
            <div class="seat-map-header">
              <span>座位图</span>
              <div class="seat-legend">
                <div class="legend-item">
                  <div class="seat seat-passage">
                    <span class="seat-status">过</span>
                  </div>
                  <span class="legend-text">过道</span>
                </div>
                <div class="legend-item">
                  <div class="seat seat-free">
                    <span class="seat-status">空</span>
                  </div>
                  <span class="legend-text">空闲</span>
                </div>
                <div class="legend-item">
                  <div class="seat seat-locked">
                    <span class="seat-status">锁</span>
                  </div>
                  <span class="legend-text">已锁定</span>
                </div>
                <div class="legend-item">
                  <div class="seat seat-sold">
                    <span class="seat-status">售</span>
                  </div>
                  <span class="legend-text">已售出</span>
                </div>
              </div>
            </div>
          </template>
          <div class="seat-map-wrapper">
            <div class="seat-grid" :style="{ transform: `scale(${zoomLevel})`, transformOrigin: 'top left', display: 'inline-block' }">
              <!-- 列坐标 -->
              <div class="col-header">
                <div class="header-cell"></div>
                <div v-for="col in seatsData.totalCols" :key="`col-${col}`" class="col-cell">{{ col }}</div>
              </div>
              <!-- 座位网格 -->
              <div v-for="row in seatsData.totalRows" :key="`row-${row}`" class="row-container">
                <!-- 行坐标 -->
                <div class="row-header">{{ row }}</div>
                <!-- 座位 -->
                <div class="seat-row">
                  <div
                    v-for="seat in seatsData.seats.filter(s => s.row === row)"
                    :key="seat.id"
                    :class="['seat', getSeatClass(seat.status), { 'seat-selected': isSeatSelected(seat) }]"
                    :title="getSeatTitle(seat)"
                    @click="handleSeatClick(seat)"
                  >
                    <span class="seat-status">{{ getSeatStatusShortText(seat.status) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 缩放控制 -->
          <div class="zoom-controls">
            <el-button size="small" @click="zoomIn" :disabled="zoomLevel >= 2">
              <el-icon><ZoomIn /></el-icon>
            </el-button>
            <el-button size="small" @click="zoomOut" :disabled="zoomLevel <= 0.5">
              <el-icon><ZoomOut /></el-icon>
            </el-button>
            <el-button size="small" @click="resetZoom">
              重置
            </el-button>
          </div>
          
          <!-- 订单创建 -->
          <div v-if="seatsData" class="order-creation">
            <el-card shadow="hover" class="order-card">
              <template #header>
                <div class="order-header">
                  <span>订单信息</span>
                </div>
              </template>
              <div class="order-content">
                <div class="order-info">
                  <p>已选择座位：{{ selectedSeats.length }}/6</p>
                  <p>总价格：¥{{ (totalPrice / 100).toFixed(2) }}</p>
                </div>
                <div class="order-actions">
                  <el-button @click="clearSelection" :disabled="selectedSeats.length === 0">
                    清空选择
                  </el-button>
                  <el-button type="primary" @click="createOrder" :disabled="selectedSeats.length === 0" :loading="createOrderLoading">
                    创建订单
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </el-card>
      </div>
      
      <!-- 无数据提示 -->
      <div v-else-if="!loading" class="empty-container">
        <el-empty description="请选择教室查看座位图" />
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ZoomIn, ZoomOut } from '@element-plus/icons-vue'
import api from '../../services/api'

const loading = ref(false)
const campuses = ref([])
const buildings = ref([])
const rooms = ref([])
const roomInfo = ref(null)
const seatsData = ref(null)
const zoomLevel = ref(1)
const selectedSeats = ref([])
const totalPrice = ref(0)
const createOrderLoading = ref(false)

const filterForm = reactive({
  campusId: '',
  buildingId: '',
  roomId: ''
})

// 获取校区列表
const getCampuses = async () => {
  try {
    // 检查缓存
    const cachedData = localStorage.getItem('campuses')
    const cachedExpiry = localStorage.getItem('campuses_expiry')
    const now = Date.now()
    
    if (cachedData && cachedExpiry && now < parseInt(cachedExpiry)) {
      campuses.value = JSON.parse(cachedData)
      return
    }
    
    // 缓存无效，请求API
    const response = await api.getCampuses()
    if (response.code === 0) {
      campuses.value = response.data
      // 存储到缓存，设置24小时过期
      localStorage.setItem('campuses', JSON.stringify(campuses.value))
      localStorage.setItem('campuses_expiry', (now + 24 * 60 * 60 * 1000).toString())
    }
  } catch (error) {
    ElMessage.error('获取校区列表失败')
  }
}

// 获取楼栋列表
const getBuildings = async (campusId) => {
  try {
    if (campusId) {
      // 获取指定校区下的楼栋
      const response = await api.getCampusBuildings(campusId)
      if (response.code === 0) {
        buildings.value = response.data
      }
    } else {
      // 获取所有楼栋
      const response = await api.getBuildings()
      if (response.code === 0) {
        buildings.value = response.data
      }
    }
  } catch (error) {
    ElMessage.error('获取楼栋列表失败')
  }
}

// 获取教室列表
const getRooms = async (buildingId) => {
  try {
    if (buildingId) {
      // 获取指定楼栋下的教室
      const response = await api.getBuildingRooms(buildingId)
      if (response.code === 0) {
        rooms.value = response.data
      }
    } else {
      // 获取所有教室
      const response = await api.getRooms()
      if (response.code === 0) {
        rooms.value = response.data
      }
    }
  } catch (error) {
    ElMessage.error('获取教室列表失败')
  }
}

// 获取教室详情
const getRoomDetail = async (roomId) => {
  try {
    const response = await api.getRoomDetail(roomId)
    if (response.code === 0) {
      roomInfo.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取教室详情失败')
  }
}

// 获取座位图
const getSeats = async (roomId) => {
  try {
    loading.value = true
    const response = await api.getRoomSeats(roomId)
    if (response.code === 0) {
      seatsData.value = response.data
    }
  } catch (error) {
    ElMessage.error('获取座位图失败')
  } finally {
    loading.value = false
  }
}

// 处理校区选择变化
const handleCampusChange = (campusId) => {
  filterForm.buildingId = ''
  filterForm.roomId = ''
  rooms.value = []
  roomInfo.value = null
  seatsData.value = null
  getBuildings(campusId)
}

// 处理楼栋选择变化
const handleBuildingChange = (buildingId) => {
  filterForm.roomId = ''
  rooms.value = []
  roomInfo.value = null
  seatsData.value = null
  getRooms(buildingId)
}

// 处理教室选择变化
const handleRoomChange = (roomId) => {
  if (roomId) {
    getRoomDetail(roomId)
    getSeats(roomId)
  } else {
    roomInfo.value = null
    seatsData.value = null
  }
}

// 获取座位状态类名
const getSeatClass = (status) => {
  const statusClasses = {
    0: 'seat-passage',
    1: 'seat-free',
    2: 'seat-locked',
    3: 'seat-sold'
  }
  return statusClasses[status] || 'seat-passage'
}

// 获取座位状态文本
const getSeatStatusText = (status) => {
  const statusTexts = {
    0: '过道',
    1: '空闲',
    2: '已锁定',
    3: '已售出'
  }
  return statusTexts[status] || '未知状态'
}

// 获取座位状态简短文本
const getSeatStatusShortText = (status) => {
  const statusTexts = {
    0: '过',
    1: '空',
    2: '锁',
    3: '售'
  }
  return statusTexts[status] || ''
}

// 获取座位title信息
const getSeatTitle = (seat) => {
  let title = `座位: ${seat.row}-${seat.col}\n状态: ${getSeatStatusText(seat.status)}`
  // 对于过道和已锁定的座位不显示价格
  if (seat.status !== 0 && seat.status !== 2) {
    title += `\n价格: ¥${(seat.price / 100).toFixed(2)}`
  }
  return title
}

// 缩放控制方法
const zoomIn = () => {
  if (zoomLevel.value < 2) {
    zoomLevel.value += 0.1
  }
}

const zoomOut = () => {
  if (zoomLevel.value > 0.5) {
    zoomLevel.value -= 0.1
  }
}

// 重置缩放
const resetZoom = () => {
  zoomLevel.value = 1
}

// 处理座位点击
const handleSeatClick = (seat) => {
  // 只有空闲座位可以选择
  if (seat.status !== 1) return
  
  // 检查是否已经选择了6个座位
  if (selectedSeats.value.length >= 6 && !isSeatSelected(seat)) {
    ElMessage.warning('最多只能选择6个座位')
    return
  }
  
  // 切换座位选择状态
  const index = selectedSeats.value.findIndex(s => s.id === seat.id)
  if (index > -1) {
    // 取消选择
    selectedSeats.value.splice(index, 1)
  } else {
    // 添加选择
    selectedSeats.value.push({
      id: seat.id,
      price: seat.price,
      version: seat.version
    })
  }
  
  // 更新总价格
  updateTotalPrice()
}

// 检查座位是否已选择
const isSeatSelected = (seat) => {
  return selectedSeats.value.some(s => s.id === seat.id)
}

// 更新总价格
const updateTotalPrice = () => {
  totalPrice.value = selectedSeats.value.reduce((sum, seat) => sum + seat.price, 0)
}

// 创建订单
const createOrder = async () => {
  if (selectedSeats.value.length === 0) {
    ElMessage.warning('请选择至少一个座位')
    return
  }
  
  if (!filterForm.campusId || !filterForm.buildingId || !filterForm.roomId) {
    ElMessage.warning('请选择完整的校区、楼栋和教室信息')
    return
  }
  
  try {
    createOrderLoading.value = true
    
    const orderData = {
      campusId: filterForm.campusId,
      buildingId: filterForm.buildingId,
      roomId: filterForm.roomId,
      seatList: selectedSeats.value
    }
    
    const response = await api.createOrder(orderData)
    if (response.code === 0) {
      ElMessage.success('订单创建成功')
      // 清空选择
      selectedSeats.value = []
      totalPrice.value = 0
      // 刷新座位状态
      if (filterForm.roomId) {
        getSeats(filterForm.roomId)
      }
    } else {
      ElMessage.error(response.msg || '订单创建失败')
    }
  } catch (error) {
    ElMessage.error('订单创建失败，请重试')
  } finally {
    createOrderLoading.value = false
  }
}

// 清空选择
const clearSelection = () => {
  selectedSeats.value = []
  totalPrice.value = 0
}

onMounted(() => {
  getCampuses()
})
</script>

<style scoped>
.user-seat-list {
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

.room-info {
  margin-bottom: 20px;
}

.room-info-card {
  margin-bottom: 20px;
}

.room-info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-info-content {
  font-size: 14px;
  line-height: 1.6;
}

.seat-map {
  margin-top: 20px;
}

.seat-map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.seat-legend {
  display: flex;
  gap: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  margin-right: 20px;
}

.legend-text {
  color: #606266;
  white-space: nowrap;
}

.seat-map-wrapper {
  margin: 20px 0;
  overflow: auto;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.seat-grid {
  display: inline-block;
}

.col-header {
  display: flex;
  margin-bottom: 5px;
}

.header-cell {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 5px;
}

.col-cell {
  width: 40px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 5px;
  border: 1px solid #e4e7ed;
  background-color: #ffffff;
}

.row-container {
  display: flex;
  margin-bottom: 5px;
}

.row-header {
  width: 30px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 5px;
  border: 1px solid #e4e7ed;
  background-color: #ffffff;
}

.seat-row {
  display: flex;
}

.seat {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-right: 5px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.seat:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 10;
}

.seat-passage {
  background-color: #f0f0f0;
  cursor: default;
}

.seat-passage:hover {
  transform: none;
  box-shadow: none;
}

.seat-free {
  background-color: #67c23a;
  color: white;
}

.seat-locked {
  background-color: #e6a23c;
  color: white;
}

.seat-sold {
  background-color: #f56c6c;
  color: white;
}

.seat-selected {
  border: 3px solid #409eff !important;
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
  z-index: 20;
}

.seat-status {
  display: block;
  text-align: center;
}

.zoom-controls {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
}

.order-creation {
  margin-top: 20px;
}

.order-card {
  margin-top: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  font-size: 14px;
  line-height: 1.6;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.loading-container {
  margin: 20px 0;
}

.empty-container {
  margin: 40px 0;
  text-align: center;
}
</style>