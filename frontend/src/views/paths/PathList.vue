<script setup>
import { onMounted, ref } from 'vue'
import { pathApi } from '@/api/paths'
import PathCard from '@/components/PathCard.vue'
import PaginationBar from '@/components/PaginationBar.vue'

const paths = ref([])
const loading = ref(true)
const page = ref(1)
const size = ref(6)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await pathApi.list({ page: page.value, size: size.value })
    paths.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="banner">
      <h2>🗺️ 学习路径</h2>
      <p>系统化的进阶路线，从入门到精通，一步一步带你成为 3D 建模高手。</p>
    </div>

    <div v-loading="loading" class="path-grid">
      <PathCard v-for="p in paths" :key="p.id" :path="p" />
    </div>
    <el-empty v-if="!loading && paths.length === 0" description="暂无学习路径" />

    <PaginationBar v-if="total > 0" v-model:page="page" v-model:size="size" :total="total" :sizes="[6, 12, 24]" @change="load" />
  </div>
</template>

<style scoped>
.banner {
  background: linear-gradient(135deg, #409eff, #6d8df0);
  color: #fff;
  border-radius: 10px;
  padding: 28px 32px;
  margin-bottom: 20px;
}
.banner h2 {
  margin: 0 0 8px;
}
.banner p {
  margin: 0;
  opacity: 0.9;
}
.path-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  min-height: 100px;
}
</style>
