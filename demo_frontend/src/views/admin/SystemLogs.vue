<template>
  <div class="space-y-4">
    <!-- 标题区 -->
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-xl bg-[#3b82f6]/10 flex items-center justify-center">
        <i class="ri-file-list-3-line text-[#3b82f6] text-lg"></i>
      </div>
      <h1 class="text-xl font-bold text-gray-800">系统日志</h1>
    </div>

    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" class="bg-white rounded-xl shadow-sm px-5 pt-2" @tab-change="handleTabChange">
      <el-tab-pane label="登录日志" name="login">
        <template #label>
          <span class="flex items-center gap-1.5"><i class="ri-login-circle-line"></i>登录日志</span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="操作日志" name="operation">
        <template #label>
          <span class="flex items-center gap-1.5"><i class="ri-terminal-box-line"></i>操作日志</span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <!-- 筛选栏 -->
    <div class="bg-white p-4 rounded-xl shadow-sm -mt-3">
      <div class="flex flex-wrap gap-3 items-end">
        <div>
          <label class="block text-xs text-gray-500 mb-1">时间范围</label>
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            :shortcuts="dateShortcuts"
            class="!w-[360px]"
          />
        </div>
        <div>
          <label class="block text-xs text-gray-500 mb-1">{{ activeTab === 'login' ? '用户名' : '操作用户' }}</label>
          <input
            v-model="searchUser"
            type="text"
            :placeholder="activeTab === 'login' ? '搜索登录用户...' : '搜索操作用户...'"
            class="w-[180px] pl-8 pr-3 py-2 border border-gray-200 rounded-lg text-sm focus:border-[#3b82f6] focus:ring-2 focus:ring-[#3b82f6]/10 outline-none transition-all"
          />
        </div>
        <div>
          <label class="block text-xs text-gray-500 mb-1">状态</label>
          <el-select v-model="statusFilter" placeholder="全部状态" clearable class="w-[130px]">
            <el-option
              v-for="opt in statusOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </div>
        <div class="flex gap-2 ml-auto">
          <el-button @click="handleSearch">
            <i class="ri-search-line mr-1"></i>搜索
          </el-button>
          <el-button plain @click="handleReset">
            <i class="ri-refresh-line mr-1"></i>重置
          </el-button>
        </div>
      </div>
    </div>

    <!-- 日志列表表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <!-- 登录日志表格 -->
      <el-table
        v-if="activeTab === 'login'"
        :data="pagedLoginLogs"
        stripe
        style="width: 100%"
        v-loading="tableLoading"
        :header-cell-style="{ background: '#f8fafc', fontWeight: 600, color: '#374151' }"
      >
        <el-table-column prop="time" label="时间" min-width="170" fixed />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column label="角色" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'info' : row.role === 'TEACHER' ? 'success' : 'warning'" size="small" effect="light">
              {{ roleMap[row.role] || row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="140">
          <template #default="{ row }">
            <code class="text-xs text-gray-600">{{ row.ip }}</code>
          </template>
        </el-table-column>
        <el-table-column label="登录方式" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.method === '密码登录' ? 'info' : 'info'" effect="plain">{{ row.method }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small" effect="light">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="failReason" label="失败原因" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.failReason" class="text-red-500 text-sm">{{ row.failReason }}</span>
            <span v-else class="text-gray-300">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetailDrawer(row)">
              <i class="ri-eye-line mr-0.5"></i>详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 操作日志表格 -->
      <el-table
        v-if="activeTab === 'operation'"
        :data="pagedOperationLogs"
        stripe
        style="width: 100%"
        v-loading="tableLoading"
        :header-cell-style="{ background: '#f8fafc', fontWeight: 600, color: '#374151' }"
      >
        <el-table-column prop="time" label="时间" min-width="170" fixed />
        <el-table-column prop="operator" label="操作用户" width="120" />
        <el-table-column prop="actionType" label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="getActionTagType(row.actionType)" effect="plain">{{ row.actionType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="所属模块" width="110" />
        <el-table-column label="请求方法" width="85" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="getMethodColor(row.httpMethod)" effect="plain">{{ row.httpMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="130">
          <template #default="{ row }">
            <code class="text-xs text-gray-600">{{ row.ip }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时(ms)" width="95" align="right">
          <template #default="{ row }">
            <span :class="row.duration > 1000 ? 'text-red-500 font-medium' : 'text-gray-600'">{{ row.duration }}ms</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small" effect="light">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="flex justify-end bg-white p-4 rounded-xl shadow-sm">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="currentTotal"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 日志详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="activeTab === 'login' ? '登录日志详情' : '操作日志详情'"
      direction="rtl"
      size="520px"
      destroy-on-close
    >
      <template v-if="detailData">
        <!-- 基本信息 -->
        <div class="space-y-4 mb-6">
          <h3 class="text-sm font-semibold text-gray-700 pb-2 border-b border-gray-100">基本信息</h3>
          <div class="grid grid-cols-2 gap-y-3 text-sm">
            <div><span class="text-gray-400">时间：</span><span class="text-gray-700">{{ detailData.time }}</span></div>
            <div v-if="activeTab === 'login'">
              <span class="text-gray-400">用户名：</span><span class="text-gray-700">{{ (detailData as LoginLogItem).username }}</span>
            </div>
            <div v-else>
              <span class="text-gray-400">操作人：</span><span class="text-gray-700">{{ (detailData as OperationLogItem).operator }}</span>
            </div>
            <div><span class="text-gray-400">IP 地址：</span><code class="text-gray-700">{{ detailData.ip }}</code></div>
            <div v-if="activeTab === 'login'">
              <span class="text-gray-400">角色：</span>
              <el-tag size="small" :type="(detailData as LoginLogItem).role === 'ADMIN' ? 'info' : (detailData as LoginLogItem).role === 'TEACHER' ? 'success' : 'warning'" effect="light">
                {{ roleMap[(detailData as LoginLogItem).role] }}
              </el-tag>
            </div>
            <div v-if="activeTab === 'operation'">
              <span class="text-gray-400">耗时：</span>
              <span class="text-gray-700">{{ (detailData as OperationLogItem).duration }}ms</span>
            </div>
            <div><span class="text-gray-400">状态：</span>
              <el-tag :type="detailData.status === 'success' ? 'success' : 'danger'" size="small" effect="light">
                {{ detailData.status === 'success' ? '成功' : '失败' }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 请求参数 -->
        <div class="mb-6">
          <h3 class="text-sm font-semibold text-gray-700 pb-2 border-b border-gray-100 mb-3">请求参数</h3>
          <pre class="bg-slate-900 text-green-400 p-4 rounded-lg text-xs overflow-auto max-h-[280px] leading-relaxed font-mono">{{ formatJson(detailData.params) }}</pre>
        </div>

        <!-- 返回结果 -->
        <div>
          <h3 class="text-sm font-semibold text-gray-700 pb-2 border-b border-gray-100 mb-3">返回结果</h3>
          <pre class="bg-slate-900 text-blue-300 p-4 rounded-lg text-xs overflow-auto max-h-[280px] leading-relaxed font-mono">{{ formatJson(detailData.result) }}</pre>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getLoginLogs, getOperationLogs, unwrapList, unwrapTotal } from '@/api/admin'

// ==================== Tab 与筛选 ====================
const activeTab = ref('login')
const dateRange = ref<[string, string] | null>(null)
const searchUser = ref('')
const statusFilter = ref('')
const tableLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const loginTotal = ref(0)
const operationTotal = ref(0)

const statusOptions = computed(() =>
  activeTab.value === 'login'
    ? [
        { label: '全部', value: '' },
        { label: '登录成功', value: 'success' },
        { label: '登录失败', value: 'fail' },
      ]
    : [
        { label: '全部', value: '' },
        { label: '操作成功', value: 'success' },
        { label: '操作失败', value: 'fail' },
      ]
)

const dateShortcuts = [
  {
    text: '最近一小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000)
      return [start, end]
    },
  },
  {
    text: '今天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setHours(0, 0, 0, 0)
      return [start, end]
    },
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
]

function handleTabChange() {
  currentPage.value = 1
  searchUser.value = ''
  statusFilter.value = ''
  fetchLogs()
}
function handleSearch() { currentPage.value = 1; fetchLogs() }
function handleReset() {
  dateRange.value = null
  searchUser.value = ''
  statusFilter.value = ''
  currentPage.value = 1
  fetchLogs()
}
function handleSizeChange(val: number) { pageSize.value = val; currentPage.value = 1; fetchLogs() }
function handlePageChange(val: number) { currentPage.value = val; fetchLogs() }

// ==================== 类型定义 ====================
interface LoginLogItem {
  id: number
  time: string
  username: string
  role: string
  ip: string
  method: string
  status: 'success' | 'fail'
  failReason?: string
  params: Record<string, unknown>
  result: Record<string, unknown>
}

interface OperationLogItem {
  id: number
  time: string
  operator: string
  actionType: string
  module: string
  httpMethod: string
  ip: string
  duration: number
  status: 'success' | 'fail'
  params: Record<string, unknown>
  result: Record<string, unknown>
}

// ==================== 角色映射 & 辅助方法 ====================
const roleMap: Record<string, string> = {
  ADMIN: '管理员',
  TEACHER: '教师',
  STUDENT: '学生',
}

function getMethodColor(method: string): 'success' | 'warning' | 'danger' | 'info' {
  switch (method) {
    case 'GET': return 'info'
    case 'POST': return 'success'
    case 'PUT': return 'warning'
    case 'DELETE': return 'danger'
    default: return 'info'
  }
}

function getActionTagType(actionType: string): 'success' | 'warning' | 'danger' | 'info' {
  if (['新增', '创建'].includes(actionType)) return 'success'
  if (['修改', '更新', '编辑'].includes(actionType)) return 'warning'
  if (['删除', '移除'].includes(actionType)) return 'danger'
  if (['查询', '查看', '导出'].includes(actionType)) return 'info'
  return 'info'
}

function formatJson(data: unknown): string {
  try {
    return JSON.stringify(data, null, 2)
  } catch {
    return String(data)
  }
}

// ==================== 日志数据（从后端API获取）====================
const loginLogs = ref<LoginLogItem[]>([])
const operationLogs = ref<OperationLogItem[]>([])

// 加载日志数据
async function fetchLogs() {
  tableLoading.value = true
  try {
    if (activeTab.value === 'login') {
      const params: any = {
        page: currentPage.value,
        pageSize: pageSize.value,
      }
      if (searchUser.value) params.username = searchUser.value
      if (statusFilter.value) params.status = statusFilter.value === 'success' ? 1 : 0
      if (dateRange.value && dateRange.value[0]) params.startTime = dateRange.value[0]
      if (dateRange.value && dateRange.value[1]) params.endTime = dateRange.value[1]

      const res: any = await getLoginLogs(params)
      if (res) {
        const list = unwrapList(res)
        loginLogs.value = list.map((item: any) => ({
          id: item.id,
          time: item.createTime || item.time,
          username: item.username,
          role: item.role || '',
          ip: item.ipAddress || item.ip || '',
          method: item.method || '密码登录',
          status: item.status === 1 || item.status === 'success' ? 'success' : 'fail',
          failReason: item.failReason || (item.status === 0 || item.status === 'fail' ? '登录失败' : ''),
          params: item.params ? (typeof item.params === 'string' ? JSON.parse(item.params) : item.params) : {},
          result: item.result ? (typeof item.result === 'string' ? JSON.parse(item.result) : item.result) : {},
        }))
        loginTotal.value = unwrapTotal(res, list.length)
      }
    } else {
      const params: any = {
        page: currentPage.value,
        pageSize: pageSize.value,
      }
      if (searchUser.value) params.username = searchUser.value
      if (statusFilter.value) params.status = statusFilter.value
      if (dateRange.value && dateRange.value[0]) params.startTime = dateRange.value[0]
      if (dateRange.value && dateRange.value[1]) params.endTime = dateRange.value[1]

      const res: any = await getOperationLogs(params)
      if (res) {
        const list = unwrapList(res)
        operationLogs.value = list.map((item: any) => ({
          id: item.id,
          time: item.createTime || item.time,
          operator: item.username || item.operator,
          actionType: item.operation || item.actionType,
          module: item.module || '',
          httpMethod: item.method || item.httpMethod || 'GET',
          ip: item.ipAddress || item.ip || '',
          duration: item.executionTime || item.duration || 0,
          status: item.result === 'success' || item.status === 'success' ? 'success' : 'fail',
          params: item.params ? (typeof item.params === 'string' ? JSON.parse(item.params) : item.params) : {},
          result: item.result ? (typeof item.result === 'string' ? JSON.parse(item.result) : item.result) : {},
        }))
        operationTotal.value = unwrapTotal(res, list.length)
      }
    }
  } catch (error) {
    console.error('获取日志数据失败:', error)
    ElMessage.error('获取日志数据失败')
  } finally {
    tableLoading.value = false
  }
}

// ==================== 过滤与分页 ====================
const currentTotal = computed(() =>
  activeTab.value === 'login' ? loginTotal.value : operationTotal.value
)

const pagedLoginLogs = computed(() => loginLogs.value)
const pagedOperationLogs = computed(() => operationLogs.value)

// ==================== 详情抽屉 ====================
const drawerVisible = ref(false)
const detailData = ref<LoginLogItem | OperationLogItem | null>(null)

function openDetailDrawer(row: LoginLogItem | OperationLogItem) {
  detailData.value = row
  drawerVisible.value = true
}

// ==================== 生命周期 ====================
onMounted(() => {
  fetchLogs()
})

// 监听Tab切换时重新加载数据
watch(activeTab, () => {
  fetchLogs()
})
</script>
