<script setup>
import AiChatPanel from '@/components/AiChatPanel.vue'
</script>

<template>
  <div class="page-container">
    <div class="page-title">🤖 AI 智能学习助手</div>
    <el-card class="chat-card" body-style="padding: 0">
      <AiChatPanel />
    </el-card>
  </div>
</template>

<style scoped>
.chat-card {
  max-width: 900px;
}
</style>
