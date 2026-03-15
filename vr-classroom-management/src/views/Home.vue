<template>
  <div class="home-container">
    <div class="header">
      <h1>VR教室管理系统</h1>
      <p>欢迎使用VR教室管理系统，请选择您需要的功能模块</p>
    </div>
    <div class="content">
      <div class="module-card audit-module">
        <div class="module-header">
          <el-icon class="module-icon"><View /></el-icon>
          <h2>后台管理</h2>
        </div>
        <div class="module-content">
          <p>后台管理模块用于管理和审核用户发布的内容，监控系统运行状态，以及发布系统公告。</p>
          <ul>
            <li>帖子审核：审核用户发布的帖子，确保内容符合平台规范</li>
            <li>评论审核：审核用户发布的评论，确保内容符合平台规范</li>
            <li>日志记录：查看系统操作日志，了解系统运行状态</li>
            <li>接口文档：查看后端API接口文档，了解接口使用方法</li>
            <li>公告管理：管理系统公告，向用户发布重要信息</li>
            <li>系统说明：查看系统功能和使用说明</li>
          </ul>
        </div>
        <div class="module-footer">
          <el-button type="primary" size="large" @click="goToAudit">进入后台管理</el-button>
        </div>
      </div>
      <div class="module-card user-module">
        <div class="module-header">
          <el-icon class="module-icon"><User /></el-icon>
          <h2>用户中心</h2>
        </div>
        <div class="module-content">
          <p>用户中心模块用于用户浏览内容、管理个人信息、查看订单和预订座位等操作。</p>
          <ul>
            <li>帖子浏览：浏览和搜索平台上的帖子，查看帖子详情</li>
            <li>我的帖子：管理自己发布的帖子，查看帖子状态</li>
            <li>我的评论：查看和管理自己发布的评论</li>
            <li>我点赞的帖子：查看自己点赞过的帖子列表</li>
            <li>我点赞的评论：查看自己点赞过的评论列表</li>
            <li>我的订单：查看和管理VR教室的预订订单</li>
            <li>创建订单：创建新的VR教室预订订单</li>
            <li>座位管理：查看VR教室的座位布局，选择和预订座位</li>
            <li>个人中心：管理个人信息，查看账号状态</li>
          </ul>
        </div>
        <div class="module-footer">
          <el-button type="success" size="large" @click="goToUser">进入用户中心</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { View, User } from '@element-plus/icons-vue'

const router = useRouter()

const goToAudit = () => {
  router.push('/audit')
}

const goToUser = () => {
  router.push('/user')
}
</script>

<style scoped>
.home-container {
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.header h1 {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
}

.header p {
  font-size: 18px;
  opacity: 0.9;
}

.content {
  display: flex;
  gap: 40px;
  width: 100%;
  max-width: 1000px;
}

.module-card {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  min-height: 350px;
}

.module-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.module-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.module-icon {
  font-size: 32px;
  margin-right: 15px;
  color: #1890ff;
}

.audit-module .module-icon {
  color: #1890ff;
}

.user-module .module-icon {
  color: #67c23a;
}

.module-header h2 {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.module-content {
  flex: 1;
  margin-bottom: 30px;
}

.module-content p {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 15px;
  color: #606266;
}

.module-content ul {
  padding-left: 20px;
}

.module-content li {
  margin-bottom: 8px;
  color: #606266;
}

.module-footer {
  text-align: center;
  margin-top: auto;
}

.module-footer .el-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}

@media (max-width: 768px) {
  .content {
    flex-direction: column;
    gap: 20px;
  }
  
  .module-card {
    width: 100%;
  }
}
</style>