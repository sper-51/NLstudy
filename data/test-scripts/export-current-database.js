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
    } catch {}
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
  charset: 'utf8mb4',
}

const outFile = process.env.NLSTUDY_SQL_OUT ||
  path.join(process.cwd(), 'data', 'exam_platform_latest_20260608.sql')

function quoteIdentifier(name) {
  return `\`${String(name).replace(/`/g, '``')}\``
}

function sqlValue(value) {
  if (value === null || value === undefined) return 'NULL'
  if (value instanceof Date) {
    const pad = number => String(number).padStart(2, '0')
    return `'${value.getFullYear()}-${pad(value.getMonth() + 1)}-${pad(value.getDate())} ${pad(value.getHours())}:${pad(value.getMinutes())}:${pad(value.getSeconds())}'`
  }
  if (Buffer.isBuffer(value)) return `X'${value.toString('hex')}'`
  if (typeof value === 'number') return Number.isFinite(value) ? String(value) : 'NULL'
  if (typeof value === 'bigint') return String(value)
  if (typeof value === 'boolean') return value ? '1' : '0'
  return `'${String(value)
    .replace(/\\/g, '\\\\')
    .replace(/\0/g, '\\0')
    .replace(/\n/g, '\\n')
    .replace(/\r/g, '\\r')
    .replace(/\t/g, '\\t')
    .replace(/\x1a/g, '\\Z')
    .replace(/'/g, "''")}'`
}

function stripSchemaComments(createSql) {
  return createSql
    .replace(/\s+COMMENT\s+'(?:''|[^'])*'/g, '')
    .replace(/\s+COMMENT='(?:''|[^'])*'/g, '')
}

async function main() {
  const conn = await mysql.createConnection(dbConfig)
  const [tables] = await conn.query(`
    SELECT TABLE_NAME AS tableName
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = ?
      AND TABLE_TYPE = 'BASE TABLE'
      AND TABLE_NAME NOT LIKE 'backup\\_%'
    ORDER BY TABLE_NAME
  `, [dbConfig.database])

  const lines = [
    '-- NLstudy 智考平台当前数据库脚本',
    `-- Database: ${dbConfig.database}`,
    `-- Generated at: ${new Date().toISOString()}`,
    '-- Source: data/test-scripts/export-current-database.js',
    '',
    'SET NAMES utf8mb4;',
    'SET FOREIGN_KEY_CHECKS = 0;',
    `CREATE DATABASE IF NOT EXISTS ${quoteIdentifier(dbConfig.database)} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`,
    `USE ${quoteIdentifier(dbConfig.database)};`,
    '',
  ]

  for (const { tableName } of tables) {
    const table = quoteIdentifier(tableName)
    const [createRows] = await conn.query(`SHOW CREATE TABLE ${table}`)
    const createSql = stripSchemaComments(createRows[0]['Create Table'])
    const [rows] = await conn.query(`SELECT * FROM ${table}`)
    const columns = rows.length ? Object.keys(rows[0]) : (await conn.query(`SHOW COLUMNS FROM ${table}`))[0].map(item => item.Field)

    lines.push(`DROP TABLE IF EXISTS ${table};`)
    lines.push(`${createSql};`)

    if (rows.length) {
      const columnSql = columns.map(quoteIdentifier).join(', ')
      const batchSize = 100
      for (let index = 0; index < rows.length; index += batchSize) {
        const batch = rows.slice(index, index + batchSize)
        const values = batch
          .map(row => `(${columns.map(column => sqlValue(row[column])).join(', ')})`)
          .join(',\n')
        lines.push(`INSERT INTO ${table} (${columnSql}) VALUES\n${values};`)
      }
    }
    lines.push('')
  }

  lines.push('SET FOREIGN_KEY_CHECKS = 1;')
  lines.push('')

  fs.mkdirSync(path.dirname(outFile), { recursive: true })
  fs.writeFileSync(outFile, lines.join('\n'), 'utf8')
  await conn.end()

  console.log(JSON.stringify({
    database: `${dbConfig.host}:${dbConfig.port}/${dbConfig.database}`,
    tables: tables.length,
    outFile,
  }, null, 2))
}

main().catch(error => {
  console.error(error)
  process.exit(1)
})
