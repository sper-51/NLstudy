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

const mysql = requireFromRunner('mysql2/promise')

const dbConfig = {
  host: process.env.NLSTUDY_DB_HOST || '127.0.0.1',
  port: Number(process.env.NLSTUDY_DB_PORT || 3307),
  user: process.env.NLSTUDY_DB_USER || 'root',
  password: process.env.NLSTUDY_DB_PASSWORD || '123456',
  database: process.env.NLSTUDY_DB_NAME || 'exam_platform',
}

async function count(conn, sql, params = []) {
  const [rows] = await conn.query(sql, params)
  return Number(rows[0]?.cnt || 0)
}

async function sample(conn, sql, params = []) {
  const [rows] = await conn.query(sql, params)
  return rows
}

const lowQualityNamePattern = '^(g+|f+|d+|[0-9]+|发发发)$'

async function main() {
  const conn = await mysql.createConnection(dbConfig)

  const checks = [
    {
      key: 'mojibake_courses',
      severity: 'high',
      description: '课程名称或简介包含明显乱码/问号占位',
      count: await count(conn, `
        SELECT COUNT(*) cnt FROM course
        WHERE name REGEXP '^\\\\?+$'
           OR description REGEXP '^\\\\?+$'
           OR name REGEXP '[ÃÂ�]'
           OR description REGEXP '[ÃÂ�]'
      `),
      sample: await sample(conn, `
        SELECT id, name, code, description, teacher_id
        FROM course
        WHERE name REGEXP '^\\\\?+$'
           OR description REGEXP '^\\\\?+$'
           OR name REGEXP '[ÃÂ�]'
           OR description REGEXP '[ÃÂ�]'
        ORDER BY id
        LIMIT 20
      `),
    },
    {
      key: 'over_score_answers',
      severity: 'high',
      description: '答题记录得分超过题目满分',
      count: await count(conn, `
        SELECT COUNT(*) cnt
        FROM answer_record ar
        JOIN question q ON ar.question_id = q.id
        WHERE ar.score > q.score
      `),
      sample: await sample(conn, `
        SELECT ar.id, ar.exam_record_id, ar.question_id, ar.score, q.score AS question_score
        FROM answer_record ar
        JOIN question q ON ar.question_id = q.id
        WHERE ar.score > q.score
        ORDER BY ar.id
        LIMIT 20
      `),
    },
    {
      key: 'mojibake_answers',
      severity: 'medium',
      description: '学生答案包含编码污染字符',
      count: await count(conn, `
        SELECT COUNT(*) cnt
        FROM answer_record
        WHERE student_answer REGEXP '[ÃÂ�äåæçèéï¼ã€]'
      `),
      sample: await sample(conn, `
        SELECT id, exam_record_id, question_id, student_answer
        FROM answer_record
        WHERE student_answer REGEXP '[ÃÂ�äåæçèéï¼ã€]'
        ORDER BY id
        LIMIT 20
      `),
    },
    {
      key: 'codex_residual_rows',
      severity: 'medium',
      description: '联调临时数据残留',
      count: await count(conn, `
        SELECT SUM(cnt) cnt FROM (
          SELECT COUNT(*) cnt FROM sys_user WHERE username LIKE 'codex_%'
          UNION ALL SELECT COUNT(*) FROM sys_class WHERE name LIKE 'Codex%'
          UNION ALL SELECT COUNT(*) FROM semester WHERE name LIKE 'Codex%'
        ) t
      `),
      sample: await sample(conn, `
        SELECT 'sys_user' AS tbl, id, username AS marker FROM sys_user WHERE username LIKE 'codex_%'
        UNION ALL SELECT 'sys_class', id, name FROM sys_class WHERE name LIKE 'Codex%'
        UNION ALL SELECT 'semester', id, name FROM semester WHERE name LIKE 'Codex%'
        LIMIT 20
      `),
    },
    {
      key: 'low_quality_display_names',
      severity: 'medium',
      description: '课程、试卷、考试存在明显临时/无语义展示名',
      count: await count(conn, `
        SELECT SUM(cnt) cnt FROM (
          SELECT COUNT(*) cnt FROM course
          WHERE deleted = 0
            AND (name REGEXP ? OR description REGEXP ? OR name LIKE 'codex_accept_%')
          UNION ALL SELECT COUNT(*) FROM exam_paper
          WHERE deleted = 0
            AND (name REGEXP ? OR name LIKE 'codex_accept_%')
          UNION ALL SELECT COUNT(*) FROM exam
          WHERE deleted = 0
            AND (name REGEXP ? OR name LIKE 'codex_accept_%')
        ) t
      `, [lowQualityNamePattern, lowQualityNamePattern, lowQualityNamePattern, lowQualityNamePattern]),
      sample: await sample(conn, `
        SELECT 'course' AS tbl, id, name AS marker FROM course
        WHERE deleted = 0
          AND (name REGEXP ? OR description REGEXP ? OR name LIKE 'codex_accept_%')
        UNION ALL SELECT 'exam_paper', id, name FROM exam_paper
        WHERE deleted = 0
          AND (name REGEXP ? OR name LIKE 'codex_accept_%')
        UNION ALL SELECT 'exam', id, name FROM exam
        WHERE deleted = 0
          AND (name REGEXP ? OR name LIKE 'codex_accept_%')
        LIMIT 20
      `, [lowQualityNamePattern, lowQualityNamePattern, lowQualityNamePattern, lowQualityNamePattern]),
    },
    {
      key: 'non_student_exam_records',
      severity: 'high',
      description: '非学生账号出现在考试记录或成绩表中',
      count: await count(conn, `
        SELECT SUM(cnt) cnt FROM (
          SELECT COUNT(*) cnt
          FROM exam_record er
          JOIN sys_user u ON er.student_id = u.id
          WHERE er.deleted = 0
            AND u.role <> 'student'
          UNION ALL SELECT COUNT(*)
          FROM grade g
          JOIN sys_user u ON g.student_id = u.id
          WHERE g.deleted = 0
            AND u.role <> 'student'
        ) t
      `),
      sample: await sample(conn, `
        SELECT 'exam_record' AS tbl, er.id, CONCAT(u.username, '/', u.role, '/', e.name) AS marker
        FROM exam_record er
        JOIN sys_user u ON er.student_id = u.id
        LEFT JOIN exam e ON er.exam_id = e.id
        WHERE er.deleted = 0
          AND u.role <> 'student'
        UNION ALL SELECT 'grade', g.id, CONCAT(u.username, '/', u.role, '/', e.name)
        FROM grade g
        JOIN sys_user u ON g.student_id = u.id
        LEFT JOIN exam e ON g.exam_id = e.id
        WHERE g.deleted = 0
          AND u.role <> 'student'
        LIMIT 20
      `),
    },
  ]

  await conn.end()

  const report = {
    database: `${dbConfig.host}:${dbConfig.port}/${dbConfig.database}`,
    generatedAt: new Date().toISOString(),
    checks,
    summary: {
      high: checks.filter(item => item.severity === 'high' && item.count > 0).length,
      medium: checks.filter(item => item.severity === 'medium' && item.count > 0).length,
      totalIssues: checks.reduce((sum, item) => sum + item.count, 0),
    },
  }

  console.log(JSON.stringify(report, null, 2))
  if (process.env.NLSTUDY_AUDIT_STRICT === '1' && report.summary.high > 0) {
    process.exit(1)
  }
}

main().catch(error => {
  console.error(error)
  process.exit(1)
})
