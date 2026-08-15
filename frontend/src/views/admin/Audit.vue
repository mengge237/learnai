<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'
import { useAuthStore } from '@/stores/auth'
import { formatDate, formatPrice } from '@/utils/format'

const auth = useAuthStore()
const router = useRouter()

const resources = ref([])
const models = ref([])
const loading = ref(true)

async function load() {
  loading.value = true
  try {
    if (auth.isAdmin || auth.isAuditor) {
      ;[resources.value, models.value] = await Promise.all([adminApi.pendingResources(), adminApi.pendingModels()])
    }
  } finally {
    loading.value = false
  }
}

async function approveResource(r) {
  await adminApi.reviewResource(r.id, { approved: true })
  ElMessage.success('已通过')
  load()
}

async function rejectResource(r) {
  const { value } = await ElMessageBox.prompt('请填写驳回原因', '驳回资源', {
    inputPattern: /\S+/,
    inputErrorMessage: '驳回原因不能为空',
  })
  await adminApi.reviewResource(r.id, { approved: false, reason: value })
  ElMessage.success('已驳回')
  load()
}

async function approveModel(m) {
  await adminApi.reviewModel(m.id, { approved: true })
  ElMessage.success('已通过')
  load()
}

async function rejectModel(m) {
  const { value } = await ElMessageBox.prompt('请填写驳回原因', '驳回模型', {
    inputPattern: /\S+/,
    inputErrorMessage: '驳回原因不能为空',
  })
  await adminApi.reviewModel(m.id, { approved: false, reason: value })
  ElMessage.success('已驳回')
  load()
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="page-title">🛡 内容审核</div>

    <el-tabs v-loading="loading">
      <el-tab-pane :label="`待审核资源（${resources.length}）`">
        <el-empty v-if="resources.length === 0" description="太棒了，没有待审核的资源" />
        <el-card v-for="r in resources" :key="r.id" class="audit-card">
          <div class="row">
            <el-image v-if="r.previewUrl" :src="r.previewUrl" fit="cover" class="thumb" />
            <div v-else class="thumb thumb-fallback" />
            <div class="info">
              <div class="title">
                {{ r.title }}
                <el-tag size="small" effect="plain">{{ r.resourceCode }}</el-tag>
                <el-tag v-if="r.isFree" size="small" type="success" effect="plain">免费</el-tag>
                <el-tag v-else size="small" type="danger" effect="plain">{{ formatPrice(r.price) }}</el-tag>
              </div>
              <div class="text-muted">
                {{ r.author || '未知作者' }} · {{ r.categoryName }} · {{ r.difficultyLevel }} · 提交于 {{ formatDate(r.createDate) }}
              </div>
              <div class="desc text-muted">{{ r.description }}</div>
              <div class="actions">
                <el-button size="small" type="success" @click="approveResource(r)">✅ 通过</el-button>
                <el-button size="small" type="danger" plain @click="rejectResource(r)">❌ 驳回</el-button>
                <el-button size="small" @click="router.push(`/resources/${r.id}`)">查看详情</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane :label="`待审核模型（${models.length}）`">
        <el-empty v-if="models.length === 0" description="太棒了，没有待审核的模型" />
        <el-card v-for="m in models" :key="m.id" class="audit-card">
          <div class="row">
            <el-image v-if="m.previewUrl" :src="m.previewUrl" fit="cover" class="thumb" />
            <div v-else class="thumb thumb-fallback" />
            <div class="info">
              <div class="title">
                {{ m.name }}
                <el-tag size="small" effect="plain">{{ m.modelCode }}</el-tag>
                <el-tag size="small" type="danger" effect="plain">{{ formatPrice(m.price) }}</el-tag>
              </div>
              <div class="text-muted">
                {{ m.creator || '未知创作者' }} · {{ m.categoryName }} · 提交于 {{ formatDate(m.createDate) }}
              </div>
              <div class="actions">
                <el-button size="small" type="success" @click="approveModel(m)">✅ 通过</el-button>
                <el-button size="small" type="danger" plain @click="rejectModel(m)">❌ 驳回</el-button>
                <el-button size="small" @click="router.push(`/market/${m.id}`)">查看详情</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.audit-card {
  margin-bottom: 12px;
}
.row {
  display: flex;
  gap: 14px;
}
.thumb {
  width: 120px;
  height: 84px;
  border-radius: 6px;
  flex-shrink: 0;
}
.thumb-fallback {
  width: 120px;
  height: 84px;
  border-radius: 6px;
  background: linear-gradient(135deg, #9db8ff, #6d8df0);
  flex-shrink: 0;
}
.info {
  flex: 1;
}
.title {
  font-weight: 600;
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 6px;
}
.desc {
  font-size: 13px;
  margin: 6px 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>
