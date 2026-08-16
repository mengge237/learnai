<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { adminApi } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import { formatDate, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const { t } = useI18n()
const auth = useAuthStore()
const router = useRouter()

const resources = ref([])
const models = ref([])
const stats = ref(null)
const loading = ref(true)

// 审核历史
const historyType = ref('resources')
const history = ref([])
const historyTotal = ref(0)
const historyPage = ref(1)
const historySize = ref(10)
const historyLoading = ref(false)

const statCards = computed(() => [
  { key: 'pendingResources', label: t('待审资源'), icon: 'book' },
  { key: 'pendingModels', label: t('待审模型'), icon: 'cube' },
  { key: 'totalReviewed', label: t('累计审核'), icon: 'check' },
  { key: 'reviewedToday', label: t('今日审核'), icon: 'flame' },
])

async function load() {
  loading.value = true
  try {
    if (auth.isAdmin || auth.isAuditor) {
      ;[resources.value, models.value, stats.value] = await Promise.all([
        adminApi.pendingResources(),
        adminApi.pendingModels(),
        adminApi.auditStats(),
      ])
    }
  } finally {
    loading.value = false
  }
}

async function loadHistory() {
  historyLoading.value = true
  try {
    const res = await adminApi.auditHistory(historyType.value, {
      page: historyPage.value,
      size: historySize.value,
    })
    history.value = res.content
    historyTotal.value = res.totalElements
  } finally {
    historyLoading.value = false
  }
}

function onHistoryTypeChange() {
  historyPage.value = 1
  loadHistory()
}

async function approveResource(r) {
  await adminApi.reviewResource(r.id, { approved: true })
  ElMessage.success(t('已通过'))
  load()
}

async function rejectResource(r) {
  let value
  try {
    ;({ value } = await ElMessageBox.prompt(t('请填写驳回原因'), t('驳回资源'), {
      inputPattern: /\S+/,
      inputErrorMessage: t('驳回原因不能为空'),
    }))
  } catch {
    return // 用户取消
  }
  await adminApi.reviewResource(r.id, { approved: false, reason: value })
  ElMessage.success(t('已驳回'))
  load()
}

async function approveModel(m) {
  await adminApi.reviewModel(m.id, { approved: true })
  ElMessage.success(t('已通过'))
  load()
}

async function rejectModel(m) {
  let value
  try {
    ;({ value } = await ElMessageBox.prompt(t('请填写驳回原因'), t('驳回模型'), {
      inputPattern: /\S+/,
      inputErrorMessage: t('驳回原因不能为空'),
    }))
  } catch {
    return // 用户取消
  }
  await adminApi.reviewModel(m.id, { approved: false, reason: value })
  ElMessage.success(t('已驳回'))
  load()
}

onMounted(() => {
  load()
  loadHistory()
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="page-title"><LineIcon name="check" :size="19" /> {{ $t('内容审核工作台') }}</div>

    <!-- 统计卡片 -->
    <div v-if="stats" class="stat-grid">
      <div v-for="s in statCards" :key="s.key" class="stat-card" :class="{ warn: s.key.startsWith('pending') }">
        <div class="stat-head">
          <LineIcon :name="s.icon" :size="18" />
        </div>
        <div class="stat-value">{{ stats[s.key] }}</div>
        <div class="stat-label text-muted">{{ s.label }}</div>
      </div>
    </div>

    <el-tabs>
      <el-tab-pane :label="$t('待审核资源（{n}）', { n: resources.length })">
        <el-empty v-if="resources.length === 0" :description="$t('太棒了，没有待审核的资源')" />
        <el-card v-for="r in resources" :key="r.id" class="audit-card">
          <div class="row">
            <el-image v-if="r.previewUrl" :src="r.previewUrl" fit="cover" class="thumb" />
            <div v-else class="thumb thumb-fallback" />
            <div class="info">
              <div class="title">
                {{ r.title }}
                <el-tag size="small" effect="plain">{{ r.resourceCode }}</el-tag>
                <el-tag v-if="r.isFree" size="small" type="success" effect="plain">{{ $t('免费') }}</el-tag>
                <el-tag v-else size="small" type="danger" effect="plain">{{ formatPrice(r.price) }}</el-tag>
              </div>
              <div class="text-muted">
                {{ r.author || $t('未知作者') }} · {{ r.categoryName }} · {{ $t(r.difficultyLevel) }} · {{ $t('提交于') }} {{ formatDate(r.createDate) }}
              </div>
              <div class="desc text-muted">{{ r.description }}</div>
              <div class="actions">
                <el-button size="small" type="success" @click="approveResource(r)"><LineIcon name="check" :size="14" /> {{ $t('通过') }}</el-button>
                <el-button size="small" type="danger" plain @click="rejectResource(r)"><LineIcon name="close" :size="14" /> {{ $t('驳回') }}</el-button>
                <el-button size="small" @click="router.push(`/resources/${r.id}`)">{{ $t('查看详情') }}</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane :label="$t('待审核模型（{n}）', { n: models.length })">
        <el-empty v-if="models.length === 0" :description="$t('太棒了，没有待审核的模型')" />
        <el-card v-for="m in models" :key="m.id" class="audit-card">
          <div class="row">
            <el-image v-if="m.previewUrl" :src="m.previewUrl" fit="cover" class="thumb" />
            <div v-else class="thumb thumb-fallback" />
            <div class="info">
              <div class="title">
                {{ m.name }}
                <el-tag size="small" effect="plain">{{ m.modelCode }}</el-tag>
                <el-tag size="small" type="danger" effect="plain">{{ formatPrice(m.price) }}</el-tag>
              </div>
              <div class="text-muted">
                {{ m.creator || $t('未知创作者') }} · {{ m.categoryName }} · {{ $t('提交于') }} {{ formatDate(m.createDate) }}
              </div>
              <div class="actions">
                <el-button size="small" type="success" @click="approveModel(m)"><LineIcon name="check" :size="14" /> {{ $t('通过') }}</el-button>
                <el-button size="small" type="danger" plain @click="rejectModel(m)"><LineIcon name="close" :size="14" /> {{ $t('驳回') }}</el-button>
                <el-button size="small" @click="router.push(`/market/${m.id}`)">{{ $t('查看详情') }}</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane :label="$t('审核历史')">
        <div class="history-head">
          <el-radio-group v-model="historyType" @change="onHistoryTypeChange">
            <el-radio-button value="resources">{{ $t('资源审核历史') }}</el-radio-button>
            <el-radio-button value="models">{{ $t('模型审核历史') }}</el-radio-button>
          </el-radio-group>
          <span class="text-muted history-total">{{ $t('共 {n} 条记录', { n: historyTotal }) }}</span>
        </div>
        <el-table v-loading="historyLoading" :data="history" size="small">
          <el-table-column :label="$t('编号')" width="90">
            <template #default="{ row }">#{{ row.id }}</template>
          </el-table-column>
          <el-table-column :label="$t('类型')" width="80" align="center">
            <template #default="{ row }">
              <el-tag size="small" effect="plain">{{ row.type === 'resource' ? $t('资源') : $t('模型') }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('标题')" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <el-link type="primary" @click="router.push(row.type === 'resource' ? `/resources/${row.id}` : `/market/${row.id}`)">
                {{ row.title }}
              </el-link>
            </template>
          </el-table-column>
          <el-table-column :label="$t('作者')" width="130" show-overflow-tooltip>
            <template #default="{ row }">{{ row.author || $t('未知') }}</template>
          </el-table-column>
          <el-table-column :label="$t('结果')" width="90" align="center">
            <template #default="{ row }">
              <el-tag size="small" :type="row.result === 'approved' ? 'success' : 'danger'" effect="plain">
                {{ row.result === 'approved' ? $t('通过') : $t('驳回') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('驳回原因')" min-width="160" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.reason" class="text-muted">{{ row.reason }}</span>
              <span v-else class="text-muted">—</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('审核人')" width="110">
            <template #default="{ row }">{{ row.reviewerName || $t('未知') }}</template>
          </el-table-column>
          <el-table-column :label="$t('审核时间')" width="160">
            <template #default="{ row }">{{ formatDate(row.reviewedAt) }}</template>
          </el-table-column>
        </el-table>
        <div class="pager">
          <el-pagination
            layout="total, prev, pager, next"
            :total="historyTotal"
            :page-size="historySize"
            :current-page="historyPage"
            @current-change="(p) => { historyPage = p; loadHistory() }"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
/* 统计卡片：工业风线框 + 橙色序号边 */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 12px;
  margin-bottom: 8px;
}
.stat-card {
  border: 1px solid var(--border-color);
  border-left: 4px solid var(--theme-color);
  border-radius: 2px;
  background: var(--el-bg-color);
  padding: 14px 16px;
}
.stat-card.warn {
  border-left-color: #e6a23c;
}
.stat-head {
  color: var(--el-text-color-secondary);
}
.stat-value {
  font-size: 26px;
  font-weight: 800;
  font-family: 'Consolas', 'Courier New', monospace;
  margin: 2px 0;
}
.stat-label {
  font-size: 13px;
}
.audit-card {
  margin-bottom: 12px;
}
.row {
  display: flex;
  gap: 14px;
}
.thumb {
  width: 120px;
  height: 84px;
  border-radius: 6px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 120px;
  height: 84px;
  border-radius: 6px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
  flex-shrink: 0;
}
.info {
  flex: 1;
}
.title {
  font-weight: 600;
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 6px;
}
.desc {
  font-size: 13px;
  margin: 6px 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.actions {
  display: flex;
  gap: 8px;
}
.history-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.history-total {
  font-size: 13px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
