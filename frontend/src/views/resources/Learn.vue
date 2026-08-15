<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { resourceApi } from '@/api/resources'
import { LEARNING_STATUS, LEARNING_TAG, STEP_STATUS, formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)

const progress = ref(null)
const loading = ref(true)
const busy = ref(false)
const sliderProgress = ref(0)

async function load() {
  loading.value = true
  try {
    progress.value = await resourceApi.getProgress(id)
    sliderProgress.value = Math.round(progress.value.progress || 0)
  } finally {
    loading.value = false
  }
}

async function start() {
  busy.value = true
  try {
    progress.value = await resourceApi.startLearn(id)
    sliderProgress.value = 0
    ElMessage.success('开始学习！先完成第一步吧')
  } finally {
    busy.value = false
  }
}

async function saveProgress() {
  busy.value = true
  try {
    progress.value = await resourceApi.updateProgress(id, {
      progress: sliderProgress.value,
      notes: progress.value.notes,
      durationMinutes: progress.value.durationMinutes,
    })
    ElMessage.success('进度已保存')
  } finally {
    busy.value = false
  }
}

async function setStep(step, status) {
  busy.value = true
  try {
    progress.value = await resourceApi.updateStep(id, step.stepNumber, { status })
    const allDone = progress.value.steps.every((s) => s.status === 'Completed')
    if (status === 'Completed' && allDone) {
      ElMessage.success('所有步骤都完成了！可以提交完成学习～')
    }
  } finally {
    busy.value = false
  }
}

async function complete() {
  const { value } = await ElMessageBox.prompt('给自己本次学习打个分吧（0-100）', '完成学习', {
    inputPattern: /^$|^\d{1,3}$/,
    inputErrorMessage: '请输入 0-100 的整数',
    inputValue: progress.value.score != null ? String(progress.value.score) : '',
  })
  busy.value = true
  try {
    progress.value = await resourceApi.complete(id, {
      score: value ? Number(value) : null,
      notes: progress.value.notes,
    })
    ElMessage.success('🎉 恭喜完成学习！')
  } finally {
    busy.value = false
  }
}

const stepIcon = (s) => (s === 'Completed' ? '✅' : s === 'InProgress' ? '⏳' : '⬜')

onMounted(load)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <el-page-header @back="router.back()" class="back">
      <template #content>
        <span v-if="progress" class="title">{{ progress.resourceTitle }} · 学习中心</span>
      </template>
    </el-page-header>

    <!-- 未开始 -->
    <el-card v-if="progress && progress.recordId == null" class="box">
      <el-empty description="还没有开始学习这个资源">
        <el-button type="primary" size="large" :loading="busy" @click="start">🚀 开始学习</el-button>
      </el-empty>
    </el-card>

    <!-- 学习中 -->
    <template v-if="progress && progress.recordId != null">
      <el-card class="box">
        <div class="head-row">
          <el-tag :type="LEARNING_TAG[progress.status] || 'info'">
            {{ LEARNING_STATUS[progress.status] || progress.status }}
          </el-tag>
          <span class="text-muted">开始于 {{ formatDate(progress.startTime) }}</span>
          <span v-if="progress.endTime" class="text-muted">完成于 {{ formatDate(progress.endTime) }}</span>
          <span v-if="progress.score != null" class="score">🏅 得分：{{ progress.score }}</span>
        </div>

        <div class="progress-row">
          <span class="text-muted">学习进度</span>
          <el-slider v-model="sliderProgress" :disabled="progress.status === 'Completed'" class="slider" :step="5" show-stops />
          <el-button type="primary" plain :loading="busy" :disabled="progress.status === 'Completed'" @click="saveProgress">
            保存进度
          </el-button>
        </div>
        <el-progress :percentage="sliderProgress" :stroke-width="14" striped striped-flow />
      </el-card>

      <!-- 步骤列表 -->
      <el-card class="box">
        <div class="section-label">📋 学习步骤</div>
        <div v-for="step in progress.steps" :key="step.stepNumber" class="step-row">
          <span class="step-icon">{{ stepIcon(step.status) }}</span>
          <div class="step-info">
            <div class="step-title">
              步骤 {{ step.stepNumber }}：{{ step.stepTitle }}
              <el-tag size="small" effect="plain">{{ STEP_STATUS[step.status] || step.status }}</el-tag>
            </div>
            <div class="text-muted" v-if="step.completedTime">完成于 {{ formatDate(step.completedTime) }}</div>
          </div>
          <div class="step-actions">
            <el-button v-if="step.status === 'NotStarted'" size="small" :loading="busy" @click="setStep(step, 'InProgress')">
              开始本步骤
            </el-button>
            <el-button v-if="step.status !== 'Completed'" size="small" type="success" plain :loading="busy" @click="setStep(step, 'Completed')">
              标记完成
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 完成学习 -->
      <el-card v-if="progress.status !== 'Completed'" class="box finish-box">
        <div>
          <div class="section-label">🎓 完成学习</div>
          <p class="text-muted">完成所有步骤并提交得分后，系统会记录你的学习成就。</p>
        </div>
        <el-button type="success" size="large" :loading="busy" @click="complete">提交完成</el-button>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.back {
  margin-bottom: 16px;
}
.title {
  font-weight: 600;
}
.box {
  margin-bottom: 16px;
}
.head-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}
.score {
  font-weight: 600;
  color: #e6a23c;
}
.progress-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}
.slider {
  flex: 1;
}
.section-label {
  font-weight: 600;
  margin-bottom: 12px;
}
.step-row {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px dashed var(--border-color);
}
.step-icon {
  font-size: 20px;
}
.step-info {
  flex: 1;
}
.step-title {
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}
.step-actions {
  display: flex;
  gap: 8px;
}
.finish-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
