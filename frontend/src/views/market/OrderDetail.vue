<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { marketApi } from '@/api/market'
import { ORDER_STATUS, ORDER_TAG, formatDate, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'
import PageBreadcrumb from '@/components/PageBreadcrumb.vue'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)
const { t } = useI18n()

const order = ref(null)
const loading = ref(true)

const STATUS_FLOW = ['PendingPayment', 'Pending', 'Processing', 'Shipped', 'Completed']
const activeStep = computed(() => STATUS_FLOW.indexOf(order.value?.status))

async function load() {
  loading.value = true
  try {
    order.value = await marketApi.orderDetail(id)
  } finally {
    loading.value = false
  }
}

async function pay() {
  await marketApi.pay(id)
  ElMessage.success(t('支付成功！等待管理员处理'))
  load()
}

async function cancel() {
  try {
    await ElMessageBox.confirm(t('确定取消该订单吗？'), t('提示'), { type: 'warning' })
  } catch {
    return // 用户取消
  }
  await marketApi.cancel(id)
  ElMessage.success(t('订单已取消'))
  load()
}

onMounted(load)
</script>

<template>
  <div class="page-container narrow" v-loading="loading">
    <PageBreadcrumb
      v-if="order"
      :items="[
        { label: $t('首页'), to: '/' },
        { label: $t('模型资源库'), to: '/market' },
        { label: $t('我的订单'), to: '/market/orders' },
        { label: $t('订单 #{id}', { id: order.id }) },
      ]"
    />

    <template v-if="order">
      <el-card class="block">
        <div class="head-row">
          <span class="text-muted">{{ $t('下单时间：') }}{{ formatDate(order.orderDate) }}</span>
          <el-tag :type="ORDER_TAG[order.status] || 'info'" size="large">
            {{ $t(ORDER_STATUS[order.status] || order.status) }}
          </el-tag>
        </div>

        <el-result v-if="order.status === 'Cancelled'" icon="warning" :title="$t('订单已取消')" :sub-title="$t('该订单已被取消，如需购买请重新下单')" />
        <el-steps v-else :active="activeStep" finish-status="success" align-center class="steps">
          <el-step :title="$t('待支付')" :description="$t('提交订单')" />
          <el-step :title="$t('待处理')" :description="$t('支付成功')" />
          <el-step :title="$t('处理中')" :description="$t('商家备货')" />
          <el-step :title="$t('已发货')" :description="$t('数字商品发放')" />
          <el-step :title="$t('已完成')" :description="$t('交易完成')" />
        </el-steps>
      </el-card>

      <el-card class="block">
        <div class="section-label"><LineIcon name="box" :size="15" /> {{ $t('商品明细') }}</div>
        <el-table :data="order.items">
          <el-table-column :label="$t('模型')" min-width="200">
            <template #default="{ row }">
              <div class="model-cell">
                <el-image v-if="row.previewUrl" :src="row.previewUrl" fit="cover" class="thumb" />
                <div v-else class="thumb thumb-fallback" />
                <el-link type="primary" @click="router.push(`/market/${row.modelId}`)">{{ row.modelName }}</el-link>
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('授权')" width="100" align="center">
            <template #default="{ row }">{{ $t(row.licenseType) }}</template>
          </el-table-column>
          <el-table-column :label="$t('单价')" width="110" align="right">
            <template #default="{ row }">{{ formatPrice(row.unitPrice) }}</template>
          </el-table-column>
          <el-table-column :label="$t('数量')" width="80" align="center">
            <template #default="{ row }">{{ row.quantity }}</template>
          </el-table-column>
          <el-table-column :label="$t('小计')" width="120" align="right">
            <template #default="{ row }">
              <b>{{ formatPrice(row.subtotal) }}</b>
            </template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          <span>{{ $t('订单合计：') }}<b class="total">{{ formatPrice(order.totalAmount) }}</b></span>
        </div>
      </el-card>

      <el-card class="block">
        <div class="section-label"><LineIcon name="user" :size="15" /> {{ $t('收货信息') }}</div>
        <div class="addr-row">
          <span class="text-muted">{{ $t('收货人：') }}</span>{{ order.recipientName }}
          <span class="text-muted addr-phone">{{ $t('电话：') }}</span>{{ order.recipientPhone }}
        </div>
        <div class="addr-row">
          <span class="text-muted">{{ $t('地址：') }}</span>{{ order.recipientAddress }}
        </div>
        <div class="actions">
          <el-button v-if="order.status === 'PendingPayment'" type="danger" size="large" @click="pay">{{ $t('立即支付') }}</el-button>
          <el-button v-if="['PendingPayment', 'Pending'].includes(order.status)" size="large" @click="cancel">{{ $t('取消订单') }}</el-button>
        </div>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.narrow {
  max-width: 860px;
}
.block {
  margin-bottom: 16px;
}
.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.steps {
  padding: 10px 0;
}
.section-label {
  font-weight: 600;
  margin-bottom: 12px;
}
.model-cell {
  display: flex;
  gap: 10px;
  align-items: center;
}
.thumb {
  width: 52px;
  height: 36px;
  border-radius: 4px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 52px;
  height: 36px;
  border-radius: 4px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
  flex-shrink: 0;
}
.total-row {
  text-align: right;
  margin-top: 14px;
}
.total {
  color: #f56c6c;
  font-size: 20px;
}
.addr-row {
  margin-bottom: 6px;
}
.addr-phone {
  margin-left: 30px;
}
.actions {
  margin-top: 16px;
}
</style>
