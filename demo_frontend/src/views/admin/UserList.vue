<template>
  <div class="space-y-4">
    <!-- 顶部标题区 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-[#3b82f6]/10 flex items-center justify-center">
          <i :class="isTeacher ? 'ri-user-star-line' : 'ri-user-line'" class="text-[#3b82f6] text-lg"></i>
        </div>
        <h1 class="text-xl font-bold text-gray-800">{{ pageTitle }}</h1>
        <el-tag size="small" :type="isTeacher ? 'info' : 'success'">{{ isTeacher ? '教师' : '学生' }}</el-tag>
      </div>
      <div class="flex items-center gap-2 flex-wrap">
        <el-button @click="importDialogVisible = true" plain>
          <i class="ri-upload-cloud-2-line mr-1"></i>批量导入
        </el-button>
        <el-button @click="handleExport" plain>
          <i class="ri-download-line mr-1"></i>导出用户
        </el-button>
        <el-button type="primary" @click="openAddDialog">
          <i class="ri-add-line mr-1"></i>添加{{ roleLabel }}
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="flex gap-3 flex-wrap bg-white p-4 rounded-xl shadow-sm">
      <div class="relative flex-1 min-w-[200px] max-w-[320px]">
        <i class="ri-search-line absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索姓名/账号..."
          class="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:border-[#3b82f6] focus:ring-2 focus:ring-[#3b82f6]/10 outline-none transition-all"
        />
      </div>
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable class="w-[140px]">
        <el-option label="全部" value="" />
        <el-option label="启用" value="1" />
        <el-option label="禁用" value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <i class="ri-search-line mr-1"></i>搜索
      </el-button>
    </div>

    <!-- 用户列表表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <el-table
        :data="userList"
        stripe
        style="width: 100%"
        v-loading="tableLoading"
        :header-cell-style="{ background: '#f8fafc', fontWeight: 600, color: '#374151' }"
      >
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column
          v-if="isTeacher"
          prop="title"
          label="职称"
          width="120"
        >
          <template #default="{ row }">
            <el-tag size="small" :type="getTitleTagType(row.title)">{{ row.title }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          v-else
          prop="studentNo"
          label="学号"
          width="120"
        >
          <template #default="{ row }">
            <span class="font-mono text-sm">{{ row.studentNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="light">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后登录" width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.lastLoginTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="flex items-center gap-1">
              <el-button link type="primary" size="small" @click="openEditDialog(row)">
                <i class="ri-edit-line"></i>
              </el-button>
              <el-popconfirm title="确定要重置该用户的密码吗？" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleResetPassword(row)">
                <template #reference>
                  <el-button link type="warning" size="small"><i class="ri-lock-password-line"></i></el-button>
                </template>
              </el-popconfirm>
              <el-button
                :link="true"
                :type="row.status === 1 ? 'danger' : 'success'"
                size="small"
                @click="toggleStatus(row)"
              >
                <i :class="row.status === 1 ? 'ri-forbid-line' : 'ri-check-line'"></i>
              </el-button>
              <el-popconfirm :title="`确定要删除「${row.realName}」吗？此操作不可恢复`" confirm-button-text="确定" cancel-button-text="取消" @confirm="handleDelete(row.id)">
                <template #reference>
                  <el-button link type="danger" size="small"><i class="ri-delete-bin-line"></i></el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="flex justify-end bg-white p-4 rounded-xl shadow-sm">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalCount"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新建/编辑用户对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="isEditing ? `编辑${roleLabel}` : `添加${roleLabel}`"
      width="560px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px" class="pr-4">
        <el-form-item label="账号" prop="username">
          <el-input v-model="formData.username" placeholder="请输入登录账号" :disabled="isEditing" />
        </el-form-item>
        <el-form-item v-if="!isEditing" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入初始密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item v-if="isTeacher" label="职称" prop="title">
          <el-select v-model="formData.title" placeholder="请选择职称" class="w-full">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="批量导入用户" width="540px" destroy-on-close>
      <div class="space-y-4">
        <div class="flex items-start gap-2 p-3 bg-blue-50 rounded-lg text-sm text-blue-700">
          <i class="ri-information-line mt-0.5 flex-shrink-0"></i>
          <p>请按照模板格式填写用户信息后上传 Excel 文件。系统将自动校验数据并创建账号。</p>
        </div>
        <el-upload
          drag
          accept=".xlsx,.xls"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-remove="() => (uploadFile = null)"
          class="w-full"
        >
          <div class="flex flex-col items-center py-4">
            <i class="ri-file-excel-2-line text-4xl text-green-500 mb-2"></i>
            <p class="text-sm text-gray-600">将文件拖到此处，或<span class="text-[#3b82f6] cursor-pointer">点击上传</span></p>
            <p class="text-xs text-gray-400 mt-1">仅支持 .xlsx / .xls 格式</p>
          </div>
        </el-upload>
        <div class="flex items-center justify-between">
          <el-button link type="primary" @click="downloadTemplate">
            <i class="ri-download-2-line mr-1"></i>下载导入模板
          </el-button>
          <span v-if="uploadFile" class="text-sm text-gray-500">已选择: {{ uploadFile.name }}</span>
        </div>
        <div v-if="importResult.visible" class="border-t pt-4">
          <div class="flex items-center gap-2 mb-2">
            <i class="ri-checkbox-circle-fill" :class="importResult.success ? 'text-[#10b981]' : 'text-[#ef4444]'"></i>
            <span class="text-sm font-medium">{{ importResult.success ? '导入完成' : '导入失败' }}</span>
          </div>
          <div class="text-sm text-gray-500 space-y-1">
            <p>成功导入：<strong class="text-[#10b981]">{{ importResult.successCount }}</strong> 条</p>
            <p v-if="importResult.failCount > 0">失败：<strong class="text-[#ef4444]">{{ importResult.failCount }}</strong> 条</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="importDialogVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="!uploadFile" :loading="importLoading" @click="handleImportSubmit">确认导入</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPwdDialogVisible" title="重置密码" width="420px" destroy-on-close>
      <div class="py-4 space-y-4">
        <div class="flex items-center gap-3 p-4 bg-yellow-50 rounded-lg">
          <i class="ri-alert-line text-[#f59e0b] text-xl"></i>
          <div class="text-sm">
            <p class="font-medium text-gray-700">即将为以下用户重置密码：</p>
            <p class="mt-1 text-gray-600">{{ resetPwdTarget?.realName }}（{{ resetPwdTarget?.username }}）</p>
          </div>
        </div>
        <div class="bg-gray-50 rounded-lg p-4">
          <p class="text-sm text-gray-500 mb-2">新密码预览：</p>
          <div class="flex items-center gap-2">
            <code class="px-3 py-1.5 bg-white border border-gray-200 rounded-md font-mono text-lg tracking-widest text-[#3b82f6]">123456</code>
            <el-tag size="small" type="info">默认密码</el-tag>
          </div>
          <p class="text-xs text-gray-400 mt-2">提示：用户首次登录后建议修改默认密码</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="resetPwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResetPassword">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type UploadFile } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, resetUserPassword, updateUserStatus, importUsers, unwrapList, unwrapTotal } from '@/api/admin'

const route = useRoute()
const isTeacher = computed(() => route.meta.role === 'teacher')
const roleLabel = computed(() => isTeacher.value ? '教师' : '学生')
const pageTitle = computed(() => isTeacher.value ? '教师账号管理' : '学生账号管理')

// ==================== 类型定义 ====================
interface UserInfo {
  id: number
  username: string
  realName: string
  title?: string
  studentNo?: string
  email: string
  phone: string
  status: number
  lastLoginTime: string
}

// ==================== 搜索与筛选 ====================
const searchKeyword = ref('')
const statusFilter = ref('')
const tableLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)

// ==================== 用户列表数据 ====================
const userList = ref<UserInfo[]>([])

// 加载用户列表
async function loadUserList() {
  tableLoading.value = true
  try {
    const role = isTeacher.value ? 'teacher' : 'student'
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value,
      role,
    }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (statusFilter.value !== '') params.status = Number(statusFilter.value)
    
    const res = await getUserList(params)
    if (res) {
      const data = res as any
      const list = unwrapList(data)
      userList.value = list.map((u: any) => ({
        ...u,
        studentNo: u.studentNo || u.username,
      }))
      totalCount.value = unwrapTotal(data, list.length)
    }
  } catch (err) {
    console.error('加载用户列表失败', err)
    ElMessage.error('加载用户列表失败')
    userList.value = []
    totalCount.value = 0
  } finally {
    tableLoading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadUserList()
}
function handleSizeChange(val: number) { pageSize.value = val; currentPage.value = 1; loadUserList() }
function handlePageChange(val: number) { currentPage.value = val; loadUserList() }

// ==================== 表单相关 ====================
const formDialogVisible = ref(false)
const isEditing = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)

const formData = reactive({
  username: '',
  password: '',
  realName: '',
  title: '',
  email: '',
  phone: '',
})

const formRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: !isEditing.value, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
}

function openAddDialog() {
  isEditing.value = false
  Object.assign(formData, { username: '', password: '', realName: '', title: '', email: '', phone: '' })
  formDialogVisible.value = true
}

function openEditDialog(row: UserInfo) {
  isEditing.value = true
  editingId.value = row.id
  Object.assign(formData, {
    username: row.username,
    password: '',
    realName: row.realName,
    title: (row as any).title || '',
    email: row.email,
    phone: row.phone,
  })
  formDialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const role = isTeacher.value ? 'teacher' : 'student'
    if (isEditing.value && editingId.value !== null) {
      await updateUser(editingId.value, {
        ...formData,
        role,
      })
      ElMessage.success('更新成功')
    } else {
      await createUser({
        ...formData,
        role,
        status: 1,
      })
      ElMessage.success('创建成功')
    }
    formDialogVisible.value = false
    loadUserList()
  } catch (err) {
    console.error('提交失败', err)
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

// ==================== 操作方法 ====================
function getTitleTagType(title: string | undefined): 'success' | 'warning' | 'info' | 'danger' {
  switch (title) {
    case '教授': return 'info'
    case '副教授': return 'success'
    case '讲师': return 'warning'
    case '助教': return 'info'
    default: return 'info'
  }
}

async function toggleStatus(row: UserInfo) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(`已${newStatus === 1 ? '启用' : '禁用'} ${row.realName}`)
  } catch (err) {
    console.error('更新状态失败', err)
    ElMessage.error('更新状态失败')
  }
}

async function handleDelete(id: number) {
  try {
    await deleteUser(id)
    userList.value = userList.value.filter(u => u.id !== id)
    totalCount.value--
    ElMessage.success('删除成功')
  } catch (err) {
    console.error('删除失败', err)
    ElMessage.error('删除失败')
  }
}

// ==================== 重置密码 ====================
const resetPwdDialogVisible = ref(false)
const resetPwdTarget = ref<UserInfo | null>(null)

function handleResetPassword(row: UserInfo) {
  resetPwdTarget.value = row
  resetPwdDialogVisible.value = true
}

async function confirmResetPassword() {
  if (!resetPwdTarget.value) return
  try {
    await resetUserPassword(resetPwdTarget.value.id)
    ElMessage.success(`已重置 ${resetPwdTarget.value.realName} 的密码`)
    resetPwdDialogVisible.value = false
  } catch (err) {
    console.error('重置密码失败', err)
    ElMessage.error('重置密码失败')
  }
}

// ==================== 导出 ====================
function handleExport() {
  ElMessage.info('导出功能开发中')
}

// ==================== 批量导入 ====================
const importDialogVisible = ref(false)
const uploadFile = ref<UploadFile | null>(null)
const importLoading = ref(false)
const importResult = reactive({ visible: false, success: false, successCount: 0, failCount: 0 })

function handleFileChange(file: UploadFile) {
  uploadFile.value = file
  importResult.visible = false
}

function downloadTemplate() {
  ElMessage.info('正在下载导入模板...')
}

async function handleImportSubmit() {
  if (!uploadFile.value) return
  importLoading.value = true
  try {
    const file = uploadFile.value.raw
    if (!file) throw new Error('文件不存在')
    const res = await importUsers(file)
    importResult.visible = true
    importResult.success = true
    // 从后端返回结果中解析成功/失败数量
    const resultData = res as any
    importResult.successCount = resultData.successCount || resultData.importedCount || 0
    importResult.failCount = resultData.failCount || resultData.failedCount || 0
    ElMessage.success(`导入完成，成功 ${importResult.successCount} 条`)
    loadUserList()
  } catch (err: any) {
    console.error('导入失败', err)
    importResult.visible = true
    importResult.success = false
    importResult.successCount = 0
    importResult.failCount = err?.response?.data?.failCount || 0
    ElMessage.error(err?.response?.data?.message || '导入失败，请检查文件格式后重试')
  } finally {
    importLoading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserList()
})

watch(
  () => route.meta.role,
  () => {
    searchKeyword.value = ''
    statusFilter.value = ''
    currentPage.value = 1
    userList.value = []
    totalCount.value = 0
    loadUserList()
  },
)
</script>
