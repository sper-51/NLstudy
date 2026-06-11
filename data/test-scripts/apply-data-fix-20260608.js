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

const mysql = requireFromRunner('mysql2/promise')
const dbConfig = {
  host: process.env.NLSTUDY_DB_HOST || '127.0.0.1',
  port: Number(process.env.NLSTUDY_DB_PORT || 3307),
  user: process.env.NLSTUDY_DB_USER || 'root',
  password: process.env.NLSTUDY_DB_PASSWORD || '123456',
  database: process.env.NLSTUDY_DB_NAME || 'exam_platform',
  multipleStatements: false,
}

async function scalar(conn, sql) {
  const [rows] = await conn.query(sql)
  return Number(rows[0]?.cnt || 0)
}

async function main() {
  const tag = process.env.NLSTUDY_FIX_TAG || '20260608_codex'
  if (!/^[A-Za-z0-9_]+$/.test(tag)) throw new Error('Invalid backup tag')
  const backupCourse = `backup_course_${tag}`
  const backupPaper = `backup_exam_paper_${tag}`
  const backupAnswer = `backup_answer_record_${tag}`
  const conn = await mysql.createConnection(dbConfig)

  const before = {
    courses: await scalar(conn, `SELECT COUNT(*) cnt FROM course WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`),
    papers: await scalar(conn, `SELECT COUNT(*) cnt FROM exam_paper WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`),
    overScores: await scalar(conn, `SELECT COUNT(*) cnt FROM answer_record ar JOIN question q ON ar.question_id = q.id WHERE ar.score > q.score`),
    badAnswers: await scalar(conn, `SELECT COUNT(*) cnt FROM answer_record WHERE student_answer REGEXP '[ÃÂ�äåæçèéï¼ã€]'`),
  }

  await conn.beginTransaction()
  try {
    await conn.query(`CREATE TABLE IF NOT EXISTS \`${backupCourse}\` AS SELECT * FROM course WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`)
    await conn.query(`CREATE TABLE IF NOT EXISTS \`${backupPaper}\` AS SELECT * FROM exam_paper WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`)
    await conn.query(`CREATE TABLE IF NOT EXISTS \`${backupAnswer}\` AS SELECT ar.* FROM answer_record ar LEFT JOIN question q ON ar.question_id = q.id WHERE ar.student_answer REGEXP '[ÃÂ�äåæçèéï¼ã€]' OR (q.id IS NOT NULL AND ar.score > q.score)`)

    await conn.query(`UPDATE course SET name = CONCAT('课程 ', id), description = '课程信息存在编码异常，已临时修复为可读占位。' WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`)
    await conn.query(`UPDATE exam_paper SET name = CONCAT('试卷 ', id), description = CASE WHEN description IS NULL OR description = '' OR description REGEXP '^\\\\?+$' OR description REGEXP '[ÃÂ�]' THEN '试卷信息存在编码异常，已临时修复为可读占位。' ELSE description END WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`)
    await conn.query(`UPDATE answer_record ar JOIN question q ON ar.question_id = q.id SET ar.score = q.score WHERE ar.score > q.score`)
    await conn.query(`UPDATE answer_record SET student_answer = '答案内容存在编码异常，请联系教师复核' WHERE student_answer REGEXP '[ÃÂ�äåæçèéï¼ã€]'`)

    await conn.commit()
  } catch (error) {
    await conn.rollback()
    throw error
  }

  const after = {
    courses: await scalar(conn, `SELECT COUNT(*) cnt FROM course WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`),
    papers: await scalar(conn, `SELECT COUNT(*) cnt FROM exam_paper WHERE name REGEXP '^\\\\?+$' OR description REGEXP '^\\\\?+$' OR name REGEXP '[ÃÂ�]' OR description REGEXP '[ÃÂ�]'`),
    overScores: await scalar(conn, `SELECT COUNT(*) cnt FROM answer_record ar JOIN question q ON ar.question_id = q.id WHERE ar.score > q.score`),
    badAnswers: await scalar(conn, `SELECT COUNT(*) cnt FROM answer_record WHERE student_answer REGEXP '[ÃÂ�äåæçèéï¼ã€]'`),
  }
  const backups = {
    [backupCourse]: await scalar(conn, `SELECT COUNT(*) cnt FROM \`${backupCourse}\``),
    [backupPaper]: await scalar(conn, `SELECT COUNT(*) cnt FROM \`${backupPaper}\``),
    [backupAnswer]: await scalar(conn, `SELECT COUNT(*) cnt FROM \`${backupAnswer}\``),
  }

  await conn.end()
  console.log(JSON.stringify({ database: `${dbConfig.host}:${dbConfig.port}/${dbConfig.database}`, tag, before, after, backups }, null, 2))
}

main().catch(error => { console.error(error); process.exit(1) })
