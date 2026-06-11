<template>
  <aside
    :class="[
      'bg-[#1a2332] flex-shrink-0 flex flex-col transition-all duration-300 z-10',
      collapsed ? 'w-[70px]' : 'w-[240px]'
    ]"
  >
    <!-- Logo 区域 -->
    <div class="h-16 flex items-center px-4 border-b border-white/10 flex-shrink-0">
      <router-link to="/admin" class="flex items-center gap-3 overflow-hidden">
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
    <nav class="admin-nav flex-1 overflow-y-auto py-3 px-2 space-y-0.5">
      <!-- 仪表盘（多Tab入口） -->
      <router-link
        to="/admin"
        :class="dashboardItemClass('global')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-dashboard-3-line text-lg flex-shrink-0" :class="isDashboardTab('global') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isDashboardTab('global') ? 'text-white' : 'text-slate-300'">全局控制台</span></transition>
      </router-link>

      <router-link
        to="/admin?tab=teaching"
        :class="dashboardItemClass('teaching')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-bar-chart-grouped-line text-lg flex-shrink-0" :class="isDashboardTab('teaching') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isDashboardTab('teaching') ? 'text-white' : 'text-slate-300'">课程教学成果</span></transition>
      </router-link>

      <router-link
        to="/admin?tab=quality"
        :class="dashboardItemClass('quality')"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer"
      >
        <i class="ri-line-chart-line text-lg flex-shrink-0" :class="isDashboardTab('quality') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isDashboardTab('quality') ? 'text-white' : 'text-slate-300'">考试质量看板</span></transition>
      </router-link>

      <!-- 分隔线 -->
      <div v-show="!collapsed" class="my-3 mx-2 h-px bg-white/10"></div>

      <!-- 系统配置分组标题 -->
      <div v-show="!collapsed" class="px-3 py-1.5">
        <span class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">系统配置</span>
      </div>

      <router-link to="/admin/semesters" :class="navItemClass('/semesters')" class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer">
        <i class="ri-calendar-check-line text-lg flex-shrink-0" :class="isActive('/semesters') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/semesters') ? 'text-white' : 'text-slate-300'">学期与规则配置</span></transition>
      </router-link>

      <!-- 用户账号管理子菜单 -->
      <div>
        <div :class="[ 'group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer', isActive('/users/') ? 'bg-white/10 text-white' : 'hover:bg-white/5 text-slate-300']" @click="toggleUsersMenu">
          <i class="ri-user-settings-line text-lg flex-shrink-0" :class="isActive('/users/') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
          <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium flex-1 truncate">用户账号管理</span></transition>
          <transition name="fade"><i v-show="!collapsed" :class="['ri-arrow-down-s-line text-xs transition-transform', usersExpanded ? 'rotate-180' : '', 'text-slate-500']"></i></transition>
        </div>
        <transition name="slide">
          <div v-show="(usersExpanded || collapsed)" :class="[collapsed ? '' : 'pl-10 mt-0.5 space-y-0.5']">
            <router-link to="/admin/users/teachers" :class="subNavClass('/users/teachers')" class="block py-2 px-3 rounded-md text-sm transition-colors cursor-pointer">
              <span v-show="!collapsed">教师账号</span><i v-show="collapsed" class="ri-user-star-line text-base"></i>
            </router-link>
            <router-link to="/admin/users/students" :class="subNavClass('/users/students')" class="block py-2 px-3 rounded-md text-sm transition-colors cursor-pointer">
              <span v-show="!collapsed">学生账号</span><i v-show="collapsed" class="ri-user-line text-base"></i>
            </router-link>
          </div>
        </transition>
      </div>

      <router-link to="/admin/classes" :class="navItemClass('/classes')" class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer">
        <i class="ri-team-line text-lg flex-shrink-0" :class="isActive('/classes') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/classes') ? 'text-white' : 'text-slate-300'">班级管理中心</span></transition>
      </router-link>

      <!-- 分隔线 + 数据监控分组 -->
      <div v-show="!collapsed" class="my-3 mx-2 h-px bg-white/10"></div>
      <div v-show="!collapsed" class="px-3 py-1.5">
        <span class="text-[11px] font-semibold text-slate-500 uppercase tracking-wider">数据与监控</span>
      </div>

      <router-link to="/admin/data/backup" :class="navItemClass('/data/backup')" class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer">
        <i class="ri-database-2-line text-lg flex-shrink-0" :class="isActive('/data/backup') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/data/backup') ? 'text-white' : 'text-slate-300'">数据备份运维</span></transition>
      </router-link>

      <router-link to="/admin/data/logs" :class="navItemClass('/data/logs')" class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer">
        <i class="ri-file-list-3-line text-lg flex-shrink-0" :class="isActive('/data/logs') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/data/logs') ? 'text-white' : 'text-slate-300'">日志审计监控</span></transition>
      </router-link>

      <router-link to="/admin/monitor/online" :class="navItemClass('/monitor/online')" class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer">
        <i class="ri-wifi-line text-lg flex-shrink-0" :class="isActive('/monitor/online') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/monitor/online') ? 'text-white' : 'text-slate-300'">在线用户追踪</span></transition>
      </router-link>

      <router-link to="/admin/monitor/audit" :class="navItemClass('/monitor/audit')" class="group flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 cursor-pointer">
        <i class="ri-shield-check-line text-lg flex-shrink-0" :class="isActive('/monitor/audit') ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-sm font-medium truncate" :class="isActive('/monitor/audit') ? 'text-white' : 'text-slate-300'">操作日志审计</span></transition>
      </router-link>
    </nav>

    <!-- 底部：折叠按钮 + 用户信息 -->
    <div class="p-3 border-t border-white/10 flex-shrink-0">
      <button @click="toggleCollapse" class="w-full flex items-center justify-center gap-2 py-2 rounded-lg hover:bg-white/5 transition-colors cursor-pointer text-slate-400 hover:text-slate-300 mb-2">
        <i :class="collapsed ? 'ri-arrow-right-s-line' : 'ri-arrow-left-s-line'" class="text-sm"></i>
        <transition name="fade"><span v-show="!collapsed" class="text-xs">收起菜单</span></transition>
      </button>
      <div v-show="!collapsed" class="flex items-center gap-2 px-2 py-2 rounded-lg hover:bg-white/5 transition-colors cursor-pointer">
        <el-avatar :size="28" class="bg-primary-500/20 text-primary-300 text-xs font-semibold flex-shrink-0">
          {{ userStore.userInfo?.realName?.charAt(0) || '管' }}
        </el-avatar>
        <div class="min-w-0 flex-1">
          <div class="text-xs font-medium text-slate-300 truncate">{{ userStore.userInfo?.realName || '管理员' }}</div>
          <div class="text-[10px] text-slate-500 truncate">{{ userStore.userInfo?.username || userStore.userInfo?.email || 'admin' }}</div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)
const usersExpanded = ref(true)

function toggleCollapse() { collapsed.value = !collapsed.value }
function toggleUsersMenu() { if (!collapsed.value) usersExpanded.value = !usersExpanded.value }

function isActive(path: string): boolean { return route.path.startsWith(path) }

function isDashboardTab(tab: 'global' | 'teaching' | 'quality'): boolean {
  if (route.path !== '/admin') return false
  const currentTab = typeof route.query.tab === 'string' ? route.query.tab : 'global'
  return tab === 'global' ? !route.query.tab : currentTab === tab
}

function dashboardItemClass(tab: 'global' | 'teaching' | 'quality'): string {
  return isDashboardTab(tab) ? 'bg-white/12 text-white shadow-sm shadow-black/10' : 'text-slate-400 hover:bg-white/5 hover:text-slate-200'
}

function navItemClass(path: string): string {
  return isActive(path) ? 'bg-white/12 text-white shadow-sm shadow-black/10' : 'text-slate-400 hover:bg-white/5 hover:text-slate-200'
}

function subNavClass(path: string): string {
  return route.path === path ? 'text-white font-medium bg-white/8' : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
}
</script>

<style scoped>
.fade-enter-active,.fade-leave-active{transition:all .15s ease}.fade-enter-from,.fade-leave-to{opacity:0}
.slide-enter-active,.slide-leave-active{transition:all .2s ease;overflow:hidden}.slide-enter-from,.slide-leave-to{max-height:0;opacity:0}
.admin-nav {
  scrollbar-width: none;
}
.admin-nav::-webkit-scrollbar {
  width: 0;
  height: 0;
}
</style>
