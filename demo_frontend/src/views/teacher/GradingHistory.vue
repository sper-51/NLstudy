<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">批改历史</h1>
        <p class="text-sm text-gray-500 mt-1">查看所有批改记录，追溯批改详情</p>
      </div>
      <div class="flex items-center gap-3">
        <!-- 统计信息 -->
        <div class="flex items-center gap-3 mr-2">
          <div class="bg-white rounded-lg px-4 py-2 shadow-sm border border-gray-100">
            <span class="text-xs text-gray-400">今日批改</span>
            <span class="ml-2 text-lg font-bold text-primary-500">{{ stats.todayCount }}</span>
          </div>
          <div class="bg-white rounded-lg px-4 py-2 shadow-sm border border-gray-100">
            <span class="text-xs text-gray-400">本周批改</span>
            <span class="ml-2 text-lg font-bold text-green-500">{{ stats.weekCount }}</span>
          </div>
          <div class="bg-white rounded-lg px-4 py-2 shadow-sm border border-gray-100">
            <span class="text-xs text-gray-400">平均时长</span>
            <span class="ml-2 text-lg font-bold text-blue-500">{{ stats.avgDuration }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="bg-white rounded-xl p-4 shadow-sm flex items-center gap-3">
      <el-select v-model="filterExam" placeholder="按考试筛选" style="width: 200px" size="default" clearable>
        <el-option v-for="e in examOptions" :key="e.value" :label="e.label" :value="e.value" />
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 260px"
        size="default"
        value-format="YYYY-MM-DD"
      />
      <el-select v-model="filterTeacher" placeholder="批改教师" style="width: 160px" size="default" clearable>
        <el-option v-for="t in teacherOptions" :key="t.value" :label="t.label" :value="t.value" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <i class="ri-search-line mr-1"></i>查询
      </el-button>
      <el-button @click="resetFilter">
        <i class="ri-refresh-line mr-1"></i>重置
      </el-button>
    </div>

    <!-- 批改记录表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <el-table :data="filteredRecords" stripe style="width: 100%" :default-sort="{ prop: 'gradedAt', order: 'descending' }">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="p-5 bg-gray-50/80 mx-4 my-2 rounded-lg border border-gray-100">
              <h4 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
                <i class="ri-chat-quote-line text-primary-500"></i>完整评语
              </h4>
              <p class="text-sm text-gray-600 leading-relaxed whitespace-pre-wrap">{{ row.fullComment || row.comment }}</p>
              <div class="mt-3 pt-3 border-t border-gray-200 flex items-center gap-6 text-xs text-gray-400">
                <span><i class="ri-time-line mr-1"></i>批改耗时：{{ row.duration }}分钟</span>
                <span><i class="ri-file-text-line mr-1"></i>题目类型：{{ row.typeName }}</span>
                <span><i class="ri-user-line mr-1"></i>批改教师：{{ row.teacherName }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="学生姓名" prop="studentName" min-width="100" />
        <el-table-column label="考试名称" prop="examName" min-width="180" show-overflow-tooltip />
        <el-table-column label="题目类型" prop="typeName" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.questionType)" size="small" effect="plain">{{ row.typeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="得分" prop="score" width="90" sortable>
          <template #default="{ row }">
            <span class="font-semibold" :class="row.score >= row.maxScore * 0.8 ? 'text-green-600' : row.score >= row.maxScore * 0.6 ? 'text-orange-500' : 'text-red-500'">
              {{ row.score }} / {{ row.maxScore }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="评语摘要" prop="comment" min-width="200" show-overflow-tooltip />
        <el-table-column label="批改时间" prop="gradedAt" width="170" sortable />
        <el-table-column label="批改教师" prop="teacherName" width="100" />

        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">
              <i class="ri-eye-line"></i>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="p-4 flex items-center justify-between border-t border-gray-100">
        <span class="text-xs text-gray-400">共 {{ filteredRecords.length }} 条记录</span>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredRecords.length"
          layout="sizes, prev, pager, next"
          size="small"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getGradingHistory } from '@/api/teacher'

// ========== 统计 ==========
const stats = ref({
  todayCount: 0,
  weekCount: 0,
  avgDuration: '-',
})

// ========== 筛选 ==========
const filterExam = ref('')
const dateRange = ref<[string, string] | null>(null)
const filterTeacher = ref('')

const examOptions = ref<{ label: string; value: string }[]>([])
const teacherOptions = ref<{ label: string; value: string }[]>([])

// ========== 分页 ==========
const currentPage = ref(1)
const pageSize = ref(10)

// ========== 批改记录数据 ==========
interface GradingRecord {
  id: number
  studentName?: string
  studentId?: number
  examId?: number
  examName?: string
  questionId?: number
  questionType?: string
  typeName?: string
  score?: number
  fullScore?: number
  maxScore?: number
  comment?: string
  fullComment?: string
  gradedAt?: string
  gradeTime?: string
  teacherName?: string
  duration?: number
  status?: string
}

const gradingRecords = ref<GradingRecord[]>([])
const loading = ref(false)

// 加载批改历史数据
async function loadHistory() {
  loading.value = true
  try {
    const res = await getGradingHistory({ pageSize: 100 })
    const resData = res as any
    const list = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || [])
    gradingRecords.value = list.map((item: any) => ({
      id: item.id || item.taskId,
      studentName: item.studentName || '-',
      studentId: item.studentId,
      examId: item.examId,
      examName: item.examName || '-',
      questionId: item.questionId,
      questionType: item.questionType || 'essay',
      typeName: item.typeName || (item.questionType === 'single_choice' ? '单选题' : item.questionType === 'multiple_choice' ? '多选题' : item.questionType === 'true_false' ? '判断题' : item.questionType === 'fill_blank' ? '填空题' : item.questionType === 'essay' ? '简答题' : '主观题'),
      score: item.score ?? 0,
      fullScore: item.fullScore ?? item.maxScore ?? 0,
      maxScore: item.fullScore ?? item.maxScore ?? 100,
      comment: item.comment || '',
      fullComment: item.comment || '',
      gradedAt: item.gradeTime || item.createTime || item.gradedAt || '',
      teacherName: item.teacherName || '',
      duration: item.duration || 0,
      status: item.status || 'completed',
    }))
    // 统计信息
    stats.value.todayCount = gradingRecords.value.length
    stats.value.weekCount = gradingRecords.value.length
  } catch (err) {
    console.error('加载批改历史失败', err)
    gradingRecords.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadHistory()
})

// ========== 筛选逻辑 ==========
const filteredRecords = computed(() => {
  let list = gradingRecords.value
  if (filterExam.value) {
    const examLabel = examOptions.value.find(e => e.value === filterExam.value)?.label || ''
    list = list.filter(item => item.examName === examLabel)
  }
  if (filterTeacher.value) {
    const teacherLabel = teacherOptions.value.find(t => t.value === filterTeacher.value)?.label || ''
    list = list.filter(item => item.teacherName === teacherLabel)
  }
  if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
    list = list.filter(item => {
      const date = (item.gradedAt || '').split(' ')[0]
      return date >= dateRange.value![0] && date <= dateRange.value![1]
    })
  }
  return list
})

// ========== 操作 ==========
function handleSearch() {
  // 前端筛选已通过 computed 实现
  ElMessage.success('筛选条件已应用')
}

function resetFilter() {
  filterExam.value = ''
  dateRange.value = null
  filterTeacher.value = ''
  currentPage.value = 1
}

function viewDetail(row: GradingRecord) {
  ElMessage.info(`查看 ${row.studentName} 的批改详情（ID: ${row.id}）`)
}

function getTypeTagType(type: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    essay: 'warning',
    code: 'danger',
    fill: 'info',
    single_choice: 'success',
    multiple_choice: 'info',
    true_false: '',
  }
  return map[type] || 'info'
}
</script>
