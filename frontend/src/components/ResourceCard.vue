<script setup>
import { useRouter } from 'vue-router'
import { formatCount, formatPrice } from '@/utils/format'
import LineIcon from './LineIcon.vue'

const props = defineProps({
  resource: { type: Object, required: true },
})
const router = useRouter()
</script>

<template>
  <el-card class="res-card corner-brackets" shadow="hover" :body-style="{ padding: 0 }" @click="router.push(`/resources/${resource.id}`)">
    <div class="cover-wrap">
      <el-image v-if="resource.previewUrl" :src="resource.previewUrl" fit="cover" class="cover" lazy />
      <div v-else class="cover cover-fallback" :style="{ background: `linear-gradient(135deg, hsl(${(resource.id || 0) * 37 % 360} 60% 62%), hsl(${(resource.id || 0) * 37 % 360 + 40} 60% 42%))` }" />
      <span v-if="resource.isFree" class="badge badge-free">{{ $t('免费') }}</span>
      <span v-else class="badge badge-paid">{{ formatPrice(resource.price) }}</span>
    </div>
    <div class="body">
      <div class="title" :title="resource.title">{{ resource.title }}</div>
      <div class="meta">
        <el-tag size="small" type="info" effect="plain">{{ resource.categoryName }}</el-tag>
        <el-tag v-if="resource.difficultyLevel" size="small" effect="plain">{{ resource.difficultyLevel }}</el-tag>
      </div>
      <div class="stats text-muted">
        <span>{{ formatCount(resource.viewCount) }} {{ $t('浏览') }}</span>
        <span><LineIcon name="heart" :size="13" /> {{ formatCount(resource.likeCount) }}</span>
        <span><LineIcon name="user" :size="13" /> {{ formatCount(resource.completionCount) }} {{ $t('人学过') }}</span>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.res-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  /* 四角取景框：默认隐藏，hover 显现 */
  --cf-color: var(--theme-color);
  --cf-opacity: 0;
  --cf-inset: -5px;
  --cf-size: 12px;
}
.res-card:hover {
  transform: translateY(-4px);
  --cf-opacity: 1;
  box-shadow: 6px 6px 0 color-mix(in srgb, var(--line-color) 70%, transparent);
}
.cover-wrap {
  position: relative;
  height: 140px;
}
.cover {
  width: 100%;
  height: 140px;
  display: block;
}
.cover-fallback {
  width: 100%;
  height: 140px;
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
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}
.stats {
  display: flex;
  gap: 12px;
}
</style>
