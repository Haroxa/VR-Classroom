<template>
  <div class="api-docs-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>后端接口文档</span>
          <el-button type="primary" size="small" @click="refreshDocs">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <div class="api-docs-content">
        <iframe
          ref="iframeRef"
          :src="apiDocsUrl"
          class="api-docs-iframe"
          frameborder="0"
          @load="onIframeLoad"
          @error="onIframeError"
        />
        <div v-if="loading" class="loading-overlay">
          <el-loading :text="'加载中...'" :fullscreen="false" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElLoading } from 'element-plus'

const loading = ref(true)
// 本地使用绝对路径
const apiDocsUrl = 'http://localhost:8082/swagger-ui/index.html'
// 服务器使用相对路径
// const apiDocsUrl = '/swagger-ui/index.html'
const iframeRef = ref(null)

const onIframeLoad = () => {
  console.log('Iframe loaded successfully')
  loading.value = false
}

const onIframeError = () => {
  console.log('Iframe loading error')
  loading.value = false
  alert('接口文档加载失败，请检查后端服务是否运行')
}

const refreshDocs = () => {
  loading.value = true
  if (iframeRef.value) {
    console.log('Refreshing iframe...')
    iframeRef.value.src = apiDocsUrl
  }
}

onMounted(() => {
  console.log('ApiDocs component mounted')
  console.log('Api docs URL:', apiDocsUrl)
  console.log('Iframe element:', iframeRef.value)
})
</script>

<style scoped>
.api-docs-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.api-docs-content {
  position: relative;
  height: 80vh;
  min-height: 600px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 10;
}

.api-docs-iframe {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 4px;
}
</style>