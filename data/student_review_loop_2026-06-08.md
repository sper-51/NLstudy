# 学生端成绩复核闭环测试（2026-06-08）

- Base: http://127.0.0.1:3000
- 账号: `stu_001 / 123456`
- 目标: 学生成绩报告 -> 申请复核 -> 状态回显 -> 教师端可处理

## 登录
- URL: http://127.0.0.1:3000/home
- 选中成绩: gradeId=112, exam=222, score=0
## 成绩报告
- URL: http://127.0.0.1:3000/report/112
- 页面摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 成绩报告 选择课程 Web前端开发技术（419AB2AE） Java程序设计（JAVA2024001） Python数据分析（PYTH2024001） ddd（8FE21D07） 考试 222 (06-08) ddd 加课码: 8FE21D07 222 2026-06-08T09:53:12 整卷复核：待处理 我的得分 0 满分 100 班级均分 -- 共 2 人 最高分 -- 参考值 排名 #2 百分位 50 正确率 0% 0/0 成绩复核 如发现成绩、主观题得分或题目判分存在疑问，可提交整卷或单题复核申请，教师处理后成绩会同步更新。 待处理 知识点掌握度 本课程成绩趋势 逐题回顾 共 0 题 · 答对 0 题
- 复核入口: 未发现可提交按钮，可能已有 pending/approved 复核
## 接口核验
- reviews 数量: 1
- pending 数量: 1
- 最新复核: {"reason":"自动化联调复核申请 2026-06-08T03-20-04-305Z：核查得分与参考答案匹配情况。","originalScore":0,"gradeId":112,"createTime":"2026-06-08T11:20:04","examId":17,"id":15,"status":"pending"}

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
- api:200 http://127.0.0.1:3000/api/v1/notifications?pageSize=20
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/wrongQuestions?pageSize=1
- api:200 http://127.0.0.1:3000/api/v1/student/grades?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/student/exams/history
- api:200 http://127.0.0.1:3000/api/v1/student/exams/pending
- api:200 http://127.0.0.1:3000/api/v1/student/grades?pageSize=100
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/grades/112
- api:200 http://127.0.0.1:3000/api/v1/student/grades
- api:200 http://127.0.0.1:3000/api/v1/student/grades/112
- api:200 http://127.0.0.1:3000/api/v1/student/grades/112