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
    } catch (error) {
      // Try the next configured runner directory.
    }
  }

  return require(moduleName)
}

const { chromium } = requireFromRunner('playwright')

const baseURL = process.env.NLSTUDY_BASE_URL || 'http://localhost:3000'
const chromePath = process.env.PLAYWRIGHT_CHROME_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe'
const outDir = process.env.NLSTUDY_TEST_OUT || path.join(process.cwd(), 'admin-smoke-output')

const routes = [
  { path: '/admin', name: 'dashboard', must: ['全局控制台'] },
  { path: '/admin/users/teachers', name: 'teachers', must: ['教师账号管理'] },
  { path: '/admin/users/students', name: 'students', must: ['学生账号管理'] },
  { path: '/admin/classes', name: 'classes', must: ['班级管理中心'] },
  { path: '/admin/semesters', name: 'semesters', must: ['学期与规则配置'] },
  { path: '/admin/data/backup', name: 'backup', must: ['数据管理'] },
  { path: '/admin/data/logs', name: 'system-logs', must: ['日志审计监控'] },
  { path: '/admin/monitor/online', name: 'online', must: ['在线用户监控'] },
  { path: '/admin/monitor/status', name: 'status', must: ['系统状态监控'] },
  { path: '/admin/monitor/audit', name: 'audit', must: ['操作日志审计'] },
]

fs.mkdirSync(outDir, { recursive: true })

async function tryLogin(page, username, password) {
  await page.goto(`${baseURL}/login`, { waitUntil: 'networkidle' })
  await page.locator('input[autocomplete="username"]').fill(username)
  await page.locator('input[autocomplete="current-password"]').fill(password)
  await page.getByRole('button', { name: /进入平台|正在登录/ }).click()
  await page.waitForLoadState('networkidle').catch(() => {})
  await page.waitForTimeout(1000)
  return page.url().includes('/admin')
}

async function main() {
  const browser = await chromium.launch({ headless: true, executablePath: chromePath })
  const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } })
  const page = await context.newPage()
  const consoleEvents = []
  const apiErrors = []

  page.on('console', msg => {
    if (['error', 'warning'].includes(msg.type())) {
      consoleEvents.push({ type: msg.type(), text: msg.text().slice(0, 500) })
    }
  })
  page.on('pageerror', err => consoleEvents.push({ type: 'pageerror', text: err.message }))
  page.on('response', res => {
    if (res.url().includes('/api/') && res.status() >= 400) apiErrors.push({ status: res.status(), url: res.url() })
  })

  let usedAccount = null
  for (const [username, password] of [['admin', '123456'], ['admin', 'admin123']]) {
    if (await tryLogin(page, username, password)) {
      usedAccount = username
      break
    }
  }
  if (!usedAccount) throw new Error('Admin login failed')

  const results = []
  for (const route of routes) {
    const beforeConsole = consoleEvents.length
    const beforeApi = apiErrors.length
    await page.goto(`${baseURL}${route.path}`, { waitUntil: 'networkidle' })
    await page.waitForTimeout(800)
    const bodyText = await page.locator('body').innerText().catch(() => '')
    const matched = route.must.every(text => bodyText.includes(text))
    const blank = bodyText.trim().length < 50
    const screenshot = path.join(outDir, `${route.name}.png`)
    await page.screenshot({ path: screenshot, fullPage: true })
    results.push({
      route: route.path,
      name: route.name,
      ok: matched && !blank,
      matched,
      blank,
      newConsole: consoleEvents.slice(beforeConsole),
      newApiErrors: apiErrors.slice(beforeApi),
      screenshot,
    })
  }

  const report = { usedAccount, baseURL, outDir, results, consoleEvents, apiErrors }
  fs.writeFileSync(path.join(outDir, 'report.json'), JSON.stringify(report, null, 2), 'utf8')
  console.log(JSON.stringify(report, null, 2))
  await browser.close()

  if (results.some(item => !item.ok) || apiErrors.length > 0) process.exit(1)
}

main().catch(error => {
  console.error(error)
  process.exit(1)
})
