import http from './http'

/**
 * 全局搜索：跨学习资源 / 学习路径 / 3D 模型
 */
export const searchApi = {
  /** keyword 为空返回空分组 */
  search(keyword) {
    return http.get('/search', { params: { keyword: keyword || '' } })
  },
}
