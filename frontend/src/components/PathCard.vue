<script setup>
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { PATH_DIFFICULTY } from '@/utils/format'
import LineIcon from './LineIcon.vue'

const props = defineProps({
  path: { type: Object, required: true },
})
const router = useRouter()
const { t } = useI18n()
</script>

<template>
  <el-card class="path-card" shadow="hover" @click="router.push(`/paths/${path.id}`)">
    <div class="head">
      <span class="icon"><LineIcon name="layers" :size="22" /></span>
      <div>
        <div class="title">{{ path.name }}</div>
        <div class="text-muted">
          {{ t(PATH_DIFFICULTY[path.difficultyLevel] || '入门') }} · {{ $t('约 {n} 小时', { n: path.estimatedHours }) }}
        </div>
      </div>
    </div>
    <div class="desc text-muted">{{ path.description }}</div>
    <div class="foot">
      <span class="text-muted">{{ $t('{n} 个资源', { n: path.resourceCount || 0 }) }}</span>
      <span class="text-muted"><LineIcon name="user" :size="13" /> {{ $t('{n} 人已报名', { n: path.enrollmentCount }) }}</span>
    </div>
  </el-card>
</template>

<style scoped>
.path-card {
  cursor: pointer;
}
.head {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}
.icon {
  font-size: 26px;
}
.title {
  font-size: 16px;
  font-weight: 600;
}
.desc {
  font-size: 13px;
  line-height: 1.5;
  min-height: 40px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.foot {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}
</style>
