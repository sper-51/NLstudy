import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, TeacherInfo } from '@/api/types'

export const useUserStore = defineStore('user', () => {
  // 状态 - 从 localStorage 恢复用户信息
  const storedUserInfo = localStorage.getItem('userInfo')
  const userInfo = ref<UserInfo | null>(storedUserInfo ? JSON.parse(storedUserInfo) : null)
  const teacherInfo = ref<TeacherInfo | null>(null)
  const token = ref<string>(localStorage.getItem('token') || '')
  const isLoggedIn = ref<boolean>(!!token.value)

  // 角色判断 computed
  const isTeacher = computed(() => userInfo.value?.role === 'teacher')
  const isStudent = computed(() => userInfo.value?.role === 'student')
  const isAdmin = computed(() => userInfo.value?.role === 'admin')

  // 设置用户信息（根据角色存储不同信息）
  function setUserInfo(info: UserInfo) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
    localStorage.setItem('userId', String(info.id))
    localStorage.setItem('role', info.role)
    if (info.role === 'teacher') {
      // 教师登录时同步设置教师信息
      teacherInfo.value = {
        id: info.id,
        userId: info.id,
        name: info.realName || info.username,
        title: '',
        avatar: info.avatar,
      }
    } else {
      teacherInfo.value = null
    }
  }

  // 设置教师信息
  function setTeacherInfo(info: TeacherInfo) {
    teacherInfo.value = info
  }

  // 设置 Token
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
    isLoggedIn.value = true
  }

  function clearSession() {
    userInfo.value = null
    teacherInfo.value = null
    token.value = ''
    isLoggedIn.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
    localStorage.removeItem('userInfo')
  }

  // 登出 - 清空所有状态
  function logout() {
    clearSession()
  }

  return {
    userInfo,
    teacherInfo,
    token,
    isLoggedIn,
    isTeacher,
    isStudent,
    isAdmin,
    setUserInfo,
    setTeacherInfo,
    setToken,
    clearSession,
    logout,
  }
})
