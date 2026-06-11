const fs = require('fs')
const path = require('path')
const { chromium } = require('./pw_tmp/node_modules/playwright-core')

const BASE = 'http://127.0.0.1:3000'
const OUT = 'D:/123/Java_items/NLstudy/screenshots/student_review_loop_2026-06-08'
const REPORT = 'D:/123/Java_items/NLstudy/data/student_review_loop_2026-06-08.md'
fs.mkdirSync(OUT, { recursive: true })

function stamp() { return new Date().toISOString().replace(/[:.]/g, '-') }

;(async () => {
  const browser = await chromium.launch({ headless: true, executablePath: 'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe' })
  const page = await browser.newPage({ viewport: { width: 1440, height: 1050 } })
  const apiErrors = []
  const jsErrors = []
  const consoleLines = []
  page.on('console', msg => { const text = `${msg.type()}: ${msg.text()}`; consoleLines.push(text); if (msg.type() === 'error') jsErrors.push(text) })
  page.on('pageerror', err => jsErrors.push(err.message))
  page.on('response', response => { const url = response.url(); if (url.includes('/api/')) { const line = `${response.status()} ${url}`; consoleLines.push(`api:${line}`); if (response.status() >= 400) apiErrors.push(line) } })

  const report = ['# 学生端成绩复核闭环测试（2026-06-08）', '', `- Base: ${BASE}`, '- 账号: `stu_001 / 123456`', '- 目标: 学生成绩报告 -> 申请复核 -> 状态回显 -> 教师端可处理', '']
  try {
    await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
    await page.evaluate(() => localStorage.clear())
    await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
    const inputs = page.locator('input')
    await inputs.nth(0).fill('stu_001')
    await inputs.nth(1).fill('123456')
    await page.locator('button[type="submit"], .submit-btn').first().click()
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(900)
    report.push('## 登录')
    report.push(`- URL: ${page.url()}`)

    const gradesPayload = await page.evaluate(async () => {
      const res = await fetch('/api/v1/student/grades?pageSize=100', { headers: { 'X-User-Id': localStorage.getItem('userId') || '' } })
      return await res.json()
    })
    const grades = Array.isArray(gradesPayload.data) ? gradesPayload.data : (gradesPayload.data?.list || [])
    const targetGrade = grades.find(item => item.gradeId || item.id)
    if (!targetGrade) throw new Error('当前学生没有可用于复核测试的成绩')
    const gradeId = targetGrade.gradeId || targetGrade.id
    report.push(`- 选中成绩: gradeId=${gradeId}, exam=${targetGrade.examName}, score=${targetGrade.totalScore}`)

    await page.goto(`${BASE}/report/${gradeId}`, { waitUntil: 'networkidle' })
    await page.waitForTimeout(1500)
    await page.screenshot({ path: path.join(OUT, '01-report-before.png'), fullPage: true })
    const beforeBody = await page.locator('body').innerText()
    report.push('## 成绩报告')
    report.push(`- URL: ${page.url()}`)
    report.push(`- 页面摘要: ${beforeBody.replace(/\s+/g, ' ').slice(0, 800)}`)

    const questionReviewButtons = page.getByText('申请本题复核')
    const wholeReviewButton = page.getByText('申请整卷复核')
    let submittedScope = ''
    if (await questionReviewButtons.count()) { await questionReviewButtons.first().click(); submittedScope = '单题复核' }
    else if (await wholeReviewButton.count()) { await wholeReviewButton.first().click(); submittedScope = '整卷复核' }
    else { report.push('- 复核入口: 未发现可提交按钮，可能已有 pending/approved 复核') }

    if (submittedScope) {
      await page.waitForTimeout(300)
      const reason = `自动化联调复核申请 ${stamp()}：核查得分与参考答案匹配情况。`
      await page.locator('.el-dialog textarea').first().fill(reason)
      await page.getByText('提交复核').click()
      await page.waitForLoadState('networkidle').catch(() => {})
      await page.waitForTimeout(1600)
      await page.screenshot({ path: path.join(OUT, '02-report-after-submit.png'), fullPage: true })
      const body = await page.locator('body').innerText()
      report.push('## 提交复核')
      report.push(`- 提交类型: ${submittedScope}`)
      report.push(`- 状态回显包含待处理: ${body.includes('待处理') ? '是' : '否'}`)
      report.push(`- 提交后摘要: ${body.replace(/\s+/g, ' ').slice(0, 900)}`)
    }

    const apiCheck = await page.evaluate(async (id) => {
      const res = await fetch(`/api/v1/student/grades/${id}`, { headers: { 'X-User-Id': localStorage.getItem('userId') || '' } })
      return await res.json()
    }, gradeId)
    const reviews = apiCheck?.data?.reviews || []
    report.push('## 接口核验')
    report.push(`- reviews 数量: ${reviews.length}`)
    report.push(`- pending 数量: ${reviews.filter(item => item.status === 'pending').length}`)
    report.push(`- 最新复核: ${reviews[0] ? JSON.stringify(reviews[0]) : '无'}`)
  } catch (err) {
    report.push('## 异常')
    report.push(`- ${err.message}`)
  } finally {
    report.push('', '## 汇总')
    report.push(`- API 4xx/5xx: ${apiErrors.length}`)
    apiErrors.forEach(item => report.push(`  - ${item}`))
    report.push(`- JS Error: ${jsErrors.length}`)
    jsErrors.forEach(item => report.push(`  - ${item}`))
    report.push('## 日志摘录')
    consoleLines.slice(-100).forEach(item => report.push(`- ${item}`))
    fs.writeFileSync(REPORT, report.join('\n'), 'utf8')
    await browser.close()
  }
})()
