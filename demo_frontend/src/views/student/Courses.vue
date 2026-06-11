<template>
  <div class="h-full overflow-auto p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-xl font-bold text-gray-800">我的课程</h1>
      <p class="text-sm text-gray-500 mt-1">共 {{ courses.length }} 门课程</p>
    </div>

    <!-- 课程卡片网格 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <router-link
        v-for="course in courses"
        :key="course.id"
        :to="'/courses/' + course.id"
        class="bg-white rounded-xl p-5 shadow-sm hover:shadow-md transition-all group cursor-pointer block"
      >
        <!-- 顶部：图标 + 标签 -->
        <div class="flex items-start justify-between mb-4">
          <div 
            :class="['w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0', getCourseIconBg(course.progress)]"
          >
            <i :class="[getCourseIcon(course.code), 'text-2xl']"></i>
          </div>
          <div class="flex gap-1.5">
            <span 
              v-if="(course.pendingExamCount || 0) > 0"
              class="px-2 py-0.5 bg-danger-50 text-danger-600 text-xs font-medium rounded-full"
            >
              待考{{ course.pendingExamCount }}场
            </span>
            <span 
              v-if="(course.wrongQuestionCount || 0) > 0"
              class="px-2 py-0.5 bg-warning-50 text-warning-600 text-xs font-medium rounded-full"
            >
              错题{{ course.wrongQuestionCount }}道
            </span>
          </div>
        </div>

        <!-- 课程信息 -->
        <h3 class="font-semibold text-base text-gray-800 mb-1.5 group-hover:text-primary-600 transition-colors truncate">
          {{ course.name }}
        </h3>
        <p class="text-sm text-gray-500 mb-3">{{ course.teacherName }} · {{ course.code }}</p>

        <!-- 进度条 -->
        <div class="space-y-1.5 mb-3">
          <div class="flex items-center justify-between text-xs">
            <span class="text-gray-500">学习进度</span>
            <span :class="getProgressColor(course.progress)">{{ course.progress }}%</span>
          </div>
          <div class="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
            <div 
              class="h-full rounded-full transition-all duration-300"
              :class="getProgressBgColor(course.progress)"
              :style="{ width: course.progress + '%' }"
            ></div>
          </div>
        </div>

        <!-- 底部信息 -->
        <div class="flex items-center justify-between pt-3 border-t border-gray-100 text-xs text-gray-400">
          <span>{{ course.credits }} 学分 · {{ course.hours }} 学时</span>
          <span class="flex items-center gap-1 group-hover:text-primary-500 transition-colors">
            进入课程 <i class="ri-arrow-right-s-line"></i>
          </span>
        </div>
      </router-link>
    </div>

    <!-- 空状态 -->
    <div v-if="courses.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-400">
      <i class="ri-book-open-line text-5xl mb-4"></i>
      <p class="text-lg font-medium">暂无课程</p>
      <p class="text-sm mt-1">使用课程码加入新课程吧</p>
      <router-link to="/home" class="mt-4 px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600 transition-colors cursor-pointer text-sm">
        去加课
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Course } from '@/api/types'
import { getCourses } from '@/api/student'

const courses = ref<Course[]>([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getCourses()
    courses.value = (Array.isArray(res) ? res : []).map((c: any) => ({
      ...c,
      progress: c.progress ?? 0,
      pendingExamCount: c.pendingExamCount ?? 0,
      wrongQuestionCount: c.wrongQuestionCount ?? 0,
    }))
  } catch (err) {
    console.error('加载课程失败', err)
  } finally {
    loading.value = false
  }
})

function getCourseIcon(code: string | undefined | null): string {
  if (!code) return 'ri-book-3-fill text-primary-500'
  if (code.includes('Java') || code.includes('101')) return 'ri-code-box-fill text-blue-500'
  if (code.includes('数据结构') || code.includes('201')) return 'ri-git-branch-line text-purple-500'
  if (code.includes('数据库') || code.includes('301')) return 'ri-database-2-fill text-green-500'
  if (code.includes('网络') || code.includes('401')) return 'ri-global-line text-orange-500'
  return 'ri-book-3-fill text-primary-500'
}

function getCourseIconBg(progress: number): string {
  if (progress >= 80) return 'bg-success-50'
  if (progress >= 60) return 'bg-primary-50'
  if (progress >= 40) return 'bg-warning-50'
  return 'bg-gray-50'
}

function getProgressColor(progress: number): string {
  if (progress >= 80) return 'text-success-500 font-semibold'
  if (progress >= 60) return 'text-primary-500 font-semibold'
  if (progress >= 40) return 'text-warning-500'
  return 'text-gray-500'
}

function getProgressBgColor(progress: number): string {
  if (progress >= 80) return 'bg-success-500'
  if (progress >= 60) return 'bg-primary-500'
  if (progress >= 40) return 'bg-warning-500'
  return 'bg-gray-300'
}
</script>
