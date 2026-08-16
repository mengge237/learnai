<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { pathApi } from '@/api/paths'
import { useAuthStore } from '@/stores/auth'
import { PATH_DIFFICULTY, PATH_STATUS, PATH_TAG, formatDate, formatCount, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'
import PageBreadcrumb from '@/components/PageBreadcrumb.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const { t } = useI18n()

const id = Number(route.params.id)
const path = ref(null)
const loading = ref(true)
const myPaths = ref([])
const enrollBusy = ref(false)

const enrolled = computed(() => myPaths.value.find((m) => m.pathId === id))

async function load() {
  loading.value = true
  try {
    path.value = await pathApi.detail(id)
    if (auth.isLoggedIn) {
      myPaths.value = await pathApi.my().catch(() => [])
    }
  } finally {
    loading.value = false
  }
}

async function enroll() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  enrollBusy.value = true
  try {
    const mine = await pathApi.enroll(id)
    myPaths.value = [mine, ...myPaths.value.filter((m) => m.pathId !== id)]
    ElMessage.success(t('报名成功！开始你的学习之旅吧'))
  } finally {
    enrollBusy.value = false
  }
}
</script>

<template>
  <div class="page-container" v-loading="loading">
    <PageBreadcrumb
      v-if="path"
      :items="[
        { label: $t('首页'), to: '/' },
        { label: $t('学习路径'), to: '/paths' },
        { label: path.name },
      ]"
    />

    <template v-if="path">
      <el-card class="head-card">
        <div class="head-layout">
          <div class="info">
            <h2 class="title">
              <LineIcon name="layers" :size="16" /> {{ path.name }}
              <el-tag v-if="enrolled" :type="PATH_TAG[enrolled.status] || 'info'">
                {{ $t(PATH_STATUS[enrolled.status] || enrolled.status) }}
              </el-tag>
            </h2>
            <p class="desc">{{ path.description }}</p>
            <div class="meta">
              <span><LineIcon name="clock" :size="14" /> {{ $t('适合人群：{a}', { a: path.targetAudience || $t('不限') }) }}</span>
              <span>{{ $t('难度：{l}', { l: $t(PATH_DIFFICULTY[path.difficultyLevel] || '入门') }) }}</span>
              <span><LineIcon name="clock" :size="14" /> {{ $t('预计 {n} 小时', { n: path.estimatedHours }) }}</span>
              <span><LineIcon name="user" :size="14" /> {{ formatCount(path.viewCount) }} {{ $t('浏览') }}</span>
              <span><LineIcon name="user" :size="14" /> {{ $t('{n} 人已报名', { n: formatCount(path.enrollmentCount) }) }}</span>
              <span><LineIcon name="clock" :size="14" /> {{ $t('创建于 {d}', { d: formatDate(path.createDate) }) }}</span>
            </div>
            <div class="actions">
              <el-button type="primary" size="large" :loading="enrollBusy" @click="enroll">
                <LineIcon name="arrowRight" :size="15" /> {{ enrolled ? $t('再次确认报名') : $t('立即报名') }}
              </el-button>
              <el-button v-if="enrolled" size="large" @click="router.push('/paths/my')">{{ $t('查看我的学习') }}</el-button>
            </div>
          </div>
          <el-image v-if="path.coverImageUrl" :src="path.coverImageUrl" fit="cover" class="cover" />
          <div v-else class="cover cover-fallback"><LineIcon name="layers" :size="20" /></div>
        </div>
      </el-card>

      <el-card class="resources-card">
        <div class="section-label"><LineIcon name="book" :size="15" /> {{ $t('路径资源（{n} 个）', { n: path.resources.length }) }}</div>
        <div v-for="(r, i) in path.resources" :key="r.id" class="resource-row" @click="router.push(`/resources/${r.id}`)">
          <span class="seq">{{ String(i + 1).padStart(2, '0') }}</span>
          <el-image v-if="r.previewUrl || r.thumbnailUrl" :src="r.previewUrl || r.thumbnailUrl" fit="cover" class="thumb" />
          <div v-else class="thumb thumb-fallback" />
          <div class="r-info">
            <div class="r-title">{{ r.title }}</div>
            <div class="text-muted">
              {{ r.difficultyLevel }} · {{ r.durationMinutes }} {{ $t('分钟') }} · {{ r.learningType }} · {{ r.categoryName }}
            </div>
          </div>
          <div class="r-right">
            <el-tag v-if="r.isFree" type="success" effect="plain">{{ $t('免费') }}</el-tag>
            <el-tag v-else type="danger" effect="plain">{{ formatPrice(r.price) }}</el-tag>
            <span class="text-muted"><LineIcon name="heart" :size="14" /> {{ r.likeCount }}</span>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.head-card {
  margin-bottom: 16px;
}
.head-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
}
.title {
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.desc {
  color: var(--el-text-color-regular);
  line-height: 1.7;
}
.meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 12px 0 20px;
}
.cover {
  width: 280px;
  height: 170px;
  border-radius: 8px;
}
.cover-fallback {
  background: linear-gradient(135deg, #409eff, #6d8df0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #fff;
}
.section-label {
  font-weight: 600;
  margin-bottom: 12px;
}
.resource-row {
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 12px 8px;
  border-bottom: 1px dashed var(--border-color);
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.2s;
}
.resource-row:hover {
  background: var(--el-fill-color-light);
}
.seq {
  font-size: 18px;
  font-weight: 700;
  color: var(--theme-color);
  width: 30px;
  text-align: center;
}
.thumb {
  width: 90px;
  height: 56px;
  border-radius: 6px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 90px;
  height: 56px;
  border-radius: 6px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
  flex-shrink: 0;
}
.r-info {
  flex: 1;
}
.r-title {
  font-weight: 600;
  margin-bottom: 4px;
}
.r-right {
  display: flex;
  gap: 12px;
  align-items: center;
}
@media (max-width: 860px) {
  .head-layout {
    grid-template-columns: 1fr;
  }
  .cover {
    width: 100%;
    height: 180px;
  }
}
</style>
