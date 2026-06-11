<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">学生管理</h1>
        <p class="text-sm text-gray-500 mt-1">管理课程选课名单，导入/导出学生信息</p>
      </div>
      <div class="flex items-center gap-3">
        <span class="text-sm text-gray-500">共 <span class="font-semibold text-primary-600">{{ filteredStudents.length }}</span> 名学生</span>
      </div>
    </div>

    <!-- 筛选 + 操作栏 -->
    <div class="bg-white rounded-xl p-4 shadow-sm flex items-center justify-between gap-4">
      <div class="flex items-center gap-3">
        <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 220px" size="default" @change="handleCourseChange">
          <el-option v-for="c in courseOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学号或姓名..."
          style="width: 240px"
          size="default"
          clearable
          :prefix-icon="'ri-search-line'"
          @input="handleSearch"
        />
      </div>
      <div class="flex items-center gap-2">
        <el-button type="primary" @click="showImportDialog = true">
          <i class="ri-upload-line mr-1"></i>导入学生
        </el-button>
        <el-button @click="handleExport" :disabled="!selectedRows.length">
          <i class="ri-download-line mr-1"></i>导出名单
        </el-button>
        <el-button type="danger" plain :disabled="!selectedRows.length" @click="handleBatchRemove">
          <i class="ri-delete-bin-line mr-1"></i>批量移除 ({{ selectedRows.length }})
        </el-button>
      </div>
    </div>

    <!-- 学生列表表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden" v-loading="loading">
      <el-table
        ref="tableRef"
        :data="paginatedStudents"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column label="学号" prop="studentId" min-width="130" sortable />
        <el-table-column label="姓名" prop="realName" min-width="100" sortable>
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <div class="w-7 h-7 rounded-full bg-primary-100 text-primary-600 text-xs font-bold flex items-center justify-center">
                {{ (row.realName || row.name || '?').charAt(0) }}
              </div>
              <span class="font-medium text-gray-800">{{ row.realName || row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="班级" prop="className" min-width="140" />
        <el-table-column label="加入课程时间" prop="selectTime" min-width="170" sortable>
          <template #default="{ row }">
            {{ formatTime(row.selectTime || row.joinTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '在读' : '已退课' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewGrades(row)">
              <i class="ri-bar-chart-box-line mr-1"></i>查看成绩
            </el-button>
            <el-popconfirm title="确定要将该学生从本课程中移除吗？" confirm-button-text="确认" cancel-button-text="取消" @confirm="removeStudent(row)">
              <template #reference>
                <el-button link type="danger" size="small" :disabled="row.status !== 1">
                  <i class="ri-user-unfollow-line"></i>
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="p-4 flex items-center justify-between border-t border-gray-100">
        <span class="text-xs text-gray-400">共 {{ filteredStudents.length }} 条记录</span>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredStudents.length"
          layout="sizes, prev, pager, next"
          size="small"
        />
      </div>
    </div>

    <!-- 导入弹窗 -->
    <el-dialog v-model="showImportDialog" title="导入学生" width="520px" destroy-on-close>
      <div class="space-y-4">
        <!-- 模板下载 -->
        <div class="bg-blue-50 rounded-lg p-4 flex items-center justify-between">
          <div class="flex items-center gap-2 text-sm text-blue-700">
            <i class="ri-file-excel-2-line text-lg"></i>
            <span>请先下载导入模板，按格式填写后上传</span>
          </div>
          <el-button type="primary" link size="small" @click="downloadTemplate">
            <i class="ri-download-line mr-1"></i>下载模板
          </el-button>
        </div>

        <!-- 上传区域 -->
        <el-upload
          drag
          action="#"
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls,.csv"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          class="w-full"
        >
          <div class="py-6">
            <i class="ri-upload-cloud-2-line text-4xl text-gray-300"></i>
            <p class="mt-2 text-sm text-gray-500">将Excel文件拖到此处，或<span class="text-primary-500 cursor-pointer">点击上传</span></p>
            <p class="text-xs text-gray-400 mt-1">支持 .xlsx / .xls / .csv 格式，单次不超过500条</p>
          </div>
        </el-upload>

        <!-- 预览区域 -->
        <div v-if="previewData.length > 0" class="border rounded-lg overflow-hidden">
          <div class="bg-gray-50 px-4 py-2 text-sm font-medium text-gray-700 border-b">
            <i class="ri-eye-line mr-1"></i>数据预览（前5条）
          </div>
          <el-table :data="previewData.slice(0, 5)" size="small" max-height="200">
            <el-table-column label="学号" prop="studentId" width="120" />
            <el-table-column label="姓名" prop="name" width="100" />
            <el-table-column label="班级" prop="className" />
          </el-table>
          <div class="px-4 py-2 bg-gray-50 text-xs text-gray-400 border-t">
            共解析到 {{ previewData.length }} 条数据
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" :disabled="!uploadFile" :loading="importing" @click="handleImport">
          确认导入
        </el-button>
      </template>
    </el-dialog>

    <!-- 成绩详情弹窗 -->
    <el-dialog v-model="showGradeDialog" :title="`${currentStudent?.realName || currentStudent?.name} - 成绩详情`" width="600px" destroy-on-close>
      <div v-if="currentStudent" class="space-y-4">
        <div class="grid grid-cols-3 gap-4">
          <div class="bg-gray-50 rounded-lg p-3 text-center">
            <div class="text-xs text-gray-400 mb-1">参考考试数</div>
            <div class="text-xl font-bold text-primary-600">{{ currentStudent.examCount || 0 }}</div>
          </div>
          <div class="bg-gray-50 rounded-lg p-3 text-center">
            <div class="text-xs text-gray-400 mb-1">平均分</div>
            <div class="text-xl font-bold text-green-600">{{ currentStudent.avgScore || '—' }}</div>
          </div>
          <div class="bg-gray-50 rounded-lg p-3 text-center">
            <div class="text-xs text-gray-400 mb-1">排名</div>
            <div class="text-xl font-bold text-orange-500">{{ currentStudent.rank || '—' }}/{{ currentStudent.totalStudents || '—' }}</div>
          </div>
        </div>
        <el-table :data="currentStudent.gradeList || []" size="small" stripe>
          <el-table-column label="考试名称" prop="examName" show-overflow-tooltip />
          <el-table-column label="得分" prop="score" width="80" />
          <el-table-column label="满分" prop="maxScore" width="70" />
          <el-table-column label="排名" prop="rank" width="70" />
          <el-table-column label="批改时间" prop="gradedAt" width="160" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile, UploadRawFile } from 'element-plus'
import { getTeacherCourses, getCourseStudents, removeCourseStudent, exportGrades } from '@/api/teacher'
import request from '@/api/request'

// ========== 课程选择 ==========
const selectedCourse = ref<number>(1)
const courseOptions = ref<Array<{ id: number; name: string }>>([])
const searchKeyword = ref('')
const loading = ref(false)

// ========== 分页 ==========
const currentPage = ref(1)
const pageSize = ref(10)

// ========== 学生数据 ==========
interface Student {
  studentId: number | string
  username?: string
  realName?: string
  name?: string
  className?: string
  selectTime?: string
  joinTime?: string
  status: number
  examCount?: number
  avgScore?: string
  rank?: number
  totalStudents?: number
  gradeList?: Array<{
    examName: string
    score: number
    maxScore: number
    rank: number
    gradedAt: string
  }>
}

const students = ref<Student[]>([])

// ========== 加载课程列表 ==========
async function loadCourses() {
  try {
    const res = await getTeacherCourses({ pageSize: 100 })
    // 兼容多种返回格式：axios拦截器已解包一层(res.data)
    const resData = res as any
    const list = Array.isArray(resData) ? resData : (resData?.list || resData?.data?.list || [])
    courseOptions.value = list.map((c: any) => ({
      id: c.id,
      name: c.name
    }))
    if (courseOptions.value.length > 0) {
      selectedCourse.value = courseOptions.value[0].id
      loadStudents()
    }
  } catch (err) {
    console.error('加载课程列表失败', err)
  }
}

// ========== 加载学生列表 ==========
async function loadStudents() {
  loading.value = true
  try {
    const res = await getCourseStudents(selectedCourse.value, {
      page: 1,
      pageSize: 100,
      keyword: searchKeyword.value || undefined
    })
    const resData = res as any
    const data = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || [])
    students.value = data.map((s: any) => ({
      studentId: s.studentId,
      username: s.username,
      realName: s.realName || s.name,
      selectTime: s.selectTime || s.joinTime,
      status: s.status || 1
    }))
  } catch (err) {
    console.error('加载学生列表失败', err)
    ElMessage.error('加载学生列表失败')
  } finally {
    loading.value = false
  }
}

// ========== 筛选 + 分页 ==========
const filteredStudents = computed(() => {
  let list = students.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(s =>
      String(s.studentId).toLowerCase().includes(keyword) ||
      (s.realName || s.name || '').toLowerCase().includes(keyword)
    )
  }
  return list
})

const paginatedStudents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredStudents.value.slice(start, start + pageSize.value)
})

// ========== 事件处理 ==========
function handleCourseChange() {
  currentPage.value = 1
  loadStudents()
}

function handleSearch() {
  currentPage.value = 1
  // 防抖处理
}

// ========== 选择操作 ==========
const selectedRows = ref<Student[]>([])

function handleSelectionChange(rows: Student[]) {
  selectedRows.value = rows
}

// ========== 移除学生 ==========
async function removeStudent(row: Student) {
  try {
    await removeCourseStudent(selectedCourse.value, Number(row.studentId))
    ElMessage.success(`已将 ${row.realName || row.name} 从本课程移除`)
    loadStudents()
  } catch (err) {
    console.error('移除学生失败', err)
    ElMessage.error('移除学生失败')
  }
}

async function handleBatchRemove() {
  await ElMessageBox.confirm(
    `确定要将选中的 ${selectedRows.value.length} 名学生从本课程中移除吗？`,
    '批量移除确认',
    { confirmButtonText: '确认移除', cancelButtonText: '取消', type: 'warning' }
  )
  for (const row of selectedRows.value) {
    try {
      await removeCourseStudent(selectedCourse.value, Number(row.studentId))
    } catch (err) {
      console.error(`移除学生 ${row.realName} 失败`, err)
    }
  }
  ElMessage.success(`已成功移除 ${selectedRows.value.length} 名学生`)
  selectedRows.value = []
  loadStudents()
}

// ========== 导出 ==========
async function handleExport() {
  try {
    const res = await exportGrades({
      courseId: selectedCourse.value,
      format: 'xlsx',
    })
    const blob = new Blob([res as any], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `学生名单_${selectedCourse.value}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (err) {
    console.error('导出失败', err)
    ElMessage.error('导出失败，请重试')
  }
}

// ========== 导入相关 ==========
const showImportDialog = ref(false)
const uploadFile = ref<UploadRawFile | null>(null)
const importing = ref(false)
const previewData = ref<Array<{ studentId: string; name: string; className: string }>>([])

function handleFileChange(file: UploadFile) {
  uploadFile.value = file.raw || null
  // 显示文件基本信息，实际解析需后端支持
  previewData.value = file.raw ? [
    { studentId: '(待解析)', name: `文件: ${file.name}`, className: '—' },
  ] : []
}

function handleFileRemove() {
  uploadFile.value = null
  previewData.value = []
}

async function downloadTemplate() {
  try {
    const res = await request.get('/teacher/students/template', {
      responseType: 'blob',
    })
    const blob = new Blob([res as any], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '学生导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    ElMessage.success('模板下载成功')
  } catch (err) {
    console.warn('下载模板失败，尝试备用方式', err)
    // 备用：提示用户联系管理员获取模板
    ElMessage.warning('模板下载失败，请联系管理员获取导入模板')
  }
}

async function handleImport() {
  if (!uploadFile.value) {
    ElMessage.warning('请先选择要导入的文件')
    return
  }
  importing.value = true
  try {
    const formData = new FormData()
    formData.append('file', uploadFile.value)
    formData.append('courseId', String(selectedCourse.value))
    await request.post('/teacher/students/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    ElMessage.success('学生导入成功')
    showImportDialog.value = false
    uploadFile.value = null
    previewData.value = []
    loadStudents()
  } catch (err: any) {
    console.error('导入失败', err)
    const msg = err?.response?.data?.message || err?.message || '导入失败，请检查文件格式'
    ElMessage.error(msg)
  } finally {
    importing.value = false
  }
}

// ========== 成绩详情 ==========
const showGradeDialog = ref(false)
const currentStudent = ref<Student | null>(null)

function viewGrades(row: Student) {
  currentStudent.value = row
  showGradeDialog.value = true
}

// ========== 工具函数 ==========
function formatTime(time: string | undefined): string {
  if (!time) return '—'
  try {
    const d = new Date(time)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
  } catch {
    return time
  }
}

// ========== 初始化 ==========
onMounted(() => {
  loadCourses()
})
</script>
