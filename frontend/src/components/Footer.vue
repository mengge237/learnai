<template>
  <footer class="footer">
    <div class="footer-inner">
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
    </div>
    <div class="copyright">© 2026 {{ $t('AI智学 · 校园学习平台') }} · {{ $t('仅供学习交流使用') }}</div>
  </footer>
</template>

<style scoped>
.footer {
  background: var(--el-bg-color);
  border-top: 1px solid var(--border-color);
  padding: 32px 16px 16px;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 32px;
}
.footer-col {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.footer-col h4 {
  margin: 0 0 8px;
  font-size: 15px;
}
.footer-col a {
  color: var(--el-text-color-regular);
  font-size: 14px;
}
.footer-col a:hover {
  color: var(--theme-color);
}
.copyright {
  text-align: center;
  margin-top: 24px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
@media (max-width: 700px) {
  .footer-inner {
    grid-template-columns: 1fr;
  }
}
</style>
