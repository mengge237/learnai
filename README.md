# AI智学 · 校园学习平台

一个面向 3D 建模学习者的前后端分离教育类网站（类似菜鸟教程），包含学习资源、学习路径、学习进度、AI 智能答疑、模型资源库（含模拟支付）等完整功能。

> 本项目由旧版 ASP.NET MVC 5 单体应用（`E:\NET Program\模型实现`）重构而来。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Spring Boot 4.0.7 · Java 21 · Spring Security（JWT 无状态认证）· JPA/Hibernate · MySQL 8.0 |
| 前端 | Vue 3 · Vite · Pinia · Vue Router · Axios · Element Plus · Three.js（3D 在线预览） |
| 数据库 | MySQL 8.0（库名 `learnai`，17 张表） |

## 项目结构

```
LearnAI/
├── backend/    # Spring Boot REST API（端口 8080）
│   └── src/main/java/com/learnai/
│       ├── controller/  # 认证/资源/路径/答疑/商城/互动/管理/审核/学习激励
│       ├── service/     # 业务逻辑（服务端计价、状态机、文件存储、学习活动等）
│       │   ├── factory/ # StepTemplateFactory —— 教程步骤模板工厂 + 注册表
│       │   └── storage/ # StorageStrategy 策略接口 + LocalStorageStrategy + 策略工厂
│       ├── entity/      # JPA 实体 + 枚举（学习/步骤/路径/订单/每日学习日志）
│       ├── dto/         # 请求/响应对象
│       ├── security/    # JWT 工具 + 认证过滤器
│       ├── config/      # Security 配置、静态资源映射、数据初始化
│       └── exception/   # 统一异常处理（中文 JSON 提示）
├── frontend/   # Vue 3 单页应用（端口 5173，代理 /api 和 /uploads）
│   └── src/
│       ├── api/         # axios 封装（拦截器统一处理 401/403）
│       ├── stores/      # Pinia：auth / cart（唯一购物车）/ prefs（主题）
│       ├── router/      # 路由 + 登录/角色守卫
│       ├── components/  # 导航栏、卡片、评论树、分页、3D 查看器
│       └── views/       # 首页、资源、路径、AI、商城、个人中心、管理
└── uploads/    # 上传文件（backend 工作目录下，静态映射 /uploads/**）
```

## 设计模式与架构约定

| 模式 | 落点 | 说明 |
|---|---|---|
| 工厂模式 | `StepTemplateFactory` | 教程步骤模板集中注册（标题 + 正文模板），运行期建步骤与种子数据共用同一份定义 |
| 工厂 + 策略模式 | `StorageStrategyFactory` / `StorageStrategy` | 文件存储可插拔：默认 `LocalStorageStrategy`，扩展对象存储只需新增实现自动注册 |
| 工厂 + 策略模式 | `AiProviderFactory` / `AiProvider` | 答疑可插拔：配置 API Key 用真实大模型（`LlmAiProvider`，OpenAI 兼容协议），否则规则式演示（`RuleBasedAiProvider`），LLM 调用失败自动回退 |
| 统一响应 | `GlobalExceptionHandler` + `ApiException` | 全部错误统一为 `{status, message, timestamp}` 中文 JSON |
| 统一分页 | `PageResponse<T>` | 列表接口统一 `content / totalElements` 结构 |
| 统一取当前用户 | `SecurityUtils.currentUserId()` | Controller 不碰 SecurityContext，鉴权逻辑集中一处 |

学习激励：`StudyActivityService` 接收学习页 30s 心跳，按日聚合到 `study_log` 表，提供今日时长 / 连续打卡 / 周统计 / 「是否正在学习」（90s 心跳窗口）检测。

### 答疑大模型接入（可选）

默认演示模式（规则式回复，消息上标注「演示模式」）。要接入真实大模型，在 `backend/src/main/resources/application.yml` 的 `app.ai` 配置段填入任意 OpenAI 兼容服务的 Key（DeepSeek / 通义千问 / OpenAI 等）：

```yaml
app:
  ai:
    api-key: "sk-xxxxxxxx"   # 留空 = 演示模式
    base-url: https://api.deepseek.com
    model: deepseek-chat
```

调用失败时自动回退规则式回复，不影响使用；大模型回答会携带当前用户的学习进度概况和正在学习的资源标题作为上下文。

## 快速启动

### 1. 数据库（MySQL 8.0，本机）

已建好库 `learnai` 和专用账号：

- 数据库账号与密码通过环境变量 `DB_USERNAME` / `DB_PASSWORD` 配置（见 `.env.example`，不再写入仓库）
- 首次启动自动建表 + 播种演示数据（中文课程、评论、订单、AI 对话等，含可下载/可预览的占位文件）

### 2. 后端（端口 8080）

```bash
cd backend
mvnw.cmd spring-boot:run        # Windows（已内置 Maven Wrapper，无需安装 Maven）
```

### 3. 前端（端口 5173）

```bash
cd frontend
npm install
npm run dev                     # 浏览器打开 http://localhost:5173
```

## 演示账号（校园特供版，登录页可一键填入）

| 角色 | 账号 | 密码 | 学号 |
|---|---|---|---|
| 管理员 | `admin` | `admin123` | T0001 |
| 审核员 | `auditor` | `audit123` | T0002 |
| 普通用户 | `demo` | `demo123` | 2026010016 |

## 功能清单

- **学习资源**：左侧侧边栏（搜索 + 分类树导航，每个分类显示公开课程数，选中父分类自动包含子分类资源）、排序/分页、详情、点赞、评论（树形回复）、下载（中文文件名）
- **全局搜索**：导航栏搜索直达 `/search`，跨学习资源 / 学习路径 / 3D 模型三组检索，分组计数 + 查看全部
- **首页交互**（源自 HTML_TRIAL 创意实验）：hero 线框立方体可鼠标拖拽旋转（松手惯性滑行，空闲自动恢复）、英文标题乱码逐字解码动效、主按钮旋转霓虹边框 + 鼠标跟随光斑、入口方块 3D 悬停倾斜、全站十字准星光标
- **学习中心**：面包屑导航、开始学习 → 步骤打卡 → 进度保存 → 完成打分，教程式"上一步/下一步"章节导航
- **学习路径**：系统化路线（含顺序资源列表）、报名（幂等）、我的路径进度
- **AI 助手**：全站右下角悬浮按钮 + 抽屉式聊天面板（也保留整页入口），策略模式接入真实大模型（OpenAI 兼容协议，见下方配置；未配置 Key 时为规则式演示并在消息上标注），智能推荐、学习分析（近 7 天图表、分类统计）
- **导航栏**：悬停"我的学习"实时下拉最近学习进度（标题/状态/进度条）
- **个人控制台**：学习统计卡片、快捷入口、最近学习、管理入口（管理员/审核员可见）
- **模型资源库**（URL `/market`）：模型目录、**Three.js 在线 3D 预览**（GLB/GLTF/OBJ）、购物车（Pinia 持久化）、结算、**模拟支付**、订单状态机（待支付→待处理→处理中→已发货→已完成）、管理员推进状态
- **内容审核**：用户提交资源/模型 → 审核员/管理员通过或驳回
- **管理员**：数据看板（11 项统计）、用户管理（角色/学号/启用禁用）
- **个性化**：工业风黑白主题（直角/强调色）、暗色模式、主题色、字体大小、动画速度（免登录可用，登录后云端同步）
- **校园特供版**：注册含学号字段、演示账号一键填入登录
- **安全**：JWT 认证、注册强制普通用户角色、服务端订单计价、他人订单 404 隐私保护、401/403 中文提示

## 安全设计要点

- 密码 BCrypt 存储；JWT 24 小时有效；过滤器每次请求从数据库重载用户（角色变更即时生效）
- 上传白名单（PDF/Word/PPT/ZIP/3D 文件），UUID 重命名 + 路径穿越防护
- 订单总价由服务端按模型价格计算，客户端伪造金额无效
