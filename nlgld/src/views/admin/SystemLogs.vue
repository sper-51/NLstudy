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
            <el-tag :type="row.role === 'ADMIN' ? '' : row.role === 'TEACHER' ? 'success' : 'warning'" size="small" effect="light">
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
            <el-tag size="small" :type="row.method === '密码登录' ? '' : 'info'" effect="plain">{{ row.method }}</el-tag>
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
              <el-tag size="small" :type="(detailData as LoginLogItem).role === 'ADMIN' ? '' : (detailData as LoginLogItem).role === 'TEACHER' ? 'success' : 'warning'" effect="light">
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
import { ref, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// ==================== Tab 与筛选 ====================
const activeTab = ref('login')
const dateRange = ref<[string, string] | null>(null)
const searchUser = ref('')
const statusFilter = ref('')
const tableLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

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
}
function handleSearch() { currentPage.value = 1 }
function handleReset() {
  dateRange.value = null
  searchUser.value = ''
  statusFilter.value = ''
  currentPage.value = 1
}
function handleSizeChange(val: number) { pageSize.value = val; currentPage.value = 1 }
function handlePageChange(val: number) { currentPage.value = val }

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

function getMethodColor(method: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  switch (method) {
    case 'GET': return ''
    case 'POST': return 'success'
    case 'PUT': return 'warning'
    case 'DELETE': return 'danger'
    default: return 'info'
  }
}

function getActionTagType(actionType: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  if (['新增', '创建'].includes(actionType)) return 'success'
  if (['修改', '更新', '编辑'].includes(actionType)) return 'warning'
  if (['删除', '移除'].includes(actionType)) return 'danger'
  if (['查询', '查看', '导出'].includes(actionType)) return ''
  return 'info'
}

function formatJson(data: unknown): string {
  try {
    return JSON.stringify(data, null, 2)
  } catch {
    return String(data)
  }
}

// ==================== Mock 数据：登录日志（15+条）====================
// TODO: 替换为API调用
const loginLogs = ref<LoginLogItem[]>([
  { id: 1, time: '2026-06-03 08:00:05', username: 'admin', role: 'ADMIN', ip: '192.168.1.100', method: '密码登录', status: 'success', params: { username: 'admin', captcha: '****' }, result: { code: 200, message: '登录成功', token: 'eyJhbGciOiJIUzI1NiJ9...' } },
  { id: 2, time: '2026-06-03 07:55:12', username: 'zhang_wei', role: 'TEACHER', ip: '10.0.0.45', method: '密码登录', status: 'success', params: { username: 'zhang_wei', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 3, time: '2026-06-03 07:52:33', username: 'stu_2023001', role: 'STUDENT', ip: '172.16.5.22', method: '密码登录', status: 'success', params: { username: 'stu_2023001', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 4, time: '2026-06-02 23:45:01', username: 'li_na', role: 'TEACHER', ip: '10.0.0.46', method: '密码登录', status: 'success', params: { username: 'li_na', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 5, time: '2026-06-02 23:30:18', username: 'admin', role: 'ADMIN', ip: '192.168.1.101', method: '密码登录', status: 'fail', failReason: '验证码错误', params: { username: 'admin', captcha: 'ABCD' }, result: { code: 401, message: '验证码错误' } },
  { id: 6, time: '2026-06-02 23:28:45', username: 'admin', role: 'ADMIN', ip: '192.168.1.101', method: '密码登录', status: 'fail', failReason: '账号或密码错误', params: { username: 'admin', password: '******' }, result: { code: 401, message: '账号或密码错误' } },
  { id: 7, time: '2026-06-02 22:15:09', username: 'wang_fang', role: 'TEACHER', ip: '10.0.0.47', method: '密码登录', status: 'success', params: { username: 'wang_fang', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 8, time: '2026-06-02 21:40:33', username: 'stu_2023015', role: 'STUDENT', ip: '172.16.5.33', method: '密码登录', status: 'success', params: { username: 'stu_2023015', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 9, time: '2026-06-02 20:18:42', username: 'zhao_xue', role: 'STUDENT', ip: '172.16.5.18', method: '密码登录', status: 'success', params: { username: 'zhao_xue', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 10, time: '2026-06-02 19:05:11', username: 'unknown_user', role: '', ip: '203.0.113.50', method: '密码登录', status: 'fail', failReason: '账号不存在', params: { username: 'unknown_user', password: '******' }, result: { code: 404, message: '账号不存在' } },
  { id: 11, time: '2026-06-02 17:32:00', username: 'liu_jun', role: 'TEACHER', ip: '10.0.0.48', method: '密码登录', status: 'fail', failReason: '账号已被禁用', params: { username: 'liu_jun', captcha: '****' }, result: { code: 403, message: '账号已被禁用，请联系管理员' } },
  { id: 12, time: '2026-06-02 16:20:44', username: 'admin', role: 'ADMIN', ip: '192.168.1.100', method: '密码登录', status: 'success', params: { username: 'admin', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 13, time: '2026-06-02 14:55:27', username: 'stu_2023032', role: 'STUDENT', ip: '172.16.5.41', method: '密码登录', status: 'fail', failReason: '账号已被禁用', params: { username: 'stu_2023032', captcha: '****' }, result: { code: 403, message: '账号已被禁用，请联系管理员' } },
  { id: 14, time: '2026-06-02 13:10:08', username: 'zhang_wei', role: 'TEACHER', ip: '10.0.0.45', method: '密码登录', status: 'success', params: { username: 'zhang_wei', captcha: '****' }, result: { code: 200, message: '登录成功' } },
  { id: 15, time: '2026-06-02 11:45:36', username: 'admin', role: 'ADMIN', ip: '192.168.1.102', method: '密码登录', status: 'fail', failReason: '密码错误次数超限，账户已锁定15分钟', params: { username: 'admin', password: '******' }, result: { code: 429, message: '密码错误次数过多，请15分钟后重试' } },
  { id: 16, time: '2026-06-02 10:22:19', username: 'stu_2023002', role: 'STUDENT', ip: '172.16.5.25', method: '密码登录', status: 'success', params: { username: 'stu_2023002', captcha: '****' }, result: { code: 200, message: '登录成功' } },
])

// ==================== Mock 数据：操作日志（15+条）====================
// TODO: 替换为API调用
const operationLogs = ref<OperationLogItem[]>([
  { id: 101, time: '2026-06-03 08:05:12', operator: 'admin', actionType: '创建', module: '考试管理', httpMethod: 'POST', ip: '192.168.1.100', duration: 235, status: 'success', params: { examName: '2026春季期末考试', subjectId: 3, startTime: '2026-07-10 09:00:00' }, result: { code: 200, data: { examId: 56 } } },
  { id: 102, time: '2026-06-03 07:58:33', operator: 'zhang_wei', actionType: '修改', module: '题库管理', httpMethod: 'PUT', ip: '10.0.0.45', duration: 156, status: 'success', params: { questionId: 1205, content: '修改后的题目内容...', difficulty: 3 }, result: { code: 200, message: '更新成功' } },
  { id: 103, time: '2026-06-03 07:45:08', operator: 'admin', actionType: '导出', module: '成绩管理', httpMethod: 'GET', ip: '192.168.1.100', duration: 1245, status: 'success', params: { examId: 45, format: 'xlsx' }, result: { code: 200, fileUrl: '/downloads/score_20260603.xlsx' } },
  { id: 104, time: '2026-06-02 23:10:22', operator: 'li_na', actionType: '新增', module: '班级管理', httpMethod: 'POST', ip: '10.0.0.46', duration: 89, status: 'success', params: { className: '软件工程2301班', grade: '2023', major: '软件工程' }, result: { code: 200, data: { classId: 78 } } },
  { id: 105, time: '2026-06-02 22:35:45', operator: 'admin', actionType: '删除', module: '题库管理', httpMethod: 'DELETE', ip: '192.168.1.100', duration: 67, status: 'fail', params: { questionIds: [1198, 1199, 1200] }, result: { code: 500, message: '部分题目已被引用，无法删除' } },
  { id: 106, time: '2026-06-02 21:48:11', operator: 'wang_fang', actionType: '编辑', module: '考试管理', httpMethod: 'PUT', ip: '10.0.0.47', duration: 312, status: 'success', params: { examId: 54, endTime: '2026-07-10 11:30:00', totalScore: 100 }, result: { code: 200, message: '更新成功' } },
  { id: 107, time: '2026-06-02 20:22:39', operator: 'admin', actionType: '查询', module: '用户管理', httpMethod: 'GET', ip: '192.168.1.100', duration: 45, status: 'success', params: { role: 'student', keyword: '', page: 1, size: 20 }, result: { code: 200, data: { total: 3842, list: [] } } },
  { id: 108, time: '2026-06-02 19:15:04', operator: 'zhang_wei', actionType: '导入', module: '题库管理', httpMethod: 'POST', ip: '10.0.0.45', duration: 2340, status: 'success', params: { file: 'questions_batch.xlsx', category: '选择题' }, result: { code: 200, importedCount: 150, failedCount: 3 } },
  { id: 109, time: '2026-06-02 17:55:28', operator: 'admin', actionType: '修改', module: '学期管理', httpMethod: 'PUT', ip: '192.168.1.100', duration: 98, status: 'success', params: { semesterId: 12, name: '2025-2026学年第二学期', status: 'active' }, result: { code: 200, message: '更新成功' } },
  { id: 110, time: '2026-06-02 16:30:17', operator: 'li_na', actionType: '发布', module: '考试管理', httpMethod: 'POST', ip: '10.0.0.46', duration: 178, status: 'success', params: { examId: 53, notifyStudents: true }, result: { code: 200, message: '考试已发布并通知学生' } },
  { id: 111, time: '2026-06-02 15:12:50', operator: 'admin', actionType: '删除', module: '班级管理', httpMethod: 'DELETE', ip: '192.168.1.100', duration: 112, status: 'success', params: { classId: 75, force: true }, result: { code: 200, message: '班级已删除' } },
  { id: 112, time: '2026-06-02 14:08:33', operator: 'wang_fang', actionType: '批改', module: '成绩管理', httpMethod: 'POST', ip: '10.0.0.47', duration: 567, status: 'success', params: { examId: 51, studentId: 201, score: 85, comment: '答题规范，思路清晰' }, result: { code: 200, message: '评分已保存' } },
  { id: 113, time: '2026-06-02 12:45:09', operator: 'admin', actionType: '备份', module: '数据管理', httpMethod: 'POST', ip: '192.168.1.100', duration: 1856, status: 'success', params: { type: 'full_backup', compress: true }, result: { code: 200, backupFile: 'backup_20260602_124509.sql.gz', size: '247.3MB' } },
  { id: 114, time: '2026-06-02 11:20:46', operator: 'zhang_wei', actionType: '创建', module: '题库管理', httpMethod: 'POST', ip: '10.0.0.45', duration: 134, status: 'fail', params: { content: '', difficulty: 2, options: ['A', 'B'] }, result: { code: 400, message: '题目内容不能为空' } },
  { id: 115, time: '2026-06-02 10:05:22', operator: 'admin', actionType: '查看', module: '系统监控', httpMethod: 'GET', ip: '192.168.1.100', duration: 38, status: 'success', params: { metric: 'online_users' }, result: { code: 200, data: { onlineCount: 127, peakToday: 189 } } },
  { id: 116, time: '2026-06-02 09:30:15', operator: 'li_na', actionType: '修改', module: '用户管理', httpMethod: 'PUT', ip: '10.0.0.46', duration: 201, status: 'success', params: { userId: 204, status: 0, reason: '长期未使用' }, result: { code: 200, message: '用户已禁用' } },
  { id: 117, time: '2026-06-02 08:15:43', operator: 'system', actionType: '自动清理', module: '数据管理', httpMethod: 'POST', ip: '127.0.0.1', duration: 3421, status: 'success', params: { task: 'clean_expired_tokens', days: 30 }, result: { code: 200, cleanedRows: 1523 } },
])

// ==================== 过滤与分页 ====================
function filterLogs<T extends LoginLogItem | OperationLogItem>(logs: T[]): T[] {
  let list = [...logs]
  if (searchUser.value) {
    const kw = searchUser.value.toLowerCase()
    const field = activeTab.value === 'login' ? 'username' : 'operator'
    list = list.filter((log: any) => (log[field] as string)?.toLowerCase().includes(kw))
  }
  if (statusFilter.value) {
    list = list.filter((log: any) => log.status === statusFilter.value)
  }
  if (dateRange.value && dateRange.value[0] && dateRange.value[1]) {
    list = list.filter((log: any) => log.time >= dateRange.value![0] && log.time <= dateRange.value![1])
  }
  return list
}

const filteredLoginLogs = computed(() => filterLogs(loginLogs.value))
const filteredOperationLogs = computed(() => filterLogs(operationLogs.value))

const currentTotal = computed(() =>
  activeTab.value === 'login' ? filteredLoginLogs.value.length : filteredOperationLogs.value.length
)

const pagedLoginLogs = computed(() =>
  filteredLoginLogs.value.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
)

const pagedOperationLogs = computed(() =>
  filteredOperationLogs.value.slice(
    (currentPage.value - 1) * pageSize.value,
    currentPage.value * pageSize.value
  )
)

// ==================== 详情抽屉 ====================
const drawerVisible = ref(false)
const detailData = ref<LoginLogItem | OperationLogItem | null>(null)

function openDetailDrawer(row: LoginLogItem | OperationLogItem) {
  detailData.value = row
  drawerVisible.value = true
}
</script>
