<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { aiApi } from '@/api/ai'
import { resourceApi } from '@/api/resources'
import { formatDate } from '@/utils/format'

/**
 * AI 聊天面板：整页（AiChat 页）与悬浮助手抽屉共用。
 * compact=true 时隐藏"关联学习资源"工具栏，高度自适应抽屉。
 */
const props = defineProps({
  compact: { type: Boolean, default: false },
})

const messages = ref([])
const input = ref('')
const sending = ref(false)
const loading = ref(false)
const listRef = ref()
const learningResources = ref([])
const contextResourceId = ref(null)

const quickQuestions = ['你好', '给我推荐一些学习资源', '我的学习进度怎么样', '如何学习 Blender？', '谢谢']

async function loadHistory() {
  loading.value = true
  try {
    messages.value = await aiApi.history()
    scrollToBottom()
  } finally {
    loading.value = false
  }
}

async function scrollToBottom() {
  await nextTick()
  listRef.value?.scrollTo?.({ top: listRef.value.scrollHeight, behavior: 'smooth' })
}

async function send(text) {
  const content = (text ?? input.value).trim()
  if (!content || sending.value) return
  input.value = ''
  sending.value = true
  // 先显示用户消息（乐观渲染）
  messages.value.push({ userMessage: content, aiMessage: '…', interactionTime: null })
  scrollToBottom()
  try {
    const res = await aiApi.chat({
      message: content,
      resourceId: contextResourceId.value ?? undefined,
    })
    // 用服务端返回替换最后一条
    const idx = messages.value.length - 1
    messages.value[idx] = res
    scrollToBottom()
  } catch {
    messages.value.pop()
    ElMessage.error('服务暂时开小差了，请稍后再试')
  } finally {
    sending.value = false
  }
}

onMounted(async () => {
  loadHistory()
  try {
    learningResources.value = await resourceApi.myLearning()
  } catch {
    /* 未登录或暂无学习记录 */
  }
})

defineExpose({ loadHistory })
</script>

<template>
  <div class="chat-panel" :class="{ compact: props.compact }">
    <div ref="listRef" v-loading="loading" class="msg-list">
      <el-empty
        v-if="!loading && messages.length === 0"
        description="我是你的学习助手，试试问我学习问题吧～"
        :image-size="80"
      />
      <div v-for="(m, i) in messages" :key="i" class="msg" :class="m.userMessage ? 'from-user' : 'from-ai'">
        <div class="avatar">{{ m.userMessage ? '我' : '助手' }}</div>
        <div class="bubble">
          <div class="text">{{ m.aiMessage }}</div>
          <div class="time text-muted">{{ m.interactionTime ? formatDate(m.interactionTime) : '…' }}</div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <div v-if="!props.compact" class="toolbar">
        <el-select v-model="contextResourceId" placeholder="关联学习资源（可选）" clearable size="small" style="width: 260px">
          <el-option v-for="r in learningResources" :key="r.resourceId" :label="r.title" :value="r.resourceId" />
        </el-select>
        <span class="text-muted">选择正在学习的资源，回答更有针对性</span>
      </div>
      <div class="quick">
        <el-tag v-for="q in quickQuestions" :key="q" class="quick-tag" @click="send(q)">{{ q }}</el-tag>
      </div>
      <div class="send-row">
        <el-input
          v-model="input"
          type="textarea"
          :rows="props.compact ? 2 : 2"
          placeholder="输入你的问题…（Enter 发送，Shift+Enter 换行）"
          maxlength="500"
          @keydown.enter.exact.prevent="send()"
        />
        <el-button type="primary" :loading="sending" @click="send()">发送</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-panel {
  display: flex;
  flex-direction: column;
}
.chat-panel.compact {
  height: 100%;
}
.msg-list {
  height: 420px;
  overflow-y: auto;
  padding: 20px;
}
.compact .msg-list {
  height: auto;
  flex: 1;
  padding: 14px;
}
.msg {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
}
.msg.from-user {
  flex-direction: row-reverse;
}
.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  flex-shrink: 0;
}
.from-user .avatar {
  background: #67c23a;
}
.from-ai .avatar {
  background: var(--theme-color);
}
.bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 10px;
  background: var(--el-fill-color-light);
}
.from-user .bubble {
  background: var(--el-color-primary-light-9);
}
.text {
  white-space: pre-wrap;
  line-height: 1.6;
  font-size: 14px;
}
.time {
  font-size: 12px;
  margin-top: 4px;
  text-align: right;
}
.input-area {
  border-top: 1px solid var(--border-color);
  padding: 12px 16px 16px;
}
.compact .input-area {
  padding: 10px 12px;
}
.toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 8px;
}
.quick {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
.quick-tag {
  cursor: pointer;
}
.send-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}
.send-row .el-textarea {
  flex: 1;
}
</style>
