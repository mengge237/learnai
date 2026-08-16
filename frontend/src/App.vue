<script setup>
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'
import AiAssistant from '@/components/AiAssistant.vue'
import CustomCursor from '@/components/CustomCursor.vue'
import { usePrefsStore } from '@/stores/prefs'
import { useAuthStore } from '@/stores/auth'

const prefs = usePrefsStore()
const auth = useAuthStore()
const { locale } = useI18n()

// Element Plus 组件内置文案（分页/日期/空状态等）随语言切换
const epLocale = computed(() => (locale.value === 'en-US' ? en : zhCn))

onMounted(() => {
  // 启动时应用本地主题；若已登录则用服务器偏好覆盖并刷新用户信息
  prefs.apply()
  if (auth.isLoggedIn) {
    auth.fetchMe().then((user) => prefs.syncFromServer(user)).catch(() => {})
  }
})
</script>

<template>
  <el-config-provider :locale="epLocale">
    <NavBar />
    <router-view />
    <Footer />
    <AiAssistant />
    <CustomCursor />
  </el-config-provider>
</template>
