<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { aiApi } from '@/api/ai'
import { studyApi } from '@/api/study'
import { useAuthStore } from '@/stores/auth'
import { LEARNING_STATUS, formatDate } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const auth = useAuthStore()
const data = ref(null)
const study = ref(null)
const loading = ref(true)

const quickEntries = [
  { icon: 'book', label: '我的学习', path: '/resources/my' },
  { icon: 'layers', label: '学习路径', path: '/paths' },
  { icon: 'chart', label: '学习分析', path: '/ai/analytics' },
  { icon: 'sparkle', label: '智能推荐', path: '/ai/recommend' },
  { icon: 'chat', label: '在线答疑', path: '/ai/chat' },
  { icon: 'star', label: '我的收藏', path: '/user/favorites' },
  { icon: 'download', label: '下载历史', path: '/user/downloads' },
  { icon: 'box', label: '我的订单', path: '/market/orders' },
  { icon: 'cube', label: '模型资源库', path: '/market' },
  { icon: 'upload', label: '提交资源', path: '/resources/submit' },
]

const stats = [
  { key: 'totalLearningResources', label: '学习资源', suffix: '个' },
  { key: 'totalCompleted', label: '已完成', suffix: '个' },
  { key: 'totalInProgress', label: '进行中', suffix: '个' },
  { key: 'totalInteractions', label: '答疑互动', suffix: '次' },
  { key: 'averageProgress', label: '平均进度', suffix: '%', precision: 1 },
  { key: 'totalLearningMinutes', label: '累计时长', suffix: '分钟' },
]

const weekMax = computed(() => Math.max(1, ...(study.value?.week || []).map((d) => d.minutes)))

onMounted(async () => {
  try {
    ;[data.value, study.value] = await Promise.all([aiApi.analytics(), studyApi.stats()])
  } catch {
    // 部分接口失败不影响控制台展示
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="console-head">
      <div class="page-title" style="margin-bottom: 8px"><LineIcon name="monitor" :size="19" /> 个人控制台</div>
      <div class="console-sub text-muted">
        {{ auth.user?.username }}
        <template v-if="auth.user?.studentNo">· 学号 {{ auth.user.studentNo }}</template>
        · {{ auth.user?.roleName || '普通用户' }} · 欢迎回来，今天也要好好学习
      </div>
    </div>

    <!-- ============ 学习激励 ============ -->
    <template v-if="study">
      <div class="study-grid">
        <div class="study-card">
          <div class="sc-label">今日学习</div>
          <div class="sc-value">{{ study.todayMinutes }}<span class="sc-unit">分钟</span></div>
          <div class="sc-sub text-muted">累计 {{ study.totalMinutes }} 分钟</div>
        </div>
        <div class="study-card">
          <div class="sc-label">连续学习</div>
          <div class="sc-value flame"><LineIcon name="flame" :size="24" /> {{ study.streakDays }}<span class="sc-unit">天</span></div>
          <div class="sc-sub text-muted">坚持就是胜利</div>
        </div>
        <div class="study-card" :class="{ online: study.isStudying }">
          <div class="sc-label">学习状态</div>
          <div class="sc-value sc-state">
            {{ study.isStudying ? '● 正在学习' : '○ 空闲' }}
          </div>
          <div class="sc-sub text-muted">
            {{ study.isStudying && study.currentResourceTitle ? `《${study.currentResourceTitle}》` : '去学习页开始计时吧' }}
          </div>
        </div>
        <div class="study-card week-card">
          <div class="sc-label">本周学习（分钟）</div>
          <div class="week-bars">
            <div v-for="d in study.week" :key="d.date" class="week-col">
              <div class="week-bar-wrap">
                <div class="week-bar" :style="{ height: `${Math.max(4, (d.minutes / weekMax) * 100)}%` }" />
              </div>
              <span class="week-day">{{ d.label.slice(1) }}</span>
              <span class="week-min">{{ d.minutes }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>

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

      <div class="section-title"><LineIcon name="sparkle" :size="15" /> 快捷入口</div>
      <div class="quick-grid">
        <button v-for="q in quickEntries" :key="q.path" class="quick-item" @click="router.push(q.path)">
          <span class="quick-icon"><LineIcon :name="q.icon" :size="22" /></span>
          <span class="quick-label">{{ q.label }}</span>
        </button>
      </div>

      <div class="section-title"><LineIcon name="clock" :size="15" /> 最近学习</div>
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
        <div class="section-title"><LineIcon name="settings" :size="15" /> 管理入口</div>
        <div class="quick-grid">
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin')">
            <span class="quick-icon"><LineIcon name="monitor" :size="22" /></span><span class="quick-label">管理后台</span>
          </button>
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/dashboard')">
            <span class="quick-icon"><LineIcon name="chart" :size="22" /></span><span class="quick-label">数据看板</span>
          </button>
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/users')">
            <span class="quick-icon"><LineIcon name="user" :size="22" /></span><span class="quick-label">用户管理</span>
          </button>
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/resources')">
            <span class="quick-icon"><LineIcon name="book" :size="22" /></span><span class="quick-label">资源管理</span>
          </button>
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/models')">
            <span class="quick-icon"><LineIcon name="cube" :size="22" /></span><span class="quick-label">模型管理</span>
          </button>
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/orders')">
            <span class="quick-icon"><LineIcon name="box" :size="22" /></span><span class="quick-label">订单管理</span>
          </button>
          <button v-if="auth.isAdmin" class="quick-item" @click="router.push('/admin/categories')">
            <span class="quick-icon"><LineIcon name="layers" :size="22" /></span><span class="quick-label">分类管理</span>
          </button>
          <button v-if="auth.isAuditorOrAdmin" class="quick-item" @click="router.push('/audit')">
            <span class="quick-icon"><LineIcon name="check" :size="22" /></span><span class="quick-label">审核工作台</span>
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
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
}
/* ================= 学习激励卡片 ================= */
.study-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}
.study-card {
  border: 1px solid var(--border-color);
  border-top: 3px solid var(--line-color);
  border-radius: 2px;
  background: var(--el-bg-color);
  padding: 14px 16px;
}
.study-card.online {
  border-top-color: #67c23a;
}
.sc-label {
  font-size: 12px;
  letter-spacing: 2px;
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
}
.sc-value {
  font-size: 26px;
  font-weight: 800;
  font-family: 'Consolas', 'Courier New', monospace;
}
.sc-unit {
  font-size: 13px;
  font-weight: 400;
  margin-left: 4px;
}
.flame {
  color: #e8590c;
}
.sc-state {
  font-size: 20px;
  letter-spacing: 1px;
}
.study-card.online .sc-state {
  color: #67c23a;
}
.sc-sub {
  font-size: 12px;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
/* 周柱状图 */
.week-card {
  grid-column: span 1;
}
.week-bars {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  height: 84px;
}
.week-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  height: 100%;
}
.week-bar-wrap {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.week-bar {
  width: 60%;
  max-width: 26px;
  min-height: 4px;
  background: var(--line-soft);
  transition: background 0.2s;
}
.week-col:hover .week-bar {
  background: var(--theme-color);
}
.week-day {
  font-size: 11px;
  color: var(--el-text-color-secondary);
}
.week-min {
  font-size: 10px;
  font-family: 'Consolas', 'Courier New', monospace;
  color: var(--el-text-color-secondary);
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
@media (max-width: 900px) {
  .study-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
