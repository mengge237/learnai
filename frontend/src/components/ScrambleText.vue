<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * 乱码解码文字（HTML_TRIAL scramble 创意）：
 * 悬停时文字被随机符号覆盖，再逐字还原，工业科技感。
 * auto=true 时挂载后自动解码一次（首页入场动画）。
 */
const props = defineProps({
  text: { type: String, required: true },
  chars: { type: String, default: '#$%&*+=?<>/' },
  speed: { type: Number, default: 30 }, // 每帧毫秒
  duration: { type: Number, default: 900 }, // 解码总时长上限
  auto: { type: Boolean, default: false },
})

const el = ref(null)
let timer = null

function decode() {
  const target = props.text
  const len = target.length
  const step = Math.max(0.5, len / Math.max(1, Math.round(props.duration / props.speed)))
  let frame = 0
  clearInterval(timer)
  timer = setInterval(() => {
    if (!el.value) {
      clearInterval(timer)
      return
    }
    el.value.textContent = target
      .split('')
      .map((ch, i) => {
        if (ch === ' ') return ' '
        if (i < frame) return ch
        return props.chars[Math.floor(Math.random() * props.chars.length)]
      })
      .join('')
    frame += step
    if (frame >= len) {
      el.value.textContent = target
      clearInterval(timer)
    }
  }, props.speed)
}

onMounted(() => {
  if (props.auto) setTimeout(decode, 400)
})

onBeforeUnmount(() => clearInterval(timer))
</script>

<template>
  <span ref="el" class="scramble" @mouseenter="decode">{{ text }}</span>
</template>

<style scoped>
.scramble {
  display: inline-block;
  white-space: pre;
}
</style>
