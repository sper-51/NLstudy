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
          <h3 class="text-sm font-semibold text-gray-800">全校考试并发走势与系统资源监测联动</h3>
          <div class="flex items-center gap-4 text-xs text-gray-500">
            <span class="flex items-center gap-1"><span class="w-2.5 h-2.5 rounded-full bg-red-400"></span>考试并发</span>
            <span class="flex items-center gap-1"><span class="w-2.5 h-2.5 rounded-full bg-blue-500"></span>CPU</span>
            <span class="flex items-center gap-1"><span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>内存</span>
          </div>
        </div>
        <p class="text-[11px] text-gray-400 -mt-0.5 mb-3">全24小时×6个学部与时段各维度公测 · 系统 CPU/内存/占用率</p>
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
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3">
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
      </div>
    </template>

    <!-- ===== Tab 2: 课程教学成果 ===== -->
    <template v-if="activeTab === 'teaching'">
      <!-- 4个统计卡片 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-emerald-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">课程综合及格率</span>
            <i class="ri-arrow-up-line text-emerald-500 text-sm"></i><span class="text-xs font-bold text-emerald-500">+4.2%</span>
          </div>
          <div class="text-3xl font-bold text-emerald-600">85.0%</div>
          <div class="text-[11px] text-gray-400 mt-0.5">所有正式考试成绩均</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-blue-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">课程全员平均分</span>
            <i class="ri-arrow-up-line text-blue-500 text-sm"></i><span class="text-xs font-bold text-blue-500">+2.8分</span>
          </div>
          <div class="text-3xl font-bold text-blue-600">72.5<span class="text-lg ml-0.5">分</span></div>
          <div class="text-[11px] text-gray-400 mt-0.5">历次测验起点均值</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-orange-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">学生考试参与率</span>
            <i class="ri-arrow-up-line text-orange-500 text-sm"></i><span class="text-xs font-bold text-orange-500">+1.6%</span>
          </div>
          <div class="text-3xl font-bold text-orange-600">95.0%</div>
          <div class="text-[11px] text-gray-400 mt-0.5">平均到考率基</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4 border-l-4 border-red-500">
          <div class="flex items-center justify-between mb-1">
            <span class="text-xs text-gray-400">最高分与最低分</span>
            <i class="ri-arrow-down-line text-red-500 text-sm"></i><span class="text-xs font-bold text-red-500">差63.0分</span>
          </div>
          <div class="text-3xl font-bold text-red-500">98.0 / 35.0</div>
          <div class="text-[11px] text-gray-400 mt-0.5">两极化程度·监</div>
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
            <span class="text-[11px] text-gray-400">总参考者 127 人</span>
          </div>
          <div ref="scoreDistChartRef" style="height: 260px; width: 100%"></div>
        </div>
      </div>
    </template>

    <!-- ===== Tab 3: 考试质量看板 ===== -->
    <template v-if="activeTab === 'quality'">
      <!-- 学院排名柱状图 + 折线 -->
      <div class="bg-white rounded-xl shadow-sm p-5">
        <h3 class="text-sm font-semibold text-gray-800 mb-1">全校各学院考试通过率与均分排名</h3>
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

const route = useRoute()
const activeTab = ref('global')

// 从路由query读取tab参数
watch(() => route.query.tab, (val) => {
  if (val === 'teaching') activeTab.value = 'teaching'
  else if (val === 'quality') activeTab.value = 'quality'
  else activeTab.value = 'global'
}, { immediate: true })

// ==================== Tab 1: 全局控制台 数据 ====================
const globalStats = reactive([
  { label: '当前全校在线参考人数', value: '1,842', icon: 'ri-user-line', color: '#f43f5e', trend: 12, subLabel: '' },
  { label: '今日考试场次（已完成/总计）', value: '12 / 45', icon: 'ri-file-list-3-line', color: '#3b82f6', trend: 0, subLabel: '一 进行中' },
  { label: '全站总注册师生数', value: '11533', icon: 'ri-user-star-line', color: '#10b981', trend: 5.6, subLabel: '' },
  { label: '实时系统并发 GPS / 峰值', value: '150 / 120ms', icon: 'ri-speed-line', color: '#f59e0b', trend: 0, subLabel: '— 正常' },
  { label: '系统资源告警状态', value: 'CPU 42.5%\n内存 68.3%', icon: 'ri-alarm-warning-line', color: '#ef4444', trend: 0, subLabel: '— 稳定' },
])

const liveExams = ref([
  { id: 1, name: '高等数学（上）期中考试', department: '数学学院', teacher: '王教授', time: '09:00 – 11:00', participants: 156 },
  { id: 2, name: '大学英语四级模拟测试', department: '外语学院', teacher: '李老师', time: '08:30 – 10:30', participants: 203 },
  { id: 3, name: '数据结构与算法期末考', department: '计算机学院', teacher: '王教授', time: '10:00 – 12:00', participants: 89 },
  { id: 4, name: '大学物理实验操作考核', department: '物理学院', teacher: '陈教授', time: '14:00 – 16:00', participants: 67 },
])

// ==================== ECharts 实例引用 ====================
const trendChartRef = ref<HTMLElement>()
const scoreTrendChartRef = ref<HTMLElement>()
const classCompareChartRef = ref<HTMLElement>()
const scoreDistChartRef = ref<HTMLElement>()
const collegeRankChartRef = ref<HTMLElement>()
const questionTypeChartRef = ref<HTMLElement>()
const difficultyRadarRef = ref<HTMLElement>()

let charts: echarts.ECHARTS[] = []
let resizeObserver: ResizeObserver | null = null

function initChart(dom: HTMLElement | undefined, option: echarts.EChartsOption): echarts.ECHARTS | null {
  if (!dom) return null
  const chart = echarts.init(dom, null, { renderer: 'canvas' })
  chart.setOption(option)
  charts.push(chart)
  return chart
}

// 24小时趋势图配置
function getTrendOption(): echarts.EChartsOption {
  const hours = Array.from({ length: 25 }, (_, i) => `${String(i).padStart(2, '0')}:00`)
  const examData = [120, 180, 250, 320, 450, 680, 920, 1100, 1350, 1280, 1150, 1080, 1020, 1180, 1320, 1250, 1120, 1050, 980, 890, 780, 650, 480, 350, 200]
  const cpuData = [35, 38, 42, 45, 52, 58, 65, 72, 78, 75, 70, 68, 62, 67, 73, 76, 74, 69, 64, 58, 52, 48, 42, 38, 34]
  const memData = [48, 50, 53, 55, 58, 62, 66, 70, 74, 72, 69, 67, 64, 66, 70, 72, 71, 68, 65, 61, 57, 54, 51, 49, 47]
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' }, backgroundColor: '#fff', borderColor: '#e5e7eb', textStyle: { color: '#374151', fontSize: 12 } },
    grid: { left: 50, right: 50, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: hours, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 10, color: '#9ca3af', interval: 2 }, boundaryGap: false },
    yAxis: [
      { type: 'value', name: '并发人数', nameTextStyle: { fontSize: 10, color: '#9ca3af' }, position: 'left', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f3f4f6' } }, axisLabel: { fontSize: 10, color: '#9ca3af' }, min: 0, max: 1600 },
      { type: 'value', name: '%', nameTextStyle: { fontSize: 10, color: '#9ca3af' }, position: 'right', axisLine: { show: false }, splitLine: { show: false }, axisLabel: { fontSize: 10, color: '#9ca3af', formatter: '{value}%' }, min: 0, max: 100 },
    ],
    series: [
      { name: '考试并发', type: 'line', data: examData, smooth: true, symbol: 'none', lineStyle: { width: 2.5, color: '#f43f5e' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(244,63,94,0.25)' }, { offset: 1, color: 'rgba(244,63,94,0.02)' }]) } },
      { name: 'CPU', type: 'line', yAxisIndex: 1, data: cpuData, smooth: true, symbol: 'none', lineStyle: { width: 2, color: '#3b82f6' } },
      { name: '内存', type: 'line', yAxisIndex: 1, data: memData, smooth: true, symbol: 'none', lineStyle: { width: 2, color: '#10b981' } },
    ],
  }
}

// 成绩趋势组合图
function getScoreTrendOption(): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0, itemWidth: 12, itemHeight: 8, textStyle: { fontSize: 11 } },
    grid: { left: 50, right: 50, top: 15, bottom: 40 },
    xAxis: { type: 'category', data: ['第一章节测', '第二章节测', '第三章节测', '期中考试', '第四章节测', '第五章节测', '期末考试'], axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 11, color: '#6b7280' } },
    yAxis: [
      { type: 'value', name: '分数', min: 0, max: 100, axisLabel: { fontSize: 10 }, splitLine: { lineStyle: { color: '#f3f4f6' } } },
      { type: 'value', name: '及格率%', min: 0, max: 100, axisLabel: { fontSize: 10, formatter: '{value}%' }, splitLine: { show: false } },
    ],
    series: [
      { name: '及格率', type: 'bar', barWidth: 20, data: [72, 75, 78, 81, 79, 83, 85], itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#86efac' }, { offset: 1, color: '#d1fae5' }]), borderRadius: [4, 4, 0, 0] } },
      { name: '越线平均分', type: 'line', yAxisIndex: 1, data: [68, 70, 73, 75, 74, 77, 80], smooth: true, symbol: 'circle', symbolSize: 6, lineStyle: { width: 2.5, color: '#3b82f6' }, itemStyle: { color: '#3b82f6' } },
    ],
  }
}

// 多班级对比柱状图
function getClassCompareOption(): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0, itemWidth: 12, itemHeight: 8, textStyle: { fontSize: 11 } },
    grid: { left: 45, right: 15, top: 10, bottom: 35 },
    xAxis: { type: 'category', data: ['一班', '二班', '三班', '四班', '五班', '六班'], axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 11, color: '#6b7280' } },
    yAxis: { type: 'value', min: 0, max: 100, axisLabel: { fontSize: 10 }, splitLine: { lineStyle: { color: '#f3f4f6' } } },
    series: [
      { name: '平均分', type: 'bar', barWidth: 14, data: [78, 72, 81, 69, 84, 76], itemStyle: { color: '#3b82f6', borderRadius: [3, 3, 0, 0] } },
      { name: '及格率', type: 'bar', barWidth: 14, data: [88, 82, 90, 78, 94, 86], itemStyle: { color: '#10b981', borderRadius: [3, 3, 0, 0] } },
    ],
  }
}

// 分数分布环形图
function getScoreDistOption(): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', itemWidth: 10, itemHeight: 8, textStyle: { fontSize: 11 } },
    series: [{
      type: 'pie', radius: ['50%', '72%'], center: ['40%', '50%'],
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
      data: [
        { value: 26, name: '优秀(≥90)', itemStyle: { color: '#22c55e' } },
        { value: 41, name: '良好(80-89)', itemStyle: { color: '#3b82f6' } },
        { value: 36, name: '中等(70-79)', itemStyle: { color: '#f59e0b' } },
        { value: 16, name: '及格(60-69)', itemStyle: { color: '#f97316' } },
        { value: 8, name: '不及格(<60)', itemStyle: { color: '#ef4444' } },
      ],
    }],
  }
}

// 学院排名组合图
function getCollegeRankOption(): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'axis' },
    legend: { top: 0, itemWidth: 12, itemHeight: 8, textStyle: { fontSize: 11 } },
    grid: { left: 60, right: 60, top: 25, bottom: 20 },
    xAxis: { type: 'value', min: 0, max: 100, axisLabel: { fontSize: 10, formatter: '{value}' }, splitLine: { lineStyle: { color: '#f3f4f6' } } },
    yAxis: { type: 'category', data: ['数学学院', '计算机学院', '外语学院', '物理学院', '化学学院', '经济学院', '文学院', '法学院'], inverse: true, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { fontSize: 11, color: '#6b7280' } },
    series: [
      { name: '及格率', type: 'bar', barWidth: 14, data: [92, 88, 95, 87, 91, 85, 93, 89], itemStyle: { color: '#10b981', borderRadius: [0, 3, 3, 0] } },
      { name: '平均分', type: 'bar', barWidth: 14, data: [78, 82, 76, 74, 80, 72, 79, 77], itemStyle: { color: '#3b82f6', borderRadius: [0, 3, 3, 0] } },
      { name: '总分', type: 'line', data: [85, 88, 82, 80, 86, 78, 87, 84], smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2, color: '#8b5cf6' }, itemStyle: { color: '#8b5cf6' } },
    ],
  }
}

// 题型分布环形图
function getQuestionTypeOption(): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c}% ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', itemWidth: 10, itemHeight: 8, textStyle: { fontSize: 11 } },
    series: [{
      type: 'pie', radius: ['45%', '70%'], center: ['38%', '50%'],
      label: { show: true, position: 'outside', fontSize: 11, fontWeight: 'bold', formatter: '{b}\n{d}%' },
      labelLine: { length: 12, length2: 8 },
      emphasis: { label: { show: true, fontSize: 13 } },
      data: [
        { value: 42, name: '单选题', itemStyle: { color: '#3b82f6' } },
        { value: 18, name: '多选题', itemStyle: { color: '#10b981' } },
        { value: 22, name: '填空题', itemStyle: { color: '#f59e0b' } },
        { value: 12, name: '简答题', itemStyle: { color: '#ef4444' } },
        { value: 6, name: '编程题', itemStyle: { color: '#8b5cf6' } },
      ],
    }],
  }
}

// 难度系数雷达图
function getDifficultyRadarOption(): echarts.EChartsOption {
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
        value: [85, 78, 65, 42, 28],
        name: '题库难度库差',
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
  initAllCharts()
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
