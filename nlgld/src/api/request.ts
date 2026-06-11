import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  }
)

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('网络异常，请稍后重试')
    }
    return Promise.reject(error)
  }
)

// ==================== 认证 API ====================
export function login(data: { username: string; password: string; captcha: string }) {
  return request.post('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUser() {
  return request.get('/auth/currentUser')
}

export function updatePassword(data: { oldPassword: string; newPassword: string }) {
  return request.put('/auth/password', data)
}

// ==================== 管理员 - 用户管理 API ====================
export function getUserList(params: any) {
  return request.get('/admin/users', { params })
}

export function createUser(data: any) {
  return request.post('/admin/users', data)
}

export function updateUser(id: number, data: any) {
  return request.put(`/admin/users/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete(`/admin/users/${id}`)
}

export function resetUserPassword(id: number) {
  return request.put(`/admin/users/${id}/resetPassword`)
}

export function updateUserStatus(id: number, status: number) {
  return request.put(`/admin/users/${id}/status`, { status })
}

export function importUsers(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/users/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function exportUsers(params: any) {
  return request.get('/admin/users/export', { params, responseType: 'blob' })
}

// ==================== 管理员 - 学期管理 API ====================
export function getSemesterList() {
  return request.get('/admin/semesters')
}

export function createSemester(data: any) {
  return request.post('/admin/semesters', data)
}

export function updateSemester(id: number, data: any) {
  return request.put(`/admin/semesters/${id}`, data)
}

export function deleteSemester(id: number) {
  return request.delete(`/admin/semesters/${id}`)
}

// ==================== 管理员 - 班级管理 API ====================
export function getClassList(params: any) {
  return request.get('/admin/classes', { params })
}

export function createClass(data: any) {
  return request.post('/admin/classes', data)
}

export function updateClass(id: number, data: any) {
  return request.put(`/admin/classes/${id}`, data)
}

export function deleteClass(id: number) {
  return request.delete(`/admin/classes/${id}`)
}

export function assignStudentsToClass(classId: number, studentIds: number[]) {
  return request.post(`/admin/classes/${classId}/students`, { studentIds })
}

// ==================== 系统监控 API ====================
export function getOnlineUsers() {
  return request.get('/admin/monitor/onlineUsers')
}

export function getSystemStatus() {
  return request.get('/admin/monitor/systemStatus')
}

export function getOperationLogs(params: any) {
  return request.get('/admin/logs/operations', { params })
}

export function getLoginLogs(params: any) {
  return request.get('/admin/logs/login', { params })
}

export function backupData() {
  return request.post('/admin/backup')
}

export default request
