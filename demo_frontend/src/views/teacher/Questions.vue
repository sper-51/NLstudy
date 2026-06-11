<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">题目管理</h1>
        <p class="text-sm text-gray-500 mt-1">共 {{ totalQuestions }} 道题目</p>
      </div>
      <el-button type="primary" @click="handleAddQuestion">
        <i class="ri-add-line mr-1"></i>新增题目
      </el-button>
    </div>

    <!-- 搜索栏 + 筛选器 -->
    <div class="bg-white rounded-xl p-4 shadow-sm">
      <div class="flex items-center gap-3 flex-wrap">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索题目内容..."
          prefix-icon="Search"
          style="width: 280px"
          clearable
          @input="onFilterChange"
        />
        <el-select v-model="filterType" placeholder="题型筛选" style="width: 130px" clearable @change="onFilterChange">
          <el-option label="单选题" value="single" />
          <el-option label="多选题" value="multiple" />
          <el-option label="判断题" value="judge" />
          <el-option label="填空题" value="fill" />
          <el-option label="简答题" value="essay" />
        </el-select>
        <el-select v-model="filterDifficulty" placeholder="难度筛选" style="width: 120px" clearable @change="onFilterChange">
          <el-option label="简单" value="easy" />
          <el-option label="中等" value="medium" />
          <el-option label="困难" value="hard" />
        </el-select>
        <el-select v-model="filterCourse" placeholder="所属课程" style="width: 180px" clearable @change="onFilterChange">
          <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
        <div class="flex-1"></div>
        <el-button @click="resetFilters">
          <i class="ri-refresh-line mr-1"></i>重置
        </el-button>
      </div>
    </div>

    <!-- 批量操作栏 -->
    <div
      v-if="selectedIds.length > 0"
      class="bg-primary-50 border border-primary-200 rounded-xl px-4 py-3 flex items-center justify-between"
    >
      <span class="text-sm text-primary-700">
        已选择 <strong>{{ selectedIds.length }}</strong> 项
      </span>
      <div class="flex items-center gap-2">
        <el-button size="small" type="danger" plain @click="handleBatchDelete">
          <i class="ri-delete-bin-line mr-1"></i>批量删除
        </el-button>
        <el-button size="small" type="primary" plain @click="handleBatchExport">
          <i class="ri-download-line mr-1"></i>批量导出
        </el-button>
        <el-button size="small" link @click="selectedIds = []">取消选择</el-button>
      </div>
    </div>

    <!-- 题目列表表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <el-table
        :data="filteredQuestions"
        stripe
        style="width: 100%"
        empty-text="暂无题目数据"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="45" />

        <el-table-column prop="type" label="题型" width="90">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small" effect="plain">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="题目内容" min-width="300">
          <template #default="{ row }">
            <div class="flex items-start gap-2">
              <span class="text-sm text-gray-800 line-clamp-2">{{ row.content }}</span>
              <el-tag v-if="row.isFavorite" type="warning" size="small" effect="light" class="flex-shrink-0">
                <i class="ri-star-fill"></i>
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="difficulty" label="难度" width="85">
          <template #default="{ row }">
            <el-tag :type="getDiffTagType(row.difficulty)" size="small" effect="plain">
              {{ getDiffText(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="courseName" label="所属课程" width="150" show-overflow-tooltip />

        <el-table-column prop="score" label="分值" width="65" align="center">
          <template #default="{ row }">
            <span class="font-medium text-gray-700">{{ row.score }}分</span>
          </template>
        </el-table-column>

        <el-table-column prop="usedCount" label="使用次数" width="85" align="center" sortable />

        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-1">
              <el-button link type="primary" size="small" @click="handlePreview(row)">
                <i class="ri-eye-line mr-1"></i>预览
              </el-button>
              <el-button link type="primary" size="small" @click="handleEdit(row)">
                <i class="ri-edit-line mr-1"></i>编辑
              </el-button>
              <el-button
                link
                :type="row.isFavorite ? 'warning' : 'default'"
                size="small"
                @click="handleToggleFavorite(row)"
              >
                <i :class="row.isFavorite ? 'ri-star-fill' : 'ri-star-line'" class="mr-1"></i>
                {{ row.isFavorite ? '取消收藏' : '收藏' }}
              </el-button>
              <el-popconfirm title="确定删除此题？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button link type="danger" size="small">
                    <i class="ri-delete-bin-line mr-1"></i>删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="p-4 flex items-center justify-between border-t border-gray-100">
        <span class="text-xs text-gray-400">共 {{ filteredQuestions.length }} 条记录</span>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredQuestions.length"
          layout="sizes, prev, pager, next"
          size="small"
          @current-change="onPageChange"
        />
      </div>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="题目预览" width="600px" destroy-on-close>
      <div v-if="currentPreview" class="space-y-4">
        <div class="flex items-center gap-2 mb-3">
          <el-tag :type="getTypeTagType(currentPreview.type)" effect="plain">{{ getTypeName(currentPreview.type) }}</el-tag>
          <el-tag :type="getDiffTagType(currentPreview.difficulty)" effect="plain">{{ getDiffText(currentPreview.difficulty) }}</el-tag>
          <span class="text-sm text-gray-500">{{ currentPreview.score }}分</span>
        </div>
        <div class="p-4 bg-gray-50 rounded-lg">
          <p class="text-sm text-gray-800 leading-relaxed">{{ currentPreview.content }}</p>
        </div>
        <div v-if="currentPreview.options && currentPreview.options.length > 0" class="space-y-2">
          <div v-for="(opt, idx) in currentPreview.options" :key="idx" class="flex items-center gap-2 p-2 rounded-lg" :class="opt.isCorrect ? 'bg-green-50 text-green-700' : 'bg-gray-50'">
            <span class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0" :class="opt.isCorrect ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-600'">
              {{ ['A', 'B', 'C', 'D'][idx] }}
            </span>
            <span class="text-sm">{{ opt.text }}</span>
            <i v-if="opt.isCorrect" class="ri-check-line ml-auto text-green-500"></i>
          </div>
        </div>
        <div class="border-t pt-3 space-y-2">
          <div><span class="text-sm font-medium text-gray-600">参考答案：</span><span class="text-sm text-gray-800">{{ currentPreview.answer || '-' }}</span></div>
          <div><span class="text-sm font-medium text-gray-600">解析：</span><span class="text-sm text-gray-600">{{ currentPreview.explanation || '暂无解析' }}</span></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestions, deleteQuestion, favoriteQuestion, getTeacherCourses } from '@/api/teacher'
import request from '@/api/request'

const router = useRouter()

// 搜索和筛选
const searchKeyword = ref('')
const filterType = ref('')
const filterDifficulty = ref('')
const filterCourse = ref('')

const courseOptions = ref<Array<{ label: string; value: string | number }>>([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)

// 加载状态
const loading = ref(false)

// 批量选择
const selectedIds = ref<number[]>([])

// 预览弹窗
const previewVisible = ref(false)
const currentPreview = ref<any>(null)

// 题目数据类型
interface QuestionItem {
  id: number
  type: string
  typeName?: string
  content: string
  difficulty: string
  courseId?: number
  courseName: string
  score: number
  usedCount: number
  status: string
  isFavorite: boolean
  options?: Array<{ label?: string; content?: string; text?: string; isCorrect?: boolean }>
  answer?: string
  explanation?: string
  analysis?: string
}

// 题目列表
const questions = ref<QuestionItem[]>([])

const totalQuestions = computed(() => totalCount.value)

// 筛选后的列表（前端分页）
const filteredQuestions = computed(() => {
  let list = questions.value
  const search = searchKeyword.value.toLowerCase()
  if (search) {
    list = list.filter(q => q.content.toLowerCase().includes(search))
  }
  if (filterType.value) {
    list = list.filter(q => q.type === filterType.value)
  }
  if (filterDifficulty.value) {
    list = list.filter(q => q.difficulty === filterDifficulty.value)
  }
  if (filterCourse.value) {
    list = list.filter(q => q.courseId?.toString() === filterCourse.value.toString() || q.courseName.includes(filterCourse.value.toString()))
  }
  return list
})

// 类型映射：前端简化类型 -> 后端类型
function mapTypeToBackend(type: string): string {
  const map: Record<string, string> = {
    single: 'single_choice',
    multiple: 'multi_choice',
    judge: 'true_false',
    fill: 'fill_blank',
    essay: 'essay',
  }
  return map[type] || type
}

// 加载题目列表
async function loadQuestions() {
  loading.value = true
  try {
    const res = await getQuestions({
      courseId: filterCourse.value || undefined,
      type: filterType.value ? mapTypeToBackend(filterType.value) : undefined,
      keyword: searchKeyword.value || undefined,
      page: currentPage.value,
      pageSize: pageSize.value,
    })
    const resData = res as any
    const data = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || [])
    questions.value = data.map((q: any) => ({
      ...q,
      type: q.type || q.questionType || 'single',
      typeName: q.typeName || q.typeName,
      content: q.content || q.questionContent || '',
      difficulty: q.difficulty || 'medium',
      courseName: q.courseName || '',
      score: q.score || q.fullScore || 0,
      usedCount: q.usageCount || 0,
      isFavorite: q.isFavorited || false,
      options: q.options?.map((o: any, i: number) => ({
        label: ['A', 'B', 'C', 'D', 'E', 'F'][i],
        content: o.content || o.text || '',
        text: o.content || o.text || '',
        isCorrect: o.isCorrect || false,
      })) || [],
      answer: Array.isArray(q.answer) ? q.answer.join(',') : (q.answer || ''),
      explanation: q.explanation || q.analysis || '',
    }))
    // 获取总数
    if (resData?.pagination) {
      totalCount.value = resData.pagination.total
    } else {
      totalCount.value = questions.value.length
    }
  } catch (err) {
    console.error('加载题目列表失败', err)
    questions.value = []
    totalCount.value = 0
  } finally {
    loading.value = false
  }
}

// 加载课程列表（用于筛选）
async function loadCourseOptions() {
  try {
    const res = await getTeacherCourses({ pageSize: 100 })
    const resData = res as any
    const courses = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || resData?.data || [])
    courseOptions.value = courses.map((c: any) => ({
      label: c.name,
      value: c.id,
    }))
  } catch (err) {
    console.error('加载课程列表失败', err)
  }
}

// 重置筛选
function resetFilters() {
  searchKeyword.value = ''
  filterType.value = ''
  filterDifficulty.value = ''
  filterCourse.value = ''
  currentPage.value = 1
  loadQuestions()
}

function handleSelectionChange(rows: QuestionItem[]) {
  selectedIds.value = rows.map(r => r.id)
}

function handleAddQuestion() {
  router.push('/teacher/questions/new')
}

function handlePreview(row: QuestionItem) {
  currentPreview.value = row
  previewVisible.value = true
}

function handleEdit(row: QuestionItem) {
  router.push(`/teacher/questions/new?id=${row.id}`)
}

async function handleToggleFavorite(row: QuestionItem) {
  try {
    await favoriteQuestion(row.id)
    row.isFavorite = !row.isFavorite
    ElMessage.success(row.isFavorite ? '已添加到收藏' : '已取消收藏')
  } catch (err) {
    console.error('收藏操作失败', err)
    // 前端乐观更新
    row.isFavorite = !row.isFavorite
  }
}

async function handleDelete(row: QuestionItem) {
  try {
    await deleteQuestion(row.id)
    questions.value = questions.value.filter(q => q.id !== row.id)
    totalCount.value--
    ElMessage.success('题目已删除')
  } catch (err) {
    console.error('删除题目失败', err)
    ElMessage.error('删除失败')
  }
}

async function handleBatchDelete() {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 道题目吗？此操作不可撤销。`,
      '批量删除确认',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' }
    )
    // 逐个删除
    for (const id of selectedIds.value) {
      try {
        await deleteQuestion(id)
      } catch (e) {
        console.error(`删除题目${id}失败`, e)
      }
    }
    ElMessage.success(`已删除 ${selectedIds.value.length} 道题目`)
    selectedIds.value = []
    loadQuestions()
  } catch {
    // 用户取消
  }
}

async function handleBatchExport() {
  try {
    const params: Record<string, any> = { format: 'xlsx' }
    if (selectedIds.value.length > 0) {
      params.ids = selectedIds.value.join(',')
    }
    const res = await request.get('/teacher/questions/export', {
      params,
      responseType: 'blob',
    })
    const blob = new Blob([res as any], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = selectedIds.value.length > 0 ? `选中题目_${selectedIds.value.length}道.xlsx` : '题目列表.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    ElMessage.success(`成功导出 ${selectedIds.value.length || filteredQuestions.value.length} 道题目`)
  } catch (err) {
    console.error('导出失败', err)
    ElMessage.error('导出失败，请重试')
  }
}

// 监听筛选变化
function onFilterChange() {
  currentPage.value = 1
  loadQuestions()
}

// 监听分页变化
function onPageChange() {
  loadQuestions()
}

onMounted(() => {
  loadQuestions()
  loadCourseOptions()
})

// 类型映射函数
function getTypeTagType(type: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    single_choice: '',
    multi_choice: 'success',
    true_false: 'warning',
    fill_blank: 'info',
    essay: 'danger',
    single: '',
    multiple: 'success',
    judge: 'warning',
    fill: 'info',
  }
  return map[type] || 'info'
}

function getTypeName(type: string): string {
  const map: Record<string, string> = {
    single_choice: '单选',
    multi_choice: '多选',
    true_false: '判断',
    fill_blank: '填空',
    essay: '简答',
    single: '单选',
    multiple: '多选',
    judge: '判断',
    fill: '填空',
  }
  return map[type] || type
}

function getDiffTagType(diff: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger',
  }
  return map[diff] || 'info'
}

function getDiffText(diff: string): string {
  const map: Record<string, string> = {
    easy: '简单',
    medium: '中等',
    hard: '困难',
  }
  return map[diff] || diff
}
</script>
