import request from './request'
import type { Course, Exam, ExamRecord, Grade, WrongQuestion, StudentQuestionBank } from './types'

// ========== 课程相关 ==========

// 获取课程列表
export function getCourses(params?: { status?: number; keyword?: string }) {
  return request.get<any, Course[]>('/student/courses', { params })
}

// 获取课程详情
export function getCourseDetail(courseId: number) {
  return request.get<any, Course>(`/student/courses/${courseId}`)
}

// 使用课程码加课
export function joinCourse(courseCode: string) {
  return request.post<any, any>('/student/courses/join', { courseCode })
}

// 获取/更新学生个人信息
export function getStudentProfile() {
  return request.get<any, any>('/student/profile')
}

export function updateStudentProfile(data: { realName?: string; email?: string; phone?: string; avatar?: string }) {
  return request.put<any, any>('/student/profile', data)
}

export function changePassword(data: { oldPassword: string; newPassword: string }) {
  return request.put<any, any>('/auth/password', data)
}

// ========== 考试相关 ==========

// 获取待参加考试列表
export function getPendingExams() {
  return request.get<any, Exam[]>('/student/exams/pending')
}

// 获取历史考试（已完成）列表
export function getExamsHistory() {
  return request.get<any, Exam[]>('/student/exams/history')
}

// 考试资格验证
export function checkExamQualification(examId: number) {
  return request.get<any, any>(`/student/exams/${examId}/qualification`)
}

// 开始考试
export function startExam(examId: number) {
  return request.post<any, ExamRecord>(`/student/exams/${examId}/start`)
}

// 保存答题进度（断点续考）
export function saveExamProgress(examRecordId: number, data: any) {
  return request.post(`/student/exams/records/${examRecordId}/save`, data)
}

// 恢复答题进度
export function restoreExamProgress(examRecordId: number) {
  return request.get<any, ExamRecord>(`/student/exams/records/${examRecordId}/restore`)
}

// 提交试卷
export function submitExam(examRecordId: number, data?: any) {
  return request.post(`/student/exams/records/${examRecordId}/submit`, data)
}

// ========== 成绩相关 ==========

// 获取成绩列表
export function getGrades(params?: { courseId?: number; page?: number; pageSize?: number }) {
  return request.get<any, any>('/student/grades', { params })
}

// 获取成绩详情
export function getGradeDetail(gradeId: number) {
  return request.get<any, Grade>(`/student/grades/${gradeId}`)
}

// 申请成绩复核
export function applyGradeReview(gradeId: number, data: { questionId?: number; reason: string }) {
  return request.post(`/student/grades/${gradeId}/review`, data)
}

// ========== 错题本相关 ==========

// 获取错题列表
export function getWrongQuestions(params?: {
  sourceType?: string
  courseId?: number | string
  examId?: number | string
  knowledgePoint?: string
  page?: number
  pageSize?: number
}) {
  return request.get<any, any>('/student/wrongQuestions', { params })
}

// 获取错题详情
export function getWrongQuestionDetail(id: number) {
  return request.get<any, WrongQuestion>(`/student/wrongQuestions/${id}`)
}

// 更新解题思路备注
export function updateWrongQuestionSolution(id: number, mySolution: string) {
  return request.put(`/student/wrongQuestions/${id}/solution`, { mySolution })
}

// 收藏/取消收藏错题
export function toggleFavoriteWrongQuestion(id: number, isFavorite: boolean) {
  if (isFavorite) {
    return request.post(`/student/wrongQuestions/${id}/favorite`)
  } else {
    return request.delete(`/student/wrongQuestions/${id}/favorite`)
  }
}

// 删除错题
export function deleteWrongQuestion(id: number) {
  return request.delete(`/student/wrongQuestions/${id}`)
}

// 错题重练
export function practiceWrongQuestion(id: number, answer: string) {
  return request.post(`/student/wrongQuestions/${id}/practice`, { answer })
}

// 获取错题报告
export function getWrongQuestionReport(params?: { courseId?: number; examId?: number }) {
  return request.get<any, any>('/student/wrongQuestions/report', { params })
}

// ========== 学生题库相关 ==========

// 获取题库列表
export function getQuestionBanks() {
  return request.get<any, StudentQuestionBank[]>('/student/questionBanks')
}

// 创建题库
export function createQuestionBank(data: { name: string; description?: string }) {
  return request.post<any, any>('/student/questionBanks', data)
}

// 编辑题库
export function updateQuestionBank(id: number, data: { name?: string; description?: string }) {
  return request.put(`/student/questionBanks/${id}`, data)
}

// 删除题库
export function deleteQuestionBank(id: number) {
  return request.delete(`/student/questionBanks/${id}`)
}

// 分享题库
export function shareQuestionBank(bankId: number, expireTime?: string) {
  return request.post(`/student/questionBanks/${bankId}/share`, { expireTime })
}

// 加入分享的题库
export function joinSharedBank(shareCode: string) {
  return request.post<any, any>('/student/questionBanks/join', { shareCode })
}

// ========== 通知相关 ==========

// 获取通知列表
export function getNotifications(params?: {
  type?: string
  isRead?: number | undefined
  page?: number
  pageSize?: number
}) {
  return request.get<any, any>('/notifications', { params })
}

// 标记通知已读
export function markNotificationRead(id: number) {
  return request.put(`/notifications/${id}/read`)
}

// 标记全部已读
export function markAllNotificationsRead() {
  return request.put('/notifications/readAll')
}

// 删除通知
export function deleteNotification(id: number) {
  return request.delete(`/notifications/${id}`)
}

// ========== AI 错题辅导 ==========

export function getWrongQuestionAiSessions(wrongQuestionId: number) {
  return request.get<any, any[]>(`/student/ai/wrongQuestions/${wrongQuestionId}/sessions`)
}

export function createWrongQuestionAiSession(wrongQuestionId: number, data?: { title?: string }) {
  return request.post<any, any>(`/student/ai/wrongQuestions/${wrongQuestionId}/sessions`, data || {})
}

export function getAiSessionDetail(sessionId: number) {
  return request.get<any, any>(`/student/ai/sessions/${sessionId}`)
}

export function deleteAiSession(sessionId: number) {
  return request.delete<any, any>(`/student/ai/sessions/${sessionId}`)
}

export function addAiSessionMessage(sessionId: number, data: { role: 'user' | 'assistant'; content: string }) {
  return request.post<any, any>(`/student/ai/sessions/${sessionId}/messages`, data)
}

export function askWrongQuestionAi(wrongQuestionId: number, data: {
  sessionId?: number
  question: string
  history?: Array<{ role: string; content: string }>
  relatedWrongQuestions?: any[]
}) {
  return request.post<any, any>(`/student/ai/wrongQuestions/${wrongQuestionId}/ask`, data)
}

export async function askWrongQuestionAiStream(
  wrongQuestionId: number,
  data: { sessionId?: number; question: string; history?: Array<{ role: string; content: string }>; relatedWrongQuestions?: any[] },
  onMessage: (payload: any) => void,
  signal?: AbortSignal
) {
  const token = localStorage.getItem('token')
  const userId = localStorage.getItem('userId')
  const response = await fetch(`/api/v1/student/ai/wrongQuestions/${wrongQuestionId}/ask/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(userId ? { 'X-User-Id': userId } : {}),
    },
    body: JSON.stringify(data),
    signal,
  })
  if (!response.ok || !response.body) {
    throw new Error('AI 服务连接失败')
  }
  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })
    const chunks = buffer.split('\n\n')
    buffer = chunks.pop() || ''
    for (const chunk of chunks) {
      handleSseChunk(chunk, onMessage)
    }
  }
  if (buffer.trim()) handleSseChunk(buffer, onMessage)
}

function handleSseChunk(chunk: string, onMessage: (payload: any) => void) {
  const dataLines = chunk
    .split('\n')
    .filter(line => line.trim().startsWith('data:'))
    .map(line => line.replace(/^data:\s?/, ''))
  const raw = dataLines.length > 0 ? dataLines.join('\n') : chunk.trim()
  if (!raw) return
  try {
    onMessage(JSON.parse(raw))
  } catch {
    onMessage({ type: 'token', content: raw })
  }
}
