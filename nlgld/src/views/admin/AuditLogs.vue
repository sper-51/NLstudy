<template>
  <div class="min-h-screen bg-[#F5F7FA] p-6">
    <!-- 标题栏 -->
    <div class="flex items-center gap-3 mb-6">
      <div class="w-10 h-10 rounded-xl bg-primary-500/10 flex items-center justify-center">
        <i class="ri-file-list-3-line text-primary-500 text-xl"></i>
      </div>
      <h1 class="text-xl font-bold text-gray-800">操作日志审计</h1>
    </div>

    <!-- 统计概览 -->
    <div class="grid grid-cols-5 gap-4 mb-6">
      <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-blue-500">
        <p class="text-xs text-gray-500 mb-1">今日操作总数</p>
        <p class="text-2xl font-bold text-blue-600">{{ stats.totalToday }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-emerald-500">
        <p class="text-xs text-gray-500 mb-1">登录次数</p>
        <p class="text-2xl font-bold text-emerald-600">{{ stats.loginCount }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-red-500">
        <p class="text-xs text-gray-500 mb-1">异常操作数</p>
        <p class="text-2xl font-bold text-red-600">{{ stats.errorCount }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-purple-500">
        <p class="text-xs text-gray-500 mb-1">最活跃用户</p>
        <p class="text-base font-bold text-purple-600 truncate">{{ stats.activeUser }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-orange-500">
        <p class="text-xs text-gray-500 mb-1">平均响应时间</p>
        <p class="text-2xl font-bold text-orange-600">{{ stats.avgResponseTime }}ms</p>
      </div>
    </div>

    <!-- 高级筛选面板 -->
    <div class="bg-white rounded-xl shadow-sm p-5 mb-4">
      <el-collapse v-model="filterExpanded" class="border-none">
        <el-collapse-item name="filter" :class="'!border-none'">
          <template #title>
            <div class="flex items-center gap-2 px-1">
              <i class="ri-filter-3-line text-primary-500"></i>
              <span class="font-medium text-gray-700 text-sm">高级筛选</span>
            </div>
          </template>
          <div class="grid grid-cols-3 gap-4 mt-2">
            <!-- 行1: 时间范围 + 操作类型 -->
            <el-date-picker
              v-model="filterForm.dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
            <el-select v-model="filterForm.operationType" placeholder="操作类型" clearable style="width: 100%">
              <el-option label="全部" value="" />
              <el-option label="新增 (CREATE)" value="CREATE" />
              <el-option label="查询 (READ)" value="READ" />
              <el-option label="更新 (UPDATE)" value="UPDATE" />
              <el-option label="删除 (DELETE)" value="DELETE" />
              <el-option label="登录 (LOGIN)" value="LOGIN" />
              <el-option label="导出 (EXPORT)" value="EXPORT" />
            </el-select>
            <div></div> <!-- 占位 -->

            <!-- 行2: 用户名 + 模块 -->
            <el-input v-model="filterForm.username" placeholder="请输入用户名" clearable prefix-icon="User" />
            <el-select v-model="filterForm.module" placeholder="所属模块" clearable style="width: 100%">
              <el-option label="全部" value="" />
              <el-option label="用户管理" value="用户管理" />
              <el-option label="课程管理" value="课程管理" />
              <el-option label="题库管理" value="题库管理" />
              <el-option label="考试管理" value="考试管理" />
              <el-option label="成绩管理" value="成绩管理" />
              <el-option label="系统设置" value="系统设置" />
            </el-select>
            <div></div>

            <!-- 行3: 状态 + IP地址 -->
            <el-select v-model="filterForm.status" placeholder="操作状态" clearable style="width: 100%">
              <el-option label="全部" value="" />
              <el-option label="成功" value="SUCCESS" />
              <el-option label="失败" value="FAILED" />
            </el-select>
            <el-input v-model="filterForm.ipAddress" placeholder="IP地址" clearable prefix-icon="Position" />

            <!-- 操作按钮 -->
            <div class="flex gap-2">
              <el-button type="primary" @click="handleSearch">
                <i class="ri-search-line mr-1"></i>搜索
              </el-button>
              <el-button @click="handleReset">
                <i class="ri-refresh-line mr-1"></i>重置
              </el-button>
              <el-button type="warning" plain @click="handleExport">
                <i class="ri-download-line mr-1"></i>导出
              </el-button>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 审计日志表格 -->
    <div class="bg-white rounded-xl shadow-sm p-5 mb-6">
      <el-table :data="pagedLogs" stripe border style="width: 100%" size="default">
        <el-table-column prop="createTime" label="时间" width="170" fixed>
          <template #default="{ row }">{{ row.createTime }}</template>
        </el-table-column>
        <el-table-column label="操作用户" width="120">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-avatar :size="24" :style="{ backgroundColor: getAvatarColor(row.username) }" class="flex-shrink-0">
                {{ row.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="text-sm font-medium">{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getOperationTagType(row.operation)" size="small" effect="light">
              {{ getOperationLabel(row.operation) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属模块" width="110" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small" effect="plain">{{ row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="method" label="请求方法" width="100" align="center">
          <template #default="{ row }">
            <span class="font-mono text-xs bg-slate-100 px-1.5 py-0.5 rounded text-gray-500">{{ row.method }}</span>
          </template>
        </el-table-column>
        <el-table-column label="请求参数" min-width="180">
          <template #default="{ row }">
            <el-tooltip :content="row.params" placement="top" :show-after="300" effect="dark">
              <span class="text-xs text-gray-600 truncate block max-w-[200px] cursor-pointer hover:text-primary-500">{{ row.params }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.result === 'SUCCESS' ? 'success' : 'danger'" size="small">
              {{ row.result === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="错误信息" min-width="140">
          <template #default="{ row }">
            <span v-if="row.errorMessage" class="text-xs text-red-500 truncate block max-w-[160px]">{{ row.errorMessage }}</span>
            <span v-else class="text-xs text-gray-300">-</span>
          </template>
        </el-table-column>
        <el-table-column label="执行耗时" width="90" align="center">
          <template #default="{ row }">
            <span :class="row.executionTime > 500 ? 'text-red-500 font-medium' : 'text-gray-600'">{{ row.executionTime }}ms</span>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openDetailDrawer(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="flex justify-end mt-4">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredLogs.length"
          layout="total, sizes, prev, pager, next, jumper"
          background
          small
        />
      </div>
    </div>

    <!-- 操作频率热力图 -->
    <div class="bg-white rounded-xl shadow-sm p-5 mb-6">
      <div class="flex items-center gap-2 mb-4">
        <i class="ri-bar-chart-grouped-line text-primary-500"></i>
        <h2 class="text-base font-semibold text-gray-800">操作频率分布（最近7天）</h2>
      </div>
      <div ref="heatmapChartRef" style="height: 280px; width: 100%"></div>
    </div>

    <!-- 日志详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="日志详情" direction="rtl" size="480px">
      <template v-if="currentLog">
        <div class="space-y-5">
          <!-- 基本信息 -->
          <div>
            <h3 class="text-sm font-semibold text-gray-700 mb-3 pb-2 border-b border-gray-100">基本信息</h3>
            <div class="space-y-2.5">
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">时间：</span><span class="text-gray-700">{{ currentLog.createTime }}</span></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">操作用户：</span><span class="text-gray-700">{{ currentLog.username }}</span></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">操作类型：</span><el-tag :type="getOperationTagType(currentLog.operation)" size="small">{{ getOperationLabel(currentLog.operation) }}</el-tag></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">所属模块：</span><el-tag type="info" size="small">{{ currentLog.module }}</el-tag></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">请求方法：</span><span class="font-mono text-xs bg-slate-100 px-1.5 py-0.5 rounded text-gray-600">{{ currentLog.method }}</span></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">请求路径：</span><span class="text-gray-700 break-all">{{ currentLog.url || '/api/admin/users' }}</span></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">执行耗时：</span><span :class="currentLog.executionTime > 500 ? 'text-red-500 font-medium' : 'text-gray-700'">{{ currentLog.executionTime }}ms</span></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">操作结果：</span><el-tag :type="currentLog.result === 'SUCCESS' ? 'success' : 'danger'" size="small">{{ currentLog.result === 'SUCCESS' ? '成功' : '失败' }}</el-tag></div>
              <div class="flex text-sm"><span class="text-gray-400 w-24 flex-shrink-0">IP地址：</span><span class="text-gray-700">{{ currentLog.ipAddress }}</span></div>
            </div>
          </div>

          <!-- 请求参数 -->
          <div>
            <h3 class="text-sm font-semibold text-gray-700 mb-3 pb-2 border-b border-gray-100">请求参数</h3>
            <pre class="bg-slate-900 text-green-400 p-3 rounded-lg text-xs overflow-auto max-h-[200px] leading-relaxed">{{ formatJson(currentLog.params) }}</pre>
          </div>

          <!-- 错误信息（仅失败时显示） -->
          <div v-if="currentLog.errorMessage">
            <h3 class="text-sm font-semibold text-gray-700 mb-3 pb-2 border-b border-gray-100">错误信息</h3>
            <pre class="bg-red-50 text-red-600 p-3 rounded-lg text-xs overflow-auto border border-red-200">{{ currentLog.errorMessage }}</pre>
          </div>

          <!-- 关联信息 -->
          <div>
            <h3 class="text-sm font-semibold text-gray-700 mb-3 pb-2 border-b border-gray-100">关联信息</h3>
            <div class="space-y-2.5">
              <div class="flex text-sm"><span class="text-gray-400 w-28 flex-shrink-0">用户代理：</span><span class="text-gray-600 text-xs break-all">{{ currentLog.userAgent || 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0' }}</span></div>
              <div class="flex text-sm"><span class="text-gray-400 w-28 flex-shrink-0">IP地理位置：</span><span class="text-gray-700">{{ ipLocation }}</span></div>
              <div class="flex text-sm"><span class="text-gray-400 w-28 flex-shrink-0">会话ID：</span><span class="font-mono text-xs text-gray-500">{{ sessionId }}</span></div>
            </div>
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// ==================== 类型定义 ====================
interface AuditLog {
  id: number
  createTime: string
  username: string
  operation: string
  module: string
  method: string
  params: string
  result: 'SUCCESS' | 'FAILED'
  errorMessage?: string
  executionTime: number
  ipAddress: string
  url?: string
  userAgent?: string
}

// ==================== 响应式数据 ====================
const filterExpanded = ref('filter')
const currentPage = ref(1)
const pageSize = ref(10)
const drawerVisible = ref(false)
const currentLog = ref<AuditLog | null>(null)
const heatmapChartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// IP地理位置 mock
const ipLocation = ref('中国 · 北京市 · 朝阳区')
const sessionId = ref('sess_' + Math.random().toString(36).substring(2, 14))

const filterForm = ref({
  dateRange: null as [string, string] | null,
  operationType: '',
  username: '',
  module: '',
  status: '',
  ipAddress: '',
})

const stats = ref({
  totalToday: 1286,
  loginCount: 342,
  errorCount: 18,
  activeUser: 'admin',
  avgResponseTime: 86,
})

// TODO: 替换为API调用
const allLogs = ref<AuditLog[]>([
  { id: 1, createTime: '2024-06-03 09:33:12', username: 'admin', operation: 'LOGIN', module: '系统设置', method: 'POST', params: '{"username":"admin","password":"***"}', result: 'SUCCESS', executionTime: 125, ipAddress: '192.168.1.10', url: '/api/auth/login' },
  { id: 2, createTime: '2024-06-03 09:32:45', username: 'zhangsan', operation: 'CREATE', module: '题库管理', method: 'POST', params: '{"title":"Java基础选择题","subjectId":3,"count":20}', result: 'SUCCESS', executionTime: 234, ipAddress: '192.168.1.101', url: '/api/questions/batch-create' },
  { id: 3, createTime: '2024-06-03 09:31:20', username: 'lisi', operation: 'UPDATE', module: '考试管理', method: 'PUT', params: '{"examId":15,"name":"期末考试A卷","duration":120,"status":"PUBLISHED"}', result: 'SUCCESS', executionTime: 89, ipAddress: '192.168.1.102', url: '/api/exams/15' },
  { id: 4, createTime: '2024-06-03 09:30:08', username: 'wangwu', operation: 'READ', module: '成绩管理', method: 'GET', params: '{"examId":12,"classId":5,"page":1,"size":20}', result: 'SUCCESS', executionTime: 56, ipAddress: '192.168.1.103', url: '/api/scores/list' },
  { id: 5, createTime: '2024-06-03 09:29:55', username: 'zhaoliu', operation: 'DELETE', module: '用户管理', method: 'DELETE', params: '{"userId":208,"reason":"毕业离校"}', result: 'FAILED', executionTime: 678, errorMessage: '权限不足：无法删除拥有活跃考试记录的用户', ipAddress: '192.168.1.104', url: '/api/users/208' },
  { id: 6, createTime: '2024-06-03 09:28:30', username: 'sunqi', operation: 'EXPORT', module: '成绩管理', method: 'POST', params: '{"examId":14,"format":"xlsx","includeDetails":true}', result: 'SUCCESS', executionTime: 1523, ipAddress: '192.168.1.105', url: '/api/scores/export' },
  { id: 7, createTime: '2024-06-03 09:27:15', username: 'zhouba', operation: 'CREATE', module: '课程管理', method: 'POST', params: '{"name":"高等数学II","semesterId":8,"teacherId":3}', result: 'SUCCESS', executionTime: 145, ipAddress: '192.168.1.106', url: '/api/courses' },
  { id: 8, createTime: '2024-06-03 09:26:42', username: 'wujiu', operation: 'LOGIN', module: '系统设置', method: 'POST', params: '{"username":"wujiu","password":"***"}', result: 'SUCCESS', executionTime: 98, ipAddress: '192.168.1.107', url: '/api/auth/login' },
  { id: 9, createTime: '2024-06-03 09:25:30', username: 'zhengshi', operation: 'UPDATE', module: '用户管理', method: 'PUT', params: '{"userId":156,"role":"TEACHER","departments":[1,3]}', result: 'SUCCESS', executionTime: 201, ipAddress: '192.168.1.108', url: '/api/users/156' },
  { id: 10, createTime: '2024-06-03 09:24:18', username: 'chenyi', operation: 'READ', module: '题库管理', method: 'GET', params: '{"keyword":"数组","page":1,"size":15}', result: 'SUCCESS', executionTime: 43, ipAddress: '192.168.1.109', url: '/api/questions/search' },
  { id: 11, createTime: '2024-06-03 09:23:05', username: 'huanger', operation: 'CREATE', module: '班级管理', method: 'POST', params: '{"name":"软件工程2401","grade":"2024","majorId":7}', result: 'FAILED', executionTime: 312, errorMessage: '班级名称已存在，请使用其他名称', ipAddress: '192.168.1.110', url: '/api/classes' },
  { id: 12, createTime: '2024-06-03 09:22:40', username: 'liusan', operation: 'DELETE', module: '考试管理', method: 'DELETE', params: '{"examId":22}', result: 'SUCCESS', executionTime: 167, ipAddress: '192.168.1.111', url: '/api/exams/22' },
  { id: 13, createTime: '2024-06-03 09:21:22', username: 'yangsi', operation: 'UPDATE', module: '成绩管理', method: 'PUT', params: '{"scoreId":892,"score":92,"comment":"优秀"}', result: 'SUCCESS', executionTime: 76, ipAddress: '192.168.1.112', url: '/api/scores/892' },
  { id: 14, createTime: '2024-06-03 09:20:10', username: 'xuwu', operation: 'LOGIN', module: '系统设置', method: 'POST', params: '{"username":"xuwu","password":"***"}', result: 'SUCCESS', executionTime: 110, ipAddress: '192.168.1.113', url: '/api/auth/login' },
  { id: 15, createTime: '2024-06-03 09:19:35', username: 'heliu', operation: 'EXPORT', module: '题库管理', method: 'POST', params: '{"subjectId":3,"format":"csv"}', result: 'SUCCESS', executionTime: 2156, ipAddress: '192.168.1.114', url: '/api/questions/export' },
  { id: 16, createTime: '2024-06-03 09:18:20', username: 'luoqi', operation: 'READ', module: '用户管理', method: 'GET', params: '{"role":"STUDENT","classId":8,"page":1,"size":30}', result: 'SUCCESS', executionTime: 38, ipAddress: '192.168.1.115', url: '/api/users/list' },
  { id: 17, createTime: '2024-06-03 09:17:05', username: 'zhangsan', operation: 'UPDATE', module: '课程管理', method: 'PUT', params: '{"courseId":45,"schedule":[{"day":1,"periods":[3,4]}]}', result: 'SUCCESS', executionTime: 189, ipAddress: '192.168.1.101', url: '/api/courses/45' },
  { id: 18, createTime: '2024-06-03 09:16:48', username: 'lisi', operation: 'CREATE', module: '考试管理', method: 'POST', params: '{"name":"期中测验B","courseId":23,"startTime":"2024-06-10 09:00"}', result: 'FAILED', executionTime: 445, errorMessage: '该时间段存在冲突，已有其他考试安排', ipAddress: '192.168.1.102', url: '/api/exams' },
  { id: 19, createTime: '2024-06-03 09:15:30', username: 'wangwu', operation: 'DELETE', module: '题库管理', method: 'DELETE', params: '{"questionIds":[567,568,569]}', result: 'SUCCESS', executionTime: 132, ipAddress: '192.168.1.103', url: '/api/questions/batch-delete' },
  { id: 20, createTime: '2024-06-03 09:14:12', username: 'zhaoliu', operation: 'READ', module: '课程管理', method: 'GET', params: '{"semesterId":8,"teacherId":3}', result: 'SUCCESS', executionTime: 51, ipAddress: '192.168.1.104', url: '/api/courses/my-courses' },
  { id: 21, createTime: '2024-06-03 09:13:00', username: 'sunqi', operation: 'CREATE', module: '用户管理', method: 'POST', params: '{"username":"newstudent01","realName":"新学生","role":"STUDENT","classId":10}', result: 'SUCCESS', executionTime: 278, ipAddress: '192.168.1.105', url: '/api/users/create' },
  { id: 22, createTime: '2024-06-03 09:12:35', username: 'zhouba', operation: 'UPDATE', module: '系统设置', method: 'PUT', params: '{"key":"system.name","value":"智考平台V2"}', result: 'SUCCESS', executionTime: 64, ipAddress: '192.168.1.106', url: '/api/settings/update' },
  { id: 23, createTime: '2024-06-03 09:11:20', username: 'wujiu', operation: 'EXPORT', module: '考试管理', method: 'POST', params: '{"examId":11,"format":"pdf","withAnalysis":true}', result: 'SUCCESS', executionTime: 1890, ipAddress: '192.168.1.107', url: '/api/exams/export-report' },
  { id: 24, createTime: '2024-06-03 09:10:05', username: 'zhengshi', operation: 'LOGIN', module: '系统设置', method: 'POST', params: '{"username":"zhengshi","password":"***"}', result: 'FAILED', executionTime: 520, errorMessage: '密码错误，剩余尝试次数: 3', ipAddress: '192.168.1.108', url: '/api/auth/login' },
  { id: 25, createTime: '2024-06-03 09:09:40', username: 'chenyi', operation: 'READ', module: '考试管理', method: 'GET', params: '{"status":"UPCOMING","page":1,"size":10}', result: 'SUCCESS', executionTime: 47, ipAddress: '192.168.1.109', url: '/api/exams/upcoming' },
])

// ==================== 计算属性 ====================
const filteredLogs = computed(() => {
  let list = [...allLogs.value]
  if (filterForm.value.operationType) list = list.filter(l => l.operation === filterForm.value.operationType)
  if (filterForm.value.username) list = list.filter(l => l.username.toLowerCase().includes(filterForm.value.username!.toLowerCase()))
  if (filterForm.value.module) list = list.filter(l => l.module === filterForm.value.module)
  if (filterForm.value.status) list = list.filter(l => l.result === filterForm.value.status)
  if (filterForm.value.ipAddress) list = list.filter(l => l.ipAddress.includes(filterForm.value.ipAddress!))
  return list
})

const pagedLogs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredLogs.value.slice(start, start + pageSize.value)
})

// ==================== 方法 ====================
function getAvatarColor(name: string): string {
  const colors = ['#3b82f6', '#10b981', '#8b5cf6', '#f59e0b', '#ef4444', '#ec4899', '#06b6d4', '#84cc16']
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return colors[Math.abs(hash) % colors.length]
}

function getOperationTagType(operation: string): '' | 'success' | 'warning' | 'danger' | 'info' {
  const map: Record<string, '' | 'success' | 'warning' | 'danger' | 'info'> = {
    CREATE: 'success',
    READ: 'info',
    UPDATE: 'warning',
    DELETE: 'danger',
    LOGIN: '',
    EXPORT: 'warning',
  }
  return map[operation] ?? 'info'
}

function getOperationLabel(operation: string): string {
  const map: Record<string, string> = {
    CREATE: '新增',
    READ: '查询',
    UPDATE: '修改',
    DELETE: '删除',
    LOGIN: '登录',
    EXPORT: '导出',
  }
  return map[operation] ?? operation
}

function formatJson(str: string): string {
  try {
    const obj = JSON.parse(str.replace(/"password"\s*:\s*"[^"]*"/g, '"password":"***"'))
    return JSON.stringify(obj, null, 2)
  } catch {
    return str
  }
}

function handleSearch() {
  currentPage.value = 1
  ElMessage.success(`筛选完成，共 ${filteredLogs.value.length} 条记录`)
}

function handleReset() {
  filterForm.value = { dateRange: null, operationType: '', username: '', module: '', status: '', ipAddress: '' }
  currentPage.value = 1
  ElMessage.info('已重置所有筛选条件')
}

function handleExport() {
  ElMessage.success('正在导出日志数据，请稍候...')
  // TODO: 替换为API调用
}

function openDetailDrawer(log: AuditLog) {
  currentLog.value = log
  // 随机生成mock地理位置
  const locations = ['中国 · 北京市 · 海淀区', '中国 · 上海市 · 浦东新区', '中国 · 广东省 · 广州市 · 天河区', '中国 · 浙江省 · 杭州市 · 西湖区']
  ipLocation.value = locations[Math.floor(Math.random() * locations.length)]
  sessionId.value = 'sess_' + Math.random().toString(36).substring(2, 14)
  drawerVisible.value = true
}

// ==================== Echarts 操作频率图 ====================
function initHeatmapChart() {
  if (!heatmapChartRef.value) return
  chartInstance = echarts.init(heatmapChartRef.value)

  // 最近7天 x 每小时 的操作频次
  const days: string[] = []
  const hours: string[] = []
  const data: [string, string, number][] = []

  for (let d = 6; d >= 0; d--) {
    const date = new Date(Date.now() - d * 86400000)
    const dayStr = `${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')}`
    days.push(dayStr)

    for (let h = 0; h < 24; h++) {
      hours.push(`${h.toString().padStart(2, '0')}:00`)
      // 模拟：工作日白天频次高，夜间低
      let base = Math.random() * 15
      if (h >= 8 && h <= 18) base += 25
      if (d >= 1 && d <= 5) base += 10 // 工作日
      data.push([dayStr, `${h.toString().padStart(2, '0')}:00`, Math.floor(base)])
    }
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      position: 'top',
      formatter(params: any) {
        return `${params.data[0]} ${params.data[1]}<br/>操作次数: ${params.data[2]}`
      },
    },
    grid: {
      left: '8%', right: '12%', bottom: '10%', top: '8%',
    },
    xAxis: {
      type: 'category',
      data: Array.from(new Set(days)),
      splitArea: { show: true },
      axisLabel: { fontSize: 11 },
    },
    yAxis: {
      type: 'category',
      data: [...new Set(hours)],
      splitArea: { show: true },
      axisLabel: { fontSize: 10, interval: 3 },
    },
    visualMap: {
      min: 0,
      max: 60,
      calculable: true,
      orient: 'vertical',
      right: '2%',
      top: 'center',
      inRange: {
        color: ['#f0f9ff', '#bae6fd', '#7dd3fc', '#38bdf8', '#0ea5e9', '#0284c7'],
      },
      text: ['高', '低'],
    },
    series: [{
      type: 'heatmap',
      data,
      label: { show: false },
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.3)' },
      },
    }],
  }

  chartInstance.setOption(option)
}

function handleResize() {
  chartInstance?.resize()
}

// ==================== 生命周期 ====================
onMounted(() => {
  nextTick(() => {
    initHeatmapChart()
  })
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
