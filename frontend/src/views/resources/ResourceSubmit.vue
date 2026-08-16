<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { resourceApi } from '@/api/resources'
import http from '@/api/http'
import { groupCategories } from '@/utils/categories'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const { t } = useI18n()
const formRef = ref()
const submitting = ref(false)
const catGroups = ref([])
const file = ref(null)
const fileInput = ref()

const form = reactive({
  title: '',
  categoryId: undefined,
  difficultyLevel: '入门',
  durationMinutes: 60,
  learningType: '视频',
  isFree: true,
  isPublic: true,
  description: '',
  videoUrl: '',
})

const rules = computed(() => ({
  title: [{ required: true, message: t('请输入资源标题'), trigger: 'blur' }],
  categoryId: [{ required: true, message: t('请选择分类'), trigger: 'change' }],
  description: [{ required: true, message: t('请输入资源简介'), trigger: 'blur' }],
}))

const difficultyOptions = ['入门', '初级', '中级', '高级']
const typeOptions = ['视频', '图文', '练习', '源码']

function onFileChange(uploadFile) {
  file.value = uploadFile.raw
}

async function submit() {
  await formRef.value.validate()
  if (!file.value) {
    ElMessage.warning(t('请上传资源文件'))
    return
  }
  submitting.value = true
  try {
    const fd = new FormData()
    Object.entries(form).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== '') fd.append(k, v)
    })
    fd.append('file', file.value)
    const created = await resourceApi.create(fd)
    ElMessage.success(t('提交成功！等待审核通过后即可在资源库展示'))
    router.push(`/resources/${created.id}`)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  catGroups.value = groupCategories(await http.get('/categories'))
})
</script>

<template>
  <div class="page-container narrow">
    <div class="page-title"><LineIcon name="upload" :size="19" /> {{ $t('提交学习资源') }}</div>
    <el-alert type="info" :closable="false" class="tip" show-icon
      :title="$t('提交后需管理员/审核员审核通过才会公开展示')"
      :description="$t('支持格式：PDF、Word、PPT、ZIP、RAR、Blend、OBJ、FBX、STL、DAE、3DS')" />

    <el-card class="form-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item :label="$t('标题')" prop="title">
          <el-input v-model="form.title" :placeholder="$t('例如：Blender 基础建模入门')" maxlength="100" />
        </el-form-item>
        <el-form-item :label="$t('分类')" prop="categoryId">
          <el-select v-model="form.categoryId" :placeholder="$t('选择分类')">
            <el-option-group v-for="g in catGroups" :key="g.parent.id" :label="g.parent.name">
              <el-option :label="g.parent.name + $t('（全部）')" :value="g.parent.id" />
              <el-option v-for="child in g.children" :key="child.id" :label="child.name" :value="child.id" />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('难度')">
          <el-select v-model="form.difficultyLevel">
            <el-option v-for="d in difficultyOptions" :key="d" :label="$t(d)" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('时长(分钟)')">
          <el-input-number v-model="form.durationMinutes" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item :label="$t('学习类型')">
          <el-select v-model="form.learningType">
            <el-option v-for="t in typeOptions" :key="t" :label="$t(t)" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('免费资源')">
          <el-switch v-model="form.isFree" />
        </el-form-item>
        <el-form-item :label="$t('公开可见')">
          <el-switch v-model="form.isPublic" />
        </el-form-item>
        <el-form-item :label="$t('视频链接')">
          <el-input v-model="form.videoUrl" :placeholder="$t('选填：B 站/YouTube 视频地址')" />
        </el-form-item>
        <el-form-item :label="$t('资源文件')">
          <input ref="fileInput" type="file" class="file-input" @change="onFileChange({ raw: $event.target.files[0] })" />
          <div class="text-muted file-tip">{{ $t('支持 PDF / Word / PPT / 压缩包 / 3D 文件，200MB 以内') }}</div>
        </el-form-item>
        <el-form-item :label="$t('简介')" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" maxlength="2000" show-word-limit
            :placeholder="$t('介绍资源的内容、适合人群、学习收获…')" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="submit">{{ $t('提交审核') }}</el-button>
          <el-button size="large" @click="router.back()">{{ $t('取消') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.narrow {
  max-width: 800px;
}
.tip {
  margin-bottom: 16px;
}
.file-input {
  font-size: 14px;
}
.file-tip {
  margin-top: 4px;
}
</style>
