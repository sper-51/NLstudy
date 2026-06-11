<template>
  <div class="space-y-6">
    <!-- 页面标题栏 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-xl font-bold text-gray-800 flex items-center gap-2">
          <i class="ri-team-line text-primary-500"></i>班级管理
        </h1>
        <p class="text-sm text-gray-500 mt-1">管理全校班级信息，支持学生班级分配</p>
      </div>
      <el-button type="primary" @click="openCreateDialog" class="cursor-pointer">
        <i class="ri-add-line mr-1"></i>添加班级
      </el-button>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="bg-white rounded-xl shadow-sm p-4 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <i class="ri-search-line absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索班级名称..."
          class="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:border-primary-400 focus:ring-2 focus:ring-primary-50 outline-none transition-all"
          @keyup.enter="handleSearch"
        />
      </div>
      <el-select v-model="filterGrade" placeholder="全部年级" clearable class="w-[140px] cursor-pointer">
        <el-option label="2024级" value="2024级" />
        <el-option label="2023级" value="2023级" />
        <el-option label="2022级" value="2022级" />
        <el-option label="2021级" value="2021级" />
      </el-select>
      <button @click="handleSearch" class="px-4 py-2 bg-primary-50 text-primary-600 rounded-lg text-sm font-medium hover:bg-primary-100 transition-colors cursor-pointer">
        <i class="ri-search-line mr-1"></i>搜索
      </button>
      <button @click="resetFilters" class="px-4 py-2 bg-gray-50 text-gray-600 rounded-lg text-sm hover:bg-gray-100 transition-colors cursor-pointer">
        重置
      </button>
    </div>

    <!-- 班级列表表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <el-table :data="classList" stripe style="width: 100%" v-loading="tableLoading" header-cell-class-name="bg-gray-50">
        <el-table-column prop="id" label="ID" width="70" align="center">
          <template #default="{ row }">{{ row.id }}</template>
        </el-table-column>
        <el-table-column prop="name" label="班级名称" min-width="160">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-primary-50 flex items-center justify-center flex-shrink-0">
                <i class="ri-team-line text-primary-500 text-sm"></i>
              </div>
              <span class="font-medium text-gray-800">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="年级" width="100" align="center">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.grade }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentCount" label="学生人数" width="110" align="center" sortable>
          <template #default="{ row }">
            <el-tag size="small" :type="row.studentCount > 0 ? 'primary' : 'info'" round effect="plain">
              {{ row.studentCount }} 人
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="text-gray-500 text-sm">{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" sortable>
          <template #default="{ row }">
            <span class="text-gray-500 text-sm">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-1.5">
              <el-button size="small" type="primary" link @click="openEditDialog(row)" class="cursor-pointer">
                <i class="ri-edit-line mr-0.5"></i>编辑
              </el-button>
              <el-button size="small" type="warning" link @click="openAssignDialog(row)" class="cursor-pointer">
                <i class="ri-user-add-line mr-0.5"></i>分配学生
              </el-button>
              <el-popconfirm title="确定删除该班级吗？删除后不可恢复" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button size="small" type="danger" link class="cursor-pointer">
                    <i class="ri-delete-bin-line mr-0.5"></i>删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>

        <!-- 空状态 -->
        <template #empty>
          <div class="py-12 text-center">
            <i class="ri-folder-open-line text-4xl text-gray-300 mb-2 block"></i>
            <p class="text-gray-400 text-sm">暂无班级数据</p>
            <el-button type="primary" size="small" class="mt-3 cursor-pointer" @click="openCreateDialog">添加第一个班级</el-button>
          </div>
        </template>
      </el-table>

      <!-- 分页 -->
      <div class="flex items-center justify-between px-5 py-4 border-t border-gray-100">
        <span class="text-sm text-gray-500">共 {{ total }} 个班级</span>
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="sizes, prev, pager, next, jumper"
          background
          small
        />
      </div>
    </div>

    <!-- 新建/编辑班级对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑班级' : '添加班级'"
      width="520px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px" class="mt-4">
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入班级名称，如：计算机1班" maxlength="30" show-word-limit />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择年级" class="w-full cursor-pointer">
            <el-option label="2024级" value="2024级" />
            <el-option label="2023级" value="2023级" />
            <el-option label="2022级" value="2022级" />
            <el-option label="2021级" value="2021级" />
            <el-option label="2020级" value="2020级" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="可选，填写班级备注信息" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit" class="cursor-pointer">
          {{ isEditing ? '保存修改' : '确认添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配学生对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配学生到班级" width="640px" destroy-on-close>
      <div class="mb-4 p-3 bg-blue-50 rounded-lg flex items-start gap-2">
        <i class="ri-information-line text-primary-500 mt-0.5 flex-shrink-0"></i>
        <p class="text-xs text-gray-600 leading-relaxed">
          当前选择：<strong>{{ currentAssignClass?.name }}</strong>（{{ currentAssignClass?.grade }}），
          已有 <strong class="text-primary-600">{{ currentAssignClass?.studentCount || 0 }}</strong> 名学生。
          勾选下方学生后点击确认完成分配。
        </p>
      </div>

      <!-- 学生搜索 + 已选数量 -->
      <div class="flex items-center gap-3 mb-4">
        <div class="relative flex-1">
          <i class="ri-search-line absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          <input
            v-model="assignSearchKeyword"
            type="text"
            placeholder="搜索学生姓名或学号..."
            class="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-lg text-sm focus:border-primary-400 outline-none"
          />
        </div>
        <el-tag type="primary" round effect="light" size="large">
          已选 <strong>{{ selectedStudentIds.length }}</strong> 人
        </el-tag>
      </div>

      <!-- 学生列表（checkbox多选） -->
      <div class="border border-gray-200 rounded-lg max-h-[360px] overflow-y-auto">
        <el-checkbox-group v-model="selectedStudentIds">
          <div
            v-for="student in filteredStudentsForAssign"
            :key="student.id"
            class="flex items-center px-4 py-3 hover:bg-gray-50 border-b border-gray-100 last:border-b-0 transition-colors"
          >
            <el-checkbox :value="student.id" class="mr-3 cursor-pointer" />
            <el-avatar :size="32" class="bg-primary-50 text-primary-600 text-xs font-semibold mr-3 flex-shrink-0">
              {{ student.realName.charAt(0) }}
            </el-avatar>
            <div class="flex-1 min-w-0">
              <div class="text-sm font-medium text-gray-800">{{ student.realName }}</div>
              <div class="text-xs text-gray-400">{{ student.username }} · {{ student.className || '未分配' }}</div>
            </div>
            <el-tag v-if="student.currentClassId === currentAssignClass?.id" size="small" type="success" round effect="plain">已在班内</el-tag>
          </div>
        </el-checkbox-group>
      </div>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignLoading" @click="handleAssignSubmit" :disabled="selectedStudentIds.length === 0" class="cursor-pointer">
          确认分配（{{ selectedStudentIds.length }}人）
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

// ==================== 数据类型定义 ====================
interface ClassItem {
  id: number
  name: string
  grade: string
  description: string
  studentCount: number
  createTime: string
}

interface StudentItem {
  id: number
  username: string
  realName: string
  className?: string
  currentClassId?: number
}

// ==================== 响应式数据 ====================
const tableLoading = ref(false)
const searchKeyword = ref('')
const filterGrade = ref<string | undefined>(undefined)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const isEditing = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  grade: '',
  description: '',
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }, { max: 30, message: '班级名称不超过30字', trigger: 'blur' }],
  grade: [{ required: true, message: '请选择年级', trigger: 'change' }],
}

// 分配学生相关
const assignDialogVisible = ref(false)
const assignLoading = ref(false)
const assignSearchKeyword = ref('')
const currentAssignClass = ref<ClassItem | null>(null)
const selectedStudentIds = ref<number[]>([])

// ==================== Mock数据 ====================
const classList = ref<ClassItem[]>([
  { id: 1, name: '计算机科学与技术1班', grade: '2024级', description: '计算机专业本科一年级', studentCount: 42, createTime: '2024-09-01 10:00:00' },
  { id: 2, name: '计算机科学与技术2班', grade: '2024级', description: '', studentCount: 38, createTime: '2024-09-01 10:05:00' },
  { id: 3, name: '软件工程1班', grade: '2024级', description: '软件工程专业', studentCount: 45, createTime: '2024-09-01 11:00:00' },
  { id: 4, name: '网络工程1班', grade: '2024级', description: '网络工程专业', studentCount: 35, createTime: '2024-09-01 11:15:00' },
  { id: 5, name: '计算机科学与技术1班', grade: '2023级', description: '计算机专业二年级', studentCount: 40, createTime: '2023-09-01 08:00:00' },
  { id: 6, name: '软件工程1班', grade: '2023级', description: '', studentCount: 36, createTime: '2023-09-01 08:10:00' },
  { id: 7, name: '人工智能1班', grade: '2024级', description: '新开设专业方向', studentCount: 28, createTime: '2024-09-02 14:00:00' },
  { id: 8, name: '信息安全1班', grade: '2024级', description: '', studentCount: 32, createTime: '2024-09-03 09:20:00' },
  { id: 9, name: '数据科学1班', grade: '2023级', description: '跨学科专业', studentCount: 25, createTime: '2023-09-05 16:00:00' },
  { id: 10, name: '物联网1班', grade: '2024级', description: '物联网工程专业', studentCount: 30, createTime: '2024-09-04 10:30:00' },
])

// 所有可分配的学生（mock）
const allStudents = ref<StudentItem[]>([
  { id: 101, username: '2024001', realName: '王小明', className: '计算机科学与技术1班', currentClassId: 1 },
  { id: 102, username: '2024002', realName: '李华', className: '计算机科学与技术1班', currentClassId: 1 },
  { id: 103, username: '2024003', realName: '张三', className: '计算机科学与技术2班', currentClassId: 2 },
  { id: 104, username: '2024004', realName: '赵四', className: '软件工程1班', currentClassId: 3 },
  { id: 105, username: '2024005', realName: '孙五', className: '未分配', currentClassId: undefined },
  { id: 106, username: '2024006', realName: '周六', className: '未分配', currentClassId: undefined },
  { id: 107, username: '2024007', realName: '吴七', className: '未分配', currentClassId: undefined },
  { id: 108, username: '2024008', realName: '郑八', className: '未分配', currentClassId: undefined },
  { id: 109, username: '2024009', realName: '王九', className: '网络工程1班', currentClassId: 4 },
  { id: 110, username: '2024010', realName: '陈十', className: '未分配', currentClassId: undefined },
  { id: 111, username: '2024011', realName: '林一一', className: '未分配', currentClassId: undefined },
  { id: 112, username: '2024012', realName: '黄一二', className: '未分配', currentClassId: undefined },
])

total.value = classList.value.length

// 过滤后的学生列表（用于分配）
const filteredStudentsForAssign = computed(() => {
  if (!assignSearchKeyword.value) return allStudents.value
  const kw = assignSearchKeyword.value.toLowerCase()
  return allStudents.value.filter(
    (s) => s.realName.includes(kw) || s.username.includes(kw),
  )
})

// ==================== 方法 ====================
function handleSearch() {
  // TODO: 替换为API调用
  tableLoading.value = true
  setTimeout(() => {
    tableLoading.value = false
    ElMessage.success('搜索完成')
  }, 300)
}

function resetFilters() {
  searchKeyword.value = ''
  filterGrade.value = undefined
  handleSearch()
}

function openCreateDialog() {
  isEditing.value = false
  editingId.value = null
  form.name = ''
  form.grade = ''
  form.description = ''
  dialogVisible.value = true
}

function openEditDialog(row: ClassItem) {
  isEditing.value = true
  editingId.value = row.id
  form.name = row.name
  form.grade = row.grade
  form.description = row.description || ''
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true

  try {
    // TODO: 替换为API调用
    await new Promise((resolve) => setTimeout(resolve, 500))

    if (isEditing.value && editingId.value) {
      const idx = classList.value.findIndex((c) => c.id === editingId.value)
      if (idx !== -1) {
        classList.value[idx] = {
          ...classList.value[idx],
          name: form.name,
          grade: form.grade,
          description: form.description,
        }
      }
      ElMessage.success('班级信息已更新')
    } else {
      const newClass: ClassItem = {
        id: Date.now(),
        name: form.name,
        grade: form.grade,
        description: form.description,
        studentCount: 0,
        createTime: new Date().toLocaleString('zh-CN'),
      }
      classList.value.unshift(newClass)
      total.value++
      ElMessage.success('班级创建成功')
    }

    dialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: ClassItem) {
  // TODO: 替换为API调用
  const idx = classList.value.findIndex((c) => c.id === row.id)
  if (idx !== -1) {
    classList.value.splice(idx, 1)
    total.value--
  }
  ElMessage.success(`已删除班级「${row.name}」`)
}

// ====== 分配学生 ======
function openAssignDialog(row: ClassItem) {
  currentAssignClass.value = row
  selectedStudentIds.value = allStudents.value
    .filter((s) => s.currentClassId === row.id)
    .map((s) => s.id)
  assignSearchKeyword.value = ''
  assignDialogVisible.value = true
}

async function handleAssignSubmit() {
  assignLoading.value = true
  try {
    // TODO: 替换为API调用 - 调用 assignStudentsToClass API
    await new Promise((resolve) => setTimeout(resolve, 500))

    // 更新本地数据
    for (const sid of selectedStudentIds.value) {
      const student = allStudents.value.find((s) => s.id === sid)
      if (student) {
        student.currentClassId = currentAssignClass.value!.id
        student.className = currentAssignClass.value!.name
      }
    }
    // 更新班级人数
    const cls = classList.value.find((c) => c.id === currentAssignClass.value!.id)
    if (cls) {
      cls.studentCount = allStudents.value.filter((s) => s.currentClassId === cls.id).length
    }

    ElMessage.success(`成功分配 ${selectedStudentIds.value.length} 名学生到「${currentAssignClass.value!.name}」`)
    assignDialogVisible.value = false
  } finally {
    assignLoading.value = false
  }
}
</script>
