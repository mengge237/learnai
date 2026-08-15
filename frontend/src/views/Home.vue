<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ResourceCard from '@/components/ResourceCard.vue'
import PathCard from '@/components/PathCard.vue'
import ModelCard from '@/components/ModelCard.vue'
import { resourceApi } from '@/api/resources'
import { pathApi } from '@/api/paths'
import { marketApi } from '@/api/market'
import { groupCategories } from '@/utils/categories'
import http from '@/api/http'

const router = useRouter()
const resources = ref([])
const paths = ref([])
const models = ref([])
const catGroups = ref([])
const searchText = ref('')
const stats = ref({ resources: 0, paths: 0, models: 0 })

onMounted(async () => {
  const [res, pathList, modelList, cats] = await Promise.all([
    resourceApi.list({ page: 1, size: 6, sort: 'popular' }),
    pathApi.list({ page: 1, size: 4 }),
    marketApi.listModels({ page: 1, size: 6, sort: 'newest' }),
    http.get('/categories'),
  ])
  resources.value = res.content
  paths.value = pathList.content
  models.value = modelList.content
  catGroups.value = groupCategories(cats).slice(0, 8)
  stats.value = {
    resources: res.totalElements,
    paths: pathList.totalElements,
    models: modelList.totalElements,
  }
})

function onSearch() {
  router.push({ path: '/resources', query: { search: searchText.value || undefined } })
}
</script>

<template>
  <div>
    <!-- Hero：工业黑底 + 大搜索 -->
    <section class="hero">
      <div class="hero-inner">
        <h1>让 3D 学习更简单<span class="hero-accent">。</span></h1>
        <p>系统化课程 · 学习路径规划 · AI 智能答疑 · 3D 模型资源，一站式校园学习平台</p>

        <div class="hero-search">
          <el-input
            v-model="searchText"
            size="large"
            placeholder="搜索学习资源，例如：Blender、Maya、图形学…"
            clearable
            @keyup.enter="onSearch"
          >
            <template #append>
              <el-button class="search-btn" @click="onSearch">搜 索</el-button>
            </template>
          </el-input>
        </div>

        <div class="hero-actions">
          <el-button type="primary" size="large" @click="router.push('/resources')">🚀 开始学习</el-button>
          <el-button size="large" class="ghost" @click="router.push('/paths')">🗺️ 学习路径</el-button>
          <el-button size="large" class="ghost" @click="router.push('/market')">🏪 模型资源库</el-button>
        </div>

        <div class="hero-stats">
          <div class="stat"><span class="num">{{ stats.resources }}</span><span class="label">学习资源</span></div>
          <div class="stat"><span class="num">{{ stats.paths }}</span><span class="label">学习路径</span></div>
          <div class="stat"><span class="num">{{ stats.models }}</span><span class="label">3D 模型</span></div>
          <div class="stat"><span class="num">AI</span><span class="label">智能答疑</span></div>
        </div>
      </div>
    </section>

    <div class="page-container">
      <!-- 分类磁贴 -->
      <div class="section-title">🧭 学习分类</div>
      <div class="cat-grid">
        <button v-for="g in catGroups" :key="g.parent.id" class="cat-tile" @click="router.push({ path: '/resources', query: { categoryId: g.parent.id } })">
          <span class="cat-name">{{ g.parent.name }}</span>
          <span class="cat-children text-muted">{{ g.children.map((c) => c.name).join(' · ') || '综合' }}</span>
        </button>
      </div>

      <!-- 热门资源 -->
      <div class="section-title">
        🔥 热门学习资源
        <el-link type="primary" class="more" @click="router.push('/resources')">查看更多 →</el-link>
      </div>
      <div class="card-grid">
        <ResourceCard v-for="r in resources" :key="r.id" :resource="r" />
      </div>

      <!-- 学习路径 -->
      <div class="section-title">
        🗺️ 学习路径
        <el-link type="primary" class="more" @click="router.push('/paths')">查看更多 →</el-link>
      </div>
      <div class="card-grid">
        <PathCard v-for="p in paths" :key="p.id" :path="p" />
      </div>

      <!-- 最新模型 -->
      <div class="section-title">
        📦 最新 3D 模型
        <el-link type="primary" class="more" @click="router.push('/market')">查看更多 →</el-link>
      </div>
      <div class="card-grid">
        <ModelCard v-for="m in models" :key="m.id" :model="m" />
      </div>

      <!-- AI 助手 -->
      <section class="ai-banner">
        <div class="ai-text">
          <h3>🤖 AI 学习助手</h3>
          <p>智能推荐学习资源、分析学习进度、解答 3D 建模问题——点击右下角悬浮按钮，随时随地问 AI！</p>
        </div>
        <el-button size="large" class="ai-btn" @click="router.push('/ai/chat')">进入 AI 答疑 →</el-button>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* ---------- Hero：工业黑 ---------- */
.hero {
  background: #17181c;
  color: #fff;
  padding: 56px 16px 48px;
  border-bottom: 4px solid var(--theme-color);
}
.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}
.hero h1 {
  font-size: 40px;
  letter-spacing: 4px;
  margin: 0 0 12px;
  text-transform: uppercase;
}
.hero-accent {
  color: var(--theme-color);
}
.hero p {
  font-size: 16px;
  letter-spacing: 1px;
  color: #b8bcc4;
  margin: 0 0 28px;
}
.hero-search {
  max-width: 620px;
  margin: 0 auto 24px;
}
.hero-search :deep(.el-input__wrapper) {
  background: #fff;
  border-radius: 2px;
}
.search-btn {
  background: var(--theme-color);
  border-color: var(--theme-color);
  color: #fff;
}
.search-btn:hover {
  background: #d14f07;
  border-color: #d14f07;
}
.hero-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 36px;
}
.hero-actions .el-button--primary {
  background: var(--theme-color);
  border-color: var(--theme-color);
}
.hero-actions .el-button--primary:hover {
  background: #d14f07;
  border-color: #d14f07;
}
.ghost {
  background: transparent;
  border: 1px solid #4a4d55;
  color: #d4d7dd;
}
.ghost:hover {
  border-color: var(--theme-color);
  color: var(--theme-color);
}
.hero-stats {
  display: flex;
  justify-content: center;
  gap: 56px;
}
.stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.stat .num {
  font-size: 26px;
  font-weight: 700;
  color: var(--theme-color);
}
.stat .label {
  font-size: 13px;
  color: #b8bcc4;
  letter-spacing: 1px;
}

/* ---------- 分类磁贴 ---------- */
.cat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}
.cat-tile {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  background: var(--el-bg-color);
  cursor: pointer;
  text-align: left;
  transition: all 0.15s;
}
.cat-tile:hover {
  border-color: var(--theme-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}
.cat-name {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 1px;
}
.cat-children {
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more {
  margin-left: auto;
}

/* ---------- AI 横幅：工业黑卡 ---------- */
.ai-banner {
  margin-top: 40px;
  padding: 24px 32px;
  border-radius: 2px;
  border-left: 4px solid var(--theme-color);
  background: #17181c;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.ai-text h3 {
  margin: 0 0 6px;
  color: #fff;
  letter-spacing: 1px;
}
.ai-text p {
  margin: 0;
  color: #b8bcc4;
}
.ai-btn {
  background: var(--theme-color);
  border-color: var(--theme-color);
  color: #fff;
}
.ai-btn:hover {
  background: #d14f07;
  border-color: #d14f07;
  color: #fff;
}
@media (max-width: 640px) {
  .hero h1 {
    font-size: 30px;
  }
  .hero-stats {
    gap: 28px;
  }
  .ai-banner {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
