<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ResourceCard from '@/components/ResourceCard.vue'
import PaginationBar from '@/components/PaginationBar.vue'
import { resourceApi } from '@/api/resources'
import { groupCategories } from '@/utils/categories'
import http from '@/api/http'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const resources = ref([])
const total = ref(0)
const catGroups = ref([])

const query = reactive({
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : undefined,
  search: route.query.search || '',
  sort: 'newest',
  page: 1,
  size: 12,
})

const sortOptions = [
  { value: 'newest', label: '最新发布' },
  { value: 'popular', label: '最受欢迎' },
  { value: 'views', label: '最多浏览' },
]

async function load() {
  loading.value = true
  try {
    const data = await resourceApi.list({
      categoryId: query.categoryId || undefined,
      search: query.search || undefined,
      sort: query.sort,
      page: query.page,
      size: query.size,
    })
    resources.value = data.content
    total.value = data.totalElements
  } finally {
    loading.value = false
  }
}

function syncQuery() {
  router.replace({ query: { ...(query.search ? { search: query.search } : {}) } })
}

function onFilterChange() {
  query.page = 1
  load()
}

onMounted(async () => {
  catGroups.value = groupCategories(await http.get('/categories'))
  load()
})
</script>

<template>
  <div class="page-container">
    <div class="page-title">学习资源</div>

    <div class="filter-bar">
      <el-input v-model="query.search" placeholder="搜索课程、教程关键词…" clearable class="search" @keyup.enter="onFilterChange" @clear="onFilterChange">
        <template #append>
          <el-button @click="onFilterChange">🔍 搜索</el-button>
        </template>
      </el-input>
      <el-select v-model="query.categoryId" placeholder="全部分类" clearable class="cat" @change="onFilterChange">
        <el-option-group v-for="g in catGroups" :key="g.parent.id" :label="g.parent.name">
          <el-option :label="g.parent.name + '（全部）'" :value="g.parent.id" />
          <el-option v-for="child in g.children" :key="child.id" :label="child.name" :value="child.id" />
        </el-option-group>
      </el-select>
      <el-radio-group v-model="query.sort" @change="onFilterChange">
        <el-radio-button v-for="s in sortOptions" :key="s.value" :value="s.value">{{ s.label }}</el-radio-button>
      </el-radio-group>
    </div>

    <div v-loading="loading">
      <el-empty v-if="!loading && resources.length === 0" description="没有找到相关资源" />
      <div v-else class="card-grid">
        <ResourceCard v-for="r in resources" :key="r.id" :resource="r" />
      </div>
    </div>

    <PaginationBar v-if="total > 0" v-model:page="query.page" v-model:size="query.size" :total="total" @change="load" />
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.search {
  width: 320px;
}
.cat {
  width: 220px;
}
</style>
