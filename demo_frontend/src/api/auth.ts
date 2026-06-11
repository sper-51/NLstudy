import request from './request'

/** 通用登录 - 后端根据 username 自动识别角色 */
export function login(username: string, password: string) {
  return request.post<any, { token: string; userInfo: any }>('/auth/login', { username, password })
}

export function loginAsStudent(username: string, password: string) {
  return request.post<any, { token: string; userInfo: any }>('/auth/student/login', { username, password })
}

export function loginAsTeacher(username: string, password: string) {
  return request.post<any, { token: string; userInfo: any }>('/auth/teacher/login', { username, password })
}

export function loginAsAdmin(username: string, password: string) {
  return request.post<any, { token: string; userInfo: any }>('/auth/admin/login', { username, password })
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUser() {
  return request.get<any, any>('/auth/currentUser')
}

export function changePassword(oldPassword: string, newPassword: string) {
  return request.put('/auth/password', { oldPassword, newPassword })
}
