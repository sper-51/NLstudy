<template>
  <div class="h-full overflow-auto p-6">
  <div v-if="loading" class="flex items-center justify-center h-64">
    <div class="text-gray-400">加载中...</div>
  </div>
  <template v-else-if="course">
    <!-- 面包屑 -->
    <nav class="flex items-center gap-2 text-sm text-gray-500 mb-6">
      <router-link to="/courses" class="hover:text-primary-500 transition-colors cursor-pointer">我的课程</router-link>
      <i class="ri-arrow-right-s-line"></i>
      <span class="text-gray-800 font-medium">{{ course.name }}</span>
    </nav>

    <!-- 课程基本信息 -->
    <div class="bg-white rounded-xl p-6 shadow-sm mb-6">
      <div class="flex items-start gap-6">
        <div class="w-16 h-16 rounded-2xl bg-primary-100 flex items-center justify-center flex-shrink-0">
          <i class="ri-code-box-fill text-primary-500 text-3xl"></i>
        </div>
        <div class="flex-1">
          <div class="flex items-start justify-between mb-2">
            <div>
              <h1 class="text-xl font-bold text-gray-800">{{ course.name }}</h1>
              <p class="text-sm text-gray-500 mt-1">{{ course.code || '-' }} · {{ course.teacherName || '暂无' }}</p>
            </div>
            <div class="flex gap-2">
              <span class="px-3 py-1 bg-primary-50 text-primary-600 text-sm rounded-full">{{ course.credits ?? '-' }} 学分</span>
              <span class="px-3 py-1 bg-gray-100 text-gray-600 text-sm rounded-full">{{ course.hours ?? '-' }} 学时</span>
            </div>
          </div>
          <p class="text-sm text-gray-600 leading-relaxed">{{ course.description || '暂无课程描述' }}</p>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-success-400">
        <div class="text-xs text-gray-500 mb-1">已完成考试</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.completedExams }}</div>
        <div class="text-xs text-success-500 mt-0.5">均分 {{ stats.avgScore }}</div>
      </div>
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-warning-400">
        <div class="text-xs text-gray-500 mb-1">待参加考试</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.pendingExams }}</div>
        <div class="text-xs text-warning-500 mt-0.5">最近一场 {{ stats.nextExamTime }}</div>
      </div>
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-danger-400">
        <div class="text-xs text-gray-500 mb-1">错题数</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.wrongQuestions }}</div>
        <div class="text-xs text-danger-500 mt-0.5">涉及 {{ stats.knowledgePoints }} 个知识点</div>
      </div>
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-primary-400">
        <div class="text-xs text-gray-500 mb-1">学习进度</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.progress }}%</div>
        <div class="w-full h-1.5 bg-gray-100 rounded-full mt-2 overflow-hidden">
          <div class="h-full bg-primary-500 rounded-full" :style="{ width: stats.progress + '%' }"></div>
        </div>
      </div>
    </div>

    <!-- 知识点掌握度 -->
    <div class="bg-white rounded-xl p-5 shadow-sm mb-6">
      <h3 class="text-base font-semibold text-gray-800 mb-4 flex items-center gap-2">
        <i class="ri-pie-chart-line text-primary-500"></i>
        知识点掌握度
      </h3>
      <div class="space-y-3">
        <div
          v-for="kp in knowledgePoints"
          :key="kp.name"
          class="flex items-center gap-3"
        >
          <span class="w-28 text-sm text-gray-600 truncate">{{ kp.name }}</span>
          <div class="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
            <div 
              class="h-full rounded-full transition-all duration-300"
              :class="getMasteryColor(kp.mastery)"
              :style="{ width: kp.mastery + '%' }"
            ></div>
          </div>
          <span class="w-12 text-right text-sm font-medium" :class="getMasteryTextColor(kp.mastery)">
            {{ kp.mastery }}%
          </span>
        </div>
      </div>
    </div>

    <!-- 考试列表 -->
    <div class="bg-white rounded-xl p-5 shadow-sm">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
          <i class="ri-file-list-3-line text-primary-500"></i>
          考试列表
        </h3>
        
        <!-- Tab 切换 -->
        <div class="flex bg-gray-100 rounded-lg p-0.5">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            @click="activeTab = tab.value"
            :class="[
              'px-4 py-1.5 text-sm font-medium rounded-md transition-all',
              activeTab === tab.value ? 'bg-white text-primary-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'
            ]"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- 考试表格 -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="text-left text-xs font-medium text-gray-500 pb-3 px-2">考试名称</th>
              <th class="text-left text-xs font-medium text-gray-500 pb-3 px-2">时间</th>
              <th class="text-left text-xs font-medium text-gray-500 pb-3 px-2">时长/题数/总分</th>
              <th class="text-right text-xs font-medium text-gray-500 pb-3 px-2">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="exam in filteredExams"
              :key="exam.id"
              class="border-b border-gray-50 last:border-0 hover:bg-gray-50/50 transition-colors"
            >
              <td class="py-3.5 px-2">
                <div class="font-medium text-sm text-gray-800">{{ exam.examName }}</div>
                <span 
                  :class="['inline-block mt-1 px-2 py-0.5 text-xs rounded', getStatusBadgeClass(exam.status)]"
                >
                  {{ getStatusText(exam.status) }}
                </span>
              </td>
              <td class="py-3.5 px-2 text-sm text-gray-500">
                {{ formatTime(exam.startTime) }} - {{ formatEndTime(exam.startTime, exam.duration) }}
              </td>
              <td class="py-3.5 px-2 text-sm text-gray-500">
                {{ exam.duration }}分钟 · {{ exam.questionCount || 25 }}题 · {{ exam.totalScore }}分
              </td>
              <td class="py-3.5 px-2 text-right">
                <template v-if="exam.status === 'completed' || exam.status === 'finished'">
                  <router-link v-if="exam.gradeId" :to="'/report/' + exam.gradeId" class="text-xs text-primary-500 hover:text-primary-600 mr-3 cursor-pointer">
                    考试回顾
                  </router-link>
                  <span v-else class="text-xs text-gray-400 mr-3">成绩生成中</span>
                  <router-link v-if="exam.gradeId" :to="{ path: '/report/' + exam.gradeId, query: { review: '1' } }" class="text-xs text-purple-500 hover:text-purple-600 mr-3 cursor-pointer">
                    申请复核
                  </router-link>
                  <router-link to="/wrong-questions" class="text-xs text-danger-500 hover:text-danger-600 cursor-pointer">
                    错题重刷
                  </router-link>
                </template>
                <template v-else>
                  <router-link :to="'/exam/' + exam.id" class="px-3 py-1.5 bg-primary-500 text-white text-xs rounded-lg hover:bg-primary-600 transition-colors inline-block cursor-pointer">
                    开始考试
                  </router-link>
                </template>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="filteredExams.length === 0" class="py-10 text-center text-gray-400 text-sm">
        暂无{{ activeTab === 'pending' ? '待参加' : '已完成' }}的考试
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="grid grid-cols-2 gap-4 mt-6">
      <router-link to="/wrong-questions" class="bg-gradient-to-r from-orange-50 to-red-50 rounded-xl p-5 border border-orange-100 hover:shadow-md transition-all cursor-pointer block">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-lg bg-danger-100 flex items-center justify-center">
            <i class="ri-error-warning-line text-danger-500 text-lg"></i>
          </div>
          <div>
            <h4 class="font-semibold text-sm text-gray-800">复习本课程错题</h4>
            <p class="text-xs text-gray-500 mt-0.5">共 {{ stats.wrongQuestions }} 道错题待复习</p>
          </div>
        </div>
      </router-link>
      
      <router-link :to="latestGradeId ? '/report/' + latestGradeId : '/report'" class="bg-gradient-to-r from-green-50 to-emerald-50 rounded-xl p-5 border border-green-100 hover:shadow-md transition-all cursor-pointer block">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-lg bg-success-100 flex items-center justify-center">
            <i class="ri-bar-chart-box-line text-success-500 text-lg"></i>
          </div>
          <div>
            <h4 class="font-semibold text-sm text-gray-800">查看最近成绩报告</h4>
            <p class="text-xs text-gray-500 mt-0.5">最近一次均分 {{ stats.avgScore }}</p>
          </div>
        </div>
      </router-link>
    </div>
  </template>
  <div v-else class="flex items-center justify-center h-64 text-gray-400">
    课程不存在
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import type { Course, Exam, KnowledgePoint } from '@/api/types'
import { getCourseDetail, getWrongQuestionReport } from '@/api/student'

const route = useRoute()
const loading = ref(false)

const courseId = computed(() => Number(route.params.courseId))
const activeTab = ref<'all' | 'pending' | 'completed'>('all')

const tabs = [
  { label: '全部', value: 'all' as const },
  { label: '待参加', value: 'pending' as const },
  { label: '已完成', value: 'completed' as const },
]

// API 数据
const course = ref<Course | null>(null)
const exams = ref<Exam[]>([])
// 从错题报告接口获取的真实数据
const apiWrongQuestions = ref(0)
const apiKnowledgePoints = ref(0)

// 计算统计数据
const stats = computed(() => {
  if (!course.value) return { completedExams: 0, avgScore: 0, pendingExams: 0, nextExamTime: '-', wrongQuestions: 0, knowledgePoints: 0, progress: 0 }
  const completed = exams.value.filter(e => e.status === 'completed' || e.status === 'finished').length
  const pending = exams.value.filter(e => e.status === 'pending').length
  const completedExams = exams.value.filter(e => e.myScore !== undefined)
  const avgScore = completedExams.length > 0
    ? Math.round(completedExams.reduce((sum, e) => sum + (e.myScore ?? 0), 0) / completedExams.length)
    : 0
  return {
    completedExams: completed,
    avgScore,
    pendingExams: pending,
    nextExamTime: pending > 0 ? '待定' : '-',
    wrongQuestions: apiWrongQuestions.value,
    knowledgePoints: apiKnowledgePoints.value,
    progress: course.value.progress ?? 0,
  }
})

// 知识点（暂用默认值，后续可从 API 获取）
const knowledgePoints = ref<KnowledgePoint[]>([
  { name: '待加载', mastery: 0 },
])

const filteredExams = computed(() => {
  if (activeTab.value === 'pending') return exams.value.filter(e => e.status === 'pending')
  if (activeTab.value === 'completed') return exams.value.filter(e => ['completed', 'finished'].includes(e.status as string))
  return exams.value
})

const latestGradeId = computed(() => {
  const completed = exams.value.find((exam: any) => exam.gradeId)
  return completed ? (completed as any).gradeId : null
})

onMounted(async () => {
  loading.value = true
  try {
    const [res, reportRes] = await Promise.all([
      getCourseDetail(courseId.value),
      getWrongQuestionReport({ courseId: courseId.value }).catch(() => null),
    ])
    course.value = res as any
    // 如果 API 返回考试列表则使用，否则空数组
    const data = res as any
    exams.value = Array.isArray(data.exams) ? data.exams : []
    knowledgePoints.value = Array.isArray(data.knowledgePoints) ? data.knowledgePoints : []

    // 从错题报告接口获取错题数和知识点数
    if (reportRes) {
      const reportData = reportRes as any
      apiWrongQuestions.value = reportData.totalWrongCount ?? reportData.wrongCount ?? 0
      apiKnowledgePoints.value = reportData.knowledgePointCount ?? 0
      // 如果课程详情没有知识点数据，尝试从报告中获取
      if (knowledgePoints.value.length === 0 && Array.isArray(reportData.knowledgePoints)) {
        knowledgePoints.value = reportData.knowledgePoints
      }
    }
  } catch (err) {
    console.error('加载课程详情失败', err)
  } finally {
    loading.value = false
  }
})

// 方法
function formatTime(time: string): string {
  return dayjs(time).format('MM-DD HH:mm')
}

function formatEndTime(startTime: string, duration: number): string {
  return dayjs(startTime).add(duration, 'minute').format('HH:mm')
}

function getStatusText(status: string): string {
  const map: Record<string, string> = { pending: '待参加', ongoing: '进行中', finished: '已结束', completed: '已完成' }
  return map[status] || status
}

function getStatusBadgeClass(status: string): string {
  const map: Record<string, string> = {
    pending: 'bg-warning-50 text-warning-600',
    ongoing: 'bg-primary-50 text-primary-600',
    finished: 'bg-gray-100 text-gray-600',
    completed: 'bg-success-50 text-success-600',
  }
  return map[status] || ''
}

function getMasteryColor(mastery: number): string {
  if (mastery >= 80) return 'bg-success-500'
  if (mastery >= 60) return 'bg-primary-500'
  if (mastery >= 40) return 'bg-warning-500'
  return 'bg-danger-500'
}

function getMasteryTextColor(mastery: number): string {
  if (mastery >= 80) return 'text-success-500'
  if (mastery >= 60) return 'text-primary-500'
  if (mastery >= 40) return 'text-warning-500'
  return 'text-danger-500'
}
</script>
