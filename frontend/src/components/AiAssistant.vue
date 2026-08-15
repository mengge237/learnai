<script setup>
import { ref } from 'vue'
import AiChatPanel from '@/components/AiChatPanel.vue'

/**
 * 全站右下角悬浮 AI 助手：点击展开抽屉式聊天面板，自然融入每个页面。
 */
const open = ref(false)
</script>

<template>
  <div class="ai-assistant">
    <el-drawer v-model="open" direction="rtl" size="400px" :with-header="false" class="ai-drawer">
      <div class="drawer-head">
        <span class="drawer-title">🤖 AI 智能学习助手</span>
        <span class="drawer-sub text-muted">随时提问 · 学习答疑</span>
      </div>
      <AiChatPanel v-if="open" compact />
    </el-drawer>

    <button class="fab" :class="{ active: open }" @click="open = !open" title="AI 学习助手">
      <span class="fab-icon">🤖</span>
      <span class="fab-tip">AI 助手</span>
    </button>
  </div>
</template>

<style scoped>
.ai-drawer :deep(.el-drawer__body) {
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.drawer-head {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-color);
}
.drawer-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  padding-left: 10px;
  border-left: 4px solid var(--theme-color);
}
.fab {
  position: fixed;
  right: 24px;
  bottom: 88px;
  z-index: 99;
  width: 56px;
  height: 56px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0;
  border: none;
  border-radius: 4px;
  border-bottom: 3px solid var(--theme-color);
  background: var(--el-color-primary);
  color: var(--el-bg-color);
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
  transition: transform 0.2s;
}
.fab:hover {
  transform: translateY(-2px);
}
.fab.active {
  background: var(--theme-color);
  border-bottom-color: var(--el-color-primary);
}
.fab-icon {
  font-size: 22px;
  line-height: 1;
}
.fab-tip {
  font-size: 10px;
  margin-top: 3px;
  letter-spacing: 1px;
}
@media (max-width: 640px) {
  .fab {
    right: 12px;
    bottom: 76px;
    width: 48px;
    height: 48px;
  }
  .fab-icon {
    font-size: 18px;
  }
  .fab-tip {
    font-size: 9px;
  }
}
</style>
