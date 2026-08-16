<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { adminApi } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import PaginationBar from '@/components/PaginationBar.vue'
import LineIcon from '@/components/LineIcon.vue'

const { t } = useI18n()
const auth = useAuthStore()
const users = ref([])
const loading = ref(true)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const search = ref('')

const dialogVisible = ref(false)
const editing = ref(null)
const editForm = reactive({ roleId: 3, isActive: true })
const saving = ref(false)

const ROLE_TAG = { 1: 'danger', 2: 'warning', 3: 'info' }

async function load() {
  loading.value = true
  try {
    const res = await adminApi.users({ page: page.value, size: size.value, search: search.value || undefined })
    users.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function openEdit(u) {
  editing.value = u
  editForm.roleId = u.roleId
  editForm.isActive = u.isActive
  dialogVisible.value = true
}

async function save() {
  if (editing.value.id === auth.user?.id && editForm.roleId !== 1) {
    ElMessage.warning(t('不能取消自己的管理员角色'))
    return
  }
  saving.value = true
  try {
    await adminApi.updateUser(editing.value.id, { roleId: editForm.roleId, isActive: editForm.isActive })
    ElMessage.success(t('已更新'))
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

function doSearch() {
  page.value = 1
  load()
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="user" :size="19" /> {{ $t('用户管理') }}</div>

    <div class="toolbar">
      <el-input v-model="search" :placeholder="$t('搜索用户名 / 邮箱 / 手机号')" clearable class="search" @keyup.enter="doSearch" @clear="doSearch">
        <template #append>
          <el-button @click="doSearch">{{ $t('搜索') }}</el-button>
        </template>
      </el-input>
    </div>

    <el-card>
      <el-table :data="users" v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" :label="$t('用户名')" min-width="120" />
        <el-table-column :label="$t('角色')" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="ROLE_TAG[row.roleId] || 'info'">{{ $t(row.roleName) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" :label="$t('邮箱')" min-width="160" />
        <el-table-column prop="phone" :label="$t('手机号')" width="130" />
        <el-table-column prop="location" :label="$t('地区')" min-width="120" />
        <el-table-column :label="$t('状态')" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.isActive ? 'success' : 'danger'">{{ row.isActive ? $t('正常状态') : $t('禁用') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('操作')" width="90" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openEdit(row)">{{ $t('编辑') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <PaginationBar v-if="total > 0" v-model:page="page" v-model:size="size" :total="total" :sizes="[10, 20, 50]" @change="load" />

    <el-dialog v-model="dialogVisible" :title="$t('编辑用户：{name}', { name: editing?.username })" width="420px">
      <el-form label-width="80px">
        <el-form-item :label="$t('角色')">
          <el-select v-model="editForm.roleId">
            <el-option :label="$t('管理员')" :value="1" />
            <el-option :label="$t('审核员')" :value="2" />
            <el-option :label="$t('普通用户')" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('账号状态')">
          <el-switch v-model="editForm.isActive" :active-text="$t('正常状态')" :inactive-text="$t('禁用')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ $t('取消') }}</el-button>
        <el-button type="primary" :loading="saving" @click="save">{{ $t('保存') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
.search {
  width: 340px;
}
</style>
