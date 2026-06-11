<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">成绩复核</h1>
        <p class="text-sm text-gray-500 mt-1">处理学生提交的成绩复核申请</p>
      </div>
    </div>

    <!-- 筛选 + 统计 -->
    <div class="bg-white rounded-xl p-4 shadow-sm flex items-center justify-between gap-4">
      <div class="flex items-center gap-3">
        <el-select v-model="filterStatus" placeholder="全部状态" style="width: 150px" size="default" clearable>
          <el-option label="待处理" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
          <el-option label="已解决" value="resolved" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学生姓名或考试名称..."
          style="width: 260px"
          size="default"
          clearable
          :prefix-icon="'ri-search-line'"
        />
      </div>
      <div class="flex items-center gap-3">
        <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-warning-50 border border-warning-200">
          <i class="ri-time-line text-warning-500"></i>
          <span class="text-sm font-medium text-warning-700">{{ statusCount.pending }}</span>
          <span class="text-xs text-warning-500">待处理</span>
        </div>
        <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-success-50 border border-success-200">
          <i class="ri-check-line text-success-500"></i>
          <span class="text-sm font-medium text-success-700">{{ statusCount.approved }}</span>
          <span class="text-xs text-success-500">已通过</span>
        </div>
        <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-danger-50 border border-danger-200">
          <i class="ri-close-circle-line text-danger-500"></i>
          <span class="text-sm font-medium text-danger-700">{{ statusCount.rejected }}</span>
          <span class="text-xs text-danger-500">已驳回</span>
        </div>
      </div>
    </div>

    <!-- 复核申请列表（卡片式） -->
    <div class="space-y-3" v-loading="loading">
      <div
        v-for="item in filteredAppeals"
        :key="item.id"
        class="bg-white rounded-xl shadow-sm border border-gray-100 hover:border-primary-200 hover:shadow-md transition-all"
      >
        <div class="p-5">
          <!-- 卡片头部：学生信息 + 状态标签 -->
          <div class="flex items-start justify-between mb-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-primary-100 text-primary-600 font-bold flex items-center justify-center text-sm">
                {{ item.studentName.charAt(0) }}
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <span class="font-semibold text-gray-800">{{ item.studentName }}</span>
                  <el-tag :type="getStatusTagType(item.status)" size="small" effect="light" round>{{ getStatusLabel(item.status) }}</el-tag>
                </div>
                <div class="text-xs text-gray-400 mt-0.5">{{ item.studentId }} · {{ item.examName }}</div>
              </div>
            </div>
            <div class="text-right">
              <div class="text-xs text-gray-400">{{ item.appliedAt }}</div>
              <div v-if="item.status === 'pending'" class="mt-1">
                <el-button type="primary" size="small" @click="openHandleDialog(item)">
                  <i class="ri-settings-4-line mr-1"></i>处理复核
                </el-button>
              </div>
            </div>
          </div>

          <!-- 申请详情 -->
          <div class="grid grid-cols-2 gap-4">
            <div v-if="item.questionTitle" class="bg-gray-50 rounded-lg p-3">
              <div class="text-xs text-gray-400 mb-1"><i class="ri-question-line mr-1"></i>申请题目</div>
              <div class="text-sm text-gray-700 line-clamp-1">{{ item.questionTitle }}</div>
            </div>
            <div class="bg-gray-50 rounded-lg p-3">
              <div class="text-xs text-gray-400 mb-1"><i class="ri-chat-new-line mr-1"></i>申请原因</div>
              <div class="text-sm text-gray-700 line-clamp-2">{{ item.reason }}</div>
            </div>
          </div>

          <!-- 处理结果（已处理的显示） -->
          <div v-if="item.status !== 'pending'" class="mt-3 pt-3 border-t border-gray-100">
            <div class="flex items-start gap-2 text-sm">
              <i class="ri-feedback-line mt-0.5" :class="item.status === 'approved' ? 'text-green-500' : 'text-red-500'"></i>
              <div>
                <span class="font-medium" :class="item.status === 'approved' ? 'text-green-600' : 'text-red-600'">
                  {{ item.status === 'approved' ? '✓ 已通过' : '✗ 已驳回' }}
                </span>
                <span v-if="item.newScore !== undefined" class="ml-2 text-gray-500">
                  新分数：{{ item.newScore }}分（原{{ item.originalScore }}分）
                </span>
                <div v-if="item.teacherComment" class="text-xs text-gray-500 mt-1">{{ item.teacherComment }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredAppeals.length === 0" class="flex flex-col items-center justify-center py-16 text-gray-400 bg-white rounded-xl">
        <i class="ri-inbox-line text-5xl mb-3 opacity-50"></i>
        <p class="text-sm font-medium">暂无复核申请</p>
        <p class="text-xs mt-1">学生提交的复核申请将在此处显示</p>
      </div>
    </div>

    <!-- 处理复核弹窗 -->
    <el-dialog v-model="showHandleDialog" title="处理成绩复核" width="650px" destroy-on-close @close="resetForm">
      <template v-if="currentAppeal">
        <!-- 学生与考试信息 -->
        <div class="mb-5 pb-4 border-b border-gray-100">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-9 h-9 rounded-full bg-primary-100 text-primary-600 font-bold flex items-center justify-center text-sm">
              {{ currentAppeal.studentName.charAt(0) }}
            </div>
            <div>
              <span class="font-semibold text-gray-800">{{ currentAppeal.studentName }}</span>
              <span class="text-gray-400 mx-2">·</span>
              <span class="text-sm text-gray-500">{{ currentAppeal.examName }}</span>
            </div>
          </div>
          <div class="bg-orange-50 rounded-lg p-3 border border-orange-100">
            <div class="text-xs text-orange-600 font-medium mb-1"><i class="ri-chat-new-line mr-1"></i>申请原因</div>
            <p class="text-sm text-gray-700">{{ currentAppeal.reason }}</p>
          </div>
        </div>

        <!-- 原始答卷区域 -->
        <div class="mb-5">
          <h4 class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
            <i class="ri-file-text-line text-primary-500"></i>原始答卷
          </h4>
          <div class="bg-gray-50 rounded-xl p-4 border border-gray-100 space-y-3">
            <div v-if="currentAppeal.questionTitle">
              <div class="text-xs text-gray-400 mb-1">题目：{{ currentAppeal.questionTitle }}</div>
            </div>
            <div>
              <div class="text-xs text-gray-400 mb-1">学生作答：</div>
              <div class="bg-white rounded-lg p-3 border border-gray-200 text-sm text-gray-700 leading-relaxed whitespace-pre-wrap font-mono">
                {{ currentAppeal.studentAnswer || '(无作答内容)' }}
              </div>
            </div>
            <div>
              <div class="text-xs text-gray-400 mb-1">参考答案：</div>
              <div class="bg-green-50/60 rounded-lg p-3 border border-green-100 text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
                {{ currentAppeal.referenceAnswer || '(暂无参考答案)' }}
              </div>
            </div>
          </div>
        </div>

        <!-- 分数调整 -->
        <div class="grid grid-cols-2 gap-4 mb-5">
          <div>
            <label class="text-sm font-medium text-gray-700 block mb-1.5">
              <i class="ri-lock-line mr-1 text-gray-400"></i>{{ currentAppeal.questionTitle ? '原本题分' : '原总分' }}
            </label>
            <el-input :model-value="String(currentAppeal.originalScore)" disabled size="large">
              <template #append><span class="text-gray-400">分</span></template>
            </el-input>
          </div>
          <div>
            <label class="text-sm font-medium text-gray-700 block mb-1.5">
              <i class="ri-edit-line mr-1 text-primary-500"></i>{{ currentAppeal.questionTitle ? '新本题分' : '新总分' }}
            </label>
            <el-input-number
              v-model="handleForm.newScore"
              :min="0"
              :max="currentAppeal.maxScore || 100"
              :step="1"
              controls-position="right"
              size="large"
              style="width: 100%"
            />
          </div>
        </div>

        <!-- 教师意见 -->
        <div class="mb-2">
          <label class="text-sm font-medium text-gray-700 block mb-1.5">
            <i class="ri-chat-quote-line mr-1 text-primary-500"></i>教师意见 / 评语
          </label>
          <el-input
            v-model="handleForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入处理意见和评语..."
            resize="none"
          />
        </div>
      </template>

      <template #footer>
        <div class="flex items-center justify-between w-full">
          <el-button @click="showHandleDialog = false">取消</el-button>
          <div class="flex items-center gap-2">
            <el-button type="danger" plain @click="handleReject" :disabled="!handleForm.comment.trim() || handleLoading">
              <i class="ri-close-circle-line mr-1"></i>驳回
            </el-button>
            <el-button type="primary" @click="handleApprove" :loading="handleLoading">
              <i class="ri-check-double-line mr-1"></i>通过
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviews, handleReview as handleReviewApi } from '@/api/teacher'

// ========== 加载状态 ==========
const loading = ref(false)

// ========== 筛选 ==========
const filterStatus = ref('')
const searchKeyword = ref('')

// ========== 统计 ==========
const statusCount = computed(() => {
  const counts = { pending: 0, approved: 0, rejected: 0, resolved: 0 }
  appeals.value.forEach(a => { if (counts[a.status as keyof typeof counts] !== undefined) counts[a.status as keyof typeof counts]++ })
  return counts
})

// ========== 复核申请数据 ==========
interface AppealItem {
  id: number
  studentId: number | string
  studentName: string
  examName: string
  questionNumber?: number
  questionTitle?: string
  reason: string
  studentAnswer?: string
  referenceAnswer?: string
  originalScore: number
  questionScore?: number
  maxScore?: number
  newScore?: number
  teacherComment?: string
  status: 'pending' | 'approved' | 'rejected' | 'resolved'
  appliedAt: string
}

const appeals = ref<AppealItem[]>([])

// ========== 加载复核列表 ==========
async function loadReviews() {
  loading.value = true
  try {
    const res = await getReviews({
      status: filterStatus.value || undefined,
      page: 1,
      pageSize: 100,
    })
    // 拦截器返回 res.data，即 {list: [...], pagination: {...}}
    const list = Array.isArray(res) ? res : (res?.list || [])
    appeals.value = list.map((item: any) => ({
      id: item.id,
      studentId: item.studentId,
      studentName: item.studentName || `学生${item.studentId}`,
      examName: item.examName || '',
      questionNumber: item.questionNumber,
      questionTitle: item.questionTitle,
      reason: item.reason || item.studentComment || '',
      studentAnswer: item.studentAnswer,
      referenceAnswer: item.referenceAnswer,
      originalScore: item.originalScore || 0,
      questionScore: item.questionScore ?? item.answerScore,
      maxScore: item.maxScore || item.fullScore || 100,
      newScore: item.newScore,
      teacherComment: item.teacherComment,
      status: item.status === 'approved' ? 'approved' : item.status === 'rejected' ? 'rejected' : item.status === 'resolved' ? 'resolved' : 'pending',
      appliedAt: item.createTime ? formatTime(item.createTime) : '',
    }))
  } catch (err) {
    console.error('加载复核列表失败', err)
    appeals.value = []
  } finally {
    loading.value = false
  }
}

function formatTime(time: string | Date): string {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}

// ========== 筛选逻辑 ==========
const filteredAppeals = computed(() => {
  let list = appeals.value
  if (filterStatus.value) {
    list = list.filter(item => item.status === filterStatus.value)
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(item =>
      item.studentName.toLowerCase().includes(keyword) ||
      item.examName.toLowerCase().includes(keyword)
    )
  }
  // 待处理的排在前面
  return [...list].sort((a, b) => {
    if (a.status === 'pending' && b.status !== 'pending') return -1
    if (a.status !== 'pending' && b.status === 'pending') return 1
    return 0
  })
})

// ========== 页面加载 ==========
onMounted(() => {
  loadReviews()
})

// ========== 筛选监听 ==========
watch(filterStatus, () => {
  loadReviews()
})

// ========== 处理弹窗 ==========
const showHandleDialog = ref(false)
const currentAppeal = ref<AppealItem | null>(null)
const handleLoading = ref(false)

const handleForm = ref({
  newScore: 0,
  comment: '',
})

function openHandleDialog(item: AppealItem) {
  currentAppeal.value = item
  handleForm.value = {
    newScore: item.questionTitle ? (item.questionScore ?? Math.min(item.originalScore, item.maxScore || item.originalScore)) : item.originalScore,
    comment: '',
  }
  showHandleDialog.value = true
}

function resetForm() {
  handleForm.value = { newScore: 0, comment: '' }
  currentAppeal.value = null
}

async function handleApprove() {
  if (!currentAppeal.value) return
  handleLoading.value = true
  try {
    await handleReviewApi(currentAppeal.value.id, {
      action: 'approve',
      newScore: handleForm.value.newScore,
      teacherComment: handleForm.value.comment || '复核通过，分数已调整。',
    })
    showHandleDialog.value = false
    ElMessage.success(`已通过 ${currentAppeal.value.studentName} 的复核申请，新分数：${handleForm.value.newScore}分`)
    // 重新从服务器拉取数据，确保状态一致
    await loadReviews()
  } catch (err: any) {
    ElMessage.error(err?.message || '处理失败')
  } finally {
    handleLoading.value = false
  }
}

async function handleReject() {
  if (!handleForm.value.comment.trim()) {
    ElMessage.warning('驳回时必须填写原因')
    return
  }
  handleLoading.value = true
  try {
    await ElMessageBox.confirm(
      `确定要驳回 ${currentAppeal.value?.studentName} 的复核申请吗？`,
      '确认驳回',
      { confirmButtonText: '确认驳回', cancelButtonText: '取消', type: 'warning' }
    )
    await handleReviewApi(currentAppeal.value!.id, {
      action: 'reject',
      teacherComment: handleForm.value.comment,
    })
    showHandleDialog.value = false
    ElMessage.info(`已驳回 ${currentAppeal.value?.studentName} 的复核申请`)
    // 重新从服务器拉取数据，确保状态一致
    await loadReviews()
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err?.message || '处理失败')
    }
  } finally {
    handleLoading.value = false
  }
}

// ========== 辅助函数 ==========
function getStatusTagType(status: string): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    resolved: '',
  }
  return map[status] || 'info'
}

function getStatusLabel(status: string): string {
  const map: Record<string, string> = {
    pending: '待处理',
    approved: '已通过',
    rejected: '已驳回',
    resolved: '已解决',
  }
  return map[status] || status
}
</script>
