<script setup>
import { onMounted } from 'vue'
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'
import AiAssistant from '@/components/AiAssistant.vue'
import { usePrefsStore } from '@/stores/prefs'
import { useAuthStore } from '@/stores/auth'

const prefs = usePrefsStore()
const auth = useAuthStore()

onMounted(() => {
  // 启动时应用本地主题；若已登录则用服务器偏好覆盖并刷新用户信息
  prefs.apply()
  if (auth.isLoggedIn) {
    auth.fetchMe().then((user) => prefs.syncFromServer(user)).catch(() => {})
  }
})
</script>

<template>
  <NavBar />
  <router-view />
  <Footer />
  <AiAssistant />
</template>
