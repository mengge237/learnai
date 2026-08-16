<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="page-container">
    <el-result icon="warning" title="404" :sub-title="$t('页面不存在或已被移除')">
      <template #extra>
        <el-button type="primary" @click="router.push('/')">{{ $t('返回首页') }}</el-button>
      </template>
    </el-result>
  </div>
</template>
