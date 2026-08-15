<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { resourceApi } from '@/api/resources'
import { pathApi } from '@/api/paths'
import { marketApi } from '@/api/market'
import { groupCategories } from '@/utils/categories'
import { formatCount, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'
import http from '@/api/http'

const router = useRouter()
const resources = ref([])
const paths = ref([])
const models = ref([])
const catGroups = ref([])
const searchText = ref('')
const stats = ref({ resources: 0, paths: 0, models: 0 })

/** 学习入口方块（count = stats 中的键，无 count 显示箭头） */
const portals = [
  { en: 'COURSES', label: '学习资源', icon: 'book', route: '/resources', count: 'resources' },
  { en: 'PATHS', label: '学习路径', icon: 'layers', route: '/paths', count: 'paths' },
  { en: 'MODELS', label: '模型资源库', icon: 'cube', route: '/market', count: 'models' },
  { en: 'MY-LEARN', label: '我的学习', icon: 'user', route: '/resources/my' },
  { en: 'Q&A', label: '在线答疑', icon: 'chat', route: '/ai/chat' },
  { en: 'ANALYTICS', label: '学习分析', icon: 'chart', route: '/ai/analytics' },
  { en: 'SUBMIT', label: '提交资源', icon: 'upload', route: '/resources/submit' },
  { en: 'SEARCH', label: '全局搜索', icon: 'search', route: '/search' },
]

onMounted(async () => {
  const [res, pathList, modelList, cats] = await Promise.all([
    resourceApi.list({ page: 1, size: 6, sort: 'popular' }),
    pathApi.list({ page: 1, size: 4 }),
    marketApi.listModels({ page: 1, size: 3, sort: 'newest' }),
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
  <div class="home">
    <!-- ============ HERO：深色对撞区 ============ -->
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-top">
          <span class="hero-en">AIZHIXUE · CAMPUS LEARNING PLATFORM</span>
          <span class="hero-en">EST. 2026</span>
        </div>

        <div class="hero-body">
          <div class="hero-text">
            <h1 class="hero-title">
              从一条线开始<br />
              <span class="hero-title-2">构建一方世界</span>
            </h1>
            <p class="hero-slogan">
              面向三维建模与图形开发学习者的校园学习平台。系统化教程逐章可读，
              学习路径规划成长路线，模型资源库让灵感落地。
            </p>

            <div class="hero-search">
              <el-input
                v-model="searchText"
                size="large"
                placeholder="搜索课程、路径或 3D 模型…"
                clearable
                @keyup.enter="onSearch"
              >
                <template #append>
                  <el-button class="search-btn" @click="onSearch">搜索</el-button>
                </template>
              </el-input>
            </div>

            <div class="hero-actions">
              <button class="hero-btn hero-btn-primary" @click="router.push('/resources')">开始学习</button>
              <button class="hero-btn" @click="router.push('/paths')">学习路径</button>
              <button class="hero-btn" @click="router.push('/market')">模型资源库</button>
            </div>
          </div>

          <div class="hero-cube">
            <div class="cube-stage">
              <div class="cube">
                <div class="face front" /><div class="face back" />
                <div class="face left" /><div class="face right" />
                <div class="face top" /><div class="face bottom" />
                <div class="cube-inner" />
              </div>
              <span class="crosshair ch-1" /><span class="crosshair ch-2" />
              <span class="crosshair ch-3" /><span class="crosshair ch-4" />
            </div>
            <span class="cube-label">WIREFRAME · 线框是世界的起点</span>
          </div>
        </div>

        <div class="hero-stats">
          <button class="hstat" @click="router.push('/resources')"><span class="hnum">{{ stats.resources }}</span><span class="hlabel">门课程</span></button>
          <div class="hstat-line" />
          <button class="hstat" @click="router.push('/paths')"><span class="hnum">{{ stats.paths }}</span><span class="hlabel">条路径</span></button>
          <div class="hstat-line" />
          <button class="hstat" @click="router.push('/market')"><span class="hnum">{{ stats.models }}</span><span class="hlabel">个模型</span></button>
          <div class="hstat-line" />
          <button class="hstat" @click="router.push('/paths')"><span class="hnum">3</span><span class="hlabel">步进阶法</span></button>
        </div>
      </div>
    </section>

    <!-- ============ 理念三行 ============ -->
    <section class="manifesto">
      <div class="mani-row" @click="router.push('/resources')">
        <span class="mani-en">LEARN</span>
        <h2 class="mani-title">学</h2>
        <p class="mani-desc">系统化教程逐章可读，像翻阅图纸一样学习，随时答疑解惑。</p>
        <span class="mani-arrow">→</span>
      </div>
      <div class="mani-row" @click="router.push('/resources/my')">
        <span class="mani-en">PRACTICE</span>
        <h2 class="mani-title">练</h2>
        <p class="mani-desc">步骤打卡、学习计时与连续记录，每一步进步都有迹可循。</p>
        <span class="mani-arrow">→</span>
      </div>
      <div class="mani-row" @click="router.push('/market')">
        <span class="mani-en">CREATE</span>
        <h2 class="mani-title">创</h2>
        <p class="mani-desc">模型资源库在线预览 3D 作品，让灵感直接落地。</p>
        <span class="mani-arrow">→</span>
      </div>
    </section>

    <!-- ============ 学习入口方块 ============ -->
    <section class="portal-block">
      <div class="block-head">
        <div class="block-head-left">
          <span class="block-en">PORTALS</span>
          <h2 class="block-title">学习入口</h2>
        </div>
        <span class="portal-hint">点击方块直达</span>
      </div>
      <div class="portal-grid">
        <button v-for="(p, i) in portals" :key="p.en" class="portal-tile" @click="router.push(p.route)">
          <span class="pt-top">
            <span class="pt-en">{{ p.en }}</span>
            <span class="pt-no">{{ String(i + 1).padStart(2, '0') }}</span>
          </span>
          <span class="pt-icon"><LineIcon :name="p.icon" :size="30" /></span>
          <span class="pt-bottom">
            <span class="pt-label">{{ p.label }}</span>
            <span v-if="p.count" class="pt-num">{{ stats[p.count] ?? '—' }}</span>
            <span v-else class="pt-arrow">→</span>
          </span>
        </button>
      </div>
    </section>

    <div class="page-container">
      <!-- ============ 课程 ============ -->
      <section class="block">
        <div class="block-head">
          <div class="block-head-left">
            <span class="block-en">COURSES</span>
            <h2 class="block-title">课程</h2>
          </div>
          <button class="more-link" @click="router.push('/resources')">全部课程 →</button>
        </div>
        <div class="list-rows">
          <button v-for="(r, i) in resources" :key="r.id" class="list-row" @click="router.push(`/resources/${r.id}`)">
            <span class="row-no">{{ String(i + 1).padStart(2, '0') }}</span>
            <span class="row-title">{{ r.title }}</span>
            <span class="row-meta">
              <template v-if="r.categoryName">{{ r.categoryName }}</template>
              <template v-if="r.difficultyLevel"> · {{ r.difficultyLevel }}</template>
              <template v-if="r.completionCount"> · {{ formatCount(r.completionCount) }} 人学过</template>
            </span>
            <span class="row-arrow">→</span>
          </button>
        </div>
      </section>

      <!-- ============ 模型精选 ============ -->
      <section class="block">
        <div class="block-head">
          <div class="block-head-left">
            <span class="block-en">MODELS</span>
            <h2 class="block-title">模型精选</h2>
          </div>
          <button class="more-link" @click="router.push('/market')">进入模型资源库 →</button>
        </div>
        <div class="model-row">
          <button v-for="m in models" :key="m.id" class="model-item" @click="router.push(`/market/${m.id}`)">
            <div class="model-frame">
              <span class="model-wire">◇</span>
            </div>
            <span class="model-name">{{ m.name }}</span>
            <span class="model-meta">
              {{ m.categoryName || '模型' }} · {{ Number(m.price) === 0 ? '免费' : formatPrice(m.price) }}
            </span>
          </button>
        </div>
      </section>

      <!-- ============ 路径与分类 ============ -->
      <section class="block two-col">
        <div>
          <div class="block-head">
            <div class="block-head-left">
              <span class="block-en">PATHS</span>
              <h2 class="block-title">学习路径</h2>
            </div>
            <button class="more-link" @click="router.push('/paths')">全部 →</button>
          </div>
          <div class="list-rows slim">
            <button v-for="p in paths" :key="p.id" class="list-row" @click="router.push(`/paths/${p.id}`)">
              <span class="row-title">{{ p.name }}</span>
              <span class="row-meta">{{ p.resourceCount || 0 }} 个资源 · {{ p.enrollmentCount }} 人报名</span>
              <span class="row-arrow">→</span>
            </button>
          </div>
        </div>
        <div>
          <div class="block-head">
            <div class="block-head-left">
              <span class="block-en">CATEGORIES</span>
              <h2 class="block-title">学习分类</h2>
            </div>
          </div>
          <div class="cat-chips">
            <button
              v-for="g in catGroups"
              :key="g.parent.id"
              class="cat-chip"
              @click="router.push({ path: '/resources', query: { categoryId: g.parent.id } })"
            >
              {{ g.parent.name }}
            </button>
          </div>
        </div>
      </section>

      <!-- ============ 收尾 CTA ============ -->
      <section class="closing">
        <p class="closing-text">学习路上，答疑随行。</p>
        <button class="closing-btn" @click="router.push('/resources')">从今天开始你的第一课 →</button>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* ================= HERO：深色对撞 ================= */
.hero {
  background: var(--el-color-primary);
  color: var(--el-bg-color);
  border-bottom: 1px solid var(--line-color);
}
.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px 56px;
}
.hero-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(128, 128, 128, 0.35);
}
.hero-en {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 3px;
  opacity: 0.7;
}
.hero-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
  padding: 56px 0 40px;
}
.hero-title {
  font-size: 58px;
  font-weight: 900;
  line-height: 1.18;
  letter-spacing: 2px;
  margin: 0 0 20px;
}
.hero-title-2 {
  color: var(--theme-color);
}
.hero-slogan {
  font-size: 15px;
  line-height: 2;
  letter-spacing: 1px;
  opacity: 0.75;
  max-width: 520px;
  margin: 0 0 28px;
}
.hero-search {
  max-width: 480px;
  margin-bottom: 22px;
}
.hero-search :deep(.el-input__wrapper) {
  border-radius: 2px;
  background: transparent;
  box-shadow: 0 0 0 1px rgba(128, 128, 128, 0.5) inset;
}
.hero-search :deep(.el-input__inner) {
  color: var(--el-bg-color);
}
.search-btn {
  background: var(--theme-color);
  border-color: var(--theme-color);
  color: #fff;
}
.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.hero-btn {
  background: transparent;
  border: 1px solid rgba(128, 128, 128, 0.6);
  color: var(--el-bg-color);
  font-size: 14px;
  letter-spacing: 2px;
  padding: 10px 22px;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.15s;
}
.hero-btn:hover {
  border-color: var(--theme-color);
  color: var(--theme-color);
}
.hero-btn-primary {
  background: var(--theme-color);
  border-color: var(--theme-color);
  color: #fff;
}
.hero-btn-primary:hover {
  background: #d14f07;
  color: #fff;
}

/* 线框立方体 */
.hero-cube {
  flex-shrink: 0;
  text-align: center;
}
.cube-stage {
  width: 220px;
  height: 220px;
  position: relative;
  margin: 0 auto;
  perspective: 800px;
}
.cube {
  position: absolute;
  inset: 0;
  transform-style: preserve-3d;
  animation: cube-spin 16s linear infinite;
}
.face {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 140px;
  height: 140px;
  margin: -70px 0 0 -70px;
  border: 1.5px solid var(--el-bg-color);
  background: transparent;
}
.face.front { transform: translateZ(70px); }
.face.back { transform: rotateY(180deg) translateZ(70px); }
.face.left { transform: rotateY(-90deg) translateZ(70px); }
.face.right { transform: rotateY(90deg) translateZ(70px); }
.face.top { transform: rotateX(90deg) translateZ(70px); }
.face.bottom { transform: rotateX(-90deg) translateZ(70px); }
.cube-inner {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 56px;
  height: 56px;
  margin: -28px 0 0 -28px;
  border: 1px dashed var(--el-bg-color);
  animation: inner-spin 16s linear infinite;
}
@keyframes cube-spin {
  from { transform: rotateX(-18deg) rotateY(0deg); }
  to { transform: rotateX(-18deg) rotateY(360deg); }
}
@keyframes inner-spin {
  from { transform: rotateX(0) rotateY(0); }
  to { transform: rotateX(360deg) rotateY(360deg); }
}
.crosshair { position: absolute; width: 14px; height: 14px; }
.crosshair::before,
.crosshair::after {
  content: '';
  position: absolute;
  background: rgba(128, 128, 128, 0.6);
}
.crosshair::before { left: 6px; top: 0; width: 1px; height: 14px; }
.crosshair::after { left: 0; top: 6px; width: 14px; height: 1px; }
.ch-1 { top: 2px; left: 2px; }
.ch-2 { top: 2px; right: 2px; }
.ch-3 { bottom: 2px; left: 2px; }
.ch-4 { bottom: 2px; right: 2px; }
.cube-label {
  display: block;
  margin-top: 6px;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 2px;
  opacity: 0.6;
}

/* 数据条 */
.hero-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  padding-top: 20px;
  border-top: 1px solid rgba(128, 128, 128, 0.35);
  flex-wrap: wrap;
}
.hstat {
  display: flex;
  align-items: baseline;
  gap: 10px;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: inherit;
  font-family: inherit;
  transition: transform 0.15s, opacity 0.15s;
}
.hstat:hover {
  transform: translateY(-2px);
  opacity: 0.85;
}
.hnum {
  font-size: 30px;
  font-weight: 800;
  font-family: 'Consolas', 'Courier New', monospace;
  color: var(--theme-color);
}
.hlabel {
  font-size: 12px;
  letter-spacing: 3px;
  opacity: 0.7;
}
.hstat-line {
  width: 1px;
  background: rgba(128, 128, 128, 0.35);
}

/* ================= 理念三行 ================= */
.manifesto {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}
.mani-row {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 34px 8px;
  border-bottom: 1px solid var(--line-color);
  cursor: pointer;
  transition: padding-left 0.2s;
}
.mani-row:hover {
  padding-left: 20px;
}
.mani-row:hover .mani-arrow {
  color: var(--theme-color);
  transform: translateX(4px);
}
.mani-en {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 12px;
  letter-spacing: 4px;
  color: var(--el-text-color-secondary);
  width: 110px;
  flex-shrink: 0;
}
.mani-title {
  font-size: 40px;
  font-weight: 900;
  letter-spacing: 12px;
  margin: 0;
  width: 90px;
  flex-shrink: 0;
}
.mani-desc {
  flex: 1;
  margin: 0;
  color: var(--el-text-color-secondary);
  line-height: 1.9;
  font-size: 14px;
  letter-spacing: 1px;
}
.mani-arrow {
  font-size: 22px;
  transition: all 0.2s;
}

/* ================= 学习入口方块 ================= */
.portal-block {
  max-width: 1200px;
  margin: 88px auto 0;
  padding: 0 24px;
}
.portal-hint {
  font-size: 12px;
  letter-spacing: 2px;
  color: var(--el-text-color-secondary);
}
.portal-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}
.portal-tile {
  aspect-ratio: 1 / 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 18px;
  border: 1px solid var(--border-color);
  border-top: 3px solid var(--line-soft);
  background: var(--el-bg-color);
  cursor: pointer;
  text-align: left;
  position: relative;
  overflow: hidden;
  transition: background 0.15s, border-color 0.15s, transform 0.15s, color 0.15s;
}
.portal-tile:hover {
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
  color: var(--el-bg-color);
  transform: translateY(-3px);
}
.pt-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pt-en {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 3px;
  color: var(--el-text-color-secondary);
}
.pt-no {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.portal-tile:hover .pt-en,
.portal-tile:hover .pt-no {
  color: inherit;
  opacity: 0.65;
}
.pt-icon {
  display: flex;
  align-items: center;
  color: var(--theme-color);
}
.pt-bottom {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}
.pt-label {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 2px;
}
.pt-num {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 24px;
  font-weight: 800;
  color: var(--theme-color);
  line-height: 1;
}
.pt-arrow {
  font-size: 18px;
  transition: transform 0.15s;
}
.portal-tile:hover .pt-arrow {
  transform: translateX(4px);
}

/* ================= 区块通用 ================= */
.block {
  margin-top: 88px;
}
.block-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  padding-bottom: 14px;
  border-bottom: 2px solid var(--line-color);
  margin-bottom: 20px;
}
.block-en {
  display: block;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 4px;
  color: var(--el-text-color-secondary);
  margin-bottom: 4px;
}
.block-title {
  font-size: 26px;
  font-weight: 800;
  letter-spacing: 6px;
  margin: 0;
}
.more-link {
  background: none;
  border: none;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  letter-spacing: 2px;
  cursor: pointer;
  padding: 6px 0;
  transition: color 0.15s;
}
.more-link:hover {
  color: var(--theme-color);
}

/* 列表行（hover 反色） */
.list-rows {
  border: 1px solid var(--border-color);
}
.list-row {
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100%;
  padding: 18px 20px;
  border: none;
  border-bottom: 1px solid var(--border-color);
  background: var(--el-bg-color);
  cursor: pointer;
  text-align: left;
  transition: all 0.12s;
}
.list-row:last-child {
  border-bottom: none;
}
.list-row:hover {
  background: var(--el-color-primary);
  color: var(--el-bg-color);
}
.row-no {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 12px;
  letter-spacing: 2px;
  color: var(--theme-color);
  width: 28px;
  flex-shrink: 0;
}
.row-title {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  flex-shrink: 0;
}
.row-meta {
  flex: 1;
  font-size: 12px;
  letter-spacing: 1px;
  color: var(--el-text-color-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.list-row:hover .row-meta {
  color: inherit;
  opacity: 0.7;
}
.row-arrow {
  font-size: 18px;
  transition: transform 0.15s;
}
.list-row:hover .row-arrow {
  transform: translateX(4px);
}
.list-rows.slim .row-title {
  font-size: 14px;
}

/* 模型精选 */
.model-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.model-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
  border: 1px solid var(--border-color);
  background: var(--el-bg-color);
  padding: 0 0 16px;
  cursor: pointer;
  text-align: left;
  transition: all 0.15s;
}
.model-item:hover {
  border-color: var(--line-color);
  transform: translateY(-3px);
}
.model-frame {
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-bg-color-page, #f5f5f3);
  border-bottom: 1px solid var(--border-color);
}
.model-wire {
  font-size: 56px;
  color: var(--line-soft);
  transition: color 0.2s;
}
.model-item:hover .model-wire {
  color: var(--theme-color);
}
.model-name {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  padding: 0 16px;
}
.model-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  padding: 0 16px;
  letter-spacing: 1px;
}

/* 双列 */
.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
}
.cat-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.cat-chip {
  border: 1px solid var(--border-color);
  background: var(--el-bg-color);
  padding: 8px 18px;
  font-size: 13px;
  letter-spacing: 2px;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.15s;
}
.cat-chip:hover {
  border-color: var(--theme-color);
  color: var(--theme-color);
}

/* 收尾 CTA */
.closing {
  margin-top: 88px;
  padding: 48px 24px;
  background: var(--el-color-primary);
  color: var(--el-bg-color);
  text-align: center;
}
.closing-text {
  font-size: 20px;
  letter-spacing: 8px;
  margin: 0 0 18px;
  opacity: 0.85;
}
.closing-btn {
  background: var(--theme-color);
  border: none;
  color: #fff;
  font-size: 14px;
  letter-spacing: 3px;
  padding: 12px 28px;
  cursor: pointer;
  border-radius: 2px;
  transition: background 0.15s;
}
.closing-btn:hover {
  background: #d14f07;
}

/* ================= 响应式 ================= */
@media (max-width: 960px) {
  .hero-title {
    font-size: 40px;
  }
  .hero-body {
    flex-direction: column;
    text-align: center;
    gap: 32px;
  }
  .hero-slogan {
    margin-left: auto;
    margin-right: auto;
  }
  .hero-search,
  .hero-actions {
    margin-left: auto;
    margin-right: auto;
    justify-content: center;
  }
  .two-col {
    grid-template-columns: 1fr;
    gap: 64px;
  }
  .mani-row {
    flex-wrap: wrap;
    gap: 12px 20px;
  }
  .mani-title {
    width: auto;
  }
  .mani-desc {
    flex-basis: 100%;
  }
  .portal-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 640px) {
  .hero-inner {
    padding: 24px 16px 36px;
  }
  .hero-title {
    font-size: 32px;
  }
  .hero-stats {
    gap: 20px;
  }
  .hstat-line {
    display: none;
  }
  .model-row {
    grid-template-columns: 1fr;
  }
  .row-meta {
    display: none;
  }
  .block {
    margin-top: 56px;
  }
  .portal-block {
    margin-top: 56px;
    padding: 0 16px;
  }
  .closing {
    margin-top: 56px;
  }
}
</style>
