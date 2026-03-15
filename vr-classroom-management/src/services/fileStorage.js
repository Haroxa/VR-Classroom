// 文件存储服务
// 方案1：从静态JSON文件加载初始数据，LocalStorage存储用户修改

const ANNOUNCEMENTS_FILE = 'announcements.json'
const ANNOUNCEMENTS_VERSION_KEY = 'announcements_version'

// 保存公告到本地存储（用户修改）
const saveAnnouncements = (announcements) => {
  try {
    localStorage.setItem(ANNOUNCEMENTS_FILE, JSON.stringify(announcements))
    console.log('公告保存到本地存储成功')
    return true
  } catch (error) {
    console.error('保存公告失败:', error)
    return false
  }
}

// 从静态文件加载公告（初始数据）
const loadFromStaticFile = async () => {
  try {
    // 添加时间戳参数，避免浏览器缓存
    const timestamp = new Date().getTime()
    const response = await fetch(`/${ANNOUNCEMENTS_FILE}?t=${timestamp}`)
    if (response.ok) {
      const data = await response.json()
      console.log('从静态文件加载公告成功')
      
      // 检查数据结构
      if (data && data.announcements) {
        // 保存版本信息到本地存储
        localStorage.setItem(ANNOUNCEMENTS_VERSION_KEY, data.version || '1.0.0')
        return data.announcements
      } else {
        // 兼容旧格式
        return data
      }
    } else {
      console.error('加载静态文件失败:', response.status)
      return []
    }
  } catch (error) {
    console.error('加载静态文件失败:', error)
    return []
  }
}

// 从本地存储或静态文件加载公告
const loadAnnouncements = async () => {
  try {
    let data
    // 先尝试从LocalStorage加载（用户修改的版本）
    const stored = localStorage.getItem(ANNOUNCEMENTS_FILE)
    if (stored) {
      console.log('从本地存储加载公告成功')
      data = JSON.parse(stored)
    } else {
      // 若LocalStorage无数据，从静态文件加载
      data = await loadFromStaticFile()
    }
    
    // 按照创建时间降序排列
    if (data && data.length > 0) {
      data.sort((a, b) => {
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
    }
    
    return data
  } catch (error) {
    console.error('加载公告失败:', error)
    // 出错时尝试从静态文件加载
    const data = await loadFromStaticFile()
    // 按照创建时间降序排列
    if (data && data.length > 0) {
      data.sort((a, b) => {
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
    }
    return data
  }
}

// 检查并更新公告（如果有新版本）
const checkAndUpdateAnnouncements = async () => {
  try {
    // 添加时间戳参数，避免浏览器缓存
    const timestamp = new Date().getTime()
    const response = await fetch(`/${ANNOUNCEMENTS_FILE}?t=${timestamp}`)
    if (response.ok) {
      const data = await response.json()
      if (data && data.version) {
        const currentVersion = localStorage.getItem(ANNOUNCEMENTS_VERSION_KEY) || '1.0.0'
        
        // 如果版本更新了，重新加载公告
        if (data.version !== currentVersion) {
          console.log(`公告版本更新: ${currentVersion} -> ${data.version}`)
          localStorage.setItem(ANNOUNCEMENTS_VERSION_KEY, data.version)
          
          // 清除本地存储的公告数据，下次加载时会从静态文件加载新数据
          localStorage.removeItem(ANNOUNCEMENTS_FILE)
          return true
        }
      }
    }
    return false
  } catch (error) {
    console.error('检查公告更新失败:', error)
    return false
  }
}

// 清空公告
const clearAnnouncements = () => {
  try {
    localStorage.removeItem(ANNOUNCEMENTS_FILE)
    console.log('清空公告成功')
    return true
  } catch (error) {
    console.error('清空公告失败:', error)
    return false
  }
}

export {
  saveAnnouncements,
  loadAnnouncements,
  clearAnnouncements,
  checkAndUpdateAnnouncements
}