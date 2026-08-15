import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'

const TOKEN_KEY = 'learnai_token'
const USER_KEY = 'learnai_user'

/**
 * 登录态：token + 用户信息，localStorage 持久化。
 * 角色判断基于 roleId（1=管理员、2=审核员、3=普通用户）。
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null'),
  }),

  getters: {
    isLoggedIn: (s) => !!s.token,
    roleId: (s) => s.user?.roleId,
    isAdmin: (s) => s.user?.roleId === 1,
    isAuditor: (s) => s.user?.roleId === 2,
    isAuditorOrAdmin: (s) => s.user?.roleId === 1 || s.user?.roleId === 2,
    hasRole: (s) => (role) => {
      if (role === 'ADMIN') return s.user?.roleId === 1
      if (role === 'AUDITOR') return s.user?.roleId === 2
      return false
    },
  },

  actions: {
    persist() {
      localStorage.setItem(TOKEN_KEY, this.token)
      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    },
    async login(payload) {
      const data = await authApi.login(payload)
      this.token = data.token
      this.user = data.user
      this.persist()
      return data
    },
    async register(payload) {
      await authApi.register(payload)
    },
    /** 拉取最新用户信息（角色变更即时生效） */
    async fetchMe() {
      const user = await authApi.me()
      this.user = user
      this.persist()
      return user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
  },
})
