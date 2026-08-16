<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<template>
  <div class="page-container">
    <el-empty :description="$t('「{name}」正在开发中，敬请期待', { name: $t(route.meta.title || '该模块') })" />
  </div>
</template>
