<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 + 筛选 + 统计 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">待阅任务</h1>
        <p class="text-sm text-gray-500 mt-1">批改主观题与编程题，高效完成阅卷工作</p>
        <!-- 状态切换 Tab -->
        <div class="flex items-center gap-1 mt-3">
          <button
            v-for="tab in [{value:'pending',label:'未批阅'},{value:'completed',label:'已批阅'},{value:'all',label:'全部'}]"
            :key="tab.value"
            :class="[
              'px-4 py-1.5 rounded-full text-sm font-medium transition-all cursor-pointer',
              gradingStatus === tab.value
                ? 'bg-primary-500 text-white shadow-sm'
                : 'bg-white text-gray-500 hover:text-gray-700 border border-gray-200'
            ]"
            @click="gradingStatus = tab.value as any"
          >{{ tab.label }}</button>
        </div>
      </div>
      <div class="flex items-center gap-3">
        <!-- 统计信息 -->
        <div class="flex items-center gap-3 mr-2">
          <div class="bg-white rounded-lg px-4 py-2 shadow-sm border border-gray-100">
            <span class="text-xs text-gray-400">{{ gradingStatus === 'completed' ? '已批改数' : '待批改总数' }}</span>
            <span :class="['ml-2 text-lg font-bold', gradingStatus === 'completed' ? 'text-green-500' : 'text-danger-500']">{{ totalCount }}</span>
          </div>
          <div class="bg-white rounded-lg px-4 py-2 shadow-sm border border-gray-100">
            <span class="text-xs text-gray-400">今日已完成</span>
            <span class="ml-2 text-lg font-bold text-green-500">{{ todayCompleted }}</span>
          </div>
        </div>
        <!-- 筛选 -->
        <el-select v-model="filterCourse" placeholder="按课程筛选" style="width: 180px" size="default" clearable>
          <el-option v-for="course in courseOptions" :key="course.value" :label="course.label" :value="course.value" />
        </el-select>
        <el-select v-model="filterExam" placeholder="按考试筛选" style="width: 180px" size="default" clearable>
          <el-option v-for="e in examOptions" :key="e.value" :label="e.label" :value="e.value" />
        </el-select>
        <el-select v-model="filterType" placeholder="按题型筛选" style="width: 140px" size="default" clearable>
          <el-option label="简答题" value="essay" />
          <el-option label="编程题" value="code" />
          <el-option label="填空题" value="fill" />
        </el-select>
      </div>
    </div>

    <!-- 主内容区：左侧队列 + 右侧工作区 -->
    <div class="flex gap-5" style="height: calc(100vh - 210px)">
      <!-- 左侧：批改队列列表 -->
      <div class="w-[420px] flex-shrink-0 bg-white rounded-xl shadow-sm flex flex-col overflow-hidden">
        <div class="p-4 border-b border-gray-100">
          <h3 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
            <i class="ri-list-ordered text-primary-500"></i>{{ gradingStatus === 'completed' ? '已批阅记录' : '批改队列' }}
            <span class="text-xs text-gray-400 font-normal">（{{ filteredQueue.length }} 条{{ gradingStatus === 'completed' ? '记录' : '待处理' }}）</span>
          </h3>
        </div>
        <div class="flex-1 overflow-auto p-3 space-y-3">
          <div
            v-for="item in filteredQueue"
            :key="item.id"
            :class="[
              'border rounded-xl p-4 cursor-pointer transition-all',
              currentItem?.id === item.id
                ? 'border-primary-400 bg-primary-50/50 shadow-sm ring-1 ring-primary-200'
                : 'border-gray-100 hover:border-primary-200 hover:shadow-sm'
            ]"
            @click="selectItem(item)"
          >
            <!-- 学生 + 考试信息 -->
            <div class="flex items-start justify-between mb-2">
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 rounded-full bg-primary-100 text-primary-600 text-sm font-bold flex items-center justify-center">
                  {{ item.studentName.charAt(0) }}
                </div>
                <div>
                  <div class="text-sm font-semibold text-gray-800">{{ item.studentName }}</div>
                  <div class="text-xs text-gray-400">{{ item.examName }}</div>
                </div>
              </div>
              <el-tag :type="getTypeTagType(item.questionType)" size="small" effect="plain">{{ item.typeName }}</el-tag>
            </div>

            <!-- 题目摘要 -->
            <div class="bg-gray-50 rounded-lg p-3 mb-3">
              <div class="text-xs text-gray-400 mb-1">第 {{ item.questionNumber }} 题 · {{ item.typeName }} · {{ item.maxScore }} 分</div>
              <p class="text-sm text-gray-700 line-clamp-2 leading-relaxed">{{ item.questionSummary }}</p>
            </div>

            <!-- 批改状态 / 进度 -->
            <div class="flex items-center gap-3">
              <div class="flex-1">
                <div class="flex items-center justify-between text-xs mb-1">
                  <span class="text-gray-400">{{ item.status === 'completed' ? '批改结果' : '批改进度' }}</span>
                  <span class="text-gray-500">
                    {{ item.status === 'completed' ? `${item.score ?? 0}/${item.maxScore}` : `${item.progress}/${item.totalInQueue}` }}
                  </span>
                </div>
                <el-progress
                  :percentage="item.status === 'completed' ? Math.round(((item.score || 0) / Math.max(item.maxScore || 1, 1)) * 100) : Math.round((item.progress / Math.max(item.totalInQueue, 1)) * 100)"
                  :stroke-width="6"
                  :show-text="false"
                  :color="item.status === 'completed' ? '#22c55e' : '#6366f1'"
                />
              </div>
              <el-button
                size="small"
                :type="item.status === 'completed' ? 'success' : 'primary'"
                :plain="item.status === 'completed'"
                @click.stop="startGrading(item)"
              >
                {{ item.status === 'completed' ? '查看记录' : '开始批改' }}
              </el-button>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredQueue.length === 0" class="flex flex-col items-center justify-center py-16 text-gray-400">
            <i class="ri-checkbox-circle-line text-5xl mb-3 opacity-50"></i>
            <p class="text-sm font-medium">{{ gradingStatus === 'completed' ? '暂无已批阅记录' : '暂无待批改任务' }}</p>
            <p class="text-xs mt-1">{{ gradingStatus === 'completed' ? '完成批改后记录将显示在这里' : '所有任务已完成' }}</p>
          </div>
        </div>
      </div>

      <!-- 右侧：批改工作区 -->
      <div class="flex-1 bg-white rounded-xl shadow-sm flex flex-col overflow-hidden">
        <!-- 空状态：未选择任何任务 -->
        <div v-if="!currentItem" class="flex-1 flex flex-col items-center justify-center text-gray-400">
          <i class="ri-edit-box-line text-6xl mb-4 opacity-30"></i>
          <p class="text-lg font-medium">{{ gradingStatus === 'completed' ? '暂无已批阅记录' : '选择一道题目开始批改' }}</p>
          <p class="text-sm mt-1">{{ gradingStatus === 'completed' ? '切换到「未批阅」查看待处理任务' : '从左侧队列中选择待批改的任务' }}</p>
        </div>

        <!-- 工作区内容：已选中任务 -->
        <template v-else>
          <!-- 工作区头部 -->
          <div class="p-4 border-b border-gray-100 flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl bg-primary-50 flex items-center justify-center">
                <i class="ri-edit-2-line text-primary-500 text-lg"></i>
              </div>
              <div>
                <h3 class="text-sm font-semibold text-gray-800">批改工作区</h3>
                <div class="text-xs text-gray-400 flex items-center gap-2 mt-0.5">
                  <span>{{ currentItem.studentName }}</span>
                  <span>|</span>
                  <span>{{ currentItem.examName }}</span>
                  <span>|</span>
                  <span>第 {{ currentItem.questionNumber }} 题</span>
                </div>
              </div>
            </div>
            <div class="flex items-center gap-2 text-xs text-gray-400">
              <kbd class="px-1.5 py-0.5 bg-gray-100 rounded text-gray-500 font-mono">←</kbd>
              <span>上一题</span>
              <kbd class="px-1.5 py-0.5 bg-gray-100 rounded text-gray-500 font-mono ml-2">→</kbd>
              <span>下一题</span>
              <kbd class="px-1.5 py-0.5 bg-gray-100 rounded text-gray-500 font-mono ml-2">Ctrl+S</kbd>
              <span>提交</span>
            </div>
          </div>

          <!-- 工作区内容 -->
          <div class="flex-1 overflow-auto p-5 space-y-5">
            <!-- 题目完整内容 -->
            <div>
              <h4 class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                <i class="ri-questionnaire-line text-primary-500"></i>题目内容
              </h4>
              <div class="bg-gray-50 rounded-xl p-4 border border-gray-100">
                <div class="flex items-center gap-2 mb-2">
                  <el-tag :type="getTypeTagType(currentItem.questionType)" size="small" effect="plain">{{ currentItem.typeName }}</el-tag>
                  <span class="text-xs text-gray-400">满分：{{ currentItem.maxScore }} 分</span>
                </div>
                <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">{{ currentItem.fullQuestion }}</p>
              </div>
            </div>

            <!-- 学生答案展示区 -->
            <div>
              <h4 class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                <i class="ri-chat-new-line text-primary-500"></i>学生答案
              </h4>
              <div class="bg-primary-50/50 rounded-xl p-4 border border-primary-100 min-h-[120px]">
                <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap font-mono">{{ currentItem.studentAnswer || '(该生未作答)' }}</p>
              </div>
            </div>

            <!-- 参考答案（可选显示） -->
            <div>
              <div class="flex items-center justify-between mb-2">
                <h4 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
                  <i class="ri-lightbulb-line text-warning-500"></i>参考答案
                </h4>
                <el-switch v-model="showReference" size="small" active-text="显示" inactive-text="隐藏" />
              </div>
              <div v-if="showReference" class="bg-green-50/50 rounded-xl p-4 border border-green-100">
                <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">{{ currentItem.referenceAnswer }}</p>
              </div>
            </div>

            <!-- 得分 + 评语：未批阅时可编辑 -->
            <div v-if="currentItem.status !== 'completed'" class="grid grid-cols-2 gap-4">
              <div>
                <h4 class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                  <i class="ri-star-line text-primary-500"></i>得分
                </h4>
                <div class="flex items-center gap-3">
                  <el-input-number
                    v-model="gradingForm.score"
                    :min="0"
                    :max="currentItem.maxScore"
                    :step="1"
                    size="large"
                    controls-position="right"
                    style="width: 140px"
                  />
                  <span class="text-sm text-gray-500">/ {{ currentItem.maxScore }} 分</span>
                </div>
                <div class="mt-2 flex items-center gap-2">
                  <el-button-group size="small">
                    <el-button @click="gradingForm.score = 0">0</el-button>
                    <el-button @click="gradingForm.score = Math.round(currentItem.maxScore * 0.6)">60%</el-button>
                    <el-button @click="gradingForm.score = Math.round(currentItem.maxScore * 0.8)">80%</el-button>
                    <el-button @click="gradingForm.score = currentItem.maxScore">满分</el-button>
                  </el-button-group>
                </div>
              </div>
              <div>
                <h4 class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                  <i class="ri-chat-quote-line text-primary-500"></i>评语
                </h4>
                <el-input
                  v-model="gradingForm.comment"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入批改评语..."
                  resize="none"
                />
              </div>
            </div>

            <!-- 已批阅成绩展示（只读） -->
            <div v-else class="bg-green-50/50 rounded-xl p-5 border border-green-200">
              <div class="flex items-center justify-between mb-3">
                <h4 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
                  <i class="ri-checkbox-circle-fill text-green-500"></i>批改结果
                </h4>
                <el-tag type="success" size="large" effect="dark">
                  {{ currentItem.maxScore > 0 ? ((currentItem.score || 0) / currentItem.maxScore * 100).toFixed(0) : 0 }}分
                </el-tag>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <span class="text-xs text-gray-400">得分</span>
                  <p class="text-2xl font-bold text-green-600 mt-1">{{ currentItem.score || 0 }} <span class="text-sm font-normal text-gray-400">/ {{ currentItem.maxScore }} 分</span></p>
                </div>
                <div>
                  <span class="text-xs text-gray-400">满分</span>
                  <p class="text-2xl font-bold text-gray-600 mt-1">{{ currentItem.maxScore }}</p>
                </div>
              </div>
              <div v-if="currentItem.comment" class="mt-3 pt-3 border-t border-green-200">
                <span class="text-xs text-gray-400">评语</span>
                <p class="text-sm text-gray-700 mt-1 whitespace-pre-wrap">{{ currentItem.comment }}</p>
              </div>
            </div>
          </div>

          <!-- 底部导航 + 提交 -->
          <div class="p-4 border-t border-gray-100 flex items-center justify-between bg-gray-50/50">
            <div class="flex items-center gap-2">
              <el-button @click="prevQuestion" :disabled="currentIndex <= 0">
                <i class="ri-arrow-left-line mr-1"></i>上一题
              </el-button>
              <el-button @click="nextQuestion" :disabled="currentIndex >= filteredQueue.length - 1">
                下一题<i class="ri-arrow-right-line ml-1"></i>
              </el-button>
              <span class="text-xs text-gray-400 ml-2">
                {{ currentIndex + 1 }} / {{ filteredQueue.length }}
              </span>
            </div>
            <div v-if="currentItem.status !== 'completed'" class="flex items-center gap-2">
              <el-button @click="skipCurrent">跳过此题</el-button>
              <el-button type="primary" @click="submitGrading" :loading="submitting">
                <i class="ri-check-double-line mr-1"></i>提交批改
              </el-button>
            </div>
            <div v-else class="text-xs text-gray-400">
              <i class="ri-check-line text-green-500"></i> 已批阅记录，只读查看
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getGradingTasks, getSchedules, getTeacherCourses, submitGrading as submitGradingApi } from '@/api/teacher'

// ========== 统计 ==========
const totalCount = ref(0)
const todayCompleted = ref(0)

// ========== 筛选 ==========
const filterExam = ref('')
const filterCourse = ref('')
const filterType = ref('')

const examOptions = ref<Array<{ label: string; value: string | number }>>([])
const courseOptions = ref<Array<{ label: string; value: string | number }>>([])

// ========== 批改队列数据 ==========
interface GradingItem {
  id: number
  examRecordId?: number
  studentName: string
  studentId: string
  courseId?: number
  courseName: string
  examId?: number
  examName: string
  questionNumber: number
  questionType: string
  typeName: string
  questionSummary: string
  fullQuestion: string
  studentAnswer: string
  referenceAnswer: string
  maxScore: number
  fullScore?: number
  score?: number
  comment?: string
  status?: 'pending' | 'grading' | 'completed'
  gradeTime?: string
  progress: number
  totalInQueue: number
}

const gradingQueue = ref<GradingItem[]>([])
const loading = ref(false)

// ========== 状态切换（未批阅/已批阅/全部）==========
const gradingStatus = ref<'pending' | 'completed' | 'all'>('pending')
const filteredQueue = computed(() => {
  let list = gradingQueue.value
  if (filterCourse.value) {
    list = list.filter(item => String(item.courseId) === String(filterCourse.value))
  }
  if (filterExam.value) {
    list = list.filter(item => String(item.examId) === String(filterExam.value))
  }
  if (filterType.value) {
    list = list.filter(item => item.questionType === filterType.value)
  }
  return list
})

// 加载批改任务
async function loadGradingTasks() {
  loading.value = true
  try {
    const params: any = {
      examId: filterExam.value || undefined,
      pageSize: 100,
    }
    params.status = gradingStatus.value
    const res = await getGradingTasks(params)
    const resData = res as any
    const data = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || [])
    gradingQueue.value = data.map((item: any, index: number) => ({
      id: item.id || item.taskId || index,
      examRecordId: item.examRecordId,
      studentName: item.studentName || '',
      studentId: item.studentId || '',
      courseId: item.courseId,
      courseName: item.courseName || '',
      examId: item.examId,
      examName: item.examName || '',
      questionNumber: item.questionNumber || (index + 1),
      questionType: item.questionType || 'essay',
      typeName: item.typeName || getTypeName(item.questionType),
      questionSummary: item.questionContent || item.questionSummary || '',
      fullQuestion: item.questionContent || item.questionSummary || '',
      studentAnswer: item.studentAnswer || '',
      referenceAnswer: item.correctAnswer || item.referenceAnswer || '',
      maxScore: item.fullScore || item.score || 10,
      fullScore: item.fullScore || item.score || 10,
      score: item.score != null ? Number(item.score) : undefined,
      comment: item.comment || '',
      status: item.status || 'pending',
      gradeTime: item.gradeTime || item.gradingTime || '',
      progress: item.status === 'completed' ? 1 : 0,
      totalInQueue: data.length,
    }))

    // 收集考试选项
    const examMap = new Map<string | number, string>()
    data.forEach((item: any) => {
      if (item.examId && item.examName) examMap.set(item.examId, item.examName)
    })
    examOptions.value = Array.from(examMap.entries()).map(([value, label]) => ({ value, label }))

    // 更新统计
    totalCount.value = gradingQueue.value.length
  } catch (err) {
    console.error('加载批改任务失败', err)
    gradingQueue.value = []
    totalCount.value = 0
  } finally {
    loading.value = false
  }
}

async function loadFilterOptions() {
  try {
    const [coursesRes, examsRes] = await Promise.all([
      getTeacherCourses({ page: 1, pageSize: 100 }),
      getSchedules({ page: 1, pageSize: 100 }),
    ])
    const courses = Array.isArray(coursesRes) ? coursesRes : (coursesRes?.list || [])
    const exams = Array.isArray(examsRes) ? examsRes : (examsRes?.list || [])
    courseOptions.value = courses.map((course: any) => ({ label: course.name, value: course.id }))
    if (examOptions.value.length === 0) {
      examOptions.value = exams.map((exam: any) => ({ label: exam.examName || exam.name, value: exam.id }))
    }
  } catch (error) {
    console.error('加载阅卷筛选项失败', error)
  }
}

function getTypeName(type: string): string {
  const map: Record<string, string> = {
    essay: '简答题',
    code: '编程题',
    fill: '填空题',
    single: '单选题',
    multiple: '多选题',
    judge: '判断题',
  }
  return map[type] || type
}

// ========== 当前选中项 ==========
const currentItem = ref<GradingItem | null>(null)
const currentIndex = computed(() => {
  if (!currentItem.value) return -1
  return filteredQueue.value.findIndex(item => item.id === currentItem.value!.id)
})

const gradingForm = ref({
  score: 0,
  comment: '',
})
const showReference = ref(false)
const submitting = ref(false)

function selectItem(item: GradingItem) {
  currentItem.value = item
  gradingForm.value = {
    score: item.status === 'completed' ? Number(item.score || 0) : 0,
    comment: item.status === 'completed' ? (item.comment || '') : '',
  }
  showReference.value = false
}

function startGrading(item: GradingItem) {
  selectItem(item)
}

// ========== 导航 ==========
function prevQuestion() {
  if (currentIndex.value > 0) {
    selectItem(filteredQueue.value[currentIndex.value - 1])
  }
}

function nextQuestion() {
  if (currentIndex.value < filteredQueue.value.length - 1) {
    selectItem(filteredQueue.value[currentIndex.value + 1])
  }
}

function skipCurrent() {
  if (currentIndex.value < filteredQueue.value.length - 1) {
    nextQuestion()
  } else {
    ElMessage.info('已是最后一道题')
  }
}

// ========== 提交批改 ==========
async function submitGrading() {
  if (!currentItem.value) return
  if (currentItem.value.status === 'completed') {
    ElMessage.info('该任务已批阅，只能查看记录')
    return
  }
  if (gradingForm.value.score < 0 || gradingForm.value.score > (currentItem.value.maxScore || 10)) {
    ElMessage.warning('分数超出范围')
    return
  }

  submitting.value = true
  try {
    await submitGradingApi(currentItem.value.id, {
      score: gradingForm.value.score,
      comment: gradingForm.value.comment,
    })
    todayCompleted.value++

    // 从队列中移除已完成的
    const idx = gradingQueue.value.findIndex(i => i.id === currentItem.value!.id)
    if (idx > -1) {
      gradingQueue.value.splice(idx, 1)
      totalCount.value--
    }

    ElMessage.success(`已提交批改：${currentItem.value!.studentName} 的第 ${currentItem.value!.questionNumber} 题，得分 ${gradingForm.value.score}/${currentItem.value!.maxScore}`)

    // 自动跳到下一题
    if (filteredQueue.value.length > 0) {
      const nextIdx = Math.min(currentIndex.value, filteredQueue.value.length - 1)
      selectItem(filteredQueue.value[nextIdx])
    } else {
      currentItem.value = null
    }
  } catch (err) {
    console.error('提交批改失败', err)
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

// ========== 快捷键 ==========
function handleKeyDown(e: KeyboardEvent) {
  if (gradingStatus.value === 'completed') {
    return
  }
  // Ctrl/Cmd + S 提交
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault()
    submitGrading()
    return
  }
  // 左箭头 上一题
  if (e.key === 'ArrowLeft') {
    prevQuestion()
    return
  }
  // 右箭头 下一题
  if (e.key === 'ArrowRight') {
    nextQuestion()
    return
  }
}

// 状态切换时重新加载数据
watch(gradingStatus, () => {
  loadGradingTasks()
})

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown)
  loadFilterOptions()
  loadGradingTasks()
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown)
})

// ========== 辅助函数 ==========
function getTypeTagType(type: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    essay: 'warning',
    code: 'danger',
    fill: 'info',
  }
  return map[type] || 'info'
}
</script>
