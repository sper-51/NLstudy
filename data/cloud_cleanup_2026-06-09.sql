-- NLstudy 云端演示数据清理补丁（2026-06-09）
-- 适用库：exam_platform
-- 目的：
-- 1. 将线上联调过程中产生的低质量展示名（ggg/fff/ddd/111/222/333/123/242/gggg/发发发/codex_accept_*）改为可展示名称。
-- 2. 逻辑删除教师账号误入学生考试记录/成绩统计的数据，避免教师端分析页把“王老师”展示为学生。
-- 3. 仅做定向修复，不删除正常学生提交记录，不影响 codex_accept_ 验收展示数据。

START TRANSACTION;

CREATE TABLE IF NOT EXISTS backup_cloud_cleanup_20260609_course AS
SELECT *
FROM course
WHERE id IN (14, 15, 19, 20);

CREATE TABLE IF NOT EXISTS backup_cloud_cleanup_20260609_exam_paper AS
SELECT *
FROM exam_paper
WHERE id IN (15, 16, 17, 19, 20, 21, 23);

CREATE TABLE IF NOT EXISTS backup_cloud_cleanup_20260609_exam AS
SELECT *
FROM exam
WHERE id IN (7, 16, 17, 19, 20, 21, 22);

CREATE TABLE IF NOT EXISTS backup_cloud_cleanup_20260609_exam_record AS
SELECT er.*
FROM exam_record er
JOIN sys_user u ON er.student_id = u.id
WHERE u.role <> 'student'
  AND er.deleted = 0;

CREATE TABLE IF NOT EXISTS backup_cloud_cleanup_20260609_grade AS
SELECT g.*
FROM grade g
JOIN sys_user u ON g.student_id = u.id
WHERE u.role <> 'student'
  AND g.deleted = 0;

UPDATE course
SET name = '网络设备配置实训',
    description = '用于联调展示的网络设备配置与交换机基础课程',
    update_time = NOW()
WHERE id = 14
  AND name = 'ddd';

UPDATE course
SET name = '网络工程综合实训',
    description = '用于联调展示的网络工程综合实训课程',
    update_time = NOW()
WHERE id = 15
  AND name = 'ddd';

UPDATE course
SET name = '数据库系统实践',
    description = '用于联调展示的数据库系统实践课程',
    update_time = NOW()
WHERE id = 19
  AND name = 'fff';

UPDATE course
SET name = '软件测试基础',
    description = '用于联调展示的软件测试基础课程',
    update_time = NOW()
WHERE id = 20
  AND name = 'ggg';

UPDATE exam_paper
SET name = '网络工程综合实训-课堂测验一',
    description = '用于联调展示的网络工程课堂测验试卷',
    update_time = NOW()
WHERE id = 17
  AND name = '111';

UPDATE exam_paper
SET name = '网络工程综合实训-阶段测验',
    description = '用于联调展示的网络工程阶段测验试卷',
    update_time = NOW()
WHERE id = 19
  AND name = '222';

UPDATE exam_paper
SET name = '网络工程综合实训-期末练习',
    description = '用于联调展示的网络工程期末练习试卷',
    update_time = NOW()
WHERE id = 20
  AND name = '333';

UPDATE exam_paper
SET name = '数据库系统实践-课堂练习草稿',
    description = '用于联调展示的数据库系统实践课堂练习草稿',
    update_time = NOW()
WHERE id = 21
  AND name = '123';

UPDATE exam_paper
SET name = 'Web前端开发技术-随堂练习',
    description = '用于联调展示的 Web 前端开发随堂练习试卷',
    update_time = NOW()
WHERE id = 15
  AND name = '发发发';

UPDATE exam_paper
SET name = '网络设备配置实训-基础测验',
    description = '用于联调展示的网络设备配置基础测验试卷',
    update_time = NOW()
WHERE id = 16
  AND name = '242';

UPDATE exam_paper
SET name = 'Web前端开发技术-展示试卷',
    description = '用于学生端成绩报告和错题辅导验收的展示试卷',
    update_time = NOW()
WHERE id = 23
  AND name LIKE 'codex_accept_%';

UPDATE exam
SET name = '网络工程综合实训-课堂测验一',
    update_time = NOW()
WHERE id IN (7, 16)
  AND name = '111';

UPDATE exam
SET name = '网络工程综合实训-阶段测验',
    update_time = NOW()
WHERE id = 17
  AND name = '222';

UPDATE exam
SET name = '数据库系统实践-课堂练习',
    update_time = NOW()
WHERE id = 20
  AND name = 'fff';

UPDATE exam
SET name = '软件测试基础-随堂测验',
    update_time = NOW()
WHERE id = 21
  AND name = 'ggg';

UPDATE exam
SET name = 'Web前端开发技术-展示测验',
    update_time = NOW()
WHERE id = 19
  AND name LIKE 'codex_accept_%';

UPDATE exam
SET name = '软件测试基础-阶段测验',
    update_time = NOW()
WHERE id = 22
  AND name = 'gggg';

UPDATE grade g
JOIN sys_user u ON g.student_id = u.id
SET g.deleted = 1,
    g.update_time = NOW()
WHERE u.role <> 'student'
  AND g.deleted = 0;

UPDATE exam_record er
JOIN sys_user u ON er.student_id = u.id
SET er.deleted = 1,
    er.update_time = NOW()
WHERE u.role <> 'student'
  AND er.deleted = 0;

COMMIT;
