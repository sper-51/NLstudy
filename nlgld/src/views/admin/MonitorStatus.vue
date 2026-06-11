<template>
  <div class="min-h-screen bg-[#F5F7FA] p-6">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-primary-500/10 flex items-center justify-center">
          <i class="ri-monitor-line text-primary-500 text-xl"></i>
        </div>
        <h1 class="text-xl font-bold text-gray-800">系统状态监控</h1>
      </div>
      <button @click="handleRefresh" :disabled="refreshing" class="inline-flex items-center gap-2 px-4 py-2 bg-white rounded-lg shadow-sm border border-gray-200 hover:border-primary-300 hover:text-primary-600 transition-all cursor-pointer disabled:opacity-50">
        <i :class="[refreshing ? 'ri-loader-4-line animate-spin' : 'ri-refresh-line']"></i>
        <span class="text-sm">{{ refreshing ? '刷新中...' : '刷新' }}</span>
      </button>
    </div>

    <!-- 核心指标卡片区 -->
    <div class="grid grid-cols-4 gap-4 mb-6">
      <!-- CPU 使用率 -->
      <div class="bg-white rounded-xl shadow-sm p-5 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm text-gray-500">CPU 使用率</span>
          <span class="inline-flex w-2.5 h-2.5 rounded-full" :class="healthColor(cpuUsage)"></span>
        </div>
        <div class="flex items-center gap-4">
          <el-progress type="circle" :percentage="cpuUsage" :width="72" :stroke-width="8" :color="progressColor(cpuUsage)" />
          <div>
            <p class="text-lg font-bold text-gray-800">{{ cpuUsage }}%</p>
            <p class="text-xs text-gray-400">8核 / 16线程</p>
            <p class="text-xs text-gray-400">上限: 100%</p>
          </div>
        </div>
      </div>

      <!-- 内存使用率 -->
      <div class="bg-white rounded-xl shadow-sm p-5 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm text-gray-500">内存使用率</span>
          <span class="inline-flex w-2.5 h-2.5 rounded-full" :class="healthColor(memUsage)"></span>
        </div>
        <div class="flex items-center gap-4">
          <el-progress type="circle" :percentage="memUsage" :width="72" :stroke-width="8" :color="progressColor(memUsage)" />
          <div>
            <p class="text-lg font-bold text-gray-800">{{ memUsage }}%</p>
            <p class="text-xs text-gray-400">{{ usedMem }}GB / {{ totalMem }}GB</p>
            <p class="text-xs text-gray-400">上限: 16GB</p>
          </div>
        </div>
      </div>

      <!-- 磁盘使用率 -->
      <div class="bg-white rounded-xl shadow-sm p-5 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm text-gray-500">磁盘使用率</span>
          <span class="inline-flex w-2.5 h-2.5 rounded-full" :class="healthColor(diskUsage)"></span>
        </div>
        <div class="flex items-center gap-4">
          <el-progress type="circle" :percentage="diskUsage" :width="72" :stroke-width="8" :color="progressColor(diskUsage)" />
          <div>
            <p class="text-lg font-bold text-gray-800">{{ diskUsage }}%</p>
            <p class="text-xs text-gray-400">{{ usedDisk }}GB / {{ totalDisk }}GB</p>
            <p class="text-xs text-gray-400">上限: 500GB</p>
          </div>
        </div>
      </div>

      <!-- 系统负载 -->
      <div class="bg-white rounded-xl shadow-sm p-5 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm text-gray-500">系统负载</span>
          <span class="inline-flex w-2.5 h-2.5 rounded-full" :class="systemLoad > 6 ? 'bg-red-500' : systemLoad > 3 ? 'bg-yellow-500' : 'bg-emerald-500'"></span>
        </div>
        <div class="flex items-center gap-4">
          <div class="w-[72px] h-[72px] rounded-full bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
            <span class="text-xl font-bold" :class="systemLoad > 6 ? 'text-red-600' : systemLoad > 3 ? 'text-yellow-600' : 'text-blue-600'">{{ systemLoad.toFixed(2) }}</span>
          </div>
          <div>
            <p class="text-lg font-bold text-gray-800">{{ systemLoad.toFixed(2) }}</p>
            <p class="text-xs text-gray-400">1分钟 / 5分钟 / 15分钟</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ load1m }} / {{ load5m }} / {{ load15m }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 服务状态表格 -->
    <div class="bg-white rounded-xl shadow-sm p-5 mb-6">
      <div class="flex items-center gap-2 mb-4">
        <i class="ri-server-line text-primary-500"></i>
        <h2 class="text-base font-semibold text-gray-800">服务状态</h2>
      </div>
      <el-table :data="services" stripe style="width: 100%">
        <el-table-column prop="name" label="服务名称" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <i :class="row.icon + ' text-gray-500'" class="text-base"></i>
              <span class="font-medium text-gray-700">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="port" label="端口" width="90" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'running' ? 'success' : 'danger'" size="small" effect="dark">
              {{ row.status === 'running' ? '运行中' : '已停止' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="responseTime" label="响应时间(ms)" width="130" align="center">
          <template #default="{ row }">
            <span :class="row.responseTime > 200 ? 'text-orange-600 font-medium' : 'text-gray-600'">{{ row.responseTime }}ms</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="启动时间" min-width="170" />
        <el-table-column label="操作" width="90" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleRestart(row)">重启</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 资源使用趋势图 -->
    <div class="bg-white rounded-xl shadow-sm p-5 mb-6">
      <div class="flex items-center gap-2 mb-4">
        <i class="ri-area-chart-line text-primary-500"></i>
        <h2 class="text-base font-semibold text-gray-800">资源使用趋势（最近1小时）</h2>
      </div>
      <div ref="resourceChartRef" style="height: 300px; width: 100%"></div>
    </div>

    <!-- JVM 信息面板 -->
    <div class="bg-white rounded-xl shadow-sm p-5">
      <div class="flex items-center gap-2 mb-4">
        <i class="ri-cpu-line text-primary-500"></i>
        <h2 class="text-base font-semibold text-gray-800">JVM 信息</h2>
      </div>
      <div class="grid grid-cols-3 gap-4">
        <!-- JVM 内存使用 -->
        <div class="rounded-xl bg-slate-50 p-4 border border-slate-100">
          <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-1.5">
            <i class="ri-hard-drive-2-line text-blue-500"></i>JVM 内存使用
          </h3>
          <div class="space-y-3">
            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-gray-500">堆内存 (Heap)</span>
                <span class="font-medium text-gray-700">{{ jvmInfo.heapUsed }}MB / {{ jvmInfo.heapMax }}MB</span>
              </div>
              <el-progress :percentage="Math.round((jvmInfo.heapUsed / jvmInfo.heapMax) * 100)" :stroke-width="6" :show-text="false" color="#3b82f6" />
            </div>
            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-gray-500">非堆内存 (Non-Heap)</span>
                <span class="font-medium text-gray-700">{{ jvmInfo.nonHeapUsed }}MB / {{ jvmInfo.nonHeapMax }}MB</span>
              </div>
              <el-progress :percentage="Math.round((jvmInfo.nonHeapUsed / jvmInfo.nonHeapMax) * 100)" :stroke-width="6" :show-text="false" color="#8b5cf6" />
            </div>
            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-gray-500">直接内存 (Direct)</span>
                <span class="font-medium text-gray-700">{{ jvmInfo.directUsed }}MB / {{ jvmInfo.directMax }}MB</span>
              </div>
              <el-progress :percentage="Math.round((jvmInfo.directUsed / jvmInfo.directMax) * 100)" :stroke-width="6" :show-text="false" color="#f59e0b" />
            </div>
          </div>
        </div>

        <!-- GC 信息 -->
        <div class="rounded-xl bg-slate-50 p-4 border border-slate-100">
          <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-1.5">
            <i class="ri-recycle-line text-emerald-500"></i>GC 信息
          </h3>
          <div class="space-y-3">
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">GC 次数</span>
              <span class="text-base font-bold text-gray-800">{{ jvmInfo.gcCount.toLocaleString() }} 次</span>
            </div>
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">GC 总耗时</span>
              <span class="text-base font-bold text-gray-800">{{ jvmInfo.gcTotalTime }} ms</span>
            </div>
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">Young GC</span>
              <span class="text-sm font-medium text-blue-600">{{ jvmInfo.youngGcCount }}次 / {{ jvmInfo.youngGcTime }}ms</span>
            </div>
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">Full GC</span>
              <span class="text-sm font-medium text-purple-600">{{ jvmInfo.fullGcCount }}次 / {{ jvmInfo.fullGcTime }}ms</span>
            </div>
          </div>
        </div>

        <!-- 线程信息 -->
        <div class="rounded-xl bg-slate-50 p-4 border border-slate-100">
          <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-1.5">
            <i class="ri-git-branch-line text-orange-500"></i>线程信息
          </h3>
          <div class="space-y-3">
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">活跃线程数</span>
              <span class="text-base font-bold text-blue-600">{{ jvmInfo.activeThreads }}</span>
            </div>
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">守护线程数</span>
              <span class="text-base font-bold text-emerald-600">{{ jvmInfo.daemonThreads }}</span>
            </div>
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">峰值线程数</span>
              <span class="text-base font-bold text-gray-700">{{ jvmInfo.peakThreads }}</span>
            </div>
            <div class="flex items-center justify-between p-2.5 bg-white rounded-lg">
              <span class="text-xs text-gray-500">已启动线程总数</span>
              <span class="text-base font-bold text-gray-700">{{ jvmInfo.totalStartedThreads }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// ==================== 类型定义 ====================
interface Service {
  name: string
  port: number
  status: 'running' | 'stopped'
  responseTime: number
  startTime: string
  icon: string
}

// ==================== 响应式数据 ====================
const refreshing = ref(false)
const resourceChartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// TODO: 替换为API调用
const cpuUsage = ref(42)
const memUsage = ref(68)
const diskUsage = ref(55)
const usedMem = ref(10.9)
const totalMem = ref(16)
const usedDisk = ref(275)
const totalDisk = ref(500)

const systemLoad = ref(2.34)
const load1m = ref('2.34')
const load5m = ref('1.89')
const load15m = ref('1.56')

const services = ref<Service[]>([
  { name: 'SpringBoot Application', port: 8080, status: 'running', responseTime: 45, startTime: '2024-06-01 08:00:12', icon: 'ri-code-box-line' },
  { name: 'MySQL Database', port: 3306, status: 'running', responseTime: 12, startTime: '2024-05-28 06:30:00', icon: 'ri-database-2-line' },
  { name: 'Redis Cache', port: 6379, status: 'running', responseTime: 2, startTime: '2024-05-28 06:30:05', icon: 'ri-flashlight-line' },
  { name: 'Nginx', port: 80, status: 'running', responseTime: 8, startTime: '2024-05-28 06:30:10', icon: 'ri-global-line' },
])

const jvmInfo = ref({
  heapUsed: 384,
  heapMax: 1024,
  nonHeapUsed: 128,
  nonHeapMax: 256,
  directUsed: 32,
  directMax: 128,
  gcCount: 1523,
  gcTotalTime: 12840,
  youngGcCount: 1498,
  youngGcTime: 8920,
  fullGcCount: 25,
  fullGcTime: 3920,
  activeThreads: 48,
  daemonThreads: 42,
  peakThreads: 56,
  totalStartedThreads: 1248,
})

// ==================== 方法 ====================
function healthColor(value: number): string {
  if (value >= 85) return 'bg-red-500'
  if (value >= 65) return 'bg-yellow-500'
  return 'bg-emerald-500'
}

function progressColor(percentage: number): string {
  if (percentage >= 85) return '#ef4444'
  if (percentage >= 65) return '#f59e0b'
  return '#10b981'
}

function handleRefresh() {
  refreshing.value = true
  // TODO: 替换为API调用
  setTimeout(() => {
    cpuUsage.value = Math.floor(Math.random() * 30) + 25
    memUsage.value = Math.floor(Math.random() * 20) + 55
    diskUsage.value = Math.floor(Math.random() * 10) + 50
    systemLoad.value = parseFloat((Math.random() * 4 + 0.5).toFixed(2))
    refreshing.value = false
    ElMessage.success('数据已刷新')
  }, 800)
}

function handleRestart(service: Service) {
  ElMessage.info(`正在重启服务: ${service.name}`)
}

// ==================== Echarts 资源趋势图 ====================
function initResourceChart() {
  if (!resourceChartRef.value) return
  chartInstance = echarts.init(resourceChartRef.value)

  // 最近60分钟数据（每分钟一个点）
  const minutes: string[] = []
  const cpuData: number[] = []
  const memData: number[] = []
  const diskData: number[] = []

  for (let i = 59; i >= 0; i--) {
    const t = new Date(Date.now() - i * 60000)
    minutes.push(`${t.getHours().toString().padStart(2, '0')}:${t.getMinutes().toString().padStart(2, '0')}`)
    cpuData.push(Math.floor(Math.random() * 35) + 20)
    memData.push(Math.floor(Math.random() * 18) + 52)
    diskData.push(Math.floor(Math.random() * 8) + 48)
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
    },
    legend: {
      data: ['CPU', '内存', '磁盘'],
      top: 0,
    },
    grid: {
      left: '3%', right: '4%', bottom: '3%', containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: minutes,
      axisLabel: { fontSize: 10, interval: 9 },
    },
    yAxis: {
      type: 'value',
      name: '使用率 %',
      max: 100,
    },
    series: [
      {
        name: 'CPU',
        type: 'line',
        smooth: true,
        stack: 'total',
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59,130,246,0.4)' },
            { offset: 1, color: 'rgba(59,130,246,0.02)' },
          ]),
        },
        lineStyle: { color: '#3b82f6', width: 2 },
        itemStyle: { color: '#3b82f6' },
        data: cpuData,
      },
      {
        name: '内存',
        type: 'line',
        smooth: true,
        stack: 'total',
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16,185,129,0.4)' },
            { offset: 1, color: 'rgba(16,185,129,0.02)' },
          ]),
        },
        lineStyle: { color: '#10b981', width: 2 },
        itemStyle: { color: '#10b981' },
        data: memData,
      },
      {
        name: '磁盘',
        type: 'line',
        smooth: true,
        stack: 'total',
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245,158,11,0.4)' },
            { offset: 1, color: 'rgba(245,158,11,0.02)' },
          ]),
        },
        lineStyle: { color: '#f59e0b', width: 2 },
        itemStyle: { color: '#f59e0b' },
        data: diskData,
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
    initResourceChart()
  })
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
