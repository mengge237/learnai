<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { formatPrice } from '@/utils/format'

const props = defineProps({
  model: { type: Object, required: true },
})
const router = useRouter()
const cart = useCartStore()

function addToCart(e) {
  e.stopPropagation()
  if (props.model.isApproved === false) {
    ElMessage.warning('该模型尚未通过审核')
    return
  }
  cart.add(props.model)
  ElMessage.success('已加入购物车')
}
</script>

<template>
  <el-card class="model-card" shadow="hover" :body-style="{ padding: 0 }" @click="router.push(`/market/${model.id}`)">
    <div class="cover-wrap">
      <el-image v-if="model.previewUrl" :src="model.previewUrl" fit="cover" class="cover" lazy />
      <div v-else class="cover cover-fallback" :style="{ background: `linear-gradient(135deg, hsl(${(model.id || 0) * 53 % 360} 55% 60%), hsl(${(model.id || 0) * 53 % 360 + 50} 55% 38%))` }" />
      <span v-if="Number(model.price) === 0" class="badge badge-free">免费</span>
      <span v-else class="badge badge-paid">{{ formatPrice(model.price) }}</span>
    </div>
    <div class="body">
      <div class="title" :title="model.name">{{ model.name }}</div>
      <div class="meta text-muted">
        <span>{{ model.categoryName }}</span>
        <span v-if="model.creator">· {{ model.creator }}</span>
      </div>
      <div class="actions">
        <el-button size="small" type="primary" plain @click="addToCart">🛒 加入购物车</el-button>
        <el-button size="small" @click.stop="router.push(`/market/${model.id}`)">详情</el-button>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.model-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.model-card:hover {
  transform: translateY(-4px);
}
.cover-wrap {
  position: relative;
  height: 150px;
}
.cover {
  width: 100%;
  height: 150px;
  display: block;
}
.cover-fallback {
  width: 100%;
  height: 150px;
}
.badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}
.badge-free {
  background: #67c23a;
}
.badge-paid {
  background: #f56c6c;
}
.body {
  padding: 12px;
}
.title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta {
  margin-bottom: 10px;
  display: flex;
  gap: 4px;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>
