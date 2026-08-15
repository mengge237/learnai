<script setup>
import LineIcon from '@/components/LineIcon.vue'

/**
 * 统一面包屑（工业线稿风）：序号 + 路径层级，
 * 替代各处零散的 el-breadcrumb / el-page-header。
 * items: [{ label: string, to?: string }]，最后一项为当前页（不可点击）。
 */
defineProps({
  items: { type: Array, required: true },
})
</script>

<template>
  <nav class="page-breadcrumb" aria-label="面包屑">
    <template v-for="(it, i) in items" :key="i">
      <span v-if="i > 0" class="bc-sep"><LineIcon name="arrowRight" :size="11" /></span>
      <router-link v-if="it.to && i < items.length - 1" :to="it.to" class="bc-item">
        <span class="bc-no">{{ String(i + 1).padStart(2, '0') }}</span>
        <span class="bc-label">{{ it.label }}</span>
      </router-link>
      <span v-else class="bc-item current">
        <span class="bc-no">{{ String(i + 1).padStart(2, '0') }}</span>
        <span class="bc-label">{{ it.label }}</span>
      </span>
    </template>
  </nav>
</template>

<style scoped>
.page-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  font-size: 12px;
  letter-spacing: 1px;
}
.bc-sep {
  display: inline-flex;
  color: var(--line-soft);
}
.bc-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  color: var(--el-text-color-secondary);
  text-decoration: none;
  transition: color 0.15s;
}
a.bc-item:hover {
  color: var(--theme-color);
}
.bc-no {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 10px;
  letter-spacing: 0;
  color: var(--el-text-color-secondary);
  border: 1px solid var(--border-color);
  border-radius: 2px;
  padding: 0 4px;
  line-height: 15px;
  flex-shrink: 0;
}
a.bc-item:hover .bc-no {
  color: var(--theme-color);
  border-color: var(--theme-color);
}
.bc-item.current {
  color: var(--el-text-color-primary);
  font-weight: 600;
}
.bc-item.current .bc-no {
  color: var(--theme-color);
  border-color: var(--theme-color);
}
.bc-item.current .bc-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 420px;
}
</style>
