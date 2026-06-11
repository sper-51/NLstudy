-- ============================================
-- 王老师(wanglaoshi/id=103) 测试数据补充脚本
-- 为王老师绑定课程、学生、考试等完整数据
-- ============================================

USE `exam_platform`;

-- ============================================
-- 一、确认 wanglaoshi 用户存在
-- ============================================
SELECT '=== 检查用户 ===' AS info;
SELECT id, username, real_name, role FROM sys_user WHERE username = 'wanglaoshi';

-- ============================================
-- 二、为王老师创建课程（3门）
-- ============================================
INSERT IGNORE INTO course (id, name, code, teacher_id, semester_id, credits, hours, description, status) VALUES
(201, 'Web前端开发技术', 'WEB101', 103, 1, 3.0, 48, 'HTML5/CSS3/JavaScript/Vue.js 全栈前端开发课程', 1),
(202, '软件工程导论', 'SE201', 103, 1, 2.5, 40, '软件开发生命周期、敏捷开发、UML建模', 1),
(203, 'Python程序设计', 'PY301', 103, 2, 3.0, 56, 'Python基础语法、数据结构、面向对象编程', 1);

SELECT '=== 课程已创建 ===' AS info;
SELECT id, name, code, teacher_id FROM course WHERE teacher_id = 103;

-- ============================================
-- 三、创建选课王老师课程的学生（新增3名学生）
-- ============================================
INSERT IGNORE INTO sys_user (id, username, password, real_name, role, email, phone, status) VALUES
(401, 'stu_wang_01', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '孙小芳', 'student', 'sunfang@example.com', '13800130101', 1),
(402, 'stu_wang_02', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '周小强', 'student', 'zhouqiang@example.com', '13800130102', 1),
(403, 'stu_wang_03', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '吴小敏', 'student', 'wumin@example.com', '13800130103', 1);

-- 学生选课：每个学生选修王老师的2门课
INSERT IGNORE INTO course_selection (student_id, course_id, select_time, status) VALUES
-- 孙小芳 选 Web前端 + 软件工程
(401, 201, '2026-05-15 09:00:00', 1),
(401, 202, '2026-05-16 10:30:00', 1),
-- 周小强 选 Web前端 + Python
(402, 201, '2026-05-14 14:00:00', 1),
(402, 203, '2026-05-17 11:00:00', 1),
-- 吴小敏 选 软件工程 + Python
(403, 202, '2026-05-13 16:00:00', 1),
(403, 203, '2026-05-18 08:30:00', 1);

SELECT '=== 学生已创建并选课 ===' AS info;
SELECT u.id, u.real_name, c.name AS course_name
FROM sys_user u
JOIN course_selection cs ON u.id = cs.student_id
JOIN course c ON cs.course_id = c.id
WHERE u.role = 'student' AND c.teacher_id = 103
ORDER BY u.id, c.id;

-- ============================================
-- 四、为每门课创建题目（每种题型各2道）
-- ============================================

-- Web前端开发技术 题目 (course_id=201)
INSERT IGNORE INTO question (id, course_id, teacher_id, type, difficulty, content, answer, analysis, score, knowledge_points, chapter, status) VALUES
(301, 201, 103, 'single_choice', 'easy', 'HTML中用于定义段落的标签是？', '{"A":"<p>","B":"<div>","C":"<span>","D":"<section>"}', 'A', 'HTML基础标签，<p>表示paragraph段落', 5.00, 'HTML基础', '第一章 HTML基础', 1),
(302, 201, 103, 'single_choice', 'medium', 'CSS中设置元素居中的正确写法是？', '{"A":"margin: auto; text-align: center;","B":"align: center;","C":"center: both;","D":"float: center;"}', 'A', 'flex布局或margin:auto配合text-align实现水平垂直居中', 5.00, 'CSS布局', '第二章 CSS布局', 1),
(303, 201, 103, 'multi_choice', 'medium', '以下哪些是Vue3的新特性？（多选）', '{"A":"Composition API","B":"Teleport组件","C":"v-model改进","D":"jQuery集成"}', 'A,B,C', 'Vue3引入Composition API、Teleport、改进的v-model，不包含jQuery', 8.00, 'Vue3核心', '第三章 Vue3入门', 1),
(304, 201, 103, 'multi_choice', 'hard', 'JavaScript中哪些方法可以遍历数组？（多选）', '{"A":"forEach()","B":"map()","C":"for...of","D":"while()"}', 'A,B,C', 'forEach/map/for...of均可遍历数组，while不是数组专用方法', 8.00, 'JS数组', '第四章 JavaScript进阶', 1),
(305, 201, 103, 'true_false', 'easy', 'HTML5新增了<video>和<audio>标签用于多媒体播放', 'true', 'HTML5原生支持音视频播放，无需Flash插件', 2.00, 'HTML5新特性', '第五章 HTML5 API', 1),
(306, 201, 103, 'true_false', 'medium', 'CSS Grid布局只能实现二维布局，不能做一维排列', 'false', 'Grid可以做一维排列，但Flex更适合一维，Grid主打二维', 2.00, 'CSS Grid', '第六章 CSS Grid', 1),
(307, 201, 103, 'fill_blank', 'medium', '在Vue3中，使用___API可以更好地组织组件逻辑，替代Options API', 'Composition', 'Composition API提供了setup函数、ref/reactive等响应式工具', 5.00, 'Vue3 Composition API', '第七章 Vue3进阶', 1),
(308, 201, 103, 'essay', 'hard', '请简述SPA（单页应用）的优缺点，并说明Vue Router在其中扮演的角色', 'SPA优点：用户体验好、减少服务器压力；缺点：首屏加载慢、SEO不友好。Vue Router负责管理页面路由，实现组件切换而不刷新页面。', '从用户体验、性能、SEO角度分析SPA优缺点，说明路由管理的重要性', 15.00, '前端架构', '第八章 项目实战', 1);

-- 软件工程导论 题目 (course_id=202)
INSERT IGNORE INTO question (id, course_id, teacher_id, type, difficulty, content, answer, analysis, score, knowledge_points, chapter, status) VALUES
(309, 202, 103, 'single_choice', 'easy', '瀑布模型的特点是？', '{"A":"迭代开发","B":"线性顺序、阶段分明","C":"快速原型","D":"螺旋式推进"}', 'B', '瀑布模型按需求→设计→编码→测试→维护的线性流程进行', 5.00, '软件过程模型', '第一章 软件工程概述', 1),
(310, 202, 103, 'single_choice', 'medium', '敏捷开发中Scrum的核心角色不包括？', '{"A":"Product Owner","B":"Scrum Master","C":"Development Team","D":"Project Manager"}', 'D', 'Scrum三角色：PO/SM/DevTeam，PM是传统项目管理角色', 5.00, '敏捷开发', '第二章 敏捷方法', 1),
(311, 202, 103, 'multi_choice', 'medium', '以下哪些属于UML图？（多选）', '{"A":"用例图","B":"类图","C":"时序图","D":"甘特图"}', 'A,B,C', '甘特图是项目管理工具图，不属于UML标准 diagrams', 8.00, 'UML建模', '第三章 UML', 1),
(312, 202, 103, 'multi_choice', 'hard', '好的单元测试应该具备哪些特性？（多选）', '{"A":"独立性","B":"可重复性","C":"执行速度快","D":"依赖外部服务"}', 'A,B,C', '单元测试不应依赖外部服务，应独立可重复且快速', 8.00, '软件测试', '第四章 软件测试', 1),
(313, 202, 103, 'true_false', 'easy', '需求分析阶段的主要产出物是详细设计文档', 'false', '需求分析产出的是SRS（软件需求规格说明书），详细设计在设计阶段产出', 2.00, '需求工程', '第五章 需求分析', 1),
(314, 202, 103, 'true_false', 'medium', '代码审查(Code Review)是一种有效的质量保证手段', 'true', 'Code Review能发现潜在bug、统一编码风格、促进知识共享', 2.00, '代码质量', '第六章 代码审查', 1),
(315, 202, 103, 'fill_blank', 'medium', '在软件开发生命周期中，___模型强调通过多次迭代逐步完善产品', '增量/迭代', '增量模型和迭代模型都强调分步交付、持续改进', 5.00, '软件过程', '第七章 过程改进', 1),
(316, 202, 103, 'essay', 'hard', '请比较传统瀑布模型与敏捷开发的适用场景，各举一个适合的例子', '瀑布模型适合需求明确、变更少的项目如航天软件；敏捷适合需求多变、需要快速反馈的项目如互联网应用。关键区别在于对变更的应对方式和文档重视程度。', '从需求稳定性、团队规模、客户参与度等多维度对比两种方法论', 15.00, '软件方法论', '第八章 综合实践', 1);

-- Python程序设计 题目 (course_id=203)
INSERT IGNORE INTO question (id, course_id, teacher_id, type, difficulty, content, answer, analysis, score, knowledge_points, chapter, status) VALUES
(317, 203, 103, 'single_choice', 'easy', 'Python中输出"Hello World"的正确语句是？', '{"A":"print(""Hello World"")","B":"echo ""Hello World""","C":"console.log(""Hello World"")","D":"System.out.println(""Hello World"")"}', 'A', 'Python使用print()函数输出，不需要分号结尾', 5.00, 'Python基础', '第一章 Python简介', 1),
(318, 203, 103, 'single_choice', 'medium', '以下哪个不是Python的数据类型？', '{"A":"list","B":"tuple","C":"array","D":"dict"}', 'C', 'Python内置list/tuple/dict，array需导入array模块', 5.00, '数据类型', '第二章 数据类型', 1),
(319, 203, 103, 'multi_choice', 'medium', 'Python中哪些是可变类型？（多选）', '{"A":"list","B":"dict","C":"set","D":"str"}', 'A,B,C', 'str是不可变的字符串类型，list/dict/set均可修改内容', 8.00, '可变性', '第三章 数据结构', 1),
(320, 203, 103, 'multi_choice', 'hard', '关于Python装饰器，下列说法正确的有？（多选）', '{"A":"装饰器本质是函数","B":"可以叠加使用","C":"必须带参数","D":"可以修饰类方法"}', 'A,B,D', '装饰器不一定带参数，@语法糖会自动将被装饰函数传入', 8.00, '装饰器', '第四章 高级特性', 1),
(321, 203, 103, 'true_false', 'easy', 'Python使用缩进来表示代码块的范围', 'true', 'Python独特的缩进规则是其语法特色之一', 2.00, '语法基础', '第五章 控制流', 1),
(322, 203, 103, 'true_false', 'medium', 'Python的列表切片操作会修改原列表', 'false', '切片返回的是新列表副本，不会修改原列表', 2.00, '列表操作', '第六章 列表详解', 1),
(323, 203, 103, 'fill_blank', 'medium', 'Python中使用___关键字来定义一个函数', 'def', 'def function_name(parameters): 是Python函数定义的标准语法', 5.00, '函数', '第七章 函数编程', 1),
(324, 203, 103, 'essay', 'hard', '请解释Python中的GIL（全局解释器锁），它对多线程编程有什么影响？', 'GIL是CPython解释器的互斥锁，同一时刻只允许一个线程执行字节码。影响：CPU密集型多线程无法真正并行，但IO密集型场景影响较小。可用multiprocessing绕过GIL限制。', '从GIL原理、存在原因、对多线程的影响及解决方案全面阐述', 15.00, '并发编程', '第八章 高级主题', 1);

SELECT '=== 题目已创建 ===' AS info;
SELECT COUNT(*) AS total_questions FROM question WHERE teacher_id = 103;

-- ============================================
-- 五、创建试卷（每门课一张）
-- ============================================
INSERT IGNORE INTO exam_paper (id, name, course_id, teacher_id, total_score, pass_score, status, create_time) VALUES
(51, 'Web前端开发技术 - 期中测验', 201, 103, 50.0, 30.0, 1, '2026-06-01 10:00:00'),
(52, '软件工程导论 - 单元测试', 202, 103, 50.0, 30.0, 1, '2026-06-02 14:00:00'),
(53, 'Python程序设计 - 期末考试', 203, 103, 50.0, 30.0, 1, '2026-06-03 09:00:00');

-- 试卷题目关联
INSERT IGNORE INTO paper_question (paper_id, question_id, sort_order) VALUES
(51, 301, 1), (51, 302, 2), (51, 303, 3), (51, 304, 4), (51, 305, 5), (51, 306, 6), (51, 307, 7), (51, 308, 8),
(52, 309, 1), (52, 310, 2), (52, 311, 3), (52, 312, 4), (52, 313, 5), (52, 314, 6), (52, 315, 7), (52, 316, 8),
(53, 317, 1), (53, 318, 2), (53, 319, 3), (53, 320, 4), (53, 321, 5), (53, 322, 6), (53, 323, 7), (53, 324, 8);

SELECT '=== 试卷已创建 ===' AS info;

-- ============================================
-- 六、创建考试（每门课一场）
-- ============================================
INSERT IGNORE INTO exam (id, name, course_id, exam_paper_id, teacher_id, start_time, end_time, duration, total_score, pass_score, status, student_count, submit_count, create_time) VALUES
(11, 'Web前端期中测验', 201, 51, 103, '2026-06-05 14:00:00', '2026-06-05 15:30:00', 90, 50.0, 30.0, 'ongoing', 3, 1, '2026-06-04 10:00:00'),
(12, '软件工程单元测试', 202, 52, 103, '2026-06-06 10:00:00', '2026-06-06 11:45:00', 105, 50.0, 30.0, 'published', 3, 0, '2026-06-04 11:00:00'),
(13, 'Python期末考试', 203, 53, 103, '2026-06-07 09:00:00', '2026-06-07 11:00:00', 120, 50.0, 30.0, 'published', 3, 0, '2026-06-04 12:00:00');

SELECT '=== 考试已创建 ===' AS info;

-- ============================================
-- 七、创建考试记录和答题记录
-- ============================================

-- Web前端期中测验 (exam_id=11): 3人参加，1人已提交
INSERT IGNORE INTO exam_record (id, exam_id, student_id, start_time, submit_time, status, objective_score, subjective_score, total_score, is_passed) VALUES
(11, 11, 401, '2026-06-05 14:05:00', '2026-06-05 15:20:00', 'submitted', 20.0, 12.0, 32.0, 1),
(12, 11, 402, '2026-06-05 14:02:00', NULL, 'ongoing', 0.0, 0.0, 0.0, 0),
(13, 11, 403, '2026-06-05 14:10:00', NULL, 'ongoing', 0.0, 0.0, 0.0, 0);

-- Web前端答题记录（孙小芳 已提交）
INSERT IGNORE INTO answer_record (id, exam_record_id, question_id, student_answer, is_correct, score, answer_times, time_spent) VALUES
(21, 11, 301, 'A', 1, 5.00, 1, 30),
(22, 11, 302, 'A', 1, 5.00, 1, 60),
(23, 11, 303, 'A,C', 0, 4.00, 2, 120),
(24, 11, 304, 'A,B', 0, 2.00, 1, 90),
(25, 11, 305, 'true', 1, 2.00, 1, 10),
(26, 11, 306, 'false', 1, 2.00, 1, 15),
(27, 11, 307, 'Composition', 1, 5.00, 1, 300),
(28, 11, 308, 'SPA的优点包括用户体验好、服务器压力小等。Vue Router负责路由管理。', NULL, 0.00, 1, 600);

-- Web前端答题记录（周小强 进行中）
INSERT IGNORE INTO answer_record (id, exam_record_id, question_id, student_answer, is_correct, score, answer_times, time_spent) VALUES
(29, 12, 301, 'B', 0, 0.00, 1, 25),
(30, 12, 302, 'C', 0, 0.00, 1, 45);

-- Web前端答题记录（吴小敏 进行中）
INSERT IGNORE INTO answer_record (id, exam_record_id, question_id, student_answer, is_correct, score, answer_times, time_spent) VALUES
(31, 13, 301, 'A', 1, 5.00, 1, 35),
(32, 13, 305, 'true', 1, 2.00, 1, 12);

-- ============================================
-- 八、创建批改任务（针对已提交的主观题）
-- ============================================
INSERT IGNORE INTO grading_task (id, exam_record_id, question_id, student_id, status, score, comment, grader_id, create_time, grade_time) VALUES
(21, 11, 308, 401, 'graded', 0.00, '答案过于简略，未充分展开论述SPA缺点和Vue Router具体作用', 1, '2026-06-05 15:25:00', '2026-06-05 15:30:00');

SELECT '=== 全部数据插入完成 ===' AS info;
SELECT '--- 王老师数据汇总 ---' AS summary;
SELECT '课程数' AS item, COUNT(*) AS value FROM course WHERE teacher_id = 103
UNION ALL
SELECT '学生数', COUNT(DISTINCT cs.student_id) FROM course_selection cs JOIN course c ON cs.course_id = c.id WHERE c.teacher_id = 103
UNION ALL
SELECT '题目数', COUNT(*) FROM question WHERE teacher_id = 103
UNION ALL
SELECT '试卷数', COUNT(*) FROM exam_paper WHERE teacher_id = 103
UNION ALL
SELECT '考试数', COUNT(*) FROM exam WHERE teacher_id = 103
UNION ALL
SELECT '待批改任务', COUNT(*) FROM grading_task gt JOIN exam e ON gt.exam_record_id IN (SELECT id FROM exam_record WHERE exam_id = e.id) WHERE e.teacher_id = 103 AND gt.status = 'pending'
UNION ALL
SELECT '已批改记录', COUNT(*) FROM grading_task gt JOIN exam e ON gt.exam_record_id IN (SELECT id FROM exam_record WHERE exam_id = e.id) WHERE e.teacher_id = 103 AND gt.status = 'graded';
