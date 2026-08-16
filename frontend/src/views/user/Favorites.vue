<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { interactionApi } from '@/api/interaction'
import { formatDate, formatPrice } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const { t } = useI18n()
const router = useRouter()
const items = ref([])
const loading = ref(true)

async function load() {
  loading.value = true
  try {
    items.value = await interactionApi.favorites()
  } finally {
    loading.value = false
  }
}

function go(it) {
  router.push(it.type === 'resource' ? `/resources/${it.targetId}` : `/market/${it.targetId}`)
}

async function remove(it) {
  await interactionApi.toggleFavorite(
    it.type === 'resource' ? { resourceId: it.targetId } : { modelId: it.targetId },
  )
  ElMessage.success(t('已取消收藏'))
  load()
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="star" :size="19" /> {{ $t('我的收藏') }}</div>

    <el-empty v-if="!loading && items.length === 0" :description="$t('还没有收藏任何内容')">
      <el-button type="primary" @click="router.push('/resources')">{{ $t('去发现') }}</el-button>
    </el-empty>

    <div class="fav-grid">
      <el-card v-for="it in items" :key="it.favoriteId" class="fav-card" shadow="hover" @click="go(it)">
        <div class="cover-wrap">
          <el-image v-if="it.cover" :src="it.cover" fit="cover" class="cover" />
          <div v-else class="cover cover-fallback" />
          <el-tag size="small" class="type-tag" :type="it.type === 'resource' ? 'success' : 'warning'" effect="dark">
            {{ it.type === 'resource' ? $t('资源') : $t('模型') }}
          </el-tag>
        </div>
        <div class="body">
          <div class="title" :title="it.title">{{ it.title }}</div>
          <div class="foot">
            <span class="text-muted">{{ formatDate(it.addedDate) }}</span>
            <span v-if="it.price != null && Number(it.price) > 0" class="price">{{ formatPrice(it.price) }}</span>
          </div>
          <el-button type="danger" size="small" plain class="remove-btn" @click.stop="remove(it)">{{ $t('取消收藏') }}</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.fav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.fav-card {
  cursor: pointer;
}
.fav-card :deep(.el-card__body) {
  padding: 0;
}
.cover-wrap {
  position: relative;
}
.cover {
  width: 100%;
  height: 130px;
  display: block;
}
.cover-fallback {
  width: 100%;
  height: 130px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
}
.type-tag {
  position: absolute;
  top: 8px;
  left: 8px;
}
.body {
  padding: 10px 12px 12px;
}
.title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}
.foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #f56c6c;
  font-weight: 600;
}
.remove-btn {
  width: 100%;
  margin-top: 10px;
}
</style>
