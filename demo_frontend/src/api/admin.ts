import request from './request'
export function unwrapList<T = any>(res: any): T[] {
  if (Array.isArray(res)) return res
  if (Array.isArray(res?.list)) return res.list
  if (Array.isArray(res?.records)) return res.records
  if (Array.isArray(res?.data?.list)) return res.data.list
  if (Array.isArray(res?.data?.records)) return res.data.records
  if (Array.isArray(res?.data)) return res.data
  return []
}

export function unwrapTotal(res: any, fallback = 0): number {
  return Number(res?.pagination?.total ?? res?.total ?? res?.data?.pagination?.total ?? res?.data?.total ?? fallback)
}

// ==================== 管理员 - 用户管理 API ====================
export function getUserList(params: { page: number; pageSize: number; role: string; keyword?: string }) {
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

export function exportUsers(params: { role: string }) {
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
export function getClassList(params: { page: number; pageSize: number; keyword?: string }) {
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
  return request.get('/admin/monitor/online/current')
}

export function forceLogoutUser(userId: number) {
  return request.post(`/admin/monitor/onlineUsers/${userId}/logout`)
}

export function getOnlineTrend(params?: { hours?: number }) {
  return request.get('/admin/monitor/online/trend', { params })
}

export function getCurrentOnline() {
  return request.get('/admin/monitor/online/current')
}

export function getSystemStatus() {
  return request.get('/admin/monitor/health')
}

export function getOperationLogs(params: any) {
  return request.get('/admin/logs/operation', { params })
}

export function getLoginLogs(params: any) {
  return request.get('/admin/logs/login', { params })
}

export function getAuditLogs(params: any) {
  return request.get('/admin/logs/operation', { params })
}

// ==================== 数据管理 API ====================
export function backupData() {
  return Promise.reject(new Error('后端暂未实现 /admin/backup'))
}

export function cleanupData(days: number) {
  return Promise.reject(new Error(`后端暂未实现 /admin/cleanup, days=${days}`))
}

export function getStorageStats() {
  return request.get('/admin/data/storage')
}

// ==================== 仪表盘 API ====================
export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}

export function getDashboardTrend(params: { range?: string }) {
  return request.get('/admin/dashboard/trend', { params })
}

// ==================== 日志统计 API ====================
export function getLogStatistics() {
  return request.get('/admin/logs/statistics')
}

