<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { interactionApi } from '@/api/interaction'
import { resourceApi } from '@/api/resources'
import LineIcon from '@/components/LineIcon.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()

const searchText = ref('')
const favCount = ref(0)

// 我的学习 hover 面板：打开时实时拉取
const studyList = ref([])
const studyLoading = ref(false)
const STUDY_STATUS = { NotStarted: '未开始', InProgress: '学习中', Completed: '已完成' }

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

const menus = [
  { index: '/', label: '首页' },
  { index: '/resources', label: '学习资源' },
  { index: '/paths', label: '学习路径' },
  { index: '/market', label: '模型资源库' },
]

function onSearch() {
  const kw = searchText.value.trim()
  if (!kw) return
  router.push({ path: '/search', query: { q: kw } })
}

async function handleCommand(cmd) {
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
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
        <span class="logo-mark">◈</span>
        <span class="logo-text">AI智学<span class="logo-sub">校园学习平台</span></span>
      </router-link>

      <!-- ellipsis=true：宽度不足时收起溢出项到「…」子菜单，避免与右侧搜索框重叠 -->
      <el-menu :default-active="activeMenu" mode="horizontal" :ellipsis="true" router class="nav-menu">
        <el-menu-item v-for="m in menus" :key="m.index" :index="m.index">{{ m.label }}</el-menu-item>
        <el-menu-item v-if="auth.isLoggedIn" index="/console">控制台</el-menu-item>
        <el-menu-item v-if="auth.isAuditorOrAdmin" index="/audit">审核工作台</el-menu-item>
        <el-menu-item v-if="auth.isAdmin" index="/admin">管理后台</el-menu-item>
      </el-menu>

      <div class="navbar-right">
        <el-input
          v-model="searchText"
          class="search-input"
          placeholder="全局搜索：课程 / 路径 / 模型…"
          clearable
          @keyup.enter="onSearch"
        >
          <template #append>
            <el-button class="search-btn" @click="onSearch" aria-label="搜索">
              <LineIcon name="search" :size="15" />
            </el-button>
          </template>
        </el-input>

        <el-tooltip content="个性化设置">
          <button class="icon-btn" @click="router.push('/user/settings')" title="个性化设置">
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
              我的学习
            </span>
          </template>
          <div class="study-panel" v-loading="studyLoading">
            <div class="study-panel-head">
              <span class="sp-title">我的学习</span>
              <span class="sp-sub text-muted">最近进度 · 实时更新</span>
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
              还没有学习记录，去<a @click="router.push('/resources')">学习资源</a>开始学习吧
            </div>
            <div class="sp-foot">
              <router-link to="/resources/my">查看全部学习记录 <LineIcon name="arrowRight" :size="13" /></router-link>
            </div>
          </div>
        </el-popover>

        <el-badge :value="favCount" :hidden="favCount === 0">
          <button class="text-btn" @click="router.push('/user/favorites')" title="我的收藏">我的收藏</button>
        </el-badge>

        <template v-if="auth.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-chip">
              <span class="avatar">{{ (auth.user?.username || '?').slice(0, 1).toUpperCase() }}</span>
              <span class="user-name">{{ auth.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="/console">个人控制台</el-dropdown-item>
                <el-dropdown-item command="/ai/analytics">学习分析</el-dropdown-item>
                <el-dropdown-item command="/ai/recommend">智能推荐</el-dropdown-item>
                <el-dropdown-item command="/user/profile" divided>个人资料</el-dropdown-item>
                <el-dropdown-item command="/user/settings">个性化设置</el-dropdown-item>
                <el-dropdown-item command="/user/favorites">我的收藏</el-dropdown-item>
                <el-dropdown-item command="/user/downloads">下载历史</el-dropdown-item>
                <el-dropdown-item command="/market/orders">我的订单</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin" divided>管理后台</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/dashboard">数据看板</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/users">用户管理</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/resources">资源管理</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/models">模型管理</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/orders">订单管理</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/categories">分类管理</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAuditorOrAdmin" command="/audit" divided>审核工作台</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push({ name: 'login', query: { redirect: route.fullPath } })">
            登录
          </el-button>
          <el-button @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--border-color);
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
.logo-mark {
  font-size: 24px;
}
.logo-sub {
  font-size: 12px;
  font-weight: 400;
  color: var(--el-text-color-secondary);
  margin-left: 4px;
}
.nav-menu {
  flex: 1;
  border-bottom: none;
  min-width: 0;
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
  .icon-btn {
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
