<template>
  <div class="min-h-screen bg-[#F5F7FA] p-6">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-primary-500/10 flex items-center justify-center">
          <i class="ri-radar-line text-primary-500 text-xl"></i>
        </div>
        <h1 class="text-xl font-bold text-gray-800">在线用户监控</h1>
        <!-- 当前在线人数 badge 脉冲动画 -->
        <span class="relative inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200">
          <span class="relative flex h-2 w-2">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
          </span>
          <span class="text-sm font-semibold text-emerald-700">当前在线: {{ onlineUsers.length }} 人</span>
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
            <p class="text-sm text-gray-500 mb-1">当前在线总人数</p>
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
            <p class="text-sm text-gray-500 mb-1">在线教师数</p>
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
            <p class="text-sm text-gray-500 mb-1">在线学生数</p>
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
            <p class="text-sm text-gray-500 mb-1">今日登录人数</p>
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
            <el-popconfirm title="确定要强制该用户下线吗？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleForceLogout(row)">
              <template #reference>
                <el-button type="danger" size="small" link>强制下线</el-button>
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
        <h2 class="text-base font-semibold text-gray-800">在线趋势图（最近24小时）</h2>
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

// TODO: 替换为API调用
const onlineUsers = ref<OnlineUser[]>([
  { id: 1, username: 'zhangsan', realName: '张三', role: 'TEACHER', loginTime: '2024-06-03 08:15:22', lastActiveTime: '2024-06-03 09:32:10', ipAddress: '192.168.1.101' },
  { id: 2, username: 'lisi', realName: '李四', role: 'STUDENT', loginTime: '2024-06-03 08:20:45', lastActiveTime: '2024-06-03 09:28:33', ipAddress: '192.168.1.102' },
  { id: 3, username: 'wangwu', realName: '王五', role: 'STUDENT', loginTime: '2024-06-03 08:05:18', lastActiveTime: '2024-06-03 09:30:55', ipAddress: '192.168.1.103' },
  { id: 4, username: 'zhaoliu', realName: '赵六', role: 'TEACHER', loginTime: '2024-06-03 07:50:30', lastActiveTime: '2024-06-03 09:25:00', ipAddress: '192.168.1.104' },
  { id: 5, username: 'sunqi', realName: '孙七', role: 'STUDENT', loginTime: '2024-06-03 08:35:12', lastActiveTime: '2024-06-03 09:31:20', ipAddress: '192.168.1.105' },
  { id: 6, username: 'zhouba', realName: '周八', role: 'STUDENT', loginTime: '2024-06-03 08:42:08', lastActiveTime: '2024-06-03 09:29:45', ipAddress: '192.168.1.106' },
  { id: 7, username: 'wujiu', realName: '吴九', role: 'TEACHER', loginTime: '2024-06-03 07:55:40', lastActiveTime: '2024-06-03 09:27:18', ipAddress: '192.168.1.107' },
  { id: 8, username: 'zhengshi', realName: '郑十', role: 'STUDENT', loginTime: '2024-06-03 09:01:55', lastActiveTime: '2024-06-03 09:33:02', ipAddress: '192.168.1.108' },
  { id: 9, username: 'chenyi', realName: '陈一', role: 'STUDENT', loginTime: '2024-06-03 08:10:33', lastActiveTime: '2024-06-03 09:26:41', ipAddress: '192.168.1.109' },
  { id: 10, username: 'huanger', realName: '黄二', role: 'TEACHER', loginTime: '2024-06-03 08:48:21', lastActiveTime: '2024-06-03 09:24:56', ipAddress: '192.168.1.110' },
  { id: 11, username: 'liusan', realName: '刘三', role: 'STUDENT', loginTime: '2024-06-03 09:05:17', lastActiveTime: '2024-06-03 09:32:38', ipAddress: '192.168.1.111' },
  { id: 12, username: 'yangsi', realName: '杨四', role: 'STUDENT', loginTime: '2024-06-03 08:28:44', lastActiveTime: '2024-06-03 09:30:12', ipAddress: '192.168.1.112' },
  { id: 13, username: 'xuwu', realName: '许五', role: 'TEACHER', loginTime: '2024-06-03 07:42:59', lastActiveTime: '2024-06-03 09:23:47', ipAddress: '192.168.1.113' },
  { id: 14, username: 'heliu', realName: '何六', role: 'STUDENT', loginTime: '2024-06-03 08:55:30', lastActiveTime: '2024-06-03 09:31:55', ipAddress: '192.168.1.114' },
  { id: 15, username: 'luoqi', realName: '罗七', role: 'STUDENT', loginTime: '2024-06-03 09:12:08', lastActiveTime: '2024-06-03 09:33:28', ipAddress: '192.168.1.115' },
])

// ==================== 计算属性 ====================
const teacherCount = computed(() => onlineUsers.value.filter(u => u.role === 'TEACHER').length)
const studentCount = computed(() => onlineUsers.value.filter(u => u.role === 'STUDENT').length)
const todayLoginCount = ref(186)

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
  refreshing.value = true
  setTimeout(() => {
    refreshing.value = false
    ElMessage.success('数据已刷新')
  }, 800)
}

function handleForceLogout(user: OnlineUser) {
  ElMessage.success(`已强制用户 ${user.realName}(${user.username}) 下线`)
  onlineUsers.value = onlineUsers.value.filter(u => u.id !== user.id)
}

// ==================== Echarts 图表 ====================
function initTrendChart() {
  if (!trendChartRef.value) return
  chartInstance = echarts.init(trendChartRef.value)

  // 生成最近24小时的时间标签和mock数据
  const hours: string[] = []
  const teacherData: number[] = []
  const studentData: number[] = []

  for (let i = 23; i >= 0; i--) {
    hours.push(dayjs().subtract(i, 'hour').format('HH:00'))
    teacherData.push(Math.floor(Math.random() * 8) + 2)
    studentData.push(Math.floor(Math.random() * 25) + 8)
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
    },
    legend: {
      data: ['教师在线数', '学生在线数'],
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
        name: '教师在线数',
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
        name: '学生在线数',
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
  nextTick(() => {
    initTrendChart()
  })
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
