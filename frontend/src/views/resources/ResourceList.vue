<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import ResourceCard from '@/components/ResourceCard.vue'
import PaginationBar from '@/components/PaginationBar.vue'
import LineIcon from '@/components/LineIcon.vue'
import { resourceApi } from '@/api/resources'
import http from '@/api/http'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const resources = ref([])
const total = ref(0)
const catTree = ref([])

const query = reactive({
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : undefined,
  search: route.query.search || '',
  sort: 'newest',
  page: 1,
  size: 12,
})

const sortOptions = computed(() => [
  { value: 'newest', label: t('最新发布') },
  { value: 'popular', label: t('最受欢迎') },
  { value: 'views', label: t('最多浏览') },
])

/** 全部公开课程数 = 各根分类（含子分类）资源数之和 */
const totalCourses = computed(() => catTree.value.reduce((sum, c) => sum + (c.resourceCount || 0), 0))

/** 当前选中分类名称（用于主区标题） */
const activeCategoryName = computed(() => {
  for (const root of catTree.value) {
    if (root.id === query.categoryId) return root.name
    const child = (root.children || []).find((c) => c.id === query.categoryId)
    if (child) return `${root.name} / ${child.name}`
  }
  return ''
})

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
  router.replace({
    query: {
      ...(query.categoryId ? { categoryId: query.categoryId } : {}),
      ...(query.search ? { search: query.search } : {}),
    },
  })
}

function onFilterChange() {
  query.page = 1
  syncQuery()
  load()
}

/** 点击分类：再次点击已选中分类或点「全部课程」= 清除筛选 */
function pickCategory(id) {
  if (id === null || id === undefined || query.categoryId === id) {
    query.categoryId = undefined
  } else {
    query.categoryId = id
  }
  onFilterChange()
}

function onSideSearch() {
  query.categoryId = undefined
  onFilterChange()
}

onMounted(async () => {
  catTree.value = await http.get('/categories')
  load()
})
</script>

<template>
  <div class="page-container wide">
    <div class="page-title"><LineIcon name="book" :size="19" /> {{ $t('学习资源') }}</div>

    <div class="res-layout">
      <!-- ============ 侧边栏：搜索 + 分类树 ============ -->
      <aside class="res-sidebar">
        <div class="side-search">
          <el-input
            v-model="query.search"
            :placeholder="$t('搜索课程、教程关键词…')"
            clearable
            @keyup.enter="onSideSearch"
            @clear="onSideSearch"
          >
            <template #prefix><LineIcon name="search" :size="14" /></template>
          </el-input>
          <el-button class="side-search-btn" @click="onSideSearch">{{ $t('搜索') }}</el-button>
        </div>

        <nav class="cat-nav">
          <div class="cat-nav-title">{{ $t('课程分类') }}</div>

          <button class="cat-row all" :class="{ active: !query.categoryId }" @click="pickCategory(null)">
            <span class="cat-name">{{ $t('全部课程') }}</span>
            <span class="cat-count">{{ totalCourses }}</span>
          </button>

          <div v-for="root in catTree" :key="root.id" class="cat-group">
            <button class="cat-row root" :class="{ active: query.categoryId === root.id }" @click="pickCategory(root.id)">
              <span class="cat-name">{{ root.name }}</span>
              <span class="cat-count">{{ root.resourceCount || 0 }}</span>
            </button>
            <div v-if="root.children?.length" class="cat-children">
              <button
                v-for="child in root.children"
                :key="child.id"
                class="cat-row child"
                :class="{ active: query.categoryId === child.id }"
                @click="pickCategory(child.id)"
              >
                <span class="cat-name">{{ child.name }}</span>
                <span class="cat-count">{{ child.resourceCount || 0 }}</span>
              </button>
            </div>
          </div>
        </nav>

        <div class="side-note text-muted">
          {{ $t('分类后数字为该分类下') }}<br />{{ $t('全部公开课程数量') }}
        </div>
      </aside>

      <!-- ============ 主区：列表 ============ -->
      <main class="res-main">
        <div class="main-bar">
          <div class="main-title">
            <template v-if="query.search">{{ $t('搜索「{q}」', { q: query.search }) }}</template>
            <template v-else>{{ activeCategoryName || $t('全部课程') }}</template>
            <span class="main-total text-muted">{{ $t('共 {n} 门课程', { n: total }) }}</span>
          </div>
          <el-radio-group v-model="query.sort" @change="onFilterChange">
            <el-radio-button v-for="s in sortOptions" :key="s.value" :value="s.value">{{ s.label }}</el-radio-button>
          </el-radio-group>
        </div>

        <div v-loading="loading" class="main-list">
          <el-empty v-if="!loading && resources.length === 0" :description="$t('没有找到相关资源')" />
          <div v-else class="card-grid">
            <ResourceCard v-for="r in resources" :key="r.id" :resource="r" />
          </div>
        </div>

        <PaginationBar v-if="total > 0" v-model:page="query.page" v-model:size="query.size" :total="total" @change="load" />
      </main>
    </div>
  </div>
</template>

<style scoped>
.wide {
  max-width: 1280px;
}
.res-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
  align-items: start;
}

/* ---------- 侧边栏 ---------- */
.res-sidebar {
  position: sticky;
  top: 84px;
  border: 1px solid var(--border-color);
  border-top: 3px solid var(--line-color);
  border-radius: 2px;
  background: var(--el-bg-color);
  padding: 16px 14px;
}
.side-search {
  display: flex;
  gap: 8px;
  margin-bottom: 18px;
}
.side-search .el-input {
  flex: 1;
  min-width: 0;
}
.side-search-btn {
  flex-shrink: 0;
}
.cat-nav-title {
  font-size: 12px;
  letter-spacing: 3px;
  color: var(--el-text-color-secondary);
  margin-bottom: 10px;
  padding-left: 8px;
  border-left: 4px solid var(--theme-color);
  line-height: 1.2;
}
.cat-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  width: 100%;
  border: none;
  background: none;
  font-family: inherit;
  font-size: 13px;
  color: var(--el-text-color-primary);
  padding: 8px 10px;
  margin-bottom: 2px;
  cursor: pointer;
  border-left: 2px solid transparent;
  border-radius: 0;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
}
.cat-row:hover {
  background: var(--el-fill-color-light);
  color: var(--theme-color);
}
.cat-row.active {
  background: var(--el-fill-color-light);
  border-left-color: var(--theme-color);
  color: var(--theme-color);
  font-weight: 600;
}
.cat-row.root {
  font-weight: 600;
  letter-spacing: 1px;
  margin-top: 4px;
}
.cat-row.child {
  padding-left: 26px;
  font-weight: 400;
}
.cat-count {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 11px;
  color: var(--el-text-color-secondary);
  border: 1px solid var(--border-color);
  border-radius: 2px;
  padding: 0 5px;
  line-height: 16px;
}
.cat-row.active .cat-count {
  color: var(--theme-color);
  border-color: var(--theme-color);
}
.side-note {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
  font-size: 12px;
  line-height: 1.7;
}

/* ---------- 主区 ---------- */
.main-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.main-title {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
}
.main-total {
  font-size: 12px;
  font-weight: 400;
  margin-left: 10px;
  letter-spacing: 0;
}
.main-list {
  min-height: 300px;
}
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
}

@media (max-width: 960px) {
  .res-layout {
    grid-template-columns: 1fr;
  }
  .res-sidebar {
    position: static;
  }
}
</style>
