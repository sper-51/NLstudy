<template>
  <div class="min-h-screen flex items-center justify-center bg-[#F5F7FA]">
    <div class="text-center">
      <div class="mb-6">
        <i class="ri-error-warning-fill text-6xl text-gray-300"></i>
      </div>
      <h1 class="text-2xl font-bold text-gray-800 mb-2">404 - 页面未找到</h1>
      <p class="text-gray-500 mb-8">抱歉，您访问的页面不存在或已被移除</p>
      <router-link to="/dashboard" class="inline-flex items-center gap-2 px-6 py-2.5 bg-primary-500 text-white rounded-lg hover:bg-primary-600 transition-colors cursor-pointer">
        <i class="ri-home-4-line"></i>返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>
