import http from './http'

export const aiApi = {
  /** 对话历史 */
  history: () => http.get('/ai/history'),
  /** 发送消息（规则式答疑） */
  chat: (data) => http.post('/ai/chat', data),
  /** 智能推荐 */
  recommend: () => http.get('/ai/recommend'),
  /** 学习分析 */
  analytics: () => http.get('/ai/analytics'),
}
