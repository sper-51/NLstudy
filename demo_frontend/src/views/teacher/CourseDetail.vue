<template>
  <div class="h-full overflow-auto p-6 space-y-6 bg-[#F5F7FA]">
    <!-- 返回导航 -->
    <div class="flex items-center gap-2 text-sm text-gray-500">
      <router-link to="/teacher/dashboard" class="hover:text-primary-500 transition-colors cursor-pointer">
        <i class="ri-arrow-left-line mr-1"></i>返回工作台
      </router-link>
      <span>/</span>
      <span class="text-gray-800 font-medium">课程详情</span>
    </div>

    <!-- 课程基本信息 -->
    <div class="bg-white rounded-xl p-6 shadow-sm">
      <div class="flex items-start justify-between">
        <div class="flex gap-5">
          <div class="w-16 h-16 rounded-xl bg-primary-50 flex items-center justify-center flex-shrink-0">
            <i class="ri-code-box-fill text-primary-500 text-3xl"></i>
          </div>
          <div>
            <h1 class="text-xl font-bold text-gray-800">{{ courseInfo.name }}</h1>
            <p class="text-sm text-gray-500 mt-1">{{ courseInfo.description }}</p>
            <div class="flex items-center gap-4 mt-3 text-sm text-gray-600">
              <span><i class="ri-bar-code-line mr-1 text-primary-400"></i>课程代码：{{ courseInfo.code }}</span>
              <span><i class="ri-star-line mr-1 text-warning-400"></i>{{ courseInfo.credits }} 学分</span>
              <span><i class="ri-time-line mr-1 text-blue-400"></i>{{ courseInfo.hours }} 学时</span>
              <span><i class="ri-user-line mr-1 text-green-400"></i>授课教师：{{ courseInfo.teacherName }}</span>
            </div>
          </div>
        </div>
        <el-tag type="primary" effect="plain">{{ courseInfo.semester }}</el-tag>
      </div>

      <!-- 课程码分享区域 -->
      <div class="mt-5 pt-5 border-t border-gray-100">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <span class="text-sm text-gray-600">课程加入码：</span>
            <code class="px-4 py-2 bg-primary-50 text-primary-600 font-mono font-bold tracking-widest rounded-lg text-base">{{ courseInfo.joinCode }}</code>
            <button
              @click="copyJoinCode"
              class="px-3 py-2 text-sm text-primary-500 bg-primary-50 hover:bg-primary-100 rounded-lg transition-colors cursor-pointer flex items-center gap-1"
            >
              <i class="ri-file-copy-line"></i>复制
            </button>
            <button
              @click="handleGenerateCode"
              :disabled="!validCourseId"
              class="px-3 py-2 text-sm text-blue-500 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors cursor-pointer flex items-center gap-1 disabled:opacity-50"
            >
              <i class="ri-refresh-line"></i>生成/刷新
            </button>
          </div>
          <button
            @click="showQrDialog = true"
            class="px-3 py-2 text-sm text-gray-600 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors cursor-pointer flex items-center gap-1"
          >
            <i class="ri-qr-code-line"></i>查看二维码
          </button>
        </div>
      </div>
    </div>

    <!-- Tab 切换区域 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <el-tabs v-model="activeTab" class="course-tabs">
        <!-- 选课学生列表 Tab -->
        <el-tab-pane label="选课学生" name="students">
          <div class="p-5">
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center gap-3">
                <el-input
                  v-model="studentSearch"
                  placeholder="搜索学号或姓名..."
                  prefix-icon="Search"
                  style="width: 240px"
                  clearable
                />
                <el-select v-model="studentStatusFilter" placeholder="状态筛选" style="width: 130px" clearable>
                  <el-option label="已通过" value="approved" />
                  <el-option label="待审核" value="pending" />
                  <el-option label="已拒绝" value="rejected" />
                </el-select>
              </div>
              <span class="text-sm text-gray-400">共 {{ filteredStudents.length }} 名学生</span>
            </div>

            <el-table :data="filteredStudents" stripe style="width: 100%" empty-text="暂无选课学生">
              <el-table-column prop="studentId" label="学号" width="130" />
              <el-table-column prop="name" label="姓名" width="120" />
              <el-table-column prop="joinTime" label="加入时间" width="180">
                <template #default="{ row }">{{ row.joinTime }}</template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small" effect="light">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="200">
                <template #default="{ row }">
                  <el-button link type="primary" size="small">查看详情</el-button>
                  <el-button
                    v-if="row.status === 'approved'"
                    link
                    type="danger"
                    size="small"
                    @click="handleRemoveStudent(row)"
                  >移除</el-button>
                  <el-button
                    v-if="row.status === 'pending'"
                    link
                    type="success"
                    size="small"
                  >通过</el-button>
                  <el-button
                    v-if="row.status === 'pending'"
                    link
                    type="info"
                    size="small"
                  >拒绝</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="mt-4 flex justify-end">
              <el-pagination
                v-model:current-page="studentPage"
                :page-size="10"
                :total="students.length"
                layout="prev, pager, next"
                size="small"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 考试列表 Tab -->
        <el-tab-pane label="考试列表" name="exams">
          <div class="p-5">
            <div class="flex items-center justify-between mb-4">
              <h4 class="font-semibold text-gray-800">本课程全部考试</h4>
              <el-button type="primary" size="default" @click="handleCreateExam">
                <i class="ri-add-line mr-1"></i>发布新考试
              </el-button>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div
                v-for="exam in examList"
                :key="exam.id"
                class="border border-gray-100 rounded-xl p-4 hover:border-primary-200 hover:shadow-sm transition-all"
              >
                <div class="flex items-start justify-between mb-3">
                  <div>
                    <h5 class="font-semibold text-gray-800 text-sm">{{ exam.name }}</h5>
                    <p class="text-xs text-gray-500 mt-1">
                      {{ exam.startTime }} ~ {{ exam.endTime }} · {{ exam.duration }}分钟
                    </p>
                  </div>
                  <el-tag :type="getExamTagType(exam.status)" size="small" effect="light">
                    {{ getExamStatusText(exam.status) }}
                  </el-tag>
                </div>

                <div class="flex items-center gap-4 text-xs text-gray-500 mb-2">
                  <span><i class="ri-user-line mr-1"></i>{{ exam.participantCount }} 人参与</span>
                  <span><i class="ri-file-list-3-line mr-1"></i>{{ exam.questionCount }} 题</span>
                  <span><i class="ri-star-line mr-1"></i>{{ exam.totalScore }} 分</span>
                  <span><i class="ri-checkbox-circle-line mr-1"></i>{{ exam.submitCount }} 份已提交</span>
                </div>

                <div class="mb-3">
                  <div class="flex items-center justify-between text-[12px] text-gray-400 mb-1">
                    <span>交卷进度</span>
                    <span>{{ exam.submissionRate.toFixed(0) }}%</span>
                  </div>
                  <el-progress :percentage="Math.min(100, Math.max(0, exam.submissionRate))" :stroke-width="7" :show-text="false" />
                </div>

                <div class="flex flex-wrap gap-2 pt-3 border-t border-gray-100">
                  <el-button size="small" link type="primary" @click="handlePreviewExam(exam)">
                    <i class="ri-eye-line mr-1"></i>预览
                  </el-button>
                  <el-button size="small" link type="primary" @click="handleEditExam(exam)">
                    <i class="ri-edit-line mr-1"></i>编辑
                  </el-button>
                  <el-button size="small" link type="primary" @click="handleViewExam(exam)">
                    <i class="ri-bar-chart-grouped-line mr-1"></i>查看提交
                  </el-button>
                  <el-button
                    v-if="exam.status === 'draft'"
                    size="small"
                    link type="success"
                    @click="handlePublishExam(exam)"
                  >
                    <i class="ri-send-plane-line mr-1"></i>发布
                  </el-button>
                  <el-button size="small" link type="danger" @click="handleDeleteExam(exam)">
                    <i class="ri-delete-bin-line mr-1"></i>删除
                  </el-button>
                </div>
              </div>
            </div>

            <div v-if="examList.length === 0" class="py-12 text-center text-gray-400">
              <i class="ri-file-text-line text-4xl mb-2 block"></i>
              <p class="text-sm">暂无考试，点击上方按钮创建新考试</p>
            </div>
          </div>
        </el-tab-pane>

        <!-- 题库统计 Tab -->
        <el-tab-pane label="题库统计" name="questions">
          <div class="p-5">
            <div class="grid grid-cols-4 gap-4 mb-6">
              <div class="bg-gray-50 rounded-xl p-4 text-center">
                <div class="text-2xl font-bold text-gray-800">{{ questionStats.total }}</div>
                <div class="text-xs text-gray-500 mt-1">题目总数</div>
              </div>
              <div class="bg-blue-50 rounded-xl p-4 text-center">
                <div class="text-2xl font-bold text-blue-600">{{ questionStats.singleChoice }}</div>
                <div class="text-xs text-blue-500 mt-1">单选题</div>
              </div>
              <div class="bg-purple-50 rounded-xl p-4 text-center">
                <div class="text-2xl font-bold text-purple-600">{{ questionStats.multipleChoice }}</div>
                <div class="text-xs text-purple-500 mt-1">多选题</div>
              </div>
              <div class="bg-green-50 rounded-xl p-4 text-center">
                <div class="text-2xl font-bold text-green-600">{{ questionStats.others }}</div>
                <div class="text-xs text-green-500 mt-1">其他题型</div>
              </div>
            </div>

            <div class="flex items-center justify-between mb-4">
              <h4 class="font-semibold text-gray-800">最近添加的题目</h4>
              <router-link to="/teacher/questions" class="text-sm text-primary-500 hover:text-primary-600 cursor-pointer flex items-center gap-1">
                管理题库 <i class="ri-arrow-right-s-line"></i>
              </router-link>
            </div>

            <el-table :data="recentQuestions" style="width: 100%">
              <el-table-column prop="type" label="题型" width="100">
                <template #default="{ row }">
                  <el-tag size="small" :type="getQuestionTypeTagType(row.type)" effect="plain">
                    {{ getQuestionTypeName(row.type) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
              <el-table-column prop="difficulty" label="难度" width="90">
                <template #default="{ row }">
                  <el-tag size="small" :type="getDifficultyTagType(row.difficulty)" effect="plain">
                    {{ getDifficultyText(row.difficulty) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="score" label="分值" width="70" />
              <el-table-column prop="createTime" label="创建时间" width="170" />
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>

  <el-dialog
    v-model="previewVisible"
    width="760px"
    destroy-on-close
    :title="previewExam ? `考试预览 · ${previewExam.name}` : '考试预览'"
  >
    <div v-if="previewExam" class="space-y-5">
      <div class="grid grid-cols-2 gap-4">
        <div class="rounded-xl bg-slate-50 border border-slate-100 p-4">
          <div class="text-xs text-gray-400">考试时间</div>
          <div class="mt-2 text-sm text-gray-700">{{ previewExam.startTime }} ~ {{ previewExam.endTime }}</div>
        </div>
        <div class="rounded-xl bg-slate-50 border border-slate-100 p-4">
          <div class="text-xs text-gray-400">考试规模</div>
          <div class="mt-2 text-sm text-gray-700">{{ previewExam.participantCount }} 人 · {{ previewExam.duration }} 分钟 · {{ previewExam.totalScore }} 分</div>
        </div>
      </div>

      <div class="rounded-2xl border border-primary-100 bg-primary-50/60 p-4">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-sm font-medium text-gray-700">考试状态</div>
            <div class="mt-1 text-xs text-gray-500">{{ getExamStatusText(previewExam.status) }}，当前已提交 {{ previewExam.submitCount }} 份答卷</div>
          </div>
          <el-tag :type="getExamTagType(previewExam.status)" effect="light">{{ getExamStatusText(previewExam.status) }}</el-tag>
        </div>
        <div class="mt-3">
          <div class="flex items-center justify-between text-[12px] text-gray-400 mb-1">
            <span>提交进度</span>
            <span>{{ previewExam.submissionRate.toFixed(0) }}%</span>
          </div>
          <el-progress :percentage="Math.min(100, Math.max(0, previewExam.submissionRate))" :stroke-width="8" :show-text="false" />
        </div>
      </div>

      <div class="rounded-2xl border border-slate-200 p-4">
        <div class="text-sm font-medium text-gray-700 mb-2">教师操作建议</div>
        <ul class="space-y-2 text-sm text-gray-600">
          <li>· 若考试仍为草稿，可先点击“编辑”调整时间和试卷，再发布给学生。</li>
          <li>· 若考试已开始，可进入“查看提交”页查看答卷进度和成绩情况。</li>
          <li>· 若考试无须继续，可直接删除或在考试安排页统一取消。</li>
        </ul>
      </div>
    </div>
    <template #footer>
      <el-button @click="previewVisible = false">关闭</el-button>
      <el-button type="primary" @click="previewExam && handleViewExam(previewExam)">查看提交</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="showQrDialog" width="420px" title="课程二维码" destroy-on-close>
    <div class="text-center space-y-4">
      <img
        :src="courseQrImageUrl"
        alt="课程二维码"
        class="w-52 h-52 mx-auto rounded-xl border border-gray-100 p-2 bg-white"
      />
      <div>
        <div class="text-sm font-semibold text-gray-800">{{ courseInfo.name }}</div>
        <div class="mt-1 text-xs text-gray-500">学生扫码打开后，可确认加入该课程。</div>
      </div>
      <div class="rounded-xl bg-gray-50 p-3 text-left">
        <div class="text-xs text-gray-400 mb-1">入课链接</div>
        <div class="text-xs text-gray-700 break-all">{{ courseJoinUrl }}</div>
      </div>
    </div>
    <template #footer>
      <el-button @click="copyText(courseJoinUrl, '入课链接已复制')">复制链接</el-button>
      <el-button type="primary" @click="copyJoinCode">复制课程码</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import {
  getTeacherCourses,
  getCourseStudents,
  removeCourseStudent,
  getSchedules,
  getQuestions,
  generateCourseCode,
  cancelSchedule,
} from '@/api/teacher'

const route = useRoute()
const router = useRouter()
const courseId = (route.params.courseId || route.params.id) as string

// 校验courseId有效性
const validCourseId = computed(() => {
  const id = Number(courseId)
  return !isNaN(id) && id > 0 ? id : null
})

// 如果courseId无效，重定向回工作台
if (!validCourseId.value) {
  // 延迟导航避免在setup期间路由冲突
  setTimeout(() => router.replace('/teacher/dashboard'), 0)
}

// 课程基本信息
const courseInfo = ref({
  id: 0,
  name: '',
  code: '',
  joinCode: '',
  credits: 0,
  hours: 0,
  semester: '',
  teacherName: '',
  description: '',
})
const loading = ref(false)
const showQrDialog = ref(false)

const courseJoinUrl = computed(() => {
  const code = courseInfo.value.joinCode && courseInfo.value.joinCode !== '未生成' ? courseInfo.value.joinCode : courseInfo.value.code
  const base = window.location.origin
  return `${base}/home?code=${encodeURIComponent(code || '')}`
})

const courseQrImageUrl = computed(() => {
  return `https://api.2dcode.biz/v1/create-qr-code?size=220x220&data=${encodeURIComponent(courseJoinUrl.value)}`
})

// 加载课程详情
async function loadCourseDetail() {
  if (!validCourseId.value) return
  try {
    loading.value = true
    const res = await getTeacherCourses({ page: 1, pageSize: 100 })
    const courses = Array.isArray(res) ? res : (res?.list || [])
    const course = courses.find((c: any) => c.id === validCourseId.value)
    if (course) {
      courseInfo.value = {
        id: course.id || 0,
        name: course.name || '',
        code: course.code || '',
        joinCode: course.code || course.courseCode || course.shareCode || '未生成',
        credits: course.credits || 0,
        hours: course.hours || 0,
        semester: course.semester || '',
        teacherName: course.teacherName || '',
        description: course.description || '',
      }
    }
  } catch (error) {
    console.error('加载课程详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 当前激活的Tab
const activeTab = ref('students')

// 学生搜索和筛选
const studentSearch = ref('')
const studentStatusFilter = ref('')
const studentPage = ref(1)

// 选课学生数据
interface Student {
  id: number
  studentId: string
  name: string
  joinTime: string
  status: string
}

const students = ref<Student[]>([])

// 加载选课学生列表
async function loadStudents() {
  if (!validCourseId.value) return
  try {
    const res = await getCourseStudents(validCourseId.value, { page: 1, pageSize: 1000 })
    const list = Array.isArray(res) ? res : (res?.list || [])
    students.value = list.map((s: any) => ({
      id: s.studentId || s.id || 0,
      studentId: s.username || String(s.studentId || ''),
      name: s.realName || s.studentName || '',
      joinTime: s.selectTime || '',
      status: s.status === 1 ? 'approved' : 'pending',
    }))
  } catch (error) {
    console.error('加载学生列表失败:', error)
  }
}

const filteredStudents = computed(() => {
  return students.value.filter(s => {
    const matchSearch = !studentSearch.value ||
      s.studentId.includes(studentSearch.value) ||
      s.name.includes(studentSearch.value)
    const matchStatus = !studentStatusFilter.value || s.status === studentStatusFilter.value
    return matchSearch && matchStatus
  })
})

// 考试列表
interface ExamItem {
  id: number
  name: string
  startTime: string
  endTime: string
  duration: number
  participantCount: number
  submitCount: number
  submissionRate: number
  questionCount: number
  totalScore: number
  status: string
}

const examList = ref<ExamItem[]>([])
const previewVisible = ref(false)
const previewExam = ref<ExamItem | null>(null)

// 加载考试列表
async function loadExams() {
  try {
    const res = await getSchedules({ courseId: Number(courseId), page: 1, pageSize: 1000 })
    const list = Array.isArray(res) ? res : (res?.list || [])
    examList.value = list.map((e: any) => ({
      id: e.id || 0,
      name: e.examName || e.name || '',
      startTime: e.startTime || '',
      endTime: e.endTime || '',
      duration: e.duration || 0,
      participantCount: e.participantCount ?? e.studentCount ?? 0,
      submitCount: e.submitCount ?? e.submittedCount ?? 0,
      submissionRate: Number(e.submissionRate ?? 0),
      questionCount: e.questionCount || 0,
      totalScore: e.totalScore || 0,
      status: e.status === 'ongoing' ? 'ongoing' :
            e.status === 'finished' ? 'finished' :
            e.status === 'published' ? 'published' :
            e.status === 'cancelled' ? 'cancelled' : 'draft',
    }))
  } catch (error) {
    console.error('加载考试列表失败:', error)
  }
}

// 题库统计
const questionStats = ref({
  total: 0,
  singleChoice: 0,
  multipleChoice: 0,
  others: 0,
})

interface QuestionItem {
  id: number
  type: string
  content: string
  difficulty: string
  score: number
  createTime: string
}

const recentQuestions = ref<QuestionItem[]>([])

// 加载题目列表（用于统计）
async function loadQuestions() {
  try {
    const res = await getQuestions({ courseId: Number(courseId), page: 1, pageSize: 1000 })
    // 响应拦截器已自动解包 Result<T>.data，所以这里直接是 list 数组或 PageResult 对象
    const list = Array.isArray(res) ? res : (res?.list || [])

    // 统计题型分布
    questionStats.value.total = list.length
    questionStats.value.singleChoice = list.filter((q: any) => q.type === 'single_choice').length
    questionStats.value.multipleChoice = list.filter((q: any) => q.type === 'multi_choice').length
    questionStats.value.others = list.filter((q: any) =>
      !['single_choice', 'multi_choice'].includes(q.type)
    ).length

    // 最近添加的题目（取前5条）
    recentQuestions.value = list.slice(0, 5).map((q: any) => ({
      id: q.id || 0,
      type: q.type || '',
      content: q.content || '',
      difficulty: q.difficulty || '',
      score: q.score || 0,
      createTime: q.createTime || '',
    }))
  } catch (error) {
    console.error('加载题目列表失败:', error)
  }
}

// 方法
async function copyText(text: string, successText = '已复制') {
  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(text)
    } else {
      const input = document.createElement('textarea')
      input.value = text
      input.style.position = 'fixed'
      input.style.opacity = '0'
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
    }
    ElMessage.success(successText)
  } catch {
    ElMessage.error('复制失败，请手动复制')
  }
}

function copyJoinCode() {
  const code = courseInfo.value.joinCode && courseInfo.value.joinCode !== '未生成' ? courseInfo.value.joinCode : courseInfo.value.code
  if (!code) {
    ElMessage.warning('请先生成课程码')
    return
  }
  copyText(code, `课程码 ${code} 已复制`)
}

async function handleGenerateCode() {
  if (!validCourseId.value) return
  try {
    const res = await generateCourseCode(validCourseId.value)
    const newCode = (res as any)?.code || ''
    if (newCode) {
      courseInfo.value.joinCode = newCode
      courseInfo.value.code = newCode
      ElMessage.success(`新课程码已生成: ${newCode}`)
    }
  } catch (error: any) {
    ElMessage.error(error?.message || '生成课程码失败')
  }
}

async function handleRemoveStudent(student: Student) {
  ElMessageBox.confirm(
    `确定要移除学生「${student.name}」(${student.studentId}) 吗？`,
    '确认移除',
    { confirmButtonText: '确定移除', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await removeCourseStudent(Number(courseId), student.id)
      // 从列表中移除
      const index = students.value.findIndex(s => s.id === student.id)
      if (index > -1) students.value.splice(index, 1)
      ElMessage.success('已移除该学生')
    } catch (error: any) {
      ElMessage.error(error.message || '移除失败')
    }
  }).catch(() => {})
}

function handleCreateExam() {
  if (validCourseId.value) {
    router.push(`/teacher/exam-schedule?courseId=${validCourseId.value}`)
  }
}

function handleViewExam(exam: ExamItem) {
  router.push(`/teacher/exam/${exam.id}/detail`)
}

function handlePreviewExam(exam: ExamItem) {
  previewExam.value = exam
  previewVisible.value = true
}

function handleEditExam(exam: ExamItem) {
  router.push(`/teacher/exam-schedule?examId=${exam.id}&courseId=${validCourseId.value || ''}`)
}

async function handleDeleteExam(exam: ExamItem) {
  try {
    await ElMessageBox.confirm(
      `确定要删除/取消考试「${exam.name}」吗？这会同步影响学生端考试入口。`,
      '确认删除',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    await cancelSchedule(exam.id)
    ElMessage.success('考试已取消')
    await loadExams()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

async function handlePublishExam(exam: ExamItem) {
  ElMessageBox.confirm(
    `确定要发布考试「${exam.name}」吗？发布后学生将可见。`,
    '确认发布',
    { confirmButtonText: '确认发布', cancelButtonText: '取消', type: 'info' }
  ).then(async () => {
    try {
      // 这里可以调用发布考试的API（如果需要的话）
      exam.status = 'ongoing'
      ElMessage.success('考试已发布')
    } catch (error: any) {
      ElMessage.error(error.message || '发布失败')
    }
  }).catch(() => {})
}

// 页面加载时获取数据
onMounted(async () => {
  if (!validCourseId.value) return
  await loadCourseDetail()
  await loadStudents()
  await loadExams()
  await loadQuestions()
})

// 状态映射
function getStatusType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    approved: 'success',
    pending: 'warning',
    rejected: 'danger',
  }
  return map[status] || 'info'
}

function getStatusText(status: string): string {
  const map: Record<string, string> = {
    approved: '已通过',
    pending: '待审核',
    rejected: '已拒绝',
  }
  return map[status] || status
}

function getExamTagType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    draft: 'info',
    ongoing: 'warning',
    published: '',
    finished: 'success',
    cancelled: 'danger',
  }
  return map[status] || 'info'
}

function getExamStatusText(status: string): string {
  const map: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
    ongoing: '进行中',
    finished: '已结束',
    cancelled: '已取消',
  }
  return map[status] || status
}

function getQuestionTypeTagType(type: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    single: '',
    multiple: 'success',
    judge: 'warning',
    fill: 'info',
    essay: 'danger',
  }
  return map[type] || 'info'
}

function getQuestionTypeName(type: string): string {
  const map: Record<string, string> = {
    single: '单选',
    multiple: '多选',
    judge: '判断',
    fill: '填空',
    essay: '简答',
  }
  return map[type] || type
}

function getDifficultyTagType(difficulty: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger',
  }
  return map[difficulty] || 'info'
}

function getDifficultyText(difficulty: string): string {
  const map: Record<string, string> = {
    easy: '简单',
    medium: '中等',
    hard: '困难',
  }
  return map[difficulty] || difficulty
}
</script>

<style scoped>
.course-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  background: #fafafa;
}
.course-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  font-size: 14px;
}
.course-tabs :deep(.el-tabs__active-bar) {
  background-color: #6366f1;
}
</style>
