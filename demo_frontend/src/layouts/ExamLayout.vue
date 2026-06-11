<template>
  <div class="h-screen flex flex-col bg-[#F5F7FA] overflow-hidden">
    <!-- 网络状态提示 -->
    <transition name="fade">
      <div
        v-if="isOffline"
        class="fixed top-4 left-1/2 -translate-x-1/2 z-50 flex items-center gap-2 px-4 py-2.5 bg-warning-500 text-white rounded-lg shadow-lg"
      >
        <i class="ri-wifi-off-line"></i>
        <span class="text-sm font-medium">网络连接已断开，答题数据将保存在本地</span>
      </div>
    </transition>

    <!-- 顶部栏 -->
    <header class="h-16 bg-white flex items-center justify-between px-6 flex-shrink-0 shadow-sm z-30 relative">
      <!-- 左侧：考试信息 -->
      <div class="flex items-center gap-4">
        <div class="w-9 h-9 rounded-lg bg-primary-50 flex items-center justify-center">
          <i class="ri-graduation-cap-fill text-primary-500 text-lg"></i>
        </div>
        <div>
          <h1 class="text-sm font-semibold text-gray-800">{{ examData.examName }}</h1>
          <p class="text-xs text-gray-400">{{ studentName }} · {{ className }}</p>
        </div>
      </div>

      <!-- 中间：倒计时 -->
      <div class="flex-1 flex justify-center">
        <div 
          :class="[
            'px-4 py-2 rounded-lg font-mono text-base font-bold',
            timeRemaining <= 300 ? 'bg-danger-500 text-white animate-pulse' : 'bg-gray-100 text-gray-800'
          ]"
        >
          <i class="ri-timer-line mr-1.5"></i>
          {{ formatTime(timeRemaining) }}
        </div>
      </div>

      <!-- 右侧：答题进度 + 交卷按钮 -->
      <div class="flex items-center gap-3">
        <div class="text-sm text-gray-500">
          已答 <strong class="text-primary-500 text-base">{{ answeredCount }}</strong>/{{ totalQuestions }} 题
        </div>
        <button
          @click="showSubmitConfirm = true"
          class="px-6 py-2.5 bg-danger-500 text-white text-sm font-medium rounded-lg hover:bg-danger-400 transition-colors cursor-pointer whitespace-nowrap"
        >
          交卷
        </button>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="flex-1 flex overflow-hidden">
      <!-- 左侧答题卡 -->
      <div class="w-[200px] bg-white border-r border-gray-100 flex-shrink-0 overflow-y-auto p-3">
        <div class="text-xs font-semibold text-gray-500 mb-2 px-1">答题卡</div>
        
        <!-- 题型统计 -->
        <div class="grid grid-cols-2 gap-1 mb-3 pb-3 border-b border-gray-100">
          <div class="text-center p-1.5 rounded bg-gray-50">
            <div class="text-xs text-gray-400">已答</div>
            <div class="text-sm font-bold text-success-500">{{ answeredCount }}</div>
          </div>
          <div class="text-center p-1.5 rounded bg-gray-50">
            <div class="text-xs text-gray-400">未答</div>
            <div class="text-sm font-bold text-gray-600">{{ totalQuestions - answeredCount }}</div>
          </div>
        </div>

        <!-- 题号网格 -->
        <div class="grid grid-cols-5 gap-1.5">
          <button
            v-for="(q, index) in questions"
            :key="q.id"
            @click="jumpToQuestion(index)"
            :class="[
              'w-full aspect-square rounded-md text-sm font-medium transition-all duration-150 cursor-pointer flex items-center justify-center relative',
              currentIndex === index
                ? 'ring-2 ring-primary-500 bg-primary-500 text-white'
                : getAnswerStatus(q.id) === 'answered'
                  ? 'bg-success-500 text-white hover:bg-success-600'
                  : getAnswerStatus(q.id) === 'marked'
                    ? 'bg-warning-500 text-white hover:bg-warning-600'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            ]"
          >
            {{ index + 1 }}
            <span 
              v-if="getAnswerStatus(q.id) === 'marked'" 
              class="absolute bottom-0.5 right-0.5 w-1.5 h-1.5 rounded-full bg-white"
            ></span>
          </button>
        </div>
      </div>

      <!-- 右侧题目作答区 -->
      <div ref="questionContainerRef" class="flex-1 overflow-y-auto p-6">
        <!-- 加载中 -->
        <div v-if="loading" class="flex items-center justify-center h-full">
          <div class="text-center">
            <i class="ri-loader-4-line animate-spin text-4xl text-primary-500 mb-3 block"></i>
            <p class="text-gray-500">正在加载试卷...</p>
          </div>
        </div>

        <!-- 错误提示 -->
        <div v-else-if="errorMsg" class="flex items-center justify-center h-full">
          <div class="text-center bg-white rounded-xl p-8 shadow-sm max-w-md">
            <i class="ri-error-warning-line text-5xl text-danger-500 mb-4 block"></i>
            <p class="text-gray-700 mb-4">{{ errorMsg }}</p>
            <button @click="initExam()" class="px-4 py-2 bg-primary-500 text-white rounded-lg text-sm cursor-pointer">重试</button>
          </div>
        </div>

        <!-- 题目列表 -->
        <div v-else class="max-w-[860px] mx-auto space-y-5">
          <!-- 题目列表 -->
          <div
            v-for="(question, index) in questions"
            :key="question.id"
            :ref="(el: any) => { if (el) questionRefs[index] = el }"
            :id="'question-' + question.id"
            class="bg-white rounded-xl p-5 shadow-sm border border-transparent transition-all"
            :class="{ 'border-primary-200 ring-2 ring-primary-50': currentIndex === index }"
          >
            <!-- 题目头部 -->
            <div class="flex items-start gap-3 mb-4">
              <span 
                :class="[
                  'inline-flex items-center justify-center w-7 h-7 rounded-lg text-xs font-bold flex-shrink-0',
                  question.type === 'single_choice' ? 'bg-blue-50 text-blue-600' :
                  question.type === 'multi_choice' ? 'bg-purple-50 text-purple-600' :
                  question.type === 'true_false' ? 'bg-green-50 text-green-600' :
                  question.type === 'fill_blank' ? 'bg-orange-50 text-orange-600' :
                  'bg-pink-50 text-pink-600'
                ]"
              >
                {{ index + 1 }}
              </span>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 mb-2">
                  <span 
                    :class="[
                      'text-xs font-medium px-2 py-0.5 rounded',
                      question.type === 'single_choice' ? 'bg-blue-50 text-blue-600' :
                      question.type === 'multi_choice' ? 'bg-purple-50 text-purple-600' :
                      question.type === 'true_false' ? 'bg-green-50 text-green-600' :
                      question.type === 'fill_blank' ? 'bg-orange-50 text-orange-600' :
                      'bg-pink-50 text-pink-600'
                    ]"
                  >
                    {{ getTypeName(question.type) }}
                  </span>
                  <span class="text-xs text-gray-400">· {{ question.score }} 分</span>
                </div>
                <p class="text-[15px] leading-relaxed text-gray-800">{{ question.content }}</p>
              </div>
            </div>

            <!-- 选项区域 -->
            <div class="ml-10 mt-3 space-y-2">
              <!-- 单选/多选题 -->
              <template v-if="question.options && question.options.length > 0">
                <label
                  v-for="option in question.options"
                  :key="option.label"
                  :class="[
                    'flex items-center gap-3 p-3 rounded-lg border-2 cursor-pointer transition-all',
                    isSelected(question.id, option.label)
                      ? (question.type === 'multi_choice' ? 'border-purple-300 bg-purple-50' : 'border-primary-300 bg-primary-50')
                      : 'border-gray-100 hover:border-gray-200 hover:bg-gray-50'
                  ]"
                  @click="handleAnswer(question.id, option.label, index)"
                >
                  <span 
                    :class="[
                      'w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 transition-all',
                      question.type === 'multi_choice' ? 'rounded' : '',
                      isSelected(question.id, option.label)
                        ? (question.type === 'multi_choice' ? 'border-purple-500 bg-purple-500' : 'border-primary-500 bg-primary-500')
                        : 'border-gray-300'
                    ]"
                  >
                    <i v-if="isSelected(question.id, option.label)" class="ri-check-line text-white text-xs"></i>
                  </span>
                  <span class="text-sm text-gray-700">{{ option.label }}. {{ option.content }}</span>
                </label>
              </template>

              <!-- 判断题 -->
              <div v-else-if="question.type === 'true_false'" class="flex gap-3">
                <label
                  :class="[
                    'flex-1 flex items-center justify-center gap-2 p-3 rounded-lg border-2 cursor-pointer transition-all',
                    answers[question.id] === true ? 'border-success-300 bg-success-50' : 'border-gray-100 hover:border-gray-200'
                  ]"
                  @click="handleAnswer(question.id, true, index)"
                >
                  <span :class="['w-5 h-5 rounded-full border-2 flex items-center justify-center', answers[question.id] === true ? 'border-success-500 bg-success-500' : 'border-gray-300']">
                    <i v-if="answers[question.id] === true" class="ri-check-line text-white text-xs"></i>
                  </span>
                  <span class="text-sm font-medium">正确 (T)</span>
                </label>
                <label
                  :class="[
                    'flex-1 flex items-center justify-center gap-2 p-3 rounded-lg border-2 cursor-pointer transition-all',
                    answers[question.id] === false ? 'border-danger-300 bg-danger-50' : 'border-gray-100 hover:border-gray-200'
                  ]"
                  @click="handleAnswer(question.id, false, index)"
                >
                  <span :class="['w-5 h-5 rounded-full border-2 flex items-center justify-center', answers[question.id] === false ? 'border-danger-500 bg-danger-500' : 'border-gray-300']">
                    <i v-if="answers[question.id] === false" class="ri-close-line text-white text-xs"></i>
                  </span>
                  <span class="text-sm font-medium">错误 (F)</span>
                </label>
              </div>

              <!-- 填空题 -->
              <input
                v-else-if="question.type === 'fill_blank'"
                type="text"
                :value="answers[question.id] || ''"
                @input="(e: any) => handleAnswer(question.id, e.target.value, index)"
                placeholder="请输入答案..."
                class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-primary-400 focus:outline-none text-sm"
              />

              <!-- 简答题 -->
              <textarea
                v-else-if="question.type === 'essay'"
                :value="answers[question.id] || ''"
                @input="(e: any) => handleAnswer(question.id, e.target.value, index)"
                placeholder="请输入您的答案..."
                rows="4"
                class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-lg focus:border-primary-400 focus:outline-none text-sm resize-none"
              ></textarea>
            </div>
          </div>

          <!-- 底部导航按钮 -->
          <div class="flex items-center justify-center gap-3 pt-4 pb-8">
            <button
              @click="toggleMark(questions[currentIndex].id)"
              :class="[
                'flex items-center gap-1.5 px-4 py-2.5 rounded-lg text-sm font-medium transition-colors border cursor-pointer',
                isMarked(questions[currentIndex].id)
                  ? 'bg-warning-50 text-warning-600 border-warning-300'
                  : 'text-gray-500 border-gray-200 hover:bg-gray-50'
              ]"
            >
              <i :class="isMarked(questions[currentIndex].id) ? 'ri-flag-fill' : 'ri-flag-line'"></i>
              {{ isMarked(questions[currentIndex].id) ? '已标记' : '标记此题' }}
            </button>

            <button
              @click="showSubmitConfirm = true"
              class="flex items-center gap-1.5 px-5 py-2.5 rounded-lg text-sm font-medium bg-danger-500 text-white hover:bg-danger-400 transition-colors cursor-pointer"
            >
              <i class="ri-check-line"></i>
              交卷
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 交卷确认弹窗 -->
    <teleport to="body">
      <transition name="fade">
        <div v-if="showSubmitConfirm" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="showSubmitConfirm = false">
          <div class="bg-white rounded-2xl w-full max-w-[420px] shadow-xl overflow-hidden" @click.stop>
            <div class="p-6">
              <div class="flex items-center gap-3 mb-4">
                <div class="w-12 h-12 rounded-full bg-danger-50 flex items-center justify-center">
                  <i class="ri-error-warning-line text-danger-500 text-2xl"></i>
                </div>
                <div>
                  <h3 class="text-lg font-bold text-gray-800">确认提交试卷？</h3>
                  <p class="text-sm text-gray-500 mt-0.5">提交后将无法修改答案</p>
                </div>
              </div>

              <div class="bg-gray-50 rounded-lg p-4 mb-4">
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-500">已答题目</span>
                  <span class="font-bold text-gray-800">{{ answeredCount }} / {{ totalQuestions }}</span>
                </div>
                <div v-if="answeredCount < totalQuestions" class="mt-2 text-xs text-warning-600 flex items-center gap-1">
                  <i class="ri-error-warning-line"></i>
                  还有 {{ totalQuestions - answeredCount }} 道题目未作答
                </div>
              </div>

              <div class="flex gap-3">
                <button
                  @click="showSubmitConfirm = false"
                  class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors cursor-pointer"
                >
                  继续答题
                </button>
                <button
                  @click="handleSubmit"
                  class="flex-1 px-4 py-2.5 text-sm font-medium text-white bg-danger-500 rounded-lg hover:bg-danger-400 transition-colors cursor-pointer"
                >
                  确认交卷
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { Question } from '@/api/types'
import { startExam, saveExamProgress, submitExam } from '@/api/student'

const router = useRouter()
const route = useRoute()

// ========== 从 API 加载真实数据 ==========
const examId = computed(() => Number(route.params.id || route.params.examId))
const loading = ref(true)
const errorMsg = ref('')

const examData = ref<any>({
  id: 0,
  examName: '',
  duration: 120,
})

const studentName = ref('')
const className = ref('')
const examRecord = ref<any>(null)

const questions = ref<Question[]>([])

// 状态
const answers = ref<Record<number, any>>({})
const markedQuestions = ref<Set<number>>(new Set())
const currentIndex = ref(0)
const showSubmitConfirm = ref(false)
const isOffline = ref(!navigator.onLine)
const isSubmitting = ref(false)

// 引用
const questionContainerRef = ref<HTMLElement | null>(null)
const questionRefs = ref<(HTMLElement | null)[]>([])

// 计算属性
const totalQuestions = computed(() => questions.value.length)
const answeredCount = computed(() => {
  return Object.values(answers.value).filter(v => 
    v !== undefined && v !== '' && (!Array.isArray(v) || v.length > 0)
  ).length
})

const timeRemaining = ref(0) // 从API获取

// ========== 加载考试数据 ==========
async function initExam() {
  loading.value = true
  errorMsg.value = ''
  try {
    const record = await startExam(examId.value)
    examRecord.value = record
    examData.value = {
      id: record.examId || examId.value,
      examName: record.examName || '',
      duration: record.duration || 120,
    }
    // 从题目数据加载（标准化类型名称）
    const typeMap: Record<string, string> = {
      single: 'single_choice', single_choice: 'single_choice',
      multi: 'multi_choice', multi_choice: 'multi_choice',
      true_false: 'true_false', judge: 'true_false',
      fill_blank: 'fill_blank', blank: 'fill_blank',
      essay: 'essay', short_answer: 'essay',
    }
    questions.value = (record.questions || []).map((q: any) => ({
      ...q,
      type: typeMap[q.type] || q.type,
      options: q.options || [],
    }))

    // 恢复已有答案
    if (record.questions) {
      for (const q of record.questions) {
        if (q.myAnswer !== undefined && q.myAnswer !== null && q.myAnswer !== '') {
          if (q.type === 'multi_choice' && typeof q.myAnswer === 'string') {
            answers.value[q.id] = q.myAnswer.split(',')
          } else if (q.type !== 'multi_choice') {
            answers.value[q.id] = String(q.myAnswer)
          }
        }
      }
    }

    // 启动倒计时（优先使用后端计算的剩余时间，否则用 duration）
    timeRemaining.value = record.remainSeconds || (record.duration || 120) * 60

    // 启动自动保存
    startAutoSave()
  } catch (err: any) {
    console.error('加载考试失败:', err)
    errorMsg.value = err?.message || '加载试卷失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// ========== 自动保存到后端 ==========
let autoSaveTimer: ReturnType<typeof setInterval> | null = null
function startAutoSave() {
  autoSaveTimer = setInterval(() => doSaveProgress(), 30_000)
}

function stopAutoSave() {
  if (autoSaveTimer) { clearInterval(autoSaveTimer); autoSaveTimer = null }
}

async function doSaveProgress() {
  if (!examRecord.value || !examRecord.value.recordId) return
  try {
    const answerList: { questionId: number; answer: string }[] = []
    for (const q of questions.value) {
      let answer: string
      if (q.type === 'multi_choice') {
        const arr = answers.value[q.id]
        answer = Array.isArray(arr) ? arr.sort().join(',') : ''
      } else {
        answer = String(answers.value[q.id] ?? '')
      }
      answerList.push({ questionId: q.id, answer })
    }
    await saveExamProgress(examRecord.value.recordId, { answers: answerList })
  } catch (e) {
    console.error('保存进度失败:', e)
  }
}

// ========== 提交试卷 ==========
async function handleSubmit() {
  showSubmitConfirm.value = false
  isSubmitting.value = true
  try {
    if (!examRecord.value?.recordId) return
    const answerList: { questionId: number; answer: string }[] = []
    for (const q of questions.value) {
      let answer: string
      if (q.type === 'multi_choice') {
        const arr = answers.value[q.id]
        answer = Array.isArray(arr) ? arr.sort().join(',') : ''
      } else {
        answer = String(answers.value[q.id] ?? '')
      }
      answerList.push({ questionId: q.id, answer })
    }
    await submitExam(examRecord.value.recordId, { answers: answerList })
    stopAutoSave()
    ElMessage.success('交卷成功！')
    router.push('/home')
  } catch (err: any) {
    ElMessage.error(err?.message || '交卷失败，请重试')
  } finally {
    isSubmitting.value = false
  }
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

function isSelected(questionId: number, value: any): boolean {
  const answer = answers.value[questionId]
  if (Array.isArray(answer)) return answer.includes(value)
  return answer === value
}

function getAnswerStatus(questionId: number): 'answered' | 'marked' | 'empty' {
  if (markedQuestions.value.has(questionId)) return 'marked'
  const answer = answers.value[questionId]
  if (answer !== undefined && answer !== '' && (!Array.isArray(answer) || answer.length > 0)) return 'answered'
  return 'empty'
}

function handleAnswer(questionId: number, answer: any, index?: number) {
  if (typeof index === 'number') {
    currentIndex.value = index
  } else {
    const foundIndex = questions.value.findIndex(q => q.id === questionId)
    if (foundIndex >= 0) currentIndex.value = foundIndex
  }
  // 多选题特殊处理
  const question = questions.value.find(q => q.id === questionId)
  if (question?.type === 'multi_choice') {
    const current = Array.isArray(answers.value[questionId]) ? [...answers.value[questionId]] : []
    if (current.includes(answer)) {
      answers.value[questionId] = current.filter(v => v !== answer)
    } else {
      answers.value[questionId] = [...current, answer]
    }
  } else {
    answers.value[questionId] = answer
  }

  // 自动保存到 localStorage（断点续考）
  autoSave()
}

function toggleMark(questionId: number) {
  if (markedQuestions.value.has(questionId)) {
    markedQuestions.value.delete(questionId)
  } else {
    markedQuestions.value.add(questionId)
  }
}

function isMarked(questionId: number): boolean {
  return markedQuestions.value.has(questionId)
}

function jumpToQuestion(index: number) {
  currentIndex.value = index
  questionRefs.value[index]?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function goToPrev() {
  if (currentIndex.value > 0) {
    currentIndex.value--
    questionRefs.value[currentIndex.value]?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

function goToNext() {
  if (currentIndex.value < totalQuestions.value - 1) {
    currentIndex.value++
    questionRefs.value[currentIndex.value]?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

function formatTime(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

function autoSave() {
  const saveData = {
    answers: answers.value,
    markedQuestions: Array.from(markedQuestions.value),
    currentIndex: currentIndex.value,
    timestamp: new Date().toISOString(),
  }
  localStorage.setItem('exam_autosave', JSON.stringify(saveData))
}

// 倒计时
let timerInterval: ReturnType<typeof setInterval>
onMounted(async () => {
  // 从API加载考试数据
  await initExam()

  // 倒计时
  timerInterval = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
    } else {
      clearInterval(timerInterval)
      handleSubmit()
    }
  }, 1000)

  // 监听网络状态
  window.addEventListener('online', () => isOffline.value = false)
  window.addEventListener('offline', () => isOffline.value = true)

  // 键盘快捷键
  const handleKeyDown = (e: KeyboardEvent) => {
    if (showSubmitConfirm.value || isSubmitting.value) return
    
    const key = e.key.toUpperCase()
    if (['A', 'B', 'C', 'D'].includes(key)) {
      const question = questions.value[currentIndex.value]
      if (question) handleAnswer(question.id, key, currentIndex.value)
    } else if (e.key === 'ArrowLeft') {
      e.preventDefault()
      goToPrev()
    } else if (e.key === 'ArrowRight') {
      e.preventDefault()
      goToNext()
    }
  }
  window.addEventListener('keydown', handleKeyDown)
})

onUnmounted(() => {
  clearInterval(timerInterval)
  stopAutoSave()
  window.removeEventListener('online', () => isOffline.value = false)
  window.removeEventListener('offline', () => isOffline.value = true)
})
</script>
