# 学生考试-成绩-复核-教师处理闭环测试（2026-06-08）

- Base: http://127.0.0.1:3000
- 学生: `stu_001 / 123456` (`userId=400`)
- 教师: `wanglaoshi / 123456` (`userId=103`)

## 测试样本
- gradeId=114, examId=7, exam=111, beforeScore=0
## 学生登录与首页
- URL: http://127.0.0.1:3000/home
- token存在: 是
## 学生查看成绩
- URL: http://127.0.0.1:3000/report/114
- token仍存在: 是
- 页面有申请复核入口: 是
- 摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 成绩报告 选择课程 Web前端开发技术（419AB2AE） Java程序设计（JAVA2024001） Python数据分析（PYTH2024001） ddd（8FE21D07） 考试 111 (06-08) ddd 加课码: 8FE21D07 111 2026-06-08T11:25:23 我的得分 0 满分 100 班级均分 -- 共 2 人 最高分 -- 参考值 排名 #1 百分位 100 正确率 0% 0/0 成绩复核 如发现成绩、主观题得分或题目判分存在疑问，可提交整卷或单题复核申请，教师处理后成绩会同步更新。 申请整卷复核 知识点掌握度 本课程成绩趋势 逐题回顾 共 0 题 · 答对 0 题
## 学生申请复核
- reviewId=17
- 教师端可见: 是
## 教师处理复核
- 操作: approve, newScore=1
## 成绩回写
- totalScore=1
- reviewStatus=approved
- 页面含已通过: 是
- 页面含新分数: 是
- token仍存在: 是

## 汇总
- API 4xx/5xx: 0
- JS Error: 0
## 日志摘录
- api:200 http://127.0.0.1:3000/src/api/auth.ts
- api:200 http://127.0.0.1:3000/src/api/student.ts
- api:200 http://127.0.0.1:3000/src/api/request.ts
- debug: [vite] connecting...
- debug: [vite] connected.
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/auth/login
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/wrongQuestions?pageSize=1
- api:200 http://127.0.0.1:3000/api/v1/student/exams/pending
- api:200 http://127.0.0.1:3000/api/v1/student/grades?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/notifications?pageSize=20
- api:200 http://127.0.0.1:3000/api/v1/student/exams/history
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/grades/114
- api:200 http://127.0.0.1:3000/api/v1/student/grades/114
- api:200 http://127.0.0.1:3000/api/v1/student/grades
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/grades/114
- api:200 http://127.0.0.1:3000/api/v1/student/grades/114
- api:200 http://127.0.0.1:3000/api/v1/student/grades