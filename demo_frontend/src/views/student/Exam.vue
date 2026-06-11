<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 顶部工具栏 -->
    <div class="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between shadow-sm flex-shrink-0">
      <div class="flex items-center gap-4">
        <h1 class="text-lg font-bold text-gray-800">{{ examRecord?.examName || '在线考试' }}</h1>
        <span class="text-sm text-gray-500">共 {{ questions.length }} 题 · 总分 {{ totalScore }} 分</span>
      </div>
      <div class="flex items-center gap-4">
        <!-- 倒计时 -->
        <div :class="['flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-mono font-semibold',
          remainingTime <= 300 ? 'bg-red-50 text-red-600 animate-pulse' : 'bg-gray-100 text-gray-700']">
          <i class="ri-timer-line"></i>
          {{ formatCountdown(remainingTime) }}
        </div>
        <!-- 已答题数 -->
        <div class="text-sm text-gray-500">
          已答 <span class="font-bold text-primary-600">{{ answeredCount }}</span> / {{ questions.length }}
        </div>
        <!-- 自动保存状态 -->
        <span v-if="saving" class="text-xs text-gray-400 flex items-center gap-1">
          <i class="ri-loader-4-line animate-spin"></i> 保存中...
        </span>
        <span v-else-if="lastSaveTime" class="text-xs text-gray-400">已自动保存</span>
        <!-- 提交按钮 -->
        <el-button type="primary" @click="handleSubmit" :loading="submitting" :disabled="questions.length === 0">
          交卷
        </el-button>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧：题目区域 -->
      <div class="flex-1 overflow-y-auto p-6">
        <div v-if="loading" class="flex items-center justify-center h-full">
          <div class="text-center">
            <i class="ri-loader-4-line animate-spin text-4xl text-primary-500 mb-3"></i>
            <p class="text-gray-500">正在加载试卷...</p>
          </div>
        </div>

        <div v-else-if="errorMsg" class="flex items-center justify-center h-full">
          <div class="text-center bg-white rounded-xl p-8 shadow-sm max-w-md">
            <i class="ri-error-warning-line text-5xl text-danger-500 mb-4"></i>
            <p class="text-gray-700 mb-2">{{ errorMsg }}</p>
            <router-link to="/courses" class="inline-flex items-center gap-1 text-primary-500 hover:text-primary-600 cursor-pointer">
              <i class="ri-arrow-left-line"></i> 返回课程
            </router-link>
          </div>
        </div>

        <div v-else class="space-y-6 pb-20">
          <div
            v-for="(question, index) in questions"
            :id="'question-' + question.id"
            :key="question.id"
            :class="['bg-white rounded-xl p-5 shadow-sm border-2 transition-all',
              currentQuestionIndex === index ? 'border-primary-400 ring-2 ring-primary-100' : 'border-transparent']"
          >
            <!-- 题目头部 -->
            <div class="flex items-start gap-3 mb-4">
              <span :class="['flex-shrink-0 w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold',
                hasAnswered(question) ? 'bg-primary-500 text-white' : 'bg-gray-100 text-gray-500']">
                {{ index + 1 }}
              </span>
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span :class="['px-2 py-0.5 text-xs rounded', getTypeBadgeClass(question.type)]">
                    {{ question.typeName }}
                  </span>
                  <span class="text-xs text-gray-400">{{ question.score }} 分</span>
                </div>
                <p class="text-sm text-gray-800 leading-relaxed whitespace-pre-wrap">{{ question.content }}</p>
              </div>
              <!-- 标记按钮 -->
              <button
                @click="toggleMark(question.id)"
                :class="['p-1.5 rounded-lg transition-colors', markedIds.has(question.id) ? 'text-warning-500 bg-warning-50' : 'text-gray-300 hover:text-warning-400 hover:bg-warning-50/50']"
                title="标记题目"
              >
                <i class="ri-bookmark-line text-lg"></i>
              </button>
            </div>

            <!-- 单选题 -->
            <div v-if="question.type === 'single_choice'" class="ml-10 space-y-2">
              <el-radio-group v-model="answers[question.id]" @change="onAnswerChange(question.id)">
                <div v-for="option in question.options" :key="option.label" class="flex items-center">
                  <el-radio :value="option.label" class="!mr-2">
                    <span class="text-sm"><strong>{{ option.label }}.</strong> {{ option.content }}</span>
                  </el-radio>
                </div>
              </el-radio-group>
            </div>

            <!-- 多选题 -->
            <div v-else-if="question.type === 'multi_choice'" class="ml-10 space-y-2">
              <el-checkbox-group v-model="multiAnswers[question.id]" @change="onMultiAnswerChange(question.id)">
                <div v-for="option in question.options" :key="option.label" class="flex items-center py-1">
                  <el-checkbox :label="option.label" :value="option.label">
                    <span class="text-sm"><strong>{{ option.label }}.</strong> {{ option.content }}</span>
                  </el-checkbox>
                </div>
              </el-checkbox-group>
              <p v-if="multiAnswers[question.id]?.length > 0" class="text-xs text-gray-400 mt-1">
                已选：{{ (multiAnswers[question.id] as string[]).sort().join(', ') }}
              </p>
            </div>

            <!-- 判断题 -->
            <div v-else-if="question.type === 'true_false'" class="ml-10">
              <el-radio-group v-model="answers[question.id]" @change="onAnswerChange(question.id)">
                <el-radio value="true" class="mr-6">正确</el-radio>
                <el-radio value="false">错误</el-radio>
              </el-radio-group>
            </div>

            <!-- 填空题 -->
            <div v-else-if="question.type === 'fill_blank'" class="ml-10">
              <el-input
                v-model="answers[question.id]"
                placeholder="请输入答案..."
                @blur="onAnswerChange(question.id)"
                clearable
              />
            </div>

            <!-- 简答题 -->
            <div v-else-if="question.type === 'essay'" class="ml-10">
              <el-input
                v-model="answers[question.id]"
                type="textarea"
                :rows="4"
                placeholder="请输入你的回答..."
                @blur="onAnswerChange(question.id)"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：答题卡 -->
      <div class="w-56 bg-white border-l border-gray-200 flex-shrink-0 flex flex-col overflow-hidden">
        <div class="p-4 border-b border-gray-100">
          <h3 class="text-sm font-semibold text-gray-700 flex items-center gap-1.5">
            <i class="ri-checkbox-circle-line text-primary-500"></i>
            答题卡
          </h3>
        </div>
        <div class="flex-1 overflow-y-auto p-4">
          <div class="grid grid-cols-5 gap-2">
            <button
              v-for="(question, index) in questions"
              :key="question.id"
              @click="scrollToQuestion(index)"
              :class="[
                'w-9 h-9 rounded-lg text-xs font-medium transition-all flex items-center justify-center',
                getAnswerCardClass(question, index)
              ]"
            >
              {{ index + 1 }}
            </button>
          </div>
          <!-- 图例 -->
          <div class="mt-4 pt-3 border-t border-gray-100 space-y-1.5 text-xs text-gray-500">
            <div class="flex items-center gap-2"><span class="w-4 h-4 rounded bg-primary-500 text-white flex items-center justify-center text-[10px]">1</span> 已答</div>
            <div class="flex items-center gap-2"><span class="w-4 h-4 rounded bg-warning-400 text-white flex items-center justify-center text-[10px]">1</span> 已标记</div>
            <div class="flex items-center gap-2"><span class="w-4 h-4 rounded bg-gray-100 text-gray-400 flex items-center justify-center text-[10px]">1</span> 未答</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 提交确认弹窗 -->
    <el-dialog v-model="showSubmitDialog" title="确认交卷" width="420px" :close-on-click-modal="false">
      <div class="py-2">
        <div class="space-y-3 text-sm">
          <div class="flex items-center justify-between py-2 border-b border-gray-100">
            <span class="text-gray-600">总题数</span>
            <span class="font-bold text-gray-800">{{ questions.length }} 题</span>
          </div>
          <div class="flex items-center justify-between py-2 border-b border-gray-100">
            <span class="text-gray-600">已答题</span>
            <span class="font-bold text-primary-600">{{ answeredCount }} 题</span>
          </div>
          <div class="flex items-center justify-between py-2 border-b border-gray-100">
            <span class="text-gray-600">未答题</span>
            <span class="font-bold text-danger-500">{{ questions.length - answeredCount }} 题</span>
          </div>
          <div class="flex items-center justify-between py-2">
            <span class="text-gray-600">剩余时间</span>
            <span :class="['font-bold', remainingTime <= 60 ? 'text-danger-500' : 'text-gray-800']">
              {{ formatCountdown(remainingTime) }}
            </span>
          </div>
        </div>
        <div v-if="answeredCount < questions.length" class="mt-4 p-3 bg-warning-50 rounded-lg text-sm text-warning-700 flex items-start gap-2">
          <i class="ri-error-warning-line mt-0.5 flex-shrink-0"></i>
          <span>你还有 {{ questions.length - answeredCount }} 道题目未作答，确定要提交吗？提交后将无法修改答案。</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showSubmitDialog = false">继续答题</el-button>
        <el-button type="primary" @click="confirmSubmit" :loading="submitting">确认交卷</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { Question, ExamRecord } from '@/api/types'
import { startExam, saveExamProgress, submitExam } from '@/api/student'

const route = useRoute()
const router = useRouter()

// ========== 状态 ==========
const loading = ref(true)
const submitting = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const showSubmitDialog = ref(false)

const examId = computed(() => Number(route.params.id || route.params.examId))
const examRecord = ref<ExamRecord | null>(null)
const questions = ref<Question[]>([])

// 用户答案：单选/判断/填空/简答 → string | undefined
const answers = ref<Record<number, string>>({})
// 多选题答案 → string[]
const multiAnswers = ref<Record<number, string[]>>({})

// 标记的题目 ID 集合
const markedIds = ref<Set<number>>(new Set())

// 当前高亮的题目索引（用于答题卡点击定位）
const currentQuestionIndex = ref(0)

// 倒计时相关
const remainingTime = ref(0) // 秒
let countdownTimer: ReturnType<typeof setInterval> | null = null

// 自动保存相关
let autoSaveTimer: ReturnType<typeof setInterval> | null = null
const lastSaveTime = ref<Date | null>(null)

// ========== 计算属性 ==========
const totalScore = computed(() =>
  questions.value.reduce((sum, q) => sum + (q.score ?? 0), 0)
)

const answeredCount = computed(() => {
  let count = 0
  for (const q of questions.value) {
    if (hasAnswered(q)) count++
  }
  return count
})

// ========== 方法 ==========
function hasAnswered(question: Question): boolean {
  if (question.type === 'multi_choice') {
    const arr = multiAnswers.value[question.id]
    return Array.isArray(arr) && arr.length > 0
  }
  const val = answers.value[question.id]
  return val !== undefined && val !== ''
}

function getTypeBadgeClass(type: string): string {
  const map: Record<string, string> = {
    single_choice: 'bg-blue-50 text-blue-600',
    multi_choice: 'bg-purple-50 text-purple-600',
    true_false: 'bg-green-50 text-green-600',
    fill_blank: 'bg-orange-50 text-orange-600',
    essay: 'bg-pink-50 text-pink-600',
  }
  return map[type] || 'bg-gray-100 text-gray-600'
}

function getAnswerCardClass(question: Question, index: number): string {
  const isMarked = markedIds.value.has(question.id)
  const isCurrent = currentQuestionIndex.value === index
  if (isCurrent) return 'ring-2 ring-primary-400 bg-primary-50 text-primary-700 font-bold'
  if (isMarked) return 'bg-warning-400 text-white hover:bg-warning-500'
  if (hasAnswered(question)) return 'bg-primary-500 text-white hover:bg-primary-600'
  return 'bg-gray-100 text-gray-500 hover:bg-gray-200'
}

function toggleMark(id: number) {
  if (markedIds.value.has(id)) {
    markedIds.value.delete(id)
  } else {
    markedIds.value.add(id)
  }
  // 触发响应式更新
  markedIds.value = new Set(markedIds.value)
}

function scrollToQuestion(index: number) {
  currentQuestionIndex.value = index
  const el = document.getElementById('question-' + questions.value[index].id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

// 答案变更处理（防抖保存）
let saveDebounceTimer: ReturnType<typeof setTimeout> | null = null
function onAnswerChange(_questionId: number) {
  if (saveDebounceTimer) clearTimeout(saveDebounceTimer)
  saveDebounceTimer = setTimeout(() => doSaveProgress(), 1500)
}
function onMultiAnswerChange(questionId: number) {
  // 多选变更也触发保存
  onAnswerChange(questionId)
}

// 格式化倒计时
function formatCountdown(seconds: number): string {
  if (seconds <= 0) return '00:00:00'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

// ========== API 调用 ==========

async function initExam() {
  loading.value = true
  errorMsg.value = ''
  try {
    // 1. 开始考试，获取考试记录和题目
    const record = await startExam(examId.value)
    examRecord.value = record
    questions.value = record.questions || []

    // 2. 恢复已有答案（如果有）
    if (record.questions) {
      for (const q of record.questions) {
        if (q.myAnswer !== undefined && q.myAnswer !== null && q.myAnswer !== '') {
          if (q.type === 'multi_choice' && typeof q.myAnswer === 'string') {
            multiAnswers.value[q.id] = q.myAnswer.split(',')
          } else if (q.type !== 'multi_choice') {
            answers.value[q.id] = String(q.myAnswer)
          }
        }
      }
    }

    // 3. 启动倒计时
    startCountdown(record.endTime)

    // 4. 启动自动保存
    startAutoSave()
  } catch (err: any) {
    console.error('初始化考试失败:', err)
    errorMsg.value = err?.message || err?.response?.data?.message || '加载试卷失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function startCountdown(endTimeStr: string) {
  const endTime = new Date(endTimeStr).getTime()
  const update = () => {
    const now = Date.now()
    const diff = Math.floor((endTime - now) / 1000)
    remainingTime.value = diff
    if (diff <= 0) {
      stopCountdown()
      // 时间到，强制交卷
      ElMessage.warning('考试时间已到，系统将自动提交试卷')
      handleForceSubmit()
    }
  }
  update()
  countdownTimer = setInterval(update, 1000)
}

function stopCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

function startAutoSave() {
  autoSaveTimer = setInterval(() => {
    doSaveProgress()
  }, 30_000) // 每30秒自动保存
}

function stopAutoSave() {
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer)
    autoSaveTimer = null
  }
}

async function doSaveProgress() {
  if (!examRecord.value || saving.value) return
  saving.value = true
  try {
    const data = buildSubmitData()
    await saveExamProgress(examRecord.value.examRecordId, data)
    lastSaveTime.value = new Date()
  } catch (err) {
    console.error('保存进度失败:', err)
  } finally {
    saving.value = false
  }
}

function buildSubmitData() {
  const answerList: { questionId: number; answer: string }[] = []
  for (const q of questions.value) {
    let answer: string
    if (q.type === 'multi_choice') {
      const arr = multiAnswers.value[q.id]
      answer = Array.isArray(arr) ? arr.sort().join(',') : ''
    } else {
      answer = answers.value[q.id] ?? ''
    }
    answerList.push({ questionId: q.id, answer })
  }
  return { answers: answerList }
}

// ========== 提交逻辑 ==========

function handleSubmit() {
  if (questions.value.length === 0) return
  showSubmitDialog.value = true
}

async function confirmSubmit() {
  if (!examRecord.value) return
  submitting.value = true
  try {
    // 先保存一次最新进度
    const data = buildSubmitData()
    await saveExamProgress(examRecord.value.examRecordId, data)
    // 再提交
    const submitResult: any = await submitExam(examRecord.value.examRecordId)
    ElMessage.success('试卷提交成功！')
    if (submitResult?.gradeId) {
      router.push('/report/' + submitResult.gradeId)
    } else {
      router.push('/courses')
    }
  } catch (err: any) {
    console.error('提交失败:', err)
    ElMessage.error(err?.message || err?.response?.data?.message || '提交失败，请重试')
  } finally {
    submitting.value = false
    showSubmitDialog.value = false
  }
}

async function handleForceSubmit() {
  if (!examRecord.value) return
  submitting.value = true
  try {
    const data = buildSubmitData()
    await saveExamProgress(examRecord.value.examRecordId, data)
    const submitResult: any = await submitExam(examRecord.value.examRecordId)
    ElMessage.warning('考试时间已到，系统已自动为您提交试卷')
    if (submitResult?.gradeId) {
      router.push('/report/' + submitResult.gradeId)
    } else {
      router.push('/courses')
    }
  } catch (err) {
    console.error('自动提交失败:', err)
    ElMessage.error('自动提交失败，请联系老师')
  } finally {
    submitting.value = false
  }
}

// 离开页面前提示
function handleBeforeUnload(e: BeforeUnloadEvent) {
  if (!submitting.value && questions.value.length > 0) {
    e.preventDefault()
    e.returnValue = ''
  }
}

// ========== 生命周期 ==========
onMounted(async () => {
  await initExam()
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onBeforeUnmount(() => {
  stopCountdown()
  stopAutoSave()
  if (saveDebounceTimer) clearTimeout(saveDebounceTimer)
  window.removeEventListener('beforeunload', handleBeforeUnload)
})
</script>
