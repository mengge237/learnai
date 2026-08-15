import http from './http'

export const adminApi = {
  /** 用户管理列表 */
  users: (params) => http.get('/admin/users', { params }),
  /** 更新用户（角色/状态） */
  updateUser: (id, data) => http.put(`/admin/users/${id}`, data),
  /** 平台统计 */
  stats: () => http.get('/admin/stats'),
  /** 待审核资源 */
  pendingResources: () => http.get('/audit/resources'),
  /** 审核资源 */
  reviewResource: (id, data) => http.post(`/audit/resources/${id}/review`, data),
  /** 待审核模型 */
  pendingModels: () => http.get('/audit/models'),
  /** 审核模型 */
  reviewModel: (id, data) => http.post(`/audit/models/${id}/review`, data),
}
