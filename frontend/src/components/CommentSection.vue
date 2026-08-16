<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { interactionApi } from '@/api/interaction'
import { useAuthStore } from '@/stores/auth'
import { formatDate } from '@/utils/format'
import LineIcon from './LineIcon.vue'

const props = defineProps({
  resourceId: { type: Number, default: null },
  modelId: { type: Number, default: null },
})

const auth = useAuthStore()
const router = useRouter()
const { t } = useI18n()
const comments = ref([])
const loading = ref(false)
const replyTo = ref(null) // 回复目标 { id, username }
const content = ref('')
const submitting = ref(false)

async function load() {
  loading.value = true
  try {
    comments.value = await interactionApi.comments({
      resourceId: props.resourceId ?? undefined,
      modelId: props.modelId ?? undefined,
    })
  } finally {
    loading.value = false
  }
}

async function submit() {
  if (!content.value.trim()) return
  submitting.value = true
  try {
    await interactionApi.addComment({
      resourceId: props.resourceId ?? undefined,
      modelId: props.modelId ?? undefined,
      parentCommentId: replyTo.value?.id,
      content: content.value.trim(),
    })
    ElMessage.success(t('评论成功'))
    content.value = ''
    replyTo.value = null
    load()
  } finally {
    submitting.value = false
  }
}

function startReply(c) {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }
  replyTo.value = c
}
</script>

<template>
  <div class="comment-section" v-loading="loading">
    <div class="comment-count"><LineIcon name="chat" :size="15" /> {{ $t('评论（{n}）', { n: comments.length }) }}</div>

    <!-- 输入区 -->
    <div class="input-area">
      <el-input
        v-model="content"
        type="textarea"
        :rows="3"
        :placeholder="replyTo ? t('回复 @{name}：', { name: replyTo.username }) : t('友善发言，分享你的学习心得…')"
        maxlength="500"
        show-word-limit
      />
      <div class="input-actions">
        <el-button v-if="replyTo" size="small" @click="replyTo = null">{{ $t('取消回复') }}</el-button>
        <el-button type="primary" size="small" :loading="submitting" @click="submit">{{ $t('发表评论') }}</el-button>
      </div>
    </div>

    <!-- 评论树 -->
    <div v-for="c in comments" :key="c.id" class="comment">
      <div class="comment-head">
        <span class="avatar">{{ (c.username || '?').slice(0, 1).toUpperCase() }}</span>
        <span class="username">{{ c.username }}</span>
        <span class="text-muted">{{ formatDate(c.commentDate) }}</span>
      </div>
      <div class="comment-body">{{ c.content }}</div>
      <div class="comment-actions">
        <el-link type="primary" :underline="false" @click="startReply(c)">{{ $t('回复') }}</el-link>
      </div>
      <div v-for="r in c.replies || []" :key="r.id" class="comment reply">
        <div class="comment-head">
          <span class="avatar small">{{ (r.username || '?').slice(0, 1).toUpperCase() }}</span>
          <span class="username">{{ r.username }}</span>
          <span class="text-muted">{{ formatDate(r.commentDate) }}</span>
        </div>
        <div class="comment-body">{{ r.content }}</div>
      </div>
    </div>

    <el-empty v-if="!loading && comments.length === 0" :description="$t('还没有评论，来抢沙发～')" :image-size="60" />
  </div>
</template>

<style scoped>
.comment-section {
  margin-top: 16px;
}
.comment-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  margin-bottom: 12px;
}
.input-area {
  margin-bottom: 20px;
}
.input-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.comment {
  padding: 12px 0;
  border-bottom: 1px dashed var(--border-color);
}
.comment-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--theme-color);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
}
.avatar.small {
  width: 20px;
  height: 20px;
  font-size: 11px;
}
.username {
  font-weight: 600;
  font-size: 14px;
}
.comment-body {
  font-size: 14px;
  line-height: 1.6;
  margin-left: 34px;
}
.comment-actions {
  margin-left: 34px;
  margin-top: 4px;
}
.comment.reply {
  margin-left: 48px;
  border-bottom: none;
  padding: 8px 0;
  background: var(--el-fill-color-light);
  border-radius: 8px;
  padding: 10px 12px;
  margin-top: 8px;
}
</style>
