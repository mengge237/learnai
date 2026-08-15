<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marketApi } from '@/api/market'
import { interactionApi } from '@/api/interaction'
import { downloadFile } from '@/api/http'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { LICENSE_TYPES, formatDate, formatPrice } from '@/utils/format'
import CommentSection from '@/components/CommentSection.vue'
import ModelViewer from '@/components/ModelViewer.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()

const id = Number(route.params.id)
const model = ref(null)
const loading = ref(true)
const quantity = ref(1)
const licenseType = ref('个人')
const favorited = ref(false)

/** 支持在线预览的格式：glb / gltf / obj，其余提示下载 */
const viewerFormat = computed(() => {
  const name = (model.value?.originalFileName || model.value?.filePath || '').toLowerCase()
  if (name.endsWith('.glb')) return 'glb'
  if (name.endsWith('.gltf')) return 'gltf'
  if (name.endsWith('.obj')) return 'obj'
  return ''
})
const viewerSrc = computed(() =>
  viewerFormat.value && model.value?.filePath ? `/uploads/${model.value.filePath}` : '',
)

async function load() {
  loading.value = true
  try {
    model.value = await marketApi.modelDetail(id)
    await loadFavorite()
  } finally {
    loading.value = false
  }
}

async function loadFavorite() {
  if (!auth.isLoggedIn) return
  try {
    const list = await interactionApi.favorites()
    favorited.value = list.some((f) => f.type === 'model' && Number(f.targetId) === id)
  } catch {
    /* 忽略 */
  }
}

async function toggleFavorite() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  const { favorited: f } = await interactionApi.toggleFavorite({ modelId: id })
  favorited.value = f
  ElMessage.success(f ? '已加入收藏' : '已取消收藏')
}

function addToCart() {
  cart.add(model.value, quantity.value, licenseType.value)
  ElMessage.success('已加入购物车')
}

function buyNow() {
  addToCart()
  router.push('/cart')
}

function download() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  downloadFile(marketApi.downloadUrl(id), model.value.originalFileName || 'model')
}

onMounted(load)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-breadcrumb separator="/" class="back">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/market' }">模型资源库</el-breadcrumb-item>
      <el-breadcrumb-item v-if="model">{{ model.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <template v-if="model">
      <el-card class="detail-card">
        <div class="detail-layout">
          <div class="cover-box">
            <ModelViewer v-if="viewerSrc" :src="viewerSrc" :format="viewerFormat" class="viewer" />
            <template v-else>
              <el-image v-if="model.previewUrl" :src="model.previewUrl" fit="cover" class="cover" />
              <div v-else class="cover cover-fallback" />
            </template>
          </div>
          <div class="info">
            <h2 class="title">
              {{ model.name }}
              <el-tag v-if="Number(model.price) === 0" type="success">免费</el-tag>
              <el-tag v-else type="danger">{{ formatPrice(model.price) }}</el-tag>
              <el-tag v-if="model.isApproved === false" type="warning">待审核</el-tag>
            </h2>
            <div class="meta-grid">
              <div><span class="text-muted">创作者：</span>{{ model.creator || '未知' }}</div>
              <div><span class="text-muted">分类：</span>{{ model.categoryName }}</div>
              <div><span class="text-muted">编号：</span>{{ model.modelCode }}</div>
              <div><span class="text-muted">上架时间：</span>{{ formatDate(model.createDate) }}</div>
            </div>
            <div class="license-row">
              <span class="text-muted">授权类型：</span>
              <el-select v-model="licenseType" style="width: 140px">
                <el-option v-for="t in LICENSE_TYPES" :key="t" :label="t" :value="t" />
              </el-select>
              <span class="text-muted">数量：</span>
              <el-input-number v-model="quantity" :min="1" :max="99" />
            </div>
            <div class="actions">
              <el-button type="primary" size="large" :disabled="model.isApproved === false" @click="addToCart">🛒 加入购物车</el-button>
              <el-button type="danger" size="large" plain :disabled="model.isApproved === false" @click="buyNow">立即购买</el-button>
              <el-button size="large" @click="toggleFavorite">{{ favorited ? '⭐ 已收藏' : '☆ 收藏' }}</el-button>
              <el-button size="large" @click="download">⬇️ 下载模型文件</el-button>
            </div>
            <div v-if="!viewerSrc" class="viewer-tip text-muted">💡 该格式暂不支持在线预览，可下载后用本地软件（Blender / 3ds Max 等）打开</div>
          </div>
        </div>
      </el-card>

      <el-card class="comments-card">
        <CommentSection :model-id="id" />
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.back {
  margin-bottom: 16px;
}
.detail-layout {
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: 24px;
}
.cover-box {
  border-radius: 8px;
  overflow: hidden;
}
.viewer {
  height: 300px;
}
.cover {
  width: 100%;
  height: 300px;
}
.cover-fallback {
  width: 100%;
  height: 300px;
  background: linear-gradient(135deg, hsl(220 55% 60%), hsl(260 55% 40%));
}
.title {
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  font-size: 14px;
  margin-bottom: 16px;
}
.license-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.viewer-tip {
  margin-top: 14px;
}
.comments-card {
  margin-top: 16px;
}
@media (max-width: 860px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
}
</style>
