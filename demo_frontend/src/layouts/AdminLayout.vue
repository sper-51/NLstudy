<template>
  <div class="h-screen flex bg-[#F5F7FA] overflow-hidden">
    <!-- 侧边栏 -->
    <AdminSidebar />

    <!-- 主内容区 -->
    <main class="flex-1 flex flex-col overflow-hidden">
      <!-- 顶部栏 -->
      <header class="h-14 bg-white border-b border-gray-100 flex items-center justify-between px-6 flex-shrink-0">
        <!-- 面包屑 / 标题 -->
        <div class="flex items-center gap-3">
          <button
            @click="toggleSidebar"
            class="lg:hidden w-8 h-8 flex items-center justify-center rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
          >
            <i class="ri-menu-line text-gray-500"></i>
          </button>
          <div class="flex items-center gap-2 text-sm text-gray-500">
            <i class="ri-home-4-line"></i>
            <span>管理后台</span>
            <template v-if="$route.meta.title">
              <i class="ri-arrow-right-s-line"></i>
              <span class="text-gray-800 font-medium">{{ $route.meta.title }}</span>
            </template>
          </div>
        </div>

        <!-- 右侧操作区 -->
        <div class="flex items-center gap-4">
          <!-- 全屏切换 -->
          <button
            @click="toggleFullscreen"
            class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
            title="全屏"
          >
            <i :class="isFullscreen ? 'ri-fullscreen-exit-line' : 'ri-fullscreen-line'" class="text-gray-500"></i>
          </button>

          <!-- 通知铃铛 -->
          <el-badge :value="3" :max="99" class="cursor-pointer">
            <div class="relative w-8 h-8 flex items-center justify-center rounded-lg hover:bg-gray-100 transition-colors">
              <i class="ri-notification-3-line text-gray-500"></i>
            </div>
          </el-badge>

          <!-- 用户信息 + 下拉 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="flex items-center gap-2 cursor-pointer hover:bg-gray-50 rounded-lg px-2 py-1 -mx-2 transition-colors">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" class="bg-primary-100 text-primary-600 font-semibold text-sm">
                {{ userStore.userInfo?.realName?.charAt(0) || '管' }}
              </el-avatar>
              <div class="hidden md:block">
                <div class="text-sm font-medium text-gray-700 leading-tight">{{ userStore.userInfo?.realName || '用户' }}</div>
                <div class="text-xs text-gray-400">{{ userStore.userInfo?.role === 'admin' ? '系统管理员' : userStore.userInfo?.role === 'teacher' ? '教师' : '用户' }}</div>
              </div>
              <i class="ri-arrow-down-s-line text-gray-400 text-sm hidden md:block"></i>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <i class="ri-user-settings-line mr-2"></i>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <i class="ri-lock-password-line mr-2"></i>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="ri-logout-box-r-line mr-2 text-danger-500"></i><span class="text-danger-500">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <div class="flex-1 overflow-y-auto p-6">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import AdminSidebar from './AdminSidebar.vue'

const router = useRouter()
const userStore = useUserStore()
const isFullscreen = ref(false)

function toggleSidebar() {
  // 移动端侧边栏切换逻辑
}

function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

async function handleCommand(command: string) {
  switch (command) {
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        userStore.logout()
        router.push('/login')
        ElMessage.success('已退出登录')
      } catch {}
      break
    case 'password':
      ElMessage.info('修改密码功能开发中')
      break
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
