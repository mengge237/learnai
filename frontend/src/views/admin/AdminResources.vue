<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'
import { formatDate, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()

const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const search = ref('')
const status = ref('all')

function statusInfo(r) {
  if (r.isApproved) return { text: '已通过', type: 'success' }
  if (r.rejectionReason) return { text: '已驳回', type: 'danger' }
  return { text: '待审核', type: 'warning' }
}

async function load() {
  loading.value = true
  try {
    const res = await adminApi.resources({
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

async function togglePublic(r) {
  await adminApi.toggleResourcePublic(r.id, !r.isPublic)
  ElMessage.success(r.isPublic ? '已下架' : '已上架')
  load()
}

async function remove(r) {
  try {
    await ElMessageBox.confirm(`确定删除资源「${r.title}」吗？删除后不可恢复`, '删除资源', { type: 'warning' })
  } catch {
    return // 用户取消
  }
  try {
    await adminApi.deleteResource(r.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    ElMessage.error(e?.message || '删除失败')
  }
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="book" :size="19" /> 资源管理</div>

    <div class="toolbar">
      <el-radio-group v-model="status" @change="onStatusChange">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="pending">待审核</el-radio-button>
        <el-radio-button value="approved">已通过</el-radio-button>
        <el-radio-button value="rejected">已驳回</el-radio-button>
      </el-radio-group>
      <div class="toolbar-right">
        <el-input
          v-model="search"
          placeholder="按标题搜索…"
          clearable
          style="width: 240px"
          @keyup.enter="onSearch"
          @clear="onSearch"
        >
          <template #append>
            <el-button @click="onSearch" aria-label="搜索"><LineIcon name="search" :size="14" /></el-button>
          </template>
        </el-input>
      </div>
    </div>

    <el-table v-loading="loading" :data="rows" size="small">
      <el-table-column label="编号" width="80">
        <template #default="{ row }">#{{ row.id }}</template>
      </el-table-column>
      <el-table-column label="标题" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <el-link type="primary" @click="router.push(`/resources/${row.id}`)">{{ row.title }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="作者" width="120" show-overflow-tooltip>
        <template #default="{ row }">{{ row.author || '未知' }}</template>
      </el-table-column>
      <el-table-column label="分类" width="110" show-overflow-tooltip>
        <template #default="{ row }">{{ row.categoryName || '—' }}</template>
      </el-table-column>
      <el-table-column label="价格" width="90" align="right">
        <template #default="{ row }">
          <span v-if="row.isFree" class="free">免费</span>
          <span v-else>{{ formatPrice(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="statusInfo(row).type" effect="plain">{{ statusInfo(row).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.isPublic ? 'success' : 'info'" effect="plain">
            {{ row.isPublic ? '已上架' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" width="150">
        <template #default="{ row }">{{ formatDate(row.createDate) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
          <el-button size="small" :type="row.isPublic ? 'warning' : 'success'" plain @click="togglePublic(row)">
            {{ row.isPublic ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" plain @click="remove(row)">删除</el-button>
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
.free {
  color: var(--el-color-success);
  font-weight: 600;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
