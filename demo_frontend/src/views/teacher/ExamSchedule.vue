<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">考试安排</h1>
        <p class="text-sm text-gray-500 mt-1">管理所有考试的发布、监控与安排</p>
      </div>
      <el-button type="primary" @click="openPublishDialog">
        <i class="ri-add-line mr-1"></i>发布新考试
      </el-button>
    </div>

    <!-- 状态筛选 Tab -->
    <div class="bg-white rounded-xl p-1 shadow-sm inline-flex">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        @click="activeStatus = tab.value"
        :class="[
          'px-5 py-2 rounded-lg text-sm font-medium transition-all cursor-pointer',
          activeStatus === tab.value
            ? 'bg-primary-500 text-white shadow-sm'
            : 'text-gray-600 hover:text-primary-500 hover:bg-primary-50'
        ]"
      >
        {{ tab.label }}
        <span v-if="tab.count !== undefined" class="ml-1 text-xs opacity-80">({{ tab.count }})</span>
      </button>
    </div>

    <!-- 考试列表 -->
    <div class="space-y-4">
      <div
        v-for="exam in filteredExams"
        :key="exam.id"
        class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-all border border-gray-100 hover:border-primary-200"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <!-- 头部信息 -->
            <div class="flex items-center gap-3 mb-3">
              <h3 class="text-base font-semibold text-gray-800">{{ exam.name }}</h3>
              <el-tag :type="getStatusType(exam.status)" size="small" effect="dark">
                {{ getStatusLabel(exam.status) }}
              </el-tag>
              <el-tag v-if="exam.status === 'ongoing'" type="danger" size="small" effect="light" class="animate-pulse">
                <i class="ri-time-line mr-1"></i>进行中
              </el-tag>
            </div>

            <!-- 关联信息 -->
            <div class="flex items-center gap-5 text-sm text-gray-500 mb-3">
              <span class="flex items-center gap-1">
                <i class="ri-book-line text-primary-400"></i>{{ exam.courseName }}
              </span>
              <span class="flex items-center gap-1">
                <i class="ri-file-text-line text-primary-400"></i>{{ exam.paperName }}
              </span>
              <span class="flex items-center gap-1">
                <i class="ri-time-line text-primary-400"></i>{{ exam.startTime }} ~ {{ exam.endTime }}
              </span>
              <span class="flex items-center gap-1">
                <i class="ri-hourglass-line text-primary-400"></i>时长 {{ exam.duration }} 分钟
              </span>
            </div>

            <!-- 参与统计 -->
            <div class="flex items-center gap-6">
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 rounded-lg bg-blue-50 flex items-center justify-center">
                  <i class="ri-team-line text-blue-500 text-sm"></i>
                </div>
                <div>
                  <div class="text-sm font-semibold text-gray-800">{{ exam.totalStudents }}</div>
                  <div class="text-xs text-gray-400">应考人数</div>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 rounded-lg bg-green-50 flex items-center justify-center">
                  <i class="ri-check-double-line text-green-500 text-sm"></i>
                </div>
                <div>
                  <div class="text-sm font-semibold text-gray-800">{{ exam.submittedCount }}</div>
                  <div class="text-xs text-gray-400">已交卷</div>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <div class="w-8 h-8 rounded-lg bg-orange-50 flex items-center justify-center">
                  <i class="ri-loader-4-line text-orange-500 text-sm"></i>
                </div>
                <div>
                  <div class="text-sm font-semibold text-gray-800">{{ exam.totalStudents - exam.submittedCount }}</div>
                  <div class="text-xs text-gray-400">未交卷</div>
                </div>
              </div>
              <!-- 进度条 -->
              <div class="flex-1 max-w-xs">
                <div class="flex items-center justify-between text-xs text-gray-400 mb-1">
                  <span>交卷进度</span>
                  <span>{{ exam.submissionRate }}%</span>
                </div>
                <el-progress :percentage="exam.submissionRate" :stroke-width="6" :show-text="false" />
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex flex-col gap-2 ml-4">
            <el-button size="small" @click="handleEdit(exam)">
              <i class="ri-edit-line mr-1"></i>编辑
            </el-button>
            <el-button v-if="['draft', 'published'].includes(exam.status)" size="small" type="danger" plain @click="handleCancel(exam)">
              <i class="ri-close-circle-line mr-1"></i>取消考试
            </el-button>
            <el-button v-if="exam.status === 'ongoing'" size="small" type="primary" plain @click="handleMonitor(exam)">
              <i class="ri-eye-line mr-1"></i>查看监控
            </el-button>
            <el-button v-if="exam.status === 'ongoing'" size="small" type="warning" plain @click="handleForceSubmit(exam)">
              <i class="ri-stop-circle-line mr-1"></i>结束并结算
            </el-button>
            <el-button v-if="exam.status === 'finished'" size="small" type="info" plain @click="handleMakeup(exam)">
              <i class="ri-refresh-line mr-1"></i>安排补考
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredExams.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-400 bg-white rounded-xl">
        <i class="ri-calendar-schedule-line text-5xl mb-3 opacity-50"></i>
        <p class="text-lg font-medium">暂无考试记录</p>
        <p class="text-sm mt-1">点击右上角按钮发布新考试</p>
        <el-button type="primary" class="mt-4" @click="openPublishDialog">
          <i class="ri-add-line mr-1"></i>发布新考试
        </el-button>
      </div>
    </div>

    <!-- 发布考试弹窗 -->
    <el-dialog v-model="publishDialogVisible" title="发布新考试" width="720px" destroy-on-close>
      <div class="space-y-5">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>填写考试基本信息，选择参与学生后即可发布考试</template>
        </el-alert>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">考试名称 <span class="text-danger-500">*</span></label>
            <el-input v-model="publishForm.name" placeholder="例如：2024秋季学期期末考试" />
          </div>
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">选择试卷 <span class="text-danger-500">*</span></label>
            <el-select v-model="publishForm.paperId" placeholder="请选择试卷" style="width: 100%">
              <el-option v-for="p in paperOptions" :key="p.value" :label="p.label" :value="p.value" />
            </el-select>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">开始时间 <span class="text-danger-500">*</span></label>
            <el-date-picker v-model="publishForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
          </div>
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">结束时间 <span class="text-danger-500">*</span></label>
            <el-date-picker v-model="publishForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">所属课程</label>
            <el-select v-model="publishForm.courseId" placeholder="选择课程" style="width: 100%">
              <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
            </el-select>
          </div>
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">考试时长（分钟）</label>
            <el-input-number v-model="publishForm.duration" :min="30" :max="180" :step="15" style="width: 100%" />
          </div>
        </div>

        <!-- 参与学生 -->
        <div>
          <label class="text-sm text-gray-700 mb-1.5 block font-medium">参与学生 <span class="text-danger-500">*</span></label>
          <div class="mb-2">
            <el-radio-group v-model="publishForm.studentMode" size="small">
              <el-radio-button value="all">全部选课学生</el-radio-button>
              <el-radio-button value="selected">指定学生</el-radio-button>
            </el-radio-group>
          </div>
          <div v-if="publishForm.studentMode === 'selected'" class="border border-gray-200 rounded-lg p-3 max-h-48 overflow-auto">
            <el-input v-model="studentSearchKey" placeholder="搜索学生..." size="small" prefix-icon="Search" class="mb-2" clearable />
            <div class="space-y-1.5">
              <div v-for="s in filteredStudentList" :key="s.id" class="flex items-center gap-2 py-1 px-2 hover:bg-gray-50 rounded">
                <el-checkbox v-model="publishForm.selectedStudents" :value="s.id" />
                <span class="text-sm text-gray-700">{{ s.name }}</span>
                <span class="text-xs text-gray-400 ml-1">({{ s.studentId }})</span>
              </div>
            </div>
          </div>
          <div v-else class="text-sm text-gray-500 bg-gray-50 rounded-lg p-3">
            <i class="ri-information-line mr-1"></i>将从「{{ publishForm.courseId ? courseOptions.find(c => c.value === publishForm.courseId)?.label : '所选课程' }}」的所有选课学生中自动导入
          </div>
        </div>

        <!-- 其他参数 -->
        <div class="grid grid-cols-3 gap-4">
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">防作弊模式</label>
            <el-switch v-model="publishForm.antiCheat" active-text="开启" inactive-text="关闭" />
          </div>
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">切屏限制</label>
            <el-switch v-model="publishForm.screenLimit" active-text="开启" inactive-text="关闭" />
          </div>
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">允许迟到</label>
            <el-switch v-model="publishForm.allowLate" active-text="允许" inactive-text="不允许" />
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPublish" :loading="publishLoading">
          <i class="ri-send-plane-line mr-1"></i>发布考试
        </el-button>
      </template>
    </el-dialog>

    <!-- 编辑考试弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑考试" width="600px" destroy-on-close>
      <div class="space-y-4">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>修改考试基本信息</template>
        </el-alert>

        <div>
          <label class="text-sm text-gray-700 mb-1.5 block font-medium">考试名称</label>
          <el-input v-model="editForm.name" placeholder="考试名称" />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">开始时间</label>
            <el-date-picker v-model="editForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
          </div>
          <div>
            <label class="text-sm text-gray-700 mb-1.5 block font-medium">结束时间</label>
            <el-date-picker v-model="editForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
          </div>
        </div>

        <div>
          <label class="text-sm text-gray-700 mb-1.5 block font-medium">考试时长（分钟）</label>
          <el-input-number v-model="editForm.duration" :min="30" :max="180" :step="15" style="width: 100%" />
        </div>
      </div>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEdit" :loading="editLoading">
          保存修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSchedules, createSchedule, cancelSchedule, updateSchedule, getTeacherCourses, getPapers, finishExam } from '@/api/teacher'

const router = useRouter()
const route = useRoute()
const routeExamId = computed(() => Number(route.query.examId || 0))
const routeCourseId = computed(() => Number(route.query.courseId || 0))

// ========== 状态筛选 ==========
interface StatusTab {
  label: string
  value: string
  count?: number
}
const statusTabs = computed<StatusTab[]>(() => [
  { label: '全部', value: 'all', count: exams.value.length },
  { label: '待发布', value: 'draft', count: exams.value.filter(e => e.status === 'draft' || e.status === 'published').length },
  { label: '进行中', value: 'ongoing', count: exams.value.filter(e => e.status === 'ongoing').length },
  { label: '已结束', value: 'finished', count: exams.value.filter(e => e.status === 'finished').length },
])
const activeStatus = ref('all')

// ========== 考试数据 ==========
interface ExamItem {
  id: number
  name: string
  courseId?: number
  courseName: string
  paperId?: number
  paperName: string
  startTime: string
  endTime: string
  duration: number
  status: string
  totalStudents: number
  submittedCount: number
  submissionRate: number
}

const exams = ref<ExamItem[]>([])
const loading = ref(false)

const filteredExams = computed(() => {
  if (activeStatus.value === 'all') return exams.value
  return exams.value.filter(e => e.status === activeStatus.value || String(e.status) === activeStatus.value)
})

// 加载考试列表
async function loadExams() {
  loading.value = true
  try {
    const res = await getSchedules({ pageSize: 100 })
    const resData = res as any
    const data = Array.isArray(resData) ? resData : (resData?.data?.list || resData?.list || [])
    exams.value = data.map((e: any) => {
      const submitted = e.submitCount || 0
      const total = e.studentCount || 0
      const rawStatus = String(e.status || 'draft')
      const now = Date.now()
      const start = e.startTime ? new Date(e.startTime).getTime() : 0
      const end = e.endTime ? new Date(e.endTime).getTime() : 0
      let displayStatus: string
      if (rawStatus === 'cancelled') displayStatus = 'cancelled'
      else if (rawStatus === 'draft') displayStatus = 'draft'
      else if (rawStatus === 'finished' || rawStatus === '2' || rawStatus === 'completed') displayStatus = 'finished'
      else if (start && end && start <= now && end >= now) displayStatus = 'ongoing'
      else if (end && end < now) displayStatus = 'finished'
      else if (rawStatus === 'published') displayStatus = 'published'
      else if (rawStatus === 'cancelled') displayStatus = 'cancelled'
      else displayStatus = 'draft'
      return {
        id: e.id,
        name: e.examName || e.name || '',
        courseId: e.courseId,
        courseName: e.courseName || '',
        paperId: e.examPaperId || e.paperId,
        paperName: e.paperName || '',
        startTime: e.startTime || '',
        endTime: e.endTime || '',
        duration: e.duration || 0,
        status: displayStatus,
        totalStudents: total,
        submittedCount: submitted,
        submissionRate: total > 0 ? Math.round((submitted / total) * 100) : 0,
      }
    })
  } catch (err) {
    console.error('加载考试列表失败', err)
    exams.value = []
  } finally {
    loading.value = false
  }
}

// ========== 发布弹窗 ==========
const publishDialogVisible = ref(false)
const publishLoading = ref(false)

const publishForm = ref({
  name: '',
  paperId: '' as string | number,
  courseId: '' as string | number,
  startTime: '',
  endTime: '',
  duration: 120,
  studentMode: 'all' as 'all' | 'selected',
  selectedStudents: [] as number[],
  antiCheat: true,
  screenLimit: true,
  allowLate: false,
})

// ========== 编辑弹窗 ==========
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editForm = ref({
  id: 0,
  name: '',
  startTime: '',
  endTime: '',
  duration: 120,
})

const studentSearchKey = ref('')

const paperOptions = ref<Array<{ label: string; value: string | number }>>([])
const courseOptions = ref<Array<{ label: string; value: string | number }>>([])

// 加载课程和试卷选项
async function loadOptions() {
  try {
    const [coursesRes, papersRes] = await Promise.all([
      getTeacherCourses({ pageSize: 100 }),
      getPapers({ pageSize: 100 }),
    ])
    // 响应拦截器已自动解包 Result<T>.data，所以这里直接是 list 数组或 PageResult 对象
    const courses = Array.isArray(coursesRes) ? coursesRes : (coursesRes?.list || [])
    courseOptions.value = courses.map((c: any) => ({ label: c.name, value: c.id }))
    const papers = Array.isArray(papersRes) ? papersRes : (papersRes?.list || [])
    paperOptions.value = papers.map((p: any) => ({ label: p.name || p.paperName, value: p.id }))
  } catch (err) {
    console.error('加载选项失败', err)
  }
}

interface StudentOption {
  id: number
  name: string
  studentId: string
}

const allStudentList = ref<StudentOption[]>([])

const filteredStudentList = computed(() => {
  if (!studentSearchKey.value) return allStudentList.value
  return allStudentList.value.filter(s =>
    s.name.includes(studentSearchKey.value) || s.studentId.includes(studentSearchKey.value)
  )
})

function openPublishDialog() {
  publishDialogVisible.value = true
  publishForm.value = {
    name: '',
    paperId: '',
    courseId: routeCourseId.value || '',
    startTime: '',
    endTime: '',
    duration: 120,
    studentMode: 'all',
    selectedStudents: [],
    antiCheat: true,
    screenLimit: true,
    allowLate: false,
  }
  loadOptions()
}

function hydrateEditFromRoute() {
  if (!routeExamId.value) return
  const target = exams.value.find(item => item.id === routeExamId.value)
  if (!target) return
  editForm.value = {
    id: target.id,
    name: target.name,
    startTime: target.startTime,
    endTime: target.endTime,
    duration: target.duration,
  }
  editDialogVisible.value = true
}

async function handleSubmitPublish() {
  if (!publishForm.value.name) {
    ElMessage.warning('请输入考试名称')
    return
  }
  if (!publishForm.value.paperId) {
    ElMessage.warning('请选择试卷')
    return
  }
  if (!publishForm.value.startTime || !publishForm.value.endTime) {
    ElMessage.warning('请选择考试时间')
    return
  }

  publishLoading.value = true
  try {
    // 将日期格式从 "yyyy-MM-dd HH:mm:ss" 转为 ISO 格式 (LocalDateTime 需要)
    const toISO = (dateStr: string) => dateStr ? dateStr.replace(' ', 'T') : ''
    await createSchedule({
      name: publishForm.value.name,
      examPaperId: Number(publishForm.value.paperId),
      courseId: Number(publishForm.value.courseId) || undefined,
      startTime: toISO(publishForm.value.startTime),
      endTime: toISO(publishForm.value.endTime),
      duration: publishForm.value.duration,
      totalScore: 100,        // 后端必填字段
      passScore: 60,          // 后端有默认值，但显式传入更安全
    } as any)
    ElMessage.success('考试发布成功！')
    publishDialogVisible.value = false
    loadExams()
  } catch (err) {
    console.error('发布考试失败', err)
    ElMessage.error('发布失败')
  } finally {
    publishLoading.value = false
  }
}

// ========== 操作方法 ==========
function getStatusType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    draft: 'info',
    published: 'warning',
    ongoing: 'danger',
    finished: 'success',
  }
  return map[status] || 'info'
}

function getStatusLabel(status: string): string {
  const map: Record<string, string> = {
    draft: '待发布',
    published: '已发布',
    ongoing: '进行中',
    finished: '已结束',
    cancelled: '已取消',
  }
  return map[status] || status
}

function handleEdit(exam: ExamItem) {
  editForm.value = {
    id: exam.id,
    name: exam.name,
    startTime: exam.startTime,
    endTime: exam.endTime,
    duration: exam.duration,
  }
  editDialogVisible.value = true
}

async function handleSubmitEdit() {
  if (!editForm.value.name) {
    ElMessage.warning('请输入考试名称')
    return
  }
  if (!editForm.value.startTime || !editForm.value.endTime) {
    ElMessage.warning('请选择考试时间')
    return
  }

  editLoading.value = true
  try {
    await updateSchedule(editForm.value.id, {
      name: editForm.value.name,
      startTime: editForm.value.startTime,
      endTime: editForm.value.endTime,
      duration: editForm.value.duration,
    } as any)
    ElMessage.success('考试信息已更新')
    editDialogVisible.value = false
    loadExams()
  } catch (err) {
    console.error('更新考试失败', err)
    ElMessage.error('更新失败')
  } finally {
    editLoading.value = false
  }
}

async function handleCancel(exam: ExamItem) {
  try {
    await ElMessageBox.confirm(
      `确定要取消考试「${exam.name}」吗？此操作不可撤销。`,
      '确认取消',
      { confirmButtonText: '确认取消', cancelButtonText: '返回', type: 'warning' }
    )
    await cancelSchedule(exam.id)
    ElMessage.success('考试已取消')
    loadExams()
  } catch {
    // 用户取消或失败
  }
}

function handleMonitor(exam: ExamItem) {
  router.push(`/teacher/exam/${exam.id}/monitor`)
}

async function handleForceSubmit(exam: ExamItem) {
  try {
    await ElMessageBox.confirm(
      `确定要结束「${exam.name}」并立即结算成绩吗？未提交学生会被自动交卷，含主观题的记录会进入待阅任务。`,
      '确认结束考试',
      { confirmButtonText: '结束并结算', cancelButtonText: '取消', type: 'warning' }
    )
    await finishExam(exam.id)
    ElMessage.success('考试已结束，成绩已结算')
    await loadExams()
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err?.message || '操作失败')
    }
  }
}

function handleMakeup(_exam: ExamItem) {
  ElMessage.info('安排补考功能开发中，请通过"发布新考试"手动创建补考考试')
}

onMounted(() => {
  loadExams().then(() => {
    hydrateEditFromRoute()
    if (!routeExamId.value && routeCourseId.value) {
      openPublishDialog()
    }
  })
})
</script>
