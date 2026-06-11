<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 + 快捷统计 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">考试监控列表</h1>
        <p class="text-sm text-gray-500 mt-1">实时监控所有考试的进行状态</p>
      </div>
      <div class="flex items-center gap-3">
        <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-blue-50 border border-blue-200">
          <div class="w-2 h-2 rounded-full bg-blue-500 animate-pulse"></div>
          <span class="text-sm font-medium text-blue-700">{{ ongoingCount }}</span>
          <span class="text-xs text-blue-500">进行中</span>
        </div>
        <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-gray-100 border border-gray-200">
          <i class="ri-calendar-check-line text-gray-500"></i>
          <span class="text-sm font-medium text-gray-700">{{ todayEndedCount }}</span>
          <span class="text-xs text-gray-500">今日结束</span>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="bg-white rounded-xl p-4 shadow-sm flex items-center gap-3">
      <el-select v-model="filterStatus" placeholder="全部状态" style="width: 150px" size="default" clearable>
        <el-option label="进行中" value="ongoing" />
        <el-option label="已结束" value="ended" />
      </el-select>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索考试名称或课程..."
        style="width: 280px"
        size="default"
        clearable
        :prefix-icon="'ri-search-line'"
      />
      <el-date-picker
        v-model="dateFilter"
        type="date"
        placeholder="选择日期"
        style="width: 160px"
        size="default"
        value-format="YYYY-MM-DD"
        clearable
      />
    </div>

    <!-- 监控列表 -->
    <div class="space-y-3">
      <div
        v-for="exam in filteredExams"
        :key="exam.id"
        :class="[
          'bg-white rounded-xl shadow-sm border transition-all overflow-hidden cursor-pointer hover:shadow-md',
          exam.status === 'ongoing'
            ? 'border-blue-300 ring-1 ring-blue-100'
            : 'border-gray-100'
        ]"
      >
        <div class="p-5">
          <!-- 进行中的高亮标识条 -->
          <div
            v-if="exam.status === 'ongoing'"
            class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-blue-500 to-cyan-400"
          ></div>

          <div class="flex items-start justify-between relative">
            <!-- 左侧：考试信息 -->
            <div class="flex items-start gap-4 flex-1">
              <!-- 状态图标 -->
              <div
                :class="[
                  'w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0',
                  exam.status === 'ongoing' ? 'bg-blue-100' : 'bg-gray-100'
                ]"
              >
                <i
                  :class="[
                    'text-xl',
                    exam.status === 'ongoing' ? 'ri-live-line text-blue-500 animate-pulse' : 'ri-checkbox-circle-line text-gray-400'
                  ]"
                ></i>
              </div>

              <!-- 考试详情 -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 mb-1">
                  <h3 class="font-semibold text-gray-800 text-base truncate">{{ exam.name }}</h3>
                  <el-tag
                    :type="exam.status === 'ongoing' ? 'primary' : 'info'"
                    size="small"
                    effect="light"
                    round
                  >
                    {{ exam.status === 'ongoing' ? '进行中' : '已结束' }}
                  </el-tag>
                  <el-tag v-if="exam.status === 'ongoing'" type="warning" size="small" effect="light" round>
                    剩余 {{ exam.remainingTime }}
                  </el-tag>
                </div>

                <div class="flex items-center gap-4 text-sm text-gray-500 mt-2 flex-wrap">
                  <span class="flex items-center gap-1">
                    <i class="ri-book-2-line"></i>{{ exam.courseName }}
                  </span>
                  <span class="flex items-center gap-1">
                    <i class="ri-time-line"></i>{{ exam.startTime }} ~ {{ exam.endTime }}
                  </span>
                  <span class="flex items-center gap-1">
                    <i class="ri-user-line"></i>应考 {{ exam.totalStudents }} 人
                  </span>
                  <span class="flex items-center gap-1">
                    <i class="ri-file-check-line"></i>已交卷 {{ exam.submittedCount }} 人
                  </span>
                </div>

                <!-- 参与率进度条 -->
                <div class="mt-3 flex items-center gap-3">
                  <span class="text-xs text-gray-400 whitespace-nowrap">参与率</span>
                  <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden max-w-xs">
                    <div
                      class="h-full rounded-full transition-all duration-500"
                      :class="exam.participationRate >= 90 ? 'bg-green-500' : exam.participationRate >= 70 ? 'bg-blue-500' : 'bg-orange-400'"
                      :style="{ width: exam.participationRate + '%' }"
                    ></div>
                  </div>
                  <span class="text-xs font-semibold" :class="exam.participationRate >= 90 ? 'text-green-600' : 'text-gray-600'">
                    {{ exam.participationRate.toFixed(1) }}%
                  </span>
                </div>
              </div>
            </div>

            <!-- 右侧：操作按钮 -->
            <div class="flex items-center gap-2 ml-4 flex-shrink-0">
              <el-button
                v-if="exam.status === 'ongoing'"
                type="primary"
                @click.stop="enterMonitor(exam)"
              >
                <i class="ri-eye-line mr-1"></i>进入监控
              </el-button>
              <el-button
                v-else
                plain
                @click.stop="viewDetail(exam)"
              >
                <i class="ri-file-list-3-line mr-1"></i>查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredExams.length === 0" class="flex flex-col items-center justify-center py-16 text-gray-400 bg-white rounded-xl">
        <i class="ri-monitor-line text-5xl mb-3 opacity-50"></i>
        <p class="text-sm font-medium">暂无考试记录</p>
        <p class="text-xs mt-1">创建考试后将在此处显示监控入口</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSchedules } from '@/api/teacher'

const router = useRouter()

// ========== 筛选 ==========
const filterStatus = ref('')
const searchKeyword = ref('')
const dateFilter = ref('')

// ========== 考试数据 ==========
interface ExamMonitorItem {
  id: number
  name: string
  courseName: string
  startTime: string
  endTime: string
  totalStudents: number
  submittedCount: number
  participationRate: number
  status: 'ongoing' | 'ended'
  remainingTime?: string
}

const exams = ref<ExamMonitorItem[]>([])
const loading = ref(false)

// 加载考试列表
async function loadExams() {
  try {
    loading.value = true
    const res = await getSchedules({ page: 1, pageSize: 1000 })
    const list = Array.isArray(res) ? res : (res?.list || [])

    const now = new Date()
    exams.value = list.map((e: any) => {
      const startTime = new Date(e.startTime)
      const endTime = new Date(e.endTime)
      const isOngoing = startTime <= now && endTime >= now

      return {
        id: e.id || 0,
        name: e.examName || e.name || '',
        courseName: e.courseName || '',
        startTime: e.startTime ? e.startTime.replace('T', ' ').substring(0, 16) : '',
        endTime: e.endTime ? e.endTime.replace('T', ' ').substring(0, 16) : '',
        totalStudents: e.studentCount || 0,
        submittedCount: e.submitCount || 0,
        participationRate: e.studentCount > 0 ?
          Math.round(((e.submitCount || 0) / e.studentCount) * 10000) / 100 : 0,
        status: isOngoing ? 'ongoing' : 'ended',
        remainingTime: isOngoing ? calculateRemainingTime(endTime) : undefined,
      }
    })
  } catch (error) {
    console.error('加载考试列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 计算剩余时间
function calculateRemainingTime(endTime: Date): string {
  const now = new Date()
  const diff = endTime.getTime() - now.getTime()
  if (diff <= 0) return ''

  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))

  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}

// 页面加载时获取数据
onMounted(async () => {
  await loadExams()
})

// ========== 快捷统计 ==========
const ongoingCount = computed(() => exams.value.filter(e => e.status === 'ongoing').length)
const todayEndedCount = computed(() => {
  const today = new Date().toISOString().split('T')[0]
  return exams.value.filter(e => e.status === 'ended' && e.endTime.startsWith(today)).length
})

// ========== 筛选逻辑 ==========
const filteredExams = computed(() => {
  let list = exams.value

  if (filterStatus.value) {
    list = list.filter(item => item.status === filterStatus.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(item =>
      item.name.toLowerCase().includes(keyword) ||
      item.courseName.toLowerCase().includes(keyword)
    )
  }

  if (dateFilter.value) {
    list = list.filter(item => item.startTime.startsWith(dateFilter.value))
  }

  // 进行中的排前面
  return [...list].sort((a, b) => {
    if (a.status === 'ongoing' && b.status !== 'ongoing') return -1
    if (a.status !== 'ongoing' && b.status === 'ongoing') return 1
    return 0
  })
})

// ========== 操作 ==========
function enterMonitor(exam: ExamMonitorItem) {
  router.push(`/teacher/exam/${exam.id}/monitor`)
}

function viewDetail(exam: ExamMonitorItem) {
  router.push(`/teacher/exam/${exam.id}/detail`)
}
</script>
