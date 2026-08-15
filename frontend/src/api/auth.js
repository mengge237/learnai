import http from './http'

export const authApi = {
  /** 登录 → { token, user } */
  login: (data) => http.post('/auth/login', data),
  /** 注册（服务端强制普通用户角色） */
  register: (data) => http.post('/auth/register', data),
  /** 当前用户信息 */
  me: () => http.get('/auth/me'),
  /** 更新个人资料 */
  updateProfile: (data) => http.put('/users/me', data),
  /** 修改密码 */
  changePassword: (data) => http.put('/users/me/password', data),
  /** 保存界面偏好 */
  updatePreferences: (data) => http.put('/users/me/preferences', data),
}
