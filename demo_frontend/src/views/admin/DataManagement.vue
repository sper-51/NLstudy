<template>
  <div class="space-y-6">
    <!-- 标题区 -->
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-xl bg-[#3b82f6]/10 flex items-center justify-center">
        <i class="ri-database-2-line text-[#3b82f6] text-lg"></i>
      </div>
      <h1 class="text-xl font-bold text-gray-800">数据管理</h1>
    </div>

    <!-- 数据备份卡片 -->
    <div class="bg-white rounded-xl shadow-sm p-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-5">
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-lg bg-blue-50 flex items-center justify-center">
            <i class="ri-cloud-line text-[#3b82f6] text-lg"></i>
          </div>
          <h2 class="text-base font-semibold text-gray-800">数据库备份</h2>
          <el-tag size="small" type="info" effect="plain">备份接口暂未接入，存储统计已接入</el-tag>
        </div>
      </div>

      <!-- 备份信息 -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
        <div class="bg-slate-50 rounded-lg p-4">
          <p class="text-sm text-gray-500 mb-1">数据库大小</p>
          <p class="text-xl font-bold text-gray-800">{{ dbSize }}</p>
        </div>
        <div class="bg-slate-50 rounded-lg p-4">
          <p class="text-sm text-gray-500 mb-1">上次备份时间</p>
          <p class="text-lg font-semibold text-gray-700">{{ lastBackupTime }}</p>
        </div>
        <div class="bg-slate-50 rounded-lg p-4">
          <p class="text-sm text-gray-500 mb-1">备份文件数</p>
          <p class="text-lg font-semibold text-gray-700">{{ backupRecords.length }} 个</p>
        </div>
      </div>

      <!-- 备份记录列表 -->
      <div class="mb-6">
        <h3 class="text-sm font-medium text-gray-600 mb-3 flex items-center gap-1.5">
          <i class="ri-history-line"></i>最近备份记录
        </h3>
        <el-table
          :data="backupRecords"
          size="small"
          empty-text="暂无备份记录"
          :header-cell-style="{ background: '#f8fafc', fontWeight: 600, color: '#374151', fontSize: '13px' }"
        >
          <el-table-column prop="time" label="备份时间" min-width="180" />
          <el-table-column prop="size" label="文件大小" width="110" />
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small" effect="light">
                {{ row.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button v-if="row.status === 'success'" link type="primary" size="small" @click="handleDownload(row)">
                <i class="ri-download-line mr-0.5"></i>下载
              </el-button>
              <span v-else class="text-gray-300 text-xs">-</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 操作按钮 & 自动备份设置 -->
      <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 border-t pt-5">
        <el-button type="primary" size="large" disabled class="!px-8 !py-2.5" @click="handleBackup">
          <i :class="backupLoading ? 'ri-loader-4-line animate-spin' : 'ri-shield-check-line'" class="mr-1.5"></i>
          备份待接入
        </el-button>
        <div class="flex items-center gap-4 bg-slate-50 rounded-lg px-4 py-3 opacity-75">
          <label class="flex items-center gap-2 select-none">
            <span class="text-sm text-gray-600">自动备份</span>
            <el-switch v-model="autoBackupEnabled" disabled active-color="#3b82f6" />
          </label>
          <div class="text-xs text-gray-400 border-l pl-4">
            <span>计划任务暂未启用，等待后端备份接口接入</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据清理卡片 -->
    <div class="bg-white rounded-xl shadow-sm p-6">
      <div class="flex items-center gap-3 mb-5">
        <div class="w-9 h-9 rounded-lg bg-red-50 flex items-center justify-center">
          <i class="ri-delete-bin-line text-[#ef4444] text-lg"></i>
        </div>
        <h2 class="text-base font-semibold text-gray-800">历史数据清理</h2>
      </div>

      <div class="space-y-3 mb-5">
        <label
          v-for="(option, idx) in cleanOptions"
          :key="idx"
          class="flex items-start gap-3 p-3 rounded-lg hover:bg-slate-50 cursor-pointer transition-colors"
        >
          <input
            v-model="selectedCleanOptions"
            :value="idx"
            type="checkbox"
            class="mt-0.5 w-4 h-4 rounded border-gray-300 text-[#ef4444] focus:ring-[#ef4444]/20"
          />
          <div>
            <p class="text-sm font-medium text-gray-700">{{ option.label }}</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ option.desc }}</p>
          </div>
        </label>
      </div>

      <div class="flex items-start gap-2 p-3 bg-red-50 border border-red-100 rounded-lg mb-5">
        <i class="ri-error-warning-line text-[#ef4444] mt-0.5 flex-shrink-0"></i>
        <p class="text-sm text-red-600">
          警告：清理操作将永久删除所选范围内的历史数据，且不可恢复。建议在执行前先进行一次完整的数据备份。
        </p>
      </div>

      <el-popconfirm title="确定要执行数据清理操作吗？此操作不可恢复！" confirm-button-text="确定执行" cancel-button-text="取消" @confirm="handleClean">
        <template #reference>
          <el-button type="danger" disabled :loading="cleanLoading">
            <i class="ri-delete-bin-5-line mr-1"></i>清理待接入（{{ selectedCleanOptions.length }}项）
          </el-button>
        </template>
      </el-popconfirm>
    </div>

    <!-- 存储统计卡片 -->
    <div class="bg-white rounded-xl shadow-sm p-6">
      <div class="flex items-center gap-3 mb-5">
        <div class="w-9 h-9 rounded-lg bg-emerald-50 flex items-center justify-center">
          <i class="ri-pie-chart-line text-[#10b981] text-lg"></i>
        </div>
        <h2 class="text-base font-semibold text-gray-800">存储统计</h2>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 饼图区域 -->
        <div class="border border-gray-100 rounded-xl p-4">
          <h3 class="text-sm font-medium text-gray-600 mb-4">各表数据量占比</h3>
          <div v-if="storageStats.length" ref="chartRef" class="w-full h-[280px]"></div>
          <div v-else class="h-[280px] flex flex-col items-center justify-center text-gray-400">
            <i class="ri-database-2-line text-3xl mb-2"></i>
            <p class="text-sm">{{ storageLoading ? '正在加载存储统计...' : '暂无真实存储统计数据' }}</p>
          </div>
        </div>

        <!-- 表格统计 -->
        <div class="border border-gray-100 rounded-xl overflow-hidden">
          <h3 class="text-sm font-medium text-gray-600 px-4 pt-4 pb-2">表级详情</h3>
          <el-table
            :data="storageStats"
            size="small"
            :header-cell-style="{ background: '#f8fafc', fontWeight: 600, color: '#374151', fontSize: '13px' }"
          >
            <el-table-column prop="table" label="数据表" min-width="140" />
            <el-table-column prop="rows" label="数据量" width="100" align="right">
              <template #default="{ row }">
                <span class="font-mono text-sm">{{ row.rows.toLocaleString() }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="size" label="占用空间" width="100" align="right">
              <template #default="{ row }">
                <span class="text-sm text-gray-600">{{ row.size }}</span>
              </template>
            </el-table-column>
            <el-table-column label="占比" width="120">
              <template #default="{ row }">
                <div class="flex items-center gap-2">
                  <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div
                      class="h-full rounded-full transition-all"
                      :style="{ width: row.percent + '%', backgroundColor: row.color }"
                    ></div>
                  </div>
                  <span class="text-xs text-gray-500 w-10 text-right">{{ row.percent }}%</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getStorageStats } from '@/api/admin'

// ==================== 备份相关 ====================
const dbSize = ref('待接入')
const lastBackupTime = ref('暂无记录')
const backupLoading = ref(false)
const autoBackupEnabled = ref(false)

interface BackupRecord {
  time: string
  size: string
  status: 'success' | 'fail'
}

const backupRecords = ref<BackupRecord[]>([])

function handleBackup() {
  ElMessage.info('数据库备份接口暂未接入，当前仅展示真实存储统计')
}

function handleDownload(row: BackupRecord) {
  ElMessage.info(`${row.time} 的备份下载接口暂未接入`)
}

// ==================== 清理相关 ====================
interface CleanOption {
  label: string
  desc: string
}

const cleanOptions: CleanOption[] = [
  { label: '清理30天前的操作日志', desc: '将删除30天前的所有系统操作日志记录；当前未接入空间释放量统计' },
  { label: '清理90天前的登录日志', desc: '将删除90天前的所有登录/登出日志记录；当前未接入空间释放量统计' },
  { label: '清理已删除用户的关联数据', desc: '将彻底清除已标记删除用户的所有关联数据（考试记录、成绩等）；当前未接入空间释放量统计' },
]

const selectedCleanOptions = ref<number[]>([])
const cleanLoading = ref(false)

function handleClean() {
  if (selectedCleanOptions.value.length === 0) return
  ElMessage.info('数据清理接口暂未接入，为避免误删数据已禁用执行')
}

// ==================== 存储统计图表 ====================
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const storageStats = ref<Array<{ table: string; rows: number; size: string; percent: number; color: string }>>([])
const storageLoading = ref(false)

async function loadStorageStats() {
  storageLoading.value = true
  try {
    const res: any = await getStorageStats()
    const tables = Array.isArray(res?.tables) ? res.tables : []
    storageStats.value = tables.map((item: any) => ({
      table: item.table || '',
      rows: Number(item.rows) || 0,
      size: item.size || '0 B',
      percent: Number(item.percent) || 0,
      color: item.color || '#3b82f6',
    }))
    dbSize.value = res?.databaseSize || '暂无数据'
    nextTick(() => initChart())
  } catch (error) {
    console.error('获取存储统计失败:', error)
    storageStats.value = []
    dbSize.value = '暂无数据'
  } finally {
    storageLoading.value = false
  }
}

function initChart() {
  if (!chartRef.value) return
  chartInstance?.dispose()
  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 条 ({d}%)',
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { fontSize: 12, color: '#6b7280' },
      itemWidth: 12,
      itemHeight: 12,
      itemGap: 12,
    },
    series: [
      {
        name: '数据量',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['38%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' },
        },
        labelLine: { show: false },
        data: storageStats.value.map(s => ({
          name: s.table,
          value: s.rows,
          itemStyle: { color: s.color },
        })),
      },
    ],
  }
  chartInstance.setOption(option)
}

onMounted(() => {
  loadStorageStats()
  nextTick(() => initChart())
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})

function handleResize() {
  chartInstance?.resize()
}
</script>
