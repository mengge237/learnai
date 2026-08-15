<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { resourceApi } from '@/api/resources'
import { interactionApi } from '@/api/interaction'
import { downloadFile } from '@/api/http'
import { useAuthStore } from '@/stores/auth'
import { formatCount, formatDate, formatPrice } from '@/utils/format'
import CommentSection from '@/components/CommentSection.vue'
import LineIcon from '@/components/LineIcon.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const resource = ref(null)
const loading = ref(true)
const favorited = ref(false)
const likeBusy = ref(false)

const id = Number(route.params.id)

async function load() {
  loading.value = true
  try {
    resource.value = await resourceApi.detail(id)
  } finally {
    loading.value = false
  }
}

async function toggleFavorite() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  const { favorited: f } = await interactionApi.toggleFavorite({ resourceId: id })
  favorited.value = f
  ElMessage.success(f ? '已加入收藏' : '已取消收藏')
}

async function like() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (likeBusy.value) return
  likeBusy.value = true
  try {
    await resourceApi.like(id)
    resource.value.likeCount = (resource.value.likeCount || 0) + 1
    ElMessage.success('点赞成功！')
  } finally {
    likeBusy.value = false
  }
}

function startLearn() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  router.push(`/resources/${id}/learn`)
}

function download() {
  if (!auth.isLoggedIn) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  downloadFile(resourceApi.downloadUrl(id), resource.value.originalFileName || 'resource')
}

onMounted(load)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-breadcrumb separator="/" class="back">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/resources' }">学习资源</el-breadcrumb-item>
      <el-breadcrumb-item v-if="resource">{{ resource.title }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-if="resource" class="detail-card">
      <div class="detail-layout">
        <div class="cover-box">
          <el-image v-if="resource.previewUrl" :src="resource.previewUrl" fit="cover" class="cover" />
          <div v-else class="cover cover-fallback" />
        </div>
        <div class="info">
          <h2 class="title">
            {{ resource.title }}
            <el-tag v-if="resource.isFree" type="success">免费</el-tag>
            <el-tag v-else type="danger">{{ formatPrice(resource.price) }}</el-tag>
            <el-tag v-if="resource.isApproved === false" type="warning">待审核</el-tag>
          </h2>
          <p class="desc">{{ resource.description }}</p>
          <div class="meta-grid">
            <div><span class="text-muted">作者：</span>{{ resource.author || '未知' }}</div>
            <div><span class="text-muted">分类：</span>{{ resource.categoryName }}</div>
            <div><span class="text-muted">难度：</span>{{ resource.difficultyLevel }}</div>
            <div><span class="text-muted">时长：</span>{{ resource.durationMinutes }} 分钟</div>
            <div><span class="text-muted">类型：</span>{{ resource.learningType }}</div>
            <div><span class="text-muted">发布时间：</span>{{ formatDate(resource.createDate) }}</div>
          </div>
          <div class="stats">
            <span>{{ formatCount(resource.viewCount) }} 浏览</span>
            <span>{{ formatCount(resource.likeCount) }} 点赞</span>
            <span><LineIcon name="user" :size="14" /> {{ formatCount(resource.completionCount) }} 人学完</span>
          </div>
          <div class="actions">
            <el-button type="primary" size="large" @click="startLearn"><LineIcon name="book" :size="14" /> 开始学习</el-button>
            <el-button size="large" :loading="likeBusy" @click="like">点赞</el-button>
            <el-button size="large" @click="toggleFavorite">{{ favorited ? '已收藏' : '收藏' }}</el-button>
            <el-button size="large" @click="download"><LineIcon name="download" :size="14" /> 下载资料</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-card v-if="resource" class="comments-card">
      <CommentSection :resource-id="id" />
    </el-card>
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
.cover {
  width: 100%;
  height: 260px;
}
.cover-fallback {
  width: 100%;
  height: 260px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
}
.title {
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.desc {
  color: var(--el-text-color-regular);
  line-height: 1.7;
  margin-bottom: 16px;
}
.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  font-size: 14px;
  margin-bottom: 12px;
}
.stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-bottom: 20px;
}
.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
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
