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
const outDir = process.env.NLSTUDY_TEST_OUT || path.join(process.cwd(), 'admin-crud-output')
const dbConfig = {
  host: process.env.NLSTUDY_DB_HOST || '127.0.0.1',
  port: Number(process.env.NLSTUDY_DB_PORT || 3307),
  user: process.env.NLSTUDY_DB_USER || 'root',
  password: process.env.NLSTUDY_DB_PASSWORD || '123456',
  database: process.env.NLSTUDY_DB_NAME || 'exam_platform',
  multipleStatements: true,
}
const suffix = Date.now().toString().slice(-6)
const names = {
  teacherUsername: `codex_teacher_${suffix}`,
  className: `Codex联调班${suffix}`,
  semesterName: `Codex测试学期${suffix}`,
}

fs.mkdirSync(outDir, { recursive: true })

async function login(page) {
  for (const [username, password] of [['admin', '123456'], ['admin', 'admin123']]) {
    await page.goto(`${baseURL}/login`, { waitUntil: 'networkidle' })
    await page.locator('input[autocomplete="username"]').fill(username)
    await page.locator('input[autocomplete="current-password"]').fill(password)
    await page.getByRole('button', { name: /进入平台|正在登录/ }).click()
    await page.waitForLoadState('networkidle').catch(() => {})
    await page.waitForTimeout(1000)
    if (page.url().includes('/admin')) return username
  }
  throw new Error('Admin login failed')
}

async function api(page, method, requestPath, body) {
  return page.evaluate(async ({ method, requestPath, body }) => {
    const token = localStorage.getItem('token')
    const response = await fetch(`/api/v1${requestPath}`, {
      method,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
        'X-User-Id': localStorage.getItem('userId') || '1',
      },
      body: body ? JSON.stringify(body) : undefined,
    })
    const json = await response.json().catch(() => null)
    return { status: response.status, json }
  }, { method, requestPath, body })
}

async function assertPageShows(page, route, searchPlaceholder, keyword) {
  await page.goto(`${baseURL}${route}`, { waitUntil: 'networkidle' })
  const search = page.getByPlaceholder(searchPlaceholder).first()
  await search.fill(keyword)
  const button = page.getByRole('button', { name: /搜索/ }).first()
  if (await button.isVisible().catch(() => false)) await button.click()
  await page.waitForTimeout(900)
  const shown = await page.getByText(keyword, { exact: false }).first().isVisible().catch(() => false)
  await page.screenshot({ path: path.join(outDir, `${keyword}.png`), fullPage: true }).catch(() => {})
  return shown
}

async function cleanupCreatedRows() {
  const mysql = requireFromRunner('mysql2/promise')
  const conn = await mysql.createConnection(dbConfig)
  await conn.query(
    `
      DELETE FROM sys_user WHERE username = ?;
      DELETE FROM sys_class WHERE name = ?;
      DELETE FROM semester WHERE name = ?;
    `,
    [names.teacherUsername, names.className, names.semesterName],
  )
  const [rows] = await conn.query(
    `
      SELECT 'sys_user' AS tbl, COUNT(*) AS cnt FROM sys_user WHERE username = ?
      UNION ALL SELECT 'sys_class', COUNT(*) FROM sys_class WHERE name = ?
      UNION ALL SELECT 'semester', COUNT(*) FROM semester WHERE name = ?
    `,
    [names.teacherUsername, names.className, names.semesterName],
  )
  await conn.end()
  return rows
}

async function main() {
  const browser = await chromium.launch({ headless: true, executablePath: chromePath })
  const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } })
  const page = await context.newPage()
  const apiErrors = []

  page.on('response', res => {
    if (res.url().includes('/api/') && res.status() >= 400) apiErrors.push({ status: res.status(), url: res.url() })
  })

  const usedAccount = await login(page)
  const results = []

  let teacherId
  let classId
  let semesterId

  try {
    const created = await api(page, 'POST', '/admin/users', {
      username: names.teacherUsername,
      password: '123456',
      realName: `Codex教师${suffix}`,
      role: 'teacher',
      status: 1,
      email: `${names.teacherUsername}@example.com`,
      phone: '13800138000',
    })
    const list = await api(page, 'GET', `/admin/users?page=1&pageSize=10&role=teacher&keyword=${encodeURIComponent(names.teacherUsername)}`)
    teacherId = list.json?.data?.list?.[0]?.id
    const shown = await assertPageShows(page, '/admin/users/teachers', '搜索姓名/账号...', names.teacherUsername)
    results.push({ step: 'teacher api create + ui search', ok: created.status === 200 && !!teacherId && shown })
  } finally {
    if (teacherId) await api(page, 'DELETE', `/admin/users/${teacherId}`)
  }

  try {
    const created = await api(page, 'POST', '/admin/classes', {
      name: names.className,
      grade: '2024级',
      description: 'Codex Playwright 联调临时数据',
    })
    classId = created.json?.data?.id
    const shown = await assertPageShows(page, '/admin/classes', '搜索班级名称...', names.className)
    results.push({ step: 'class api create + ui search', ok: created.status === 200 && !!classId && shown })
  } finally {
    if (classId) await api(page, 'DELETE', `/admin/classes/${classId}`)
  }

  try {
    const created = await api(page, 'POST', '/admin/semesters', {
      name: names.semesterName,
      startDate: '2026-09-01',
      endDate: '2027-01-15',
      status: 1,
    })
    semesterId = created.json?.data?.id
    const shown = await assertPageShows(page, '/admin/semesters', '搜索学期名称', names.semesterName)
    results.push({ step: 'semester api create + ui search', ok: created.status === 200 && !!semesterId && shown })
  } finally {
    if (semesterId) await api(page, 'DELETE', `/admin/semesters/${semesterId}`)
  }

  const cleanupRows = await cleanupCreatedRows()
  const report = { usedAccount, names, results, apiErrors, cleanupRows, outDir }
  fs.writeFileSync(path.join(outDir, 'report.json'), JSON.stringify(report, null, 2), 'utf8')
  console.log(JSON.stringify(report, null, 2))
  await browser.close()

  if (results.some(item => !item.ok) || apiErrors.length > 0 || cleanupRows.some(row => Number(row.cnt) !== 0)) process.exit(1)
}

main().catch(error => {
  console.error(error)
  process.exit(1)
})
