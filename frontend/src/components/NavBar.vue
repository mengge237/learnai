<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { setLocale } from '@/i18n'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { interactionApi } from '@/api/interaction'
import { resourceApi } from '@/api/resources'
import LineIcon from '@/components/LineIcon.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()
const { t, locale } = useI18n()

const searchText = ref('')
const favCount = ref(0)

// 我的学习 hover 面板：打开时实时拉取
const studyList = ref([])
const studyLoading = ref(false)
const STUDY_STATUS = computed(() => ({
  NotStarted: t('未开始'),
  InProgress: t('学习中'),
  Completed: t('已完成'),
}))

async function loadMyLearning() {
  if (!auth.isLoggedIn || studyLoading.value) return
  studyLoading.value = true
  try {
    studyList.value = (await resourceApi.myLearning()).slice(0, 6)
  } catch {
    studyList.value = []
  } finally {
    studyLoading.value = false
  }
}

// 登录后加载收藏数量（导航以收藏为主，购物车入口移入模型资源库页内）
watch(
  () => auth.isLoggedIn,
  async (v) => {
    if (!v) {
      favCount.value = 0
      return
    }
    try {
      favCount.value = (await interactionApi.favorites()).length
    } catch {
      favCount.value = 0
    }
  },
  { immediate: true },
)

const activeMenu = computed(() => {
  const seg = route.path.split('/')[1]
  return seg === '' ? '/' : `/${seg}`
})

const menus = computed(() => [
  { index: '/', label: t('首页') },
  { index: '/resources', label: t('学习资源') },
  { index: '/paths', label: t('学习路径') },
  { index: '/market', label: t('模型资源库') },
  { index: '/guide', label: t('操作指南') },
])

function onSearch() {
  const kw = searchText.value.trim()
  if (!kw) return
  router.push({ path: '/search', query: { q: kw } })
}

/** 中英文切换（本地生效，不依赖后端） */
function toggleLang() {
  setLocale(locale.value === 'en-US' ? 'zh-CN' : 'en-US')
}

async function handleCommand(cmd) {
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm(t('确定要退出登录吗？'), t('提示'), { type: 'warning' })
    } catch {
      return // 用户取消
    }
    auth.logout()
    cart.clear()
    router.push({ name: 'home' })
  } else {
    router.push(cmd)
  }
}
</script>

<template>
  <header class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="logo">
        <span class="logo-mark" />
        <span class="logo-text">AI智学<span class="logo-sub">{{ $t('校园学习平台') }}</span></span>
        <span class="sys-status"><i class="led" />ONLINE</span>
      </router-link>

      <!-- ellipsis=true：宽度不足时收起溢出项到「…」子菜单，避免与右侧搜索框重叠 -->
      <el-menu :default-active="activeMenu" mode="horizontal" :ellipsis="true" router class="nav-menu">
        <el-menu-item v-for="(m, i) in menus" :key="m.index" :index="m.index">
          <span class="mi-no">{{ String(i + 1).padStart(2, '0') }}</span>{{ m.label }}
        </el-menu-item>
        <el-menu-item v-if="auth.isLoggedIn" index="/console">{{ $t('控制台') }}</el-menu-item>
        <el-menu-item v-if="auth.isAuditorOrAdmin" index="/audit">{{ $t('审核工作台') }}</el-menu-item>
        <el-menu-item v-if="auth.isAdmin" index="/admin">{{ $t('管理后台') }}</el-menu-item>
      </el-menu>

      <div class="navbar-right">
        <el-input
          v-model="searchText"
          class="search-input"
          :placeholder="$t('全局搜索：课程 / 路径 / 模型…')"
          clearable
          @keyup.enter="onSearch"
        >
          <template #append>
            <el-button class="search-btn" @click="onSearch" :aria-label="$t('搜索')">
              <LineIcon name="search" :size="15" />
            </el-button>
          </template>
        </el-input>

        <!-- 语言切换：显示目标语言（中文界面显示 EN，英文界面显示 中） -->
        <el-tooltip :content="$t('切换语言 / Switch language')">
          <button class="icon-btn lang-btn" @click="toggleLang" :title="$t('切换语言')">
            {{ locale === 'en-US' ? '中' : 'EN' }}
          </button>
        </el-tooltip>

        <el-tooltip :content="$t('个性化设置')">
          <button class="icon-btn" @click="router.push('/user/settings')" :title="$t('个性化设置')">
            <LineIcon name="settings" :size="19" />
          </button>
        </el-tooltip>

        <!-- 我的学习：悬停下拉实时显示学习进度（点击进入完整页） -->
        <el-popover
          v-if="auth.isLoggedIn"
          trigger="hover"
          :width="360"
          placement="bottom-start"
          :show-after="80"
          :hide-after="150"
          @show="loadMyLearning"
        >
          <template #reference>
            <span
              class="text-btn nav-study"
              :class="{ active: route.path.startsWith('/resources/my') }"
              @click="router.push('/resources/my')"
            >
              {{ $t('我的学习') }}
            </span>
          </template>
          <div class="study-panel" v-loading="studyLoading">
            <div class="study-panel-head">
              <span class="sp-title">{{ $t('我的学习') }}</span>
              <span class="sp-sub text-muted">{{ $t('最近进度 · 实时更新') }}</span>
            </div>
            <template v-if="studyList.length">
              <div v-for="r in studyList" :key="r.resourceId" class="sp-item" @click="router.push(`/resources/${r.resourceId}/learn`)">
                <div class="sp-item-head">
                  <span class="sp-item-title">{{ r.title }}</span>
                  <span class="sp-status" :class="r.status">{{ STUDY_STATUS[r.status] || r.status }}</span>
                </div>
                <div class="sp-item-progress">
                  <div class="sp-bar"><i :style="{ width: `${Math.round(r.progress ?? 0)}%` }" /></div>
                  <span class="sp-pct">{{ Math.round(r.progress ?? 0) }}%</span>
                </div>
              </div>
            </template>
            <div v-else-if="!studyLoading" class="sp-empty">
              {{ $t('还没有学习记录，去') }}<a @click="router.push('/resources')">{{ $t('学习资源') }}</a>{{ $t('开始学习吧') }}
            </div>
            <div class="sp-foot">
              <router-link to="/resources/my">{{ $t('查看全部学习记录') }} <LineIcon name="arrowRight" :size="13" /></router-link>
            </div>
          </div>
        </el-popover>

        <el-badge :value="favCount" :hidden="favCount === 0">
          <button class="text-btn" @click="router.push('/user/favorites')" :title="$t('我的收藏')">{{ $t('我的收藏') }}</button>
        </el-badge>

        <template v-if="auth.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-chip">
              <span class="avatar">{{ (auth.user?.username || '?').slice(0, 1).toUpperCase() }}</span>
              <span class="user-name">{{ auth.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="/console">{{ $t('个人控制台') }}</el-dropdown-item>
                <el-dropdown-item command="/ai/analytics">{{ $t('学习分析') }}</el-dropdown-item>
                <el-dropdown-item command="/ai/recommend">{{ $t('智能推荐') }}</el-dropdown-item>
                <el-dropdown-item command="/user/profile" divided>{{ $t('个人资料') }}</el-dropdown-item>
                <el-dropdown-item command="/user/settings">{{ $t('个性化设置') }}</el-dropdown-item>
                <el-dropdown-item command="/user/favorites">{{ $t('我的收藏') }}</el-dropdown-item>
                <el-dropdown-item command="/user/downloads">{{ $t('下载历史') }}</el-dropdown-item>
                <el-dropdown-item command="/market/orders">{{ $t('我的订单') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin" divided>{{ $t('管理后台') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/dashboard">{{ $t('数据看板') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/users">{{ $t('用户管理') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/resources">{{ $t('资源管理') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/models">{{ $t('模型管理') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/orders">{{ $t('订单管理') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/categories">{{ $t('分类管理') }}</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAuditorOrAdmin" command="/audit" divided>{{ $t('审核工作台') }}</el-dropdown-item>
                <el-dropdown-item command="logout" divided>{{ $t('退出登录') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push({ name: 'login', query: { redirect: route.fullPath } })">
            {{ $t('登录') }}
          </el-button>
          <el-button @click="router.push('/register')">{{ $t('注册') }}</el-button>
        </template>
      </div>
    </div>
    <!-- 工业警示条：导航底部橙色斜纹带 -->
    <div class="hazard nav-hazard" />
  </header>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  /* 毛玻璃导航：半透明 + 背景模糊，页面滚动时内容从下方透过 */
  background: var(--el-bg-color);
  background: color-mix(in srgb, var(--el-bg-color) 72%, transparent);
  -webkit-backdrop-filter: blur(14px) saturate(1.4);
  backdrop-filter: blur(14px) saturate(1.4);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 4px 16px rgba(23, 24, 28, 0.05);
}
.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  height: 60px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 20px;
  font-weight: 700;
  color: var(--theme-color);
  white-space: nowrap;
}
/* 工业警示方块标识：橙黑斜纹（替代原符号） */
.logo-mark {
  display: inline-block;
  width: 14px;
  height: 14px;
  background: repeating-linear-gradient(-45deg, var(--theme-color) 0 5px, #141518 5px 10px);
  border: 1px solid var(--line-color);
  flex-shrink: 0;
}
.logo-sub {
  font-size: 12px;
  font-weight: 400;
  color: var(--el-text-color-secondary);
  margin-left: 4px;
}
/* 系统状态徽标：LED + ONLINE */
.sys-status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  margin-left: 10px;
  padding: 2px 8px;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--el-color-success);
}
.nav-menu {
  flex: 1;
  border-bottom: none;
  min-width: 0;
  /* 菜单自身透明，避免盖住导航条的毛玻璃效果 */
  background: transparent;
  --el-menu-bg-color: transparent;
}
/* 菜单项编号（机箱目录感） */
.nav-menu :deep(.mi-no) {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 10px;
  letter-spacing: 0;
  color: var(--theme-color);
  opacity: 0.75;
  margin-right: 4px;
}
/* 激活态：橙色粗下划线 */
.nav-menu :deep(.el-menu-item.is-active) {
  border-bottom: 3px solid var(--theme-color) !important;
  color: var(--theme-color) !important;
  font-weight: 700;
}
.nav-menu :deep(.el-menu-item:hover) {
  color: var(--theme-color);
}
/* 导航底部警示斜纹 */
.nav-hazard {
  --hz-h: 4px;
}
.navbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
}
.search-input {
  width: 380px;
}
.search-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.icon-btn {
  border: none;
  background: none;
  color: var(--el-text-color-primary);
  cursor: pointer;
  padding: 4px 6px;
  line-height: 1;
  display: inline-flex;
  align-items: center;
  transition: color 0.2s;
}
.icon-btn:hover {
  color: var(--theme-color);
}
/* 语言切换按钮：等宽字体小徽标 + 印刷硬阴影 */
.lang-btn {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 1px;
  border: 1px solid var(--border-color);
  border-radius: 2px;
  padding: 3px 7px;
  box-shadow: 2px 2px 0 color-mix(in srgb, var(--line-color) 25%, transparent);
}
.lang-btn:hover {
  border-color: var(--theme-color);
}
.text-btn {
  border: none;
  background: none;
  font-size: 14px;
  color: var(--el-text-color-primary);
  cursor: pointer;
  padding: 6px 2px;
  font-family: inherit;
  border-bottom: 1px solid transparent;
  transition: color 0.2s;
}
.text-btn:hover {
  color: var(--theme-color);
  border-bottom-color: var(--theme-color);
}
.user-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
}
.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--theme-color);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}
/* 我的学习按钮（导航右侧，与收藏同款文字按钮） */
.nav-study {
  flex-shrink: 0;
  white-space: nowrap;
}
.nav-study.active {
  color: var(--theme-color);
  border-bottom-color: var(--theme-color);
}
/* 我的学习下拉面板 */
.study-panel {
  min-height: 90px;
}
.study-panel-head {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 6px;
}
.sp-title {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 1px;
  padding-left: 8px;
  border-left: 4px solid var(--theme-color);
}
.sp-sub {
  font-size: 12px;
}
.sp-item {
  padding: 9px 8px;
  cursor: pointer;
  border-bottom: 1px dashed var(--border-color);
}
.sp-item:last-of-type {
  border-bottom: none;
}
.sp-item:hover .sp-item-title {
  color: var(--theme-color);
}
.sp-item-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.sp-item-title {
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s;
}
.sp-status {
  font-size: 12px;
  flex-shrink: 0;
  color: var(--el-text-color-secondary);
}
.sp-status.InProgress {
  color: var(--el-color-success);
}
.sp-status.Completed {
  color: var(--theme-color);
}
.sp-item-progress {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}
.sp-bar {
  flex: 1;
  height: 3px;
  background: var(--el-fill-color);
}
.sp-bar i {
  display: block;
  height: 100%;
  background: var(--theme-color);
  transition: width 0.4s;
}
.sp-pct {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  width: 34px;
  text-align: right;
}
.sp-empty {
  padding: 18px 8px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.sp-empty a {
  color: var(--theme-color);
  cursor: pointer;
}
.sp-foot {
  text-align: right;
  padding-top: 8px;
  border-top: 1px solid var(--border-color);
}
.sp-foot a {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--theme-color);
}
@media (max-width: 1280px) {
  .search-input {
    width: 240px;
  }
}
@media (max-width: 900px) {
  .search-input {
    display: none;
  }
}
@media (max-width: 640px) {
  .navbar-inner {
    gap: 8px;
    padding: 0 8px;
  }
  .logo-sub,
  .user-name,
  .icon-btn,
  .sys-status {
    display: none;
  }
  .logo-text {
    font-size: 17px;
  }
  .nav-menu {
    overflow-x: auto;
  }
}
</style>
