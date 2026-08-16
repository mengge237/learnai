<script setup>
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { aiApi } from '@/api/ai'
import ResourceCard from '@/components/ResourceCard.vue'
import LineIcon from '@/components/LineIcon.vue'

const data = ref(null)
const loading = ref(true)
const { t } = useI18n()

const basedOnText = computed(() => {
  const b = data.value?.basedOn || ''
  if (b === 'popular') return t('根据全站热门资源推荐')
  if (b === 'history') return t('根据你的学习历史推荐')
  if (b.startsWith('category:')) return t('根据你关注的分类「{name}」推荐', { name: b.slice(9) })
  return t('为你推荐')
})

onMounted(async () => {
  try {
    data.value = await aiApi.recommend()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="page-title"><LineIcon name="sparkle" :size="19" /> {{ $t('智能推荐') }}</div>
    <el-alert v-if="data" :title="basedOnText" type="success" :closable="false" class="tip" show-icon />

    <div v-if="data && data.recommendations.length" class="res-grid">
      <ResourceCard v-for="r in data.recommendations" :key="r.id" :resource="r" />
    </div>
    <el-empty v-if="data && !data.recommendations.length" :description="$t('暂无推荐，先去学习一些资源吧')">
      <el-button type="primary" @click="$router.push('/resources')">{{ $t('浏览学习资源') }}</el-button>
    </el-empty>
  </div>
</template>

<style scoped>
.tip {
  margin-bottom: 16px;
}
.res-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}
</style>
