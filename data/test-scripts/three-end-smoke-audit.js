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
    } catch {
      // Try the next configured runner directory.
    }
  }

  return require(moduleName)
}

const { chromium } = requireFromRunner('playwright')

const baseURL = process.env.NLSTUDY_BASE_URL || 'http://localhost:3000'
const chromePath = process.env.PLAYWRIGHT_CHROME_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe'
const outDir = process.env.NLSTUDY_TEST_OUT || path.join(process.cwd(), 'three-end-smoke-output')

const suspiciousWords = [
  '数学学院',
  '计算机学院',
  '外语学院',
  '物理学院',
  '化学学院',
  '经济学院',
  '文学院',
  '法学院',
  '示例数据',
  '模拟数据',
  '假数据',
  'Mock',
  'mock',
  '公测',
  '学部',
  '127 人',
  '全24小时×6个学部',
]

const mojibakePattern = /[ÃÂ�]/

const roles = [
  {
    role: 'admin',
    username: process.env.NLSTUDY_ADMIN_USER || 'admin',
    password: process.env.NLSTUDY_ADMIN_PASSWORD || '123456',
    routes: ['/admin', '/admin?tab=teaching', '/admin?tab=quality', '/admin/monitor/audit'],
  },
  {
    role: 'teacher',
    username: process.env.NLSTUDY_TEACHER_USER || 'wanglaoshi',
    password: process.env.NLSTUDY_TEACHER_PASSWORD || '123456',
    routes: ['/teacher/dashboard', '/teacher/courses', '/teacher/questions', '/teacher/papers', '/teacher/exam-schedule', '/teacher/analytics'],
  },
  {
    role: 'student',
    username: process.env.NLSTUDY_STUDENT_USER || 'stu_001',
    password: process.env.NLSTUDY_STUDENT_PASSWORD || '123456',
    routes: ['/home', '/courses', '/wrong-questions', '/report'],
  },
]

fs.mkdirSync(outDir, { recursive: true })

async function login(page, username, password) {
  await page.goto(`${baseURL}/login`, { waitUntil: 'networkidle' })
  await page.locator('input[autocomplete="username"]').fill(username)
  await page.locator('input[autocomplete="current-password"]').fill(password)
  await page.getByRole('button', { name: /进入平台|正在登录/ }).click()
  await page.waitForLoadState('networkidle').catch(() => {})
  await page.waitForTimeout(1000)
  if (page.url().includes('/login')) {
    throw new Error(`Login failed for ${username}`)
  }
}

function routeName(role, route) {
  return `${role}-${route.replace(/[\\/?:=&]+/g, '_').replace(/^_/, '') || 'root'}`
}

async function auditRole(browser, config) {
  const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } })
  const page = await context.newPage()
  const apiErrors = []
  const consoleEvents = []

  page.on('console', msg => {
    if (msg.type() !== 'debug') consoleEvents.push({ type: msg.type(), text: msg.text() })
  })
  page.on('response', res => {
    if (res.url().includes('/api/') && res.status() >= 400) {
      apiErrors.push({ status: res.status(), url: res.url() })
    }
  })

  await login(page, config.username, config.password)

  const results = []
  for (const route of config.routes) {
    await page.goto(`${baseURL}${route}`, { waitUntil: 'networkidle' }).catch(() => {})
    await page.waitForTimeout(1200)
    const text = await page.locator('body').innerText().catch(() => '')
    const iconInfo = await page.locator('i[class*="ri-"]').first().evaluate(node => ({
      fontFamily: getComputedStyle(node).fontFamily,
      width: node.getBoundingClientRect().width,
    })).catch(() => null)
    const screenshot = path.join(outDir, `${routeName(config.role, route)}.png`)
    await page.screenshot({ path: screenshot, fullPage: true }).catch(() => {})
    results.push({
      route,
      finalUrl: page.url(),
      blank: text.trim().length < 30,
      suspicious: suspiciousWords.filter(word => text.includes(word)),
      hasQuestionMarks: text.includes('????'),
      hasMojibake: mojibakePattern.test(text),
      iconOk: !iconInfo || String(iconInfo.fontFamily).includes('remixicon'),
      screenshot,
      textHead: text.slice(0, 800),
    })
  }

  await context.close()
  return { role: config.role, username: config.username, results, apiErrors, consoleEvents }
}

async function main() {
  const browser = await chromium.launch({ headless: true, executablePath: chromePath })
  const roleReports = []
  for (const roleConfig of roles) {
    roleReports.push(await auditRole(browser, roleConfig))
  }
  await browser.close()

  const report = {
    baseURL,
    outDir,
    generatedAt: new Date().toISOString(),
    roleReports,
  }
  fs.writeFileSync(path.join(outDir, 'report.json'), JSON.stringify(report, null, 2), 'utf8')
  console.log(JSON.stringify(report, null, 2))

  const failed = roleReports.some(roleReport =>
    roleReport.apiErrors.length > 0 ||
    roleReport.results.some(item =>
      item.blank ||
      item.suspicious.length > 0 ||
      item.hasQuestionMarks ||
      item.hasMojibake ||
      !item.iconOk
    )
  )
  if (failed) process.exit(1)
}

main().catch(error => {
  console.error(error)
  process.exit(1)
})
