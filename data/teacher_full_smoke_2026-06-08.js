const fs = require('fs')
const path = require('path')
const { chromium } = require('./pw_tmp/node_modules/playwright-core')

const BASE = 'http://127.0.0.1:3000'
const OUT = 'D:/123/Java_items/NLstudy/screenshots/teacher_full_smoke_2026-06-08'
const REPORT = 'D:/123/Java_items/NLstudy/data/teacher_full_smoke_2026-06-08.md'
fs.mkdirSync(OUT, { recursive: true })

const routes = [
  ['工作台', '/teacher/dashboard'],
  ['课程列表', '/teacher/courses'],
  ['课程管理15', '/teacher/course/15'],
  ['学生选课', '/teacher/students'],
  ['题目管理', '/teacher/questions'],
  ['新增题目', '/teacher/questions/new'],
  ['试卷列表', '/teacher/papers'],
  ['组卷中心', '/teacher/papers/builder'],
  ['考试安排', '/teacher/exam-schedule'],
  ['考场监控', '/teacher/monitor'],
  ['实时监控6', '/teacher/exam/6/monitor'],
  ['待阅任务', '/teacher/grading'],
  ['成绩复核', '/teacher/appeals'],
  ['批改历史', '/teacher/grading/history'],
  ['成绩总览', '/teacher/analytics'],
  ['质量看板', '/teacher/analytics/dashboard'],
]

function simplify(text, max = 650) {
  return (text || '').replace(/\s+/g, ' ').trim().slice(0, max)
}

async function safeClick(locator, label, report) {
  try {
    if (await locator.count()) {
      await locator.first().click()
      await locator.page().waitForTimeout(500)
      report.push(`  - 交互 ${label}: OK`)
      return true
    }
    report.push(`  - 交互 ${label}: 未找到`)
  } catch (err) {
    report.push(`  - 交互 ${label}: FAIL ${err.message}`)
  }
  return false
}

;(async () => {
  const browser = await chromium.launch({
    headless: true,
    executablePath: 'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe',
  })
  const page = await browser.newPage({ viewport: { width: 1440, height: 1000 } })
  const logs = []
  const apiErrors = []
  const pageErrors = []

  page.on('console', msg => {
    const text = msg.text()
    logs.push(`${msg.type()}: ${text}`)
    if (msg.type() === 'error') pageErrors.push(text)
  })
  page.on('pageerror', err => pageErrors.push(err.message))
  page.on('response', response => {
    const url = response.url()
    if (url.includes('/api/')) {
      const line = `${response.status()} ${url}`
      logs.push(`api:${line}`)
      if (response.status() >= 400) apiErrors.push(line)
    }
  })

  const report = [
    '# 教师端全功能冒烟测试（2026-06-08）',
    '',
    `- Base: ${BASE}`,
    '- 账号: `wanglaoshi / 123456`',
    '',
  ]

  await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
  await page.evaluate(() => localStorage.clear()).catch(() => {})
  await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
  const inputs = page.locator('input')
  await inputs.nth(0).fill('wanglaoshi')
  await inputs.nth(1).fill('123456')
  await page.locator('button[type="submit"], .submit-btn').first().click()
  await page.waitForLoadState('networkidle')
  await page.waitForTimeout(1200)
  report.push(`## 登录`)
  report.push(`- 登录后 URL: ${page.url()}`)
  report.push('')

  for (const [name, route] of routes) {
    const beforeApi = apiErrors.length
    const beforePage = pageErrors.length
    report.push(`## ${name}`)
    try {
      await page.goto(`${BASE}${route}`, { waitUntil: 'networkidle' })
      await page.waitForTimeout(1200)
      const title = await page.locator('h1').first().innerText().catch(() => '')
      const body = await page.locator('body').innerText().catch(() => '')
      const screenshot = path.join(OUT, `${name.replace(/[\\/:\*\?"<>\|]/g, '_')}.png`)
      await page.screenshot({ path: screenshot, fullPage: true })
      report.push(`- URL: ${page.url()}`)
      report.push(`- H1: ${title.trim()}`)
      report.push(`- 摘要: ${simplify(body)}`)
      report.push(`- 截图: ${screenshot}`)

      if (route === '/teacher/grading') {
        await safeClick(page.getByRole('button', { name: '已批阅' }), '切换已批阅', report)
        await safeClick(page.getByRole('button', { name: '查看记录' }), '查看已批阅记录', report)
        const gradingText = await page.locator('body').innerText().catch(() => '')
        report.push(`  - 已批阅摘要: ${simplify(gradingText, 360)}`)
      }

      if (route === '/teacher/appeals') {
        await safeClick(page.getByRole('button', { name: /处理复核/ }), '打开复核处理弹窗', report)
        const dialogText = await page.locator('.el-dialog').innerText().catch(() => '')
        report.push(`  - 弹窗摘要: ${simplify(dialogText, 360)}`)
        await page.keyboard.press('Escape').catch(() => {})
      }

      if (route === '/teacher/papers') {
        await safeClick(page.getByRole('button', { name: /预览|查看/ }), '试卷预览/查看按钮', report)
        await page.keyboard.press('Escape').catch(() => {})
      }

      if (route === '/teacher/monitor') {
        await safeClick(page.getByRole('button', { name: /进入监控|查看监控|监控/ }), '进入监控按钮', report)
      }

      const newApiErrors = apiErrors.slice(beforeApi)
      const newPageErrors = pageErrors.slice(beforePage)
      report.push(`- API错误: ${newApiErrors.length ? newApiErrors.join('; ') : '无'}`)
      report.push(`- 页面错误: ${newPageErrors.length ? newPageErrors.join('; ') : '无'}`)
    } catch (err) {
      report.push(`- FAIL: ${err.message}`)
    }
    report.push('')
  }

  report.push('## 汇总')
  report.push(`- API 4xx/5xx 数量: ${apiErrors.length}`)
  apiErrors.slice(0, 80).forEach(item => report.push(`  - ${item}`))
  report.push(`- 页面 JS 错误数量: ${pageErrors.length}`)
  pageErrors.slice(0, 80).forEach(item => report.push(`  - ${item}`))
  report.push('## 日志摘录')
  logs.slice(-120).forEach(item => report.push(`- ${item}`))

  fs.writeFileSync(REPORT, report.join('\n'), 'utf8')
  await browser.close()
})()
