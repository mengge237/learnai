<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { resourceApi } from '@/api/resources'
import { studyApi } from '@/api/study'
import { LEARNING_STATUS, LEARNING_TAG, STEP_STATUS, formatDate } from '@/utils/format'
import { useTts } from '@/composables/useSpeech'
import LineIcon from '@/components/LineIcon.vue'
import PageBreadcrumb from '@/components/PageBreadcrumb.vue'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const id = Number(route.params.id)

const progress = ref(null)
const loading = ref(true)
const busy = ref(false)
const sliderProgress = ref(0)
const currentStep = ref(1)

// ---------- 语音朗读（Web Speech API，无需后端） ----------
const tts = useTts()
// 切换步骤时停止朗读，避免读到旧内容
watch(currentStep, () => tts.stop())

function readAloud(step) {
  if (!step?.stepContent) {
    ElMessage.info(t('本章内容正在编写中，暂无正文可朗读'))
    return
  }
  tts.speak(step.stepContent)
}

// ---------- 学习计时与激励 ----------
const study = ref(null) // StudyStatsDto
const elapsedSeconds = ref(0) // 本次学习秒数
let timer = null
let notifiedAt = 0 // 上次番茄钟提醒时的分钟数

const pomodoroMinutes = computed(() => Math.floor(elapsedSeconds.value / 60))

async function load() {
  loading.value = true
  try {
    progress.value = await resourceApi.getProgress(id)
    sliderProgress.value = Math.round(progress.value.progress || 0)
    initCurrentStep()
    refreshStudy()
    // 进入学习页即开始计时（学习中状态）
    if (progress.value.recordId != null && progress.value.status !== 'Completed') {
      startTimer()
    }
  } finally {
    loading.value = false
  }
}

async function refreshStudy() {
  try {
    study.value = await studyApi.stats()
  } catch {
    /* 未登录或接口异常时静默 */
  }
}

/** 学习计时：每 30 秒心跳上报一次实际学习时长 */
function startTimer() {
  stopTimer()
  elapsedSeconds.value = 0
  notifiedAt = 0
  timer = setInterval(async () => {
    if (!progress.value || progress.value.recordId == null) return
    if (progress.value.status === 'Completed') {
      stopTimer()
      return
    }
    elapsedSeconds.value += 30
    try {
      study.value = await studyApi.heartbeat({ resourceId: id, seconds: 30 })
    } catch {
      /* 心跳失败不打断计时 */
    }
    // 番茄钟提醒：每学习 25 分钟提醒休息
    const m = pomodoroMinutes.value
    if (m > 0 && m % 25 === 0 && notifiedAt !== m) {
      notifiedAt = m
      ElNotification({
        title: t('学习提醒'),
        message: t('已连续学习 {m} 分钟，起来活动一下，眺望远处放松眼睛～', { m }),
        type: 'success',
        duration: 4000,
      })
    }
  }, 30000)
}

function stopTimer() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

/** 当前定位到第一个未完成的步骤；全部完成则定位最后一步 */
function initCurrentStep() {
  const steps = progress.value?.steps || []
  const firstOpen = steps.find((s) => s.status !== 'Completed')
  currentStep.value = firstOpen ? firstOpen.stepNumber : (steps.at(-1)?.stepNumber || 1)
}

const currentIdx = computed(() => {
  const steps = progress.value?.steps || []
  return steps.findIndex((s) => s.stepNumber === currentStep.value)
})
const current = computed(() =>
  currentIdx.value >= 0 ? progress.value.steps[currentIdx.value] : null,
)
const prevStep = computed(() => (currentIdx.value > 0 ? progress.value.steps[currentIdx.value - 1] : null))
const nextStep = computed(() =>
  currentIdx.value >= 0 && currentIdx.value < progress.value.steps.length - 1
    ? progress.value.steps[currentIdx.value + 1]
    : null,
)

/** 点击目录/上下步：定位步骤；未开始的步骤自动开始 */
async function goStep(step) {
  if (!step || busy.value) return
  currentStep.value = step.stepNumber
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
    ElMessage.success(t('开始学习！先完成第一步吧'))
    startTimer()
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
    ElMessage.success(t('进度已保存'))
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
      ElMessage.success(t('所有步骤都完成了！可以提交完成学习～'))
    }
  } finally {
    busy.value = false
  }
}

async function complete() {
  let value
  try {
    ;({ value } = await ElMessageBox.prompt(t('给自己本次学习打个分吧（0-100）'), t('完成学习'), {
      inputPattern: /^$|^\d{1,3}$/,
      inputErrorMessage: t('请输入 0-100 的整数'),
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
    stopTimer()
    ElMessage.success(t('恭喜完成学习！'))
  } finally {
    busy.value = false
  }
}

const stepIcon = (s) => (STEP_STATUS[s] ? t(STEP_STATUS[s]) : s)

onMounted(load)
onBeforeUnmount(() => {
  stopTimer()
  tts.stop()
})
</script>

<template>
  <div class="page-container learn-page" v-loading="loading">
    <template v-if="progress">
      <!-- 面包屑：首页 / 学习资源 / 当前课程 -->
      <PageBreadcrumb
        :items="[
          { label: $t('首页'), to: '/' },
          { label: $t('学习资源'), to: '/resources' },
          { label: progress.resourceTitle },
        ]"
      />

      <!-- 未开始 -->
      <el-card v-if="progress.recordId == null" class="box">
        <el-empty :description="$t('还没有开始学习这个资源')">
          <el-button type="primary" size="large" :loading="busy" @click="start"><LineIcon name="arrowRight" :size="14" /> {{ $t('开始学习') }}</el-button>
        </el-empty>
      </el-card>

      <!-- 学习中：侧边栏目录 + 正文阅读区 -->
      <div v-else class="learn-layout">
        <!-- ============ 左侧目录侧边栏 ============ -->
        <aside class="learn-sidebar">
          <div class="side-title">
            <h2 class="side-course">{{ progress.resourceTitle }}</h2>
          </div>

          <div class="side-status">
            <el-tag :type="LEARNING_TAG[progress.status] || 'info'" size="small">
              {{ $t(LEARNING_STATUS[progress.status] || progress.status) }}
            </el-tag>
            <span v-if="progress.score != null" class="score"><LineIcon name="star" :size="13" /> {{ progress.score }} {{ $t('分') }}</span>
          </div>

          <!-- 学习计时与激励 -->
          <div class="side-study">
            <div class="study-row">
              <span class="study-label">{{ $t('本次学习') }}</span>
              <span class="study-value">{{ pomodoroMinutes }} {{ $t('分钟') }}</span>
            </div>
            <div class="study-row">
              <span class="study-label">{{ $t('今日累计') }}</span>
              <span class="study-value">{{ study?.todayMinutes ?? 0 }} {{ $t('分钟') }}</span>
            </div>
            <div class="study-row">
              <span class="study-label">{{ $t('连续学习') }}</span>
              <span class="study-value flame"><LineIcon name="flame" :size="18" /> {{ study?.streakDays ?? 0 }} {{ $t('天') }}</span>
            </div>
          </div>

          <el-progress :percentage="sliderProgress" :stroke-width="8" class="side-progress" />

          <div class="side-steps">
            <div class="side-steps-label">{{ $t('教程目录') }}</div>
            <button
              v-for="step in progress.steps"
              :key="step.stepNumber"
              class="side-step"
              :class="{ active: step.stepNumber === currentStep }"
              @click="goStep(step)"
            >
              <span class="side-step-icon">{{ stepIcon(step.status) }}</span>
              <span class="side-step-text">
                <span class="side-step-no">{{ $t('步骤') }} {{ step.stepNumber }}</span>
                <span class="side-step-name">{{ step.stepTitle }}</span>
              </span>
            </button>
          </div>

          <div class="side-foot">
            <el-button v-if="progress.status !== 'Completed'" type="success" size="small" :loading="busy" @click="complete">
              {{ $t('提交完成') }}
            </el-button>
            <span v-else class="text-muted side-done">{{ $t('已完成') }} · {{ formatDate(progress.endTime) }}</span>
          </div>
        </aside>

        <!-- ============ 右侧正文阅读区 ============ -->
        <main class="learn-main">
          <el-card class="box reader">
            <template v-if="current">
              <div class="reader-head">
                <span class="reader-no">{{ $t('步骤') }} {{ current.stepNumber }}</span>
                <h2 class="reader-title">{{ current.stepTitle }}</h2>
                <el-tag size="small" effect="plain">{{ $t(STEP_STATUS[current.status] || current.status) }}</el-tag>

                <!-- 语音朗读：支持播放/暂停/继续/停止 -->
                <div v-if="tts.supported" class="speak-ctrl">
                  <template v-if="!tts.speaking">
                    <el-button size="small" plain @click="readAloud(current)">
                      <LineIcon name="volume" :size="14" /> {{ $t('朗读本步骤') }}
                    </el-button>
                  </template>
                  <template v-else>
                    <el-button size="small" plain @click="tts.paused ? tts.resume() : tts.pause()">
                      <LineIcon :name="tts.paused ? 'play' : 'pause'" :size="13" /> {{ tts.paused ? $t('继续') : $t('暂停') }}
                    </el-button>
                    <el-button size="small" plain @click="tts.stop()">
                      <LineIcon name="stop" :size="13" /> {{ $t('停止') }}
                    </el-button>
                  </template>
                  <span v-if="tts.speaking" class="speak-indicator" :class="{ paused: tts.paused }">
                    {{ tts.paused ? $t('· 已暂停') : $t('· 正在朗读') }}
                  </span>
                </div>
              </div>
              <div class="reader-meta text-muted">
                <span v-if="current.completedTime">{{ $t('完成于') }} {{ formatDate(current.completedTime) }}</span>
                <span v-else>{{ $t('正在阅读') }}</span>
              </div>
              <div class="reader-content">{{ current.stepContent || $t('本章内容正在编写中，敬请期待。') }}</div>

              <div class="reader-actions">
                <el-button v-if="current.status === 'NotStarted'" type="primary" :loading="busy" @click="setStep(current, 'InProgress')">
                  {{ $t('开始本步骤') }}
                </el-button>
                <el-button v-if="current.status !== 'Completed'" type="success" plain :loading="busy" @click="setStep(current, 'Completed')">
                  {{ $t('标记完成') }}
                </el-button>
              </div>
            </template>
            <el-empty v-else :description="$t('暂无步骤')" />
          </el-card>

          <!-- 进度调整 -->
          <el-card class="box progress-card">
            <div class="progress-row">
              <span class="text-muted">{{ $t('学习进度') }}</span>
              <el-slider v-model="sliderProgress" :disabled="progress.status === 'Completed'" class="slider" :step="5" show-stops />
              <el-button type="primary" plain :loading="busy" :disabled="progress.status === 'Completed'" @click="saveProgress">
                {{ $t('保存进度') }}
              </el-button>
            </div>
          </el-card>

          <!-- 上下步 -->
          <div class="step-nav">
            <el-button size="large" :disabled="!prevStep" @click="goStep(prevStep)">
              ← {{ $t('上一步') }}<span v-if="prevStep" class="nav-step">：{{ prevStep.stepTitle }}</span>
            </el-button>
            <el-button size="large" type="primary" :disabled="!nextStep" @click="goStep(nextStep)">
              {{ $t('下一步') }}<span v-if="nextStep" class="nav-step">：{{ nextStep.stepTitle }}</span> →
            </el-button>
          </div>
        </main>
      </div>
    </template>
  </div>
</template>

<style scoped>
.learn-page {
  max-width: 1200px;
}
.box {
  margin-bottom: 16px;
}

/* ================= 两栏布局 ================= */
.learn-layout {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 16px;
  align-items: start;
}
@media (max-width: 900px) {
  .learn-layout {
    grid-template-columns: 1fr;
  }
  .learn-sidebar {
    position: static;
  }
}

/* ================= 侧边栏 ================= */
.learn-sidebar {
  position: sticky;
  top: 76px;
  border: 1px solid var(--border-color);
  background: var(--el-bg-color);
  border-radius: 2px;
  overflow: hidden;
}
.side-title {
  padding: 14px 16px 10px;
  border-bottom: 1px solid var(--line-soft);
}
.side-course {
  font-size: 16px;
  margin: 8px 0 0;
  letter-spacing: 1px;
}
.side-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px 0;
  flex-wrap: wrap;
}
.score {
  font-size: 13px;
  color: #e6a23c;
}
/* 学习激励区 */
.side-study {
  margin: 10px 16px;
  padding: 10px 12px;
  border: 1px dashed var(--line-color);
  background: var(--el-bg-color-page, #f5f5f3);
}
.study-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  line-height: 1.9;
}
.study-label {
  color: var(--el-text-color-secondary);
  letter-spacing: 1px;
}
.study-value {
  font-weight: 700;
  font-family: 'Consolas', 'Courier New', monospace;
}
.flame {
  color: #e8590c;
}
.side-progress {
  margin: 0 16px 6px;
}
.side-steps {
  padding: 4px 0 8px;
}
.side-steps-label {
  padding: 10px 16px 6px;
  font-size: 12px;
  letter-spacing: 3px;
  color: var(--el-text-color-secondary);
}
.side-step {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  width: 100%;
  padding: 10px 16px;
  border: none;
  border-left: 3px solid transparent;
  background: none;
  cursor: pointer;
  text-align: left;
  transition: background 0.15s;
}
.side-step:hover {
  background: var(--el-color-primary-light-9);
}
.side-step.active {
  border-left-color: var(--theme-color);
  background: var(--el-color-primary-light-9);
  font-weight: 600;
}
.side-step-icon {
  font-size: 14px;
  line-height: 1.6;
}
.side-step-text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.side-step-no {
  font-size: 11px;
  color: var(--theme-color);
  letter-spacing: 1px;
}
.side-step-name {
  font-size: 13px;
  color: var(--el-text-color-regular);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.side-foot {
  padding: 12px 16px;
  border-top: 1px solid var(--line-soft);
}
.side-done {
  font-size: 12px;
}

/* ================= 阅读区 ================= */
.reader {
  padding: 6px 10px;
}
.reader-head {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--line-soft);
}
.speak-ctrl {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}
.speak-ctrl .el-button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.speak-indicator {
  font-size: 12px;
  letter-spacing: 1px;
  color: var(--theme-color);
  animation: speak-pulse 1.2s ease-in-out infinite;
}
.speak-indicator.paused {
  animation: none;
  color: var(--el-text-color-secondary);
}
@keyframes speak-pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.35;
  }
}
.reader-no {
  font-size: 12px;
  letter-spacing: 2px;
  color: var(--theme-color);
  font-family: 'Consolas', 'Courier New', monospace;
}
.reader-title {
  font-size: 22px;
  letter-spacing: 1px;
  margin: 0;
}
.reader-meta {
  padding: 10px 0 4px;
  font-size: 12px;
}
.reader-content {
  white-space: pre-line;
  line-height: 2;
  font-size: 15px;
  color: var(--el-text-color-regular);
  padding: 12px 4px;
}
.reader-actions {
  display: flex;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
}
.progress-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.slider {
  flex: 1;
}
.step-nav {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}
.nav-step {
  font-weight: 400;
  margin-left: 6px;
}
</style>
