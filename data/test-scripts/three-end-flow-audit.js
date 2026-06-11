const fs = require('fs')
const path = require('path')
const { createRequire } = require('module')

function requireFromRunner(moduleName) {
  const candidates = [
    process.env.NLSTUDY_PW_RUNNER,
    path.join(process.env.USERPROFILE || '', '.codex', 'tmp', 'pw-runner'),
  ].filter(Boolean)

  for (const runnerDir of candidates) {
    try {
      return createRequire(path.join(runnerDir, 'package.json'))(moduleName)
    } catch {}
  }

  return require(moduleName)
}

const { chromium } = requireFromRunner('playwright')

const baseURL = process.env.NLSTUDY_BASE_URL || 'http://localhost:3000'
const chromePath = process.env.PLAYWRIGHT_CHROME_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe'
const outDir = process.env.NLSTUDY_FLOW_OUT || path.join(process.cwd(), 'data', 'runtime-logs', 'three-end-flow-audit-20260608')

const accounts = {
  admin: { username: process.env.NLSTUDY_ADMIN_USER || 'admin', password: process.env.NLSTUDY_ADMIN_PASSWORD || '123456' },
  teacher: { username: process.env.NLSTUDY_TEACHER_USER || 'wanglaoshi', password: process.env.NLSTUDY_TEACHER_PASSWORD || '123456' },
  student: { username: process.env.NLSTUDY_STUDENT_USER || 'stu_001', password: process.env.NLSTUDY_STUDENT_PASSWORD || '123456' },
}

const flows = {
  admin: [
    { route: '/admin', mustContain: ['全局控制台', '课程教学成果', '考试质量看板'] },
    { route: '/admin/users/teachers', mustContain: ['教师账号', '搜索'] },
    { route: '/admin/users/students', mustContain: ['学生账号', '搜索'] },
    { route: '/admin/classes', mustContain: ['班级', '搜索'] },
    { route: '/admin/semesters', mustContain: ['学期管理', '新建学期'] },
    { route: '/admin/data/backup', mustContain: ['数据管理', '暂未接入'] },
    { route: '/admin/data/logs', mustContain: ['日志'] },
    { route: '/admin/monitor/online', mustContain: ['在线'] },
    { route: '/admin/monitor/status', mustContain: ['系统'] },
    { route: '/admin/monitor/audit', mustContain: ['操作日志审计'] },
  ],
  teacher: [
    { route: '/teacher/dashboard', mustContain: ['教师工作台', '我的课程'] },
    { route: '/teacher/courses', mustContain: ['课程列表', '进入课程管理'] },
    { route: '/teacher/questions', mustContain: ['题目管理', '新增题目'] },
    { route: '/teacher/papers', mustContain: ['试卷管理', '新建试卷'] },
    { route: '/teacher/papers/builder', mustContain: ['组卷中心', 'AI出题'] },
    { route: '/teacher/exam-schedule', mustContain: ['考试安排'] },
    { route: '/teacher/monitor', mustContain: ['监控'] },
    { route: '/teacher/grading', mustContain: ['批改'] },
    { route: '/teacher/appeals', mustContain: ['复核'] },
    { route: '/teacher/analytics', mustContain: ['成绩'] },
  ],
  student: [
    { route: '/home', mustContain: ['学习中心', '我加入的课程'] },
    { route: '/courses', mustContain: ['我的课程', '进入课程'] },
    { route: '/wrong-questions', mustContain: ['错题本', 'AI错题辅导', '添加到对话中问答'] },
    { route: '/report', mustContain: ['成绩报告', '逐题回顾'] },
  ],
}

const badTextPattern = /(\?\?\?\?|No Data|数学学院|计算机学院|外语学院|物理学院|化学学院|经济学院|文学院|法学院|示例数据|模拟数据|假数据|Mock|mock|Ã|Â|�)/

fs.mkdirSync(outDir, { recursive: true })

async function login(page, account) {
  await page.goto(`${baseURL}/login`, { waitUntil: 'networkidle' })
  await page.locator('input[autocomplete="username"]').fill(account.username)
  await page.locator('input[autocomplete="current-password"]').fill(account.password)
  await page.getByRole('button', { name: /进入平台|正在登录/ }).click()
  await page.waitForLoadState('networkidle').catch(() => {})
  await page.waitForTimeout(1000)
  if (page.url().includes('/login')) throw new Error(`Login failed for ${account.username}`)
}

function safeName(role, route) {
  return `${role}-${route.replace(/[\\/?:=&]+/g, '_').replace(/^_/, '') || 'root'}`
}

async function auditRole(browser, role) {
  const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } })
  const page = await context.newPage()
  const apiErrors = []
  const consoleEvents = []

  page.on('console', msg => {
    const text = msg.text()
    if (msg.type() !== 'debug' && !text.includes('Sass') && !text.includes('ResizeObserver')) {
      consoleEvents.push({ type: msg.type(), text })
    }
  })
  page.on('response', res => {
    if (res.url().includes('/api/') && res.status() >= 400) {
      apiErrors.push({ status: res.status(), url: res.url() })
    }
  })

  await login(page, accounts[role])

  const checks = []
  for (const target of flows[role]) {
    await page.goto(`${baseURL}${target.route}`, { waitUntil: 'networkidle' }).catch(() => {})
    await page.waitForTimeout(1400)
    const text = await page.locator('body').innerText().catch(() => '')
    const screenshot = path.join(outDir, `${safeName(role, target.route)}.png`)
    await page.screenshot({ path: screenshot, fullPage: true }).catch(() => {})

    const missing = target.mustContain.filter(keyword => !text.includes(keyword))
    checks.push({
      route: target.route,
      finalUrl: page.url(),
      screenshot,
      ok: text.trim().length >= 30 && missing.length === 0 && !badTextPattern.test(text),
      blank: text.trim().length < 30,
      missing,
      badText: badTextPattern.test(text),
      textHead: text.slice(0, 600),
    })
  }

  await context.close()
  return { role, username: accounts[role].username, checks, apiErrors, consoleEvents }
}

async function main() {
  const browser = await chromium.launch({ headless: true, executablePath: chromePath })
  const roleReports = []
  for (const role of Object.keys(flows)) {
    roleReports.push(await auditRole(browser, role))
  }
  await browser.close()

  const report = {
    baseURL,
    outDir,
    generatedAt: new Date().toISOString(),
    accounts,
    roleReports,
  }
  fs.writeFileSync(path.join(outDir, 'report.json'), JSON.stringify(report, null, 2), 'utf8')
  console.log(JSON.stringify(report, null, 2))

  const failed = roleReports.some(item =>
    item.apiErrors.length ||
    item.consoleEvents.length ||
    item.checks.some(check => !check.ok)
  )
  if (failed) process.exit(1)
}

main().catch(error => {
  console.error(error)
  process.exit(1)
})
