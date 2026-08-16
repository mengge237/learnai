<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { searchApi } from '@/api/search'
import LineIcon from '@/components/LineIcon.vue'

/**
 * 全局搜索结果页：跨学习资源 / 学习路径 / 3D 模型分组展示
 */
const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const keyword = ref('')
const loading = ref(false)
const result = reactive({ resources: [], paths: [], models: [], resourceTotal: 0, pathTotal: 0, modelTotal: 0 })

const GROUPS = computed(() => [
  { key: 'resources', totalKey: 'resourceTotal', label: t('学习资源'), icon: 'book', route: (i) => `/resources/${i.id}`, more: '/resources' },
  { key: 'paths', totalKey: 'pathTotal', label: t('学习路径'), icon: 'layers', route: (i) => `/paths/${i.id}`, more: '/paths' },
  { key: 'models', totalKey: 'modelTotal', label: t('3D 模型'), icon: 'cube', route: (i) => `/market/${i.id}`, more: '/market' },
])

async function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  loading.value = true
  try {
    Object.assign(result, await searchApi.search(kw))
  } finally {
    loading.value = false
  }
}

function submit() {
  const kw = keyword.value.trim()
  if (!kw) return
  router.replace({ path: '/search', query: { q: kw } })
}

watch(
  () => route.query.q,
  (q) => {
    if (q) {
      keyword.value = String(q)
      doSearch()
    }
  },
)

onMounted(() => {
  if (route.query.q) {
    keyword.value = String(route.query.q)
    doSearch()
  }
})
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="search" :size="19" /> {{ $t('全局搜索') }}</div>

    <div class="search-bar">
      <el-input
        v-model="keyword"
        size="large"
        :placeholder="$t('搜索课程、学习路径、3D 模型…')"
        clearable
        @keyup.enter="submit"
        @clear="() => {}"
      >
        <template #prefix><LineIcon name="search" :size="16" /></template>
        <template #append>
          <el-button :loading="loading" @click="submit">{{ $t('搜索') }}</el-button>
        </template>
      </el-input>
    </div>

    <div v-loading="loading" class="results">
      <template v-if="route.query.q">
        <el-empty
          v-if="result.resourceTotal + result.pathTotal + result.modelTotal === 0 && !loading"
          :description="$t('没有找到与「{q}」相关的内容，换个关键词试试', { q: route.query.q })"
        />
        <section v-for="g in GROUPS" :key="g.key" class="group" :class="{ hidden: result[g.totalKey] === 0 }">
          <div class="group-head">
            <span class="group-title">
              <LineIcon :name="g.icon" :size="16" />
              {{ g.label }}
              <em class="group-count">{{ result[g.totalKey] }}</em>
            </span>
            <span class="group-line" />
            <router-link v-if="result[g.totalKey] > result[g.key].length" :to="{ path: g.more, query: { search: route.query.q } }" class="group-more">
              {{ $t('查看全部 {n} 条', { n: result[g.totalKey] }) }} <LineIcon name="arrowRight" :size="12" />
            </router-link>
          </div>
          <div class="hit-grid">
            <div v-for="(item, idx) in result[g.key]" :key="item.id" class="hit" @click="router.push(g.route(item))">
              <span class="hit-index">{{ String(idx + 1).padStart(2, '0') }}</span>
              <div class="hit-cover" v-if="item.coverUrl">
                <img :src="item.coverUrl" :alt="item.title" loading="lazy" />
              </div>
              <div v-else class="hit-cover hit-fallback"><LineIcon :name="g.icon" :size="20" /></div>
              <div class="hit-body">
                <div class="hit-title">{{ item.title }}</div>
                <div class="hit-desc text-muted">{{ item.description }}</div>
                <div class="hit-meta">{{ item.meta }}</div>
              </div>
              <LineIcon name="arrowRight" :size="14" class="hit-arrow" />
            </div>
          </div>
        </section>
      </template>
      <el-empty v-else :description="$t('在顶部输入关键词，搜索全站的课程、路径与模型')" />
    </div>
  </div>
</template>

<style scoped>
.search-bar {
  max-width: 640px;
  margin: 0 0 28px;
}
.results {
  min-height: 200px;
}
.group {
  margin-bottom: 32px;
}
.group.hidden {
  display: none;
}
.group-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}
.group-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 2px;
  white-space: nowrap;
}
.group-count {
  font-style: normal;
  font-size: 12px;
  font-weight: 400;
  color: var(--theme-color);
  border: 1px solid var(--theme-color);
  border-radius: 2px;
  padding: 0 6px;
  line-height: 18px;
}
.group-line {
  flex: 1;
  height: 1px;
  background: var(--line-color);
  opacity: 0.25;
}
.group-more {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}
.group-more:hover {
  color: var(--theme-color);
}
.hit-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
.hit {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 14px;
  border: 1px solid var(--border-color);
  border-left: 3px solid var(--line-soft);
  border-radius: 2px;
  background: var(--el-bg-color);
  cursor: pointer;
  transition: border-color 0.15s, transform 0.15s;
}
.hit:hover {
  border-left-color: var(--theme-color);
  border-color: var(--theme-color);
  transform: translateY(-2px);
}
.hit-index {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.hit-cover {
  width: 64px;
  height: 48px;
  flex-shrink: 0;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  overflow: hidden;
  background: var(--el-fill-color-light);
}
.hit-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.hit-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}
.hit-body {
  flex: 1;
  min-width: 0;
}
.hit-title {
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hit:hover .hit-title {
  color: var(--theme-color);
}
.hit-desc {
  font-size: 12px;
  margin-top: 3px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.hit-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
.hit-arrow {
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}
.hit:hover .hit-arrow {
  color: var(--theme-color);
}
@media (max-width: 760px) {
  .hit-grid {
    grid-template-columns: 1fr;
  }
}
</style>
