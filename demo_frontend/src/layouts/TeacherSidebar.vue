<template>
  <aside class="w-[230px] h-screen bg-white flex flex-col flex-shrink-0 shadow-sm z-10">
    <!-- Logo 区域 -->
    <div class="h-16 flex items-center px-5 border-b border-gray-100">
      <div class="flex items-center gap-2.5">
        <div class="w-8 h-8 rounded-lg bg-primary-500 flex items-center justify-center">
          <i class="ri-graduation-cap-fill text-white text-lg"></i>
        </div>
        <span class="font-semibold text-base text-gray-800 whitespace-nowrap">智考平台</span>
        <span class="text-xs font-medium px-1.5 py-0.5 rounded bg-primary-50 text-primary-600">教师端</span>
      </div>
    </div>

    <!-- 导航菜单 -->
    <nav class="flex-1 px-3 py-2 overflow-y-auto">
      <div class="text-xs text-gray-400 font-medium px-3 mb-2 mt-1">主菜单</div>

      <template v-for="item in menuItems" :key="item.label">
        <!-- 有子菜单的项 -->
        <div v-if="item.children" class="mb-1">
          <!-- 父级菜单标题（可展开/收起） -->
          <button
            @click="toggleExpand(item.label)"
            :class="[
              'flex items-center justify-between w-full px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 cursor-pointer',
              isGroupActive(item)
                ? 'bg-primary-50 text-primary-600'
                : 'text-gray-600 hover:bg-gray-50 hover:text-gray-800'
            ]"
          >
            <div class="flex items-center gap-3">
              <i :class="[item.icon, 'text-lg w-5 h-5 flex items-center justify-center']"></i>
              <span>{{ item.label }}</span>
            </div>
            <i
              :class="[
                'ri-arrow-down-s-line text-xs transition-transform duration-200',
                expandedKeys.includes(item.label) ? 'rotate-180' : ''
              ]"
            ></i>
          </button>

          <!-- 子菜单 -->
          <transition
            enter-active-class="transition-all duration-200 ease-out"
            enter-from-class="opacity-0 -translate-y-1"
            enter-to-class="opacity-100 translate-y-0"
            leave-active-class="transition-all duration-150 ease-in"
            leave-from-class="opacity-100 translate-y-0"
            leave-to-class="opacity-0 -translate-y-1"
          >
            <div v-show="expandedKeys.includes(item.label)" class="pl-4 mt-0.5 space-y-0.5">
              <router-link
                v-for="child in item.children"
                :key="child.path"
                :to="child.path"
                :class="[
                  'flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-all duration-200 cursor-pointer',
                  isActive(child.path, child.matchPaths)
                    ? 'bg-primary-100 text-primary-700 font-medium'
                    : 'text-gray-500 hover:bg-gray-50 hover:text-gray-700'
                ]"
              >
                <i :class="[child.icon || 'ri-circle-fill', 'text-[6px]']"></i>
                {{ child.label }}
              </router-link>
            </div>
          </transition>
        </div>

        <!-- 无子菜单的项 -->
        <router-link
          v-else
          :to="item.path!"
          :class="[
            'flex items-center gap-3 px-3 py-2.5 rounded-lg mb-1 text-sm font-medium transition-all duration-200 cursor-pointer whitespace-nowrap',
            isActive(item.path!)
              ? 'bg-primary-50 text-primary-600'
              : 'text-gray-600 hover:bg-gray-50 hover:text-gray-800'
          ]"
        >
          <i :class="[item.icon, 'text-lg w-5 h-5 flex items-center justify-center']"></i>
          {{ item.label }}
        </router-link>
      </template>
    </nav>

    <!-- 底部用户信息区 -->
    <div class="p-3 mx-2 mb-3">
      <div class="flex items-center gap-3 px-3 py-3 rounded-xl bg-gray-50">
        <img
          v-if="userStore.userInfo?.avatar"
          :src="userStore.userInfo.avatar"
          alt="avatar"
          class="w-9 h-9 rounded-full object-cover flex-shrink-0"
        />
        <div v-else class="w-9 h-9 rounded-full bg-primary-100 flex items-center justify-center flex-shrink-0">
          <i class="ri-user-line text-primary-500"></i>
        </div>
        <div class="min-w-0 flex-1">
          <div class="text-sm font-medium text-gray-800 truncate">
            {{ userStore.userInfo?.realName || '未登录' }}
          </div>
          <div class="text-xs text-gray-400 truncate">{{ userTitle }}</div>
        </div>
        <button
          @click="handleLogout"
          class="w-8 h-8 flex items-center justify-center rounded-lg text-gray-400 hover:text-danger-500 hover:bg-red-50 transition-colors cursor-pointer flex-shrink-0"
          title="退出登录"
        >
          <i class="ri-logout-box-r-line"></i>
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 菜单配置
interface ChildItem {
  path: string
  label: string
  icon?: string
  matchPaths?: string[]
}

interface MenuItem {
  label: string
  icon: string
  path?: string
  children?: ChildItem[]
}

const menuItems: MenuItem[] = [
  {
    label: '工作台',
    icon: 'ri-dashboard-line',
    path: '/teacher/dashboard',
  },
  {
    label: '课程教学',
    icon: 'ri-book-open-line',
    children: [
      { path: '/teacher/courses', matchPaths: ['/teacher/courses', '/teacher/course'], label: '课程列表', icon: 'ri-list-check' },
      { path: '/teacher/students', label: '学生选课', icon: 'ri-user-star-line' },
    ],
  },
  {
    label: '资源题库',
    icon: 'ri-database-2-line',
    children: [
      { path: '/teacher/questions', label: '题目管理', icon: 'ri-file-list-3-line' },
      { path: '/teacher/questions/new', label: '新增题目', icon: 'ri-add-circle-line' },
    ],
  },
  {
    label: '试卷管理',
    icon: 'ri-file-text-line',
    children: [
      { path: '/teacher/papers', label: '试卷列表', icon: 'ri-file-list-2-line' },
      { path: '/teacher/papers/builder', label: '组卷中心', icon: 'ri-magic-line' },
    ],
  },
  {
    label: '考务中心',
    icon: 'ri-calendar-check-line',
    children: [
      { path: '/teacher/exam-schedule', label: '考试安排', icon: 'ri-calendar-event-line' },
      { path: '/teacher/monitor', label: '考场监控', icon: 'ri-eye-line' },
    ],
  },
  {
    label: '阅卷评分',
    icon: 'ri-edit-box-line',
    children: [
      { path: '/teacher/grading', label: '待阅任务', icon: 'ri-time-line' },
      { path: '/teacher/appeals', label: '成绩复核', icon: 'ri-feedback-line' },
      { path: '/teacher/grading/history', label: '批改历史', icon: 'ri-history-line' },
    ],
  },
  {
    label: '数据分析',
    icon: 'ri-bar-chart-box-line',
    children: [
      { path: '/teacher/analytics', label: '成绩总览', icon: 'ri-pie-chart-line' },
      { path: '/teacher/analytics/dashboard', label: '质量看板', icon: 'ri-dashboard-3-line' },
    ],
  },
]

// 展开状态
const expandedKeys = ref<string[]>([])

// 根据当前路由自动展开所属菜单分组
function syncExpandedWithRoute() {
  const activeGroup = menuItems.find(item => item.children?.some(child => isActive(child.path, child.matchPaths)))
  expandedKeys.value = activeGroup ? [activeGroup.label] : []
}
syncExpandedWithRoute()

watch(
  () => route.path,
  () => {
    syncExpandedWithRoute()
  }
)

// 切换展开/收起
function toggleExpand(label: string) {
  const index = expandedKeys.value.indexOf(label)
  if (index > -1) {
    expandedKeys.value.splice(index, 1)
  } else {
    expandedKeys.value.push(label)
  }
}

// 判断路径是否激活
function isActive(path: string, matchPaths?: string[]): boolean {
  if (matchPaths?.length) {
    return matchPaths.some(matchPath => route.path === matchPath || route.path.startsWith(matchPath + '/'))
  }
  return route.path === path || route.path.startsWith(path + '/')
}

// 判断分组是否有子项被激活
function isGroupActive(item: MenuItem): boolean {
  if (!item.children) return false
  return item.children.some(child => isActive(child.path, child.matchPaths))
}

// 用户职称
const userTitle = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 'teacher') return '教师'
  if (role === 'admin') return '管理员'
  return '教师'
})

// 退出登录
function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>
