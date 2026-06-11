<template>
  <div class="space-y-6">
    <!-- 页面标题栏 -->
    <div class="bg-white rounded-xl shadow-sm p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-xl bg-primary-50 flex items-center justify-center">
          <i class="ri-calendar-line text-primary-500 text-xl"></i>
        </div>
        <div>
          <h1 class="text-lg font-bold text-gray-800">学期管理</h1>
          <p class="text-xs text-gray-400 mt-0.5">管理平台的学期信息与时间安排</p>
        </div>
      </div>
      <el-button type="primary" @click="handleCreate" class="!rounded-lg !px-5">
        <i class="ri-add-line mr-1.5"></i>新建学期
      </el-button>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="bg-white rounded-xl shadow-sm p-5">
      <div class="flex flex-col sm:flex-row gap-3">
        <div class="relative flex-1 max-w-md">
          <i class="ri-search-line absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索学期名称..."
            class="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-lg focus:border-primary-400 focus:ring-2 focus:ring-primary-50 outline-none transition-all text-sm bg-gray-50/50"
            @input="handleSearch"
          />
        </div>
        <el-select
          v-model="statusFilter"
          placeholder="状态筛选"
          clearable
          class="w-36"
          size="default"
          @change="handleFilter"
        >
          <el-option label="全部" value="" />
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </div>
    </div>

    <!-- 学期列表表格 -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <el-table
        :data="filteredSemesterList"
        stripe
        style="width: 100%"
        v-loading="tableLoading"
        empty-text="暂无学期数据"
        header-cell-class-name="!bg-gray-50 !text-gray-600 !font-semibold !text-xs"
        row-class-name="hover:bg-gray-50/50 cursor-pointer"
      >
        <el-table-column prop="name" label="学期名称" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <div
                class="w-8 h-8 rounded-lg flex items-center justify-center text-xs font-bold"
                :class="row.status === '1' ? 'bg-primary-50 text-primary-600' : 'bg-gray-100 text-gray-500'"
              >
                {{ row.name.slice(0, 4) }}
              </div>
              <span class="font-medium text-gray-800">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="startDate" label="开始日期" min-width="130" align="center">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.startDate }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="endDate" label="结束日期" min-width="130" align="center">
          <template #default="{ row }">
            <span class="text-gray-600">{{ row.endDate }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === '1' ? 'success' : 'danger'"
              effect="light"
              round
              size="small"
              class="!rounded-full"
            >
              {{ row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" min-width="160" align="center">
          <template #default="{ row }">
            <span class="text-gray-500 text-sm">{{ row.createdAt }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center gap-2">
              <el-button
                type="primary"
                link
                size="small"
                @click.stop="handleEdit(row)"
                class="!px-2"
              >
                <i class="ri-edit-line mr-1"></i>编辑
              </el-button>
              <el-popconfirm
                title="确定要删除该学期吗？删除后不可恢复！"
                confirm-button-text="确定删除"
                cancel-button-text="取消"
                confirm-button-type="danger"
                icon-color="#ef4444"
                @confirm="handleDelete(row)"
              >
                <template #reference>
                  <el-button
                    type="danger"
                    link
                    size="small"
                    @click.stop
                    class="!px-2"
                  >
                    <i class="ri-delete-bin-line mr-1"></i>删除
                  </el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="p-4 flex items-center justify-between border-t border-gray-100">
        <span class="text-sm text-gray-400">共 {{ totalItems }} 条记录</span>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalItems"
          layout="sizes, prev, pager, next, jumper"
          background
          small
          class="!justify-end"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新建/编辑学期对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学期' : '新建学期'"
      width="520px"
      :close-on-click-modal="false"
      destroy-on-close
      class="!rounded-xl"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="pt-2"
      >
        <el-form-item label="学期名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入学期名称，如：2024-2025学年 第二学期"
            maxlength="50"
            show-word-limit
            clearable
            prefix-icon="ri-calendar-line"
          />
        </el-form-item>

        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker
              v-model="formData.startDate"
              type="date"
              placeholder="选择开始日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              :disabled-date="(date: Date) => formData.endDate ? date > new Date(formData.endDate) : false"
            />
          </el-form-item>

          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker
              v-model="formData.endDate"
              type="date"
              placeholder="选择结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              :disabled-date="(date: Date) => formData.startDate ? date < new Date(formData.startDate) : false"
            />
          </el-form-item>
        </div>

        <el-form-item label="状态" prop="status">
          <div class="flex items-center justify-between w-full px-4 py-3 bg-gray-50 rounded-lg">
            <span class="text-sm text-gray-600">{{ formData.status === '1' ? '已启用' : '已禁用' }}</span>
            <el-switch
              v-model="formData.status"
              active-value="1"
              inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              active-color="#3b82f6"
            />
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="dialogVisible = false" class="!rounded-lg !px-5">取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit" class="!rounded-lg !px-5">
            {{ isEdit ? '保 存' : '创 建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

// TODO: 替换为API调用获取学期列表
const tableLoading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

interface SemesterItem {
  id: number
  name: string
  startDate: string
  endDate: string
  status: string
  createdAt: string
}

// Mock 数据 - 学期列表
const semesterList = ref<SemesterItem[]>([
  { id: 1, name: '2024-2025学年 第二学期', startDate: '2025-02-24', endDate: '2025-07-15', status: '1', createdAt: '2025-01-15 09:30:00' },
  { id: 2, name: '2024-2025学年 第一学期', startDate: '2024-09-01', endDate: '2025-01-18', status: '0', createdAt: '2024-08-20 14:22:00' },
  { id: 3, name: '2023-2024学年 第二学期', startDate: '2024-02-26', endDate: '2024-07-12', status: '0', createdAt: '2024-01-18 10:15:00' },
  { id: 4, name: '2023-2024学年 第一学期', startDate: '2023-09-04', endDate: '2024-01-19', status: '0', createdAt: '2023-08-25 16:40:00' },
  { id: 5, name: '2022-2023学年 第二学期', startDate: '2023-02-27', endDate: '2023-07-14', status: '0', createdAt: '2023-01-16 11:05:00' },
  { id: 6, name: '2022-2023学年 第一学期', startDate: '2022-09-01', endDate: '2023-01-13', status: '0', createdAt: '2022-08-22 08:55:00' },
  { id: 7, name: '2021-2022学年 第二学期', startDate: '2022-02-28', endDate: '2022-07-08', status: '0', createdAt: '2022-01-14 13:28:00' },
  { id: 8, name: '2021-2022学年 第一学期', startDate: '2021-09-06', endDate: '2022-01-14', status: '0', createdAt: '2021-08-26 09:12:00' },
])

// 筛选后的数据
const filteredSemesterList = computed(() => {
  let list = semesterList.value
  if (searchKeyword.value) {
    list = list.filter(item =>
      item.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  if (statusFilter.value !== '' && statusFilter.value !== undefined) {
    list = list.filter(item => item.status === statusFilter.value)
  }
  return list
})

const totalItems = computed(() => filteredSemesterList.value.length)

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  id: null as number | null,
  name: '',
  startDate: '',
  endDate: '',
  status: '1',
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入学期名称', trigger: 'blur' },
    { min: 5, max: 50, message: '学期名称长度在 5 到 50 个字符', trigger: 'blur' },
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' },
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' },
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' },
  ],
}

function handleSearch() {
  currentPage.value = 1
}

function handleFilter() {
  currentPage.value = 1
}

function handleSizeChange(val: number) {
  pageSize.value = val
  currentPage.value = 1
}

function handleCurrentChange(val: number) {
  currentPage.value = val
}

function handleCreate() {
  isEdit.value = false
  formData.id = null
  formData.name = ''
  formData.startDate = ''
  formData.endDate = ''
  formData.status = '1'
  dialogVisible.value = true
}

function handleEdit(row: SemesterItem) {
  isEdit.value = true
  formData.id = row.id
  formData.name = row.name
  formData.startDate = row.startDate
  formData.endDate = row.endDate
  formData.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitLoading.value = true

  try {
    // TODO: 替换为真实API调用
    // const res = await isEdit.value ? updateSemesterApi(formData) : createSemesterApi(formData)
    await new Promise(resolve => setTimeout(resolve, 800))

    if (isEdit.value) {
      const index = semesterList.value.findIndex(item => item.id === formData.id)
      if (index !== -1) {
        semesterList.value[index] = {
          ...semesterList.value[index],
          name: formData.name,
          startDate: formData.startDate,
          endDate: formData.endDate,
          status: formData.status,
        }
      }
      ElMessage.success('学期更新成功')
    } else {
      const newItem: SemesterItem = {
        id: Math.max(...semesterList.value.map(i => i.id)) + 1,
        name: formData.name,
        startDate: formData.startDate,
        endDate: formData.endDate,
        status: formData.status,
        createdAt: new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-'),
      }
      semesterList.value.unshift(newItem)
      ElMessage.success('学期创建成功')
    }

    dialogVisible.value = false
  } catch (error) {
    console.error('Submit error:', error)
    ElMessage.error(isEdit.value ? '更新失败，请重试' : '创建失败，请重试')
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(row: SemesterItem) {
  // TODO: 替换为真实API调用
  // await deleteSemesterApi(row.id)
  const index = semesterList.value.findIndex(item => item.id === row.id)
  if (index !== -1) {
    semesterList.value.splice(index, 1)
    ElMessage.success('学期已删除')
  }
}
</script>
