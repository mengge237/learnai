import { createI18n } from 'vue-i18n'

/**
 * 全站中英文切换（中文 key 模式）：
 *  - 消息 key 直接用中文原文，如 $t('开始学习')；中文模式下无需词典（zh 消息值 = key 本身）
 *  - 英文词典按模块拆分在 en/*.js（每个文件 export default { '中文': 'English' }），
 *    由 import.meta.glob 自动合并，新增模块无需改本文件
 *  - 语言仅存本地（learnai_lang），与个性化偏好分开，不同步服务器
 */

const LANG_KEY = 'learnai_lang'

const en = {}
const enModules = import.meta.glob('./en/*.js', { eager: true })
for (const mod of Object.values(enModules)) {
  Object.assign(en, mod.default || mod)
}

const messages = {
  'zh-CN': Object.fromEntries(Object.keys(en).map((k) => [k, k])),
  'en-US': en,
}

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem(LANG_KEY) || 'zh-CN',
  fallbackLocale: 'zh-CN',
  missingWarn: false,
  fallbackWarn: false,
  messages,
})

/** 切换语言：更新 i18n、本地持久化、<html lang> */
export function setLocale(lang) {
  i18n.global.locale.value = lang
  localStorage.setItem(LANG_KEY, lang)
  document.documentElement.lang = lang === 'en-US' ? 'en' : 'zh-CN'
}

/** 当前语言是否为英文 */
export function isEnglish() {
  return i18n.global.locale.value === 'en-US'
}

export default i18n
