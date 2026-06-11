<template>
  <div class="h-full overflow-auto bg-[#F5F7FA] p-6 space-y-5">
    <!-- 顶部标题栏 -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <div class="w-10 h-10 rounded-xl bg-primary-50 flex items-center justify-center">
          <i class="ri-dashboard-3-line text-primary-500 text-xl"></i>
        </div>
        <div>
          <h1 class="text-xl font-bold text-gray-900">数据看板</h1>
          <p class="text-sm text-gray-500 mt-0.5">{{ selectedExamName }}</p>
        </div>
      </div>
      <div class="flex items-center gap-4">
        <!-- 考试选择器 -->
        <el-select v-model="selectedExam" placeholder="选择考试" style="width: 240px" size="default">
          <el-option v-for="e in examOptions" :key="e.value" :label="e.label" :value="e.value" />
        </el-select>
        <!-- 自动刷新开关 -->
        <div class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-white border border-gray-200 shadow-sm">
          <i class="ri-refresh-line text-gray-400"></i>
          <span class="text-xs text-gray-500">自动刷新</span>
          <el-switch v-model="autoRefresh" size="small" />
        </div>
        <!-- 刷新时间 -->
        <span class="text-xs text-gray-400">
          <i class="ri-time-line mr-1"></i>更新于 {{ lastUpdateTime }}
        </span>
        <el-button circle size="small" @click="refreshData">
          <i class="ri-refresh-line"></i>
        </el-button>
      </div>
    </div>

    <!-- 核心KPI指标卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-all border border-gray-100">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center">
            <i class="ri-user-follow-line text-blue-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">参与率</span>
        </div>
        <div class="text-3xl font-bold tracking-tight text-gray-900">{{ kpi.participationRate }}<span class="text-lg ml-0.5 text-gray-500">%</span></div>
        <div class="mt-2 flex items-center gap-1">
          <div class="flex-1 h-1.5 bg-gray-100 rounded-full overflow-hidden">
            <div class="h-full bg-blue-500 rounded-full transition-all" :style="{ width: kpi.participationRate + '%' }"></div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-all border border-gray-100">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-green-50 flex items-center justify-center">
            <i class="ri-bar-chart-line text-green-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">平均分</span>
        </div>
        <div class="text-3xl font-bold tracking-tight text-gray-900">{{ kpi.avgScore }}</div>
        <div class="mt-2 flex items-center gap-1 text-green-500 text-xs">
          <i class="ri-arrow-up-line"></i>
          较上次 +{{ kpi.avgScoreChange }}
        </div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-all border border-gray-100">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-cyan-50 flex items-center justify-center">
            <i class="ri-checkbox-circle-line text-cyan-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">及格率</span>
        </div>
        <div class="text-3xl font-bold tracking-tight text-gray-900">{{ kpi.passRate }}<span class="text-lg ml-0.5 text-gray-500">%</span></div>
        <div class="mt-2 text-xs text-gray-400">{{ kpi.passCount }}/{{ kpi.totalCount }} 人及格</div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-all border border-gray-100">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-yellow-50 flex items-center justify-center">
            <i class="ri-medal-line text-yellow-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">优秀率</span>
        </div>
        <div class="text-3xl font-bold tracking-tight text-gray-900">{{ kpi.excellentRate }}<span class="text-lg ml-0.5 text-gray-500">%</span></div>
        <div class="mt-2 text-xs text-gray-400">{{ kpi.excellentCount }} 人达到优秀</div>
      </div>
    </div>

    <!-- 图表区域 - 上排 -->
    <div class="grid grid-cols-3 gap-5">
      <!-- 成绩分布饼图 -->
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
          <i class="ri-pie-chart-line text-primary-500"></i>成绩分布
        </h3>
        <div ref="pieChartRef" style="height: 260px"></div>
      </div>

      <!-- 各班对比柱状图 -->
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
          <i class="ri-bar-chart-grouped-line text-primary-500"></i>班级对比
        </h3>
        <div ref="barChartRef" style="height: 260px"></div>
      </div>

      <!-- 知识点正确率 -->
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
          <i class="ri-lightbulb-line text-primary-500"></i>知识点正确率
        </h3>
        <div ref="knowledgeRef" style="height: 260px"></div>
      </div>
    </div>

    <!-- 图表区域 - 下排 -->
    <div class="grid grid-cols-2 gap-5">
      <!-- 成绩趋势折线图 -->
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
          <i class="ri-line-chart-line text-primary-500"></i>成绩趋势（多场考试对比）
        </h3>
        <div ref="trendChartRef" style="height: 280px"></div>
      </div>

      <!-- 题目难度分析 -->
      <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <h3 class="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
          <i class="ri-bar-chart-horizontal-line text-primary-500"></i>题目难度分析
        </h3>
        <div ref="difficultyRef" style="height: 280px"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getGradeStatistics, getSchedules, getScoreDistribution, getClassComparison, getExamKnowledgeMastery, getExamTrend, getDifficultyAnalysis } from '@/api/teacher'

// ========== 考试选择（动态加载） ==========
const selectedExam = ref('')
const autoRefresh = ref(false)
const lastUpdateTime = ref(new Date().toLocaleTimeString())

const examOptions = ref<Array<{ label: string; value: string; courseId?: number; totalScore?: number; submitCount?: number }>>([])

// 加载考试列表
async function loadExamOptions() {
  try {
    const res = await getSchedules({ pageSize: 100 })
    const exams = Array.isArray(res) ? res : (res?.list || res?.data?.list || res?.data || [])
    examOptions.value = exams.map((e: any) => ({
      label: e.examName || e.name,
      value: String(e.id),
      courseId: e.courseId,
      totalScore: Number(e.totalScore || 0),
      submitCount: Number(e.submitCount || 0),
      maxScore: Number(e.maxScore || 0),
    }))
    if (examOptions.value.length > 0 && !selectedExam.value) {
      const preferredExam = examOptions.value.find((e: any) => (e.submitCount || 0) > 0 && (e.maxScore || 0) > 0 && (e.totalScore || 0) >= 60)
        || examOptions.value.find((e: any) => (e.submitCount || 0) > 0 && (e.maxScore || 0) > 0)
        || examOptions.value.find(e => (e.submitCount || 0) > 0)
        || examOptions.value[0]
      selectedExam.value = preferredExam.value
    }
  } catch (err) {
    console.error('加载考试列表失败', err)
  }
}

const selectedExamName = computed(() => {
  return examOptions.value.find(e => e.value === selectedExam.value)?.label || ''
})

// ========== KPI 数据（从API加载） ==========
const kpi = ref({
  participationRate: 0,
  avgScore: 0,
  avgScoreChange: 0,
  passRate: 0,
  passCount: 0,
  totalCount: 0,
  excellentRate: 0,
  excellentCount: 0,
})

// ========== 图表数据缓存（从API加载） ==========
const pieChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()
const knowledgeRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()
const difficultyRef = ref<HTMLElement>()

let charts: echarts.ECharts[] = []
let refreshTimer: ReturnType<typeof setInterval> | null = null

// 图表数据缓存
const dashboardData = ref({
  scoreDist: [] as { value: number; name: string; itemStyle?: { color: string } }[],
  classNames: [] as string[],
  classAvgScores: [] as number[],
  classMaxScores: [] as number[],
  knowledgeLabels: [] as string[],
  knowledgeRates: [] as number[],
  trendLabels: [] as string[],
  trendSeries: [] as { name: string; data: number[]; points?: any[] }[],
  difficultyLabels: [] as string[],
  difficultyActual: [] as number[],
  difficultyExpected: [] as number[],
})

function initCharts() {
  nextTick(() => {
    charts.forEach(c => c.dispose())
    charts = []

    const dd = dashboardData.value
    const hasPieData = dd.scoreDist.some(item => Number(item.value || 0) > 0)
    const pieData = dd.scoreDist.length > 0 ? dd.scoreDist : buildEmptyScoreDist()

    // 1. 成绩分布饼图
    if (pieChartRef.value) {
      const chart1 = echarts.init(pieChartRef.value)
      charts.push(chart1)
      chart1.setOption({
        title: hasPieData ? undefined : { text: '暂无成绩分布数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af', fontSize: 13, fontWeight: 'normal' } },
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { bottom: 0, textStyle: { color: '#6b7280', fontSize: 11 }, itemWidth: 12, itemHeight: 12 },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold', color: '#374151' } },
          data: pieData,
        }],
      })
    }

    const cn = dd.classNames
    const cas = dd.classAvgScores
    const cms = dd.classMaxScores
    const classLabels = cn.length > 0 ? cn : ['暂无班级数据']
    const avgData = cas.length > 0 ? cas : [0]
    const maxData = cms.length > 0 ? cms : [0]

    // 2. 班级对比柱状图
    if (barChartRef.value) {
      const chart2 = echarts.init(barChartRef.value)
      charts.push(chart2)
      chart2.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['平均分', '最高分'], top: 0, textStyle: { color: '#6b7280', fontSize: 11 } },
        grid: { left: '3%', right: '4%', bottom: '8%', top: '28px', containLabel: true },
        xAxis: {
          type: 'category',
          data: classLabels,
          axisLabel: { color: '#6b7280', fontSize: 11 },
          axisLine: { lineStyle: { color: '#e5e7eb' } },
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: { color: '#9ca3af' },
          splitLine: { lineStyle: { color: '#f3f4f6' } },
        },
        series: [
          {
            name: '平均分',
            type: 'bar',
            data: avgData,
            barWidth: '35%',
            itemStyle: { borderRadius: [4, 4, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#818cf8' },
              { offset: 1, color: '#6366f1' },
            ]) },
          },
          {
            name: '最高分',
            type: 'bar',
            data: maxData,
            barWidth: '35%',
            itemStyle: { borderRadius: [4, 4, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#4ade80' },
              { offset: 1, color: '#22c55e' },
            ]) },
          },
        ],
      })
    }

    const kl = dd.knowledgeLabels
    const kr = dd.knowledgeRates
    const kLabels = kl.length > 0 ? kl : ['暂无知识点数据']
    const kRates = kr.length > 0 ? kr : [0]

    // 3. 知识点正确率条形图
    if (knowledgeRef.value) {
      const chart3 = echarts.init(knowledgeRef.value)
      charts.push(chart3)
      chart3.setOption({
        tooltip: { trigger: 'axis', formatter: '{b}: {c}%' },
        grid: { left: '3%', right: '8%', bottom: '8%', top: '4%', containLabel: true },
        xAxis: {
          type: 'value',
          max: 100,
          axisLabel: { color: '#9ca3af', formatter: '{value}%' },
          splitLine: { lineStyle: { color: '#f3f4f6' } },
        },
        yAxis: {
          type: 'category',
          data: kLabels,
          axisLabel: { color: '#6b7280', fontSize: 11 },
          inverse: true,
        },
        series: [{
          type: 'bar',
          data: kRates.map(v => ({ value: v, itemStyle: { color: v >= 80 ? '#22c55e' : v >= 65 ? '#f97316' : v >= 50 ? '#eab308' : '#ef4444' } })),
          barWidth: '60%',
          itemStyle: { borderRadius: [0, 4, 4, 0] },
          label: { show: true, position: 'right', formatter: '{c}%', color: '#6b7280', fontSize: 11 },
        }],
      })
    }

    const tl = dd.trendLabels
    const ts = dd.trendSeries
    const trendLabelsArr = tl.length > 0 ? tl : ['暂无趋势数据']
    const trendSeriesArr = ts.length > 0 ? ts : [{ name: '暂无数据', data: [0] }]
    const hasTrendData = ts.length > 0 && ts.some(s => s.data.some(v => Number(v) > 0))
    const seriesColors = ['#6366f1', '#22c55e', '#f59e0b', '#ef4444']

    // 4. 成绩趋势折线图
    if (trendChartRef.value) {
      const chart4 = echarts.init(trendChartRef.value)
      charts.push(chart4)
      chart4.setOption({
        title: hasTrendData ? undefined : { text: '暂无趋势数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af', fontSize: 13, fontWeight: 'normal' } },
        tooltip: {
          trigger: 'axis',
          formatter: (params: any) => {
            const rows = Array.isArray(params) ? params : [params]
            const title = rows[0]?.axisValue || ''
            const lines = rows.map((p: any) => {
              const point = trendSeriesArr[p.seriesIndex]?.points?.[p.dataIndex]
              const avgScore = point?.avgScore ?? '-'
              const totalScore = point?.totalScore ?? '-'
              const submittedCount = point?.submittedCount ?? 0
              return `${p.marker}${p.seriesName}: ${p.value}%（均分 ${avgScore}/${totalScore}，${submittedCount} 人）`
            })
            return [title, ...lines].join('<br/>')
          },
        },
        legend: {
          data: trendSeriesArr.map(s => s.name),
          top: 0,
          textStyle: { color: '#6b7280', fontSize: 11 },
        },
        grid: { left: '3%', right: '4%', bottom: '8%', top: '36px', containLabel: true },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: trendLabelsArr,
          axisLabel: { color: '#6b7280', fontSize: 11 },
          axisLine: { lineStyle: { color: '#e5e7eb' } },
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: 100,
          axisLabel: { color: '#9ca3af', formatter: '{value}%' },
          splitLine: { lineStyle: { color: '#f3f4f6' } },
        },
        series: trendSeriesArr.map((s, i) => ({
          name: s.name,
          type: 'line',
          smooth: true,
          data: s.data,
          lineStyle: { width: 2.5, color: seriesColors[i % seriesColors.length] },
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: `${seriesColors[i % seriesColors.length]}26` },
            { offset: 1, color: `${seriesColors[i % seriesColors.length]}00` },
          ]) },
          symbol: 'circle',
          symbolSize: 6,
        })),
      })
    }

    const dl = dd.difficultyLabels
    const da = dd.difficultyActual
    const de = dd.difficultyExpected
    const diffLabels = dl.length > 0 ? dl : ['暂无题型数据']
    const diffActual = da.length > 0 ? da : [0]
    const diffExpected = de.length > 0 ? de : [0]
    const hasDifficultyData = dl.length > 0 && (da.some(v => Number(v) > 0) || de.some(v => Number(v) > 0))

    // 5. 题目难度分析
    if (difficultyRef.value) {
      const chart5 = echarts.init(difficultyRef.value)
      charts.push(chart5)
      chart5.setOption({
        title: hasDifficultyData ? undefined : { text: '暂无题型数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af', fontSize: 13, fontWeight: 'normal' } },
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['平均得分率', '参考得分率'], top: 0, textStyle: { color: '#6b7280', fontSize: 11 } },
        grid: { left: '3%', right: '4%', bottom: '8%', top: '28px', containLabel: true },
        xAxis: {
          type: 'category',
          data: diffLabels,
          axisLabel: { color: '#6b7280', fontSize: 10, interval: 0 },
          axisLine: { lineStyle: { color: '#e5e7eb' } },
        },
        yAxis: {
          type: 'value',
          max: 100,
          axisLabel: { color: '#9ca3af', formatter: '{value}%' },
          splitLine: { lineStyle: { color: '#f3f4f6' } },
        },
        series: [
          {
            name: '平均得分率',
            type: 'bar',
            data: diffActual,
            barWidth: '30%',
            itemStyle: { borderRadius: [4, 4, 0, 0], color: '#6366f1' },
          },
          {
            name: '参考得分率',
            type: 'bar',
            data: diffExpected,
            barWidth: '30%',
            itemStyle: { borderRadius: [4, 4, 0, 0], color: '#d1d5db' },
          },
        ],
      })
    }
  })
}

function buildEmptyScoreDist() {
  return [
    { value: 0, name: '优秀 (90+)', itemStyle: { color: '#22c55e' } },
    { value: 0, name: '良好 (80-89)', itemStyle: { color: '#6366f1' } },
    { value: 0, name: '中等 (70-79)', itemStyle: { color: '#f59e0b' } },
    { value: 0, name: '及格 (60-69)', itemStyle: { color: '#3b82f6' } },
    { value: 0, name: '不及格 (<60)', itemStyle: { color: '#ef4444' } },
  ]
}

// 加载仪表盘数据
async function loadDashboardData() {
  if (!selectedExam.value) return
  try {
    const examId = Number(selectedExam.value)
    const statsRes = await getGradeStatistics(examId)
    if (statsRes) {
      const d = statsRes as any
      kpi.value = {
        participationRate: d.submittedCount && d.totalStudents
          ? Number(((d.submittedCount / d.totalStudents) * 100).toFixed(1)) : 0,
        avgScore: d.avgScore || 0,
        avgScoreChange: d.avgScoreChange || 0,
        passRate: d.passRate || 0,
        passCount: d.passCount || 0,
        totalCount: d.totalStudents || 0,
        excellentRate: d.excellentRate || 0,
        excellentCount: d.excellentCount || 0,
      }
      const sd = d.scoreDistribution
      if (sd) {
        dashboardData.value.scoreDist = [
          { value: sd['90-100'] || 0, name: '优秀 (90+)', itemStyle: { color: '#22c55e' } },
          { value: sd['80-89'] || 0, name: '良好 (80-89)', itemStyle: { color: '#6366f1' } },
          { value: sd['70-79'] || 0, name: '中等 (70-79)', itemStyle: { color: '#f59e0b' } },
          { value: sd['60-69'] || 0, name: '及格 (60-69)', itemStyle: { color: '#3b82f6' } },
          { value: (sd['0-59'] || 0) + (sd['0-60'] || 0) + (sd['<40'] || 0), name: '不及格 (<60)', itemStyle: { color: '#ef4444' } },
        ]
      }
    }
    try {
      const compRes = await getClassComparison({ examId })
      if (compRes) {
        const cd = compRes as any
        const classes = Array.isArray(cd) ? cd : (cd.classes || cd.data || [])
        if (classes.length > 0) {
          dashboardData.value.classNames = classes.map((c: any) => c.className)
          dashboardData.value.classAvgScores = classes.map((c: any) => c.avgScore)
          dashboardData.value.classMaxScores = classes.map((c: any) => c.maxScore || 100)
        }
      }
    } catch { /* 接口可能不存在 */ }
    try {
      const knowledgeRes = await getExamKnowledgeMastery(examId)
      const knowledgeList = Array.isArray(knowledgeRes) ? knowledgeRes : ((knowledgeRes as any)?.data || [])
      if (knowledgeList.length > 0) {
        dashboardData.value.knowledgeLabels = knowledgeList.map((item: any) => item.name)
        dashboardData.value.knowledgeRates = knowledgeList.map((item: any) => item.value ?? item.mastery ?? 0)
      } else {
        dashboardData.value.knowledgeLabels = []
        dashboardData.value.knowledgeRates = []
      }
    } catch { /* 接口可能暂未提供数据 */ }

    try {
      const selectedExamOption = examOptions.value.find(e => e.value === selectedExam.value)
      const courseId = Number(selectedExamOption?.courseId || 0)
      if (courseId > 0) {
        const trendRes = await getExamTrend(courseId)
        const trendList = Array.isArray(trendRes) ? trendRes : ((trendRes as any)?.data || [])
        if (trendList.length > 0) {
          const firstPoints = trendList[0]?.points || []
          dashboardData.value.trendLabels = firstPoints.map((point: any) => point.examName || `考试${point.examId}`)
          dashboardData.value.trendSeries = trendList.map((item: any) => ({
            name: item.className || '未分班',
            data: (item.points || []).map((point: any) => Number(point.avgRate ?? point.avgScore ?? 0)),
            points: item.points || [],
          }))
        }
      }
    } catch { /* 接口可能暂未提供数据 */ }

    try {
      const difficultyRes = await getDifficultyAnalysis(examId)
      const difficultyList = Array.isArray(difficultyRes) ? difficultyRes : ((difficultyRes as any)?.data || [])
      if (difficultyList.length > 0) {
        dashboardData.value.difficultyLabels = difficultyList.map((item: any) => item.typeName || item.type || '未知题型')
        dashboardData.value.difficultyActual = difficultyList.map((item: any) => Number(item.actualRate || 0))
        dashboardData.value.difficultyExpected = difficultyList.map((item: any) => Number(item.expectedRate || 0))
      }
    } catch { /* 接口可能暂未提供数据 */ }

    try {
      const distRes = await getScoreDistribution({ examId })
      if (distRes) {
        const dd = distRes as any
        const dist = dd.distribution || dd.data || dd
        if (Array.isArray(dist) && dist.length > 0 && dashboardData.value.scoreDist.length === 0) {
          dashboardData.value.scoreDist = [
            { value: dist.find((d: any) => d.range === '90-100')?.count || 0, name: '优秀 (90+)', itemStyle: { color: '#22c55e' } },
            { value: dist.find((d: any) => d.range === '80-89')?.count || 0, name: '良好 (80-89)', itemStyle: { color: '#6366f1' } },
            { value: dist.find((d: any) => d.range === '70-79')?.count || 0, name: '中等 (70-79)', itemStyle: { color: '#f59e0b' } },
            { value: dist.find((d: any) => d.range === '60-69')?.count || 0, name: '及格 (60-69)', itemStyle: { color: '#3b82f6' } },
            { value: (dist.find((d: any) => d.range === '0-59')?.count || 0) + (dist.find((d: any) => d.range === '0-60')?.count || 0), name: '不及格 (<60)', itemStyle: { color: '#ef4444' } },
          ]
        } else if (dist && typeof dist === 'object' && dashboardData.value.scoreDist.length === 0) {
          dashboardData.value.scoreDist = [
            { value: dist['90-100'] || 0, name: '优秀 (90+)', itemStyle: { color: '#22c55e' } },
            { value: dist['80-89'] || 0, name: '良好 (80-89)', itemStyle: { color: '#6366f1' } },
            { value: dist['70-79'] || 0, name: '中等 (70-79)', itemStyle: { color: '#f59e0b' } },
            { value: dist['60-69'] || 0, name: '及格 (60-69)', itemStyle: { color: '#3b82f6' } },
            { value: dist['0-59'] || 0, name: '不及格 (<60)', itemStyle: { color: '#ef4444' } },
          ]
        }
      }
    } catch { /* 接口可能不存在 */ }
  } catch (err) {
    console.error('加载仪表盘数据失败', err)
  }
}

function refreshData() {
  lastUpdateTime.value = new Date().toLocaleTimeString()
  loadDashboardData().then(() => initCharts())
  ElMessage.success('数据已刷新')
}

onMounted(async () => {
  await loadExamOptions()
  if (selectedExam.value) {
    await loadDashboardData()
  }
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  charts.forEach(c => c.dispose())
  window.removeEventListener('resize', handleResize)
  if (refreshTimer) clearInterval(refreshTimer)
})

watch(autoRefresh, (val) => {
  if (val) {
    refreshTimer = setInterval(() => {
      lastUpdateTime.value = new Date().toLocaleTimeString()
    }, 30000)
  } else {
    if (refreshTimer) clearInterval(refreshTimer)
    refreshTimer = null
  }
})

watch(selectedExam, () => {
  loadDashboardData().then(() => initCharts())
})

function handleResize() {
  charts.forEach(c => c.resize())
}
</script>
