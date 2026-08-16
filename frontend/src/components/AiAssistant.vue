<script setup>
import { ref } from 'vue'
import AiChatPanel from '@/components/AiChatPanel.vue'
import LineIcon from '@/components/LineIcon.vue'

/**
 * 全站右下角悬浮答疑入口：点击展开抽屉式聊天面板，自然融入每个页面。
 */
const open = ref(false)
</script>

<template>
  <div class="ai-assistant">
    <el-drawer v-model="open" direction="rtl" size="400px" :with-header="false" class="ai-drawer">
      <div class="drawer-head">
        <span class="drawer-title"><LineIcon name="chat" :size="16" /> {{ $t('学习答疑') }}</span>
        <span class="drawer-sub text-muted">{{ $t('随时提问 · 随叫随到') }}</span>
      </div>
      <AiChatPanel v-if="open" compact />
    </el-drawer>

    <button class="fab" :class="{ active: open }" @click="open = !open" :title="$t('学习答疑')">
      <span class="fab-icon"><LineIcon name="chat" :size="22" /></span>
      <span class="fab-tip">{{ $t('答疑') }}</span>
    </button>
  </div>
</template>

<style scoped>
/* 抽屉整体做成毛玻璃：悬浮在页面上方，背后内容透过模糊可见 */
.ai-drawer {
  background: var(--el-bg-color);
  background: color-mix(in srgb, var(--el-bg-color) 84%, transparent);
  -webkit-backdrop-filter: blur(18px) saturate(1.4);
  backdrop-filter: blur(18px) saturate(1.4);
}
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
  /* 玻璃头：消息列表滚动时从下方透过模糊 */
  background: color-mix(in srgb, var(--el-bg-color) 55%, transparent);
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
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
