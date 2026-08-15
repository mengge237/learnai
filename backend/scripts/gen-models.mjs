/**
 * 为 8 个种子模型生成各具形态的低多边形 OBJ 文件（线稿/平面着色风格），
 * 覆盖 seedModels 已写入 uploads/models/202608/*.obj 的占位立方体。
 * 用法：在 backend 目录执行  node scripts/gen-models.mjs
 */
import { writeFileSync, mkdirSync } from 'node:fs'
import { dirname, join, resolve } from 'node:path'

const UPLOADS = resolve('uploads')
const f = (n) => Number(n.toFixed(4))

class Mesh {
  constructor() {
    this.v = []
    this.vn = []
    this.faces = []
  }
  /** 添加一组顶点并返回起始索引（1-based） */
  addVerts(verts) {
    const start = this.v.length + 1
    for (const [x, y, z] of verts) this.v.push([f(x), f(y), f(z)])
    return start
  }
  /** 按顶点索引添加多边形面（自动计算面法线，平面着色） */
  face(indices) {
    const pts = indices.map((i) => this.v[i - 1])
    const [a, b, c] = [pts[0], pts[1], pts[2]]
    const u = [b[0] - a[0], b[1] - a[1], b[2] - a[2]]
    const w = [c[0] - a[0], c[1] - a[1], c[2] - a[2]]
    let n = [u[1] * w[2] - u[2] * w[1], u[2] * w[0] - u[0] * w[2], u[0] * w[1] - u[1] * w[0]]
    const len = Math.hypot(...n) || 1
    n = [f(n[0] / len), f(n[1] / len), f(n[2] / len)]
    this.vn.push(n)
    const nIdx = this.vn.length
    this.faces.push(indices.map((i) => `${i}//${nIdx}`).join(' '))
  }
  /** 长方体（轴对齐，带平面着色面） */
  box(cx, cy, cz, w, h, d) {
    const [x0, x1, y0, y1, z0, z1] = [cx - w / 2, cx + w / 2, cy - h / 2, cy + h / 2, cz - d / 2, cz + d / 2]
    const s = this.addVerts([
      [x0, y0, z0], [x1, y0, z0], [x1, y1, z0], [x0, y1, z0],
      [x0, y0, z1], [x1, y0, z1], [x1, y1, z1], [x0, y1, z1],
    ])
    this.face([s, s + 3, s + 2, s + 1]) // front
    this.face([s + 4, s + 5, s + 6, s + 7]) // back
    this.face([s, s + 4, s + 7, s + 3]) // left
    this.face([s + 1, s + 2, s + 6, s + 5]) // right
    this.face([s + 3, s + 7, s + 6, s + 2]) // top
    this.face([s, s + 1, s + 5, s + 4]) // bottom
  }
  /** 圆柱 / 圆台（y 轴，segments 面） */
  cylinder(cx, cy, cz, rTop, rBottom, h, seg = 8) {
    const y0 = cy - h / 2
    const y1 = cy + h / 2
    const topC = this.addVerts([[cx, y1, cz]])
    const botC = this.addVerts([[cx, y0, cz]])
    const top = []
    const bot = []
    for (let i = 0; i < seg; i++) {
      const a = (i / seg) * Math.PI * 2
      top.push([cx + rTop * Math.cos(a), y1, cz + rTop * Math.sin(a)])
      bot.push([cx + rBottom * Math.cos(a), y0, cz + rBottom * Math.sin(a)])
    }
    const st = this.addVerts(top)
    const sb = this.addVerts(bot)
    for (let i = 0; i < seg; i++) {
      const j = (i + 1) % seg
      this.face([st + i, st + j, sb + j, sb + i]) // side
      this.face([st + i, st + j, topC]) // top cap
      this.face([sb + j, sb + i, botC]) // bottom cap
    }
  }
  /** 圆锥（y 轴，apex 在上） */
  cone(cx, cy, cz, r, h, seg = 8) {
    const y0 = cy - h / 2
    const apex = this.addVerts([[cx, cy + h / 2, cz]])
    const botC = this.addVerts([[cx, y0, cz]])
    const bot = []
    for (let i = 0; i < seg; i++) {
      const a = (i / seg) * Math.PI * 2
      bot.push([cx + r * Math.cos(a), y0, cz + r * Math.sin(a)])
    }
    const sb = this.addVerts(bot)
    for (let i = 0; i < seg; i++) {
      const j = (i + 1) % seg
      this.face([apex, sb + i, sb + j]) // side
      this.face([sb + j, sb + i, botC]) // bottom cap
    }
  }
  /** 低多边形球体（环 x 段） */
  sphere(cx, cy, cz, r, rings = 3, seg = 6) {
    const verts = []
    const ringYs = []
    for (let i = 0; i <= rings; i++) {
      const phi = (i / rings) * Math.PI
      ringYs.push(Math.cos(phi))
      for (let j = 0; j < seg; j++) {
        const th = (j / seg) * Math.PI * 2
        verts.push([cx + r * Math.sin(phi) * Math.cos(th), cy + r * ringYs[i], cz + r * Math.sin(phi) * Math.sin(th)])
      }
    }
    const s = this.addVerts(verts)
    for (let i = 0; i < rings; i++) {
      for (let j = 0; j < seg; j++) {
        const j2 = (j + 1) % seg
        const a = s + i * seg + j
        const b = s + i * seg + j2
        const c = s + (i + 1) * seg + j2
        const d = s + (i + 1) * seg + j
        if (i === 0) this.face([d, c, b]) // 顶部三角
        else if (i === rings - 1) this.face([a, b, c]) // 底部三角
        else this.face([a, b, c, d])
      }
    }
  }
  /** 三棱柱屋顶（沿 x 轴，屋脊在 z=cz 平面上方） */
  roofPrism(cx, cy, cz, w, baseH, h, d) {
    const [x0, x1] = [cx - w / 2, cx + w / 2]
    const yBase = cy - baseH / 2
    const yApex = cy + h
    const z0 = cz - d / 2
    const z1 = cz + d / 2
    const s = this.addVerts([
      [x0, yBase, z0], [x1, yBase, z0], [x1, yApex, z0], [x0, yApex, z0],
      [x0, yBase, z1], [x1, yBase, z1], [x1, yApex, z1], [x0, yApex, z1],
    ])
    this.face([s, s + 3, s + 2, s + 1]) // 前坡面
    this.face([s + 4, s + 5, s + 6, s + 7]) // 后坡面
    this.face([s + 1, s + 2, s + 6, s + 5]) // 右侧山墙
    this.face([s, s + 4, s + 7, s + 3]) // 左侧山墙
  }
  toObj() {
    const lines = ['# AI智学 演示模型（程序生成，平面着色）', '#']
    for (const [x, y, z] of this.v) lines.push(`v ${x} ${y} ${z}`)
    for (const [x, y, z] of this.vn) lines.push(`vn ${x} ${y} ${z}`)
    for (const fv of this.faces) lines.push(`f ${fv}`)
    return lines.join('\n') + '\n'
  }
}

// ---------- 8 个模型（坐标 z 为朝向，地面 y=0） ----------

function girl() {
  const m = new Mesh()
  m.sphere(0, 7.4, 0, 1.05, 4, 8) // 头
  m.box(0, 5.6, 0, 0.5, 0.5, 0.5) // 脖子
  m.box(0, 4.3, 0, 2.2, 2.4, 1.2) // 躯干
  m.cylinder(0, 2.2, 0, 0.7, 1.9, 1.4, 8) // 裙摆
  m.box(-1.75, 4.3, 0, 0.5, 2.1, 0.5) // 左臂
  m.box(1.75, 4.3, 0, 0.5, 2.1, 0.5) // 右臂
  m.box(-0.55, 0.9, 0, 0.6, 1.8, 0.6) // 左腿
  m.box(0.55, 0.9, 0, 0.6, 1.8, 0.6) // 右腿
  m.cylinder(0, 7.4, 0.95, 0.05, 0.05, 1.9, 6) // 马尾辫（向后）
  return m
}

function warrior() {
  const m = new Mesh()
  m.box(0, 8.1, 0, 1.4, 1.4, 1.4) // 头
  m.cone(0, 9.5, 0, 0.95, 0.9, 6) // 头盔
  m.box(0, 5.7, 0, 2.7, 3.0, 1.5) // 躯干
  m.box(-2.05, 5.9, 0, 0.6, 2.2, 0.6) // 左臂
  m.box(2.05, 5.9, 0, 0.6, 2.2, 0.6) // 右臂
  m.box(-0.75, 2.1, 0, 0.7, 2.2, 0.7) // 左腿
  m.box(0.75, 2.1, 0, 0.7, 2.2, 0.7) // 右腿
  m.box(-2.75, 5.4, 0.5, 0.3, 2.3, 1.7) // 盾牌
  m.box(3.6, 6.2, 0, 0.25, 3.6, 0.25) // 剑
  m.box(3.6, 4.2, 0, 0.6, 0.35, 0.35) // 剑格
  return m
}

function villa() {
  const m = new Mesh()
  m.box(0, 1.5, 0, 7, 3, 5) // 主体
  m.roofPrism(0, 4.6, 0, 7.8, 3, 1.6, 5.8) // 屋顶
  m.box(2.3, 4.9, 0.9, 0.7, 1.3, 0.7) // 烟囱
  m.box(0, 0.9, 2.51, 1.1, 1.8, 0.12) // 门
  m.box(-2.2, 1.9, 2.51, 1.0, 1.0, 0.1) // 左窗
  m.box(2.2, 1.9, 2.51, 1.0, 1.0, 0.1) // 右窗
  m.box(-4.2, 0.3, -1.2, 0.6, 0.6, 0.6) // 左柱
  m.box(4.2, 0.3, -1.2, 0.6, 0.6, 0.6) // 右柱
  return m
}

function fighter() {
  const m = new Mesh()
  m.box(0, 0, 0, 1.7, 1.2, 7.6) // 机身
  m.cone(0, 0, 4.55, 0.85, 1.5, 6) // 机头（z+ 指向）
  m.box(3.3, 0, -1.0, 4.6, 0.24, 2.6) // 右主翼
  m.box(-3.3, 0, -1.0, 4.6, 0.24, 2.6) // 左主翼
  m.box(1.35, 0, -4.1, 1.8, 0.22, 1.5) // 右尾翼
  m.box(-1.35, 0, -4.1, 1.8, 0.22, 1.5) // 左尾翼
  m.box(0, 1.05, -4.2, 0.24, 1.5, 1.5) // 垂尾
  return m
}

function robot() {
  const m = new Mesh()
  m.box(0, 7.2, 0, 2.1, 2.1, 2.1) // 头
  m.box(-0.6, 7.4, 1.06, 0.5, 0.5, 0.15) // 左眼
  m.box(0.6, 7.4, 1.06, 0.5, 0.5, 0.15) // 右眼
  m.box(0, 8.9, 0, 0.18, 1.2, 0.18) // 天线
  m.sphere(0, 9.6, 0, 0.3, 3, 6) // 天线头
  m.box(0, 4.6, 0, 3.1, 2.7, 2.1) // 躯干
  m.box(-2.3, 5.5, 0, 0.9, 0.9, 0.9) // 左肩
  m.box(2.3, 5.5, 0, 0.9, 0.9, 0.9) // 右肩
  m.box(-2.3, 3.5, 0, 0.7, 2.1, 0.7) // 左臂
  m.box(2.3, 3.5, 0, 0.7, 2.1, 0.7) // 右臂
  m.box(-0.85, 1.5, 0, 0.9, 2.1, 0.9) // 左腿
  m.box(0.85, 1.5, 0, 0.9, 2.1, 0.9) // 右腿
  m.cylinder(0, 4.6, 1.15, 0.35, 0.35, 0.25, 8) // 胸口表盘
  return m
}

function fox() {
  const m = new Mesh()
  m.box(0, 1.2, -0.4, 2.1, 1.5, 3.6) // 身体
  m.box(0, 2.5, 1.6, 1.7, 1.5, 1.8) // 头
  m.cone(-0.55, 3.9, 1.7, 0.35, 0.9, 5) // 左耳
  m.cone(0.55, 3.9, 1.7, 0.35, 0.9, 5) // 右耳
  m.cone(0, 1.6, 2.75, 0.25, 0.8, 5) // 鼻子
  m.cylinder(0, 1.9, -2.5, 0.28, 0.28, 1.3, 6) // 尾巴
  m.box(-0.8, 0.4, 1.2, 0.5, 0.8, 0.5) // 前左腿
  m.box(0.8, 0.4, 1.2, 0.5, 0.8, 0.5) // 前右腿
  m.box(-0.8, 0.4, -1.6, 0.5, 0.8, 0.5) // 后左腿
  m.box(0.8, 0.4, -1.6, 0.5, 0.8, 0.5) // 后右腿
  return m
}

function forest() {
  const m = new Mesh()
  m.box(0, -0.25, 0, 12, 0.5, 12) // 地面
  const trees = [
    [-3.5, 2.6, -2.5, 1.3, 1.9],
    [3.8, 3.1, -3.2, 1.6, 2.3],
    [-4.2, 2.2, 3.4, 1.1, 1.6],
    [3.0, 3.6, 3.8, 1.9, 2.7],
    [0, 4.4, -4.4, 2.4, 3.4],
  ]
  for (const [x, z, trunkH, r, h] of trees) {
    m.cylinder(x, trunkH / 2, z, 0.25, 0.4, trunkH, 6) // 树干
    m.cone(x, trunkH + h / 2, z, r, h, 7) // 树冠
  }
  m.box(4.6, 0.5, 4.6, 1.4, 1.0, 1.4) // 石头
  return m
}

function garden() {
  const m = new Mesh()
  m.box(0, 0.3, 0, 8.4, 0.6, 8.4) // 台基
  m.box(0, 2.0, 0, 3.2, 1.7, 3.2) // 一层
  m.box(0, 3.2, 0, 4.8, 0.45, 4.8) // 一层飞檐
  m.box(0, 4.4, 0, 2.3, 1.3, 2.3) // 二层
  m.box(0, 5.4, 0, 3.5, 0.4, 3.5) // 二层飞檐
  m.box(0, 6.3, 0, 1.4, 1.0, 1.4) // 三层
  m.box(0, 7.1, 0, 2.3, 0.32, 2.3) // 三层飞檐
  m.cone(0, 8.1, 0, 0.35, 0.9, 6) // 塔尖
  m.box(0, 0.9, -3.4, 8.4, 1.6, 0.5) // 后墙
  m.box(-4.05, 0.9, 0, 0.5, 1.6, 5.5) // 左墙
  m.box(4.05, 0.9, 0, 0.5, 1.6, 5.5) // 右墙
  m.box(-2.4, 0.9, 3.4, 1.6, 1.6, 0.5) // 前墙左
  m.box(2.4, 0.9, 3.4, 1.6, 1.6, 0.5) // 前墙右（中间留门）
  m.box(0, 1.5, 3.4, 0.8, 0.5, 0.3) // 门楣
  return m
}

const models = [
  ['137cd747.obj', girl()],
  ['7591ae50.obj', warrior()],
  ['1d632532.obj', villa()],
  ['f2c402c8.obj', fighter()],
  ['e837c731.obj', robot()],
  ['8118d67e.obj', fox()],
  ['6f37a91a.obj', forest()],
  ['597362c6.obj', garden()],
]

for (const [file, mesh] of models) {
  const p = join(UPLOADS, 'models', '202608', file)
  mkdirSync(dirname(p), { recursive: true })
  writeFileSync(p, mesh.toObj(), 'utf8')
  console.log('written:', p, `(${mesh.v.length} vertices)`)
}
