import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('@/views/Home.vue') },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue') },

  // 学习资源
  { path: '/resources', name: 'resources', meta: { title: '学习资源' }, component: () => import('@/views/Placeholder.vue') },
  { path: '/resources/submit', name: 'resource-submit', meta: { title: '提交资源', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/resources/my', name: 'my-learning', meta: { title: '我的学习', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/resources/:id', name: 'resource-detail', meta: { title: '资源详情' }, component: () => import('@/views/Placeholder.vue') },
  { path: '/resources/:id/learn', name: 'resource-learn', meta: { title: '开始学习', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },

  // 学习路径
  { path: '/paths', name: 'paths', meta: { title: '学习路径' }, component: () => import('@/views/Placeholder.vue') },
  { path: '/paths/my', name: 'my-paths', meta: { title: '我的路径', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/paths/:id', name: 'path-detail', meta: { title: '路径详情' }, component: () => import('@/views/Placeholder.vue') },

  // AI 助手
  { path: '/ai/chat', name: 'ai-chat', meta: { title: 'AI 答疑', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/ai/analytics', name: 'ai-analytics', meta: { title: '学习分析', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/ai/recommend', name: 'ai-recommend', meta: { title: '智能推荐', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },

  // 模型商城
  { path: '/market', name: 'market', meta: { title: '模型商城' }, component: () => import('@/views/Placeholder.vue') },
  { path: '/market/cart', name: 'cart', meta: { title: '购物车' }, component: () => import('@/views/Placeholder.vue') },
  { path: '/market/checkout', name: 'checkout', meta: { title: '结算', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/market/orders', name: 'orders', meta: { title: '我的订单', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/market/orders/:id', name: 'order-detail', meta: { title: '订单详情', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/market/:id', name: 'model-detail', meta: { title: '模型详情' }, component: () => import('@/views/Placeholder.vue') },

  // 个人中心
  { path: '/user/profile', name: 'profile', meta: { title: '个人资料', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/user/settings', name: 'settings', meta: { title: '个性化设置', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/user/favorites', name: 'favorites', meta: { title: '我的收藏', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },
  { path: '/user/downloads', name: 'downloads', meta: { title: '下载历史', requiresAuth: true }, component: () => import('@/views/Placeholder.vue') },

  // 管理 / 审核
  { path: '/admin/dashboard', name: 'admin-dashboard', meta: { title: '数据看板', requiresAuth: true, roles: ['ADMIN'] }, component: () => import('@/views/Placeholder.vue') },
  { path: '/admin/users', name: 'admin-users', meta: { title: '用户管理', requiresAuth: true, roles: ['ADMIN'] }, component: () => import('@/views/Placeholder.vue') },
  { path: '/audit', name: 'audit', meta: { title: '内容审核', requiresAuth: true, roles: ['ADMIN', 'AUDITOR'] }, component: () => import('@/views/Placeholder.vue') },

  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('@/views/NotFound.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

/** 登录守卫 + 角色守卫；已登录用户访问登录/注册页时回到首页 */
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.roles && !to.meta.roles.some((r) => auth.hasRole(r))) {
    return { name: 'home' }
  }
  if ((to.name === 'login' || to.name === 'register') && auth.isLoggedIn) {
    return { name: 'home' }
  }
  document.title = to.meta.title ? `${to.meta.title} - LearnAI 学习平台` : 'LearnAI 学习平台'
})

export default router
