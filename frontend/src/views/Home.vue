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
    <!-- ============ HERO：工程图纸区（线稿风） ============ -->
    <section class="hero blueprint-grid">
      <div class="hero-frame tech-frame">
        <span class="hero-annotation ann-tl">FIG.01 — WIREFRAME / 线稿</span>
        <span class="hero-annotation ann-tr">SCALE 1:1</span>
        <span class="hero-annotation ann-bl">X:1200 · Y:600 · Z:∞</span>
        <span class="hero-annotation ann-br">DWG-2026-A</span>

        <div class="hero-inner">
          <div class="hero-left">
            <div class="cube-stage">
              <div class="cube">
                <div class="face front" /><div class="face back" />
                <div class="face left" /><div class="face right" />
                <div class="face top" /><div class="face bottom" />
                <div class="cube-inner" />
              </div>
              <span class="crosshair ch-1" /><span class="crosshair ch-2" />
              <span class="crosshair ch-3" /><span class="crosshair ch-4" />
              <span class="cube-label">3D 线框立方体 · 学习从这里开始</span>
            </div>
          </div>

          <div class="hero-right">
            <div class="dim-strip hero-kicker">AIZHIXUE · CAMPUS EDITION · 2026</div>
            <h1 class="hero-title">
              从一根线开始<br />
              构建你的<span class="hero-accent">三维世界</span>
            </h1>
            <div class="hero-line" />
            <p class="hero-slogan">
              AI智学 · 校园学习平台 —— 系统化教程可在线阅读，学习路径规划成长路线，
              AI 答疑随时守候，模型资源库让灵感落地。从线稿到作品，每一步都有迹可循。
            </p>

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
              <el-button type="primary" size="large" @click="router.push('/resources')">▶ 开始学习</el-button>
              <el-button size="large" class="ghost" @click="router.push('/paths')">学习路径</el-button>
              <el-button size="large" class="ghost" @click="router.push('/market')">模型资源库</el-button>
            </div>
          </div>
        </div>

        <div class="hero-stats">
          <div class="stat"><span class="num">{{ stats.resources }}</span><span class="label">学习资源</span></div>
          <div class="stat-divider" />
          <div class="stat"><span class="num">{{ stats.paths }}</span><span class="label">学习路径</span></div>
          <div class="stat-divider" />
          <div class="stat"><span class="num">{{ stats.models }}</span><span class="label">3D 模型</span></div>
          <div class="stat-divider" />
          <div class="stat"><span class="num">AI</span><span class="label">智能答疑</span></div>
        </div>
      </div>
    </section>

    <div class="page-container">
      <!-- ============ 00 理念：学 · 练 · 创 ============ -->
      <div class="blueprint-section">
        <span class="bp-no">00</span>
        <span class="bp-title">我们的理念</span>
        <span class="bp-sub">LEARN · PRACTICE · CREATE</span>
        <span class="bp-line" />
      </div>
      <div class="idea-grid">
        <div class="idea-card tech-frame">
          <span class="idea-no">01</span>
          <svg class="idea-svg" viewBox="0 0 24 24"><path d="M4 4h16v16H4z" /><path d="M8 9h8M8 13h8M8 17h5" /></svg>
          <h3>学</h3>
          <p>系统化教程逐章可读，像翻阅工程图纸一样学习，AI 助手随时解答疑问。</p>
        </div>
        <div class="idea-card tech-frame">
          <span class="idea-no">02</span>
          <svg class="idea-svg" viewBox="0 0 24 24"><path d="M4 20l12-12" /><path d="M13 7l4 4" /><path d="M15 3h6v6" /></svg>
          <h3>练</h3>
          <p>步骤打卡记录每一点进步，学习分析用图表丈量你的成长轨迹。</p>
        </div>
        <div class="idea-card tech-frame">
          <span class="idea-no">03</span>
          <svg class="idea-svg" viewBox="0 0 24 24"><path d="M12 3l9 5v8l-9 5-9-5V8z" /><path d="M12 13l9-5M12 13L3 8M12 13v8" /></svg>
          <h3>创</h3>
          <p>模型资源库提供可在线预览的 3D 模型，让灵感直接落地为作品。</p>
        </div>
      </div>

      <!-- ============ 01 学习资源 ============ -->
      <div class="blueprint-section">
        <span class="bp-no">01</span>
        <span class="bp-title">学习资源</span>
        <span class="bp-sub">系统课程 · 在线阅读 · 步骤打卡</span>
        <span class="bp-line" />
        <el-link type="primary" class="bp-more" @click="router.push('/resources')">查看全部 →</el-link>
      </div>
      <div class="card-grid">
        <ResourceCard v-for="r in resources" :key="r.id" :resource="r" />
      </div>

      <!-- ============ 02 学习路径 ============ -->
      <div class="blueprint-section">
        <span class="bp-no">02</span>
        <span class="bp-title">学习路径</span>
        <span class="bp-sub">规划路线 · 循序渐进</span>
        <span class="bp-line" />
        <el-link type="primary" class="bp-more" @click="router.push('/paths')">查看全部 →</el-link>
      </div>
      <div class="card-grid">
        <PathCard v-for="p in paths" :key="p.id" :path="p" />
      </div>

      <!-- ============ 03 模型资源库 ============ -->
      <div class="blueprint-section">
        <span class="bp-no">03</span>
        <span class="bp-title">模型资源库</span>
        <span class="bp-sub">在线 3D 预览 · 灵感落地</span>
        <span class="bp-line" />
        <el-link type="primary" class="bp-more" @click="router.push('/market')">查看全部 →</el-link>
      </div>
      <div class="card-grid">
        <ModelCard v-for="m in models" :key="m.id" :model="m" />
      </div>

      <!-- ============ 04 学习分类 ============ -->
      <div class="blueprint-section">
        <span class="bp-no">04</span>
        <span class="bp-title">学习分类</span>
        <span class="bp-sub">按方向找到你的起点</span>
        <span class="bp-line" />
      </div>
      <div class="cat-grid">
        <button
          v-for="g in catGroups"
          :key="g.parent.id"
          class="cat-tile"
          @click="router.push({ path: '/resources', query: { categoryId: g.parent.id } })"
        >
          <span class="cat-name">{{ g.parent.name }}</span>
          <span class="cat-children text-muted">{{ g.children.map((c) => c.name).join(' · ') || '综合' }}</span>
        </button>
      </div>

      <!-- ============ 05 AI 助手 ============ -->
      <div class="blueprint-section">
        <span class="bp-no">05</span>
        <span class="bp-title">AI 学习助手</span>
        <span class="bp-sub">随时提问 · 全站悬浮</span>
        <span class="bp-line" />
      </div>
      <section class="ai-banner tech-frame">
        <div class="ai-text">
          <h3>🤖 学不会？问 AI。</h3>
          <p>智能推荐学习资源、分析学习进度、解答 3D 建模问题——点击右下角悬浮按钮，AI 助手随叫随到。</p>
        </div>
        <el-button size="large" class="ai-btn" @click="router.push('/ai/chat')">进入 AI 答疑 →</el-button>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* ================= HERO：工程图纸 ================= */
.hero {
  background: var(--el-bg-color);
  border-bottom: 2px solid var(--line-color);
  padding: 48px 16px 40px;
  position: relative;
}
.hero-frame {
  max-width: 1200px;
  margin: 0 auto;
  padding: 28px 40px 20px;
}
.hero-annotation {
  position: absolute;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 2px;
  color: var(--el-text-color-secondary);
}
.ann-tl { top: 2px; left: 10px; }
.ann-tr { top: 2px; right: 10px; }
.ann-bl { bottom: 0; left: 10px; }
.ann-br { bottom: 0; right: 10px; }

.hero-inner {
  display: flex;
  align-items: center;
  gap: 48px;
}
.hero-left {
  flex-shrink: 0;
}
.hero-right {
  flex: 1;
  min-width: 0;
}

/* 旋转线框立方体 */
.cube-stage {
  width: 240px;
  height: 240px;
  position: relative;
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
  width: 150px;
  height: 150px;
  margin: -75px 0 0 -75px;
  border: 2px solid var(--line-color);
  background: transparent;
}
.face.front { transform: translateZ(75px); }
.face.back { transform: rotateY(180deg) translateZ(75px); }
.face.left { transform: rotateY(-90deg) translateZ(75px); }
.face.right { transform: rotateY(90deg) translateZ(75px); }
.face.top { transform: rotateX(90deg) translateZ(75px); }
.face.bottom { transform: rotateX(-90deg) translateZ(75px); }
.cube-inner {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 60px;
  height: 60px;
  margin: -30px 0 0 -30px;
  border: 1px dashed var(--line-color);
  transform: translateZ(0);
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
.crosshair { position: absolute; }
.ch-1 { top: 8px; left: 8px; }
.ch-2 { top: 8px; right: 8px; }
.ch-3 { bottom: 8px; left: 8px; }
.ch-4 { bottom: 8px; right: 8px; }
.cube-label {
  position: absolute;
  bottom: -6px;
  left: 0;
  right: 0;
  text-align: center;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 1px;
  color: var(--el-text-color-secondary);
}

/* 标题区 */
.hero-kicker {
  margin-bottom: 18px;
  max-width: 560px;
}
.hero-title {
  font-size: 44px;
  line-height: 1.25;
  letter-spacing: 4px;
  margin: 0 0 14px;
  font-weight: 800;
}
.hero-accent {
  color: var(--theme-color);
  position: relative;
}
.hero-line {
  width: 120px;
  height: 4px;
  background: var(--theme-color);
  margin-bottom: 18px;
  transform-origin: left;
  animation: line-draw 1.4s ease-out;
}
@keyframes line-draw {
  from { transform: scaleX(0); }
  to { transform: scaleX(1); }
}
.hero-slogan {
  font-size: 15px;
  line-height: 1.9;
  color: var(--el-text-color-secondary);
  letter-spacing: 1px;
  margin: 0 0 26px;
  max-width: 620px;
}
.hero-search {
  max-width: 560px;
  margin-bottom: 22px;
}
.hero-search :deep(.el-input__wrapper) {
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
  flex-wrap: wrap;
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
  border: 1px solid var(--line-color);
  color: var(--el-text-color-primary);
}
.ghost:hover {
  border-color: var(--theme-color);
  color: var(--theme-color);
}

/* 数据条 */
.hero-stats {
  margin-top: 30px;
  padding-top: 16px;
  border-top: 1px solid var(--line-soft);
  display: flex;
  justify-content: center;
  gap: 32px;
  flex-wrap: wrap;
}
.stat {
  display: flex;
  align-items: baseline;
  gap: 10px;
}
.stat .num {
  font-size: 26px;
  font-weight: 800;
  color: var(--theme-color);
  font-family: 'Consolas', 'Courier New', monospace;
}
.stat .label {
  font-size: 13px;
  letter-spacing: 2px;
  color: var(--el-text-color-secondary);
}
.stat-divider {
  width: 1px;
  background: var(--line-soft);
}

/* ================= 理念区 ================= */
.idea-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.idea-card {
  border: 1px solid var(--border-color);
  background: var(--el-bg-color);
  padding: 26px 22px;
  position: relative;
}
.idea-no {
  position: absolute;
  top: 12px;
  right: 16px;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 26px;
  font-weight: 700;
  color: var(--line-soft);
  letter-spacing: 2px;
}
.idea-svg {
  width: 40px;
  height: 40px;
  stroke: var(--theme-color);
  stroke-width: 1.5;
  fill: none;
  margin-bottom: 14px;
}
.idea-card h3 {
  font-size: 22px;
  letter-spacing: 4px;
  margin: 0 0 8px;
}
.idea-card p {
  margin: 0;
  color: var(--el-text-color-secondary);
  line-height: 1.8;
  font-size: 14px;
}

/* ================= 蓝图分区标题延伸 ================= */
.bp-more {
  flex-shrink: 0;
}

/* ================= 分类磁贴（线条风） ================= */
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
  background: var(--el-bg-color);
  cursor: pointer;
  text-align: left;
  transition: all 0.15s;
  position: relative;
}
.cat-tile::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: transparent;
}
.cat-tile:hover {
  border-color: var(--line-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}
.cat-tile:hover::before {
  background: var(--theme-color);
}
.cat-name {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 2px;
}
.cat-children {
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ================= AI 横幅（线稿黑卡） ================= */
.ai-banner {
  padding: 26px 32px;
  border: 1px solid var(--line-color);
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

/* ================= 响应式 ================= */
@media (max-width: 960px) {
  .hero-inner {
    flex-direction: column;
    text-align: center;
    gap: 24px;
  }
  .hero-title {
    font-size: 34px;
  }
  .hero-line {
    margin: 0 auto 18px;
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
  .hero-kicker {
    margin-left: auto;
    margin-right: auto;
  }
  .idea-grid {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 640px) {
  .hero-frame {
    padding: 22px 18px 14px;
  }
  .hero-annotation {
    display: none;
  }
  .cube-stage {
    width: 180px;
    height: 180px;
  }
  .face {
    width: 110px;
    height: 110px;
    margin: -55px 0 0 -55px;
  }
  .face.front { transform: translateZ(55px); }
  .face.back { transform: rotateY(180deg) translateZ(55px); }
  .face.left { transform: rotateY(-90deg) translateZ(55px); }
  .face.right { transform: rotateY(90deg) translateZ(55px); }
  .face.top { transform: rotateX(90deg) translateZ(55px); }
  .face.bottom { transform: rotateX(-90deg) translateZ(55px); }
  .hero-stats {
    gap: 16px;
  }
  .stat-divider {
    display: none;
  }
  .ai-banner {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
