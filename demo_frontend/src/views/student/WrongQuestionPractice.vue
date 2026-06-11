<template>
  <div class="h-full overflow-auto p-6 bg-[#F5F7FA]">
    <div class="max-w-5xl mx-auto space-y-5">
      <div class="flex items-center justify-between">
        <div>
          <router-link to="/wrong-questions" class="text-sm text-gray-500 hover:text-primary-500">
            <i class="ri-arrow-left-line mr-1"></i>返回错题本
          </router-link>
          <h1 class="text-xl font-bold text-gray-800 mt-2">错题刷题模式</h1>
          <p class="text-sm text-gray-500 mt-1">仅练习本次选中的考试错题，答题后可查看结果和解析。</p>
        </div>
        <div class="text-sm text-gray-500">
          {{ currentIndex + 1 }} / {{ practiceQuestions.length }}
        </div>
      </div>

      <div v-if="loading" class="bg-white rounded-xl p-12 text-center text-gray-400">
        <i class="ri-loader-4-line animate-spin text-3xl block mb-2"></i>
        正在加载错题...
      </div>

      <div v-else-if="practiceQuestions.length === 0" class="bg-white rounded-xl p-12 text-center text-gray-400">
        <i class="ri-checkbox-circle-line text-4xl block mb-3"></i>
        暂无可练习错题
      </div>

      <template v-else>
        <div class="bg-white rounded-xl p-5 shadow-sm">
          <div class="flex gap-2 flex-wrap">
            <button
              v-for="(item, index) in practiceQuestions"
              :key="item.wrongQuestionId"
              :class="[
                'w-9 h-9 rounded-lg text-sm font-semibold transition-colors',
                currentIndex === index
                  ? 'bg-primary-500 text-white'
                  : results[item.wrongQuestionId]
                    ? (results[item.wrongQuestionId].isCorrect ? 'bg-success-100 text-success-700' : 'bg-danger-100 text-danger-700')
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              ]"
              @click="currentIndex = index"
            >
              {{ index + 1 }}
            </button>
          </div>
        </div>

        <div class="bg-white rounded-2xl p-6 shadow-sm">
          <div class="flex items-center gap-2 mb-4">
            <el-tag effect="light">{{ getTypeName(currentQuestion.type) }}</el-tag>
            <el-tag type="warning" effect="light">{{ currentQuestion.examName || currentQuestion.courseName }}</el-tag>
            <span class="text-xs text-gray-400">错 {{ currentQuestion.wrongCount || currentQuestion.wrongTimes || 1 }} 次</span>
          </div>

          <div class="text-base text-gray-800 leading-relaxed mb-5 whitespace-pre-wrap">{{ currentQuestion.content }}</div>

          <div v-if="currentQuestion.options?.length" class="space-y-2 mb-5">
            <label
              v-for="option in currentQuestion.options"
              :key="option.label"
              :class="[
                'flex items-center gap-3 p-3 rounded-xl border-2 cursor-pointer transition-all',
                answer === option.label ? 'border-primary-300 bg-primary-50' : 'border-gray-100 hover:bg-gray-50'
              ]"
              @click="answer = option.label"
            >
              <span :class="['w-6 h-6 rounded-full border-2 flex items-center justify-center', answer === option.label ? 'border-primary-500 bg-primary-500 text-white' : 'border-gray-300']">
                <i v-if="answer === option.label" class="ri-check-line"></i>
              </span>
              <span class="text-sm text-gray-700">{{ option.label }}. {{ option.content }}</span>
            </label>
          </div>

          <textarea
            v-else
            v-model="answer"
            rows="4"
            placeholder="请输入你的答案..."
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl text-sm focus:border-primary-400 focus:outline-none resize-none mb-5"
          ></textarea>

          <div v-if="currentResult" class="rounded-xl p-4 mb-5" :class="currentResult.isCorrect ? 'bg-success-50' : 'bg-danger-50'">
            <div class="font-semibold" :class="currentResult.isCorrect ? 'text-success-700' : 'text-danger-700'">
              {{ currentResult.isCorrect ? '回答正确' : '回答错误' }}
            </div>
            <div class="text-sm text-gray-700 mt-2">正确答案：{{ currentResult.correctAnswer || currentQuestion.correctAnswer }}</div>
            <p v-if="currentResult.analysis || currentQuestion.analysis" class="text-sm text-gray-600 mt-2 leading-relaxed">
              {{ currentResult.analysis || currentQuestion.analysis }}
            </p>
          </div>

          <div class="flex items-center justify-between">
            <el-button :disabled="currentIndex === 0" @click="goPrev">上一道</el-button>
            <div class="flex gap-2">
              <el-button @click="answer = ''">清空答案</el-button>
              <el-button type="primary" :loading="submitting" @click="submitCurrent">提交本题</el-button>
              <el-button type="success" :disabled="currentIndex >= practiceQuestions.length - 1" @click="goNext">下一道</el-button>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { WrongQuestion } from '@/api/types'
import { getWrongQuestionDetail, getWrongQuestions, practiceWrongQuestion } from '@/api/student'

const route = useRoute()
const loading = ref(false)
const submitting = ref(false)
const currentIndex = ref(0)
const answer = ref('')
const practiceQuestions = ref<WrongQuestion[]>([])
const results = ref<Record<number, any>>({})

const currentQuestion = computed(() => practiceQuestions.value[currentIndex.value] || {} as WrongQuestion)
const currentResult = computed(() => currentQuestion.value?.wrongQuestionId ? results.value[currentQuestion.value.wrongQuestionId] : null)

watch(currentIndex, () => {
  answer.value = ''
})

async function loadQuestions() {
  loading.value = true
  try {
    const ids = String(route.query.ids || '')
      .split(',')
      .map(item => Number(item))
      .filter(Boolean)
    const examId = route.query.examId ? Number(route.query.examId) : undefined
    if (ids.length > 0) {
      const details = await Promise.all(ids.map(id => getWrongQuestionDetail(id)))
      practiceQuestions.value = details.map((item: any) => normalizeWrong(item))
    } else {
      const res = await getWrongQuestions({ examId, sourceType: 'exam' })
      const list = Array.isArray(res) ? res : (res?.data || [])
      practiceQuestions.value = await Promise.all(list.map(async (item: any) => {
        const id = item.id ?? item.wrongQuestionId
        const detail = await getWrongQuestionDetail(id).catch(() => item)
        return normalizeWrong({ ...item, ...(detail as any) })
      }))
    }
  } catch (error) {
    console.error('加载错题刷题失败', error)
    ElMessage.error('加载错题失败')
  } finally {
    loading.value = false
  }
}

function normalizeWrong(item: any): WrongQuestion {
  return {
    ...item,
    wrongQuestionId: item.id ?? item.wrongQuestionId,
    myAnswer: item.myAnswer ?? item.studentAnswer,
    sourceTypeName: item.sourceType === 'exam' ? '考试错题' : '自建题库',
  }
}

async function submitCurrent() {
  if (!currentQuestion.value?.wrongQuestionId) return
  if (!String(answer.value).trim()) {
    ElMessage.warning('请先作答')
    return
  }
  submitting.value = true
  try {
    const result = await practiceWrongQuestion(currentQuestion.value.wrongQuestionId, String(answer.value).trim())
    results.value[currentQuestion.value.wrongQuestionId] = result
    ElMessage.success((result as any)?.isCorrect ? '回答正确' : '已提交，注意查看解析')
  } catch (error: any) {
    ElMessage.error(error?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

function goPrev() {
  if (currentIndex.value > 0) currentIndex.value--
}

function goNext() {
  if (currentIndex.value < practiceQuestions.value.length - 1) currentIndex.value++
}

function getTypeName(type: string) {
  const map: Record<string, string> = {
    single_choice: '单选题',
    multi_choice: '多选题',
    true_false: '判断题',
    fill_blank: '填空题',
    essay: '简答题',
  }
  return map[type] || type || '题目'
}

onMounted(loadQuestions)
</script>
