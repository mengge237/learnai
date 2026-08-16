<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { adminApi } from '@/api/admin'
import { formatDate, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const { t } = useI18n()

const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const search = ref('')
const status = ref('all')

function statusInfo(m) {
  if (m.isApproved) return { text: t('已通过'), type: 'success' }
  if (m.rejectionReason) return { text: t('已驳回'), type: 'danger' }
  return { text: t('待审核'), type: 'warning' }
}

async function load() {
  loading.value = true
  try {
    const res = await adminApi.models({
      search: search.value || undefined,
      status: status.value === 'all' ? undefined : status.value,
      page: page.value,
      size: size.value,
    })
    rows.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.value = 1
  load()
}

function onStatusChange() {
  page.value = 1
  load()
}

async function togglePublic(m) {
  await adminApi.toggleModelPublic(m.id, !m.isPublic)
  ElMessage.success(t(m.isPublic ? '已下架' : '已上架'))
  load()
}

async function remove(m) {
  try {
    await ElMessageBox.confirm(t('确定删除模型「{name}」吗？删除后不可恢复', { name: m.name }), t('删除模型'), { type: 'warning' })
  } catch {
    return // 用户取消
  }
  try {
    await adminApi.deleteModel(m.id)
    ElMessage.success(t('已删除'))
    load()
  } catch (e) {
    ElMessage.error(e?.message || t('删除失败'))
  }
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="cube" :size="19" /> {{ $t('模型管理') }}</div>

    <div class="toolbar">
      <el-radio-group v-model="status" @change="onStatusChange">
        <el-radio-button value="all">{{ $t('全部') }}</el-radio-button>
        <el-radio-button value="pending">{{ $t('待审核') }}</el-radio-button>
        <el-radio-button value="approved">{{ $t('已通过') }}</el-radio-button>
        <el-radio-button value="rejected">{{ $t('已驳回') }}</el-radio-button>
      </el-radio-group>
      <div class="toolbar-right">
        <el-input
          v-model="search"
          :placeholder="$t('按名称搜索…')"
          clearable
          style="width: 240px"
          @keyup.enter="onSearch"
          @clear="onSearch"
        >
          <template #append>
            <el-button @click="onSearch" :aria-label="$t('搜索')"><LineIcon name="search" :size="14" /></el-button>
          </template>
        </el-input>
      </div>
    </div>

    <el-table v-loading="loading" :data="rows" size="small">
      <el-table-column :label="$t('编号')" width="80">
        <template #default="{ row }">#{{ row.id }}</template>
      </el-table-column>
      <el-table-column :label="$t('名称')" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <el-link type="primary" @click="router.push(`/market/${row.id}`)">{{ row.name }}</el-link>
        </template>
      </el-table-column>
      <el-table-column :label="$t('创作者')" width="120" show-overflow-tooltip>
        <template #default="{ row }">{{ row.creator || $t('未知') }}</template>
      </el-table-column>
      <el-table-column :label="$t('分类')" width="110" show-overflow-tooltip>
        <template #default="{ row }">{{ row.categoryName || '—' }}</template>
      </el-table-column>
      <el-table-column :label="$t('价格')" width="90" align="right">
        <template #default="{ row }">{{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column :label="$t('审核状态')" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="statusInfo(row).type" effect="plain">{{ statusInfo(row).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('上架状态')" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.isPublic ? 'success' : 'info'" effect="plain">
            {{ row.isPublic ? $t('已上架') : $t('已下架') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('提交时间')" width="150">
        <template #default="{ row }">{{ formatDate(row.createDate) }}</template>
      </el-table-column>
      <el-table-column :label="$t('操作')" width="180" align="center">
        <template #default="{ row }">
          <el-button size="small" :type="row.isPublic ? 'warning' : 'success'" plain @click="togglePublic(row)">
            {{ row.isPublic ? $t('下架') : $t('上架') }}
          </el-button>
          <el-button size="small" type="danger" plain @click="remove(row)">{{ $t('删除') }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        layout="total, prev, pager, next"
        :total="total"
        :page-size="size"
        :current-page="page"
        @current-change="(p) => { page = p; load() }"
      />
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}
.toolbar-right {
  display: flex;
  gap: 8px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
