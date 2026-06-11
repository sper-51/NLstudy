<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <div class="text-center">
      <div class="w-24 h-24 mx-auto mb-6 bg-gray-100 rounded-full flex items-center justify-center">
        <i class="ri-error-warning-line text-4xl text-gray-400"></i>
      </div>
      <h1 class="text-2xl font-bold text-gray-800 mb-2">404 - 页面不存在</h1>
      <p class="text-gray-500 mb-6">您访问的页面不存在或已被移除</p>
      <router-link 
        to="/home"
        class="inline-flex items-center gap-2 px-6 py-3 bg-primary-500 text-white font-medium rounded-lg hover:bg-primary-600 transition-colors cursor-pointer"
      >
        <i class="ri-home-line"></i>
        返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>