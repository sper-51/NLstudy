const fs = require('fs')
const path = require('path')
const { chromium } = require('./pw_tmp/node_modules/playwright-core')

const BASE = 'http://127.0.0.1:3000'
const OUT = 'D:/123/Java_items/NLstudy/screenshots/student_full_smoke_2026-06-08'
const REPORT = 'D:/123/Java_items/NLstudy/data/student_full_smoke_2026-06-08.md'
fs.mkdirSync(OUT, { recursive: true })

const routes = [
  ['学习中心', '/home'],
  ['我的课程', '/courses'],
  ['课程详情15', '/courses/15'],
  ['成绩报告', '/report'],
  ['成绩报告99', '/report/99'],
  ['错题本', '/wrong-questions'],
]

function simplify(text, max = 650) {
  return (text || '').replace(/\s+/g, ' ').trim().slice(0, max)
}

async function safeClick(locator, label, report) {
  try {
    if (await locator.count()) {
      await locator.first().click()
      await locator.page().waitForTimeout(700)
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
    '# 学生端全功能冒烟测试（2026-06-08）',
    '',
    `- Base: ${BASE}`,
    '- 账号: `stu_001 / 123456`',
    '',
  ]

  await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
  await page.evaluate(() => localStorage.clear()).catch(() => {})
  await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
  const inputs = page.locator('input')
  await inputs.nth(0).fill('stu_001')
  await inputs.nth(1).fill('123456')
  await page.locator('button[type="submit"], .submit-btn').first().click()
  await page.waitForLoadState('networkidle')
  await page.waitForTimeout(1200)
  report.push('## 登录')
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

      if (route === '/courses') {
        await safeClick(page.getByRole('button', { name: /加入课程|查看详情|进入课程/ }), '课程页主要按钮', report)
      }
      if (route.startsWith('/report')) {
        await safeClick(page.locator('.el-select').first(), '成绩报告筛选下拉', report)
      }
      if (route === '/wrong-questions') {
        await safeClick(page.getByRole('button', { name: /查看解析|重新练习|详情/ }), '错题本操作按钮', report)
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
