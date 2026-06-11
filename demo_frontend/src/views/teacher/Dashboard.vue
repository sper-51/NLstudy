<template>
  <div class="h-full overflow-auto p-6 space-y-6 bg-[#F5F7FA]">
    <!-- 顶部：标题 + 欢迎语 + 学期选择 + 头像 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">教师工作台</h1>
        <p class="text-sm text-gray-500 mt-1">欢迎回来，{{ teacherInfo.name }}老师！今天是 {{ currentDate }}</p>
      </div>
      <div class="flex items-center gap-4">
        <el-select v-model="currentSemester" placeholder="选择学期" style="width: 180px" size="default">
          <el-option
            v-for="sem in semesterOptions"
            :key="sem.value"
            :label="sem.label"
            :value="sem.value"
          />
        </el-select>
        <div class="flex items-center gap-2">
          <el-avatar :size="40" src="" class="bg-primary-100 text-primary-600 font-semibold">
            {{ teacherInfo.name.charAt(0) }}
          </el-avatar>
          <span class="text-sm font-medium text-gray-700">{{ teacherInfo.name }}</span>
        </div>
      </div>
    </div>

    <!-- 4个统计卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-primary-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-primary-50 flex items-center justify-center">
            <i class="ri-book-2-line text-primary-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">本学期</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.courseCount }}</div>
        <div class="text-xs text-gray-500 mt-1">管理课程数</div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-success-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-green-50 flex items-center justify-center">
            <i class="ri-file-list-3-line text-green-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">累计</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.questionCount }}</div>
        <div class="text-xs text-gray-500 mt-1">题库总量</div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-warning-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-orange-50 flex items-center justify-center">
            <i class="ri-edit-line text-warning-500 text-xl"></i>
          </div>
          <span class="text-xs text-danger-500" v-if="stats.pendingReview > 0">待处理</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.pendingReview }}</div>
        <div class="text-xs text-gray-500 mt-1">待批改份数</div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-blue-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center">
            <i class="ri-time-line text-blue-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">进行中</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.ongoingExams }}</div>
        <div class="text-xs text-gray-500 mt-1">进行中考试数</div>
      </div>
    </div>

    <!-- 进行中考试提醒条 -->
    <div
      v-if="ongoingExamList.length > 0"
      class="bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl p-4 flex items-center justify-between text-white shadow-md"
    >
      <div class="flex items-center gap-3">
        <i class="ri-alarm-warning-line text-2xl"></i>
        <div>
          <div class="font-medium">有 {{ ongoingExamList.length }} 场考试正在进行中</div>
          <div class="text-sm opacity-90 mt-0.5">
            最近一场：{{ ongoingExamList[0].examName }}（{{ ongoingExamList[0].courseName }}）· 剩余 {{ ongoingExamList[0].remainingTime }}
          </div>
        </div>
      </div>
      <button
        class="px-4 py-2 bg-white/20 hover:bg-white/30 rounded-lg text-sm font-medium transition-colors cursor-pointer backdrop-blur-sm whitespace-nowrap"
        @click="handleEnterMonitor(ongoingExamList[0])"
      >
        <i class="ri-eye-line mr-1"></i>进入监控
      </button>
    </div>

    <!-- 我的课程 -->
    <div class="bg-white rounded-xl p-5 shadow-sm">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
          <i class="ri-book-open-line text-primary-500"></i>
          我的课程
        </h3>
        <div class="flex items-center gap-2">
          <span class="text-sm text-gray-400">共 {{ courseList.length }} 门</span>
          <el-button type="primary" size="small" @click="showCreateDialog = true" class="ml-2">
            <i class="ri-add-line mr-1"></i>创建课程
          </el-button>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div
          v-for="course in courseList"
          :key="course.id"
          class="border border-gray-100 rounded-xl p-4 hover:border-primary-200 hover:shadow-sm transition-all"
        >
          <div class="flex items-start justify-between mb-3">
            <div class="flex items-center gap-3">
              <div class="w-11 h-11 rounded-xl bg-primary-50 flex items-center justify-center flex-shrink-0">
                <i class="ri-code-box-fill text-primary-500 text-xl"></i>
              </div>
              <div>
                <h4 class="font-semibold text-gray-800 text-sm">{{ course.name }}</h4>
                <p class="text-xs text-gray-500 mt-0.5">{{ course.semester }}</p>
              </div>
            </div>
          </div>

          <!-- 课程码 + 复制 -->
          <div class="flex items-center gap-2 mb-3 px-3 py-2 bg-gray-50 rounded-lg">
            <span class="text-xs text-gray-500">课程码：</span>
            <code class="text-xs font-mono font-semibold text-primary-600 tracking-wider">{{ course.code }}</code>
            <button
              @click="copyCode(course.code)"
              class="ml-auto text-gray-400 hover:text-primary-500 transition-colors cursor-pointer"
              title="复制课程码"
            >
              <i class="ri-file-copy-line text-sm"></i>
            </button>
          </div>

          <!-- 统计信息 -->
          <div class="flex items-center gap-4 text-xs text-gray-500 mb-3">
            <span><i class="ri-user-line mr-1"></i>{{ course.studentCount }} 学生</span>
            <span><i class="ri-file-text-line mr-1"></i>{{ course.examCount }} 考试</span>
            <span><i class="ri-star-line mr-1"></i>{{ course.credits }} 学分</span>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-2 pt-3 border-t border-gray-100">
            <router-link
              v-if="course.id"
              :to="'/teacher/course/' + course.id"
              class="flex-1 text-center py-2 text-xs font-medium text-primary-500 bg-primary-50 hover:bg-primary-100 rounded-lg transition-colors cursor-pointer"
            >
              <i class="ri-settings-4-line mr-1"></i>进入课程管理
            </router-link>
            <span v-else class="flex-1 text-center py-2 text-xs font-medium text-gray-300 bg-gray-50 rounded-lg cursor-not-allowed">
              <i class="ri-settings-4-line mr-1"></i>进入课程管理
            </span>
            <button
              class="flex-1 py-2 text-xs font-medium text-gray-600 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors cursor-pointer"
              @click="handleViewRoster(course)"
            >
              <i class="ri-team-line mr-1"></i>选课名单
            </button>
          </div>
        </div>
      </div>

      <div v-if="courseList.length === 0" class="py-12 text-center text-gray-400">
        <i class="ri-book-open-line text-4xl mb-2 block"></i>
        <p class="text-sm">暂无课程</p>
        <el-button type="primary" size="small" class="mt-3" @click="showCreateDialog = true">
          <i class="ri-add-line mr-1"></i>创建第一门课程
        </el-button>
      </div>
    </div>

    <!-- 创建课程弹窗 -->
    <el-dialog v-model="showCreateDialog" title="创建新课程" width="520px" destroy-on-close @open="createForm.code = generateRandomCode()">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="90px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入课程名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="课程码">
          <div class="flex items-center gap-2 w-full">
            <code class="flex-1 px-3 py-2 bg-primary-50 text-primary-600 font-mono font-bold tracking-widest rounded-lg text-sm text-center">{{ createForm.code }}</code>
            <el-button size="small" @click="regenerateCode" title="重新生成">
              <i class="ri-refresh-line"></i>
            </el-button>
          </div>
          <p class="text-xs text-gray-400 mt-1">学生使用此码加入课程（也可在课程详情页生成）</p>
        </el-form-item>
        <el-form-item label="学分">
          <el-input-number v-model="createForm.credits" :min="0.5" :max="10" :step="0.5" :precision="1" />
        </el-form-item>
        <el-form-item label="学时">
          <el-input-number v-model="createForm.hours" :min="8" :max="200" :step="8" />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="createForm.semester" placeholder="选择学期" style="width: 100%">
            <el-option label="2025-2026学年 第一学期" value="2025-2026-1" />
            <el-option label="2025-2026学年 第二学期" value="2025-2026-2" />
            <el-option label="2024-2025学年 第一学期" value="2024-2025-1" />
            <el-option label="2024-2025学年 第二学期" value="2024-2025-2" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="可选，输入课程简介" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateCourse">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rosterDialogVisible" :title="rosterCourse ? `选课名单 · ${rosterCourse.name}` : '选课名单'" width="760px" destroy-on-close>
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <div class="text-sm text-gray-500">展示当前课程的真实选课学生数据</div>
          <div class="text-sm text-gray-400">共 {{ rosterStudents.length }} 人</div>
        </div>
        <el-table :data="rosterStudents" stripe max-height="420">
          <el-table-column prop="studentId" label="学号" width="140" />
          <el-table-column prop="name" label="姓名" width="140" />
          <el-table-column prop="className" label="班级" min-width="160" />
          <el-table-column prop="joinTime" label="加入时间" min-width="180" />
          <el-table-column prop="statusText" label="状态" width="100" />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="rosterDialogVisible = false">关闭</el-button>
        <el-button v-if="rosterCourse?.id" type="primary" @click="goToCourseManage">进入课程管理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getTeacherCourses, getSchedules, getQuestions, getGradingTasks, createCourse, getCourseStudents } from '@/api/teacher'

const userStore = useUserStore()
const router = useRouter()

// 教师信息
const teacherInfo = computed(() => ({
  name: userStore.userInfo?.realName || userStore.userInfo?.username || '教师',
  id: userStore.userInfo?.id || '',
}))

// 当前日期
const currentDate = computed(() => dayjs().format('YYYY年MM月DD日'))

// 学期选择
const currentSemester = ref('')
const semesterOptions = ref<Array<{ label: string; value: string }>>([])

// 加载状态
const loading = ref(false)

// 统计数据
const stats = ref({
  courseCount: 0,
  questionCount: 0,
  pendingReview: 0,
  ongoingExams: 0,
})

// 进行中的考试
const ongoingExamList = ref<Array<{
  id: number
  examName: string
  courseName: string
  startTime: string
  remainingTime: string
}>>([])

// 课程列表
interface CourseItem {
  id: number
  name: string
  code: string
  semester: string
  studentCount: number
  examCount: number
  credits: number
}

const courseList = ref<CourseItem[]>([])
const rosterDialogVisible = ref(false)
const rosterCourse = ref<CourseItem | null>(null)
const rosterStudents = ref<Array<{ studentId: string; name: string; className: string; joinTime: string; statusText: string }>>([])

function unwrapList(payload: any): any[] {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.list)) return payload.list
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.data)) return payload.data
  if (Array.isArray(payload?.data?.list)) return payload.data.list
  if (Array.isArray(payload?.data?.records)) return payload.data.records
  return []
}

function unwrapTotal(payload: any, fallbackList: any[] = []): number {
  return Number(
    payload?.pagination?.total ??
    payload?.total ??
    payload?.data?.pagination?.total ??
    payload?.data?.total ??
    fallbackList.length ??
    0
  )
}

function isReadableText(value: unknown): value is string {
  const text = String(value ?? '').trim()
  return !!text && !/^\?+$/.test(text) && !/[ÃÂ�]/.test(text)
}

function displayText(value: unknown, fallback: string) {
  return isReadableText(value) ? String(value).trim() : fallback
}

// ========== 创建课程 ==========
const showCreateDialog = ref(false)
const creating = ref(false)
const createFormRef = ref()

function generateRandomCode() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let code = 'CRS'
  for (let i = 0; i < 6; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return code
}

const createForm = ref({
  name: '',
  code: generateRandomCode(),
  credits: 3,
  hours: 64,
  semester: '2025-2026-2',
  description: '',
})
const createRules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
}

function regenerateCode() {
  createForm.value.code = generateRandomCode()
}

async function handleCreateCourse() {
  if (!createFormRef.value) return
  try {
    await createFormRef.value.validate()
    creating.value = true
    await createCourse(createForm.value)
    ElMessage.success('课程创建成功')
    showCreateDialog.value = false
    // 重置表单（重新生成课程码）
    createForm.value = { name: '', code: generateRandomCode(), credits: 3, hours: 64, semester: '2025-2026-2', description: '' }
    // 刷新列表
    await loadData()
  } catch (error: any) {
    if (error !== false) {
      ElMessage.error(error?.message || '创建失败')
    }
  } finally {
    creating.value = false
  }
}

// 复制课程码
async function copyCode(code: string) {
  if (!code) {
    ElMessage.warning('该课程暂无课程码')
    return
  }
  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(code)
    } else {
      const input = document.createElement('textarea')
      input.value = code
      input.style.position = 'fixed'
      input.style.opacity = '0'
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
    }
    ElMessage.success(`课程码 ${code} 已复制`)
  } catch {
    ElMessage.error('复制失败，请手动复制')
  }
}

async function handleViewRoster(course: CourseItem) {
  rosterCourse.value = course
  rosterDialogVisible.value = true
  try {
    const res: any = await getCourseStudents(course.id, { page: 1, pageSize: 1000 })
    const list = Array.isArray(res) ? res : (res?.list || [])
    rosterStudents.value = list.map((item: any) => ({
      studentId: item.username || String(item.studentId || ''),
      name: item.realName || item.studentName || '',
      className: item.className || '-',
      joinTime: item.selectTime || '-',
      statusText: item.status === 1 ? '在读' : '已退课',
    }))
  } catch (error) {
    console.error('加载选课名单失败', error)
    rosterStudents.value = []
    ElMessage.error('加载选课名单失败')
  }
}

function goToCourseManage() {
  if (rosterCourse.value?.id) {
    rosterDialogVisible.value = false
    router.push(`/teacher/course/${rosterCourse.value.id}`)
  }
}

function handleEnterMonitor(exam: { id: number }) {
  if (exam?.id) {
    router.push(`/teacher/exam/${exam.id}/monitor`)
  }
}

// 计算剩余时间
function calcRemainingTime(endTime: string): string {
  const now = dayjs()
  const end = dayjs(endTime)
  if (end.isBefore(now)) return '已结束'
  const minutes = end.diff(now, 'minute')
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours < 24) return `${hours}小时${mins > 0 ? mins + '分钟' : ''}`
  const days = Math.floor(hours / 24)
  return `${days}天${hours % 24 > 0 ? (hours % 24) + '小时' : ''}`
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    // 加载课程列表
    // 注意：axios响应拦截器已解包一层(res.data)，所以这里拿到的是 { list, total } 或直接数组
    const coursesRes = await getTeacherCourses({ pageSize: 100 })
    // 兼容多种返回格式：数组 / { list } / { data: { list } } / { records }
    let rawCourses: any[] = unwrapList(coursesRes)
    courseList.value = rawCourses.map((c: any) => ({
      id: Number(c.id) || 0,
      name: displayText(c.name, `课程 ${c.id || ''}`.trim()),
      code: displayText(c.code || c.shareCode, ''),
      semester: displayText(c.semester, ''),
      studentCount: c.studentCount || 0,
      examCount: c.examCount || 0,
      credits: c.credits || c.credit || 0,
    }))

    // 收集学期选项
    const semesters = [...new Set(rawCourses.map((c: any) => c.semester).filter(Boolean))]
    semesterOptions.value = semesters.map((s: string) => ({
      label: s,
      value: s,
    }))
    if (semesterOptions.value.length > 0 && !currentSemester.value) {
      currentSemester.value = semesterOptions.value[0]?.value || ''
    }

    // 计算统计数据
    stats.value = {
      courseCount: rawCourses.length,
      questionCount: 0, // 下面从API获取
      pendingReview: 0, // 下面从API获取
      ongoingExams: 0,
    }

    // 获取题库总量
    try {
      const qRes = await getQuestions({ pageSize: 1 })
      stats.value.questionCount = unwrapTotal(qRes, unwrapList(qRes))
    } catch { /* 题库统计接口可能不存在 */ }

    // 获取待批改数量
    try {
      const gRes = await getGradingTasks({ status: 'pending', pageSize: 1 })
      stats.value.pendingReview = unwrapTotal(gRes, unwrapList(gRes))
    } catch { /* 批改接口可能不存在 */ }

    // 加载进行中的考试
    const examsRes = await getSchedules({ status: 'ongoing', pageSize: 10 })
    const exams = unwrapList(examsRes)
    ongoingExamList.value = exams.slice(0, 5).map((e: any) => {
      const course = rawCourses.find((c: any) => c.id === e.courseId)
      return {
        id: e.id,
        examName: displayText(e.examName || e.name, `考试 ${e.id || ''}`.trim()),
        courseName: course?.name || displayText(e.courseName, ''),
        startTime: e.startTime || '',
        remainingTime: calcRemainingTime(e.endTime),
      }
    })
    stats.value.ongoingExams = ongoingExamList.value.length

  } catch (err) {
    console.error('加载仪表盘数据失败', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
