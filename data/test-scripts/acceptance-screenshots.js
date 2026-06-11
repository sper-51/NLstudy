const fs = require('fs')
const path = require('path')
const { createRequire } = require('module')

function requireFromRunner(moduleName) {
  const candidates = [
    process.env.NLSTUDY_PW_RUNNER,
    path.join(process.env.USERPROFILE || '', '.codex', 'tmp', 'pw-runner'),
  ].filter(Boolean)
  for (const runnerDir of candidates) {
    try { return createRequire(path.join(runnerDir, 'package.json'))(moduleName) } catch {}
  }
  return require(moduleName)
}

const { chromium } = requireFromRunner('playwright')
const baseURL = process.env.NLSTUDY_BASE_URL || 'http://localhost:3000'
const chromePath = process.env.PLAYWRIGHT_CHROME_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe'
const outDir = process.env.NLSTUDY_ACCEPTANCE_SHOTS || path.join(process.cwd(), 'data', 'runtime-logs', 'acceptance-screenshots-20260608')

const accounts = {
  admin: { username: process.env.NLSTUDY_ADMIN_USER || 'admin', password: process.env.NLSTUDY_ADMIN_PASSWORD || '123456' },
  teacher: { username: process.env.NLSTUDY_TEACHER_USER || 'wanglaoshi', password: process.env.NLSTUDY_TEACHER_PASSWORD || '123456' },
  student: { username: process.env.NLSTUDY_STUDENT_USER || 'stu_001', password: process.env.NLSTUDY_STUDENT_PASSWORD || '123456' },
}

const pages = [
  { role: 'admin', route: '/admin', name: 'admin-dashboard' },
  { role: 'admin', route: '/admin/data/backup', name: 'admin-data-management' },
  { role: 'admin', route: '/admin/monitor/audit', name: 'admin-audit-logs' },
  { role: 'teacher', route: '/teacher/courses', name: 'teacher-courses' },
  { role: 'student', route: '/report', name: 'student-report' },
]

async function login(page, account) {
  await page.goto(`${baseURL}/login`, { waitUntil: 'networkidle' })
  await page.locator('input[autocomplete="username"]').fill(account.username)
  await page.locator('input[autocomplete="current-password"]').fill(account.password)
  await page.getByRole('button', { name: /进入平台|正在登录/ }).click()
  await page.waitForLoadState('networkidle').catch(() => {})
  await page.waitForTimeout(1200)
  if (page.url().includes('/login')) throw new Error(`Login failed for ${account.username}`)
}

async function main() {
  fs.mkdirSync(outDir, { recursive: true })
  const browser = await chromium.launch({ headless: true, executablePath: chromePath })
  const results = []

  for (const [role, account] of Object.entries(accounts)) {
    const rolePages = pages.filter(item => item.role === role)
    if (!rolePages.length) continue
    const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } })
    const page = await context.newPage()
    const consoleEvents = []
    const apiErrors = []
    page.on('console', msg => { if (msg.type() !== 'debug') consoleEvents.push({ type: msg.type(), text: msg.text() }) })
    page.on('response', res => { if (res.url().includes('/api/') && res.status() >= 400) apiErrors.push({ status: res.status(), url: res.url() }) })
    await login(page, account)

    for (const target of rolePages) {
      await page.goto(`${baseURL}${target.route}`, { waitUntil: 'networkidle' }).catch(() => {})
      await page.waitForTimeout(1500)
      const text = await page.locator('body').innerText().catch(() => '')
      const screenshot = path.join(outDir, `${target.name}.png`)
      await page.screenshot({ path: screenshot, fullPage: true })
      results.push({
        role,
        account: account.username,
        route: target.route,
        screenshot,
        blank: text.trim().length < 30,
        hasQuestionMarks: text.includes('????'),
        hasNoData: text.includes('No Data'),
        hasMockCollege: /数学学院|计算机学院|外语学院|物理学院|化学学院|经济学院|文学院|法学院/.test(text),
        textHead: text.slice(0, 500),
      })
    }
    await context.close()
    results.push({ role, account: account.username, consoleEvents, apiErrors })
  }

  await browser.close()
  const report = { baseURL, outDir, generatedAt: new Date().toISOString(), accounts, results }
  fs.writeFileSync(path.join(outDir, 'report.json'), JSON.stringify(report, null, 2), 'utf8')
  console.log(JSON.stringify(report, null, 2))

  const failed = results.some(item => item.blank || item.hasQuestionMarks || item.hasNoData || item.hasMockCollege || (item.consoleEvents && item.consoleEvents.length) || (item.apiErrors && item.apiErrors.length))
  if (failed) process.exit(1)
}

main().catch(error => { console.error(error); process.exit(1) })
