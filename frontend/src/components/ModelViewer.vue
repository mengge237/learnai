<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import { OBJLoader } from 'three/addons/loaders/OBJLoader.js'

/**
 * 3D 模型在线查看器（Three.js 0.185）：
 * - glb / gltf → GLTFLoader；obj → OBJLoader
 * - SRGB 输出 + ACESFilmic 色调映射
 * - 自动取景（包围盒居中）+ 组件卸载时完整释放 GPU 资源
 */
const props = defineProps({
  src: { type: String, required: true }, // 模型文件 URL
  format: { type: String, required: true }, // glb | gltf | obj
})

const container = ref(null)
const status = ref('loading') // loading | ready | error
const errorMsg = ref('')

let renderer = null
let scene = null
let camera = null
let controls = null
let modelObject = null
let rafId = 0
let resizeObserver = null

function initScene() {
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true })
  renderer.outputColorSpace = THREE.SRGBColorSpace
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  container.value.appendChild(renderer.domElement)

  scene = new THREE.Scene()

  camera = new THREE.PerspectiveCamera(45, 1, 0.1, 1000)
  camera.position.set(3, 2.5, 4)

  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.08
  controls.target.set(0, 0, 0)

  scene.add(new THREE.HemisphereLight(0xffffff, 0x8a93a6, 1.6))
  const keyLight = new THREE.DirectionalLight(0xffffff, 2.2)
  keyLight.position.set(5, 8, 3)
  scene.add(keyLight)

  const grid = new THREE.GridHelper(8, 16, 0xb8c0cc, 0xdfe4ea)
  scene.add(grid)
}

/** 按包围盒自动取景，让模型居中且完整可见 */
function fitCamera() {
  const box = new THREE.Box3().setFromObject(modelObject)
  const center = box.getCenter(new THREE.Vector3())
  const size = box.getSize(new THREE.Vector3())
  const maxDim = Math.max(size.x, size.y, size.z) || 1

  const offset = controls.target.clone().sub(camera.position)
  const distance = offset.length()
  const fov = (camera.fov * Math.PI) / 180
  const fitDistance = (maxDim / 2 / Math.tan(fov / 2)) * 1.25

  camera.position.copy(center.clone().add(offset.normalize().multiplyScalar(fitDistance)))
  camera.near = Math.max(fitDistance / 100, 0.01)
  camera.far = fitDistance * 20
  camera.updateProjectionMatrix()
  controls.target.copy(center)
  controls.update()
}

function onLoaded(obj) {
  modelObject = obj
  obj.traverse((child) => {
    if (!child.isMesh) return
    // OBJ 占位文件没有法线，按面计算法线后平面着色更好看
    if (!child.geometry.getAttribute('normal')) child.geometry.computeVertexNormals()
    if (child.material) {
      child.material = new THREE.MeshStandardMaterial({
        color: 0x6d8df0,
        roughness: 0.4,
        metalness: 0.1,
        flatShading: true,
      })
    }
  })
  scene.add(obj)
  fitCamera()
  status.value = 'ready'
}

function onError(err) {
  console.error('[ModelViewer] 加载失败:', err)
  errorMsg.value = '模型文件加载失败'
  status.value = 'error'
}

function loadModel() {
  if (props.format === 'obj') {
    new OBJLoader().load(props.src, onLoaded, undefined, onError)
  } else {
    new GLTFLoader().load(props.src, (gltf) => onLoaded(gltf.scene), undefined, onError)
  }
}

function render() {
  if (!renderer) return
  const w = container.value.clientWidth || 1
  const h = container.value.clientHeight || 1
  renderer.setSize(w, h, false)
  camera.aspect = w / h
  camera.updateProjectionMatrix()
  renderer.render(scene, camera)
}

function animate() {
  rafId = requestAnimationFrame(animate)
  controls.update()
  renderer.render(scene, camera)
}

function dispose() {
  cancelAnimationFrame(rafId)
  resizeObserver?.disconnect()
  modelObject?.traverse((child) => {
    if (child.isMesh) {
      child.geometry?.dispose()
      if (Array.isArray(child.material)) child.material.forEach((m) => m.dispose())
      else child.material?.dispose()
    }
  })
  controls?.dispose()
  renderer?.dispose()
  renderer?.domElement.remove()
  renderer = null
}

onMounted(() => {
  initScene()
  loadModel()
  resizeObserver = new ResizeObserver(render)
  resizeObserver.observe(container.value)
  render()
  animate()
})

onUnmounted(dispose)
</script>

<template>
  <div class="viewer">
    <div ref="container" class="canvas" />
    <div v-if="status === 'loading'" class="overlay">
      <span class="spinner" />
      <span class="text-muted">模型加载中…</span>
    </div>
    <div v-if="status === 'error'" class="overlay">
      <span class="text-muted">{{ errorMsg }}，请下载后在本地软件中打开</span>
    </div>
    <div v-if="status === 'ready'" class="hint text-muted">🖱 拖拽旋转 · 滚轮缩放 · 右键平移</div>
  </div>
</template>

<style scoped>
.viewer {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 280px;
}
.canvas {
  width: 100%;
  height: 100%;
}
.canvas :deep(canvas) {
  display: block;
}
.overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: var(--el-fill-color-light);
}
.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid var(--border-color);
  border-top-color: var(--theme-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
.hint {
  position: absolute;
  left: 10px;
  bottom: 8px;
  font-size: 12px;
  pointer-events: none;
}
</style>
