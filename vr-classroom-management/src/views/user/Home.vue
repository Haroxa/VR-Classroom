<template>
  <div class="user-home-container">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-left">
        <h2>用户中心</h2>
      </div>
      <div class="nav-right">
        <div v-if="isLoggedIn" class="user-info">
          <div class="user-main">
            <div class="user-avatar">
              <img :src="userAvatar" alt="头像">
            </div>
            <div class="user-name">{{ userName }}</div>
          </div>
          <div class="user-actions">
            <el-button link @click="showUserSwitchDialog" class="switch-btn">切换用户</el-button>
            <el-button link @click="logout" class="logout-btn">退出登录</el-button>
          </div>
        </div>
        <div v-else class="login-form">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" inline>
            <el-form-item prop="phone">
              <el-select v-model="loginForm.phone" placeholder="请选择或输入手机号" clearable style="width: 200px;">
                <el-option
                  v-for="user in userList"
                  :key="user.phone"
                  :label="`${user.phone} - ${user.name}`"
                  :value="user.phone"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="login" :loading="loginLoading">登录</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    
    <el-container class="content-container">
      <el-aside width="200px" class="aside">
        <!-- <div class="logo">菜单</div> -->
        
        <!-- 菜单区域 -->
        <div class="menu-container">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            @select="handleMenuSelect"
          >
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="posts">
              <el-icon><Message /></el-icon>
              <span>浏览帖子</span>
            </el-menu-item>
            <el-menu-item index="seats">
              <el-icon><View /></el-icon>
              <span>浏览座位</span>
            </el-menu-item>
            <el-menu-item index="my-posts">
              <el-icon><Document /></el-icon>
              <span>我的帖子</span>
            </el-menu-item>
            <el-menu-item index="my-comments">
              <el-icon><ChatDotRound /></el-icon>
              <span>我的评论</span>
            </el-menu-item>
            <el-menu-item index="liked-posts">
              <el-icon><Star /></el-icon>
              <span>我点赞的帖子</span>
            </el-menu-item>
            <el-menu-item index="liked-comments">
              <el-icon><Star /></el-icon>
              <span>我点赞的评论</span>
            </el-menu-item>
            <el-menu-item index="orders">
              <el-icon><ShoppingCart /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
          </el-menu>
        </div>
        
        <!-- 底部按钮 -->
        <div class="bottom-buttons">
          <div class="switch-btn">
            <el-button type="primary" size="small" @click="switchToAudit">
              <el-icon><View /></el-icon>
              <span>审核页面</span>
            </el-button>
          </div>
          <div class="home-btn">
            <el-button size="small" @click="goToHome">
              <el-icon><House /></el-icon>
              <span>返回主页</span>
            </el-button>
          </div>
        </div>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>

    <!-- 用户切换对话框 -->
    <el-dialog
      v-model="userSwitchDialogVisible"
      title="切换用户"
      width="400px"
    >
      <el-form :model="switchUserForm" ref="switchUserFormRef">
        <el-form-item label="选择用户">
          <el-select v-model="switchUserForm.phone" placeholder="请选择用户" style="width: 100%">
            <el-option
              v-for="user in userList"
              :key="user.phone"
              :label="`${user.phone} - ${user.name}`"
              :value="user.phone"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userSwitchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="switchUserConfirm" :loading="switchUserLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Message, ChatDotRound, ShoppingCart, Plus, View, House, User, Document, Star, Link } from '@element-plus/icons-vue'
import api from '../../services/api'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  const path = route.path.split('/').pop() || 'profile'
  // 处理特殊路由映射
  if (path === 'comments') return 'my-comments'
  return path
})

const handleMenuSelect = (key) => {
  // 处理特殊路由
  if (key === 'profile') {
    router.push('/user/profile')
  } else if (key === 'posts') {
    router.push('/user/posts')
  } else if (key === 'seats') {
    router.push('/user/seats')
  } else if (key === 'my-posts') {
    router.push('/user/my-posts')
  } else if (key === 'my-comments') {
    router.push('/user/comments')
  } else if (key === 'liked-posts') {
    router.push('/user/liked-posts')
  } else if (key === 'liked-comments') {
    router.push('/user/liked-comments')
  } else {
    router.push(`/user/${key}`)
  }
}

const userAvatar = computed(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  return userInfo?.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar&image_size=square'
})

// 使用 ref 而不是 computed 来跟踪登录状态，确保状态能够及时更新
const isLoggedIn = ref(localStorage.getItem('token') !== null)

const userName = computed(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  return userInfo?.name || '用户'
})

// 更新登录状态的函数
const updateLoginStatus = () => {
  isLoggedIn.value = localStorage.getItem('token') !== null
  console.log('登录状态更新为：', isLoggedIn.value)
}

const loginForm = ref({
  phone: ''
})

const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const loginFormRef = ref(null)
const loginLoading = ref(false)

// 用户切换对话框相关状态
const userSwitchDialogVisible = ref(false)
const switchUserForm = ref({
  phone: ''
})
const switchUserFormRef = ref(null)
const switchUserLoading = ref(false)

// 从API获取的用户列表
const userList = ref([])

// 获取用户列表函数
const fetchUserList = async () => {
  try {
    // 先从缓存中获取
    const cachedUserList = localStorage.getItem('userList')
    if (cachedUserList) {
      userList.value = JSON.parse(cachedUserList)
      console.log('从缓存中获取用户列表:', userList.value)
      return
    }
    
    // 缓存不存在，从API获取
    console.log('从API获取用户列表')
    const response = await api.getAllUsers({ page: 1, pageSize: 20 })
    if (response.code === 0) {
      userList.value = response.data.records
      console.log('从API获取用户列表成功:', userList.value)
      // 缓存用户列表
      localStorage.setItem('userList', JSON.stringify(userList.value))
      console.log('用户列表已缓存到localStorage')
    } else {
      console.error('获取用户列表失败:', response.msg)
      ElMessage.error('获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表错误:', error)
    ElMessage.error('获取用户列表失败')
  }
}

// 自动登录函数 - 禁用自动登录，确保用户必需手动选择登录
const autoLogin = async () => {
  console.log('自动登录函数执行 - 禁用自动登录')
  // 检查是否有退出登录标志
  const logoutFlag = localStorage.getItem('logoutFlag')
  console.log('退出登录标志：', logoutFlag)
  if (logoutFlag) {
    // 清除退出登录标志
    localStorage.removeItem('logoutFlag')
    console.log('清除退出登录标志')
  }
  
  // 更新登录状态
  updateLoginStatus()
  console.log('当前登录状态：', isLoggedIn.value)
  if (isLoggedIn.value) {
    console.log('用户已经登录，保持登录状态')
  } else {
    console.log('用户未登录，等待用户手动登录')
  }
}

// 通用登录函数
const handleLogin = async (phone, loadingRef, successMessage) => {
  try {
    loadingRef.value = true
    console.log('开始登录，手机号：', phone)
    console.log('调用登录API')
    const response = await api.login(phone)
    console.log('登录响应：', response)
    if (response.data.code === 0) {
      console.log('登录成功，token：', response.data.data.token)
      console.log('用户信息：', response.data.data.user)
      localStorage.setItem('token', response.data.data.token)
      localStorage.setItem('userInfo', JSON.stringify(response.data.data.user))
      console.log('存储后的token：', localStorage.getItem('token'))
      console.log('存储后的用户信息：', localStorage.getItem('userInfo'))
      // 更新登录状态
      updateLoginStatus()
      ElMessage.success(successMessage)
      // 刷新页面以显示登录状态
      setTimeout(() => {
        console.log('刷新页面以显示登录状态')
        window.location.reload()
      }, 1000)
      return true
    } else {
      console.log('登录失败：', response.data.msg)
      ElMessage.error(response.data.msg)
      return false
    }
  } catch (error) {
    console.error('登录错误：', error)
    if (error.response) {
      console.error('登录错误响应：', error.response.data)
      ElMessage.error(`登录失败：${error.response.data.data?.msg || error.response.data.msg || '服务器内部错误'}`)
    } else if (error.request) {
      console.error('登录请求错误：', error.request)
      ElMessage.error('登录失败：服务器无响应，请检查后端服务是否运行')
    } else {
      console.error('登录错误信息：', error.message)
      ElMessage.error(`登录失败：${error.message}`)
    }
    return false
  } finally {
    loadingRef.value = false
    console.log('登录过程结束')
  }
}

const login = async () => {
  console.log('登录函数执行')
  if (!loginFormRef.value) {
    console.log('登录表单引用为空')
    return
  }
  await loginFormRef.value.validate(async (valid) => {
    console.log('表单验证结果：', valid)
    if (valid) {
      await handleLogin(loginForm.value.phone, loginLoading, '登录成功')
    } else {
      console.log('表单验证失败')
    }
  })
}

// 显示用户切换对话框
const showUserSwitchDialog = () => {
  // 默认选择当前登录用户
  const currentUserInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  switchUserForm.value.phone = currentUserInfo?.phone || ''
  userSwitchDialogVisible.value = true
}

// 确认切换用户
const switchUserConfirm = async () => {
  console.log('切换用户函数执行')
  if (!switchUserForm.value.phone) {
    console.log('请选择用户')
    ElMessage.error('请选择用户')
    return
  }
  
  const success = await handleLogin(switchUserForm.value.phone, switchUserLoading, '用户切换成功')
  if (success) {
    userSwitchDialogVisible.value = false
  }
}

const logout = () => {
  // 清除所有与用户相关的存储项
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('postListPagination')
  localStorage.setItem('logoutFlag', 'true') // 添加退出登录标志
  // 更新登录状态
  updateLoginStatus()
  ElMessage.success('已退出登录')
  // 刷新页面以显示登录表单
  window.location.reload()
}

const switchToAudit = () => {
  router.push('/audit')
}

const goToHome = () => {
  router.push('/')
}

// 组件挂载时自动登录
import { onMounted } from 'vue'
onMounted(async () => {
  // 先获取用户列表
  await fetchUserList()
  // 然后执行自动登录
  autoLogin()
})
</script>

<style scoped>
.user-home-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.top-nav {
  background-color: #001529;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  border-bottom: 1px solid #002140;
  flex-shrink: 0;
}

.nav-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.nav-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-end;
}

.user-actions .el-button {
  font-size: 12px;
  padding: 0 10px;
  height: 24px;
  line-height: 24px;
  min-width: 80px;
  text-align: center;
}

.login-form {
  display: flex;
  align-items: center;
}

/* 内容容器 */
.content-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.aside {
  background-color: #001529;
  color: white;
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  padding: 20px;
  border-bottom: 1px solid #002140;
  flex-shrink: 0;
}

.menu-container {
  flex: 1;
  overflow-y: auto;
  margin: 20px 0;
}

.menu-container .el-menu-vertical-demo {
  background-color: transparent;
  border-right: none;
}

/* 滚动条样式 */
.menu-container::-webkit-scrollbar {
  width: 6px;
}

.menu-container::-webkit-scrollbar-track {
  background: #001529;
}

.menu-container::-webkit-scrollbar-thumb {
  background: #002140;
  border-radius: 3px;
}

.menu-container::-webkit-scrollbar-thumb:hover {
  background: #003366;
}

.bottom-buttons {
  flex-shrink: 0;
  border-top: 1px solid #002140;
}

.switch-btn {
  padding: 15px 20px;
  text-align: center;
  border-top: 1px solid #002140;
}

.switch-btn .el-button {
  width: 100%;
}

.home-btn {
  padding: 0 20px 15px;
  text-align: center;
  border-top: 1px solid #002140;
}

.home-btn .el-button {
  width: 100%;
}

.el-menu-item {
  color: rgba(255, 255, 255, 0.7);
  margin: 0 10px;
  border-radius: 4px;
  margin-top: 10px;
}

.el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.el-menu-item.is-active {
  background-color: #1890ff;
  color: white;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 14px;
  font-weight: bold;
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
}

.switch-btn {
  color: rgba(255, 255, 255, 0.8);
}

.switch-btn:hover {
  color: white;
}

.logout-btn {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  padding: 0;
}

.logout-btn:hover {
  color: white;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  height: 100%;
}
</style>