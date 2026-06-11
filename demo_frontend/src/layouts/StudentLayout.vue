<template>
  <div class="h-screen flex bg-[#F5F7FA]">
    <!-- 侧边栏 -->
    <aside class="w-[220px] h-screen bg-white flex flex-col flex-shrink-0 shadow-sm z-10">
      <!-- Logo 区域 -->
      <div class="h-16 flex items-center px-5 border-b border-gray-100">
        <div class="flex items-center gap-2.5">
          <div class="w-8 h-8 rounded-lg bg-primary-500 flex items-center justify-center">
            <i class="ri-graduation-cap-fill text-white text-lg"></i>
          </div>
          <span class="font-semibold text-base text-gray-800 whitespace-nowrap">智考平台</span>
        </div>
      </div>

      <!-- 导航菜单 -->
      <nav class="flex-1 px-3 py-2 overflow-y-auto">
        <div class="text-xs text-gray-400 font-medium px-3 mb-2 mt-1">主菜单</div>
        <template v-for="item in menuItems" :key="item.path">
          <router-link
            :to="item.path"
            :class="[
              'flex items-center gap-3 px-3 py-2.5 rounded-lg mb-1 text-sm font-medium transition-all duration-200 cursor-pointer whitespace-nowrap',
              isActive(item.path)
                ? 'bg-primary-50 text-primary-600'
                : 'text-gray-600 hover:bg-gray-50 hover:text-gray-800'
            ]"
          >
            <i :class="[item.icon, 'text-lg w-5 h-5 flex items-center justify-center']"></i>
            {{ item.label }}
          </router-link>
        </template>
      </nav>

      <!-- 底部用户信息 -->
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
            <div class="text-xs text-gray-400 truncate">{{ userStore.userInfo?.username || '' }}</div>
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

    <!-- 主内容区域 -->
    <main class="flex-1 overflow-hidden">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

// 菜单配置（可扩展：根据角色动态加载）
const menuItems = [
  { path: '/home', icon: 'ri-dashboard-line', label: '学习中心' },
  { path: '/courses', icon: 'ri-book-3-line', label: '我的课程' },
  { path: '/wrong-questions', icon: 'ri-book-open-line', label: '错题本' },
  { path: '/report', icon: 'ri-bar-chart-2-line', label: '成绩报告' },
  { path: '/profile', icon: 'ri-user-settings-line', label: '我的信息' },
]

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

// 退出登录
async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch {
    // 取消操作
  }
}
</script>
