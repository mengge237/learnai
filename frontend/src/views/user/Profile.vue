<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const loading = ref(true)
const saving = ref(false)
const formRef = ref()

const form = reactive({
  gender: '',
  phone: '',
  email: '',
  location: '',
  birthdate: null,
  bio: '',
  province: '',
  city: '',
  defaultShippingAddress: '',
})

onMounted(async () => {
  try {
    const u = await auth.fetchMe()
    Object.keys(form).forEach((k) => {
      if (u[k] !== undefined && u[k] !== null) form[k] = u[k]
    })
  } finally {
    loading.value = false
  }
})

async function save() {
  saving.value = true
  try {
    await authApi.updateProfile({ ...form })
    await auth.fetchMe()
    ElMessage.success('资料已保存')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="page-container narrow" v-loading="loading">
    <div class="page-title">👤 个人资料</div>

    <el-card class="info-card">
      <div class="avatar-big">{{ (auth.user?.username || '?').slice(0, 1).toUpperCase() }}</div>
      <div>
        <div class="username">{{ auth.user?.username }}</div>
        <div class="text-muted">
          {{ auth.user?.roleName || '普通用户' }}
          <template v-if="auth.user?.email"> · {{ auth.user.email }}</template>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-form ref="formRef" :model="form" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择" clearable>
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
                <el-option label="保密" value="保密" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期">
              <el-date-picker v-model="form.birthdate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="省份">
              <el-input v-model="form.province" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市">
              <el-input v-model="form.city" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所在地区">
              <el-input v-model="form.location" maxlength="100" placeholder="例如：北京市海淀区" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="默认收货地址">
              <el-input v-model="form.defaultShippingAddress" type="textarea" :rows="2" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人简介">
              <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.narrow {
  max-width: 800px;
}
.info-card {
  margin-bottom: 16px;
}
.info-card :deep(.el-card__body) {
  display: flex;
  gap: 16px;
  align-items: center;
}
.avatar-big {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: var(--theme-color);
  color: #fff;
  font-size: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.username {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}
</style>
