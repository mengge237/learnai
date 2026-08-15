<script setup>
import { computed } from 'vue'

/**
 * 服务端分页条：双向绑定 page/size，变化时触发 change 事件。
 */
const props = defineProps({
  page: { type: Number, default: 1 },
  size: { type: Number, default: 12 },
  total: { type: Number, default: 0 },
  sizes: { type: Array, default: () => [12, 24, 48] },
})
const emit = defineEmits(['update:page', 'update:size', 'change'])

const currentPage = computed({
  get: () => props.page,
  set: (v) => emit('update:page', v),
})
const pageSize = computed({
  get: () => props.size,
  set: (v) => emit('update:size', v),
})

function onChange() {
  emit('change', { page: props.page, size: props.size })
}
</script>

<template>
  <div class="pagination-bar">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="sizes"
      background
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="onChange"
      @size-change="onChange"
    />
  </div>
</template>

<style scoped>
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
