// API 响应基础结构
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应
export interface PaginatedResponse<T> {
  list: T[]
  pagination: {
    page: number
    pageSize: number
    total: number
  }
}

// 用户信息
export interface UserInfo {
  id: number
  username: string
  realName: string
  role: 'admin' | 'teacher' | 'student'
  email?: string
  phone?: string
  avatar?: string
  status: number
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
  captcha?: string
}

// 登录响应
export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

// 课程信息
export interface Course {
  id: number
  name: string
  code: string
  teacherName: string
  credits: number
  hours: number
  description?: string
  studentCount?: number
    examCount?: number
    pendingExamCount?: number
    wrongQuestionCount?: number
    ongoingExamCount?: number
  completedExamCount?: number
  progress: number
  completedLessons: number
  totalLessons: number
  courseCode?: string
}

// 考试信息
export interface Exam {
  id: number
    examName: string
    courseName: string
    courseId: number
    gradeId?: number
    examStatus?: string
    submitStatus?: string
    startTime: string
  endTime: string
  duration: number
  totalScore: number
  passScore?: number
  questionCount?: number
  status: 'pending' | 'ongoing' | 'finished' | 'completed'
  examType: 'formal' | 'practice'
  myScore?: number
  avgScore?: number
  maxScore?: number
  rank?: number
  totalStudents?: number
  completedAt?: string
}

// 题目类型
type QuestionType = 'single_choice' | 'multi_choice' | 'true_false' | 'fill_blank' | 'essay'

// 题目选项
export interface QuestionOption {
  label: string
  content: string
  isCorrect?: boolean
}

// 题目信息
export interface Question {
  id: number
  type: QuestionType
  typeName: string
  content: string
  options?: QuestionOption[]
  answer: string | string[]
  analysis?: string
  score: number
  fullScore?: number
  sortOrder?: number
  isMarked?: boolean
  myAnswer?: string | string[] | undefined
  studentAnswer?: string
  correctAnswer?: string
  isCorrect?: boolean
}

// 考试记录（用于答题）
export interface ExamRecord {
    examRecordId: number
    examId: number
    examName?: string
    questions: Question[]
  totalQuestions: number
    duration: number
    endTime: string
    remainSeconds?: number
    source: 'exam' | 'practice' | 'review'
}

// 成绩信息
  export interface Grade {
    gradeId: number
    examId: number
    courseId?: number
    examName: string
  courseName: string
  studentId: number
  studentName: string
  className?: string
  totalScore: number
  objectiveScore?: number
  subjectiveScore?: number
  rank?: number
  percentile?: number
  status: 'pending' | 'published' | 'reviewed'
  publishTime?: string
  correctCount?: number
  wrongCount?: number
  unansweredCount?: number
}

// 错题信息
export interface WrongQuestion {
  wrongQuestionId: number
  questionId: number
  courseId: number
  courseName: string
  examId: number
  examName: string
  sourceType: 'exam' | 'practice'
  sourceTypeName: string
  questionType: string
  type: QuestionType
  content: string
  myAnswer: string
  correctAnswer: string
  wrongTimes: number
  lastWrongTime: string
  practiceCount: number
  correctCount: number
  mySolution?: string
  isFavorited: boolean
  knowledgePoint?: string
  knowledgePoints?: string
  studentAnswer?: string
  analysis?: string
  wrongCount?: number
  options?: Array<{ label: string; content: string; isCorrect?: boolean }>
}

// 自建题库
export interface StudentQuestionBank {
  id: number
  bankName: string
  description?: string
  courseName?: string
  questionCount: number
  createdAt: string
  shareCode?: string
  shareExpiry?: string
  isShared: boolean
  source: 'original' | 'imported' | 'shared'
}

// 通知信息
export interface Notification {
  id: number
  type: 'exam' | 'grade' | 'review' | 'system'
  title: string
  content: string
  relatedId?: number
  isRead: boolean
  readTime?: string
  createTime: string
  time?: string
}

// 知识点掌握度
export interface KnowledgePoint {
  name: string
  mastery: number
}

// ====== 教师端类型定义 ======

// 教师信息
export interface TeacherInfo {
  id: number
  userId: number
  name: string
  title: string       // 职称
  researchDirection?: string
  avatar?: string
}

// 教师课程（扩展Course）
export interface TeacherCourse extends Course {
  code: string          // 课程码
  semester: string      // 学期
  credit: number        // 学分
  shareCode?: string    // 分享码
  studentCount: number
  examCount: number
}

// 题目详情（扩展Question）
export interface QuestionDetail extends Question {
  teacherId: number
  teacherName: string
  usageCount: number
  source: 'original' | 'shared'
  isFavorited: boolean
  options: QuestionOption[]
}

// 试卷信息
export interface ExamPaper {
  id: number
  name: string
  courseId: number
  courseName: string
  teacherId: number
  totalScore: number
  passScore: number
  duration: number
  questionCount: number
  type: 'manual' | 'random' | 'template'
  status: 0 | 1         // 0-草稿，1-已发布
  createTime: string
  questions?: ExamPaperQuestion[]
}

// 试卷题目关联
export interface ExamPaperQuestion {
  id: number
  examPaperId: number
  questionId: number
  score: number
  sortOrder: number
  question?: QuestionDetail
}

// 考试安排
export interface ExamSchedule extends Exam {
  allowTimes: number
  isRandomOrder: boolean
  isRandomOptions: boolean
  studentCount: number
  submitCount: number
  answeringCount: number
}

// 考试监控学生状态
export interface MonitorStudent {
  studentId: number
  studentName: string
  status: 'pending' | 'ongoing' | 'submitted' | 'expired'
  startTime?: string
  answeredCount: number
  totalQuestions: number
  switchScreenCount: number
  lastActiveTime: string
}

// 批改任务
export interface GradingTask {
  id: number
  examRecordId: number
  examId: number
  examName: string
  questionId: number
  questionType: string
  questionContent: string
  studentId: number
  studentName: string
  score?: number
  fullScore: number
  comment?: string
  status: 'pending' | 'grading' | 'completed'
  studentAnswer?: string
  correctAnswer?: string
}

// 成绩复核申请
export interface GradeReview {
  id: number
  gradeId: number
  examId: number
  examName: string
  studentId: number
  studentName: string
  questionId?: number
  reason: string
  studentComment?: string
  status: 'pending' | 'approved' | 'rejected' | 'resolved'
  originalScore?: number
  newScore?: number
  teacherComment?: string
  createTime: string
}

// 成绩统计
export interface GradeStatistics {
  examId: number
  examName: string
  totalStudents: number
  submittedCount: number
  avgScore: number
  maxScore: number
  minScore: number
  passCount: number
  passRate: number
  scoreDistribution: Record<string, number>
  classComparison: Array<{ className: string; avgScore: number; passRate: number }>
}

// 选课学生
export interface CourseStudent {
  id: number
  studentId: number
  username: string
  realName: string
  className?: string
  selectTime: string
  status: 0 | 1   // 0-已退课，1-在读
}
