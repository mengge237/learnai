<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { resourceApi } from '@/api/resources'
import { LEARNING_STATUS, LEARNING_TAG, formatDate } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const records = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    records.value = await resourceApi.myLearning()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="book" :size="19" /> 我的学习</div>

    <div class="quick-row">
      <el-button size="small" @click="router.push('/ai/analytics')"><LineIcon name="chart" :size="14" /> 学习分析</el-button>
      <el-button size="small" @click="router.push('/ai/recommend')"><LineIcon name="sparkle" :size="14" /> 智能推荐</el-button>
      <el-button size="small" @click="router.push('/console')"><LineIcon name="monitor" :size="14" /> 个人控制台</el-button>
    </div>

    <el-empty v-if="!loading && records.length === 0" description="还没有学习记录，快去挑一门课程吧">
      <el-button type="primary" @click="router.push('/resources')">浏览学习资源</el-button>
    </el-empty>

    <el-card v-for="r in records" :key="r.resourceId" class="record-card" shadow="hover"
      @click="router.push(`/resources/${r.resourceId}/learn`)">
      <div class="record-row">
        <el-image v-if="r.thumbnailUrl" :src="r.thumbnailUrl" class="thumb" fit="cover" />
        <div v-else class="thumb thumb-fallback" />
        <div class="info">
          <div class="title-row">
            <span class="title">{{ r.title }}</span>
            <el-tag size="small" :type="LEARNING_TAG[r.status] || 'info'">{{ LEARNING_STATUS[r.status] || r.status }}</el-tag>
            <el-tag v-if="r.difficultyLevel" size="small" effect="plain">{{ r.difficultyLevel }}</el-tag>
          </div>
          <div class="text-muted">{{ r.categoryName }} · 开始于 {{ formatDate(r.startTime) }}</div>
          <el-progress :percentage="Math.round(r.progress || 0)" :stroke-width="10" class="bar" />
          <div class="text-muted">
            学习 {{ r.durationMinutes || 0 }} 分钟
            <template v-if="r.score != null"> · <LineIcon name="star" :size="13" /> 得分 {{ r.score }}</template>
          </div>
        </div>
        <el-button type="primary" plain>{{ r.status === 'Completed' ? '查看详情' : '继续学习' }}</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.quick-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.record-card {
  margin-bottom: 12px;
  cursor: pointer;
}
.record-row {
  display: flex;
  gap: 16px;
  align-items: center;
}
.thumb {
  width: 120px;
  height: 76px;
  border-radius: 6px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 120px;
  height: 76px;
  border-radius: 6px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
  flex-shrink: 0;
}
.info {
  flex: 1;
}
.title-row {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 4px;
}
.title {
  font-weight: 600;
  font-size: 16px;
}
.bar {
  margin: 6px 0;
}
</style>
