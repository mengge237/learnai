<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { adminApi } from '@/api/admin'
import { marketApi } from '@/api/market'
import { formatDate, formatPrice, ORDER_STATUS } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const { t } = useI18n()

const rows = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const status = ref('')

/** 管理员可推进的状态链：待处理 → 处理中 → 已发货 → 已完成 */
const NEXT_STATUS = { Pending: 'Processing', Processing: 'Shipped', Shipped: 'Completed' }

const STATUS_TAG = {
  PendingPayment: 'warning',
  Pending: 'danger',
  Processing: 'primary',
  Shipped: 'primary',
  Completed: 'success',
  Cancelled: 'info',
}

const statusOptions = computed(() => [
  { value: '', label: t('全部状态') },
  ...Object.keys(ORDER_STATUS).map((k) => ({ value: k, label: t(ORDER_STATUS[k]) })),
])

async function load() {
  loading.value = true
  try {
    const res = await adminApi.orders({
      status: status.value || undefined,
      page: page.value,
      size: size.value,
    })
    rows.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function onStatusChange() {
  page.value = 1
  load()
}

async function advance(o) {
  const next = NEXT_STATUS[o.status]
  if (!next) return
  try {
    await ElMessageBox.confirm(
      t('确定将订单 #{id} 从「{from}」推进为「{to}」吗？', {
        id: o.id,
        from: t(ORDER_STATUS[o.status]),
        to: t(ORDER_STATUS[next]),
      }),
      t('推进订单状态'),
      { type: 'warning' },
    )
  } catch {
    return // 用户取消
  }
  await marketApi.updateStatus(o.id, { status: next })
  ElMessage.success(t('已推进至 {status}', { status: t(ORDER_STATUS[next]) }))
  load()
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="box" :size="19" /> {{ $t('订单管理') }}</div>

    <div class="toolbar">
      <el-select v-model="status" style="width: 180px" @change="onStatusChange">
        <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
      </el-select>
      <span class="text-muted tip">{{ $t('共 {n} 笔订单', { n: total }) }}</span>
    </div>

    <el-table v-loading="loading" :data="rows" size="small">
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="expand-box">
            <div v-for="i in row.items" :key="i.orderItemId" class="expand-item">
              <span class="expand-name">{{ i.modelName }}</span>
              <span class="text-muted">{{ $t(i.licenseType) }} × {{ i.quantity }} · {{ $t('单价') }} {{ formatPrice(i.unitPrice) }}</span>
              <span class="expand-subtotal">{{ formatPrice(i.subtotal) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('订单号')" width="100">
        <template #default="{ row }">#{{ row.id }}</template>
      </el-table-column>
      <el-table-column :label="$t('下单用户')" width="130" show-overflow-tooltip>
        <template #default="{ row }">{{ row.username }}</template>
      </el-table-column>
      <el-table-column :label="$t('收货人')" width="110" show-overflow-tooltip>
        <template #default="{ row }">{{ row.recipientName || '—' }}</template>
      </el-table-column>
      <el-table-column :label="$t('金额')" width="110" align="right">
        <template #default="{ row }">{{ formatPrice(row.totalAmount) }}</template>
      </el-table-column>
      <el-table-column :label="$t('状态')" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="STATUS_TAG[row.status] || 'info'" effect="plain">
            {{ $t(ORDER_STATUS[row.status] || row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('下单时间')" width="160">
        <template #default="{ row }">{{ formatDate(row.orderDate) }}</template>
      </el-table-column>
      <el-table-column :label="$t('操作')" width="140" align="center">
        <template #default="{ row }">
          <el-button v-if="NEXT_STATUS[row.status]" size="small" type="primary" plain @click="advance(row)">
            {{ $t('推进状态') }}
          </el-button>
          <span v-else class="text-muted">—</span>
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
  align-items: center;
  gap: 14px;
  margin-bottom: 14px;
}
.tip {
  font-size: 13px;
}
.expand-box {
  padding: 4px 40px;
}
.expand-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 6px 0;
  border-bottom: 1px dashed var(--border-color);
  font-size: 13px;
}
.expand-item:last-child {
  border-bottom: none;
}
.expand-name {
  font-weight: 600;
  min-width: 160px;
}
.expand-subtotal {
  margin-left: auto;
  font-family: 'Consolas', 'Courier New', monospace;
  font-weight: 700;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
