import http from './http'

export const pathApi = {
  /** 学习路径列表 */
  list: (params) => http.get('/paths', { params }),
  /** 路径详情（含排序资源） */
  detail: (id) => http.get(`/paths/${id}`),
  /** 我的报名路径 */
  my: () => http.get('/paths/my'),
  /** 报名（幂等） */
  enroll: (id) => http.post(`/paths/${id}/enroll`),
  /** 管理员：创建路径 */
  create: (data) => http.post('/paths', data),
  /** 管理员：更新路径 */
  update: (id, data) => http.put(`/paths/${id}`, data),
  /** 管理员：删除路径 */
  remove: (id) => http.delete(`/paths/${id}`),
  /** 管理员：替换路径资源序列 */
  updateResources: (id, data) => http.put(`/paths/${id}/resources`, data),
}
