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
            <p class="text-xs text-gray-400">{{ cpuCores }} 核</p>
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
            <p class="text-xs text-gray-400">JVM 最大内存</p>
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
            <p class="text-xs text-gray-400 truncate max-w-[120px]" :title="diskPath">{{ diskPath }}</p>
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
        <h2 class="text-base font-semibold text-gray-800">当前资源使用快照</h2>
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
              <el-progress :percentage="safePercent(jvmInfo.heapUsed, jvmInfo.heapMax)" :stroke-width="6" :show-text="false" color="#3b82f6" />
            </div>
            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-gray-500">非堆内存 (Non-Heap)</span>
                <span class="font-medium text-gray-700">{{ jvmInfo.nonHeapUsed }}MB / {{ jvmInfo.nonHeapMax }}MB</span>
              </div>
              <el-progress :percentage="safePercent(jvmInfo.nonHeapUsed, jvmInfo.nonHeapMax)" :stroke-width="6" :show-text="false" color="#8b5cf6" />
            </div>
            <div>
              <div class="flex justify-between text-xs mb-1">
                <span class="text-gray-500">直接内存 (Direct)</span>
                <span class="font-medium text-gray-700">{{ jvmInfo.directUsed }}MB / {{ jvmInfo.directMax }}MB</span>
              </div>
              <el-progress :percentage="safePercent(jvmInfo.directUsed, jvmInfo.directMax)" :stroke-width="6" :show-text="false" color="#f59e0b" />
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
import { getSystemStatus } from '@/api/admin'

// ==================== 类型定义 ====================
interface Service {
  name: string
  port: number
  status: 'running' | 'stopped'
  responseTime: number
  startTime: string
  icon: string
}

// ==================== 响应式数据（从后端API获取）====================
const refreshing = ref(false)
const resourceChartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const cpuUsage = ref(0)
const memUsage = ref(0)
const diskUsage = ref(0)
const usedMem = ref(0)
const totalMem = ref(0)
const usedDisk = ref(0)
const totalDisk = ref(0)
const cpuCores = ref(0)
const diskPath = ref('—')

const systemLoad = ref(0)
const load1m = ref('0')
const load5m = ref('0')
const load15m = ref('0')

const services = ref<Service[]>([])

const jvmInfo = ref({
  heapUsed: 0,
  heapMax: 0,
  nonHeapUsed: 0,
  nonHeapMax: 0,
  directUsed: 0,
  directMax: 0,
  gcCount: 0,
  gcTotalTime: 0,
  youngGcCount: 0,
  youngGcTime: 0,
  fullGcCount: 0,
  fullGcTime: 0,
  activeThreads: 0,
  daemonThreads: 0,
  peakThreads: 0,
  totalStartedThreads: 0,
})

// 加载系统状态数据
async function fetchSystemStatus() {
  try {
    refreshing.value = true
    const res: any = await getSystemStatus()
    if (res) {
      const data = res
      // CPU/内存/磁盘数据
      cpuUsage.value = toNumber(data.cpuUsage ?? data.cpu?.usage)
      memUsage.value = toNumber(data.memUsage ?? data.memory?.usage)
      diskUsage.value = toNumber(data.diskUsage ?? data.disk?.usage)
      usedMem.value = toNumber(data.usedMem ?? data.memory?.used)
      totalMem.value = toNumber(data.totalMem ?? data.memory?.total)
      usedDisk.value = toNumber(data.usedDisk ?? data.disk?.used)
      totalDisk.value = toNumber(data.totalDisk ?? data.disk?.total)
      cpuCores.value = toNumber(data.cpuCores)
      diskPath.value = data.diskPath || '—'
      // 系统负载
      systemLoad.value = toNumber(data.systemLoad ?? data.load?.load1)
      load1m.value = String(toNumber(data.load1m ?? data.load?.load1))
      load5m.value = String(toNumber(data.load5m ?? data.load?.load5))
      load15m.value = String(toNumber(data.load15m ?? data.load?.load15))
      // 服务状态
      if (data.services) {
        services.value = data.services
      }
      // JVM信息
      if (data.jvm) {
        jvmInfo.value = { ...jvmInfo.value, ...data.jvm }
      }
    }
  } catch (error) {
    console.error('获取系统状态失败:', error)
    ElMessage.error('获取系统状态失败')
  } finally {
    refreshing.value = false
  }
}

// ==================== 方法 ====================
function healthColor(value: number): string {
  if (value >= 85) return 'bg-red-500'
  if (value >= 65) return 'bg-yellow-500'
  return 'bg-emerald-500'
}

function toNumber(value: unknown): number {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

function safePercent(used: number, total: number): number {
  if (!Number.isFinite(used) || !Number.isFinite(total) || total <= 0) return 0
  return Math.max(0, Math.min(100, Math.round((used / total) * 100)))
}

function progressColor(percentage: number): string {
  if (percentage >= 85) return '#ef4444'
  if (percentage >= 65) return '#f59e0b'
  return '#10b981'
}

function handleRefresh() {
  fetchSystemStatus().then(() => initResourceChart())
}

function handleRestart(service: Service) {
  ElMessage.info(`正在重启服务: ${service.name}`)
}

// ==================== Echarts 资源趋势图 ====================
function initResourceChart() {
  if (!resourceChartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(resourceChartRef.value)
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
    },
    grid: {
      left: '3%', right: '4%', bottom: '3%', containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: ['CPU', '内存', '磁盘'],
    },
    yAxis: {
      type: 'value',
      name: '使用率 %',
      max: 100,
    },
    series: [
      {
        name: '当前使用率',
        type: 'bar',
        barWidth: 42,
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: (params: any) => ['#3b82f6', '#10b981', '#f59e0b'][params.dataIndex] || '#64748b',
        },
        data: [cpuUsage.value, memUsage.value, diskUsage.value],
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
  fetchSystemStatus()
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
