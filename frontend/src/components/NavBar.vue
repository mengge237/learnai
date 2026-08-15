<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { usePrefsStore } from '@/stores/prefs'
import { interactionApi } from '@/api/interaction'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()
const prefs = usePrefsStore()

const searchText = ref('')
const favCount = ref(0)

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
  router.push({ path: '/resources', query: { search: searchText.value || undefined } })
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

      <el-menu :default-active="activeMenu" mode="horizontal" :ellipsis="false" router class="nav-menu">
        <el-menu-item v-for="m in menus" :key="m.index" :index="m.index">{{ m.label }}</el-menu-item>
        <el-menu-item v-if="auth.isLoggedIn" index="/resources/my">我的学习</el-menu-item>
        <el-menu-item v-if="auth.isLoggedIn" index="/console">控制台</el-menu-item>
      </el-menu>

      <div class="navbar-right">
        <el-input
          v-model="searchText"
          class="search-input"
          placeholder="搜索学习资源 / 3D 模型…"
          clearable
          @keyup.enter="onSearch"
        >
          <template #append>
            <el-button @click="onSearch">🔍</el-button>
          </template>
        </el-input>

        <el-tooltip content="个性化设置">
          <button class="icon-btn" @click="router.push('/user/settings')" title="个性化设置">⚙️</button>
        </el-tooltip>

        <el-tooltip :content="prefs.prefs.darkMode ? '切换到浅色模式' : '切换到深色模式'">
          <button class="icon-btn" @click="prefs.toggleDark()">
            {{ prefs.prefs.darkMode ? '☀️' : '🌙' }}
          </button>
        </el-tooltip>

        <el-tooltip content="我的收藏">
          <el-badge :value="favCount" :hidden="favCount === 0">
            <button class="icon-btn fav-btn" @click="router.push('/user/favorites')" title="我的收藏">☆</button>
          </el-badge>
        </el-tooltip>

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
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/dashboard" divided>数据看板</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAdmin" command="/admin/users">用户管理</el-dropdown-item>
                <el-dropdown-item v-if="auth.isAuditorOrAdmin" command="/audit">内容审核</el-dropdown-item>
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
  gap: 16px;
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
  width: 320px;
}
.icon-btn {
  border: none;
  background: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px 6px;
  line-height: 1;
}
.fav-btn {
  font-size: 22px;
  color: var(--el-text-color-primary);
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
@media (max-width: 1000px) {
  .search-input {
    width: 200px;
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
  .user-name {
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
