import axios from 'axios'
import type { ApiResponse } from './types'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置 NProgress
NProgress.configure({ showSpinner: false, minimum: 0.3, speed: 500 })

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 显示进度条
    NProgress.start()

    // 添加 Token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加 X-User-Id，并用 userInfo 兜底修复跨角色切换后的旧 userId
    let userId = localStorage.getItem('userId')
    const userInfoText = localStorage.getItem('userInfo')
    if (userInfoText) {
      try {
        const userInfo = JSON.parse(userInfoText)
        if (userInfo?.id && String(userInfo.id) !== userId) {
          userId = String(userInfo.id)
          localStorage.setItem('userId', userId)
          if (userInfo.role) localStorage.setItem('role', userInfo.role)
        }
      } catch {
        // ignore invalid cache
      }
    }
    if (userId) {
      config.headers['X-User-Id'] = userId
    }

    return config
  },
  (error) => {
    NProgress.done()
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    NProgress.done()

    const res = response.data as ApiResponse

    // 根据业务状态码处理
    if (res.code === 200 || res.code === 0) {
      return res.data
    } else {
      // 业务错误提示
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
  },
  (error) => {
    NProgress.done()

    // HTTP 错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误，请稍后重试')
          break
        default:
          ElMessage.error(error.response.data?.message || '网络错误')
      }
    } else if (error.message.includes('timeout')) {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }

    return Promise.reject(error)
  }
)

export default request
