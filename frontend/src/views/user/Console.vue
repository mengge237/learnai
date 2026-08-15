<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { aiApi } from '@/api/ai'
import { useAuthStore } from '@/stores/auth'
import { LEARNING_STATUS, formatDate } from '@/utils/format'

const router = useRouter()
const auth = useAuthStore()
const data = ref(null)
const loading = ref(true)

const quickEntries = [
  { icon: '📚', label: '我的学习', path: '/resources/my' },
  { icon: '🗺️', label: '学习路径', path: '/paths' },
  { icon: '📊', label: '学习分析', path: '/ai/analytics' },
  { icon: '🧭', label: '智能推荐', path: '/ai/recommend' },
  { icon: '💬', label: 'AI 答疑', path: '/ai/chat' },
  { icon: '⭐', label: '我的收藏', path: '/user/favorites' },
  { icon: '⬇️', label: '下载历史', path: '/user/downloads' },
  { icon: '📦', label: '我的订单', path: '/market/orders' },
  { icon: '🏪', label: '模型资源库', path: '/market' },
  { icon: '📤', label: '提交资源', path: '/resources/submit' },
]

const stats = [
  { key: 'totalLearningResources', label: '学习资源', suffix: '个' },
  { key: 'totalCompleted', label: '已完成', suffix: '个' },
  { key: 'totalInProgress', label: '进行中', suffix: '个' },
  { key: 'totalAIInteractions', label: 'AI 交互', suffix: '次' },
  { key: 'averageProgress', label: '平均进度', suffix: '%', precision: 1 },
  { key: 'totalLearningMinutes', label: '累计时长', suffix: '分钟' },
]

onMounted(async () => {
  try {
    data.value = await aiApi.analytics()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="console-head">
      <div class="page-title" style="margin-bottom: 8px">🎛 个人控制台</div>
      <div class="console-sub text-muted">
        {{ auth.user?.username }}
        <template v-if="auth.user?.studentNo">· 学号 {{ auth.user.studentNo }}</template>
        · {{ auth.user?.roleName || '普通用户' }} · 欢迎回来，今天也要好好学习
      </div>
    </div>

    <template v-if="data">
      <div class="stat-grid">
        <div v-for="s in stats" :key="s.key" class="stat-card">
          <div class="stat-value">
            {{ s.precision ? Number(data[s.key] || 0).toFixed(s.precision) : (data[s.key] ?? 0) }}
            <span class="stat-suffix">{{ s.suffix }}</span>
          </div>
          <div class="stat-label text-muted">{{ s.label }}</div>
        </div>
      </div>

      <div class="section-title">⚡ 快捷入口</div>
      <div class="quick-grid">
        <button v-for="q in quickEntries" :key="q.path" class="quick-item" @click="router.push(q.path)">
          <span class="quick-icon">{{ q.icon }}</span>
          <span class="quick-label">{{ q.label }}</span>
        </button>
      </div>

      <div class="section-title">🕘 最近学习</div>
      <el-card>
        <el-table v-if="data.recentRecords?.length" :data="data.recentRecords" size="small">
          <el-table-column label="资源" min-width="220">
            <template #default="{ row }">
              <el-link type="primary" @click="router.push(`/resources/${row.resourceId}`)">{{ row.title }}</el-link>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">{{ LEARNING_STATUS[row.status] || row.status }}</template>
          </el-table-column>
          <el-table-column label="进度" width="120" align="center">
            <template #default="{ row }">
              <el-progress :percentage="Math.round(row.progress || 0)" :stroke-width="8" />
            </template>
          </el-table-column>
          <el-table-column label="开始时间" width="150">
            <template #default="{ row }">{{ formatDate(row.startTime) }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="还没有学习记录，快去学习资源看看吧" :image-size="80" />
      </el-card>

      <template v-if="auth.isAuditorOrAdmin">
        <div class="section-title">🛠 管理入口</div>
        <div class="quick-grid">
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/dashboard')">
            <span class="quick-icon">📈</span><span class="quick-label">数据看板</span>
          </button>
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/users')">
            <span class="quick-icon">👥</span><span class="quick-label">用户管理</span>
          </button>
          <button v-if="auth.isAuditorOrAdmin" class="quick-item" @click="router.push('/audit')">
            <span class="quick-icon">✅</span><span class="quick-label">内容审核</span>
          </button>
        </div>
      </template>
    </template>
  </div>
</template>

<style scoped>
.console-sub {
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
}
.stat-suffix {
  font-size: 12px;
  font-weight: 400;
  color: var(--el-text-color-secondary);
  margin-left: 2px;
}
.stat-label {
  margin-top: 4px;
  font-size: 13px;
}
.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 12px;
}
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 18px 8px;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  background: var(--el-bg-color);
  cursor: pointer;
  transition: all 0.15s;
}
.quick-item:hover {
  border-color: var(--theme-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}
.quick-icon {
  font-size: 26px;
  line-height: 1;
}
.quick-label {
  font-size: 13px;
  letter-spacing: 1px;
}
</style>
