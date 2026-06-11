<template>
  <div class="h-full overflow-auto p-6 space-y-6">
    <!-- 统计概览 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-warning-400">
        <div class="text-xs text-gray-500 mb-1">待考</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.pendingExams }}</div>
        <div class="text-xs text-gray-400 mt-0.5">场考试</div>
      </div>
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-success-400">
        <div class="text-xs text-gray-500 mb-1">已考</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.completedExams }}</div>
        <div class="text-xs text-gray-400 mt-0.5">场均分{{ stats.avgScore }}</div>
      </div>
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-danger-400">
        <div class="text-xs text-gray-500 mb-1">错题</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.wrongQuestions }}</div>
        <div class="text-xs text-gray-400 mt-0.5">道待复习</div>
      </div>
      <div class="bg-white rounded-xl p-4 shadow-sm border-l-4 border-primary-400">
        <div class="text-xs text-gray-500 mb-1">进度</div>
        <div class="text-2xl font-bold text-gray-800">{{ stats.progress }}%</div>
        <div class="w-full h-1.5 bg-gray-100 rounded-full mt-2 overflow-hidden">
          <div class="h-full bg-primary-500 rounded-full" :style="{ width: stats.progress + '%' }"></div>
        </div>
      </div>
    </div>

    <!-- 课程码加课 -->
    <div class="bg-white rounded-xl p-5 shadow-sm">
      <h3 class="text-base font-semibold text-gray-800 mb-3 flex items-center gap-2">
        <i class="ri-add-circle-line text-primary-500"></i>
        使用课程码加入新课程
      </h3>
      <div class="flex gap-3 max-w-2xl">
        <input
          v-model="courseCode"
          type="text"
          placeholder="请输入课程码..."
          maxlength="20"
          class="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg focus:border-primary-400 focus:outline-none text-sm uppercase tracking-widest font-mono"
        />
        <button
          @click="handleJoinCourse"
          :disabled="!courseCode || joiningCourse"
          class="px-6 py-2.5 bg-primary-500 text-white text-sm font-medium rounded-lg hover:bg-primary-600 transition-colors disabled:opacity-50 cursor-pointer whitespace-nowrap"
        >
          {{ joiningCourse ? '加入中...' : '加入课程' }}
        </button>
        <button
          @click="openScanDialog"
          class="px-5 py-2.5 bg-blue-50 text-primary-600 text-sm font-medium rounded-lg hover:bg-blue-100 transition-colors cursor-pointer whitespace-nowrap flex items-center gap-1"
        >
          <i class="ri-qr-scan-2-line"></i>
          扫码加入
        </button>
      </div>
      <transition name="fade">
        <div v-if="joinMessage" :class="['mt-3 text-sm flex items-center gap-1.5', joinSuccess ? 'text-success-600' : 'text-danger-600']">
          <i :class="joinSuccess ? 'ri-check-line' : 'ri-error-warning-line'"></i>
          {{ joinMessage }}
        </div>
      </transition>
    </div>

    <el-dialog v-model="scanDialogVisible" title="扫码加入课程" width="460px" destroy-on-close @closed="stopQrScanner">
      <div
        class="space-y-4"
        @dragenter.prevent="qrDragOver = true"
        @dragover.prevent="qrDragOver = true"
        @dragleave.prevent="handleQrDragLeave"
        @drop.prevent="handleQrImageDrop"
      >
        <div class="rounded-xl bg-blue-50 border border-blue-100 p-3 text-sm text-blue-700">
          扫描教师端生成的课程二维码，识别后会自动填入课程码，仍需你点击“加入课程”确认。
        </div>
        <div class="relative overflow-hidden rounded-xl bg-gray-900 aspect-video flex items-center justify-center">
          <video ref="scanVideoRef" class="w-full h-full object-cover" autoplay muted playsinline></video>
          <div v-if="!scanning" class="absolute inset-0 flex flex-col items-center justify-center text-white/80 text-sm">
            <i class="ri-qr-scan-2-line text-4xl mb-2"></i>
            等待摄像头启动
          </div>
          <div class="absolute inset-8 border-2 border-white/70 rounded-xl pointer-events-none"></div>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <button
            class="px-3 py-2 rounded-lg bg-primary-50 text-primary-600 text-sm font-medium hover:bg-primary-100 transition-colors flex items-center justify-center gap-1"
            @click="startQrScanner"
          >
            <i class="ri-camera-line"></i>
            摄像头扫码
          </button>
          <button
            class="px-3 py-2 rounded-lg bg-gray-50 text-gray-600 text-sm font-medium hover:bg-gray-100 transition-colors flex items-center justify-center gap-1"
            @click="triggerQrImageUpload"
          >
            <i class="ri-image-add-line"></i>
            上传二维码图片
          </button>
          <input
            ref="qrImageInputRef"
            type="file"
            accept="image/*"
            class="hidden"
            @change="handleQrImageUpload"
          />
        </div>
        <div
          data-testid="qr-drop-zone"
          :class="[
            'rounded-xl border-2 border-dashed p-4 text-center transition-colors cursor-pointer',
            qrDragOver ? 'border-primary-400 bg-primary-50 text-primary-600' : 'border-gray-200 bg-gray-50 text-gray-500 hover:border-primary-200 hover:bg-blue-50'
          ]"
          @click="triggerQrImageUpload"
          @dragenter.prevent="qrDragOver = true"
          @dragover.prevent="qrDragOver = true"
          @dragleave.prevent="qrDragOver = false"
          @drop.prevent="handleQrImageDrop"
        >
          <i class="ri-upload-cloud-2-line text-2xl mb-1 block"></i>
          <div class="text-sm font-medium">拖拽二维码图片到这里</div>
          <div class="text-xs mt-1">或点击选择图片，支持教师端课程二维码截图</div>
        </div>
        <div v-if="scanMessage" :class="['text-sm flex items-center gap-1.5', scanSuccess ? 'text-success-600' : 'text-warning-600']">
          <i :class="scanSuccess ? 'ri-check-line' : 'ri-information-line'"></i>
          {{ scanMessage }}
        </div>
        <div class="text-xs text-gray-400 leading-relaxed">
          如果浏览器不支持二维码识别，可以直接用手机扫描教师二维码打开本页面，或复制二维码里的课程码手动输入。
        </div>
      </div>
      <template #footer>
        <el-button @click="scanDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="triggerQrImageUpload">上传图片识别</el-button>
      </template>
    </el-dialog>

    <!-- 我加入的课程 -->
    <div class="bg-white rounded-xl p-5 shadow-sm">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
          <i class="ri-book-3-line text-primary-500"></i>
          我加入的课程
        </h3>
        <router-link to="/courses" class="text-sm text-primary-500 hover:text-primary-600 transition-colors flex items-center gap-1 cursor-pointer">
          查看全部 <i class="ri-arrow-right-s-line"></i>
        </router-link>
      </div>

      <div class="flex gap-4 overflow-x-auto pb-2 -mx-1 px-1">
        <router-link
          v-for="course in courses.slice(0, 4)"
          :key="course.id"
          :to="'/courses/' + course.id"
          class="min-w-[240px] bg-gray-50 rounded-xl p-4 hover:bg-blue-50 hover:shadow-md transition-all group cursor-pointer block"
        >
          <div class="flex items-start justify-between mb-3">
            <div class="w-11 h-11 rounded-xl bg-primary-100 flex items-center justify-center flex-shrink-0">
              <i class="ri-code-box-fill text-primary-500 text-xl"></i>
            </div>
            <span 
              v-if="(course.pendingExamCount || 0) > 0"
              class="px-2 py-0.5 bg-danger-50 text-danger-600 text-xs font-medium rounded-full"
            >
              待考 {{ course.pendingExamCount }} 场
            </span>
          </div>
          <h4 class="font-semibold text-gray-800 mb-1 truncate group-hover:text-primary-600 transition-colors">{{ course.name }}</h4>
          <p class="text-xs text-gray-500 mb-3">{{ course.teacherName }} · {{ course.code }}</p>
          
          <div class="space-y-1.5">
            <div class="flex items-center justify-between text-xs">
              <span class="text-gray-500">学习进度</span>
              <span :class="getProgressColor(course.progress)">{{ course.progress }}%</span>
            </div>
            <div class="w-full h-1.5 bg-gray-200 rounded-full overflow-hidden">
              <div 
                class="h-full rounded-full transition-all duration-300"
                :class="getProgressBgColor(course.progress)"
                :style="{ width: course.progress + '%' }"
              ></div>
            </div>
          </div>
        </router-link>
        
        <div v-if="courses.length === 0" class="min-w-[240px] flex flex-col items-center justify-center py-8 text-gray-400">
          <i class="ri-book-open-line text-3xl mb-2"></i>
          <p class="text-sm">暂无课程，使用课程码加入吧</p>
        </div>
      </div>
    </div>

    <!-- 底部双栏：待参加考试 + 已完成考试 -->
    <div class="grid grid-cols-2 gap-6">
      <!-- 待参加考试 -->
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
            <i class="ri-time-line text-warning-500"></i>
            待参加考试
          </h3>
          <span class="text-xs text-gray-400">{{ pendingExams.length }} 场</span>
        </div>

        <div class="space-y-3">
          <div
            v-for="exam in pendingExams.slice(0, 3)"
            :key="exam.id"
            class="p-3.5 bg-warning-50/30 rounded-lg border border-warning-100"
          >
            <div class="flex items-start justify-between mb-2">
              <h4 class="text-sm font-semibold text-gray-800">{{ exam.examName }}</h4>
              <span class="px-2 py-0.5 bg-warning-100 text-warning-700 text-xs rounded font-medium">
                {{ getExamStatusText(exam.examStatus || exam.status || exam.submitStatus || '') }}
              </span>
            </div>
            <p class="text-xs text-gray-500 mb-2">{{ exam.courseName }} · {{ formatTime(exam.startTime) }}</p>
            <div class="flex items-center justify-between">
              <span class="text-xs text-gray-400">
                <i class="ri-timer-line mr-1"></i>{{ exam.duration }}分钟 · {{ exam.totalScore }}分
              </span>
              <router-link 
                :to="'/exam/' + exam.id"
                class="text-xs text-primary-500 hover:text-primary-600 font-medium cursor-pointer flex items-center gap-1"
              >
                进入考试 <i class="ri-arrow-right-s-line"></i>
              </router-link>
            </div>
          </div>

          <div v-if="pendingExams.length === 0" class="py-8 text-center text-gray-400 text-sm">
            <i class="ri-calendar-check-line text-2xl mb-2 block"></i>
            暂无待参加的考试
          </div>
        </div>
      </div>

      <!-- 已完成考试 -->
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
            <i class="ri-checkbox-circle-line text-success-500"></i>
            已完成考试
          </h3>
          <span class="text-xs text-gray-400">{{ completedExams.length }} 场</span>
        </div>

        <div class="space-y-3">
          <div
            v-for="exam in completedExams.slice(0, 3)"
            :key="exam.id"
            class="p-3.5 bg-success-50/20 rounded-lg border border-success-100"
          >
            <div class="flex items-start justify-between mb-2">
              <h4 class="text-sm font-semibold text-gray-800">{{ exam.examName }}</h4>
              <span :class="['px-2 py-0.5 text-xs rounded font-medium', getScoreBadgeClass(exam.myScore ?? 0)]">
                {{ exam.myScore }}分
              </span>
            </div>
            <p class="text-xs text-gray-500 mb-2">{{ exam.courseName }} · 排名 #{{ exam.rank || '-' }}</p>
            <div class="flex items-center gap-2">
              <router-link 
                v-if="exam.gradeId"
                :to="'/report/' + exam.gradeId"
                class="text-xs text-primary-500 hover:text-primary-600 font-medium cursor-pointer flex items-center gap-1"
              >
                <i class="ri-file-chart-line"></i> 成绩报告
              </router-link>
              <span v-else class="text-xs text-gray-400 flex items-center gap-1">
                <i class="ri-file-chart-line"></i> 成绩生成中
              </span>
              <span class="text-gray-300">|</span>
              <router-link
                v-if="exam.gradeId"
                :to="{ path: '/report/' + exam.gradeId, query: { review: '1' } }"
                class="text-xs text-purple-500 hover:text-purple-600 font-medium cursor-pointer flex items-center gap-1"
              >
                <i class="ri-chat-check-line"></i> 申请复核
              </router-link>
              <span v-else class="text-xs text-gray-400 flex items-center gap-1">
                <i class="ri-chat-check-line"></i> 暂不可复核
              </span>
              <span class="text-gray-300">|</span>
              <router-link 
                to="/wrong-questions"
                class="text-xs text-danger-500 hover:text-danger-600 font-medium cursor-pointer flex items-center gap-1"
              >
                <i class="ri-error-warning-line"></i> 错题重刷
              </router-link>
            </div>
          </div>

          <div v-if="completedExams.length === 0" class="py-8 text-center text-gray-400 text-sm">
            <i class="ri-file-text-line text-2xl mb-2 block"></i>
            暂无已完成的考试
          </div>
        </div>
      </div>
    </div>

    <!-- 通知提醒 -->
    <div class="bg-white rounded-xl p-5 shadow-sm">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
          <i class="ri-notification-3-line text-primary-500"></i>
          通知提醒
        </h3>
        <span class="text-xs text-gray-400">共 {{ notifications.length }} 条</span>
      </div>

      <div class="space-y-2">
        <div
          v-for="notif in notifications.slice(0, 4)"
          :key="notif.id"
          :class="['flex items-start gap-3 p-3 rounded-lg cursor-pointer transition-colors hover:bg-gray-50', !notif.isRead && 'bg-blue-50/40']"
        >
          <div :class="['w-9 h-9 rounded-lg flex items-center justify-center flex-shrink-0', getNotifIconClass(notif.type)]">
            <i :class="[getNotifIcon(notif.type), 'text-lg']"></i>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <h4 class="text-sm font-medium text-gray-800 truncate">{{ notif.title }}</h4>
              <span v-if="!notif.isRead" class="w-1.5 h-1.5 bg-primary-500 rounded-full flex-shrink-0"></span>
            </div>
            <p class="text-xs text-gray-500 mt-0.5 line-clamp-1">{{ notif.content }}</p>
            <span class="text-xs text-gray-400 mt-1 block">{{ notif.time }}</span>
          </div>
        </div>

        <div v-if="notifications.length === 0" class="py-6 text-center text-gray-400 text-sm">
          <i class="ri-notification-off-line text-2xl mb-2 block"></i>
          暂无通知
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import jsQR from 'jsqr'
import type { Course, Exam, Notification } from '@/api/types'
import { getCourses, getPendingExams, getExamsHistory, getGrades, getWrongQuestions, getNotifications, joinCourse } from '@/api/student'

const route = useRoute()

// 状态
const courseCode = ref('')
const joiningCourse = ref(false)
const joinMessage = ref('')
const joinSuccess = ref(false)
const loading = ref(false)
const scanDialogVisible = ref(false)
const scanning = ref(false)
const scanMessage = ref('')
const scanSuccess = ref(false)
const scanVideoRef = ref<HTMLVideoElement | null>(null)
const qrImageInputRef = ref<HTMLInputElement | null>(null)
const qrDragOver = ref(false)
let scanStream: MediaStream | null = null
let scanTimer: number | null = null

// 首页统计数据
const stats = ref({
  pendingExams: 0,
  completedExams: 0,
  avgScore: 0,
  wrongQuestions: 0,
  progress: 0,
})

const courses = ref<Course[]>([])
const pendingExams = ref<Exam[]>([])
const completedExams = ref<Exam[]>([])
const notifications = ref<Notification[]>([])

onMounted(async () => {
  const codeFromQuery = String(route.query.code || route.query.courseCode || '').trim()
  if (codeFromQuery) {
    courseCode.value = codeFromQuery.toUpperCase()
    joinMessage.value = '已识别扫码课程码，请确认后加入课程'
    joinSuccess.value = true
  }
  loading.value = true
  try {
    const [coursesRes, pendingRes, historyRes, gradesRes, wrongQRes, notificationsRes] = await Promise.all([
      getCourses(),
      getPendingExams(),
      getExamsHistory().catch(() => []),
      getGrades({ pageSize: 100 }).catch(() => ({ data: [] })),
      getWrongQuestions({ pageSize: 1 }).catch(() => ({ data: [], total: 0 })),
      getNotifications({ pageSize: 20 }),
    ])

    // 处理课程数据
    courses.value = (Array.isArray(coursesRes) ? coursesRes : []).map((c: any) => ({
      ...c,
      progress: c.progress ?? 0,
      pendingExamCount: c.pendingExamCount ?? 0,
    }))

    // 处理待考考试，统一使用 id 字段（兼容后端返回 examId）
    const pendingList = Array.isArray(pendingRes) ? pendingRes : []
    pendingExams.value = pendingList.map((e: any) => ({
      ...e,
      id: e.id ?? e.examId,
    }))

    // 处理已完成考试（从历史接口获取真实数据）
    const historyList = Array.isArray(historyRes) ? historyRes : []
    completedExams.value = historyList.map((e: any) => ({
      ...e,
      id: e.id ?? e.examId ?? e.recordId,
      gradeId: e.gradeId ?? null,
      myScore: e.totalScore ?? e.myScore ?? 0,
    }))

    // 从成绩接口计算平均分
    const gradesData = Array.isArray(gradesRes) ? gradesRes : ((gradesRes as any)?.list || (gradesRes as any)?.data || [])
    const scores = gradesData.map((g: any) => g.totalScore ?? g.score ?? g.myScore).filter((s: number) => Number(s) > 0)
    const avgScoreVal = scores.length > 0
      ? Math.round(scores.reduce((sum: number, s: number) => sum + s, 0) / scores.length)
      : 0

    // 从错题接口获取错题数
    const wqData = wrongQRes as any
    const wrongCount = wqData?.total ?? (Array.isArray(wqData?.data) ? wqData.data.length : 0)

    // 计算统计
    const allCourses = courses.value
    const totalPending = pendingExams.value.length
    const totalCompleted = historyList.length > 0 ? historyList.length : allCourses.reduce((sum: number, c: any) => sum + (c.completedExamCount ?? 0), 0)
    const totalProgress = allCourses.length > 0
      ? Math.round(allCourses.reduce((sum: number, c: any) => sum + (c.progress ?? 0), 0) / allCourses.length)
      : 0
    stats.value = {
      pendingExams: totalPending,
      completedExams: totalCompleted,
      avgScore: avgScoreVal,
      wrongQuestions: wrongCount,
      progress: totalProgress,
    }

    // 处理通知
    const notifData = notificationsRes as any
    const notifList = Array.isArray(notifData) ? notifData
      : (notifData?.data ? notifData.data : [])
    notifications.value = notifList.map((n: any) => ({
      ...n,
      isRead: Boolean(n.isRead),
    }))
  } catch (err) {
    console.error('加载首页数据失败', err)
  } finally {
    loading.value = false
  }
})

onBeforeUnmount(() => {
  stopQrScanner()
})

// 方法
async function handleJoinCourse() {
  if (!courseCode.value) return

  joiningCourse.value = true
  joinMessage.value = ''

  try {
    await joinCourse(courseCode.value)

    // 重新加载课程
    const coursesRes = await getCourses()
    courses.value = (Array.isArray(coursesRes) ? coursesRes : []).map((c: any) => ({
      ...c,
      progress: c.progress ?? 0,
      pendingExamCount: c.pendingExamCount ?? 0,
    }))

    joinSuccess.value = true
    joinMessage.value = `课程码 ${courseCode.value} 加入成功！`
    courseCode.value = ''
  } catch (error: any) {
    joinSuccess.value = false
    joinMessage.value = error?.message || '课程码无效或已过期'
  } finally {
    joiningCourse.value = false
  }
}

async function openScanDialog() {
  scanDialogVisible.value = true
  scanMessage.value = ''
  scanSuccess.value = false
  await nextTick()
  await startQrScanner()
}

async function startQrScanner() {
  stopQrScanner()
  if (!navigator.mediaDevices?.getUserMedia) {
    scanMessage.value = '当前环境无法访问摄像头，请手动输入课程码'
    scanSuccess.value = false
    return
  }
  try {
    scanStream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
    if (scanVideoRef.value) {
      scanVideoRef.value.srcObject = scanStream
      await scanVideoRef.value.play()
    }
    scanning.value = true
    scanMessage.value = '请将课程二维码放入取景框'
    scanSuccess.value = true
    const BarcodeDetectorCtor = getBarcodeDetector()
    const detector = BarcodeDetectorCtor ? new BarcodeDetectorCtor({ formats: ['qr_code'] }) : null
    const scanLoop = async () => {
      if (!scanDialogVisible.value || !scanVideoRef.value || !scanning.value) return
      try {
        const rawValue = detector
          ? (await detector.detect(scanVideoRef.value))?.[0]?.rawValue
          : detectQrFromVideo(scanVideoRef.value)
        if (rawValue) {
          handleQrResult(rawValue)
          return
        }
      } catch (error) {
        console.warn('二维码识别失败', error)
      }
      scanTimer = window.setTimeout(scanLoop, 500)
    }
    scanLoop()
  } catch (error: any) {
    scanning.value = false
    scanMessage.value = error?.name === 'NotAllowedError' ? '摄像头权限被拒绝，请允许后重试' : '摄像头启动失败，请手动输入课程码'
    scanSuccess.value = false
  }
}

function triggerQrImageUpload() {
  qrImageInputRef.value?.click()
}

async function handleQrImageUpload(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  await handleQrImageFile(file)
}

async function handleQrImageDrop(event: DragEvent) {
  qrDragOver.value = false
  const file = Array.from(event.dataTransfer?.files || []).find(item => item.type.startsWith('image/'))
  if (!file) {
    scanMessage.value = '请拖入二维码图片文件'
    scanSuccess.value = false
    return
  }
  await handleQrImageFile(file)
}

function handleQrDragLeave(event: DragEvent) {
  const current = event.currentTarget as HTMLElement | null
  const related = event.relatedTarget as Node | null
  if (!current || !related || !current.contains(related)) {
    qrDragOver.value = false
  }
}

async function handleQrImageFile(file: File) {
  try {
    stopQrScanner()
    scanMessage.value = '正在识别上传的二维码图片...'
    scanSuccess.value = true
    const bitmap = await createImageBitmap(file)
    const rawValue = await detectQrFromBitmap(bitmap)
    bitmap.close()
    if (!rawValue) {
      scanMessage.value = '未在图片中识别到二维码，请换一张清晰图片'
      scanSuccess.value = false
      return
    }
    handleQrResult(rawValue)
  } catch (error) {
    console.warn('二维码图片识别失败', error)
    scanMessage.value = '二维码图片识别失败，请换一张清晰图片或手动输入课程码'
    scanSuccess.value = false
  }
}

function getBarcodeDetector() {
  const BarcodeDetectorCtor = (window as any).BarcodeDetector
  return BarcodeDetectorCtor || null
}

async function detectQrFromBitmap(bitmap: ImageBitmap) {
  const BarcodeDetectorCtor = getBarcodeDetector()
  if (BarcodeDetectorCtor) {
    const detector = new BarcodeDetectorCtor({ formats: ['qr_code'] })
    const codes = await detector.detect(bitmap)
    if (codes?.[0]?.rawValue) return codes[0].rawValue
  }
  return detectQrFromCanvasSource(bitmap.width, bitmap.height, (context) => {
    context.drawImage(bitmap, 0, 0)
  })
}

function detectQrFromVideo(video: HTMLVideoElement) {
  const width = video.videoWidth
  const height = video.videoHeight
  if (!width || !height) return ''
  return detectQrFromCanvasSource(width, height, (context) => {
    context.drawImage(video, 0, 0, width, height)
  })
}

function detectQrFromCanvasSource(width: number, height: number, draw: (context: CanvasRenderingContext2D) => void) {
  const canvas = document.createElement('canvas')
  canvas.width = width
  canvas.height = height
  const context = canvas.getContext('2d', { willReadFrequently: true })
  if (!context) return ''
  draw(context)
  const imageData = context.getImageData(0, 0, width, height)
  const result = jsQR(imageData.data, imageData.width, imageData.height)
  return result?.data || ''
}

function stopQrScanner() {
  if (scanTimer) {
    window.clearTimeout(scanTimer)
    scanTimer = null
  }
  if (scanStream) {
    scanStream.getTracks().forEach(track => track.stop())
    scanStream = null
  }
  scanning.value = false
}

function handleQrResult(rawValue: string) {
  const code = extractCourseCode(rawValue)
  if (!code) {
    scanMessage.value = '未识别到有效课程码，请确认二维码来自本系统教师端'
    scanSuccess.value = false
    return
  }
  courseCode.value = code.toUpperCase()
  joinMessage.value = `已扫码识别课程码 ${courseCode.value}，请确认后加入课程`
  joinSuccess.value = true
  scanMessage.value = '识别成功，已填入课程码'
  scanSuccess.value = true
  scanDialogVisible.value = false
  stopQrScanner()
}

function extractCourseCode(rawValue: string) {
  const text = (rawValue || '').trim()
  if (!text) return ''
  try {
    const url = new URL(text, window.location.origin)
    const code = url.searchParams.get('code') || url.searchParams.get('courseCode')
    if (code) return code.trim()
  } catch {
    // 非 URL 时按纯课程码处理
  }
  return /^[A-Za-z0-9_-]{4,32}$/.test(text) ? text : ''
}

function formatTime(time: string): string {
  if (!time) return '-'
  return dayjs(time).format('MM-DD HH:mm')
}

function getProgressColor(progress: number): string {
  if (progress >= 80) return 'text-success-500'
  if (progress >= 60) return 'text-primary-500'
  if (progress >= 40) return 'text-warning-500'
  return 'text-gray-500'
}

function getProgressBgColor(progress: number): string {
  if (progress >= 80) return 'bg-success-500'
  if (progress >= 60) return 'bg-primary-500'
  if (progress >= 40) return 'bg-warning-500'
  return 'bg-gray-400'
}

function getExamStatusText(status: string): string {
  const map: Record<string, string> = {
    pending: '待开始',
    ongoing: '进行中',
    finished: '已结束',
    completed: '已完成',
  }
  return map[status] || status
}

function getScoreBadgeClass(score: number): string {
  if (score >= 90) return 'bg-success-100 text-success-700'
  if (score >= 80) return 'bg-primary-100 text-primary-700'
  if (score >= 70) return 'bg-warning-100 text-warning-700'
  if (score >= 60) return 'bg-orange-100 text-orange-700'
  return 'bg-danger-100 text-danger-700'
}

function getNotifIconClass(type: string): string {
  const map: Record<string, string> = {
    exam: 'bg-blue-50',
    grade: 'bg-green-50',
    review: 'bg-purple-50',
    system: 'bg-orange-50',
  }
  return map[type] || 'bg-gray-50'
}

function getNotifIcon(type: string): string {
  const map: Record<string, string> = {
    exam: 'ri-time-line text-blue-500',
    grade: 'ri-bar-chart-2-line text-green-500',
    review: 'ri-chat-check-line text-purple-500',
    system: 'ri-settings-3-line text-orange-500',
  }
  return map[type] || 'ri-notification-line text-gray-500'
}
</script>
