<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { setLocale } from '@/i18n'
import { authApi } from '@/api/auth'
import { usePrefsStore } from '@/stores/prefs'
import { useAuthStore } from '@/stores/auth'
import LineIcon from '@/components/LineIcon.vue'

const { t, locale } = useI18n()
const prefs = usePrefsStore()
const auth = useAuthStore()

const pwdFormRef = ref()
const changing = ref(false)
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

// computed：语言切换后校验文案随之更新
const pwdRules = computed(() => ({
  oldPassword: [{ required: true, message: t('请输入原密码'), trigger: 'blur' }],
  newPassword: [
    { required: true, message: t('请输入新密码'), trigger: 'blur' },
    { min: 6, message: t('密码至少 6 位'), trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: t('请再次输入新密码'), trigger: 'blur' },
    {
      validator: (rule, value, cb) => (value !== pwd.newPassword ? cb(new Error(t('两次输入的密码不一致'))) : cb()),
      trigger: 'blur',
    },
  ],
}))

/** 语言设置：get 读 locale，set 调 setLocale 持久化 */
const lang = computed({
  get: () => locale.value,
  set: (v) => setLocale(v),
})

async function changePassword() {
  await pwdFormRef.value.validate()
  changing.value = true
  try {
    await authApi.changePassword({ oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    ElMessage.success(t('密码修改成功'))
    Object.keys(pwd).forEach((k) => (pwd[k] = ''))
  } finally {
    changing.value = false
  }
}
</script>

<template>
  <div class="page-container narrow">
    <div class="page-title"><LineIcon name="settings" :size="19" /> {{ $t('个性化设置') }}</div>

    <el-card class="block">
      <div class="section-label"><LineIcon name="monitor" :size="15" /> {{ $t('界面外观') }}</div>
      <div class="pref-row">
        <span>{{ $t('外观模式') }}</span>
        <el-radio-group :model-value="prefs.prefs.themeMode || 'auto'" @change="(v) => prefs.update({ themeMode: v })">
          <el-radio-button value="auto">{{ $t('跟随系统') }}</el-radio-button>
          <el-radio-button value="light">{{ $t('浅色') }}</el-radio-button>
          <el-radio-button value="dark">{{ $t('深色') }}</el-radio-button>
        </el-radio-group>
      </div>
      <div class="pref-row">
        <span>{{ $t('主题色') }}</span>
        <el-color-picker :model-value="prefs.prefs.themeColor" @change="(v) => prefs.update({ themeColor: v })" />
      </div>
      <div class="pref-row">
        <span>{{ $t('边框颜色') }}</span>
        <el-color-picker :model-value="prefs.prefs.borderColor" @change="(v) => prefs.update({ borderColor: v })" />
      </div>
      <div class="pref-row">
        <span>{{ $t('字体大小') }}</span>
        <el-slider :model-value="prefs.prefs.fontSize" :min="12" :max="20" :step="1" class="slider"
          @change="(v) => prefs.update({ fontSize: v })" />
        <span class="text-muted">{{ prefs.prefs.fontSize }}px</span>
      </div>
      <div class="pref-row">
        <span>{{ $t('侧边栏位置') }}</span>
        <el-radio-group :model-value="prefs.prefs.sidebarPosition" @change="(v) => prefs.update({ sidebarPosition: v })">
          <el-radio-button value="left">{{ $t('左侧') }}</el-radio-button>
          <el-radio-button value="right">{{ $t('右侧') }}</el-radio-button>
        </el-radio-group>
      </div>
      <div class="pref-row">
        <span>{{ $t('动画速度') }}</span>
        <el-select :model-value="prefs.prefs.animationSpeed" class="speed" @change="(v) => prefs.update({ animationSpeed: v })">
          <el-option :label="$t('关闭动画')" value="none" />
          <el-option :label="$t('慢速')" value="slow" />
          <el-option :label="$t('正常')" value="normal" />
          <el-option :label="$t('快速')" value="fast" />
        </el-select>
      </div>
      <div class="text-muted tip">{{ auth.isLoggedIn ? $t('设置已自动同步到你的账号') : $t('登录后设置将同步到账号') }}</div>
    </el-card>

    <el-card class="block">
      <div class="section-label"><LineIcon name="globe" :size="15" /> {{ $t('语言') }}</div>
      <div class="pref-row">
        <span>{{ $t('语言') }}</span>
        <el-radio-group :model-value="lang" @change="(v) => (lang = v)">
          <el-radio-button value="zh-CN">{{ $t('中文') }}</el-radio-button>
          <el-radio-button value="en-US">English</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <el-card v-if="auth.isLoggedIn" class="block">
      <div class="section-label"><LineIcon name="lock" :size="15" /> {{ $t('修改密码') }}</div>
      <el-form ref="pwdFormRef" :model="pwd" :rules="pwdRules" label-width="90px">
        <el-form-item :label="$t('原密码')" prop="oldPassword">
          <el-input v-model="pwd.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item :label="$t('新密码')" prop="newPassword">
          <el-input v-model="pwd.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item :label="$t('确认新密码')" prop="confirmPassword">
          <el-input v-model="pwd.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="changing" @click="changePassword">{{ $t('修改密码') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card v-else class="block">
      <div class="section-label"><LineIcon name="lock" :size="15" /> {{ $t('修改密码') }}</div>
      <div class="text-muted">{{ $t('登录后可修改密码，') }}<router-link to="/login" class="login-link">{{ $t('去登录') }}</router-link></div>
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
.page-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.section-label {
  display: flex;
  align-items: center;
  gap: 8px;
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
