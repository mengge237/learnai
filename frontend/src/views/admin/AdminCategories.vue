<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { adminApi } from '@/api/admin'
import LineIcon from '@/components/LineIcon.vue'

const { t } = useI18n()

const rows = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)

const form = reactive({ name: '', description: '', parentId: null, sortOrder: 1, isActive: true })

/** 上级分类候选（编辑时排除自己） */
const parentOptions = computed(() => rows.value.filter((c) => c.id !== editingId.value))

function openCreate() {
  editingId.value = null
  Object.assign(form, { name: '', description: '', parentId: null, sortOrder: 1, isActive: true })
  dialogVisible.value = true
}

function openEdit(c) {
  editingId.value = c.id
  Object.assign(form, {
    name: c.name,
    description: c.description || '',
    parentId: c.parentId,
    sortOrder: c.sortOrder,
    isActive: c.isActive,
  })
  dialogVisible.value = true
}

async function save() {
  saving.value = true
  try {
    const data = { ...form, description: form.description || null }
    if (editingId.value) {
      await adminApi.updateCategory(editingId.value, data)
      ElMessage.success(t('已保存'))
    } else {
      await adminApi.createCategory(data)
      ElMessage.success(t('已新增'))
    }
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e?.message || t('保存失败'))
  } finally {
    saving.value = false
  }
}

async function remove(c) {
  try {
    await ElMessageBox.confirm(t('确定删除分类「{name}」吗？', { name: c.name }), t('删除分类'), { type: 'warning' })
  } catch {
    return // 用户取消
  }
  try {
    await adminApi.deleteCategory(c.id)
    ElMessage.success(t('已删除'))
    load()
  } catch (e) {
    ElMessage.error(e?.message || t('删除失败'))
  }
}

async function load() {
  loading.value = true
  try {
    rows.value = await adminApi.categories()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="head">
      <div class="page-title" style="margin-bottom: 0"><LineIcon name="layers" :size="19" /> {{ $t('分类管理') }}</div>
      <el-button type="primary" @click="openCreate"><LineIcon name="plus" :size="14" /> {{ $t('新增分类') }}</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" size="small">
      <el-table-column :label="$t('编号')" width="80">
        <template #default="{ row }">#{{ row.id }}</template>
      </el-table-column>
      <el-table-column :label="$t('名称')" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">
          <span class="cat-name">{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('描述')" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">{{ row.description || '—' }}</template>
      </el-table-column>
      <el-table-column :label="$t('上级分类')" width="140">
        <template #default="{ row }">
          {{ rows.find((c) => c.id === row.parentId)?.name || $t('（顶级）') }}
        </template>
      </el-table-column>
      <el-table-column :label="$t('排序号')" width="80" align="center">
        <template #default="{ row }">{{ row.sortOrder }}</template>
      </el-table-column>
      <el-table-column :label="$t('资源数')" width="90" align="center">
        <template #default="{ row }">{{ row.resourceCount }}</template>
      </el-table-column>
      <el-table-column :label="$t('状态')" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.isActive ? 'success' : 'info'" effect="plain">
            {{ row.isActive ? $t('启用') : $t('停用') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('操作')" width="150" align="center">
        <template #default="{ row }">
          <el-button size="small" plain @click="openEdit(row)">{{ $t('编辑') }}</el-button>
          <el-button size="small" type="danger" plain @click="remove(row)">{{ $t('删除') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingId ? $t('编辑分类') : $t('新增分类')" width="460px">
      <el-form label-width="80px">
        <el-form-item :label="$t('名称')" required>
          <el-input v-model="form.name" maxlength="50" :placeholder="$t('分类名称')" />
        </el-form-item>
        <el-form-item :label="$t('描述')">
          <el-input v-model="form.description" type="textarea" :rows="2" maxlength="500" :placeholder="$t('分类描述（可选）')" />
        </el-form-item>
        <el-form-item :label="$t('上级分类')">
          <el-select v-model="form.parentId" :placeholder="$t('不选则为顶级分类')" clearable style="width: 100%">
            <el-option v-for="c in parentOptions" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('排序号')">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
          <span class="text-muted form-tip">{{ $t('数字越小越靠前') }}</span>
        </el-form-item>
        <el-form-item :label="$t('启用')">
          <el-switch v-model="form.isActive" />
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
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
.cat-name {
  font-weight: 600;
}
.form-tip {
  margin-left: 10px;
  font-size: 12px;
}
</style>
