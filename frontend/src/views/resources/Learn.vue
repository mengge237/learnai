<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
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
const currentStep = ref(1)
const stepEls = ref([])

async function load() {
  loading.value = true
  try {
    progress.value = await resourceApi.getProgress(id)
    sliderProgress.value = Math.round(progress.value.progress || 0)
    initCurrentStep()
  } finally {
    loading.value = false
  }
}

/** 当前定位到第一个未完成的步骤；全部完成则定位最后一步 */
function initCurrentStep() {
  const steps = progress.value?.steps || []
  const firstOpen = steps.find((s) => s.status !== 'Completed')
  currentStep.value = firstOpen ? firstOpen.stepNumber : (steps.at(-1)?.stepNumber || 1)
}

function setStepRef(el, idx) {
  stepEls.value[idx] = el
}

const currentIdx = computed(() => {
  const steps = progress.value?.steps || []
  return steps.findIndex((s) => s.stepNumber === currentStep.value)
})
const prevStep = computed(() => (currentIdx.value > 0 ? progress.value.steps[currentIdx.value - 1] : null))
const nextStep = computed(() =>
  currentIdx.value >= 0 && currentIdx.value < progress.value.steps.length - 1
    ? progress.value.steps[currentIdx.value + 1]
    : null,
)

/** 教程式上下步：跳转并滚动到该步骤，未开始的步骤自动开始 */
async function goStep(step) {
  if (!step || busy.value) return
  currentStep.value = step.stepNumber
  await nextTick()
  stepEls.value[step.stepNumber - 1]?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  if (step.status === 'NotStarted') {
    await setStep(step, 'InProgress')
  }
}

/** 点击步骤标题：仅展开阅读正文，不改变学习状态 */
function expandStep(step) {
  currentStep.value = step.stepNumber
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
  let value
  try {
    ;({ value } = await ElMessageBox.prompt('给自己本次学习打个分吧（0-100）', '完成学习', {
      inputPattern: /^$|^\d{1,3}$/,
      inputErrorMessage: '请输入 0-100 的整数',
      inputValue: progress.value.score != null ? String(progress.value.score) : '',
    }))
  } catch {
    return // 用户取消
  }
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
    <!-- 面包屑（紧凑） -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/resources' }">学习资源</el-breadcrumb-item>
      <el-breadcrumb-item v-if="progress" :to="{ path: `/resources/${id}` }">{{ progress.resourceTitle }}</el-breadcrumb-item>
    </el-breadcrumb>

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

      <!-- 步骤列表（教程正文可在线阅读） -->
      <el-card class="box">
        <div class="section-label">📋 教程目录 <span class="text-muted step-hint">点击步骤标题阅读教程内容</span></div>
        <div
          v-for="step in progress.steps"
          :key="step.stepNumber"
          :ref="(el) => setStepRef(el, step.stepNumber - 1)"
          class="step-row"
          :class="{ 'step-current': step.stepNumber === currentStep }"
        >
          <span class="step-icon">{{ stepIcon(step.status) }}</span>
          <div class="step-info">
            <div class="step-title" @click="expandStep(step)">
              <span class="step-no">步骤 {{ step.stepNumber }}</span>
              <span class="step-name">{{ step.stepTitle }}</span>
              <el-tag size="small" effect="plain">{{ STEP_STATUS[step.status] || step.status }}</el-tag>
              <span v-if="step.stepNumber === currentStep" class="text-muted">▼ 正在阅读</span>
            </div>
            <div class="text-muted" v-if="step.completedTime">完成于 {{ formatDate(step.completedTime) }}</div>
            <div v-if="step.stepNumber === currentStep" class="step-content">{{ step.stepContent || '本章内容正在编写中，敬请期待。' }}</div>
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

      <!-- 教程式上下步导航 -->
      <el-card v-if="progress.steps.length" class="box nav-box">
        <el-button :disabled="!prevStep" @click="goStep(prevStep)">
          ← 上一步<span v-if="prevStep" class="nav-step">：步骤 {{ prevStep.stepNumber }} {{ prevStep.stepTitle }}</span>
        </el-button>
        <el-button type="primary" :disabled="!nextStep" @click="goStep(nextStep)">
          下一步<span v-if="nextStep" class="nav-step">：步骤 {{ nextStep.stepNumber }} {{ nextStep.stepTitle }}</span> →
        </el-button>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.breadcrumb {
  margin-bottom: 16px;
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
  padding: 12px 8px;
  border-bottom: 1px dashed var(--border-color);
  border-left: 3px solid transparent;
}
.step-current {
  border-left-color: var(--theme-color);
  background: var(--el-color-primary-light-9);
}
.nav-box {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}
.nav-step {
  font-weight: 400;
  margin-left: 6px;
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
  cursor: pointer;
  flex-wrap: wrap;
}
.step-hint {
  font-size: 12px;
  font-weight: 400;
  margin-left: 8px;
}
.step-no {
  color: var(--theme-color);
  font-weight: 700;
  letter-spacing: 1px;
}
.step-name {
  font-weight: 600;
}
/* 教程正文：像文章一样可读 */
.step-content {
  margin-top: 10px;
  padding: 14px 16px;
  background: var(--el-bg-color-page, #f5f5f3);
  border-left: 3px solid var(--theme-color);
  white-space: pre-line;
  line-height: 1.9;
  font-size: 14px;
  color: var(--el-text-color-regular);
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
