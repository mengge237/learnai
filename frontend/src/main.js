import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus, { ElMessage } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import App from './App.vue'
import router from './router'
import './style.css'

// 全局消息提示缩短为 1.5 秒（同模块单例，所有页面生效）
;['success', 'warning', 'info', 'error'].forEach((type) => {
  ElMessage[type] = (message) => ElMessage({ type, message, duration: 1500 })
})

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')
