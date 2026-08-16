<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { marketApi } from '@/api/market'
import { useCartStore } from '@/stores/cart'
import ModelCard from '@/components/ModelCard.vue'
import PaginationBar from '@/components/PaginationBar.vue'
import LineIcon from '@/components/LineIcon.vue'

const route = useRoute()
const router = useRouter()
const cart = useCartStore()

const models = ref([])
const loading = ref(true)
const page = ref(1)
const size = ref(12)
const total = ref(0)
const sort = ref('newest')
const search = ref('')
const categoryId = ref(null)
const categories = ref([])

async function load() {
  loading.value = true
  try {
    const res = await marketApi.listModels({
      page: page.value,
      size: size.value,
      sort: sort.value,
      search: search.value || undefined,
      categoryId: categoryId.value ?? undefined,
    })
    models.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function doSearch() {
  page.value = 1
  load()
  router.replace({ query: { ...route.query, search: search.value || undefined } })
}

onMounted(async () => {
  categories.value = await marketApi.modelCategories()
  if (route.query.search) search.value = String(route.query.search)
  load()
})

watch(sort, () => {
  page.value = 1
  load()
})
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="cube" :size="19" /> {{ $t('模型资源库') }}</div>

    <div class="toolbar">
      <el-input v-model="search" :placeholder="$t('搜索模型名称 / 创作者')" clearable class="search" @keyup.enter="doSearch" @clear="doSearch">
        <template #append>
          <el-button @click="doSearch">{{ $t('搜索') }}</el-button>
        </template>
      </el-input>
      <el-select v-model="categoryId" :placeholder="$t('全部分类')" clearable class="cat" @change="page = 1; load()">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="sort" class="sort">
        <el-option :label="$t('最新上架')" value="newest" />
        <el-option :label="$t('价格从低到高')" value="priceAsc" />
        <el-option :label="$t('价格从高到低')" value="priceDesc" />
      </el-select>
      <el-button class="cart-btn" @click="router.push('/market/cart')"><LineIcon name="box" :size="14" /> {{ $t('购物车（{n}）', { n: cart.totalCount }) }}</el-button>
    </div>

    <div v-loading="loading" class="model-grid">
      <ModelCard v-for="m in models" :key="m.id" :model="m" />
    </div>
    <el-empty v-if="!loading && models.length === 0" :description="$t('没有找到相关模型')" />

    <PaginationBar v-if="total > 0" v-model:page="page" v-model:size="size" :total="total" @change="load" />
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.search {
  width: 320px;
}
.cat {
  width: 180px;
}
.sort {
  width: 170px;
}
.cart-btn {
  margin-left: auto;
}
.model-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  min-height: 100px;
}
</style>
