<template>
  <div class="system-info-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统说明</span>
        </div>
      </template>
      <div class="system-info-content">
        <h2>项目说明</h2>
        <p>VR教室管理系统是一个用于管理VR教室相关功能的综合平台，包括帖子管理、评论管理、订单管理、座位管理等功能。系统分为后台管理和用户页面两个主要模块，分别提供管理和使用功能。</p>
        
        <h2>后台管理模块</h2>
        <ul>
          <li><strong>帖子审核</strong>：审核用户发布的帖子，确保内容符合平台规范。可以通过状态、分类和关键词进行筛选，对帖子进行通过或驳回操作。</li>
          <li><strong>评论审核</strong>：审核用户发布的评论，确保内容符合平台规范。可以通过状态和关键词进行筛选，对评论进行通过或驳回操作。</li>
          <li><strong>日志记录</strong>：查看系统操作日志，了解系统运行状态。可以按日志类型和关键词进行筛选，查看详细的API请求和响应信息。</li>
          <li><strong>接口文档</strong>：查看后端API接口文档，了解接口使用方法。支持在线测试接口功能。</li>
          <li><strong>公告管理</strong>：管理系统公告，向用户发布重要信息。可以设置公告的标签、发布人和内容，并控制哪些公告在用户页面显示。</li>
        </ul>
        
        <h2>用户页面模块</h2>
        <ul>
          <li><strong>帖子浏览</strong>：浏览和搜索平台上的帖子，查看帖子详情，支持点赞和评论功能。</li>
          <li><strong>我的帖子</strong>：管理自己发布的帖子，查看帖子状态和评论情况。</li>
          <li><strong>我的评论</strong>：查看和管理自己发布的评论。</li>
          <li><strong>我点赞的帖子</strong>：查看自己点赞过的帖子列表。</li>
          <li><strong>我点赞的评论</strong>：查看自己点赞过的评论列表。</li>
          <li><strong>我的订单</strong>：查看和管理VR教室的预订订单，包括创建新订单和查看订单状态。</li>
          <li><strong>座位管理</strong>：查看VR教室的座位布局，选择和预订座位。</li>
          <li><strong>个人中心</strong>：管理个人信息，查看账号状态和相关数据。</li>
        </ul>
        
        <h2>相关链接</h2>
        <div class="link-list">
          <el-card shadow="hover" class="link-card">
            <template #header>
              <div class="link-header">
                <el-icon><Link /></el-icon>
                <span>GitHub仓库</span>
              </div>
            </template>
            <div class="link-content">
              <a href="https://github.com/Haroxa/VR-Classroom" target="_blank" class="link">https://github.com/Haroxa/VR-Classroom</a>
              <el-button type="primary" size="small" @click="openLink('https://github.com/Haroxa/VR-Classroom')">访问</el-button>
            </div>
          </el-card>
          
          <el-card shadow="hover" class="link-card">
            <template #header>
              <div class="link-header">
                <el-icon><Link /></el-icon>
                <span>1Panel 控制台</span>
              </div>
            </template>
            <div class="link-content">
              <a href="http://10.86.136.242:40216/01044b5cb5" target="_blank" class="link">http://10.86.136.242:40216/01044b5cb5</a>
              <el-button type="primary" size="small" @click="openLink('http://10.86.136.242:40216/01044b5cb5')">访问</el-button>
            </div>
            <div class="login-info">
              <p><strong>用户名：</strong>361f63cc12</p>
              <p><strong>密码：</strong>5ee809e6cf</p>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Link } from '@element-plus/icons-vue'

const openLink = (url) => {
  window.open(url, '_blank')
}
</script>

<style scoped>
.system-info-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.system-info-content {
  padding: 20px 0;
}

.system-info-content h2 {
  font-size: 16px;
  font-weight: bold;
  /* margin-top: 15px; */
  margin-bottom: 10px;
  color: #303133;
}

.system-info-content p {
  margin-bottom: 15px;
  line-height: 1.6;
  color: #606266;
}

.system-info-content ul {
  padding-left: 20px;
  margin-bottom: 20px;
}

.system-info-content li {
  margin-bottom: 10px;
  line-height: 1.6;
  color: #606266;
}

.link-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.link-card {
  transition: all 0.3s ease;
}

.link-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.link-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.link-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.link {
  color: #409eff;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 10px;
}

.link:hover {
  text-decoration: underline;
}

.login-info {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
}

.login-info p {
  margin: 5px 0;
  color: #606266;
}


</style>