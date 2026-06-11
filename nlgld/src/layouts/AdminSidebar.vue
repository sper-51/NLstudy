<template>
  <aside
    :class="[
      'bg-[#1a2332] flex-shrink-0 flex flex-col transition-all duration-300 z-10',
      collapsed ? 'w-[70px]' : 'w-[240px]'
    ]"
  >
    <!-- Logo 区域 -->
    <div class="h-16 flex items-center px-4 border-b border-white/10 flex-shrink-0">
      <router-link to="/dashboard" class="flex items-center gap-3 overflow-hidden">
        <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-primary-400 to-primary-500 flex items-center justify-center flex-shrink-0 shadow-lg shadow-primary-900/30">
          <i class="ri-shield-keyhole-fill text-white text-lg"></i>
        </div>
        <transition name="fade">
          <div v-show="!collapsed" class="overflow-hidden whitespace-nowrap">
            <span class="text-base font-bold text-white tracking-tight">智考平台</span>
            <span class="block text-[11px] text-slate-400 -mt-0.5">管理后台</span>
          </div>
        </transition>
      </router-link>
    </div>

    <!-- 导航菜单 -->
    <nav class="flex-1 overflow-y-auto py-3 px-2 space-y-0.5">
      <!-- 仪表盘（多Tab入口） -->
      <router-link
        to="/dashboard"
        :class="navItemClass('/dashboard')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-dashboard-3-line text-lg flex-shrink-0" :class="isActive('/dashboard') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/dashboard') ? 'text-white' : 'text-slate-300'">全局控制台</span></transition>
      </router-link>

      <!-- 课程教学成果（Dashboard内Tab） -->
      <router-link
        to="/dashboard?tab=teaching"
        :class="navItemClass('/dashboard')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-bar-chart-grouped-line text-lg flex-shrink-0" :class="isActive('/dashboard') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/dashboard') ? 'text-white' : 'text-slate-300'">课程教学成果</span></transition>
      </router-link>

      <!-- 考试质量看板（Dashboard内Tab） -->
      <router-link
        to="/dashboard?tab=quality"
        :class="navItemClass('/dashboard')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-line-chart-line text-lg flex-shrink-0" :class="isActive('/dashboard') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/dashboard') ? 'text-white' : 'text-slate-300'">考试质量看板</span></transition>
      </router-link>

      <!-- 分隔线 -->
      <div v-show="!collapsed" class="my-3 mx-2 h-px bg-white/10"></div>

      <!-- 系统配置分组标题 -->
      <div v-show="!collapsed" class="px-3 py-1.5">
        <span class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">系统配置</span>
      </div>

      <router-link
        to="/semesters"
        :class="navItemClass('/semesters')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-calendar-check-line text-lg flex-shrink-0" :class="isActive('/semesters') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/semesters') ? 'text-white' : 'text-slate-300'">学期与规则配置</span></transition>
      </router-link>

      <!-- 用户账号管理（有子菜单） -->
      <div>
        <div
          :class="[
            'group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer',
            isActive('/users/') ? 'bg-white/10 text-white' : 'hover:bg-white/5 text-slate-300'
          ]"
          @click="toggleUsersMenu"
        >
          <i class="ri-user-settings-line text-lg flex-shrink-0" :class="isActive('/users/') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
          <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium flex-1 truncate">用户账号管理</span></transition>
          <transition name="fade">
            <i v-show="!collapsed" :class="['ri-arrow-down-s-line text-xs transition-transform', usersExpanded ? 'rotate-180' : '', 'text-slate-500']"></i>
          </transition>
        </div>
        <transition name="slide">
          <div v-show="(usersExpanded || collapsed)" :class="[collapsed ? '' : 'pl-10 mt-0.5 space-y-0.5']">
            <router-link
              to="/users/teachers"
              :class="subNavClass('/users/teachers')"
              class="block py-2 px-3 rounded-md text-sm transition-colors cursor-pointer"
            >
              <span v-show="!collapsed">教师账号</span>
              <i v-show="collapsed" class="ri-user-star-line text-base"></i>
            </router-link>
            <router-link
              to="/users/students"
              :class="subNavClass('/users/students')"
              class="block py-2 px-3 rounded-md text-sm transition-colors cursor-pointer"
            >
              <span v-show="!collapsed">学生账号</span>
              <i v-show="collapsed" class="ri-user-line text-base"></i>
            </router-link>
          </div>
        </transition>
      </div>

      <router-link
        to="/classes"
        :class="navItemClass('/classes')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-team-line text-lg flex-shrink-0" :class="isActive('/classes') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/classes') ? 'text-white' : 'text-slate-300'">班级管理中心</span></transition>
      </router-link>

      <!-- 分隔线 + 数据监控分组 -->
      <div v-show="!collapsed" class="my-3 mx-2 h-px bg-white/10"></div>
      <div v-show="!collapsed" class="px-3 py-1.5">
        <span class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">数据与监控</span>
      </div>

      <router-link
        to="/data/backup"
        :class="navItemClass('/data/backup')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-database-2-line text-lg flex-shrink-0" :class="isActive('/data/backup') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/data/backup') ? 'text-white' : 'text-slate-300'">数据备份运维</span></transition>
      </router-link>

      <router-link
        to="/data/logs"
        :class="navItemClass('/data/logs')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-file-list-3-line text-lg flex-shrink-0" :class="isActive('/data/logs') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/data/logs') ? 'text-white' : 'text-slate-300'">日志审计监控</span></transition>
      </router-link>

      <router-link
        to="/monitor/online"
        :class="navItemClass('/monitor/online')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-wifi-line text-lg flex-shrink-0" :class="isActive('/monitor/online') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/monitor/online') ? 'text-white' : 'text-slate-300'">在线用户追踪</span></transition>
      </router-link>

      <router-link
        to="/monitor/status"
        :class="navItemClass('/monitor/status')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-monitor-line text-lg flex-shrink-0" :class="isActive('/monitor/status') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/monitor/status') ? 'text-white' : 'text-slate-300'">系统资源监控</span></transition>
      </router-link>

      <router-link
        to="/monitor/audit"
        :class="navItemClass('/monitor/audit')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-shield-check-line text-lg flex-shrink-0" :class="isActive('/monitor/audit') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/monitor/audit') ? 'text-white' : 'text-slate-300'">数据选连</span></transition>
      </router-link>
    </nav>

    <!-- 底部：折叠按钮 + 用户信息 -->
    <div class="p-3 border-t border-white/10 flex-shrink-0">
      <!-- 折叠按钮 -->
      <button
        @click="toggleCollapse"
        class="w-full flex items-center justify-center gap-2 py-2 rounded-lg hover:bg-white/5 transition-colors cursor-pointer text-slate-400 hover:text-slate-300 mb-2"
      >
        <i :class="collapsed ? 'ri-arrow-right-s-line' : 'ri-arrow-left-s-line'" class="text-sm"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-xs">收起菜单</span></transition>
      </button>

      <!-- 当前用户 -->
      <div v-show="!collapsed" class="flex items-center gap-2 px-2 py-2 rounded-lg hover:bg-white/5 transition-colors cursor-pointer">
        <el-avatar :size="28" class="bg-primary-500/20 text-primary-300 text-xs font-semibold flex-shrink-0">
          管
        </el-avatar>
        <div class="min-w-0 flex-1">
          <div class="text-xs font-medium text-slate-300 truncate">超级管理员</div>
          <div class="text-[10px] text-slate-500 truncate">admin@example.com</div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const collapsed = ref(false)
const usersExpanded = ref(true)

function toggleCollapse() {
  collapsed.value = !collapsed.value
}

function toggleUsersMenu() {
  if (!collapsed.value) {
    usersExpanded.value = !usersExpanded.value
  }
}

function isActive(path: string): boolean {
  return route.path.startsWith(path)
}

function navItemClass(path: string): string {
  const active = isActive(path)
  return active
    ? 'bg-white/12 text-white shadow-sm shadow-black/10'
    : 'text-slate-400 hover:bg-white/5 hover:text-slate-200'
}

function subNavClass(path: string): string {
  return route.path === path
    ? 'text-white font-medium bg-white/8'
    : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active { transition: all 0.15s ease; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }

.slide-enter-active,
.slide-leave-active { transition: all 0.2s ease; overflow: hidden; }
.slide-enter-from,
.slide-leave-to { max-height: 0; opacity: 0; }
</style>
