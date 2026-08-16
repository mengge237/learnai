<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { LICENSE_TYPES, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const cart = useCartStore()
const auth = useAuthStore()
const { t } = useI18n()

function checkout() {
  if (!auth.isLoggedIn) {
    ElMessage.warning(t('请先登录再结算'))
    return router.push({ name: 'login', query: { redirect: '/checkout' } })
  }
  router.push('/checkout')
}

async function clearAll() {
  try {
    await ElMessageBox.confirm(t('确定要清空购物车吗？'), t('提示'), { type: 'warning' })
  } catch {
    return // 用户取消
  }
  cart.clear()
  ElMessage.success(t('购物车已清空'))
}
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="box" :size="19" /> {{ $t('购物车（{n} 件）', { n: cart.totalCount }) }}</div>

    <el-empty v-if="cart.items.length === 0" :description="$t('购物车还是空的，去挑几个模型吧')">
      <el-button type="primary" @click="router.push('/market')">{{ $t('去逛逛') }}</el-button>
    </el-empty>

    <template v-else>
      <el-card class="table-card">
        <el-table :data="cart.items">
          <el-table-column :label="$t('模型')" min-width="220">
            <template #default="{ row }">
              <div class="model-cell">
                <el-image v-if="row.previewUrl" :src="row.previewUrl" fit="cover" class="thumb" />
                <div v-else class="thumb thumb-fallback" />
                <el-link type="primary" @click="router.push(`/market/${row.modelId}`)">{{ row.name }}</el-link>
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('单价')" width="110">
            <template #default="{ row }">{{ formatPrice(row.price) }}</template>
          </el-table-column>
          <el-table-column :label="$t('授权类型')" width="130">
            <template #default="{ row }">
              <el-select :model-value="row.licenseType" size="small" @change="(v) => { row.licenseType = v; cart.persist() }">
                <el-option v-for="lic in LICENSE_TYPES" :key="lic" :label="$t(lic)" :value="lic" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column :label="$t('数量')" width="150">
            <template #default="{ row }">
              <el-input-number :model-value="row.quantity" :min="1" :max="99" size="small"
                @change="(v) => cart.updateQuantity(row.modelId, v)" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('小计')" width="110">
            <template #default="{ row }">{{ formatPrice(Number(row.price) * row.quantity) }}</template>
          </el-table-column>
          <el-table-column :label="$t('操作')" width="90">
            <template #default="{ row }">
              <el-button type="danger" link @click="cart.remove(row.modelId)">{{ $t('移除') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="settle-card">
        <div class="settle-row">
          <el-button link type="danger" @click="clearAll">{{ $t('清空购物车') }}</el-button>
          <div class="total-box">
            <span class="text-muted">{{ $t('合计：') }}</span>
            <span class="total">{{ formatPrice(cart.totalPrice) }}</span>
            <el-button type="danger" size="large" class="settle-btn" @click="checkout">{{ $t('去结算') }}</el-button>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.table-card {
  margin-bottom: 16px;
}
.model-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.thumb {
  width: 60px;
  height: 40px;
  border-radius: 4px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 60px;
  height: 40px;
  border-radius: 4px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
  flex-shrink: 0;
}
.settle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.total-box {
  display: flex;
  align-items: center;
  gap: 10px;
}
.total {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}
.settle-btn {
  margin-left: 8px;
}
</style>
