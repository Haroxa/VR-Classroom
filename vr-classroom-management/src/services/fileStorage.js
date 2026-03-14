// 文件存储服务
// 方案1：从静态JSON文件加载初始数据，LocalStorage存储用户修改

const ANNOUNCEMENTS_FILE = 'announcements.json'

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
    const response = await fetch(`/${ANNOUNCEMENTS_FILE}`)
    if (response.ok) {
      const data = await response.json()
      console.log('从静态文件加载公告成功')
      return data
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
  clearAnnouncements
}