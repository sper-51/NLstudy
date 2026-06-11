import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { title: '登录', requiresAuth: false },
    },
    {
      path: '/',
      component: () => import('@/layouts/AdminLayout.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { title: '仪表盘' },
        },
        {
          path: 'semesters',
          name: 'Semesters',
          component: () => import('@/views/admin/SemesterList.vue'),
          meta: { title: '学期管理' },
        },
        {
          path: 'users/teachers',
          name: 'TeacherUsers',
          component: () => import('@/views/admin/UserList.vue'),
          meta: { title: '教师账号管理', role: 'teacher' },
        },
        {
          path: 'users/students',
          name: 'StudentUsers',
          component: () => import('@/views/admin/UserList.vue'),
          meta: { title: '学生账号管理', role: 'student' },
        },
        {
          path: 'classes',
          name: 'Classes',
          component: () => import('@/views/admin/ClassList.vue'),
          meta: { title: '班级管理' },
        },
        {
          path: 'data/backup',
          name: 'DataBackup',
          component: () => import('@/views/admin/DataManagement.vue'),
          meta: { title: '数据备份' },
        },
        {
          path: 'data/logs',
          name: 'SystemLogs',
          component: () => import('@/views/admin/SystemLogs.vue'),
          meta: { title: '系统日志' },
        },
        {
          path: 'monitor/online',
          name: 'OnlineUsers',
          component: () => import('@/views/admin/MonitorOnline.vue'),
          meta: { title: '在线用户监控' },
        },
        {
          path: 'monitor/status',
          name: 'SystemStatus',
          component: () => import('@/views/admin/MonitorStatus.vue'),
          meta: { title: '系统状态监控' },
        },
        {
          path: 'monitor/audit',
          name: 'AuditLogs',
          component: () => import('@/views/admin/AuditLogs.vue'),
          meta: { title: '操作日志审计' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFound.vue'),
    },
  ],
})

router.beforeEach((to, _from, next) => {
  NProgress.start()
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth !== false && !token) {
    next({ name: 'Login' })
  } else if (to.name === 'Login' && token) {
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

router.afterEach((to) => {
  NProgress.done()
  document.title = to.meta.title ? `${to.meta.title} - 智考平台管理后台` : '智考平台管理后台'
})

export default router
