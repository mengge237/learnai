<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  studentNo: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  gender: '男',
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度需在 4-20 个字符之间', trigger: 'blur' },
  ],
  studentNo: [{ max: 30, message: '学号长度不能超过 30 个字符', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度需在 6-50 个字符之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        value === form.password ? callback() : callback(new Error('两次输入的密码不一致'))
      },
      trigger: 'blur',
    },
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await auth.register({ ...form })
    ElMessage.success('注册成功，请登录')
    router.push({ name: 'login', query: { username: form.username } })
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
          <span class="brand-name">AI智学</span>
          <span class="campus-badge">校园特供版</span>
        </div>
        <div class="auth-sub text-muted">注册校园账号 · 开启学习之旅</div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="4-20 个字符" size="large" />
        </el-form-item>
        <el-form-item label="学号（校园特供版）" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="选填，如 2026010016" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少 6 位" show-password size="large" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password size="large" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="选填" size="large" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="选填" size="large" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button type="primary" size="large" class="submit" :loading="loading" @click="onSubmit">
          注 册
        </el-button>
      </el-form>
      <div class="auth-foot">
        已有账号？<el-link type="primary" @click="router.push('/login')">去登录</el-link>
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
</style>
