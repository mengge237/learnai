<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { marketApi } from '@/api/market'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()
const { t } = useI18n()

const formRef = ref()
const submitting = ref(false)

const form = reactive({
  recipientName: auth.user?.username || '',
  recipientPhone: auth.user?.phone || '',
  recipientAddress: auth.user?.defaultShippingAddress || [auth.user?.province, auth.user?.city, auth.user?.location].filter(Boolean).join(' ') || '',
})

// 校验文案走 i18n，切换语言后即时更新
const rules = computed(() => ({
  recipientName: [{ required: true, message: t('请输入收货人姓名'), trigger: 'blur' }],
  recipientPhone: [
    { required: true, message: t('请输入联系电话'), trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: t('请输入正确的手机号'), trigger: 'blur' },
  ],
  recipientAddress: [{ required: true, message: t('请输入收货地址'), trigger: 'blur' }],
}))

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const order = await marketApi.createOrder({
      items: cart.items.map((it) => ({ modelId: it.modelId, quantity: it.quantity, licenseType: it.licenseType })),
      recipientName: form.recipientName,
      recipientPhone: form.recipientPhone,
      recipientAddress: form.recipientAddress,
    })
    cart.clear()
    let value
    try {
      ;({ value } = await ElMessageBox.confirm(
        t('订单 #{id} 创建成功，合计 {amount}。是否立即模拟支付？', { id: order.id, amount: formatPrice(order.totalAmount) }),
        t('下单成功'),
        { confirmButtonText: t('去支付'), cancelButtonText: t('稍后支付'), type: 'success' },
      ))
    } catch {
      router.push('/orders')
      return // 用户选择稍后支付
    }
    if (value) {
      await marketApi.pay(order.id)
      ElMessage.success(t('支付成功！等待管理员处理发货'))
      router.push(`/orders/${order.id}`)
    } else {
      router.push('/orders')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page-container narrow">
    <div class="page-title"><LineIcon name="box" :size="19" /> {{ $t('确认订单') }}</div>

    <el-empty v-if="cart.items.length === 0" :description="$t('购物车是空的，无法结算')">
      <el-button type="primary" @click="router.push('/market')">{{ $t('去逛逛') }}</el-button>
    </el-empty>

    <template v-else>
      <el-card class="block">
        <div class="section-label"><LineIcon name="box" :size="15" /> {{ $t('商品清单') }}</div>
        <div v-for="it in cart.items" :key="it.modelId" class="item-row">
          <el-image v-if="it.previewUrl" :src="it.previewUrl" fit="cover" class="thumb" />
          <div v-else class="thumb thumb-fallback" />
          <span class="name">{{ it.name }}</span>
          <span class="text-muted">{{ $t(it.licenseType + '授权') }} × {{ it.quantity }}</span>
          <span class="subtotal">{{ formatPrice(Number(it.price) * it.quantity) }}</span>
        </div>
        <div class="total-row">
          <span class="text-muted">{{ $t('服务端将按商品原价重新计价') }}</span>
          <span>{{ $t('合计：') }}<b class="total">{{ formatPrice(cart.totalPrice) }}</b></span>
        </div>
      </el-card>

      <el-card class="block">
        <div class="section-label"><LineIcon name="user" :size="15" /> {{ $t('收货信息') }}</div>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
          <el-form-item :label="$t('收货人')" prop="recipientName">
            <el-input v-model="form.recipientName" maxlength="50" />
          </el-form-item>
          <el-form-item :label="$t('联系电话')" prop="recipientPhone">
            <el-input v-model="form.recipientPhone" maxlength="20" />
          </el-form-item>
          <el-form-item :label="$t('收货地址')" prop="recipientAddress">
            <el-input v-model="form.recipientAddress" type="textarea" :rows="2" maxlength="200" />
          </el-form-item>
          <el-form-item>
            <el-button type="danger" size="large" :loading="submitting" @click="submit">{{ $t('提交订单') }}</el-button>
            <el-button size="large" @click="router.push('/cart')">{{ $t('返回购物车') }}</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.narrow {
  max-width: 800px;
}
.block {
  margin-bottom: 16px;
}
.section-label {
  font-weight: 600;
  margin-bottom: 14px;
}
.item-row {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed var(--border-color);
}
.thumb {
  width: 56px;
  height: 38px;
  border-radius: 4px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 56px;
  height: 38px;
  border-radius: 4px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
  flex-shrink: 0;
}
.name {
  flex: 1;
  font-weight: 500;
}
.subtotal {
  font-weight: 600;
}
.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
}
.total {
  color: #f56c6c;
  font-size: 22px;
}
</style>
