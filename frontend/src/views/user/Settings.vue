<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { usePrefsStore } from '@/stores/prefs'
import { useAuthStore } from '@/stores/auth'

const prefs = usePrefsStore()
const auth = useAuthStore()

const pwdFormRef = ref()
const changing = ref(false)
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, cb) => (value !== pwd.newPassword ? cb(new Error('两次输入的密码不一致')) : cb()),
      trigger: 'blur',
    },
  ],
}

async function changePassword() {
  await pwdFormRef.value.validate()
  changing.value = true
  try {
    await authApi.changePassword({ oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    ElMessage.success('密码修改成功')
    Object.keys(pwd).forEach((k) => (pwd[k] = ''))
  } finally {
    changing.value = false
  }
}
</script>

<template>
  <div class="page-container narrow">
    <div class="page-title">⚙️ 个性化设置</div>

    <el-card class="block">
      <div class="section-label">🎨 界面外观</div>
      <div class="pref-row">
        <span>外观模式</span>
        <el-radio-group :model-value="prefs.prefs.themeMode || 'auto'" @change="(v) => prefs.update({ themeMode: v })">
          <el-radio-button value="auto">跟随系统</el-radio-button>
          <el-radio-button value="light">浅色</el-radio-button>
          <el-radio-button value="dark">深色</el-radio-button>
        </el-radio-group>
      </div>
      <div class="pref-row">
        <span>主题色</span>
        <el-color-picker :model-value="prefs.prefs.themeColor" @change="(v) => prefs.update({ themeColor: v })" />
      </div>
      <div class="pref-row">
        <span>边框颜色</span>
        <el-color-picker :model-value="prefs.prefs.borderColor" @change="(v) => prefs.update({ borderColor: v })" />
      </div>
      <div class="pref-row">
        <span>字体大小</span>
        <el-slider :model-value="prefs.prefs.fontSize" :min="12" :max="20" :step="1" class="slider"
          @change="(v) => prefs.update({ fontSize: v })" />
        <span class="text-muted">{{ prefs.prefs.fontSize }}px</span>
      </div>
      <div class="pref-row">
        <span>侧边栏位置</span>
        <el-radio-group :model-value="prefs.prefs.sidebarPosition" @change="(v) => prefs.update({ sidebarPosition: v })">
          <el-radio-button value="left">左侧</el-radio-button>
          <el-radio-button value="right">右侧</el-radio-button>
        </el-radio-group>
      </div>
      <div class="pref-row">
        <span>动画速度</span>
        <el-select :model-value="prefs.prefs.animationSpeed" class="speed" @change="(v) => prefs.update({ animationSpeed: v })">
          <el-option label="关闭动画" value="none" />
          <el-option label="慢速" value="slow" />
          <el-option label="正常" value="normal" />
          <el-option label="快速" value="fast" />
        </el-select>
      </div>
      <div class="text-muted tip">{{ auth.isLoggedIn ? '设置已自动同步到你的账号' : '登录后设置将同步到账号' }}</div>
    </el-card>

    <el-card v-if="auth.isLoggedIn" class="block">
      <div class="section-label">🔒 修改密码</div>
      <el-form ref="pwdFormRef" :model="pwd" :rules="pwdRules" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwd.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwd.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwd.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="changing" @click="changePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card v-else class="block">
      <div class="section-label">🔒 修改密码</div>
      <div class="text-muted">登录后可修改密码，<router-link to="/login" class="login-link">去登录</router-link></div>
    </el-card>
  </div>
</template>

<style scoped>
.narrow {
  max-width: 760px;
}
.block {
  margin-bottom: 16px;
}
.section-label {
  font-weight: 600;
  margin-bottom: 14px;
}
.pref-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}
.pref-row > span:first-child {
  width: 90px;
  color: var(--el-text-color-regular);
}
.slider {
  width: 260px;
}
.speed {
  width: 140px;
}
.tip {
  font-size: 13px;
}
.login-link {
  color: var(--theme-color);
}

</style>
