<template>
  <div class="h-screen flex bg-[#F5F7FA] overflow-hidden">
    <TeacherSidebar />
    <main class="flex-1 overflow-y-auto">
      <div class="p-6 md:p-8">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import TeacherSidebar from './TeacherSidebar.vue'
</script>
