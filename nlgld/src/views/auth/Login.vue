<template>
  <div class="min-h-screen flex">
    <!-- 左侧品牌区域 -->
    <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-slate-800 via-slate-900 to-slate-950 p-12 flex-col justify-between relative overflow-hidden">
      <!-- 装饰背景 -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-20 left-10 w-72 h-72 rounded-full bg-primary-400 blur-3xl"></div>
        <div class="absolute bottom-20 right-10 w-96 h-96 rounded-full bg-emerald-400 blur-3xl"></div>
      </div>

      <!-- Logo -->
      <div class="relative z-10">
        <div class="flex items-center gap-3">
          <div class="w-11 h-11 rounded-xl bg-white/15 backdrop-blur flex items-center justify-center">
            <i class="ri-shield-keyhole-fill text-white text-xl"></i>
          </div>
          <span class="text-white text-xl font-bold">智考平台</span>
        </div>
      </div>

      <!-- 中间内容 -->
      <div class="relative z-10 space-y-8">
        <div>
          <h1 class="text-4xl font-bold text-white leading-tight mb-4">管理后台</h1>
          <p class="text-lg text-slate-300 leading-relaxed max-w-md">
            系统稳定性管理、基础数据维护、用户权限管控，全方位保障平台安全运行。
          </p>
        </div>

        <div class="space-y-5">
          <div class="flex items-start gap-4 group">
            <div class="w-12 h-12 rounded-xl bg-white/10 backdrop-blur flex items-center justify-center flex-shrink-0 group-hover:bg-white/20 transition-colors">
              <i class="ri-admin-line text-white text-xl"></i>
            </div>
            <div>
              <h3 class="text-white font-semibold text-base mb-1">用户管理</h3>
              <p class="text-slate-400 text-sm">统一管理教师与学生账号，支持批量导入导出</p>
            </div>
          </div>

          <div class="flex items-start gap-4 group">
            <div class="w-12 h-12 rounded-xl bg-white/10 backdrop-blur flex items-center justify-center flex-shrink-0 group-hover:bg-white/20 transition-colors">
              <i class="ri-line-chart-line text-white text-xl"></i>
            </div>
            <div>
              <h3 class="text-white font-semibold text-base mb-1">系统监控</h3>
              <p class="text-slate-400 text-sm">实时监控系统状态，在线用户追踪，操作日志审计</p>
            </div>
          </div>

          <div class="flex items-start gap-4 group">
            <div class="w-12 h-12 rounded-xl bg-white/10 backdrop-blur flex items-center justify-center flex-shrink-0 group-hover:bg-white/20 transition-colors">
              <i class="ri-database-2-line text-white text-xl"></i>
            </div>
            <div>
              <h3 class="text-white font-semibold text-base mb-1">数据安全</h3>
              <p class="text-slate-400 text-sm">定期数据备份，完整日志记录，保障数据安全</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部版权 -->
      <div class="relative z-10 text-slate-500 text-sm">
        &copy; 2024 智考平台 &middot; 基于SpringBoot的智能在线考试系统
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="flex-1 flex items-center justify-center bg-gradient-to-br from-slate-50 via-white to-gray-50 p-6">
      <div class="w-full max-w-[420px]">
        <!-- 移动端Logo -->
        <div class="lg:hidden text-center mb-8">
          <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-slate-700 to-slate-900 shadow-lg mb-4">
            <i class="ri-shield-keyhole-fill text-white text-3xl"></i>
          </div>
          <h1 class="text-2xl font-bold text-gray-800">智考平台</h1>
          <p class="text-sm text-gray-500 mt-1">管理后台</p>
        </div>

        <!-- 登录卡片 -->
        <div class="bg-white rounded-2xl shadow-lg p-8 border border-gray-100/80">
          <h2 class="text-xl font-bold text-gray-800 mb-2 text-center">管理员登录</h2>
          <p class="text-sm text-gray-400 text-center mb-6">请使用管理员账号登录后台管理系统</p>

          <form @submit.prevent="handleLogin" class="space-y-5">
            <!-- 错误提示 -->
            <transition enter-active-class="transition-all duration-200 ease-out" enter-from-class="opacity-0 -translate-y-2" enter-to-class="opacity-100 translate-y-0" leave-active-class="transition-all duration-150 ease-in" leave-from-class="opacity-100 translate-y-0" leave-to-class="opacity-0 -translate-y-2">
              <div v-if="errorMsg" class="flex items-center gap-2 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-danger-600 text-sm">
                <i class="ri-error-warning-line flex-shrink-0"></i>
                <span>{{ errorMsg }}</span>
              </div>
            </transition>

            <!-- 用户名 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">管理员账号</label>
              <div class="relative">
                <i class="ri-admin-line absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"></i>
                <input
                  v-model="loginForm.username"
                  type="text"
                  placeholder="请输入管理员账号"
                  class="w-full pl-10 pr-4 py-2.5 border border-gray-200 rounded-lg focus:border-primary-400 focus:ring-2 focus:ring-primary-50 outline-none transition-all text-sm"
                />
              </div>
            </div>

            <!-- 密码 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">密码</label>
              <div class="relative">
                <i class="ri-lock-line absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"></i>
                <input
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="请输入密码"
                  class="w-full pl-10 pr-12 py-2.5 border border-gray-200 rounded-lg focus:border-primary-400 focus:ring-2 focus:ring-primary-50 outline-none transition-all text-sm"
                />
                <button type="button" @click="showPassword = !showPassword" class="absolute right-3.5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer">
                  <i :class="showPassword ? 'ri-eye-off-line' : 'ri-eye-line'"></i>
                </button>
              </div>
            </div>

            <!-- 记住我 & 忘记密码 -->
            <div class="flex items-center justify-between">
              <label class="flex items-center gap-2 cursor-pointer">
                <input v-model="rememberMe" type="checkbox" class="w-4 h-4 rounded border-gray-300 text-primary-500 focus:ring-primary-400" />
                <span class="text-sm text-gray-600">记住登录状态</span>
              </label>
              <a href="#" class="text-sm text-primary-500 hover:text-primary-600 transition-colors">忘记密码？</a>
            </div>

            <!-- 登录按钮 -->
            <button type="submit" :disabled="loading" class="w-full py-2.5 bg-gradient-to-r from-slate-800 to-slate-900 text-white font-medium rounded-lg hover:from-slate-900 hover:to-black transition-all disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer shadow-md shadow-slate-200/30">
              <span v-if="loading" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
              {{ loading ? '登录中...' : '登 录' }}
            </button>
          </form>

          <!-- 演示账号提示 -->
          <div class="mt-5 p-4 bg-slate-50 border border-slate-200 rounded-lg">
            <div class="flex items-start gap-2">
              <i class="ri-information-line text-slate-500 mt-0.5 flex-shrink-0"></i>
              <div class="text-xs text-gray-600 leading-relaxed space-y-1">
                <p><strong class="text-gray-700">演示账号：</strong></p>
                <p>管理员：admin / admin123</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部信息（移动端） -->
        <p class="lg:hidden text-center text-xs text-gray-400 mt-6">&copy; 2024 智考平台 &middot; 管理后台</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({ username: '', password: '' })
const showPassword = ref(false)
const rememberMe = ref(false)
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''
  if (!loginForm.value.username || !loginForm.value.password) {
    errorMsg.value = '请输入账号和密码'
    return
  }

  loading.value = true
  try {
    // TODO: 替换为真实API调用
    const mockToken = 'admin-token-' + Date.now()
    localStorage.setItem('token', mockToken)
    userStore.setToken(mockToken)
    userStore.setUserInfo({
      id: 1,
      username: loginForm.value.username,
      realName: '系统管理员',
      role: 'ADMIN',
      avatar: '',
    })
    setTimeout(() => router.push('/dashboard'), 300)
  } catch (error) {
    console.error('Login error:', error)
    errorMsg.value = '登录失败，请检查账号密码'
  } finally {
    loading.value = false
  }
}
</script>
