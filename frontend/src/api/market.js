import http from './http'

export const marketApi = {
  /** 模型目录（sort: newest/priceAsc/priceDesc） */
  listModels: (params) => http.get('/models', { params }),
  /** 模型详情 */
  modelDetail: (id) => http.get(`/models/${id}`),
  /** 提交模型（multipart） */
  createModel: (formData) => http.post('/models', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  /** 下载模型文件 */
  downloadUrl: (id) => `/api/models/${id}/download`,
  /** 模型分类 */
  modelCategories: () => http.get('/model-categories'),
  /** 创建订单（服务端计价） */
  createOrder: (data) => http.post('/orders', data),
  /** 我的订单 */
  myOrders: () => http.get('/orders/my'),
  /** 订单详情 */
  orderDetail: (id) => http.get(`/orders/${id}`),
  /** 模拟支付 */
  pay: (id) => http.post(`/orders/${id}/pay`),
  /** 取消订单 */
  cancel: (id) => http.post(`/orders/${id}/cancel`),
  /** 管理员：推进订单状态 */
  updateStatus: (id, data) => http.put(`/orders/${id}/status`, data),
}
