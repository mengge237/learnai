<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api/admin'
import { formatPrice } from '@/utils/format'

const router = useRouter()
const stats = ref(null)
const loading = ref(true)

const cards = [
  { key: 'userCount', label: '注册用户', icon: '👥' },
  { key: 'resourceCount', label: '学习资源', icon: '📚' },
  { key: 'pendingResourceCount', label: '待审资源', icon: '⏳', warn: true },
  { key: 'modelCount', label: '3D 模型', icon: '🧊' },
  { key: 'pendingModelCount', label: '待审模型', icon: '⏳', warn: true },
  { key: 'orderCount', label: '订单总数', icon: '📦' },
  { key: 'pendingOrderCount', label: '待处理订单', icon: '📋', warn: true },
  { key: 'completedOrderCount', label: '已完成订单', icon: '✅' },
  { key: 'commentCount', label: '评论数', icon: '💬' },
  { key: 'favoriteCount', label: '收藏数', icon: '⭐' },
  { key: 'downloadCount', label: '下载数', icon: '⬇️' },
]

onMounted(async () => {
  try {
    stats.value = await adminApi.stats()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="page-title">📈 平台数据总览</div>

    <template v-if="stats">
      <el-row :gutter="16">
        <el-col v-for="c in cards" :key="c.key" :xs="12" :sm="8" :md="6" :lg="4" class="stat-col">
          <el-card class="stat-card" :class="{ warn: c.warn && stats[c.key] > 0 }">
            <div class="icon">{{ c.icon }}</div>
            <div class="value">{{ stats[c.key] }}</div>
            <div class="label text-muted">{{ c.label }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6" :lg="4" class="stat-col">
          <el-card class="stat-card sales">
            <div class="icon">💰</div>
            <div class="value">{{ formatPrice(stats.totalSalesAmount) }}</div>
            <div class="label text-muted">总销售额</div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="quick-row">
        <el-col :span="12">
          <el-card shadow="hover" @click="router.push('/admin/users')">
            <div class="quick">👥 用户管理</div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" @click="router.push('/audit')">
            <div class="quick">🛡 内容审核</div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<style scoped>
.stat-col {
  margin-bottom: 16px;
}
.stat-card {
  text-align: center;
  padding: 6px 0;
}
.stat-card.warn {
  border: 1px solid #e6a23c;
}
.stat-card.sales {
  border: 1px solid #f56c6c;
}
.icon {
  font-size: 26px;
}
.value {
  font-size: 24px;
  font-weight: 700;
  margin: 6px 0 2px;
}
.quick-row .el-card {
  cursor: pointer;
  text-align: center;
}
.quick {
  font-weight: 600;
}
</style>
