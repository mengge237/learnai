import http from './http'

export const resourceApi = {
  /** 资源列表（sort: newest/popular/views） */
  list: (params) => http.get('/resources', { params }),
  /** 资源详情（浏览量 +1） */
  detail: (id) => http.get(`/resources/${id}`),
  /** 我的学习记录 */
  myLearning: () => http.get('/resources/my-learning'),
  /** 提交资源（multipart） */
  create: (formData) => http.post('/resources', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  /** 点赞 */
  like: (id) => http.post(`/resources/${id}/like`),
  /** 下载资源文件（内部走 downloadFile 携带 token） */
  downloadUrl: (id) => `/api/resources/${id}/download`,
  /** 学习进度详情（含步骤） */
  getProgress: (resourceId) => http.get(`/resources/${resourceId}/learn`),
  /** 开始学习 */
  startLearn: (resourceId) => http.post(`/resources/${resourceId}/learn/start`),
  /** 更新进度 */
  updateProgress: (resourceId, data) => http.put(`/resources/${resourceId}/learn/progress`, data),
  /** 完成学习 */
  complete: (resourceId, data) => http.post(`/resources/${resourceId}/learn/complete`, data),
  /** 更新步骤状态 */
  updateStep: (resourceId, stepNumber, data) =>
    http.put(`/resources/${resourceId}/learn/steps/${stepNumber}`, data),
}
