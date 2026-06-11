<template>
  <div class="h-full overflow-auto p-6 space-y-6">
    <div class="flex items-start justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800">我的信息</h1>
        <p class="text-sm text-gray-500 mt-1">班级身份由管理端维护，课程身份由课程码加入后生成。</p>
      </div>
      <el-button type="primary" :loading="loading" @click="loadProfile">
        <i class="ri-refresh-line mr-1"></i>刷新
      </el-button>
    </div>

    <div class="grid grid-cols-1 xl:grid-cols-[minmax(0,1fr)_360px] gap-6">
      <div class="space-y-6">
        <div class="bg-white rounded-xl p-6 shadow-sm">
          <div class="flex items-center justify-between mb-5">
            <h2 class="text-base font-semibold text-gray-800 flex items-center gap-2">
              <i class="ri-user-settings-line text-primary-500"></i>
              基本信息
            </h2>
            <el-tag type="success" effect="light">学生账号</el-tag>
          </div>

          <el-form label-position="top" class="grid grid-cols-1 md:grid-cols-2 gap-x-5">
            <el-form-item label="学号 / 登录账号">
              <el-input :model-value="profile.username" disabled />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="form.realName" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-form>

          <div class="flex justify-end">
            <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
          </div>
        </div>

        <div class="bg-white rounded-xl p-6 shadow-sm">
          <h2 class="text-base font-semibold text-gray-800 flex items-center gap-2 mb-4">
            <i class="ri-team-line text-blue-500"></i>
            班级身份
          </h2>
          <div v-if="profile.classes?.length" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="item in profile.classes"
              :key="item.id"
              class="rounded-xl border border-blue-100 bg-blue-50/40 p-4"
            >
              <div class="text-sm font-semibold text-gray-800">{{ item.name }}</div>
              <div class="text-xs text-blue-600 mt-1">{{ item.grade || '未设置年级' }}</div>
              <p class="text-xs text-gray-500 mt-2">{{ item.description || '暂无班级说明' }}</p>
            </div>
          </div>
          <div v-else class="py-8 text-center text-gray-400">
            <i class="ri-team-line text-3xl block mb-2"></i>
            暂未分配班级，请联系管理员
          </div>
        </div>

        <div class="bg-white rounded-xl p-6 shadow-sm">
          <h2 class="text-base font-semibold text-gray-800 flex items-center gap-2 mb-4">
            <i class="ri-book-3-line text-primary-500"></i>
            课程身份
          </h2>
          <el-table :data="profile.courses || []" empty-text="暂无已加入课程">
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="courseCode" label="课程码" width="150" />
            <el-table-column prop="credits" label="学分" width="90" />
            <el-table-column prop="hours" label="学时" width="90" />
          </el-table>
        </div>
      </div>

      <div class="bg-white rounded-xl p-6 shadow-sm h-fit">
        <h2 class="text-base font-semibold text-gray-800 flex items-center gap-2 mb-4">
          <i class="ri-lock-password-line text-warning-500"></i>
          修改密码
        </h2>
        <el-form label-position="top">
          <el-form-item label="旧密码">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认新密码">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
          </el-form-item>
        </el-form>
        <el-button type="warning" class="w-full" :loading="changingPassword" @click="submitPassword">
          确认修改密码
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { changePassword, getStudentProfile, updateStudentProfile } from '@/api/student'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const changingPassword = ref(false)
const profile = ref<any>({})

const form = reactive({
  realName: '',
  email: '',
  phone: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

async function loadProfile() {
  loading.value = true
  try {
    const data = await getStudentProfile()
    profile.value = data || {}
    form.realName = data?.realName || ''
    form.email = data?.email || ''
    form.phone = data?.phone || ''
  } catch (error) {
    console.error('加载学生信息失败', error)
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  saving.value = true
  try {
    const updated = await updateStudentProfile({
      realName: form.realName.trim(),
      email: form.email.trim(),
      phone: form.phone.trim(),
    })
    if (updated?.id) {
      userStore.setUserInfo({
        ...userStore.userInfo,
        ...updated,
        role: 'student',
        status: userStore.userInfo?.status ?? 1,
      })
    }
    ElMessage.success('个人信息已保存')
    await loadProfile()
  } catch (error: any) {
    ElMessage.error(error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function submitPassword() {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请输入旧密码和新密码')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于 6 位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  changingPassword.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    })
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    ElMessage.success('密码已修改')
  } catch (error: any) {
    ElMessage.error(error?.message || '修改失败')
  } finally {
    changingPassword.value = false
  }
}

onMounted(loadProfile)
</script>
