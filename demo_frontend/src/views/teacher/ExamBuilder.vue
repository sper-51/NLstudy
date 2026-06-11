<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">组卷中心</h1>
        <p class="text-sm text-gray-500 mt-1">智能组卷 / 手动选题，快速构建高质量试卷</p>
      </div>
      <div class="flex items-center gap-2">
        <el-button @click="handleSaveDraft">
          <i class="ri-save-line mr-1"></i>保存草稿
        </el-button>
        <el-button type="primary" @click="handlePreviewExam">
          <i class="ri-eye-line mr-1"></i>预览试卷
        </el-button>
        <el-button type="success" @click="handlePublishExam">
          <i class="ri-send-plane-line mr-1"></i>发布为考试
        </el-button>
      </div>
    </div>

    <!-- 模式切换 Tab -->
    <div class="bg-white rounded-xl p-2 shadow-sm inline-flex">
      <button
        v-for="tab in modeTabs"
        :key="tab.value"
        @click="currentMode = tab.value"
        :class="[
          'px-5 py-2 rounded-lg text-sm font-medium transition-all cursor-pointer',
          currentMode === tab.value
            ? 'bg-primary-500 text-white shadow-sm'
            : 'text-gray-600 hover:text-primary-500 hover:bg-primary-50'
        ]"
      >
        <i :class="tab.icon + ' mr-1'"></i>{{ tab.label }}
      </button>
    </div>

    <!-- 主内容区：左右分栏 -->
    <div class="flex gap-5 items-stretch min-h-[720px]">
      <!-- 左侧：题库筛选 + 题目列表 -->
      <div class="w-[480px] flex-shrink-0 flex flex-col gap-4 min-h-0">
        <div v-if="currentMode === 'ai'" class="bg-white rounded-xl p-4 shadow-sm space-y-3">
          <h3 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
            <i class="ri-sparkling-2-line text-primary-500"></i>AI 出题配置
          </h3>
          <div class="space-y-3">
            <div>
              <label class="text-xs text-gray-500 mb-1 block">课程</label>
              <el-input v-model="aiConfig.courseName" placeholder="请输入课程名称，例如：计算机网络原理" size="small" />
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">章节 / 知识范围</label>
              <el-input v-model="aiConfig.chapter" placeholder="请输入章节或知识范围，例如：第三章 数据链路层、交换机、MAC地址" size="small" />
              <p class="text-[11px] text-gray-400 mt-1">AI 只生成草稿，保存到题库或加入试卷前仍需在右侧选择真实关联课程。</p>
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">题型</label>
              <el-select v-model="aiConfig.questionTypes" multiple collapse-tags collapse-tags-tooltip placeholder="选择题型" style="width: 100%" size="small">
                <el-option label="单选题" value="single_choice" />
                <el-option label="多选题" value="multi_choice" />
                <el-option label="判断题" value="true_false" />
                <el-option label="填空题" value="fill_blank" />
                <el-option label="简答题" value="essay" />
                <el-option label="编程题" value="code" />
              </el-select>
            </div>
            <div class="grid grid-cols-3 gap-2">
              <div>
                <label class="text-xs text-gray-500 mb-1 block">难度</label>
                <el-select v-model="aiConfig.difficulty" size="small" style="width: 100%">
                  <el-option label="简单" value="easy" />
                  <el-option label="中等" value="medium" />
                  <el-option label="困难" value="hard" />
                </el-select>
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">数量</label>
                <el-input-number v-model="aiConfig.count" :min="1" :max="20" size="small" controls-position="right" style="width: 100%" />
              </div>
              <div>
                <label class="text-xs text-gray-500 mb-1 block">分值</label>
                <el-input-number v-model="aiConfig.score" :min="1" :max="50" size="small" controls-position="right" style="width: 100%" />
              </div>
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">额外要求</label>
              <el-input v-model="aiConfig.requirements" type="textarea" :rows="3" placeholder="例如：贴近期中考试，避免偏题，解析要适合学生复盘" />
            </div>
            <el-button type="primary" :loading="aiGenerating" style="width: 100%" @click="handleGenerateAiQuestions">
              <i class="ri-magic-line mr-1"></i>生成题目草稿
            </el-button>
          </div>
        </div>

        <!-- 筛选区 -->
        <div v-else class="bg-white rounded-xl p-4 shadow-sm space-y-3">
          <h3 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
            <i class="ri-filter-3-line text-primary-500"></i>题库筛选
          </h3>
          <div class="space-y-3">
            <div>
              <label class="text-xs text-gray-500 mb-1 block">题型</label>
              <el-select v-model="filters.type" placeholder="全部题型" style="width: 100%" size="small" clearable>
                <el-option label="单选题" value="single" />
                <el-option label="多选题" value="multiple" />
                <el-option label="判断题" value="judge" />
                <el-option label="填空题" value="fill" />
                <el-option label="简答题" value="essay" />
                <el-option label="编程题" value="code" />
              </el-select>
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">难度</label>
              <el-select v-model="filters.difficulty" placeholder="全部难度" style="width: 100%" size="small" clearable>
                <el-option label="简单" value="easy" />
                <el-option label="中等" value="medium" />
                <el-option label="困难" value="hard" />
              </el-select>
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">课程</label>
              <el-select v-model="filters.course" placeholder="选择课程" style="width: 100%" size="small" clearable>
                <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
            </div>
            <div>
              <label class="text-xs text-gray-500 mb-1 block">知识点</label>
              <el-select v-model="filters.knowledge" placeholder="选择知识点" style="width: 100%" size="small" clearable>
                <el-option v-for="k in knowledgeOptions" :key="k.value" :label="k.label" :value="k.value" />
              </el-select>
            </div>
            <div class="flex gap-2 pt-1">
              <el-input v-model="searchKeyword" placeholder="搜索题目内容..." prefix-icon="Search" size="small" clearable />
              <el-button size="small" @click="resetFilters"><i class="ri-refresh-line"></i></el-button>
            </div>
          </div>
        </div>

        <!-- 题目列表 -->
        <div v-if="currentMode === 'ai'" class="bg-white rounded-xl shadow-sm flex-1 flex flex-col min-h-0 overflow-hidden">
          <div class="p-4 border-b border-gray-100 flex items-center justify-between">
            <span class="text-sm font-semibold text-gray-700">
              <i class="ri-draft-line text-primary-500 mr-1"></i>AI 草稿
              <span class="text-xs text-gray-400 font-normal ml-1">（{{ aiDrafts.length }} 题）</span>
            </span>
            <el-checkbox v-model="aiSelectAll" @change="handleAiSelectAll">全选</el-checkbox>
          </div>
          <div class="flex-1 min-h-[360px] overflow-auto p-3 space-y-3">
            <div v-for="(draft, index) in aiDrafts" :key="draft.draftId || index" class="border border-gray-100 rounded-lg p-3 space-y-2">
              <div class="flex items-center gap-2">
                <el-checkbox v-model="draft.selected" />
                <el-tag size="small" :type="getTypeTagType(draft.type)" effect="plain">{{ getTypeName(draft.type) }}</el-tag>
                <el-tag size="small" :type="getDifficultyTagType(draft.difficulty)" effect="plain">{{ getDifficultyName(draft.difficulty) }}</el-tag>
                <span class="text-xs text-gray-400">{{ draft.score }} 分</span>
                <button class="ml-auto text-gray-300 hover:text-danger-500" @click="removeAiDraft(index)">
                  <i class="ri-close-circle-line"></i>
                </button>
              </div>
              <el-input v-model="draft.content" type="textarea" :rows="3" placeholder="题干" />
              <div v-if="isChoiceType(draft.type)" class="space-y-1">
                <div v-for="option in draft.options" :key="option.label" class="flex items-center gap-2">
                  <el-checkbox v-model="option.isCorrect" />
                  <span class="text-xs text-gray-500 w-4">{{ option.label }}</span>
                  <el-input v-model="option.content" size="small" />
                </div>
              </div>
              <div class="grid grid-cols-2 gap-2">
                <el-input v-model="draft.answer" placeholder="答案" size="small" />
                <el-input v-model="draft.knowledgePoints" placeholder="知识点" size="small" />
              </div>
              <el-input v-model="draft.analysis" type="textarea" :rows="2" placeholder="解析" />
            </div>
            <div v-if="aiDrafts.length === 0" class="py-12 text-center text-gray-400">
              <i class="ri-sparkling-line text-3xl mb-2 block"></i>
              <p class="text-sm">配置左侧条件后生成题目草稿</p>
            </div>
          </div>
          <div class="p-3 border-t border-gray-100 flex gap-2">
            <el-button :disabled="selectedAiDrafts.length === 0" @click="handleSaveAiDrafts">保存到题库</el-button>
            <el-button type="primary" :disabled="selectedAiDrafts.length === 0" @click="handleAddAiDraftsToPaper">加入当前试卷</el-button>
          </div>
        </div>

        <div v-else class="bg-white rounded-xl shadow-sm flex-1 flex flex-col min-h-0 overflow-hidden">
          <div class="p-4 border-b border-gray-100 flex items-center justify-between">
            <span class="text-sm font-semibold text-gray-700">
              <i class="ri-list-check-2 text-primary-500 mr-1"></i>题目列表
              <span class="text-xs text-gray-400 font-normal ml-1">（共 {{ filteredQuestions.length }} 题）</span>
            </span>
            <el-checkbox v-model="selectAll" @change="handleSelectAll">全选当前页</el-checkbox>
          </div>
          <div class="flex-1 min-h-[360px] overflow-auto p-3 space-y-2">
            <div
              v-for="q in pagedQuestions"
              :key="q.id"
              :class="[
                'border rounded-lg p-3 transition-all cursor-pointer',
                selectedQuestionIds.includes(q.id)
                  ? 'border-primary-300 bg-primary-50/50'
                  : 'border-gray-100 hover:border-primary-200 hover:shadow-sm'
              ]"
              @click="toggleQuestion(q.id)"
            >
              <div class="flex items-start gap-3">
                <el-checkbox
                  :model-value="selectedQuestionIds.includes(q.id)"
                  @change="toggleQuestion(q.id)"
                  @click.stop
                  class="mt-0.5"
                />
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-1.5">
                    <el-tag size="small" :type="getTypeTagType(q.type)" effect="plain">{{ q.typeName }}</el-tag>
                    <el-tag size="small" :type="getDifficultyTagType(q.difficulty)" effect="plain">{{ q.difficultyName }}</el-tag>
                    <span class="text-xs text-gray-400">{{ q.score }} 分</span>
                  </div>
                  <p class="text-sm text-gray-700 leading-relaxed line-clamp-2">{{ q.content }}</p>
                  <div class="flex items-center gap-3 mt-2 text-xs text-gray-400">
                    <span><i class="ri-book-line mr-0.5"></i>{{ q.courseName }}</span>
                    <span><i class="ri-price-tag-3-line mr-0.5"></i>{{ q.knowledge }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="pagedQuestions.length === 0" class="py-10 text-center text-gray-400">
              <i class="ri-search-line text-3xl mb-2 block"></i>
              <p class="text-sm">没有找到匹配的题目</p>
            </div>
          </div>
          <div class="p-3 border-t border-gray-100">
            <el-pagination size="small" layout="prev, pager, next" :total="filteredQuestions.length" :page-size="pageSize" :current-page="currentPage" @current-change="currentPage = $event" />
          </div>
        </div>
      </div>

      <!-- 右侧：试卷预览区 -->
      <div class="flex-1 bg-white rounded-xl shadow-sm flex flex-col min-h-0 overflow-hidden">
        <div class="p-4 border-b border-gray-100">
          <h3 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
            <i class="ri-file-text-line text-primary-500"></i>试卷预览
          </h3>
        </div>

        <!-- 试卷基本信息 -->
        <div class="p-4 border-b border-gray-100 space-y-3">
          <el-input v-model="paperInfo.name" placeholder="请输入试卷名称" size="large" class="font-semibold">
            <template #prefix><i class="ri-edit-box-line text-gray-400"></i></template>
          </el-input>
          <div>
            <el-select v-model="paperInfo.courseId" placeholder="选择关联课程（必选）" size="default" style="width: 100%">
              <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
            </el-select>
          </div>
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-2">
              <span class="text-xs text-gray-500">总分：</span>
              <el-input-number v-model="paperInfo.totalScore" :min="10" :max="200" :step="10" size="small" controls-position="right" />
              <span class="text-xs text-gray-400">分</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs text-gray-500">时长：</span>
              <el-input-number v-model="paperInfo.duration" :min="30" :max="180" :step="15" size="small" controls-position="right" />
              <span class="text-xs text-gray-400">分钟</span>
            </div>
          </div>
        </div>

        <!-- 已选题目列表 -->
        <div class="flex-1 min-h-[420px] overflow-auto p-4">
          <div v-if="selectedQuestions.length === 0" class="flex flex-col items-center justify-center h-full text-gray-400">
            <i class="ri-drag-drop-line text-5xl mb-3 opacity-50"></i>
            <p class="text-sm">从左侧勾选题目添加到试卷</p>
            <p class="text-xs mt-1">支持拖拽排序和设置每题分值</p>
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="(q, index) in selectedQuestions"
              :key="q.id"
              class="border border-gray-100 rounded-lg p-3 hover:border-primary-200 transition-all group"
              draggable="true"
              @dragstart="handleDragStart($event, index)"
              @dragover.prevent
              @drop="handleDrop($event, index)"
            >
              <div class="flex items-start gap-3">
                <div class="mt-1 text-gray-300 cursor-move group-hover:text-primary-400">
                  <i class="ri-draggable"></i>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-1">
                    <span class="w-6 h-6 rounded-full bg-primary-100 text-primary-600 text-xs flex items-center justify-center font-medium">{{ index + 1 }}</span>
                    <el-tag size="small" :type="getTypeTagType(q.type)" effect="plain">{{ q.typeName }}</el-tag>
                    <span class="text-xs text-gray-400 ml-auto">ID: {{ q.id }}</span>
                  </div>
                  <p class="text-sm text-gray-700 leading-relaxed">{{ q.content }}</p>
                </div>
                <div class="flex items-center gap-2 ml-3">
                  <el-input-number
                    v-model="q.assignedScore"
                    :min="1" :max="50" size="small"
                    controls-position="right"
                    style="width: 100px"
                  />
                  <span class="text-xs text-gray-400">分</span>
                  <button
                    @click="removeQuestion(q.id)"
                    class="text-gray-300 hover:text-danger-500 transition-colors cursor-pointer p-1"
                  >
                    <i class="ri-close-circle-line"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分值统计 -->
        <div class="p-4 border-t border-gray-100 bg-gray-50/50">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-6">
              <div v-for="(stat, idx) in scoreStats" :key="idx" class="flex items-center gap-2">
                <span class="text-xs text-gray-500">{{ stat.label }}:</span>
                <span class="text-sm font-semibold text-primary-600">{{ stat.score }} 分</span>
                <span class="text-xs text-gray-400">({{ stat.count }} 题)</span>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs text-gray-500">合计:</span>
              <span class="text-lg font-bold" :class="totalAssignedScore === paperInfo.totalScore ? 'text-green-600' : totalAssignedScore > paperInfo.totalScore ? 'text-danger-500' : 'text-warning-500'">
                {{ totalAssignedScore }}
              </span>
              <span class="text-xs text-gray-400">/ {{ paperInfo.totalScore }} 分</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 智能组卷配置面板 -->
    <el-dialog v-model="autoComposeVisible" title="智能组卷配置" width="600px" destroy-on-close>
      <div class="space-y-4">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>系统将根据您设定的规则，自动从题库中抽取题目组成试卷</template>
        </el-alert>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-700 mb-1 block">试卷总分</label>
            <el-input-number v-model="autoConfig.totalScore" :min="50" :max="200" :step="10" style="width: 100%" />
          </div>
          <div>
            <label class="text-sm text-gray-700 mb-1 block">考试时长（分钟）</label>
            <el-input-number v-model="autoConfig.duration" :min="30" :max="180" :step="15" style="width: 100%" />
          </div>
        </div>
        <div>
          <label class="text-sm text-gray-700 mb-2 block">题型与数量配置</label>
          <div class="space-y-2">
            <div v-for="item in autoConfig.types" :key="item.type" class="flex items-center gap-3 bg-gray-50 rounded-lg p-3">
              <span class="text-sm text-gray-700 w-20">{{ item.label }}</span>
              <el-input-number v-model="item.count" :min="0" :max="50" size="small" controls-position="right" />
              <span class="text-xs text-gray-400">题 ×</span>
              <el-input-number v-model="item.score" :min="1" :max="20" size="small" controls-position="right" />
              <span class="text-xs text-gray-400">分 = {{ item.count * item.score }} 分</span>
            </div>
          </div>
        </div>
        <div>
          <label class="text-sm text-gray-700 mb-1 block">难度比例</label>
          <div class="flex items-center gap-4">
            <span class="text-xs text-gray-500">简单</span>
            <el-slider v-model="autoConfig.difficultyRatio" range :min="0" :max="100" :step="5" style="flex: 1" />
            <span class="text-xs text-gray-500">困难</span>
          </div>
          <div class="flex items-center gap-4 mt-1 text-xs text-gray-400 px-1">
            <span>{{ autoConfig.difficultyRatio[0] }}%</span>
            <span class="flex-1 text-center">中等 {{ autoConfig.difficultyRatio[1] - autoConfig.difficultyRatio[0] }}%</span>
            <span>{{ 100 - autoConfig.difficultyRatio[1] }}%</span>
          </div>
        </div>
        <div>
          <label class="text-sm text-gray-700 mb-1 block">知识点覆盖范围</label>
          <el-select v-model="autoConfig.knowledgePoints" multiple collapse-tags collapse-tags-tooltip placeholder="选择要覆盖的知识点" style="width: 100%">
            <el-option v-for="k in knowledgeOptions" :key="k.value" :label="k.label" :value="k.value" />
          </el-select>
        </div>
        <div>
          <label class="text-sm text-gray-700 mb-1 block">排除已用题目</label>
          <el-switch v-model="autoConfig.excludeUsed" active-text="是" inactive-text="否" />
        </div>
      </div>
      <template #footer>
        <el-button @click="autoComposeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAutoCompose" :loading="autoLoading">
          <i class="ri-magic-line mr-1"></i>开始智能组卷
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTeacherCourses, getQuestionsForPaper, getKnowledgePoints, createPaper, addPaperQuestions, updatePaper, generateAiQuestions, saveAiQuestionDrafts, addAiQuestionsToPaper, type AiQuestionDraft } from '@/api/teacher'

// ========== 模式切换 ==========
const modeTabs = [
  { label: '手动选题', value: 'manual', icon: 'ri-hand-coin-line' },
  { label: '智能组卷', value: 'auto', icon: 'ri-magic-line' },
  { label: 'AI出题', value: 'ai', icon: 'ri-sparkling-2-line' },
]
const currentMode = ref('manual')

watch(currentMode, (val) => {
  if (val === 'auto') {
    autoComposeVisible.value = true
  }
})

// ========== 筛选 ==========
const filters = ref({
  type: '',
  difficulty: '',
  course: '',
  knowledge: '',
})
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(8)
const selectAll = ref(false)
const loadingQuestions = ref(false)

interface EditableAiQuestionDraft extends AiQuestionDraft {
  selected?: boolean
}

const aiGenerating = ref(false)
const aiSelectAll = ref(true)
const aiDrafts = ref<EditableAiQuestionDraft[]>([])
const aiConfig = ref({
  courseId: '' as string | number,
  courseName: '',
  chapter: '',
  knowledgePoints: [] as string[],
  questionTypes: ['single_choice'] as string[],
  difficulty: 'medium',
  count: 5,
  score: 5,
  requirements: '',
})

watch(() => filters.value.course, (courseId) => {
  if (!aiConfig.value.courseId && courseId) {
    aiConfig.value.courseId = courseId
  }
})

const selectedAiDrafts = computed(() => aiDrafts.value.filter(item => item.selected))

function handleAiSelectAll(val: boolean) {
  aiDrafts.value.forEach(item => { item.selected = val })
}

function removeAiDraft(index: number) {
  aiDrafts.value.splice(index, 1)
}

function isChoiceType(type: string) {
  return type === 'single_choice' || type === 'multi_choice'
}

function getTypeName(type: string): string {
  const map: Record<string, string> = {
    single_choice: '单选题',
    multi_choice: '多选题',
    true_false: '判断题',
    fill_blank: '填空题',
    essay: '简答题',
    code: '编程题',
    single: '单选题',
    multiple: '多选题',
    judge: '判断题',
    fill: '填空题',
  }
  return map[type] || type
}

function getDifficultyName(diff: string): string {
  const map: Record<string, string> = {
    easy: '简单',
    medium: '中等',
    hard: '困难',
  }
  return map[diff] || diff
}

async function handleGenerateAiQuestions() {
  if (!aiConfig.value.courseName.trim()) {
    ElMessage.warning('请输入想要出题的课程')
    return
  }
  if (!aiConfig.value.chapter.trim()) {
    ElMessage.warning('请输入对应章节或知识范围')
    return
  }
  if (aiConfig.value.questionTypes.length === 0) {
    ElMessage.warning('请至少选择一种题型')
    return
  }
  aiGenerating.value = true
  try {
    const requestPayload = {
      ...aiConfig.value,
      courseId: undefined,
      courseName: aiConfig.value.courseName.trim(),
      chapter: aiConfig.value.chapter.trim(),
      knowledgePoints: [aiConfig.value.chapter.trim()],
    }
    const res = await generateAiQuestions(requestPayload)
    aiDrafts.value = (res?.questions || []).map((item: AiQuestionDraft, index: number) => ({
      ...item,
      draftId: item.draftId || `ai-${Date.now()}-${index}`,
      selected: true,
      options: item.options || [],
    }))
    aiSelectAll.value = true
    ElMessage.success(`已生成 ${aiDrafts.value.length} 道题目草稿`)
  } catch (err: any) {
    console.error('AI出题失败', err)
    ElMessage.error(err?.message || 'AI出题失败')
  } finally {
    aiGenerating.value = false
  }
}

async function handleSaveAiDrafts() {
  if (!paperInfo.value.courseId) {
    ElMessage.warning('请先在右侧选择保存题目的关联课程')
    return
  }
  if (selectedAiDrafts.value.length === 0) {
    ElMessage.warning('请先勾选题目草稿')
    return
  }
  try {
    await saveAiQuestionDrafts({
      courseId: paperInfo.value.courseId,
      questions: selectedAiDrafts.value,
    })
    ElMessage.success('AI题目已保存到题库')
    await loadQuestions()
  } catch (err) {
    console.error('保存AI题目失败', err)
    ElMessage.error('保存失败')
  }
}

async function handleAddAiDraftsToPaper() {
  if (!paperInfo.value.name) paperInfo.value.name = 'AI生成试卷草稿'
  if (!paperInfo.value.courseId) {
    ElMessage.warning('请先选择试卷关联课程')
    return
  }
  try {
    if (!currentPaperId.value) {
      const created: any = await createPaper({
        name: paperInfo.value.name,
        courseId: Number(paperInfo.value.courseId),
        totalScore: totalAssignedScore.value || aiConfig.value.count * aiConfig.value.score,
        duration: paperInfo.value.duration,
        status: 0,
      })
      currentPaperId.value = created?.id || created?.data?.id || null
    }
    if (!currentPaperId.value) {
      ElMessage.error('试卷创建失败')
      return
    }
    const res = await addAiQuestionsToPaper({
      paperId: currentPaperId.value,
      courseId: paperInfo.value.courseId,
      questions: selectedAiDrafts.value,
    })
    const ids = res?.questionIds || []
    await loadQuestions()
    selectedQuestionIds.value = Array.from(new Set([...selectedQuestionIds.value, ...ids]))
    ElMessage.success('AI题目已加入当前试卷')
  } catch (err) {
    console.error('加入试卷失败', err)
    ElMessage.error('加入试卷失败')
  }
}

// 课程选项
const courseOptions = ref<{ label: string; value: number }[]>([])
const knowledgeOptions = ref<{ label: string; value: string }[]>([])

// 加载课程列表
async function loadCourses() {
  try {
    const res: any = await getTeacherCourses({ pageSize: 100 })
    courseOptions.value = (Array.isArray(res) ? res : res?.list || res?.data?.list || []).map((c: any) => ({
      label: c.name,
      value: c.id,
    }))
  } catch (err) {
    console.error('加载课程失败', err)
  }
}

// 加载知识点列表
async function loadKnowledgePoints() {
  try {
    const res: any = await getKnowledgePoints(filters.value.course || undefined)
    knowledgeOptions.value = Array.isArray(res) ? res : (res?.data || [])
  } catch (err) {
    console.error('加载知识点失败', err)
  }
}

// 监听课程变化，重新加载知识点
watch(() => filters.value.course, () => {
  filters.value.knowledge = ''
  loadKnowledgePoints()
})

function resetFilters() {
  filters.value = { type: '', difficulty: '', course: '', knowledge: '' }
  searchKeyword.value = ''
  loadQuestions()
}

// ========== 题目数据 ==========
interface Question {
  id: number
  content: string
  type: string
  typeName: string
  difficulty: string
  difficultyName: string
  score: number
  assignedScore: number
  courseName: string
  knowledge: string
}

const allQuestions = ref<Question[]>([])

async function loadQuestions() {
  loadingQuestions.value = true
  try {
    const res = await getQuestionsForPaper({
      courseId: filters.value.course || undefined,
      type: filters.value.type || undefined,
      difficulty: filters.value.difficulty || undefined,
      keyword: searchKeyword.value || undefined,
      pageSize: 200,
    })
    const data = res?.questions || []
    allQuestions.value = data.map((q: any) => ({
      ...q,
      id: q.id,
      content: q.content || '',
      type: q.type || 'essay',
      typeName: q.typeName || '其他',
      difficulty: q.difficulty || 'medium',
      difficultyName: q.difficultyName || '中等',
      score: Number(q.score) || 10,
      assignedScore: Number(q.assignedScore) || Number(q.score) || 10,
      courseName: q.courseName || '',
      knowledge: q.knowledge || '',
    }))
  } catch (err) {
    console.error('加载题目失败', err)
    ElMessage.error('加载题目列表失败')
  } finally {
    loadingQuestions.value = false
  }
}

// 监听筛选条件变化，重新加载题目
watch([() => filters.value.type, () => filters.value.difficulty, searchKeyword], () => {
  loadQuestions()
})

// 初始加载
onMounted(async () => {
  await loadCourses()
  await loadKnowledgePoints()
  await loadQuestions()
})

const filteredQuestions = computed(() => {
  return allQuestions.value.filter(q => {
    if (filters.value.type && q.type !== filters.value.type) return false
    if (filters.value.difficulty && q.difficulty !== filters.value.difficulty) return false
    if (searchKeyword.value && !q.content.toLowerCase().includes(searchKeyword.value.toLowerCase())) return false
    return true
  })
})

const pagedQuestions = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredQuestions.value.slice(start, start + pageSize.value)
})

// ========== 选择逻辑 ==========
const selectedQuestionIds = ref<number[]>([])

const selectedQuestions = computed(() => {
  return allQuestions.value.filter(q => selectedQuestionIds.value.includes(q.id))
})

function toggleQuestion(id: number) {
  const idx = selectedQuestionIds.value.indexOf(id)
  if (idx > -1) {
    selectedQuestionIds.value.splice(idx, 1)
  } else {
    selectedQuestionIds.value.push(id)
  }
}

function handleSelectAll(val: boolean) {
  if (val) {
    const pageIds = pagedQuestions.value.map(q => q.id)
    selectedQuestionIds.value = Array.from(new Set([...selectedQuestionIds.value, ...pageIds]))
  } else {
    const pageIds = pagedQuestions.value.map(q => q.id)
    selectedQuestionIds.value = selectedQuestionIds.value.filter(id => !pageIds.includes(id))
  }
}

function removeQuestion(id: number) {
  selectedQuestionIds.value = selectedQuestionIds.value.filter(qid => qid !== id)
}

// ========== 拖拽排序 ==========
let dragIndex = -1

function handleDragStart(_e: DragEvent, index: number) {
  dragIndex = index
}

function handleDrop(_e: DragEvent, index: number) {
  if (dragIndex === -1 || dragIndex === index) return
  const arr = [...selectedQuestions.value]
  const [removed] = arr.splice(dragIndex, 1)
  arr.splice(index, 0, removed)
  // 更新选中顺序
  selectedQuestionIds.value = arr.map(q => q.id)
  dragIndex = -1
}

// ========== 试卷信息 ==========
const paperInfo = ref({
  name: '',
  courseId: '' as string | number,
  totalScore: 100,
  duration: 120,
})
const savingPaper = ref(false)
const currentPaperId = ref<number | null>(null)

// ========== 分值统计 ==========
const scoreStats = computed(() => {
  const map: Record<string, { label: string; score: number; count: number }> = {}
  for (const q of selectedQuestions.value) {
    if (!map[q.type]) {
      map[q.type] = { label: q.typeName, score: 0, count: 0 }
    }
    map[q.type].score += q.assignedScore
    map[q.type].count++
  }
  return Object.values(map)
})

const totalAssignedScore = computed(() => {
  return selectedQuestions.value.reduce((sum, q) => sum + q.assignedScore, 0)
})

watch(totalAssignedScore, (value) => {
  if (value > 0 && paperInfo.value.totalScore !== value) {
    paperInfo.value.totalScore = value
  }
})

// ========== 类型映射 ==========
function getTypeTagType(type: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    single: '',
    multiple: 'success',
    judge: 'warning',
    fill: 'info',
    single_choice: '',
    multi_choice: 'success',
    true_false: 'warning',
    fill_blank: 'info',
    essay: 'danger',
    code: 'danger',
  }
  return map[type] || 'info'
}

function getDifficultyTagType(diff: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger',
  }
  return map[diff] || 'info'
}

// ========== 智能组卷 ==========
const autoComposeVisible = ref(false)
const autoLoading = ref(false)

const autoConfig = ref({
  totalScore: 100,
  duration: 120,
  types: [
    { type: 'single', label: '单选题', count: 10, score: 2 },
    { type: 'multiple', label: '多选题', count: 5, score: 4 },
    { type: 'judge', label: '判断题', count: 5, score: 2 },
    { type: 'fill', label: '填空题', count: 5, score: 2 },
    { type: 'essay', label: '简答题', count: 2, score: 10 },
    { type: 'code', label: '编程题', count: 1, score: 20 },
  ],
  difficultyRatio: [30, 70] as [number, number],
  knowledgePoints: [] as string[],
  excludeUsed: true,
})

function handleAutoCompose() {
  autoLoading.value = true
  try {
    const selectedIds: number[] = []
    const shortage: string[] = []
    const knowledgeFilter = autoConfig.value.knowledgePoints.map(item => item.trim()).filter(Boolean)
    const difficultyRange = autoConfig.value.difficultyRatio

    for (const rule of autoConfig.value.types.filter(item => item.count > 0)) {
      const candidates = allQuestions.value
        .filter(q => normalizeQuestionType(q.type) === normalizeQuestionType(rule.type))
        .filter(q => !paperInfo.value.courseId || String((q as any).courseId || '') === String(paperInfo.value.courseId))
        .filter(q => matchDifficultyRatio(q.difficulty, difficultyRange))
        .filter(q => knowledgeFilter.length === 0 || knowledgeFilter.some(point => String(q.knowledge || '').includes(point)))
        .filter(q => !selectedIds.includes(q.id))
        .sort((a, b) => a.id - b.id)

      const picked = candidates.slice(0, rule.count)
      if (picked.length < rule.count) {
        shortage.push(`${rule.label}缺少 ${rule.count - picked.length} 道`)
      }
      picked.forEach(q => {
        q.assignedScore = rule.score || q.assignedScore || q.score || 10
        selectedIds.push(q.id)
      })
    }

    if (selectedIds.length === 0) {
      ElMessage.warning('当前真实题库中没有符合条件的题目，请调整课程、题型或难度配置')
      return
    }

    selectedQuestionIds.value = selectedIds
    paperInfo.value.totalScore = totalAssignedScore.value || autoConfig.value.totalScore
    paperInfo.value.duration = autoConfig.value.duration
    autoComposeVisible.value = false
    const shortageText = shortage.length ? `，${shortage.join('、')}` : ''
    ElMessage.success(`智能组卷完成，已从真实题库选取 ${selectedIds.length} 道题${shortageText}`)
  } finally {
    autoLoading.value = false
  }
}

function normalizeQuestionType(type: string): string {
  const map: Record<string, string> = {
    single: 'single_choice',
    multiple: 'multi_choice',
    judge: 'true_false',
    fill: 'fill_blank',
  }
  return map[type] || type
}

function matchDifficultyRatio(difficulty: string, range: [number, number]): boolean {
  const level = difficulty === 'easy' ? 30 : difficulty === 'hard' ? 90 : 60
  return level >= range[0] && level <= range[1]
}

// ========== 操作 ==========
async function handleSaveDraft() {
  if (!paperInfo.value.name) {
    ElMessage.warning('请先输入试卷名称')
    return
  }
  if (!paperInfo.value.courseId) {
    ElMessage.warning('请先选择关联课程')
    return
  }
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }

  savingPaper.value = true
  try {
    // 构建题目列表
    const questionIds = selectedQuestions.value.map(q => q.id)
    const scores = selectedQuestions.value.map(q => q.assignedScore)

    if (currentPaperId.value) {
      // 更新已有试卷
      await updatePaper(currentPaperId.value, {
        name: paperInfo.value.name,
        courseId: Number(paperInfo.value.courseId),
        totalScore: totalAssignedScore.value,
        duration: paperInfo.value.duration,
      })
      // 更新题目
      await addPaperQuestions(currentPaperId.value, { questionIds, scores })
      ElMessage.success('试卷已更新')
    } else {
      // 创建新试卷
      const res: any = await createPaper({
        name: paperInfo.value.name,
        courseId: Number(paperInfo.value.courseId),
        totalScore: totalAssignedScore.value,
        duration: paperInfo.value.duration,
        status: 0, // 草稿状态
      })
      currentPaperId.value = res?.id || res?.data?.id || null
      if (currentPaperId.value) {
        // 添加题目
        await addPaperQuestions(currentPaperId.value, { questionIds, scores })
      }
      ElMessage.success('试卷草稿已保存')
    }
  } catch (err) {
    console.error('保存失败', err)
    ElMessage.error('保存失败')
  } finally {
    savingPaper.value = false
  }
}

function handlePreviewExam() {
  if (!paperInfo.value.name) {
    ElMessage.warning('请先输入试卷名称')
    return
  }
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }
  ElMessage.info('正在预览试卷...')
}

async function handlePublishExam() {
  if (!paperInfo.value.name) {
    ElMessage.warning('请先输入试卷名称')
    return
  }
  if (!paperInfo.value.courseId) {
    ElMessage.warning('请先选择关联课程')
    return
  }
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }

  try {
    await handleSaveDraft()
  } catch {
    return
  }

  if (!currentPaperId.value) {
    ElMessage.error('请先保存试卷')
    return
  }

  ElMessageBox.confirm(
    `确定将「${paperInfo.value.name}」发布为考试吗？发布后不可修改试卷内容。`,
    '确认发布',
    { confirmButtonText: '确认发布', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      if (currentPaperId.value) {
        await updatePaper(currentPaperId.value, { status: 1 } as any)
      }
      ElMessage.success('考试发布成功！')
      // 可选：跳转到考试安排页面
    } catch (err) {
      console.error('发布失败', err)
      ElMessage.error('发布失败')
    }
  }).catch(() => {})
}
</script>

