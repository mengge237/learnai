<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ResourceCard from '@/components/ResourceCard.vue'
import PathCard from '@/components/PathCard.vue'
import ModelCard from '@/components/ModelCard.vue'
import { resourceApi } from '@/api/resources'
import { pathApi } from '@/api/paths'
import { marketApi } from '@/api/market'

const router = useRouter()
const resources = ref([])
const paths = ref([])
const models = ref([])
const stats = ref({ resources: 0, paths: 0, models: 0 })

onMounted(async () => {
  const [res, pathList, modelList] = await Promise.all([
    resourceApi.list({ page: 1, size: 6, sort: 'popular' }),
    pathApi.list({ page: 1, size: 4 }),
    marketApi.listModels({ page: 1, size: 6, sort: 'newest' }),
  ])
  resources.value = res.content
  paths.value = pathList.content
  models.value = modelList.content
  stats.value = {
    resources: res.totalElements,
    paths: pathList.totalElements,
    models: modelList.totalElements,
  }
})
</script>

<template>
  <div>
    <!-- Hero -->
    <section class="hero">
      <div class="hero-inner">
        <h1>让 3D 学习更简单</h1>
        <p>系统化课程 · 学习路径规划 · AI 智能答疑 · 3D 模型资源，一站式在线学习平台</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="router.push('/resources')">🚀 开始学习</el-button>
          <el-button size="large" @click="router.push('/paths')">🗺️ 学习路径</el-button>
          <el-button size="large" @click="router.push('/market')">🛒 模型商城</el-button>
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

      <!-- 热门模型 -->
      <div class="section-title">
        🛒 最新 3D 模型
        <el-link type="primary" class="more" @click="router.push('/market')">查看更多 →</el-link>
      </div>
      <div class="card-grid">
        <ModelCard v-for="m in models" :key="m.id" :model="m" />
      </div>

      <!-- AI 助手 -->
      <section class="ai-banner">
        <div class="ai-text">
          <h3>🤖 AI 学习助手</h3>
          <p>智能推荐学习资源、分析学习进度、解答 3D 建模问题，随时随地问 AI！</p>
        </div>
        <el-button size="large" @click="router.push('/ai/chat')">立即体验 →</el-button>
      </section>
    </div>
  </div>
</template>

<style scoped>
.hero {
  background: linear-gradient(135deg, #1f3b8c 0%, #3a6df0 55%, #6f9bff 100%);
  color: #fff;
  padding: 64px 16px;
}
.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}
.hero h1 {
  font-size: 40px;
  margin: 0 0 12px;
}
.hero p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 24px;
}
.hero-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 36px;
}
.hero-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
}
.stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.stat .num {
  font-size: 26px;
  font-weight: 700;
}
.stat .label {
  font-size: 13px;
  opacity: 0.85;
}
.more {
  margin-left: auto;
}
.ai-banner {
  margin-top: 40px;
  padding: 24px 32px;
  border-radius: 12px;
  background: linear-gradient(120deg, #e8f1ff, #f0e8ff);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.ai-text h3 {
  margin: 0 0 6px;
}
.ai-text p {
  margin: 0;
  color: var(--el-text-color-secondary);
}
</style>
