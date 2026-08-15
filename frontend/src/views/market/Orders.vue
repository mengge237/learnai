<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { marketApi } from '@/api/market'
import { ORDER_STATUS, ORDER_TAG, formatDate, formatPrice } from '@/utils/format'

const router = useRouter()
const orders = ref([])
const loading = ref(true)

async function load() {
  loading.value = true
  try {
    orders.value = await marketApi.myOrders()
  } finally {
    loading.value = false
  }
}

async function pay(order) {
  await marketApi.pay(order.id)
  ElMessage.success('支付成功！等待管理员处理')
  load()
}

async function cancel(order) {
  await ElMessageBox.confirm(`确定取消订单 #${order.id} 吗？`, '提示', { type: 'warning' })
  await marketApi.cancel(order.id)
  ElMessage.success('订单已取消')
  load()
}

onMounted(load)
</script>

<template>
  <div class="page-container narrow">
    <div class="page-title">📦 我的订单</div>

    <el-empty v-if="!loading && orders.length === 0" description="还没有订单，去商城逛逛吧">
      <el-button type="primary" @click="router.push('/market')">去逛逛</el-button>
    </el-empty>

    <el-card v-for="o in orders" :key="o.id" class="order-card" shadow="hover">
      <div class="head-row">
        <div>
          <span class="order-id">订单 #{{ o.id }}</span>
          <span class="text-muted"> · {{ formatDate(o.orderDate) }}</span>
        </div>
        <el-tag :type="ORDER_TAG[o.status] || 'info'">{{ ORDER_STATUS[o.status] || o.status }}</el-tag>
      </div>
      <div v-for="it in o.items" :key="it.orderItemId" class="item-row">
        <el-image v-if="it.previewUrl" :src="it.previewUrl" fit="cover" class="thumb" />
        <div v-else class="thumb thumb-fallback" />
        <el-link type="primary" @click="router.push(`/market/${it.modelId}`)">{{ it.modelName }}</el-link>
        <span class="text-muted">{{ it.licenseType }}授权 × {{ it.quantity }}</span>
        <span class="subtotal">{{ formatPrice(it.subtotal) }}</span>
      </div>
      <div class="foot-row">
        <div class="actions">
          <el-button v-if="o.status === 'PendingPayment'" type="danger" size="small" @click="pay(o)">💰 立即支付</el-button>
          <el-button v-if="['PendingPayment', 'Pending'].includes(o.status)" size="small" @click="cancel(o)">取消订单</el-button>
        </div>
        <div>
          <span class="text-muted">合计：</span>
          <b class="total">{{ formatPrice(o.totalAmount) }}</b>
          <el-button type="primary" plain size="small" class="detail-btn" @click="router.push(`/orders/${o.id}`)">查看详情</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.narrow {
  max-width: 860px;
}
.order-card {
  margin-bottom: 14px;
}
.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 8px;
}
.order-id {
  font-weight: 600;
}
.item-row {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 6px 0;
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
.subtotal {
  margin-left: auto;
  font-weight: 500;
}
.foot-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.total {
  color: #f56c6c;
  font-size: 18px;
}
.detail-btn {
  margin-left: 10px;
}
</style>
