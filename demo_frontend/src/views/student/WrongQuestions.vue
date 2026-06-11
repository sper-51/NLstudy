﻿﻿﻿<template>
  <div class="h-full overflow-auto p-6">
    <!-- 页面标题 + 统计 -->
    <div class="flex items-start justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-gray-800">错题本</h1>
        <p class="text-sm text-gray-500 mt-1">共 {{ totalCount }} 道错题</p>
      </div>
      
      <!-- 统计信息 -->
      <div class="flex gap-3">
        <div class="px-4 py-2 bg-danger-50 rounded-lg text-sm">
          <span class="text-danger-600 font-semibold">{{ highFreqCount }}</span>
          <span class="text-gray-500 ml-1">高频错题</span>
        </div>
        <div class="px-4 py-2 bg-primary-50 rounded-lg text-sm">
          <span class="text-primary-600 font-semibold">{{ courseCount }}</span>
          <span class="text-gray-500 ml-1">涉及课程</span>
        </div>
        <div class="px-4 py-2 bg-warning-50 rounded-lg text-sm">
          <span class="text-warning-600 font-semibold">{{ examCount }}</span>
          <span class="text-gray-500 ml-1">涉及考试</span>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="bg-white rounded-xl p-4 shadow-sm mb-6">
      <div class="flex items-center gap-4 flex-wrap">
        <div class="flex items-center gap-2">
          <span class="text-sm text-gray-500 whitespace-nowrap">来源类型：</span>
          <div class="flex bg-gray-100 rounded-lg p-0.5">
            <button
              v-for="type in sourceTypes"
              :key="type.value"
              @click="filters.sourceType = filters.sourceType === type.value ? '' : type.value"
              :class="[
                'px-3 py-1.5 text-sm font-medium rounded-md transition-all',
                filters.sourceType === type.value ? 'bg-white text-primary-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'
              ]"
            >
              {{ type.label }}
            </button>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-sm text-gray-500 whitespace-nowrap">课程：</span>
          <el-select v-model="filters.courseId" placeholder="全部课程" clearable size="default" style="width: 220px">
            <el-option v-for="course in courseOptions" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-sm text-gray-500 whitespace-nowrap">知识点：</span>
          <el-select
            v-model="filters.knowledgePoint"
            placeholder="全部知识点"
            clearable
            size="default"
            style="width: 200px"
            :disabled="knowledgeOptions.length === 0"
            :empty-values="[null, undefined]"
            value-on-clear=""
          >
            <el-option v-if="knowledgeOptions.length === 0" label="暂无知识点可筛选" value="" disabled />
            <el-option v-for="point in knowledgeOptions" :key="point" :label="point" :value="point" />
          </el-select>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-[minmax(0,1fr)_minmax(420px,1fr)] gap-5 items-start">
      <div class="min-w-0">
        <!-- 自建题库区域 -->
        <div class="bg-gradient-to-r from-purple-50 to-blue-50 rounded-xl p-5 mb-6 border border-purple-100">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
              <i class="ri-folder-add-line text-primary-500"></i>
              我的自建题库
            </h3>
            <button @click="showCreateBankModal = true" class="px-3 py-1.5 bg-primary-500 text-white text-sm rounded-lg hover:bg-primary-600 transition-colors cursor-pointer flex items-center gap-1">
              <i class="ri-add-line"></i> 新建题库
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div
              v-for="bank in myBanks"
              :key="bank.id"
              class="bg-white rounded-lg p-4 border border-gray-100 hover:shadow-md transition-all cursor-pointer"
            >
              <div class="flex items-start justify-between mb-2">
                <div class="w-9 h-9 rounded-lg bg-primary-50 flex items-center justify-center">
                  <i class="ri-bookmark-fill text-primary-500"></i>
                </div>
                <span v-if="bank.shareCode" class="px-2 py-0.5 bg-green-50 text-green-600 text-xs rounded">已分享</span>
              </div>
              <h4 class="font-medium text-sm text-gray-800 mb-1 truncate">{{ bank.bankName }}</h4>
              <p class="text-xs text-gray-500 mb-3">{{ bank.questionCount }} 道题目</p>
              
              <div class="flex gap-2">
                <button class="flex-1 px-3 py-1.5 bg-primary-50 text-primary-600 text-xs rounded-lg hover:bg-primary-100 transition-colors cursor-pointer">
                  开始练习
                </button>
                <button @click="handleShare(bank)" class="px-3 py-1.5 bg-gray-100 text-gray-600 text-xs rounded-lg hover:bg-gray-200 transition-colors cursor-pointer">
                  分享
                </button>
              </div>
            </div>

            <div 
              v-if="myBanks.length === 0"
              class="col-span-1 md:col-span-3 py-8 text-center text-gray-400 text-sm border-2 border-dashed border-gray-200 rounded-lg cursor-pointer hover:border-primary-300 hover:text-primary-500 transition-colors"
              @click="showCreateBankModal = true"
            >
              <i class="ri-add-circle-line text-2xl mb-2 block"></i>
              点击创建你的第一个题库
            </div>
          </div>
        </div>

        <!-- 错题列表 -->
        <div class="bg-white rounded-xl p-5 shadow-sm">
          <div class="flex items-center justify-between gap-3 mb-4">
            <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
              <i class="ri-error-warning-line text-danger-500"></i>
              错题列表
            </h3>
            <div class="flex items-center gap-2">
              <el-button
                size="small"
                type="success"
                :disabled="wrongQuestions.length === 0"
                :title="wrongQuestions.length === 0 ? '暂无错题可练习' : '选择课程和考试后开始刷题'"
                @click="openPracticeDialog"
              >
                <i class="ri-play-circle-line mr-1"></i>
                刷题模式
              </el-button>
              <el-button
                size="small"
                type="danger"
                plain
                :disabled="selectedWrongIds.length === 0"
                :title="selectedWrongIds.length === 0 ? '请先勾选错题' : '删除所选错题'"
                @click="deleteSelectedWrongQuestions"
              >
                <i class="ri-delete-bin-line mr-1"></i>
                删除选中
              </el-button>
              <el-button
                size="small"
                type="primary"
                :disabled="selectedWrongIds.length === 0"
                :title="selectedWrongIds.length === 0 ? '请先勾选错题' : '加入右侧 AI 对话'"
                @click="addSelectedWrongQuestionsToChat"
              >
                <i class="ri-chat-new-line mr-1"></i>
                问 AI（{{ aiSelectableCount }}/3）
              </el-button>
            </div>
          </div>

          <div class="space-y-3">
            <div
              v-for="wrong in filteredWrongQuestions"
              :key="wrong.wrongQuestionId"
              :class="[
                'p-4 rounded-lg border transition-all cursor-pointer group',
                selectedWrongQuestion?.wrongQuestionId === wrong.wrongQuestionId
                  ? 'border-primary-300 bg-primary-50/40'
                  : 'border-gray-100 hover:border-danger-200 hover:bg-red-50/30'
              ]"
              @click="selectWrongQuestion(wrong)"
            >
          <div class="flex items-start gap-3">
            <el-checkbox
              :model-value="selectedWrongIds.includes(wrong.wrongQuestionId)"
              class="mt-8"
              @click.stop
              @change="(checked: boolean) => toggleWrongSelection(wrong, checked)"
            />
            <!-- 左侧：题目信息 -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-2">
                <span 
                  :class="['px-2 py-0.5 text-xs rounded font-medium', getSourceBadgeClass(wrong.sourceType)]"
                >
                  {{ wrong.sourceTypeName }}
                </span>
                <span class="text-xs text-gray-400">{{ wrong.examName || wrong.courseName }}</span>
                <span v-if="(wrong.wrongCount || 0) > 1" class="px-1.5 py-0.5 bg-danger-50 text-danger-600 text-xs rounded">
                  错{{ wrong.wrongCount }}次
                </span>
              </div>
              
              <p class="text-sm text-gray-800 mb-2 line-clamp-2 group-hover:text-primary-600 transition-colors">
                {{ wrong.content }}
              </p>

              <div v-if="wrong.options?.length" class="grid grid-cols-1 gap-1 mb-2">
                <div v-for="option in wrong.options" :key="option.label" class="text-xs text-gray-500 truncate">
                  <span class="font-medium text-gray-600">{{ option.label }}.</span> {{ option.content }}
                </div>
              </div>

              <div class="flex items-center gap-4 text-xs text-gray-500">
                <span>{{ getTypeName(wrong.type) }}</span>
                <span>我的答案：<span class="text-danger-500 font-medium">{{ wrong.myAnswer || '(未答)' }}</span></span>
                <span>正确答案：<span class="text-success-500 font-medium">{{ wrong.correctAnswer }}</span></span>
                <span class="ml-auto">上次错误：{{ formatDate(wrong.lastWrongTime) }}</span>
              </div>
            </div>

            <!-- 右侧：操作按钮 -->
            <div class="flex flex-col gap-2">
              <button 
                @click.stop="handlePractice(wrong)"
                class="px-3 py-1.5 bg-danger-500 text-white text-xs rounded-lg hover:bg-danger-400 transition-colors cursor-pointer"
              >
                重练
              </button>
              <button 
                @click.stop="toggleFavorite(wrong)"
                :class="[
                  'px-3 py-1.5 text-xs rounded-lg transition-colors cursor-pointer',
                  wrong.isFavorited ? 'bg-warning-100 text-warning-600' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                ]"
              >
                <i :class="wrong.isFavorited ? 'ri-star-fill' : 'ri-star-line'"></i>
                {{ wrong.isFavorited ? '已收藏' : '收藏' }}
              </button>
            </div>
          </div>
            </div>
          </div>

          <div v-if="filteredWrongQuestions.length === 0" class="py-16 text-center text-gray-400">
            <i class="ri-checkbox-circle-line text-4xl mb-3 block"></i>
            <p class="text-lg font-medium">{{ wrongQuestions.length === 0 ? '太棒了！暂无错题' : '当前筛选条件下暂无错题' }}</p>
            <p class="text-sm mt-1">{{ wrongQuestions.length === 0 ? '继续保持，你做得很棒！' : '可以清空筛选条件后再查看' }}</p>
          </div>
        </div>
      </div>

      <AiWrongQuestionPanel
        ref="aiPanelRef"
        :wrong-question="selectedWrongQuestion"
        :related-wrong-questions="chatWrongQuestions"
        class="sticky top-6"
        @remove-related-question="removeChatWrongQuestion"
        @clear-related-questions="clearChatWrongQuestions"
      />
    </div>

    <el-dialog v-model="practiceDialogVisible" title="选择刷题范围" width="460px">
      <div class="space-y-4">
        <div class="rounded-lg bg-green-50 border border-green-100 px-3 py-2 text-sm text-green-700">
          请选择一个课程下的某次考试，系统会自动整理该考试中的错题进入刷题模式。
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1.5">课程</label>
          <el-select v-model="practiceForm.courseId" placeholder="请选择课程" class="w-full" clearable>
            <el-option
              v-for="course in practiceCourseOptions"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1.5">考试</label>
          <el-select
            v-model="practiceForm.examId"
            placeholder="请选择考试"
            class="w-full"
            clearable
            :disabled="!practiceForm.courseId"
            :empty-values="[null, undefined]"
            value-on-clear=""
          >
            <el-option v-if="practiceExamOptions.length === 0" label="该课程暂无考试错题" value="" disabled />
            <el-option
              v-for="exam in practiceExamOptions"
              :key="exam.id"
              :label="`${exam.name}（${exam.count}题）`"
              :value="exam.id"
            />
          </el-select>
        </div>
        <div class="rounded-lg bg-gray-50 px-3 py-2 text-sm text-gray-500">
          当前可练习：<span class="font-semibold text-primary-600">{{ practiceQuestionCount }}</span> 道错题
        </div>
      </div>
      <template #footer>
        <el-button @click="practiceDialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="practiceQuestionCount === 0" @click="startPracticeFromDialog">
          开始刷题
        </el-button>
      </template>
    </el-dialog>

    <!-- 分享弹窗 -->
    <teleport to="body">
      <transition name="fade">
        <div v-if="showShareModal" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="showShareModal = false">
          <div class="bg-white rounded-2xl w-full max-w-[420px] shadow-xl overflow-hidden" @click.stop>
            <div class="p-6">
              <h3 class="text-lg font-bold text-gray-800 mb-4">分享题库</h3>
              
              <div class="space-y-4">
                <div>
                  <label class="block text-sm text-gray-500 mb-1.5">分享码</label>
                  <div class="flex gap-2">
                    <input
                      :value="shareCode"
                      readonly
                      class="flex-1 px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg text-sm font-mono tracking-widest"
                    />
                    <button @click="copyShareCode" class="px-4 py-2.5 bg-primary-500 text-white text-sm rounded-lg hover:bg-primary-600 transition-colors cursor-pointer">
                      复制
                    </button>
                  </div>
                </div>
                
                <div>
                  <label class="block text-sm text-gray-500 mb-1.5">邀请链接</label>
                  <div class="flex gap-2">
                    <input
                      :value="shareLink"
                      readonly
                      class="flex-1 px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg text-sm"
                    />
                    <button @click="copyShareLink" class="px-4 py-2.5 bg-primary-500 text-white text-sm rounded-lg hover:bg-primary-600 transition-colors cursor-pointer">
                      复制
                    </button>
                  </div>
                </div>

                <div class="p-3 bg-blue-50 rounded-lg text-xs text-blue-600">
                  <i class="ri-information-line mr-1"></i>
                  分享码有效期为 7 天，最多可被使用 20 次
                </div>
              </div>

              <button
                @click="showShareModal = false"
                class="w-full mt-4 px-4 py-2.5 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors cursor-pointer"
              >
                关闭
              </button>
            </div>
          </div>
        </div>
      </transition>
    </teleport>

    <!-- 错题详情 + 重练弹窗 -->
    <teleport to="body">
      <transition name="fade">
        <div v-if="showDetailModal" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="showDetailModal = false">
          <div class="bg-white rounded-2xl w-full max-w-[560px] shadow-xl overflow-hidden max-h-[80vh] flex flex-col" @click.stop>
            <div class="p-6 border-b border-gray-100">
              <h3 class="text-lg font-bold text-gray-800 mb-1">错题详情</h3>
              <p v-if="currentDetail" class="text-xs text-gray-400">
                {{ currentDetail.examName || currentDetail.courseName }} · 错{{ currentDetail.wrongCount || currentDetail.wrongTimes || 1 }}次
              </p>
            </div>

            <div class="p-6 overflow-y-auto flex-1 space-y-4" v-if="currentDetail && !detailLoading">
              <!-- 题目内容 -->
              <div class="bg-gray-50 rounded-lg p-4">
                <p class="text-sm text-gray-800 leading-relaxed whitespace-pre-wrap">{{ currentDetail.content }}</p>
              </div>

              <!-- 答案对比 -->
              <div class="grid grid-cols-2 gap-3">
                <div class="bg-danger-50/50 rounded-lg p-3">
                  <div class="text-xs text-gray-500 mb-1">我的答案</div>
                  <div class="text-sm font-medium text-danger-600">{{ currentDetail.myAnswer || '(未作答)' }}</div>
                </div>
                <div class="bg-success-50/50 rounded-lg p-3">
                  <div class="text-xs text-gray-500 mb-1">正确答案</div>
                  <div class="text-sm font-medium text-success-600">{{ currentDetail.correctAnswer }}</div>
                </div>
              </div>

              <!-- 解析 -->
              <div v-if="currentDetail.analysis" class="bg-blue-50/50 rounded-lg p-4">
                <div class="text-xs font-medium text-blue-700 mb-1">解析</div>
                <p class="text-sm text-gray-700 leading-relaxed">{{ currentDetail.analysis }}</p>
              </div>

              <!-- 解题思路备注 -->
              <div v-if="currentDetail.mySolution !== undefined" class="rounded-lg p-4 border border-gray-100">
                <div class="text-xs font-medium text-gray-500 mb-1">我的解题思路</div>
                <p class="text-sm text-gray-700">{{ currentDetail.mySolution || '暂无' }}</p>
              </div>

              <!-- 重练答题区 -->
              <div class="border-t border-gray-100 pt-4">
                <label class="block text-sm font-medium text-gray-700 mb-2">重新作答</label>
                <textarea
                  v-model="practiceAnswer"
                  placeholder="请输入你的答案..."
                  rows="3"
                  class="w-full px-3 py-2.5 border border-gray-200 rounded-lg text-sm focus:border-primary-400 focus:outline-none resize-none"
                ></textarea>
                <button
                  @click="handlePractice(currentDetail!)"
                  :disabled="detailLoading"
                  class="mt-3 w-full px-4 py-2.5 bg-danger-500 text-white text-sm font-medium rounded-lg hover:bg-danger-400 transition-colors disabled:opacity-50 cursor-pointer"
                >
                  提交重练
                </button>
              </div>
            </div>

            <!-- 加载状态 -->
            <div v-else class="p-10 text-center text-gray-400">
              <i class="ri-loader-4-line animate-spin text-2xl block mx-auto mb-2"></i>
              加载中...
            </div>

            <div class="p-4 border-t border-gray-100">
              <button
                @click="showDetailModal = false"
                class="w-full px-4 py-2.5 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors cursor-pointer"
              >
                关闭
              </button>
            </div>
          </div>
        </div>
      </transition>
    </teleport>

    <!-- 新建题库弹窗 -->
    <teleport to="body">
      <transition name="fade">
        <div v-if="showCreateBankModal" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="showCreateBankModal = false">
          <div class="bg-white rounded-2xl w-full max-w-[420px] shadow-xl overflow-hidden" @click.stop>
            <div class="p-6">
              <h3 class="text-lg font-bold text-gray-800 mb-4">新建题库</h3>

              <div class="space-y-4">
                <div>
                  <label class="block text-sm text-gray-500 mb-1.5">题库名称 <span class="text-danger-500">*</span></label>
                  <input
                    v-model="newBankForm.name"
                    type="text"
                    placeholder="请输入题库名称"
                    maxlength="30"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:border-primary-400 focus:outline-none"
                  />
                </div>
                <div>
                  <label class="block text-sm text-gray-500 mb-1.5">描述（可选）</label>
                  <textarea
                    v-model="newBankForm.description"
                    placeholder="简单描述一下这个题库的用途..."
                    rows="3"
                    maxlength="200"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-lg text-sm focus:border-primary-400 focus:outline-none resize-none"
                  ></textarea>
                </div>
              </div>

              <div class="flex gap-3 mt-5">
                <button
                  @click="showCreateBankModal = false; newBankForm = { name: '', description: '' }"
                  class="flex-1 px-4 py-2.5 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors cursor-pointer"
                >
                  取消
                </button>
                <button
                  @click="handleCreateBank"
                  :disabled="creatingBank || !newBankForm.name.trim()"
                  class="flex-1 px-4 py-2.5 bg-primary-500 text-white text-sm font-medium rounded-lg hover:bg-primary-600 transition-colors disabled:opacity-50 cursor-pointer"
                >
                  {{ creatingBank ? '创建中...' : '确认创建' }}
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
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import type { WrongQuestion, StudentQuestionBank } from '@/api/types'
import { getWrongQuestions, getWrongQuestionDetail, practiceWrongQuestion, getQuestionBanks, shareQuestionBank, createQuestionBank, toggleFavoriteWrongQuestion, getCourses, deleteWrongQuestion } from '@/api/student'
import AiWrongQuestionPanel from './AiWrongQuestionPanel.vue'

// 注册 dayjs 插件
dayjs.extend(relativeTime)
const router = useRouter()

// 筛选状态
const filters = ref({
  sourceType: '',
  courseId: '',
  knowledgePoint: '',
})

const sourceTypes = [
  { label: '全部', value: '' },
  { label: '考试错题', value: 'exam' },
  { label: '自建题库', value: 'practice' },
]

// 弹窗状态
const showCreateBankModal = ref(false)
const showShareModal = ref(false)
const showDetailModal = ref(false)
const practiceDialogVisible = ref(false)
const shareCode = ref('')
const shareLink = ref('')
const loading = ref(false)
const practiceForm = ref({
  courseId: '' as number | string,
  examId: '' as number | string,
})

// 错题详情
const currentDetail = ref<WrongQuestion | null>(null)
const detailLoading = ref(false)
const practiceAnswer = ref('')

// 新建题库表单
const newBankForm = ref({
  name: '',
  description: '',
})
const creatingBank = ref(false)

// 课程下拉选项
const courseOptions = ref<any[]>([])

// API 数据
const totalCount = ref(0)
const highFreqCount = ref(0)
const courseCount = ref(0)
const examCount = ref(0)
const myBanks = ref<StudentQuestionBank[]>([])
const wrongQuestions = ref<WrongQuestion[]>([])
const selectedWrongQuestion = ref<WrongQuestion | null>(null)
const selectedWrongIds = ref<number[]>([])
const chatWrongQuestions = ref<WrongQuestion[]>([])
const aiPanelRef = ref<InstanceType<typeof AiWrongQuestionPanel> | null>(null)
const aiSelectableCount = computed(() => Math.min(selectedWrongIds.value.length, 3))

onMounted(async () => {
  loading.value = true
  try {
    const [wqRes, bankRes, coursesRes] = await Promise.all([
      getWrongQuestions(),
      getQuestionBanks(),
      getCourses(),
    ]) as [any, any, any]

    // 处理错题
    const wqData = Array.isArray(wqRes) ? wqRes : (wqRes?.data || [])
    wrongQuestions.value = wqData.map((w: any) => ({
      ...w,
      wrongQuestionId: w.id ?? w.wrongQuestionId,
      myAnswer: w.myAnswer ?? w.studentAnswer,
      knowledgePoint: w.knowledgePoint ?? w.knowledgePoints,
      sourceTypeName: w.sourceType === 'exam' ? '考试错题' : '自建题库',
    }))
    selectedWrongQuestion.value = wrongQuestions.value[0] || null
    if (selectedWrongQuestion.value) {
      await selectWrongQuestion(selectedWrongQuestion.value)
    }

    // 统计
    totalCount.value = wrongQuestions.value.length
    refreshStats()

    // 处理题库
    myBanks.value = Array.isArray(bankRes) ? bankRes : (bankRes?.data || [])

    // 加载课程下拉
    courseOptions.value = Array.isArray(coursesRes) ? coursesRes : (coursesRes?.data || [])
  } catch (err) {
    console.error('加载错题本失败', err)
  } finally {
    loading.value = false
  }
})

// 计算属性
const filteredWrongQuestions = computed(() => {
  let result = wrongQuestions.value
  if (filters.value.sourceType) {
    result = result.filter(w => w.sourceType === filters.value.sourceType)
  }
  if (filters.value.courseId !== '' && filters.value.courseId !== null && filters.value.courseId !== undefined) {
    result = result.filter(w => String(w.courseId) === String(filters.value.courseId))
  }
  if (filters.value.knowledgePoint) {
    result = result.filter(w => getWrongKnowledge(w).includes(filters.value.knowledgePoint))
  }
  return result
})

const knowledgeOptions = computed(() => {
  const points = wrongQuestions.value
    .flatMap(getWrongKnowledge)
    .map((item: string) => item.trim())
    .filter(Boolean)
  return [...new Set(points)]
})

const practiceCourseOptions = computed(() => {
  const courseMap = new Map<string, { id: number | string; name: string }>()
  wrongQuestions.value.forEach((wrong) => {
    if (wrong.courseId) {
      courseMap.set(String(wrong.courseId), {
        id: wrong.courseId,
        name: wrong.courseName || `课程 ${wrong.courseId}`,
      })
    }
  })
  return Array.from(courseMap.values())
})

const practiceExamOptions = computed(() => {
  if (!practiceForm.value.courseId) return []
  const examMap = new Map<string, { id: number | string; name: string; count: number }>()
  wrongQuestions.value
    .filter(wrong => String(wrong.courseId) === String(practiceForm.value.courseId) && wrong.examId)
    .forEach((wrong) => {
      const key = String(wrong.examId)
      const current = examMap.get(key)
      if (current) {
        current.count += 1
      } else {
        examMap.set(key, {
          id: wrong.examId as number | string,
          name: wrong.examName || `考试 ${wrong.examId}`,
          count: 1,
        })
      }
    })
  return Array.from(examMap.values())
})

const practiceQuestionCount = computed(() => getPracticeWrongQuestions().length)

watch(filteredWrongQuestions, (rows) => {
  const visibleIds = new Set(rows.map(item => item.wrongQuestionId))
  selectedWrongIds.value = selectedWrongIds.value.filter(id => visibleIds.has(id))
  chatWrongQuestions.value = chatWrongQuestions.value.filter(item => visibleIds.has(item.wrongQuestionId))
  if (selectedWrongQuestion.value && !visibleIds.has(selectedWrongQuestion.value.wrongQuestionId)) {
    selectedWrongQuestion.value = rows[0] || null
    if (selectedWrongQuestion.value) {
      selectWrongQuestion(selectedWrongQuestion.value)
    }
  }
})

watch(() => practiceForm.value.courseId, () => {
  practiceForm.value.examId = ''
})

async function selectWrongQuestion(wrong: WrongQuestion) {
  selectedWrongQuestion.value = wrong
  try {
    const detail = await getWrongQuestionDetail(wrong.wrongQuestionId)
    selectedWrongQuestion.value = { ...wrong, ...(detail as any), myAnswer: (detail as any).myAnswer ?? (detail as any).studentAnswer ?? wrong.myAnswer }
  } catch (err) {
    console.error('获取AI错题上下文失败', err)
  }
}

function toggleWrongSelection(wrong: WrongQuestion, checked: boolean) {
  if (checked) {
    if (selectedWrongIds.value.includes(wrong.wrongQuestionId)) return
    if (selectedWrongIds.value.length >= 3) {
      ElMessage.warning('最多选择 3 道错题加入对话')
      return
    }
    selectedWrongIds.value.push(wrong.wrongQuestionId)
  } else {
    selectedWrongIds.value = selectedWrongIds.value.filter(id => id !== wrong.wrongQuestionId)
  }
}

async function addSelectedWrongQuestionsToChat() {
  if (selectedWrongIds.value.length === 0) return
  const aiIds = selectedWrongIds.value.slice(0, 3)
  try {
    const details = await Promise.all(aiIds.map(async id => {
      const base = wrongQuestions.value.find(item => item.wrongQuestionId === id)
      const detail = await getWrongQuestionDetail(id).catch(() => null)
      return {
        ...(base || {}),
        ...((detail || {}) as any),
        wrongQuestionId: id,
        myAnswer: (detail as any)?.myAnswer ?? (detail as any)?.studentAnswer ?? base?.myAnswer,
      } as WrongQuestion
    }))
    chatWrongQuestions.value = details
    ElMessage.success(`已将 ${details.length} 道错题加入 AI 对话`)
    await nextTick()
    aiPanelRef.value?.focusComposer()
  } catch (err) {
    console.error('添加错题到AI对话失败', err)
    ElMessage.error('添加失败，请稍后重试')
  }
}

function openPracticeDialog() {
  const firstVisible = filteredWrongQuestions.value.find(item => item.courseId && item.examId)
  practiceForm.value.courseId = firstVisible?.courseId || practiceCourseOptions.value[0]?.id || ''
  practiceForm.value.examId = ''
  practiceDialogVisible.value = true
}

function getPracticeWrongQuestions() {
  if (!practiceForm.value.courseId || !practiceForm.value.examId) return []
  return wrongQuestions.value.filter(item =>
    String(item.courseId) === String(practiceForm.value.courseId) &&
    String(item.examId) === String(practiceForm.value.examId)
  )
}

function startPracticeFromDialog() {
  const selected = getPracticeWrongQuestions()
  if (selected.length === 0) {
    ElMessage.warning('该考试暂无可练习错题')
    return
  }
  practiceDialogVisible.value = false
  router.push({
    path: '/wrong-questions/practice',
    query: {
      ids: selected.map(item => item.wrongQuestionId).join(','),
      examId: String(practiceForm.value.examId),
    },
  })
}

async function deleteSelectedWrongQuestions() {
  if (selectedWrongIds.value.length === 0) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedWrongIds.value.length} 道错题吗？删除后不会影响原考试记录。`, '删除错题', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
    const deletedIds = [...selectedWrongIds.value]
    await Promise.all(deletedIds.map(id => deleteWrongQuestion(id)))
    wrongQuestions.value = wrongQuestions.value.filter(item => !deletedIds.includes(item.wrongQuestionId))
    chatWrongQuestions.value = chatWrongQuestions.value.filter(item => !deletedIds.includes(item.wrongQuestionId))
    selectedWrongIds.value = []
    selectedWrongQuestion.value = filteredWrongQuestions.value[0] || wrongQuestions.value[0] || null
    refreshStats()
    ElMessage.success('已删除选中错题')
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error(error?.message || '删除失败')
  }
}

function removeChatWrongQuestion(id: number) {
  chatWrongQuestions.value = chatWrongQuestions.value.filter(item => item.wrongQuestionId !== id)
  selectedWrongIds.value = selectedWrongIds.value.filter(item => item !== id)
}

function clearChatWrongQuestions() {
  chatWrongQuestions.value = []
  selectedWrongIds.value = []
}

function getWrongKnowledge(wrong: WrongQuestion): string[] {
  const raw = ((wrong as any).knowledgePoint ?? (wrong as any).knowledgePoints ?? '').toString()
  return raw
    .split(/[,，;；、\n]/)
    .map((item: string) => item.trim())
    .filter(Boolean)
}

function refreshStats() {
  totalCount.value = wrongQuestions.value.length
  highFreqCount.value = wrongQuestions.value.filter(w => (w.wrongTimes ?? w.wrongCount ?? 0) >= 2).length
  courseCount.value = new Set(wrongQuestions.value.map(w => w.courseId).filter(Boolean)).size
  examCount.value = new Set(wrongQuestions.value.map(w => w.examId).filter(Boolean)).size
}

// 方法
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

function formatDate(date: string): string {
  return dayjs(date).fromNow()
}

function getSourceBadgeClass(sourceType: string): string {
  return sourceType === 'exam' ? 'bg-orange-50 text-orange-600' : 'bg-purple-50 text-purple-600'
}

async function showWrongDetail(wrong: WrongQuestion) {
  detailLoading.value = true
  showDetailModal.value = true
  currentDetail.value = wrong
  practiceAnswer.value = ''
  try {
    const detail = await getWrongQuestionDetail(wrong.wrongQuestionId)
    currentDetail.value = { ...wrong, ...(detail as any) }
  } catch (err) {
    console.error('获取错题详情失败', err)
    ElMessage.error('获取错题详情失败')
  } finally {
    detailLoading.value = false
  }
}

async function handlePractice(wrong: WrongQuestion) {
  // 如果还没有打开详情弹窗，先打开
  if (!showDetailModal.value || currentDetail.value?.wrongQuestionId !== wrong.wrongQuestionId) {
    await showWrongDetail(wrong)
    return
  }
  if (!practiceAnswer.value.trim()) {
    ElMessage.warning('请输入答案后再提交')
    return
  }
  try {
    const res = await practiceWrongQuestion(wrong.wrongQuestionId, practiceAnswer.value.trim())
    const result = res as any
    if (result.isCorrect !== undefined ? result.isCorrect : true) {
      ElMessage.success('回答正确！')
    } else {
      ElMessage.warning(`回答错误，正确答案是：${result.correctAnswer ?? '请查看详情'}`)
    }
    // 刷新错题列表
    const wqRes: any = await getWrongQuestions()
    const wqData = Array.isArray(wqRes) ? wqRes : (wqRes?.data || [])
    wrongQuestions.value = wqData.map((w: any) => ({
      ...w,
      wrongQuestionId: w.id ?? w.wrongQuestionId,
      myAnswer: w.myAnswer ?? w.studentAnswer,
      knowledgePoint: w.knowledgePoint ?? w.knowledgePoints,
      sourceTypeName: w.sourceType === 'exam' ? '考试错题' : '自建题库',
    }))
    selectedWrongQuestion.value = wrongQuestions.value.find(w => w.wrongQuestionId === selectedWrongQuestion.value?.wrongQuestionId) || wrongQuestions.value[0] || null
    totalCount.value = wrongQuestions.value.length
    showDetailModal.value = false
  } catch (err: any) {
    ElMessage.error(err?.message || '练习提交失败')
  }
}

async function toggleFavorite(wrong: WrongQuestion) {
  const newVal = !wrong.isFavorited
  wrong.isFavorited = newVal
  try {
    await toggleFavoriteWrongQuestion(wrong.wrongQuestionId, newVal)
  } catch (err) {
    // 回滚
    wrong.isFavorited = !newVal
    console.error('收藏操作失败', err)
  }
}

async function handleShare(bank: StudentQuestionBank) {
  // 如果已有分享码，直接展示
  if (bank.shareCode) {
    shareCode.value = bank.shareCode
    shareLink.value = `https://exam.example.com/join?code=${bank.shareCode}`
    showShareModal.value = true
    return
  }
  // 调用后端 API 生成分享码
  try {
    ElMessage.info('正在生成分享码...')
    const res = await shareQuestionBank(bank.id)
    const data = res as any
    shareCode.value = data.shareCode || data.code || ''
    shareLink.value = `https://exam.example.com/join?code=${shareCode.value}`
    // 更新本地数据
    const idx = myBanks.value.findIndex(b => b.id === bank.id)
    if (idx >= 0 && shareCode.value) {
      myBanks.value[idx] = { ...myBanks.value[idx], shareCode: shareCode.value }
    }
    showShareModal.value = true
    ElMessage.success('分享码生成成功')
  } catch (err: any) {
    ElMessage.error(err?.message || '分享失败，请稍后重试')
  }
}

async function handleCreateBank() {
  if (!newBankForm.value.name.trim()) {
    ElMessage.warning('请输入题库名称')
    return
  }
  creatingBank.value = true
  try {
    const res = await createQuestionBank({
      name: newBankForm.value.name.trim(),
      description: newBankForm.value.description.trim() || undefined,
    })
    const newBank = res as any
    myBanks.value.unshift({
      id: newBank.id,
      bankName: newBank.name,
      description: newBank.description,
      questionCount: 0,
      createdAt: new Date().toISOString(),
      shareCode: '',
      isShared: false,
      source: 'original',
    })
    showCreateBankModal.value = false
    newBankForm.value = { name: '', description: '' }
    ElMessage.success('题库创建成功')
  } catch (err: any) {
    ElMessage.error(err?.message || '创建失败')
  } finally {
    creatingBank.value = false
  }
}

function copyShareCode() {
  navigator.clipboard.writeText(shareCode.value)
}

function copyShareLink() {
  navigator.clipboard.writeText(shareLink.value)
}
</script>

