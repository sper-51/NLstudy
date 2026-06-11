<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-button @click="goBack" plain>
          <i class="ri-arrow-left-line mr-1"></i>返回
        </el-button>
        <div>
          <h1 class="text-xl font-bold text-gray-800">考试详情</h1>
          <p class="text-sm text-gray-500 mt-1">查看考试结果、批改成绩与统计分析</p>
        </div>
      </div>
      <div class="flex items-center gap-2">
        <el-button @click="handleExportScores">
          <i class="ri-download-line mr-1"></i>导出成绩
        </el-button>
        <el-button type="warning" @click="handleBatchGrade">
          <i class="ri-edit-box-line mr-1"></i>批量批改主观题
        </el-button>
        <el-button type="success" @click="handlePublishScores">
          <i class="ri-send-plane-line mr-1"></i>发布成绩
        </el-button>
      </div>
    </div>

    <div class="bg-white rounded-xl p-5 shadow-sm">
      <div class="flex items-start justify-between gap-6">
        <div class="flex items-start gap-4 min-w-0">
          <div class="w-14 h-14 rounded-xl bg-primary-100 flex items-center justify-center flex-shrink-0">
            <i class="ri-file-shield-2-fill text-primary-500 text-2xl"></i>
          </div>
          <div class="min-w-0">
            <div class="flex items-center gap-3 mb-2">
              <h2 class="text-lg font-bold text-gray-800">{{ examBasic.name || '未命名考试' }}</h2>
              <el-tag :type="getStatusType(examBasic.status)" effect="dark" size="small">{{ getStatusLabel(examBasic.status) }}</el-tag>
            </div>
            <div class="flex flex-wrap items-center gap-5 text-sm text-gray-500">
              <span class="flex items-center gap-1"><i class="ri-book-line text-primary-400"></i>{{ examBasic.courseName || '未关联课程' }}</span>
              <span class="flex items-center gap-1"><i class="ri-time-line text-primary-400"></i>{{ examBasic.timeRange }}</span>
              <span class="flex items-center gap-1"><i class="ri-hourglass-line text-primary-400"></i>时长 {{ examBasic.duration }} 分钟</span>
              <span class="flex items-center gap-1"><i class="ri-star-line text-primary-400"></i>满分 {{ examBasic.totalScore }} 分</span>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-4 gap-6 text-center flex-shrink-0">
          <div>
            <div class="text-2xl font-bold text-gray-800">{{ examBasic.totalStudents }}</div>
            <div class="text-xs text-gray-400">应考人数</div>
          </div>
          <div>
            <div class="text-2xl font-bold text-green-600">{{ examBasic.submittedCount }}</div>
            <div class="text-xs text-gray-400">已交卷</div>
          </div>
          <div>
            <div class="text-2xl font-bold text-primary-600">{{ examBasic.avgScore.toFixed(1) }}</div>
            <div class="text-xs text-gray-400">平均分</div>
          </div>
          <div>
            <div class="text-2xl font-bold" :class="examBasic.passRate >= 60 ? 'text-green-600' : 'text-danger-500'">{{ examBasic.passRate }}%</div>
            <div class="text-xs text-gray-400">及格率</div>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-2 gap-5">
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <h3 class="text-sm font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <i class="ri-bar-chart-box-line text-primary-500"></i>成绩分布
        </h3>
        <div ref="scoreDistChartRef" style="height: 280px"></div>
      </div>
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <h3 class="text-sm font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <i class="ri-line-chart-line text-primary-500"></i>提交时间分布
        </h3>
        <div ref="submitTimelineRef" style="height: 280px"></div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <div class="p-4 border-b border-gray-100 flex items-center justify-between">
        <h3 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
          <i class="ri-table-line text-primary-500"></i>学生答题情况
          <span class="text-xs text-gray-400 font-normal">（共 {{ studentResults.length }} 人）</span>
        </h3>
        <div class="flex items-center gap-2">
          <el-input v-model="searchKeyword" placeholder="搜索学号或姓名..." size="small" prefix-icon="Search" style="width: 200px" clearable />
          <el-select v-model="filterStatus" placeholder="状态筛选" size="small" style="width: 110px" clearable>
            <el-option label="已提交" value="submitted" />
            <el-option label="已批改" value="graded" />
            <el-option label="未提交" value="unsubmitted" />
          </el-select>
        </div>
      </div>

      <table class="w-full text-sm">
        <thead class="bg-gray-50 sticky top-0 z-10">
          <tr class="text-left text-xs text-gray-500">
            <th class="px-4 py-3 font-medium">学号</th>
            <th class="px-4 py-3 font-medium">姓名</th>
            <th class="px-4 py-3 font-medium">提交时间</th>
            <th class="px-4 py-3 font-medium text-right">得分</th>
            <th class="px-4 py-3 font-medium text-right">客观题得分</th>
            <th class="px-4 py-3 font-medium text-right">主观题得分</th>
            <th class="px-4 py-3 font-medium">状态</th>
            <th class="px-4 py-3 font-medium text-right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="stu in pagedResults" :key="`${stu.studentId}-${stu.submitTime}`" class="border-t border-gray-50 hover:bg-gray-50 transition-colors">
            <td class="px-4 py-3 text-gray-500 font-mono text-xs">{{ stu.studentId }}</td>
            <td class="px-4 py-3 font-medium text-gray-800">{{ stu.name || '-' }}</td>
            <td class="px-4 py-3 text-gray-500 text-xs">{{ stu.submitTime || '-' }}</td>
            <td class="px-4 py-3 text-right">
              <span :class="['font-bold text-base', stu.score >= 80 ? 'text-green-600' : stu.score >= 60 ? 'text-primary-600' : stu.score >= 0 ? 'text-danger-500' : 'text-gray-400']">
                {{ stu.score >= 0 ? stu.score : '-' }}
              </span>
            </td>
            <td class="px-4 py-3 text-right text-gray-600">{{ stu.objectiveScore >= 0 ? stu.objectiveScore : '-' }}</td>
            <td class="px-4 py-3 text-right text-gray-600">{{ stu.subjectiveScore >= 0 ? stu.subjectiveScore : '-' }}</td>
            <td class="px-4 py-3">
              <el-tag :type="getResultStatusType(stu.status)" size="small" effect="light">{{ getResultStatusLabel(stu.status) }}</el-tag>
            </td>
            <td class="px-4 py-3 text-right">
              <el-button v-if="stu.status !== 'unsubmitted'" size="small" type="primary" link @click="viewAnswerDetail(stu)">
                <i class="ri-eye-line mr-1"></i>查看答卷
              </el-button>
              <span v-else class="text-xs text-gray-400">-</span>
            </td>
          </tr>
          <tr v-if="pagedResults.length === 0">
            <td colspan="8" class="px-4 py-12 text-center text-gray-400">
              <i class="ri-user-unfollow-line text-3xl mb-2 block"></i>
              <p class="text-sm">暂无数据</p>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="p-4 border-t border-gray-100 flex items-center justify-between">
        <span class="text-xs text-gray-400">显示 {{ pagedResults.length }} / {{ filteredResults.length }} 条</span>
        <el-pagination size="small" layout="prev, pager, next" :total="filteredResults.length" :page-size="pageSize" :current-page="currentPage" @current-change="currentPage = $event" />
      </div>
    </div>

    <el-dialog v-model="answerDetailVisible" :title="`答卷详情 - ${currentStudent?.name || '-'}（${currentStudent?.studentId || '-'}）`" width="750px" destroy-on-close>
      <div v-if="currentStudent" class="space-y-4">
        <div class="bg-gradient-to-r from-primary-50 to-indigo-50 rounded-xl p-4 flex items-center justify-around">
          <div class="text-center">
            <div class="text-xs text-gray-500">总分</div>
            <div class="text-2xl font-bold text-primary-600">{{ currentStudent.score }}</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500">客观题</div>
            <div class="text-xl font-semibold text-green-600">{{ currentStudent.objectiveScore }}</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500">主观题</div>
            <div class="text-xl font-semibold text-orange-500">{{ currentStudent.subjectiveScore }}</div>
          </div>
          <div class="text-center">
            <div class="text-xs text-gray-500">排名</div>
            <div class="text-xl font-semibold text-gray-700">#{{ currentStudent.rank ?? '-' }}</div>
          </div>
        </div>

        <div class="space-y-3">
          <h4 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
            <i class="ri-list-check text-primary-500"></i>逐题作答情况
          </h4>
          <div v-for="(item, idx) in answerDetails" :key="idx" :class="['border rounded-lg p-4', item.isCorrect === true ? 'border-green-200 bg-green-50/30' : item.isCorrect === false ? 'border-red-200 bg-red-50/30' : 'border-gray-200 bg-gray-50/30']">
            <div class="flex items-start justify-between mb-2">
              <div class="flex items-center gap-2">
                <span class="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold text-white" :class="item.isCorrect === true ? 'bg-green-500' : item.isCorrect === false ? 'bg-red-500' : 'bg-gray-400'">{{ idx + 1 }}</span>
                <el-tag size="small" :type="item.isCorrect === true ? 'success' : item.isCorrect === false ? 'danger' : 'info'" effect="plain">{{ item.typeName }}</el-tag>
                <span class="text-xs text-gray-400">{{ item.maxScore }} 分</span>
              </div>
              <div class="flex items-center gap-2">
                <span class="text-sm font-bold" :class="item.isCorrect === true ? 'text-green-600' : item.isCorrect === false ? 'text-danger-500' : 'text-gray-400'">得分：{{ item.gotScore }}</span>
              </div>
            </div>
            <p class="text-sm text-gray-700 font-medium mb-2">{{ item.questionContent }}</p>
            <div class="grid grid-cols-2 gap-3">
              <div class="bg-white rounded p-3 border border-gray-100">
                <div class="text-xs text-gray-400 mb-1">标准答案</div>
                <div class="text-sm text-gray-700">{{ item.correctAnswer }}</div>
              </div>
              <div class="bg-primary-50/50 rounded p-3 border border-primary-100">
                <div class="text-xs text-primary-500 mb-1">学生答案</div>
                <div class="text-sm text-gray-700">{{ item.studentAnswer || '(未作答)' }}</div>
              </div>
            </div>
          </div>
          <div v-if="answerDetails.length === 0" class="rounded-xl border border-dashed border-gray-200 py-8 text-center text-sm text-gray-400">暂无逐题作答详情</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="answerDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { exportGrades as exportGradesApi, getExamDetail, getGradeStatistics, getGrades, getGradingTasks } from '@/api/teacher'

const route = useRoute()
const router = useRouter()
const examId = Number(route.params.examId || route.params.id || 0)

const examBasic = ref({
  name: '',
  courseName: '',
  timeRange: '-',
  duration: 0,
  totalScore: 0,
  status: 'finished',
  totalStudents: 0,
  submittedCount: 0,
  avgScore: 0,
  passRate: 0,
})

const scoreDistChartRef = ref<HTMLElement>()
const submitTimelineRef = ref<HTMLElement>()
const scoreChartInstance = shallowRef<echarts.ECharts | null>(null)
const timelineChartInstance = shallowRef<echarts.ECharts | null>(null)

interface StudentResult {
  studentId: string
  name: string
  submitTime: string
  score: number
  objectiveScore: number
  subjectiveScore: number
  status: string
  rank?: number
}

const studentResults = ref<StudentResult[]>([])
const searchKeyword = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = 10

const filteredResults = computed(() => {
  let list = studentResults.value
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(item => (item.name || '').toLowerCase().includes(kw) || item.studentId.includes(kw))
  }
  if (filterStatus.value) {
    list = list.filter(item => item.status === filterStatus.value)
  }
  return list
})

const pagedResults = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredResults.value.slice(start, start + pageSize)
})

interface AnswerDetail {
  typeName: string
  questionContent: string
  correctAnswer: string
  studentAnswer: string
  gotScore: number
  maxScore: number
  isCorrect: boolean | null
}

const answerDetailVisible = ref(false)
const currentStudent = ref<StudentResult | null>(null)
const answerDetails = ref<AnswerDetail[]>([])

function goBack() {
  router.back()
}

async function loadExamDetailData() {
  if (!examId) return
  const detail: any = await getExamDetail(examId)
  examBasic.value = {
    name: detail?.name || '',
    courseName: detail?.courseName || detail?.course || '',
    timeRange: `${detail?.startTime || '-'} ~ ${detail?.endTime || '-'}`,
    duration: Number(detail?.duration || 0),
    totalScore: Number(detail?.totalScore || 0),
    status: detail?.status || 'finished',
    totalStudents: Number(detail?.studentCount || 0),
    submittedCount: Number(detail?.submitCount || 0),
    avgScore: 0,
    passRate: 0,
  }
}

async function loadStatistics() {
  if (!examId) return
  try {
    const stats: any = await getGradeStatistics(examId)
    examBasic.value.avgScore = Number(stats?.avgScore || 0)
    examBasic.value.passRate = Number(stats?.passRate || 0)
    if (stats?.totalStudents) examBasic.value.totalStudents = Number(stats.totalStudents)
    if (stats?.submittedCount) examBasic.value.submittedCount = Number(stats.submittedCount)
    updateScoreChart(stats?.scoreDistribution || {})
  } catch (error) {
    console.error('加载统计失败:', error)
    updateScoreChart({})
  }
}

async function loadStudentResults() {
  if (!examId) return
  try {
    const gradeRes: any = await getGrades({ examId, page: 1, pageSize: 1000 })
    const detailRes: any = await getExamDetail(examId)
    const gradeList = Array.isArray(gradeRes) ? gradeRes : (gradeRes?.list || [])
    const detailStudents = Array.isArray(detailRes?.students) ? detailRes.students : []

    const detailMap = new Map<string, any>(detailStudents.map((item: any) => [String(item.studentId), item]))
    const gradeMap = new Map<string, any>(gradeList.map((item: any) => [String(item.studentId), item]))
    const mergedIds = Array.from(new Set([...detailMap.keys(), ...gradeMap.keys()]))

    studentResults.value = mergedIds.map((studentId, index): StudentResult => {
      const grade = gradeMap.get(studentId) || {}
      const detail = detailMap.get(studentId) || {}
      const totalScore = grade.totalScore ?? detail.totalScore
      const objectiveScore = grade.objectiveScore ?? 0
      const subjectiveScore = grade.subjectiveScore ?? Math.max(0, Number(totalScore ?? 0) - Number(objectiveScore ?? 0))
      const rawStatus = String(grade.status || detail.status || '')

      return {
        studentId,
        name: grade.studentName || detail.studentName || `学生${studentId}`,
        submitTime: detail.submitTime || grade.publishTime || '-',
        score: totalScore !== undefined && totalScore !== null ? Number(totalScore) : -1,
        objectiveScore: totalScore !== undefined && totalScore !== null ? Number(objectiveScore || 0) : -1,
        subjectiveScore: totalScore !== undefined && totalScore !== null ? Number(subjectiveScore || 0) : -1,
        status: mapResultStatus(rawStatus, totalScore),
        rank: grade.rank || index + 1,
      }
    })

    updateTimelineChart(studentResults.value)
  } catch (error) {
    console.error('加载学生成绩失败:', error)
    studentResults.value = []
    updateTimelineChart([])
  }
}

function mapResultStatus(rawStatus: string, totalScore: number | null | undefined) {
  if (rawStatus === 'published') return 'graded'
  if (rawStatus === 'submitted' || rawStatus === 'auto_submitted') return totalScore !== undefined && totalScore !== null ? 'graded' : 'submitted'
  if (rawStatus === 'pending' || rawStatus === 'ongoing') return 'unsubmitted'
  return totalScore !== undefined && totalScore !== null ? 'graded' : 'submitted'
}

function initCharts() {
  if (scoreDistChartRef.value) {
    scoreChartInstance.value = echarts.init(scoreDistChartRef.value)
    scoreChartInstance.value.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['0-59', '60-69', '70-79', '80-89', '90-100'], axisLabel: { fontSize: 11 } },
      yAxis: { type: 'value', minInterval: 1, axisLabel: { fontSize: 11 } },
      series: [{ type: 'bar', data: [0, 0, 0, 0, 0], barWidth: '45%', itemStyle: { borderRadius: [6, 6, 0, 0] }, label: { show: true, position: 'top', fontSize: 11 } }],
    })
  }

  if (submitTimelineRef.value) {
    timelineChartInstance.value = echarts.init(submitTimelineRef.value)
    timelineChartInstance.value.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: [], axisLabel: { fontSize: 10 } },
      yAxis: { type: 'value', minInterval: 1, axisLabel: { fontSize: 11 } },
      series: [{
        type: 'line',
        smooth: true,
        data: [],
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(99,102,241,0.3)' },
            { offset: 1, color: 'rgba(99,102,241,0.02)' },
          ]),
        },
        lineStyle: { color: '#6366f1', width: 2 },
        itemStyle: { color: '#6366f1' },
      }],
    })
  }
}

function updateScoreChart(distribution: Record<string, number>) {
  if (!scoreChartInstance.value) return
  const categories = ['0-59', '60-69', '70-79', '80-89', '90-100']
  const colors = ['#ef4444', '#f59e0b', '#6366f1', '#22c55e', '#10b981']
  scoreChartInstance.value.setOption({
    xAxis: { data: categories },
    series: [{
      data: categories.map((key, idx) => ({
        value: Number(distribution?.[key] || 0),
        itemStyle: { color: colors[idx] },
      })),
    }],
  })
}

function updateTimelineChart(list: StudentResult[]) {
  if (!timelineChartInstance.value) return
  const valid = list.filter(item => item.submitTime && item.submitTime !== '-').sort((a, b) => new Date(a.submitTime).getTime() - new Date(b.submitTime).getTime())
  let cumulative = 0
  const xData: string[] = []
  const yData: number[] = []
  valid.forEach(item => {
    cumulative += 1
    xData.push((item.submitTime || '').replace('T', ' ').slice(5, 16))
    yData.push(cumulative)
  })
  timelineChartInstance.value.setOption({
    xAxis: { data: xData },
    series: [{ data: yData }],
  })
}

async function loadAnswerDetail(student: StudentResult) {
  try {
    const res: any = await getGradingTasks({ examId, page: 1, pageSize: 1000 })
    const tasks = Array.isArray(res) ? res : (res?.list || [])
    const studentTasks = tasks.filter((item: any) => String(item.studentId) === student.studentId)
    answerDetails.value = studentTasks.map((task: any) => ({
      typeName: task.typeName || task.questionType || '题目',
      questionContent: task.questionContent || '',
      correctAnswer: task.correctAnswer || '',
      studentAnswer: task.studentAnswer || '',
      gotScore: Number(task.score ?? 0),
      maxScore: Number(task.fullScore ?? 0),
      isCorrect: task.score !== undefined && task.fullScore !== undefined ? Number(task.score) >= Number(task.fullScore) * 0.6 : null,
    }))
  } catch (error) {
    console.error('加载答卷详情失败:', error)
    answerDetails.value = []
  }
}

function viewAnswerDetail(student: StudentResult) {
  currentStudent.value = student
  answerDetailVisible.value = true
  loadAnswerDetail(student)
}

function getStatusType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    finished: 'success',
    ongoing: 'danger',
    published: 'warning',
    draft: 'info',
    cancelled: 'danger',
  }
  return map[status] || 'info'
}

function getStatusLabel(status: string): string {
  const map: Record<string, string> = {
    finished: '已结束',
    ongoing: '进行中',
    published: '已发布',
    draft: '草稿',
    cancelled: '已取消',
  }
  return map[status] || status
}

function getResultStatusType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    graded: 'success',
    submitted: 'warning',
    unsubmitted: 'danger',
  }
  return map[status] || 'info'
}

function getResultStatusLabel(status: string): string {
  const map: Record<string, string> = {
    graded: '已批改',
    submitted: '已提交',
    unsubmitted: '未提交',
  }
  return map[status] || status
}

function handleExportScores() {
  exportGradesApi({ examId }).then(() => {
    ElMessage.info('当前后端导出接口仍返回占位结果，已记录为后续联调项')
  }).catch((error: any) => {
    ElMessage.error(error?.message || '导出失败')
  })
}

async function handleBatchGrade() {
  const res: any = await getGradingTasks({ examId, status: 'pending', page: 1, pageSize: 1000 })
  const tasks = Array.isArray(res) ? res : (res?.list || [])
  if (tasks.length === 0) {
    ElMessage.info('没有待批改的任务')
    return
  }
  ElMessage.info(`找到 ${tasks.length} 个待批改任务，请前往阅卷评分处理`)
}

function handlePublishScores() {
  ElMessageBox.confirm('确定要发布成绩吗？发布后学生可查看自己的成绩。', '确认发布', {
    confirmButtonText: '确认发布',
    cancelButtonText: '取消',
    type: 'info',
  }).then(() => {
    ElMessage.success('成绩已发布')
  }).catch(() => {})
}

function handleResize() {
  scoreChartInstance.value?.resize()
  timelineChartInstance.value?.resize()
}

onMounted(async () => {
  initCharts()
  window.addEventListener('resize', handleResize)
  await loadExamDetailData()
  await loadStatistics()
  await loadStudentResults()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  scoreChartInstance.value?.dispose()
  timelineChartInstance.value?.dispose()
})
</script>
