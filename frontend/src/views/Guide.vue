<script setup>
import { useRouter } from 'vue-router'
import LineIcon from '@/components/LineIcon.vue'
import ScrambleText from '@/components/ScrambleText.vue'

const router = useRouter()

/**
 * 操作指南：全站功能说明书（蓝图图纸风格，中英文随语言切换）。
 * sections 里所有中文文案都是 i18n key，英文词典见 src/i18n/en/guide.js
 */
const sections = [
  {
    no: '01',
    en: 'QUICK START',
    title: '快速上手',
    desc: '三步成为校园学习平台的新成员。',
    steps: ['注册校园账号，学号可选填', '登录后解锁全部学习功能', '从首页搜索或按分类找到第一门课'],
    to: '/register',
    cta: '立即注册',
  },
  {
    no: '02',
    en: 'COURSES',
    title: '学习资源',
    desc: '系统化教程逐章可读，每个资源包含三个学习步骤。',
    steps: ['按分类筛选或搜索关键词找到课程', '进入详情页查看简介、难度与时长', '点击「开始学习」，按三步进阶法逐章完成', '每章支持语音朗读，眼睛累了可以听'],
    to: '/resources',
    cta: '浏览课程',
  },
  {
    no: '03',
    en: 'PATHS',
    title: '学习路径',
    desc: '规划好的成长路线，让学习有方向。',
    steps: ['查看路径包含的资源序列', '一键报名，路径会出现在「我的路径」', '按顺序学习，逐项点亮进度'],
    to: '/paths',
    cta: '查看路径',
  },
  {
    no: '04',
    en: 'MY LEARNING & ANALYTICS',
    title: '我的学习与学习分析',
    desc: '每一步进步都有迹可循。',
    steps: ['学习页自动计时，每 25 分钟番茄钟提醒休息', '连续学习天数打卡激励', '个人控制台查看累计时长与本周统计', '学习分析页查看分类分布与学习趋势'],
    to: '/console',
    cta: '打开控制台',
  },
  {
    no: '05',
    en: 'MODEL LIBRARY',
    title: '模型资源库',
    desc: '在线预览 3D 作品，让灵感直接落地。',
    steps: ['浏览模型目录，支持分类与搜索', '详情页在线 3D 预览，可拖拽旋转观察', '收藏感兴趣的模型，下载可用的资源文件', '加入购物车结算，模拟支付生成订单'],
    to: '/market',
    cta: '逛逛模型库',
  },
  {
    no: '06',
    en: 'AI ASSISTANT',
    title: 'AI 助手',
    desc: '学习路上，答疑随行。',
    steps: ['右下角随时唤起答疑抽屉，全站可用', '支持语音提问（Chrome/Edge 浏览器）', '智能推荐根据你的学习情况推荐资源', '学习分析帮你了解自己的学习状态'],
    to: '/ai/chat',
    cta: '去提问',
  },
  {
    no: '07',
    en: 'SUBMISSION & REVIEW',
    title: '提交资源与审核',
    desc: '校园共建内容，人人可分享知识。',
    steps: ['普通用户可提交学习资源，进入待审核状态', '审核员在审核工作台通过或驳回', '管理员管理用户、资源、模型与订单'],
    to: '/resources/submit',
    cta: '提交资源',
  },
  {
    no: '08',
    en: 'PREFERENCES',
    title: '个性化设置',
    desc: '把网站调成你喜欢的样子。',
    steps: ['主题色、字体大小、边框颜色自由搭配', '浅色 / 深色 / 跟随系统三种外观模式', '右上角 EN / 中 按钮一键切换中英文'],
    to: '/user/settings',
    cta: '去设置',
  },
]
</script>

<template>
  <div class="guide-page">
    <!-- ============ 顶部标题区（深色对撞 + 工程网格 + 玻璃条） ============ -->
    <section class="guide-hero">
      <div class="guide-hero-inner">
        <div class="gh-top">
          <span class="gh-en"><ScrambleText text="OPERATION MANUAL · USER GUIDE" auto /></span>
          <span class="gh-en">{{ $t('版本 2026 · 校园版') }}</span>
        </div>
        <h1 class="gh-title">{{ $t('操作指南') }}</h1>
        <p class="gh-sub">{{ $t('从这里开始 · 三分钟上手 AI智学') }}</p>
        <div class="gh-stats">
          <span class="gh-stat">{{ $t('学习') }}<b>{{ $t('学') }}</b></span>
          <i class="gh-line" />
          <span class="gh-stat">{{ $t('练习') }}<b>{{ $t('练') }}</b></span>
          <i class="gh-line" />
          <span class="gh-stat">{{ $t('创作') }}<b>{{ $t('创') }}</b></span>
        </div>
      </div>
    </section>

    <!-- ============ 指南章节 ============ -->
    <div class="page-container blueprint-grid guide-main">
      <section v-for="s in sections" :key="s.no" class="guide-card glass">
        <div class="blueprint-section">
          <span class="bp-no">{{ s.no }}</span>
          <span class="bp-title">{{ $t(s.title) }}</span>
          <span class="bp-sub">{{ s.en }}</span>
          <span class="bp-line" />
        </div>
        <p class="g-desc">{{ $t(s.desc) }}</p>
        <ol class="g-steps">
          <li v-for="(st, i) in s.steps" :key="i">
            <span class="g-step-no">{{ String(i + 1).padStart(2, '0') }}</span>
            <span class="g-step-text">{{ $t(st) }}</span>
          </li>
        </ol>
        <button class="g-cta" @click="router.push(s.to)">
          {{ $t(s.cta) }} <LineIcon name="arrowRight" :size="14" />
        </button>
      </section>

      <!-- 收尾 -->
      <section class="g-end">
        <span class="crosshair" />
        <p>{{ $t('还有疑问？随时点击右下角「答疑」按钮，AI 助手全天候在线。') }}</p>
        <span class="crosshair" />
      </section>
    </div>
  </div>
</template>

<style scoped>
/* ================= 顶部标题区 ================= */
.guide-hero {
  background: var(--el-color-primary);
  background-image: linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 32px 32px;
  color: var(--el-bg-color);
  border-bottom: 1px solid var(--line-color);
}
.guide-hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 36px 24px 44px;
}
.gh-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 10px 16px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.06);
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.08) inset;
}
.gh-en {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  letter-spacing: 3px;
  opacity: 0.7;
}
.gh-title {
  font-size: 52px;
  font-weight: 900;
  letter-spacing: 10px;
  margin: 34px 0 12px;
}
.gh-sub {
  font-size: 15px;
  letter-spacing: 3px;
  opacity: 0.75;
  margin: 0 0 30px;
}
.gh-stats {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}
.gh-stat {
  font-size: 12px;
  letter-spacing: 4px;
  opacity: 0.7;
  display: inline-flex;
  align-items: baseline;
  gap: 10px;
}
.gh-stat b {
  font-size: 26px;
  font-weight: 900;
  color: var(--theme-color);
  opacity: 1;
}
.gh-line {
  width: 1px;
  height: 26px;
  background: rgba(128, 128, 128, 0.35);
}
/* ================= 章节卡片 ================= */
.guide-main {
  padding-top: 40px;
  padding-bottom: 64px;
}
.guide-card {
  margin-bottom: 22px;
  padding: 22px 26px 24px;
  border-radius: 2px;
}
.guide-card .blueprint-section {
  margin: 0 0 12px;
}
.g-desc {
  margin: 0 0 14px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  letter-spacing: 1px;
  line-height: 1.9;
}
.g-steps {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 32px;
}
.g-steps li {
  display: flex;
  align-items: baseline;
  gap: 12px;
  font-size: 14px;
  line-height: 1.8;
  letter-spacing: 1px;
}
.g-step-no {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 12px;
  color: var(--theme-color);
  flex-shrink: 0;
}
.g-cta {
  margin-top: 18px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: var(--el-color-primary);
  color: var(--el-bg-color);
  border: none;
  border-radius: 2px;
  font-size: 13px;
  letter-spacing: 2px;
  padding: 9px 20px;
  cursor: pointer;
  transition: background 0.15s;
}
.g-cta:hover {
  background: var(--theme-color);
}
/* 收尾 */
.g-end {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 30px 0 10px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  letter-spacing: 2px;
}
@media (max-width: 640px) {
  .gh-title {
    font-size: 34px;
  }
  .g-steps {
    grid-template-columns: 1fr;
  }
  .guide-card {
    padding: 16px;
  }
}
</style>
