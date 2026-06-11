const path = require('path')
const { createRequire } = require('module')
function requireFromRunner(moduleName) {
  const candidates = [process.env.NLSTUDY_PW_RUNNER, path.join(process.env.USERPROFILE || '', '.codex', 'tmp', 'pw-runner')].filter(Boolean)
  for (const runnerDir of candidates) { try { return createRequire(path.join(runnerDir, 'package.json'))(moduleName) } catch {} }
  return require(moduleName)
}
const mysql = requireFromRunner('mysql2/promise')
const cfg = { host:'127.0.0.1', port:3307, user:'root', password:'123456', database:'exam_platform' }
async function main(){
 const c=await mysql.createConnection(cfg)
 await c.beginTransaction()
 try {
  const [ids] = await c.query(`SELECT (SELECT id FROM sys_user WHERE username='wanglaoshi') teacher_id,(SELECT id FROM sys_user WHERE username='stu_001') student_id`)
  const teacherId = ids[0].teacher_id, studentId = ids[0].student_id
  if(!teacherId || !studentId) throw new Error('required accounts missing')

  const [existing] = await c.query(`SELECT id FROM exam WHERE name='codex_accept_Web前端开发-展示测验' LIMIT 1`)
  if(existing.length){ console.log(JSON.stringify({ skipped:true, examId: existing[0].id }, null, 2)); await c.rollback(); await c.end(); return }

  const [paperRes] = await c.query(`INSERT INTO exam_paper (name, course_id, teacher_id, total_score, pass_score, duration, question_count, type, description, status, create_time, update_time, deleted) VALUES ('codex_accept_Web前端开发-展示试卷', 11, ?, 15, 9, 45, 3, 'manual', '验收展示用试卷，可按 codex_accept 标记清理', 1, NOW(), NOW(), 0)`, [teacherId])
  const paperId = paperRes.insertId
  const questions = [40,41,42]
  for(let i=0;i<questions.length;i++){
    await c.query(`INSERT INTO exam_paper_question (exam_paper_id, question_id, score, sort_order, create_time) VALUES (?, ?, 5, ?, NOW())`, [paperId, questions[i], i+1])
  }
  const [examRes] = await c.query(`INSERT INTO exam (name, exam_paper_id, course_id, teacher_id, start_time, end_time, duration, total_score, pass_score, allow_times, is_random_order, is_random_options, exam_type, status, student_count, submit_count, create_time, update_time, deleted) VALUES ('codex_accept_Web前端开发-展示测验', ?, 11, ?, '2026-06-01 09:00:00', '2026-06-01 09:45:00', 45, 15, 9, 1, 0, 0, 'formal', 'ended', 1, 1, NOW(), NOW(), 0)`, [paperId, teacherId])
  const examId = examRes.insertId
  const [examStudentRes] = await c.query(`INSERT INTO exam_student (exam_id, student_id, status, create_time) VALUES (?, ?, 1, NOW())`, [examId, studentId])
  const examStudentId = examStudentRes.insertId
  const [recordRes] = await c.query(`INSERT INTO exam_record (exam_id, student_id, exam_student_id, start_time, submit_time, total_score, objective_score, subjective_score, status, source, ip_address, user_agent, create_time, update_time) VALUES (?, ?, ?, '2026-06-01 09:03:00', '2026-06-01 09:32:00', 12, 12, 0, 'submitted', 'exam', '127.0.0.1', 'codex_acceptance_seed', NOW(), NOW())`, [examId, studentId, examStudentId])
  const recordId = recordRes.insertId
  const answers = [
    { q:40, ans:'A', ok:1, score:5 },
    { q:41, ans:'A', ok:1, score:5 },
    { q:42, ans:'B', ok:0, score:2 },
  ]
  for(const a of answers){
    await c.query(`INSERT INTO answer_record (exam_record_id, question_id, student_answer, is_correct, score, is_marked, first_answer_time, last_answer_time, answer_times, create_time, update_time) VALUES (?, ?, ?, ?, ?, 1, '2026-06-01 09:10:00', '2026-06-01 09:30:00', 1, NOW(), NOW())`, [recordId, a.q, a.ans, a.ok, a.score])
  }
  await c.query("INSERT INTO grade (exam_id, student_id, exam_record_id, total_score, objective_score, subjective_score, `rank`, percentile_rank, status, publish_time, create_time, update_time, deleted) VALUES (?, ?, ?, 12, 12, 0, 1, 100, 'published', '2026-06-01 10:00:00', NOW(), NOW(), 0)", [examId, studentId, recordId])
  await c.query(`INSERT INTO wrong_question (student_id, source_type, exam_id, exam_record_id, question_id, student_answer, correct_answer, wrong_times, last_wrong_time, is_favorited, practice_count, correct_count, create_time, update_time) VALUES (?, 'exam', ?, ?, 42, 'B', 'A', 1, NOW(), 0, 0, 0, NOW(), NOW())`, [studentId, examId, recordId])
  await c.commit()
  console.log(JSON.stringify({ inserted:true, paperId, examId, examStudentId, recordId, studentId, teacherId }, null, 2))
 } catch(e){ await c.rollback(); throw e } finally { await c.end() }
}
main().catch(e=>{console.error(e);process.exit(1)})


