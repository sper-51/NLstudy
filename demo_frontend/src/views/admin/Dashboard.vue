<template>
  <div class="space-y-5">
    <!-- ===== Tab 1: 全局控制台 ===== -->
    <template v-if="activeTab === 'global'">
      <!-- 5个统计卡片 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        <div v-for="(card, i) in globalStats" :key="i" class="bg-white rounded-xl shadow-sm p-4 border-l-4 hover:shadow-md transition-shadow cursor-pointer group" :style="{ borderColor: card.color }">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs text-gray-400">{{ card.label }}</span>
            <i :class="card.icon" :style="{ color: card.color }" class="text-lg opacity-60"></i>
          </div>
          <div class="text-2xl font-bold text-gray-800">{{ card.value }}</div>
          <div class="flex items-center gap-1 mt-1" :class="card.trend > 0 ? 'text-green-500' : 'text-red-500'">
            <i :class="card.trend > 0 ? 'ri-arrow-up-line' : 'ri-arrow-down-line'" class="text-xs"></i>
            <span class="text-xs font-medium">{{ Math.abs(card.trend) }}%</span>
            <span class="text-[10px] text-gray-300 ml-auto">{{ card.subLabel }}</span>
          </div>
        </div>
      </div>

      <!-- 全校考试并发走势与系统资源监测联动 图表 -->
      <div class="bg-white rounded-xl shadow-sm p-5">
        <div class="flex items-center justify-between mb-2">
          <h3 class="text-sm font-semibold text-gray-800">平台考试与成绩趋势</h3>
          <div class="flex items-center gap-4 text-xs text-gray-500">
            <span class="flex items-center gap-1"><span class="w-2.5 h-2.5 rounded-full bg-red-400"></span>考试创建数</span>
            <span class="flex items-center gap-1"><span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>新增用户</span>
            <span class="flex items-center gap-1"><span class="w-2.5 h-2.5 rounded-full bg-blue-500"></span>平均分</span>
          </div>
        </div>
        <p class="text-[11px] text-gray-400 -mt-0.5 mb-3">基于当前系统真实考试、登录与资源数据；无数据时显示暂无</p>
        <div ref="trendChartRef" style="height: 280px; width: 100%"></div>
      </div>

      <!-- 实时进行中的重大考试 LIVE -->
      <div class="bg-white rounded-xl shadow-sm p-5">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span>
            <h3 class="text-sm font-semibold text-gray-800">实时进行中的重大考试</h3>
            <el-tag size="small" type="danger" effect="dark" round>LIVE</el-tag>
          </div>
          <span class="text-xs text-gray-400">{{ liveExams.length }} 场进行中</span>
        </div>
        <div v-if="liveExams.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3">
          <div v-for="exam in liveExams" :key="exam.id" class="border border-gray-100 rounded-lg p-4 hover:border-primary-200 hover:shadow-sm transition-all cursor-pointer">
            <div class="flex items-start justify-between mb-2">
              <div class="flex items-center gap-1.5">
                <span class="w-1.5 h-1.5 rounded-full bg-red-400 animate-pulse"></span>
                <span class="font-semibold text-sm text-gray-800 truncate">{{ exam.name }}</span>
              </div>
              <el-tag size="small" type="warning" effect="plain" round>●</el-tag>
            </div>
            <div class="space-y-1.5 mt-3">
              <div class="flex items-center justify-between text-xs">
                <span class="text-gray-400">{{ exam.department }}</span>
                <span class="text-primary-600 font-medium">{{ exam.teacher }}</span>
              </div>
              <div class="flex items-center justify-between text-xs">
                <span class="text-gray-400 flex items-center gap-1"><i class="ri-time-line"></i>{{ exam.time }}</span>
                <span class="text-gray-500">{{ exam.participants }}人</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无进行中的考试" :image-size="90" />
      </div>
    </template>

    <!-- ===== Tab 2: 课程教学成果 ===== -->
    <template v-if="activeTab === 'teaching'">
      <!-- 4个统计卡片 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-emerald-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">课程综合及格率</span>
            <span class="text-xs text-gray-400">真实成绩统计</span>
          </div>
          <div class="text-3xl font-bold text-emerald-600">{{ formatPercent(teachingStatsData.passRate) }}</div>
          <div class="text-[11px] text-gray-400 mt-0.5">来自已发布成绩</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-blue-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">课程全员平均分</span>
            <span class="text-xs text-gray-400">真实成绩统计</span>
          </div>
          <div class="text-3xl font-bold text-blue-600">{{ formatScore(teachingStatsData.avgScore) }}</div>
          <div class="text-[11px] text-gray-400 mt-0.5">所有有效成绩均值</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-orange-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">学生考试参与率</span>
            <span class="text-xs text-gray-400">真实考试统计</span>
          </div>
          <div class="text-3xl font-bold text-orange-600">{{ formatPercent(teachingStatsData.participationRate) }}</div>
          <div class="text-[11px] text-gray-400 mt-0.5">提交人数 / 应考人数</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-red-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">最高分与最低分</span>
            <span class="text-xs text-gray-400">真实成绩统计</span>
          </div>
          <div class="text-3xl font-bold text-red-500">{{ teachingStatsData.maxMinScore || '暂无' }}</div>
          <div class="text-[11px] text-gray-400 mt-0.5">最高分 / 最低分</div>
        </div>
      </div>

      <!-- 历次考试成绩及格率与平均分演变趋势 组合图 -->
      <div class="bg-white rounded-xl shadow-sm p-5">
        <h3 class="text-sm font-semibold text-gray-800 mb-1">历次考试成绩及格率与平均分演变趋势</h3>
        <div ref="scoreTrendChartRef" style="height: 280px; width: 100%"></div>
      </div>

      <!-- 底部两图：多班级对比 + 分数分布 -->
      <div class="grid grid-cols-1 lg:grid-cols-5 gap-4">
        <!-- 多班级教学成果横向对比 (占3列) -->
        <div class="lg:col-span-3 bg-white rounded-xl shadow-sm p-5">
          <h3 class="text-sm font-semibold text-gray-800 mb-1">多班级教学成果横向对比</h3>
          <div class="flex items-center gap-3 text-[11px] text-gray-500 mb-2">
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-blue-500"></span>平均分</span>
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-emerald-500"></span>及格率</span>
          </div>
          <div ref="classCompareChartRef" style="height: 260px; width: 100%"></div>
        </div>

        <!-- 课程标准分数分布 饼图 (占2列) -->
        <div class="lg:col-span-2 bg-white rounded-xl shadow-sm p-5">
          <div class="flex items-center justify-between mb-1">
            <h3 class="text-sm font-semibold text-gray-800">课程标准分数分布</h3>
            <span class="text-[11px] text-gray-400">真实成绩区间统计</span>
          </div>
          <div ref="scoreDistChartRef" style="height: 260px; width: 100%"></div>
        </div>
      </div>
    </template>

    <!-- ===== Tab 3: 考试质量看板 ===== -->
    <template v-if="activeTab === 'quality'">
      <!-- 课程/班级排名柱状图 + 折线 -->
      <div class="bg-white rounded-xl shadow-sm p-5">
        <h3 class="text-sm font-semibold text-gray-800 mb-1">课程/班级考试通过率与均分排名</h3>
        <div class="flex items-center gap-3 text-[11px] text-gray-500 mb-2">
          <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-emerald-500"></span>及格率</span>
          <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-blue-500"></span>平均分</span>
          <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-violet-500"></span>总分</span>
        </div>
        <div ref="collegeRankChartRef" style="height: 300px; width: 100%"></div>
      </div>

      <!-- 底部：题型分布 + 难度雷达 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <!-- 全校题型分布比例 环形饼图 -->
        <div class="bg-white rounded-xl shadow-sm p-5">
          <h3 class="text-sm font-semibold text-gray-800 mb-1">全校题型分布比例</h3>
          <div ref="questionTypeChartRef" style="height: 280px; width: 100%"></div>
        </div>

        <!-- 题库难度系数雷达图 -->
        <div class="bg-white rounded-xl shadow-sm p-5">
          <h3 class="text-sm font-semibold text-gray-800 mb-1">题库难度系数雷达图</h3>
          <div ref="difficultyRadarRef" style="height: 280px; width: 100%"></div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import { getDashboardStats, getDashboardTrend } from '@/api/admin'

const route = useRoute()
const activeTab = ref('global')

// 从路由query读取tab参数
function normalizeTab(tab: unknown) {
  return tab === 'teaching' || tab === 'quality' ? tab : 'global'
}

function syncActiveTabFromUrl() {
  activeTab.value = normalizeTab(route.query.tab)
}

syncActiveTabFromUrl()

// ==================== Tab 1: 全局控制台 数据 ====================
const globalStats = reactive([
  { label: '当前全校在线参考人数', value: '0', icon: 'ri-user-line', color: '#f43f5e', trend: 0, subLabel: '' },
  { label: '今日考试场次（已完成/总计）', value: '0 / 0', icon: 'ri-file-list-3-line', color: '#3b82f6', trend: 0, subLabel: '— 进行中' },
  { label: '全站总注册师生数', value: '0', icon: 'ri-user-star-line', color: '#10b981', trend: 0, subLabel: '' },
  { label: '近1小时登录 / 峰值', value: '— / —', icon: 'ri-speed-line', color: '#f59e0b', trend: 0, subLabel: '— 当前活跃' },
  { label: '系统资源告警状态', value: '—', icon: 'ri-alarm-warning-line', color: '#ef4444', trend: 0, subLabel: '— 稳定' },
])

const liveExams = ref<Array<{
  id: number
  name: string
  department: string
  teacher: string
  time: string
  participants: number
}>>([])

const globalTrendData = ref({
  labels: [] as string[],
  examCount: [] as number[],
  userActivity: [] as number[],
  avgScore: [] as number[],
})

// 加载仪表盘统计数据
async function loadDashboardStats() {
  try {
    const res = await getDashboardStats()
    if (res) {
      const data = res as any
      // 更新统计卡片
      if (data.onlineCount !== undefined) {
        globalStats[0].value = data.onlineCount.toLocaleString()
      }
      if (data.todayExams !== undefined) {
        globalStats[1].value = `${data.todayCompletedExams || 0} / ${data.todayExams}`
      }
      if (data.totalUsers !== undefined) {
        globalStats[2].value = data.totalUsers.toLocaleString()
      }
      if (data.concurrent !== undefined) {
        globalStats[3].value = `${data.concurrent} / ${data.peak || 0}`
      }
      if (data.cpuUsage !== undefined && data.memoryUsage !== undefined) {
        globalStats[4].value = `CPU ${data.cpuUsage}% / 内存 ${data.memoryUsage}%`
      }
      // 更新liveExams
      if (Array.isArray(data.liveExams)) {
        liveExams.value = data.liveExams.map((e: any) => ({
          id: e.id,
          name: e.examName || e.name,
          department: e.department || e.faculty || '',
          teacher: e.teacherName || e.teacher || '',
          time: e.examTime || `${e.startTime || ''} – ${e.endTime || ''}`,
          participants: e.participants || e.studentCount || 0,
        }))
      }

      // 加载教学成果数据（Tab2）
      if (data.teachingStats) {
        const ts = data.teachingStats
        teachingStatsData.value = {
          passRate: ts.passRate ?? null,
          avgScore: ts.avgScore ?? null,
          participationRate: ts.participationRate ?? null,
          maxMinScore: ts.maxScore != null && ts.minScore != null ? `${ts.maxScore} / ${ts.minScore}` : '',
          scoreTrend: ts.scoreTrend || { labels: [], passRates: [], avgScores: [] },
          classCompare: ts.classCompare || { classNames: [], avgScores: [], passRates: [] },
          scoreDist: ts.scoreDist || [],
        }
      }

      // 加载考试质量数据（Tab3）
      if (data.qualityStats) {
        const qs = data.qualityStats
        qualityStatsData.value = {
          collegeRank: qs.collegeRank || { names: [], passRates: [], avgScores: [], totalScores: [] },
          questionType: qs.questionType || [],
          difficultyRadar: qs.difficultyRadar || [],
        }
      }
    }
  } catch (err) {
    console.error('加载仪表盘数据失败', err)
  }
}

async function loadDashboardTrend() {
  try {
    const data = await getDashboardTrend({ range: '7' }) as any
    globalTrendData.value = {
      labels: Array.isArray(data?.labels) ? data.labels : [],
      examCount: Array.isArray(data?.examCount) ? data.examCount : [],
      userActivity: Array.isArray(data?.userActivity) ? data.userActivity : [],
      avgScore: Array.isArray(data?.avgScore) ? data.avgScore : [],
    }
  } catch (err) {
    console.error('加载仪表盘趋势失败', err)
  }
}

// ==================== Tab2/3 图表数据缓存（从API加载） ====================
const teachingStatsData = ref({
  passRate: null as number | null,
  avgScore: null as number | null,
  participationRate: null as number | null,
  maxMinScore: '',
  scoreTrend: { labels: [] as string[], passRates: [] as number[], avgScores: [] as number[] },
  classCompare: { classNames: [] as string[], avgScores: [] as number[], passRates: [] as number[] },
  scoreDist: [] as number[],
})

const qualityStatsData = ref({
  collegeRank: { names: [] as string[], passRates: [] as number[], avgScores: [] as number[], totalScores: [] as number[] },
  questionType: [] as { value: number; name: string }[],
  difficultyRadar: [] as number[],
})

// ==================== ECharts 实例引用 ====================
const trendChartRef = ref<HTMLElement>()
const scoreTrendChartRef = ref<HTMLElement>()
const classCompareChartRef = ref<HTMLElement>()
const scoreDistChartRef = ref<HTMLElement>()
const collegeRankChartRef = ref<HTMLElement>()
const questionTypeChartRef = ref<HTMLElement>()
const difficultyRadarRef = ref<HTMLElement>()

let charts: echarts.ECharts[] = []
let resizeObserver: ResizeObserver | null = null

function formatPercent(value: number | null | undefined) {
  return value == null ? '暂无' : `${Number(value).toFixed(1)}%`
}

function formatScore(value: number | null | undefined) {
  return value == null ? '暂无' : `${Number(value).toFixed(1)}分`
}

function initChart(dom: HTMLElement | undefined, option: echarts.EChartsOption): echarts.ECharts | null {
  if (!dom) return null
  const chart = echarts.init(dom, null, { renderer: 'canvas' })
  chart.setOption(option)
  charts.push(chart)
  return chart
}

function getEmptyChartOption(message: string): echarts.EChartsOption {
  return {
    title: {
      text: message,
      left: 'center',
      top: 'middle',
      textStyle: { color: '#9ca3af', fontSize: 13, fontWeight: 400 },
    },
    xAxis: { show: false },
    yAxis: { show: false },
    series: [],
  }
}

// 24小时趋势图配置
function getTrendOption(): echarts.EChartsOption {
  const trend = globalTrendData.value
  if (!trend.labels.length) return getEmptyChartOption('暂无趋势数据')
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' }, backgroundColor: '#fff', borderColor: '#e5e7eb', textStyle: { color: '#374151', fontSize: 12 } },
    grid: { left: 50, right: 50, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: trend.labels, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 10, color: '#9ca3af' }, boundaryGap: false },
    yAxis: [
      { type: 'value', name: '数量', nameTextStyle: { fontSize: 10, color: '#9ca3af' }, position: 'left', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f3f4f6' } }, axisLabel: { fontSize: 10, color: '#9ca3af' }, min: 0, alignTicks: false },
      { type: 'value', name: '分数', nameTextStyle: { fontSize: 10, color: '#9ca3af' }, position: 'right', axisLine: { show: false }, splitLine: { show: false }, axisLabel: { fontSize: 10, color: '#9ca3af' }, min: 0, max: 100, alignTicks: false },
    ],
    series: [
      { name: '考试创建数', type: 'line', data: trend.examCount, smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2.5, color: '#f43f5e' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(244,63,94,0.25)' }, { offset: 1, color: 'rgba(244,63,94,0.02)' }]) } },
      { name: '新增用户', type: 'line', data: trend.userActivity, smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2, color: '#10b981' } },
      { name: '平均分', type: 'line', yAxisIndex: 1, data: trend.avgScore, smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2, color: '#3b82f6' } },
    ],
  }
}

// 成绩趋势组合图
function getScoreTrendOption(): echarts.EChartsOption {
  const ts = teachingStatsData.value.scoreTrend
  if (!ts.passRates.length && !ts.avgScores.length) return getEmptyChartOption('暂无成绩趋势数据')
  const labels = ts.labels?.length ? ts.labels : ts.passRates.map((_, index) => `考试${index + 1}`)
  return {
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0, itemWidth: 12, itemHeight: 8, textStyle: { fontSize: 11 } },
    grid: { left: 50, right: 50, top: 15, bottom: 40 },
    xAxis: { type: 'category', data: labels, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 11, color: '#6b7280' } },
    yAxis: [
      { type: 'value', name: '分数', min: 0, max: 100, alignTicks: false, axisLabel: { fontSize: 10 }, splitLine: { lineStyle: { color: '#f3f4f6' } } },
      { type: 'value', name: '及格率%', min: 0, max: 100, alignTicks: false, axisLabel: { fontSize: 10, formatter: '{value}%' }, splitLine: { show: false } },
    ],
    series: [
      { name: '及格率', type: 'bar', barWidth: 20, data: ts.passRates, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#86efac' }, { offset: 1, color: '#d1fae5' }]), borderRadius: [4, 4, 0, 0] } },
      { name: '平均分', type: 'line', yAxisIndex: 1, data: ts.avgScores, smooth: true, symbol: 'circle', symbolSize: 6, lineStyle: { width: 2.5, color: '#3b82f6' }, itemStyle: { color: '#3b82f6' } },
    ],
  }
}

// 多班级对比柱状图
function getClassCompareOption(): echarts.EChartsOption {
  const cc = teachingStatsData.value.classCompare
  if (!cc.classNames.length) return getEmptyChartOption('暂无班级对比数据')
  return {
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0, itemWidth: 12, itemHeight: 8, textStyle: { fontSize: 11 } },
    grid: { left: 45, right: 15, top: 10, bottom: 35 },
    xAxis: { type: 'category', data: cc.classNames, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 11, color: '#6b7280' } },
    yAxis: { type: 'value', min: 0, max: 100, alignTicks: false, axisLabel: { fontSize: 10 }, splitLine: { lineStyle: { color: '#f3f4f6' } } },
    series: [
      { name: '平均分', type: 'bar', barWidth: 14, data: cc.avgScores, itemStyle: { color: '#3b82f6', borderRadius: [3, 3, 0, 0] } },
      { name: '及格率', type: 'bar', barWidth: 14, data: cc.passRates, itemStyle: { color: '#10b981', borderRadius: [3, 3, 0, 0] } },
    ],
  }
}

// 分数分布环形图
function getScoreDistOption(): echarts.EChartsOption {
  const sd = teachingStatsData.value.scoreDist
  if (!sd.length) return getEmptyChartOption('暂无分数分布数据')
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', itemWidth: 10, itemHeight: 8, textStyle: { fontSize: 11 } },
    series: [{
      type: 'pie', radius: ['50%', '72%'], center: ['40%', '50%'],
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
      data: [
        { value: sd[0] || 0, name: '优秀(≥90)', itemStyle: { color: '#22c55e' } },
        { value: sd[1] || 0, name: '良好(80-89)', itemStyle: { color: '#3b82f6' } },
        { value: sd[2] || 0, name: '中等(70-79)', itemStyle: { color: '#f59e0b' } },
        { value: sd[3] || 0, name: '及格(60-69)', itemStyle: { color: '#f97316' } },
        { value: sd[4] || 0, name: '不及格(<60)', itemStyle: { color: '#ef4444' } },
      ],
    }],
  }
}

// 课程/班级排名组合图
function getCollegeRankOption(): echarts.EChartsOption {
  const cr = qualityStatsData.value.collegeRank
  if (!cr.names.length) return getEmptyChartOption('暂无课程/班级排名数据')
  return {
    tooltip: { trigger: 'axis' },
    legend: { top: 0, itemWidth: 12, itemHeight: 8, textStyle: { fontSize: 11 } },
    grid: { left: 60, right: 60, top: 25, bottom: 20 },
    xAxis: { type: 'value', min: 0, max: 100, alignTicks: false, axisLabel: { fontSize: 10, formatter: '{value}' }, splitLine: { lineStyle: { color: '#f3f4f6' } } },
    yAxis: { type: 'category', data: cr.names, inverse: true, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 11, color: '#6b7280' } },
    series: [
      { name: '及格率', type: 'bar', barWidth: 14, data: cr.passRates, itemStyle: { color: '#10b981', borderRadius: [0, 3, 3, 0] } },
      { name: '平均分', type: 'bar', barWidth: 14, data: cr.avgScores, itemStyle: { color: '#3b82f6', borderRadius: [0, 3, 3, 0] } },
      { name: '总分', type: 'line', data: cr.totalScores, smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2, color: '#8b5cf6' }, itemStyle: { color: '#8b5cf6' } },
    ],
  }
}

// 题型分布环形图
function getQuestionTypeOption(): echarts.EChartsOption {
  const qt = qualityStatsData.value.questionType
  if (!qt.length) return getEmptyChartOption('暂无题型分布数据')
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c}% ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', itemWidth: 10, itemHeight: 8, textStyle: { fontSize: 11 } },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['38%', '50%'],
      label: { show: true, position: 'outside', fontSize: 11, fontWeight: 'bold', formatter: '{b}\n{d}%' },
      labelLine: { length: 12, length2: 8 },
      emphasis: { label: { show: true, fontSize: 13 } },
      data: qt,
    }],
  }
}

// 难度系数雷达图
function getDifficultyRadarOption(): echarts.EChartsOption {
  const dr = qualityStatsData.value.difficultyRadar
  if (!dr.length) return getEmptyChartOption('暂无题库难度数据')
  return {
    tooltip: {},
    radar: {
      indicator: [
        { name: '简单', max: 100 },
        { name: '较易', max: 100 },
        { name: '中等', max: 100 },
        { name: '较难', max: 100 },
        { name: '困难', max: 100 },
      ],
      shape: 'polygon',
      radius: '65%',
      axisName: { color: '#6b7280', fontSize: 11 },
      splitArea: { areaStyle: { color: ['#f8fafc', '#f1f5f9', '#f8fafc', '#f1f5f9'] } },
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      splitLine: { lineStyle: { color: '#e5e7eb' } },
    },
    series: [{
      type: 'radar',
      data: [{
        value: dr,
        name: '题库难度分布',
        areaStyle: { color: 'rgba(59,130,246,0.15)' },
        lineStyle: { color: '#3b82f6', width: 2 },
        itemStyle: { color: '#3b82f6' },
      }],
    }],
  }
}

// 初始化所有图表
function initAllCharts() {
  // 清理旧实例
  charts.forEach(c => c.dispose())
  charts = []

  if (activeTab.value === 'global') {
    nextTick(() => {
      initChart(trendChartRef.value, getTrendOption())
    })
  } else if (activeTab.value === 'teaching') {
    nextTick(() => {
      initChart(scoreTrendChartRef.value, getScoreTrendOption())
      initChart(classCompareChartRef.value, getClassCompareOption())
      initChart(scoreDistChartRef.value, getScoreDistOption())
    })
  } else if (activeTab.value === 'quality') {
    nextTick(() => {
      initChart(collegeRankChartRef.value, getCollegeRankOption())
      initChart(questionTypeChartRef.value, getQuestionTypeOption())
      initChart(difficultyRadarRef.value, getDifficultyRadarOption())
    })
  }
}

onMounted(() => {
  syncActiveTabFromUrl()
  loadDashboardStats().then(() => initAllCharts())
  loadDashboardTrend().then(() => {
    if (activeTab.value === 'global') initAllCharts()
  })
  // ResizeObserver 监听容器大小变化
  resizeObserver = new ResizeObserver(() => {
    charts.forEach(c => c.resize())
  })
  if (trendChartRef.value?.parentElement) {
    resizeObserver.observe(trendChartRef.value.parentElement)
  }
})

onUnmounted(() => {
  charts.forEach(c => c.dispose())
  charts = []
  if (resizeObserver) resizeObserver.disconnect()
})

watch(
  () => route.query.tab,
  (tab) => {
    activeTab.value = normalizeTab(tab)
  }
)

// tab切换时重新初始化图表
watch(activeTab, () => {
  // 先销毁旧图表
  charts.forEach(c => c.dispose())
  charts = []
  nextTick(() => {
    initAllCharts()
  })
})
</script>
