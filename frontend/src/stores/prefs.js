import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import { useAuthStore } from './auth'

const PREFS_KEY = 'learnai_prefs'

const DEFAULTS = {
  fontSize: 16,
  borderColor: '#d0d0d0',
  themeColor: '#e8590c',
  darkMode: false,
  themeMode: 'auto', // light / dark / auto（跟随系统）
  sidebarPosition: 'left',
  animationSpeed: 'normal',
}

let mediaWatcher = null

/**
 * 界面个性化偏好：本地持久化 + 登录后与服务器同步（服务器优先）。
 * 外观模式三态：浅色 / 深色 / 跟随系统（auto 监听系统 prefers-color-scheme）。
 * apply() 把偏好写到 <html> 的 CSS 变量 / class / data 属性上。
 */
export const usePrefsStore = defineStore('prefs', {
  state: () => ({
    prefs: { ...DEFAULTS, ...JSON.parse(localStorage.getItem(PREFS_KEY) || '{}') },
  }),

  getters: {
    /** 实际生效的暗色状态（auto 模式按系统解析） */
    isDarkEffective() {
      return this.prefs.themeMode === 'auto'
        ? resolveSystemDark()
        : this.prefs.themeMode === 'dark'
    },
  },

  actions: {
    persist() {
      localStorage.setItem(PREFS_KEY, JSON.stringify(this.prefs))
    },
    /** 注册系统主题变化监听（仅一次），auto 模式下跟随系统实时切换 */
    ensureSystemWatch() {
      if (mediaWatcher) return
      const mq = window.matchMedia('(prefers-color-scheme: dark)')
      mediaWatcher = mq
      const onChange = () => {
        if (this.prefs.themeMode === 'auto') this.apply()
      }
      mq.addEventListener?.('change', onChange)
      mq.addListener?.(onChange) // 旧浏览器兜底
    },
    apply() {
      const root = document.documentElement
      this.ensureSystemWatch()
      root.style.setProperty('--font-size', `${this.prefs.fontSize}px`)
      // 深色下把用户选的边框色压暗（内联变量优先级最高，不处理会覆盖 html.dark 的深色边框）
      root.style.setProperty('--border-color', this.isDarkEffective ? darkenHex(this.prefs.borderColor, 0.42) : this.prefs.borderColor)
      root.style.setProperty('--theme-color', this.prefs.themeColor)
      root.classList.toggle('dark', this.isDarkEffective)
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
    /** 导航栏切换按钮：auto 时按当前实际状态切到明确的相反模式；否则 light/dark 互换 */
    toggleDark() {
      if (this.prefs.themeMode === 'auto') {
        return this.update({ themeMode: resolveSystemDark() ? 'light' : 'dark' })
      }
      return this.update({ themeMode: this.prefs.themeMode === 'dark' ? 'light' : 'dark' })
    },
  },
})

/** 系统当前是否为深色 */
function resolveSystemDark() {
  return typeof window !== 'undefined' && window.matchMedia?.('(prefers-color-scheme: dark)').matches
}

/** 十六进制颜色按比例压暗（ratio 0~1，结果 0~1 亮度区间），非法输入回退深灰 */
function darkenHex(hex, ratio) {
  const m = /^#?([0-9a-f]{6})$/i.exec(String(hex || ''))
  if (!m) return '#3a3a3a'
  const n = parseInt(m[1], 16)
  const dim = (v) => Math.max(0, Math.round(v * (1 - ratio)))
  return `#${[dim((n >> 16) & 255), dim((n >> 8) & 255), dim(n & 255)]
    .map((v) => v.toString(16).padStart(2, '0'))
    .join('')}`
}
