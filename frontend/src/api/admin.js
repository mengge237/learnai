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
  /** 审核工作台统计 */
  auditStats: () => http.get('/audit/stats'),
  /** 审核历史（type: resources / models） */
  auditHistory: (type, params) => http.get(`/audit/history/${type}`, { params }),
  /** 全部资源（status: all/pending/approved/rejected） */
  resources: (params) => http.get('/admin/resources', { params }),
  /** 资源上架/下架 */
  toggleResourcePublic: (id, isPublic) => http.put(`/admin/resources/${id}/public`, { isPublic }),
  /** 删除资源 */
  deleteResource: (id) => http.delete(`/admin/resources/${id}`),
  /** 全部模型（status: all/pending/approved/rejected） */
  models: (params) => http.get('/admin/models', { params }),
  /** 模型上架/下架 */
  toggleModelPublic: (id, isPublic) => http.put(`/admin/models/${id}/public`, { isPublic }),
  /** 删除模型 */
  deleteModel: (id) => http.delete(`/admin/models/${id}`),
  /** 全部订单（可按状态筛选） */
  orders: (params) => http.get('/admin/orders', { params }),
  /** 全部分类（含停用） */
  categories: () => http.get('/admin/categories'),
  /** 新增分类 */
  createCategory: (data) => http.post('/admin/categories', data),
  /** 编辑分类 */
  updateCategory: (id, data) => http.put(`/admin/categories/${id}`, data),
  /** 删除分类 */
  deleteCategory: (id) => http.delete(`/admin/categories/${id}`),
}
