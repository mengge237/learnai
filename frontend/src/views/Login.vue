<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { usePrefsStore } from '@/stores/prefs'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const prefs = usePrefsStore()
const { t } = useI18n()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

// 注册成功后跳转回来时自动填入用户名
onMounted(() => {
  if (route.query.username) form.username = route.query.username
})

// 演示账号（校园特供版一键填入）
const demoAccounts = [
  { label: '学生演示', username: 'demo', password: 'demo123' },
  { label: '审核员', username: 'auditor', password: 'audit123' },
  { label: '管理员', username: 'admin', password: 'admin123' },
]

// 校验提示随语言切换（用 computed 重新生成 rules）
const rules = computed(() => ({
  username: [{ required: true, message: t('请输入用户名'), trigger: 'blur' }],
  password: [{ required: true, message: t('请输入密码'), trigger: 'blur' }],
}))

function fillDemo(acc) {
  form.username = acc.username
  form.password = acc.password
  ElMessage.info(t('已填入「{label}」演示账号，点击登录即可', { label: t(acc.label) }))
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const { user } = await auth.login(form)
    prefs.syncFromServer(user) // 服务器偏好覆盖本地
    ElMessage.success(t('欢迎回来，{name}！', { name: user.username }))
    router.push(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <div class="auth-head">
        <div class="auth-brand">
          <span class="brand-mark">◈</span>
          <span class="brand-name">{{ $t('AI智学') }}</span>
          <span class="campus-badge">{{ $t('校园特供版') }}</span>
        </div>
        <div class="auth-sub text-muted">{{ $t('校园学习平台') }} · {{ $t('欢迎登录') }}</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="onSubmit">
        <el-form-item :label="$t('用户名 / 学号')" prop="username">
          <el-input v-model="form.username" :placeholder="$t('请输入用户名')" size="large" />
        </el-form-item>
        <el-form-item :label="$t('密码')" prop="password">
          <el-input v-model="form.password" type="password" :placeholder="$t('请输入密码')" show-password size="large" @keyup.enter="onSubmit" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit" :loading="loading" @click="onSubmit">
          {{ $t('登 录') }}
        </el-button>
      </el-form>
      <div class="auth-foot">
        {{ $t('还没有账号？') }}<el-link type="primary" @click="router.push('/register')">{{ $t('立即注册') }}</el-link>
      </div>

      <el-divider><span class="text-muted">{{ $t('演示账号（点击一键填入）') }}</span></el-divider>
      <div class="demo-tips">
        <el-button v-for="acc in demoAccounts" :key="acc.username" size="small" class="demo-btn" @click="fillDemo(acc)">
          {{ $t(acc.label) }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.auth-page {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 16px;
}
.auth-card {
  width: 400px;
  border-top: 4px solid var(--theme-color);
}
.auth-head {
  text-align: center;
  margin-bottom: 24px;
}
.auth-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--theme-color);
}
.brand-mark {
  font-size: 28px;
}
.brand-name {
  color: var(--el-text-color-primary);
}
.campus-badge {
  font-size: 11px;
  font-weight: 400;
  letter-spacing: 1px;
  color: var(--theme-color);
  border: 1px solid var(--theme-color);
  border-radius: 2px;
  padding: 2px 6px;
  margin-left: 4px;
}
.auth-sub {
  margin-top: 8px;
  letter-spacing: 1px;
}
.submit {
  width: 100%;
}
.auth-foot {
  text-align: center;
  margin-top: 12px;
  font-size: 14px;
}
.demo-tips {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}
.demo-btn {
  border-radius: 2px;
}
</style>
