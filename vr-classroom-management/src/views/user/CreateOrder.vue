<template>
  <div class="user-create-order">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>创建订单</span>
        </div>
      </template>
      <el-form :model="orderForm" :rules="orderRules" ref="orderFormRef">
        <el-form-item label="订单名称" prop="name">
          <el-input v-model="orderForm.name" placeholder="请输入订单名称" />
        </el-form-item>
        <el-form-item label="订单内容" prop="items">
          <div v-for="(item, index) in orderForm.items" :key="index" class="order-item">
            <el-input v-model="item.name" placeholder="商品名称" style="width: 200px; margin-right: 10px" />
            <el-input v-model.number="item.quantity" type="number" placeholder="数量" style="width: 100px; margin-right: 10px" />
            <el-input v-model.number="item.price" type="number" placeholder="单价" style="width: 100px; margin-right: 10px" />
            <el-button type="danger" @click="removeItem(index)">删除</el-button>
          </div>
          <el-button type="primary" @click="addItem">添加商品</el-button>
        </el-form-item>
        <el-form-item label="总金额" prop="amount">
          <el-input v-model.number="orderForm.amount" type="number" placeholder="总金额" readonly />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="orderForm.remark"
            type="textarea"
            placeholder="请输入备注信息"
            rows="3"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitOrder">提交订单</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../../services/api'

const router = useRouter()

const orderForm = reactive({
  name: '',
  items: [{
    name: '',
    quantity: 1,
    price: 0
  }],
  amount: 0,
  remark: ''
})

const orderRules = {
  name: [{ required: true, message: '请输入订单名称', trigger: 'blur' }],
  items: [{ required: true, message: '请添加订单商品', trigger: 'change' }],
  amount: [{ required: true, message: '请输入总金额', trigger: 'blur' }]
}

const orderFormRef = ref(null)

// 计算总金额
const calculateTotal = () => {
  let total = 0
  orderForm.items.forEach(item => {
    if (item.quantity && item.price) {
      total += item.quantity * item.price
    }
  })
  orderForm.amount = total
}

// 添加商品
const addItem = () => {
  orderForm.items.push({
    name: '',
    quantity: 1,
    price: 0
  })
}

// 删除商品
const removeItem = (index) => {
  orderForm.items.splice(index, 1)
  calculateTotal()
}

// 重置表单
const resetForm = () => {
  orderForm.name = ''
  orderForm.items = [{
    name: '',
    quantity: 1,
    price: 0
  }]
  orderForm.amount = 0
  orderForm.remark = ''
}

// 提交订单
const submitOrder = async () => {
  if (!orderFormRef.value) return
  await orderFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 计算总金额
        calculateTotal()
        
        const data = {
          name: orderForm.name,
          items: orderForm.items,
          amount: orderForm.amount,
          remark: orderForm.remark
        }
        
        // 假设API接口为createOrder
        const response = await api.createOrder(data)
        if (response.code === 0) {
          ElMessage.success('订单创建成功')
          router.push('/user/orders')
        } else {
          ElMessage.error(response.msg)
        }
      } catch (error) {
        ElMessage.error('订单创建失败，请重试')
      }
    }
  })
}

// 监听商品变化，自动计算总金额
const watchItems = () => {
  orderForm.items.forEach(item => {
    watch(() => item.quantity, calculateTotal)
    watch(() => item.price, calculateTotal)
  })
}

onMounted(() => {
  watchItems()
})
</script>

<style scoped>
.user-create-order {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.order-item:last-child {
  margin-bottom: 20px;
}
</style>