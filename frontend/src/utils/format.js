import i18n from '@/i18n'

/** 格式化后端 LocalDateTime（"yyyy-MM-dd HH:mm:ss"）为 "YYYY-MM-DD HH:mm" */
export function formatDate(str) {
  if (!str) return '-'
  const s = String(str).replace('T', ' ')
  return s.length >= 16 ? s.slice(0, 16) : s
}

/** 价格：¥ 保留两位小数 */
export function formatPrice(v) {
  const n = Number(v ?? 0)
  return n === Math.floor(n) ? `¥${n}` : `¥${n.toFixed(2)}`
}

/** 大数字缩写：3560 → 3.6k；中文模式 12345 → 1.2万，英文模式 12345 → 12.3k */
export function formatCount(n) {
  const num = Number(n ?? 0)
  const en = i18n.global.locale.value === 'en-US'
  if (num >= 10000) return en ? `${(num / 1000).toFixed(1)}k` : `${(num / 10000).toFixed(1)}万`
  if (num >= 1000) return `${(num / 1000).toFixed(1)}k`
  return String(num)
}

export const ORDER_STATUS = {
  PendingPayment: '待支付',
  Pending: '待处理',
  Processing: '处理中',
  Shipped: '已发货',
  Completed: '已完成',
  Cancelled: '已取消',
}

export const ORDER_TAG = {
  PendingPayment: 'warning',
  Pending: 'primary',
  Processing: 'primary',
  Shipped: 'info',
  Completed: 'success',
  Cancelled: 'danger',
}

export const LEARNING_STATUS = {
  NotStarted: '未开始',
  InProgress: '进行中',
  Paused: '已暂停',
  Completed: '已完成',
  Abandoned: '已放弃',
}

export const LEARNING_TAG = {
  NotStarted: 'info',
  InProgress: 'primary',
  Paused: 'warning',
  Completed: 'success',
  Abandoned: 'danger',
}

export const STEP_STATUS = {
  NotStarted: '未开始',
  InProgress: '进行中',
  Completed: '已完成',
  Skipped: '已跳过',
}

/** 路径难度等级（Integer 1-3） */
export const PATH_DIFFICULTY = { 1: '入门', 2: '进阶', 3: '高级' }

/** 学习路径报名状态 */
export const PATH_STATUS = {
  NotStarted: '未开始',
  InProgress: '进行中',
  Paused: '已暂停',
  Completed: '已完成',
}

export const PATH_TAG = {
  NotStarted: 'info',
  InProgress: 'primary',
  Paused: 'warning',
  Completed: 'success',
}

export const LICENSE_TYPES = ['个人', '商用', '教育']
