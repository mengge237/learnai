import http from './http'

export const interactionApi = {
  /** 我的收藏（资源+模型合并） */
  favorites: () => http.get('/favorites'),
  /** 收藏/取消收藏切换 → { favorited } */
  toggleFavorite: (data) => http.post('/favorites/toggle', data),
  /** 评论列表（树形） */
  comments: (params) => http.get('/comments', { params }),
  /** 发表评论 */
  addComment: (data) => http.post('/comments', data),
  /** 我的下载历史 */
  downloads: () => http.get('/downloads'),
}
