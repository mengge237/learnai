import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'

/**
 * axios 实例：baseURL '/api'（Vite 代理到后端），自动携带 Bearer token；
 * 401 清空登录态并跳转登录页，400/403 等以中文提示。
 */
const http = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

http.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const status = err.response?.status
    const message = err.response?.data?.message || '网络请求失败，请稍后重试'
    if (status === 401) {
      const auth = useAuthStore()
      auth.logout()
      const current = router.currentRoute.value
      if (current.name !== 'login') {
        router.push({ name: 'login', query: { redirect: current.fullPath } })
      }
      ElMessage.error('登录已过期，请重新登录')
    } else if (status === 403) {
      ElMessage.error('没有权限执行此操作')
    } else if (status === 400) {
      ElMessage.error(message)
    } else if (status >= 500) {
      ElMessage.error('服务器开小差了，请稍后重试')
    }
    return Promise.reject(err)
  }
)

/**
 * 下载文件：携带 token 请求二进制，前端触发保存（支持中文文件名）。
 */
export async function downloadFile(url, fallbackName = 'download') {
  const auth = useAuthStore()
  const res = await axios.get(url, {
    baseURL: '',
    responseType: 'blob',
    headers: auth.token ? { Authorization: `Bearer ${auth.token}` } : {},
  })
  const disposition = res.headers['content-disposition'] || ''
  const match = disposition.match(/filename\*=UTF-8''([^;]+)/)
  const name = match ? decodeURIComponent(match[1]) : fallbackName
  const link = document.createElement('a')
  link.href = URL.createObjectURL(res.data)
  link.download = name
  link.click()
  URL.revokeObjectURL(link.href)
}

export default http
