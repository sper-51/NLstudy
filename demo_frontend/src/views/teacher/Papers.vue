<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">试卷管理</h1>
        <p class="text-sm text-gray-500 mt-1">共 {{ filteredPapers.length }} 份试卷</p>
      </div>
      <el-button type="primary" @click="handleCreatePaper">
        <i class="ri-add-line mr-1"></i>新建试卷
      </el-button>
    </div>

    <!-- 搜索 + 筛选栏 -->
    <div class="bg-white rounded-xl p-4 shadow-sm">
      <div class="flex items-center gap-3 flex-wrap">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索试卷名称..."
          prefix-icon="Search"
          style="width: 260px"
          clearable
          @input="onFilterChange"
        />
        <el-select v-model="filterCourse" placeholder="所属课程" style="width: 180px" clearable @change="onFilterChange">
          <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="状态筛选" style="width: 130px" clearable @change="onFilterChange">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
        </el-select>
        <div class="flex-1"></div>
        <el-button @click="resetFilters">
          <i class="ri-refresh-line mr-1"></i>重置筛选
        </el-button>
      </div>
    </div>

    <!-- 试卷卡片列表 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="paper in filteredPapers"
        :key="paper.id"
        class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-all border border-gray-100 hover:border-primary-200 group"
      >
        <!-- 卡片头部 -->
        <div class="flex items-start justify-between mb-3">
          <div class="flex items-start gap-3">
            <div class="w-11 h-11 rounded-xl bg-primary-50 flex items-center justify-center flex-shrink-0 mt-0.5">
              <i class="ri-file-list-2-fill text-primary-500 text-xl"></i>
            </div>
            <div>
              <h4 class="font-semibold text-gray-800 text-sm leading-snug group-hover:text-primary-600 transition-colors">{{ displayText(paper.name, `试卷 ${paper.id}`) }}</h4>
              <p class="text-xs text-gray-500 mt-1">{{ displayText(paper.courseName, paper.courseId ? `课程 ${paper.courseId}` : '未关联课程') }}</p>
            </div>
          </div>
          <el-tag :type="getStatusTagType(String(paper.status))" size="small" effect="light">
            {{ getStatusText(String(paper.status)) }}
          </el-tag>
        </div>

        <!-- 试卷信息 -->
        <div class="flex items-center gap-4 text-xs text-gray-500 mb-4 px-1">
          <span class="flex items-center gap-1"><i class="ri-star-line"></i>总分：{{ paper.totalScore }}</span>
          <span class="flex items-center gap-1"><i class="ri-file-text-line"></i>{{ paper.questionCount }} 题</span>
          <span class="flex items-center gap-1"><i class="ri-stack-line"></i>{{ getMethodText(paper.method || paper.type || '') }}</span>
        </div>

        <!-- 操作按钮组 -->
        <div class="flex items-center gap-2 pt-3 border-t border-gray-100">
          <button
            @click="handlePreview(paper)"
            class="flex-1 py-2 text-xs font-medium text-primary-500 bg-primary-50 hover:bg-primary-100 rounded-lg transition-colors cursor-pointer flex items-center justify-center gap-1"
          >
            <i class="ri-eye-line"></i>预览
          </button>
          <button
            @click="handleEdit(paper)"
            class="flex-1 py-2 text-xs font-medium text-gray-600 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors cursor-pointer flex items-center justify-center gap-1"
          >
            <i class="ri-edit-line"></i>编辑
          </button>
          <button
            v-if="paper.status === 'draft'"
            @click="handlePublish(paper)"
            class="flex-1 py-2 text-xs font-medium text-success-600 bg-green-50 hover:bg-green-100 rounded-lg transition-colors cursor-pointer flex items-center justify-center gap-1"
          >
            <i class="ri-send-plane-line"></i>发布
          </button>
          <button
            @click="handleCompose(paper)"
            class="py-2 px-3 text-xs font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors cursor-pointer flex items-center gap-1"
            title="智能组卷"
          >
            <i class="ri-magic-line"></i>
          </button>
          <el-popconfirm title="确定删除此试卷？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleDelete(paper)">
            <template #reference>
              <button
                class="py-2 px-3 text-xs font-medium text-danger-500 bg-red-50 hover:bg-red-100 rounded-lg transition-colors cursor-pointer flex items-center gap-1"
              >
                <i class="ri-delete-bin-line"></i>
              </button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredPapers.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-400">
      <i class="ri-file-list-2-line text-5xl mb-4"></i>
      <p class="text-lg font-medium">暂无试卷</p>
      <p class="text-sm mt-1">点击右上角按钮创建新试卷</p>
      <el-button type="primary" class="mt-4" @click="handleCreatePaper">
        <i class="ri-add-line mr-1"></i>新建试卷
      </el-button>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="试卷预览" width="700px" destroy-on-close>
      <div v-if="currentPreview" class="space-y-4">
        <div class="border-b pb-4">
          <h3 class="text-lg font-bold text-gray-800">{{ displayText(currentPreview.name, `试卷 ${currentPreview.id}`) }}</h3>
          <div class="flex items-center gap-4 mt-2 text-sm text-gray-500">
            <span><i class="ri-book-line mr-1"></i>{{ displayText(currentPreview.courseName, currentPreview.courseId ? `课程 ${currentPreview.courseId}` : '未关联课程') }}</span>
            <span><i class="ri-star-line mr-1"></i>{{ currentPreview.totalScore }} 分</span>
            <span><i class="ri-file-text-line mr-1"></i>{{ currentPreview.questionCount }} 道题</span>
            <el-tag :type="getStatusTagType(currentPreview.status)" size="small">{{ getStatusText(currentPreview.status) }}</el-tag>
          </div>
        </div>

        <div class="space-y-3">
          <div v-for="(section, sIdx) in currentPreview.sections" :key="sIdx" class="bg-gray-50 rounded-lg p-4">
            <div class="font-semibold text-sm text-gray-700 mb-2 flex items-center gap-2">
              <span class="w-6 h-6 rounded-full bg-primary-100 text-primary-600 text-xs flex items-center justify-center">{{ sIdx + 1 }}</span>
              {{ section.title }}
              <span class="text-xs text-gray-400 font-normal">（共 {{ section.questions.length }} 题，{{ section.score }} 分）</span>
            </div>
            <div class="space-y-2 pl-8">
              <div v-for="(q, qIdx) in section.questions" :key="qIdx" class="text-sm text-gray-600">
                <span class="text-gray-400 mr-1">{{ qIdx + 1 }}.</span>
                {{ q.content }}
                <span class="text-xs text-gray-400 ml-2">（{{ q.score }}分）</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEditFromPreview">编辑试卷</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPapers, deletePaper, previewPaper, updatePaper, getTeacherCourses } from '@/api/teacher'

const router = useRouter()

// 搜索和筛选
const searchKeyword = ref('')
const filterCourse = ref('')
const filterStatus = ref('')

const courseOptions = ref<Array<{ label: string; value: string | number }>>([])

// 加载状态
const loading = ref(false)

// 预览弹窗
const previewVisible = ref(false)
const currentPreview = ref<any>(null)

// 试卷数据类型
interface PaperQuestion {
  content: string
  score: number
}

interface PaperSection {
  title: string
  questions: PaperQuestion[]
  score: number
}

interface Paper {
  id: number
  name: string
  courseId?: number
  courseName: string
  totalScore: number
  questionCount: number
  type?: string
  method?: string
  status: string | number
  createTime: string
  sections?: PaperSection[]
}

const papers = ref<Paper[]>([])

function isReadableText(value: unknown): boolean {
  if (value === null || value === undefined) return false
  const text = String(value).trim()
  return text.length > 0 && !/^\?+$/.test(text) && !/[�ÃÂæäåéèç]/.test(text)
}

function displayText(value: unknown, fallback = '暂无数据'): string {
  return isReadableText(value) ? String(value) : fallback
}

// 筛选后的列表
const filteredPapers = computed(() => {
  return papers.value.filter(p => {
    const matchSearch = !searchKeyword.value ||
      p.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    const matchCourse = !filterCourse.value || p.courseId?.toString() === filterCourse.value.toString() || p.courseName.includes(filterCourse.value.toString())
    const matchStatus = !filterStatus.value || String(p.status) === filterStatus.value
    return matchSearch && matchCourse && matchStatus
  })
})

// 加载试卷列表
async function loadPapers() {
  loading.value = true
  try {
    const res = await getPapers({
      courseId: filterCourse.value || undefined,
      status: filterStatus.value ? (filterStatus.value === 'published' ? 1 : 0) : undefined,
      keyword: searchKeyword.value || undefined,
      pageSize: 100,
    })
    const resData = res as any
    const data = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || [])
    papers.value = data.map((p: any) => ({
      id: p.id,
      name: displayText(p.name || p.paperName, `试卷 ${p.id}`),
      courseId: p.courseId,
      courseName: displayText(p.courseName, p.courseId ? `课程 ${p.courseId}` : '未关联课程'),
      totalScore: p.totalScore || 0,
      questionCount: p.questionCount || 0,
      type: p.type,
      method: p.type === 'manual' ? 'manual' : 'auto',
      status: p.status === 1 || p.status === 'published' ? 'published' : 'draft',
      createTime: p.createTime || p.createAt || '',
    }))
  } catch (err) {
    console.error('加载试卷列表失败', err)
    papers.value = []
  } finally {
    loading.value = false
  }
}

// 加载课程列表
async function loadCourseOptions() {
  try {
    const res = await getTeacherCourses({ pageSize: 100 })
    const resData = res as any
    const courses = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || resData?.data || [])
    courseOptions.value = courses.map((c: any) => ({
      label: displayText(c.name, `课程 ${c.id}`),
      value: c.id,
    }))
  } catch (err) {
    console.error('加载课程列表失败', err)
  }
}

// 重置筛选
function resetFilters() {
  searchKeyword.value = ''
  filterCourse.value = ''
  filterStatus.value = ''
  loadPapers()
}

function handleCreatePaper() {
  router.push('/teacher/papers/builder')
}

async function handlePreview(paper: Paper) {
  try {
    const res = await previewPaper(paper.id)
    currentPreview.value = {
      ...paper,
      ...res,
      sections: res?.questions ? buildSections(res.questions) : [],
    }
    previewVisible.value = true
  } catch (err) {
    console.error('预览试卷失败', err)
    // 如果预览失败，使用本地数据
    currentPreview.value = paper
    previewVisible.value = true
  }
}

// 从题目列表构建章节
function buildSections(questions: any[]): PaperSection[] {
  const sectionMap = new Map<string, { title: string; questions: PaperQuestion[]; score: number }>()
  questions.forEach(q => {
    const typeName = getTypeName(q.type || q.questionType || 'single')
    const sectionTitle = `【${typeName}】`
    if (!sectionMap.has(sectionTitle)) {
      sectionMap.set(sectionTitle, { title: sectionTitle, questions: [], score: 0 })
    }
    const section = sectionMap.get(sectionTitle)!
    section.questions.push({ content: q.content || q.questionContent || '', score: q.score || q.fullScore || 0 })
    section.score += q.score || q.fullScore || 0
  })
  return Array.from(sectionMap.values())
}

function getTypeName(type: string): string {
  const map: Record<string, string> = {
    single: '单选题',
    multiple: '多选题',
    judge: '判断题',
    fill: '填空题',
    essay: '简答题',
  }
  return map[type] || type
}

function handleEdit(paper: Paper) {
  router.push(`/teacher/papers/builder?id=${paper.id}`)
}

function handleEditFromPreview() {
  if (currentPreview.value) {
    previewVisible.value = false
    handleEdit(currentPreview.value)
  }
}

async function handlePublish(paper: Paper) {
  try {
    await ElMessageBox.confirm(
      `确定要发布试卷「${displayText(paper.name, `试卷 ${paper.id}`)}」吗？发布后学生将可见。`,
      '确认发布',
      { confirmButtonText: '确认发布', cancelButtonText: '取消', type: 'info' }
    )
    await updatePaper(paper.id, { status: 1 })
    ElMessage.success('试卷已发布')
    paper.status = 'published'
    loadPapers()
  } catch (err) {
    console.error('发布失败', err)
    ElMessage.error('发布失败')
  }
}

function handleCompose(paper: Paper) {
  router.push(`/teacher/papers/compose?id=${paper.id}`)
}

async function handleDelete(paper: Paper) {
  try {
    await deletePaper(paper.id)
    papers.value = papers.value.filter(p => p.id !== paper.id)
    ElMessage.success('试卷已删除')
  } catch (err) {
    console.error('删除试卷失败', err)
    ElMessage.error('删除失败')
  }
}

// 状态映射
function getStatusTagType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    draft: 'warning',
    published: 'success',
  }
  return map[status] || 'info'
}

function getStatusText(status: string): string {
  const map: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
  }
  return map[status] || status
}

function getMethodText(method: string): string {
  const map: Record<string, string> = {
    manual: '手动组卷',
    auto: '智能组卷',
  }
  return map[method] || method
}

// 监听筛选变化
function onFilterChange() {
  loadPapers()
}

onMounted(() => {
  loadPapers()
  loadCourseOptions()
})
</script>
