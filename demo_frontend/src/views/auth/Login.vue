<template>
  <div class="login-shell min-h-screen overflow-hidden text-slate-900">
    <div class="login-grid"></div>
    <div class="login-glow login-glow-a"></div>
    <div class="login-glow login-glow-b"></div>

    <main class="relative z-10 min-h-screen grid lg:grid-cols-[1fr_480px]">
      <section class="hidden lg:flex flex-col justify-between px-16 py-14">
        <div class="brand-row">
          <div class="brand-mark">
            <i class="ri-graduation-cap-fill"></i>
          </div>
          <div>
            <div class="brand-code">NL STUDY</div>
            <div class="brand-name">智考平台</div>
          </div>
        </div>

        <div class="hero-copy">
          <div class="status-pill">
            <span></span>
            统一身份入口 · 教师 / 学生 / 管理端
          </div>
          <h1>教学、考试与学情分析一体化平台。</h1>
          <p>课程、考试、阅卷、成绩与 AI 辅导集中在同一套教学闭环中，入口保持克制，操作保持清晰。</p>

          <div class="metric-strip">
            <div>
              <strong>3</strong>
              <span>端工作台</span>
            </div>
            <div>
              <strong>AI</strong>
              <span>出题与辅导</span>
            </div>
            <div>
              <strong>实时</strong>
              <span>考试数据</span>
            </div>
          </div>
        </div>

        <div class="left-footer">
          <span>请使用分配账号登录</span>
          <button type="button" class="tutorial-link" @click="openTutorial">
            <i class="ri-guide-line"></i>
            系统使用教程
          </button>
        </div>
      </section>

      <section class="flex items-center justify-center px-5 py-8 lg:pr-16">
        <div class="w-full max-w-[430px]">
          <div class="mobile-brand lg:hidden">
            <div class="brand-mark">
              <i class="ri-graduation-cap-fill"></i>
            </div>
            <div>
              <div class="brand-code">NL STUDY</div>
              <div class="brand-name">智考平台</div>
            </div>
          </div>

          <div class="login-card">
            <div class="card-head">
              <div>
                <div class="eyebrow">
                  <i class="ri-shield-check-line"></i>
                  安全登录
                </div>
                <h2>欢迎回来</h2>
                <p>登录后将自动进入对应工作台。</p>
              </div>
              <div class="card-stamp">
                <i class="ri-fingerprint-line"></i>
              </div>
            </div>

            <div class="role-hints">
              <span>管理员</span>
              <span>教师</span>
              <span>学生</span>
            </div>

            <form @submit.prevent="handleLogin" class="space-y-5">
              <transition
                enter-active-class="transition duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-2"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition duration-150 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-2"
              >
                <div v-if="errorMsg" class="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-600 flex items-start gap-2">
                  <i class="ri-error-warning-line mt-0.5"></i>
                  <span>{{ errorMsg }}</span>
                </div>
              </transition>

              <div class="field-group">
                <label class="field-label">账号</label>
                <div class="field-wrap">
                  <i class="ri-user-line field-icon"></i>
                  <input
                    v-model="loginForm.username"
                    type="text"
                    placeholder="请输入账号"
                    class="field-input"
                    autocomplete="username"
                  />
                </div>
              </div>

              <div class="field-group">
                <label class="field-label">密码</label>
                <div class="field-wrap">
                  <i class="ri-lock-line field-icon"></i>
                  <input
                    v-model="loginForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="请输入密码"
                    class="field-input pr-12"
                    autocomplete="current-password"
                  />
                  <button
                    type="button"
                    class="field-toggle"
                    @click="showPassword = !showPassword"
                  >
                    <i :class="showPassword ? 'ri-eye-off-line' : 'ri-eye-line'"></i>
                  </button>
                </div>
              </div>

              <div class="flex items-center justify-between text-sm">
                <label class="inline-flex items-center gap-2 text-slate-500 cursor-pointer">
                  <input
                    v-model="rememberMe"
                    type="checkbox"
                    class="w-4 h-4 rounded border-slate-300 text-primary-500 focus:ring-primary-400"
                  />
                  记住账号
                </label>
                <span class="text-slate-400">如忘记密码，请联系管理员</span>
              </div>

              <button type="submit" :disabled="loading" class="submit-btn">
                <span v-if="loading" class="w-4 h-4 rounded-full border-2 border-white/35 border-t-white animate-spin"></span>
                <span>{{ loading ? '正在登录...' : '进入平台' }}</span>
              </button>

              <button type="button" class="tutorial-btn" @click="openTutorial">
                <i class="ri-book-open-line"></i>
                查看使用教程
              </button>
            </form>

            <div class="login-note">
              <i class="ri-route-line"></i>
              <span>系统会依据账号角色跳转，无需手动选择端口。</span>
            </div>
          </div>

          <p class="copyright">© 2026 NLstudy · 智考平台</p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  username: localStorage.getItem('rememberedUsername') || '',
  password: '',
})

const showPassword = ref(false)
const rememberMe = ref(!!localStorage.getItem('rememberedUsername'))
const loading = ref(false)
const errorMsg = ref('')

function openTutorial() {
  window.open('/tutorial.html', '_blank', 'noopener,noreferrer')
}

onMounted(() => {
  if (userStore.token && userStore.userInfo?.role) {
    // 登录页保留可访问，但如果用户主动回到登录页，不再强制清理会话
  }
})

async function handleLogin() {
  errorMsg.value = ''

  if (!loginForm.value.username || !loginForm.value.password) {
    errorMsg.value = '请输入账号和密码'
    return
  }

  loading.value = true

  try {
    userStore.clearSession()
    const res = await login(loginForm.value.username, loginForm.value.password)

    if (!res) {
      throw new Error('登录失败，请稍后重试')
    }

    const { token, userInfo } = res
    userStore.setToken(token)
    userStore.setUserInfo(userInfo)

    if (rememberMe.value) {
      localStorage.setItem('rememberedUsername', loginForm.value.username)
    } else {
      localStorage.removeItem('rememberedUsername')
    }

    if (userInfo.role === 'admin') {
      router.push('/admin')
    } else if (userInfo.role === 'teacher') {
      router.push('/teacher/dashboard')
    } else {
      router.push('/home')
    }
  } catch (error: any) {
    console.error('Login error:', error)
    errorMsg.value = error.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-shell {
  position: relative;
  background:
    radial-gradient(circle at 14% 12%, rgba(37, 99, 235, 0.13), transparent 30%),
    radial-gradient(circle at 88% 10%, rgba(14, 165, 233, 0.12), transparent 26%),
    linear-gradient(135deg, #f7f9fc 0%, #edf4ff 48%, #f8fbff 100%);
}

.login-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(100, 116, 139, 0.075) 1px, transparent 1px),
    linear-gradient(90deg, rgba(100, 116, 139, 0.075) 1px, transparent 1px);
  background-size: 36px 36px;
  mask-image: linear-gradient(to bottom, rgba(0, 0, 0, 0.72), transparent 92%);
  pointer-events: none;
}

.login-glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(64px);
  opacity: 0.55;
  pointer-events: none;
}

.login-glow-a {
  width: 22rem;
  height: 22rem;
  left: 8%;
  top: 8%;
  background: rgba(59, 130, 246, 0.15);
}

.login-glow-b {
  width: 18rem;
  height: 18rem;
  right: 8%;
  bottom: 14%;
  background: rgba(6, 182, 212, 0.13);
}

.brand-row,
.mobile-brand {
  display: flex;
  align-items: center;
  gap: 0.85rem;
}

.mobile-brand {
  justify-content: center;
  margin-bottom: 1.4rem;
}

.brand-mark {
  width: 3.35rem;
  height: 3.35rem;
  border-radius: 1.15rem;
  display: grid;
  place-items: center;
  color: #fff;
  font-size: 1.5rem;
  background: linear-gradient(135deg, #1d4ed8 0%, #0ea5e9 100%);
  box-shadow: 0 18px 42px rgba(37, 99, 235, 0.24);
}

.brand-code {
  color: #1e293b;
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.18em;
}

.brand-name {
  margin-top: 0.12rem;
  color: #64748b;
  font-size: 0.95rem;
  font-weight: 600;
}

.hero-copy {
  max-width: 44rem;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  padding: 0.5rem 0.78rem;
  border-radius: 999px;
  border: 1px solid rgba(37, 99, 235, 0.14);
  background: rgba(255, 255, 255, 0.66);
  color: #335172;
  font-size: 0.82rem;
  font-weight: 700;
  box-shadow: 0 12px 34px rgba(15, 23, 42, 0.06);
}

.status-pill span {
  width: 0.48rem;
  height: 0.48rem;
  border-radius: 999px;
  background: #22c55e;
  box-shadow: 0 0 0 5px rgba(34, 197, 94, 0.12);
}

.hero-copy h1 {
  margin-top: 1.7rem;
  max-width: 38rem;
  color: #0f172a;
  font-size: clamp(3rem, 5vw, 5.4rem);
  line-height: 0.98;
  letter-spacing: -0.065em;
  font-weight: 850;
}

.hero-copy p {
  margin-top: 1.25rem;
  max-width: 32rem;
  color: #64748b;
  font-size: 1.02rem;
  line-height: 1.85;
}

.metric-strip {
  margin-top: 2.3rem;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  max-width: 34rem;
  border: 1px solid rgba(226, 232, 240, 0.92);
  border-radius: 1.5rem;
  background: rgba(255, 255, 255, 0.62);
  box-shadow: 0 22px 70px rgba(15, 23, 42, 0.07);
  backdrop-filter: blur(18px);
  overflow: hidden;
}

.metric-strip div {
  min-height: 6.25rem;
  padding: 1.15rem 1.2rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.32rem;
}

.metric-strip div + div {
  border-left: 1px solid rgba(226, 232, 240, 0.86);
}

.metric-strip strong {
  color: #1d4ed8;
  font-size: 1.55rem;
  line-height: 1;
  font-weight: 850;
}

.metric-strip span {
  color: #64748b;
  font-size: 0.82rem;
  font-weight: 700;
}

.left-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 34rem;
  color: #94a3b8;
  font-size: 0.86rem;
}

.login-card {
  position: relative;
  padding: 2rem;
  border-radius: 1.8rem;
  border: 1px solid rgba(255, 255, 255, 0.92);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.75)),
    rgba(255, 255, 255, 0.76);
  box-shadow: 0 32px 90px rgba(15, 23, 42, 0.14);
  backdrop-filter: blur(24px);
}

.login-card::before {
  content: '';
  position: absolute;
  inset: 0.65rem;
  border-radius: 1.35rem;
  border: 1px solid rgba(226, 232, 240, 0.72);
  pointer-events: none;
}

.card-head {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  color: #2563eb;
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.08em;
}

.card-head h2 {
  margin-top: 0.55rem;
  color: #0f172a;
  font-size: 2.15rem;
  line-height: 1;
  font-weight: 850;
  letter-spacing: -0.04em;
}

.card-head p {
  margin-top: 0.65rem;
  color: #64748b;
  font-size: 0.92rem;
}

.card-stamp {
  width: 3rem;
  height: 3rem;
  border-radius: 1rem;
  display: grid;
  place-items: center;
  color: #2563eb;
  font-size: 1.3rem;
  background: rgba(239, 246, 255, 0.92);
  border: 1px solid rgba(191, 219, 254, 0.8);
}

.role-hints {
  position: relative;
  z-index: 1;
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.45rem;
}

.role-hints span {
  flex: 1;
  height: 2rem;
  display: grid;
  place-items: center;
  border-radius: 999px;
  border: 1px solid rgba(226, 232, 240, 0.95);
  background: rgba(248, 250, 252, 0.86);
  color: #64748b;
  font-size: 0.78rem;
  font-weight: 700;
}

.login-card form,
.login-note {
  position: relative;
  z-index: 1;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.field-label {
  font-size: 0.86rem;
  color: #334155;
  font-weight: 700;
}

.field-wrap {
  position: relative;
}

.field-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 1rem;
}

.field-input {
  width: 100%;
  height: 3.25rem;
  border-radius: 1rem;
  border: 1px solid rgba(203, 213, 225, 0.9);
  background: rgba(248, 250, 252, 0.9);
  padding: 0 1rem 0 2.9rem;
  color: #0f172a;
  font-size: 0.95rem;
  outline: none;
  transition: all 0.2s ease;
}

.field-input:focus {
  border-color: rgba(99, 102, 241, 0.55);
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.08);
  background: #fff;
}

.field-toggle {
  position: absolute;
  right: 0.95rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  transition: color 0.2s ease;
}

.field-toggle:hover {
  color: #475569;
}

.submit-btn {
  width: 100%;
  height: 3.45rem;
  border-radius: 1.1rem;
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 52%, #0ea5e9 100%);
  color: #fff;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  box-shadow: 0 18px 40px rgba(79, 70, 229, 0.26);
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 22px 44px rgba(79, 70, 229, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.tutorial-btn {
  width: 100%;
  height: 3.05rem;
  border-radius: 1.05rem;
  border: 1px solid rgba(99, 102, 241, 0.18);
  background: rgba(255, 255, 255, 0.72);
  color: #4f46e5;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.2s ease;
}

.tutorial-btn:hover,
.tutorial-link:hover {
  transform: translateY(-1px);
  color: #2563eb;
  border-color: rgba(37, 99, 235, 0.25);
  background: rgba(239, 246, 255, 0.9);
}

.tutorial-link {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.42rem 0.72rem;
  border-radius: 999px;
  border: 1px solid rgba(99, 102, 241, 0.16);
  background: rgba(255, 255, 255, 0.58);
  color: #4f46e5;
  font-weight: 600;
  transition: all 0.2s ease;
}

.login-note {
  margin-top: 1.25rem;
  display: flex;
  align-items: center;
  gap: 0.55rem;
  padding: 0.85rem 0.95rem;
  border-radius: 1rem;
  background: rgba(248, 250, 252, 0.78);
  color: #64748b;
  font-size: 0.8rem;
}

.login-note i {
  color: #2563eb;
}

.copyright {
  margin-top: 1.15rem;
  text-align: center;
  color: #94a3b8;
  font-size: 0.78rem;
}

@media (max-width: 1023px) {
  .login-shell {
    background:
      radial-gradient(circle at 50% 0%, rgba(37, 99, 235, 0.12), transparent 34%),
      linear-gradient(145deg, #f8fbff 0%, #eef4ff 100%);
  }

  .login-card {
    padding: 1.45rem;
    border-radius: 1.45rem;
  }

  .login-card::before {
    display: none;
  }

  .card-head h2 {
    font-size: 1.9rem;
  }
}
</style>
