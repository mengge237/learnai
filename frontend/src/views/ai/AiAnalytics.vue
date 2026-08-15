<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { aiApi } from '@/api/ai'
import { LEARNING_STATUS, formatDate } from '@/utils/format'

const router = useRouter()
const data = ref(null)
const loading = ref(true)

const maxWeekly = computed(() => {
  const vals = (data.value?.weeklyStats || []).map((w) => Math.max(w.totalLearning, w.completed, 1))
  return vals.length ? Math.max(...vals, 1) : 1
})

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
    <div class="page-title">📊 学习分析</div>

    <template v-if="data">
      <el-row :gutter="16" class="stat-row">
        <el-col :span="8"><el-card><el-statistic title="学习资源总数" :value="data.totalLearningResources" /></el-card></el-col>
        <el-col :span="8"><el-card><el-statistic title="已完成" :value="data.totalCompleted" /></el-card></el-col>
        <el-col :span="8"><el-card><el-statistic title="进行中" :value="data.totalInProgress" /></el-card></el-col>
        <el-col :span="8"><el-card><el-statistic title="答疑互动" :value="data.totalInteractions" suffix="次" /></el-card></el-col>
        <el-col :span="8"><el-card><el-statistic title="平均进度" :value="data.averageProgress" suffix="%" :precision="1" /></el-card></el-col>
        <el-col :span="8"><el-card><el-statistic title="累计学习时长" :value="data.totalLearningMinutes" suffix="分钟" /></el-card></el-col>
      </el-row>

      <el-card class="block">
        <div class="section-label">📅 近 7 天学习情况</div>
        <div class="week-chart">
          <div v-for="w in data.weeklyStats" :key="w.date" class="week-col">
            <div class="week-value text-muted">{{ w.totalLearning }}</div>
            <div class="bar-wrap">
              <div class="bar learn" :style="{ height: `${(w.totalLearning / maxWeekly) * 100}%` }" />
            </div>
            <div class="bar-wrap">
              <div class="bar done" :style="{ height: `${(w.completed / maxWeekly) * 100}%` }" />
            </div>
            <div class="week-day text-muted">{{ w.dayName }}</div>
          </div>
        </div>
        <div class="legend text-muted">
          <span><i class="dot learn" /> 学习次数</span>
          <span><i class="dot done" /> 完成次数</span>
        </div>
      </el-card>

      <el-row :gutter="16">
        <el-col :xs="24" :md="10">
          <el-card class="block">
            <div class="section-label">🗂 分类统计</div>
            <el-table :data="data.categoryStats" size="small">
              <el-table-column prop="categoryName" label="分类" />
              <el-table-column prop="totalResources" label="资源数" width="80" align="center" />
              <el-table-column prop="completedResources" label="已完成" width="80" align="center" />
              <el-table-column label="平均进度" width="110" align="center">
                <template #default="{ row }">
                  <el-progress :percentage="Math.round(row.avgProgress)" :stroke-width="8" />
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="14">
          <el-card class="block">
            <div class="section-label">🕘 最近学习记录</div>
            <el-table :data="data.recentRecords" size="small">
              <el-table-column label="资源" min-width="180">
                <template #default="{ row }">
                  <el-link type="primary" @click="router.push(`/resources/${row.resourceId}`)">{{ row.title }}</el-link>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="90" align="center">
                <template #default="{ row }">{{ LEARNING_STATUS[row.status] || row.status }}</template>
              </el-table-column>
              <el-table-column label="进度" width="100" align="center">
                <template #default="{ row }">
                  <el-progress :percentage="Math.round(row.progress || 0)" :stroke-width="8" />
                </template>
              </el-table-column>
              <el-table-column label="开始时间" width="140">
                <template #default="{ row }">{{ formatDate(row.startTime) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<style scoped>
.stat-row .el-col {
  margin-bottom: 16px;
}
.block {
  margin-bottom: 16px;
}
.section-label {
  font-weight: 600;
  margin-bottom: 14px;
}
.week-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 180px;
  padding: 0 10px;
}
.week-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex: 1;
  height: 100%;
}
.week-value {
  font-size: 12px;
}
.bar-wrap {
  width: 18px;
  flex: 1;
  display: flex;
  align-items: flex-end;
}
.bar {
  width: 100%;
  border-radius: 4px 4px 0 0;
  min-height: 2px;
  transition: height 0.4s;
}
.bar.learn {
  background: var(--theme-color);
}
.bar.done {
  background: #67c23a;
}
.week-day {
  font-size: 12px;
}
.legend {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 10px;
}
.dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 4px;
}
.dot.learn {
  background: var(--theme-color);
}
.dot.done {
  background: #67c23a;
}
</style>
