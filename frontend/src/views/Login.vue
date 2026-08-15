<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { usePrefsStore } from '@/stores/prefs'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const prefs = usePrefsStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const { user } = await auth.login(form)
    prefs.syncFromServer(user) // 服务器偏好覆盖本地
    ElMessage.success(`欢迎回来，${user.username}！`)
    router.push(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2 class="auth-title">登录 LearnAI</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" @keyup.enter="onSubmit" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit" :loading="loading" @click="onSubmit">
          登 录
        </el-button>
      </el-form>
      <div class="auth-foot">
        还没有账号？<el-link type="primary" @click="router.push('/register')">立即注册</el-link>
      </div>
      <el-divider><span class="text-muted">演示账号</span></el-divider>
      <div class="demo-tips text-muted">
        <div>普通用户：demo / demo123</div>
        <div>审核员：auditor / audit123</div>
        <div>管理员：admin / admin123</div>
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
}
.auth-title {
  text-align: center;
  margin-bottom: 24px;
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
  text-align: center;
  line-height: 1.8;
}
</style>
