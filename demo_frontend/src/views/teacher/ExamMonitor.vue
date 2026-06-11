<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]" v-loading="loading">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-button @click="goBack" plain>
          <i class="ri-arrow-left-line mr-1"></i>返回
        </el-button>
        <div>
          <h1 class="text-xl font-bold text-gray-800">考场监控</h1>
          <p class="text-sm text-gray-500 mt-1">实时监控学生考试状态与异常行为</p>
        </div>
      </div>
      <div class="flex items-center gap-2">
        <span class="text-xs text-gray-400"><i class="ri-refresh-line mr-1"></i>自动刷新中（每10秒）</span>
        <el-switch v-model="autoRefresh" active-text="" inactive-text="" />
        <el-button size="small" type="warning" @click="handleFinishExam">
          <i class="ri-stop-circle-line mr-1"></i>结束考试
        </el-button>
        <el-button size="small" @click="refreshData" :loading="refreshing">
          <i class="ri-refresh-line mr-1"></i>立即刷新
        </el-button>
      </div>
    </div>

    <div class="bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl p-4 text-white shadow-md">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4 min-w-0">
          <div class="w-12 h-12 rounded-xl bg-white/20 flex items-center justify-center backdrop-blur-sm">
            <i class="ri-file-shield-2-line text-2xl"></i>
          </div>
          <div class="min-w-0">
            <h3 class="font-bold text-lg">{{ examInfo.name || '未命名考试' }}</h3>
            <div class="text-sm opacity-90 mt-0.5 flex flex-wrap items-center gap-3">
              <span><i class="ri-book-line mr-1"></i>{{ examInfo.courseName || '未关联课程' }}</span>
              <span><i class="ri-time-line mr-1"></i>{{ examInfo.timeRange }}</span>
            </div>
          </div>
        </div>
        <div class="flex items-center gap-8">
          <div class="text-right">
            <div class="text-xs opacity-80">剩余时间</div>
            <div class="text-2xl font-mono font-bold tracking-wider" :class="countdownWarning ? 'text-yellow-300 animate-pulse' : ''">{{ countdown }}</div>
          </div>
          <div class="text-right">
            <div class="text-xs opacity-80">参与率</div>
            <div class="text-2xl font-bold">{{ examInfo.participationRate }}%</div>
          </div>
        </div>
      </div>
    </div>

    <div class="flex gap-5 min-h-[620px]">
      <div class="w-64 flex-shrink-0 space-y-3">
        <div class="bg-white rounded-xl p-4 shadow-sm">
          <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
            <i class="ri-pie-chart-line text-primary-500"></i>学生状态总览
          </h3>
          <div class="space-y-3">
            <div v-for="stat in statusStats" :key="stat.key" :class="['rounded-lg p-3 flex items-center justify-between transition-all', stat.bgClass]">
              <div class="flex items-center gap-2">
                <div :class="['w-8 h-8 rounded-lg flex items-center justify-center', stat.iconBg]">
                  <i :class="[stat.icon, stat.iconColor]"></i>
                </div>
                <div>
                  <div class="text-lg font-bold text-gray-800">{{ stat.count }}</div>
                  <div class="text-xs text-gray-500">{{ stat.label }}</div>
                </div>
              </div>
              <div class="text-right">
                <div class="text-sm font-semibold text-gray-700">{{ stat.percent }}%</div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="abnormalStudents.length > 0" class="bg-red-50 border border-red-200 rounded-xl p-4">
          <div class="flex items-center gap-2 mb-2">
            <i class="ri-alarm-warning-line text-danger-500 animate-pulse"></i>
            <span class="text-sm font-semibold text-danger-600">异常告警</span>
          </div>
          <div class="space-y-1.5">
            <div v-for="s in abnormalStudents.slice(0, 3)" :key="s.id" class="flex items-center justify-between text-xs bg-white rounded px-2 py-1.5">
              <span class="text-gray-700">{{ s.name }}（{{ s.studentId }}）</span>
              <span class="text-danger-500 font-medium">{{ s.warningReason }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="flex-1 bg-white rounded-xl shadow-sm flex flex-col overflow-hidden">
        <div class="p-4 border-b border-gray-100 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h3 class="text-sm font-semibold text-gray-700">
              <i class="ri-user-follow-line text-primary-500 mr-1"></i>学生详情列表
            </h3>
            <el-input v-model="searchKeyword" placeholder="搜索学号或姓名..." size="small" prefix-icon="Search" style="width: 200px" clearable />
          </div>
          <el-select v-model="filterStatus" placeholder="状态筛选" size="small" style="width: 110px" clearable>
            <el-option label="答题中" value="answering" />
            <el-option label="已提交" value="submitted" />
            <el-option label="未开始" value="not_started" />
            <el-option label="异常" value="abnormal" />
          </el-select>
        </div>

        <div class="flex-1 overflow-auto">
          <table class="w-full text-sm">
            <thead class="sticky top-0 bg-gray-50 z-10">
              <tr class="text-left text-xs text-gray-500">
                <th class="px-4 py-3 font-medium">学生姓名</th>
                <th class="px-4 py-3 font-medium">学号</th>
                <th class="px-4 py-3 font-medium">状态</th>
                <th class="px-4 py-3 font-medium">进入时间</th>
                <th class="px-4 py-3 font-medium">答题进度</th>
                <th class="px-4 py-3 font-medium">切屏次数</th>
                <th class="px-4 py-3 font-medium">最后活跃</th>
                <th class="px-4 py-3 font-medium text-right">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="student in filteredStudents" :key="student.id" :class="['border-t border-gray-50 transition-colors', student.isAbnormal ? 'bg-red-50/40' : 'hover:bg-gray-50']">
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <div :class="['w-2 h-2 rounded-full', getOnlineDotClass(student.status, student.isAbnormal)]"></div>
                    <span class="font-medium text-gray-800">{{ student.name }}</span>
                  </div>
                </td>
                <td class="px-4 py-3 text-gray-500 font-mono text-xs">{{ student.studentId }}</td>
                <td class="px-4 py-3">
                  <el-tag :type="getStatusTagType(student.status, student.isAbnormal)" size="small" effect="light">{{ getStatusLabel(student.status, student.isAbnormal) }}</el-tag>
                </td>
                <td class="px-4 py-3 text-gray-500 text-xs">{{ student.enterTime }}</td>
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <el-progress :percentage="safePercent(student.progress)" :stroke-width="6" :show-text="false" :color="student.isAbnormal ? '#ef4444' : '#6366f1'" style="width: 80px" />
                    <span class="text-xs text-gray-500">{{ student.answered }}/{{ student.totalQuestions }}</span>
                  </div>
                </td>
                <td class="px-4 py-3">
                  <span :class="['font-medium text-sm', student.switchCount >= 5 ? 'text-danger-500 font-bold' : student.switchCount >= 3 ? 'text-warning-500' : 'text-gray-600']">{{ student.switchCount }}</span>
                </td>
                <td class="px-4 py-3 text-xs text-gray-400">{{ student.lastActive }}</td>
                <td class="px-4 py-3 text-right">
                  <div class="flex items-center justify-end gap-1">
                    <el-button size="small" type="primary" link @click="handleViewDetail(student)">
                      <i class="ri-eye-line"></i>
                    </el-button>
                    <el-button size="small" type="danger" link @click="handleForceSubmit(student)" :disabled="student.status === 'submitted'">
                      <i class="ri-stop-circle-line"></i>
                    </el-button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredStudents.length === 0">
                <td colspan="8" class="px-4 py-12 text-center text-gray-400">
                  <i class="ri-user-unfollow-line text-3xl mb-2 block"></i>
                  <p class="text-sm">暂无学生数据</p>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="p-3 border-t border-gray-100 bg-gray-50/50 flex items-center justify-between text-xs text-gray-500">
          <span>共 {{ filteredStudents.length }} 名学生</span>
          <div class="flex items-center gap-4">
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-green-400"></span>正常</span>
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-yellow-400"></span>警告</span>
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-red-400"></span>异常</span>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="detailDialogVisible" :title="`答题详情 - ${currentStudent?.name || '-'}`" width="700px" destroy-on-close>
      <div v-if="currentStudent" class="space-y-4">
        <div class="bg-gray-50 rounded-lg p-4 flex items-center justify-between">
          <div class="flex items-center gap-4 text-sm">
            <span>学号：<strong>{{ currentStudent.studentId }}</strong></span>
            <span>状态：<el-tag :type="getStatusTagType(currentStudent.status, currentStudent.isAbnormal)" size="small">{{ getStatusLabel(currentStudent.status, currentStudent.isAbnormal) }}</el-tag></span>
            <span>切屏：<strong :class="currentStudent.switchCount >= 3 ? 'text-danger-500' : ''">{{ currentStudent.switchCount }}</strong> 次</span>
          </div>
        </div>
        <div class="space-y-2">
          <div v-for="(ans, idx) in studentAnswers" :key="idx" class="border border-gray-100 rounded-lg p-3">
            <div class="flex items-start justify-between mb-2">
              <span class="text-sm font-medium text-gray-700">第 {{ ans.questionNum }} 题（{{ ans.typeName }}）</span>
              <el-tag :type="ans.isCorrect === true ? 'success' : ans.isCorrect === false ? 'danger' : 'info'" size="small" effect="light">
                {{ ans.isCorrect === true ? '正确' : ans.isCorrect === false ? '错误' : '待判定' }}
              </el-tag>
            </div>
            <p class="text-sm text-gray-600 mb-2">{{ ans.questionContent }}</p>
            <div class="bg-primary-50 rounded p-2 text-sm">
              <span class="text-xs text-primary-500 font-medium">学生答案：</span>
              <span class="text-gray-700">{{ ans.studentAnswer || '(未作答)' }}</span>
            </div>
            <div v-if="ans.correctAnswer" class="bg-green-50 rounded p-2 text-sm mt-2">
              <span class="text-xs text-green-600 font-medium">标准答案：</span>
              <span class="text-gray-700">{{ ans.correctAnswer }}</span>
            </div>
            <div class="text-xs text-gray-400 mt-2">得分：{{ ans.scoreDisplay }}</div>
          </div>
          <div v-if="studentAnswers.length === 0" class="rounded-xl border border-dashed border-gray-200 py-8 text-center text-sm text-gray-400">暂无逐题详情</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { finishExam, forceSubmit, getExamDetail, getMonitorData } from '@/api/teacher'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const currentExamId = Number(route.params.examId || 0)

const loading = ref(false)
const refreshing = ref(false)
const autoRefresh = ref(true)

const examInfo = ref({
  name: '',
  courseName: '',
  timeRange: '-',
  participationRate: 0,
  endTime: '',
})

const countdown = ref('--:--:--')
const countdownWarning = ref(false)
let countdownTimer: ReturnType<typeof setInterval> | null = null
let refreshTimer: ReturnType<typeof setInterval> | null = null

interface StudentMonitor {
  id: number
  recordId: number
  name: string
  studentId: string
  status: string
  enterTime: string
  answered: number
  totalQuestions: number
  progress: number
  switchCount: number
  lastActive: string
  isAbnormal: boolean
  warningReason?: string
}

const students = ref<StudentMonitor[]>([])
const searchKeyword = ref('')
const filterStatus = ref('')

const filteredStudents = computed(() => {
  let list = students.value
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(s => s.name.toLowerCase().includes(kw) || s.studentId.includes(kw))
  }
  if (filterStatus.value) {
    if (filterStatus.value === 'abnormal') return list.filter(s => s.isAbnormal)
    list = list.filter(s => s.status === filterStatus.value)
  }
  return list
})

const statusStats = computed(() => {
  const total = students.value.length || 1
  const submitted = students.value.filter(s => s.status === 'submitted').length
  const answering = students.value.filter(s => s.status === 'answering').length
  const notStarted = students.value.filter(s => s.status === 'not_started').length
  const abnormal = students.value.filter(s => s.isAbnormal).length
  const online = students.value.filter(s => ['answering', 'submitted'].includes(s.status)).length
  return [
    { key: 'online', label: '在线人数', count: online, percent: Math.round((online / total) * 100), icon: 'ri-wifi-line', iconBg: 'bg-blue-50', iconColor: 'text-blue-500', bgClass: 'bg-blue-50/50' },
    { key: 'submitted', label: '已提交', count: submitted, percent: Math.round((submitted / total) * 100), icon: 'ri-check-double-line', iconBg: 'bg-green-50', iconColor: 'text-green-500', bgClass: 'bg-green-50/50' },
    { key: 'answering', label: '答题中', count: answering, percent: Math.round((answering / total) * 100), icon: 'ri-edit-2-line', iconBg: 'bg-purple-50', iconColor: 'text-purple-500', bgClass: 'bg-purple-50/50' },
    { key: 'not_started', label: '未开始', count: notStarted, percent: Math.round((notStarted / total) * 100), icon: 'ri-time-line', iconBg: 'bg-gray-50', iconColor: 'text-gray-400', bgClass: 'bg-gray-50/50' },
    { key: 'abnormal', label: '异常', count: abnormal, percent: Math.round((abnormal / total) * 100), icon: 'ri-error-warning-line', iconBg: 'bg-red-50', iconColor: 'text-red-500', bgClass: 'bg-red-50/50' },
  ]
})

const abnormalStudents = computed(() => students.value.filter(s => s.isAbnormal))

const detailDialogVisible = ref(false)
const currentStudent = ref<StudentMonitor | null>(null)
const studentAnswers = ref<Array<{ questionNum: number; typeName: string; questionContent: string; studentAnswer: string; correctAnswer: string; isCorrect: boolean | null; scoreDisplay: string }>>([])

function goBack() {
  router.back()
}

function mapStatus(status: string) {
  if (status === 'submitted') return 'submitted'
  if (status === 'ongoing') return 'answering'
  return 'not_started'
}

function formatTime(time: string) {
  if (!time) return '-'
  return time.replace('T', ' ').slice(11, 19)
}

function formatTimeAgo(time: string) {
  if (!time) return '-'
  const date = new Date(time)
  const diff = Math.max(0, Math.floor((Date.now() - date.getTime()) / 1000))
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  return `${Math.floor(diff / 86400)}天前`
}

function safePercent(value: unknown) {
  const percent = Number(value)
  if (!Number.isFinite(percent)) return 0
  return Math.max(0, Math.min(100, Math.round(percent)))
}

function startCountdown() {
  if (countdownTimer) clearInterval(countdownTimer)
  countdownTimer = setInterval(() => {
    if (!examInfo.value.endTime) {
      countdown.value = '--:--:--'
      return
    }
    const diff = new Date(examInfo.value.endTime).getTime() - Date.now()
    if (diff <= 0) {
      countdown.value = '00:00:00'
      countdownWarning.value = true
      return
    }
    const totalSeconds = Math.floor(diff / 1000)
    countdownWarning.value = totalSeconds <= 900
    const h = String(Math.floor(totalSeconds / 3600)).padStart(2, '0')
    const m = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0')
    const s = String(totalSeconds % 60).padStart(2, '0')
    countdown.value = `${h}:${m}:${s}`
  }, 1000)
}

async function loadExamInfo() {
  const detail: any = await getExamDetail(currentExamId)
  examInfo.value = {
    name: detail?.name || '',
    courseName: detail?.courseName || detail?.course || '',
    timeRange: `${detail?.startTime || '-'} ~ ${detail?.endTime || '-'}`,
    participationRate: detail?.studentCount ? Math.round((Number(detail.submitCount || 0) / Number(detail.studentCount || 1)) * 100) : 0,
    endTime: detail?.endTime || '',
  }
  startCountdown()
}

async function loadMonitorDataAction() {
  loading.value = true
  refreshing.value = true
  try {
    const res: any = await getMonitorData(currentExamId)
    const list = Array.isArray(res) ? res : (res?.list || [])
    students.value = list.map((item: any, index: number) => {
      const status = mapStatus(String(item.status || 'pending'))
      const answered = Number(item.answeredCount || 0)
      const totalQuestions = Number(item.totalQuestions || 0)
      const switchCount = Number(item.switchScreenCount || 0)
      const isAbnormal = switchCount >= 5
      return {
        id: index + 1,
        recordId: Number(item.examRecordId || item.recordId || item.id || index + 1),
        name: item.studentName || `学生${item.studentId}`,
        studentId: String(item.studentId || ''),
        status,
        enterTime: formatTime(item.startTime || ''),
        answered,
        totalQuestions,
        progress: totalQuestions > 0 ? Math.round((answered / totalQuestions) * 100) : 0,
        switchCount,
        lastActive: formatTimeAgo(item.lastActiveTime || ''),
        isAbnormal,
        warningReason: isAbnormal ? '频繁切屏' : undefined,
      }
    })
  } catch (error) {
    console.error('加载监控数据失败', error)
    students.value = []
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

function refreshData() {
  loadMonitorDataAction()
}

function getStatusTagType(status: string, isAbnormal: boolean): '' | 'success' | 'warning' | 'info' | 'danger' {
  if (isAbnormal) return 'danger'
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    answering: 'warning',
    submitted: 'success',
    not_started: 'info',
  }
  return map[status] || 'info'
}

function getStatusLabel(status: string, isAbnormal: boolean) {
  if (isAbnormal) return '异常'
  const map: Record<string, string> = {
    answering: '答题中',
    submitted: '已提交',
    not_started: '未开始',
  }
  return map[status] || status
}

function getOnlineDotClass(status: string, isAbnormal: boolean) {
  if (isAbnormal) return 'bg-red-400'
  const map: Record<string, string> = {
    answering: 'bg-yellow-400',
    submitted: 'bg-green-400',
    not_started: 'bg-gray-300',
  }
  return map[status] || 'bg-gray-300'
}

async function loadStudentAnswers(studentId: string) {
  try {
    const target = students.value.find(item => item.studentId === studentId)
    if (!target?.recordId) {
      studentAnswers.value = []
      return
    }
    const detail: any = await request.get(`/examRecords/${target.recordId}`)
    const answers = Array.isArray(detail?.answers) ? detail.answers : []
    studentAnswers.value = answers.map((answer: any, index: number) => ({
      questionNum: Number(answer.sortOrder || index + 1),
      typeName: getQuestionTypeName(answer.questionType),
      questionContent: answer.questionContent || '',
      studentAnswer: answer.studentAnswer || '',
      correctAnswer: answer.correctAnswer || '',
      isCorrect: answer.isCorrect ?? null,
      scoreDisplay: `${answer.score ?? 0} / ${answer.fullScore ?? '-'}`,
    }))
  } catch (error) {
    console.error('加载学生答题详情失败', error)
    studentAnswers.value = []
  }
}

function getQuestionTypeName(type: string) {
  const map: Record<string, string> = {
    single_choice: '单选题',
    multi_choice: '多选题',
    true_false: '判断题',
    fill_blank: '填空题',
    essay: '简答题',
    code: '编程题',
  }
  return map[type] || type || '题目'
}

function handleViewDetail(student: StudentMonitor) {
  currentStudent.value = student
  detailDialogVisible.value = true
  loadStudentAnswers(student.studentId)
}

function handleForceSubmit(student: StudentMonitor) {
  ElMessageBox.confirm(`确定强制让 ${student.name} 交卷吗？此操作不可撤销。`, '强制交卷确认', {
    confirmButtonText: '确认强制交卷',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await forceSubmit(currentExamId, Number(student.studentId))
      ElMessage.success(`已强制 ${student.name} 交卷`)
      await loadMonitorDataAction()
      await loadExamInfo()
    } catch (error) {
      console.error('强制交卷失败', error)
      ElMessage.error('强制交卷失败')
    }
  }).catch(() => {})
}

function handleFinishExam() {
  ElMessageBox.confirm('确定立即结束本场考试吗？系统会强制未提交学生交卷，并为主观题生成待阅任务。', '结束考试', {
    confirmButtonText: '确认结束',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res: any = await finishExam(currentExamId)
      ElMessage.success(`考试已结束，已结算 ${res?.settledCount || 0} 份答卷，生成 ${res?.gradingTaskCount || 0} 条待阅任务`)
      router.push('/teacher/grading')
    } catch (error) {
      console.error('结束考试失败', error)
      ElMessage.error('结束考试失败')
    }
  }).catch(() => {})
}

onMounted(async () => {
  if (!currentExamId) {
    ElMessage.error('无效的考试ID')
    router.back()
    return
  }
  await loadExamInfo()
  await loadMonitorDataAction()
  refreshTimer = setInterval(() => {
    if (autoRefresh.value) {
      loadMonitorDataAction()
      loadExamInfo()
    }
  }, 10000)
})

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>
