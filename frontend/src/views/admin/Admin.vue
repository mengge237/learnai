<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminApi } from '@/api/admin'
import { formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const stats = ref(null)
const loading = ref(true)

const entries = [
  { icon: 'chart', label: '数据看板', desc: '平台数据总览', path: '/admin/dashboard' },
  { icon: 'user', label: '用户管理', desc: '账号与角色维护', path: '/admin/users' },
  { icon: 'book', label: '资源管理', desc: '学习资源审核与上下架', path: '/admin/resources' },
  { icon: 'cube', label: '模型管理', desc: '3D 模型审核与上下架', path: '/admin/models' },
  { icon: 'box', label: '订单管理', desc: '订单状态推进', path: '/admin/orders' },
  { icon: 'layers', label: '分类管理', desc: '资源分类维护', path: '/admin/categories' },
  { icon: 'check', label: '内容审核', desc: '待审内容与审核历史', path: '/audit' },
]

const overview = [
  { key: 'userCount', label: '注册用户' },
  { key: 'resourceCount', label: '学习资源' },
  { key: 'modelCount', label: '3D 模型' },
  { key: 'orderCount', label: '订单总数' },
  { key: 'completedOrderCount', label: '已完成订单' },
  { key: 'totalSalesAmount', label: '总销售额', price: true },
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
    <div class="page-title"><LineIcon name="monitor" :size="19" /> 管理后台</div>
    <div class="admin-sub text-muted">管理员控制台 · 平台运营与内容治理</div>

    <!-- 平台概览 -->
    <div v-if="stats" class="stat-grid">
      <div v-for="o in overview" :key="o.key" class="stat-card">
        <div class="stat-value">{{ o.price ? formatPrice(stats[o.key]) : stats[o.key] }}</div>
        <div class="stat-label text-muted">{{ o.label }}</div>
      </div>
    </div>

    <!-- 管理入口 -->
    <div v-if="stats" class="section-title"><LineIcon name="settings" :size="15" /> 管理入口</div>
    <div v-if="stats" class="entry-grid">
      <button v-for="e in entries" :key="e.path" class="entry-item" @click="router.push(e.path)">
        <span class="entry-icon"><LineIcon :name="e.icon" :size="24" /></span>
        <span class="entry-label">{{ e.label }}</span>
        <span class="entry-desc text-muted">{{ e.desc }}</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.admin-sub {
  margin-bottom: 20px;
  letter-spacing: 1px;
}
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
.stat-value {
  font-size: 24px;
  font-weight: 700;
  font-family: 'Consolas', 'Courier New', monospace;
}
.stat-label {
  margin-top: 4px;
  font-size: 13px;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0 12px;
}
.entry-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}
.entry-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding: 18px 16px;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  background: var(--el-bg-color);
  cursor: pointer;
  transition: all 0.15s;
  font-family: inherit;
  text-align: left;
}
.entry-item:hover {
  border-color: var(--theme-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}
.entry-icon {
  font-size: 26px;
  line-height: 1;
}
.entry-label {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 1px;
}
.entry-desc {
  font-size: 12px;
  line-height: 1.4;
}
</style>
