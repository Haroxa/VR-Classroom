<template>
  <div class="audit-home-container">
    <el-container>
      <el-aside width="200px" class="aside">
        <div class="logo">后台管理</div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          @select="handleMenuSelect"
        >
          <el-menu-item index="posts">
            <el-icon><Message /></el-icon>
            <span>帖子审核</span>
          </el-menu-item>
          <el-menu-item index="comments">
            <el-icon><ChatDotRound /></el-icon>
            <span>评论审核</span>
          </el-menu-item>
          <el-menu-item index="logs">
            <el-icon><Document /></el-icon>
            <span>日志记录</span>
          </el-menu-item>
          <el-menu-item index="api-docs">
            <el-icon><Link /></el-icon>
            <span>接口文档</span>
          </el-menu-item>
          <el-menu-item index="announcements">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
          <el-menu-item index="system-info">
            <el-icon><View /></el-icon>
            <span>系统说明</span>
          </el-menu-item>
        </el-menu>
        <div class="switch-btn">
          <el-button type="primary" size="small" @click="switchToUser">
            <el-icon><User /></el-icon>
            <span>用户页面</span>
          </el-button>
        </div>
        <div class="home-btn">
          <el-button size="small" @click="goToHome">
            <el-icon><House /></el-icon>
            <span>返回主页</span>
          </el-button>
        </div>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Message, ChatDotRound, Document, User, House, Link, Bell, View } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => {
  return route.path.split('/').pop() || 'posts'
})

const handleMenuSelect = (key) => {
  router.push(`/audit/${key}`)
}

const switchToUser = () => {
  try {
    console.log('跳转到用户页面...')
    router.push('/user/posts')
  } catch (error) {
    console.error('跳转失败:', error)
  }
}

const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.audit-home-container {
  height: 100vh;
  overflow: hidden;
}

.aside {
  background-color: #001529;
  color: white;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 100vh;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  padding: 20px;
  border-bottom: 1px solid #002140;
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

.el-menu-vertical-demo {
  flex: 1;
  background-color: transparent;
  border-right: none;
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

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  height: 100vh;
}
</style>