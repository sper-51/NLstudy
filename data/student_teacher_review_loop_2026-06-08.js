const fs = require('fs')
const path = require('path')
const { chromium } = require('./pw_tmp/node_modules/playwright-core')

const BASE = 'http://127.0.0.1:3000'
const API = 'http://127.0.0.1:8080/api/v1'
const OUT = 'D:/123/Java_items/NLstudy/screenshots/student_teacher_review_loop_2026-06-08'
const REPORT = 'D:/123/Java_items/NLstudy/data/student_teacher_review_loop_2026-06-08.md'
fs.mkdirSync(OUT, { recursive: true })

async function api(method, apiPath, userId, body) {
  const res = await fetch(`${API}${apiPath}`, { method, headers: { 'Content-Type': 'application/json', 'X-User-Id': String(userId) }, body: body ? JSON.stringify(body) : undefined })
  return await res.json()
}
function lineText(text, max = 700) { return String(text || '').replace(/\s+/g, ' ').slice(0, max) }

;(async () => {
  const browser = await chromium.launch({ headless: true, executablePath: 'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe' })
  const page = await browser.newPage({ viewport: { width: 1440, height: 1050 } })
  const apiErrors = []
  const jsErrors = []
  const logs = []
  page.on('console', msg => { const item = `${msg.type()}: ${msg.text()}`; logs.push(item); if (msg.type() === 'error') jsErrors.push(item) })
  page.on('pageerror', err => jsErrors.push(err.message))
  page.on('response', response => { const url = response.url(); if (url.includes('/api/')) { const item = `${response.status()} ${url}`; logs.push(`api:${item}`); if (response.status() >= 400) apiErrors.push(item) } })

  const report = ['# 学生考试-成绩-复核-教师处理闭环测试（2026-06-08）', '', `- Base: ${BASE}`, '- 学生: `stu_001 / 123456` (`userId=400`)', '- 教师: `wanglaoshi / 123456` (`userId=103`)', '']
  try {
    const gradesPayload = await api('GET', '/student/grades?pageSize=100', 400)
    const grades = gradesPayload.data || []
    let target = null
    for (const grade of grades) {
      const detail = await api('GET', `/student/grades/${grade.gradeId}`, 400)
      if (!['pending', 'approved'].includes(detail.data?.reviewStatus)) { target = grade; break }
    }
    if (!target) throw new Error('当前学生所有成绩均已存在 pending/approved 整卷复核，无法重复申请')
    const gradeId = target.gradeId
    const examId = target.examId
    const beforeScore = Number(target.totalScore || 0)
    report.push('## 测试样本')
    report.push(`- gradeId=${gradeId}, examId=${examId}, exam=${target.examName}, beforeScore=${beforeScore}`)

    await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
    await page.evaluate(() => localStorage.clear())
    await page.goto(`${BASE}/login`, { waitUntil: 'networkidle' })
    const inputs = page.locator('input')
    await inputs.nth(0).fill('stu_001')
    await inputs.nth(1).fill('123456')
    await page.locator('button[type="submit"], .submit-btn').first().click()
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(1000)
    report.push('## 学生登录与首页')
    report.push(`- URL: ${page.url()}`)
    report.push(`- token存在: ${await page.evaluate(() => !!localStorage.getItem('token')) ? '是' : '否'}`)

    await page.goto(`${BASE}/report/${gradeId}`, { waitUntil: 'networkidle' })
    await page.waitForTimeout(1400)
    await page.screenshot({ path: path.join(OUT, '01-student-report.png'), fullPage: true })
    const bodyBefore = await page.locator('body').innerText()
    report.push('## 学生查看成绩')
    report.push(`- URL: ${page.url()}`)
    report.push(`- token仍存在: ${await page.evaluate(() => !!localStorage.getItem('token')) ? '是' : '否'}`)
    report.push(`- 页面有申请复核入口: ${bodyBefore.includes('申请整卷复核') ? '是' : '否'}`)
    report.push(`- 摘要: ${lineText(bodyBefore)}`)

    const createPayload = await api('POST', `/student/grades/${gradeId}/review`, 400, { reason: `闭环测试复核申请 ${new Date().toISOString()}` })
    const reviewId = createPayload.data?.reviewId
    if (!reviewId) throw new Error(`创建复核失败：${createPayload.message || JSON.stringify(createPayload)}`)
    report.push('## 学生申请复核')
    report.push(`- reviewId=${reviewId}`)

    const reviewsPayload = await api('GET', `/teacher/grading/reviews?examId=${examId}&status=pending&pageSize=100`, 103)
    const reviewList = reviewsPayload.data?.list || reviewsPayload.data?.records || reviewsPayload.data || []
    const review = reviewList.find(r => Number(r.id) === Number(reviewId))
    report.push(`- 教师端可见: ${review ? '是' : '否'}`)
    if (!review) throw new Error('教师端未查询到复核申请')

    const newScore = beforeScore + 1
    await api('POST', `/teacher/grading/reviews/${reviewId}/handle`, 103, { action: 'approve', newScore, teacherComment: '自动化闭环测试：同意复核并调整总分。' })
    report.push('## 教师处理复核')
    report.push(`- 操作: approve, newScore=${newScore}`)

    const detailAfter = await api('GET', `/student/grades/${gradeId}`, 400)
    report.push('## 成绩回写')
    report.push(`- totalScore=${detailAfter.data?.totalScore}`)
    report.push(`- reviewStatus=${detailAfter.data?.reviewStatus}`)

    await page.goto(`${BASE}/report/${gradeId}`, { waitUntil: 'networkidle' })
    await page.waitForTimeout(1400)
    await page.screenshot({ path: path.join(OUT, '02-student-report-after-approve.png'), fullPage: true })
    const bodyAfter = await page.locator('body').innerText()
    report.push(`- 页面含已通过: ${bodyAfter.includes('已通过') ? '是' : '否'}`)
    report.push(`- 页面含新分数: ${bodyAfter.includes(String(newScore)) ? '是' : '否'}`)
    report.push(`- token仍存在: ${await page.evaluate(() => !!localStorage.getItem('token')) ? '是' : '否'}`)
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
    logs.slice(-120).forEach(item => report.push(`- ${item}`))
    fs.writeFileSync(REPORT, report.join('\n'), 'utf8')
    await browser.close()
  }
})()
