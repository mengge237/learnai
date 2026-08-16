<script setup>
import AiChatPanel from '@/components/AiChatPanel.vue'
import LineIcon from '@/components/LineIcon.vue'
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="chat" :size="19" /> {{ $t('在线答疑') }}</div>
    <el-card class="chat-card" body-style="padding: 0">
      <AiChatPanel />
    </el-card>
  </div>
</template>

<style scoped>
.page-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.chat-card {
  max-width: 900px;
}
</style>
