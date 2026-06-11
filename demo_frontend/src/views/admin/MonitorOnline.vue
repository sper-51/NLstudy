<template>
  <div class="min-h-screen bg-[#F5F7FA] p-6">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-primary-500/10 flex items-center justify-center">
          <i class="ri-radar-line text-primary-500 text-xl"></i>
        </div>
        <h1 class="text-xl font-bold text-gray-800">在线会话追踪</h1>
        <span class="relative inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200">
          <span class="relative flex h-2 w-2">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
          </span>
          <span class="text-sm font-semibold text-emerald-700">近 5 分钟活跃: {{ onlineUsers.length }} 人</span>
        </span>
      </div>
      <button @click="handleRefresh" :disabled="refreshing" class="inline-flex items-center gap-2 px-4 py-2 bg-white rounded-lg shadow-sm border border-gray-200 hover:border-primary-300 hover:text-primary-600 transition-all cursor-pointer disabled:opacity-50">
        <i :class="[refreshing ? 'ri-loader-4-line animate-spin' : 'ri-refresh-line']"></i>
        <span class="text-sm">{{ refreshing ? '刷新中...' : '刷新' }}</span>
      </button>
    </div>

    <!-- 统计概览卡片行 -->
    <div class="grid grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-xl shadow-sm p-5 border-l-4 border-blue-500 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 mb-1">活跃会话总数</p>
            <p class="text-3xl font-bold text-blue-600">{{ onlineUsers.length }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-blue-50 flex items-center justify-center">
            <i class="ri-group-line text-blue-500 text-2xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-5 border-l-4 border-emerald-500 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 mb-1">活跃教师数</p>
            <p class="text-3xl font-bold text-emerald-600">{{ teacherCount }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-emerald-50 flex items-center justify-center">
            <i class="ri-user-star-line text-emerald-500 text-2xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-5 border-l-4 border-purple-500 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 mb-1">活跃学生数</p>
            <p class="text-3xl font-bold text-purple-600">{{ studentCount }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-purple-50 flex items-center justify-center">
            <i class="ri-user-line text-purple-500 text-2xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-5 border-l-4 border-orange-500 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 mb-1">今日登录次数</p>
            <p class="text-3xl font-bold text-orange-600">{{ todayLoginCount }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-orange-50 flex items-center justify-center">
            <i class="ri-login-circle-line text-orange-500 text-2xl"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- 在线用户列表 -->
    <div class="bg-white rounded-xl shadow-sm p-5">
      <div class="flex items-center gap-2 mb-4 rounded-lg bg-blue-50 px-3 py-2 text-sm text-blue-700">
        <i class="ri-information-line"></i>
        <span>统计口径：登录后创建会话，访问任意接口会刷新最后活跃时间；超过 5 分钟无请求视为离线。</span>
      </div>
      <!-- 工具栏 -->
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-3">
          <el-input v-model="searchKeyword" placeholder="搜索用户名/账号" clearable prefix-icon="Search" style="width: 220px" />
          <el-select v-model="roleFilter" placeholder="角色筛选" style="width: 140px">
            <el-option label="全部" value="" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
          <el-select v-model="sortField" placeholder="排序方式" style="width: 150px">
            <el-option label="登录时间降序" value="loginTimeDesc" />
            <el-option label="登录时间升序" value="loginTimeAsc" />
            <el-option label="活跃时间降序" value="activeTimeDesc" />
          </el-select>
        </div>
        <span class="text-sm text-gray-400">共 {{ filteredUsers.length }} 条记录</span>
      </div>

      <!-- 表格 -->
      <el-table :data="filteredUsers" stripe style="width: 100%">
        <el-table-column label="头像" width="70" align="center">
          <template #default="{ row }">
            <el-avatar :size="36" :style="{ backgroundColor: getAvatarColor(row.username) }">
              {{ row.realName?.charAt(0) || row.username.charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名/账号" min-width="130" />
        <el-table-column prop="realName" label="真实姓名" min-width="110" />
        <el-table-column label="角色" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'TEACHER' ? 'warning' : 'info'" size="small">
              {{ row.role === 'TEACHER' ? '教师' : '学生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="登录时间" width="165">
          <template #default="{ row }">
            {{ formatTime(row.loginTime) }}
          </template>
        </el-table-column>
        <el-table-column label="最后活跃" width="150">
          <template #default="{ row }">
            <span class="text-gray-600">{{ formatTime(row.lastActiveTime) }}</span>
            <br />
            <span class="text-xs text-gray-400">{{ getRelativeTime(row.lastActiveTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="145" />
        <el-table-column label="状态" width="80" align="center">
          <template #default>
            <span class="inline-block w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
            <span class="ml-1.5 text-xs text-emerald-600">在线</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-popconfirm title="确定要将该用户标记为离线吗？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleForceLogout(row)">
              <template #reference>
                <el-button type="danger" size="small" link>标记离线</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 在线趋势图 -->
    <div class="bg-white rounded-xl shadow-sm p-5 mt-6">
      <div class="flex items-center gap-2 mb-4">
        <i class="ri-line-chart-line text-primary-500"></i>
        <h2 class="text-base font-semibold text-gray-800">登录趋势图（最近24小时）</h2>
      </div>
      <div ref="trendChartRef" style="height: 320px; width: 100%"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import { getOnlineUsers, getOnlineTrend, forceLogoutUser, unwrapList } from '@/api/admin'

dayjs.extend(relativeTime)

// ==================== 类型定义 ====================
interface OnlineUser {
  id: number
  username: string
  realName: string
  role: 'TEACHER' | 'STUDENT'
  loginTime: string
  lastActiveTime: string
  ipAddress: string
}

// ==================== 响应式数据 ====================
const searchKeyword = ref('')
const roleFilter = ref('')
const sortField = ref('loginTimeDesc')
const refreshing = ref(false)
const trendChartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null
let refreshTimer: number | null = null

// 在线用户列表（从后端API获取）
const onlineUsers = ref<OnlineUser[]>([])
const todayLoginCount = ref(0)

// 加载在线用户数据
async function fetchOnlineUsers() {
  try {
    refreshing.value = true
    const res: any = await getOnlineUsers()
    onlineUsers.value = unwrapList(res).map((item: any) => ({
      id: item.id,
      username: item.username || '',
      realName: item.realName || item.username || '',
      role: normalizeRole(item.role),
      loginTime: item.loginTime || item.createTime || new Date().toISOString(),
      lastActiveTime: item.lastActiveTime || item.updateTime || item.loginTime || new Date().toISOString(),
      ipAddress: item.ipAddress || item.ip || '—',
    }))
    todayLoginCount.value = res?.todayLoginCount || res?.loginCount || 0
  } catch (error) {
    console.error('获取在线用户列表失败:', error)
    ElMessage.error('获取在线用户列表失败')
  } finally {
    refreshing.value = false
  }
}

// ==================== 计算属性 ====================
const teacherCount = computed(() => onlineUsers.value.filter(u => u.role === 'TEACHER').length)
const studentCount = computed(() => onlineUsers.value.filter(u => u.role === 'STUDENT').length)

const filteredUsers = computed(() => {
  let list = [...onlineUsers.value]
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(u => u.username.toLowerCase().includes(kw) || u.realName.includes(kw))
  }
  if (roleFilter.value) {
    list = list.filter(u => u.role === roleFilter.value)
  }
  // 排序
  switch (sortField.value) {
    case 'loginTimeDesc':
      list.sort((a, b) => new Date(b.loginTime).getTime() - new Date(a.loginTime).getTime())
      break
    case 'loginTimeAsc':
      list.sort((a, b) => new Date(a.loginTime).getTime() - new Date(b.loginTime).getTime())
      break
    case 'activeTimeDesc':
      list.sort((a, b) => new Date(b.lastActiveTime).getTime() - new Date(a.lastActiveTime).getTime())
      break
  }
  return list
})

// ==================== 方法 ====================
function formatTime(time: string): string {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

function getRelativeTime(time: string): string {
  return dayjs(time).fromNow()
}

function getAvatarColor(name: string): string {
  const colors = ['#3b82f6', '#10b981', '#8b5cf6', '#f59e0b', '#ef4444', '#ec4899', '#06b6d4', '#84cc16']
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return colors[Math.abs(hash) % colors.length]
}

function handleRefresh() {
  fetchOnlineUsers().then(() => initTrendChart())
}

async function handleForceLogout(user: OnlineUser) {
  try {
    await forceLogoutUser(user.id)
    ElMessage.success(`已将 ${user.realName}(${user.username}) 标记为离线`)
    // 重新获取列表以更新状态
    await fetchOnlineUsers()
  } catch (error) {
    console.error('强制下线失败:', error)
    ElMessage.error('强制下线失败，请重试')
  }
}

// ==================== Echarts 图表 ====================
function normalizeRole(role: string): 'TEACHER' | 'STUDENT' {
  return String(role || '').toLowerCase() === 'teacher' ? 'TEACHER' : 'STUDENT'
}

async function initTrendChart() {
  if (!trendChartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(trendChartRef.value)
  }

  let hours: string[] = []
  let teacherData: number[] = []
  let studentData: number[] = []

  try {
    const trend: any = await getOnlineTrend({ hours: 24 })
    hours = trend?.hours || []
    teacherData = trend?.teacherData || []
    studentData = trend?.studentData || []
  } catch (error) {
    console.error('获取在线趋势失败:', error)
  }

  if (!hours.length) {
    for (let i = 23; i >= 0; i--) {
      hours.push(dayjs().subtract(i, 'hour').format('HH:00'))
      teacherData.push(0)
      studentData.push(0)
    }
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
    },
    legend: {
      data: ['教师登录数', '学生登录数'],
      top: 0,
    },
    grid: {
      left: '3%', right: '4%', bottom: '3%', containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: hours,
      axisLabel: { fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      name: '人数',
      minInterval: 1,
    },
    series: [
      {
        name: '教师登录数',
        type: 'line',
        smooth: true,
        data: teacherData,
        lineStyle: { color: '#f59e0b', width: 2 },
        itemStyle: { color: '#f59e0b' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245,158,11,0.25)' },
            { offset: 1, color: 'rgba(245,158,11,0.02)' },
          ]),
        },
      },
      {
        name: '学生登录数',
        type: 'line',
        smooth: true,
        data: studentData,
        lineStyle: { color: '#3b82f6', width: 2 },
        itemStyle: { color: '#3b82f6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59,130,246,0.25)' },
            { offset: 1, color: 'rgba(59,130,246,0.02)' },
          ]),
        },
      },
    ],
  }

  chartInstance.setOption(option)
}

function handleResize() {
  chartInstance?.resize()
}

// ==================== 生命周期 ====================
onMounted(() => {
  fetchOnlineUsers()
  nextTick(() => {
    initTrendChart()
  })
  refreshTimer = window.setInterval(fetchOnlineUsers, 60000)
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (refreshTimer) window.clearInterval(refreshTimer)
  chartInstance?.dispose()
})
</script>
