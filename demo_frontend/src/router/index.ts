﻿﻿﻿import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'

// 布局组件
import StudentLayout from '@/layouts/StudentLayout.vue'
import TeacherLayout from '@/layouts/TeacherLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import ExamLayout from '@/layouts/ExamLayout.vue'

// 页面组件 - 学生端
import Login from '@/views/auth/Login.vue'
import Home from '@/views/student/Home.vue'
import Courses from '@/views/student/Courses.vue'
import CourseDetail from '@/views/student/CourseDetail.vue'
import Exam from '@/views/student/Exam.vue'
import Report from '@/views/student/Report.vue'
import WrongQuestions from '@/views/student/WrongQuestions.vue'
import Profile from '@/views/student/Profile.vue'
const WrongQuestionPractice = () => import('@/views/student/WrongQuestionPractice.vue')

// 页面组件 - 教师端（懒加载）
const Dashboard = () => import('@/views/teacher/Dashboard.vue')
const TeacherCourseList = () => import('@/views/teacher/TeacherCourseList.vue')
const TeacherCourseDetail = () => import('@/views/teacher/CourseDetail.vue')
const Questions = () => import('@/views/teacher/Questions.vue')
const QuestionsNew = () => import('@/views/teacher/QuestionsNew.vue')
const Papers = () => import('@/views/teacher/Papers.vue')
const ExamBuilder = () => import('@/views/teacher/ExamBuilder.vue')
const ExamSchedule = () => import('@/views/teacher/ExamSchedule.vue')
const ExamMonitor = () => import('@/views/teacher/ExamMonitor.vue')
const ExamDetail = () => import('@/views/teacher/ExamDetail.vue')
const Grading = () => import('@/views/teacher/Grading.vue')
const GradingHistory = () => import('@/views/teacher/GradingHistory.vue')
const Students = () => import('@/views/teacher/Students.vue')
const AnalyticsOverview = () => import('@/views/teacher/AnalyticsOverview.vue')
const AnalyticsDashboard = () => import('@/views/teacher/AnalyticsDashboard.vue')
const Appeals = () => import('@/views/teacher/Appeals.vue')
const MonitorList = () => import('@/views/teacher/MonitorList.vue')

// 页面组件 - 管理端（懒加载）
const AdminDashboard = () => import('@/views/admin/Dashboard.vue')
const SemesterList = () => import('@/views/admin/SemesterList.vue')
const UserList = () => import('@/views/admin/UserList.vue')
const ClassList = () => import('@/views/admin/ClassList.vue')
const DataManagement = () => import('@/views/admin/DataManagement.vue')
const SystemLogs = () => import('@/views/admin/SystemLogs.vue')
const MonitorOnline = () => import('@/views/admin/MonitorOnline.vue')
const MonitorStatus = () => import('@/views/admin/MonitorStatus.vue')
const AuditLogs = () => import('@/views/admin/AuditLogs.vue')

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', requiresAuth: false },
  },
  // ========== 学生端路由 ==========
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/',
    component: StudentLayout,
    meta: { requiresAuth: true, role: 'student' },
    children: [
      { path: 'home', name: 'Home', component: Home, meta: { title: '学习中心', icon: 'ri-dashboard-line' } },
      { path: 'courses', name: 'Courses', component: Courses, meta: { title: '我的课程', icon: 'ri-book-3-line' } },
      { path: 'courses/:courseId', name: 'CourseDetail', component: CourseDetail, meta: { title: '课程详情', hidden: true } },
      { path: 'report/:id?', name: 'Report', component: Report, meta: { title: '成绩报告', icon: 'ri-bar-chart-2-line' } },
      { path: 'wrong-questions', name: 'WrongQuestions', component: WrongQuestions, meta: { title: '错题本', icon: 'ri-book-open-line' } },
      { path: 'wrong-questions/practice', name: 'WrongQuestionPractice', component: WrongQuestionPractice, meta: { title: '错题刷题', hidden: true } },
      { path: 'profile', name: 'StudentProfile', component: Profile, meta: { title: '我的信息', icon: 'ri-user-settings-line' } },
    ],
  },
  // ========== 教师端路由 ==========
  {
    path: '/teacher',
    component: TeacherLayout,
    redirect: '/teacher/dashboard',
    meta: { requiresAuth: true, role: 'teacher' },
    children: [
      { path: 'dashboard', name: 'TeacherDashboard', component: Dashboard, meta: { title: '教师工作台', icon: 'ri-dashboard-line' } },
      { path: 'courses', name: 'TeacherCourseList', component: TeacherCourseList, meta: { title: '课程列表' } },
      { path: 'course/:courseId', name: 'TeacherCourseDetail', component: TeacherCourseDetail, meta: { title: '课程管理', hidden: true } },
      { path: 'students', name: 'TeacherStudents', component: Students, meta: { title: '学生选课管理', icon: 'ri-group-line' } },
      { path: 'questions', name: 'TeacherQuestions', component: Questions, meta: { title: '题目管理', icon: 'ri-file-list-3-line' } },
      { path: 'questions/new', name: 'TeacherQuestionsNew', component: QuestionsNew, meta: { title: '新增题目', hidden: true } },
      { path: 'questions/:id/edit', name: 'TeacherQuestionsEdit', component: QuestionsNew, meta: { title: '编辑题目', hidden: true } },
      { path: 'papers', name: 'TeacherPapers', component: Papers, meta: { title: '试卷列表', icon: 'ri-file-text-line' } },
      { path: 'papers/builder', name: 'TeacherExamBuilder', component: ExamBuilder, meta: { title: '组卷中心', hidden: true } },
      { path: 'exam-schedule', name: 'TeacherExamSchedule', component: ExamSchedule, meta: { title: '考试安排', icon: 'ri-calendar-check-line' } },
      { path: 'monitor', name: 'TeacherMonitorList', component: MonitorList, meta: { title: '考场监控', icon: 'ri-radar-line' } },
      { path: 'exam/:examId/monitor', name: 'TeacherExamMonitor', component: ExamMonitor, meta: { title: '实时监控', hidden: true } },
      { path: 'exam/:examId/detail', name: 'TeacherExamDetail', component: ExamDetail, meta: { title: '考试详情', hidden: true } },
      { path: 'grading', name: 'TeacherGrading', component: Grading, meta: { title: '待阅任务', icon: 'ri-edit-line' } },
      { path: 'grading/history', name: 'TeacherGradingHistory', component: GradingHistory, meta: { title: '批改历史', icon: 'ri-history-line' } },
      { path: 'appeals', name: 'TeacherAppeals', component: Appeals, meta: { title: '成绩复核', icon: 'ri-chat-check-line' } },
      { path: 'analytics', name: 'TeacherAnalytics', component: AnalyticsOverview, meta: { title: '成绩总览', icon: 'ri-bar-chart-box-line' } },
      { path: 'analytics/dashboard', name: 'TeacherAnalyticsDashboard', component: AnalyticsDashboard, meta: { title: '质量看板', hidden: true } },
    ],
  },
  // ========== 管理端路由 ==========
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin',
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      // 全局控制台 / 课程教学成果 / 考试质量看板（同一页面，通过query.tab切换）
      { path: '', name: 'AdminDashboard', component: AdminDashboard, meta: { title: '全局控制台', icon: 'ri-dashboard-3-line' } },
      // 系统配置
      { path: 'semesters', name: 'AdminSemesters', component: SemesterList, meta: { title: '学期与规则配置', icon: 'ri-calendar-check-line' } },
      // 用户账号管理
      { path: 'users/teachers', name: 'AdminTeachers', component: UserList, meta: { title: '教师账号管理', role: 'teacher' } },
      { path: 'users/students', name: 'AdminStudents', component: UserList, meta: { title: '学生账号管理', role: 'student' } },
      // 班级管理
      { path: 'classes', name: 'AdminClasses', component: ClassList, meta: { title: '班级管理中心', icon: 'ri-team-line' } },
      // 数据与监控
      { path: 'data/backup', name: 'AdminDataBackup', component: DataManagement, meta: { title: '数据备份运维', icon: 'ri-database-2-line' } },
      { path: 'data/logs', name: 'AdminSystemLogs', component: SystemLogs, meta: { title: '日志审计监控', icon: 'ri-file-list-3-line' } },
      { path: 'monitor/online', name: 'AdminMonitorOnline', component: MonitorOnline, meta: { title: '在线用户追踪', icon: 'ri-wifi-line' } },
      { path: 'monitor/status', name: 'AdminMonitorStatus', component: MonitorStatus, meta: { title: '系统资源监控', icon: 'ri-monitor-line' } },
      { path: 'monitor/audit', name: 'AdminAuditLogs', component: AuditLogs, meta: { title: '操作日志审计', icon: 'ri-shield-check-line' } },
    ],
  },
  // 考试页面（全屏无侧边栏，学生端和教师端共用）
  {
    path: '/exam/:id',
    component: ExamLayout,
    meta: { title: '在线考试', requiresAuth: true, fullscreen: true },
    children: [{ path: '', name: 'Exam', component: Exam }],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  },
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  NProgress.start()

  // 拦截无效的数字型路由参数（防止NaN/undefined传到后端）
  const courseId = to.params.courseId || to.params.id || to.params.examId
  if (courseId) {
    const num = Number(courseId)
    if (isNaN(num) || !isFinite(num)) {
      console.warn('[Router] 拦截到无效参数:', courseId, '路径:', to.path)
      // 根据角色重定向
      const role = localStorage.getItem('role')
      if (role === 'admin') next('/admin')
      else if (role === 'teacher') next('/teacher/dashboard')
      else next('/home')
      return
    }
  }

  const token = localStorage.getItem('token')
  // 根路径统一落到登录页，避免复用旧会话时直接进入学生页
  if (to.path === '/') {
    next({ name: 'Login' })
    return
  }

  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login' })
    return
  }
  next()
})

router.afterEach((to) => {
  NProgress.done()
  if (to.meta.title) document.title = `${to.meta.title} - 智考平台`
})

export default router

