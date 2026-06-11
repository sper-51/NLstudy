# 学生端全功能冒烟测试（2026-06-08）

- Base: http://127.0.0.1:3000
- 账号: `stu_001 / 123456`

## 登录
- 登录后 URL: http://127.0.0.1:3000/home

## 学习中心
- URL: http://127.0.0.1:3000/home
- H1: 
- 摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 待考 1 场考试 已考 6 场均分65 错题 0 道待复习 进度 0% 使用课程码加入新课程 加入课程 我加入的课程 查看全部 Web前端开发技术 王老师 · 419AB2AE 学习进度 0% Java程序设计 王老师 · JAVA2024001 学习进度 0% Python数据分析 王老师 · PYTH2024001 学习进度 0% ddd 王老师 · 8FE21D07 学习进度 0% 待参加考试 1 场 111 进行中 ddd · 06-01 00:00 120分钟 · 100分 进入考试 已完成考试 6 场 222 0分 · 排名 #- 成绩报告 | 错题重刷 课程15联调考试A 0分 · 排名 #- 成绩报告 | 错题重刷 Java程序设计-期中考试 76分 · 排名 #- 成绩报告 | 错题重刷 通知提醒 共 0 条 暂无通知
- 截图: D:\123\Java_items\NLstudy\screenshots\student_full_smoke_2026-06-08\学习中心.png
- API错误: 无
- 页面错误: 无

## 我的课程
- URL: http://127.0.0.1:3000/courses
- H1: 我的课程
- 摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 我的课程 共 4 门课程 Web前端开发技术 王老师 · 419AB2AE 学习进度 0% 3 学分 · 64 学时 进入课程 Java程序设计 王老师 · JAVA2024001 学习进度 0% 4 学分 · 56 学时 进入课程 Python数据分析 王老师 · PYTH2024001 学习进度 0% 2 学分 · 32 学时 进入课程 ddd 王老师 · 8FE21D07 学习进度 0% 3 学分 · 64 学时 进入课程
- 截图: D:\123\Java_items\NLstudy\screenshots\student_full_smoke_2026-06-08\我的课程.png
  - 交互 课程页主要按钮: 未找到
- API错误: 无
- 页面错误: 无

## 课程详情15
- URL: http://127.0.0.1:3000/courses/15
- H1: ddd
- 摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 我的课程 ddd ddd 8FE21D07 · 王老师 3 学分 64 学时 1111 已完成考试 2 均分 0 待参加考试 0 最近一场 - 错题数 0 涉及 0 个知识点 学习进度 0% 知识点掌握度 考试列表 全部 待参加 已完成 考试名称 时间 时长/题数/总分 操作 111 已完成 06-05 00:00 - 02:00 120分钟 · 25题 · 100分 考试回顾 错题重刷 课程15联调考试A 已完成 06-06 00:00 - 00:30 30分钟 · 25题 · 5分 考试回顾 错题重刷 111 进行中 06-01 00:00 - 02:00 120分钟 · 25题 · 100分 开始考试 复习本课程错题 共 0 道错题待复习 查看最近成绩报告 最近一次均分 0
- 截图: D:\123\Java_items\NLstudy\screenshots\student_full_smoke_2026-06-08\课程详情15.png
- API错误: 无
- 页面错误: 无

## 成绩报告
- URL: http://127.0.0.1:3000/report
- H1: 成绩报告
- 摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 成绩报告 选择课程 Web前端开发技术（419AB2AE） Java程序设计（JAVA2024001） Python数据分析（PYTH2024001） ddd（8FE21D07） 考试 暂无成绩数据 该课程下暂无已发布的考试成绩
- 截图: D:\123\Java_items\NLstudy\screenshots\student_full_smoke_2026-06-08\成绩报告.png
  - 交互 成绩报告筛选下拉: 未找到
- API错误: 无
- 页面错误: 无

## 成绩报告99
- URL: http://127.0.0.1:3000/report/99
- H1: 成绩报告
- 摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 成绩报告 选择课程 Web前端开发技术（419AB2AE） Java程序设计（JAVA2024001） Python数据分析（PYTH2024001） ddd（8FE21D07） 考试 Java程序设计-期中考试 (06-05) Java程序设计-第二次单元测验 (04-15) Java程序设计-第一次单元测验 (03-10) Java程序设计 加课码: JAVA2024001 Java程序设计-期中考试 2026-06-05T21:14:00 我的得分 76 满分 100 班级均分 74 共 12 人 最高分 95 参考值 排名 #6 百分位 58.3 正确率 16.7% 1/6 知识点掌握度 本课程成绩趋势 逐题回顾 共 6 题 · 答对 1 题 1 Java中main方法的返回类型是什么？ 我的答案：B 正确答案：A 单选题 · 5分 · 得14分 加入错题本 2 以下哪个不是Java的基本数据类型？ 我的答案：C 正确答案：B 单选题 · 5分 · 得14分 加入错题本 3 ArrayList和LinkedList的主要区别？ 我的答案：B 正确答案：A 单选题 · 5分 · 得14分 加入错题本 4 Java源文件编译后会生成对应的.class文件。 我的答案：true 判断题 · 10分 · 得8分 5 在Java中，定义变量 age 并赋值18，代码应写为：int ___ = 18。 我的
- 截图: D:\123\Java_items\NLstudy\screenshots\student_full_smoke_2026-06-08\成绩报告99.png
  - 交互 成绩报告筛选下拉: 未找到
- API错误: 无
- 页面错误: 无

## 错题本
- URL: http://127.0.0.1:3000/wrong-questions
- H1: 错题本
- 摘要: 智考平台 主菜单 学习中心 我的课程 错题本 成绩报告 张明 stu_001 错题本 共 1 道错题 0高频错题 1涉及课程 1涉及考试 来源类型： 全部 考试错题 自建题库 课程： 全部课程 知识点： 全部知识点 我的自建题库 新建题库 点击创建你的第一个题库 错题列表 考试错题 课程15联调考试A 课程15联调样题：2 + 2 = ? 单选题 我的答案：(未答) 正确答案： 上次错误：2 days ago 重练 已收藏
- 截图: D:\123\Java_items\NLstudy\screenshots\student_full_smoke_2026-06-08\错题本.png
  - 交互 错题本操作按钮: 未找到
- API错误: 无
- 页面错误: 无

## 汇总
- API 4xx/5xx 数量: 0
- 页面 JS 错误数量: 0
## 日志摘录
- api:200 http://127.0.0.1:3000/src/api/student.ts
- api:200 http://127.0.0.1:3000/src/api/auth.ts
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
- api:200 http://127.0.0.1:3000/api/v1/notifications?pageSize=20
- api:200 http://127.0.0.1:3000/api/v1/student/grades?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/student/exams/pending
- api:200 http://127.0.0.1:3000/api/v1/student/exams/history
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/notifications?pageSize=20
- api:200 http://127.0.0.1:3000/api/v1/student/wrongQuestions?pageSize=1
- api:200 http://127.0.0.1:3000/api/v1/student/exams/history
- api:200 http://127.0.0.1:3000/api/v1/student/grades?pageSize=100
- api:200 http://127.0.0.1:3000/api/v1/student/exams/pending
- debug: [vite] connecting...
- debug: [vite] connected.
- api:200 http://127.0.0.1:3000/src/api/auth.ts
- api:200 http://127.0.0.1:3000/src/api/student.ts
- api:200 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/wrongQuestions/report?courseId=15
- api:200 http://127.0.0.1:3000/api/v1/student/courses/15
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/grades?courseId=11
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/grades
- api:200 http://127.0.0.1:3000/api/v1/student/grades?courseId=12
- api:200 http://127.0.0.1:3000/api/v1/student/grades/99
- warning: [ECharts] The ticks may be not readable when set min: 0, max: 100 and alignTicks: true
- warning: [ECharts] The ticks may be not readable when set min: 0, max: 100 and alignTicks: true
- warning: [ECharts] The ticks may be not readable when set min: 0, max: 100 and alignTicks: true
- warning: [ECharts] The ticks may be not readable when set min: 0, max: 100 and alignTicks: true
- debug: [vite] connecting...
- debug: [vite] connected.
- api:304 http://127.0.0.1:3000/src/api/student.ts
- api:304 http://127.0.0.1:3000/src/api/auth.ts
- api:304 http://127.0.0.1:3000/src/api/request.ts
- api:200 http://127.0.0.1:3000/api/v1/student/questionBanks
- api:200 http://127.0.0.1:3000/api/v1/student/courses
- api:200 http://127.0.0.1:3000/api/v1/student/wrongQuestions