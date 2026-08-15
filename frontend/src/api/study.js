import http from './http'

/** 学习活动与激励：心跳上报、学习统计 */
export const studyApi = {
  /** 心跳：上报实际学习秒数，返回实时统计 */
  heartbeat: (data) => http.post('/study/heartbeat', data),
  /** 学习统计：今日/累计/连续打卡/周统计/学习状态 */
  stats: () => http.get('/study/stats'),
}
