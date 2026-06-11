import request from './request'
import type {
  LoginResponse,
  TeacherCourse,
  QuestionDetail,
  ExamPaper,
  ExamSchedule,
  GradingTask,
  GradeStatistics,
  CourseStudent,
  PaginatedResponse,
} from './types'

type ListResult<T> = T[] | (PaginatedResponse<T> & {
  data?: any
  records?: T[]
  total?: number
})

// ========== 认证相关 ==========

// 教师登录
export function loginAsTeacher(username: string, password: string) {
  return request.post<any, LoginResponse>('/auth/teacher/login', { username, password })
}

// ========== 课程管理 ==========

// 获取课程列表
export function getTeacherCourses(params?: { page?: number; pageSize?: number; keyword?: string }) {
  return request.get<any, ListResult<TeacherCourse>>('/teacher/courses', { params })
}

// 创建课程
export function createCourse(data: Partial<TeacherCourse>) {
  return request.post<any, TeacherCourse>('/teacher/courses', data)
}

// 编辑课程
export function updateCourse(id: number, data: Partial<TeacherCourse>) {
  return request.put<any, TeacherCourse>(`/teacher/courses/${id}`, data)
}

// 删除课程
export function deleteCourse(id: number) {
  return request.delete(`/teacher/courses/${id}`)
}

// 生成课程码
export function generateCourseCode(id: number, data?: { expireTime?: string }) {
  return request.post<any, { code: string; shareLink: string }>(`/teacher/courses/${id}/generateCode`, data)
}

// 获取选课学生
export function getCourseStudents(courseId: number, params?: { page?: number; pageSize?: number; keyword?: string }) {
  return request.get<any, ListResult<CourseStudent>>(`/teacher/courses/${courseId}/students`, { params })
}

// 移除学生
export function removeCourseStudent(courseId: number, studentId: number) {
  return request.delete(`/teacher/courses/${courseId}/students/${studentId}`)
}

// ========== 题库管理 ==========

// 获取题目列表
export function getQuestions(params?: {
  courseId?: number | string
  type?: string
  keyword?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, ListResult<QuestionDetail>>('/teacher/questions', { params })
}

// 创建题目
export function createQuestion(data: Partial<QuestionDetail>) {
  return request.post<any, QuestionDetail>('/teacher/questions', data)
}

// 编辑题目
export function updateQuestion(id: number, data: Partial<QuestionDetail>) {
  return request.put<any, QuestionDetail>(`/teacher/questions/${id}`, data)
}

// 删除题目
export function deleteQuestion(id: number) {
  return request.delete(`/teacher/questions/${id}`)
}

// 获取题目详情
export function getQuestionDetail(id: number) {
  return request.get<any, QuestionDetail>(`/teacher/questions/${id}`)
}

// 收藏题目
export function favoriteQuestion(id: number) {
  return request.post(`/teacher/questions/${id}/favorite`)
}

// 导入题目
export function importQuestions(file: File, courseId: number) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('courseId', String(courseId))
  return request.post('/teacher/questions/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

// ========== 试卷管理 ==========

// 获取试卷列表
export function getPapers(params?: {
  courseId?: number | string
  status?: number
  keyword?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, ListResult<ExamPaper>>('/teacher/papers', { params })
}

// 创建试卷
export function createPaper(data: Partial<ExamPaper>) {
  return request.post<any, ExamPaper>('/teacher/papers', data)
}

// 编辑试卷
export function updatePaper(id: number, data: Partial<ExamPaper>) {
  return request.put<any, ExamPaper>(`/teacher/papers/${id}`, data)
}

// 删除试卷
export function deletePaper(id: number) {
  return request.delete(`/teacher/papers/${id}`)
}

// 添加题目到试卷
export function addPaperQuestions(paperId: number, data: { questionIds: number[]; scores: number[] }) {
  return request.post(`/teacher/papers/${paperId}/questions`, data)
}

// 移除题目
export function removePaperQuestion(paperId: number, questionId: number) {
  return request.delete(`/teacher/papers/${paperId}/questions/${questionId}`)
}

// 预览试卷
export function previewPaper(paperId: number) {
  return request.get<any, ExamPaper>(`/teacher/papers/${paperId}/preview`)
}

// ========== 组卷中心 ==========

// 获取题目列表（组卷专用）
export function getQuestionsForPaper(params?: {
  courseId?: number | string
  type?: string
  difficulty?: string
  keyword?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, { questions: any[]; total: number }>('/teacher/questions/for-paper', { params })
}

// 获取知识点列表
export function getKnowledgePoints(courseId?: number | string) {
  return request.get<any, { value: string; label: string }[]>('/teacher/questions/knowledge-points', {
    params: courseId ? { courseId } : {},
  })
}

// ========== 考试管理 ==========

// 获取考试列表
export function getSchedules(params?: {
  courseId?: number | string
  status?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, ListResult<ExamSchedule>>('/teacher/exams', { params })
}

export function getExamDetail(examId: number) {
  return request.get<any, any>(`/teacher/exams/${examId}/detail`)
}

// 发布考试
export function createSchedule(data: Partial<ExamSchedule>) {
  return request.post<any, ExamSchedule>('/teacher/exams', data)
}

// 编辑考试
export function updateSchedule(id: number, data: Partial<ExamSchedule>) {
  return request.put<any, ExamSchedule>(`/teacher/exams/${id}`, data)
}

// 取消考试
export function cancelSchedule(id: number) {
  return request.put(`/teacher/exams/${id}/cancel`)
}

// 强制交卷
export function forceSubmit(examId: number, studentId: number) {
  return request.post(`/teacher/exams/${examId}/forceSubmit/${studentId}`)
}

// 获取监控数据
export function getMonitorData(examId: number) {
  return request.get<any, any>(`/teacher/exams/${examId}/monitor`)
}

export function finishExam(examId: number) {
  return request.post<any, any>(`/teacher/exams/${examId}/finish`)
}

// 安排补考
export function scheduleMakeup(id: number, data: { startTime: string; endTime: string; duration: number }) {
  return request.post(`/teacher/exams/${id}/makeup`, data)
}

// ========== 批改管理 ==========

// 获取待批改列表
export function getGradingTasks(params?: {
  examId?: number | string
  status?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, ListResult<GradingTask>>('/teacher/grading/tasks', { params })
}

// 提交批改
export function submitGrading(taskId: number, data: { score: number; comment?: string }) {
  return request.put(`/teacher/grading/tasks/${taskId}`, data)
}

// 批量批改
export function batchGrading(data: Array<{ taskId: number; score: number; comment?: string }>) {
  return request.post('/teacher/grading/batch', { tasks: data })
}

// 批改历史
export function getGradingHistory(params?: {
  examId?: number | string
  page?: number
  pageSize?: number
}) {
  return request.get<any, ListResult<GradingTask>>('/teacher/grading/history', { params })
}

// ========== 成绩复核 ==========

// 复核列表
export function getReviews(params?: {
  examId?: number | string
  status?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, any>('/teacher/grading/reviews', { params })
}

// 处理复核
export function handleReview(reviewId: number, data: { action: 'approve' | 'reject'; newScore?: number; teacherComment?: string }) {
  return request.post(`/teacher/grading/reviews/${reviewId}/handle`, data)
}

// ========== 成绩与数据分析 ==========

// 成绩列表
export function getGrades(params?: {
  examId?: number | string
  courseId?: number | string
  className?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, any>('/teacher/grades', { params })
}

// 成绩详情
export function getGradeDetail(gradeId: number) {
  return request.get<any, any>(`/teacher/grades/${gradeId}`)
}

// 成绩统计
export function getGradeStatistics(examId: number) {
  return request.get<any, GradeStatistics>(`/teacher/grades/exam/${examId}/statistics`)
}

// 班级对比
export function getClassComparison(params?: { examId?: number; courseId?: number }) {
  return request.get<any, GradeStatistics['classComparison']>('/teacher/grades/classComparison', { params })
}

// 成绩分布
export function getScoreDistribution(params?: { examId?: number; courseId?: number }) {
  return request.get<any, Record<string, number>>('/teacher/grades/distribution', { params })
}

// 知识点掌握度
export function getKnowledgeMastery(courseId: number) {
  return request.get<any, Array<{ name: string; mastery: number }>>(`/teacher/analytics/knowledge/${courseId}`)
}

// 考试知识点掌握度
export function getExamKnowledgeMastery(examId: number) {
  return request.get<any, Array<{ name: string; value: number; mastery: number; totalQuestions: number; answeredCount: number }>>(`/teacher/analytics/knowledge/exam/${examId}`)
}

// 考试趋势
export function getExamTrend(courseId: number) {
  return request.get<any, any>(`/teacher/analytics/trend/${courseId}`)
}

export function getDifficultyAnalysis(examId: number) {
  return request.get<any, any>(`/teacher/analytics/difficulty-analysis/${examId}`)
}

// 学生排名
export function getStudentRanking(examId: number, params?: { page?: number; pageSize?: number }) {
  return request.get<any, any>(`/teacher/grades/exam/${examId}/ranking`, { params })
}

// 导出成绩
export function exportGrades(params?: { examId?: number; courseId?: number; format?: 'xlsx' | 'csv' }) {
  return request.get('/teacher/grades/export', {
    params,
    responseType: 'blob',
  })
}

// ========== AI 出题 ==========

export interface AiQuestionDraft {
  draftId?: string
  type: string
  content: string
  options?: Array<{ label: string; content: string; isCorrect?: boolean }>
  answer: string
  analysis?: string
  difficulty: string
  score: number
  knowledgePoints?: string
}

export function generateAiQuestions(data: {
  courseId?: number | string
  courseName?: string
  chapter?: string
  knowledgePoints: string[]
  questionTypes: string[]
  difficulty: string
  count: number
  score: number
  requirements?: string
}) {
  return request.post<any, { questions: AiQuestionDraft[] }>('/teacher/ai/questions/generate', data)
}

export function saveAiQuestionDrafts(data: {
  courseId: number | string
  questions: AiQuestionDraft[]
}) {
  return request.post<any, { questionIds: number[] }>('/teacher/ai/questions/save', data)
}

export function addAiQuestionsToPaper(data: {
  paperId: number
  courseId: number | string
  questions: AiQuestionDraft[]
}) {
  return request.post<any, { questionIds: number[]; paperId: number }>('/teacher/ai/questions/add-to-paper', data)
}
