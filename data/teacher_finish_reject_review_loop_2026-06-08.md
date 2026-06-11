# 教师结束考试与复核驳回闭环（2026-06-08）

## 教师结束考试

- 接口：`POST /api/v1/teacher/exams/16/finish`
- 结果：成功，返回 `settledCount=2`、`gradingTaskCount=0`、`status=finished`。

## 学生历史考试

- 学生：`stu_001 (userId=400)`
- 考试：`examId=16`
- 历史考试接口 `/student/exams/history` 已返回真实 `gradeId=113`。
- 学生可通过 `gradeId` 正确进入 `/report/113`，不会误用 `examId/recordId`。

## 复核驳回链路

- 学生对 `gradeId=113` 发起整卷复核，生成 `reviewId=18`。
- 教师端通过 `/teacher/grading/reviews` 可查询到 pending 复核。
- 教师执行驳回：`POST /teacher/grading/reviews/18/handle`，`action=reject`。
- 学生再次查看成绩：`totalScore=0.0` 保持不变，`reviewStatus=rejected`。

## 结论

- “老师结束考试 -> 学生历史考试 -> 学生查看成绩 -> 学生申请复核 -> 老师驳回 -> 学生成绩状态更新”链路已打通。
- 与同意链路互补：同意链路已在 `student_teacher_review_loop_2026-06-08.md` 验证，成绩从 `0` 更新到 `1`，状态变为 `approved/reviewed`。
