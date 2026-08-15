<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { pathApi } from '@/api/paths'
import { PATH_STATUS, PATH_TAG, formatDate } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const paths = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    paths.value = await pathApi.my()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="layers" :size="19" /> 我的学习路径</div>

    <el-empty v-if="!loading && paths.length === 0" description="还没有报名任何学习路径">
      <el-button type="primary" @click="router.push('/paths')">浏览学习路径</el-button>
    </el-empty>

    <el-card v-for="p in paths" :key="p.userPathId" class="path-card" shadow="hover"
      @click="router.push(`/paths/${p.pathId}`)">
      <div class="row">
        <el-image v-if="p.coverImageUrl" :src="p.coverImageUrl" fit="cover" class="thumb" />
        <div v-else class="thumb thumb-fallback"><LineIcon name="layers" :size="20" /></div>
        <div class="info">
          <div class="title-row">
            <span class="title">{{ p.pathName }}</span>
            <el-tag size="small" :type="PATH_TAG[p.status] || 'info'">{{ PATH_STATUS[p.status] || p.status }}</el-tag>
            <el-tag size="small" effect="plain">约 {{ p.estimatedHours }} 小时</el-tag>
          </div>
          <div class="text-muted">报名于 {{ formatDate(p.enrollDate) }}
            <template v-if="p.completedDate"> · 完成于 {{ formatDate(p.completedDate) }}</template>
          </div>
          <el-progress :percentage="Math.round(p.progress || 0)" :stroke-width="10" class="bar" />
        </div>
        <el-button type="primary" plain>{{ p.status === 'Completed' ? '查看路径' : '继续学习' }}</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.path-card {
  margin-bottom: 12px;
  cursor: pointer;
}
.row {
  display: flex;
  gap: 16px;
  align-items: center;
}
.thumb {
  width: 140px;
  height: 80px;
  border-radius: 6px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 140px;
  height: 80px;
  border-radius: 6px;
  background: linear-gradient(135deg, #409eff, #6d8df0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
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
  margin-top: 8px;
}
</style>
