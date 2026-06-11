<template>
  <div class="h-full overflow-auto p-6 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center gap-3 mb-6">
      <router-link to="/teacher/questions" class="text-gray-400 hover:text-primary-500 transition-colors cursor-pointer">
        <i class="ri-arrow-left-line text-lg"></i>
      </router-link>
      <h1 class="text-xl font-bold text-gray-800">{{ isEdit ? '编辑题目' : '新增题目' }}</h1>
    </div>

    <div class="max-w-4xl mx-auto space-y-5">
      <!-- 基本信息卡片 -->
      <div class="bg-white rounded-xl p-6 shadow-sm">
        <h3 class="text-base font-semibold text-gray-800 mb-4 flex items-center gap-2">
          <i class="ri-file-text-line text-primary-500"></i>基本信息
        </h3>

        <!-- 题型选择 -->
        <div class="mb-5">
          <label class="block text-sm font-medium text-gray-700 mb-2">题型 <span class="text-danger-500">*</span></label>
          <el-radio-group v-model="form.type" size="default" @change="handleTypeChange">
            <el-radio-button value="single">单选题</el-radio-button>
            <el-radio-button value="multiple">多选题</el-radio-button>
            <el-radio-button value="judge">判断题</el-radio-button>
            <el-radio-button value="fill">填空题</el-radio-button>
            <el-radio-button value="essay">简答题</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 题目内容 -->
        <div class="mb-5">
          <label class="block text-sm font-medium text-gray-700 mb-2">题目内容 <span class="text-danger-500">*</span></label>
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入题目内容..."
            maxlength="2000"
            show-word-limit
          />
        </div>

        <!-- 选项编辑区（选择题） -->
        <div v-if="isChoiceType" class="mb-5">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            选项设置
            <span v-if="form.type === 'multiple'" class="text-xs text-gray-400 ml-2">(可多选)</span>
          </label>
          <div class="space-y-3">
            <div
              v-for="(option, index) in form.options"
              :key="index"
              class="flex items-center gap-3 group"
            >
              <span class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-sm font-bold text-gray-600 flex-shrink-0">
                {{ ['A', 'B', 'C', 'D', 'E', 'F'][index] }}
              </span>
              <el-input
                v-model="option.text"
                :placeholder="`选项 ${['A', 'B', 'C', 'D', 'E', 'F'][index]}`"
                style="flex: 1"
              />
              <el-checkbox v-model="option.isCorrect" label="正确答案" />
              <button
                v-if="form.options.length > 2"
                @click="removeOption(index)"
                class="w-8 h-8 rounded-lg text-gray-300 hover:text-danger-500 hover:bg-red-50 transition-colors cursor-pointer flex items-center justify-center opacity-0 group-hover:opacity-100"
              >
                <i class="ri-close-line"></i>
              </button>
            </div>
          </div>
          <button
            v-if="form.options.length < 6 && isChoiceType"
            @click="addOption"
            class="mt-3 text-sm text-primary-500 hover:text-primary-600 transition-colors cursor-pointer flex items-center gap-1"
          >
            <i class="ri-add-circle-line"></i>添加选项
          </button>
        </div>

        <!-- 判断题答案 -->
        <div v-if="form.type === 'judge'" class="mb-5">
          <label class="block text-sm font-medium text-gray-700 mb-2">正确答案 <span class="text-danger-500">*</span></label>
          <el-radio-group v-model="form.judgeAnswer">
            <el-radio value="true"><i class="ri-check-line mr-1 text-green-500"></i>正确</el-radio>
            <el-radio value="false"><i class="ri-close-line mr-1 text-danger-500"></i>错误</el-radio>
          </el-radio-group>
        </div>

        <!-- 答案输入（填空/简答） -->
        <div v-if="!isChoiceType && form.type !== 'judge'" class="mb-5">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            参考答案 <span class="text-danger-500">*</span>
            <span v-if="form.type === 'fill'" class="text-xs text-gray-400 ml-2">(多个答案用英文逗号分隔)</span>
          </label>
          <el-input
            v-model="form.answer"
            type="textarea"
            :rows="form.type === 'essay' ? 5 : 2"
            placeholder="请输入参考答案..."
          />
        </div>

        <!-- 解析 -->
        <div class="mb-0">
          <label class="block text-sm font-medium text-gray-700 mb-2">题目解析（可选）</label>
          <el-input
            v-model="form.explanation"
            type="textarea"
            :rows="3"
            placeholder="请输入题目解析，帮助学生理解..."
            maxlength="2000"
            show-word-limit
          />
        </div>
      </div>

      <!-- 属性设置卡片 -->
      <div class="bg-white rounded-xl p-6 shadow-sm">
        <h3 class="text-base font-semibold text-gray-800 mb-4 flex items-center gap-2">
          <i class="ri-settings-4-line text-primary-500"></i>属性设置
        </h3>

        <div class="grid grid-cols-2 gap-5">
          <!-- 难度选择 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">难度等级 <span class="text-danger-500">*</span></label>
            <el-select v-model="form.difficulty" placeholder="请选择难度" style="width: 100%">
              <el-option label="简单" value="easy" />
              <el-option label="中等" value="medium" />
              <el-option label="困难" value="hard" />
            </el-select>
          </div>

          <!-- 所属课程 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">所属课程 <span class="text-danger-500">*</span></label>
            <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
              <el-option
                v-for="c in courseOptions"
                :key="c.value"
                :label="c.label"
                :value="c.value"
              />
            </el-select>
          </div>

          <!-- 分值 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">分值 <span class="text-danger-500">*</span></label>
            <el-input-number
              v-model="form.score"
              :min="1"
              :max="50"
              :step="1"
              controls-position="right"
              style="width: 100%"
            />
          </div>

          <!-- 知识点 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">知识点标签</label>
            <el-select
              v-model="form.knowledgePoints"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="输入或选择知识点"
              style="width: 100%"
            >
              <el-option
                v-for="kp in knowledgePointOptions"
                :key="kp"
                :label="kp"
                :value="kp"
              />
            </el-select>
          </div>

          <!-- 章节 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">所属章节</label>
            <el-cascader
              v-model="form.chapter"
              :options="chapterOptions"
              placeholder="选择章节"
              style="width: 100%"
              clearable
            />
          </div>

          <!-- 状态 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">启用状态</label>
            <el-switch
              v-model="form.status"
              active-value="active"
              inactive-value="disabled"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
            />
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="flex items-center justify-end gap-3 pb-6">
        <el-button size="large" @click="handleCancel">取消</el-button>
        <el-button size="large" @click="handleSaveDraft">保存草稿</el-button>
        <el-button type="primary" size="large" @click="handleSubmit">
          <i class="ri-check-line mr-1"></i>{{ isEdit ? '保存修改' : '创建题目' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getTeacherCourses,
  getKnowledgePoints,
  createQuestion,
  updateQuestion,
  getQuestionDetail,
} from '@/api/teacher'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.query.id)

// 表单数据
interface OptionItem {
  text: string
  isCorrect: boolean
}

const form = reactive({
  type: 'single',
  content: '',
  options: [
    { text: '', isCorrect: false },
    { text: '', isCorrect: false },
    { text: '', isCorrect: false },
    { text: '', isCorrect: false },
  ] as OptionItem[],
  judgeAnswer: '' as string,
  answer: '',
  explanation: '',
  difficulty: '' as string,
  courseId: '' as string,
  score: 2 as number,
  knowledgePoints: [] as string[],
  chapter: [] as (string | number)[],
  status: 'active' as string,
})

// 是否为选择题类型
const isChoiceType = computed(() => form.type === 'single' || form.type === 'multiple')

// 课程选项（动态加载）
const courseOptions = ref<Array<{ label: string; value: string | number }>>([])

// 加载课程列表
async function loadCourseOptions() {
  try {
    const res = await getTeacherCourses({ pageSize: 100 })
    // 后端返回 Result<PageResult> 格式：{ code: 200, data: { list: [...], total: N } }
    const resData = res as any
    const list = Array.isArray(resData) ? resData : (resData?.list || resData?.data?.list || [])
    courseOptions.value = list.map((c: any) => ({
      label: c.name,
      value: c.id,
    }))
  } catch (err) {
    console.error('加载课程列表失败', err)
  }
}

// 知识点选项（动态加载，保留本地默认值作为兜底）
const knowledgePointOptions = ref<string[]>([
  'Java基础语法',
  '面向对象编程',
  '异常处理',
  '集合框架',
  'IO流',
  '多线程',
  '网络编程',
  '数组与链表',
  '栈与队列',
  '树与图',
  '排序算法',
  '查找算法',
])

// 加载知识点列表
async function loadKnowledgePointOptions() {
  try {
    const res = await getKnowledgePoints()
    if (Array.isArray(res) && res.length > 0) {
      knowledgePointOptions.value = res.map((kp: any) => kp.label || kp.value || kp)
    }
  } catch (err) {
    console.warn('加载知识点失败，使用默认知识点', err)
  }
}

// 章节级联选项
const chapterOptions = [
  {
    value: 'ch1',
    label: '第一章 Java语言概述',
    children: [
      { value: 'ch1-1', label: '1.1 Java发展历史' },
      { value: 'ch1-2', label: '1.2 开发环境搭建' },
      { value: 'ch1-3', label: '1.3 第一个Java程序' },
    ],
  },
  {
    value: 'ch2',
    label: '第二章 基本数据类型与运算符',
    children: [
      { value: 'ch2-1', label: '2.1 数据类型' },
      { value: 'ch2-2', label: '2.2 变量与常量' },
      { value: 'ch2-3', label: '2.3 运算符' },
    ],
  },
  {
    value: 'ch3',
    label: '第三章 面向对象编程',
    children: [
      { value: 'ch3-1', label: '3.1 类与对象' },
      { value: 'ch3-2', label: '3.2 封装' },
      { value: 'ch3-3', label: '3.3 继承' },
      { value: 'ch3-4', label: '3.4 多态' },
    ],
  },
]

// 方法
function handleTypeChange() {
  // 切换题型时重置部分字段
  if (isChoiceType.value) {
    // 保持默认4个选项
  }
}

function addOption() {
  if (form.options.length < 6) {
    form.options.push({ text: '', isCorrect: false })
  }
}

function removeOption(index: number) {
  if (form.options.length > 2) {
    form.options.splice(index, 1)
  }
}

function validateForm(): boolean {
  if (!form.type) {
    ElMessage.warning('请选择题型')
    return false
  }
  if (!form.content.trim()) {
    ElMessage.warning('请输入题目内容')
    return false
  }

  if (isChoiceType.value) {
    const hasContent = form.options.every(o => o.text.trim())
    if (!hasContent) {
      ElMessage.warning('请填写所有选项内容')
      return false
    }
    const hasCorrect = form.options.some(o => o.isCorrect)
    if (!hasCorrect) {
      ElMessage.warning('请至少设置一个正确答案')
      return false
    }
  }

  if (form.type === 'judge' && !form.judgeAnswer) {
    ElMessage.warning('请选择判断题的正确答案')
    return false
  }

  if ((form.type === 'fill' || form.type === 'essay') && !form.answer.trim()) {
    ElMessage.warning('请输入参考答案')
    return false
  }

  if (!form.difficulty) {
    ElMessage.warning('请选择难度等级')
    return false
  }
  if (!form.courseId) {
    ElMessage.warning('请选择所属课程')
    return false
  }

  return true
}

// 构建提交数据
function buildSubmitData(isDraft = false) {
  const data: Record<string, any> = {
    type: form.type,
    content: form.content,
    difficulty: form.difficulty,
    courseId: Number(form.courseId),
    score: form.score,
    // 后端 knowledgePoints 字段是 String 类型，数组需要转为逗号分隔字符串
    knowledgePoints: Array.isArray(form.knowledgePoints) ? form.knowledgePoints.join(',') : (form.knowledgePoints || ''),
    // 后端没有 chapter 字段，不传
    status: isDraft ? 'draft' : (form.status || 'active'),
    analysis: form.explanation,
  }

  if (isChoiceType.value) {
    data.options = form.options.map((o, i) => ({
      label: ['A', 'B', 'C', 'D', 'E', 'F'][i],
      content: o.text,
      isCorrect: o.isCorrect,
    }))
  }

  if (form.type === 'judge') {
    data.answer = form.judgeAnswer === 'true' ? '正确' : '错误'
  } else if (!isChoiceType.value) {
    data.answer = form.answer
  }

  return data
}

async function handleSaveDraft() {
  try {
    const data = buildSubmitData(true)
    if (isEdit.value && route.query.id) {
      await updateQuestion(Number(route.query.id), data)
      ElMessage.success('草稿已保存')
    } else {
      await createQuestion(data)
      ElMessage.success('草稿已保存')
    }
  } catch (err) {
    console.error('保存草稿失败', err)
    ElMessage.error('保存草稿失败，请重试')
  }
}

async function handleSubmit() {
  if (!validateForm()) return

  ElMessageBox.confirm(
    isEdit.value ? '确定要修改此题目吗？' : '确定要创建此题目吗？',
    '确认操作',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }
  ).then(async () => {
    try {
      const data = buildSubmitData(false)
      if (isEdit.value && route.query.id) {
        await updateQuestion(Number(route.query.id), data)
        ElMessage.success('题目已更新')
      } else {
        await createQuestion(data)
        ElMessage.success('题目创建成功')
      }
      router.push('/teacher/questions')
    } catch (err) {
      console.error('保存失败', err)
      ElMessage.error('操作失败，请重试')
    }
  }).catch(() => {})
}

function handleCancel() {
  ElMessageBox.confirm(
    '当前编辑的内容尚未保存，确定要离开吗？',
    '确认离开',
    { confirmButtonText: '离开', cancelButtonText: '继续编辑', type: 'warning' }
  ).then(() => {
    router.push('/teacher/questions')
  }).catch(() => {})
}

onMounted(async () => {
  // 加载课程和知识点选项
  await Promise.all([loadCourseOptions(), loadKnowledgePointOptions()])

  if (isEdit.value) {
    // 编辑模式：加载现有题目数据
    const questionId = route.query.id
    try {
      const res = await getQuestionDetail(Number(questionId))
      const resData = res as any
      const q: Record<string, any> = resData?.data || resData || {}
      form.type = q.type || q.questionType || 'single'
      form.content = q.content || q.questionContent || ''
      form.difficulty = q.difficulty || ''
      form.courseId = String(q.courseId || '')
      form.score = q.score || q.fullScore || 2
      form.knowledgePoints = Array.isArray(q.knowledgePoints) ? q.knowledgePoints : []
      form.chapter = q.chapter ? (typeof q.chapter === 'string' ? JSON.parse(q.chapter) : q.chapter) : []
      form.status = q.status || 'active'
      form.explanation = q.explanation || q.analysis || ''

      // 回填选项（选择题）
      if ((form.type === 'single' || form.type === 'multiple') && Array.isArray(q.options) && q.options.length > 0) {
        form.options = q.options.map((o: any) => ({
          text: o.text || o.content || '',
          isCorrect: o.isCorrect || false,
        }))
        // 确保至少4个选项
        while (form.options.length < 4) {
          form.options.push({ text: '', isCorrect: false })
        }
      }

      // 回填判断题答案
      if (form.type === 'judge' && q.answer) {
        const ans = String(q.answer).trim()
        form.judgeAnswer = (ans === '正确' || ans === 'true' || ans === 'T') ? 'true' : 'false'
      }

      // 回填填空/简答题答案
      if ((form.type === 'fill' || form.type === 'essay') && q.answer) {
        form.answer = Array.isArray(q.answer) ? q.answer.join(',') : String(q.answer)
      }
    } catch (err) {
      console.error('加载题目详情失败', err)
      ElMessage.error('加载题目详情失败')
    }
  }
})
</script>
