<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * 全局十字准星光标（HTML_TRIAL cursor 创意，工业线稿风）：
 * 跟随鼠标的四角括号 + 中心点，不隐藏系统光标；
 * 悬停可交互元素时放大，仅指针设备（鼠标）启用。
 */
const el = ref(null)
const enabled = window.matchMedia?.('(pointer: fine)')?.matches ?? false

let mx = -100
let my = -100 // 目标位置
let cx = -100
let cy = -100 // 当前（插值）位置
let cur = 1
let target = 1
let rafId = 0

const INTERACTIVE = 'a, button, input, textarea, select, label, [role="button"], .el-tag'

function onMove(e) {
  mx = e.clientX
  my = e.clientY
  if (cx < -50) {
    cx = mx
    cy = my
  }
}

function onOver(e) {
  target = e.target.closest?.(INTERACTIVE) ? 1.6 : 1
}

function onLeave() {
  mx = -100
  my = -100
}

function loop() {
  cx += (mx - cx) * 0.18
  cy += (my - cy) * 0.18
  cur += (target - cur) * 0.16
  if (el.value) {
    el.value.style.transform = `translate3d(${cx.toFixed(1)}px, ${cy.toFixed(1)}px, 0) scale(${cur.toFixed(3)})`
    el.value.style.opacity = mx < -50 ? '0' : '1'
  }
  rafId = requestAnimationFrame(loop)
}

onMounted(() => {
  if (!enabled) return
  window.addEventListener('mousemove', onMove, { passive: true })
  window.addEventListener('mouseover', onOver, { passive: true })
  document.documentElement.addEventListener('mouseleave', onLeave)
  rafId = requestAnimationFrame(loop)
})

onBeforeUnmount(() => {
  window.removeEventListener('mousemove', onMove)
  window.removeEventListener('mouseover', onOver)
  document.documentElement.removeEventListener('mouseleave', onLeave)
  cancelAnimationFrame(rafId)
})
</script>

<template>
  <div v-if="enabled" ref="el" class="cursor-reticle" aria-hidden="true">
    <i class="c1" /><i class="c2" /><i class="c3" /><i class="c4" />
    <span class="c-dot" />
  </div>
</template>

<style>
.cursor-reticle {
  position: fixed;
  left: 0;
  top: 0;
  width: 20px;
  height: 20px;
  margin: -10px 0 0 -10px;
  pointer-events: none;
  z-index: 9999;
  opacity: 0;
  transition: opacity 0.2s;
}
.cursor-reticle i {
  position: absolute;
  width: 7px;
  height: 7px;
  border: 1px solid var(--theme-color);
}
.cursor-reticle .c1 { left: 0; top: 0; border-right: 0; border-bottom: 0; }
.cursor-reticle .c2 { right: 0; top: 0; border-left: 0; border-bottom: 0; }
.cursor-reticle .c3 { left: 0; bottom: 0; border-right: 0; border-top: 0; }
.cursor-reticle .c4 { right: 0; bottom: 0; border-left: 0; border-top: 0; }
.cursor-reticle .c-dot {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 2px;
  height: 2px;
  margin: -1px 0 0 -1px;
  background: var(--theme-color);
}
</style>
