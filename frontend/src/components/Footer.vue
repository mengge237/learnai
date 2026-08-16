<template>
  <footer class="footer">
    <!-- 工业警示条：页脚顶部橙色斜纹带 -->
    <div class="hazard" />
    <div class="footer-inner">
      <div class="footer-plate rivets">
        <div class="footer-col">
          <h4>{{ $t('AI智学 · 校园学习平台') }}</h4>
          <p class="text-muted">{{ $t('面向 3D 建模与图形开发学习者的在线学习平台，提供系统化课程、学习路径、在线答疑与 3D 模型资源。') }}</p>
        </div>
        <div class="footer-col">
          <h4>{{ $t('快速链接') }}</h4>
          <router-link to="/resources">{{ $t('学习资源') }}</router-link>
          <router-link to="/paths">{{ $t('学习路径') }}</router-link>
          <router-link to="/market">{{ $t('模型资源库') }}</router-link>
          <router-link to="/ai/chat">{{ $t('在线答疑') }}</router-link>
        </div>
        <div class="footer-col">
          <h4>{{ $t('关于本站') }}</h4>
          <p class="text-muted">{{ $t('学校课程设计项目') }}<br />{{ $t('Spring Boot + Vue 3 前后端分离架构') }}</p>
        </div>
        <div class="footer-col footer-tag">
          <span class="barcode" aria-hidden="true" />
          <span class="ft-mono">AIZHIXUE // EST.2026</span>
          <span class="ft-mono">MODEL · LEARN · CREATE</span>
        </div>
      </div>
      <div class="copyright">© 2026 {{ $t('AI智学 · 校园学习平台') }} · {{ $t('仅供学习交流使用') }}</div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background: var(--el-color-primary);
  color: var(--el-bg-color);
  padding: 0 16px 16px;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
}
/* 机械铭牌面板：细边框 + 四角铆钉（铆钉颜色随文字色自适应明暗） */
.footer-plate {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 32px;
  border: 1px solid color-mix(in srgb, currentColor 18%, transparent);
  padding: 28px 28px 20px;
  margin-top: 28px;
}
.footer-col {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.footer-col h4 {
  margin: 0 0 8px;
  font-size: 14px;
  letter-spacing: 3px;
  padding-left: 10px;
  border-left: 3px solid var(--theme-color);
}
.footer-col a {
  color: color-mix(in srgb, currentColor 72%, transparent);
  font-size: 14px;
  letter-spacing: 1px;
  transition: color 0.15s;
}
.footer-col a:hover {
  color: var(--theme-color);
}
.footer .text-muted {
  color: color-mix(in srgb, currentColor 55%, transparent);
}
.footer-tag {
  align-items: stretch;
}
.footer-tag .barcode {
  opacity: 0.6;
  margin-bottom: 6px;
}
.ft-mono {
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 10px;
  letter-spacing: 2px;
  color: color-mix(in srgb, currentColor 45%, transparent);
}
.copyright {
  text-align: center;
  margin-top: 18px;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 12px;
  letter-spacing: 2px;
  color: color-mix(in srgb, currentColor 50%, transparent);
}
@media (max-width: 700px) {
  .footer-plate {
    grid-template-columns: 1fr;
  }
}
</style>
