<template>
  <aside class="bg-white rounded-xl shadow-sm border border-blue-100 flex flex-col min-h-[720px] max-h-[calc(100vh-48px)] overflow-hidden">
    <div class="p-4 border-b border-gray-100 flex items-start justify-between gap-3">
      <div>
        <h3 class="text-base font-semibold text-gray-800 flex items-center gap-2">
          <span class="w-8 h-8 rounded-lg bg-primary-500 text-white flex items-center justify-center">
            <i class="ri-robot-2-line"></i>
          </span>
          AI错题辅导
        </h3>
        <p class="text-xs text-gray-400 mt-1">围绕当前错题讲解、追问和生成类似题</p>
      </div>
      <el-button size="small" @click="openSessionManager" :disabled="!activeWrongQuestion">
        <i class="ri-history-line mr-1"></i>会话
      </el-button>
    </div>

    <div v-if="wrongQuestion" class="m-4 p-3 rounded-lg bg-primary-50 border border-blue-100">
      <div class="flex items-center justify-between gap-2 mb-2">
        <span class="text-xs font-semibold text-primary-700 flex items-center gap-1">
          <i class="ri-link"></i>已绑定当前错题
        </span>
        <span class="px-2 py-0.5 bg-white text-primary-600 text-xs rounded">{{ wrongQuestion.knowledgePoint || wrongQuestion.knowledgePoints || '知识点待识别' }}</span>
      </div>
      <p class="text-xs text-gray-700 leading-relaxed">{{ wrongQuestion.content }}</p>
      <div v-if="wrongQuestion.options?.length" class="mt-2 grid grid-cols-1 gap-1.5">
        <div
          v-for="option in wrongQuestion.options"
          :key="option.label"
          :class="[
            'text-xs rounded-md px-2 py-1 border transition-colors',
            option.label === wrongQuestion.correctAnswer ? 'border-success-500 bg-success-500 text-white' : '',
            option.label === (wrongQuestion.myAnswer || wrongQuestion.studentAnswer) ? 'border-danger-500 bg-danger-500 text-white' : '',
            option.label !== wrongQuestion.correctAnswer && option.label !== (wrongQuestion.myAnswer || wrongQuestion.studentAnswer) ? 'border-blue-100 bg-white/80 text-gray-600' : ''
          ]"
        >
          <span class="font-semibold mr-1">{{ option.label }}.</span>{{ option.content }}
        </div>
      </div>
      <div class="mt-2 text-xs text-gray-500 flex flex-wrap gap-x-3 gap-y-1">
        <span>我的答案：<span class="text-danger-500">{{ wrongQuestion.myAnswer || wrongQuestion.studentAnswer || '(未答)' }}</span></span>
        <span>正确答案：<span class="text-success-600">{{ wrongQuestion.correctAnswer || '待查看' }}</span></span>
      </div>
      <div v-if="wrongQuestion.analysis" class="mt-2 text-xs text-gray-500 leading-relaxed bg-white/70 rounded-md px-2 py-1">
        解析：{{ wrongQuestion.analysis }}
      </div>
    </div>

    <div v-if="relatedWrongQuestions.length > 0" class="mx-4 mb-3 p-3 rounded-lg bg-amber-50 border border-amber-100">
      <div class="flex items-center justify-between mb-2">
        <span class="text-xs font-semibold text-amber-700 flex items-center gap-1">
          <i class="ri-links-line"></i>已加入对话：{{ relatedWrongQuestions.length }} 道错题
        </span>
        <button class="text-xs text-amber-600 hover:text-amber-700" @click="$emit('clearRelatedQuestions')">清空</button>
      </div>
      <div class="space-y-1.5">
        <div
          v-for="item in relatedWrongQuestions"
          :key="item.wrongQuestionId"
          class="flex items-center gap-2 text-xs text-gray-600 bg-white/70 rounded px-2 py-1"
        >
          <span class="shrink-0 text-amber-600 font-semibold">#{{ relatedWrongQuestions.indexOf(item) + 1 }}</span>
          <span class="flex-1 truncate">{{ item.content }}</span>
          <span class="shrink-0 max-w-[96px] truncate text-amber-600">{{ item.knowledgePoint || item.knowledgePoints || '知识点待识别' }}</span>
          <button class="text-gray-400 hover:text-danger-500" @click="$emit('removeRelatedQuestion', item.wrongQuestionId)">
            <i class="ri-close-line"></i>
          </button>
        </div>
      </div>
    </div>

    <div v-if="hasAiContext" class="px-4 pb-3 flex flex-wrap gap-2">
      <button v-for="item in quickPrompts" :key="item" class="px-2.5 py-1.5 text-xs rounded-lg border border-blue-100 bg-white text-primary-600 hover:bg-primary-50 transition-colors" @click="sendQuickPrompt(item)">
        {{ item }}
      </button>
    </div>

    <div class="flex-1 min-h-0 overflow-auto px-4 pb-3 space-y-3">
      <div v-if="!hasAiContext" class="h-full flex flex-col items-center justify-center text-gray-400 text-center">
        <i class="ri-chat-smile-3-line text-4xl mb-3"></i>
        <p class="text-sm font-medium">请选择一道错题</p>
        <p class="text-xs mt-1">AI会自动读取题干、答案和解析</p>
      </div>
      <template v-else>
        <div v-for="(message, index) in messages" :key="index" :class="['flex', message.role === 'user' ? 'justify-end' : 'justify-start']">
          <div
            :class="[
              'max-w-[88%] rounded-lg px-3 py-2 text-sm leading-relaxed',
              message.role === 'user' ? 'bg-primary-500 text-white' : 'bg-gray-50 text-gray-700 border border-gray-100',
              message.role === 'assistant' ? 'ai-markdown' : ''
            ]"
            v-html="message.role === 'assistant' ? renderMarkdown(message.content) : escapeHtml(message.content)"
          ></div>
        </div>
        <div v-if="streaming && waitingForFirstToken" class="flex justify-start">
          <div class="rounded-lg px-3 py-2 bg-gray-50 text-gray-500 border border-gray-100 flex items-center gap-2 text-sm">
            <span class="inline-flex gap-1">
              <span class="w-1.5 h-1.5 rounded-full bg-primary-400 animate-bounce"></span>
              <span class="w-1.5 h-1.5 rounded-full bg-primary-400 animate-bounce [animation-delay:120ms]"></span>
              <span class="w-1.5 h-1.5 rounded-full bg-primary-400 animate-bounce [animation-delay:240ms]"></span>
            </span>
            AI 正在思考...
          </div>
        </div>
        <div v-if="messages.length === 0" class="text-sm text-gray-400 bg-gray-50 rounded-lg p-4">
          可以先问：“这道题我错在哪里？”或点击上方快捷追问。
        </div>
      </template>
    </div>

    <div class="border-t border-gray-100 p-3">
      <div class="border border-gray-200 rounded-lg p-2 bg-white">
        <textarea
          ref="composerRef"
          v-model="input"
          rows="3"
          :disabled="!hasAiContext || streaming"
          :placeholder="relatedWrongQuestions.length > 0 ? '围绕已加入的错题继续追问...' : '继续追问这道错题...'"
          class="w-full resize-none outline-none text-sm leading-relaxed disabled:bg-white disabled:text-gray-400"
          @keydown.enter.exact.prevent="sendQuestion()"
        />
        <div class="flex items-center justify-between mt-2">
          <div class="flex gap-2 text-gray-400">
            <button class="w-8 h-8 rounded-lg bg-gray-50 hover:bg-gray-100" title="上传截图">
              <i class="ri-attachment-2"></i>
            </button>
            <button class="w-8 h-8 rounded-lg bg-gray-50 hover:bg-gray-100" title="语音输入">
              <i class="ri-mic-line"></i>
            </button>
          </div>
          <el-button type="primary" size="small" :loading="streaming" :disabled="!hasAiContext || !input.trim()" @click="sendQuestion()">
            <i class="ri-send-plane-2-line mr-1"></i>发送
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="sessionDialogVisible" title="管理AI会话" width="520px">
      <div class="flex items-center justify-between mb-3">
        <span class="text-sm text-gray-500">当前错题的历史对话</span>
        <el-button size="small" type="primary" @click="createNewSession">
          <i class="ri-add-line mr-1"></i>新建对话
        </el-button>
      </div>
      <div v-if="sessionLoading" class="py-8 text-center text-gray-400">加载中...</div>
      <div v-else-if="sessions.length === 0" class="py-8 text-center text-gray-400">暂无会话</div>
      <div v-else class="space-y-2 max-h-[360px] overflow-auto">
        <div
          v-for="item in sessions"
          :key="item.id"
          :class="[
            'p-3 rounded-lg border flex items-start gap-3 cursor-pointer transition-colors',
            sessionId === item.id ? 'border-primary-300 bg-primary-50' : 'border-gray-100 hover:bg-gray-50'
          ]"
          @click="switchSession(item.id)"
        >
          <div class="flex-1 min-w-0">
            <div class="text-sm font-medium text-gray-800 truncate">{{ item.title || '错题辅导' }}</div>
            <div class="text-xs text-gray-400 mt-1 truncate">{{ item.preview || '暂无消息' }}</div>
            <div class="text-xs text-gray-300 mt-1">{{ formatSessionTime(item.updated_at || item.updatedAt || item.created_at) }}</div>
          </div>
          <el-button size="small" type="danger" plain @click.stop="removeSession(item.id)">
            删除
          </el-button>
        </div>
      </div>
    </el-dialog>
  </aside>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { WrongQuestion } from '@/api/types'
import { addAiSessionMessage, askWrongQuestionAi, askWrongQuestionAiStream, createWrongQuestionAiSession, deleteAiSession, getAiSessionDetail, getWrongQuestionAiSessions } from '@/api/student'

type AiWrongQuestion = WrongQuestion & {
  knowledgePoints?: string
  studentAnswer?: string
}

const props = defineProps<{
  wrongQuestion: AiWrongQuestion | null
  relatedWrongQuestions?: AiWrongQuestion[]
}>()

defineEmits<{
  (e: 'removeRelatedQuestion', id: number): void
  (e: 'clearRelatedQuestions'): void
}>()

const quickPrompts = ['错因是什么', '分步讲解', '只给提示', '给我类似题', '总结知识点']
const input = ref('')
const sessionId = ref<number | undefined>()
const messages = ref<Array<{ role: 'user' | 'assistant'; content: string }>>([])
const streaming = ref(false)
const waitingForFirstToken = ref(false)
const relatedWrongQuestions = computed(() => props.relatedWrongQuestions || [])
const activeWrongQuestion = computed(() => props.wrongQuestion || relatedWrongQuestions.value[0] || null)
const hasAiContext = computed(() => Boolean(activeWrongQuestion.value || relatedWrongQuestions.value.length > 0))
const sessionDialogVisible = ref(false)
const sessionLoading = ref(false)
const sessions = ref<any[]>([])
const composerRef = ref<HTMLTextAreaElement | null>(null)

watch(() => props.wrongQuestion?.wrongQuestionId, async () => {
  messages.value = []
  sessionId.value = undefined
  input.value = ''
}, { immediate: true })

async function ensureSession() {
  if (!activeWrongQuestion.value || sessionId.value) return
  try {
    const sessions: any = await getWrongQuestionAiSessions(activeWrongQuestion.value.wrongQuestionId)
    const first = Array.isArray(sessions) ? sessions[0] : sessions?.sessions?.[0]
    if (first?.id) {
      sessionId.value = first.id
      await loadSessionDetail(first.id)
      return
    }
    const created: any = await createWrongQuestionAiSession(activeWrongQuestion.value.wrongQuestionId, { title: '错题辅导' })
    sessionId.value = created?.session_id || created?.sessionId || created?.data?.session_id
  } catch (err) {
    console.error('创建AI会话失败', err)
  }
}

async function openSessionManager() {
  if (!activeWrongQuestion.value) return
  sessionDialogVisible.value = true
  await loadSessionList()
}

async function loadSessionList() {
  if (!activeWrongQuestion.value) return
  sessionLoading.value = true
  try {
    const rows: any = await getWrongQuestionAiSessions(activeWrongQuestion.value.wrongQuestionId)
    sessions.value = Array.isArray(rows) ? rows : (rows?.sessions || rows?.data || [])
  } finally {
    sessionLoading.value = false
  }
}

async function createNewSession() {
  if (!activeWrongQuestion.value) return
  const created: any = await createWrongQuestionAiSession(activeWrongQuestion.value.wrongQuestionId, { title: '错题辅导' })
  sessionId.value = created?.session_id || created?.sessionId || created?.data?.session_id
  messages.value = []
  await loadSessionList()
  ElMessage.success('已新建对话')
}

async function switchSession(id: number) {
  sessionId.value = id
  await loadSessionDetail(id)
  sessionDialogVisible.value = false
}

async function removeSession(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该 AI 对话吗？删除后不可恢复。', '删除对话', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  await deleteAiSession(id)
  if (sessionId.value === id) {
    sessionId.value = undefined
    messages.value = []
  }
  await loadSessionList()
  ElMessage.success('对话已删除')
}

async function loadSessionDetail(id: number) {
  const detail: any = await getAiSessionDetail(id)
  const rows = detail?.messages || detail?.data?.messages || []
  messages.value = rows.map((item: any) => ({ role: item.role, content: item.content }))
}

function sendQuickPrompt(prompt: string) {
  const multiPromptMap: Record<string, string> = {
    错因是什么: '请综合分析这些错题的共性错因，并指出我最需要补的知识点。',
    分步讲解: '请逐题分步讲解这些错题，先讲思路再讲答案。',
    给我类似题: '请基于这些错题的共同知识点，给我 2 道类似练习题。',
  }
  input.value = relatedWrongQuestions.value.length > 1 ? (multiPromptMap[prompt] || prompt) : prompt
  sendQuestion()
}

async function sendQuestion() {
  const questionContext = activeWrongQuestion.value
  if (!questionContext || !input.value.trim() || streaming.value) return
  await ensureSession()
  const question = input.value.trim()
  const questionToSend = buildQuestionWithRelatedContext(question)
  input.value = ''
  messages.value.push({ role: 'user', content: question })
  let assistantIndex = -1
  streaming.value = true
  waitingForFirstToken.value = true
  try {
    await askWrongQuestionAiStream(
      questionContext.wrongQuestionId,
      {
        sessionId: sessionId.value,
        question: questionToSend,
        history: messages.value.slice(0, -2),
        relatedWrongQuestions: relatedWrongQuestions.value.map(toContextQuestion),
      },
      (payload) => {
        if (assistantIndex < 0 && payload.type !== 'error') {
          messages.value.push({ role: 'assistant', content: '' })
          assistantIndex = messages.value.length - 1
        }
        if (payload.type === 'token' && assistantIndex >= 0) {
          waitingForFirstToken.value = false
          messages.value[assistantIndex].content += payload.content || ''
        }
        if (payload.type === 'done' && payload.content && assistantIndex >= 0) {
          waitingForFirstToken.value = false
          messages.value[assistantIndex].content = payload.content
        }
        if (payload.type === 'error') {
          waitingForFirstToken.value = false
          messages.value.push({ role: 'assistant', content: `请求失败：${payload.content || 'AI服务异常'}` })
          assistantIndex = messages.value.length - 1
        }
      }
    )
    if (assistantIndex < 0 || !messages.value[assistantIndex].content.trim()) {
      const normalAnswer = await askWrongQuestionAi(questionContext.wrongQuestionId, {
        sessionId: sessionId.value,
        question: questionToSend,
        history: messages.value.filter(item => item.content.trim()),
        relatedWrongQuestions: relatedWrongQuestions.value.map(toContextQuestion),
      } as any)
      const answer = normalAnswer?.answer || normalAnswer?.data?.answer || 'AI 暂未返回内容，请稍后重试。'
      if (assistantIndex >= 0) {
        messages.value[assistantIndex].content = answer
      } else {
        messages.value.push({ role: 'assistant', content: answer })
        assistantIndex = messages.value.length - 1
      }
      waitingForFirstToken.value = false
    }
  } catch (err: any) {
    waitingForFirstToken.value = false
    messages.value.push({ role: 'assistant', content: `请求失败：${err?.message || 'AI服务异常'}` })
    assistantIndex = messages.value.length - 1
    ElMessage.error(err?.message || 'AI服务异常，请稍后重试')
  } finally {
    const assistantContent = assistantIndex >= 0 ? messages.value[assistantIndex]?.content || '' : ''
    if (sessionId.value && assistantContent.trim()) {
      await addAiSessionMessage(sessionId.value, { role: 'user', content: question })
      await addAiSessionMessage(sessionId.value, { role: 'assistant', content: assistantContent })
    }
    streaming.value = false
    waitingForFirstToken.value = false
  }
}

function focusComposer() {
  composerRef.value?.focus()
}

defineExpose({ focusComposer })

function toContextQuestion(item: AiWrongQuestion) {
  return {
    wrongQuestionId: item.wrongQuestionId,
    questionId: item.questionId,
    content: item.content,
    studentAnswer: item.myAnswer || item.studentAnswer,
    correctAnswer: item.correctAnswer,
    analysis: item.analysis,
    knowledgePoints: item.knowledgePoint || item.knowledgePoints,
    wrongCount: item.wrongCount || item.wrongTimes,
  }
}

function buildQuestionWithRelatedContext(question: string) {
  if (relatedWrongQuestions.value.length === 0) return question
  const list = relatedWrongQuestions.value
    .map((item, index) => {
      const context = toContextQuestion(item)
      return `错题${index + 1}：${context.content}\n我的答案：${context.studentAnswer || '(未答)'}\n正确答案：${context.correctAnswer || '待查看'}\n知识点：${context.knowledgePoints || '待识别'}`
    })
    .join('\n\n')
  return `请综合以下 ${relatedWrongQuestions.value.length} 道错题进行辅导。\n\n${list}\n\n我的问题：${question}`
}

function escapeHtml(value: string) {
  return (value || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function renderMarkdown(value: string) {
  const lines = (value || '').replace(/\r\n/g, '\n').split('\n')
  const blocks: string[] = []
  let index = 0

  while (index < lines.length) {
    const line = lines[index]
    if (!line.trim()) {
      index++
      continue
    }
    if (/^```/.test(line.trim())) {
      const codeLines: string[] = []
      index++
      while (index < lines.length && !/^```/.test(lines[index].trim())) {
        codeLines.push(lines[index])
        index++
      }
      index++
      blocks.push(`<pre class="my-2 p-3 rounded-lg bg-gray-900 text-gray-100 overflow-auto text-xs"><code>${escapeHtml(codeLines.join('\n'))}</code></pre>`)
      continue
    }
    if (isBlockMathStart(line)) {
      const mathLines: string[] = []
      mathLines.push(stripBlockMathMarker(line, true))
      index++
      while (index < lines.length && !isBlockMathEnd(lines[index])) {
        mathLines.push(lines[index])
        index++
      }
      if (index < lines.length) {
        mathLines.push(stripBlockMathMarker(lines[index], false))
        index++
      }
      blocks.push(renderMathBlock(mathLines.filter(Boolean).join('\n')))
      continue
    }
    if (isTableLine(line) && index + 1 < lines.length && isDividerLine(lines[index + 1])) {
      const headers = splitTableRow(line).map(renderInlineMarkdown)
      index += 2
      const rows: string[][] = []
      while (index < lines.length && isTableLine(lines[index])) {
        rows.push(splitTableRow(lines[index]).map(renderInlineMarkdown))
        index++
      }
      blocks.push(buildTableHtml(headers, rows))
      continue
    }
    if (/^#{1,3}\s+/.test(line)) {
      const level = Math.min(line.match(/^#+/)?.[0].length || 3, 3)
      const content = line.replace(/^#{1,3}\s+/, '')
      blocks.push(`<h${level} class="font-semibold text-gray-800 mt-3 mb-1">${renderInlineMarkdown(content)}</h${level}>`)
      index++
      continue
    }
    if (/^---+$/.test(line.trim())) {
      blocks.push('<hr class="my-3 border-gray-200">')
      index++
      continue
    }
    if (/^\s*[-*]\s+/.test(line)) {
      const items: string[] = []
      while (index < lines.length && /^\s*[-*]\s+/.test(lines[index])) {
        items.push(`<li>${renderInlineMarkdown(lines[index].replace(/^\s*[-*]\s+/, ''))}</li>`)
        index++
      }
      blocks.push(`<ul class="my-2 pl-5 list-disc space-y-1">${items.join('')}</ul>`)
      continue
    }
    if (/^\s*\d+[.)]\s+/.test(line)) {
      const items: string[] = []
      while (index < lines.length && /^\s*\d+[.)]\s+/.test(lines[index])) {
        items.push(`<li>${renderInlineMarkdown(lines[index].replace(/^\s*\d+[.)]\s+/, ''))}</li>`)
        index++
      }
      blocks.push(`<ol class="my-2 pl-5 list-decimal space-y-1">${items.join('')}</ol>`)
      continue
    }

    const paragraphLines: string[] = []
    while (
      index < lines.length &&
      lines[index].trim() &&
      !/^```/.test(lines[index].trim()) &&
      !isBlockMathStart(lines[index]) &&
      !(isTableLine(lines[index]) && index + 1 < lines.length && isDividerLine(lines[index + 1])) &&
      !/^#{1,3}\s+/.test(lines[index]) &&
      !/^---+$/.test(lines[index].trim()) &&
      !/^\s*[-*]\s+/.test(lines[index]) &&
      !/^\s*\d+[.)]\s+/.test(lines[index])
    ) {
      paragraphLines.push(lines[index])
      index++
    }
    blocks.push(`<p class="my-2">${paragraphLines.map(renderInlineMarkdown).join('<br>')}</p>`)
  }
  return blocks.join('')
}

function renderInlineMarkdown(value: string) {
  return renderInlineMath(escapeHtml(value))
    .replace(/\*\*(.+?)\*\*/g, '<strong class="font-semibold text-gray-800">$1</strong>')
    .replace(/\*([^*]+)\*/g, '<em>$1</em>')
    .replace(/`([^`]+)`/g, '<code class="px-1 py-0.5 rounded bg-gray-100 text-primary-700 text-xs">$1</code>')
}

function renderInlineMath(html: string) {
  return html
    .replace(/\\\((.+?)\\\)/g, '<span class="math-inline">$1</span>')
    .replace(/\$(.+?)\$/g, '<span class="math-inline">$1</span>')
}

function isBlockMathStart(line: string) {
  const trimmed = line.trim()
  return trimmed.startsWith('$$') || trimmed.startsWith('\\[')
}

function isBlockMathEnd(line: string) {
  const trimmed = line.trim()
  return trimmed.endsWith('$$') || trimmed.endsWith('\\]')
}

function stripBlockMathMarker(line: string, start: boolean) {
  const trimmed = line.trim()
  if (start) return trimmed.replace(/^(\$\$|\\\[)/, '').replace(/(\$\$|\\\])$/, '').trim()
  return trimmed.replace(/(\$\$|\\\])$/, '').trim()
}

function renderMathBlock(value: string) {
  return `<div class="math-block">${formatMathText(escapeHtml(value))}</div>`
}

function formatMathText(value: string) {
  return value
    .replace(/\\underline\{\\qquad\}/g, '<span class="math-blank"></span>')
    .replace(/\\qquad/g, '<span class="inline-block w-8"></span>')
    .replace(/\\times/g, '×')
    .replace(/\\div/g, '÷')
    .replace(/\\cdot/g, '·')
    .replace(/\\le/g, '≤')
    .replace(/\\ge/g, '≥')
    .replace(/\\ne/g, '≠')
    .replace(/\\sqrt\{([^}]+)\}/g, '√($1)')
    .replace(/\^(\{([^}]+)\}|(\S))/g, '<sup>$2$3</sup>')
    .replace(/_(\{([^}]+)\}|(\S))/g, '<sub>$2$3</sub>')
    .replace(/\n/g, '<br>')
}

function isTableLine(line: string) {
  return /^\s*\|.+\|\s*$/.test(line)
}

function isDividerLine(line: string) {
  return /^\s*\|?[\s:-]*---+[\s|:-]*\|?\s*$/.test(line)
}

function splitTableRow(line: string) {
  return line.trim().replace(/^\|/, '').replace(/\|$/, '').split('|').map(item => item.trim())
}

function buildTableHtml(headers: string[], rows: string[][]) {
  const head = headers.map(item => `<th class="border border-gray-200 bg-gray-50 px-2 py-1 text-left font-semibold">${item}</th>`).join('')
  const body = rows.map(row => `<tr>${row.map(item => `<td class="border border-gray-200 px-2 py-1 align-top">${item}</td>`).join('')}</tr>`).join('')
  return `<div class="my-2 overflow-auto"><table class="min-w-full text-xs border-collapse"><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table></div>`
}

function formatSessionTime(value?: string) {
  return value ? String(value).replace('T', ' ').slice(0, 19) : ''
}
</script>

<style scoped>
.ai-markdown :deep(strong) {
  font-weight: 700;
}

.ai-markdown :deep(table) {
  line-height: 1.6;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.ai-markdown :deep(pre) {
  white-space: pre-wrap;
}

.ai-markdown :deep(h1),
.ai-markdown :deep(h2),
.ai-markdown :deep(h3) {
  line-height: 1.5;
}

.ai-markdown :deep(p:first-child) {
  margin-top: 0;
}

.ai-markdown :deep(p:last-child) {
  margin-bottom: 0;
}

.ai-markdown :deep(.math-inline) {
  display: inline-flex;
  align-items: center;
  min-height: 1.35em;
  padding: 0 0.25rem;
  margin: 0 0.08rem;
  border-radius: 0.35rem;
  background: #eef6ff;
  color: #1d4ed8;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.ai-markdown :deep(.math-block) {
  margin: 0.6rem 0;
  padding: 0.75rem 0.9rem;
  border-radius: 0.75rem;
  border: 1px solid #bfdbfe;
  background: linear-gradient(135deg, #f8fbff 0%, #eff6ff 100%);
  color: #1f2937;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  overflow-x: auto;
}

.ai-markdown :deep(.math-blank) {
  display: inline-block;
  width: 3.5rem;
  height: 1em;
  margin: 0 0.2rem;
  border-bottom: 2px solid #2563eb;
  vertical-align: -0.1em;
}
</style>
