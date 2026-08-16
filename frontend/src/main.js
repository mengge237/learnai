import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus, { ElMessage } from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import App from './App.vue'
import router from './router'
import i18n from './i18n'
import './style.css'

// 全局消息提示缩短为 1.5 秒（同模块单例，所有页面生效）
;['success', 'warning', 'info', 'error'].forEach((type) => {
  ElMessage[type] = (message) => ElMessage({ type, message, duration: 1500 })
})

// 启动时应用上次选择的语言（Element Plus 组件内置文案在 App.vue 的 ElConfigProvider 跟随切换）
document.documentElement.lang = i18n.global.locale.value === 'en-US' ? 'en' : 'zh-CN'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(i18n)
app.use(ElementPlus)
app.mount('#app')
