import { ref } from 'vue'

/**
 * 语音辅助（Web Speech API，无需后端/密钥）：
 *  - useTts：教程正文朗读（speechSynthesis，按标点切块防长句卡顿）
 *  - useVoiceInput：语音输入（webkit SpeechRecognition，Chrome/Edge）
 */

/** 按标点把长文本切块，避免浏览器 TTS 一次读太长被打断 */
function chunkText(text, maxLen = 160) {
  const parts = String(text).split(/(?<=[。！？；\n])/)
  const chunks = []
  let cur = ''
  for (const p of parts) {
    if ((cur + p).length > maxLen && cur.trim()) {
      chunks.push(cur)
      cur = p
    } else {
      cur += p
    }
  }
  if (cur.trim()) chunks.push(cur)
  return chunks
}

export function useTts() {
  const speaking = ref(false)
  const paused = ref(false)
  let queue = []
  let idx = 0

  const synth = typeof window !== 'undefined' && 'speechSynthesis' in window ? window.speechSynthesis : null
  const supported = !!synth

  /** 优先中文普通话嗓音（Edge/Chrome 的 Xiaoxiao/Huihui 等） */
  function pickVoice() {
    if (!synth) return null
    const voices = synth.getVoices()
    return (
      voices.find((v) => /^zh[-_]CN/i.test(v.lang) && /Microsoft|Xiaoxiao|Huihui|Ting-Ting|Google/i.test(v.name)) ||
      voices.find((v) => /^zh[-_]CN/i.test(v.lang)) ||
      voices.find((v) => /^zh/i.test(v.lang)) ||
      null
    )
  }

  function speakNext() {
    if (!synth || idx >= queue.length) {
      stop()
      return
    }
    const u = new SpeechSynthesisUtterance(queue[idx])
    const voice = pickVoice()
    if (voice) u.voice = voice
    u.lang = 'zh-CN'
    u.rate = 1
    u.onend = () => {
      idx += 1
      speakNext()
    }
    u.onerror = () => {
      idx += 1
      speakNext()
    }
    synth.speak(u)
  }

  function speak(text) {
    if (!synth || !text) return
    synth.cancel()
    queue = chunkText(text)
    idx = 0
    speaking.value = true
    paused.value = false
    speakNext()
  }

  function pause() {
    if (!synth || !speaking.value) return
    synth.pause()
    paused.value = true
  }

  function resume() {
    if (!synth || !paused.value) return
    synth.resume()
    paused.value = false
  }

  function stop() {
    if (synth) synth.cancel()
    queue = []
    idx = 0
    speaking.value = false
    paused.value = false
  }

  // Chrome 的 voices 异步加载，预热一次保证首次朗读就能选到中文嗓音
  if (synth && typeof synth.getVoices === 'function') {
    synth.getVoices()
    synth.onvoiceschanged = () => synth.getVoices()
  }

  return { supported, speaking, paused, speak, pause, resume, stop }
}

/** 语音输入（按住说话/点击开关），识别结果回调 */
export function useVoiceInput(onResult) {
  const listening = ref(false)
  const SR =
    typeof window !== 'undefined' ? window.SpeechRecognition || window.webkitSpeechRecognition : null
  const supported = !!SR
  let rec = null

  function start() {
    if (!SR || listening.value) return
    rec = new SR()
    rec.lang = 'zh-CN'
    rec.continuous = false
    rec.interimResults = false
    rec.maxAlternatives = 1
    rec.onresult = (e) => {
      const text = Array.from(e.results)
        .map((r) => r[0].transcript)
        .join('')
      if (text) onResult?.(text)
    }
    rec.onerror = () => {
      listening.value = false
    }
    rec.onend = () => {
      listening.value = false
    }
    try {
      rec.start()
      listening.value = true
    } catch {
      listening.value = false
    }
  }

  function stop() {
    if (rec) rec.stop()
    listening.value = false
  }

  return { supported, listening, start, stop }
}
