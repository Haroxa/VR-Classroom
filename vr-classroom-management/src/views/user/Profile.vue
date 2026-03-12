<template>
  <div class="user-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <div class="profile-content">
        <div class="profile-avatar">
          <el-avatar :size="100" :src="userInfo.avatar" />
        </div>
        <div class="profile-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
            <el-descriptions-item label="学院">{{ userInfo.collegeName }}</el-descriptions-item>
            <el-descriptions-item label="审核状态">{{ userInfo.verifyStatus === 2 ? '已认证' : '未认证' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const userInfo = ref({
  name: '',
  phone: '',
  avatar: '',
  collegeName: '',
  verifyStatus: 0
})

const loadUserInfo = () => {
  try {
    const userData = localStorage.getItem('userInfo')
    if (userData) {
      userInfo.value = JSON.parse(userData)
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.user-profile {
  min-height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.profile-avatar {
  margin-bottom: 20px;
}

.profile-info {
  width: 100%;
  max-width: 600px;
}
</style>