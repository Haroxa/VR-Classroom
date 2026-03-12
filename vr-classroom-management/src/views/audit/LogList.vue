<template>
  <div class="audit-log-list">
    <h2>接口日志记录</h2>
    <LogManager />
  </div>
</template>

<script setup>
import LogManager from '../../components/LogManager.vue'
</script>

<style scoped>
.audit-log-list {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
}
</style>