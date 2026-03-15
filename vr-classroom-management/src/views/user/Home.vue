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
              <span>后台页面</span>
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
    
    <!-- 公告弹窗 -->
    <el-dialog
      v-model="announcementDialogVisible"
      title="系统公告"
      width="500px"
      :close-on-click-modal="false"
      center
    >
      <div v-if="activeAnnouncement" class="announcement-dialog-content">
        <h3 class="announcement-title">{{ activeAnnouncement.title }}</h3>
        <div class="announcement-meta">
          <div class="meta-item">
            <span class="meta-label">标签：</span>
            <div class="tags-container">
              <el-tag 
                v-for="tag in activeAnnouncement.tags" 
                :key="tag" 
                :type="getTagType(tag)"
                style="margin-right: 5px; margin-bottom: 5px;"
              >{{ tag }}</el-tag>
            </div>
          </div>
          <div class="meta-item">
            <span class="meta-label">发布人：</span>
            <span class="meta-value">{{ activeAnnouncement.author }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">发布时间：</span>
            <span class="meta-value">{{ activeAnnouncement.createdAt }}</span>
          </div>
        </div>
        <div class="announcement-content">
          <div class="content-label">内容：</div>
          <div class="content-text">{{ activeAnnouncement.content }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="announcementDialogVisible = false">我知道了</el-button>
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
import { loadAnnouncements } from '../../services/fileStorage'
import { saveLog, formatLog } from '../../services/logger'

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
    if (error.response) {
      console.error('获取用户列表错误响应:', error.response)
      ElMessage.error(`获取用户列表失败: ${error.response.data?.msg || error.response.status}`)
    } else if (error.request) {
      console.error('获取用户列表请求错误:', error.request)
      ElMessage.error('获取用户列表失败: 服务器无响应')
    } else {
      console.error('获取用户列表错误信息:', error.message)
      ElMessage.error(`获取用户列表失败: ${error.message}`)
    }
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
    
    // 记录登录成功日志，与其他接口日志格式一致
    saveLog(formatLog('api', `API请求-响应: post /users/login/phone`, {
      requestId: Date.now().toString(36) + Math.random().toString(36).substr(2),
      request: {
        url: '/users/login/phone',
        method: 'post',
        data: { phone },
        timestamp: new Date().toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' }),
        timestampMs: Date.now()
      },
      response: {
        status: 200,
        data: response.data,
        timestamp: new Date().toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' }),
        timestampMs: Date.now()
      },
      requestTime: Date.now() - Date.now() // 模拟请求时间
    }))
    
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

const logout = async () => {
  try {
    // 调用退出登录API
    const response = await api.logout()
    if (response.code === 0) {
      // 清除所有与用户相关的存储项
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('postListPagination')
      localStorage.setItem('logoutFlag', 'true') // 添加退出登录标志
      // 更新登录状态
      updateLoginStatus()
      ElMessage.success('已退出登录')
      // 延迟刷新页面，让用户看到提示
      setTimeout(() => {
        window.location.reload()
      }, 1000)
    } else {
      ElMessage.error(response.msg || '退出登录失败')
    }
  } catch (error) {
    console.error('退出登录错误:', error)
    // 即使API调用失败，也要清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('postListPagination')
    localStorage.setItem('logoutFlag', 'true')
    updateLoginStatus()
    ElMessage.success('已退出登录')
    // 延迟刷新页面，让用户看到提示
    setTimeout(() => {
      window.location.reload()
    }, 1000)
  }
}

const switchToAudit = () => {
  router.push('/audit')
}

const goToHome = () => {
  router.push('/')
}

// 公告相关状态
const announcementDialogVisible = ref(false)
const activeAnnouncement = ref(null)

// 获取标签类型
const getTagType = (tag) => {
  const tagTypes = {
    '新增': 'success',
    '修复': 'warning',
    '实现': 'info',
    '更新': 'primary',
    '通知': 'danger'
  }
  return tagTypes[tag] || 'default'
}

// 加载活跃公告
const loadActiveAnnouncement = async () => {
  try {
    // 使用loadAnnouncements函数加载公告，确保按照创建时间排序
    const announcements = await loadAnnouncements()
    if (announcements && announcements.length > 0) {
      // 找到第一个isActive为true的公告
      const active = announcements.find(a => a.isActive)
      if (active) {
        activeAnnouncement.value = active
        announcementDialogVisible.value = true
      }
    }
  } catch (error) {
    console.error('加载公告失败:', error)
  }
}

// 组件挂载时自动登录
import { onMounted } from 'vue'
onMounted(async () => {
  // 先获取用户列表
  await fetchUserList()
  // 然后执行自动登录
  autoLogin()
  // 检查是否有活跃公告
  await loadActiveAnnouncement()
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

/* 公告弹窗样式 */
.announcement-dialog-content {
  padding: 10px 0;
}

.announcement-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
  text-align: center;
}

.announcement-meta {
  margin: 0 0 20px 0;
}

.meta-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  font-size: 14px;
}

.meta-label {
  font-weight: bold;
  color: #666;
  min-width: 80px;
}

.meta-value {
  color: #333;
  flex: 1;
}

.tags-container {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.announcement-content {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.content-label {
  font-weight: bold;
  color: #666;
  margin-bottom: 10px;
}

.content-text {
  line-height: 1.6;
  white-space: pre-wrap;
  color: #333;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  min-height: 100px;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: center;
}
</style>