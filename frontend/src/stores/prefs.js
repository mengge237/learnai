import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import { useAuthStore } from './auth'

const PREFS_KEY = 'learnai_prefs'

const DEFAULTS = {
  fontSize: 16,
  borderColor: '#d0d0d0',
  themeColor: '#e8590c',
  darkMode: false,
  sidebarPosition: 'left',
  animationSpeed: 'normal',
}

/**
 * 界面个性化偏好：本地持久化 + 登录后与服务器同步（服务器优先）。
 * apply() 把偏好写到 <html> 的 CSS 变量 / class / data 属性上。
 */
export const usePrefsStore = defineStore('prefs', {
  state: () => ({
    prefs: { ...DEFAULTS, ...JSON.parse(localStorage.getItem(PREFS_KEY) || '{}') },
  }),

  actions: {
    persist() {
      localStorage.setItem(PREFS_KEY, JSON.stringify(this.prefs))
    },
    apply() {
      const root = document.documentElement
      root.style.setProperty('--font-size', `${this.prefs.fontSize}px`)
      root.style.setProperty('--border-color', this.prefs.borderColor)
      root.style.setProperty('--theme-color', this.prefs.themeColor)
      root.classList.toggle('dark', !!this.prefs.darkMode)
      root.setAttribute('data-animation', this.prefs.animationSpeed)
      root.setAttribute('data-sidebar', this.prefs.sidebarPosition)
    },
    /** 登录后：服务器偏好覆盖本地 */
    syncFromServer(user) {
      if (!user) return
      const keys = Object.keys(DEFAULTS)
      keys.forEach((k) => {
        if (user[k] !== undefined && user[k] !== null) this.prefs[k] = user[k]
      })
      this.persist()
      this.apply()
    },
    /** 修改偏好：本地立即生效，已登录时同步到服务器 */
    async update(patch) {
      Object.assign(this.prefs, patch)
      this.persist()
      this.apply()
      const auth = useAuthStore()
      if (auth.isLoggedIn) {
        try {
          await authApi.updatePreferences(patch)
        } catch {
          // 服务器同步失败不阻断本地生效
        }
      }
    },
    toggleDark() {
      return this.update({ darkMode: !this.prefs.darkMode })
    },
  },
})
