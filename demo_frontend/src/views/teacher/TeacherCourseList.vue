<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">课程列表</h1>
        <p class="text-sm text-gray-500 mt-1">查看和管理当前教师名下课程，继续进入课程详情完成联调。</p>
      </div>
      <div class="flex items-center gap-3">
        <el-select v-model="semesterFilter" clearable placeholder="筛选学期" style="width: 180px">
          <el-option
            v-for="item in semesterOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-input v-model="keyword" placeholder="搜索课程名称/课程码" clearable style="width: 240px">
          <template #prefix>
            <i class="ri-search-line text-gray-400"></i>
          </template>
        </el-input>
      </div>
    </div>

    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <div class="text-sm text-gray-500">课程总数</div>
        <div class="mt-2 text-2xl font-bold text-gray-800">{{ filteredCourses.length }}</div>
      </div>
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <div class="text-sm text-gray-500">学生总数</div>
        <div class="mt-2 text-2xl font-bold text-gray-800">{{ totalStudents }}</div>
      </div>
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <div class="text-sm text-gray-500">考试总数</div>
        <div class="mt-2 text-2xl font-bold text-gray-800">{{ totalExams }}</div>
      </div>
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <div class="text-sm text-gray-500">学分合计</div>
        <div class="mt-2 text-2xl font-bold text-gray-800">{{ totalCredits }}</div>
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
        <h2 class="text-base font-semibold text-gray-800">我的课程</h2>
        <div class="text-sm text-gray-400">共 {{ filteredCourses.length }} 门</div>
      </div>

      <div v-if="loading" class="p-10 text-center text-gray-400 text-sm">
        正在加载课程列表...
      </div>

      <div v-else-if="filteredCourses.length === 0" class="p-10 text-center text-gray-400 text-sm">
        暂无符合条件的课程
      </div>

      <div v-else class="divide-y divide-gray-100">
        <div
          v-for="course in filteredCourses"
          :key="course.id"
          class="px-5 py-4 hover:bg-gray-50 transition-colors"
        >
          <div class="flex items-start justify-between gap-6">
            <div class="min-w-0 flex-1">
              <div class="flex items-center gap-3 flex-wrap">
                <h3 class="text-base font-semibold text-gray-800">{{ course.name }}</h3>
                <span class="px-2 py-0.5 rounded-full bg-primary-50 text-primary-600 text-xs font-medium">{{ course.code || '未设置课程码' }}</span>
                <span v-if="course.semester" class="px-2 py-0.5 rounded-full bg-gray-100 text-gray-600 text-xs">{{ course.semester }}</span>
              </div>
              <p class="mt-2 text-sm text-gray-500 line-clamp-2">{{ course.description || '暂无课程简介' }}</p>
              <div class="mt-3 flex items-center gap-5 text-sm text-gray-500 flex-wrap">
                <span><i class="ri-group-line mr-1"></i>{{ course.studentCount }} 名学生</span>
                <span><i class="ri-file-list-3-line mr-1"></i>{{ course.examCount }} 场考试</span>
                <span><i class="ri-award-line mr-1"></i>{{ course.credits }} 学分</span>
                <span v-if="course.classHours"><i class="ri-time-line mr-1"></i>{{ course.classHours }} 学时</span>
              </div>
            </div>
            <div class="flex items-center gap-2 flex-shrink-0">
              <button
                @click="copyCourseCode(course)"
                class="px-3 py-2 rounded-lg bg-primary-50 text-primary-600 text-sm font-medium hover:bg-primary-100 transition-colors"
              >
                <i class="ri-file-copy-line mr-1"></i>复制课程码
              </button>
              <button
                @click="openQrDialog(course)"
                class="px-3 py-2 rounded-lg bg-gray-50 text-gray-600 text-sm font-medium hover:bg-gray-100 transition-colors"
              >
                <i class="ri-qr-code-line mr-1"></i>二维码
              </button>
              <router-link
                :to="`/teacher/course/${course.id}`"
                class="px-4 py-2 rounded-lg bg-primary-500 text-white text-sm font-medium hover:bg-primary-600 transition-colors"
              >
                进入课程管理
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="qrVisible" width="420px" title="课程二维码" destroy-on-close>
      <div v-if="qrCourse" class="text-center space-y-4">
        <img :src="qrImageUrl" alt="课程二维码" class="w-52 h-52 mx-auto rounded-xl border border-gray-100 p-2 bg-white" />
        <div>
          <div class="text-sm font-semibold text-gray-800">{{ qrCourse.name }}</div>
          <div class="mt-1 text-xs text-gray-500">学生扫码打开后，可确认加入该课程。</div>
        </div>
        <div class="rounded-xl bg-gray-50 p-3 text-left">
          <div class="text-xs text-gray-400 mb-1">入课链接</div>
          <div class="text-xs text-gray-700 break-all">{{ qrJoinUrl }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="qrJoinUrl && copyText(qrJoinUrl, '入课链接已复制')">复制链接</el-button>
        <el-button type="primary" @click="qrCourse && copyCourseCode(qrCourse)">复制课程码</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getTeacherCourses } from '@/api/teacher'

interface TeacherCourseItem {
  id: number
  name: string
  code: string
  semester: string
  description: string
  studentCount: number
  examCount: number
  credits: number
  classHours: number
}

const loading = ref(false)
const keyword = ref('')
const semesterFilter = ref('')
const courses = ref<TeacherCourseItem[]>([])
const qrVisible = ref(false)
const qrCourse = ref<TeacherCourseItem | null>(null)

const qrJoinUrl = computed(() => {
  if (!qrCourse.value?.code) return ''
  return `${window.location.origin}/home?code=${encodeURIComponent(qrCourse.value.code)}`
})

const qrImageUrl = computed(() => {
  return `https://api.2dcode.biz/v1/create-qr-code?size=220x220&data=${encodeURIComponent(qrJoinUrl.value)}`
})

const semesterOptions = computed(() => {
  return [...new Set(courses.value.map(item => item.semester).filter(Boolean))]
})

const filteredCourses = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  return courses.value.filter(course => {
    const matchSemester = !semesterFilter.value || course.semester === semesterFilter.value
    const matchKeyword = !kw
      || course.name.toLowerCase().includes(kw)
      || course.code.toLowerCase().includes(kw)
    return matchSemester && matchKeyword
  })
})

const totalStudents = computed(() => filteredCourses.value.reduce((sum, item) => sum + (item.studentCount || 0), 0))
const totalExams = computed(() => filteredCourses.value.reduce((sum, item) => sum + (item.examCount || 0), 0))
const totalCredits = computed(() => filteredCourses.value.reduce((sum, item) => sum + (item.credits || 0), 0))

function isReadableText(value: unknown): value is string {
  const text = String(value ?? '').trim()
  return !!text && !/^\?+$/.test(text) && !/[ÃÂ�]/.test(text)
}

function displayText(value: unknown, fallback: string) {
  return isReadableText(value) ? String(value).trim() : fallback
}

async function loadCourses() {
  loading.value = true
  try {
    const response = await getTeacherCourses({ pageSize: 100 })
    let rawCourses: any[] = []
    if (Array.isArray(response)) {
      rawCourses = response
    } else if (Array.isArray((response as any)?.list)) {
      rawCourses = (response as any).list
    } else if (Array.isArray((response as any)?.data?.list)) {
      rawCourses = (response as any).data.list
    } else if (Array.isArray((response as any)?.records)) {
      rawCourses = (response as any).records
    }

    courses.value = rawCourses.map((course: any) => ({
      id: Number(course.id) || 0,
      name: displayText(course.name, `课程 ${course.id || ''}`.trim()),
      code: displayText(course.code || course.shareCode, ''),
      semester: displayText(course.semester, ''),
      description: displayText(course.description, ''),
      studentCount: Number(course.studentCount) || 0,
      examCount: Number(course.examCount) || 0,
      credits: Number(course.credits || course.credit) || 0,
      classHours: Number(course.classHours || course.hours) || 0,
    }))
  } catch (error) {
    console.error('加载教师课程列表失败', error)
    courses.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCourses()
})

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

function copyCourseCode(course: TeacherCourseItem) {
  if (!course.code) {
    ElMessage.warning('该课程暂无课程码，请进入课程详情生成')
    return
  }
  copyText(course.code, `课程码 ${course.code} 已复制`)
}

function openQrDialog(course: TeacherCourseItem) {
  if (!course.code) {
    ElMessage.warning('该课程暂无课程码，请先生成课程码')
    return
  }
  qrCourse.value = course
  qrVisible.value = true
}
</script>
