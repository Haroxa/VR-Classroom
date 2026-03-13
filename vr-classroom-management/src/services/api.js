import axios from 'axios'
import JSONBig from 'json-bigint'
import { saveLog, formatLog } from './logger'
import { ElMessage } from 'element-plus'

// 创建一个不使用拦截器的axios实例，用于登录请求
const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  transformResponse: [(data) => {
    if (typeof data === 'string') {
      try {
        // 使用 json-bigint 解析，大数字转为字符串
        return JSONBig({ storeAsString: true }).parse(data);
      } catch (e) {
        return data;
      }
    }
    return data;
  }]
})

// 模拟用户列表，用于自动登录
const userList = [
  { phone: '13800138001', name: '张明' },
  { phone: '13800138002', name: '李华' },
  { phone: '13800138003', name: '王芳' },
  { phone: '13800138004', name: '刘洋' },
  { phone: '13800138005', name: '陈静' },
  { phone: '13800138006', name: '杨强' },
  { phone: '13800138007', name: '赵敏' },
  { phone: '13800138008', name: '周杰' },
  { phone: '13800138009', name: '吴磊' },
  { phone: '13800138010', name: '郑欣' },
  { phone: '13800138011', name: '孙伟' },
  { phone: '13800138012', name: '钱多多' },
  { phone: '13800138013', name: '韩雪' },
  { phone: '13800138014', name: '冯刚' },
  { phone: '13800138015', name: '董丽' }
]

// 存储请求日志的临时对象
const requestLogs = new Map()

// 生成唯一请求ID
const generateRequestId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  transformResponse: [(data) => {
    if (typeof data === 'string') {
      try {
        // 使用 json-bigint 解析，大数字转为字符串
        return JSONBig({ storeAsString: true }).parse(data);
      } catch (e) {
        return data;
      }
    }
    return data;
  }]
})

api.interceptors.request.use(
  config => {
    // 直接从localStorage获取token，避免在非组件环境中调用useUserStore()
    const token = localStorage.getItem('token')
    // 登录请求不需要token
    if (token && !config.url.includes('/users/login')) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 生成唯一请求ID
    const requestId = generateRequestId()
    config.requestId = requestId
    
    // 记录请求开始时间
    config.requestStartTime = Date.now()
    
    // 存储请求信息
    const requestInfo = {
      url: config.url,
      method: config.method,
      params: config.params,
      data: config.data,
      headers: config.headers,
      timestamp: new Date().toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' }),
      timestampMs: Date.now()
    }
    
    // 存储请求日志到临时对象
    requestLogs.set(requestId, requestInfo)
    
    // 记录请求日志
    console.log('=== API请求开始 ===')
    console.log('请求ID:', requestId)
    console.log('请求时间:', requestInfo.timestamp)
    console.log('请求路径:', config.url)
    console.log('请求方法:', config.method)
    console.log('请求参数:', config.params)
    console.log('请求数据:', config.data)
    console.log('请求头:', config.headers)
    console.log('==================')
    
    // 保存请求开始日志到localStorage
    saveLog(formatLog('request', `API请求开始: ${config.method} ${config.url}`, {
      url: config.url,
      method: config.method,
      params: config.params,
      data: config.data,
      headers: config.headers
    }))
    
    return config
  },
  error => {
    console.error('=== API请求错误 ===')
    console.error('错误时间:', new Date().toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' }))
    console.error('错误信息:', error.message)
    console.error('==================')
    
    // 保存请求错误日志到localStorage
    saveLog(formatLog('error', 'API请求错误', {
      message: error.message,
      stack: error.stack
    }))
    
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  response => {
    // 计算请求耗时
    const requestTime = Date.now() - response.config.requestStartTime
    
    // 获取请求信息
    const requestId = response.config.requestId
    const requestInfo = requestLogs.get(requestId)
    

    
    // 合并请求和响应信息
    const mergedLog = {
      requestId,
      request: requestInfo,
      response: {
        status: response.status,
        data: response.data,
        timestamp: new Date().toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' }),
        timestampMs: Date.now()
      },
      requestTime
    }
    
    // 记录合并后的日志
    console.log('=== API请求-响应完成 ===')
    console.log('请求ID:', requestId)
    console.log('请求时间:', requestInfo.timestamp)
    console.log('响应时间:', mergedLog.response.timestamp)
    console.log('请求路径:', response.config.url)
    console.log('请求方法:', response.config.method)
    console.log('请求耗时:', requestTime, 'ms')
    console.log('响应状态:', response.status)
    console.log('响应数据:', response.data)
    console.log('==================')
    
    // 保存合并后的日志到localStorage
    saveLog(formatLog('api', `API请求-响应: ${response.config.method} ${response.config.url}`, mergedLog))
    
    // 从临时存储中移除请求信息
    requestLogs.delete(requestId)
    
    return response.data
  },
  error => {
    // 计算请求耗时
    const requestTime = Date.now() - (error.config?.requestStartTime || Date.now())
    
    // 获取请求信息
    const requestId = error.config?.requestId
    const requestInfo = requestLogs.get(requestId)
    
    // 合并请求和错误信息
    const mergedLog = {
      requestId,
      request: requestInfo,
      error: {
        message: error.message,
        response: error.response?.data,
        stack: error.stack,
        timestamp: new Date().toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' }),
        timestampMs: Date.now()
      },
      requestTime
    }
    
    console.error('=== API请求-响应错误 ===')
    console.error('请求ID:', requestId)
    console.error('请求时间:', requestInfo?.timestamp)
    console.error('错误时间:', mergedLog.error.timestamp)
    console.error('请求路径:', error.config?.url)
    console.error('请求耗时:', requestTime, 'ms')
    console.error('错误信息:', error.message)
    console.error('错误响应:', error.response?.data)
    console.error('==================')
    
    // 保存合并后的错误日志到localStorage
    saveLog(formatLog('error', `API请求-响应错误: ${error.config?.url}`, mergedLog))
    
    // 从临时存储中移除请求信息
    if (requestId) {
      requestLogs.delete(requestId)
    }
    
    // 处理401未认证错误，切换到退出登录模式
    if (error.response && error.response.status === 401) {
      console.log('Token失效，切换到退出登录模式...')
      
      // 显示提示弹窗
      ElMessage.error('登录已过期，请重新登录')
      
      // 清除当前token和用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.setItem('logoutFlag', 'true') // 添加退出登录标志
      
      // 刷新页面以显示登录表单
      setTimeout(() => {
        window.location.reload()
      }, 1000)
    }
    
    return Promise.reject(error)
  }
)

export default {
  // 登录
  login(phone) {
    return axiosInstance.post('/users/login/phone', { phone })
  },
  // 后台管理 - 帖子列表
  getAdminPosts(params) {
    return api.get('/admin/posts', { params })
  },
  // 后台管理 - 评论列表
  getAdminComments(params) {
    return api.get('/admin/comments', { params })
  },
  // 后台管理 - 审核帖子
  auditPost(id, data) {
    return api.patch(`/admin/posts/${id}`, data)
  },
  // 后台管理 - 审核评论
  auditComment(id, data) {
    return api.patch(`/admin/comments/${id}`, data)
  },
  // 帖子详情
  getPostDetail(id) {
    return api.get(`/posts/${id}`)
  },
  // 获取分类列表
  getCategories() {
    return api.get('/categories')
  },
  // 后台管理 - 添加帖子
  addPost(data) {
    return api.post('/admin/posts', data)
  },
  // 后台管理 - 修改帖子
  updateAdminPost(id, data) {
    return api.put(`/admin/posts/${id}`, data)
  },
  // 后台管理 - 删除帖子
  deleteAdminPost(id) {
    return api.delete(`/admin/posts/${id}`)
  },
  // 上传图片
  uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/images/post', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  // 用户模块 - 获取帖子列表
  getPosts(params) {
    return api.get('/posts', { params })
  },
  // 用户模块 - 获取帖子评论
  getPostComments(postId, params) {
    return api.get(`/comments`, { params: { postId, ...params } })
  },
  // 用户模块 - 点赞帖子
  likePost(postId) {
    return api.post(`/posts/${postId}/likes`)
  },
  // 用户模块 - 取消点赞帖子
  unlikePost(postId) {
    return api.delete(`/posts/${postId}/likes`)
  },
  // 用户模块 - 点赞评论
  likeComment(commentId) {
    return api.post(`/comments/${commentId}/likes`)
  },
  // 用户模块 - 取消点赞评论
  unlikeComment(commentId) {
    return api.delete(`/comments/${commentId}/likes`)
  },
  // 用户模块 - 添加评论
  addComment(postId, data) {
    return api.post(`/comments`, { ...data, postId })
  },
  // 用户模块 - 获取用户评论
  getUserComments(params) {
    return api.get('/users/comments', { params })
  },
  // 用户模块 - 删除评论
  deleteComment(commentId) {
    return api.delete(`/comments/${commentId}`)
  },
  // 用户模块 - 修改评论
  updateComment(commentId, data) {
    return api.put(`/comments/${commentId}`, data)
  },
  // 用户模块 - 获取我的帖子
  getMyPosts(params) {
    return api.get('/users/posts', { params })
  },
  // 用户模块 - 获取我点赞的帖子
  getLikedPosts(params) {
    return api.get('/users/liked-posts', { params })
  },
  // 用户模块 - 获取我点赞的评论
  getLikedComments(params) {
    return api.get('/users/liked-comments', { params })
  },
  // 用户模块 - 获取订单详情
  getOrderDetail(orderId) {
    return api.get(`/orders/${orderId.toString()}`)
  },
  // 用户模块 - 创建订单
  createOrder(data) {
    return api.post('/orders', data)
  },
  // 用户模块 - 查询订单
  getUserOrders(params) {
    return api.get('/orders', { params })
  },
  // 用户模块 - 取消订单
  cancelOrder(orderId) {
    return api.patch(`/orders/${orderId.toString()}`, { status: 'CANCELLED' })
  },
  // 用户模块 - 模拟支付
  mockPay(orderId) {
    return api.post('/mock/pay/notify', { orderId: orderId.toString() })
  },
  // 用户模块 - 发布帖子
  createPost(data) {
    return api.post('/posts', data)
  },
  // 用户模块 - 修改帖子
  updatePost(postId, data) {
    return api.put(`/posts/${postId}`, data)
  },
  // 用户模块 - 删除帖子
  deletePost(postId) {
    return api.delete(`/posts/${postId}`)
  },
  // 后台管理 - 获取订单列表
  getAdminOrders(params) {
    return api.get('/admin/orders', { params })
  },
  // 后台管理 - 审核订单
  auditOrder(orderId, data) {
    return api.patch(`/admin/orders/${orderId}`, data)
  },
  // 校区相关接口
  getCampuses() {
    return api.get('/campuses')
  },
  // 获取校区下的楼栋
  getCampusBuildings(campusId) {
    return api.get(`/campuses/${campusId}/buildings`)
  },
  // 获取所有楼栋
  getBuildings() {
    return api.get('/buildings')
  },
  // 获取楼栋下的教室
  getBuildingRooms(buildingId) {
    return api.get(`/buildings/${buildingId}/rooms`)
  },
  // 获取所有教室
  getRooms() {
    return api.get('/rooms')
  },
  // 获取教室详情
  getRoomDetail(roomId) {
    return api.get(`/rooms/${roomId}`)
  },
  // 获取教室座位图
  getRoomSeats(roomId) {
    return api.get(`/rooms/${roomId}/seats`)
  },
  // 获取所有用户
  getAllUsers(params) {
    return api.get('/users/all', { params })
  }
}