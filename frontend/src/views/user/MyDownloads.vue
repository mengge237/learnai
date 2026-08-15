<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { interactionApi } from '@/api/interaction'
import { downloadFile } from '@/api/http'
import { resourceApi } from '@/api/resources'
import { marketApi } from '@/api/market'
import { formatDate } from '@/utils/format'
import LineIcon from '@/components/LineIcon.vue'

const router = useRouter()
const items = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    items.value = await interactionApi.downloads()
  } finally {
    loading.value = false
  }
})

function reDownload(it) {
  const url = it.type === 'resource' ? resourceApi.downloadUrl(it.targetId) : marketApi.downloadUrl(it.targetId)
  downloadFile(url, it.title)
}
</script>

<template>
  <div class="page-container">
    <div class="page-title"><LineIcon name="download" :size="19" /> 我的下载</div>

    <el-empty v-if="!loading && items.length === 0" description="还没有下载记录" />

    <el-card v-else class="table-card">
      <el-table :data="items" v-loading="loading">
        <el-table-column label="内容" min-width="240">
          <template #default="{ row }">
            <el-link type="primary" @click="router.push(row.type === 'resource' ? `/resources/${row.targetId}` : `/market/${row.targetId}`)">
              {{ row.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.type === 'resource' ? 'success' : 'warning'" effect="plain">
              {{ row.type === 'resource' ? '学习资源' : '3D 模型' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下载时间" width="160">
          <template #default="{ row }">{{ formatDate(row.downloadTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="reDownload(row)">再次下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.table-card {
  max-width: 900px;
}
</style>
