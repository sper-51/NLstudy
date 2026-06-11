<template>
  <div class="h-full overflow-auto p-6">
    <!-- 顶部：课程选择 + 考试选择 -->
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-xl font-bold text-gray-800">成绩报告</h1>
      <div class="flex items-center gap-3">
        <!-- 课程选择（加课码维度） -->
        <div class="flex items-center gap-2">
          <label class="text-sm text-gray-500 whitespace-nowrap">选择课程</label>
          <select
            v-model="selectedCourseId"
            class="px-4 py-2 border border-gray-200 rounded-lg focus:border-primary-400 outline-none text-sm bg-white min-w-[220px]"
          >
            <option v-for="course in courseList" :key="course.id" :value="course.id">
              {{ formatCourseOption(course) }}
            </option>
          </select>
        </div>
        <!-- 考试选择 -->
        <div class="flex items-center gap-2">
          <label class="text-sm text-gray-500 whitespace-nowrap">考试</label>
          <select
            v-model="selectedExamId"
            :disabled="examList.length === 0"
            class="px-4 py-2 border border-gray-200 rounded-lg focus:border-primary-400 outline-none text-sm bg-white min-w-[200px] disabled:bg-gray-100 disabled:text-gray-400"
          >
            <option v-for="exam in examList" :key="exam.id" :value="exam.id">
              {{ exam.examName }} ({{ formatTime(exam.completedAt || exam.startTime) }})
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- 无数据提示 -->
    <div v-if="!selectedCourseId || examList.length === 0" class="flex flex-col items-center justify-center py-20">
      <div class="w-20 h-20 rounded-full bg-gray-100 flex items-center justify-center mb-4">
        <i class="ri-file-text-line text-3xl text-gray-300"></i>
      </div>
      <p class="text-gray-500 text-base mb-1">暂无成绩数据</p>
      <p class="text-gray-400 text-sm">{{ selectedCourseId ? '该课程下暂无已发布的考试成绩' : '请先选择一门课程查看成绩' }}</p>
    </div>

    <template v-if="selectedCourseId && examList.length > 0 && selectedExamId">
      <!-- 课程信息标签 -->
      <div class="flex items-center gap-3 mb-6">
        <span class="px-3 py-1.5 bg-primary-50 text-primary-600 text-sm font-medium rounded-lg">{{ currentCourse?.name }}</span>
        <span v-if="currentCourse?.code" class="px-3 py-1.5 bg-blue-50 text-blue-600 text-sm rounded-lg">加课码: {{ currentCourse.code }}</span>
        <span class="px-3 py-1.5 bg-gray-100 text-gray-600 text-sm rounded-lg">{{ gradeData.examName }}</span>
        <span class="px-3 py-1.5 bg-gray-100 text-gray-500 text-sm rounded-lg">{{ gradeData.publishTime }}</span>
        <span
          v-if="gradeData.reviewStatus"
          :class="['px-3 py-1.5 text-sm rounded-lg', getReviewStatusClass(gradeData.reviewStatus)]"
        >
          整卷复核：{{ getReviewStatusText(gradeData.reviewStatus) }}
        </span>
      </div>

      <!-- 成绩概览卡片 -->
      <div class="grid grid-cols-5 gap-4 mb-6">
        <div class="bg-white rounded-xl p-4 shadow-sm text-center">
          <div class="text-xs text-gray-500 mb-1">我的得分</div>
          <div :class="['text-3xl font-bold', getScoreColor(gradeData.totalScore)]">{{ gradeData.totalScore }}</div>
          <div class="text-xs text-gray-400 mt-1">满分 {{ gradeData.fullScore || 100 }}</div>
        </div>
        <div class="bg-white rounded-xl p-4 shadow-sm text-center">
          <div class="text-xs text-gray-500 mb-1">班级均分</div>
          <div class="text-2xl font-bold text-gray-700">{{ gradeData.classAvgScore || '--' }}</div>
          <div class="text-xs text-gray-400 mt-1">共 {{ gradeData.totalStudents || 0 }} 人</div>
        </div>
        <div class="bg-white rounded-xl p-4 shadow-sm text-center">
          <div class="text-xs text-gray-500 mb-1">最高分</div>
          <div class="text-2xl font-bold text-success-600">{{ gradeData.maxScore || '--' }}</div>
          <div class="text-xs text-gray-400 mt-1">参考值</div>
        </div>
        <div class="bg-white rounded-xl p-4 shadow-sm text-center">
          <div class="text-xs text-gray-500 mb-1">排名</div>
          <div class="text-2xl font-bold text-primary-600">{{ gradeData.rank ? `#${gradeData.rank}` : '暂无' }}</div>
          <div class="text-xs text-gray-400 mt-1">{{ gradeData.percentile ? `百分位 ${gradeData.percentile}` : '暂无排名数据' }}</div>
        </div>
        <div class="bg-white rounded-xl p-4 shadow-sm text-center">
          <div class="text-xs text-gray-500 mb-1">正确率</div>
          <div class="text-2xl font-bold text-success-600">{{ hasAnswerStats ? `${correctRate}%` : '暂无' }}</div>
          <div class="text-xs text-gray-400 mt-1">{{ hasAnswerStats ? `${gradeData.correctCount || 0}/${gradeData.totalQuestions || 0}` : '暂无答题统计' }}</div>
        </div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm mb-6 flex items-center justify-between">
        <div>
          <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
            <i class="ri-chat-check-line text-primary-500"></i>
            成绩复核
          </h3>
          <p class="text-sm text-gray-500 mt-1">
            如发现成绩、主观题得分或题目判分存在疑问，可提交整卷或单题复核申请，教师处理后成绩会同步更新。
          </p>
        </div>
        <button
          :disabled="isReviewLocked(gradeData.reviewStatus)"
          :class="[
            'px-4 py-2 rounded-lg text-sm font-medium transition-colors',
            isReviewLocked(gradeData.reviewStatus)
              ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
              : 'bg-primary-500 text-white hover:bg-primary-600'
          ]"
          @click="openReviewDialog()"
        >
          {{ isReviewLocked(gradeData.reviewStatus) ? getReviewStatusText(gradeData.reviewStatus) : '申请整卷复核' }}
        </button>
      </div>

      <!-- 图表区域 -->
      <div class="grid grid-cols-2 gap-6 mb-6">
        <!-- 知识点掌握度 -->
        <div class="bg-white rounded-xl p-5 shadow-sm">
          <h3 class="text-base font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <i class="ri-pie-chart-line text-primary-500"></i>
            知识点掌握度
          </h3>
          <div v-if="knowledgePoints.length > 0" ref="knowledgeChartRef" class="w-full h-[300px]"></div>
          <div v-else class="h-[300px] flex flex-col items-center justify-center text-gray-400">
            <i class="ri-pie-chart-line text-3xl mb-2"></i>
            <p class="text-sm">暂无知识点数据</p>
          </div>
        </div>

        <!-- 历史趋势图（本课程内多次考试） -->
        <div class="bg-white rounded-xl p-5 shadow-sm">
          <h3 class="text-base font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <i class="ri-line-chart-line text-primary-500"></i>
            本课程成绩趋势
          </h3>
          <div v-if="historyTrend.length >= 2" ref="trendChartRef" class="w-full h-[300px]"></div>
          <div v-else class="h-[300px] flex flex-col items-center justify-center text-gray-400">
            <i class="ri-line-chart-line text-3xl mb-2"></i>
            <p class="text-sm">暂无趋势数据</p>
            <p class="text-xs mt-1">至少两次同课程成绩后展示趋势</p>
          </div>
        </div>
      </div>

      <!-- 逐题回顾 -->
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
            <i class="ri-file-list-3-line text-primary-500"></i>
            逐题回顾
          </h3>
          <span class="text-xs text-gray-400">共 {{ reviewQuestions.length }} 题 · 答对 {{ reviewQuestions.filter((q: any) => q.isCorrect).length }} 题</span>
        </div>

        <div class="space-y-3">
          <div
            v-for="(question, index) in reviewQuestions"
            :key="question.id"
            :class="['p-4 rounded-lg border-2 transition-colors', question.isCorrect ? 'border-success-100 bg-success-50/20' : 'border-danger-100 bg-danger-50/20']"
          >
            <div class="flex items-start gap-3">
              <span
                :class="['inline-flex items-center justify-center w-7 h-7 rounded-lg text-xs font-bold flex-shrink-0', question.isCorrect ? 'bg-success-100 text-success-600' : 'bg-danger-100 text-danger-600']"
              >
                {{ index + 1 }}
              </span>
              <div class="flex-1 min-w-0">
                <p class="text-sm text-gray-800 mb-2 leading-relaxed">{{ question.content }}</p>

                <div class="flex gap-6 text-xs">
                  <div>
                    <span class="text-gray-500">我的答案：</span>
                    <span :class="[question.isCorrect ? 'text-success-600' : 'text-danger-600', 'font-medium']">
                      {{ displayAnswer(question.studentAnswer) }}
                    </span>
                  </div>
                  <div v-if="!question.isCorrect">
                    <span class="text-gray-500">正确答案：</span>
                    <span class="text-success-600 font-medium">{{ displayAnswer(question.correctAnswer) }}</span>
                  </div>
                </div>

                <div class="mt-2 flex items-center justify-between gap-3">
                  <span class="text-xs text-gray-400">
                    {{ getTypeName(question.type) }} · {{ formatQuestionScore(question.score) }} · {{ formatMyScore(question) }}
                  </span>
                  <div class="flex items-center gap-3">
                    <span
                      v-if="question.reviewStatus"
                      :class="['text-xs px-2 py-1 rounded-full', getReviewStatusClass(question.reviewStatus)]"
                    >
                      复核{{ getReviewStatusText(question.reviewStatus) }}
                    </span>
                    <button
                      :disabled="isReviewLocked(question.reviewStatus)"
                      :class="[
                        'text-xs flex items-center gap-1 transition-colors',
                        isReviewLocked(question.reviewStatus)
                          ? 'text-gray-400 cursor-not-allowed'
                          : 'text-primary-500 hover:text-primary-600 cursor-pointer'
                      ]"
                      @click="openReviewDialog(question)"
                    >
                      <i class="ri-chat-check-line"></i>
                      {{ isReviewLocked(question.reviewStatus) ? '已提交复核' : '申请本题复核' }}
                    </button>
                    <router-link
                      to="/wrong-questions"
                      v-if="!question.isCorrect"
                      class="text-xs text-danger-500 hover:text-danger-600 cursor-pointer flex items-center gap-1"
                    >
                      <i class="ri-error-warning-line"></i> 加入错题本
                    </router-link>
                  </div>
                </div>
              </div>

              <div
                :class="['w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0', question.isCorrect ? 'bg-success-100' : 'bg-danger-100']"
              >
                <i :class="[question.isCorrect ? 'ri-check-line text-success-500' : 'ri-close-line text-danger-500', 'text-sm']"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <el-dialog v-model="reviewDialogVisible" width="520px" :title="reviewTargetQuestion ? '申请本题复核' : '申请整卷复核'">
      <div class="space-y-4">
        <div class="rounded-xl bg-primary-50/70 border border-primary-100 p-4">
          <div class="text-sm font-medium text-gray-800 mb-1">
            {{ gradeData.examName || '当前考试' }}
          </div>
          <div class="text-xs text-gray-500">
            <template v-if="reviewTargetQuestion">
              单题复核 · 原得分 {{ reviewTargetQuestion.myScore || 0 }}/{{ reviewTargetQuestion.score }} 分
            </template>
            <template v-else>
              整卷复核 · 当前总分 {{ gradeData.totalScore || 0 }}/{{ gradeData.fullScore || 100 }} 分
            </template>
          </div>
        </div>
        <div v-if="reviewTargetQuestion" class="rounded-lg bg-gray-50 p-3 text-sm text-gray-700 leading-relaxed max-h-28 overflow-auto">
          {{ reviewTargetQuestion.content }}
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">复核原因</label>
          <el-input
            v-model="reviewReason"
            type="textarea"
            :rows="5"
            maxlength="300"
            show-word-limit
            placeholder="请说明需要复核的原因，例如主观题要点遗漏、分数显示异常、答案判定疑问等"
          />
        </div>
      </div>
      <template #footer>
        <button class="px-4 py-2 rounded-lg text-sm text-gray-600 hover:bg-gray-100" @click="reviewDialogVisible = false">
          取消
        </button>
        <button
          :disabled="submittingReview"
          class="px-4 py-2 rounded-lg text-sm bg-primary-500 text-white hover:bg-primary-600 disabled:opacity-60"
          @click="submitReview"
        >
          {{ submittingReview ? '提交中...' : '提交复核' }}
        </button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import * as echarts from 'echarts'
import { getCourses, getGrades, getGradeDetail, applyGradeReview } from '@/api/student'

const route = useRoute()
const knowledgeChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()

let knowledgeChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null
const loading = ref(false)
const reviewDialogVisible = ref(false)
const reviewTargetQuestion = ref<any | null>(null)
const reviewReason = ref('')
const submittingReview = ref(false)
const routeGradeId = computed(() => Number(route.params.id || 0))
const autoReviewOpened = ref(false)

// ========== 课程列表（加课码维度） ==========
const selectedCourseId = ref<number | null>(null)
const courseList = ref<any[]>([])

const currentCourse = computed(() => courseList.value.find(c => c.id === selectedCourseId.value))

// ========== 该课程下的考试列表 ==========
const selectedExamId = ref<number | null>(null)
const examList = ref<any[]>([])

// ========== 成绩详情 ==========
const gradeDetailData = ref<any>(null)
const gradeData = computed(() => gradeDetailData.value || {})
const correctRate = computed(() => {
  if (!gradeDetailData.value) return 0
  const total = gradeDetailData.value.totalQuestions || 0
  const correct = gradeDetailData.value.correctCount || 0
  return total > 0 ? Math.round((correct / total) * 100 * 10) / 10 : 0
})
const hasAnswerStats = computed(() => (gradeDetailData.value?.totalQuestions || 0) > 0)

// ========== 图表依赖数据 ==========
const knowledgePoints = computed(() => gradeDetailData.value?.knowledgePoints || [])
const historyTrend = computed(() => gradeDetailData.value?.historyTrend || [])
const reviewQuestions = computed(() => gradeDetailData.value?.questions || [])

function isReadableText(value: unknown): value is string {
  const text = String(value ?? '').trim()
  return !!text && !/[ÃÂ�äåæçèéï¼ã€]/.test(text)
}

function displayAnswer(value: unknown) {
  if (value === null || value === undefined || String(value).trim() === '') return '(未作答)'
  return isReadableText(value) ? String(value).trim() : '答案内容存在编码异常，请联系教师复核'
}

function formatCourseOption(course: any) {
  const name = course?.name || '未命名课程'
  return course?.code ? `${name}（${course.code}）` : name
}

function formatQuestionScore(score: unknown) {
  const value = Number(score)
  return Number.isFinite(value) ? `${value}分` : '分值暂无'
}

function formatMyScore(question: any) {
  const myScore = Number(question?.myScore || 0)
  const fullScore = Number(question?.score || 0)
  if (fullScore > 0 && myScore > fullScore) return `得分异常（${myScore}/${fullScore}）`
  return `得${Number.isFinite(myScore) ? myScore : 0}分`
}

// ========== 页面初始化 ==========
onMounted(async () => {
  loading.value = true
  try {
    const res = await getCourses()
    courseList.value = Array.isArray(res) ? res : []
    if (routeGradeId.value > 0) {
      try {
        const detail = await getGradeDetail(routeGradeId.value)
        gradeDetailData.value = detail
        selectedCourseId.value = Number(detail?.courseId || 0) || null
        selectedExamId.value = Number(detail?.examId || 0) || null
        nextTick(initCharts)
        const gradeListRes = await getGrades()
        const gradeList = Array.isArray(gradeListRes) ? gradeListRes : ((gradeListRes as any)?.list || (gradeListRes as any)?.data || [])
        const matchedGrade = gradeList.find((item: any) => Number(item.gradeId || 0) === routeGradeId.value)
        selectedCourseId.value = Number(detail?.courseId || matchedGrade?.courseId || 0) || (courseList.value[0]?.id ?? null)
      } catch {
        selectedCourseId.value = courseList.value[0]?.id ?? null
      }
    } else if (courseList.value.length > 0) {
      selectedCourseId.value = courseList.value[0].id
    }
  } catch (err) {
    console.error('加载课程成绩数据失败', err)
  } finally {
    loading.value = false
  }
  window.addEventListener('resize', handleResize)
})

// ========== 切换课程 → 加载该课程考试列表 ==========
watch(selectedCourseId, async (courseId) => {
  if (!courseId) {
    examList.value = []
    selectedExamId.value = null
    gradeDetailData.value = null
    return
  }
  try {
    if (routeGradeId.value > 0 && Number(gradeDetailData.value?.gradeId || 0) === routeGradeId.value) {
      examList.value = [{
        id: gradeDetailData.value.examId,
        gradeId: gradeDetailData.value.gradeId,
        examName: gradeDetailData.value.examName,
        completedAt: gradeDetailData.value.publishTime,
        courseId,
      }]
      selectedExamId.value = gradeDetailData.value.examId
      return
    }
    const gradesRes = await getGrades({ courseId })
    const grades = Array.isArray(gradesRes) ? gradesRes : (gradesRes?.list || gradesRes?.data || [])
    examList.value = grades.map((g: any) => ({
      id: g.examId ?? g.gradeId,
      gradeId: g.gradeId,
      examName: g.examName,
      completedAt: g.publishTime || g.createTime,
      courseId,
    }))
    if (examList.value.length > 0) {
      const matchedByGradeId = routeGradeId.value > 0 ? examList.value.find((item: any) => Number(item.gradeId || 0) === routeGradeId.value) : null
      selectedExamId.value = matchedByGradeId?.id || examList.value[0].id
    } else {
      selectedExamId.value = null
    }
  } catch (err) {
    console.error('加载考试列表失败', err)
    examList.value = []
  }
}, { immediate: true })

// ========== 切换考试 → 加载成绩详情 ==========
watch(selectedExamId, async (examId) => {
  if (!examId) {
    gradeDetailData.value = null
    return
  }
  try {
    const currentExam = examList.value.find((item: any) => Number(item.id) === Number(examId))
    const gradeId = Number(currentExam?.gradeId || routeGradeId.value || 0)
    if (!gradeId) {
      gradeDetailData.value = null
      return
    }
    const detail = await getGradeDetail(gradeId)
    gradeDetailData.value = detail
    nextTick(() => {
      initCharts()
      openReviewFromQuery()
    })
  } catch (err) {
    console.error('加载成绩详情失败', err)
    gradeDetailData.value = null
  }
}, { immediate: false })

function formatTime(time: string): string {
  return dayjs(time).format('MM-DD')
}

function getScoreColor(score: number): string {
  if (score >= 90) return 'text-success-500'
  if (score >= 80) return 'text-primary-500'
  if (score >= 70) return 'text-warning-500'
  return 'text-danger-500'
}

function getTypeName(type: string): string {
  const map: Record<string, string> = {
    single_choice: '单选题',
    multi_choice: '多选题',
    true_false: '判断题',
    fill_blank: '填空题',
    essay: '简答题',
  }
  return map[type] || type
}

function getReviewStatusText(status?: string): string {
  const map: Record<string, string> = {
    pending: '待处理',
    approved: '已通过',
    rejected: '已驳回',
    resolved: '已处理',
  }
  return status ? (map[status] || status) : '未申请'
}

function getReviewStatusClass(status?: string): string {
  const map: Record<string, string> = {
    pending: 'bg-warning-50 text-warning-600',
    approved: 'bg-success-50 text-success-600',
    rejected: 'bg-danger-50 text-danger-600',
    resolved: 'bg-blue-50 text-blue-600',
  }
  return status ? (map[status] || 'bg-gray-100 text-gray-600') : 'bg-gray-100 text-gray-500'
}

function isReviewLocked(status?: string): boolean {
  return status === 'pending' || status === 'approved'
}

function openReviewDialog(question?: any) {
  const status = question?.reviewStatus || gradeData.value.reviewStatus
  if (isReviewLocked(status)) {
    ElMessage.info(`该复核${getReviewStatusText(status)}，不能重复提交`)
    return
  }
  reviewTargetQuestion.value = question || null
  reviewReason.value = ''
  reviewDialogVisible.value = true
}

function openReviewFromQuery() {
  if (autoReviewOpened.value || route.query.review !== '1' || !gradeData.value.gradeId) return
  autoReviewOpened.value = true
  if (isReviewLocked(gradeData.value.reviewStatus)) return
  openReviewDialog()
}

async function submitReview() {
  const reason = reviewReason.value.trim()
  if (reason.length < 6) {
    ElMessage.warning('请填写至少 6 个字的复核原因')
    return
  }
  const gradeId = Number(gradeData.value.gradeId || 0)
  if (!gradeId) {
    ElMessage.error('成绩记录不存在，无法提交复核')
    return
  }
  submittingReview.value = true
  try {
    await applyGradeReview(gradeId, {
      questionId: reviewTargetQuestion.value?.id,
      reason,
    })
    ElMessage.success('复核申请已提交，等待教师处理')
    reviewDialogVisible.value = false
    const detail = await getGradeDetail(gradeId)
    gradeDetailData.value = detail
  } catch (err: any) {
    ElMessage.error(err?.message || '提交复核失败')
  } finally {
    submittingReview.value = false
  }
}

// ========== 图表初始化 ==========
function initKnowledgeChart() {
  if (!knowledgeChartRef.value || knowledgePoints.value.length === 0) return
  knowledgeChart?.dispose()
  knowledgeChart = echarts.init(knowledgeChartRef.value)

  const items = knowledgePoints.value
    .map((kp: any) => ({ name: kp.name || '未命名知识点', value: Number(kp.value || 0) }))
    .sort((a: any, b: any) => a.value - b.value)

  knowledgeChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter(params: any) {
        const item = params?.[0]
        return `${item?.name || ''}<br/>掌握度：<b>${item?.value ?? 0}%</b>`
      },
    },
    grid: { left: 90, right: 28, top: 12, bottom: 24 },
    xAxis: {
      type: 'value',
      min: 0,
      max: 100,
      alignTicks: false,
      axisLabel: { formatter: '{value}%', color: '#9ca3af', fontSize: 11 },
      splitLine: { lineStyle: { color: '#f3f4f6' } },
    },
    yAxis: {
      type: 'category',
      data: items.map((item: any) => item.name),
      axisLabel: { color: '#6b7280', fontSize: 11 },
      axisLine: { show: false },
      axisTick: { show: false },
    },
    series: [{
      name: '掌握度',
      type: 'bar',
      barWidth: 14,
      data: items.map((item: any) => item.value),
      label: { show: true, position: 'right', formatter: '{c}%', color: '#6b7280', fontSize: 11 },
      itemStyle: {
        borderRadius: [0, 6, 6, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#93c5fd' },
          { offset: 1, color: '#3b82f6' },
        ]),
      },
    }],
  })
}

function initTrendChart() {
  if (!trendChartRef.value || historyTrend.value.length < 2) return
  trendChart?.dispose()
  trendChart = echarts.init(trendChartRef.value)
  const scores = historyTrend.value.map((h: any) => Number(h.score || 0))
  const minScore = Math.max(0, Math.floor(Math.min(...scores) - 5))
  const maxScore = Math.ceil(Math.max(...scores) + 5)

  trendChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter(params: any) {
        const p = params[0]
        return `${p.data.label}<br/>得分：<b>${p.data.score}</b>`
      },
    },
    xAxis: {
      type: 'category',
      data: historyTrend.value.map((h: any) => h.date),
      axisLine: { lineStyle: { color: '#e5e7eb' } },
    },
    yAxis: {
      type: 'value',
      min: minScore,
      max: maxScore,
      scale: true,
      alignTicks: false,
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6' } },
    },
    series: [{
      type: 'line',
      data: historyTrend.value.map((h: any) => ({ value: h.score, label: h.label })),
      smooth: true,
      symbolSize: 8,
      itemStyle: { color: '#3b82f6' },
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.02)' },
          ],
        },
      },
      lineStyle: { width: 3 },
    }],
  })
}

function initCharts() {
  initKnowledgeChart()
  initTrendChart()
}

// 响应式销毁
function handleResize() {
  knowledgeChart?.resize()
  trendChart?.resize()
}

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  knowledgeChart?.dispose()
  trendChart?.dispose()
})
</script>
