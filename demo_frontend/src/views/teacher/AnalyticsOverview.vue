<template>
  <div class="h-full overflow-auto p-6 space-y-5 bg-[#F5F7FA]" v-loading="loading">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">成绩分析</h1>
        <p class="text-sm text-gray-500 mt-1">多维度数据分析，全面掌握考试情况</p>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="bg-white rounded-xl p-4 shadow-sm flex items-center gap-3">
      <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 220px" size="default">
        <el-option v-for="c in courseOptions" :key="c.value" :label="c.label" :value="c.value" />
      </el-select>
      <el-select v-model="selectedExam" placeholder="选择考试" style="width: 240px" size="default">
        <el-option v-for="e in examOptions" :key="e.value" :label="e.label" :value="e.value" />
      </el-select>
      <el-select v-model="filterClass" placeholder="按班级筛选" style="width: 160px" size="default" clearable>
        <el-option v-for="cls in classOptions" :key="cls.value" :label="cls.label" :value="cls.value" />
      </el-select>
      <el-select v-model="filterScoreRange" placeholder="按分数段筛选" style="width: 140px" size="default" clearable>
        <el-option label="优秀 (90+)" value="excellent" />
        <el-option label="良好 (80-89)" value="good" />
        <el-option label="中等 (70-79)" value="average" />
        <el-option label="及格 (60-69)" value="pass" />
        <el-option label="不及格 (<60)" value="fail" />
      </el-select>
    </div>

    <!-- 核心指标卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-primary-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-primary-50 flex items-center justify-center">
            <i class="ri-bar-chart-line text-primary-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">平均分</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ metrics.avgScore }}</div>
        <div class="text-xs text-green-500 mt-1"><i class="ri-arrow-up-line"></i> 较上次 +{{ metrics.avgScoreChange }}</div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-success-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-green-50 flex items-center justify-center">
            <i class="ri-trophy-line text-green-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">最高分</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ metrics.maxScore }}</div>
        <div class="text-xs text-gray-500 mt-1">{{ metrics.topStudent }}</div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-danger-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-red-50 flex items-center justify-center">
            <i class="ri-arrow-down-line text-danger-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">最低分</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ metrics.minScore }}</div>
        <div class="text-xs text-gray-500 mt-1">{{ metrics.bottomStudent }}</div>
      </div>

      <div class="bg-white rounded-xl p-5 shadow-sm border-l-4 border-blue-400 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center">
            <i class="ri-pie-chart-line text-blue-500 text-xl"></i>
          </div>
          <span class="text-xs text-gray-400">及格率</span>
        </div>
        <div class="text-2xl font-bold text-gray-800">{{ metrics.passRate }}%</div>
        <div class="text-xs text-blue-500 mt-1">{{ metrics.passCount }}/{{ metrics.totalCount }} 人及格</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="grid grid-cols-2 gap-5">
      <!-- 成绩分布直方图 -->
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <h3 class="text-sm font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <i class="ri-bar-chart-box-line text-primary-500"></i>成绩分布
        </h3>
        <div ref="scoreDistRef" style="height: 300px"></div>
      </div>

      <!-- 班级成绩对比图 -->
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <h3 class="text-sm font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <i class="ri-group-line text-primary-500"></i>班级成绩对比
        </h3>
        <div ref="classCompareRef" style="height: 300px"></div>
      </div>

      <!-- 知识点掌握度雷达图 -->
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <h3 class="text-sm font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <i class="ri-radar-line text-primary-500"></i>知识点掌握度
        </h3>
        <div ref="radarRef" style="height: 300px"></div>
      </div>

      <!-- 题型得分率 -->
      <div class="bg-white rounded-xl p-5 shadow-sm">
        <h3 class="text-sm font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <i class="ri-file-chart-line text-primary-500"></i>题型得分率
        </h3>
        <div ref="questionTypeRef" style="height: 300px"></div>
      </div>
    </div>

    <!-- 成绩排名表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <div class="p-4 border-b border-gray-100 flex items-center justify-between">
        <h3 class="text-sm font-semibold text-gray-700 flex items-center gap-2">
          <i class="ri-list-ordered text-primary-500"></i>成绩排名
        </h3>
        <el-radio-group v-model="rankTab" size="small">
          <el-radio-button value="top">Top 10</el-radio-button>
          <el-radio-button value="bottom">Bottom 5</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="rankData" stripe style="width: 100%">
        <el-table-column label="排名" width="70" align="center">
          <template #default="{ $index }">
            <span v-if="$index < 3" :class="{ 'text-yellow-500': $index === 0, 'text-gray-400': $index === 1, 'text-orange-400': $index === 2 }" class="font-bold text-lg">
              {{ $index === 0 ? '🥇' : $index === 1 ? '🥈' : '🥉' }}
            </span>
            <span v-else class="text-gray-500 font-medium">{{ rankTab === 'top' ? $index + 1 : `倒${$index + 1}` }}</span>
          </template>
        </el-table-column>
        <el-table-column label="学号" prop="studentId" min-width="120" />
        <el-table-column label="姓名" prop="name" min-width="90" />
        <el-table-column label="班级" prop="className" min-width="180" />
        <el-table-column label="得分" prop="score" width="80" sortable>
          <template #default="{ row }">
            <span class="font-semibold" :class="row.score >= 90 ? 'text-green-600' : row.score >= 60 ? 'text-gray-700' : 'text-red-500'">
              {{ row.score }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="排名变化" width="90" align="center">
          <template #default="{ row }">
            <span v-if="row.rankChange > 0" class="text-green-500 text-xs">
              <i class="ri-arrow-up-line"></i>{{ row.rankChange }}
            </span>
            <span v-else-if="row.rankChange < 0" class="text-red-500 text-xs">
              <i class="ri-arrow-down-line"></i>{{ Math.abs(row.rankChange) }}
            </span>
            <span v-else class="text-gray-400 text-xs">-</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { getGradeStatistics, getStudentRanking, getScoreDistribution, getClassComparison, getTeacherCourses, getSchedules, getExamKnowledgeMastery, getDifficultyAnalysis } from '@/api/teacher'

// ========== 筛选下拉框（动态加载） ==========
const selectedCourse = ref('')
const selectedExam = ref('')
const filterClass = ref('')
const filterScoreRange = ref('')

const courseOptions = ref<Array<{ label: string; value: string }>>([])
const examOptions = ref<Array<{ label: string; value: string; courseId?: number; totalScore?: number; submitCount?: number; avgScore?: number; maxScore?: number }>>([])
const classOptions = ref<Array<{ label: string; value: string }>>([])
const allExamOptions = ref<Array<{ label: string; value: string; courseId?: number; totalScore?: number; submitCount?: number; avgScore?: number; maxScore?: number }>>([])

// 加载筛选选项
async function loadFilterOptions() {
  try {
    // 加载课程列表
    const coursesRes = await getTeacherCourses({ pageSize: 100 })
    const courses = Array.isArray(coursesRes) ? coursesRes : (coursesRes?.list || coursesRes?.data?.list || coursesRes?.data || [])
    courseOptions.value = courses.map((c: any) => ({
      label: c.name,
      value: String(c.id),
    }))
    // 加载考试列表
    const examsRes = await getSchedules({ pageSize: 100 })
    const exams = Array.isArray(examsRes) ? examsRes : (examsRes?.list || examsRes?.data?.list || examsRes?.data || [])
    allExamOptions.value = exams.map((e: any) => ({
      label: e.examName || e.name,
      value: String(e.id),
      courseId: e.courseId,
      totalScore: Number(e.totalScore || 0),
      submitCount: Number(e.submitCount || 0),
      avgScore: Number(e.avgScore || 0),
      maxScore: Number(e.maxScore || 0),
    }))
    if (!selectedCourse.value) {
      const preferredExam = allExamOptions.value.find(e => (e.submitCount || 0) > 0 && (e.maxScore || 0) > 0 && (e.totalScore || 0) >= 60)
        || allExamOptions.value.find(e => (e.submitCount || 0) > 0 && (e.maxScore || 0) > 0)
        || allExamOptions.value.find(e => (e.submitCount || 0) > 0)
      selectedCourse.value = preferredExam?.courseId ? String(preferredExam.courseId) : (courseOptions.value[0]?.value || '')
    }
    syncExamOptions()
  } catch (err) {
    console.error('加载筛选选项失败', err)
    // 保留空数组，等待后端接口就绪
  }
}

function syncExamOptions() {
  examOptions.value = selectedCourse.value
    ? allExamOptions.value.filter(e => String(e.courseId || '') === selectedCourse.value)
    : [...allExamOptions.value]
  const currentStillValid = examOptions.value.some(e => e.value === selectedExam.value)
  if (!currentStillValid) {
    const preferredExam = examOptions.value.find(e => (e.submitCount || 0) > 0 && (e.maxScore || 0) > 0 && (e.totalScore || 0) >= 60)
      || examOptions.value.find(e => (e.submitCount || 0) > 0 && (e.maxScore || 0) > 0)
      || examOptions.value.find(e => (e.submitCount || 0) > 0)
      || examOptions.value[0]
    selectedExam.value = preferredExam?.value || ''
  }
}

// ========== 加载状态 ==========
const loading = ref(false)

// ========== 指标数据 ==========
const metrics = ref({
  avgScore: 0,
  avgScoreChange: 0,
  maxScore: 0,
  topStudent: '-',
  minScore: 0,
  bottomStudent: '-',
  passRate: 0,
  passCount: 0,
  totalCount: 0,
})

// ========== 排名 Tab ==========
const rankTab = ref<'top' | 'bottom'>('top')

const topRankData = ref<Array<{ studentId: string; name: string; className: string; score: number; rankChange: number }>>([])
const bottomRankData = ref<Array<{ studentId: string; name: string; className: string; score: number; rankChange: number }>>([])

const rankData = computed(() => {
  return rankTab.value === 'top' ? topRankData.value : bottomRankData.value
})

// ========== 加载分析数据（含图表数据） ==========
async function loadAnalytics() {
  loading.value = true
  try {
    const examId = Number(selectedExam.value)

    // 加载统计指标
    const statsRes = await getGradeStatistics(examId)
    if (statsRes) {
      const data = statsRes as any
      metrics.value = {
        avgScore: data.avgScore || 0,
        avgScoreChange: data.avgScoreChange || 0,
        maxScore: data.maxScore || 0,
        topStudent: data.topStudent || '-',
        minScore: data.minScore || 0,
        bottomStudent: data.bottomStudent || '-',
        passRate: data.passRate || 0,
        passCount: data.passCount || 0,
        totalCount: data.totalStudents || 0,
      }

      // 从统计结果中提取图表数据
      if (data.scoreDistribution) {
        const dist = data.scoreDistribution
        chartData.value.scoreDistribution = ['0-59', '60-69', '70-79', '80-89', '90-100'].map(
          key => dist[key] || 0
        )
      }
    }

    // 加载班级对比数据
    try {
      const compRes = await getClassComparison({ examId })
      if (compRes) {
        const cd = compRes as any
        const classes = Array.isArray(cd) ? cd : (cd.classes || cd.data || [])
        if (classes.length > 0) {
          chartData.value.classNames = classes.map((c: any) => c.className)
          chartData.value.classAvgScores = classes.map((c: any) => c.avgScore)
          chartData.value.classMaxScores = classes.map((c: any) => c.maxScore || 100)
        }
      }
    } catch { /* 班级对比接口可能不存在 */ }

    // 加载成绩分布数据
    try {
      const distRes = await getScoreDistribution({ examId })
      if (distRes) {
        const dd = distRes as any
        const distribution = dd.distribution || dd.data || dd
        if (Array.isArray(distribution) && distribution.length > 0) {
          chartData.value.scoreDistribution = distribution.map((d: any) => d.count || d.value || 0)
        } else if (distribution && typeof distribution === 'object') {
          chartData.value.scoreDistribution = ['0-59', '60-69', '70-79', '80-89', '90-100'].map(key => distribution[key] || 0)
        }
      }
    } catch { /* 分布接口可能不存在 */ }

    // 加载考试级知识点掌握度
    try {
      const knowledgeRes = await getExamKnowledgeMastery(examId)
      const knowledgeList = Array.isArray(knowledgeRes) ? knowledgeRes : ((knowledgeRes as any)?.data || [])
      chartData.value.knowledgePoints = knowledgeList.map((item: any) => item.name).filter(Boolean)
      chartData.value.knowledgeMastery = knowledgeList.map((item: any) => Number(item.value ?? item.mastery ?? 0))
    } catch {
      chartData.value.knowledgePoints = []
      chartData.value.knowledgeMastery = []
    }

    // 加载题型得分率，复用难度分析真实实际得分率
    try {
      const difficultyRes = await getDifficultyAnalysis(examId)
      const difficultyList = Array.isArray(difficultyRes) ? difficultyRes : ((difficultyRes as any)?.data || [])
      chartData.value.questionTypes = difficultyList.map((item: any) => item.typeName || item.type || '未知题型')
      chartData.value.questionTypeRates = difficultyList.map((item: any) => Number(item.actualRate || 0))
    } catch {
      chartData.value.questionTypes = []
      chartData.value.questionTypeRates = []
    }

    // 加载排名数据
    const rankRes = await getStudentRanking(examId, { page: 1, pageSize: 50 })
    const rankList = Array.isArray(rankRes)
      ? rankRes
      : ((rankRes as any)?.list || (rankRes as any)?.data?.list || [])
    topRankData.value = rankList.slice(0, 10).map((item: any, index: number) => ({
      studentId: item.studentId || item.userId || '',
      name: item.studentName || item.realName || `学生${index + 1}`,
      className: item.className || '-',
      score: item.totalScore || item.score || 0,
      rankChange: item.rankChange || 0,
    }))
    bottomRankData.value = rankList.slice(-5).reverse().map((item: any, index: number) => ({
      studentId: item.studentId || item.userId || '',
      name: item.studentName || item.realName || `学生${index + 1}`,
      className: item.className || '-',
      score: item.totalScore || item.score || 0,
      rankChange: item.rankChange || 0,
    }))
  } catch (err) {
    console.error('加载分析数据失败', err)
  } finally {
    loading.value = false
  }
}

// ========== ECharts 图表数据（从API加载） ==========
const scoreDistRef = ref<HTMLElement>()
const classCompareRef = ref<HTMLElement>()
const radarRef = ref<HTMLElement>()
const questionTypeRef = ref<HTMLElement>()

// 图表数据缓存
const chartData = ref({
  scoreDistribution: [] as number[],
  classNames: [] as string[],
  classAvgScores: [] as number[],
  classMaxScores: [] as number[],
  knowledgePoints: [] as string[],
  knowledgeMastery: [] as number[],
  questionTypes: [] as string[],
  questionTypeRates: [] as number[],
})

let charts: echarts.ECharts[] = []

function initCharts() {
  nextTick(() => {
    // 销毁旧图表
    charts.forEach(c => c.dispose())
    charts = []

    const sd = chartData.value.scoreDistribution
    const distData = sd.length > 0 ? sd : [0, 0, 0, 0, 0]

    // 1. 成绩分布柱状图
    if (scoreDistRef.value) {
      const chart1 = echarts.init(scoreDistRef.value)
      charts.push(chart1)
      chart1.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: ['0-59', '60-69', '70-79', '80-89', '90-100'],
          axisLabel: { fontSize: 11 },
        },
        yAxis: { type: 'value', name: '人数', nameTextStyle: { fontSize: 11 } },
        series: [{
          type: 'bar',
          data: distData,
          barWidth: '55%',
          itemStyle: {
            borderRadius: [4, 4, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#6366f1' },
              { offset: 1, color: '#a5b4fc' },
            ]),
          },
        }],
      })
    }

    const cn = chartData.value.classNames
    const cas = chartData.value.classAvgScores
    const cms = chartData.value.classMaxScores
    const classLabels = cn.length > 0 ? cn : ['暂无班级数据']
    const avgData = cas.length > 0 ? cas : [0]
    const maxData = cms.length > 0 ? cms : [0]

    // 2. 班级对比分组柱状图
    if (classCompareRef.value) {
      const chart2 = echarts.init(classCompareRef.value)
      charts.push(chart2)
      chart2.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['平均分', '最高分'], top: 0, textStyle: { fontSize: 11 } },
        grid: { left: '3%', right: '4%', bottom: '3%', top: '30px', containLabel: true },
        xAxis: {
          type: 'category',
          data: classLabels,
          axisLabel: { fontSize: 11 },
        },
        yAxis: { type: 'value', name: '分数', nameTextStyle: { fontSize: 11 }, max: 100 },
        series: [
          {
            name: '平均分',
            type: 'bar',
            data: avgData,
            barWidth: '30%',
            itemStyle: { borderRadius: [4, 4, 0, 0], color: '#6366f1' },
          },
          {
            name: '最高分',
            type: 'bar',
            data: maxData,
            barWidth: '30%',
            itemStyle: { borderRadius: [4, 4, 0, 0], color: '#22c55e' },
          },
        ],
      })
    }

    const kp = chartData.value.knowledgePoints
    const km = chartData.value.knowledgeMastery
    const hasKnowledgeData = kp.length > 0 && km.length > 0
    const kpLabels = hasKnowledgeData ? kp : ['暂无知识点数据']
    const kpValues = hasKnowledgeData ? km : [0]

    // 3. 雷达图
    if (radarRef.value) {
      const chart3 = echarts.init(radarRef.value)
      charts.push(chart3)
      chart3.setOption({
        title: hasKnowledgeData ? undefined : { text: '暂无知识点数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af', fontSize: 13, fontWeight: 'normal' } },
        tooltip: {},
        radar: {
          indicator: kpLabels.map(name => ({ name, max: 100 })),
          shape: 'polygon',
          splitNumber: 5,
          axisName: { fontSize: 11, color: '#666' },
        },
        series: [{
          type: 'radar',
          data: [
            {
              value: kpValues,
              name: '班级平均',
              areaStyle: { color: 'rgba(99,102,241,0.15)' },
              lineStyle: { color: '#6366f1' },
              itemStyle: { color: '#6366f1' },
            },
          ],
        }],
      })
    }

    const qt = chartData.value.questionTypes
    const qtr = chartData.value.questionTypeRates
    const hasQuestionTypeData = qt.length > 0 && qtr.length > 0
    const qtLabels = hasQuestionTypeData ? qt : ['暂无题型数据']
    const qtValues = hasQuestionTypeData ? qtr : [0]

    // 4. 题型得分率横向柱状图
    if (questionTypeRef.value) {
      const chart4 = echarts.init(questionTypeRef.value)
      charts.push(chart4)
      chart4.setOption({
        title: hasQuestionTypeData ? undefined : { text: '暂无题型数据', left: 'center', top: 'center', textStyle: { color: '#9ca3af', fontSize: 13, fontWeight: 'normal' } },
        tooltip: { trigger: 'axis', formatter: '{b}: {c}%' },
        grid: { left: '3%', right: '12%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
        yAxis: {
          type: 'category',
          data: qtLabels,
          axisLabel: { fontSize: 11 },
        },
        series: [{
          type: 'bar',
          data: qtValues,
          barWidth: '55%',
          itemStyle: {
            borderRadius: [0, 4, 4, 0],
            color: (params: any) => {
              const colors = ['#ef4444', '#f97316', '#eab308', '#22c55e', '#6366f1']
              return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: colors[params.dataIndex % colors.length] },
                { offset: 1, color: colors[params.dataIndex % colors.length] + '99' },
              ])
            },
          },
          label: { show: true, position: 'right', formatter: '{c}%', fontSize: 11 },
        }],
      })
    }
  })
}

onMounted(async () => {
  await loadFilterOptions()
  window.addEventListener('resize', handleResize)
  // 筛选选项加载完成后，再加载分析数据和图表
  if (selectedExam.value) {
    await loadAnalytics()
    initCharts()
  }
})

onUnmounted(() => {
  charts.forEach(c => c.dispose())
  window.removeEventListener('resize', handleResize)
})

function handleResize() {
  charts.forEach(c => c.resize())
}

watch(selectedCourse, () => {
  syncExamOptions()
})

watch([selectedExam, filterClass, filterScoreRange], async () => {
  if (!selectedExam.value) return
  await loadAnalytics()
  initCharts()
})
</script>
