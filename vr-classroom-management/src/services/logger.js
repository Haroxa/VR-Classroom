// 日志存储服务

// 日志存储键名
const LOG_STORAGE_KEY = 'api_logs'

// 最大日志数量
const MAX_LOGS = 1000

// 保存日志到localStorage
export const saveLog = (log) => {
  try {
    const logs = getLogs()
    logs.push(log)
    
    // 限制日志数量
    if (logs.length > MAX_LOGS) {
      logs.splice(0, logs.length - MAX_LOGS)
    }
    
    localStorage.setItem(LOG_STORAGE_KEY, JSON.stringify(logs))
  } catch (error) {
    console.error('保存日志失败:', error)
  }
}

// 获取所有日志
export const getLogs = () => {
  try {
    const logs = localStorage.getItem(LOG_STORAGE_KEY)
    return logs ? JSON.parse(logs) : []
  } catch (error) {
    console.error('获取日志失败:', error)
    return []
  }
}

// 清空日志
export const clearLogs = () => {
  try {
    localStorage.removeItem(LOG_STORAGE_KEY)
  } catch (error) {
    console.error('清空日志失败:', error)
  }
}

// 导出日志为文件
export const exportLogs = () => {
  try {
    const logs = getLogs()
    const logContent = logs.map(log => {
      return `[${log.timestamp}] ${log.type.toUpperCase()}: ${log.message}\n${JSON.stringify(log.data, null, 2)}\n`
    }).join('\n====================================\n')
    
    const blob = new Blob([logContent], { type: 'text/plain' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `api-logs-${new Date().toISOString().slice(0, 19).replace(/[:T]/g, '-')}.txt`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    
    return true
  } catch (error) {
    console.error('导出日志失败:', error)
    return false
  }
}

// 格式化日志数据
export const formatLog = (type, message, data = {}) => {
  return {
    type,
    message,
    data,
    timestamp: new Date().toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' }),
    timestampMs: Date.now()
  }
}
